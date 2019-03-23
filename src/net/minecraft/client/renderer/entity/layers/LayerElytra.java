package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.model.ModelElytra;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerElytra implements LayerRenderer<EntityLivingBase> {
   private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");
   protected final RenderLivingBase<?> renderPlayer;
   private final ModelElytra modelElytra = new ModelElytra();

   public LayerElytra(RenderLivingBase<?> p_i47185_1_) {
      this.renderPlayer = p_i47185_1_;
   }

   public void doRenderLayer(EntityLivingBase p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
      ItemStack itemstack = p_177141_1_.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
      if (itemstack.getItem() == Items.ELYTRA) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         if (p_177141_1_ instanceof AbstractClientPlayer) {
            AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer)p_177141_1_;
            if (abstractclientplayer.isPlayerInfoSet() && abstractclientplayer.getLocationElytra() != null) {
               this.renderPlayer.bindTexture(abstractclientplayer.getLocationElytra());
            } else if (abstractclientplayer.hasPlayerInfo() && abstractclientplayer.getLocationCape() != null && abstractclientplayer.isWearing(EnumPlayerModelParts.CAPE)) {
               this.renderPlayer.bindTexture(abstractclientplayer.getLocationCape());
            } else {
               this.renderPlayer.bindTexture(TEXTURE_ELYTRA);
            }
         } else {
            this.renderPlayer.bindTexture(TEXTURE_ELYTRA);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 0.0F, 0.125F);
         this.modelElytra.setRotationAngles(p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_, p_177141_1_);
         this.modelElytra.render(p_177141_1_, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_);
         if (itemstack.isEnchanted()) {
            LayerArmorBase.renderEnchantedGlint(this.renderPlayer, p_177141_1_, this.modelElytra, p_177141_2_, p_177141_3_, p_177141_4_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_);
         }

         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
