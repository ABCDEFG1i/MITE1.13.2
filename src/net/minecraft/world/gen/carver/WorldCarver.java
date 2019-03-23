package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import java.util.BitSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.gen.feature.IFeatureConfig;

public abstract class WorldCarver<C extends IFeatureConfig> implements IWorldCarver<C> {
   protected static final IBlockState DEFAULT_AIR = Blocks.AIR.getDefaultState();
   protected static final IBlockState DEFAULT_CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
   protected static final IFluidState WATER_FLUID = Fluids.WATER.getDefaultState();
   protected static final IFluidState LAVA_FLUID = Fluids.LAVA.getDefaultState();
   protected Set<Block> terrainBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE);
   protected Set<Fluid> terrainFluids = ImmutableSet.of(Fluids.WATER);

   public int func_202520_b() {
      return 4;
   }

   protected abstract boolean carveAtTarget(IWorld p_202516_1_, long p_202516_2_, int p_202516_4_, int p_202516_5_, double p_202516_6_, double p_202516_8_, double p_202516_10_, double p_202516_12_, double p_202516_14_, BitSet p_202516_16_);

   protected boolean isTargetAllowed(IBlockState p_202519_1_) {
      return this.terrainBlocks.contains(p_202519_1_.getBlock());
   }

   protected boolean isTargetSafeFromFalling(IBlockState p_202517_1_, IBlockState p_202517_2_) {
      Block block = p_202517_1_.getBlock();
      return this.isTargetAllowed(p_202517_1_) || (block == Blocks.SAND || block == Blocks.GRAVEL) && !p_202517_2_.getFluidState().isTagged(FluidTags.WATER);
   }

   protected boolean doesAreaHaveFluids(IWorldReaderBase p_202524_1_, int p_202524_2_, int p_202524_3_, int p_202524_4_, int p_202524_5_, int p_202524_6_, int p_202524_7_, int p_202524_8_, int p_202524_9_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int i = p_202524_4_; i < p_202524_5_; ++i) {
         for(int j = p_202524_8_; j < p_202524_9_; ++j) {
            for(int k = p_202524_6_ - 1; k <= p_202524_7_ + 1; ++k) {
               if (this.terrainFluids.contains(p_202524_1_.getFluidState(blockpos$mutableblockpos.setPos(i + p_202524_2_ * 16, k, j + p_202524_3_ * 16)).getFluid())) {
                  return true;
               }

               if (k != p_202524_7_ + 1 && !this.isInBounds(p_202524_4_, p_202524_5_, p_202524_8_, p_202524_9_, i, j)) {
                  k = p_202524_7_;
               }
            }
         }
      }

      return false;
   }

   private boolean isInBounds(int p_202514_1_, int p_202514_2_, int p_202514_3_, int p_202514_4_, int p_202514_5_, int p_202514_6_) {
      return p_202514_5_ == p_202514_1_ || p_202514_5_ == p_202514_2_ - 1 || p_202514_6_ == p_202514_3_ || p_202514_6_ == p_202514_4_ - 1;
   }

   protected boolean isWithinGenerationDepth(int p_202515_1_, int p_202515_2_, double p_202515_3_, double p_202515_5_, int p_202515_7_, int p_202515_8_, float p_202515_9_) {
      double d0 = (double)(p_202515_1_ * 16 + 8);
      double d1 = (double)(p_202515_2_ * 16 + 8);
      double d2 = p_202515_3_ - d0;
      double d3 = p_202515_5_ - d1;
      double d4 = (double)(p_202515_8_ - p_202515_7_);
      double d5 = (double)(p_202515_9_ + 2.0F + 16.0F);
      return d2 * d2 + d3 * d3 - d4 * d4 <= d5 * d5;
   }
}
