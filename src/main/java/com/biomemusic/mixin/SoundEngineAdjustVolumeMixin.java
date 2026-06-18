package com.biomemusic.mixin;

import com.biomemusic.ISoundVolumeSetter;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(SoundEngine.class)
public abstract class SoundEngineAdjustVolumeMixin implements ISoundVolumeSetter
{
    @Shadow
    @Final
    private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

    @Shadow
    protected abstract float calculateVolume(final SoundInstance p_120328_);

    @Override
    public void adjustVolume(final SoundSource category, final float volumeModifier)
    {
        for (Map.Entry<SoundInstance, ChannelAccess.ChannelHandle> entry : this.instanceToChannel.entrySet())
        {
            final SoundInstance soundInstance = entry.getKey();
            final ChannelAccess.ChannelHandle channel = entry.getValue();
            if (soundInstance.getSource() == category)
            {
                float f = this.calculateVolume(soundInstance) * volumeModifier;
                channel.execute((channel1) -> {
                    if (f <= 0.0F)
                    {
                        channel1.stop();
                    }
                    else
                    {
                        channel1.setVolume(f);
                    }
                });
            }
        }
    }
}
