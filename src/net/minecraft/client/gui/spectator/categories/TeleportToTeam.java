package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TeleportToTeam implements ISpectatorMenuView, ISpectatorMenuObject {
   private final List<ISpectatorMenuObject> items = Lists.newArrayList();

   public TeleportToTeam() {
      Minecraft minecraft = Minecraft.getInstance();

      for(ScorePlayerTeam scoreplayerteam : minecraft.world.getScoreboard().getTeams()) {
         this.items.add(new TeleportToTeam.TeamSelectionObject(scoreplayerteam));
      }

   }

   public List<ISpectatorMenuObject> getItems() {
      return this.items;
   }

   public ITextComponent getPrompt() {
      return new TextComponentTranslation("spectatorMenu.team_teleport.prompt");
   }

   public void selectItem(SpectatorMenu p_178661_1_) {
      p_178661_1_.selectCategory(this);
   }

   public ITextComponent getSpectatorName() {
      return new TextComponentTranslation("spectatorMenu.team_teleport");
   }

   public void renderIcon(float p_178663_1_, int p_178663_2_) {
      Minecraft.getInstance().getTextureManager().bindTexture(GuiSpectator.SPECTATOR_WIDGETS);
      Gui.drawModalRectWithCustomSizedTexture(0, 0, 16.0F, 0.0F, 16, 16, 256.0F, 256.0F);
   }

   public boolean isEnabled() {
      for(ISpectatorMenuObject ispectatormenuobject : this.items) {
         if (ispectatormenuobject.isEnabled()) {
            return true;
         }
      }

      return false;
   }

   @OnlyIn(Dist.CLIENT)
   class TeamSelectionObject implements ISpectatorMenuObject {
      private final ScorePlayerTeam team;
      private final ResourceLocation location;
      private final List<NetworkPlayerInfo> players;

      public TeamSelectionObject(ScorePlayerTeam p_i45492_2_) {
         this.team = p_i45492_2_;
         this.players = Lists.newArrayList();

         for(String s : p_i45492_2_.getMembershipCollection()) {
            NetworkPlayerInfo networkplayerinfo = Minecraft.getInstance().getConnection().getPlayerInfo(s);
            if (networkplayerinfo != null) {
               this.players.add(networkplayerinfo);
            }
         }

         if (this.players.isEmpty()) {
            this.location = DefaultPlayerSkin.getDefaultSkinLegacy();
         } else {
            String s1 = this.players.get((new Random()).nextInt(this.players.size())).getGameProfile().getName();
            this.location = AbstractClientPlayer.getLocationSkin(s1);
            AbstractClientPlayer.getDownloadImageSkin(this.location, s1);
         }

      }

      public void selectItem(SpectatorMenu p_178661_1_) {
         p_178661_1_.selectCategory(new TeleportToPlayer(this.players));
      }

      public ITextComponent getSpectatorName() {
         return this.team.getDisplayName();
      }

      public void renderIcon(float p_178663_1_, int p_178663_2_) {
         Integer integer = this.team.getColor().getColor();
         if (integer != null) {
            float f = (float)(integer >> 16 & 255) / 255.0F;
            float f1 = (float)(integer >> 8 & 255) / 255.0F;
            float f2 = (float)(integer & 255) / 255.0F;
            Gui.drawRect(1, 1, 15, 15, MathHelper.rgb(f * p_178663_1_, f1 * p_178663_1_, f2 * p_178663_1_) | p_178663_2_ << 24);
         }

         Minecraft.getInstance().getTextureManager().bindTexture(this.location);
         GlStateManager.color4f(p_178663_1_, p_178663_1_, p_178663_1_, (float)p_178663_2_ / 255.0F);
         Gui.drawScaledCustomSizeModalRect(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
         Gui.drawScaledCustomSizeModalRect(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
      }

      public boolean isEnabled() {
         return !this.players.isEmpty();
      }
   }
}
