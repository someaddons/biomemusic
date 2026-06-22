package com.biomemusic.mixin;

import com.biomemusic.BiomeMusic;
import com.biomemusic.ISoundVolumeSetter;
import com.biomemusic.MusicChoice;
import com.biomemusic.environment.MusicEnvironment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundSource;
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
    @Unique
    private boolean playedMusicManaged = false;

    @Unique
    float fadeTowards = -1;

    @Unique
    float modifiedVolume = 1.0f;

    @Inject(method = "startPlaying", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"))
    private void onStartMusic(final Music music, final CallbackInfo ci)
    {
        playedMusic = music;
        playedMusicManaged = playedMusic == MusicChoice.lastChoice;

        if (BiomeMusic.config.getCommonConfig().smartMusic && playedMusic != null && Minecraft.getInstance().level != null && Minecraft.getInstance().player != null)
        {
            startTime = Minecraft.getInstance().level.getGameTime();
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
                    Minecraft.getInstance().getMusicManager().nextSongDelay /= 2;
                }

                fadeTowards = -1;
                ((ISoundVolumeSetter) (Minecraft.getInstance().getSoundManager().soundEngine)).adjustVolume(SoundSource.MUSIC,
                    Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC));
                // Done
            }
        }

        if (playedMusic != null && BiomeMusic.rand.nextInt(20) == 0 && Minecraft.getInstance().level != null && Minecraft.getInstance().player != null
            && BiomeMusic.config.getCommonConfig().smartMusic)
        {
            if (playedMusicManaged && !MusicChoice.currentPossibleTracks.contains(playedMusic))
            {
                if (BiomeMusic.config.getCommonConfig().displayMusicPlayed && fadeTowards != 0.1f)
                {
                    BiomeMusic.LOGGER.info("Fading out music: " + playedMusic.getEvent().unwrapKey().get().location() + " because it is no longer eligible. Environment:"
                        + MusicEnvironment.environment);
                }

                fadeOut();
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
