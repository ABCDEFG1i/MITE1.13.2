package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class EntityDamageSourceIndirect extends EntityDamageSource {
   private final Entity indirectEntity;

   public EntityDamageSourceIndirect(String p_i1568_1_, Entity p_i1568_2_, @Nullable Entity p_i1568_3_) {
      super(p_i1568_1_, p_i1568_2_);
      this.indirectEntity = p_i1568_3_;
   }

   @Nullable
   public Entity getImmediateSource() {
      return this.damageSourceEntity;
   }

   @Nullable
   public Entity getTrueSource() {
      return this.indirectEntity;
   }

   public ITextComponent getDeathMessage(EntityLivingBase p_151519_1_) {
      ITextComponent itextcomponent = this.indirectEntity == null ? this.damageSourceEntity.getDisplayName() : this.indirectEntity.getDisplayName();
      ItemStack itemstack = this.indirectEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.indirectEntity).getHeldItemMainhand() : ItemStack.EMPTY;
      String s = "death.attack." + this.damageType;
      String s1 = s + ".item";
      return !itemstack.isEmpty() && itemstack.hasDisplayName() ? new TextComponentTranslation(s1, p_151519_1_.getDisplayName(), itextcomponent, itemstack.getTextComponent()) : new TextComponentTranslation(s, p_151519_1_.getDisplayName(), itextcomponent);
   }
}
