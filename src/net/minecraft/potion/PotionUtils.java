package net.minecraft.potion;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PotionUtils {
   public static List<PotionEffect> getEffectsFromStack(ItemStack p_185189_0_) {
      return getEffectsFromTag(p_185189_0_.getTag());
   }

   public static List<PotionEffect> mergeEffects(PotionType p_185186_0_, Collection<PotionEffect> p_185186_1_) {
      List<PotionEffect> list = Lists.newArrayList();
      list.addAll(p_185186_0_.getEffects());
      list.addAll(p_185186_1_);
      return list;
   }

   public static List<PotionEffect> getEffectsFromTag(@Nullable NBTTagCompound p_185185_0_) {
      List<PotionEffect> list = Lists.newArrayList();
      list.addAll(getPotionTypeFromNBT(p_185185_0_).getEffects());
      addCustomPotionEffectToList(p_185185_0_, list);
      return list;
   }

   public static List<PotionEffect> getFullEffectsFromItem(ItemStack p_185190_0_) {
      return getFullEffectsFromTag(p_185190_0_.getTag());
   }

   public static List<PotionEffect> getFullEffectsFromTag(@Nullable NBTTagCompound p_185192_0_) {
      List<PotionEffect> list = Lists.newArrayList();
      addCustomPotionEffectToList(p_185192_0_, list);
      return list;
   }

   public static void addCustomPotionEffectToList(@Nullable NBTTagCompound p_185193_0_, List<PotionEffect> p_185193_1_) {
      if (p_185193_0_ != null && p_185193_0_.hasKey("CustomPotionEffects", 9)) {
         NBTTagList nbttaglist = p_185193_0_.getTagList("CustomPotionEffects", 10);

         for(int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            PotionEffect potioneffect = PotionEffect.read(nbttagcompound);
            if (potioneffect != null) {
               p_185193_1_.add(potioneffect);
            }
         }
      }

   }

   public static int getColor(ItemStack p_190932_0_) {
      NBTTagCompound nbttagcompound = p_190932_0_.getTag();
      if (nbttagcompound != null && nbttagcompound.hasKey("CustomPotionColor", 99)) {
         return nbttagcompound.getInteger("CustomPotionColor");
      } else {
         return getPotionFromItem(p_190932_0_) == PotionTypes.EMPTY ? 16253176 : getPotionColorFromEffectList(getEffectsFromStack(p_190932_0_));
      }
   }

   public static int getPotionColor(PotionType p_185183_0_) {
      return p_185183_0_ == PotionTypes.EMPTY ? 16253176 : getPotionColorFromEffectList(p_185183_0_.getEffects());
   }

   public static int getPotionColorFromEffectList(Collection<PotionEffect> p_185181_0_) {
      int i = 3694022;
      if (p_185181_0_.isEmpty()) {
         return 3694022;
      } else {
         float f = 0.0F;
         float f1 = 0.0F;
         float f2 = 0.0F;
         int j = 0;

         for(PotionEffect potioneffect : p_185181_0_) {
            if (potioneffect.doesShowParticles()) {
               int k = potioneffect.getPotion().getLiquidColor();
               int l = potioneffect.getAmplifier() + 1;
               f += (float)(l * (k >> 16 & 255)) / 255.0F;
               f1 += (float)(l * (k >> 8 & 255)) / 255.0F;
               f2 += (float)(l * (k >> 0 & 255)) / 255.0F;
               j += l;
            }
         }

         if (j == 0) {
            return 0;
         } else {
            f = f / (float)j * 255.0F;
            f1 = f1 / (float)j * 255.0F;
            f2 = f2 / (float)j * 255.0F;
            return (int)f << 16 | (int)f1 << 8 | (int)f2;
         }
      }
   }

   public static PotionType getPotionFromItem(ItemStack p_185191_0_) {
      return getPotionTypeFromNBT(p_185191_0_.getTag());
   }

   public static PotionType getPotionTypeFromNBT(@Nullable NBTTagCompound p_185187_0_) {
      return p_185187_0_ == null ? PotionTypes.EMPTY : PotionType.getPotionTypeForName(p_185187_0_.getString("Potion"));
   }

   public static ItemStack addPotionToItemStack(ItemStack p_185188_0_, PotionType p_185188_1_) {
      ResourceLocation resourcelocation = IRegistry.field_212621_j.func_177774_c(p_185188_1_);
      if (p_185188_1_ == PotionTypes.EMPTY) {
         p_185188_0_.removeChildTag("Potion");
      } else {
         p_185188_0_.getOrCreateTag().setString("Potion", resourcelocation.toString());
      }

      return p_185188_0_;
   }

   public static ItemStack appendEffects(ItemStack p_185184_0_, Collection<PotionEffect> p_185184_1_) {
      if (p_185184_1_.isEmpty()) {
         return p_185184_0_;
      } else {
         NBTTagCompound nbttagcompound = p_185184_0_.getOrCreateTag();
         NBTTagList nbttaglist = nbttagcompound.getTagList("CustomPotionEffects", 9);

         for(PotionEffect potioneffect : p_185184_1_) {
            nbttaglist.add((INBTBase)potioneffect.write(new NBTTagCompound()));
         }

         nbttagcompound.setTag("CustomPotionEffects", nbttaglist);
         return p_185184_0_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void addPotionTooltip(ItemStack p_185182_0_, List<ITextComponent> p_185182_1_, float p_185182_2_) {
      List<PotionEffect> list = getEffectsFromStack(p_185182_0_);
      List<Tuple<String, AttributeModifier>> list1 = Lists.newArrayList();
      if (list.isEmpty()) {
         p_185182_1_.add((new TextComponentTranslation("effect.none")).applyTextStyle(TextFormatting.GRAY));
      } else {
         for(PotionEffect potioneffect : list) {
            ITextComponent itextcomponent = new TextComponentTranslation(potioneffect.getEffectName());
            Potion potion = potioneffect.getPotion();
            Map<IAttribute, AttributeModifier> map = potion.getAttributeModifierMap();
            if (!map.isEmpty()) {
               for(Entry<IAttribute, AttributeModifier> entry : map.entrySet()) {
                  AttributeModifier attributemodifier = entry.getValue();
                  AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.getAttributeModifierAmount(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                  list1.add(new Tuple<>(entry.getKey().getName(), attributemodifier1));
               }
            }

            if (potioneffect.getAmplifier() > 0) {
               itextcomponent.appendText(" ").appendSibling(new TextComponentTranslation("potion.potency." + potioneffect.getAmplifier()));
            }

            if (potioneffect.getDuration() > 20) {
               itextcomponent.appendText(" (").appendText(PotionUtil.getPotionDurationString(potioneffect, p_185182_2_)).appendText(")");
            }

            p_185182_1_.add(itextcomponent.applyTextStyle(potion.isBadEffect() ? TextFormatting.RED : TextFormatting.BLUE));
         }
      }

      if (!list1.isEmpty()) {
         p_185182_1_.add(new TextComponentString(""));
         p_185182_1_.add((new TextComponentTranslation("potion.whenDrank")).applyTextStyle(TextFormatting.DARK_PURPLE));

         for(Tuple<String, AttributeModifier> tuple : list1) {
            AttributeModifier attributemodifier2 = tuple.getB();
            double d0 = attributemodifier2.getAmount();
            double d1;
            if (attributemodifier2.getOperation() != 1 && attributemodifier2.getOperation() != 2) {
               d1 = attributemodifier2.getAmount();
            } else {
               d1 = attributemodifier2.getAmount() * 100.0D;
            }

            if (d0 > 0.0D) {
               p_185182_1_.add((new TextComponentTranslation("attribute.modifier.plus." + attributemodifier2.getOperation(), ItemStack.DECIMALFORMAT.format(d1), new TextComponentTranslation("attribute.name." + (String)tuple.getA()))).applyTextStyle(TextFormatting.BLUE));
            } else if (d0 < 0.0D) {
               d1 = d1 * -1.0D;
               p_185182_1_.add((new TextComponentTranslation("attribute.modifier.take." + attributemodifier2.getOperation(), ItemStack.DECIMALFORMAT.format(d1), new TextComponentTranslation("attribute.name." + (String)tuple.getA()))).applyTextStyle(TextFormatting.RED));
            }
         }
      }

   }
}
