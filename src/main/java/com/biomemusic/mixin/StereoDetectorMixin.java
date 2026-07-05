package com.biomemusic.mixin;

import com.biomemusic.AdditionalMusic;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(SoundBufferLibrary.class)
public class StereoDetectorMixin
{
    @Inject(method = "getStream", at = @At("RETURN"), cancellable = true, require = 0)
    private void addFuture(final Identifier id, final boolean p_120206_, final CallbackInfoReturnable<CompletableFuture<AudioStream>> cir)
    {
        cir.setReturnValue(cir.getReturnValue().whenComplete((s, e) -> {
            if (s.getFormat().getChannels() > 1)
            {
                AdditionalMusic.stereoIDs.put(id, id);
            }
        }));
    }

    @Inject(method = "lambda$getCompleteBuffer$1", at = @At(value = "RETURN"), require = 0)
    private void addFuture(final Identifier id, final CallbackInfoReturnable<SoundBuffer> cir)
    {
        if (cir.getReturnValue().format().getChannels() > 1)
        {

            AdditionalMusic.stereoIDs.put(id, id);
        }
    }
}
