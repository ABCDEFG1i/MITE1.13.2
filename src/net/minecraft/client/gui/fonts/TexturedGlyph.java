package net.minecraft.client.gui.fonts;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TexturedGlyph {
   private final ResourceLocation textureLocation;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;
   private final float field_211240_f;
   private final float field_211241_g;
   private final float field_211242_h;
   private final float field_211243_i;

   public TexturedGlyph(ResourceLocation p_i49852_1_, float p_i49852_2_, float p_i49852_3_, float p_i49852_4_, float p_i49852_5_, float p_i49852_6_, float p_i49852_7_, float p_i49852_8_, float p_i49852_9_) {
      this.textureLocation = p_i49852_1_;
      this.u0 = p_i49852_2_;
      this.u1 = p_i49852_3_;
      this.v0 = p_i49852_4_;
      this.v1 = p_i49852_5_;
      this.field_211240_f = p_i49852_6_;
      this.field_211241_g = p_i49852_7_;
      this.field_211242_h = p_i49852_8_;
      this.field_211243_i = p_i49852_9_;
   }

   public void render(TextureManager p_211234_1_, boolean p_211234_2_, float p_211234_3_, float p_211234_4_, BufferBuilder p_211234_5_, float p_211234_6_, float p_211234_7_, float p_211234_8_, float p_211234_9_) {
      int i = 3;
      float f = p_211234_3_ + this.field_211240_f;
      float f1 = p_211234_3_ + this.field_211241_g;
      float f2 = this.field_211242_h - 3.0F;
      float f3 = this.field_211243_i - 3.0F;
      float f4 = p_211234_4_ + f2;
      float f5 = p_211234_4_ + f3;
      float f6 = p_211234_2_ ? 1.0F - 0.25F * f2 : 0.0F;
      float f7 = p_211234_2_ ? 1.0F - 0.25F * f3 : 0.0F;
      p_211234_5_.pos((double)(f + f6), (double)f4, 0.0D).tex((double)this.u0, (double)this.v0).color(p_211234_6_, p_211234_7_, p_211234_8_, p_211234_9_).endVertex();
      p_211234_5_.pos((double)(f + f7), (double)f5, 0.0D).tex((double)this.u0, (double)this.v1).color(p_211234_6_, p_211234_7_, p_211234_8_, p_211234_9_).endVertex();
      p_211234_5_.pos((double)(f1 + f7), (double)f5, 0.0D).tex((double)this.u1, (double)this.v1).color(p_211234_6_, p_211234_7_, p_211234_8_, p_211234_9_).endVertex();
      p_211234_5_.pos((double)(f1 + f6), (double)f4, 0.0D).tex((double)this.u1, (double)this.v0).color(p_211234_6_, p_211234_7_, p_211234_8_, p_211234_9_).endVertex();
   }

   @Nullable
   public ResourceLocation getTextureLocation() {
      return this.textureLocation;
   }
}
