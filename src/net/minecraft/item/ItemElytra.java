package net.minecraft.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemElytra extends Item {
   public ItemElytra(Item.Properties p_i48507_1_) {
      super(p_i48507_1_);
      this.addPropertyOverride(new ResourceLocation("broken"), (p_210312_0_, p_210312_1_, p_210312_2_) -> {
         return isUsable(p_210312_0_) ? 0.0F : 1.0F;
      });
      BlockDispenser.registerDispenseBehavior(this, ItemArmor.DISPENSER_BEHAVIOR);
   }

   public static boolean isUsable(ItemStack p_185069_0_) {
      return p_185069_0_.getDamage() < p_185069_0_.getMaxDamage() - 1;
   }

   public boolean getIsRepairable(ItemStack p_82789_1_, ItemStack p_82789_2_) {
      return p_82789_2_.getItem() == Items.PHANTOM_MEMBRANE;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
      ItemStack itemstack1 = p_77659_2_.getItemStackFromSlot(entityequipmentslot);
      if (itemstack1.isEmpty()) {
         p_77659_2_.setItemStackToSlot(entityequipmentslot, itemstack.copy());
         itemstack.setCount(0);
         return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
      } else {
         return new ActionResult<>(EnumActionResult.FAIL, itemstack);
      }
   }
}
