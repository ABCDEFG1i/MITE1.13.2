package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRedstoneLamp extends Block {
   public static final BooleanProperty LIT = BlockRedstoneTorch.LIT;

   public BlockRedstoneLamp(Block.Properties p_i48343_1_) {
      super(p_i48343_1_);
      this.setDefaultState(this.getDefaultState().with(LIT, Boolean.valueOf(false)));
   }

   public int getLightValue(IBlockState p_149750_1_) {
      return p_149750_1_.get(LIT) ? super.getLightValue(p_149750_1_) : 0;
   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      super.onBlockAdded(p_196259_1_, p_196259_2_, p_196259_3_, p_196259_4_);
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getDefaultState().with(LIT, Boolean.valueOf(p_196258_1_.getWorld().isBlockPowered(p_196258_1_.getPos())));
   }

   public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
      if (!p_189540_2_.isRemote) {
         boolean flag = p_189540_1_.get(LIT);
         if (flag != p_189540_2_.isBlockPowered(p_189540_3_)) {
            if (flag) {
               p_189540_2_.getPendingBlockTicks().scheduleTick(p_189540_3_, this, 4);
            } else {
               p_189540_2_.setBlockState(p_189540_3_, p_189540_1_.cycle(LIT), 2);
            }
         }

      }
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (!p_196267_2_.isRemote) {
         if (p_196267_1_.get(LIT) && !p_196267_2_.isBlockPowered(p_196267_3_)) {
            p_196267_2_.setBlockState(p_196267_3_, p_196267_1_.cycle(LIT), 2);
         }

      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(LIT);
   }
}
