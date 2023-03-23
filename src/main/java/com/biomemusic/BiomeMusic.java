package com.biomemusic;

import com.biomemusic.config.Configuration;
import com.biomemusic.event.ClientEventHandler;
import com.biomemusic.event.EventHandler;
import com.biomemusic.event.ModEventHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static com.biomemusic.BiomeMusic.MODID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MODID)
public class BiomeMusic
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
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "", (c, b) -> true));
        Mod.EventBusSubscriber.Bus.MOD.bus().get().register(ModEventHandler.class);
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(EventHandler.class);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    @SubscribeEvent
    public void clientSetup(FMLClientSetupEvent event)
    {
        // Side safe client event handler
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(ClientEventHandler.class);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.info(MODID + " mod initialized");
    }
}
