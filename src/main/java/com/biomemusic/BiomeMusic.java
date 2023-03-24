package com.biomemusic;

import com.biomemusic.config.Configuration;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

// The value here should match an entry in the META-INF/mods.toml file

public class BiomeMusic implements ModInitializer
{
    public static final String        MODID  = "biomemusic";
    public static final Logger        LOGGER = LogManager.getLogger();
    private static      Configuration config = null;
    public static       Random        rand   = new Random();

    public static Configuration getConfig()
    {
        if (config == null)
        {
            config = new Configuration();
            config.load();
        }
        return config;
    }

    public BiomeMusic()
    {
    }

    @Override
    public void onInitialize()
    {
        LOGGER.info(MODID + " mod initialized");
    }
}
