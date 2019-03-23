package net.minecraft.block;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockRailDetector extends BlockRailBase {
   public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   public BlockRailDetector(Block.Properties p_i48417_1_) {
      super(true, p_i48417_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(POWERED, Boolean.valueOf(false)).with(SHAPE, RailShape.NORTH_SOUTH));
   }

   public int tickRate(IWorldReaderBase p_149738_1_) {
      return 20;
   }

   public boolean canProvidePower(IBlockState p_149744_1_) {
      return true;
   }

   public void onEntityCollision(IBlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_2_.isRemote) {
         if (!p_196262_1_.get(POWERED)) {
            this.updatePoweredState(p_196262_2_, p_196262_3_, p_196262_1_);
         }
      }
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (!p_196267_2_.isRemote && p_196267_1_.get(POWERED)) {
         this.updatePoweredState(p_196267_2_, p_196267_3_, p_196267_1_);
      }
   }

   public int getWeakPower(IBlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, EnumFacing p_180656_4_) {
      return p_180656_1_.get(POWERED) ? 15 : 0;
   }

   public int getStrongPower(IBlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, EnumFacing p_176211_4_) {
      if (!p_176211_1_.get(POWERED)) {
         return 0;
      } else {
         return p_176211_4_ == EnumFacing.UP ? 15 : 0;
      }
   }

   private void updatePoweredState(World p_176570_1_, BlockPos p_176570_2_, IBlockState p_176570_3_) {
      boolean flag = p_176570_3_.get(POWERED);
      boolean flag1 = false;
      List<EntityMinecart> list = this.func_200878_a(p_176570_1_, p_176570_2_, EntityMinecart.class, (Predicate<Entity>)null);
      if (!list.isEmpty()) {
         flag1 = true;
      }

      if (flag1 && !flag) {
         p_176570_1_.setBlockState(p_176570_2_, p_176570_3_.with(POWERED, Boolean.valueOf(true)), 3);
         this.updateConnectedRails(p_176570_1_, p_176570_2_, p_176570_3_, true);
         p_176570_1_.notifyNeighborsOfStateChange(p_176570_2_, this);
         p_176570_1_.notifyNeighborsOfStateChange(p_176570_2_.down(), this);
         p_176570_1_.markBlockRangeForRenderUpdate(p_176570_2_, p_176570_2_);
      }

      if (!flag1 && flag) {
         p_176570_1_.setBlockState(p_176570_2_, p_176570_3_.with(POWERED, Boolean.valueOf(false)), 3);
         this.updateConnectedRails(p_176570_1_, p_176570_2_, p_176570_3_, false);
         p_176570_1_.notifyNeighborsOfStateChange(p_176570_2_, this);
         p_176570_1_.notifyNeighborsOfStateChange(p_176570_2_.down(), this);
         p_176570_1_.markBlockRangeForRenderUpdate(p_176570_2_, p_176570_2_);
      }

      if (flag1) {
         p_176570_1_.getPendingBlockTicks().scheduleTick(p_176570_2_, this, this.tickRate(p_176570_1_));
      }

      p_176570_1_.updateComparatorOutputLevel(p_176570_2_, this);
   }

   protected void updateConnectedRails(World p_185592_1_, BlockPos p_185592_2_, IBlockState p_185592_3_, boolean p_185592_4_) {
      BlockRailState blockrailstate = new BlockRailState(p_185592_1_, p_185592_2_, p_185592_3_);

      for(BlockPos blockpos : blockrailstate.getConnectedRails()) {
         IBlockState iblockstate = p_185592_1_.getBlockState(blockpos);
         iblockstate.neighborChanged(p_185592_1_, blockpos, iblockstate.getBlock(), p_185592_2_);
      }

   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      if (p_196259_4_.getBlock() != p_196259_1_.getBlock()) {
         super.onBlockAdded(p_196259_1_, p_196259_2_, p_196259_3_, p_196259_4_);
         this.updatePoweredState(p_196259_2_, p_196259_3_, p_196259_1_);
      }
   }

   public IProperty<RailShape> getShapeProperty() {
      return SHAPE;
   }

   public boolean hasComparatorInputOverride(IBlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      if (p_180641_1_.get(POWERED)) {
         List<EntityMinecartCommandBlock> list = this.func_200878_a(p_180641_2_, p_180641_3_, EntityMinecartCommandBlock.class, (Predicate<Entity>)null);
         if (!list.isEmpty()) {
            return list.get(0).getCommandBlockLogic().getSuccessCount();
         }

         List<EntityMinecart> list1 = this.func_200878_a(p_180641_2_, p_180641_3_, EntityMinecart.class, EntitySelectors.HAS_INVENTORY);
         if (!list1.isEmpty()) {
            return Container.calcRedstoneFromInventory((IInventory)list1.get(0));
         }
      }

      return 0;
   }

   protected <T extends EntityMinecart> List<T> func_200878_a(World p_200878_1_, BlockPos p_200878_2_, Class<T> p_200878_3_, @Nullable Predicate<Entity> p_200878_4_) {
      return p_200878_1_.getEntitiesWithinAABB(p_200878_3_, this.getDectectionBox(p_200878_2_), p_200878_4_);
   }

   private AxisAlignedBB getDectectionBox(BlockPos p_176572_1_) {
      float f = 0.2F;
      return new AxisAlignedBB((double)((float)p_176572_1_.getX() + 0.2F), (double)p_176572_1_.getY(), (double)((float)p_176572_1_.getZ() + 0.2F), (double)((float)(p_176572_1_.getX() + 1) - 0.2F), (double)((float)(p_176572_1_.getY() + 1) - 0.2F), (double)((float)(p_176572_1_.getZ() + 1) - 0.2F));
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      switch(p_185499_2_) {
      case CLOCKWISE_180:
         switch((RailShape)p_185499_1_.get(SHAPE)) {
         case ASCENDING_EAST:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return p_185499_1_.with(SHAPE, RailShape.NORTH_WEST);
         case SOUTH_WEST:
            return p_185499_1_.with(SHAPE, RailShape.NORTH_EAST);
         case NORTH_WEST:
            return p_185499_1_.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_EAST:
            return p_185499_1_.with(SHAPE, RailShape.SOUTH_WEST);
         }
      case COUNTERCLOCKWISE_90:
         switch((RailShape)p_185499_1_.get(SHAPE)) {
         case ASCENDING_EAST:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_WEST:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_NORTH:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_SOUTH:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_EAST);
         case SOUTH_EAST:
            return p_185499_1_.with(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return p_185499_1_.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return p_185499_1_.with(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return p_185499_1_.with(SHAPE, RailShape.NORTH_WEST);
         case NORTH_SOUTH:
            return p_185499_1_.with(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return p_185499_1_.with(SHAPE, RailShape.NORTH_SOUTH);
         }
      case CLOCKWISE_90:
         switch((RailShape)p_185499_1_.get(SHAPE)) {
         case ASCENDING_EAST:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_WEST:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_NORTH:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_SOUTH:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_WEST);
         case SOUTH_EAST:
            return p_185499_1_.with(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return p_185499_1_.with(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return p_185499_1_.with(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return p_185499_1_.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_SOUTH:
            return p_185499_1_.with(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return p_185499_1_.with(SHAPE, RailShape.NORTH_SOUTH);
         }
      default:
         return p_185499_1_;
      }
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      RailShape railshape = p_185471_1_.get(SHAPE);
      switch(p_185471_2_) {
      case LEFT_RIGHT:
         switch(railshape) {
         case ASCENDING_NORTH:
            return p_185471_1_.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return p_185471_1_.with(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return p_185471_1_.with(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return p_185471_1_.with(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return p_185471_1_.with(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return p_185471_1_.with(SHAPE, RailShape.SOUTH_EAST);
         default:
            return super.mirror(p_185471_1_, p_185471_2_);
         }
      case FRONT_BACK:
         switch(railshape) {
         case ASCENDING_EAST:
            return p_185471_1_.with(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return p_185471_1_.with(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
         case ASCENDING_SOUTH:
         default:
            break;
         case SOUTH_EAST:
            return p_185471_1_.with(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return p_185471_1_.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return p_185471_1_.with(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return p_185471_1_.with(SHAPE, RailShape.NORTH_WEST);
         }
      }

      return super.mirror(p_185471_1_, p_185471_2_);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(SHAPE, POWERED);
   }
}
