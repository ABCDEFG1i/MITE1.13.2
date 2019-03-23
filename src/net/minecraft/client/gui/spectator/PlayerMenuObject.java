package net.minecraft.client.gui.spectator;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerMenuObject implements ISpectatorMenuObject {
   private final GameProfile profile;
   private final ResourceLocation resourceLocation;

   public PlayerMenuObject(GameProfile p_i45498_1_) {
      this.profile = p_i45498_1_;
      Minecraft minecraft = Minecraft.getInstance();
      Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(p_i45498_1_);
      if (map.containsKey(Type.SKIN)) {
         this.resourceLocation = minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN);
      } else {
         this.resourceLocation = DefaultPlayerSkin.getDefaultSkin(EntityPlayer.getUUID(p_i45498_1_));
      }

   }

   public void selectItem(SpectatorMenu p_178661_1_) {
      Minecraft.getInstance().getConnection().sendPacket(new CPacketSpectate(this.profile.getId()));
   }

   public ITextComponent getSpectatorName() {
      return new TextComponentString(this.profile.getName());
   }

   public void renderIcon(float p_178663_1_, int p_178663_2_) {
      Minecraft.getInstance().getTextureManager().bindTexture(this.resourceLocation);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, (float)p_178663_2_ / 255.0F);
      Gui.drawScaledCustomSizeModalRect(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
      Gui.drawScaledCustomSizeModalRect(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
   }

   public boolean isEnabled() {
      return true;
   }
}
