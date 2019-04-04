package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public abstract class GuiScreen extends GuiEventHandler implements GuiYesNoCallback {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Set<String> PROTOCOLS = Sets.newHashSet("http", "https");
   protected final List<IGuiEventListener> eventListeners = Lists.newArrayList();
   public Minecraft mc;
   protected ItemRenderer itemRender;
   public int width;
   public int height;
   protected final List<GuiButton> buttons = Lists.newArrayList();
   protected final List<GuiLabel> labels = Lists.newArrayList();
   public boolean allowUserInput;
   protected FontRenderer fontRenderer;
   private URI clickedLinkURI;

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      for(int i = 0; i < this.buttons.size(); ++i) {
         this.buttons.get(i).render(p_73863_1_, p_73863_2_, p_73863_3_);
      }

      for(int j = 0; j < this.labels.size(); ++j) {
         this.labels.get(j).render(p_73863_1_, p_73863_2_, p_73863_3_);
      }

   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256 && this.allowCloseWithEscape()) {
         this.close();
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public boolean allowCloseWithEscape() {
      return true;
   }

   public void close() {
      this.mc.displayGuiScreen(null);
   }

   protected <T extends GuiButton> T addButton(T p_189646_1_) {
      this.buttons.add(p_189646_1_);
      this.eventListeners.add(p_189646_1_);
      return p_189646_1_;
   }

   protected void renderToolTip(ItemStack p_146285_1_, int p_146285_2_, int p_146285_3_) {
      this.drawHoveringText(this.getItemToolTip(p_146285_1_), p_146285_2_, p_146285_3_);
   }

   public List<String> getItemToolTip(ItemStack p_191927_1_) {
      List<ITextComponent> list = p_191927_1_.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
      List<String> list1 = Lists.newArrayList();

      for(ITextComponent itextcomponent : list) {
         list1.add(itextcomponent.getFormattedText());
      }

      return list1;
   }

   public void drawHoveringText(String p_146279_1_, int p_146279_2_, int p_146279_3_) {
      this.drawHoveringText(Arrays.asList(p_146279_1_), p_146279_2_, p_146279_3_);
   }

   public void drawHoveringText(List<String> p_146283_1_, int p_146283_2_, int p_146283_3_) {
      if (!p_146283_1_.isEmpty()) {
         GlStateManager.disableRescaleNormal();
         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableLighting();
         GlStateManager.disableDepthTest();
         int i = 0;

         for(String s : p_146283_1_) {
            int j = this.fontRenderer.getStringWidth(s);
            if (j > i) {
               i = j;
            }
         }

         int l1 = p_146283_2_ + 12;
         int i2 = p_146283_3_ - 12;
         int k = 8;
         if (p_146283_1_.size() > 1) {
            k += 2 + (p_146283_1_.size() - 1) * 10;
         }

         if (l1 + i > this.width) {
            l1 -= 28 + i;
         }

         if (i2 + k + 6 > this.height) {
            i2 = this.height - k - 6;
         }

         this.zLevel = 300.0F;
         this.itemRender.zLevel = 300.0F;
         int l = -267386864;
         this.drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, -267386864, -267386864);
         this.drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, -267386864, -267386864);
         this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, -267386864, -267386864);
         this.drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
         this.drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, -267386864, -267386864);
         int i1 = 1347420415;
         int j1 = 1344798847;
         this.drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
         this.drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
         this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 1347420415, 1347420415);
         this.drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 1344798847, 1344798847);

         for(int k1 = 0; k1 < p_146283_1_.size(); ++k1) {
            String s1 = p_146283_1_.get(k1);
            this.fontRenderer.drawStringWithShadow(s1, (float)l1, (float)i2, -1);
            if (k1 == 0) {
               i2 += 2;
            }

            i2 += 10;
         }

         this.zLevel = 0.0F;
         this.itemRender.zLevel = 0.0F;
         GlStateManager.enableLighting();
         GlStateManager.enableDepthTest();
         RenderHelper.enableStandardItemLighting();
         GlStateManager.enableRescaleNormal();
      }
   }

   protected void handleComponentHover(ITextComponent p_175272_1_, int p_175272_2_, int p_175272_3_) {
      if (p_175272_1_ != null && p_175272_1_.getStyle().getHoverEvent() != null) {
         HoverEvent hoverevent = p_175272_1_.getStyle().getHoverEvent();
         if (hoverevent.getAction() == HoverEvent.Action.SHOW_ITEM) {
            ItemStack itemstack = ItemStack.EMPTY;

            try {
               INBTBase inbtbase = JsonToNBT.getTagFromJson(hoverevent.getValue().getString());
               if (inbtbase instanceof NBTTagCompound) {
                  itemstack = ItemStack.loadFromNBT((NBTTagCompound)inbtbase);
               }
            } catch (CommandSyntaxException var10) {
            }

            if (itemstack.isEmpty()) {
               this.drawHoveringText(TextFormatting.RED + "Invalid Item!", p_175272_2_, p_175272_3_);
            } else {
               this.renderToolTip(itemstack, p_175272_2_, p_175272_3_);
            }
         } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
            if (this.mc.gameSettings.advancedItemTooltips) {
               try {
                  NBTTagCompound nbttagcompound = JsonToNBT.getTagFromJson(hoverevent.getValue().getString());
                  List<String> list = Lists.newArrayList();
                  ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(nbttagcompound.getString("name"));
                  if (itextcomponent != null) {
                     list.add(itextcomponent.getFormattedText());
                  }

                  if (nbttagcompound.hasKey("type", 8)) {
                     String s = nbttagcompound.getString("type");
                     list.add("Type: " + s);
                  }

                  list.add(nbttagcompound.getString("id"));
                  this.drawHoveringText(list, p_175272_2_, p_175272_3_);
               } catch (CommandSyntaxException | JsonSyntaxException var9) {
                  this.drawHoveringText(TextFormatting.RED + "Invalid Entity!", p_175272_2_, p_175272_3_);
               }
            }
         } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_TEXT) {
            this.drawHoveringText(this.mc.fontRenderer.listFormattedStringToWidth(hoverevent.getValue().getFormattedText(), Math.max(this.width / 2, 200)), p_175272_2_, p_175272_3_);
         }

         GlStateManager.disableLighting();
      }
   }

   protected void setText(String p_175274_1_, boolean p_175274_2_) {
   }

   public boolean handleComponentClick(ITextComponent p_175276_1_) {
      if (p_175276_1_ == null) {
         return false;
      } else {
         ClickEvent clickevent = p_175276_1_.getStyle().getClickEvent();
         if (isShiftKeyDown()) {
            if (p_175276_1_.getStyle().getInsertion() != null) {
               this.setText(p_175276_1_.getStyle().getInsertion(), false);
            }
         } else if (clickevent != null) {
            if (clickevent.getAction() == ClickEvent.Action.OPEN_URL) {
               if (!this.mc.gameSettings.chatLinks) {
                  return false;
               }

               try {
                  URI uri = new URI(clickevent.getValue());
                  String s = uri.getScheme();
                  if (s == null) {
                     throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
                  }

                  if (!PROTOCOLS.contains(s.toLowerCase(Locale.ROOT))) {
                     throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + s.toLowerCase(Locale.ROOT));
                  }

                  if (this.mc.gameSettings.chatLinksPrompt) {
                     this.clickedLinkURI = uri;
                     this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, clickevent.getValue(), 31102009, false));
                  } else {
                     this.openWebLink(uri);
                  }
               } catch (URISyntaxException urisyntaxexception) {
                  LOGGER.error("Can't open url for {}", clickevent, urisyntaxexception);
               }
            } else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE) {
               URI uri1 = (new File(clickevent.getValue())).toURI();
               this.openWebLink(uri1);
            } else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
               this.setText(clickevent.getValue(), true);
            } else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
               this.sendChatMessage(clickevent.getValue(), false);
            } else {
               LOGGER.error("Don't know how to handle {}", clickevent);
            }

            return true;
         }

         return false;
      }
   }

   public void sendChatMessage(String p_175275_1_) {
      this.sendChatMessage(p_175275_1_, true);
   }

   public void sendChatMessage(String p_175281_1_, boolean p_175281_2_) {
      if (p_175281_2_) {
         this.mc.ingameGUI.getChatGUI().addToSentMessages(p_175281_1_);
      }

      this.mc.player.sendChatMessage(p_175281_1_);
   }

   public void setWorldAndResolution(Minecraft p_146280_1_, int p_146280_2_, int p_146280_3_) {
      this.mc = p_146280_1_;
      this.itemRender = p_146280_1_.getItemRenderer();
      this.fontRenderer = p_146280_1_.fontRenderer;
      this.width = p_146280_2_;
      this.height = p_146280_3_;
      this.buttons.clear();
      this.eventListeners.clear();
      this.initGui();
   }

   public List<? extends IGuiEventListener> getChildren() {
      return this.eventListeners;
   }

   protected void initGui() {
      this.eventListeners.addAll(this.labels);
   }

   public void tick() {
   }

   public void onGuiClosed() {
   }

   public void drawDefaultBackground() {
      this.drawWorldBackground(0);
   }

   public void drawWorldBackground(int p_146270_1_) {
      if (this.mc.world != null) {
         this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
      } else {
         this.drawBackground(p_146270_1_);
      }

   }

   public void drawBackground(int p_146278_1_) {
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      this.mc.getTextureManager().bindTexture(OPTIONS_BACKGROUND);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      bufferbuilder.pos(0.0D, (double)this.height, 0.0D).tex(0.0D, (double)((float)this.height / 32.0F + (float)p_146278_1_)).color(64, 64, 64, 255).endVertex();
      bufferbuilder.pos((double)this.width, (double)this.height, 0.0D).tex((double)((float)this.width / 32.0F), (double)((float)this.height / 32.0F + (float)p_146278_1_)).color(64, 64, 64, 255).endVertex();
      bufferbuilder.pos((double)this.width, 0.0D, 0.0D).tex((double)((float)this.width / 32.0F), (double)p_146278_1_).color(64, 64, 64, 255).endVertex();
      bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double)p_146278_1_).color(64, 64, 64, 255).endVertex();
      tessellator.draw();
   }

   public boolean doesGuiPauseGame() {
      return true;
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (p_confirmResult_2_ == 31102009) {
         if (p_confirmResult_1_) {
            this.openWebLink(this.clickedLinkURI);
         }

         this.clickedLinkURI = null;
         this.mc.displayGuiScreen(this);
      }

   }

   private void openWebLink(URI p_175282_1_) {
      Util.getOSType().openURI(p_175282_1_);
   }

   public static boolean isCtrlKeyDown() {
      if (Minecraft.IS_RUNNING_ON_MAC) {
         return InputMappings.isKeyDown(343) || InputMappings.isKeyDown(347);
      } else {
         return InputMappings.isKeyDown(341) || InputMappings.isKeyDown(345);
      }
   }

   public static boolean isShiftKeyDown() {
      return InputMappings.isKeyDown(340) || InputMappings.isKeyDown(344);
   }

   public static boolean isAltKeyDown() {
      return InputMappings.isKeyDown(342) || InputMappings.isKeyDown(346);
   }

   public static boolean isKeyComboCtrlX(int p_175277_0_) {
      return p_175277_0_ == 88 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public static boolean isKeyComboCtrlV(int p_175279_0_) {
      return p_175279_0_ == 86 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public static boolean isKeyComboCtrlC(int p_175280_0_) {
      return p_175280_0_ == 67 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public static boolean isKeyComboCtrlA(int p_175278_0_) {
      return p_175278_0_ == 65 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public void onResize(Minecraft p_175273_1_, int p_175273_2_, int p_175273_3_) {
      this.setWorldAndResolution(p_175273_1_, p_175273_2_, p_175273_3_);
   }

   public static void runOrMakeCrashReport(Runnable p_195121_0_, String p_195121_1_, String p_195121_2_) {
      try {
         p_195121_0_.run();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, p_195121_1_);
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
         crashreportcategory.addDetail("Screen name", () -> {
            return p_195121_2_;
         });
         throw new ReportedException(crashreport);
      }
   }
}
