package com.biomemusic.mixin;

import com.biomemusic.AdditionalMusic;
import com.biomemusic.BiomeMusic;
import com.biomemusic.environment.MusicEnvironment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(SoundEngine.class)
public class MusicPlayMixin
{
    @Shadow
    @Final
    private Map<SoundInstance, Integer> soundDeleteTime;

    @Unique
    private static final Map<ResourceLocation, ResourceLocation> oncePlayed = new HashMap<>();

    @Inject(method = "play", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/sounds/SoundInstance;getSound()Lnet/minecraft/client/resources/sounds/Sound;"))
    private void biomesMusic$onPlay(final SoundInstance sound, final CallbackInfo ci)
    {
        if (sound.getSource() != SoundSource.MUSIC)
        {
            if (sound.getSource() == SoundSource.RECORDS && BiomeMusic.config.getCommonConfig().stopMusicForRecords)
            {
                Minecraft.getInstance().getMusicManager().stopPlaying();
            }

            return;
        }

        if (sound instanceof AbstractSoundInstance && BiomeMusic.config.getCommonConfig().pitchVariance > 0f)
        {
            ((AbstractSoundInstance) sound).pitch +=
                BiomeMusic.rand.nextFloat(BiomeMusic.config.getCommonConfig().pitchVariance * 2) - BiomeMusic.config.getCommonConfig().pitchVariance;
        }

        if (BiomeMusic.config.getCommonConfig().displayMusicPlayed)
        {
            BiomeMusic.LOGGER.info("playing: " + sound.getLocation() + " sound:" + sound.getSound().getLocation() + " environment: " + MusicEnvironment.environment);
            if (Minecraft.getInstance().player != null)
            {
                Minecraft.getInstance().player.displayClientMessage(Component.literal("playing: " + sound.getSound().getLocation()), true);
            }
        }

        if (sound.getSound() == SoundManager.EMPTY_SOUND && (sound.getLocation().getNamespace().equals("biomemusic")))
        {
            Minecraft.getInstance().getMusicManager().nextSongDelay = 0;
        }
    }

    @Inject(method = "play", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/sounds/SoundInstance;getVolume()F"), cancellable = true)
    private void biomesMusic$onPlaySound(final SoundInstance sound, final CallbackInfo ci)
    {
        if (sound.getSound() == null || Minecraft.getInstance().player == null)
        {
            return;
        }

        // When playing a distance based sound, check distance first
        if (!sound.isRelative() && sound.getAttenuation() != SoundInstance.Attenuation.NONE)
        {
            if (!oncePlayed.containsKey(sound.getLocation()))
            {
                oncePlayed.put(sound.getLocation(), sound.getLocation());
                return;
            }

            if (AdditionalMusic.stereoIDs.containsKey(sound.getLocation()))
            {
                return;
            }

            if (sound.getX() == 0.0 && sound.getY() == 0.0 && sound.getZ() == 0.0)
            {
                return;
            }

            final double distance = Minecraft.getInstance().player.position().distanceTo(new Vec3(sound.getX(), sound.getY(), sound.getZ()));
            if (distance > (Math.max(1.0, sound.getVolume()) * sound.getSound().getAttenuationDistance()) + 10)
            {
                ci.cancel();
            }
        }
    }

    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    private void biomesMusic$limitMaxConcurrent(final SoundInstance soundInstance, final CallbackInfo ci)
    {
        if (soundDeleteTime == null || soundInstance == null || Minecraft.getInstance().isPaused())
        {
            return;
        }

        int similarcount = 0;
        for (final SoundInstance sound : soundDeleteTime.keySet())
        {
            if (sound.getLocation().equals(soundInstance.getLocation()))
            {
                similarcount++;
                if (similarcount == BiomeMusic.config.getCommonConfig().maxConcurrentSounds)
                {
                    ci.cancel();
                    break;
                }
            }

            if (BiomeMusic.config.getCommonConfig().stopMusicForRecords && sound.getSource() == SoundSource.RECORDS && soundInstance.getSource() == SoundSource.MUSIC)
            {
                ci.cancel();
            }
        }
    }
}
