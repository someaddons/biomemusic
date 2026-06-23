package com.biomemusic;

import com.biomemusic.environment.CaveDetectionSystem;
import com.biomemusic.environment.MusicEnvironment;
import com.biomemusic.environment.MusicType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.tags.TagKey;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static com.biomemusic.AdditionalMusic.WATER_ADDITIONAL;

public class MusicChoice
{
    private final static Random musicRandom = new Random();

    public static       Music      lastChoice            = null;
    public static final Set<Music> currentPossibleTracks = Collections.newSetFromMap(new IdentityHashMap<>());

    private static boolean             lastChoicePlayed    = false;
    private static CaveDetectionSystem caveDetectionSystem = new CaveDetectionSystem();

    public static void chooseMusic(final CallbackInfoReturnable<Music> cir)
    {
        final Player player = Minecraft.getInstance().player;
        if (player == null)
        {
            currentPossibleTracks.clear();
            if (Minecraft.getInstance().getMusicManager().isPlayingMusic(AdditionalMusic.MENU_ADDITIONAL) || (BiomeMusic.rand.nextInt(5) == 0 && !AdditionalMusic.DISABLED.contains(
                AdditionalMusic.MENU_ADDITIONAL)
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

        // 26.1 +  new music selection. Dimensions and biomes supply the background music env, biome takes priority. Background music consists of a triple of creative, underwater and default music, creative and underwater are chosen with priority when they exist
        BackgroundMusic backgroundMusic = Minecraft.getInstance().gameRenderer.mainCamera().attributeProbe().getValue(EnvironmentAttributes.BACKGROUND_MUSIC, 1.0F);
        final boolean isCreative = player.getAbilities().instabuild && player.getAbilities().mayfly;
        final boolean isUnderwater = player.isUnderWater();
        final Music vanillaMusic = backgroundMusic.select(isCreative, isUnderwater).orElse(null);
        final boolean musicMuted = Minecraft.getInstance().gameRenderer.mainCamera().attributeProbe().getValue(EnvironmentAttributes.MUSIC_VOLUME, 1.0F) <= 0.0F;

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
            player.level().dimensionType().hasSkyLight() && !player.level().dimensionType().hasFixedTime() && (player.level().getOverworldClockTime() % 24000) > 12600);

        if (musicMuted)
        {
            currentPossibleTracks.clear();
            cir.setReturnValue(null);
            return;
        }

        if (player.isUnderWater() && backgroundMusic.underwaterMusic().isPresent())
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
            if (Minecraft.getInstance().gui.hud.getBossOverlay().shouldPlayMusic())
            {
                possibleTracks.add(Musics.END_BOSS);
                possibleTracks.add(Musics.END_BOSS);
                possibleTracks.add(Musics.END_BOSS);
                possibleTracks.add(Musics.END_BOSS);
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
            if (isCreative)
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
            final Music biomeMusic = (vanillaMusic != Musics.GAME && vanillaMusic != Musics.CREATIVE) ? vanillaMusic : null;
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
                    if (holder.unwrapKey().isPresent() && holder.unwrapKey().get().identifier().getPath().contains(entry.getKey()))
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

        currentPossibleTracks.clear();
        currentPossibleTracks.addAll(possibleTracks);

        if (possibleTracks.isEmpty())
        {
            Minecraft.getInstance().getMusicManager().nextSongDelay = 2 * 60 * 20;
            return;
        }

        if (lastChoice != null && possibleTracks.contains(lastChoice))
        {
            if (Minecraft.getInstance().getMusicManager().isPlayingMusic(lastChoice))
            {
                lastChoicePlayed = true;
            }

            if (!lastChoicePlayed || Minecraft.getInstance().getMusicManager().isPlayingMusic(lastChoice))
            {
                cir.setReturnValue(lastChoice);
                return;
            }
        }

        if (lastChoice != null)
        {
            possibleTracks.removeIf(track -> track == lastChoice);
            if (possibleTracks.isEmpty())
            {
                possibleTracks.add(lastChoice);
            }
        }

        Music choice = possibleTracks.get(musicRandom.nextInt(possibleTracks.size()));
        lastChoice = choice;
        lastChoicePlayed = false;

        cir.setReturnValue(choice);
    }
}
