package net.minecraft.util.text;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;

public class TextComponentUtils {
   public static ITextComponent func_211401_a(ITextComponent p_211401_0_, Style p_211401_1_) {
      if (p_211401_1_.isEmpty()) {
         return p_211401_0_;
      } else {
         return p_211401_0_.getStyle().isEmpty() ? p_211401_0_.setStyle(p_211401_1_.createShallowCopy()) : (new TextComponentString("")).appendSibling(p_211401_0_).setStyle(p_211401_1_.createShallowCopy());
      }
   }

   public static ITextComponent func_197680_a(@Nullable CommandSource p_197680_0_, ITextComponent p_197680_1_, @Nullable Entity p_197680_2_) throws CommandSyntaxException {
      ITextComponent itextcomponent;
      if (p_197680_1_ instanceof TextComponentScore && p_197680_0_ != null) {
         TextComponentScore textcomponentscore = (TextComponentScore)p_197680_1_;
         String s;
         if (textcomponentscore.func_197666_h() != null) {
            List<? extends Entity> list = textcomponentscore.func_197666_h().select(p_197680_0_);
            if (list.isEmpty()) {
               s = textcomponentscore.getName();
            } else {
               if (list.size() != 1) {
                  throw EntityArgument.TOO_MANY_ENTITIES.create();
               }

               s = list.get(0).getScoreboardName();
            }
         } else {
            s = textcomponentscore.getName();
         }

         String s1 = p_197680_2_ != null && s.equals("*") ? p_197680_2_.getScoreboardName() : s;
         itextcomponent = new TextComponentScore(s1, textcomponentscore.getObjective());
         ((TextComponentScore)itextcomponent).setValue(textcomponentscore.getUnformattedComponentText());
         ((TextComponentScore)itextcomponent).func_197665_b(p_197680_0_);
      } else if (p_197680_1_ instanceof TextComponentSelector && p_197680_0_ != null) {
         itextcomponent = ((TextComponentSelector)p_197680_1_).func_197668_a(p_197680_0_);
      } else if (p_197680_1_ instanceof TextComponentString) {
         itextcomponent = new TextComponentString(((TextComponentString)p_197680_1_).getText());
      } else if (p_197680_1_ instanceof TextComponentKeybind) {
         itextcomponent = new TextComponentKeybind(((TextComponentKeybind)p_197680_1_).getKeybind());
      } else {
         if (!(p_197680_1_ instanceof TextComponentTranslation)) {
            return p_197680_1_;
         }

         Object[] aobject = ((TextComponentTranslation)p_197680_1_).getFormatArgs();

         for(int i = 0; i < aobject.length; ++i) {
            Object object = aobject[i];
            if (object instanceof ITextComponent) {
               aobject[i] = func_197680_a(p_197680_0_, (ITextComponent)object, p_197680_2_);
            }
         }

         itextcomponent = new TextComponentTranslation(((TextComponentTranslation)p_197680_1_).getKey(), aobject);
      }

      for(ITextComponent itextcomponent1 : p_197680_1_.getSiblings()) {
         itextcomponent.appendSibling(func_197680_a(p_197680_0_, itextcomponent1, p_197680_2_));
      }

      return func_211401_a(itextcomponent, p_197680_1_.getStyle());
   }

   public static ITextComponent func_197679_a(GameProfile p_197679_0_) {
      if (p_197679_0_.getName() != null) {
         return new TextComponentString(p_197679_0_.getName());
      } else {
         return p_197679_0_.getId() != null ? new TextComponentString(p_197679_0_.getId().toString()) : new TextComponentString("(unknown)");
      }
   }

   public static ITextComponent makeGreenSortedList(Collection<String> p_197678_0_) {
      return makeSortedList(p_197678_0_, (p_197681_0_) -> {
         return (new TextComponentString(p_197681_0_)).applyTextStyle(TextFormatting.GREEN);
      });
   }

   public static <T extends Comparable<T>> ITextComponent makeSortedList(Collection<T> p_197675_0_, Function<T, ITextComponent> p_197675_1_) {
      if (p_197675_0_.isEmpty()) {
         return new TextComponentString("");
      } else if (p_197675_0_.size() == 1) {
         return p_197675_1_.apply(p_197675_0_.iterator().next());
      } else {
         List<T> list = Lists.newArrayList(p_197675_0_);
         list.sort(Comparable::compareTo);
         return makeList(p_197675_0_, p_197675_1_);
      }
   }

   public static <T> ITextComponent makeList(Collection<T> p_197677_0_, Function<T, ITextComponent> p_197677_1_) {
      if (p_197677_0_.isEmpty()) {
         return new TextComponentString("");
      } else if (p_197677_0_.size() == 1) {
         return p_197677_1_.apply(p_197677_0_.iterator().next());
      } else {
         ITextComponent itextcomponent = new TextComponentString("");
         boolean flag = true;

         for(T t : p_197677_0_) {
            if (!flag) {
               itextcomponent.appendSibling((new TextComponentString(", ")).applyTextStyle(TextFormatting.GRAY));
            }

            itextcomponent.appendSibling(p_197677_1_.apply(t));
            flag = false;
         }

         return itextcomponent;
      }
   }

   public static ITextComponent wrapInSquareBrackets(ITextComponent p_197676_0_) {
      return (new TextComponentString("[")).appendSibling(p_197676_0_).appendText("]");
   }

   public static ITextComponent toTextComponent(Message p_202465_0_) {
      return p_202465_0_ instanceof ITextComponent ? (ITextComponent)p_202465_0_ : new TextComponentString(p_202465_0_.getString());
   }
}
