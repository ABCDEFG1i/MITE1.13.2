package net.minecraft.entity.monster;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityZombieVillager extends EntityZombie {
   private static final DataParameter<Boolean> CONVERTING = EntityDataManager.createKey(EntityZombieVillager.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> PROFESSION = EntityDataManager.createKey(EntityZombieVillager.class, DataSerializers.VARINT);
   private int conversionTime;
   private UUID converstionStarter;

   public EntityZombieVillager(World p_i47277_1_) {
      super(EntityType.ZOMBIE_VILLAGER, p_i47277_1_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(CONVERTING, false);
      this.dataManager.register(PROFESSION, 0);
   }

   public void setProfession(int p_190733_1_) {
      this.dataManager.set(PROFESSION, p_190733_1_);
   }

   public int getProfession() {
      return Math.max(this.dataManager.get(PROFESSION) % 6, 0);
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("Profession", this.getProfession());
      p_70014_1_.setInteger("ConversionTime", this.isConverting() ? this.conversionTime : -1);
      if (this.converstionStarter != null) {
         p_70014_1_.setUniqueId("ConversionPlayer", this.converstionStarter);
      }

   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.setProfession(p_70037_1_.getInteger("Profession"));
      if (p_70037_1_.hasKey("ConversionTime", 99) && p_70037_1_.getInteger("ConversionTime") > -1) {
         this.startConverting(p_70037_1_.hasUniqueId("ConversionPlayer") ? p_70037_1_.getUniqueId("ConversionPlayer") : null, p_70037_1_.getInteger("ConversionTime"));
      }

   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      this.setProfession(this.world.rand.nextInt(6));
      return super.onInitialSpawn(p_204210_1_, p_204210_2_, p_204210_3_);
   }

   public void tick() {
      if (!this.world.isRemote && this.isConverting()) {
         int i = this.getConversionProgress();
         this.conversionTime -= i;
         if (this.conversionTime <= 0) {
            this.finishConversion();
         }
      }

      super.tick();
   }

   public boolean processInteract(EntityPlayer p_184645_1_, EnumHand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
      if (itemstack.getItem() == Items.GOLDEN_APPLE && this.isPotionActive(MobEffects.WEAKNESS)) {
         if (!p_184645_1_.capabilities.isCreativeMode) {
            itemstack.shrink(1);
         }

         if (!this.world.isRemote) {
            this.startConverting(p_184645_1_.getUniqueID(), this.rand.nextInt(2401) + 3600);
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean shouldDrown() {
      return false;
   }

   public boolean canDespawn() {
      return !this.isConverting();
   }

   public boolean isConverting() {
      return this.getDataManager().get(CONVERTING);
   }

   protected void startConverting(@Nullable UUID p_191991_1_, int p_191991_2_) {
      this.converstionStarter = p_191991_1_;
      this.conversionTime = p_191991_2_;
      this.getDataManager().set(CONVERTING, true);
      this.removePotionEffect(MobEffects.WEAKNESS);
      this.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, p_191991_2_, Math.min(this.world.getDifficulty().getId() - 1, 0)));
      this.world.setEntityState(this, (byte)16);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 16) {
         if (!this.isSilent()) {
            this.world.playSound(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, this.getSoundCategory(), 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F, false);
         }
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   protected void finishConversion() {
      EntityVillager entityvillager = new EntityVillager(this.world);
      entityvillager.copyLocationAndAnglesFrom(this);
      entityvillager.setProfession(this.getProfession());
      entityvillager.finalizeMobSpawn(this.world.getDifficultyForLocation(new BlockPos(entityvillager)), null,
              null, false);
      entityvillager.setLookingForHome();
      if (this.isChild()) {
         entityvillager.setGrowingAge(-24000);
      }

      this.world.removeEntity(this);
      entityvillager.setNoAI(this.isAIDisabled());
      if (this.hasCustomName()) {
         entityvillager.setCustomName(this.getCustomName());
         entityvillager.setCustomNameVisible(this.isCustomNameVisible());
      }

      this.world.spawnEntity(entityvillager);
      if (this.converstionStarter != null) {
         EntityPlayer entityplayer = this.world.getPlayerEntityByUUID(this.converstionStarter);
         if (entityplayer instanceof EntityPlayerMP) {
            CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((EntityPlayerMP)entityplayer, this, entityvillager);
         }
      }

      entityvillager.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0));
      this.world.playEvent(null, 1027, new BlockPos((int)this.posX, (int)this.posY, (int)this.posZ), 0);
   }

   protected int getConversionProgress() {
      int i = 1;
      if (this.rand.nextFloat() < 0.01F) {
         int j = 0;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int k = (int)this.posX - 4; k < (int)this.posX + 4 && j < 14; ++k) {
            for(int l = (int)this.posY - 4; l < (int)this.posY + 4 && j < 14; ++l) {
               for(int i1 = (int)this.posZ - 4; i1 < (int)this.posZ + 4 && j < 14; ++i1) {
                  Block block = this.world.getBlockState(blockpos$mutableblockpos.setPos(k, l, i1)).getBlock();
                  if (block == Blocks.IRON_BARS || block instanceof BlockBed) {
                     if (this.rand.nextFloat() < 0.3F) {
                        ++i;
                     }

                     ++j;
                  }
               }
            }
         }
      }

      return i;
   }

   protected float getSoundPitch() {
      return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 2.0F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
   }

   public SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT;
   }

   public SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_HURT;
   }

   public SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_DEATH;
   }

   public SoundEvent getStepSound() {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_STEP;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_ZOMBIE_VILLAGER;
   }

   protected ItemStack getSkullDrop() {
      return ItemStack.EMPTY;
   }
}
