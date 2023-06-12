package com.biomemusic;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdditionalMusic
{
    public static Music NETHER_ALL = new Music(Registry.register(Registry.SOUND_EVENT,
      new ResourceLocation(BiomeMusic.MODID, "music.nether"),
      new SoundEvent(new ResourceLocation(BiomeMusic.MODID, "music.nether"))), 12000, 24000, false);

    public static final Map<TagKey<Biome>, List<Music>> taggedMusic = new HashMap<>();
    public static final Map<String, List<Music>>        namedMusic  = new HashMap<>();

    public static void init()
    {
        taggedMusic.computeIfAbsent(BiomeTags.HAS_VILLAGE_SNOWY, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SNOWY_SLOPES));
        namedMusic.computeIfAbsent("cave", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DRIPSTONE_CAVES));
        taggedMusic.computeIfAbsent(BiomeTags.IS_FOREST, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_GROVE));
        taggedMusic.computeIfAbsent(BiomeTags.HAS_VILLAGE_SNOWY, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_GROVE));
        taggedMusic.computeIfAbsent(BiomeTags.IS_MOUNTAIN, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JAGGED_PEAKS));
        namedMusic.computeIfAbsent("cave", key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_LUSH_CAVES));
        taggedMusic.computeIfAbsent(BiomeTags.IS_MOUNTAIN, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_MEADOW));
        taggedMusic.computeIfAbsent(BiomeTags.IS_MOUNTAIN, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FROZEN_PEAKS));
        taggedMusic.computeIfAbsent(BiomeTags.IS_MOUNTAIN, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SNOWY_SLOPES));
        taggedMusic.computeIfAbsent(BiomeTags.IS_MOUNTAIN, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_STONY_PEAKS));
    }
}
