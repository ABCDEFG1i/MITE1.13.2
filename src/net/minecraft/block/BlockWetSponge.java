package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Particles;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockWetSponge extends Block {
   protected BlockWetSponge(Block.Properties p_i48294_1_) {
      super(p_i48294_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      EnumFacing enumfacing = EnumFacing.random(p_180655_4_);
      if (enumfacing != EnumFacing.UP && !p_180655_2_.getBlockState(p_180655_3_.offset(enumfacing)).isTopSolid()) {
         double d0 = (double)p_180655_3_.getX();
         double d1 = (double)p_180655_3_.getY();
         double d2 = (double)p_180655_3_.getZ();
         if (enumfacing == EnumFacing.DOWN) {
            d1 = d1 - 0.05D;
            d0 += p_180655_4_.nextDouble();
            d2 += p_180655_4_.nextDouble();
         } else {
            d1 = d1 + p_180655_4_.nextDouble() * 0.8D;
            if (enumfacing.getAxis() == EnumFacing.Axis.X) {
               d2 += p_180655_4_.nextDouble();
               if (enumfacing == EnumFacing.EAST) {
                  ++d0;
               } else {
                  d0 += 0.05D;
               }
            } else {
               d0 += p_180655_4_.nextDouble();
               if (enumfacing == EnumFacing.SOUTH) {
                  ++d2;
               } else {
                  d2 += 0.05D;
               }
            }
         }

         p_180655_2_.spawnParticle(Particles.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
      }
   }
}
