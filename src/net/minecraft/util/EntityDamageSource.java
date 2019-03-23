package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class EntityDamageSource extends DamageSource {
   @Nullable
   protected Entity damageSourceEntity;
   private boolean isThornsDamage;

   public EntityDamageSource(String p_i1567_1_, @Nullable Entity p_i1567_2_) {
      super(p_i1567_1_);
      this.damageSourceEntity = p_i1567_2_;
   }

   public EntityDamageSource setIsThornsDamage() {
      this.isThornsDamage = true;
      return this;
   }

   public boolean getIsThornsDamage() {
      return this.isThornsDamage;
   }

   @Nullable
   public Entity getTrueSource() {
      return this.damageSourceEntity;
   }

   public ITextComponent getDeathMessage(EntityLivingBase p_151519_1_) {
      ItemStack itemstack = this.damageSourceEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.damageSourceEntity).getHeldItemMainhand() : ItemStack.EMPTY;
      String s = "death.attack." + this.damageType;
      return !itemstack.isEmpty() && itemstack.hasDisplayName() ? new TextComponentTranslation(s + ".item", p_151519_1_.getDisplayName(), this.damageSourceEntity.getDisplayName(), itemstack.getTextComponent()) : new TextComponentTranslation(s, p_151519_1_.getDisplayName(), this.damageSourceEntity.getDisplayName());
   }

   public boolean isDifficultyScaled() {
      return this.damageSourceEntity != null && this.damageSourceEntity instanceof EntityLivingBase && !(this.damageSourceEntity instanceof EntityPlayer);
   }

   @Nullable
   public Vec3d getDamageLocation() {
      return new Vec3d(this.damageSourceEntity.posX, this.damageSourceEntity.posY, this.damageSourceEntity.posZ);
   }
}
