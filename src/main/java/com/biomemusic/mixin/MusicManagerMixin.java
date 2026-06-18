package com.biomemusic.mixin;

import com.biomemusic.AdditionalMusic;
import com.biomemusic.BiomeMusic;
import com.biomemusic.ISoundVolumeSetter;
import com.biomemusic.environment.MusicEnvironment;
import com.biomemusic.environment.MusicType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MusicManager.class, priority = 5)
public abstract class MusicManagerMixin
{
    @Shadow
    public abstract void stopPlaying();

    @Unique
    private Music playedMusic   = null;
    @Unique
    private long  startTime     = 0;
    private long  lastBiomeTime = 0;

    @Unique
    float fadeTowards = -1;

    @Unique
    float modifiedVolume = 1.0f;

    @Inject(method = "startPlaying", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"))
    private void onStartMusic(final Music music, final CallbackInfo ci)
    {
        playedMusic = music;
        if (BiomeMusic.config.getCommonConfig().smartMusic && playedMusic != null && Minecraft.getInstance().level != null && Minecraft.getInstance().player != null)
        {
            startTime = Minecraft.getInstance().level.getGameTime();
            final Biome biome = Minecraft.getInstance().level.getBiome(Minecraft.getInstance().player.blockPosition()).value();
            if (biome.getBackgroundMusic().isPresent() && biome.getBackgroundMusic().get().equals(playedMusic))
            {
                lastBiomeTime = startTime;
            }
            else
            {
                lastBiomeTime = 0;
            }

            fadeIn();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(final CallbackInfo ci)
    {
        if (playedMusic != null && fadeTowards != -1 && Minecraft.getInstance().level != null
              && Minecraft.getInstance().player != null && (fadeTowards > 0.2 || Minecraft.getInstance().level.getGameTime() - startTime > 20 * 15))
        {
            modifiedVolume += (fadeTowards > 0.1f ? 1 : -1) * (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC) / (20 * 20));
            modifiedVolume = Math.min(Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC), modifiedVolume);
            ((ISoundVolumeSetter) (Minecraft.getInstance().getSoundManager().soundEngine)).adjustVolume(SoundSource.MUSIC, modifiedVolume);
            if (Math.abs(modifiedVolume - fadeTowards) < 0.01)
            {
                if (fadeTowards == 0.1f)
                {
                    playedMusic = null;
                    stopPlaying();
                }

                fadeTowards = -1;
                ((ISoundVolumeSetter) (Minecraft.getInstance().getSoundManager().soundEngine)).adjustVolume(SoundSource.MUSIC, Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC));
                // Done
            }
        }

        if (playedMusic != null && BiomeMusic.rand.nextInt(20) == 0 && Minecraft.getInstance().level != null && Minecraft.getInstance().player != null && BiomeMusic.config.getCommonConfig().smartMusic)
        {
            final Level level = Minecraft.getInstance().level;
            final Player player = Minecraft.getInstance().player;

            if (playedMusic == AdditionalMusic.NIGHT_ADDITIONAL && !MusicEnvironment.canPlay(MusicType.Night))
            {
                if (BiomeMusic.config.getCommonConfig().displayMusicPlayed)
                {
                    BiomeMusic.LOGGER.info("Fading out music: " + playedMusic + " due to daytime");
                }

                fadeOut();
                return;
            }

            if (playedMusic == AdditionalMusic.CAVE_ADDITIONAL && !MusicEnvironment.canPlay(MusicType.Cave))
            {
                if (BiomeMusic.config.getCommonConfig().displayMusicPlayed)
                {
                    BiomeMusic.LOGGER.info("Fading out music: " + playedMusic + " due to leaving cave");
                }

                fadeOut();
                return;
            }

            if ((playedMusic == AdditionalMusic.WATER_ADDITIONAL || playedMusic == Musics.UNDER_WATER) && Minecraft.getInstance().player != null
                  && !MusicEnvironment.canPlay(MusicType.Water))
            {
                if (BiomeMusic.config.getCommonConfig().displayMusicPlayed)
                {
                    BiomeMusic.LOGGER.info("Fading out music: " + playedMusic + " due to leaving water");
                }

                fadeOut();
                return;
            }

            if ((playedMusic == AdditionalMusic.END_ADDITIONAL || playedMusic == Musics.END || playedMusic == Musics.END_BOSS) && Minecraft.getInstance().player != null
                  && !MusicEnvironment.canPlay(MusicType.End))
            {
                if (BiomeMusic.config.getCommonConfig().displayMusicPlayed)
                {
                    BiomeMusic.LOGGER.info("Fading out music: " + playedMusic + " due to leaving the end");
                }

                fadeOut();
                return;
            }

            if (playedMusic == AdditionalMusic.NETHER_ALL && Minecraft.getInstance().player != null && !MusicEnvironment.canPlay(MusicType.Nether))
            {
                if (BiomeMusic.config.getCommonConfig().displayMusicPlayed)
                {
                    BiomeMusic.LOGGER.info("Fading out music: " + playedMusic + " due to leaving the nether");
                }

                fadeOut();
                return;
            }

            if (lastBiomeTime > 0)
            {
                final Biome biome = level.getBiome(player.blockPosition()).value();
                if (biome.getBackgroundMusic().isPresent())
                {
                    if (biome.getBackgroundMusic().get().equals(playedMusic))
                    {
                        lastBiomeTime = level.getGameTime();
                    }
                    else if (level.getGameTime() - lastBiomeTime > 20 * 30)
                    {
                        if (BiomeMusic.config.getCommonConfig().displayMusicPlayed)
                        {
                            BiomeMusic.LOGGER.info("Fading out music: " + playedMusic + " due to changing biomes");
                        }

                        fadeOut();
                    }
                }

                return;
            }
        }
    }

    @Unique
    private void fadeOut()
    {
        if (fadeTowards != 0.1f)
        {
            modifiedVolume = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC);
            fadeTowards = 0.1f;
        }
    }

    @Unique
    private void fadeIn()
    {
        if (fadeTowards != Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC))
        {
            fadeTowards = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC);
            modifiedVolume = 0.1f;
            Minecraft.getInstance().getSoundManager().updateSourceVolume(SoundSource.MUSIC, modifiedVolume);
        }
    }
}
