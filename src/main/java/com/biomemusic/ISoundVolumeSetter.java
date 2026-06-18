package com.biomemusic;

import net.minecraft.sounds.SoundSource;

public interface ISoundVolumeSetter
{
    void adjustVolume(SoundSource category, float volumeModifier);
}
