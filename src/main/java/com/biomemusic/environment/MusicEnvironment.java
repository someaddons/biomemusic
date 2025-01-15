package com.biomemusic.environment;

import com.biomemusic.BiomeMusic;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;

public enum MusicEnvironment
{
    CAVE,
    WATER,
    NIGHT,
    OVERWORLD,
    NETHER,
    END;

    public static Object2BooleanOpenHashMap<MusicEnvironment> environment = new Object2BooleanOpenHashMap<>();

    public static boolean canPlay(final MusicType musicType)
    {
        final MusicType.MusicTypeData musicTypeData = BiomeMusic.config.getCommonConfig().musicConditions.get(musicType);
        if (musicTypeData == null)
        {
            return false;
        }

        for (final MusicEnvironment requiredEnv : musicTypeData.required)
        {
            if (!environment.getBoolean(requiredEnv))
            {
                return false;
            }
        }

        for (final MusicEnvironment disallowedEnv : musicTypeData.disallowed)
        {
            if (environment.getBoolean(disallowedEnv))
            {
                return false;
            }
        }

        return true;
    }
}
