package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntitySquid extends EntityWaterMob {
   public float squidPitch;
   public float prevSquidPitch;
   public float squidYaw;
   public float prevSquidYaw;
   public float squidRotation;
   public float prevSquidRotation;
   public float tentacleAngle;
   public float lastTentacleAngle;
   private float randomMotionSpeed;
   private float rotationVelocity;
   private float rotateSpeed;
   private float randomMotionVecX;
   private float randomMotionVecY;
   private float randomMotionVecZ;

   public EntitySquid(World p_i1693_1_) {
      super(EntityType.SQUID, p_i1693_1_);
      this.setSize(0.8F, 0.8F);
      this.rand.setSeed((long)(1 + this.getEntityId()));
      this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntitySquid.AIMoveRandom(this));
      this.tasks.addTask(1, new EntitySquid.AIFlee());
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
   }

   public float getEyeHeight() {
      return this.height * 0.5F;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SQUID_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_SQUID_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SQUID_DEATH;
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_SQUID;
   }

   public void livingTick() {
      super.livingTick();
      this.prevSquidPitch = this.squidPitch;
      this.prevSquidYaw = this.squidYaw;
      this.prevSquidRotation = this.squidRotation;
      this.lastTentacleAngle = this.tentacleAngle;
      this.squidRotation += this.rotationVelocity;
      if ((double)this.squidRotation > (Math.PI * 2D)) {
         if (this.world.isRemote) {
            this.squidRotation = ((float)Math.PI * 2F);
         } else {
            this.squidRotation = (float)((double)this.squidRotation - (Math.PI * 2D));
            if (this.rand.nextInt(10) == 0) {
               this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
            }

            this.world.setEntityState(this, (byte)19);
         }
      }

      if (this.isInWaterOrBubbleColumn()) {
         if (this.squidRotation < (float)Math.PI) {
            float f = this.squidRotation / (float)Math.PI;
            this.tentacleAngle = MathHelper.sin(f * f * (float)Math.PI) * (float)Math.PI * 0.25F;
            if ((double)f > 0.75D) {
               this.randomMotionSpeed = 1.0F;
               this.rotateSpeed = 1.0F;
            } else {
               this.rotateSpeed *= 0.8F;
            }
         } else {
            this.tentacleAngle = 0.0F;
            this.randomMotionSpeed *= 0.9F;
            this.rotateSpeed *= 0.99F;
         }

         if (!this.world.isRemote) {
            this.motionX = (double)(this.randomMotionVecX * this.randomMotionSpeed);
            this.motionY = (double)(this.randomMotionVecY * this.randomMotionSpeed);
            this.motionZ = (double)(this.randomMotionVecZ * this.randomMotionSpeed);
         }

         float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.renderYawOffset += (-((float)MathHelper.atan2(this.motionX, this.motionZ)) * (180F / (float)Math.PI) - this.renderYawOffset) * 0.1F;
         this.rotationYaw = this.renderYawOffset;
         this.squidYaw = (float)((double)this.squidYaw + Math.PI * (double)this.rotateSpeed * 1.5D);
         this.squidPitch += (-((float)MathHelper.atan2((double)f1, this.motionY)) * (180F / (float)Math.PI) - this.squidPitch) * 0.1F;
      } else {
         this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.squidRotation)) * (float)Math.PI * 0.25F;
         if (!this.world.isRemote) {
            this.motionX = 0.0D;
            this.motionZ = 0.0D;
            if (this.isPotionActive(MobEffects.LEVITATION)) {
               this.motionY += 0.05D * (double)(this.getActivePotionEffect(MobEffects.LEVITATION).getAmplifier() + 1) - this.motionY;
            } else if (!this.hasNoGravity()) {
               this.motionY -= 0.08D;
            }

            this.motionY *= (double)0.98F;
         }

         this.squidPitch = (float)((double)this.squidPitch + (double)(-90.0F - this.squidPitch) * 0.02D);
      }

   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (super.attackEntityFrom(p_70097_1_, p_70097_2_) && this.getRevengeTarget() != null) {
         this.func_203039_dq();
         return true;
      } else {
         return false;
      }
   }

   private Vec3d func_207400_b(Vec3d p_207400_1_) {
      Vec3d vec3d = p_207400_1_.rotatePitch(this.prevSquidPitch * ((float)Math.PI / 180F));
      vec3d = vec3d.rotateYaw(-this.prevRenderYawOffset * ((float)Math.PI / 180F));
      return vec3d;
   }

   private void func_203039_dq() {
      this.playSound(SoundEvents.ENTITY_SQUID_SQUIRT, this.getSoundVolume(), this.getSoundPitch());
      Vec3d vec3d = this.func_207400_b(new Vec3d(0.0D, -1.0D, 0.0D)).add(this.posX, this.posY, this.posZ);

      for(int i = 0; i < 30; ++i) {
         Vec3d vec3d1 = this.func_207400_b(new Vec3d((double)this.rand.nextFloat() * 0.6D - 0.3D, -1.0D, (double)this.rand.nextFloat() * 0.6D - 0.3D));
         Vec3d vec3d2 = vec3d1.scale(0.3D + (double)(this.rand.nextFloat() * 2.0F));
         ((WorldServer)this.world).spawnParticle(Particles.SQUID_INK, vec3d.x, vec3d.y + 0.5D, vec3d.z, 0, vec3d2.x, vec3d2.y, vec3d2.z, (double)0.1F);
      }

   }

   public void travel(float p_191986_1_, float p_191986_2_, float p_191986_3_) {
      this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
   }

   public boolean func_205020_a(IWorld p_205020_1_, boolean p_205020_2_) {
      return this.posY > 45.0D && this.posY < (double)p_205020_1_.getSeaLevel();
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 19) {
         this.squidRotation = 0.0F;
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   public void setMovementVector(float p_175568_1_, float p_175568_2_, float p_175568_3_) {
      this.randomMotionVecX = p_175568_1_;
      this.randomMotionVecY = p_175568_2_;
      this.randomMotionVecZ = p_175568_3_;
   }

   public boolean hasMovementVector() {
      return this.randomMotionVecX != 0.0F || this.randomMotionVecY != 0.0F || this.randomMotionVecZ != 0.0F;
   }

   class AIFlee extends EntityAIBase {
      private int field_203125_b;

      private AIFlee() {
      }

      public boolean shouldExecute() {
         EntityLivingBase entitylivingbase = EntitySquid.this.getRevengeTarget();
         if (EntitySquid.this.isInWater() && entitylivingbase != null) {
            return EntitySquid.this.getDistanceSq(entitylivingbase) < 100.0D;
         } else {
            return false;
         }
      }

      public void startExecuting() {
         this.field_203125_b = 0;
      }

      public void updateTask() {
         ++this.field_203125_b;
         EntityLivingBase entitylivingbase = EntitySquid.this.getRevengeTarget();
         if (entitylivingbase != null) {
            Vec3d vec3d = new Vec3d(EntitySquid.this.posX - entitylivingbase.posX, EntitySquid.this.posY - entitylivingbase.posY, EntitySquid.this.posZ - entitylivingbase.posZ);
            IBlockState iblockstate = EntitySquid.this.world.getBlockState(new BlockPos(EntitySquid.this.posX + vec3d.x, EntitySquid.this.posY + vec3d.y, EntitySquid.this.posZ + vec3d.z));
            IFluidState ifluidstate = EntitySquid.this.world.getFluidState(new BlockPos(EntitySquid.this.posX + vec3d.x, EntitySquid.this.posY + vec3d.y, EntitySquid.this.posZ + vec3d.z));
            if (ifluidstate.isTagged(FluidTags.WATER) || iblockstate.isAir()) {
               double d0 = vec3d.length();
               if (d0 > 0.0D) {
                  vec3d.normalize();
                  float f = 3.0F;
                  if (d0 > 5.0D) {
                     f = (float)((double)f - (d0 - 5.0D) / 5.0D);
                  }

                  if (f > 0.0F) {
                     vec3d = vec3d.scale((double)f);
                  }
               }

               if (iblockstate.isAir()) {
                  vec3d = vec3d.subtract(0.0D, vec3d.y, 0.0D);
               }

               EntitySquid.this.setMovementVector((float)vec3d.x / 20.0F, (float)vec3d.y / 20.0F, (float)vec3d.z / 20.0F);
            }

            if (this.field_203125_b % 10 == 5) {
               EntitySquid.this.world.spawnParticle(Particles.BUBBLE, EntitySquid.this.posX, EntitySquid.this.posY, EntitySquid.this.posZ, 0.0D, 0.0D, 0.0D);
            }

         }
      }
   }

   class AIMoveRandom extends EntityAIBase {
      private final EntitySquid squid;

      public AIMoveRandom(EntitySquid p_i48823_2_) {
         this.squid = p_i48823_2_;
      }

      public boolean shouldExecute() {
         return true;
      }

      public void updateTask() {
         int i = this.squid.getIdleTime();
         if (i > 100) {
            this.squid.setMovementVector(0.0F, 0.0F, 0.0F);
         } else if (this.squid.getRNG().nextInt(50) == 0 || !this.squid.inWater || !this.squid.hasMovementVector()) {
            float f = this.squid.getRNG().nextFloat() * ((float)Math.PI * 2F);
            float f1 = MathHelper.cos(f) * 0.2F;
            float f2 = -0.1F + this.squid.getRNG().nextFloat() * 0.2F;
            float f3 = MathHelper.sin(f) * 0.2F;
            this.squid.setMovementVector(f1, f2, f3);
         }

      }
   }
}
