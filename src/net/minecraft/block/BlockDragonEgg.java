package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockDragonEgg extends BlockFalling {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   public BlockDragonEgg(Block.Properties p_i48411_1_) {
      super(p_i48411_1_);
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPE;
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      this.teleport(p_196250_1_, p_196250_2_, p_196250_3_);
      return true;
   }

   public void onBlockClicked(IBlockState p_196270_1_, World p_196270_2_, BlockPos p_196270_3_, EntityPlayer p_196270_4_) {
      this.teleport(p_196270_1_, p_196270_2_, p_196270_3_);
   }

   private void teleport(IBlockState p_196443_1_, World p_196443_2_, BlockPos p_196443_3_) {
      for(int i = 0; i < 1000; ++i) {
         BlockPos blockpos = p_196443_3_.add(p_196443_2_.rand.nextInt(16) - p_196443_2_.rand.nextInt(16), p_196443_2_.rand.nextInt(8) - p_196443_2_.rand.nextInt(8), p_196443_2_.rand.nextInt(16) - p_196443_2_.rand.nextInt(16));
         if (p_196443_2_.getBlockState(blockpos).isAir()) {
            if (p_196443_2_.isRemote) {
               for(int j = 0; j < 128; ++j) {
                  double d0 = p_196443_2_.rand.nextDouble();
                  float f = (p_196443_2_.rand.nextFloat() - 0.5F) * 0.2F;
                  float f1 = (p_196443_2_.rand.nextFloat() - 0.5F) * 0.2F;
                  float f2 = (p_196443_2_.rand.nextFloat() - 0.5F) * 0.2F;
                  double d1 = (double)blockpos.getX() + (double)(p_196443_3_.getX() - blockpos.getX()) * d0 + (p_196443_2_.rand.nextDouble() - 0.5D) + 0.5D;
                  double d2 = (double)blockpos.getY() + (double)(p_196443_3_.getY() - blockpos.getY()) * d0 + p_196443_2_.rand.nextDouble() - 0.5D;
                  double d3 = (double)blockpos.getZ() + (double)(p_196443_3_.getZ() - blockpos.getZ()) * d0 + (p_196443_2_.rand.nextDouble() - 0.5D) + 0.5D;
                  p_196443_2_.spawnParticle(Particles.PORTAL, d1, d2, d3, (double)f, (double)f1, (double)f2);
               }
            } else {
               p_196443_2_.setBlockState(blockpos, p_196443_1_, 2);
               p_196443_2_.removeBlock(p_196443_3_);
            }

            return;
         }
      }

   }

   public int tickRate(IWorldReaderBase p_149738_1_) {
      return 5;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
