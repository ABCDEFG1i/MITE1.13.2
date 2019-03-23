package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTNTPrimed extends Render<EntityTNTPrimed> {
   public RenderTNTPrimed(RenderManager p_i46134_1_) {
      super(p_i46134_1_);
      this.shadowSize = 0.5F;
   }

   public void doRender(EntityTNTPrimed p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)p_76986_2_, (float)p_76986_4_ + 0.5F, (float)p_76986_6_);
      if ((float)p_76986_1_.getFuse() - p_76986_9_ + 1.0F < 10.0F) {
         float f = 1.0F - ((float)p_76986_1_.getFuse() - p_76986_9_ + 1.0F) / 10.0F;
         f = MathHelper.clamp(f, 0.0F, 1.0F);
         f = f * f;
         f = f * f;
         float f1 = 1.0F + f * 0.3F;
         GlStateManager.scalef(f1, f1, f1);
      }

      float f2 = (1.0F - ((float)p_76986_1_.getFuse() - p_76986_9_ + 1.0F) / 100.0F) * 0.8F;
      this.bindEntityTexture(p_76986_1_);
      GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
      blockrendererdispatcher.renderBlockBrightness(Blocks.TNT.getDefaultState(), p_76986_1_.getBrightness());
      GlStateManager.translatef(0.0F, 0.0F, 1.0F);
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(p_76986_1_));
         blockrendererdispatcher.renderBlockBrightness(Blocks.TNT.getDefaultState(), 1.0F);
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      } else if (p_76986_1_.getFuse() / 5 % 2 == 0) {
         GlStateManager.disableTexture2D();
         GlStateManager.disableLighting();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, f2);
         GlStateManager.polygonOffset(-3.0F, -3.0F);
         GlStateManager.enablePolygonOffset();
         blockrendererdispatcher.renderBlockBrightness(Blocks.TNT.getDefaultState(), 1.0F);
         GlStateManager.polygonOffset(0.0F, 0.0F);
         GlStateManager.disablePolygonOffset();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.enableLighting();
         GlStateManager.enableTexture2D();
      }

      GlStateManager.popMatrix();
      super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   protected ResourceLocation getEntityTexture(EntityTNTPrimed p_110775_1_) {
      return TextureMap.LOCATION_BLOCKS_TEXTURE;
   }
}
