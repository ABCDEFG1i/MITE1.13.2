package net.minecraft.enchantment;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAbstractSkull;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemTrident;

public enum EnumEnchantmentType {
   ALL {
      public boolean canEnchantItem(Item p_77557_1_) {
         for(EnumEnchantmentType enumenchantmenttype : EnumEnchantmentType.values()) {
            if (enumenchantmenttype != EnumEnchantmentType.ALL && enumenchantmenttype.canEnchantItem(p_77557_1_)) {
               return true;
            }
         }

         return false;
      }
   },
   ARMOR {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof ItemArmor;
      }
   },
   ARMOR_FEET {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof ItemArmor && ((ItemArmor)p_77557_1_).getEquipmentSlot() == EntityEquipmentSlot.FEET;
      }
   },
   ARMOR_LEGS {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof ItemArmor && ((ItemArmor)p_77557_1_).getEquipmentSlot() == EntityEquipmentSlot.LEGS;
      }
   },
   ARMOR_CHEST {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof ItemArmor && ((ItemArmor)p_77557_1_).getEquipmentSlot() == EntityEquipmentSlot.CHEST;
      }
   },
   ARMOR_HEAD {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof ItemArmor && ((ItemArmor)p_77557_1_).getEquipmentSlot() == EntityEquipmentSlot.HEAD;
      }
   },
   WEAPON {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof ItemSword;
      }
   },
   DIGGER {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof ItemTool;
      }
   },
   FISHING_ROD {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof ItemFishingRod;
      }
   },
   TRIDENT {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof ItemTrident;
      }
   },
   BREAKABLE {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_.isDamageable();
      }
   },
   BOW {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof ItemBow;
      }
   },
   WEARABLE {
      public boolean canEnchantItem(Item p_77557_1_) {
         Block block = Block.getBlockFromItem(p_77557_1_);
         return p_77557_1_ instanceof ItemArmor || p_77557_1_ instanceof ItemElytra || block instanceof BlockAbstractSkull || block instanceof BlockPumpkin;
      }
   };

   EnumEnchantmentType() {
   }

   public abstract boolean canEnchantItem(Item p_77557_1_);
}
