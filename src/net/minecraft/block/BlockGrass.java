package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.CompositeFlowerFeature;

public class BlockGrass extends BlockDirtSnowySpreadable implements IGrowable {
   public BlockGrass(Block.Properties p_i48388_1_) {
      super(p_i48388_1_);
   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, IBlockState p_176473_3_, boolean p_176473_4_) {
      return p_176473_1_.getBlockState(p_176473_2_.up()).isAir();
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, IBlockState p_180670_4_) {
      return true;
   }

   public void grow(World p_176474_1_, Random p_176474_2_, BlockPos p_176474_3_, IBlockState p_176474_4_) {
      BlockPos blockpos = p_176474_3_.up();
      IBlockState iblockstate = Blocks.GRASS.getDefaultState();

      for(int i = 0; i < 128; ++i) {
         BlockPos blockpos1 = blockpos;
         int j = 0;

         while(true) {
            if (j >= i / 16) {
               IBlockState iblockstate2 = p_176474_1_.getBlockState(blockpos1);
               if (iblockstate2.getBlock() == iblockstate.getBlock() && p_176474_2_.nextInt(10) == 0) {
                  ((IGrowable)iblockstate.getBlock()).grow(p_176474_1_, p_176474_2_, blockpos1, iblockstate2);
               }

               if (!iblockstate2.isAir()) {
                  break;
               }

               IBlockState iblockstate1;
               if (p_176474_2_.nextInt(8) == 0) {
                  List<CompositeFlowerFeature<?>> list = p_176474_1_.getBiome(blockpos1).getFlowers();
                  if (list.isEmpty()) {
                     break;
                  }

                  iblockstate1 = list.get(0).getRandomFlower(p_176474_2_, blockpos1);
               } else {
                  iblockstate1 = iblockstate;
               }

               if (iblockstate1.isValidPosition(p_176474_1_, blockpos1)) {
                  p_176474_1_.setBlockState(blockpos1, iblockstate1, 3);
               }
               break;
            }

            blockpos1 = blockpos1.add(p_176474_2_.nextInt(3) - 1, (p_176474_2_.nextInt(3) - 1) * p_176474_2_.nextInt(3) / 2, p_176474_2_.nextInt(3) - 1);
            if (p_176474_1_.getBlockState(blockpos1.down()).getBlock() != this || p_176474_1_.getBlockState(blockpos1).isBlockNormalCube()) {
               break;
            }

            ++j;
         }
      }

   }

   public boolean isSolid(IBlockState p_200124_1_) {
      return true;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT_MIPPED;
   }
}
