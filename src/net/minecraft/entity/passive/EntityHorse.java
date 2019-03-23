package net.minecraft.entity.passive;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityHorse extends AbstractHorse {
   private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
   private static final DataParameter<Integer> HORSE_VARIANT = EntityDataManager.createKey(EntityHorse.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> HORSE_ARMOR = EntityDataManager.createKey(EntityHorse.class, DataSerializers.VARINT);
   private static final String[] HORSE_TEXTURES = new String[]{"textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png"};
   private static final String[] HORSE_TEXTURES_ABBR = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
   private static final String[] HORSE_MARKING_TEXTURES = new String[]{null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png"};
   private static final String[] HORSE_MARKING_TEXTURES_ABBR = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
   private String texturePrefix;
   private final String[] horseTexturesArray = new String[3];

   public EntityHorse(World p_i1685_1_) {
      super(EntityType.HORSE, p_i1685_1_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(HORSE_VARIANT, 0);
      this.dataManager.register(HORSE_ARMOR, HorseArmorType.NONE.getOrdinal());
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("Variant", this.getHorseVariant());
      if (!this.horseChest.getStackInSlot(1).isEmpty()) {
         p_70014_1_.setTag("ArmorItem", this.horseChest.getStackInSlot(1).write(new NBTTagCompound()));
      }

   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.setHorseVariant(p_70037_1_.getInteger("Variant"));
      if (p_70037_1_.hasKey("ArmorItem", 10)) {
         ItemStack itemstack = ItemStack.loadFromNBT(p_70037_1_.getCompoundTag("ArmorItem"));
         if (!itemstack.isEmpty() && HorseArmorType.isHorseArmor(itemstack.getItem())) {
            this.horseChest.setInventorySlotContents(1, itemstack);
         }
      }

      this.updateHorseSlots();
   }

   public void setHorseVariant(int p_110235_1_) {
      this.dataManager.set(HORSE_VARIANT, p_110235_1_);
      this.resetTexturePrefix();
   }

   public int getHorseVariant() {
      return this.dataManager.get(HORSE_VARIANT);
   }

   private void resetTexturePrefix() {
      this.texturePrefix = null;
   }

   @OnlyIn(Dist.CLIENT)
   private void setHorseTexturePaths() {
      int i = this.getHorseVariant();
      int j = (i & 255) % 7;
      int k = ((i & '\uff00') >> 8) % 5;
      HorseArmorType horsearmortype = this.getHorseArmorType();
      this.horseTexturesArray[0] = HORSE_TEXTURES[j];
      this.horseTexturesArray[1] = HORSE_MARKING_TEXTURES[k];
      this.horseTexturesArray[2] = horsearmortype.getTextureName();
      this.texturePrefix = "horse/" + HORSE_TEXTURES_ABBR[j] + HORSE_MARKING_TEXTURES_ABBR[k] + horsearmortype.getHash();
   }

   @OnlyIn(Dist.CLIENT)
   public String getHorseTexture() {
      if (this.texturePrefix == null) {
         this.setHorseTexturePaths();
      }

      return this.texturePrefix;
   }

   @OnlyIn(Dist.CLIENT)
   public String[] getVariantTexturePaths() {
      if (this.texturePrefix == null) {
         this.setHorseTexturePaths();
      }

      return this.horseTexturesArray;
   }

   protected void updateHorseSlots() {
      super.updateHorseSlots();
      this.setHorseArmorStack(this.horseChest.getStackInSlot(1));
   }

   public void setHorseArmorStack(ItemStack p_146086_1_) {
      HorseArmorType horsearmortype = HorseArmorType.getByItemStack(p_146086_1_);
      this.dataManager.set(HORSE_ARMOR, horsearmortype.getOrdinal());
      this.resetTexturePrefix();
      if (!this.world.isRemote) {
         this.getAttribute(SharedMonsterAttributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
         int i = horsearmortype.getProtection();
         if (i != 0) {
            this.getAttribute(SharedMonsterAttributes.ARMOR).applyModifier((new AttributeModifier(ARMOR_MODIFIER_UUID, "Horse armor bonus", (double)i, 0)).setSaved(false));
         }
      }

   }

   public HorseArmorType getHorseArmorType() {
      return HorseArmorType.getByOrdinal(this.dataManager.get(HORSE_ARMOR));
   }

   public void onInventoryChanged(IInventory p_76316_1_) {
      HorseArmorType horsearmortype = this.getHorseArmorType();
      super.onInventoryChanged(p_76316_1_);
      HorseArmorType horsearmortype1 = this.getHorseArmorType();
      if (this.ticksExisted > 20 && horsearmortype != horsearmortype1 && horsearmortype1 != HorseArmorType.NONE) {
         this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
      }

   }

   protected void playGallopSound(SoundType p_190680_1_) {
      super.playGallopSound(p_190680_1_);
      if (this.rand.nextInt(10) == 0) {
         this.playSound(SoundEvents.ENTITY_HORSE_BREATHE, p_190680_1_.getVolume() * 0.6F, p_190680_1_.getPitch());
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)this.getModifiedMaxHealth());
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.getModifiedMovementSpeed());
      this.getAttribute(JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
   }

   public void tick() {
      super.tick();
      if (this.world.isRemote && this.dataManager.isDirty()) {
         this.dataManager.setClean();
         this.resetTexturePrefix();
      }

   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.ENTITY_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.ENTITY_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      super.getHurtSound(p_184601_1_);
      return SoundEvents.ENTITY_HORSE_HURT;
   }

   protected SoundEvent getAngrySound() {
      super.getAngrySound();
      return SoundEvents.ENTITY_HORSE_ANGRY;
   }

   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_HORSE;
   }

   public boolean processInteract(EntityPlayer p_184645_1_, EnumHand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
      boolean flag = !itemstack.isEmpty();
      if (flag && itemstack.getItem() instanceof ItemSpawnEgg) {
         return super.processInteract(p_184645_1_, p_184645_2_);
      } else {
         if (!this.isChild()) {
            if (this.isTame() && p_184645_1_.isSneaking()) {
               this.openGUI(p_184645_1_);
               return true;
            }

            if (this.isBeingRidden()) {
               return super.processInteract(p_184645_1_, p_184645_2_);
            }
         }

         if (flag) {
            if (this.handleEating(p_184645_1_, itemstack)) {
               if (!p_184645_1_.capabilities.isCreativeMode) {
                  itemstack.shrink(1);
               }

               return true;
            }

            if (itemstack.interactWithEntity(p_184645_1_, this, p_184645_2_)) {
               return true;
            }

            if (!this.isTame()) {
               this.makeMad();
               return true;
            }

            boolean flag1 = HorseArmorType.getByItemStack(itemstack) != HorseArmorType.NONE;
            boolean flag2 = !this.isChild() && !this.isHorseSaddled() && itemstack.getItem() == Items.SADDLE;
            if (flag1 || flag2) {
               this.openGUI(p_184645_1_);
               return true;
            }
         }

         if (this.isChild()) {
            return super.processInteract(p_184645_1_, p_184645_2_);
         } else {
            this.mountTo(p_184645_1_);
            return true;
         }
      }
   }

   public boolean canMateWith(EntityAnimal p_70878_1_) {
      if (p_70878_1_ == this) {
         return false;
      } else if (!(p_70878_1_ instanceof EntityDonkey) && !(p_70878_1_ instanceof EntityHorse)) {
         return false;
      } else {
         return this.canMate() && ((AbstractHorse)p_70878_1_).canMate();
      }
   }

   public EntityAgeable createChild(EntityAgeable p_90011_1_) {
      AbstractHorse abstracthorse;
      if (p_90011_1_ instanceof EntityDonkey) {
         abstracthorse = new EntityMule(this.world);
      } else {
         EntityHorse entityhorse = (EntityHorse)p_90011_1_;
         abstracthorse = new EntityHorse(this.world);
         int j = this.rand.nextInt(9);
         int i;
         if (j < 4) {
            i = this.getHorseVariant() & 255;
         } else if (j < 8) {
            i = entityhorse.getHorseVariant() & 255;
         } else {
            i = this.rand.nextInt(7);
         }

         int k = this.rand.nextInt(5);
         if (k < 2) {
            i = i | this.getHorseVariant() & '\uff00';
         } else if (k < 4) {
            i = i | entityhorse.getHorseVariant() & '\uff00';
         } else {
            i = i | this.rand.nextInt(5) << 8 & '\uff00';
         }

         ((EntityHorse)abstracthorse).setHorseVariant(i);
      }

      this.setOffspringAttributes(p_90011_1_, abstracthorse);
      return abstracthorse;
   }

   public boolean wearsArmor() {
      return true;
   }

   public boolean isArmor(ItemStack p_190682_1_) {
      return HorseArmorType.isHorseArmor(p_190682_1_.getItem());
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      p_204210_2_ = super.onInitialSpawn(p_204210_1_, p_204210_2_, p_204210_3_);
      int i;
      if (p_204210_2_ instanceof EntityHorse.GroupData) {
         i = ((EntityHorse.GroupData)p_204210_2_).variant;
      } else {
         i = this.rand.nextInt(7);
         p_204210_2_ = new EntityHorse.GroupData(i);
      }

      this.setHorseVariant(i | this.rand.nextInt(5) << 8);
      return p_204210_2_;
   }

   public static class GroupData implements IEntityLivingData {
      public int variant;

      public GroupData(int p_i47337_1_) {
         this.variant = p_i47337_1_;
      }
   }
}
