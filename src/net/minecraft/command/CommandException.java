package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.text.ITextComponent;

public class CommandException extends RuntimeException {
   private final ITextComponent field_197004_a;

   public CommandException(ITextComponent p_i47972_1_) {
      super(p_i47972_1_.getUnformattedComponentText(), (Throwable)null, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES);
      this.field_197004_a = p_i47972_1_;
   }

   public ITextComponent func_197003_a() {
      return this.field_197004_a;
   }
}
