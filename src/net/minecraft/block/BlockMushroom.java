package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class BlockMushroom extends BlockBush implements IGrowable {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

   public BlockMushroom(Block.Properties p_i48363_1_) {
      super(p_i48363_1_);
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPE;
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (p_196267_4_.nextInt(25) == 0) {
         int i = 5;
         int j = 4;

         for(BlockPos blockpos : BlockPos.getAllInBoxMutable(p_196267_3_.add(-4, -1, -4), p_196267_3_.add(4, 1, 4))) {
            if (p_196267_2_.getBlockState(blockpos).getBlock() == this) {
               --i;
               if (i <= 0) {
                  return;
               }
            }
         }

         BlockPos blockpos1 = p_196267_3_.add(p_196267_4_.nextInt(3) - 1, p_196267_4_.nextInt(2) - p_196267_4_.nextInt(2), p_196267_4_.nextInt(3) - 1);

         for(int k = 0; k < 4; ++k) {
            if (p_196267_2_.isAirBlock(blockpos1) && p_196267_1_.isValidPosition(p_196267_2_, blockpos1)) {
               p_196267_3_ = blockpos1;
            }

            blockpos1 = p_196267_3_.add(p_196267_4_.nextInt(3) - 1, p_196267_4_.nextInt(2) - p_196267_4_.nextInt(2), p_196267_4_.nextInt(3) - 1);
         }

         if (p_196267_2_.isAirBlock(blockpos1) && p_196267_1_.isValidPosition(p_196267_2_, blockpos1)) {
            p_196267_2_.setBlockState(blockpos1, p_196267_1_, 2);
         }
      }

   }

   protected boolean isValidGround(IBlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return p_200014_1_.isOpaqueCube(p_200014_2_, p_200014_3_);
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      BlockPos blockpos = p_196260_3_.down();
      IBlockState iblockstate = p_196260_2_.getBlockState(blockpos);
      Block block = iblockstate.getBlock();
      if (block != Blocks.MYCELIUM && block != Blocks.PODZOL) {
         return p_196260_2_.getLightSubtracted(p_196260_3_, 0) < 13 && this.isValidGround(iblockstate, p_196260_2_, blockpos);
      } else {
         return true;
      }
   }

   public boolean generateBigMushroom(IWorld p_176485_1_, BlockPos p_176485_2_, IBlockState p_176485_3_, Random p_176485_4_) {
      p_176485_1_.removeBlock(p_176485_2_);
      Feature<NoFeatureConfig> feature = null;
      if (this == Blocks.BROWN_MUSHROOM) {
         feature = Feature.BIG_BROWN_MUSHROOM;
      } else if (this == Blocks.RED_MUSHROOM) {
         feature = Feature.BIG_RED_MUSHROOM;
      }

      if (feature != null && feature.func_212245_a(p_176485_1_, p_176485_1_.getChunkProvider().getChunkGenerator(), p_176485_4_, p_176485_2_, IFeatureConfig.NO_FEATURE_CONFIG)) {
         return true;
      } else {
         p_176485_1_.setBlockState(p_176485_2_, p_176485_3_, 3);
         return false;
      }
   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, IBlockState p_176473_3_, boolean p_176473_4_) {
      return true;
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, IBlockState p_180670_4_) {
      return (double)p_180670_2_.nextFloat() < 0.4D;
   }

   public void grow(World p_176474_1_, Random p_176474_2_, BlockPos p_176474_3_, IBlockState p_176474_4_) {
      this.generateBigMushroom(p_176474_1_, p_176474_3_, p_176474_4_, p_176474_2_);
   }

   public boolean needsPostProcessing(IBlockState p_201783_1_, IBlockReader p_201783_2_, BlockPos p_201783_3_) {
      return true;
   }
}
