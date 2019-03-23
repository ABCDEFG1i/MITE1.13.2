package net.minecraft.util;

import net.minecraft.nbt.NBTTagCompound;

public class WeightedSpawnerEntity extends WeightedRandom.Item {
   private final NBTTagCompound nbt;

   public WeightedSpawnerEntity() {
      super(1);
      this.nbt = new NBTTagCompound();
      this.nbt.setString("id", "minecraft:pig");
   }

   public WeightedSpawnerEntity(NBTTagCompound p_i46715_1_) {
      this(p_i46715_1_.hasKey("Weight", 99) ? p_i46715_1_.getInteger("Weight") : 1, p_i46715_1_.getCompoundTag("Entity"));
   }

   public WeightedSpawnerEntity(int p_i46716_1_, NBTTagCompound p_i46716_2_) {
      super(p_i46716_1_);
      this.nbt = p_i46716_2_;
   }

   public NBTTagCompound toCompoundTag() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      if (!this.nbt.hasKey("id", 8)) {
         this.nbt.setString("id", "minecraft:pig");
      } else if (!this.nbt.getString("id").contains(":")) {
         this.nbt.setString("id", (new ResourceLocation(this.nbt.getString("id"))).toString());
      }

      nbttagcompound.setTag("Entity", this.nbt);
      nbttagcompound.setInteger("Weight", this.itemWeight);
      return nbttagcompound;
   }

   public NBTTagCompound getNbt() {
      return this.nbt;
   }
}
