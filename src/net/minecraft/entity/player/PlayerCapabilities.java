package net.minecraft.entity.player;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PlayerCapabilities {
   public boolean disableDamage;
   public boolean isFlying;
   public boolean allowFlying;
   public boolean isCreativeMode;
   public boolean allowEdit = true;
   private double flySpeed = (double)0.05F;
   private float walkSpeed = 0.1F;

   public void writeCapabilitiesToNBT(NBTTagCompound p_75091_1_) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setBoolean("invulnerable", this.disableDamage);
      nbttagcompound.setBoolean("flying", this.isFlying);
      nbttagcompound.setBoolean("mayfly", this.allowFlying);
      nbttagcompound.setBoolean("instabuild", this.isCreativeMode);
      nbttagcompound.setBoolean("mayBuild", this.allowEdit);
      nbttagcompound.setFloat("flySpeed", (float)this.flySpeed);
      nbttagcompound.setFloat("walkSpeed", this.walkSpeed);
      p_75091_1_.setTag("abilities", nbttagcompound);
   }

   public void readCapabilitiesFromNBT(NBTTagCompound p_75095_1_) {
      if (p_75095_1_.hasKey("abilities", 10)) {
         NBTTagCompound nbttagcompound = p_75095_1_.getCompoundTag("abilities");
         this.disableDamage = nbttagcompound.getBoolean("invulnerable");
         this.isFlying = nbttagcompound.getBoolean("flying");
         this.allowFlying = nbttagcompound.getBoolean("mayfly");
         this.isCreativeMode = nbttagcompound.getBoolean("instabuild");
         if (nbttagcompound.hasKey("flySpeed", 99)) {
            this.flySpeed = (double)nbttagcompound.getFloat("flySpeed");
            this.walkSpeed = nbttagcompound.getFloat("walkSpeed");
         }

         if (nbttagcompound.hasKey("mayBuild", 1)) {
            this.allowEdit = nbttagcompound.getBoolean("mayBuild");
         }
      }

   }

   public float getFlySpeed() {
      return (float)this.flySpeed;
   }

   @OnlyIn(Dist.CLIENT)
   public void setFlySpeed(double p_195931_1_) {
      this.flySpeed = p_195931_1_;
   }

   public float getWalkSpeed() {
      return this.walkSpeed;
   }

   @OnlyIn(Dist.CLIENT)
   public void setWalkSpeed(float p_82877_1_) {
      this.walkSpeed = p_82877_1_;
   }
}
