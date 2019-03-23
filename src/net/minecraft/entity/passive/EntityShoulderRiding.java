package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class EntityShoulderRiding extends EntityTameable {
   private int rideCooldownCounter;

   protected EntityShoulderRiding(EntityType<?> p_i48566_1_, World p_i48566_2_) {
      super(p_i48566_1_, p_i48566_2_);
   }

   public boolean setEntityOnShoulder(EntityPlayer p_191994_1_) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setString("id", this.getEntityString());
      this.writeToNBT(nbttagcompound);
      if (p_191994_1_.addShoulderEntity(nbttagcompound)) {
         this.world.removeEntity(this);
         return true;
      } else {
         return false;
      }
   }

   public void tick() {
      ++this.rideCooldownCounter;
      super.tick();
   }

   public boolean canSitOnShoulder() {
      return this.rideCooldownCounter > 100;
   }
}
