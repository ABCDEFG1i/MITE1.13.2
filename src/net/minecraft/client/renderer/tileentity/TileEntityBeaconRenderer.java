package net.minecraft.client.renderer.tileentity;

import java.util.List;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityBeaconRenderer extends TileEntityRenderer<TileEntityBeacon> {
   private static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation("textures/entity/beacon_beam.png");

   public void render(TileEntityBeacon p_199341_1_, double p_199341_2_, double p_199341_4_, double p_199341_6_, float p_199341_8_, int p_199341_9_) {
      this.func_188206_a(p_199341_2_, p_199341_4_, p_199341_6_, (double)p_199341_8_, (double)p_199341_1_.shouldBeamRender(), p_199341_1_.getBeamSegments(), p_199341_1_.getWorld().getTotalWorldTime());
   }

   private void func_188206_a(double p_188206_1_, double p_188206_3_, double p_188206_5_, double p_188206_7_, double p_188206_9_, List<TileEntityBeacon.BeamSegment> p_188206_11_, long p_188206_12_) {
      GlStateManager.alphaFunc(516, 0.1F);
      this.bindTexture(TEXTURE_BEACON_BEAM);
      if (p_188206_9_ > 0.0D) {
         GlStateManager.disableFog();
         int i = 0;

         for(int j = 0; j < p_188206_11_.size(); ++j) {
            TileEntityBeacon.BeamSegment tileentitybeacon$beamsegment = p_188206_11_.get(j);
            func_188204_a(p_188206_1_, p_188206_3_, p_188206_5_, p_188206_7_, p_188206_9_, p_188206_12_, i, tileentitybeacon$beamsegment.getHeight(), tileentitybeacon$beamsegment.getColors());
            i += tileentitybeacon$beamsegment.getHeight();
         }

         GlStateManager.enableFog();
      }

   }

   private static void func_188204_a(double p_188204_0_, double p_188204_2_, double p_188204_4_, double p_188204_6_, double p_188204_8_, long p_188204_10_, int p_188204_12_, int p_188204_13_, float[] p_188204_14_) {
      func_188205_a(p_188204_0_, p_188204_2_, p_188204_4_, p_188204_6_, p_188204_8_, p_188204_10_, p_188204_12_, p_188204_13_, p_188204_14_, 0.2D, 0.25D);
   }

   public static void func_188205_a(double p_188205_0_, double p_188205_2_, double p_188205_4_, double p_188205_6_, double p_188205_8_, long p_188205_10_, int p_188205_12_, int p_188205_13_, float[] p_188205_14_, double p_188205_15_, double p_188205_17_) {
      int i = p_188205_12_ + p_188205_13_;
      GlStateManager.texParameteri(3553, 10242, 10497);
      GlStateManager.texParameteri(3553, 10243, 10497);
      GlStateManager.disableLighting();
      GlStateManager.disableCull();
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.pushMatrix();
      GlStateManager.translated(p_188205_0_ + 0.5D, p_188205_2_, p_188205_4_ + 0.5D);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      double d0 = (double)Math.floorMod(p_188205_10_, 40L) + p_188205_6_;
      double d1 = p_188205_13_ < 0 ? d0 : -d0;
      double d2 = MathHelper.frac(d1 * 0.2D - (double)MathHelper.floor(d1 * 0.1D));
      float f = p_188205_14_[0];
      float f1 = p_188205_14_[1];
      float f2 = p_188205_14_[2];
      GlStateManager.pushMatrix();
      GlStateManager.func_212477_a(d0 * 2.25D - 45.0D, 0.0D, 1.0D, 0.0D);
      double d3 = 0.0D;
      double d5 = 0.0D;
      double d6 = -p_188205_15_;
      double d7 = 0.0D;
      double d8 = 0.0D;
      double d9 = -p_188205_15_;
      double d10 = 0.0D;
      double d11 = 1.0D;
      double d12 = -1.0D + d2;
      double d13 = (double)p_188205_13_ * p_188205_8_ * (0.5D / p_188205_15_) + d12;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      bufferbuilder.pos(0.0D, (double)i, p_188205_15_).tex(1.0D, d13).color(f, f1, f2, 1.0F).endVertex();
      bufferbuilder.pos(0.0D, (double)p_188205_12_, p_188205_15_).tex(1.0D, d12).color(f, f1, f2, 1.0F).endVertex();
      bufferbuilder.pos(p_188205_15_, (double)p_188205_12_, 0.0D).tex(0.0D, d12).color(f, f1, f2, 1.0F).endVertex();
      bufferbuilder.pos(p_188205_15_, (double)i, 0.0D).tex(0.0D, d13).color(f, f1, f2, 1.0F).endVertex();
      bufferbuilder.pos(0.0D, (double)i, d9).tex(1.0D, d13).color(f, f1, f2, 1.0F).endVertex();
      bufferbuilder.pos(0.0D, (double)p_188205_12_, d9).tex(1.0D, d12).color(f, f1, f2, 1.0F).endVertex();
      bufferbuilder.pos(d6, (double)p_188205_12_, 0.0D).tex(0.0D, d12).color(f, f1, f2, 1.0F).endVertex();
      bufferbuilder.pos(d6, (double)i, 0.0D).tex(0.0D, d13).color(f, f1, f2, 1.0F).endVertex();
      bufferbuilder.pos(p_188205_15_, (double)i, 0.0D).tex(1.0D, d13).color(f, f1, f2, 1.0F).endVertex();
      bufferbuilder.pos(p_188205_15_, (double)p_188205_12_, 0.0D).tex(1.0D, d12).color(f, f1, f2, 1.0F).endVertex();
      bufferbuilder.pos(0.0D, (double)p_188205_12_, d9).tex(0.0D, d12).color(f, f1, f2, 1.0F).endVertex();
      bufferbuilder.pos(0.0D, (double)i, d9).tex(0.0D, d13).color(f, f1, f2, 1.0F).endVertex();
      bufferbuilder.pos(d6, (double)i, 0.0D).tex(1.0D, d13).color(f, f1, f2, 1.0F).endVertex();
      bufferbuilder.pos(d6, (double)p_188205_12_, 0.0D).tex(1.0D, d12).color(f, f1, f2, 1.0F).endVertex();
      bufferbuilder.pos(0.0D, (double)p_188205_12_, p_188205_15_).tex(0.0D, d12).color(f, f1, f2, 1.0F).endVertex();
      bufferbuilder.pos(0.0D, (double)i, p_188205_15_).tex(0.0D, d13).color(f, f1, f2, 1.0F).endVertex();
      tessellator.draw();
      GlStateManager.popMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.depthMask(false);
      d3 = -p_188205_17_;
      double d4 = -p_188205_17_;
      d5 = -p_188205_17_;
      d6 = -p_188205_17_;
      d10 = 0.0D;
      d11 = 1.0D;
      d12 = -1.0D + d2;
      d13 = (double)p_188205_13_ * p_188205_8_ + d12;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      bufferbuilder.pos(d3, (double)i, d4).tex(1.0D, d13).color(f, f1, f2, 0.125F).endVertex();
      bufferbuilder.pos(d3, (double)p_188205_12_, d4).tex(1.0D, d12).color(f, f1, f2, 0.125F).endVertex();
      bufferbuilder.pos(p_188205_17_, (double)p_188205_12_, d5).tex(0.0D, d12).color(f, f1, f2, 0.125F).endVertex();
      bufferbuilder.pos(p_188205_17_, (double)i, d5).tex(0.0D, d13).color(f, f1, f2, 0.125F).endVertex();
      bufferbuilder.pos(p_188205_17_, (double)i, p_188205_17_).tex(1.0D, d13).color(f, f1, f2, 0.125F).endVertex();
      bufferbuilder.pos(p_188205_17_, (double)p_188205_12_, p_188205_17_).tex(1.0D, d12).color(f, f1, f2, 0.125F).endVertex();
      bufferbuilder.pos(d6, (double)p_188205_12_, p_188205_17_).tex(0.0D, d12).color(f, f1, f2, 0.125F).endVertex();
      bufferbuilder.pos(d6, (double)i, p_188205_17_).tex(0.0D, d13).color(f, f1, f2, 0.125F).endVertex();
      bufferbuilder.pos(p_188205_17_, (double)i, d5).tex(1.0D, d13).color(f, f1, f2, 0.125F).endVertex();
      bufferbuilder.pos(p_188205_17_, (double)p_188205_12_, d5).tex(1.0D, d12).color(f, f1, f2, 0.125F).endVertex();
      bufferbuilder.pos(p_188205_17_, (double)p_188205_12_, p_188205_17_).tex(0.0D, d12).color(f, f1, f2, 0.125F).endVertex();
      bufferbuilder.pos(p_188205_17_, (double)i, p_188205_17_).tex(0.0D, d13).color(f, f1, f2, 0.125F).endVertex();
      bufferbuilder.pos(d6, (double)i, p_188205_17_).tex(1.0D, d13).color(f, f1, f2, 0.125F).endVertex();
      bufferbuilder.pos(d6, (double)p_188205_12_, p_188205_17_).tex(1.0D, d12).color(f, f1, f2, 0.125F).endVertex();
      bufferbuilder.pos(d3, (double)p_188205_12_, d4).tex(0.0D, d12).color(f, f1, f2, 0.125F).endVertex();
      bufferbuilder.pos(d3, (double)i, d4).tex(0.0D, d13).color(f, f1, f2, 0.125F).endVertex();
      tessellator.draw();
      GlStateManager.popMatrix();
      GlStateManager.enableLighting();
      GlStateManager.enableTexture2D();
      GlStateManager.depthMask(true);
   }

   public boolean isGlobalRenderer(TileEntityBeacon p_188185_1_) {
      return true;
   }
}
