package net.minecraft.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public class MultiPartEntityPart extends Entity {
   public final IEntityMultiPart parent;
   public final String partName;

   public MultiPartEntityPart(IEntityMultiPart p_i1697_1_, String p_i1697_2_, float p_i1697_3_, float p_i1697_4_) {
      super(p_i1697_1_.getType(), p_i1697_1_.getWorld());
      this.setSize(p_i1697_3_, p_i1697_4_);
      this.parent = p_i1697_1_;
      this.partName = p_i1697_2_;
   }

   protected void registerData() {
   }

   protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
   }

   protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      return !this.isInvulnerableTo(p_70097_1_) && this.parent.attackEntityFromPart(this, p_70097_1_, p_70097_2_);
   }

   public boolean isEntityEqual(Entity p_70028_1_) {
      return this == p_70028_1_ || this.parent == p_70028_1_;
   }
}
