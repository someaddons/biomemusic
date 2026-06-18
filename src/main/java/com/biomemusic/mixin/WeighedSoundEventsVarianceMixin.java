package com.biomemusic.mixin;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(WeighedSoundEvents.class)
public abstract class WeighedSoundEventsVarianceMixin
{
    @Shadow
    @Final
    private List<Weighted<Sound>> list;

    @Unique
    private final List<Sound> lastSounds = new ArrayList<>();

    @Redirect(method = "getWeight", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/Weighted;getWeight()I"))
    private int adjustWeight(final Weighted instance)
    {
        return getAdjustedWeight(instance);
    }

    @Redirect(method = "getSound(Lnet/minecraft/util/RandomSource;)Lnet/minecraft/client/resources/sounds/Sound;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/Weighted;getWeight()I"))
    private int adjustWeightCompare(final Weighted instance)
    {
        return getAdjustedWeight(instance);
    }

    @Inject(method = "getSound(Lnet/minecraft/util/RandomSource;)Lnet/minecraft/client/resources/sounds/Sound;", at = @At("RETURN"))
    private void storeUsed(final RandomSource p_235265_, final CallbackInfoReturnable<Sound> cir)
    {
        final Sound used = cir.getReturnValue();
        if (used != null && list.size() > 1)
        {
            lastSounds.add(0, used);
            if (lastSounds.size() > 3)
            {
                lastSounds.remove(3);
            }
        }
    }

    @Unique
    private int getAdjustedWeight(final Weighted instance)
    {
        if (list.size() <= 1)
        {
            return instance.getWeight();
        }

        for (int i = 0; i < lastSounds.size(); i++)
        {
            if (instance == lastSounds.get(i))
            {
                return Math.max(i == 0 ? 0 : 1, (int) (instance.getWeight() * 10 * (0.1 + (0.3 * i))));
            }
        }

        return instance.getWeight() * 10;
    }
}
