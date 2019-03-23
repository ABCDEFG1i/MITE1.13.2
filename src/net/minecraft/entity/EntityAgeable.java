package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public abstract class EntityAgeable extends EntityCreature {
   private static final DataParameter<Boolean> BABY = EntityDataManager.createKey(EntityAgeable.class, DataSerializers.BOOLEAN);
   protected int growingAge;
   protected int forcedAge;
   protected int forcedAgeTimer;
   private float ageWidth = -1.0F;
   private float ageHeight;

   protected EntityAgeable(EntityType<?> p_i48581_1_, World p_i48581_2_) {
      super(p_i48581_1_, p_i48581_2_);
   }

   @Nullable
   public abstract EntityAgeable createChild(EntityAgeable p_90011_1_);

   public boolean processInteract(EntityPlayer p_184645_1_, EnumHand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
      Item item = itemstack.getItem();
      if (item instanceof ItemSpawnEgg && ((ItemSpawnEgg)item).hasType(itemstack.getTag(), this.getType())) {
         if (!this.world.isRemote) {
            EntityAgeable entityageable = this.createChild(this);
            if (entityageable != null) {
               entityageable.setGrowingAge(-24000);
               entityageable.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
               this.world.spawnEntity(entityageable);
               if (itemstack.hasDisplayName()) {
                  entityageable.setCustomName(itemstack.getDisplayName());
               }

               if (!p_184645_1_.capabilities.isCreativeMode) {
                  itemstack.shrink(1);
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(BABY, false);
   }

   public int getGrowingAge() {
      if (this.world.isRemote) {
         return this.dataManager.get(BABY) ? -1 : 1;
      } else {
         return this.growingAge;
      }
   }

   public void ageUp(int p_175501_1_, boolean p_175501_2_) {
      int i = this.getGrowingAge();
      int j = i;
      i = i + p_175501_1_ * 20;
      if (i > 0) {
         i = 0;
         if (j < 0) {
            this.onGrowingAdult();
         }
      }

      int k = i - j;
      this.setGrowingAge(i);
      if (p_175501_2_) {
         this.forcedAge += k;
         if (this.forcedAgeTimer == 0) {
            this.forcedAgeTimer = 40;
         }
      }

      if (this.getGrowingAge() == 0) {
         this.setGrowingAge(this.forcedAge);
      }

   }

   public void addGrowth(int p_110195_1_) {
      this.ageUp(p_110195_1_, false);
   }

   public void setGrowingAge(int p_70873_1_) {
      this.dataManager.set(BABY, p_70873_1_ < 0);
      this.growingAge = p_70873_1_;
      this.setScaleForAge(this.isChild());
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("Age", this.getGrowingAge());
      p_70014_1_.setInteger("ForcedAge", this.forcedAge);
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.setGrowingAge(p_70037_1_.getInteger("Age"));
      this.forcedAge = p_70037_1_.getInteger("ForcedAge");
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (BABY.equals(p_184206_1_)) {
         this.setScaleForAge(this.isChild());
      }

      super.notifyDataManagerChange(p_184206_1_);
   }

   public void livingTick() {
      super.livingTick();
      if (this.world.isRemote) {
         if (this.forcedAgeTimer > 0) {
            if (this.forcedAgeTimer % 4 == 0) {
               this.world.spawnParticle(Particles.HAPPY_VILLAGER, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, 0.0D, 0.0D, 0.0D);
            }

            --this.forcedAgeTimer;
         }
      } else {
         int i = this.getGrowingAge();
         if (i < 0) {
            ++i;
            this.setGrowingAge(i);
            if (i == 0) {
               this.onGrowingAdult();
            }
         } else if (i > 0) {
            --i;
            this.setGrowingAge(i);
         }
      }

   }

   protected void onGrowingAdult() {
   }

   public boolean isChild() {
      return this.getGrowingAge() < 0;
   }

   public void setScaleForAge(boolean p_98054_1_) {
      this.setScale(p_98054_1_ ? 0.5F : 1.0F);
   }

   protected final void setSize(float p_70105_1_, float p_70105_2_) {
      this.ageWidth = p_70105_1_;
      this.ageHeight = p_70105_2_;
      this.setScale(1.0F);
   }

   protected final void setScale(float p_98055_1_) {
      super.setSize(this.ageWidth * p_98055_1_, this.ageHeight * p_98055_1_);
   }
}
