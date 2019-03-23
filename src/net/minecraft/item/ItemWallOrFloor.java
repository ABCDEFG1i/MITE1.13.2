package net.minecraft.item;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;

public class ItemWallOrFloor extends ItemBlock {
   protected final Block wallBlock;

   public ItemWallOrFloor(Block p_i48462_1_, Block p_i48462_2_, Item.Properties p_i48462_3_) {
      super(p_i48462_1_, p_i48462_3_);
      this.wallBlock = p_i48462_2_;
   }

   @Nullable
   protected IBlockState getStateForPlacement(BlockItemUseContext p_195945_1_) {
      IBlockState iblockstate = this.wallBlock.getStateForPlacement(p_195945_1_);
      IBlockState iblockstate1 = null;
      IWorldReaderBase iworldreaderbase = p_195945_1_.getWorld();
      BlockPos blockpos = p_195945_1_.getPos();

      for(EnumFacing enumfacing : p_195945_1_.func_196009_e()) {
         if (enumfacing != EnumFacing.UP) {
            IBlockState iblockstate2 = enumfacing == EnumFacing.DOWN ? this.getBlock().getStateForPlacement(p_195945_1_) : iblockstate;
            if (iblockstate2 != null && iblockstate2.isValidPosition(iworldreaderbase, blockpos)) {
               iblockstate1 = iblockstate2;
               break;
            }
         }
      }

      return iblockstate1 != null && iworldreaderbase.checkNoEntityCollision(iblockstate1, blockpos) ? iblockstate1 : null;
   }

   public void addToBlockToItemMap(Map<Block, Item> p_195946_1_, Item p_195946_2_) {
      super.addToBlockToItemMap(p_195946_1_, p_195946_2_);
      p_195946_1_.put(this.wallBlock, p_195946_2_);
   }
}
