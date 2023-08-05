package com.biomemusic;

import com.biomemusic.config.CommonConfiguration;
import com.cupboard.config.CupboardConfig;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

// The value here should match an entry in the META-INF/mods.toml file

public class BiomeMusic implements ModInitializer
{
    public static final String                              MODID  = "biomemusic";
    public static final Logger                              LOGGER = LogManager.getLogger();
    public static      CupboardConfig<CommonConfiguration> config = new CupboardConfig<>(MODID, new CommonConfiguration());
    public static       Random                              rand   = new Random();

    public BiomeMusic()
    {
    }

    @Override
    public void onInitialize()
    {
        LOGGER.info(MODID + " mod initialized");
    }
}
