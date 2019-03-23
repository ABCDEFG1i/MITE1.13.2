package net.minecraft.client.gui;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiSleepMP extends GuiChat {
   protected void initGui() {
      super.initGui();
      this.addButton(new GuiButton(1, this.width / 2 + 100, this.height - 40, 100,20,I18n.format("multiplayer.stopSleeping")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiSleepMP.this.wakeFromSleep();
         }
      });
   }

   public void close() {
      this.wakeFromSleep();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.wakeFromSleep();
      } else if (p_keyPressed_1_ == 257 || p_keyPressed_1_ == 335) {
         String s = this.inputField.getText().trim();
         if (!s.isEmpty()) {
            this.mc.player.sendChatMessage(s);
         }

         this.inputField.setText("");
         this.mc.ingameGUI.getChatGUI().resetScroll();
         return true;
      }

      return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   private void wakeFromSleep() {
      NetHandlerPlayClient nethandlerplayclient = this.mc.player.connection;
      nethandlerplayclient.sendPacket(new CPacketEntityAction(this.mc.player, CPacketEntityAction.Action.STOP_SLEEPING));
   }
}
