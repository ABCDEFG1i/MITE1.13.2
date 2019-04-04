package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemSplashPotion extends ItemPotion {
   public ItemSplashPotion(Item.Properties p_i48463_1_) {
      super(p_i48463_1_);
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      ItemStack itemstack1 = p_77659_2_.capabilities.isCreativeMode ? itemstack.copy() : itemstack.split(1);
      p_77659_1_.playSound(null, p_77659_2_.posX, p_77659_2_.posY, p_77659_2_.posZ, SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      if (!p_77659_1_.isRemote) {
         EntityPotion entitypotion = new EntityPotion(p_77659_1_, p_77659_2_, itemstack1);
         entitypotion.shoot(p_77659_2_, p_77659_2_.rotationPitch, p_77659_2_.rotationYaw, -20.0F, 0.5F, 1.0F);
         p_77659_1_.spawnEntity(entitypotion);
      }

      p_77659_2_.func_71029_a(StatList.ITEM_USED.func_199076_b(this));
      return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
   }
}
