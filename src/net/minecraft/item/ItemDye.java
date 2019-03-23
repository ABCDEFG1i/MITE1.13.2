package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public class ItemDye extends Item {
   private static final Map<EnumDyeColor, ItemDye> COLOR_DYE_ITEM_MAP = Maps.newEnumMap(EnumDyeColor.class);
   private final EnumDyeColor dyeColor;

   public ItemDye(EnumDyeColor p_i48510_1_, Item.Properties p_i48510_2_) {
      super(p_i48510_2_);
      this.dyeColor = p_i48510_1_;
      COLOR_DYE_ITEM_MAP.put(p_i48510_1_, this);
   }

   public boolean itemInteractionForEntity(ItemStack p_111207_1_, EntityPlayer p_111207_2_, EntityLivingBase p_111207_3_, EnumHand p_111207_4_) {
      if (p_111207_3_ instanceof EntitySheep) {
         EntitySheep entitysheep = (EntitySheep)p_111207_3_;
         if (!entitysheep.getSheared() && entitysheep.getFleeceColor() != this.dyeColor) {
            entitysheep.setFleeceColor(this.dyeColor);
            p_111207_1_.shrink(1);
         }

         return true;
      } else {
         return false;
      }
   }

   public EnumDyeColor getDyeColor() {
      return this.dyeColor;
   }

   public static ItemDye getItem(EnumDyeColor p_195961_0_) {
      return COLOR_DYE_ITEM_MAP.get(p_195961_0_);
   }
}
