package net.minecraft.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.PistonType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityPiston extends TileEntity implements ITickable {
   private IBlockState pistonState;
   private EnumFacing pistonFacing;
   private boolean extending;
   private boolean shouldHeadBeRendered;
   private static final ThreadLocal<EnumFacing> MOVING_ENTITY = new ThreadLocal<EnumFacing>() {
      protected EnumFacing initialValue() {
         return null;
      }
   };
   private float progress;
   private float lastProgress;
   private long field_211147_k;

   public TileEntityPiston() {
      super(TileEntityType.PISTON);
   }

   public TileEntityPiston(IBlockState p_i45665_1_, EnumFacing p_i45665_2_, boolean p_i45665_3_, boolean p_i45665_4_) {
      this();
      this.pistonState = p_i45665_1_;
      this.pistonFacing = p_i45665_2_;
      this.extending = p_i45665_3_;
      this.shouldHeadBeRendered = p_i45665_4_;
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public boolean isExtending() {
      return this.extending;
   }

   public EnumFacing func_212363_d() {
      return this.pistonFacing;
   }

   public boolean shouldPistonHeadBeRendered() {
      return this.shouldHeadBeRendered;
   }

   public float getProgress(float p_145860_1_) {
      if (p_145860_1_ > 1.0F) {
         p_145860_1_ = 1.0F;
      }

      return this.lastProgress + (this.progress - this.lastProgress) * p_145860_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public float getOffsetX(float p_174929_1_) {
      return (float)this.pistonFacing.getXOffset() * this.getExtendedProgress(this.getProgress(p_174929_1_));
   }

   @OnlyIn(Dist.CLIENT)
   public float getOffsetY(float p_174928_1_) {
      return (float)this.pistonFacing.getYOffset() * this.getExtendedProgress(this.getProgress(p_174928_1_));
   }

   @OnlyIn(Dist.CLIENT)
   public float getOffsetZ(float p_174926_1_) {
      return (float)this.pistonFacing.getZOffset() * this.getExtendedProgress(this.getProgress(p_174926_1_));
   }

   private float getExtendedProgress(float p_184320_1_) {
      return this.extending ? p_184320_1_ - 1.0F : 1.0F - p_184320_1_;
   }

   private IBlockState getCollisionRelatedBlockState() {
      return !this.isExtending() && this.shouldPistonHeadBeRendered() ? Blocks.PISTON_HEAD.getDefaultState().with(BlockPistonExtension.TYPE, this.pistonState.getBlock() == Blocks.STICKY_PISTON ? PistonType.STICKY : PistonType.DEFAULT).with(BlockPistonExtension.FACING, this.pistonState.get(BlockPistonBase.FACING)) : this.pistonState;
   }

   private void moveCollidedEntities(float p_184322_1_) {
      EnumFacing enumfacing = this.getMotionDirection();
      double d0 = (double)(p_184322_1_ - this.progress);
      VoxelShape voxelshape = this.getCollisionRelatedBlockState().getCollisionShape(this.world, this.getPos());
      if (!voxelshape.isEmpty()) {
         List<AxisAlignedBB> list = voxelshape.toBoundingBoxList();
         AxisAlignedBB axisalignedbb = this.moveByPositionAndProgress(this.getMinMaxPiecesAABB(list));
         List<Entity> list1 = this.world.func_72839_b(null, this.getMovementArea(axisalignedbb, enumfacing, d0).union(axisalignedbb));
         if (!list1.isEmpty()) {
            boolean flag = this.pistonState.getBlock() == Blocks.SLIME_BLOCK;

            for(int i = 0; i < list1.size(); ++i) {
               Entity entity = list1.get(i);
               if (entity.getPushReaction() != EnumPushReaction.IGNORE) {
                  if (flag) {
                     switch(enumfacing.getAxis()) {
                     case X:
                        entity.motionX = (double)enumfacing.getXOffset();
                        break;
                     case Y:
                        entity.motionY = (double)enumfacing.getYOffset();
                        break;
                     case Z:
                        entity.motionZ = (double)enumfacing.getZOffset();
                     }
                  }

                  double d1 = 0.0D;

                  for(int j = 0; j < list.size(); ++j) {
                     AxisAlignedBB axisalignedbb1 = this.getMovementArea(this.moveByPositionAndProgress(list.get(j)), enumfacing, d0);
                     AxisAlignedBB axisalignedbb2 = entity.getEntityBoundingBox();
                     if (axisalignedbb1.intersects(axisalignedbb2)) {
                        d1 = Math.max(d1, this.getMovement(axisalignedbb1, enumfacing, axisalignedbb2));
                        if (d1 >= d0) {
                           break;
                        }
                     }
                  }

                  if (!(d1 <= 0.0D)) {
                     d1 = Math.min(d1, d0) + 0.01D;
                     MOVING_ENTITY.set(enumfacing);
                     entity.move(MoverType.PISTON, d1 * (double)enumfacing.getXOffset(), d1 * (double)enumfacing.getYOffset(), d1 * (double)enumfacing.getZOffset());
                     MOVING_ENTITY.set(null);
                     if (!this.extending && this.shouldHeadBeRendered) {
                        this.fixEntityWithinPistonBase(entity, enumfacing, d0);
                     }
                  }
               }
            }

         }
      }
   }

   public EnumFacing getMotionDirection() {
      return this.extending ? this.pistonFacing : this.pistonFacing.getOpposite();
   }

   private AxisAlignedBB getMinMaxPiecesAABB(List<AxisAlignedBB> p_191515_1_) {
      double d0 = 0.0D;
      double d1 = 0.0D;
      double d2 = 0.0D;
      double d3 = 1.0D;
      double d4 = 1.0D;
      double d5 = 1.0D;

      for(AxisAlignedBB axisalignedbb : p_191515_1_) {
         d0 = Math.min(axisalignedbb.minX, d0);
         d1 = Math.min(axisalignedbb.minY, d1);
         d2 = Math.min(axisalignedbb.minZ, d2);
         d3 = Math.max(axisalignedbb.maxX, d3);
         d4 = Math.max(axisalignedbb.maxY, d4);
         d5 = Math.max(axisalignedbb.maxZ, d5);
      }

      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   private double getMovement(AxisAlignedBB p_190612_1_, EnumFacing p_190612_2_, AxisAlignedBB p_190612_3_) {
      switch(p_190612_2_.getAxis()) {
      case X:
         return getDeltaX(p_190612_1_, p_190612_2_, p_190612_3_);
      case Y:
      default:
         return getDeltaY(p_190612_1_, p_190612_2_, p_190612_3_);
      case Z:
         return getDeltaZ(p_190612_1_, p_190612_2_, p_190612_3_);
      }
   }

   private AxisAlignedBB moveByPositionAndProgress(AxisAlignedBB p_190607_1_) {
      double d0 = (double)this.getExtendedProgress(this.progress);
      return p_190607_1_.offset((double)this.pos.getX() + d0 * (double)this.pistonFacing.getXOffset(), (double)this.pos.getY() + d0 * (double)this.pistonFacing.getYOffset(), (double)this.pos.getZ() + d0 * (double)this.pistonFacing.getZOffset());
   }

   private AxisAlignedBB getMovementArea(AxisAlignedBB p_190610_1_, EnumFacing p_190610_2_, double p_190610_3_) {
      double d0 = p_190610_3_ * (double)p_190610_2_.getAxisDirection().getOffset();
      double d1 = Math.min(d0, 0.0D);
      double d2 = Math.max(d0, 0.0D);
      switch(p_190610_2_) {
      case WEST:
         return new AxisAlignedBB(p_190610_1_.minX + d1, p_190610_1_.minY, p_190610_1_.minZ, p_190610_1_.minX + d2, p_190610_1_.maxY, p_190610_1_.maxZ);
      case EAST:
         return new AxisAlignedBB(p_190610_1_.maxX + d1, p_190610_1_.minY, p_190610_1_.minZ, p_190610_1_.maxX + d2, p_190610_1_.maxY, p_190610_1_.maxZ);
      case DOWN:
         return new AxisAlignedBB(p_190610_1_.minX, p_190610_1_.minY + d1, p_190610_1_.minZ, p_190610_1_.maxX, p_190610_1_.minY + d2, p_190610_1_.maxZ);
      case UP:
      default:
         return new AxisAlignedBB(p_190610_1_.minX, p_190610_1_.maxY + d1, p_190610_1_.minZ, p_190610_1_.maxX, p_190610_1_.maxY + d2, p_190610_1_.maxZ);
      case NORTH:
         return new AxisAlignedBB(p_190610_1_.minX, p_190610_1_.minY, p_190610_1_.minZ + d1, p_190610_1_.maxX, p_190610_1_.maxY, p_190610_1_.minZ + d2);
      case SOUTH:
         return new AxisAlignedBB(p_190610_1_.minX, p_190610_1_.minY, p_190610_1_.maxZ + d1, p_190610_1_.maxX, p_190610_1_.maxY, p_190610_1_.maxZ + d2);
      }
   }

   private void fixEntityWithinPistonBase(Entity p_190605_1_, EnumFacing p_190605_2_, double p_190605_3_) {
      AxisAlignedBB axisalignedbb = p_190605_1_.getEntityBoundingBox();
      AxisAlignedBB axisalignedbb1 = VoxelShapes.func_197868_b().getBoundingBox().offset(this.pos);
      if (axisalignedbb.intersects(axisalignedbb1)) {
         EnumFacing enumfacing = p_190605_2_.getOpposite();
         double d0 = this.getMovement(axisalignedbb1, enumfacing, axisalignedbb) + 0.01D;
         double d1 = this.getMovement(axisalignedbb1, enumfacing, axisalignedbb.intersect(axisalignedbb1)) + 0.01D;
         if (Math.abs(d0 - d1) < 0.01D) {
            d0 = Math.min(d0, p_190605_3_) + 0.01D;
            MOVING_ENTITY.set(p_190605_2_);
            p_190605_1_.move(MoverType.PISTON, d0 * (double)enumfacing.getXOffset(), d0 * (double)enumfacing.getYOffset(), d0 * (double)enumfacing.getZOffset());
            MOVING_ENTITY.set(null);
         }
      }

   }

   private static double getDeltaX(AxisAlignedBB p_190611_0_, EnumFacing p_190611_1_, AxisAlignedBB p_190611_2_) {
      return p_190611_1_.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? p_190611_0_.maxX - p_190611_2_.minX : p_190611_2_.maxX - p_190611_0_.minX;
   }

   private static double getDeltaY(AxisAlignedBB p_190608_0_, EnumFacing p_190608_1_, AxisAlignedBB p_190608_2_) {
      return p_190608_1_.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? p_190608_0_.maxY - p_190608_2_.minY : p_190608_2_.maxY - p_190608_0_.minY;
   }

   private static double getDeltaZ(AxisAlignedBB p_190604_0_, EnumFacing p_190604_1_, AxisAlignedBB p_190604_2_) {
      return p_190604_1_.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? p_190604_0_.maxZ - p_190604_2_.minZ : p_190604_2_.maxZ - p_190604_0_.minZ;
   }

   public IBlockState getPistonState() {
      return this.pistonState;
   }

   public void clearPistonTileEntity() {
      if (this.lastProgress < 1.0F && this.world != null) {
         this.progress = 1.0F;
         this.lastProgress = this.progress;
         this.world.removeTileEntity(this.pos);
         this.invalidate();
         if (this.world.getBlockState(this.pos).getBlock() == Blocks.MOVING_PISTON) {
            IBlockState iblockstate;
            if (this.shouldHeadBeRendered) {
               iblockstate = Blocks.AIR.getDefaultState();
            } else {
               iblockstate = Block.getValidBlockForPosition(this.pistonState, this.world, this.pos);
            }

            this.world.setBlockState(this.pos, iblockstate, 3);
            this.world.neighborChanged(this.pos, iblockstate.getBlock(), this.pos);
         }
      }

   }

   public void tick() {
      this.field_211147_k = this.world.getTotalWorldTime();
      this.lastProgress = this.progress;
      if (this.lastProgress >= 1.0F) {
         this.world.removeTileEntity(this.pos);
         this.invalidate();
         if (this.pistonState != null && this.world.getBlockState(this.pos).getBlock() == Blocks.MOVING_PISTON) {
            IBlockState iblockstate = Block.getValidBlockForPosition(this.pistonState, this.world, this.pos);
            if (iblockstate.isAir()) {
               this.world.setBlockState(this.pos, this.pistonState, 84);
               Block.replaceBlock(this.pistonState, iblockstate, this.world, this.pos, 3);
            } else {
               if (iblockstate.has(BlockStateProperties.WATERLOGGED) && iblockstate.get(BlockStateProperties.WATERLOGGED)) {
                  iblockstate = iblockstate.with(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false));
               }

               this.world.setBlockState(this.pos, iblockstate, 67);
               this.world.neighborChanged(this.pos, iblockstate.getBlock(), this.pos);
            }
         }

      } else {
         float f = this.progress + 0.5F;
         this.moveCollidedEntities(f);
         this.progress = f;
         if (this.progress >= 1.0F) {
            this.progress = 1.0F;
         }

      }
   }

   public void readFromNBT(NBTTagCompound p_145839_1_) {
      super.readFromNBT(p_145839_1_);
      this.pistonState = NBTUtil.readBlockState(p_145839_1_.getCompoundTag("blockState"));
      this.pistonFacing = EnumFacing.byIndex(p_145839_1_.getInteger("facing"));
      this.progress = p_145839_1_.getFloat("progress");
      this.lastProgress = this.progress;
      this.extending = p_145839_1_.getBoolean("extending");
      this.shouldHeadBeRendered = p_145839_1_.getBoolean("source");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
      super.writeToNBT(p_189515_1_);
      p_189515_1_.setTag("blockState", NBTUtil.writeBlockState(this.pistonState));
      p_189515_1_.setInteger("facing", this.pistonFacing.getIndex());
      p_189515_1_.setFloat("progress", this.lastProgress);
      p_189515_1_.setBoolean("extending", this.extending);
      p_189515_1_.setBoolean("source", this.shouldHeadBeRendered);
      return p_189515_1_;
   }

   public VoxelShape func_195508_a(IBlockReader p_195508_1_, BlockPos p_195508_2_) {
      VoxelShape voxelshape;
      if (!this.extending && this.shouldHeadBeRendered) {
         voxelshape = this.pistonState.with(BlockPistonBase.EXTENDED, Boolean.valueOf(true)).getCollisionShape(p_195508_1_, p_195508_2_);
      } else {
         voxelshape = VoxelShapes.func_197880_a();
      }

      EnumFacing enumfacing = MOVING_ENTITY.get();
      if ((double)this.progress < 1.0D && enumfacing == this.getMotionDirection()) {
         return voxelshape;
      } else {
         IBlockState iblockstate;
         if (this.shouldPistonHeadBeRendered()) {
            iblockstate = Blocks.PISTON_HEAD.getDefaultState().with(BlockPistonExtension.FACING, this.pistonFacing).with(BlockPistonExtension.SHORT, Boolean.valueOf(this.extending != 1.0F - this.progress < 4.0F));
         } else {
            iblockstate = this.pistonState;
         }

         float f = this.getExtendedProgress(this.progress);
         double d0 = (double)((float)this.pistonFacing.getXOffset() * f);
         double d1 = (double)((float)this.pistonFacing.getYOffset() * f);
         double d2 = (double)((float)this.pistonFacing.getZOffset() * f);
         return VoxelShapes.func_197872_a(voxelshape, iblockstate.getCollisionShape(p_195508_1_, p_195508_2_).withOffset(d0, d1, d2));
      }
   }

   public long func_211146_k() {
      return this.field_211147_k;
   }
}
