package net.minecraft.client.renderer.texture;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DynamicTexture extends AbstractTexture implements AutoCloseable {
   @Nullable
   private NativeImage dynamicTextureData;

   public DynamicTexture(NativeImage p_i48124_1_) {
      this.dynamicTextureData = p_i48124_1_;
      TextureUtil.allocateTexture(this.getGlTextureId(), this.dynamicTextureData.getWidth(), this.dynamicTextureData.getHeight());
      this.updateDynamicTexture();
   }

   public DynamicTexture(int p_i48125_1_, int p_i48125_2_, boolean p_i48125_3_) {
      this.dynamicTextureData = new NativeImage(p_i48125_1_, p_i48125_2_, p_i48125_3_);
      TextureUtil.allocateTexture(this.getGlTextureId(), this.dynamicTextureData.getWidth(), this.dynamicTextureData.getHeight());
   }

   public void loadTexture(IResourceManager p_195413_1_) throws IOException {
   }

   public void updateDynamicTexture() {
      this.bindTexture();
      this.dynamicTextureData.uploadTextureSub(0, 0, 0, false);
   }

   @Nullable
   public NativeImage func_195414_e() {
      return this.dynamicTextureData;
   }

   public void func_195415_a(NativeImage p_195415_1_) throws Exception {
      this.dynamicTextureData.close();
      this.dynamicTextureData = p_195415_1_;
   }

   public void close() {
      this.dynamicTextureData.close();
      this.deleteGlTexture();
      this.dynamicTextureData = null;
   }
}
