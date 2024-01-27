package com.biomemusic.mixin;

import com.biomemusic.BiomeMusic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class MusicPlayMixin
{
    @Inject(method = "play", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/sounds/SoundInstance;getSound()Lnet/minecraft/client/resources/sounds/Sound;"))
    private void biomesMusic$onPlay(final SoundInstance sound, final CallbackInfo ci)
    {
        if (sound.getSource() != SoundSource.MUSIC)
        {
            return;
        }

        if (sound instanceof AbstractSoundInstance && BiomeMusic.config.getCommonConfig().pitchVariance > 0f)
        {
            ((AbstractSoundInstance) sound).pitch +=
              BiomeMusic.rand.nextFloat(BiomeMusic.config.getCommonConfig().pitchVariance * 2) - BiomeMusic.config.getCommonConfig().pitchVariance;
        }

        if (BiomeMusic.config.getCommonConfig().displayMusicPlayed)
        {
            BiomeMusic.LOGGER.info("playing: " + sound.getLocation() + " sound:" + sound.getSound().getLocation());
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
}
