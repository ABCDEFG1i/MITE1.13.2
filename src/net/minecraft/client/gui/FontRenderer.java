package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FontRenderer implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   public int FONT_HEIGHT = 9;
   public Random fontRandom = new Random();
   private final TextureManager textureManager;
   private final Font font;
   private boolean bidiFlag;

   public FontRenderer(TextureManager p_i49744_1_, Font p_i49744_2_) {
      this.textureManager = p_i49744_1_;
      this.font = p_i49744_2_;
   }

   public void setGlyphProviders(List<IGlyphProvider> p_211568_1_) {
      this.font.setGlyphProviders(p_211568_1_);
   }

   public void close() {
      this.font.close();
   }

   public int drawStringWithShadow(String p_175063_1_, float p_175063_2_, float p_175063_3_, int p_175063_4_) {
      GlStateManager.enableAlphaTest();
      return this.renderString(p_175063_1_, p_175063_2_, p_175063_3_, p_175063_4_, true);
   }

   public int drawString(String p_211126_1_, float p_211126_2_, float p_211126_3_, int p_211126_4_) {
      GlStateManager.enableAlphaTest();
      return this.renderString(p_211126_1_, p_211126_2_, p_211126_3_, p_211126_4_, false);
   }

   private String bidiReorder(String p_147647_1_) {
      try {
         Bidi bidi = new Bidi((new ArabicShaping(8)).shape(p_147647_1_), 127);
         bidi.setReorderingMode(0);
         return bidi.writeReordered(2);
      } catch (ArabicShapingException var3) {
         return p_147647_1_;
      }
   }

   private int renderString(String p_180455_1_, float p_180455_2_, float p_180455_3_, int p_180455_4_, boolean p_180455_5_) {
      if (p_180455_1_ == null) {
         return 0;
      } else {
         if (this.bidiFlag) {
            p_180455_1_ = this.bidiReorder(p_180455_1_);
         }

         if ((p_180455_4_ & -67108864) == 0) {
            p_180455_4_ |= -16777216;
         }

         if (p_180455_5_) {
            this.renderStringAtPos(p_180455_1_, p_180455_2_, p_180455_3_, p_180455_4_, true);
         }

         p_180455_2_ = this.renderStringAtPos(p_180455_1_, p_180455_2_, p_180455_3_, p_180455_4_, false);
         return (int)p_180455_2_ + (p_180455_5_ ? 1 : 0);
      }
   }

   private float renderStringAtPos(String p_211843_1_, float p_211843_2_, float p_211843_3_, int p_211843_4_, boolean p_211843_5_) {
      float f = p_211843_5_ ? 0.25F : 1.0F;
      float f1 = (float)(p_211843_4_ >> 16 & 255) / 255.0F * f;
      float f2 = (float)(p_211843_4_ >> 8 & 255) / 255.0F * f;
      float f3 = (float)(p_211843_4_ & 255) / 255.0F * f;
      float f4 = f1;
      float f5 = f2;
      float f6 = f3;
      float f7 = (float)(p_211843_4_ >> 24 & 255) / 255.0F;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      ResourceLocation resourcelocation = null;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      boolean flag = false;
      boolean flag1 = false;
      boolean flag2 = false;
      boolean flag3 = false;
      boolean flag4 = false;
      List<FontRenderer.Entry> list = Lists.newArrayList();

      for(int i = 0; i < p_211843_1_.length(); ++i) {
         char c0 = p_211843_1_.charAt(i);
         if (c0 == 167 && i + 1 < p_211843_1_.length()) {
            TextFormatting textformatting = TextFormatting.fromFormattingCode(p_211843_1_.charAt(i + 1));
            if (textformatting != null) {
               if (textformatting.isNormalStyle()) {
                  flag = false;
                  flag1 = false;
                  flag4 = false;
                  flag3 = false;
                  flag2 = false;
                  f4 = f1;
                  f5 = f2;
                  f6 = f3;
               }

               if (textformatting.getColor() != null) {
                  int j = textformatting.getColor();
                  f4 = (float)(j >> 16 & 255) / 255.0F * f;
                  f5 = (float)(j >> 8 & 255) / 255.0F * f;
                  f6 = (float)(j & 255) / 255.0F * f;
               } else if (textformatting == TextFormatting.OBFUSCATED) {
                  flag = true;
               } else if (textformatting == TextFormatting.BOLD) {
                  flag1 = true;
               } else if (textformatting == TextFormatting.STRIKETHROUGH) {
                  flag4 = true;
               } else if (textformatting == TextFormatting.UNDERLINE) {
                  flag3 = true;
               } else if (textformatting == TextFormatting.ITALIC) {
                  flag2 = true;
               }
            }

            ++i;
         } else {
            IGlyph iglyph = this.font.findGlyph(c0);
            TexturedGlyph texturedglyph = flag && c0 != ' ' ? this.font.func_211188_a(iglyph) : this.font.func_211187_a(c0);
            ResourceLocation resourcelocation1 = texturedglyph.getTextureLocation();
            if (resourcelocation1 != null) {
               if (resourcelocation != resourcelocation1) {
                  tessellator.draw();
                  this.textureManager.bindTexture(resourcelocation1);
                  bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                  resourcelocation = resourcelocation1;
               }

               float f8 = flag1 ? iglyph.getBoldOffset() : 0.0F;
               float f9 = p_211843_5_ ? iglyph.getShadowOffset() : 0.0F;
               this.func_212452_a(texturedglyph, flag1, flag2, f8, p_211843_2_ + f9, p_211843_3_ + f9, bufferbuilder, f4, f5, f6, f7);
            }

            float f10 = iglyph.getAdvance(flag1);
            float f11 = p_211843_5_ ? 1.0F : 0.0F;
            if (flag4) {
               list.add(new FontRenderer.Entry(p_211843_2_ + f11 - 1.0F, p_211843_3_ + f11 + (float)this.FONT_HEIGHT / 2.0F, p_211843_2_ + f11 + f10, p_211843_3_ + f11 + (float)this.FONT_HEIGHT / 2.0F - 1.0F, f4, f5, f6, f7));
            }

            if (flag3) {
               list.add(new FontRenderer.Entry(p_211843_2_ + f11 - 1.0F, p_211843_3_ + f11 + (float)this.FONT_HEIGHT, p_211843_2_ + f11 + f10, p_211843_3_ + f11 + (float)this.FONT_HEIGHT - 1.0F, f4, f5, f6, f7));
            }

            p_211843_2_ += f10;
         }
      }

      tessellator.draw();
      if (!list.isEmpty()) {
         GlStateManager.disableTexture2D();
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);

         for(FontRenderer.Entry fontrenderer$entry : list) {
            fontrenderer$entry.pipe(bufferbuilder);
         }

         tessellator.draw();
         GlStateManager.enableTexture2D();
      }

      return p_211843_2_;
   }

   private void func_212452_a(TexturedGlyph p_212452_1_, boolean p_212452_2_, boolean p_212452_3_, float p_212452_4_, float p_212452_5_, float p_212452_6_, BufferBuilder p_212452_7_, float p_212452_8_, float p_212452_9_, float p_212452_10_, float p_212452_11_) {
      p_212452_1_.render(this.textureManager, p_212452_3_, p_212452_5_, p_212452_6_, p_212452_7_, p_212452_8_, p_212452_9_, p_212452_10_, p_212452_11_);
      if (p_212452_2_) {
         p_212452_1_.render(this.textureManager, p_212452_3_, p_212452_5_ + p_212452_4_, p_212452_6_, p_212452_7_, p_212452_8_, p_212452_9_, p_212452_10_, p_212452_11_);
      }

   }

   public int getStringWidth(String p_78256_1_) {
      if (p_78256_1_ == null) {
         return 0;
      } else {
         float f = 0.0F;
         boolean flag = false;

         for(int i = 0; i < p_78256_1_.length(); ++i) {
            char c0 = p_78256_1_.charAt(i);
            if (c0 == 167 && i < p_78256_1_.length() - 1) {
               ++i;
               TextFormatting textformatting = TextFormatting.fromFormattingCode(p_78256_1_.charAt(i));
               if (textformatting == TextFormatting.BOLD) {
                  flag = true;
               } else if (textformatting != null && textformatting.isNormalStyle()) {
                  flag = false;
               }
            } else {
               f += this.font.findGlyph(c0).getAdvance(flag);
            }
         }

         return MathHelper.ceil(f);
      }
   }

   private float getCharWidth(char p_211125_1_) {
      return p_211125_1_ == 167 ? 0.0F : (float)MathHelper.ceil(this.font.findGlyph(p_211125_1_).getAdvance(false));
   }

   public String trimStringToWidth(String p_78269_1_, int p_78269_2_) {
      return this.trimStringToWidth(p_78269_1_, p_78269_2_, false);
   }

   public String trimStringToWidth(String p_78262_1_, int p_78262_2_, boolean p_78262_3_) {
      StringBuilder stringbuilder = new StringBuilder();
      float f = 0.0F;
      int i = p_78262_3_ ? p_78262_1_.length() - 1 : 0;
      int j = p_78262_3_ ? -1 : 1;
      boolean flag = false;
      boolean flag1 = false;

      for(int k = i; k >= 0 && k < p_78262_1_.length() && f < (float)p_78262_2_; k += j) {
         char c0 = p_78262_1_.charAt(k);
         if (flag) {
            flag = false;
            TextFormatting textformatting = TextFormatting.fromFormattingCode(c0);
            if (textformatting == TextFormatting.BOLD) {
               flag1 = true;
            } else if (textformatting != null && textformatting.isNormalStyle()) {
               flag1 = false;
            }
         } else if (c0 == 167) {
            flag = true;
         } else {
            f += this.getCharWidth(c0);
            if (flag1) {
               ++f;
            }
         }

         if (f > (float)p_78262_2_) {
            break;
         }

         if (p_78262_3_) {
            stringbuilder.insert(0, c0);
         } else {
            stringbuilder.append(c0);
         }
      }

      return stringbuilder.toString();
   }

   private String trimStringNewline(String p_78273_1_) {
      while(p_78273_1_ != null && p_78273_1_.endsWith("\n")) {
         p_78273_1_ = p_78273_1_.substring(0, p_78273_1_.length() - 1);
      }

      return p_78273_1_;
   }

   public void drawSplitString(String p_78279_1_, int p_78279_2_, int p_78279_3_, int p_78279_4_, int p_78279_5_) {
      p_78279_1_ = this.trimStringNewline(p_78279_1_);
      this.func_211124_b(p_78279_1_, p_78279_2_, p_78279_3_, p_78279_4_, p_78279_5_);
   }

   private void func_211124_b(String p_211124_1_, int p_211124_2_, int p_211124_3_, int p_211124_4_, int p_211124_5_) {
      for(String s : this.listFormattedStringToWidth(p_211124_1_, p_211124_4_)) {
         float f = (float)p_211124_2_;
         if (this.bidiFlag) {
            int i = this.getStringWidth(this.bidiReorder(s));
            f += (float)(p_211124_4_ - i);
         }

         this.renderString(s, f, (float)p_211124_3_, p_211124_5_, false);
         p_211124_3_ += this.FONT_HEIGHT;
      }

   }

   public int getWordWrappedHeight(String p_78267_1_, int p_78267_2_) {
      return this.FONT_HEIGHT * this.listFormattedStringToWidth(p_78267_1_, p_78267_2_).size();
   }

   public void setBidiFlag(boolean p_78275_1_) {
      this.bidiFlag = p_78275_1_;
   }

   public List<String> listFormattedStringToWidth(String p_78271_1_, int p_78271_2_) {
      return Arrays.asList(this.wrapFormattedStringToWidth(p_78271_1_, p_78271_2_).split("\n"));
   }

   public String wrapFormattedStringToWidth(String p_78280_1_, int p_78280_2_) {
      String s;
      String s1;
      for(s = ""; !p_78280_1_.isEmpty(); s = s + s1 + "\n") {
         int i = this.sizeStringToWidth(p_78280_1_, p_78280_2_);
         if (p_78280_1_.length() <= i) {
            return s + p_78280_1_;
         }

         s1 = p_78280_1_.substring(0, i);
         char c0 = p_78280_1_.charAt(i);
         boolean flag = c0 == ' ' || c0 == '\n';
         p_78280_1_ = TextFormatting.func_211164_a(s1) + p_78280_1_.substring(i + (flag ? 1 : 0));
      }

      return s;
   }

   private int sizeStringToWidth(String p_78259_1_, int p_78259_2_) {
      int i = Math.max(1, p_78259_2_);
      int j = p_78259_1_.length();
      float f = 0.0F;
      int k = 0;
      int l = -1;
      boolean flag = false;

      for(boolean flag1 = true; k < j; ++k) {
         char c0 = p_78259_1_.charAt(k);
         switch(c0) {
         case '\n':
            --k;
            break;
         case ' ':
            l = k;
         default:
            if (f != 0.0F) {
               flag1 = false;
            }

            f += this.getCharWidth(c0);
            if (flag) {
               ++f;
            }
            break;
         case '\u00a7':
            if (k < j - 1) {
               ++k;
               TextFormatting textformatting = TextFormatting.fromFormattingCode(p_78259_1_.charAt(k));
               if (textformatting == TextFormatting.BOLD) {
                  flag = true;
               } else if (textformatting != null && textformatting.isNormalStyle()) {
                  flag = false;
               }
            }
         }

         if (c0 == '\n') {
            ++k;
            l = k;
            break;
         }

         if (f > (float)i) {
            if (flag1) {
               ++k;
            }
            break;
         }
      }

      return k != j && l != -1 && l < k ? l : k;
   }

   public boolean getBidiFlag() {
      return this.bidiFlag;
   }

   @OnlyIn(Dist.CLIENT)
   static class Entry {
      protected final float x1;
      protected final float y1;
      protected final float x2;
      protected final float y2;
      protected final float red;
      protected final float green;
      protected final float blue;
      protected final float alpha;

      private Entry(float p_i49707_1_, float p_i49707_2_, float p_i49707_3_, float p_i49707_4_, float p_i49707_5_, float p_i49707_6_, float p_i49707_7_, float p_i49707_8_) {
         this.x1 = p_i49707_1_;
         this.y1 = p_i49707_2_;
         this.x2 = p_i49707_3_;
         this.y2 = p_i49707_4_;
         this.red = p_i49707_5_;
         this.green = p_i49707_6_;
         this.blue = p_i49707_7_;
         this.alpha = p_i49707_8_;
      }

      public void pipe(BufferBuilder p_211168_1_) {
         p_211168_1_.pos((double)this.x1, (double)this.y1, 0.0D).color(this.red, this.green, this.blue, this.alpha).endVertex();
         p_211168_1_.pos((double)this.x2, (double)this.y1, 0.0D).color(this.red, this.green, this.blue, this.alpha).endVertex();
         p_211168_1_.pos((double)this.x2, (double)this.y2, 0.0D).color(this.red, this.green, this.blue, this.alpha).endVertex();
         p_211168_1_.pos((double)this.x1, (double)this.y2, 0.0D).color(this.red, this.green, this.blue, this.alpha).endVertex();
      }
   }
}
