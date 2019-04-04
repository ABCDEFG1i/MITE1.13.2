package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GrassColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GrassColorReloadListener implements IResourceManagerReloadListener {
   private static final ResourceLocation GRASS_LOCATION = new ResourceLocation("textures/colormap/grass.png");

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      try {
         GrassColors.func_77479_a(TextureUtil.makePixelArray(p_195410_1_, GRASS_LOCATION));
      } catch (IOException var3) {
      }

   }
}
