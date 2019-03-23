package net.minecraft.client.gui.chat;

import com.mojang.text2speech.Narrator;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NarratorChatListener implements IChatListener {
   public static final NarratorChatListener INSTANCE = new NarratorChatListener();
   private final Narrator narrator = Narrator.getNarrator();

   public void say(ChatType p_192576_1_, ITextComponent p_192576_2_) {
      int i = Minecraft.getInstance().gameSettings.narrator;
      if (i != 0 && this.narrator.active()) {
         if (i == 1 || i == 2 && p_192576_1_ == ChatType.CHAT || i == 3 && p_192576_1_ == ChatType.SYSTEM) {
            if (p_192576_2_ instanceof TextComponentTranslation && "chat.type.text".equals(((TextComponentTranslation)p_192576_2_).getKey())) {
               this.narrator.say((new TextComponentTranslation("chat.type.text.narrate", ((TextComponentTranslation)p_192576_2_).getFormatArgs())).getString());
            } else {
               this.narrator.say(p_192576_2_.getString());
            }
         }

      }
   }

   public void announceMode(int p_193641_1_) {
      this.narrator.clear();
      this.narrator.say((new TextComponentTranslation("options.narrator")).getString() + " : " + (new TextComponentTranslation(GameSettings.NARRATOR_MODES[p_193641_1_])).getString());
      GuiToast guitoast = Minecraft.getInstance().getToastGui();
      if (this.narrator.active()) {
         if (p_193641_1_ == 0) {
            SystemToast.addOrUpdate(guitoast, SystemToast.Type.NARRATOR_TOGGLE, new TextComponentTranslation("narrator.toast.disabled"), (ITextComponent)null);
         } else {
            SystemToast.addOrUpdate(guitoast, SystemToast.Type.NARRATOR_TOGGLE, new TextComponentTranslation("narrator.toast.enabled"), new TextComponentTranslation(GameSettings.NARRATOR_MODES[p_193641_1_]));
         }
      } else {
         SystemToast.addOrUpdate(guitoast, SystemToast.Type.NARRATOR_TOGGLE, new TextComponentTranslation("narrator.toast.disabled"), new TextComponentTranslation("options.narrator.notavailable"));
      }

   }

   public boolean isActive() {
      return this.narrator.active();
   }

   public void clear() {
      this.narrator.clear();
   }
}
