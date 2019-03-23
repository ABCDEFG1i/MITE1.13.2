package net.minecraft.command;

import net.minecraft.util.text.ITextComponent;

public interface ICommandSource {
   void sendMessage(ITextComponent p_145747_1_);

   boolean shouldReceiveFeedback();

   boolean shouldReceiveErrors();

   boolean allowLogging();
}
