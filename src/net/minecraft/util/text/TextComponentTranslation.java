package net.minecraft.util.text;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.util.text.translation.LanguageMap;

public class TextComponentTranslation extends TextComponentBase {
   private static final LanguageMap field_200526_d = new LanguageMap();
   private static final LanguageMap field_200527_e = LanguageMap.getInstance();
   private final String key;
   private final Object[] formatArgs;
   private final Object syncLock = new Object();
   private long lastTranslationUpdateTimeInMilliseconds = -1L;
   @VisibleForTesting
   List<ITextComponent> children = Lists.newArrayList();
   public static final Pattern STRING_VARIABLE_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

   public TextComponentTranslation(String p_i45160_1_, Object... p_i45160_2_) {
      this.key = p_i45160_1_;
      this.formatArgs = p_i45160_2_;

      for(int i = 0; i < p_i45160_2_.length; ++i) {
         Object object = p_i45160_2_[i];
         if (object instanceof ITextComponent) {
            ITextComponent itextcomponent = ((ITextComponent)object).func_212638_h();
            this.formatArgs[i] = itextcomponent;
            itextcomponent.getStyle().setParentStyle(this.getStyle());
         } else if (object == null) {
            this.formatArgs[i] = "null";
         }
      }

   }

   @VisibleForTesting
   synchronized void ensureInitialized() {
      synchronized(this.syncLock) {
         long i = field_200527_e.getLastUpdateTimeInMilliseconds();
         if (i == this.lastTranslationUpdateTimeInMilliseconds) {
            return;
         }

         this.lastTranslationUpdateTimeInMilliseconds = i;
         this.children.clear();
      }

      try {
         this.initializeFromFormat(field_200527_e.translateKey(this.key));
      } catch (TextComponentTranslationFormatException textcomponenttranslationformatexception) {
         this.children.clear();

         try {
            this.initializeFromFormat(field_200526_d.translateKey(this.key));
         } catch (TextComponentTranslationFormatException var5) {
            throw textcomponenttranslationformatexception;
         }
      }

   }

   protected void initializeFromFormat(String p_150269_1_) {
      Matcher matcher = STRING_VARIABLE_PATTERN.matcher(p_150269_1_);

      try {
         int i = 0;

         int j;
         int l;
         for(j = 0; matcher.find(j); j = l) {
            int k = matcher.start();
            l = matcher.end();
            if (k > j) {
               ITextComponent itextcomponent = new TextComponentString(String.format(p_150269_1_.substring(j, k)));
               itextcomponent.getStyle().setParentStyle(this.getStyle());
               this.children.add(itextcomponent);
            }

            String s2 = matcher.group(2);
            String s = p_150269_1_.substring(k, l);
            if ("%".equals(s2) && "%%".equals(s)) {
               ITextComponent itextcomponent2 = new TextComponentString("%");
               itextcomponent2.getStyle().setParentStyle(this.getStyle());
               this.children.add(itextcomponent2);
            } else {
               if (!"s".equals(s2)) {
                  throw new TextComponentTranslationFormatException(this, "Unsupported format: '" + s + "'");
               }

               String s1 = matcher.group(1);
               int i1 = s1 != null ? Integer.parseInt(s1) - 1 : i++;
               if (i1 < this.formatArgs.length) {
                  this.children.add(this.getFormatArgumentAsComponent(i1));
               }
            }
         }

         if (j < p_150269_1_.length()) {
            ITextComponent itextcomponent1 = new TextComponentString(String.format(p_150269_1_.substring(j)));
            itextcomponent1.getStyle().setParentStyle(this.getStyle());
            this.children.add(itextcomponent1);
         }

      } catch (IllegalFormatException illegalformatexception) {
         throw new TextComponentTranslationFormatException(this, illegalformatexception);
      }
   }

   private ITextComponent getFormatArgumentAsComponent(int p_150272_1_) {
      if (p_150272_1_ >= this.formatArgs.length) {
         throw new TextComponentTranslationFormatException(this, p_150272_1_);
      } else {
         Object object = this.formatArgs[p_150272_1_];
         ITextComponent itextcomponent;
         if (object instanceof ITextComponent) {
            itextcomponent = (ITextComponent)object;
         } else {
            itextcomponent = new TextComponentString(object == null ? "null" : object.toString());
            itextcomponent.getStyle().setParentStyle(this.getStyle());
         }

         return itextcomponent;
      }
   }

   public ITextComponent setStyle(Style p_150255_1_) {
      super.setStyle(p_150255_1_);

      for(Object object : this.formatArgs) {
         if (object instanceof ITextComponent) {
            ((ITextComponent)object).getStyle().setParentStyle(this.getStyle());
         }
      }

      if (this.lastTranslationUpdateTimeInMilliseconds > -1L) {
         for(ITextComponent itextcomponent : this.children) {
            itextcomponent.getStyle().setParentStyle(p_150255_1_);
         }
      }

      return this;
   }

   public Stream<ITextComponent> func_212640_c() {
      this.ensureInitialized();
      return Streams.<ITextComponent>concat(this.children.stream(), this.siblings.stream()).flatMap(ITextComponent::func_212640_c);
   }

   public String getUnformattedComponentText() {
      this.ensureInitialized();
      StringBuilder stringbuilder = new StringBuilder();

      for(ITextComponent itextcomponent : this.children) {
         stringbuilder.append(itextcomponent.getUnformattedComponentText());
      }

      return stringbuilder.toString();
   }

   public TextComponentTranslation createCopy() {
      Object[] aobject = new Object[this.formatArgs.length];

      for(int i = 0; i < this.formatArgs.length; ++i) {
         if (this.formatArgs[i] instanceof ITextComponent) {
            aobject[i] = ((ITextComponent)this.formatArgs[i]).func_212638_h();
         } else {
            aobject[i] = this.formatArgs[i];
         }
      }

      return new TextComponentTranslation(this.key, aobject);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof TextComponentTranslation)) {
         return false;
      } else {
         TextComponentTranslation textcomponenttranslation = (TextComponentTranslation)p_equals_1_;
         return Arrays.equals(this.formatArgs, textcomponenttranslation.formatArgs) && this.key.equals(textcomponenttranslation.key) && super.equals(p_equals_1_);
      }
   }

   public int hashCode() {
      int i = super.hashCode();
      i = 31 * i + this.key.hashCode();
      i = 31 * i + Arrays.hashCode(this.formatArgs);
      return i;
   }

   public String toString() {
      return "TranslatableComponent{key='" + this.key + '\'' + ", args=" + Arrays.toString(this.formatArgs) + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }

   public String getKey() {
      return this.key;
   }

   public Object[] getFormatArgs() {
      return this.formatArgs;
   }
}
