package com.biomemusic.mixin;

import com.biomemusic.AdditionalMusic;
import com.biomemusic.BiomeMusic;
import com.biomemusic.environment.MusicEnvironment;
import com.biomemusic.environment.MusicType;
import net.minecraft.Optionull;
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
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.biomemusic.AdditionalMusic.CAVE_TICKS;
import static com.biomemusic.AdditionalMusic.WATER_ADDITIONAL;

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

        Music music = Optionull.map(this.screen, Screen::getBackgroundMusic);
        if (music != null)
        {
            cir.setReturnValue(music);
            return;
        }

        if (player == null)
        {
            cir.setReturnValue(Musics.MENU);
            return;
        }

        final List<Music> possibleTracks = new ArrayList<>();

        if (player.getY() < player.level().getSeaLevel() && !player.level().canSeeSky(player.blockPosition()))
        {
            if (player.level().getBrightness(LightLayer.BLOCK, player.blockPosition()) < 6)
            {
                CAVE_TICKS++;
            }
        }
        else
        {
            CAVE_TICKS = 0;
        }

        // Initializes conditions
        MusicEnvironment.environment.put(MusicEnvironment.END, this.player.level().dimension() == Level.END);
        MusicEnvironment.environment.put(MusicEnvironment.NETHER, this.player.level().dimension() == Level.NETHER);
        MusicEnvironment.environment.put(MusicEnvironment.OVERWORLD, this.player.level().dimension() == Level.OVERWORLD);
        MusicEnvironment.environment.put(MusicEnvironment.CAVE, CAVE_TICKS > 300);
        MusicEnvironment.environment.put(MusicEnvironment.NIGHT,
            player.level().dimensionType().hasSkyLight() && !player.level().dimensionType().hasFixedTime() && (player.level().getDayTime() % 24000) > 12600);

        if (this.player.isUnderWater() && this.player.level().getBiome(this.player.blockPosition()).is(BiomeTags.PLAYS_UNDERWATER_MUSIC))
        {
            MusicEnvironment.environment.put(MusicEnvironment.WATER, true);
            CAVE_TICKS = 0;
        }

        if (MusicEnvironment.canPlay(MusicType.End))
        {
            if (gui.getBossOverlay().shouldPlayMusic())
            {
                possibleTracks.add(Musics.END_BOSS);
            }
            else
            {
                possibleTracks.add(Musics.END);
            }

            possibleTracks.add(AdditionalMusic.END_ADDITIONAL);
            possibleTracks.add(AdditionalMusic.END_ADDITIONAL);
        }

        if (MusicEnvironment.canPlay(MusicType.Nether))
        {
            possibleTracks.add(AdditionalMusic.NETHER_ALL);
            possibleTracks.add(AdditionalMusic.NETHER_ALL);
        }

        if (MusicEnvironment.canPlay(MusicType.Night))
        {
            possibleTracks.add(AdditionalMusic.NIGHT_ADDITIONAL);
            possibleTracks.add(AdditionalMusic.NIGHT_ADDITIONAL);
        }

        if (MusicEnvironment.canPlay(MusicType.Cave))
        {
            possibleTracks.add(AdditionalMusic.CAVE_ADDITIONAL);
            possibleTracks.add(AdditionalMusic.CAVE_ADDITIONAL);
            possibleTracks.add(AdditionalMusic.CAVE_ADDITIONAL);
            possibleTracks.add(AdditionalMusic.CAVE_ADDITIONAL);
            possibleTracks.add(AdditionalMusic.CAVE_ADDITIONAL);
            possibleTracks.add(AdditionalMusic.CAVE_ADDITIONAL);
        }

        if (MusicEnvironment.canPlay(MusicType.Game))
        {
            if (player.isCreative())
            {
                possibleTracks.add(Musics.CREATIVE);
            }

            possibleTracks.add(Musics.GAME);
            possibleTracks.add(Musics.GAME);
            possibleTracks.add(AdditionalMusic.GAME_ADDITIONAL);
            possibleTracks.add(AdditionalMusic.GAME_ADDITIONAL);
        }

        if (MusicEnvironment.canPlay(MusicType.Water))
        {
            possibleTracks.add(Musics.UNDER_WATER);
            possibleTracks.add(Musics.UNDER_WATER);
            possibleTracks.add(Musics.UNDER_WATER);
            possibleTracks.add(WATER_ADDITIONAL);
            possibleTracks.add(WATER_ADDITIONAL);
        }

        if (MusicEnvironment.canPlay(MusicType.Biome))
        {
            // Add biome music
            Holder<Biome> holder = this.player.level().getBiome(this.player.blockPosition());
            final Music biomeMusic = holder.value().getBackgroundMusic().orElse(null);
            if (biomeMusic != null)
            {
                possibleTracks.add(biomeMusic);
                possibleTracks.add(biomeMusic);
                possibleTracks.add(biomeMusic);
                possibleTracks.add(biomeMusic);
                possibleTracks.add(biomeMusic);
            }

            if (BiomeMusic.config.getCommonConfig().musicVariance)
            {
                for (final Map.Entry<TagKey<Biome>, List<Music>> entry : AdditionalMusic.taggedMusic.entrySet())
                {
                    if (holder.is(entry.getKey()))
                    {
                        possibleTracks.addAll(entry.getValue());
                        possibleTracks.addAll(entry.getValue());
                    }
                }

                for (final Map.Entry<String, List<Music>> entry : AdditionalMusic.namedMusic.entrySet())
                {
                    if (holder.unwrapKey().isPresent() && holder.unwrapKey().get().location().getPath().contains(entry.getKey()))
                    {
                        possibleTracks.addAll(entry.getValue());
                        possibleTracks.addAll(entry.getValue());
                    }
                }
            }
        }

        if (possibleTracks.isEmpty())
        {
            return;
        }

        for (Iterator<Music> iterator = possibleTracks.iterator(); iterator.hasNext(); )
        {
            final Music track = iterator.next();
            if (AdditionalMusic.DISABLED.contains(track))
            {
                iterator.remove();
            }
        }

        cir.setReturnValue(possibleTracks.get(BiomeMusic.rand.nextInt(possibleTracks.size())));
    }
}
