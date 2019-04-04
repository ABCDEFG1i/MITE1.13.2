package net.minecraft.entity.projectile;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityPotion extends EntityThrowable {
   private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntityPotion.class, DataSerializers.ITEM_STACK);
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Predicate<EntityLivingBase> WATER_SENSITIVE = EntityPotion::isWaterSensitiveEntity;

   public EntityPotion(World p_i1788_1_) {
      super(EntityType.POTION, p_i1788_1_);
   }

   public EntityPotion(World p_i1790_1_, EntityLivingBase p_i1790_2_, ItemStack p_i1790_3_) {
      super(EntityType.POTION, p_i1790_2_, p_i1790_1_);
      this.setItem(p_i1790_3_);
   }

   public EntityPotion(World p_i1792_1_, double p_i1792_2_, double p_i1792_4_, double p_i1792_6_, ItemStack p_i1792_8_) {
      super(EntityType.POTION, p_i1792_2_, p_i1792_4_, p_i1792_6_, p_i1792_1_);
      if (!p_i1792_8_.isEmpty()) {
         this.setItem(p_i1792_8_);
      }

   }

   protected void registerData() {
      this.getDataManager().register(ITEM, ItemStack.EMPTY);
   }

   public ItemStack getPotion() {
      ItemStack itemstack = this.getDataManager().get(ITEM);
      if (itemstack.getItem() != Items.SPLASH_POTION && itemstack.getItem() != Items.LINGERING_POTION) {
         if (this.world != null) {
            LOGGER.error("ThrownPotion entity {} has no item?!", this.getEntityId());
         }

         return new ItemStack(Items.SPLASH_POTION);
      } else {
         return itemstack;
      }
   }

   public void setItem(ItemStack p_184541_1_) {
      this.getDataManager().set(ITEM, p_184541_1_);
   }

   protected float getGravityVelocity() {
      return 0.05F;
   }

   protected void onImpact(RayTraceResult p_70184_1_) {
      if (!this.world.isRemote) {
         ItemStack itemstack = this.getPotion();
         PotionType potiontype = PotionUtils.getPotionFromItem(itemstack);
         List<PotionEffect> list = PotionUtils.getEffectsFromStack(itemstack);
         boolean flag = potiontype == PotionTypes.WATER && list.isEmpty();
         if (p_70184_1_.type == RayTraceResult.Type.BLOCK && flag) {
            BlockPos blockpos = p_70184_1_.getBlockPos().offset(p_70184_1_.sideHit);
            this.extinguishFires(blockpos, p_70184_1_.sideHit);

            for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
               this.extinguishFires(blockpos.offset(enumfacing), enumfacing);
            }
         }

         if (flag) {
            this.applyWater();
         } else if (!list.isEmpty()) {
            if (this.isLingering()) {
               this.makeAreaOfEffectCloud(itemstack, potiontype);
            } else {
               this.applySplash(p_70184_1_, list);
            }
         }

         int i = potiontype.hasInstantEffect() ? 2007 : 2002;
         this.world.playEvent(i, new BlockPos(this), PotionUtils.getColor(itemstack));
         this.setDead();
      }
   }

   private void applyWater() {
      AxisAlignedBB axisalignedbb = this.getEntityBoundingBox().grow(4.0D, 2.0D, 4.0D);
      List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb, WATER_SENSITIVE);
      if (!list.isEmpty()) {
         for(EntityLivingBase entitylivingbase : list) {
            double d0 = this.getDistanceSq(entitylivingbase);
            if (d0 < 16.0D && isWaterSensitiveEntity(entitylivingbase)) {
               entitylivingbase.attackEntityFrom(DamageSource.DROWN, 1.0F);
            }
         }
      }

   }

   private void applySplash(RayTraceResult p_190543_1_, List<PotionEffect> p_190543_2_) {
      AxisAlignedBB axisalignedbb = this.getEntityBoundingBox().grow(4.0D, 2.0D, 4.0D);
      List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
      if (!list.isEmpty()) {
         for(EntityLivingBase entitylivingbase : list) {
            if (entitylivingbase.canBeHitWithPotion()) {
               double d0 = this.getDistanceSq(entitylivingbase);
               if (d0 < 16.0D) {
                  double d1 = 1.0D - Math.sqrt(d0) / 4.0D;
                  if (entitylivingbase == p_190543_1_.entity) {
                     d1 = 1.0D;
                  }

                  for(PotionEffect potioneffect : p_190543_2_) {
                     Potion potion = potioneffect.getPotion();
                     if (potion.isInstant()) {
                        potion.affectEntity(this, this.getThrower(), entitylivingbase, potioneffect.getAmplifier(), d1);
                     } else {
                        int i = (int)(d1 * (double)potioneffect.getDuration() + 0.5D);
                        if (i > 20) {
                           entitylivingbase.addPotionEffect(new PotionEffect(potion, i, potioneffect.getAmplifier(), potioneffect.isAmbient(), potioneffect.doesShowParticles()));
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private void makeAreaOfEffectCloud(ItemStack p_190542_1_, PotionType p_190542_2_) {
      EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.world, this.posX, this.posY, this.posZ);
      entityareaeffectcloud.setOwner(this.getThrower());
      entityareaeffectcloud.setRadius(3.0F);
      entityareaeffectcloud.setRadiusOnUse(-0.5F);
      entityareaeffectcloud.setWaitTime(10);
      entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
      entityareaeffectcloud.setPotion(p_190542_2_);

      for(PotionEffect potioneffect : PotionUtils.getFullEffectsFromItem(p_190542_1_)) {
         entityareaeffectcloud.addEffect(new PotionEffect(potioneffect));
      }

      NBTTagCompound nbttagcompound = p_190542_1_.getTag();
      if (nbttagcompound != null && nbttagcompound.hasKey("CustomPotionColor", 99)) {
         entityareaeffectcloud.setColor(nbttagcompound.getInteger("CustomPotionColor"));
      }

      this.world.spawnEntity(entityareaeffectcloud);
   }

   private boolean isLingering() {
      return this.getPotion().getItem() == Items.LINGERING_POTION;
   }

   private void extinguishFires(BlockPos p_184542_1_, EnumFacing p_184542_2_) {
      if (this.world.getBlockState(p_184542_1_).getBlock() == Blocks.FIRE) {
         this.world.extinguishFire(null, p_184542_1_.offset(p_184542_2_), p_184542_2_.getOpposite());
      }

   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      ItemStack itemstack = ItemStack.loadFromNBT(p_70037_1_.getCompoundTag("Potion"));
      if (itemstack.isEmpty()) {
         this.setDead();
      } else {
         this.setItem(itemstack);
      }

   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      ItemStack itemstack = this.getPotion();
      if (!itemstack.isEmpty()) {
         p_70014_1_.setTag("Potion", itemstack.write(new NBTTagCompound()));
      }

   }

   private static boolean isWaterSensitiveEntity(EntityLivingBase p_190544_0_) {
      return p_190544_0_ instanceof EntityEnderman || p_190544_0_ instanceof EntityBlaze;
   }
}
