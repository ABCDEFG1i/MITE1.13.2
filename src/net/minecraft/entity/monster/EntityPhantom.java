package net.minecraft.entity.monster;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityBodyHelper;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityPhantom extends EntityFlying implements IMob {
   private static final DataParameter<Integer> field_203035_a = EntityDataManager.createKey(EntityPhantom.class, DataSerializers.VARINT);
   private Vec3d field_203036_b = Vec3d.ZERO;
   private BlockPos field_203037_c = BlockPos.ORIGIN;
   private EntityPhantom.AttackPhase field_203038_bx = EntityPhantom.AttackPhase.CIRCLE;

   public EntityPhantom(World p_i48793_1_) {
      super(EntityType.PHANTOM, p_i48793_1_);
      this.experienceValue = 5;
      this.setSize(0.9F, 0.5F);
      this.moveHelper = new EntityPhantom.MoveHelper(this);
      this.lookHelper = new EntityPhantom.LookHelper(this);
   }

   protected EntityBodyHelper createBodyHelper() {
      return new EntityPhantom.BodyHelper(this);
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new EntityPhantom.AIPickAttack());
      this.tasks.addTask(2, new EntityPhantom.AISweepAttack());
      this.tasks.addTask(3, new EntityPhantom.AIOrbitPoint());
      this.targetTasks.addTask(1, new EntityPhantom.AIAttackPlayer());
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(field_203035_a, 0);
   }

   public void func_203034_a(int p_203034_1_) {
      if (p_203034_1_ < 0) {
         p_203034_1_ = 0;
      } else if (p_203034_1_ > 64) {
         p_203034_1_ = 64;
      }

      this.dataManager.set(field_203035_a, p_203034_1_);
      this.func_203033_m();
   }

   public void func_203033_m() {
      int i = this.dataManager.get(field_203035_a);
      this.setSize(0.9F + 0.2F * (float)i, 0.5F + 0.1F * (float)i);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue((double)(6 + i));
   }

   public int func_203032_dq() {
      return this.dataManager.get(field_203035_a);
   }

   public float getEyeHeight() {
      return this.height * 0.35F;
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (field_203035_a.equals(p_184206_1_)) {
         this.func_203033_m();
      }

      super.notifyDataManagerChange(p_184206_1_);
   }

   public void tick() {
      super.tick();
      if (this.world.isRemote) {
         float f = MathHelper.cos((float)(this.getEntityId() * 3 + this.ticksExisted) * 0.13F + (float)Math.PI);
         float f1 = MathHelper.cos((float)(this.getEntityId() * 3 + this.ticksExisted + 1) * 0.13F + (float)Math.PI);
         if (f > 0.0F && f1 <= 0.0F) {
            this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PHANTOM_FLAP, this.getSoundCategory(), 0.95F + this.rand.nextFloat() * 0.05F, 0.95F + this.rand.nextFloat() * 0.05F, false);
         }

         int i = this.func_203032_dq();
         float f2 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F)) * (1.3F + 0.21F * (float)i);
         float f3 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F)) * (1.3F + 0.21F * (float)i);
         float f4 = (0.3F + f * 0.45F) * ((float)i * 0.2F + 1.0F);
         this.world.spawnParticle(Particles.MYCELIUM, this.posX + (double)f2, this.posY + (double)f4, this.posZ + (double)f3, 0.0D, 0.0D, 0.0D);
         this.world.spawnParticle(Particles.MYCELIUM, this.posX - (double)f2, this.posY + (double)f4, this.posZ - (double)f3, 0.0D, 0.0D, 0.0D);
      }

      if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
         this.setDead();
      }

   }

   public void livingTick() {
      if (this.isInDaylight()) {
         this.setFire(8);
      }

      super.livingTick();
   }

   protected void updateAITasks() {
      super.updateAITasks();
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      this.field_203037_c = (new BlockPos(this)).up(5);
      this.func_203034_a(0);
      return super.onInitialSpawn(p_204210_1_, p_204210_2_, p_204210_3_);
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      if (p_70037_1_.hasKey("AX")) {
         this.field_203037_c = new BlockPos(p_70037_1_.getInteger("AX"), p_70037_1_.getInteger("AY"), p_70037_1_.getInteger("AZ"));
      }

      this.func_203034_a(p_70037_1_.getInteger("Size"));
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("AX", this.field_203037_c.getX());
      p_70014_1_.setInteger("AY", this.field_203037_c.getY());
      p_70014_1_.setInteger("AZ", this.field_203037_c.getZ());
      p_70014_1_.setInteger("Size", this.func_203032_dq());
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      return true;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_PHANTOM_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_PHANTOM_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PHANTOM_DEATH;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_PHANTOM;
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.UNDEAD;
   }

   protected float getSoundVolume() {
      return 1.0F;
   }

   public boolean canAttackClass(Class<? extends EntityLivingBase> p_70686_1_) {
      return true;
   }

   class AIAttackPlayer extends EntityAIBase {
      private int field_203142_b = 20;

      private AIAttackPlayer() {
      }

      public boolean shouldExecute() {
         if (this.field_203142_b > 0) {
            --this.field_203142_b;
            return false;
         } else {
            this.field_203142_b = 60;
            AxisAlignedBB axisalignedbb = EntityPhantom.this.getEntityBoundingBox().grow(16.0D, 64.0D, 16.0D);
            List<EntityPlayer> list = EntityPhantom.this.world.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
            if (!list.isEmpty()) {
               list.sort((p_203140_0_, p_203140_1_) -> {
                  return p_203140_0_.posY > p_203140_1_.posY ? -1 : 1;
               });

               for(EntityPlayer entityplayer : list) {
                  if (EntityAITarget.isSuitableTarget(EntityPhantom.this, entityplayer, false, false)) {
                     EntityPhantom.this.setAttackTarget(entityplayer);
                     return true;
                  }
               }
            }

            return false;
         }
      }

      public boolean shouldContinueExecuting() {
         return EntityAITarget.isSuitableTarget(EntityPhantom.this, EntityPhantom.this.getAttackTarget(), false, false);
      }
   }

   abstract class AIMove extends EntityAIBase {
      public AIMove() {
         this.setMutexBits(1);
      }

      protected boolean func_203146_f() {
         return EntityPhantom.this.field_203036_b.squareDistanceTo(EntityPhantom.this.posX, EntityPhantom.this.posY, EntityPhantom.this.posZ) < 4.0D;
      }
   }

   class AIOrbitPoint extends EntityPhantom.AIMove {
      private float field_203150_c;
      private float field_203151_d;
      private float field_203152_e;
      private float field_203153_f;

      private AIOrbitPoint() {
         super();
      }

      public boolean shouldExecute() {
         return EntityPhantom.this.getAttackTarget() == null || EntityPhantom.this.field_203038_bx == EntityPhantom.AttackPhase.CIRCLE;
      }

      public void startExecuting() {
         this.field_203151_d = 5.0F + EntityPhantom.this.rand.nextFloat() * 10.0F;
         this.field_203152_e = -4.0F + EntityPhantom.this.rand.nextFloat() * 9.0F;
         this.field_203153_f = EntityPhantom.this.rand.nextBoolean() ? 1.0F : -1.0F;
         this.func_203148_i();
      }

      public void updateTask() {
         if (EntityPhantom.this.rand.nextInt(350) == 0) {
            this.field_203152_e = -4.0F + EntityPhantom.this.rand.nextFloat() * 9.0F;
         }

         if (EntityPhantom.this.rand.nextInt(250) == 0) {
            ++this.field_203151_d;
            if (this.field_203151_d > 15.0F) {
               this.field_203151_d = 5.0F;
               this.field_203153_f = -this.field_203153_f;
            }
         }

         if (EntityPhantom.this.rand.nextInt(450) == 0) {
            this.field_203150_c = EntityPhantom.this.rand.nextFloat() * 2.0F * (float)Math.PI;
            this.func_203148_i();
         }

         if (this.func_203146_f()) {
            this.func_203148_i();
         }

         if (EntityPhantom.this.field_203036_b.y < EntityPhantom.this.posY && !EntityPhantom.this.world.isAirBlock((new BlockPos(EntityPhantom.this)).down(1))) {
            this.field_203152_e = Math.max(1.0F, this.field_203152_e);
            this.func_203148_i();
         }

         if (EntityPhantom.this.field_203036_b.y > EntityPhantom.this.posY && !EntityPhantom.this.world.isAirBlock((new BlockPos(EntityPhantom.this)).up(1))) {
            this.field_203152_e = Math.min(-1.0F, this.field_203152_e);
            this.func_203148_i();
         }

      }

      private void func_203148_i() {
         if (BlockPos.ORIGIN.equals(EntityPhantom.this.field_203037_c)) {
            EntityPhantom.this.field_203037_c = new BlockPos(EntityPhantom.this);
         }

         this.field_203150_c += this.field_203153_f * 15.0F * ((float)Math.PI / 180F);
         EntityPhantom.this.field_203036_b = (new Vec3d(EntityPhantom.this.field_203037_c)).add((double)(this.field_203151_d * MathHelper.cos(this.field_203150_c)), (double)(-4.0F + this.field_203152_e), (double)(this.field_203151_d * MathHelper.sin(this.field_203150_c)));
      }
   }

   class AIPickAttack extends EntityAIBase {
      private int field_203145_b;

      private AIPickAttack() {
      }

      public boolean shouldExecute() {
         return EntityAITarget.isSuitableTarget(EntityPhantom.this, EntityPhantom.this.getAttackTarget(), false, false);
      }

      public void startExecuting() {
         this.field_203145_b = 10;
         EntityPhantom.this.field_203038_bx = EntityPhantom.AttackPhase.CIRCLE;
         this.func_203143_f();
      }

      public void resetTask() {
         EntityPhantom.this.field_203037_c = EntityPhantom.this.world.getHeight(Heightmap.Type.MOTION_BLOCKING, EntityPhantom.this.field_203037_c).up(10 + EntityPhantom.this.rand.nextInt(20));
      }

      public void updateTask() {
         if (EntityPhantom.this.field_203038_bx == EntityPhantom.AttackPhase.CIRCLE) {
            --this.field_203145_b;
            if (this.field_203145_b <= 0) {
               EntityPhantom.this.field_203038_bx = EntityPhantom.AttackPhase.SWOOP;
               this.func_203143_f();
               this.field_203145_b = (8 + EntityPhantom.this.rand.nextInt(4)) * 20;
               EntityPhantom.this.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 10.0F, 0.95F + EntityPhantom.this.rand.nextFloat() * 0.1F);
            }
         }

      }

      private void func_203143_f() {
         EntityPhantom.this.field_203037_c = (new BlockPos(EntityPhantom.this.getAttackTarget())).up(20 + EntityPhantom.this.rand.nextInt(20));
         if (EntityPhantom.this.field_203037_c.getY() < EntityPhantom.this.world.getSeaLevel()) {
            EntityPhantom.this.field_203037_c = new BlockPos(EntityPhantom.this.field_203037_c.getX(), EntityPhantom.this.world.getSeaLevel() + 1, EntityPhantom.this.field_203037_c.getZ());
         }

      }
   }

   class AISweepAttack extends EntityPhantom.AIMove {
      private AISweepAttack() {
         super();
      }

      public boolean shouldExecute() {
         return EntityPhantom.this.getAttackTarget() != null && EntityPhantom.this.field_203038_bx == EntityPhantom.AttackPhase.SWOOP;
      }

      public boolean shouldContinueExecuting() {
         EntityLivingBase entitylivingbase = EntityPhantom.this.getAttackTarget();
         if (entitylivingbase == null) {
            return false;
         } else if (!entitylivingbase.isEntityAlive()) {
            return false;
         } else {
            return (!(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer) entitylivingbase).isSpectator() && !((EntityPlayer) entitylivingbase).isCreative()) && this.shouldExecute();
         }
      }

      public void startExecuting() {
      }

      public void resetTask() {
         EntityPhantom.this.setAttackTarget(null);
         EntityPhantom.this.field_203038_bx = EntityPhantom.AttackPhase.CIRCLE;
      }

      public void updateTask() {
         EntityLivingBase entitylivingbase = EntityPhantom.this.getAttackTarget();
         EntityPhantom.this.field_203036_b = new Vec3d(entitylivingbase.posX, entitylivingbase.posY + (double)entitylivingbase.height * 0.5D, entitylivingbase.posZ);
         if (EntityPhantom.this.getEntityBoundingBox().grow((double)0.2F).intersects(entitylivingbase.getEntityBoundingBox())) {
            EntityPhantom.this.attackEntityAsMob(entitylivingbase);
            EntityPhantom.this.field_203038_bx = EntityPhantom.AttackPhase.CIRCLE;
            EntityPhantom.this.world.playEvent(1039, new BlockPos(EntityPhantom.this), 0);
         } else if (EntityPhantom.this.collidedHorizontally || EntityPhantom.this.hurtTime > 0) {
            EntityPhantom.this.field_203038_bx = EntityPhantom.AttackPhase.CIRCLE;
         }

      }
   }

   enum AttackPhase {
      CIRCLE,
      SWOOP
   }

   class BodyHelper extends EntityBodyHelper {
      public BodyHelper(EntityLivingBase p_i48805_2_) {
         super(p_i48805_2_);
      }

      public void updateRenderAngles() {
         EntityPhantom.this.rotationYawHead = EntityPhantom.this.renderYawOffset;
         EntityPhantom.this.renderYawOffset = EntityPhantom.this.rotationYaw;
      }
   }

   class LookHelper extends EntityLookHelper {
      public LookHelper(EntityLiving p_i48802_2_) {
         super(p_i48802_2_);
      }

      public void tick() {
      }
   }

   class MoveHelper extends EntityMoveHelper {
      private float field_203105_j = 0.1F;

      public MoveHelper(EntityLiving p_i48801_2_) {
         super(p_i48801_2_);
      }

      public void tick() {
         if (EntityPhantom.this.collidedHorizontally) {
            EntityPhantom.this.rotationYaw += 180.0F;
            this.field_203105_j = 0.1F;
         }

         float f = (float)(EntityPhantom.this.field_203036_b.x - EntityPhantom.this.posX);
         float f1 = (float)(EntityPhantom.this.field_203036_b.y - EntityPhantom.this.posY);
         float f2 = (float)(EntityPhantom.this.field_203036_b.z - EntityPhantom.this.posZ);
         double d0 = (double)MathHelper.sqrt(f * f + f2 * f2);
         double d1 = 1.0D - (double)MathHelper.abs(f1 * 0.7F) / d0;
         f = (float)((double)f * d1);
         f2 = (float)((double)f2 * d1);
         d0 = (double)MathHelper.sqrt(f * f + f2 * f2);
         double d2 = (double)MathHelper.sqrt(f * f + f2 * f2 + f1 * f1);
         float f3 = EntityPhantom.this.rotationYaw;
         float f4 = (float)MathHelper.atan2((double)f2, (double)f);
         float f5 = MathHelper.wrapDegrees(EntityPhantom.this.rotationYaw + 90.0F);
         float f6 = MathHelper.wrapDegrees(f4 * (180F / (float)Math.PI));
         EntityPhantom.this.rotationYaw = MathHelper.func_203303_c(f5, f6, 4.0F) - 90.0F;
         EntityPhantom.this.renderYawOffset = EntityPhantom.this.rotationYaw;
         if (MathHelper.func_203301_d(f3, EntityPhantom.this.rotationYaw) < 3.0F) {
            this.field_203105_j = MathHelper.func_203300_b(this.field_203105_j, 1.8F, 0.005F * (1.8F / this.field_203105_j));
         } else {
            this.field_203105_j = MathHelper.func_203300_b(this.field_203105_j, 0.2F, 0.025F);
         }

         float f7 = (float)(-(MathHelper.atan2((double)(-f1), d0) * (double)(180F / (float)Math.PI)));
         EntityPhantom.this.rotationPitch = f7;
         float f8 = EntityPhantom.this.rotationYaw + 90.0F;
         double d3 = (double)(this.field_203105_j * MathHelper.cos(f8 * ((float)Math.PI / 180F))) * Math.abs((double)f / d2);
         double d4 = (double)(this.field_203105_j * MathHelper.sin(f8 * ((float)Math.PI / 180F))) * Math.abs((double)f2 / d2);
         double d5 = (double)(this.field_203105_j * MathHelper.sin(f7 * ((float)Math.PI / 180F))) * Math.abs((double)f1 / d2);
         EntityPhantom.this.motionX += (d3 - EntityPhantom.this.motionX) * 0.2D;
         EntityPhantom.this.motionY += (d5 - EntityPhantom.this.motionY) * 0.2D;
         EntityPhantom.this.motionZ += (d4 - EntityPhantom.this.motionZ) * 0.2D;
      }
   }
}
