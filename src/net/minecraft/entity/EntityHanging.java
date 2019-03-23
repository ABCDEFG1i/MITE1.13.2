package net.minecraft.entity;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public abstract class EntityHanging extends Entity {
   protected static final Predicate<Entity> IS_HANGING_ENTITY = (p_210144_0_) -> {
      return p_210144_0_ instanceof EntityHanging;
   };
   private int tickCounter1;
   protected BlockPos hangingPosition;
   @Nullable
   public EnumFacing facingDirection;

   protected EntityHanging(EntityType<?> p_i48561_1_, World p_i48561_2_) {
      super(p_i48561_1_, p_i48561_2_);
      this.setSize(0.5F, 0.5F);
   }

   protected EntityHanging(EntityType<?> p_i48562_1_, World p_i48562_2_, BlockPos p_i48562_3_) {
      this(p_i48562_1_, p_i48562_2_);
      this.hangingPosition = p_i48562_3_;
   }

   protected void registerData() {
   }

   protected void updateFacingWithBoundingBox(EnumFacing p_174859_1_) {
      Validate.notNull(p_174859_1_);
      Validate.isTrue(p_174859_1_.getAxis().isHorizontal());
      this.facingDirection = p_174859_1_;
      this.rotationYaw = (float)(this.facingDirection.getHorizontalIndex() * 90);
      this.prevRotationYaw = this.rotationYaw;
      this.updateBoundingBox();
   }

   protected void updateBoundingBox() {
      if (this.facingDirection != null) {
         double d0 = (double)this.hangingPosition.getX() + 0.5D;
         double d1 = (double)this.hangingPosition.getY() + 0.5D;
         double d2 = (double)this.hangingPosition.getZ() + 0.5D;
         double d3 = 0.46875D;
         double d4 = this.offs(this.getWidthPixels());
         double d5 = this.offs(this.getHeightPixels());
         d0 = d0 - (double)this.facingDirection.getXOffset() * 0.46875D;
         d2 = d2 - (double)this.facingDirection.getZOffset() * 0.46875D;
         d1 = d1 + d5;
         EnumFacing enumfacing = this.facingDirection.rotateYCCW();
         d0 = d0 + d4 * (double)enumfacing.getXOffset();
         d2 = d2 + d4 * (double)enumfacing.getZOffset();
         this.posX = d0;
         this.posY = d1;
         this.posZ = d2;
         double d6 = (double)this.getWidthPixels();
         double d7 = (double)this.getHeightPixels();
         double d8 = (double)this.getWidthPixels();
         if (this.facingDirection.getAxis() == EnumFacing.Axis.Z) {
            d8 = 1.0D;
         } else {
            d6 = 1.0D;
         }

         d6 = d6 / 32.0D;
         d7 = d7 / 32.0D;
         d8 = d8 / 32.0D;
         this.setEntityBoundingBox(new AxisAlignedBB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
      }
   }

   private double offs(int p_190202_1_) {
      return p_190202_1_ % 32 == 0 ? 0.5D : 0.0D;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.tickCounter1++ == 100 && !this.world.isRemote) {
         this.tickCounter1 = 0;
         if (!this.isDead && !this.onValidSurface()) {
            this.setDead();
            this.onBroken((Entity)null);
         }
      }

   }

   public boolean onValidSurface() {
      if (!this.world.isCollisionBoxesEmpty(this, this.getEntityBoundingBox())) {
         return false;
      } else {
         int i = Math.max(1, this.getWidthPixels() / 16);
         int j = Math.max(1, this.getHeightPixels() / 16);
         BlockPos blockpos = this.hangingPosition.offset(this.facingDirection.getOpposite());
         EnumFacing enumfacing = this.facingDirection.rotateYCCW();
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int k = 0; k < i; ++k) {
            for(int l = 0; l < j; ++l) {
               int i1 = (i - 1) / -2;
               int j1 = (j - 1) / -2;
               blockpos$mutableblockpos.setPos(blockpos).move(enumfacing, k + i1).move(EnumFacing.UP, l + j1);
               IBlockState iblockstate = this.world.getBlockState(blockpos$mutableblockpos);
               if (!iblockstate.getMaterial().isSolid() && !BlockRedstoneDiode.isDiode(iblockstate)) {
                  return false;
               }
            }
         }

         return this.world.func_175674_a(this, this.getEntityBoundingBox(), IS_HANGING_ENTITY).isEmpty();
      }
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean hitByEntity(Entity p_85031_1_) {
      return p_85031_1_ instanceof EntityPlayer ? this.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)p_85031_1_), 0.0F) : false;
   }

   public EnumFacing getHorizontalFacing() {
      return this.facingDirection;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         if (!this.isDead && !this.world.isRemote) {
            this.setDead();
            this.markVelocityChanged();
            this.onBroken(p_70097_1_.getTrueSource());
         }

         return true;
      }
   }

   public void move(MoverType p_70091_1_, double p_70091_2_, double p_70091_4_, double p_70091_6_) {
      if (!this.world.isRemote && !this.isDead && p_70091_2_ * p_70091_2_ + p_70091_4_ * p_70091_4_ + p_70091_6_ * p_70091_6_ > 0.0D) {
         this.setDead();
         this.onBroken((Entity)null);
      }

   }

   public void addVelocity(double p_70024_1_, double p_70024_3_, double p_70024_5_) {
      if (!this.world.isRemote && !this.isDead && p_70024_1_ * p_70024_1_ + p_70024_3_ * p_70024_3_ + p_70024_5_ * p_70024_5_ > 0.0D) {
         this.setDead();
         this.onBroken((Entity)null);
      }

   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      p_70014_1_.setByte("Facing", (byte)this.facingDirection.getHorizontalIndex());
      BlockPos blockpos = this.getHangingPosition();
      p_70014_1_.setInteger("TileX", blockpos.getX());
      p_70014_1_.setInteger("TileY", blockpos.getY());
      p_70014_1_.setInteger("TileZ", blockpos.getZ());
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      this.hangingPosition = new BlockPos(p_70037_1_.getInteger("TileX"), p_70037_1_.getInteger("TileY"), p_70037_1_.getInteger("TileZ"));
      this.updateFacingWithBoundingBox(EnumFacing.byHorizontalIndex(p_70037_1_.getByte("Facing")));
   }

   public abstract int getWidthPixels();

   public abstract int getHeightPixels();

   public abstract void onBroken(@Nullable Entity p_110128_1_);

   public abstract void playPlaceSound();

   public EntityItem entityDropItem(ItemStack p_70099_1_, float p_70099_2_) {
      EntityItem entityitem = new EntityItem(this.world, this.posX + (double)((float)this.facingDirection.getXOffset() * 0.15F), this.posY + (double)p_70099_2_, this.posZ + (double)((float)this.facingDirection.getZOffset() * 0.15F), p_70099_1_);
      entityitem.setDefaultPickupDelay();
      this.world.spawnEntity(entityitem);
      return entityitem;
   }

   protected boolean shouldSetPosAfterLoading() {
      return false;
   }

   public void setPosition(double p_70107_1_, double p_70107_3_, double p_70107_5_) {
      this.hangingPosition = new BlockPos(p_70107_1_, p_70107_3_, p_70107_5_);
      this.updateBoundingBox();
      this.isAirBorne = true;
   }

   public BlockPos getHangingPosition() {
      return this.hangingPosition;
   }

   public float getRotatedYaw(Rotation p_184229_1_) {
      if (this.facingDirection != null && this.facingDirection.getAxis() != EnumFacing.Axis.Y) {
         switch(p_184229_1_) {
         case CLOCKWISE_180:
            this.facingDirection = this.facingDirection.getOpposite();
            break;
         case COUNTERCLOCKWISE_90:
            this.facingDirection = this.facingDirection.rotateYCCW();
            break;
         case CLOCKWISE_90:
            this.facingDirection = this.facingDirection.rotateY();
         }
      }

      float f = MathHelper.wrapDegrees(this.rotationYaw);
      switch(p_184229_1_) {
      case CLOCKWISE_180:
         return f + 180.0F;
      case COUNTERCLOCKWISE_90:
         return f + 90.0F;
      case CLOCKWISE_90:
         return f + 270.0F;
      default:
         return f;
      }
   }

   public float getMirroredYaw(Mirror p_184217_1_) {
      return this.getRotatedYaw(p_184217_1_.toRotation(this.facingDirection));
   }

   public void onStruckByLightning(EntityLightningBolt p_70077_1_) {
   }
}
