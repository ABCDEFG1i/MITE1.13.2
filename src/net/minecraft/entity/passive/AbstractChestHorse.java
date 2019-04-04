package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public abstract class AbstractChestHorse extends AbstractHorse {
   private static final DataParameter<Boolean> DATA_ID_CHEST = EntityDataManager.createKey(AbstractChestHorse.class, DataSerializers.BOOLEAN);

   protected AbstractChestHorse(EntityType<?> p_i48564_1_, World p_i48564_2_) {
      super(p_i48564_1_, p_i48564_2_);
      this.canGallop = false;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(DATA_ID_CHEST, false);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)this.getModifiedMaxHealth());
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.175F);
      this.getAttribute(JUMP_STRENGTH).setBaseValue(0.5D);
   }

   public boolean hasChest() {
      return this.dataManager.get(DATA_ID_CHEST);
   }

   public void setChested(boolean p_110207_1_) {
      this.dataManager.set(DATA_ID_CHEST, p_110207_1_);
   }

   protected int getInventorySize() {
      return this.hasChest() ? 17 : super.getInventorySize();
   }

   public double getMountedYOffset() {
      return super.getMountedYOffset() - 0.25D;
   }

   protected SoundEvent getAngrySound() {
      super.getAngrySound();
      return SoundEvents.ENTITY_DONKEY_ANGRY;
   }

   public void onDeath(DamageSource p_70645_1_) {
      super.onDeath(p_70645_1_);
      if (this.hasChest()) {
         if (!this.world.isRemote) {
            this.entityDropItem(Blocks.CHEST);
         }

         this.setChested(false);
      }

   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setBoolean("ChestedHorse", this.hasChest());
      if (this.hasChest()) {
         NBTTagList nbttaglist = new NBTTagList();

         for(int i = 2; i < this.horseChest.getSizeInventory(); ++i) {
            ItemStack itemstack = this.horseChest.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
               NBTTagCompound nbttagcompound = new NBTTagCompound();
               nbttagcompound.setByte("Slot", (byte)i);
               itemstack.write(nbttagcompound);
               nbttaglist.add(nbttagcompound);
            }
         }

         p_70014_1_.setTag("Items", nbttaglist);
      }

   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.setChested(p_70037_1_.getBoolean("ChestedHorse"));
      if (this.hasChest()) {
         NBTTagList nbttaglist = p_70037_1_.getTagList("Items", 10);
         this.initHorseChest();

         for(int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            if (j >= 2 && j < this.horseChest.getSizeInventory()) {
               this.horseChest.setInventorySlotContents(j, ItemStack.loadFromNBT(nbttagcompound));
            }
         }
      }

      this.updateHorseSlots();
   }

   public boolean replaceItemInInventory(int p_174820_1_, ItemStack p_174820_2_) {
      if (p_174820_1_ == 499) {
         if (this.hasChest() && p_174820_2_.isEmpty()) {
            this.setChested(false);
            this.initHorseChest();
            return true;
         }

         if (!this.hasChest() && p_174820_2_.getItem() == Blocks.CHEST.asItem()) {
            this.setChested(true);
            this.initHorseChest();
            return true;
         }
      }

      return super.replaceItemInInventory(p_174820_1_, p_174820_2_);
   }

   public boolean processInteract(EntityPlayer p_184645_1_, EnumHand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
      if (itemstack.getItem() instanceof ItemSpawnEgg) {
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

         if (!itemstack.isEmpty()) {
            boolean flag = this.handleEating(p_184645_1_, itemstack);
            if (!flag) {
               if (!this.isTame() || itemstack.getItem() == Items.NAME_TAG) {
                  if (itemstack.interactWithEntity(p_184645_1_, this, p_184645_2_)) {
                     return true;
                  } else {
                     this.makeMad();
                     return true;
                  }
               }

               if (!this.hasChest() && itemstack.getItem() == Blocks.CHEST.asItem()) {
                  this.setChested(true);
                  this.playChestEquipSound();
                  flag = true;
                  this.initHorseChest();
               }

               if (!this.isChild() && !this.isHorseSaddled() && itemstack.getItem() == Items.SADDLE) {
                  this.openGUI(p_184645_1_);
                  return true;
               }
            }

            if (flag) {
               if (!p_184645_1_.capabilities.isCreativeMode) {
                  itemstack.shrink(1);
               }

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

   protected void playChestEquipSound() {
      this.playSound(SoundEvents.ENTITY_DONKEY_CHEST, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
   }

   public int getInventoryColumns() {
      return 5;
   }
}
