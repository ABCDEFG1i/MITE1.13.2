package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum HorseArmorType {
   NONE(0),
   IRON(5, "iron", "meo"),
   GOLD(7, "gold", "goo"),
   DIAMOND(11, "diamond", "dio");

   private final String textureName;
   private final String hash;
   private final int protection;

   HorseArmorType(int p_i46799_3_) {
      this.protection = p_i46799_3_;
      this.textureName = null;
      this.hash = "";
   }

   HorseArmorType(int p_i46800_3_, String p_i46800_4_, String p_i46800_5_) {
      this.protection = p_i46800_3_;
      this.textureName = "textures/entity/horse/armor/horse_armor_" + p_i46800_4_ + ".png";
      this.hash = p_i46800_5_;
   }

   public int getOrdinal() {
      return this.ordinal();
   }

   @OnlyIn(Dist.CLIENT)
   public String getHash() {
      return this.hash;
   }

   public int getProtection() {
      return this.protection;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public String getTextureName() {
      return this.textureName;
   }

   public static HorseArmorType getByOrdinal(int p_188575_0_) {
      return values()[p_188575_0_];
   }

   public static HorseArmorType getByItemStack(ItemStack p_188580_0_) {
      return p_188580_0_.isEmpty() ? NONE : getByItem(p_188580_0_.getItem());
   }

   public static HorseArmorType getByItem(Item p_188576_0_) {
      if (p_188576_0_ == Items.IRON_HORSE_ARMOR) {
         return IRON;
      } else if (p_188576_0_ == Items.GOLDEN_HORSE_ARMOR) {
         return GOLD;
      } else {
         return p_188576_0_ == Items.DIAMOND_HORSE_ARMOR ? DIAMOND : NONE;
      }
   }

   public static boolean isHorseArmor(Item p_188577_0_) {
      return getByItem(p_188577_0_) != NONE;
   }
}
