package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.nbt.NBTTagCompound;

@Immutable
public class LockCode {
   public static final LockCode EMPTY_CODE = new LockCode("");
   private final String lock;

   public LockCode(String p_i45903_1_) {
      this.lock = p_i45903_1_;
   }

   public boolean isEmpty() {
      return this.lock == null || this.lock.isEmpty();
   }

   public String getLock() {
      return this.lock;
   }

   public void toNBT(NBTTagCompound p_180157_1_) {
      p_180157_1_.setString("Lock", this.lock);
   }

   public static LockCode fromNBT(NBTTagCompound p_180158_0_) {
      if (p_180158_0_.hasKey("Lock", 8)) {
         String s = p_180158_0_.getString("Lock");
         return new LockCode(s);
      } else {
         return EMPTY_CODE;
      }
   }
}
