package net.minecraft.client.renderer.model;

import java.util.Map;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelManager implements IResourceManagerReloadListener {
   private Map<ModelResourceLocation, IBakedModel> field_174958_a;
   private final TextureMap field_174956_b;
   private final BlockModelShapes field_174957_c;
   private IBakedModel field_174955_d;

   public ModelManager(TextureMap p_i46082_1_) {
      this.field_174956_b = p_i46082_1_;
      this.field_174957_c = new BlockModelShapes(this);
   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      ModelBakery modelbakery = new ModelBakery(p_195410_1_, this.field_174956_b);
      this.field_174958_a = modelbakery.func_177570_a();
      this.field_174955_d = this.field_174958_a.get(ModelBakery.field_177604_a);
      this.field_174957_c.reloadModels();
   }

   public IBakedModel func_174953_a(ModelResourceLocation p_174953_1_) {
      return this.field_174958_a.getOrDefault(p_174953_1_, this.field_174955_d);
   }

   public IBakedModel func_174951_a() {
      return this.field_174955_d;
   }

   public BlockModelShapes func_174954_c() {
      return this.field_174957_c;
   }
}
