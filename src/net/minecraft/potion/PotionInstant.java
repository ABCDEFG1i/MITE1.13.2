package net.minecraft.potion;

public class PotionInstant extends Potion {
   public PotionInstant(boolean p_i46816_1_, int p_i46816_2_) {
      super(p_i46816_1_, p_i46816_2_);
   }

   public boolean isInstant() {
      return true;
   }

   public boolean isReady(int p_76397_1_, int p_76397_2_) {
      return p_76397_1_ >= 1;
   }
}
