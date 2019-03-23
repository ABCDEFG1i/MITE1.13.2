package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockStainedGlass extends BlockBreakable {
   private final EnumDyeColor color;

   public BlockStainedGlass(EnumDyeColor p_i48323_1_, Block.Properties p_i48323_2_) {
      super(p_i48323_2_);
      this.color = p_i48323_1_;
   }

   public boolean func_200123_i(IBlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return true;
   }

   public EnumDyeColor getColor() {
      return this.color;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return 0;
   }

   protected boolean canSilkHarvest() {
      return true;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
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
