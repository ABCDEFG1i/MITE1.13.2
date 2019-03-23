package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemEnchantedBook extends Item {
   public ItemEnchantedBook(Item.Properties p_i48505_1_) {
      super(p_i48505_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect(ItemStack p_77636_1_) {
      return true;
   }

   public boolean isEnchantable(ItemStack p_77616_1_) {
      return false;
   }

   public static NBTTagList getEnchantments(ItemStack p_92110_0_) {
      NBTTagCompound nbttagcompound = p_92110_0_.getTag();
      return nbttagcompound != null ? nbttagcompound.getTagList("StoredEnchantments", 10) : new NBTTagList();
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      super.addInformation(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
      NBTTagList nbttaglist = getEnchantments(p_77624_1_);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
         Enchantment enchantment = IRegistry.field_212628_q.func_212608_b(ResourceLocation.makeResourceLocation(nbttagcompound.getString("id")));
         if (enchantment != null) {
            p_77624_3_.add(enchantment.func_200305_d(nbttagcompound.getInteger("lvl")));
         }
      }

   }

   public static void addEnchantment(ItemStack p_92115_0_, EnchantmentData p_92115_1_) {
      NBTTagList nbttaglist = getEnchantments(p_92115_0_);
      boolean flag = true;
      ResourceLocation resourcelocation = IRegistry.field_212628_q.func_177774_c(p_92115_1_.enchantment);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
         ResourceLocation resourcelocation1 = ResourceLocation.makeResourceLocation(nbttagcompound.getString("id"));
         if (resourcelocation1 != null && resourcelocation1.equals(resourcelocation)) {
            if (nbttagcompound.getInteger("lvl") < p_92115_1_.enchantmentLevel) {
               nbttagcompound.setShort("lvl", (short)p_92115_1_.enchantmentLevel);
            }

            flag = false;
            break;
         }
      }

      if (flag) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         nbttagcompound1.setString("id", String.valueOf((Object)resourcelocation));
         nbttagcompound1.setShort("lvl", (short)p_92115_1_.enchantmentLevel);
         nbttaglist.add((INBTBase)nbttagcompound1);
      }

      p_92115_0_.getOrCreateTag().setTag("StoredEnchantments", nbttaglist);
   }

   public static ItemStack getEnchantedItemStack(EnchantmentData p_92111_0_) {
      ItemStack itemstack = new ItemStack(Items.ENCHANTED_BOOK);
      addEnchantment(itemstack, p_92111_0_);
      return itemstack;
   }

   public void fillItemGroup(ItemGroup p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
      if (p_150895_1_ == ItemGroup.SEARCH) {
         for(Enchantment enchantment : IRegistry.field_212628_q) {
            if (enchantment.type != null) {
               for(int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); ++i) {
                  p_150895_2_.add(getEnchantedItemStack(new EnchantmentData(enchantment, i)));
               }
            }
         }
      } else if (p_150895_1_.getRelevantEnchantmentTypes().length != 0) {
         for(Enchantment enchantment1 : IRegistry.field_212628_q) {
            if (p_150895_1_.hasRelevantEnchantmentType(enchantment1.type)) {
               p_150895_2_.add(getEnchantedItemStack(new EnchantmentData(enchantment1, enchantment1.getMaxLevel())));
            }
         }
      }

   }
}
