package net.minecraft.client.gui.fonts.providers;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.fonts.IGlyphInfo;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class TextureGlyphProvider implements IGlyphProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final NativeImage texture;
   private final Char2ObjectMap<TextureGlyphProvider.GlyphInfo> glyphInfos;

   public TextureGlyphProvider(NativeImage p_i49767_1_, Char2ObjectMap<TextureGlyphProvider.GlyphInfo> p_i49767_2_) {
      this.texture = p_i49767_1_;
      this.glyphInfos = p_i49767_2_;
   }

   public void close() {
      this.texture.close();
   }

   @Nullable
   public IGlyphInfo func_212248_a(char p_212248_1_) {
      return this.glyphInfos.get(p_212248_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IGlyphProviderFactory {
      private final ResourceLocation file;
      private final List<String> chars;
      private final int height;
      private final int ascent;

      public Factory(ResourceLocation p_i49750_1_, int p_i49750_2_, int p_i49750_3_, List<String> p_i49750_4_) {
         this.file = new ResourceLocation(p_i49750_1_.getNamespace(), "textures/" + p_i49750_1_.getPath());
         this.chars = p_i49750_4_;
         this.height = p_i49750_2_;
         this.ascent = p_i49750_3_;
      }

      public static TextureGlyphProvider.Factory deserialize(JsonObject p_211633_0_) {
         int i = JsonUtils.getInt(p_211633_0_, "height", 8);
         int j = JsonUtils.getInt(p_211633_0_, "ascent");
         if (j > i) {
            throw new JsonParseException("Ascent " + j + " higher than height " + i);
         } else {
            List<String> list = Lists.newArrayList();
            JsonArray jsonarray = JsonUtils.getJsonArray(p_211633_0_, "chars");

            for(int k = 0; k < jsonarray.size(); ++k) {
               String s = JsonUtils.getString(jsonarray.get(k), "chars[" + k + "]");
               if (k > 0) {
                  int l = s.length();
                  int i1 = list.get(0).length();
                  if (l != i1) {
                     throw new JsonParseException("Elements of chars have to be the same lenght (found: " + l + ", expected: " + i1 + "), pad with space or \\u0000");
                  }
               }

               list.add(s);
            }

            if (!list.isEmpty() && !list.get(0).isEmpty()) {
               return new TextureGlyphProvider.Factory(new ResourceLocation(JsonUtils.getString(p_211633_0_, "file")), i, j, list);
            } else {
               throw new JsonParseException("Expected to find data in chars, found none.");
            }
         }
      }

      @Nullable
      public IGlyphProvider create(IResourceManager p_211246_1_) {
         try (IResource iresource = p_211246_1_.getResource(this.file)) {
            NativeImage nativeimage = NativeImage.read(NativeImage.PixelFormat.RGBA, iresource.getInputStream());
            int i = nativeimage.getWidth();
            int j = nativeimage.getHeight();
            int k = i / this.chars.get(0).length();
            int l = j / this.chars.size();
            float f = (float)this.height / (float)l;
            Char2ObjectMap<TextureGlyphProvider.GlyphInfo> char2objectmap = new Char2ObjectOpenHashMap<>();

            for(int i1 = 0; i1 < this.chars.size(); ++i1) {
               String s = this.chars.get(i1);

               for(int j1 = 0; j1 < s.length(); ++j1) {
                  char c0 = s.charAt(j1);
                  if (c0 != 0 && c0 != ' ') {
                     int k1 = this.func_211632_a(nativeimage, k, l, j1, i1);
                     char2objectmap.put(c0, new TextureGlyphProvider.GlyphInfo(f, nativeimage, j1 * k, i1 * l, k, l, (int)(0.5D + (double)((float)k1 * f)) + 1, this.ascent));
                  }
               }
            }

            TextureGlyphProvider textureglyphprovider = new TextureGlyphProvider(nativeimage, char2objectmap);
            return textureglyphprovider;
         } catch (IOException ioexception) {
            throw new RuntimeException(ioexception.getMessage());
         }
      }

      private int func_211632_a(NativeImage p_211632_1_, int p_211632_2_, int p_211632_3_, int p_211632_4_, int p_211632_5_) {
         int i;
         for(i = p_211632_2_ - 1; i >= 0; --i) {
            int j = p_211632_4_ * p_211632_2_ + i;

            for(int k = 0; k < p_211632_3_; ++k) {
               int l = p_211632_5_ * p_211632_3_ + k;
               if (p_211632_1_.getPixelLuminanceOrAlpha(j, l) != 0) {
                  return i + 1;
               }
            }
         }

         return i + 1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static final class GlyphInfo implements IGlyphInfo {
      private final float field_211582_a;
      private final NativeImage texture;
      private final int unpackSkipPixels;
      private final int unpackSkipRows;
      private final int width;
      private final int height;
      private final int advanceWidth;
      private final int ascent;

      private GlyphInfo(float p_i49748_1_, NativeImage p_i49748_2_, int p_i49748_3_, int p_i49748_4_, int p_i49748_5_, int p_i49748_6_, int p_i49748_7_, int p_i49748_8_) {
         this.field_211582_a = p_i49748_1_;
         this.texture = p_i49748_2_;
         this.unpackSkipPixels = p_i49748_3_;
         this.unpackSkipRows = p_i49748_4_;
         this.width = p_i49748_5_;
         this.height = p_i49748_6_;
         this.advanceWidth = p_i49748_7_;
         this.ascent = p_i49748_8_;
      }

      public float getOversample() {
         return 1.0F / this.field_211582_a;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public float getAdvance() {
         return (float)this.advanceWidth;
      }

      public float getBearingY() {
         return IGlyphInfo.super.getBearingY() + 7.0F - (float)this.ascent;
      }

      public void uploadGlyph(int p_211573_1_, int p_211573_2_) {
         this.texture.uploadTextureSub(0, p_211573_1_, p_211573_2_, this.unpackSkipPixels, this.unpackSkipRows, this.width, this.height, false);
      }

      public boolean isColored() {
         return this.texture.getFormat().getPixelSize() > 1;
      }
   }
}
