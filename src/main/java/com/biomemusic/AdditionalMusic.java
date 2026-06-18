package com.biomemusic;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AdditionalMusic
{
    public static Map<ResourceLocation, ResourceLocation> stereoIDs = new ConcurrentHashMap<>();

    public static Music NETHER_ALL = new Music(Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(BiomeMusic.MODID, "music.nether"), SoundEvent.createVariableRangeEvent(new ResourceLocation(BiomeMusic.MODID, "music.nether"))), 12000, 24000, false);
    public static Music END_ADDITIONAL = new Music(Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(BiomeMusic.MODID, "music.end"), SoundEvent.createVariableRangeEvent(new ResourceLocation(BiomeMusic.MODID, "music.end"))), 12000, 24000, false);
    public static Music GAME_ADDITIONAL = new Music(Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(BiomeMusic.MODID, "music.game"), SoundEvent.createVariableRangeEvent(new ResourceLocation(BiomeMusic.MODID, "music.game"))), 12000, 24000, false);
    public static Music NIGHT_ADDITIONAL = new Music(Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(BiomeMusic.MODID, "music.night"), SoundEvent.createVariableRangeEvent(new ResourceLocation(BiomeMusic.MODID, "music.night"))), 12000, 24000, false);
    public static Music WATER_ADDITIONAL = new Music(Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT,
        new ResourceLocation(BiomeMusic.MODID, "music.water"),
        SoundEvent.createVariableRangeEvent(new ResourceLocation(BiomeMusic.MODID, "music.water"))), 12000, 24000, false);
    public static Music CAVE_ADDITIONAL  = new Music(Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT,
        new ResourceLocation(BiomeMusic.MODID, "music.cave"),
        SoundEvent.createVariableRangeEvent(new ResourceLocation(BiomeMusic.MODID, "music.cave"))), 12000, 24000, false);
    public static Music MENU_ADDITIONAL  = new Music(Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT,
        new ResourceLocation(BiomeMusic.MODID, "music.menu"),
        SoundEvent.createVariableRangeEvent(new ResourceLocation(BiomeMusic.MODID, "music.menu"))), 12000, 24000, false);

    public static final Map<TagKey<Biome>, List<Music>> taggedMusic = new HashMap<>();
    public static final Map<String, List<Music>>        namedMusic  = new HashMap<>();

    public static Set<Music> DISABLED = new HashSet<>();

    public static void init()
    {
        taggedMusic.computeIfAbsent(BiomeTags.HAS_ANCIENT_CITY, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DEEP_DARK));
        namedMusic.computeIfAbsent("deep_dark", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DEEP_DARK));
        namedMusic.computeIfAbsent("cave", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DRIPSTONE_CAVES));
        namedMusic.computeIfAbsent("cave", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_LUSH_CAVES));

        namedMusic.computeIfAbsent("desert", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DESERT));
        taggedMusic.computeIfAbsent(BiomeTags.IS_BADLANDS, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_BADLANDS));
        namedMusic.computeIfAbsent("mesa", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_BADLANDS));
        taggedMusic.computeIfAbsent(BiomeTags.IS_SAVANNA, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DESERT));
        namedMusic.computeIfAbsent("savanna_plateau", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_BADLANDS));
        namedMusic.computeIfAbsent("windswept_savanna", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_BADLANDS));

        namedMusic.computeIfAbsent("swamp", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SWAMP));
        taggedMusic.computeIfAbsent(BiomeTags.HAS_SWAMP_HUT, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SWAMP));

        taggedMusic.computeIfAbsent(BiomeTags.IS_JUNGLE, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JUNGLE));
        taggedMusic.computeIfAbsent(BiomeTags.IS_JUNGLE, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_BAMBOO_JUNGLE));
        taggedMusic.computeIfAbsent(BiomeTags.IS_JUNGLE, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SPARSE_JUNGLE));

        taggedMusic.computeIfAbsent(BiomeTags.IS_FOREST, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_GROVE));
        taggedMusic.computeIfAbsent(BiomeTags.IS_FOREST, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FOREST));
        namedMusic.computeIfAbsent("birch", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FLOWER_FOREST));
        namedMusic.computeIfAbsent("flower", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_MEADOW));
        namedMusic.computeIfAbsent("flower", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_CHERRY_GROVE));
        namedMusic.computeIfAbsent("cherry", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_MEADOW));
        namedMusic.computeIfAbsent("cherry", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FLOWER_FOREST));
        namedMusic.computeIfAbsent("meadow", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FLOWER_FOREST));
        namedMusic.computeIfAbsent("meadow", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_CHERRY_GROVE));

        taggedMusic.computeIfAbsent(BiomeTags.IS_TAIGA, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_OLD_GROWTH_TAIGA));
        namedMusic.computeIfAbsent("old_growth", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_OLD_GROWTH_TAIGA));
        namedMusic.computeIfAbsent("taiga", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_OLD_GROWTH_TAIGA));

        taggedMusic.computeIfAbsent(BiomeTags.HAS_VILLAGE_PLAINS, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_MEADOW));
        namedMusic.computeIfAbsent("sunflower", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_MEADOW));
        namedMusic.computeIfAbsent("sunflower", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FLOWER_FOREST));

        taggedMusic.computeIfAbsent(BiomeTags.HAS_VILLAGE_SNOWY, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SNOWY_SLOPES));
        taggedMusic.computeIfAbsent(BiomeTags.HAS_VILLAGE_SNOWY, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_GROVE));
        taggedMusic.computeIfAbsent(BiomeTags.HAS_VILLAGE_SNOWY, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FROZEN_PEAKS));
        taggedMusic.computeIfAbsent(BiomeTags.HAS_IGLOO, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SNOWY_SLOPES));
        taggedMusic.computeIfAbsent(BiomeTags.HAS_IGLOO, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_GROVE));
        taggedMusic.computeIfAbsent(BiomeTags.HAS_IGLOO, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FROZEN_PEAKS));
        namedMusic.computeIfAbsent("snowy", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SNOWY_SLOPES));
        namedMusic.computeIfAbsent("snowy", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_GROVE));
        namedMusic.computeIfAbsent("snowy", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FROZEN_PEAKS));
        namedMusic.computeIfAbsent("frozen", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FROZEN_PEAKS));
        namedMusic.computeIfAbsent("frozen", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SNOWY_SLOPES));
        namedMusic.computeIfAbsent("ice_spikes", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FROZEN_PEAKS));
        namedMusic.computeIfAbsent("ice_spikes", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SNOWY_SLOPES));

        taggedMusic.computeIfAbsent(BiomeTags.IS_HILL, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JAGGED_PEAKS));
        taggedMusic.computeIfAbsent(BiomeTags.IS_HILL, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_STONY_PEAKS));
        namedMusic.computeIfAbsent("stony", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_STONY_PEAKS));
        namedMusic.computeIfAbsent("windswept", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JAGGED_PEAKS));
        namedMusic.computeIfAbsent("windswept", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_STONY_PEAKS));

        taggedMusic.computeIfAbsent(BiomeTags.IS_MOUNTAIN, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JAGGED_PEAKS));
        taggedMusic.computeIfAbsent(BiomeTags.IS_MOUNTAIN, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_MEADOW));
        taggedMusic.computeIfAbsent(BiomeTags.IS_MOUNTAIN, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FROZEN_PEAKS));
        taggedMusic.computeIfAbsent(BiomeTags.IS_MOUNTAIN, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SNOWY_SLOPES));
        taggedMusic.computeIfAbsent(BiomeTags.IS_MOUNTAIN, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_STONY_PEAKS));
    }
}
