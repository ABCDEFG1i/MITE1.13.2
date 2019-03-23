package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;

public class PotionHealthBoost extends Potion {
   public PotionHealthBoost(boolean p_i46817_1_, int p_i46817_2_) {
      super(p_i46817_1_, p_i46817_2_);
   }

   public void removeAttributesModifiersFromEntity(EntityLivingBase p_111187_1_, AbstractAttributeMap p_111187_2_, int p_111187_3_) {
      super.removeAttributesModifiersFromEntity(p_111187_1_, p_111187_2_, p_111187_3_);
      if (p_111187_1_.getHealth() > p_111187_1_.getMaxHealth()) {
         p_111187_1_.setHealth(p_111187_1_.getMaxHealth());
      }

   }
}
