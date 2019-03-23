package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Queue;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSponge extends Block {
   protected BlockSponge(Block.Properties p_i48325_1_) {
      super(p_i48325_1_);
   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      if (p_196259_4_.getBlock() != p_196259_1_.getBlock()) {
         this.tryAbsorb(p_196259_2_, p_196259_3_);
      }
   }

   public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
      this.tryAbsorb(p_189540_2_, p_189540_3_);
      super.neighborChanged(p_189540_1_, p_189540_2_, p_189540_3_, p_189540_4_, p_189540_5_);
   }

   protected void tryAbsorb(World p_196510_1_, BlockPos p_196510_2_) {
      if (this.absorb(p_196510_1_, p_196510_2_)) {
         p_196510_1_.setBlockState(p_196510_2_, Blocks.WET_SPONGE.getDefaultState(), 2);
         p_196510_1_.playEvent(2001, p_196510_2_, Block.getStateId(Blocks.WATER.getDefaultState()));
      }

   }

   private boolean absorb(World p_176312_1_, BlockPos p_176312_2_) {
      Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
      queue.add(new Tuple<>(p_176312_2_, 0));
      int i = 0;

      while(!queue.isEmpty()) {
         Tuple<BlockPos, Integer> tuple = queue.poll();
         BlockPos blockpos = tuple.getA();
         int j = tuple.getB();

         for(EnumFacing enumfacing : EnumFacing.values()) {
            BlockPos blockpos1 = blockpos.offset(enumfacing);
            IBlockState iblockstate = p_176312_1_.getBlockState(blockpos1);
            IFluidState ifluidstate = p_176312_1_.getFluidState(blockpos1);
            Material material = iblockstate.getMaterial();
            if (ifluidstate.isTagged(FluidTags.WATER)) {
               if (iblockstate.getBlock() instanceof IBucketPickupHandler && ((IBucketPickupHandler)iblockstate.getBlock()).pickupFluid(p_176312_1_, blockpos1, iblockstate) != Fluids.EMPTY) {
                  ++i;
                  if (j < 6) {
                     queue.add(new Tuple<>(blockpos1, j + 1));
                  }
               } else if (iblockstate.getBlock() instanceof BlockFlowingFluid) {
                  p_176312_1_.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 3);
                  ++i;
                  if (j < 6) {
                     queue.add(new Tuple<>(blockpos1, j + 1));
                  }
               } else if (material == Material.OCEAN_PLANT || material == Material.SEA_GRASS) {
                  iblockstate.dropBlockAsItem(p_176312_1_, blockpos1, 0);
                  p_176312_1_.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 3);
                  ++i;
                  if (j < 6) {
                     queue.add(new Tuple<>(blockpos1, j + 1));
                  }
               }
            }
         }

         if (i > 64) {
            break;
         }
      }

      return i > 0;
   }
}
