package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderXPOrb extends Render<EntityXPOrb> {
   private static final ResourceLocation EXPERIENCE_ORB_TEXTURES = new ResourceLocation("textures/entity/experience_orb.png");

   public RenderXPOrb(RenderManager p_i46178_1_) {
      super(p_i46178_1_);
      this.shadowSize = 0.15F;
      this.shadowOpaque = 0.75F;
   }

   public void doRender(EntityXPOrb p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      if (!this.renderOutlines) {
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)p_76986_2_, (float)p_76986_4_, (float)p_76986_6_);
         this.bindEntityTexture(p_76986_1_);
         RenderHelper.enableStandardItemLighting();
         int i = p_76986_1_.getTextureByXP();
         float f = (float)(i % 4 * 16 + 0) / 64.0F;
         float f1 = (float)(i % 4 * 16 + 16) / 64.0F;
         float f2 = (float)(i / 4 * 16 + 0) / 64.0F;
         float f3 = (float)(i / 4 * 16 + 16) / 64.0F;
         float f4 = 1.0F;
         float f5 = 0.5F;
         float f6 = 0.25F;
         int j = p_76986_1_.getBrightnessForRender();
         int k = j % 65536;
         int l = j / 65536;
         OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, (float)k, (float)l);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         float f7 = 255.0F;
         float f8 = ((float)p_76986_1_.xpColor + p_76986_9_) / 2.0F;
         int i1 = (int)((MathHelper.sin(f8 + 0.0F) + 1.0F) * 0.5F * 255.0F);
         int j1 = 255;
         int k1 = (int)((MathHelper.sin(f8 + 4.1887903F) + 1.0F) * 0.1F * 255.0F);
         GlStateManager.translatef(0.0F, 0.1F, 0.0F);
         GlStateManager.rotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
         float f9 = 0.3F;
         GlStateManager.scalef(0.3F, 0.3F, 0.3F);
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
         bufferbuilder.pos(-0.5D, -0.25D, 0.0D).tex((double)f, (double)f3).color(i1, 255, k1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
         bufferbuilder.pos(0.5D, -0.25D, 0.0D).tex((double)f1, (double)f3).color(i1, 255, k1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
         bufferbuilder.pos(0.5D, 0.75D, 0.0D).tex((double)f1, (double)f2).color(i1, 255, k1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
         bufferbuilder.pos(-0.5D, 0.75D, 0.0D).tex((double)f, (double)f2).color(i1, 255, k1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
         tessellator.draw();
         GlStateManager.disableBlend();
         GlStateManager.disableRescaleNormal();
         GlStateManager.popMatrix();
         super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
      }
   }

   protected ResourceLocation getEntityTexture(EntityXPOrb p_110775_1_) {
      return EXPERIENCE_ORB_TEXTURES;
   }
}
