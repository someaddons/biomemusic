package com.biomemusic.environment;

import java.util.Set;

public enum MusicType
{
    Biome,
    Cave,
    Game,
    End,
    Nether,
    Night,
    Water;

    public static class MusicTypeData
    {
        public final MusicType             type;
        public final Set<MusicEnvironment> required;
        public final Set<MusicEnvironment> disallowed;

        public MusicTypeData(final MusicType type, Set<MusicEnvironment> required, final Set<MusicEnvironment> disallowed)
        {
            this.type = type;
            this.required = required;
            this.disallowed = disallowed;
        }
    }
}
