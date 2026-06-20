package com.biomemusic;

import com.biomemusic.environment.CaveDetectionSystem;
import com.biomemusic.environment.MusicEnvironment;
import com.biomemusic.environment.MusicType;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static com.biomemusic.AdditionalMusic.WATER_ADDITIONAL;

public class MusicChoice
{
    private final static Random musicRandom = new Random();
    public static        long   randomSeed  = BiomeMusic.rand.nextLong();

    private static Music lastChoice = null;
    private static CaveDetectionSystem caveDetectionSystem = new CaveDetectionSystem();

    public static void chooseMusic(final CallbackInfoReturnable<Music> cir)
    {
        caveDetectionSystem.tick(Minecraft.getInstance());
        final Player player = Minecraft.getInstance().player;
        if (player == null)
        {
            if (Minecraft.getInstance().getMusicManager().isPlayingMusic(AdditionalMusic.MENU_ADDITIONAL) || (BiomeMusic.rand.nextInt(5) == 0 && !AdditionalMusic.DISABLED.contains(AdditionalMusic.MENU_ADDITIONAL)
            && !Minecraft.getInstance().getMusicManager().isPlayingMusic(Musics.MENU)))
            {
                cir.setReturnValue(AdditionalMusic.MENU_ADDITIONAL);
            }
            else
            {
                cir.setReturnValue(Musics.MENU);
            }
            return;
        }

        final List<Music> possibleTracks = new ArrayList<>();
        // Evaluate environment
        boolean envChanged = false;
        envChanged |= MusicEnvironment.setEnvironmentFor(MusicEnvironment.END, player.level().dimension() == Level.END);
        envChanged |= MusicEnvironment.setEnvironmentFor(MusicEnvironment.NETHER, player.level().dimension() == Level.NETHER);
        envChanged |= MusicEnvironment.setEnvironmentFor(MusicEnvironment.OVERWORLD, player.level().dimension() == Level.OVERWORLD);
        envChanged |= MusicEnvironment.setEnvironmentFor(MusicEnvironment.OTHERDIMENSIONS,
            !(player.level().dimension() == Level.END || player.level().dimension() == Level.NETHER || player.level().dimension() == Level.OVERWORLD));
        envChanged |= MusicEnvironment.setEnvironmentFor(MusicEnvironment.CAVE, caveDetectionSystem.tick(Minecraft.getInstance()).goodForMusic());
        envChanged |= MusicEnvironment.setEnvironmentFor(MusicEnvironment.NIGHT,
            player.level().dimensionType().hasSkyLight() && !player.level().dimensionType().hasFixedTime() && (player.level().getDayTime() % 24000) > 12600);

        if (player.isUnderWater() && player.level().getBiome(player.blockPosition()).is(BiomeTags.PLAYS_UNDERWATER_MUSIC))
        {
            envChanged |= MusicEnvironment.setEnvironmentFor(MusicEnvironment.WATER, true);
        }
        else
        {
            envChanged |= MusicEnvironment.setEnvironmentFor(MusicEnvironment.WATER, false);
        }

        if (envChanged)
        {
            Minecraft.getInstance().getMusicManager().nextSongDelay /= 2;
        }

        if (MusicEnvironment.canPlay(MusicType.End))
        {
            if (Minecraft.getInstance().gui.getBossOverlay().shouldPlayMusic())
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

            if (BiomeMusic.config.getCommonConfig().musicVariance)
            {
                final List<Music> caveMusic = AdditionalMusic.namedMusic.get("cave");
                if (caveMusic != null)
                {
                    possibleTracks.addAll(caveMusic);
                    possibleTracks.addAll(caveMusic);
                }
            }
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
            Holder<Biome> holder = player.level().getBiome(player.blockPosition());
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

        for (Iterator<Music> iterator = possibleTracks.iterator(); iterator.hasNext(); )
        {
            final Music track = iterator.next();
            if (AdditionalMusic.DISABLED.contains(track))
            {
                iterator.remove();
            }
        }

        if (possibleTracks.isEmpty())
        {
            Minecraft.getInstance().getMusicManager().nextSongDelay = 2 * 60 * 20;
            return;
        }

        musicRandom.setSeed(randomSeed);
        Music choice = possibleTracks.get(musicRandom.nextInt(possibleTracks.size()));
        if (lastChoice != choice)
        {
            newSeed();
            musicRandom.setSeed(randomSeed);
            choice = possibleTracks.get(musicRandom.nextInt(possibleTracks.size()));
            lastChoice = choice;
        }
        cir.setReturnValue(choice);
    }

    private static void newSeed()
    {
        randomSeed = BiomeMusic.rand.nextLong();
    }
}
