package net.minecraft.client.entity;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.ThreadDownloadImageData;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractClientPlayer extends EntityPlayer {
   private NetworkPlayerInfo playerInfo;
   public float rotateElytraX;
   public float rotateElytraY;
   public float rotateElytraZ;

   public AbstractClientPlayer(World p_i45074_1_, GameProfile p_i45074_2_) {
      super(p_i45074_1_, p_i45074_2_);
   }

   public boolean isSpectator() {
      NetworkPlayerInfo networkplayerinfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getGameProfile().getId());
      return networkplayerinfo != null && networkplayerinfo.getGameType() == GameType.SPECTATOR;
   }

   public boolean isCreative() {
      NetworkPlayerInfo networkplayerinfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getGameProfile().getId());
      return networkplayerinfo != null && networkplayerinfo.getGameType() == GameType.CREATIVE;
   }

   public boolean hasPlayerInfo() {
      return this.getPlayerInfo() != null;
   }

   @Nullable
   protected NetworkPlayerInfo getPlayerInfo() {
      if (this.playerInfo == null) {
         this.playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getUniqueID());
      }

      return this.playerInfo;
   }

   public boolean hasSkin() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo != null && networkplayerinfo.hasLocationSkin();
   }

   public ResourceLocation getLocationSkin() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkplayerinfo.getLocationSkin();
   }

   @Nullable
   public ResourceLocation getLocationCape() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? null : networkplayerinfo.getLocationCape();
   }

   public boolean isPlayerInfoSet() {
      return this.getPlayerInfo() != null;
   }

   @Nullable
   public ResourceLocation getLocationElytra() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? null : networkplayerinfo.getLocationElytra();
   }

   public static ThreadDownloadImageData getDownloadImageSkin(ResourceLocation p_110304_0_, String p_110304_1_) {
      TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
      ITextureObject itextureobject = texturemanager.getTexture(p_110304_0_);
      if (itextureobject == null) {
         itextureobject = new ThreadDownloadImageData(
                 null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(p_110304_1_)), DefaultPlayerSkin.getDefaultSkin(getOfflineUUID(p_110304_1_)), new ImageBufferDownload());
         texturemanager.loadTexture(p_110304_0_, itextureobject);
      }

      return (ThreadDownloadImageData)itextureobject;
   }

   public static ResourceLocation getLocationSkin(String p_110311_0_) {
      return new ResourceLocation("skins/" + Hashing.sha1().hashUnencodedChars(StringUtils.stripControlCodes(p_110311_0_)));
   }

   public String getSkinType() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : networkplayerinfo.getSkinType();
   }

   public float getFovModifier() {
      float f = 1.0F;
      if (this.capabilities.isFlying) {
         f *= 1.1F;
      }

      IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      f = (float)((double)f * ((iattributeinstance.getAttributeValue() / (double)this.capabilities.getWalkSpeed() + 1.0D) / 2.0D));
      if (this.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
         f = 1.0F;
      }

      if (this.isHandActive() && this.getActiveItemStack().getItem() == Items.BOW) {
         int i = this.getItemInUseMaxCount();
         float f1 = (float)i / 20.0F;
         if (f1 > 1.0F) {
            f1 = 1.0F;
         } else {
            f1 = f1 * f1;
         }

         f *= 1.0F - f1 * 0.15F;
      }

      return f;
   }
}
