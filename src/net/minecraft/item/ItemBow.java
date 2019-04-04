package net.minecraft.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemBow extends Item {
   public ItemBow(Item.Properties p_i48522_1_) {
      super(p_i48522_1_);
      this.addPropertyOverride(new ResourceLocation("pull"), (p_210310_0_, p_210310_1_, p_210310_2_) -> {
         if (p_210310_2_ == null) {
            return 0.0F;
         } else {
            return p_210310_2_.getActiveItemStack().getItem() != Items.BOW ? 0.0F : (float)(p_210310_0_.getUseDuration() - p_210310_2_.getItemInUseCount()) / 20.0F;
         }
      });
      this.addPropertyOverride(new ResourceLocation("pulling"), (p_210309_0_, p_210309_1_, p_210309_2_) -> {
         return p_210309_2_ != null && p_210309_2_.isHandActive() && p_210309_2_.getActiveItemStack() == p_210309_0_ ? 1.0F : 0.0F;
      });
   }

   private ItemStack findAmmo(EntityPlayer p_185060_1_) {
      if (this.isArrow(p_185060_1_.getHeldItem(EnumHand.OFF_HAND))) {
         return p_185060_1_.getHeldItem(EnumHand.OFF_HAND);
      } else if (this.isArrow(p_185060_1_.getHeldItem(EnumHand.MAIN_HAND))) {
         return p_185060_1_.getHeldItem(EnumHand.MAIN_HAND);
      } else {
         for(int i = 0; i < p_185060_1_.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = p_185060_1_.inventory.getStackInSlot(i);
            if (this.isArrow(itemstack)) {
               return itemstack;
            }
         }

         return ItemStack.EMPTY;
      }
   }

   protected boolean isArrow(ItemStack p_185058_1_) {
      return p_185058_1_.getItem() instanceof ItemArrow;
   }

   public void onPlayerStoppedUsing(ItemStack p_77615_1_, World p_77615_2_, EntityLivingBase p_77615_3_, int p_77615_4_) {
      if (p_77615_3_ instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)p_77615_3_;
         boolean flag = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, p_77615_1_) > 0;
         ItemStack itemstack = this.findAmmo(entityplayer);
         if (!itemstack.isEmpty() || flag) {
            if (itemstack.isEmpty()) {
               itemstack = new ItemStack(Items.ARROW);
            }

            int i = this.getUseDuration(p_77615_1_) - p_77615_4_;
            float f = getArrowVelocity(i);
            if (!((double)f < 0.1D)) {
               boolean flag1 = flag && itemstack.getItem() == Items.ARROW;
               if (!p_77615_2_.isRemote) {
                  ItemArrow itemarrow = (ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
                  EntityArrow entityarrow = itemarrow.createArrow(p_77615_2_, itemstack, entityplayer);
                  entityarrow.shoot(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, f * 3.0F, 1.0F);
                  if (f == 1.0F) {
                     entityarrow.setIsCritical(true);
                  }

                  int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, p_77615_1_);
                  if (j > 0) {
                     entityarrow.setDamage(entityarrow.getDamage() + (double)j * 0.5D + 0.5D);
                  }

                  int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, p_77615_1_);
                  if (k > 0) {
                     entityarrow.setKnockbackStrength(k);
                  }

                  if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, p_77615_1_) > 0) {
                     entityarrow.setFire(100);
                  }

                  p_77615_1_.damageItem(1, entityplayer);
                  if (flag1 || entityplayer.capabilities.isCreativeMode && (itemstack.getItem() == Items.SPECTRAL_ARROW || itemstack.getItem() == Items.TIPPED_ARROW)) {
                     entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                  }

                  p_77615_2_.spawnEntity(entityarrow);
               }

               p_77615_2_.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
               if (!flag1 && !entityplayer.capabilities.isCreativeMode) {
                  itemstack.shrink(1);
                  if (itemstack.isEmpty()) {
                     entityplayer.inventory.deleteStack(itemstack);
                  }
               }

               entityplayer.func_71029_a(StatList.ITEM_USED.func_199076_b(this));
            }
         }
      }
   }

   public static float getArrowVelocity(int p_185059_0_) {
      float f = (float)p_185059_0_ / 20.0F;
      f = (f * f + f * 2.0F) / 3.0F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      return f;
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return 72000;
   }

   public EnumAction getUseAction(ItemStack p_77661_1_) {
      return EnumAction.BOW;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      boolean flag = !this.findAmmo(p_77659_2_).isEmpty();
      if (!p_77659_2_.capabilities.isCreativeMode && !flag) {
         return flag ? new ActionResult<>(EnumActionResult.PASS, itemstack) : new ActionResult<>(EnumActionResult.FAIL, itemstack);
      } else {
         p_77659_2_.setActiveHand(p_77659_3_);
         return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
      }
   }

   public int getItemEnchantability() {
      return 1;
   }
}
