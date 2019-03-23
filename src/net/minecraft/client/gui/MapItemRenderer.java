package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MapItemRenderer implements AutoCloseable {
   private static final ResourceLocation TEXTURE_MAP_ICONS = new ResourceLocation("textures/map/map_icons.png");
   private final TextureManager textureManager;
   private final Map<String, MapItemRenderer.Instance> loadedMaps = Maps.newHashMap();

   public MapItemRenderer(TextureManager p_i45009_1_) {
      this.textureManager = p_i45009_1_;
   }

   public void updateMapTexture(MapData p_148246_1_) {
      this.getMapRendererInstance(p_148246_1_).updateMapTexture();
   }

   public void renderMap(MapData p_148250_1_, boolean p_148250_2_) {
      this.getMapRendererInstance(p_148250_1_).render(p_148250_2_);
   }

   private MapItemRenderer.Instance getMapRendererInstance(MapData p_148248_1_) {
      MapItemRenderer.Instance mapitemrenderer$instance = this.loadedMaps.get(p_148248_1_.getName());
      if (mapitemrenderer$instance == null) {
         mapitemrenderer$instance = new MapItemRenderer.Instance(p_148248_1_);
         this.loadedMaps.put(p_148248_1_.getName(), mapitemrenderer$instance);
      }

      return mapitemrenderer$instance;
   }

   @Nullable
   public MapItemRenderer.Instance getMapInstanceIfExists(String p_191205_1_) {
      return this.loadedMaps.get(p_191205_1_);
   }

   public void clearLoadedMaps() {
      for(MapItemRenderer.Instance mapitemrenderer$instance : this.loadedMaps.values()) {
         mapitemrenderer$instance.close();
      }

      this.loadedMaps.clear();
   }

   @Nullable
   public MapData getData(@Nullable MapItemRenderer.Instance p_191207_1_) {
      return p_191207_1_ != null ? p_191207_1_.mapData : null;
   }

   public void close() {
      this.clearLoadedMaps();
   }

   @OnlyIn(Dist.CLIENT)
   class Instance implements AutoCloseable {
      private final MapData mapData;
      private final DynamicTexture mapTexture;
      private final ResourceLocation location;

      private Instance(MapData p_i45007_2_) {
         this.mapData = p_i45007_2_;
         this.mapTexture = new DynamicTexture(128, 128, true);
         this.location = MapItemRenderer.this.textureManager.getDynamicTextureLocation("map/" + p_i45007_2_.getName(), this.mapTexture);
      }

      private void updateMapTexture() {
         for(int i = 0; i < 128; ++i) {
            for(int j = 0; j < 128; ++j) {
               int k = j + i * 128;
               int l = this.mapData.colors[k] & 255;
               if (l / 4 == 0) {
                  this.mapTexture.func_195414_e().setPixelRGBA(j, i, (k + k / 128 & 1) * 8 + 16 << 24);
               } else {
                  this.mapTexture.func_195414_e().setPixelRGBA(j, i, MaterialColor.COLORS[l / 4].getColorRGB(l & 3));
               }
            }
         }

         this.mapTexture.updateDynamicTexture();
      }

      private void render(boolean p_148237_1_) {
         int i = 0;
         int j = 0;
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         float f = 0.0F;
         MapItemRenderer.this.textureManager.bindTexture(this.location);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         GlStateManager.disableAlphaTest();
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
         bufferbuilder.pos(0.0D, 128.0D, (double)-0.01F).tex(0.0D, 1.0D).endVertex();
         bufferbuilder.pos(128.0D, 128.0D, (double)-0.01F).tex(1.0D, 1.0D).endVertex();
         bufferbuilder.pos(128.0D, 0.0D, (double)-0.01F).tex(1.0D, 0.0D).endVertex();
         bufferbuilder.pos(0.0D, 0.0D, (double)-0.01F).tex(0.0D, 0.0D).endVertex();
         tessellator.draw();
         GlStateManager.enableAlphaTest();
         GlStateManager.disableBlend();
         int k = 0;

         for(MapDecoration mapdecoration : this.mapData.mapDecorations.values()) {
            if (!p_148237_1_ || mapdecoration.renderOnFrame()) {
               MapItemRenderer.this.textureManager.bindTexture(MapItemRenderer.TEXTURE_MAP_ICONS);
               GlStateManager.pushMatrix();
               GlStateManager.translatef(0.0F + (float)mapdecoration.getX() / 2.0F + 64.0F, 0.0F + (float)mapdecoration.getY() / 2.0F + 64.0F, -0.02F);
               GlStateManager.rotatef((float)(mapdecoration.getRotation() * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scalef(4.0F, 4.0F, 3.0F);
               GlStateManager.translatef(-0.125F, 0.125F, 0.0F);
               byte b0 = mapdecoration.getImage();
               float f1 = (float)(b0 % 16 + 0) / 16.0F;
               float f2 = (float)(b0 / 16 + 0) / 16.0F;
               float f3 = (float)(b0 % 16 + 1) / 16.0F;
               float f4 = (float)(b0 / 16 + 1) / 16.0F;
               bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
               GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               float f5 = -0.001F;
               bufferbuilder.pos(-1.0D, 1.0D, (double)((float)k * -0.001F)).tex((double)f1, (double)f2).endVertex();
               bufferbuilder.pos(1.0D, 1.0D, (double)((float)k * -0.001F)).tex((double)f3, (double)f2).endVertex();
               bufferbuilder.pos(1.0D, -1.0D, (double)((float)k * -0.001F)).tex((double)f3, (double)f4).endVertex();
               bufferbuilder.pos(-1.0D, -1.0D, (double)((float)k * -0.001F)).tex((double)f1, (double)f4).endVertex();
               tessellator.draw();
               GlStateManager.popMatrix();
               if (mapdecoration.func_204309_g() != null) {
                  FontRenderer fontrenderer = Minecraft.getInstance().fontRenderer;
                  String s = mapdecoration.func_204309_g().getFormattedText();
                  float f6 = (float)fontrenderer.getStringWidth(s);
                  float f7 = MathHelper.clamp(25.0F / f6, 0.0F, 6.0F / (float)fontrenderer.FONT_HEIGHT);
                  GlStateManager.pushMatrix();
                  GlStateManager.translatef(0.0F + (float)mapdecoration.getX() / 2.0F + 64.0F - f6 * f7 / 2.0F, 0.0F + (float)mapdecoration.getY() / 2.0F + 64.0F + 4.0F, -0.025F);
                  GlStateManager.scalef(f7, f7, 1.0F);
                  GuiIngame.drawRect(-1, -1, (int)f6, fontrenderer.FONT_HEIGHT - 1, Integer.MIN_VALUE);
                  GlStateManager.translatef(0.0F, 0.0F, -0.1F);
                  fontrenderer.drawString(s, 0.0F, 0.0F, -1);
                  GlStateManager.popMatrix();
               }

               ++k;
            }
         }

         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 0.0F, -0.04F);
         GlStateManager.scalef(1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }

      public void close() {
         this.mapTexture.close();
      }
   }
}
