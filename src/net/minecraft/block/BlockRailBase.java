package net.minecraft.block;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class BlockRailBase extends Block {
   protected static final VoxelShape FLAT_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   protected static final VoxelShape ASCENDING_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   private final boolean disableCorners;

   public static boolean isRail(World p_208488_0_, BlockPos p_208488_1_) {
      return isRail(p_208488_0_.getBlockState(p_208488_1_));
   }

   public static boolean isRail(IBlockState p_208487_0_) {
      return p_208487_0_.isIn(BlockTags.RAILS);
   }

   protected BlockRailBase(boolean p_i48444_1_, Block.Properties p_i48444_2_) {
      super(p_i48444_2_);
      this.disableCorners = p_i48444_1_;
   }

   public boolean areCornersDisabled() {
      return this.disableCorners;
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      RailShape railshape = p_196244_1_.getBlock() == this ? p_196244_1_.get(this.getShapeProperty()) : null;
      return railshape != null && railshape.isAscending() ? ASCENDING_AABB : FLAT_AABB;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      return p_196260_2_.getBlockState(p_196260_3_.down()).isTopSolid();
   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      if (p_196259_4_.getBlock() != p_196259_1_.getBlock()) {
         if (!p_196259_2_.isRemote) {
            p_196259_1_ = this.func_208489_a(p_196259_2_, p_196259_3_, p_196259_1_, true);
            if (this.disableCorners) {
               p_196259_1_.neighborChanged(p_196259_2_, p_196259_3_, this, p_196259_3_);
            }
         }

      }
   }

   public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
      if (!p_189540_2_.isRemote) {
         RailShape railshape = p_189540_1_.get(this.getShapeProperty());
         boolean flag = false;
         if (!p_189540_2_.getBlockState(p_189540_3_.down()).isTopSolid()) {
            flag = true;
         }

         if (railshape == RailShape.ASCENDING_EAST && !p_189540_2_.getBlockState(p_189540_3_.east()).isTopSolid()) {
            flag = true;
         } else if (railshape == RailShape.ASCENDING_WEST && !p_189540_2_.getBlockState(p_189540_3_.west()).isTopSolid()) {
            flag = true;
         } else if (railshape == RailShape.ASCENDING_NORTH && !p_189540_2_.getBlockState(p_189540_3_.north()).isTopSolid()) {
            flag = true;
         } else if (railshape == RailShape.ASCENDING_SOUTH && !p_189540_2_.getBlockState(p_189540_3_.south()).isTopSolid()) {
            flag = true;
         }

         if (flag && !p_189540_2_.isAirBlock(p_189540_3_)) {
            p_189540_1_.dropBlockAsItemWithChance(p_189540_2_, p_189540_3_, 1.0F, 0);
            p_189540_2_.removeBlock(p_189540_3_);
         } else {
            this.updateState(p_189540_1_, p_189540_2_, p_189540_3_, p_189540_4_);
         }

      }
   }

   protected void updateState(IBlockState p_189541_1_, World p_189541_2_, BlockPos p_189541_3_, Block p_189541_4_) {
   }

   protected IBlockState func_208489_a(World p_208489_1_, BlockPos p_208489_2_, IBlockState p_208489_3_, boolean p_208489_4_) {
      return p_208489_1_.isRemote ? p_208489_3_ : (new BlockRailState(p_208489_1_, p_208489_2_, p_208489_3_)).update(p_208489_1_.isBlockPowered(p_208489_2_), p_208489_4_).getNewState();
   }

   public EnumPushReaction getPushReaction(IBlockState p_149656_1_) {
      return EnumPushReaction.NORMAL;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_) {
         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
         if (p_196243_1_.get(this.getShapeProperty()).isAscending()) {
            p_196243_2_.notifyNeighborsOfStateChange(p_196243_3_.up(), this);
         }

         if (this.disableCorners) {
            p_196243_2_.notifyNeighborsOfStateChange(p_196243_3_, this);
            p_196243_2_.notifyNeighborsOfStateChange(p_196243_3_.down(), this);
         }

      }
   }

   public abstract IProperty<RailShape> getShapeProperty();
}
