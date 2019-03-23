package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
   public static int getEnchantmentLevel(Enchantment p_77506_0_, ItemStack p_77506_1_) {
      if (p_77506_1_.isEmpty()) {
         return 0;
      } else {
         ResourceLocation resourcelocation = IRegistry.field_212628_q.func_177774_c(p_77506_0_);
         NBTTagList nbttaglist = p_77506_1_.getEnchantmentTagList();

         for(int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            ResourceLocation resourcelocation1 = ResourceLocation.makeResourceLocation(nbttagcompound.getString("id"));
            if (resourcelocation1 != null && resourcelocation1.equals(resourcelocation)) {
               return nbttagcompound.getInteger("lvl");
            }
         }

         return 0;
      }
   }

   public static Map<Enchantment, Integer> getEnchantments(ItemStack p_82781_0_) {
      Map<Enchantment, Integer> map = Maps.newLinkedHashMap();
      NBTTagList nbttaglist = p_82781_0_.getItem() == Items.ENCHANTED_BOOK ? ItemEnchantedBook.getEnchantments(p_82781_0_) : p_82781_0_.getEnchantmentTagList();

      for(int i = 0; i < nbttaglist.size(); ++i) {
         NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
         Enchantment enchantment = IRegistry.field_212628_q.func_212608_b(ResourceLocation.makeResourceLocation(nbttagcompound.getString("id")));
         if (enchantment != null) {
            map.put(enchantment, nbttagcompound.getInteger("lvl"));
         }
      }

      return map;
   }

   public static void setEnchantments(Map<Enchantment, Integer> p_82782_0_, ItemStack p_82782_1_) {
      NBTTagList nbttaglist = new NBTTagList();

      for(Entry<Enchantment, Integer> entry : p_82782_0_.entrySet()) {
         Enchantment enchantment = entry.getKey();
         if (enchantment != null) {
            int i = entry.getValue();
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setString("id", String.valueOf((Object)IRegistry.field_212628_q.func_177774_c(enchantment)));
            nbttagcompound.setShort("lvl", (short)i);
            nbttaglist.add((INBTBase)nbttagcompound);
            if (p_82782_1_.getItem() == Items.ENCHANTED_BOOK) {
               ItemEnchantedBook.addEnchantment(p_82782_1_, new EnchantmentData(enchantment, i));
            }
         }
      }

      if (nbttaglist.isEmpty()) {
         p_82782_1_.removeChildTag("Enchantments");
      } else if (p_82782_1_.getItem() != Items.ENCHANTED_BOOK) {
         p_82782_1_.setTagInfo("Enchantments", nbttaglist);
      }

   }

   private static void func_77518_a(EnchantmentHelper.IEnchantmentVisitor p_77518_0_, ItemStack p_77518_1_) {
      if (!p_77518_1_.isEmpty()) {
         NBTTagList nbttaglist = p_77518_1_.getEnchantmentTagList();

         for(int i = 0; i < nbttaglist.size(); ++i) {
            String s = nbttaglist.getCompoundTagAt(i).getString("id");
            int j = nbttaglist.getCompoundTagAt(i).getInteger("lvl");
            Enchantment enchantment = IRegistry.field_212628_q.func_212608_b(ResourceLocation.makeResourceLocation(s));
            if (enchantment != null) {
               p_77518_0_.accept(enchantment, j);
            }
         }

      }
   }

   private static void func_77516_a(EnchantmentHelper.IEnchantmentVisitor p_77516_0_, Iterable<ItemStack> p_77516_1_) {
      for(ItemStack itemstack : p_77516_1_) {
         func_77518_a(p_77516_0_, itemstack);
      }

   }

   public static int getEnchantmentModifierDamage(Iterable<ItemStack> p_77508_0_, DamageSource p_77508_1_) {
      MutableInt mutableint = new MutableInt();
      func_77516_a((p_212576_2_, p_212576_3_) -> {
         mutableint.add(p_212576_2_.calcModifierDamage(p_212576_3_, p_77508_1_));
      }, p_77508_0_);
      return mutableint.intValue();
   }

   public static float getModifierForCreature(ItemStack p_152377_0_, CreatureAttribute p_152377_1_) {
      MutableFloat mutablefloat = new MutableFloat();
      func_77518_a((p_212573_2_, p_212573_3_) -> {
         mutablefloat.add(p_212573_2_.calcDamageByCreature(p_212573_3_, p_152377_1_));
      }, p_152377_0_);
      return mutablefloat.floatValue();
   }

   public static float getSweepingDamageRatio(EntityLivingBase p_191527_0_) {
      int i = getMaxEnchantmentLevel(Enchantments.SWEEPING, p_191527_0_);
      return i > 0 ? EnchantmentSweepingEdge.getSweepingDamageRatio(i) : 0.0F;
   }

   public static void applyThornEnchantments(EntityLivingBase p_151384_0_, Entity p_151384_1_) {
      EnchantmentHelper.IEnchantmentVisitor enchantmenthelper$ienchantmentvisitor = (p_212575_2_, p_212575_3_) -> {
         p_212575_2_.onUserHurt(p_151384_0_, p_151384_1_, p_212575_3_);
      };
      if (p_151384_0_ != null) {
         func_77516_a(enchantmenthelper$ienchantmentvisitor, p_151384_0_.getEquipmentAndArmor());
      }

      if (p_151384_1_ instanceof EntityPlayer) {
         func_77518_a(enchantmenthelper$ienchantmentvisitor, p_151384_0_.getHeldItemMainhand());
      }

   }

   public static void applyArthropodEnchantments(EntityLivingBase p_151385_0_, Entity p_151385_1_) {
      EnchantmentHelper.IEnchantmentVisitor enchantmenthelper$ienchantmentvisitor = (p_212574_2_, p_212574_3_) -> {
         p_212574_2_.onEntityDamaged(p_151385_0_, p_151385_1_, p_212574_3_);
      };
      if (p_151385_0_ != null) {
         func_77516_a(enchantmenthelper$ienchantmentvisitor, p_151385_0_.getEquipmentAndArmor());
      }

      if (p_151385_0_ instanceof EntityPlayer) {
         func_77518_a(enchantmenthelper$ienchantmentvisitor, p_151385_0_.getHeldItemMainhand());
      }

   }

   public static int getMaxEnchantmentLevel(Enchantment p_185284_0_, EntityLivingBase p_185284_1_) {
      Iterable<ItemStack> iterable = p_185284_0_.getEntityEquipment(p_185284_1_);
      if (iterable == null) {
         return 0;
      } else {
         int i = 0;

         for(ItemStack itemstack : iterable) {
            int j = getEnchantmentLevel(p_185284_0_, itemstack);
            if (j > i) {
               i = j;
            }
         }

         return i;
      }
   }

   public static int getKnockbackModifier(EntityLivingBase p_77501_0_) {
      return getMaxEnchantmentLevel(Enchantments.KNOCKBACK, p_77501_0_);
   }

   public static int getFireAspectModifier(EntityLivingBase p_90036_0_) {
      return getMaxEnchantmentLevel(Enchantments.FIRE_ASPECT, p_90036_0_);
   }

   public static int getRespirationModifier(EntityLivingBase p_185292_0_) {
      return getMaxEnchantmentLevel(Enchantments.RESPIRATION, p_185292_0_);
   }

   public static int getDepthStriderModifier(EntityLivingBase p_185294_0_) {
      return getMaxEnchantmentLevel(Enchantments.DEPTH_STRIDER, p_185294_0_);
   }

   public static int getEfficiencyModifier(EntityLivingBase p_185293_0_) {
      return getMaxEnchantmentLevel(Enchantments.EFFICIENCY, p_185293_0_);
   }

   public static int getFishingLuckBonus(ItemStack p_191529_0_) {
      return getEnchantmentLevel(Enchantments.LUCK_OF_THE_SEA, p_191529_0_);
   }

   public static int getFishingSpeedBonus(ItemStack p_191528_0_) {
      return getEnchantmentLevel(Enchantments.LURE, p_191528_0_);
   }

   public static int getLootingModifier(EntityLivingBase p_185283_0_) {
      return getMaxEnchantmentLevel(Enchantments.LOOTING, p_185283_0_);
   }

   public static boolean getAquaAffinityModifier(EntityLivingBase p_185287_0_) {
      return getMaxEnchantmentLevel(Enchantments.AQUA_AFFINITY, p_185287_0_) > 0;
   }

   public static boolean hasFrostWalker(EntityLivingBase p_189869_0_) {
      return getMaxEnchantmentLevel(Enchantments.FROST_WALKER, p_189869_0_) > 0;
   }

   public static boolean hasBindingCurse(ItemStack p_190938_0_) {
      return getEnchantmentLevel(Enchantments.BINDING_CURSE, p_190938_0_) > 0;
   }

   public static boolean hasVanishingCurse(ItemStack p_190939_0_) {
      return getEnchantmentLevel(Enchantments.VANISHING_CURSE, p_190939_0_) > 0;
   }

   public static int getLoyaltyModifier(ItemStack p_203191_0_) {
      return getEnchantmentLevel(Enchantments.LOYALTY, p_203191_0_);
   }

   public static int getRiptideModifier(ItemStack p_203190_0_) {
      return getEnchantmentLevel(Enchantments.RIPTIDE, p_203190_0_);
   }

   public static boolean hasChanneling(ItemStack p_203192_0_) {
      return getEnchantmentLevel(Enchantments.CHANNELING, p_203192_0_) > 0;
   }

   public static ItemStack getEnchantedItem(Enchantment p_92099_0_, EntityLivingBase p_92099_1_) {
      List<ItemStack> list = p_92099_0_.getEntityEquipment(p_92099_1_);
      if (list.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         List<ItemStack> list1 = Lists.newArrayList();

         for(ItemStack itemstack : list) {
            if (!itemstack.isEmpty() && getEnchantmentLevel(p_92099_0_, itemstack) > 0) {
               list1.add(itemstack);
            }
         }

         return list1.isEmpty() ? ItemStack.EMPTY : list1.get(p_92099_1_.getRNG().nextInt(list1.size()));
      }
   }

   public static int calcItemStackEnchantability(Random p_77514_0_, int p_77514_1_, int p_77514_2_, ItemStack p_77514_3_) {
      Item item = p_77514_3_.getItem();
      int i = item.getItemEnchantability();
      if (i <= 0) {
         return 0;
      } else {
         if (p_77514_2_ > 15) {
            p_77514_2_ = 15;
         }

         int j = p_77514_0_.nextInt(8) + 1 + (p_77514_2_ >> 1) + p_77514_0_.nextInt(p_77514_2_ + 1);
         if (p_77514_1_ == 0) {
            return Math.max(j / 3, 1);
         } else {
            return p_77514_1_ == 1 ? j * 2 / 3 + 1 : Math.max(j, p_77514_2_ * 2);
         }
      }
   }

   public static ItemStack addRandomEnchantment(Random p_77504_0_, ItemStack p_77504_1_, int p_77504_2_, boolean p_77504_3_) {
      List<EnchantmentData> list = buildEnchantmentList(p_77504_0_, p_77504_1_, p_77504_2_, p_77504_3_);
      boolean flag = p_77504_1_.getItem() == Items.BOOK;
      if (flag) {
         p_77504_1_ = new ItemStack(Items.ENCHANTED_BOOK);
      }

      for(EnchantmentData enchantmentdata : list) {
         if (flag) {
            ItemEnchantedBook.addEnchantment(p_77504_1_, enchantmentdata);
         } else {
            p_77504_1_.addEnchantment(enchantmentdata.enchantment, enchantmentdata.enchantmentLevel);
         }
      }

      return p_77504_1_;
   }

   public static List<EnchantmentData> buildEnchantmentList(Random p_77513_0_, ItemStack p_77513_1_, int p_77513_2_, boolean p_77513_3_) {
      List<EnchantmentData> list = Lists.newArrayList();
      Item item = p_77513_1_.getItem();
      int i = item.getItemEnchantability();
      if (i <= 0) {
         return list;
      } else {
         p_77513_2_ = p_77513_2_ + 1 + p_77513_0_.nextInt(i / 4 + 1) + p_77513_0_.nextInt(i / 4 + 1);
         float f = (p_77513_0_.nextFloat() + p_77513_0_.nextFloat() - 1.0F) * 0.15F;
         p_77513_2_ = MathHelper.clamp(Math.round((float)p_77513_2_ + (float)p_77513_2_ * f), 1, Integer.MAX_VALUE);
         List<EnchantmentData> list1 = getEnchantmentDatas(p_77513_2_, p_77513_1_, p_77513_3_);
         if (!list1.isEmpty()) {
            list.add(WeightedRandom.getRandomItem(p_77513_0_, list1));

            while(p_77513_0_.nextInt(50) <= p_77513_2_) {
               removeIncompatible(list1, Util.getLastElement(list));
               if (list1.isEmpty()) {
                  break;
               }

               list.add(WeightedRandom.getRandomItem(p_77513_0_, list1));
               p_77513_2_ /= 2;
            }
         }

         return list;
      }
   }

   public static void removeIncompatible(List<EnchantmentData> p_185282_0_, EnchantmentData p_185282_1_) {
      Iterator<EnchantmentData> iterator = p_185282_0_.iterator();

      while(iterator.hasNext()) {
         if (!p_185282_1_.enchantment.isCompatibleWith((iterator.next()).enchantment)) {
            iterator.remove();
         }
      }

   }

   public static boolean areAllCompatibleWith(Collection<Enchantment> p_201840_0_, Enchantment p_201840_1_) {
      for(Enchantment enchantment : p_201840_0_) {
         if (!enchantment.isCompatibleWith(p_201840_1_)) {
            return false;
         }
      }

      return true;
   }

   public static List<EnchantmentData> getEnchantmentDatas(int p_185291_0_, ItemStack p_185291_1_, boolean p_185291_2_) {
      List<EnchantmentData> list = Lists.newArrayList();
      Item item = p_185291_1_.getItem();
      boolean flag = p_185291_1_.getItem() == Items.BOOK;

      for(Enchantment enchantment : IRegistry.field_212628_q) {
         if ((!enchantment.isTreasureEnchantment() || p_185291_2_) && (enchantment.type.canEnchantItem(item) || flag)) {
            for(int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
               if (p_185291_0_ >= enchantment.getMinEnchantability(i) && p_185291_0_ <= enchantment.getMaxEnchantability(i)) {
                  list.add(new EnchantmentData(enchantment, i));
                  break;
               }
            }
         }
      }

      return list;
   }

   @FunctionalInterface
   interface IEnchantmentVisitor {
      void accept(Enchantment p_accept_1_, int p_accept_2_);
   }
}
