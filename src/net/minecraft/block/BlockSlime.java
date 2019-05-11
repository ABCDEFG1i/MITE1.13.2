package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockSlime extends BlockBreakable {
   public BlockSlime(Block.Properties p_i48330_1_) {
      super(p_i48330_1_);
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public void onFallenUpon(World p_180658_1_, BlockPos p_180658_2_, Entity p_180658_3_, float p_180658_4_) {
      if (p_180658_3_.isSneaking()) {
         super.onFallenUpon(p_180658_1_, p_180658_2_, p_180658_3_, p_180658_4_);
      } else {
         p_180658_3_.fall(p_180658_4_, 0.0F, false);
      }

   }

   public void onLanded(IBlockReader p_176216_1_, Entity p_176216_2_) {
      if (p_176216_2_.isSneaking()) {
         super.onLanded(p_176216_1_, p_176216_2_);
      } else if (p_176216_2_.motionY < 0.0D) {
         p_176216_2_.motionY = -p_176216_2_.motionY;
         if (!(p_176216_2_ instanceof EntityLivingBase)) {
            p_176216_2_.motionY *= 0.8D;
         }
      }

   }

   public void onEntityWalk(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {
      if (Math.abs(p_176199_3_.motionY) < 0.1D && !p_176199_3_.isSneaking()) {
         double d0 = 0.4D + Math.abs(p_176199_3_.motionY) * 0.2D;
         p_176199_3_.motionX *= d0;
         p_176199_3_.motionZ *= d0;
      }

      super.onEntityWalk(p_176199_1_, p_176199_2_, p_176199_3_);
   }

   public int getOpacity(IBlockState p_200011_1_, IBlockReader p_200011_2_, BlockPos p_200011_3_) {
      return 0;
   }
}
