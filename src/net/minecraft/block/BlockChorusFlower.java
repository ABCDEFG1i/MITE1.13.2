package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockChorusFlower extends Block {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_5;
   private final BlockChorusPlant field_196405_b;

   protected BlockChorusFlower(BlockChorusPlant p_i48429_1_, Block.Properties p_i48429_2_) {
      super(p_i48429_2_);
      this.field_196405_b = p_i48429_1_;
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Items.AIR;
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (!p_196267_1_.isValidPosition(p_196267_2_, p_196267_3_)) {
         p_196267_2_.destroyBlock(p_196267_3_, true);
      } else {
         BlockPos blockpos = p_196267_3_.up();
         if (p_196267_2_.isAirBlock(blockpos) && blockpos.getY() < 256) {
            int i = p_196267_1_.get(AGE);
            if (i < 5) {
               boolean flag = false;
               boolean flag1 = false;
               IBlockState iblockstate = p_196267_2_.getBlockState(p_196267_3_.down());
               Block block = iblockstate.getBlock();
               if (block == Blocks.END_STONE) {
                  flag = true;
               } else if (block == this.field_196405_b) {
                  int j = 1;

                  for(int k = 0; k < 4; ++k) {
                     Block block1 = p_196267_2_.getBlockState(p_196267_3_.down(j + 1)).getBlock();
                     if (block1 != this.field_196405_b) {
                        if (block1 == Blocks.END_STONE) {
                           flag1 = true;
                        }
                        break;
                     }

                     ++j;
                  }

                  if (j < 2 || j <= p_196267_4_.nextInt(flag1 ? 5 : 4)) {
                     flag = true;
                  }
               } else if (iblockstate.isAir()) {
                  flag = true;
               }

               if (flag && areAllNeighborsEmpty(p_196267_2_, blockpos, null) && p_196267_2_.isAirBlock(p_196267_3_.up(2))) {
                  p_196267_2_.setBlockState(p_196267_3_, this.field_196405_b.makeConnections(p_196267_2_, p_196267_3_), 2);
                  this.placeGrownFlower(p_196267_2_, blockpos, i);
               } else if (i < 4) {
                  int l = p_196267_4_.nextInt(4);
                  if (flag1) {
                     ++l;
                  }

                  boolean flag2 = false;

                  for(int i1 = 0; i1 < l; ++i1) {
                     EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(p_196267_4_);
                     BlockPos blockpos1 = p_196267_3_.offset(enumfacing);
                     if (p_196267_2_.isAirBlock(blockpos1) && p_196267_2_.isAirBlock(blockpos1.down()) && areAllNeighborsEmpty(p_196267_2_, blockpos1, enumfacing.getOpposite())) {
                        this.placeGrownFlower(p_196267_2_, blockpos1, i + 1);
                        flag2 = true;
                     }
                  }

                  if (flag2) {
                     p_196267_2_.setBlockState(p_196267_3_, this.field_196405_b.makeConnections(p_196267_2_, p_196267_3_), 2);
                  } else {
                     this.placeDeadFlower(p_196267_2_, p_196267_3_);
                  }
               } else {
                  this.placeDeadFlower(p_196267_2_, p_196267_3_);
               }

            }
         }
      }
   }

   private void placeGrownFlower(World p_185602_1_, BlockPos p_185602_2_, int p_185602_3_) {
      p_185602_1_.setBlockState(p_185602_2_, this.getDefaultState().with(AGE, Integer.valueOf(p_185602_3_)), 2);
      p_185602_1_.playEvent(1033, p_185602_2_, 0);
   }

   private void placeDeadFlower(World p_185605_1_, BlockPos p_185605_2_) {
      p_185605_1_.setBlockState(p_185605_2_, this.getDefaultState().with(AGE, Integer.valueOf(5)), 2);
      p_185605_1_.playEvent(1034, p_185605_2_, 0);
   }

   private static boolean areAllNeighborsEmpty(IWorldReaderBase p_185604_0_, BlockPos p_185604_1_, @Nullable EnumFacing p_185604_2_) {
      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         if (enumfacing != p_185604_2_ && !p_185604_0_.isAirBlock(p_185604_1_.offset(enumfacing))) {
            return false;
         }
      }

      return true;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ != EnumFacing.UP && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      IBlockState iblockstate = p_196260_2_.getBlockState(p_196260_3_.down());
      Block block = iblockstate.getBlock();
      if (block != this.field_196405_b && block != Blocks.END_STONE) {
         if (!iblockstate.isAir()) {
            return false;
         } else {
            boolean flag = false;

            for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
               IBlockState iblockstate1 = p_196260_2_.getBlockState(p_196260_3_.offset(enumfacing));
               if (iblockstate1.getBlock() == this.field_196405_b) {
                  if (flag) {
                     return false;
                  }

                  flag = true;
               } else if (!iblockstate1.isAir()) {
                  return false;
               }
            }

            return flag;
         }
      } else {
         return true;
      }
   }

   public void harvestBlock(World p_180657_1_, EntityPlayer p_180657_2_, BlockPos p_180657_3_, IBlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      super.harvestBlock(p_180657_1_, p_180657_2_, p_180657_3_, p_180657_4_, p_180657_5_, p_180657_6_);
      spawnAsEntity(p_180657_1_, p_180657_3_, new ItemStack(this));
   }

   protected ItemStack getSilkTouchDrop(IBlockState p_180643_1_) {
      return ItemStack.EMPTY;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   public static void generatePlant(IWorld p_185603_0_, BlockPos p_185603_1_, Random p_185603_2_, int p_185603_3_) {
      p_185603_0_.setBlockState(p_185603_1_, ((BlockChorusPlant)Blocks.CHORUS_PLANT).makeConnections(p_185603_0_, p_185603_1_), 2);
      growTreeRecursive(p_185603_0_, p_185603_1_, p_185603_2_, p_185603_1_, p_185603_3_, 0);
   }

   private static void growTreeRecursive(IWorld p_185601_0_, BlockPos p_185601_1_, Random p_185601_2_, BlockPos p_185601_3_, int p_185601_4_, int p_185601_5_) {
      BlockChorusPlant blockchorusplant = (BlockChorusPlant)Blocks.CHORUS_PLANT;
      int i = p_185601_2_.nextInt(4) + 1;
      if (p_185601_5_ == 0) {
         ++i;
      }

      for(int j = 0; j < i; ++j) {
         BlockPos blockpos = p_185601_1_.up(j + 1);
         if (!areAllNeighborsEmpty(p_185601_0_, blockpos, null)) {
            return;
         }

         p_185601_0_.setBlockState(blockpos, blockchorusplant.makeConnections(p_185601_0_, blockpos), 2);
         p_185601_0_.setBlockState(blockpos.down(), blockchorusplant.makeConnections(p_185601_0_, blockpos.down()), 2);
      }

      boolean flag = false;
      if (p_185601_5_ < 4) {
         int l = p_185601_2_.nextInt(4);
         if (p_185601_5_ == 0) {
            ++l;
         }

         for(int k = 0; k < l; ++k) {
            EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(p_185601_2_);
            BlockPos blockpos1 = p_185601_1_.up(i).offset(enumfacing);
            if (Math.abs(blockpos1.getX() - p_185601_3_.getX()) < p_185601_4_ && Math.abs(blockpos1.getZ() - p_185601_3_.getZ()) < p_185601_4_ && p_185601_0_.isAirBlock(blockpos1) && p_185601_0_.isAirBlock(blockpos1.down()) && areAllNeighborsEmpty(p_185601_0_, blockpos1, enumfacing.getOpposite())) {
               flag = true;
               p_185601_0_.setBlockState(blockpos1, blockchorusplant.makeConnections(p_185601_0_, blockpos1), 2);
               p_185601_0_.setBlockState(blockpos1.offset(enumfacing.getOpposite()), blockchorusplant.makeConnections(p_185601_0_, blockpos1.offset(enumfacing.getOpposite())), 2);
               growTreeRecursive(p_185601_0_, blockpos1, p_185601_2_, p_185601_3_, p_185601_4_, p_185601_5_ + 1);
            }
         }
      }

      if (!flag) {
         p_185601_0_.setBlockState(p_185601_1_.up(i), Blocks.CHORUS_FLOWER.getDefaultState().with(AGE, Integer.valueOf(5)), 2);
      }

   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
