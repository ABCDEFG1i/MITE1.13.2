package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerCape implements LayerRenderer<AbstractClientPlayer> {
   private final RenderPlayer playerRenderer;

   public LayerCape(RenderPlayer p_i46123_1_) {
      this.playerRenderer = p_i46123_1_;
   }

   public void doRenderLayer(AbstractClientPlayer p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
      if (p_177141_1_.hasPlayerInfo() && !p_177141_1_.isInvisible() && p_177141_1_.isWearing(EnumPlayerModelParts.CAPE) && p_177141_1_.getLocationCape() != null) {
         ItemStack itemstack = p_177141_1_.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
         if (itemstack.getItem() != Items.ELYTRA) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.playerRenderer.bindTexture(p_177141_1_.getLocationCape());
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0F, 0.0F, 0.125F);
            double d0 = p_177141_1_.prevChasingPosX + (p_177141_1_.chasingPosX - p_177141_1_.prevChasingPosX) * (double)p_177141_4_ - (p_177141_1_.prevPosX + (p_177141_1_.posX - p_177141_1_.prevPosX) * (double)p_177141_4_);
            double d1 = p_177141_1_.prevChasingPosY + (p_177141_1_.chasingPosY - p_177141_1_.prevChasingPosY) * (double)p_177141_4_ - (p_177141_1_.prevPosY + (p_177141_1_.posY - p_177141_1_.prevPosY) * (double)p_177141_4_);
            double d2 = p_177141_1_.prevChasingPosZ + (p_177141_1_.chasingPosZ - p_177141_1_.prevChasingPosZ) * (double)p_177141_4_ - (p_177141_1_.prevPosZ + (p_177141_1_.posZ - p_177141_1_.prevPosZ) * (double)p_177141_4_);
            float f = p_177141_1_.prevRenderYawOffset + (p_177141_1_.renderYawOffset - p_177141_1_.prevRenderYawOffset);
            double d3 = (double)MathHelper.sin(f * ((float)Math.PI / 180F));
            double d4 = (double)(-MathHelper.cos(f * ((float)Math.PI / 180F)));
            float f1 = (float)d1 * 10.0F;
            f1 = MathHelper.clamp(f1, -6.0F, 32.0F);
            float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
            f2 = MathHelper.clamp(f2, 0.0F, 150.0F);
            float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
            f3 = MathHelper.clamp(f3, -20.0F, 20.0F);
            if (f2 < 0.0F) {
               f2 = 0.0F;
            }

            float f4 = p_177141_1_.prevCameraYaw + (p_177141_1_.cameraYaw - p_177141_1_.prevCameraYaw) * p_177141_4_;
            f1 = f1 + MathHelper.sin((p_177141_1_.prevDistanceWalkedModified + (p_177141_1_.distanceWalkedModified - p_177141_1_.prevDistanceWalkedModified) * p_177141_4_) * 6.0F) * 32.0F * f4;
            if (p_177141_1_.isSneaking()) {
               f1 += 25.0F;
            }

            GlStateManager.rotatef(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            this.playerRenderer.getMainModel().renderCape(0.0625F);
            GlStateManager.popMatrix();
         }
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
