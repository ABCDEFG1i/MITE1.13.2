package net.minecraft.client.gui.fonts;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EmptyGlyph extends TexturedGlyph {
   public EmptyGlyph() {
      super(new ResourceLocation(""), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   public void render(TextureManager p_211234_1_, boolean p_211234_2_, float p_211234_3_, float p_211234_4_, BufferBuilder p_211234_5_, float p_211234_6_, float p_211234_7_, float p_211234_8_, float p_211234_9_) {
   }

   @Nullable
   public ResourceLocation getTextureLocation() {
      return null;
   }
}
