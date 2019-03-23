package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemTippedArrow extends ItemArrow {
   public ItemTippedArrow(Item.Properties p_i48457_1_) {
      super(p_i48457_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getDefaultInstance() {
      return PotionUtils.addPotionToItemStack(super.getDefaultInstance(), PotionTypes.POISON);
   }

   public EntityArrow createArrow(World p_200887_1_, ItemStack p_200887_2_, EntityLivingBase p_200887_3_) {
      EntityTippedArrow entitytippedarrow = new EntityTippedArrow(p_200887_1_, p_200887_3_);
      entitytippedarrow.setPotionEffect(p_200887_2_);
      return entitytippedarrow;
   }

   public void fillItemGroup(ItemGroup p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
      if (this.isInGroup(p_150895_1_)) {
         for(PotionType potiontype : IRegistry.field_212621_j) {
            if (!potiontype.getEffects().isEmpty()) {
               p_150895_2_.add(PotionUtils.addPotionToItemStack(new ItemStack(this), potiontype));
            }
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      PotionUtils.addPotionTooltip(p_77624_1_, p_77624_3_, 0.125F);
   }

   public String getTranslationKey(ItemStack p_77667_1_) {
      return PotionUtils.getPotionFromItem(p_77667_1_).getNamePrefixed(this.getTranslationKey() + ".effect.");
   }
}
