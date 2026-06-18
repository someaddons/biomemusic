package com.biomemusic.config;

import com.biomemusic.BiomeMusic;
import com.biomemusic.environment.MusicEnvironment;
import com.biomemusic.environment.MusicType;
import com.cupboard.config.ICommonConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

import static com.biomemusic.environment.MusicEnvironment.*;

public class CommonConfiguration implements ICommonConfig
{
    public double  delayModifier       = 0.35;
    public float   pitchVariance       = 0f;
    public boolean musicVariance       = true;
    public boolean displayMusicPlayed  = false;
    public boolean stopMusicForRecords = true;
    public boolean logloadedmusic      = false;
    public boolean smartMusic          = true;
    public int     maxConcurrentSounds = 10;

    public Map<MusicType, MusicType.MusicTypeData> musicConditions = new LinkedHashMap<>();

    public CommonConfiguration()
    {
        musicConditions.put(MusicType.Biome, new MusicType.MusicTypeData(MusicType.Biome, Set.of(), Set.of()));
        musicConditions.put(MusicType.Cave, new MusicType.MusicTypeData(MusicType.Cave, Set.of(CAVE), Set.of(WATER, NETHER, END)));
        musicConditions.put(MusicType.Night, new MusicType.MusicTypeData(MusicType.Night, Set.of(NIGHT), Set.of(NETHER, END, WATER, CAVE)));
        musicConditions.put(MusicType.Water, new MusicType.MusicTypeData(MusicType.Water, Set.of(WATER), Set.of(NETHER, END)));
        musicConditions.put(MusicType.Game, new MusicType.MusicTypeData(MusicType.Game, Set.of(), Set.of(NETHER, END, WATER)));
        musicConditions.put(MusicType.End, new MusicType.MusicTypeData(MusicType.End, Set.of(END), Set.of()));
        musicConditions.put(MusicType.Nether, new MusicType.MusicTypeData(MusicType.Nether, Set.of(NETHER), Set.of()));
    }

    public JsonObject serialize()
    {
        final JsonObject root = new JsonObject();

        final JsonObject entry = new JsonObject();
        entry.addProperty("desc:", "Modifies the delay between songs(requires a game restart), lower = shorter delay (1.0 = vanilla). Default = 0.25");
        entry.addProperty("delayModifier", delayModifier);
        root.add("delayModifier", entry);

        final JsonObject entry5 = new JsonObject();
        entry5.addProperty("desc:", "Adds randomized pitch variance of up to the given value (e.g. 0.1), makes songs sound slightly different. default = 0");
        entry5.addProperty("pitchVariance", pitchVariance);
        root.add("pitchVariance", entry5);

        final JsonObject entry2 = new JsonObject();
        entry2.addProperty("desc:", "Enables more varied music to be available for biomes chosen from similar other biomes, default = true");
        entry2.addProperty("musicVariance", musicVariance);
        root.add("musicVariance", entry2);

        final JsonObject entry6 = new JsonObject();
        entry6.addProperty("desc:", "Enables smart stopping of music, when the biome changes or player leaves a cave/water/night is over, default = true");
        entry6.addProperty("smartMusic", smartMusic);
        root.add("smartMusic", entry6);

        final JsonObject entry11 = new JsonObject();
        entry11.addProperty("desc:", "Enables Jukeboxes and records playing to stop the current background music, default = true");
        entry11.addProperty("stopMusicForRecords", stopMusicForRecords);
        root.add("stopMusicForRecords", entry11);

        final JsonObject entry8 = new JsonObject();
        entry8.addProperty("desc:",
            "Sets the condition under which music from their respective categories is allowed to play. Required conditions need to be all met, a single matched disallowed conditions prevents it. Possible conditions are: "
                + Set.of(MusicEnvironment.values()));

        for (final MusicType.MusicTypeData data : musicConditions.values())
        {
            JsonObject musicCondition = new JsonObject();
            musicCondition.addProperty("requires", data.required.toString());
            musicCondition.addProperty("disallowed", data.disallowed.toString());
            entry8.add(data.type.name(), musicCondition);
        }

        root.add("musicConditions", entry8);

        final JsonObject entry3 = new JsonObject();
        entry3.addProperty("desc:", "Shows currently played music in chat/log, default = false");
        entry3.addProperty("displayMusicPlayed", displayMusicPlayed);
        root.add("displayMusicPlayed", entry3);

        final JsonObject entry4 = new JsonObject();
        entry4.addProperty("desc:", "Prints the music files getting loaded into the latest.log, default = false");
        entry4.addProperty("logloadedmusic", logloadedmusic);
        root.add("logloadedmusic", entry4);

        final JsonObject entry10 = new JsonObject();
        entry10.addProperty("desc:", "Set the maximum amount of times the same sound can play at the same time. Limits the amount of lag spamming sounds can create. Default = 10");
        entry10.addProperty("maxConcurrentSounds", maxConcurrentSounds);
        root.add("maxConcurrentSounds", entry10);

        return root;
    }

    public void deserialize(JsonObject data)
    {
        delayModifier = data.get("delayModifier").getAsJsonObject().get("delayModifier").getAsDouble();
        musicVariance = data.get("musicVariance").getAsJsonObject().get("musicVariance").getAsBoolean();
        displayMusicPlayed = data.get("displayMusicPlayed").getAsJsonObject().get("displayMusicPlayed").getAsBoolean();
        smartMusic = data.get("smartMusic").getAsJsonObject().get("smartMusic").getAsBoolean();
        logloadedmusic = data.get("logloadedmusic").getAsJsonObject().get("logloadedmusic").getAsBoolean();
        pitchVariance = data.get("pitchVariance").getAsJsonObject().get("pitchVariance").getAsFloat();
        maxConcurrentSounds = data.get("maxConcurrentSounds").getAsJsonObject().get("maxConcurrentSounds").getAsInt();

        JsonObject musicConditionData = data.get("musicConditions").getAsJsonObject();
        for (final Map.Entry<String, JsonElement> entry : musicConditionData.entrySet())
        {
            if (entry.getKey().equals("desc:"))
            {
                continue;
            }
            MusicType type;
            try
            {
                type = MusicType.valueOf(entry.getKey().substring(0,1).toUpperCase(Locale.ROOT)+entry.getKey().substring(1).toLowerCase(Locale.ROOT));
            }
            catch (Exception e)
            {
                BiomeMusic.LOGGER.warn("Bad music type for music conditions: " + entry.getKey() + " allowed music types are:" + Set.of(MusicType.values()));
                throw e;
            }

            JsonObject musicEntry = entry.getValue().getAsJsonObject();

            String[] requiredEntries = musicEntry.get("requires").getAsString().replace("[", "").replace("]", "").replace(" ","").split(",");
            Set<MusicEnvironment> required = new HashSet<>();
            for (final String requiredEntry : requiredEntries)
            {
                if (requiredEntry.isEmpty())
                {
                    continue;
                }

                MusicEnvironment condition;
                try
                {
                    condition = MusicEnvironment.valueOf(requiredEntry.toUpperCase(Locale.ROOT));
                }
                catch (Exception e)
                {
                    BiomeMusic.LOGGER.warn(
                        "Bad required music condition for music conditions: " + entry.getKey() + " allowed condition values are:" + Set.of(MusicEnvironment.values()));
                    throw e;
                }

                required.add(condition);
            }

            String[] disallowedEntries = musicEntry.get("disallowed").getAsString().replace("[", "").replace("]", "").replace(" ","").split(",");
            Set<MusicEnvironment> disallowed = new HashSet<>();
            for (final String disallowedEntry : disallowedEntries)
            {
                if (disallowedEntry.isEmpty())
                {
                    continue;
                }

                MusicEnvironment condition;
                try
                {

                    condition = MusicEnvironment.valueOf(disallowedEntry.toUpperCase(Locale.ROOT));
                }
                catch (Exception e)
                {
                    BiomeMusic.LOGGER.warn(
                        "Bad disallowed music condition for music conditions: " + entry.getKey() + " allowed condition values are:" + Set.of(MusicEnvironment.values()));
                    throw e;
                }

                disallowed.add(condition);
            }

            musicConditions.put(type, new MusicType.MusicTypeData(type, required, disallowed));
            stopMusicForRecords = data.get("stopMusicForRecords").getAsJsonObject().get("stopMusicForRecords").getAsBoolean();
        }
    }
}
