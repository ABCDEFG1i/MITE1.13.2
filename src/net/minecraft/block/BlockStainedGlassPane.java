package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockStainedGlassPane extends BlockGlassPane {
   private final EnumDyeColor color;

   public BlockStainedGlassPane(EnumDyeColor p_i48322_1_, Block.Properties p_i48322_2_) {
      super(p_i48322_2_);
      this.color = p_i48322_1_;
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   public EnumDyeColor getColor() {
      return this.color;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      if (p_196259_4_.getBlock() != p_196259_1_.getBlock()) {
         if (!p_196259_2_.isRemote) {
            BlockBeacon.updateColorAsync(p_196259_2_, p_196259_3_);
         }

      }
   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         if (!p_196243_2_.isRemote) {
            BlockBeacon.updateColorAsync(p_196243_2_, p_196243_3_);
         }

      }
   }
}
