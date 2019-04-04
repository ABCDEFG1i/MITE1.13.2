package net.minecraft.entity.projectile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class EntityArrow extends Entity implements IProjectile {
   private static final Predicate<Entity> ARROW_TARGETS = EntitySelectors.NOT_SPECTATING.and(EntitySelectors.IS_ALIVE.and(Entity::canBeCollidedWith));
   private static final DataParameter<Byte> CRITICAL = EntityDataManager.createKey(EntityArrow.class, DataSerializers.BYTE);
   protected static final DataParameter<Optional<UUID>> field_212362_a = EntityDataManager.createKey(EntityArrow.class, DataSerializers.OPTIONAL_UNIQUE_ID);
   private int xTile = -1;
   private int yTile = -1;
   private int zTile = -1;
   @Nullable
   private IBlockState inBlockState;
   protected boolean inGround;
   protected int timeInGround;
   public EntityArrow.PickupStatus pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
   public int arrowShake;
   public UUID shootingEntity;
   private int ticksInGround;
   private int ticksInAir;
   private double damage = 2.0D;
   private int knockbackStrength;

   protected EntityArrow(EntityType<?> p_i48546_1_, World p_i48546_2_) {
      super(p_i48546_1_, p_i48546_2_);
      this.setSize(0.5F, 0.5F);
   }

   protected EntityArrow(EntityType<?> p_i48547_1_, double p_i48547_2_, double p_i48547_4_, double p_i48547_6_, World p_i48547_8_) {
      this(p_i48547_1_, p_i48547_8_);
      this.setPosition(p_i48547_2_, p_i48547_4_, p_i48547_6_);
   }

   protected EntityArrow(EntityType<?> p_i48548_1_, EntityLivingBase p_i48548_2_, World p_i48548_3_) {
      this(p_i48548_1_, p_i48548_2_.posX, p_i48548_2_.posY + (double)p_i48548_2_.getEyeHeight() - (double)0.1F, p_i48548_2_.posZ, p_i48548_3_);
      this.func_212361_a(p_i48548_2_);
      if (p_i48548_2_ instanceof EntityPlayer) {
         this.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 10.0D;
      if (Double.isNaN(d0)) {
         d0 = 1.0D;
      }

      d0 = d0 * 64.0D * getRenderDistanceWeight();
      return p_70112_1_ < d0 * d0;
   }

   protected void registerData() {
      this.dataManager.register(CRITICAL, (byte)0);
      this.dataManager.register(field_212362_a, Optional.empty());
   }

   public void shoot(Entity p_184547_1_, float p_184547_2_, float p_184547_3_, float p_184547_4_, float p_184547_5_, float p_184547_6_) {
      float f = -MathHelper.sin(p_184547_3_ * ((float)Math.PI / 180F)) * MathHelper.cos(p_184547_2_ * ((float)Math.PI / 180F));
      float f1 = -MathHelper.sin(p_184547_2_ * ((float)Math.PI / 180F));
      float f2 = MathHelper.cos(p_184547_3_ * ((float)Math.PI / 180F)) * MathHelper.cos(p_184547_2_ * ((float)Math.PI / 180F));
      this.shoot((double)f, (double)f1, (double)f2, p_184547_5_, p_184547_6_);
      this.motionX += p_184547_1_.motionX;
      this.motionZ += p_184547_1_.motionZ;
      if (!p_184547_1_.onGround) {
         this.motionY += p_184547_1_.motionY;
      }

   }

   public void shoot(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_) {
      float f = MathHelper.sqrt(p_70186_1_ * p_70186_1_ + p_70186_3_ * p_70186_3_ + p_70186_5_ * p_70186_5_);
      p_70186_1_ = p_70186_1_ / (double)f;
      p_70186_3_ = p_70186_3_ / (double)f;
      p_70186_5_ = p_70186_5_ / (double)f;
      p_70186_1_ = p_70186_1_ + this.rand.nextGaussian() * (double)0.0075F * (double)p_70186_8_;
      p_70186_3_ = p_70186_3_ + this.rand.nextGaussian() * (double)0.0075F * (double)p_70186_8_;
      p_70186_5_ = p_70186_5_ + this.rand.nextGaussian() * (double)0.0075F * (double)p_70186_8_;
      p_70186_1_ = p_70186_1_ * (double)p_70186_7_;
      p_70186_3_ = p_70186_3_ * (double)p_70186_7_;
      p_70186_5_ = p_70186_5_ * (double)p_70186_7_;
      this.motionX = p_70186_1_;
      this.motionY = p_70186_3_;
      this.motionZ = p_70186_5_;
      float f1 = MathHelper.sqrt(p_70186_1_ * p_70186_1_ + p_70186_5_ * p_70186_5_);
      this.rotationYaw = (float)(MathHelper.atan2(p_70186_1_, p_70186_5_) * (double)(180F / (float)Math.PI));
      this.rotationPitch = (float)(MathHelper.atan2(p_70186_3_, (double)f1) * (double)(180F / (float)Math.PI));
      this.prevRotationYaw = this.rotationYaw;
      this.prevRotationPitch = this.rotationPitch;
      this.ticksInGround = 0;
   }

   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
      this.setPosition(p_180426_1_, p_180426_3_, p_180426_5_);
      this.setRotation(p_180426_7_, p_180426_8_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
      this.motionX = p_70016_1_;
      this.motionY = p_70016_3_;
      this.motionZ = p_70016_5_;
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
         this.rotationPitch = (float)(MathHelper.atan2(p_70016_3_, (double)f) * (double)(180F / (float)Math.PI));
         this.rotationYaw = (float)(MathHelper.atan2(p_70016_1_, p_70016_5_) * (double)(180F / (float)Math.PI));
         this.prevRotationPitch = this.rotationPitch;
         this.prevRotationYaw = this.rotationYaw;
         this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         this.ticksInGround = 0;
      }

   }

   public void tick() {
      super.tick();
      boolean flag = this.func_203047_q();
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (double)(180F / (float)Math.PI));
         this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * (double)(180F / (float)Math.PI));
         this.prevRotationYaw = this.rotationYaw;
         this.prevRotationPitch = this.rotationPitch;
      }

      BlockPos blockpos = new BlockPos(this.xTile, this.yTile, this.zTile);
      IBlockState iblockstate = this.world.getBlockState(blockpos);
      if (!iblockstate.isAir() && !flag) {
         VoxelShape voxelshape = iblockstate.getCollisionShape(this.world, blockpos);
         if (!voxelshape.isEmpty()) {
            for(AxisAlignedBB axisalignedbb : voxelshape.toBoundingBoxList()) {
               if (axisalignedbb.offset(blockpos).contains(new Vec3d(this.posX, this.posY, this.posZ))) {
                  this.inGround = true;
                  break;
               }
            }
         }
      }

      if (this.arrowShake > 0) {
         --this.arrowShake;
      }

      if (this.isWet()) {
         this.extinguish();
      }

      if (this.inGround && !flag) {
         if (this.inBlockState != iblockstate && this.world.isCollisionBoxesEmpty(null, this.getEntityBoundingBox().grow(0.05D))) {
            this.inGround = false;
            this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
            this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
            this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
            this.ticksInGround = 0;
            this.ticksInAir = 0;
         } else {
            this.tryDespawn();
         }

         ++this.timeInGround;
      } else {
         this.timeInGround = 0;
         ++this.ticksInAir;
         Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
         Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d, vec3d1, RayTraceFluidMode.NEVER, true, false);
         vec3d = new Vec3d(this.posX, this.posY, this.posZ);
         vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         if (raytraceresult != null) {
            vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
         }

         Entity entity = this.findEntityOnPath(vec3d, vec3d1);
         if (entity != null) {
            raytraceresult = new RayTraceResult(entity);
         }

         if (raytraceresult != null && raytraceresult.entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)raytraceresult.entity;
            Entity entity1 = this.func_212360_k();
            if (entity1 instanceof EntityPlayer && !((EntityPlayer)entity1).canAttackPlayer(entityplayer)) {
               raytraceresult = null;
            }
         }

         if (raytraceresult != null && !flag) {
            this.onHit(raytraceresult);
            this.isAirBorne = true;
         }

         if (this.getIsCritical()) {
            for(int j = 0; j < 4; ++j) {
               this.world.spawnParticle(Particles.CRIT, this.posX + this.motionX * (double)j / 4.0D, this.posY + this.motionY * (double)j / 4.0D, this.posZ + this.motionZ * (double)j / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
            }
         }

         this.posX += this.motionX;
         this.posY += this.motionY;
         this.posZ += this.motionZ;
         float f3 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (flag) {
            this.rotationYaw = (float)(MathHelper.atan2(-this.motionX, -this.motionZ) * (double)(180F / (float)Math.PI));
         } else {
            this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (double)(180F / (float)Math.PI));
         }

         for(this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f3) * (double)(180F / (float)Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
         }

         while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
         }

         while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
         }

         while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
         }

         this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
         this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
         float f4 = 0.99F;
         float f1 = 0.05F;
         if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
               float f2 = 0.25F;
               this.world.spawnParticle(Particles.BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ);
            }

            f4 = this.getWaterDrag();
         }

         this.motionX *= (double)f4;
         this.motionY *= (double)f4;
         this.motionZ *= (double)f4;
         if (!this.hasNoGravity() && !flag) {
            this.motionY -= (double)0.05F;
         }

         this.setPosition(this.posX, this.posY, this.posZ);
         this.doBlockCollisions();
      }
   }

   protected void tryDespawn() {
      ++this.ticksInGround;
      if (this.ticksInGround >= 1200) {
         this.setDead();
      }

   }

   protected void onHit(RayTraceResult p_184549_1_) {
      if (p_184549_1_.entity != null) {
         this.onHitEntity(p_184549_1_);
      } else {
         BlockPos blockpos = p_184549_1_.getBlockPos();
         this.xTile = blockpos.getX();
         this.yTile = blockpos.getY();
         this.zTile = blockpos.getZ();
         IBlockState iblockstate = this.world.getBlockState(blockpos);
         this.inBlockState = iblockstate;
         this.motionX = (double)((float)(p_184549_1_.hitVec.x - this.posX));
         this.motionY = (double)((float)(p_184549_1_.hitVec.y - this.posY));
         this.motionZ = (double)((float)(p_184549_1_.hitVec.z - this.posZ));
         float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ) * 20.0F;
         this.posX -= this.motionX / (double)f;
         this.posY -= this.motionY / (double)f;
         this.posZ -= this.motionZ / (double)f;
         this.playSound(this.getHitGroundSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
         this.inGround = true;
         this.arrowShake = 7;
         this.setIsCritical(false);
         if (!iblockstate.isAir()) {
            this.inBlockState.onEntityCollision(this.world, blockpos, this);
         }
      }

   }

   protected void onHitEntity(RayTraceResult p_203046_1_) {
      Entity entity = p_203046_1_.entity;
      float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
      int i = MathHelper.ceil((double)f * this.damage);
      if (this.getIsCritical()) {
         i += this.rand.nextInt(i / 2 + 2);
      }

      Entity entity1 = this.func_212360_k();
      DamageSource damagesource;
      if (entity1 == null) {
         damagesource = DamageSource.causeArrowDamage(this, this);
      } else {
         damagesource = DamageSource.causeArrowDamage(this, entity1);
      }

      if (this.isBurning() && !(entity instanceof EntityEnderman)) {
         entity.setFire(5);
      }

      if (entity.attackEntityFrom(damagesource, (float)i)) {
         if (entity instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
            if (!this.world.isRemote) {
               entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
            }

            if (this.knockbackStrength > 0) {
               float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
               if (f1 > 0.0F) {
                  entitylivingbase.addVelocity(this.motionX * (double)this.knockbackStrength * (double)0.6F / (double)f1, 0.1D, this.motionZ * (double)this.knockbackStrength * (double)0.6F / (double)f1);
               }
            }

            if (entity1 instanceof EntityLivingBase) {
               EnchantmentHelper.applyThornEnchantments(entitylivingbase, entity1);
               EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase)entity1, entitylivingbase);
            }

            this.arrowHit(entitylivingbase);
            if (entity1 != null && entitylivingbase != entity1 && entitylivingbase instanceof EntityPlayer && entity1 instanceof EntityPlayerMP) {
               ((EntityPlayerMP)entity1).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
            }
         }

         this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
         if (!(entity instanceof EntityEnderman)) {
            this.setDead();
         }
      } else {
         this.motionX *= (double)-0.1F;
         this.motionY *= (double)-0.1F;
         this.motionZ *= (double)-0.1F;
         this.rotationYaw += 180.0F;
         this.prevRotationYaw += 180.0F;
         this.ticksInAir = 0;
         if (!this.world.isRemote && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < (double)0.001F) {
            if (this.pickupStatus == EntityArrow.PickupStatus.ALLOWED) {
               this.entityDropItem(this.getArrowStack(), 0.1F);
            }

            this.setDead();
         }
      }

   }

   protected SoundEvent getHitGroundSound() {
      return SoundEvents.ENTITY_ARROW_HIT;
   }

   public void move(MoverType p_70091_1_, double p_70091_2_, double p_70091_4_, double p_70091_6_) {
      super.move(p_70091_1_, p_70091_2_, p_70091_4_, p_70091_6_);
      if (this.inGround) {
         this.xTile = MathHelper.floor(this.posX);
         this.yTile = MathHelper.floor(this.posY);
         this.zTile = MathHelper.floor(this.posZ);
      }

   }

   protected void arrowHit(EntityLivingBase p_184548_1_) {
   }

   @Nullable
   protected Entity findEntityOnPath(Vec3d p_184551_1_, Vec3d p_184551_2_) {
      Entity entity = null;
      List<Entity> list = this.world.func_175674_a(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D), ARROW_TARGETS);
      double d0 = 0.0D;

      for(int i = 0; i < list.size(); ++i) {
         Entity entity1 = list.get(i);
         if (entity1 != this.func_212360_k() || this.ticksInAir >= 5) {
            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double)0.3F);
            RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(p_184551_1_, p_184551_2_);
            if (raytraceresult != null) {
               double d1 = p_184551_1_.squareDistanceTo(raytraceresult.hitVec);
               if (d1 < d0 || d0 == 0.0D) {
                  entity = entity1;
                  d0 = d1;
               }
            }
         }
      }

      return entity;
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      p_70014_1_.setInteger("xTile", this.xTile);
      p_70014_1_.setInteger("yTile", this.yTile);
      p_70014_1_.setInteger("zTile", this.zTile);
      p_70014_1_.setShort("life", (short)this.ticksInGround);
      if (this.inBlockState != null) {
         p_70014_1_.setTag("inBlockState", NBTUtil.writeBlockState(this.inBlockState));
      }

      p_70014_1_.setByte("shake", (byte)this.arrowShake);
      p_70014_1_.setByte("inGround", (byte)(this.inGround ? 1 : 0));
      p_70014_1_.setByte("pickup", (byte)this.pickupStatus.ordinal());
      p_70014_1_.setDouble("damage", this.damage);
      p_70014_1_.setBoolean("crit", this.getIsCritical());
      if (this.shootingEntity != null) {
         p_70014_1_.setUniqueId("OwnerUUID", this.shootingEntity);
      }

   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      this.xTile = p_70037_1_.getInteger("xTile");
      this.yTile = p_70037_1_.getInteger("yTile");
      this.zTile = p_70037_1_.getInteger("zTile");
      this.ticksInGround = p_70037_1_.getShort("life");
      if (p_70037_1_.hasKey("inBlockState", 10)) {
         this.inBlockState = NBTUtil.readBlockState(p_70037_1_.getCompoundTag("inBlockState"));
      }

      this.arrowShake = p_70037_1_.getByte("shake") & 255;
      this.inGround = p_70037_1_.getByte("inGround") == 1;
      if (p_70037_1_.hasKey("damage", 99)) {
         this.damage = p_70037_1_.getDouble("damage");
      }

      if (p_70037_1_.hasKey("pickup", 99)) {
         this.pickupStatus = EntityArrow.PickupStatus.getByOrdinal(p_70037_1_.getByte("pickup"));
      } else if (p_70037_1_.hasKey("player", 99)) {
         this.pickupStatus = p_70037_1_.getBoolean("player") ? EntityArrow.PickupStatus.ALLOWED : EntityArrow.PickupStatus.DISALLOWED;
      }

      this.setIsCritical(p_70037_1_.getBoolean("crit"));
      if (p_70037_1_.hasUniqueId("OwnerUUID")) {
         this.shootingEntity = p_70037_1_.getUniqueId("OwnerUUID");
      }

   }

   public void func_212361_a(@Nullable Entity p_212361_1_) {
      this.shootingEntity = p_212361_1_ == null ? null : p_212361_1_.getUniqueID();
   }

   @Nullable
   public Entity func_212360_k() {
      return this.shootingEntity != null && this.world instanceof WorldServer ? ((WorldServer)this.world).getEntityFromUuid(this.shootingEntity) : null;
   }

   public void onCollideWithPlayer(EntityPlayer p_70100_1_) {
      if (!this.world.isRemote && (this.inGround || this.func_203047_q()) && this.arrowShake <= 0) {
         boolean flag = this.pickupStatus == EntityArrow.PickupStatus.ALLOWED || this.pickupStatus == EntityArrow.PickupStatus.CREATIVE_ONLY && p_70100_1_.capabilities.isCreativeMode || this.func_203047_q() && this.func_212360_k().getUniqueID() == p_70100_1_.getUniqueID();
         if (this.pickupStatus == EntityArrow.PickupStatus.ALLOWED && !p_70100_1_.inventory.addItemStackToInventory(this.getArrowStack())) {
            flag = false;
         }

         if (flag) {
            p_70100_1_.onItemPickup(this, 1);
            this.setDead();
         }

      }
   }

   protected abstract ItemStack getArrowStack();

   protected boolean canTriggerWalking() {
      return false;
   }

   public void setDamage(double p_70239_1_) {
      this.damage = p_70239_1_;
   }

   public double getDamage() {
      return this.damage;
   }

   public void setKnockbackStrength(int p_70240_1_) {
      this.knockbackStrength = p_70240_1_;
   }

   public boolean canBeAttackedWithItem() {
      return false;
   }

   public float getEyeHeight() {
      return 0.0F;
   }

   public void setIsCritical(boolean p_70243_1_) {
      this.func_203049_a(1, p_70243_1_);
   }

   private void func_203049_a(int p_203049_1_, boolean p_203049_2_) {
      byte b0 = this.dataManager.get(CRITICAL);
      if (p_203049_2_) {
         this.dataManager.set(CRITICAL, (byte)(b0 | p_203049_1_));
      } else {
         this.dataManager.set(CRITICAL, (byte)(b0 & ~p_203049_1_));
      }

   }

   public boolean getIsCritical() {
      byte b0 = this.dataManager.get(CRITICAL);
      return (b0 & 1) != 0;
   }

   public void setEnchantmentEffectsFromEntity(EntityLivingBase p_190547_1_, float p_190547_2_) {
      int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, p_190547_1_);
      int j = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, p_190547_1_);
      this.setDamage((double)(p_190547_2_ * 2.0F) + this.rand.nextGaussian() * 0.25D + (double)((float)this.world.getDifficulty().getId() * 0.11F));
      if (i > 0) {
         this.setDamage(this.getDamage() + (double)i * 0.5D + 0.5D);
      }

      if (j > 0) {
         this.setKnockbackStrength(j);
      }

      if (EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FLAME, p_190547_1_) > 0) {
         this.setFire(100);
      }

   }

   protected float getWaterDrag() {
      return 0.6F;
   }

   public void func_203045_n(boolean p_203045_1_) {
      this.noClip = p_203045_1_;
      this.func_203049_a(2, p_203045_1_);
   }

   public boolean func_203047_q() {
      if (!this.world.isRemote) {
         return this.noClip;
      } else {
         return (this.dataManager.get(CRITICAL) & 2) != 0;
      }
   }

   public enum PickupStatus {
      DISALLOWED,
      ALLOWED,
      CREATIVE_ONLY;

      public static EntityArrow.PickupStatus getByOrdinal(int p_188795_0_) {
         if (p_188795_0_ < 0 || p_188795_0_ > values().length) {
            p_188795_0_ = 0;
         }

         return values()[p_188795_0_];
      }
   }
}
