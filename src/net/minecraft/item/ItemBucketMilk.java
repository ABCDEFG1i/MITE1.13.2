package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemBucketMilk extends Item {
   public ItemBucketMilk(Item.Properties p_i48481_1_) {
      super(p_i48481_1_);
   }

   public ItemStack onItemUseFinish(ItemStack p_77654_1_, World p_77654_2_, EntityLivingBase p_77654_3_) {
      if (p_77654_3_ instanceof EntityPlayerMP) {
         EntityPlayerMP entityplayermp = (EntityPlayerMP)p_77654_3_;
         CriteriaTriggers.CONSUME_ITEM.trigger(entityplayermp, p_77654_1_);
         entityplayermp.func_71029_a(StatList.ITEM_USED.func_199076_b(this));
      }

      if (p_77654_3_ instanceof EntityPlayer && !((EntityPlayer)p_77654_3_).capabilities.isCreativeMode) {
         p_77654_1_.shrink(1);
      }

      if (!p_77654_2_.isRemote) {
         p_77654_3_.func_195061_cb();
      }

      return p_77654_1_.isEmpty() ? new ItemStack(Items.BUCKET) : p_77654_1_;
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return 32;
   }

   public EnumAction getUseAction(ItemStack p_77661_1_) {
      return EnumAction.DRINK;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      p_77659_2_.setActiveHand(p_77659_3_);
      return new ActionResult<>(EnumActionResult.SUCCESS, p_77659_2_.getHeldItem(p_77659_3_));
   }
}
