package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTrident;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemTrident extends Item {
   public ItemTrident(Item.Properties p_i48788_1_) {
      super(p_i48788_1_);
      this.addPropertyOverride(new ResourceLocation("throwing"), (p_210315_0_, p_210315_1_, p_210315_2_) -> {
         return p_210315_2_ != null && p_210315_2_.isHandActive() && p_210315_2_.getActiveItemStack() == p_210315_0_ ? 1.0F : 0.0F;
      });
   }

   public boolean canPlayerBreakBlockWhileHolding(IBlockState p_195938_1_, World p_195938_2_, BlockPos p_195938_3_, EntityPlayer p_195938_4_) {
      return !p_195938_4_.isCreative();
   }

   public EnumAction getUseAction(ItemStack p_77661_1_) {
      return EnumAction.SPEAR;
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return 72000;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect(ItemStack p_77636_1_) {
      return false;
   }

   public void onPlayerStoppedUsing(ItemStack p_77615_1_, World p_77615_2_, EntityLivingBase p_77615_3_, int p_77615_4_) {
      if (p_77615_3_ instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)p_77615_3_;
         int i = this.getUseDuration(p_77615_1_) - p_77615_4_;
         if (i >= 10) {
            int j = EnchantmentHelper.getRiptideModifier(p_77615_1_);
            if (j <= 0 || entityplayer.isWet()) {
               if (!p_77615_2_.isRemote) {
                  p_77615_1_.damageItem(1, entityplayer);
                  if (j == 0) {
                     EntityTrident entitytrident = new EntityTrident(p_77615_2_, entityplayer, p_77615_1_);
                     entitytrident.shoot(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, 2.5F + (float)j * 0.5F, 1.0F);
                     if (entityplayer.capabilities.isCreativeMode) {
                        entitytrident.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                     }

                     p_77615_2_.spawnEntity(entitytrident);
                     if (!entityplayer.capabilities.isCreativeMode) {
                        entityplayer.inventory.deleteStack(p_77615_1_);
                     }
                  }
               }

               entityplayer.func_71029_a(StatList.ITEM_USED.func_199076_b(this));
               SoundEvent soundevent = SoundEvents.ITEM_TRIDENT_THROW;
               if (j > 0) {
                  float f = entityplayer.rotationYaw;
                  float f1 = entityplayer.rotationPitch;
                  float f2 = -MathHelper.sin(f * ((float)Math.PI / 180F)) * MathHelper.cos(f1 * ((float)Math.PI / 180F));
                  float f3 = -MathHelper.sin(f1 * ((float)Math.PI / 180F));
                  float f4 = MathHelper.cos(f * ((float)Math.PI / 180F)) * MathHelper.cos(f1 * ((float)Math.PI / 180F));
                  float f5 = MathHelper.sqrt(f2 * f2 + f3 * f3 + f4 * f4);
                  float f6 = 3.0F * ((1.0F + (float)j) / 4.0F);
                  f2 = f2 * (f6 / f5);
                  f3 = f3 * (f6 / f5);
                  f4 = f4 * (f6 / f5);
                  entityplayer.addVelocity((double)f2, (double)f3, (double)f4);
                  if (j >= 3) {
                     soundevent = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
                  } else if (j == 2) {
                     soundevent = SoundEvents.ITEM_TRIDENT_RIPTIDE_2;
                  } else {
                     soundevent = SoundEvents.ITEM_TRIDENT_RIPTIDE_1;
                  }

                  entityplayer.startSpinAttack(20);
                  if (entityplayer.onGround) {
                     float f7 = 1.1999999F;
                     entityplayer.move(MoverType.SELF, 0.0D, (double)1.1999999F, 0.0D);
                  }
               }

               p_77615_2_.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, soundevent, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
         }
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      if (itemstack.getDamage() >= itemstack.getMaxDamage()) {
         return new ActionResult<>(EnumActionResult.FAIL, itemstack);
      } else if (EnchantmentHelper.getRiptideModifier(itemstack) > 0 && !p_77659_2_.isWet()) {
         return new ActionResult<>(EnumActionResult.FAIL, itemstack);
      } else {
         p_77659_2_.setActiveHand(p_77659_3_);
         return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
      }
   }

   public boolean hitEntity(ItemStack p_77644_1_, EntityLivingBase p_77644_2_, EntityLivingBase p_77644_3_) {
      p_77644_1_.damageItem(1, p_77644_3_);
      return true;
   }

   public boolean onBlockDestroyed(ItemStack p_179218_1_, World p_179218_2_, IBlockState p_179218_3_, BlockPos p_179218_4_, EntityLivingBase p_179218_5_) {
      if ((double)p_179218_3_.getBlockHardness(p_179218_2_, p_179218_4_) != 0.0D) {
         p_179218_1_.damageItem(2, p_179218_5_);
      }

      return true;
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot p_111205_1_) {
      Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(p_111205_1_);
      if (p_111205_1_ == EntityEquipmentSlot.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 8.0D, 0));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double)-2.9F, 0));
      }

      return multimap;
   }

   public int getItemEnchantability() {
      return 1;
   }
}
