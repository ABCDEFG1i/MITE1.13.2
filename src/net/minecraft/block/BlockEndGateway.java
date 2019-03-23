package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockEndGateway extends BlockContainer {
   protected BlockEndGateway(Block.Properties p_i48407_1_) {
      super(p_i48407_1_);
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntityEndGateway();
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return 0;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      TileEntity tileentity = p_180655_2_.getTileEntity(p_180655_3_);
      if (tileentity instanceof TileEntityEndGateway) {
         int i = ((TileEntityEndGateway)tileentity).getParticleAmount();

         for(int j = 0; j < i; ++j) {
            double d0 = (double)((float)p_180655_3_.getX() + p_180655_4_.nextFloat());
            double d1 = (double)((float)p_180655_3_.getY() + p_180655_4_.nextFloat());
            double d2 = (double)((float)p_180655_3_.getZ() + p_180655_4_.nextFloat());
            double d3 = ((double)p_180655_4_.nextFloat() - 0.5D) * 0.5D;
            double d4 = ((double)p_180655_4_.nextFloat() - 0.5D) * 0.5D;
            double d5 = ((double)p_180655_4_.nextFloat() - 0.5D) * 0.5D;
            int k = p_180655_4_.nextInt(2) * 2 - 1;
            if (p_180655_4_.nextBoolean()) {
               d2 = (double)p_180655_3_.getZ() + 0.5D + 0.25D * (double)k;
               d5 = (double)(p_180655_4_.nextFloat() * 2.0F * (float)k);
            } else {
               d0 = (double)p_180655_3_.getX() + 0.5D + 0.25D * (double)k;
               d3 = (double)(p_180655_4_.nextFloat() * 2.0F * (float)k);
            }

            p_180655_2_.spawnParticle(Particles.PORTAL, d0, d1, d2, d3, d4, d5);
         }

      }
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, IBlockState p_185473_3_) {
      return ItemStack.EMPTY;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
