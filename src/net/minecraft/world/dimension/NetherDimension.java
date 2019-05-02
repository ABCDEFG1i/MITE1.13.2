package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NetherGenSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NetherDimension extends Dimension {
   public void init() {
      this.doesWaterVaporize = true;
      this.nether = true;
      this.hasSkyLight = false;
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d getFogColor(float p_76562_1_, float p_76562_2_) {
      return new Vec3d((double)0.2F, (double)0.03F, (double)0.03F);
   }

   protected void generateLightBrightnessTable() {
      float f = 0.1F;

      for(int i = 0; i <= 15; ++i) {
         float f1 = 1.0F - (float)i / 15.0F;
         this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * 0.9F + 0.1F;
      }

   }

   public IChunkGenerator<?> createChunkGenerator() {
      NetherGenSettings nethergensettings = ChunkGeneratorType.CAVES.createChunkGenSettings();
      nethergensettings.setDefaultBlock(Blocks.NETHERRACK.getDefaultState());
      nethergensettings.setDefaultFluid(Blocks.LAVA.getDefaultState());
      return ChunkGeneratorType.CAVES.create(this.world, BiomeProviderType.FIXED.create(BiomeProviderType.FIXED.createSettings().setBiome(Biomes.NETHER)), nethergensettings);
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
      return true;
   }

   public WorldBorder createWorldBorder() {
      return new WorldBorder() {
         public double getCenterX() {
            return super.getCenterX() / 8.0D;
         }

         public double getCenterZ() {
            return super.getCenterZ() / 8.0D;
         }
      };
   }

   public DimensionType getType() {
      return DimensionType.NETHER;
   }
}
