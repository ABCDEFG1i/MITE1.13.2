package net.minecraft.util.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextComponentSelector extends TextComponentBase {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String selector;
   @Nullable
   private final EntitySelector field_197670_d;

   public TextComponentSelector(String p_i45996_1_) {
      this.selector = p_i45996_1_;
      EntitySelector entityselector = null;

      try {
         EntitySelectorParser entityselectorparser = new EntitySelectorParser(new StringReader(p_i45996_1_));
         entityselector = entityselectorparser.parse();
      } catch (CommandSyntaxException commandsyntaxexception) {
         LOGGER.warn("Invalid selector component: {}", p_i45996_1_, commandsyntaxexception.getMessage());
      }

      this.field_197670_d = entityselector;
   }

   public String getSelector() {
      return this.selector;
   }

   public ITextComponent func_197668_a(CommandSource p_197668_1_) throws CommandSyntaxException {
      return (ITextComponent)(this.field_197670_d == null ? new TextComponentString("") : EntitySelector.func_197350_a(this.field_197670_d.select(p_197668_1_)));
   }

   public String getUnformattedComponentText() {
      return this.selector;
   }

   public TextComponentSelector createCopy() {
      return new TextComponentSelector(this.selector);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof TextComponentSelector)) {
         return false;
      } else {
         TextComponentSelector textcomponentselector = (TextComponentSelector)p_equals_1_;
         return this.selector.equals(textcomponentselector.selector) && super.equals(p_equals_1_);
      }
   }

   public String toString() {
      return "SelectorComponent{pattern='" + this.selector + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }
}
