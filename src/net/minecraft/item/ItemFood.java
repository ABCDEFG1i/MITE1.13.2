package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemFood extends Item {
   private final int healAmount;
   private final float saturationModifier;
   private final boolean meat;
   private boolean alwaysEdible;
   private boolean fastEating;
   private PotionEffect potionId;
   private float potionEffectProbability;

   public ItemFood(int p_i48492_1_, float p_i48492_2_, boolean p_i48492_3_, Item.Properties p_i48492_4_) {
      super(p_i48492_4_);
      this.healAmount = p_i48492_1_;
      this.meat = p_i48492_3_;
      this.saturationModifier = p_i48492_2_;
   }

   public ItemStack onItemUseFinish(ItemStack p_77654_1_, World p_77654_2_, EntityLivingBase p_77654_3_) {
      if (p_77654_3_ instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)p_77654_3_;
         entityplayer.getFoodStats().addStats(this, p_77654_1_);
         p_77654_2_.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, p_77654_2_.rand.nextFloat() * 0.1F + 0.9F);
         this.onFoodEaten(p_77654_1_, p_77654_2_, entityplayer);
         entityplayer.func_71029_a(StatList.ITEM_USED.func_199076_b(this));
         if (entityplayer instanceof EntityPlayerMP) {
            CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)entityplayer, p_77654_1_);
         }
      }

      p_77654_1_.shrink(1);
      return p_77654_1_;
   }

   protected void onFoodEaten(ItemStack p_77849_1_, World p_77849_2_, EntityPlayer p_77849_3_) {
      if (!p_77849_2_.isRemote && this.potionId != null && p_77849_2_.rand.nextFloat() < this.potionEffectProbability) {
         p_77849_3_.addPotionEffect(new PotionEffect(this.potionId));
      }

   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return this.fastEating ? 16 : 32;
   }

   public EnumAction getUseAction(ItemStack p_77661_1_) {
      return EnumAction.EAT;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      if (p_77659_2_.canEat(this.alwaysEdible)) {
         p_77659_2_.setActiveHand(p_77659_3_);
         return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
      } else {
         return new ActionResult<>(EnumActionResult.FAIL, itemstack);
      }
   }

   public int getHealAmount(ItemStack p_150905_1_) {
      return this.healAmount;
   }

   public float getSaturationModifier(ItemStack p_150906_1_) {
      return this.saturationModifier;
   }

   public boolean isMeat() {
      return this.meat;
   }

   public ItemFood setPotionEffect(PotionEffect p_185070_1_, float p_185070_2_) {
      this.potionId = p_185070_1_;
      this.potionEffectProbability = p_185070_2_;
      return this;
   }

   public ItemFood setAlwaysEdible() {
      this.alwaysEdible = true;
      return this;
   }

   public ItemFood setFastEating() {
      this.fastEating = true;
      return this;
   }
}
