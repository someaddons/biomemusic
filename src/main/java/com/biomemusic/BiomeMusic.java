package com.biomemusic;

import com.biomemusic.config.CommonConfiguration;
import com.cupboard.config.CupboardConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static com.biomemusic.BiomeMusic.MODID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MODID)
public class BiomeMusic
{
    public static final String                              MODID  = "biomemusic";
    public static final Logger                              LOGGER = LogManager.getLogger();
    public static       CupboardConfig<CommonConfiguration> config = new CupboardConfig<>(MODID, new CommonConfiguration());
    public static       Random                              rand   = new Random();

    public BiomeMusic(IEventBus modEventBus, ModContainer modContainer)
    {

    }
}
