package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityShulkerBullet extends Entity {
   private EntityLivingBase owner;
   private Entity target;
   @Nullable
   private EnumFacing direction;
   private int steps;
   private double targetDeltaX;
   private double targetDeltaY;
   private double targetDeltaZ;
   @Nullable
   private UUID ownerUniqueId;
   private BlockPos ownerBlockPos;
   @Nullable
   private UUID targetUniqueId;
   private BlockPos targetBlockPos;

   public EntityShulkerBullet(World p_i46770_1_) {
      super(EntityType.SHULKER_BULLET, p_i46770_1_);
      this.setSize(0.3125F, 0.3125F);
      this.noClip = true;
   }

   @OnlyIn(Dist.CLIENT)
   public EntityShulkerBullet(World p_i46771_1_, double p_i46771_2_, double p_i46771_4_, double p_i46771_6_, double p_i46771_8_, double p_i46771_10_, double p_i46771_12_) {
      this(p_i46771_1_);
      this.setLocationAndAngles(p_i46771_2_, p_i46771_4_, p_i46771_6_, this.rotationYaw, this.rotationPitch);
      this.motionX = p_i46771_8_;
      this.motionY = p_i46771_10_;
      this.motionZ = p_i46771_12_;
   }

   public EntityShulkerBullet(World p_i46772_1_, EntityLivingBase p_i46772_2_, Entity p_i46772_3_, EnumFacing.Axis p_i46772_4_) {
      this(p_i46772_1_);
      this.owner = p_i46772_2_;
      BlockPos blockpos = new BlockPos(p_i46772_2_);
      double d0 = (double)blockpos.getX() + 0.5D;
      double d1 = (double)blockpos.getY() + 0.5D;
      double d2 = (double)blockpos.getZ() + 0.5D;
      this.setLocationAndAngles(d0, d1, d2, this.rotationYaw, this.rotationPitch);
      this.target = p_i46772_3_;
      this.direction = EnumFacing.UP;
      this.selectNextMoveDirection(p_i46772_4_);
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      if (this.owner != null) {
         BlockPos blockpos = new BlockPos(this.owner);
         NBTTagCompound nbttagcompound = NBTUtil.createUUIDTag(this.owner.getUniqueID());
         nbttagcompound.setInteger("X", blockpos.getX());
         nbttagcompound.setInteger("Y", blockpos.getY());
         nbttagcompound.setInteger("Z", blockpos.getZ());
         p_70014_1_.setTag("Owner", nbttagcompound);
      }

      if (this.target != null) {
         BlockPos blockpos1 = new BlockPos(this.target);
         NBTTagCompound nbttagcompound1 = NBTUtil.createUUIDTag(this.target.getUniqueID());
         nbttagcompound1.setInteger("X", blockpos1.getX());
         nbttagcompound1.setInteger("Y", blockpos1.getY());
         nbttagcompound1.setInteger("Z", blockpos1.getZ());
         p_70014_1_.setTag("Target", nbttagcompound1);
      }

      if (this.direction != null) {
         p_70014_1_.setInteger("Dir", this.direction.getIndex());
      }

      p_70014_1_.setInteger("Steps", this.steps);
      p_70014_1_.setDouble("TXD", this.targetDeltaX);
      p_70014_1_.setDouble("TYD", this.targetDeltaY);
      p_70014_1_.setDouble("TZD", this.targetDeltaZ);
   }

   protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      this.steps = p_70037_1_.getInteger("Steps");
      this.targetDeltaX = p_70037_1_.getDouble("TXD");
      this.targetDeltaY = p_70037_1_.getDouble("TYD");
      this.targetDeltaZ = p_70037_1_.getDouble("TZD");
      if (p_70037_1_.hasKey("Dir", 99)) {
         this.direction = EnumFacing.byIndex(p_70037_1_.getInteger("Dir"));
      }

      if (p_70037_1_.hasKey("Owner", 10)) {
         NBTTagCompound nbttagcompound = p_70037_1_.getCompoundTag("Owner");
         this.ownerUniqueId = NBTUtil.getUUIDFromTag(nbttagcompound);
         this.ownerBlockPos = new BlockPos(nbttagcompound.getInteger("X"), nbttagcompound.getInteger("Y"), nbttagcompound.getInteger("Z"));
      }

      if (p_70037_1_.hasKey("Target", 10)) {
         NBTTagCompound nbttagcompound1 = p_70037_1_.getCompoundTag("Target");
         this.targetUniqueId = NBTUtil.getUUIDFromTag(nbttagcompound1);
         this.targetBlockPos = new BlockPos(nbttagcompound1.getInteger("X"), nbttagcompound1.getInteger("Y"), nbttagcompound1.getInteger("Z"));
      }

   }

   protected void registerData() {
   }

   private void setDirection(@Nullable EnumFacing p_184568_1_) {
      this.direction = p_184568_1_;
   }

   private void selectNextMoveDirection(@Nullable EnumFacing.Axis p_184569_1_) {
      double d0 = 0.5D;
      BlockPos blockpos;
      if (this.target == null) {
         blockpos = (new BlockPos(this)).down();
      } else {
         d0 = (double)this.target.height * 0.5D;
         blockpos = new BlockPos(this.target.posX, this.target.posY + d0, this.target.posZ);
      }

      double d1 = (double)blockpos.getX() + 0.5D;
      double d2 = (double)blockpos.getY() + d0;
      double d3 = (double)blockpos.getZ() + 0.5D;
      EnumFacing enumfacing = null;
      if (blockpos.distanceSqToCenter(this.posX, this.posY, this.posZ) >= 4.0D) {
         BlockPos blockpos1 = new BlockPos(this);
         List<EnumFacing> list = Lists.newArrayList();
         if (p_184569_1_ != EnumFacing.Axis.X) {
            if (blockpos1.getX() < blockpos.getX() && this.world.isAirBlock(blockpos1.east())) {
               list.add(EnumFacing.EAST);
            } else if (blockpos1.getX() > blockpos.getX() && this.world.isAirBlock(blockpos1.west())) {
               list.add(EnumFacing.WEST);
            }
         }

         if (p_184569_1_ != EnumFacing.Axis.Y) {
            if (blockpos1.getY() < blockpos.getY() && this.world.isAirBlock(blockpos1.up())) {
               list.add(EnumFacing.UP);
            } else if (blockpos1.getY() > blockpos.getY() && this.world.isAirBlock(blockpos1.down())) {
               list.add(EnumFacing.DOWN);
            }
         }

         if (p_184569_1_ != EnumFacing.Axis.Z) {
            if (blockpos1.getZ() < blockpos.getZ() && this.world.isAirBlock(blockpos1.south())) {
               list.add(EnumFacing.SOUTH);
            } else if (blockpos1.getZ() > blockpos.getZ() && this.world.isAirBlock(blockpos1.north())) {
               list.add(EnumFacing.NORTH);
            }
         }

         enumfacing = EnumFacing.random(this.rand);
         if (list.isEmpty()) {
            for(int i = 5; !this.world.isAirBlock(blockpos1.offset(enumfacing)) && i > 0; --i) {
               enumfacing = EnumFacing.random(this.rand);
            }
         } else {
            enumfacing = list.get(this.rand.nextInt(list.size()));
         }

         d1 = this.posX + (double)enumfacing.getXOffset();
         d2 = this.posY + (double)enumfacing.getYOffset();
         d3 = this.posZ + (double)enumfacing.getZOffset();
      }

      this.setDirection(enumfacing);
      double d6 = d1 - this.posX;
      double d7 = d2 - this.posY;
      double d4 = d3 - this.posZ;
      double d5 = (double)MathHelper.sqrt(d6 * d6 + d7 * d7 + d4 * d4);
      if (d5 == 0.0D) {
         this.targetDeltaX = 0.0D;
         this.targetDeltaY = 0.0D;
         this.targetDeltaZ = 0.0D;
      } else {
         this.targetDeltaX = d6 / d5 * 0.15D;
         this.targetDeltaY = d7 / d5 * 0.15D;
         this.targetDeltaZ = d4 / d5 * 0.15D;
      }

      this.isAirBorne = true;
      this.steps = 10 + this.rand.nextInt(5) * 10;
   }

   public void tick() {
      if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
         this.setDead();
      } else {
         super.tick();
         if (!this.world.isRemote) {
            if (this.target == null && this.targetUniqueId != null) {
               for(EntityLivingBase entitylivingbase : this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.targetBlockPos.add(-2, -2, -2), this.targetBlockPos.add(2, 2, 2)))) {
                  if (entitylivingbase.getUniqueID().equals(this.targetUniqueId)) {
                     this.target = entitylivingbase;
                     break;
                  }
               }

               this.targetUniqueId = null;
            }

            if (this.owner == null && this.ownerUniqueId != null) {
               for(EntityLivingBase entitylivingbase1 : this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.ownerBlockPos.add(-2, -2, -2), this.ownerBlockPos.add(2, 2, 2)))) {
                  if (entitylivingbase1.getUniqueID().equals(this.ownerUniqueId)) {
                     this.owner = entitylivingbase1;
                     break;
                  }
               }

               this.ownerUniqueId = null;
            }

            if (this.target == null || !this.target.isEntityAlive() || this.target instanceof EntityPlayer && ((EntityPlayer)this.target).isSpectator()) {
               if (!this.hasNoGravity()) {
                  this.motionY -= 0.04D;
               }
            } else {
               this.targetDeltaX = MathHelper.clamp(this.targetDeltaX * 1.025D, -1.0D, 1.0D);
               this.targetDeltaY = MathHelper.clamp(this.targetDeltaY * 1.025D, -1.0D, 1.0D);
               this.targetDeltaZ = MathHelper.clamp(this.targetDeltaZ * 1.025D, -1.0D, 1.0D);
               this.motionX += (this.targetDeltaX - this.motionX) * 0.2D;
               this.motionY += (this.targetDeltaY - this.motionY) * 0.2D;
               this.motionZ += (this.targetDeltaZ - this.motionZ) * 0.2D;
            }

            RayTraceResult raytraceresult = ProjectileHelper.forwardsRaycast(this, true, false, this.owner);
            if (raytraceresult != null) {
               this.bulletHit(raytraceresult);
            }
         }

         this.setPosition(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         ProjectileHelper.rotateTowardsMovement(this, 0.5F);
         if (this.world.isRemote) {
            this.world.spawnParticle(Particles.END_ROD, this.posX - this.motionX, this.posY - this.motionY + 0.15D, this.posZ - this.motionZ, 0.0D, 0.0D, 0.0D);
         } else if (this.target != null && !this.target.isDead) {
            if (this.steps > 0) {
               --this.steps;
               if (this.steps == 0) {
                  this.selectNextMoveDirection(this.direction == null ? null : this.direction.getAxis());
               }
            }

            if (this.direction != null) {
               BlockPos blockpos = new BlockPos(this);
               EnumFacing.Axis enumfacing$axis = this.direction.getAxis();
               if (this.world.isTopSolid(blockpos.offset(this.direction))) {
                  this.selectNextMoveDirection(enumfacing$axis);
               } else {
                  BlockPos blockpos1 = new BlockPos(this.target);
                  if (enumfacing$axis == EnumFacing.Axis.X && blockpos.getX() == blockpos1.getX() || enumfacing$axis == EnumFacing.Axis.Z && blockpos.getZ() == blockpos1.getZ() || enumfacing$axis == EnumFacing.Axis.Y && blockpos.getY() == blockpos1.getY()) {
                     this.selectNextMoveDirection(enumfacing$axis);
                  }
               }
            }
         }

      }
   }

   public boolean isBurning() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      return p_70112_1_ < 16384.0D;
   }

   public float getBrightness() {
      return 1.0F;
   }

   @OnlyIn(Dist.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }

   protected void bulletHit(RayTraceResult p_184567_1_) {
      if (p_184567_1_.entity == null) {
         ((WorldServer)this.world).spawnParticle(Particles.EXPLOSION, this.posX, this.posY, this.posZ, 2, 0.2D, 0.2D, 0.2D, 0.0D);
         this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HIT, 1.0F, 1.0F);
      } else {
         boolean flag = p_184567_1_.entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, this.owner).setProjectile(), 4.0F);
         if (flag) {
            this.applyEnchantments(this.owner, p_184567_1_.entity);
            if (p_184567_1_.entity instanceof EntityLivingBase) {
               ((EntityLivingBase)p_184567_1_.entity).addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 200));
            }
         }
      }

      this.setDead();
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (!this.world.isRemote) {
         this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HURT, 1.0F, 1.0F);
         ((WorldServer)this.world).spawnParticle(Particles.CRIT, this.posX, this.posY, this.posZ, 15, 0.2D, 0.2D, 0.2D, 0.0D);
         this.setDead();
      }

      return true;
   }
}
