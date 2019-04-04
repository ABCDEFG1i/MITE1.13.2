package net.minecraft.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemFishingRod extends Item {
   public ItemFishingRod(Item.Properties p_i48494_1_) {
      super(p_i48494_1_);
      this.addPropertyOverride(new ResourceLocation("cast"), (p_210313_0_, p_210313_1_, p_210313_2_) -> {
         if (p_210313_2_ == null) {
            return 0.0F;
         } else {
            boolean flag = p_210313_2_.getHeldItemMainhand() == p_210313_0_;
            boolean flag1 = p_210313_2_.getHeldItemOffhand() == p_210313_0_;
            if (p_210313_2_.getHeldItemMainhand().getItem() instanceof ItemFishingRod) {
               flag1 = false;
            }

            return (flag || flag1) && p_210313_2_ instanceof EntityPlayer && ((EntityPlayer)p_210313_2_).fishEntity != null ? 1.0F : 0.0F;
         }
      });
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      if (p_77659_2_.fishEntity != null) {
         int i = p_77659_2_.fishEntity.handleHookRetraction(itemstack);
         itemstack.damageItem(i, p_77659_2_);
         p_77659_2_.swingArm(p_77659_3_);
         p_77659_1_.playSound(null, p_77659_2_.posX, p_77659_2_.posY, p_77659_2_.posZ, SoundEvents.ENTITY_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      } else {
         p_77659_1_.playSound(null, p_77659_2_.posX, p_77659_2_.posY, p_77659_2_.posZ, SoundEvents.ENTITY_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
         if (!p_77659_1_.isRemote) {
            EntityFishHook entityfishhook = new EntityFishHook(p_77659_1_, p_77659_2_);
            int j = EnchantmentHelper.getFishingSpeedBonus(itemstack);
            if (j > 0) {
               entityfishhook.setLureSpeed(j);
            }

            int k = EnchantmentHelper.getFishingLuckBonus(itemstack);
            if (k > 0) {
               entityfishhook.setLuck(k);
            }

            p_77659_1_.spawnEntity(entityfishhook);
         }

         p_77659_2_.swingArm(p_77659_3_);
         p_77659_2_.func_71029_a(StatList.ITEM_USED.func_199076_b(this));
      }

      return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
   }

   public int getItemEnchantability() {
      return 1;
   }
}
