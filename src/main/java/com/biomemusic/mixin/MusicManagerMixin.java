package com.biomemusic.mixin;

import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MusicManager.class)
public class MusicManagerMixin
{
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/sounds/Music;replaceCurrentMusic()Z"))
    private boolean noReplace(final Music instance)
    {
        return false;
    }
}
