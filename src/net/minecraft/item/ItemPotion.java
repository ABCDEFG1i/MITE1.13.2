package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemPotion extends Item {
   public ItemPotion(Item.Properties p_i48476_1_) {
      super(p_i48476_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getDefaultInstance() {
      return PotionUtils.addPotionToItemStack(super.getDefaultInstance(), PotionTypes.WATER);
   }

   public ItemStack onItemUseFinish(ItemStack p_77654_1_, World p_77654_2_, EntityLivingBase p_77654_3_) {
      EntityPlayer entityplayer = p_77654_3_ instanceof EntityPlayer ? (EntityPlayer)p_77654_3_ : null;
      if (entityplayer == null || !entityplayer.capabilities.isCreativeMode) {
         p_77654_1_.shrink(1);
      }

      if (entityplayer instanceof EntityPlayerMP) {
         CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)entityplayer, p_77654_1_);
      }

      if (!p_77654_2_.isRemote) {
         for(PotionEffect potioneffect : PotionUtils.getEffectsFromStack(p_77654_1_)) {
            if (potioneffect.getPotion().isInstant()) {
               potioneffect.getPotion().affectEntity(entityplayer, entityplayer, p_77654_3_, potioneffect.getAmplifier(), 1.0D);
            } else {
               p_77654_3_.addPotionEffect(new PotionEffect(potioneffect));
            }
         }
      }

      if (entityplayer != null) {
         entityplayer.func_71029_a(StatList.ITEM_USED.func_199076_b(this));
      }

      if (entityplayer == null || !entityplayer.capabilities.isCreativeMode) {
         if (p_77654_1_.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
         }

         if (entityplayer != null) {
            entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
         }
      }

      return p_77654_1_;
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

   public String getTranslationKey(ItemStack p_77667_1_) {
      return PotionUtils.getPotionFromItem(p_77667_1_).getNamePrefixed(this.getTranslationKey() + ".effect.");
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      PotionUtils.addPotionTooltip(p_77624_1_, p_77624_3_, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect(ItemStack p_77636_1_) {
      return super.hasEffect(p_77636_1_) || !PotionUtils.getEffectsFromStack(p_77636_1_).isEmpty();
   }

   public void fillItemGroup(ItemGroup p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
      if (this.isInGroup(p_150895_1_)) {
         for(PotionType potiontype : IRegistry.field_212621_j) {
            if (potiontype != PotionTypes.EMPTY) {
               p_150895_2_.add(PotionUtils.addPotionToItemStack(new ItemStack(this), potiontype));
            }
         }
      }

   }
}
