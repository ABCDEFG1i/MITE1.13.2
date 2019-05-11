package net.minecraft.world.dimension;

import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NetherGenSettings;
import net.minecraft.world.gen.UnderworldGenSetting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class UnderworldDimension extends Dimension {
    public UnderworldDimension(){}
    public void init() {
        this.hasSkyLight = false;
    }

    @OnlyIn(Dist.CLIENT)
    public Vec3d getFogColor(float p_76562_1_, float p_76562_2_) {
        float f = MathHelper.cos(p_76562_1_ * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        float f1 = 0.7529412F;
        float f2 = 0.84705883F;
        float f3 = 1.0F;
        f1 = f1 * (f * 0.94F + 0.06F);
        f2 = f2 * (f * 0.94F + 0.06F);
        f3 = f3 * (f * 0.91F + 0.09F);
        return new Vec3d((double)f1, (double)f2, (double)f3);
    }


    public IChunkGenerator<?> createChunkGenerator() {
        UnderworldGenSetting underworldGenSetting = ChunkGeneratorType.UNDERWORLD.createChunkGenSettings();
        underworldGenSetting.setDefaultBlock(Blocks.STONE.getDefaultState());
        underworldGenSetting.setDefaultFluid(Blocks.WATER.getDefaultState());
        return ChunkGeneratorType.UNDERWORLD.create(this.world, BiomeProviderType.FIXED.create(BiomeProviderType.FIXED.createSettings().setBiome(Biomes.UNDERWORLD)), underworldGenSetting);
    }

    public boolean isSurfaceWorld() {
        return false;
    }

    @Nullable
    public BlockPos findSpawn(ChunkPos p_206920_1_, boolean p_206920_2_) {
        return null;
    }

    @Nullable
    public BlockPos findSpawn(int p_206921_1_, int p_206921_2_, boolean p_206921_3_) {
        return null;
    }

    public float calculateCelestialAngle(long p_76563_1_, float p_76563_3_) {
        return 0.5F;
    }

    public boolean canRespawnHere() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean doesXZShowFog(int p_76568_1_, int p_76568_2_) {
        return false;
    }

    public WorldBorder createWorldBorder() {
        return new WorldBorder() {
            public double getCenterX() {
                return super.getCenterX() ;
            }

            public double getCenterZ() {
                return super.getCenterZ();
            }
        };
    }

    public DimensionType getType() {
        return DimensionType.UNDERWORLD;
    }
}
