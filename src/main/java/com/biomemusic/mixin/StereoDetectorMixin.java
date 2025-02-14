package com.biomemusic.mixin;

import com.biomemusic.AdditionalMusic;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.FiniteAudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@Mixin(SoundBufferLibrary.class)
public class StereoDetectorMixin
{
    @Inject(method = "getStream", at = @At("RETURN"), cancellable = true, require = 0)
    private void addFuture(final ResourceLocation id, final boolean p_120206_, final CallbackInfoReturnable<CompletableFuture<AudioStream>> cir)
    {
        cir.setReturnValue(cir.getReturnValue().whenComplete((s, e) -> {
            if (s.getFormat().getChannels() > 1)
            {
                AdditionalMusic.stereoIDs.put(id, id);
            }
        }));
    }

    @Inject(method = "method_19747", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/audio/SoundBuffer;<init>(Ljava/nio/ByteBuffer;Ljavax/sound/sampled/AudioFormat;)V"), require = 0, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void addFuture(
        final ResourceLocation id,
        final CallbackInfoReturnable<SoundBuffer> cir,
        final InputStream inputstream,
        final FiniteAudioStream finiteaudiostream)
    {
        if (finiteaudiostream.getFormat().getChannels() > 1)
        {

            AdditionalMusic.stereoIDs.put(id, id);
        }
    }
}
