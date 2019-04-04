package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketUpdateBeacon;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiBeacon extends GuiContainer {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation BEACON_GUI_TEXTURES = new ResourceLocation("textures/gui/container/beacon.png");
   private final IInventory tileBeacon;
   private GuiBeacon.ConfirmButton beaconConfirmButton;
   private boolean buttonsNotDrawn;

   public GuiBeacon(InventoryPlayer p_i45507_1_, IInventory p_i45507_2_) {
      super(new ContainerBeacon(p_i45507_1_, p_i45507_2_));
      this.tileBeacon = p_i45507_2_;
      this.xSize = 230;
      this.ySize = 219;
   }

   protected void initGui() {
      super.initGui();
      this.beaconConfirmButton = new GuiBeacon.ConfirmButton(-1, this.guiLeft + 164, this.guiTop + 107);
      this.addButton(this.beaconConfirmButton);
      this.addButton(new GuiBeacon.CancelButton(-2, this.guiLeft + 190, this.guiTop + 107));
      this.buttonsNotDrawn = true;
      this.beaconConfirmButton.enabled = false;
   }

   public void tick() {
      super.tick();
      int i = this.tileBeacon.getField(0);
      Potion potion = Potion.getPotionById(this.tileBeacon.getField(1));
      Potion potion1 = Potion.getPotionById(this.tileBeacon.getField(2));
      if (this.buttonsNotDrawn && i >= 0) {
         this.buttonsNotDrawn = false;
         int j = 100;

         for(int k = 0; k <= 2; ++k) {
            int l = TileEntityBeacon.EFFECTS_LIST[k].length;
            int i1 = l * 22 + (l - 1) * 2;

            for(int j1 = 0; j1 < l; ++j1) {
               Potion potion2 = TileEntityBeacon.EFFECTS_LIST[k][j1];
               GuiBeacon.PowerButton guibeacon$powerbutton = new GuiBeacon.PowerButton(j++, this.guiLeft + 76 + j1 * 24 - i1 / 2, this.guiTop + 22 + k * 25, potion2, k);
               this.addButton(guibeacon$powerbutton);
               if (k >= i) {
                  guibeacon$powerbutton.enabled = false;
               } else if (potion2 == potion) {
                  guibeacon$powerbutton.setSelected(true);
               }
            }
         }

         int k1 = 3;
         int l1 = TileEntityBeacon.EFFECTS_LIST[3].length + 1;
         int i2 = l1 * 22 + (l1 - 1) * 2;

         for(int j2 = 0; j2 < l1 - 1; ++j2) {
            Potion potion3 = TileEntityBeacon.EFFECTS_LIST[3][j2];
            GuiBeacon.PowerButton guibeacon$powerbutton2 = new GuiBeacon.PowerButton(j++, this.guiLeft + 167 + j2 * 24 - i2 / 2, this.guiTop + 47, potion3, 3);
            this.addButton(guibeacon$powerbutton2);
            if (3 >= i) {
               guibeacon$powerbutton2.enabled = false;
            } else if (potion3 == potion1) {
               guibeacon$powerbutton2.setSelected(true);
            }
         }

         if (potion != null) {
            GuiBeacon.PowerButton guibeacon$powerbutton1 = new GuiBeacon.PowerButton(j++, this.guiLeft + 167 + (l1 - 1) * 24 - i2 / 2, this.guiTop + 47, potion, 3);
            this.addButton(guibeacon$powerbutton1);
            if (3 >= i) {
               guibeacon$powerbutton1.enabled = false;
            } else if (potion == potion1) {
               guibeacon$powerbutton1.setSelected(true);
            }
         }
      }

      this.beaconConfirmButton.enabled = !this.tileBeacon.getStackInSlot(0).isEmpty() && potion != null;
   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      RenderHelper.disableStandardItemLighting();
      this.drawCenteredString(this.fontRenderer, I18n.format("block.minecraft.beacon.primary"), 62, 10, 14737632);
      this.drawCenteredString(this.fontRenderer, I18n.format("block.minecraft.beacon.secondary"), 169, 10, 14737632);

      for(GuiButton guibutton : this.buttons) {
         if (guibutton.isMouseOver()) {
            guibutton.drawButtonForegroundLayer(p_146979_1_ - this.guiLeft, p_146979_2_ - this.guiTop);
            break;
         }
      }

      RenderHelper.enableGUIStandardItemLighting();
   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(BEACON_GUI_TEXTURES);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
      this.itemRender.zLevel = 100.0F;
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.EMERALD), i + 42, j + 109);
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.DIAMOND), i + 42 + 22, j + 109);
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 109);
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 109);
      this.itemRender.zLevel = 0.0F;
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
      this.renderHoveredToolTip(p_73863_1_, p_73863_2_);
   }

   @OnlyIn(Dist.CLIENT)
   abstract static class Button extends GuiButton {
      private final ResourceLocation iconTexture;
      private final int iconX;
      private final int iconY;
      private boolean selected;

      protected Button(int p_i1077_1_, int p_i1077_2_, int p_i1077_3_, ResourceLocation p_i1077_4_, int p_i1077_5_, int p_i1077_6_) {
         super(p_i1077_1_, p_i1077_2_, p_i1077_3_, 22, 22, "");
         this.iconTexture = p_i1077_4_;
         this.iconX = p_i1077_5_;
         this.iconY = p_i1077_6_;
      }

      public void render(int p_194828_1_, int p_194828_2_, float p_194828_3_) {
         if (this.visible) {
            Minecraft.getInstance().getTextureManager().bindTexture(GuiBeacon.BEACON_GUI_TEXTURES);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = p_194828_1_ >= this.x && p_194828_2_ >= this.y && p_194828_1_ < this.x + this.width && p_194828_2_ < this.y + this.height;
            int i = 219;
            int j = 0;
            if (!this.enabled) {
               j += this.width * 2;
            } else if (this.selected) {
               j += this.width * 1;
            } else if (this.hovered) {
               j += this.width * 3;
            }

            this.drawTexturedModalRect(this.x, this.y, j, 219, this.width, this.height);
            if (!GuiBeacon.BEACON_GUI_TEXTURES.equals(this.iconTexture)) {
               Minecraft.getInstance().getTextureManager().bindTexture(this.iconTexture);
            }

            this.drawTexturedModalRect(this.x + 2, this.y + 2, this.iconX, this.iconY, 18, 18);
         }
      }

      public boolean isSelected() {
         return this.selected;
      }

      public void setSelected(boolean p_146140_1_) {
         this.selected = p_146140_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class CancelButton extends GuiBeacon.Button {
      public CancelButton(int p_i1074_2_, int p_i1074_3_, int p_i1074_4_) {
         super(p_i1074_2_, p_i1074_3_, p_i1074_4_, GuiBeacon.BEACON_GUI_TEXTURES, 112, 220);
      }

      public void onClick(double p_194829_1_, double p_194829_3_) {
         GuiBeacon.this.mc.player.connection.sendPacket(new CPacketCloseWindow(GuiBeacon.this.mc.player.openContainer.windowId));
         GuiBeacon.this.mc.displayGuiScreen(null);
      }

      public void drawButtonForegroundLayer(int p_146111_1_, int p_146111_2_) {
         GuiBeacon.this.drawHoveringText(I18n.format("gui.cancel"), p_146111_1_, p_146111_2_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ConfirmButton extends GuiBeacon.Button {
      public ConfirmButton(int p_i1075_2_, int p_i1075_3_, int p_i1075_4_) {
         super(p_i1075_2_, p_i1075_3_, p_i1075_4_, GuiBeacon.BEACON_GUI_TEXTURES, 90, 220);
      }

      public void onClick(double p_194829_1_, double p_194829_3_) {
         GuiBeacon.this.mc.getConnection().sendPacket(new CPacketUpdateBeacon(GuiBeacon.this.tileBeacon.getField(1), GuiBeacon.this.tileBeacon.getField(2)));
         GuiBeacon.this.mc.player.connection.sendPacket(new CPacketCloseWindow(GuiBeacon.this.mc.player.openContainer.windowId));
         GuiBeacon.this.mc.displayGuiScreen(null);
      }

      public void drawButtonForegroundLayer(int p_146111_1_, int p_146111_2_) {
         GuiBeacon.this.drawHoveringText(I18n.format("gui.done"), p_146111_1_, p_146111_2_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class PowerButton extends GuiBeacon.Button {
      private final Potion effect;
      private final int tier;

      public PowerButton(int p_i47045_2_, int p_i47045_3_, int p_i47045_4_, Potion p_i47045_5_, int p_i47045_6_) {
         super(p_i47045_2_, p_i47045_3_, p_i47045_4_, GuiContainer.INVENTORY_BACKGROUND, p_i47045_5_.getStatusIconIndex() % 12 * 18, 198 + p_i47045_5_.getStatusIconIndex() / 12 * 18);
         this.effect = p_i47045_5_;
         this.tier = p_i47045_6_;
      }

      public void onClick(double p_194829_1_, double p_194829_3_) {
         if (!this.isSelected()) {
            int i = Potion.getIdFromPotion(this.effect);
            if (this.tier < 3) {
               GuiBeacon.this.tileBeacon.setField(1, i);
            } else {
               GuiBeacon.this.tileBeacon.setField(2, i);
            }

            GuiBeacon.this.buttons.clear();
            GuiBeacon.this.eventListeners.clear();
            GuiBeacon.this.initGui();
            GuiBeacon.this.tick();
         }
      }

      public void drawButtonForegroundLayer(int p_146111_1_, int p_146111_2_) {
         String s = I18n.format(this.effect.getName());
         if (this.tier >= 3 && this.effect != MobEffects.REGENERATION) {
            s = s + " II";
         }

         GuiBeacon.this.drawHoveringText(s, p_146111_1_, p_146111_2_);
      }
   }
}
