package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.BossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiBossOverlay extends Gui {
   private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation("textures/gui/bars.png");
   private final Minecraft client;
   private final Map<UUID, BossInfoClient> mapBossInfos = Maps.newLinkedHashMap();

   public GuiBossOverlay(Minecraft p_i46606_1_) {
      this.client = p_i46606_1_;
   }

   public void renderBossHealth() {
      if (!this.mapBossInfos.isEmpty()) {
         int i = this.client.mainWindow.getScaledWidth();
         int j = 12;

         for(BossInfoClient bossinfoclient : this.mapBossInfos.values()) {
            int k = i / 2 - 91;
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.client.getTextureManager().bindTexture(GUI_BARS_TEXTURES);
            this.render(k, j, bossinfoclient);
            String s = bossinfoclient.getName().getFormattedText();
            this.client.fontRenderer.drawStringWithShadow(s, (float)(i / 2 - this.client.fontRenderer.getStringWidth(s) / 2), (float)(j - 9), 16777215);
            j += 10 + this.client.fontRenderer.FONT_HEIGHT;
            if (j >= this.client.mainWindow.getScaledHeight() / 3) {
               break;
            }
         }

      }
   }

   private void render(int p_184052_1_, int p_184052_2_, BossInfo p_184052_3_) {
      this.drawTexturedModalRect(p_184052_1_, p_184052_2_, 0, p_184052_3_.getColor().ordinal() * 5 * 2, 182, 5);
      if (p_184052_3_.getOverlay() != BossInfo.Overlay.PROGRESS) {
         this.drawTexturedModalRect(p_184052_1_, p_184052_2_, 0, 80 + (p_184052_3_.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
      }

      int i = (int)(p_184052_3_.getPercent() * 183.0F);
      if (i > 0) {
         this.drawTexturedModalRect(p_184052_1_, p_184052_2_, 0, p_184052_3_.getColor().ordinal() * 5 * 2 + 5, i, 5);
         if (p_184052_3_.getOverlay() != BossInfo.Overlay.PROGRESS) {
            this.drawTexturedModalRect(p_184052_1_, p_184052_2_, 0, 80 + (p_184052_3_.getOverlay().ordinal() - 1) * 5 * 2 + 5, i, 5);
         }
      }

   }

   public void read(SPacketUpdateBossInfo p_184055_1_) {
      if (p_184055_1_.getOperation() == SPacketUpdateBossInfo.Operation.ADD) {
         this.mapBossInfos.put(p_184055_1_.getUniqueId(), new BossInfoClient(p_184055_1_));
      } else if (p_184055_1_.getOperation() == SPacketUpdateBossInfo.Operation.REMOVE) {
         this.mapBossInfos.remove(p_184055_1_.getUniqueId());
      } else {
         this.mapBossInfos.get(p_184055_1_.getUniqueId()).updateFromPacket(p_184055_1_);
      }

   }

   public void clearBossInfos() {
      this.mapBossInfos.clear();
   }

   public boolean shouldPlayEndBossMusic() {
      if (!this.mapBossInfos.isEmpty()) {
         for(BossInfo bossinfo : this.mapBossInfos.values()) {
            if (bossinfo.shouldPlayEndBossMusic()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldDarkenSky() {
      if (!this.mapBossInfos.isEmpty()) {
         for(BossInfo bossinfo : this.mapBossInfos.values()) {
            if (bossinfo.shouldDarkenSky()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldCreateFog() {
      if (!this.mapBossInfos.isEmpty()) {
         for(BossInfo bossinfo : this.mapBossInfos.values()) {
            if (bossinfo.shouldCreateFog()) {
               return true;
            }
         }
      }

      return false;
   }
}
