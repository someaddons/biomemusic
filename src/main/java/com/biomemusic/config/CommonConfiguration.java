package com.biomemusic.config;

import com.biomemusic.BiomeMusic;
import com.cupboard.config.ICommonConfig;
import com.google.gson.JsonObject;

public class CommonConfiguration implements ICommonConfig
{
    public double delayModifier = 0.35;
    public float pitchVariance = 0f;
    public boolean musicVariance                   = true;
    public boolean disableDefaultMusicInDimensions = true;
    public boolean displayMusicPlayed              = false;
    public boolean logloadedmusic = false;
    public boolean playonlycustomnightmusic        = false;

    public CommonConfiguration()
    {

    }

    public JsonObject serialize()
    {
        final JsonObject root = new JsonObject();

        final JsonObject entry = new JsonObject();
        entry.addProperty("desc:", "Modifies the delay between songs, lower = shorter delay (1.0 = vanilla). Default = 0.25");
        entry.addProperty("delayModifier", delayModifier);
        root.add("delayModifier", entry);

        final JsonObject entry5 = new JsonObject();
        entry5.addProperty("desc:", "Adds randomized pitch variance of up to the given value (e.g. 0.1), makes songs sound slightly different. default = 0");
        entry5.addProperty("pitchVariance", pitchVariance);
        root.add("pitchVariance", entry5);

        final JsonObject entry2 = new JsonObject();
        entry2.addProperty("desc:", "Enables more varied music to be available for biomes, default = true");
        entry2.addProperty("musicVariance", musicVariance);
        root.add("musicVariance", entry2);

        final JsonObject entry8 = new JsonObject();
        entry8.addProperty("desc:", "Disables overworld music being played at random(rarely) in nether/end, default = true");
        entry8.addProperty("disableDefaultMusicInDimensions", disableDefaultMusicInDimensions);
        root.add("disableDefaultMusicInDimensions", entry8);

        final JsonObject entry3 = new JsonObject();
        entry3.addProperty("desc:", "Shows currently played music in chat/log, default = false");
        entry3.addProperty("displayMusicPlayed", displayMusicPlayed);
        root.add("displayMusicPlayed", entry3);

        final JsonObject entry9 = new JsonObject();
        entry9.addProperty("desc:", "Plays only custom provided music at night, default = false");
        entry9.addProperty("playonlycustomnightmusic", playonlycustomnightmusic);
        root.add("playonlycustomnightmusic", entry9);

        final JsonObject entry4 = new JsonObject();
        entry4.addProperty("desc:", "Prints the music files getting loaded into the latest.log, default = false");
        entry4.addProperty("logloadedmusic", logloadedmusic);
        root.add("logloadedmusic", entry4);

        return root;
    }

    public void deserialize(JsonObject data)
    {
        delayModifier = data.get("delayModifier").getAsJsonObject().get("delayModifier").getAsDouble();
        musicVariance = data.get("musicVariance").getAsJsonObject().get("musicVariance").getAsBoolean();
        displayMusicPlayed = data.get("displayMusicPlayed").getAsJsonObject().get("displayMusicPlayed").getAsBoolean();
        logloadedmusic = data.get("logloadedmusic").getAsJsonObject().get("logloadedmusic").getAsBoolean();
        playonlycustomnightmusic = data.get("playonlycustomnightmusic").getAsJsonObject().get("playonlycustomnightmusic").getAsBoolean();
        disableDefaultMusicInDimensions = data.get("disableDefaultMusicInDimensions").getAsJsonObject().get("disableDefaultMusicInDimensions").getAsBoolean();
        pitchVariance = data.get("pitchVariance").getAsJsonObject().get("pitchVariance").getAsFloat();
    }
}
