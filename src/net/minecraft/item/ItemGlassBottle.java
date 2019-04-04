package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemGlassBottle extends Item {
   public ItemGlassBottle(Item.Properties p_i48523_1_) {
      super(p_i48523_1_);
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      List<EntityAreaEffectCloud> list = p_77659_1_.getEntitiesWithinAABB(EntityAreaEffectCloud.class, p_77659_2_.getEntityBoundingBox().grow(2.0D), (p_210311_0_) -> {
         return p_210311_0_ != null && p_210311_0_.isEntityAlive() && p_210311_0_.getOwner() instanceof EntityDragon;
      });
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      if (!list.isEmpty()) {
         EntityAreaEffectCloud entityareaeffectcloud = list.get(0);
         entityareaeffectcloud.setRadius(entityareaeffectcloud.getRadius() - 0.5F);
         p_77659_1_.playSound(null, p_77659_2_.posX, p_77659_2_.posY, p_77659_2_.posZ, SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
         return new ActionResult<>(EnumActionResult.SUCCESS, this.turnBottleIntoItem(itemstack, p_77659_2_, new ItemStack(Items.DRAGON_BREATH)));
      } else {
         RayTraceResult raytraceresult = this.rayTrace(p_77659_1_, p_77659_2_, true);
         if (raytraceresult == null) {
            return new ActionResult<>(EnumActionResult.PASS, itemstack);
         } else {
            if (raytraceresult.type == RayTraceResult.Type.BLOCK) {
               BlockPos blockpos = raytraceresult.getBlockPos();
               if (!p_77659_1_.isBlockModifiable(p_77659_2_, blockpos)) {
                  return new ActionResult<>(EnumActionResult.PASS, itemstack);
               }

               if (p_77659_1_.getFluidState(blockpos).isTagged(FluidTags.WATER)) {
                  p_77659_1_.playSound(p_77659_2_, p_77659_2_.posX, p_77659_2_.posY, p_77659_2_.posZ, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                  return new ActionResult<>(EnumActionResult.SUCCESS, this.turnBottleIntoItem(itemstack, p_77659_2_, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), PotionTypes.WATER)));
               }
            }

            return new ActionResult<>(EnumActionResult.PASS, itemstack);
         }
      }
   }

   protected ItemStack turnBottleIntoItem(ItemStack p_185061_1_, EntityPlayer p_185061_2_, ItemStack p_185061_3_) {
      p_185061_1_.shrink(1);
      p_185061_2_.func_71029_a(StatList.ITEM_USED.func_199076_b(this));
      if (p_185061_1_.isEmpty()) {
         return p_185061_3_;
      } else {
         if (!p_185061_2_.inventory.addItemStackToInventory(p_185061_3_)) {
            p_185061_2_.dropItem(p_185061_3_, false);
         }

         return p_185061_1_;
      }
   }
}
