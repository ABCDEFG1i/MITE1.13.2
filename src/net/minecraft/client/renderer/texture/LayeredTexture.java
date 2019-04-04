package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class LayeredTexture extends AbstractTexture {
   private static final Logger LOGGER = LogManager.getLogger();
   public final List<String> layeredTextureNames;

   public LayeredTexture(String... p_i1274_1_) {
      this.layeredTextureNames = Lists.newArrayList(p_i1274_1_);
      if (this.layeredTextureNames.isEmpty()) {
         throw new IllegalStateException("Layered texture with no layers.");
      }
   }

   public void loadTexture(IResourceManager p_195413_1_) throws IOException {
      Iterator<String> iterator = this.layeredTextureNames.iterator();
      String s = iterator.next();

      try (
         IResource iresource = p_195413_1_.getResource(new ResourceLocation(s));
         NativeImage nativeimage = NativeImage.read(iresource.getInputStream())) {
         while(true) {
            if (!iterator.hasNext()) {
               TextureUtil.allocateTexture(this.getGlTextureId(), nativeimage.getWidth(), nativeimage.getHeight());
               nativeimage.uploadTextureSub(0, 0, 0, false);
               break;
            }

            String s1 = iterator.next();
            if (s1 != null) {
               try (
                  IResource iresource1 = p_195413_1_.getResource(new ResourceLocation(s1));
                  NativeImage nativeimage1 = NativeImage.read(iresource1.getInputStream())) {
                  for(int i = 0; i < nativeimage1.getHeight(); ++i) {
                     for(int j = 0; j < nativeimage1.getWidth(); ++j) {
                        nativeimage.blendPixel(j, i, nativeimage1.getPixelRGBA(j, i));
                     }
                  }
               }
            }
         }
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't load layered image", ioexception);
      }

   }
}
