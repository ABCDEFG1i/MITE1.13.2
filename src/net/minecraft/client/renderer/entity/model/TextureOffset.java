package net.minecraft.client.renderer.entity.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureOffset {
   public final int textureOffsetX;
   public final int textureOffsetY;

   public TextureOffset(int p_i1175_1_, int p_i1175_2_) {
      this.textureOffsetX = p_i1175_1_;
      this.textureOffsetY = p_i1175_2_;
   }
}
