package com.biomemusic.mixin;

import com.biomemusic.AdditionalMusic;
import com.biomemusic.BiomeMusic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ResourceLoadStateTracker;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ResourceLoadStateTracker.class)
public class ReloadHookMixin
{
    @Inject(method = "finishReload", at = @At("RETURN"))
    private void checkExistance(final CallbackInfo ci)
    {
        final List<Music> toTest = List.of(AdditionalMusic.CAVE_ADDITIONAL, AdditionalMusic.END_ADDITIONAL, AdditionalMusic.NIGHT_ADDITIONAL,
          AdditionalMusic.GAME_ADDITIONAL, AdditionalMusic.WATER_ADDITIONAL);

        AdditionalMusic.DISABLED.clear();

        for (final Music music : toTest)
        {
            final SimpleSoundInstance soundInstance = SimpleSoundInstance.forMusic(music.getEvent().value());
            final WeighedSoundEvents sound = soundInstance.resolve(Minecraft.getInstance().getSoundManager());
            if (sound == null || soundInstance.getSound() == SoundManager.EMPTY_SOUND)
            {
                if (BiomeMusic.config.getCommonConfig().logloadedmusic)
                {
                    BiomeMusic.LOGGER.info("Disabled music event due to being empty:" + music.getEvent().value().getLocation());
                }
                AdditionalMusic.DISABLED.add(music);
            }
        }
    }
}
