package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.FoliageColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoliageColorReloadListener implements IResourceManagerReloadListener {
   private static final ResourceLocation FOLIAGE_LOCATION = new ResourceLocation("textures/colormap/foliage.png");

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      try {
         FoliageColors.func_77467_a(TextureUtil.makePixelArray(p_195410_1_, FOLIAGE_LOCATION));
      } catch (IOException var3) {
         ;
      }

   }
}
