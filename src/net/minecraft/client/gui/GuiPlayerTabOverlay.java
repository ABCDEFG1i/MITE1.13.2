package net.minecraft.client.gui;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiPlayerTabOverlay extends Gui {
   private static final Ordering<NetworkPlayerInfo> ENTRY_ORDERING = Ordering.from(new GuiPlayerTabOverlay.PlayerComparator());
   private final Minecraft mc;
   private final GuiIngame guiIngame;
   private ITextComponent footer;
   private ITextComponent header;
   private long lastTimeOpened;
   private boolean visible;

   public GuiPlayerTabOverlay(Minecraft p_i45529_1_, GuiIngame p_i45529_2_) {
      this.mc = p_i45529_1_;
      this.guiIngame = p_i45529_2_;
   }

   public ITextComponent getDisplayName(NetworkPlayerInfo p_200262_1_) {
      return p_200262_1_.getDisplayName() != null ? p_200262_1_.getDisplayName() : ScorePlayerTeam.formatMemberName(p_200262_1_.getPlayerTeam(), new TextComponentString(p_200262_1_.getGameProfile().getName()));
   }

   public void setVisible(boolean p_175246_1_) {
      if (p_175246_1_ && !this.visible) {
         this.lastTimeOpened = Util.milliTime();
      }

      this.visible = p_175246_1_;
   }

   public void renderPlayerlist(int p_175249_1_, Scoreboard p_175249_2_, @Nullable ScoreObjective p_175249_3_) {
      NetHandlerPlayClient nethandlerplayclient = this.mc.player.connection;
      List<NetworkPlayerInfo> list = ENTRY_ORDERING.sortedCopy(nethandlerplayclient.getPlayerInfoMap());
      int i = 0;
      int j = 0;

      for(NetworkPlayerInfo networkplayerinfo : list) {
         int k = this.mc.fontRenderer.getStringWidth(this.getDisplayName(networkplayerinfo).getFormattedText());
         i = Math.max(i, k);
         if (p_175249_3_ != null && p_175249_3_.func_199865_f() != ScoreCriteria.RenderType.HEARTS) {
            k = this.mc.fontRenderer.getStringWidth(" " + p_175249_2_.getOrCreateScore(networkplayerinfo.getGameProfile().getName(), p_175249_3_).getScorePoints());
            j = Math.max(j, k);
         }
      }

      list = list.subList(0, Math.min(list.size(), 80));
      int l3 = list.size();
      int i4 = l3;

      int j4;
      for(j4 = 1; i4 > 20; i4 = (l3 + j4 - 1) / j4) {
         ++j4;
      }

      boolean flag = this.mc.isIntegratedServerRunning() || this.mc.getConnection().getNetworkManager().isEncrypted();
      int l;
      if (p_175249_3_ != null) {
         if (p_175249_3_.func_199865_f() == ScoreCriteria.RenderType.HEARTS) {
            l = 90;
         } else {
            l = j;
         }
      } else {
         l = 0;
      }

      int i1 = Math.min(j4 * ((flag ? 9 : 0) + i + l + 13), p_175249_1_ - 50) / j4;
      int j1 = p_175249_1_ / 2 - (i1 * j4 + (j4 - 1) * 5) / 2;
      int k1 = 10;
      int l1 = i1 * j4 + (j4 - 1) * 5;
      List<String> list1 = null;
      if (this.header != null) {
         list1 = this.mc.fontRenderer.listFormattedStringToWidth(this.header.getFormattedText(), p_175249_1_ - 50);

         for(String s : list1) {
            l1 = Math.max(l1, this.mc.fontRenderer.getStringWidth(s));
         }
      }

      List<String> list2 = null;
      if (this.footer != null) {
         list2 = this.mc.fontRenderer.listFormattedStringToWidth(this.footer.getFormattedText(), p_175249_1_ - 50);

         for(String s1 : list2) {
            l1 = Math.max(l1, this.mc.fontRenderer.getStringWidth(s1));
         }
      }

      if (list1 != null) {
         drawRect(p_175249_1_ / 2 - l1 / 2 - 1, k1 - 1, p_175249_1_ / 2 + l1 / 2 + 1, k1 + list1.size() * this.mc.fontRenderer.FONT_HEIGHT, Integer.MIN_VALUE);

         for(String s2 : list1) {
            int i2 = this.mc.fontRenderer.getStringWidth(s2);
            this.mc.fontRenderer.drawStringWithShadow(s2, (float)(p_175249_1_ / 2 - i2 / 2), (float)k1, -1);
            k1 += this.mc.fontRenderer.FONT_HEIGHT;
         }

         ++k1;
      }

      drawRect(p_175249_1_ / 2 - l1 / 2 - 1, k1 - 1, p_175249_1_ / 2 + l1 / 2 + 1, k1 + i4 * 9, Integer.MIN_VALUE);

      for(int k4 = 0; k4 < l3; ++k4) {
         int l4 = k4 / i4;
         int i5 = k4 % i4;
         int j2 = j1 + l4 * i1 + l4 * 5;
         int k2 = k1 + i5 * 9;
         drawRect(j2, k2, j2 + i1, k2 + 8, 553648127);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableAlphaTest();
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         if (k4 < list.size()) {
            NetworkPlayerInfo networkplayerinfo1 = list.get(k4);
            GameProfile gameprofile = networkplayerinfo1.getGameProfile();
            if (flag) {
               EntityPlayer entityplayer = this.mc.world.getPlayerEntityByUUID(gameprofile.getId());
               boolean flag1 = entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.CAPE) && ("Dinnerbone".equals(gameprofile.getName()) || "Grumm".equals(gameprofile.getName()));
               this.mc.getTextureManager().bindTexture(networkplayerinfo1.getLocationSkin());
               int l2 = 8 + (flag1 ? 8 : 0);
               int i3 = 8 * (flag1 ? -1 : 1);
               Gui.drawScaledCustomSizeModalRect(j2, k2, 8.0F, (float)l2, 8, i3, 8, 8, 64.0F, 64.0F);
               if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                  int j3 = 8 + (flag1 ? 8 : 0);
                  int k3 = 8 * (flag1 ? -1 : 1);
                  Gui.drawScaledCustomSizeModalRect(j2, k2, 40.0F, (float)j3, 8, k3, 8, 8, 64.0F, 64.0F);
               }

               j2 += 9;
            }

            String s4 = this.getDisplayName(networkplayerinfo1).getFormattedText();
            if (networkplayerinfo1.getGameType() == GameType.SPECTATOR) {
               this.mc.fontRenderer.drawStringWithShadow(TextFormatting.ITALIC + s4, (float)j2, (float)k2, -1862270977);
            } else {
               this.mc.fontRenderer.drawStringWithShadow(s4, (float)j2, (float)k2, -1);
            }

            if (p_175249_3_ != null && networkplayerinfo1.getGameType() != GameType.SPECTATOR) {
               int k5 = j2 + i + 1;
               int l5 = k5 + l;
               if (l5 - k5 > 5) {
                  this.drawScoreboardValues(p_175249_3_, k2, gameprofile.getName(), k5, l5, networkplayerinfo1);
               }
            }

            this.drawPing(i1, j2 - (flag ? 9 : 0), k2, networkplayerinfo1);
         }
      }

      if (list2 != null) {
         k1 = k1 + i4 * 9 + 1;
         drawRect(p_175249_1_ / 2 - l1 / 2 - 1, k1 - 1, p_175249_1_ / 2 + l1 / 2 + 1, k1 + list2.size() * this.mc.fontRenderer.FONT_HEIGHT, Integer.MIN_VALUE);

         for(String s3 : list2) {
            int j5 = this.mc.fontRenderer.getStringWidth(s3);
            this.mc.fontRenderer.drawStringWithShadow(s3, (float)(p_175249_1_ / 2 - j5 / 2), (float)k1, -1);
            k1 += this.mc.fontRenderer.FONT_HEIGHT;
         }
      }

   }

   protected void drawPing(int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo p_175245_4_) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(ICONS);
      int i = 0;
      int j;
      if (p_175245_4_.getResponseTime() < 0) {
         j = 5;
      } else if (p_175245_4_.getResponseTime() < 150) {
         j = 0;
      } else if (p_175245_4_.getResponseTime() < 300) {
         j = 1;
      } else if (p_175245_4_.getResponseTime() < 600) {
         j = 2;
      } else if (p_175245_4_.getResponseTime() < 1000) {
         j = 3;
      } else {
         j = 4;
      }

      this.zLevel += 100.0F;
      this.drawTexturedModalRect(p_175245_2_ + p_175245_1_ - 11, p_175245_3_, 0, 176 + j * 8, 10, 8);
      this.zLevel -= 100.0F;
   }

   private void drawScoreboardValues(ScoreObjective p_175247_1_, int p_175247_2_, String p_175247_3_, int p_175247_4_, int p_175247_5_, NetworkPlayerInfo p_175247_6_) {
      int i = p_175247_1_.getScoreboard().getOrCreateScore(p_175247_3_, p_175247_1_).getScorePoints();
      if (p_175247_1_.func_199865_f() == ScoreCriteria.RenderType.HEARTS) {
         this.mc.getTextureManager().bindTexture(ICONS);
         long j = Util.milliTime();
         if (this.lastTimeOpened == p_175247_6_.getRenderVisibilityId()) {
            if (i < p_175247_6_.getLastHealth()) {
               p_175247_6_.setLastHealthTime(j);
               p_175247_6_.setHealthBlinkTime((long)(this.guiIngame.getTicks() + 20));
            } else if (i > p_175247_6_.getLastHealth()) {
               p_175247_6_.setLastHealthTime(j);
               p_175247_6_.setHealthBlinkTime((long)(this.guiIngame.getTicks() + 10));
            }
         }

         if (j - p_175247_6_.getLastHealthTime() > 1000L || this.lastTimeOpened != p_175247_6_.getRenderVisibilityId()) {
            p_175247_6_.setLastHealth(i);
            p_175247_6_.setDisplayHealth(i);
            p_175247_6_.setLastHealthTime(j);
         }

         p_175247_6_.setRenderVisibilityId(this.lastTimeOpened);
         p_175247_6_.setLastHealth(i);
         int k = MathHelper.ceil((float)Math.max(i, p_175247_6_.getDisplayHealth()) / 2.0F);
         int l = Math.max(MathHelper.ceil((float)(i / 2)), Math.max(MathHelper.ceil((float)(p_175247_6_.getDisplayHealth() / 2)), 10));
         boolean flag = p_175247_6_.getHealthBlinkTime() > (long)this.guiIngame.getTicks() && (p_175247_6_.getHealthBlinkTime() - (long)this.guiIngame.getTicks()) / 3L % 2L == 1L;
         if (k > 0) {
            float f = Math.min((float)(p_175247_5_ - p_175247_4_ - 4) / (float)l, 9.0F);
            if (f > 3.0F) {
               for(int i1 = k; i1 < l; ++i1) {
                  this.drawTexturedModalRect((float)p_175247_4_ + (float)i1 * f, (float)p_175247_2_, flag ? 25 : 16, 0, 9, 9);
               }

               for(int k1 = 0; k1 < k; ++k1) {
                  this.drawTexturedModalRect((float)p_175247_4_ + (float)k1 * f, (float)p_175247_2_, flag ? 25 : 16, 0, 9, 9);
                  if (flag) {
                     if (k1 * 2 + 1 < p_175247_6_.getDisplayHealth()) {
                        this.drawTexturedModalRect((float)p_175247_4_ + (float)k1 * f, (float)p_175247_2_, 70, 0, 9, 9);
                     }

                     if (k1 * 2 + 1 == p_175247_6_.getDisplayHealth()) {
                        this.drawTexturedModalRect((float)p_175247_4_ + (float)k1 * f, (float)p_175247_2_, 79, 0, 9, 9);
                     }
                  }

                  if (k1 * 2 + 1 < i) {
                     this.drawTexturedModalRect((float)p_175247_4_ + (float)k1 * f, (float)p_175247_2_, k1 >= 10 ? 160 : 52, 0, 9, 9);
                  }

                  if (k1 * 2 + 1 == i) {
                     this.drawTexturedModalRect((float)p_175247_4_ + (float)k1 * f, (float)p_175247_2_, k1 >= 10 ? 169 : 61, 0, 9, 9);
                  }
               }
            } else {
               float f1 = MathHelper.clamp((float)i / 20.0F, 0.0F, 1.0F);
               int j1 = (int)((1.0F - f1) * 255.0F) << 16 | (int)(f1 * 255.0F) << 8;
               String s = "" + (float)i / 2.0F;
               if (p_175247_5_ - this.mc.fontRenderer.getStringWidth(s + "hp") >= p_175247_4_) {
                  s = s + "hp";
               }

               this.mc.fontRenderer.drawStringWithShadow(s, (float)((p_175247_5_ + p_175247_4_) / 2 - this.mc.fontRenderer.getStringWidth(s) / 2), (float)p_175247_2_, j1);
            }
         }
      } else {
         String s1 = TextFormatting.YELLOW + "" + i;
         this.mc.fontRenderer.drawStringWithShadow(s1, (float)(p_175247_5_ - this.mc.fontRenderer.getStringWidth(s1)), (float)p_175247_2_, 16777215);
      }

   }

   public void setFooter(@Nullable ITextComponent p_175248_1_) {
      this.footer = p_175248_1_;
   }

   public void setHeader(@Nullable ITextComponent p_175244_1_) {
      this.header = p_175244_1_;
   }

   public void resetFooterHeader() {
      this.header = null;
      this.footer = null;
   }

   @OnlyIn(Dist.CLIENT)
   static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
      private PlayerComparator() {
      }

      public int compare(NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_) {
         ScorePlayerTeam scoreplayerteam = p_compare_1_.getPlayerTeam();
         ScorePlayerTeam scoreplayerteam1 = p_compare_2_.getPlayerTeam();
         return ComparisonChain.start().compareTrueFirst(p_compare_1_.getGameType() != GameType.SPECTATOR, p_compare_2_.getGameType() != GameType.SPECTATOR).compare(scoreplayerteam != null ? scoreplayerteam.getName() : "", scoreplayerteam1 != null ? scoreplayerteam1.getName() : "").compare(p_compare_1_.getGameProfile().getName(), p_compare_2_.getGameProfile().getName(), String::compareToIgnoreCase).result();
      }
   }
}
