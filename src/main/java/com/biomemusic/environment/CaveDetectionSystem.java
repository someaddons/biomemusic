package com.biomemusic.environment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Random;

/**
 * clientside cave detection
 */
public final class CaveDetectionSystem
{
    private static final int COOLDOWN = 20;

    private static final float ENTER_CAVE_SCORE = 0.68F;
    private static final float EXIT_CAVE_SCORE  = 0.48F;
    private static final float AMBIENCE_SCORE   = 0.70F;
    private static final float MUSIC_SCORE      = 0.76F;

    private static final int ENTER_STABLE_TICKS    = 80;
    private static final int EXIT_STABLE_TICKS     = 60;
    private static final int AMBIENCE_SETTLE_TICKS = 100;
    private static final int MUSIC_SETTLE_TICKS    = 240;

    private static final int[][] ENCLOSURE_OFFSETS = {
        {4, 0, 0}, {-4, 0, 0}, {0, 0, 4}, {0, 0, -4},
        {6, 1, 0}, {-6, 1, 0}, {0, 1, 6}, {0, 1, -6},
        {3, 2, 3}, {-3, 2, 3}, {3, 2, -3}, {-3, 2, -3},
        {0, 4, 0}, {2, 4, 0}, {-2, 4, 0}, {0, 4, 2}, {0, 4, -2},
        {0, 8, 0}
    };

    private final Random  random = new Random();
    private final boolean allowNoSkyLightDimensions;

    private int cooldown = 4;
    private int enterTicks;
    private int exitTicks;
    private int caveTicks;

    private float     rawScore;
    private float     smoothedScore;
    private boolean   inCave;
    private CaveState lastState = CaveState.EMPTY;

    public CaveDetectionSystem()
    {
        this(false);
    }

    public CaveDetectionSystem(boolean allowNoSkyLightDimensions)
    {
        this.allowNoSkyLightDimensions = allowNoSkyLightDimensions;
    }

    public CaveState tick(Minecraft minecraft)
    {
        if (minecraft.level == null || minecraft.player == null)
        {
            reset();
            return CaveState.EMPTY;
        }

        if (minecraft.isPaused())
        {
            return lastState;
        }

        final ClientLevel level = minecraft.level;
        final LocalPlayer player = minecraft.player;

        if (cooldown-- <= 0)
        {
            rawScore = checkCaveScore(level, player);
            cooldown = COOLDOWN;
        }

        smoothedScore += (rawScore - smoothedScore) * 0.07F;
        updateCaveTicks();

        boolean goodForAmbience = inCave
            && caveTicks >= AMBIENCE_SETTLE_TICKS
            && smoothedScore >= AMBIENCE_SCORE;

        boolean goodForMusic = inCave
            && caveTicks >= MUSIC_SETTLE_TICKS
            && smoothedScore >= MUSIC_SCORE;

        lastState = new CaveState(
            rawScore,
            smoothedScore,
            inCave,
            caveTicks,
            goodForAmbience,
            goodForMusic
        );

        return lastState;
    }

    public void reset()
    {
        cooldown = 0;
        enterTicks = 0;
        exitTicks = 0;
        caveTicks = 0;
        rawScore = 0.0F;
        smoothedScore = 0.0F;
        inCave = false;
        lastState = CaveState.EMPTY;
    }

    private void updateCaveTicks()
    {
        if (inCave)
        {
            caveTicks++;

            if (smoothedScore <= EXIT_CAVE_SCORE)
            {
                exitTicks++;
            }
            else
            {
                exitTicks = 0;
            }

            if (exitTicks >= EXIT_STABLE_TICKS)
            {
                inCave = false;
                caveTicks = 0;
                enterTicks = 0;
                exitTicks = 0;
            }
            return;
        }

        caveTicks = 0;
        if (smoothedScore >= ENTER_CAVE_SCORE)
        {
            enterTicks++;
        }
        else
        {
            enterTicks = 0;
        }

        if (enterTicks >= ENTER_STABLE_TICKS)
        {
            inCave = true;
            caveTicks = 0;
            enterTicks = 0;
            exitTicks = 0;
        }
    }

    private float checkCaveScore(ClientLevel level, LocalPlayer player)
    {
        if (player.isSpectator())
        {
            return 0.0F;
        }

        if (!allowNoSkyLightDimensions && !level.dimensionType().hasSkyLight())
        {
            return 0.0F;
        }

        BlockPos feet = player.blockPosition();
        BlockPos eyes = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());

        int skyLight = Math.max(
            level.getBrightness(LightLayer.SKY, feet),
            level.getBrightness(LightLayer.SKY, eyes)
        );
        int blockLight = Math.max(
            level.getBrightness(LightLayer.BLOCK, feet),
            level.getBrightness(LightLayer.BLOCK, eyes)
        );
        int surfaceY = level.getHeight(
            Heightmap.Types.WORLD_SURFACE,
            feet.getX(),
            feet.getZ()
        );
        int depthBelowSurface = surfaceY - feet.getY();

        float skyScore = 1.0F - Mth.clamp(skyLight / 12.0F, 0.0F, 1.0F);
        float depthScore = Mth.clamp((depthBelowSurface - 5) / 28.0F, 0.0F, 1.0F);
        float roofScore = roofScore(level, eyes, 18);
        float exposureScore = 1.0F - skyExposure(level, feet, 8);
        float enclosureScore = enclosureScore(level, feet);
        float artificialLightPenalty = Mth.clamp((blockLight - 10) / 20.0F, 0.0F, 0.25F);

        float score = skyScore * 0.32F
            + depthScore * 0.25F
            + roofScore * 0.18F
            + exposureScore * 0.15F
            + enclosureScore * 0.10F
            - artificialLightPenalty;

        if ((level.canSeeSky(eyes) || level.canSeeSky(feet.above())) && skyLight >= 8)
        {
            score *= 0.20F;
        }

        if (depthBelowSurface < 4 && skyLight > 2)
        {
            score *= 0.45F;
        }

        return Mth.clamp(score, 0.0F, 1.0F);
    }

    private static float roofScore(ClientLevel level, BlockPos eyes, int maxDistance)
    {
        for (int distance = 1; distance <= maxDistance; distance++)
        {
            BlockPos scanPos = eyes.above(distance);
            if (isOccluding(level, scanPos))
            {
                return 1.0F - ((distance - 1) / (float) maxDistance);
            }
        }
        return 0.0F;
    }

    private static float skyExposure(ClientLevel level, BlockPos center, int radius)
    {
        int exposed = 0;
        int total = 0;

        for (int x = -radius; x <= radius; x += 4)
        {
            for (int z = -radius; z <= radius; z += 4)
            {
                if (Math.abs(x) + Math.abs(z) > radius + 2)
                {
                    continue;
                }

                total++;
                if (level.canSeeSky(center.offset(x, 1, z)))
                {
                    exposed++;
                }
            }
        }

        return total == 0 ? 0.0F : exposed / (float) total;
    }

    private static float enclosureScore(ClientLevel level, BlockPos center)
    {
        int occluding = 0;

        for (int[] offset : ENCLOSURE_OFFSETS)
        {
            if (isOccluding(level, center.offset(offset[0], offset[1], offset[2])))
            {
                occluding++;
            }
        }

        return occluding / (float) ENCLOSURE_OFFSETS.length;
    }

    private static boolean isOccluding(ClientLevel level, BlockPos pos)
    {
        BlockState state = level.getBlockState(pos);
        return !state.isAir()
            && state.getFluidState().isEmpty()
            && (!state.getCollisionShape(level, pos).isEmpty() || state.getLightBlock(level, pos) >= 15);
    }

    public record CaveState(
        float rawScore,
        float smoothedScore,
        boolean inCave,
        int caveTicks,
        boolean goodForAmbience,
        boolean goodForMusic
    )
    {
        public static final CaveState EMPTY = new CaveState(0.0F, 0.0F, false, 0, false, false);
    }
}
