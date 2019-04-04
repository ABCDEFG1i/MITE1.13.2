package net.minecraft.client.gui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.CPacketHandshake;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiConnecting extends GuiScreen {
   private static final AtomicInteger CONNECTION_ID = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();
   private NetworkManager networkManager;
   private boolean cancel;
   private final GuiScreen previousGuiScreen;
   private ITextComponent field_209515_s = new TextComponentTranslation("connect.connecting");

   public GuiConnecting(GuiScreen p_i1181_1_, Minecraft p_i1181_2_, ServerData p_i1181_3_) {
      this.mc = p_i1181_2_;
      this.previousGuiScreen = p_i1181_1_;
      ServerAddress serveraddress = ServerAddress.fromString(p_i1181_3_.serverIP);
      p_i1181_2_.loadWorld(null);
      p_i1181_2_.setServerData(p_i1181_3_);
      this.connect(serveraddress.getIP(), serveraddress.getPort());
   }

   public GuiConnecting(GuiScreen p_i1182_1_, Minecraft p_i1182_2_, String p_i1182_3_, int p_i1182_4_) {
      this.mc = p_i1182_2_;
      this.previousGuiScreen = p_i1182_1_;
      p_i1182_2_.loadWorld(null);
      this.connect(p_i1182_3_, p_i1182_4_);
   }

   private void connect(final String p_146367_1_, final int p_146367_2_) {
      LOGGER.info("Connecting to {}, {}", p_146367_1_, p_146367_2_);
      Thread thread = new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet()) {
         public void run() {
            InetAddress inetaddress = null;

            try {
               if (GuiConnecting.this.cancel) {
                  return;
               }

               inetaddress = InetAddress.getByName(p_146367_1_);
               GuiConnecting.this.networkManager = NetworkManager.createNetworkManagerAndConnect(inetaddress, p_146367_2_, GuiConnecting.this.mc.gameSettings.isUsingNativeTransport());
               GuiConnecting.this.networkManager.setNetHandler(new NetHandlerLoginClient(GuiConnecting.this.networkManager, GuiConnecting.this.mc, GuiConnecting.this.previousGuiScreen, (p_209549_1_) -> {
                  GuiConnecting.this.func_209514_a(p_209549_1_);
               }));
               GuiConnecting.this.networkManager.sendPacket(new CPacketHandshake(p_146367_1_, p_146367_2_, EnumConnectionState.LOGIN));
               GuiConnecting.this.networkManager.sendPacket(new CPacketLoginStart(GuiConnecting.this.mc.getSession().getProfile()));
            } catch (UnknownHostException unknownhostexception) {
               if (GuiConnecting.this.cancel) {
                  return;
               }

               GuiConnecting.LOGGER.error("Couldn't connect to server", unknownhostexception);
               GuiConnecting.this.mc.addScheduledTask(() -> {
                  GuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnecting.this.previousGuiScreen, "connect.failed", new TextComponentTranslation("disconnect.genericReason", "Unknown host")));
               });
            } catch (Exception exception) {
               if (GuiConnecting.this.cancel) {
                  return;
               }

               GuiConnecting.LOGGER.error("Couldn't connect to server", exception);
               String s = inetaddress == null ? exception.toString() : exception.toString().replaceAll(inetaddress + ":" + p_146367_2_, "");
               GuiConnecting.this.mc.addScheduledTask(() -> {
                  GuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnecting.this.previousGuiScreen, "connect.failed", new TextComponentTranslation("disconnect.genericReason", s)));
               });
            }

         }
      };
      thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      thread.start();
   }

   private void func_209514_a(ITextComponent p_209514_1_) {
      this.field_209515_s = p_209514_1_;
   }

   public void tick() {
      if (this.networkManager != null) {
         if (this.networkManager.isChannelOpen()) {
            this.networkManager.tick();
         } else {
            this.networkManager.handleDisconnection();
         }
      }

   }

   public boolean allowCloseWithEscape() {
      return false;
   }

   protected void initGui() {
      this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiConnecting.this.cancel = true;
            if (GuiConnecting.this.networkManager != null) {
               GuiConnecting.this.networkManager.closeChannel(new TextComponentTranslation("connect.aborted"));
            }

            GuiConnecting.this.mc.displayGuiScreen(GuiConnecting.this.previousGuiScreen);
         }
      });
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.field_209515_s.getFormattedText(), this.width / 2, this.height / 2 - 50, 16777215);
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }
}
