package net.minecraft.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ItemChorusFruit extends ItemFood {
   public ItemChorusFruit(int p_i48518_1_, float p_i48518_2_, Item.Properties p_i48518_3_) {
      super(p_i48518_1_, p_i48518_2_, false, p_i48518_3_);
   }

   public ItemStack onItemUseFinish(ItemStack p_77654_1_, World p_77654_2_, EntityLivingBase p_77654_3_) {
      ItemStack itemstack = super.onItemUseFinish(p_77654_1_, p_77654_2_, p_77654_3_);
      if (!p_77654_2_.isRemote) {
         double d0 = p_77654_3_.posX;
         double d1 = p_77654_3_.posY;
         double d2 = p_77654_3_.posZ;

         for(int i = 0; i < 16; ++i) {
            double d3 = p_77654_3_.posX + (p_77654_3_.getRNG().nextDouble() - 0.5D) * 16.0D;
            double d4 = MathHelper.clamp(p_77654_3_.posY + (double)(p_77654_3_.getRNG().nextInt(16) - 8), 0.0D, (double)(p_77654_2_.getActualHeight() - 1));
            double d5 = p_77654_3_.posZ + (p_77654_3_.getRNG().nextDouble() - 0.5D) * 16.0D;
            if (p_77654_3_.isRiding()) {
               p_77654_3_.dismountRidingEntity();
            }

            if (p_77654_3_.attemptTeleport(d3, d4, d5)) {
               p_77654_2_.playSound((EntityPlayer)null, d0, d1, d2, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
               p_77654_3_.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
               break;
            }
         }

         if (p_77654_3_ instanceof EntityPlayer) {
            ((EntityPlayer)p_77654_3_).getCooldownTracker().setCooldown(this, 20);
         }
      }

      return itemstack;
   }
}
