package net.minecraft.world.gen;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;

public class ChunkGeneratorEnd extends AbstractChunkGenerator<EndGenSettings> {
   protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
   private NoiseGeneratorOctaves lperlinNoise1;
   private NoiseGeneratorOctaves lperlinNoise2;
   private NoiseGeneratorOctaves perlinNoise1;
   private final NoiseGeneratorOctaves noiseGen5;
   private final NoiseGeneratorOctaves noiseGen6;
   private final NoiseGeneratorPerlin field_205478_l;
   private final BlockPos spawnPoint;
   private final EndGenSettings settings;
   private final IBlockState defaultBlock;
   private final IBlockState defaultFluid;

   public ChunkGeneratorEnd(IWorld p_i48956_1_, BiomeProvider p_i48956_2_, EndGenSettings p_i48956_3_) {
      super(p_i48956_1_, p_i48956_2_);
      this.settings = p_i48956_3_;
      this.defaultBlock = this.settings.getDefaultBlock();
      this.defaultFluid = this.settings.getDefaultFluid();
      this.spawnPoint = p_i48956_3_.getSpawnPos();
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom(this.seed);
      this.lperlinNoise1 = new NoiseGeneratorOctaves(sharedseedrandom, 16);
      this.lperlinNoise2 = new NoiseGeneratorOctaves(sharedseedrandom, 16);
      this.perlinNoise1 = new NoiseGeneratorOctaves(sharedseedrandom, 8);
      this.noiseGen5 = new NoiseGeneratorOctaves(sharedseedrandom, 10);
      this.noiseGen6 = new NoiseGeneratorOctaves(sharedseedrandom, 16);
      sharedseedrandom.skip(262);
      this.field_205478_l = new NoiseGeneratorPerlin(new SharedSeedRandom(this.seed), 4);
   }

   public void prepareHeights(int p_202114_1_, int p_202114_2_, IChunk p_202114_3_) {
      int i = 2;
      int j = 3;
      int k = 33;
      int l = 3;
      double[] adouble = this.func_202113_a(p_202114_1_ * 2, 0, p_202114_2_ * 2, 3, 33, 3);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int i1 = 0; i1 < 2; ++i1) {
         for(int j1 = 0; j1 < 2; ++j1) {
            for(int k1 = 0; k1 < 32; ++k1) {
               double d0 = 0.25D;
               double d1 = adouble[((i1 + 0) * 3 + j1 + 0) * 33 + k1 + 0];
               double d2 = adouble[((i1 + 0) * 3 + j1 + 1) * 33 + k1 + 0];
               double d3 = adouble[((i1 + 1) * 3 + j1 + 0) * 33 + k1 + 0];
               double d4 = adouble[((i1 + 1) * 3 + j1 + 1) * 33 + k1 + 0];
               double d5 = (adouble[((i1 + 0) * 3 + j1 + 0) * 33 + k1 + 1] - d1) * 0.25D;
               double d6 = (adouble[((i1 + 0) * 3 + j1 + 1) * 33 + k1 + 1] - d2) * 0.25D;
               double d7 = (adouble[((i1 + 1) * 3 + j1 + 0) * 33 + k1 + 1] - d3) * 0.25D;
               double d8 = (adouble[((i1 + 1) * 3 + j1 + 1) * 33 + k1 + 1] - d4) * 0.25D;

               for(int l1 = 0; l1 < 4; ++l1) {
                  double d9 = 0.125D;
                  double d10 = d1;
                  double d11 = d2;
                  double d12 = (d3 - d1) * 0.125D;
                  double d13 = (d4 - d2) * 0.125D;

                  for(int i2 = 0; i2 < 8; ++i2) {
                     double d14 = 0.125D;
                     double d15 = d10;
                     double d16 = (d11 - d10) * 0.125D;

                     for(int j2 = 0; j2 < 8; ++j2) {
                        IBlockState iblockstate = AIR;
                        if (d15 > 0.0D) {
                           iblockstate = this.defaultBlock;
                        }

                        int k2 = i2 + i1 * 8;
                        int l2 = l1 + k1 * 4;
                        int i3 = j2 + j1 * 8;
                        p_202114_3_.setBlockState(blockpos$mutableblockpos.setPos(k2, l2, i3), iblockstate, false);
                        d15 += d16;
                     }

                     d10 += d12;
                     d11 += d13;
                  }

                  d1 += d5;
                  d2 += d6;
                  d3 += d7;
                  d4 += d8;
               }
            }
         }
      }

   }

   public void makeBase(IChunk p_202088_1_) {
      ChunkPos chunkpos = p_202088_1_.getPos();
      int i = chunkpos.x;
      int j = chunkpos.z;
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
      sharedseedrandom.setSeed(i, j);
      Biome[] abiome = this.biomeProvider.func_201539_b(i * 16, j * 16, 16, 16);
      p_202088_1_.setBiomes(abiome);
      this.prepareHeights(i, j, p_202088_1_);
      this.buildSurface(p_202088_1_, abiome, sharedseedrandom, 0);
      p_202088_1_.createHeightMap(Heightmap.Type.WORLD_SURFACE_WG, Heightmap.Type.OCEAN_FLOOR_WG);
      p_202088_1_.setStatus(ChunkStatus.BASE);
   }

   private double[] func_202113_a(int p_202113_1_, int p_202113_2_, int p_202113_3_, int p_202113_4_, int p_202113_5_, int p_202113_6_) {
      double[] adouble = new double[p_202113_4_ * p_202113_5_ * p_202113_6_];
      double d0 = 684.412D;
      double d1 = 684.412D;
      d0 = d0 * 2.0D;
      double[] adouble1 = this.perlinNoise1.func_202647_a(p_202113_1_, p_202113_2_, p_202113_3_, p_202113_4_, p_202113_5_, p_202113_6_, d0 / 80.0D, 4.277575000000001D, d0 / 80.0D);
      double[] adouble2 = this.lperlinNoise1.func_202647_a(p_202113_1_, p_202113_2_, p_202113_3_, p_202113_4_, p_202113_5_, p_202113_6_, d0, 684.412D, d0);
      double[] adouble3 = this.lperlinNoise2.func_202647_a(p_202113_1_, p_202113_2_, p_202113_3_, p_202113_4_, p_202113_5_, p_202113_6_, d0, 684.412D, d0);
      int i = p_202113_1_ / 2;
      int j = p_202113_3_ / 2;
      int k = 0;

      for(int l = 0; l < p_202113_4_; ++l) {
         for(int i1 = 0; i1 < p_202113_6_; ++i1) {
            float f = this.biomeProvider.func_201536_c(i, j, l, i1);

            for(int j1 = 0; j1 < p_202113_5_; ++j1) {
               double d2 = adouble2[k] / 512.0D;
               double d3 = adouble3[k] / 512.0D;
               double d5 = (adouble1[k] / 10.0D + 1.0D) / 2.0D;
               double d4;
               if (d5 < 0.0D) {
                  d4 = d2;
               } else if (d5 > 1.0D) {
                  d4 = d3;
               } else {
                  d4 = d2 + (d3 - d2) * d5;
               }

               d4 = d4 - 8.0D;
               d4 = d4 + (double)f;
               int k1 = 2;
               if (j1 > p_202113_5_ / 2 - k1) {
                  double d6 = (double)((float)(j1 - (p_202113_5_ / 2 - k1)) / 64.0F);
                  d6 = MathHelper.clamp(d6, 0.0D, 1.0D);
                  d4 = d4 * (1.0D - d6) - 3000.0D * d6;
               }

               k1 = 8;
               if (j1 < k1) {
                  double d7 = (double)((float)(k1 - j1) / ((float)k1 - 1.0F));
                  d4 = d4 * (1.0D - d7) - 30.0D * d7;
               }

               adouble[k] = d4;
               ++k;
            }
         }
      }

      return adouble;
   }

   public void spawnMobs(WorldGenRegion p_202093_1_) {
   }

   public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType p_177458_1_, BlockPos p_177458_2_) {
      return this.world.getBiome(p_177458_2_).getSpawnableList(p_177458_1_);
   }

   public BlockPos getSpawnPoint() {
      return this.spawnPoint;
   }

   public int spawnMobs(World p_203222_1_, boolean p_203222_2_, boolean p_203222_3_) {
      return 0;
   }

   public EndGenSettings getSettings() {
      return this.settings;
   }

   public double[] generateNoiseRegion(int p_205473_1_, int p_205473_2_) {
      double d0 = 0.03125D;
      return this.field_205478_l.generateRegion((double)(p_205473_1_ << 4), (double)(p_205473_2_ << 4), 16, 16, 0.0625D, 0.0625D, 1.0D);
   }

   public int getGroundHeight() {
      return 50;
   }
}
