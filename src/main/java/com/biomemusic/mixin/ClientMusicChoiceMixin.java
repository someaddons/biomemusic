package com.biomemusic.mixin;

import com.biomemusic.AdditionalMusic;
import com.biomemusic.BiomeMusic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(Minecraft.class)
public class ClientMusicChoiceMixin
{
    @Shadow
    @Nullable
    public Screen screen;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Final
    public Gui gui;

    @Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
    private void biomemusic$musicChoice(final CallbackInfoReturnable<Music> cir)
    {
        if (screen instanceof WinScreen)
        {
            return;
        }

        List<Music> possibleTracks = new ArrayList<>();

        if (this.player != null)
        {
            if (this.player.level.dimension() == Level.END)
            {
                if (gui.getBossOverlay().shouldPlayMusic())
                {
                    possibleTracks.add(Musics.END_BOSS);
                }
                else
                {
                    possibleTracks.add(Musics.END);
                }

                if (!BiomeMusic.config.getCommonConfig().disableDefaultMusicInDimensions)
                {
                    possibleTracks.add(Musics.GAME);
                }

                possibleTracks.add(AdditionalMusic.END_ADDITIONAL);
                possibleTracks.add(AdditionalMusic.END_ADDITIONAL);
            }
            else if (player.level.dimension() == Level.NETHER)
            {
                if (!BiomeMusic.config.getCommonConfig().disableDefaultMusicInDimensions)
                {
                    possibleTracks.add(Musics.GAME);
                }
                possibleTracks.add(AdditionalMusic.NETHER_ALL);
                possibleTracks.add(AdditionalMusic.NETHER_ALL);
            }
            else
            {
                if (player.level.isNight())
                {
                    possibleTracks.add(AdditionalMusic.NIGHT_ADDITIONAL);
                    possibleTracks.add(AdditionalMusic.NIGHT_ADDITIONAL);

                    if (BiomeMusic.config.getCommonConfig().playonlycustomnightmusic)
                    {
                        cir.setReturnValue(AdditionalMusic.NIGHT_ADDITIONAL);
                        return;
                    }
                }

                if (player.isCreative())
                {
                    possibleTracks.add(Musics.CREATIVE);
                }

                possibleTracks.add(Musics.GAME);
                possibleTracks.add(AdditionalMusic.GAME_ADDITIONAL);
                possibleTracks.add(AdditionalMusic.GAME_ADDITIONAL);

                if (this.player.isUnderWater() && this.player.level.getBiome(this.player.blockPosition()).is(BiomeTags.PLAYS_UNDERWATER_MUSIC))
                {
                    possibleTracks.clear();
                    possibleTracks.add(Musics.UNDER_WATER);
                    possibleTracks.add(Musics.UNDER_WATER);
                    possibleTracks.add(Musics.UNDER_WATER);
                    possibleTracks.add(Musics.UNDER_WATER);
                    possibleTracks.add(Musics.UNDER_WATER);
                    possibleTracks.add(Musics.UNDER_WATER);
                    possibleTracks.add(Musics.UNDER_WATER);
                    possibleTracks.add(Musics.UNDER_WATER);
                    possibleTracks.add(Musics.UNDER_WATER);
                }
            }

            // Add biome music
            Holder<Biome> holder = this.player.level.getBiome(this.player.blockPosition());
            final Music biomeMusic = holder.value().getBackgroundMusic().orElse(null);
            if (biomeMusic != null)
            {
                if (!BiomeMusic.config.getCommonConfig().musicVariance)
                {
                    possibleTracks.clear();
                }

                for (int i = 0; i < 5; i++)
                {
                    possibleTracks.add(biomeMusic);
                }
            }

            if (BiomeMusic.config.getCommonConfig().musicVariance)
            {
                for (final Map.Entry<TagKey<Biome>, List<Music>> entry : AdditionalMusic.taggedMusic.entrySet())
                {
                    if (holder.is(entry.getKey()))
                    {
                        possibleTracks.addAll(entry.getValue());
                    }
                }

                for (final Map.Entry<String, List<Music>> entry : AdditionalMusic.namedMusic.entrySet())
                {
                    if (holder.unwrapKey().isPresent() && holder.unwrapKey().get().location().getPath().contains(entry.getKey()))
                    {
                        possibleTracks.addAll(entry.getValue());
                    }
                }
            }
        }
        else
        {
            possibleTracks.add(Musics.MENU);
        }

        if (possibleTracks.isEmpty())
        {
            return;
        }

        cir.setReturnValue(possibleTracks.get(BiomeMusic.rand.nextInt(possibleTracks.size())));
    }
}
