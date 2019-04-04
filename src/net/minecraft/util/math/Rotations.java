package net.minecraft.util.math;

import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;

public class Rotations {
   protected final float x;
   protected final float y;
   protected final float z;

   public Rotations(float p_i46009_1_, float p_i46009_2_, float p_i46009_3_) {
      this.x = !Float.isInfinite(p_i46009_1_) && !Float.isNaN(p_i46009_1_) ? p_i46009_1_ % 360.0F : 0.0F;
      this.y = !Float.isInfinite(p_i46009_2_) && !Float.isNaN(p_i46009_2_) ? p_i46009_2_ % 360.0F : 0.0F;
      this.z = !Float.isInfinite(p_i46009_3_) && !Float.isNaN(p_i46009_3_) ? p_i46009_3_ % 360.0F : 0.0F;
   }

   public Rotations(NBTTagList p_i46010_1_) {
      this(p_i46010_1_.getFloatAt(0), p_i46010_1_.getFloatAt(1), p_i46010_1_.getFloatAt(2));
   }

   public NBTTagList writeToNBT() {
      NBTTagList nbttaglist = new NBTTagList();
      nbttaglist.add(new NBTTagFloat(this.x));
      nbttaglist.add(new NBTTagFloat(this.y));
      nbttaglist.add(new NBTTagFloat(this.z));
      return nbttaglist;
   }

   public boolean equals(Object p_equals_1_) {
      if (!(p_equals_1_ instanceof Rotations)) {
         return false;
      } else {
         Rotations rotations = (Rotations)p_equals_1_;
         return this.x == rotations.x && this.y == rotations.y && this.z == rotations.z;
      }
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getZ() {
      return this.z;
   }
}
