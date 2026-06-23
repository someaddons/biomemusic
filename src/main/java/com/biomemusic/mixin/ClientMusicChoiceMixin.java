package com.biomemusic.mixin;

import com.biomemusic.MusicChoice;
import net.minecraft.Optionull;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = Minecraft.class, priority = 5)
public abstract class ClientMusicChoiceMixin
{
    @Shadow
    @Nullable
    public Screen screen;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Final
    public Gui gui;

    @Shadow
    @Final
    public GameRenderer gameRenderer;

    @Inject(method = "getSituationalMusic", at = @At("RETURN"), cancellable = true)
    private void biomemusic$musicChoice(final CallbackInfoReturnable<Music> cir)
    {
        if (screen instanceof WinScreen)
        {
            return;
        }

        Music music = Optionull.map(this.screen, Screen::getBackgroundMusic);
        if (music != null)
        {
            cir.setReturnValue(music);
            return;
        }

        // Custom mixin detected
        if (cir.getReturnValue() != getSituationalMusicOriginal())
        {
            return;
        }

        MusicChoice.chooseMusic(cir);
    }

    // TODO: Update with MC version
    @Unique
    public Music getSituationalMusicOriginal()
    {
        Music screenMusic = Optionull.map(this.screen, Screen::getBackgroundMusic);
        if (screenMusic != null)
        {
            return screenMusic;
        }
        else
        {
            Camera camera = this.gameRenderer.getMainCamera();
            if (this.player != null && camera != null)
            {
                Level playerLevel = this.player.level();
                if (playerLevel.dimension() == Level.END && this.gui.getBossOverlay().shouldPlayMusic())
                {
                    return Musics.END_BOSS;
                }
                else
                {
                    BackgroundMusic backgroundMusic = camera.attributeProbe().getValue(EnvironmentAttributes.BACKGROUND_MUSIC, 1.0F);
                    boolean isCreative = this.player.getAbilities().instabuild && this.player.getAbilities().mayfly;
                    boolean isUnderwater = this.player.isUnderWater();
                    return backgroundMusic.select(isCreative, isUnderwater).orElse(null);
                }
            }
            else
            {
                return Musics.MENU;
            }
        }
    }
}
