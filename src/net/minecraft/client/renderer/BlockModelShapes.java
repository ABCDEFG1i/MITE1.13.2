package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.state.IProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockModelShapes {
   private final Map<IBlockState, IBakedModel> bakedModelStore = Maps.newIdentityHashMap();
   private final ModelManager modelManager;

   public BlockModelShapes(ModelManager p_i46245_1_) {
      this.modelManager = p_i46245_1_;
   }

   public TextureAtlasSprite getTexture(IBlockState p_178122_1_) {
      return this.func_178125_b(p_178122_1_).func_177554_e();
   }

   public IBakedModel func_178125_b(IBlockState p_178125_1_) {
      IBakedModel ibakedmodel = this.bakedModelStore.get(p_178125_1_);
      if (ibakedmodel == null) {
         ibakedmodel = this.modelManager.func_174951_a();
      }

      return ibakedmodel;
   }

   public ModelManager func_178126_b() {
      return this.modelManager;
   }

   public void reloadModels() {
      this.bakedModelStore.clear();

      for(Block block : IRegistry.field_212618_g) {
         block.getStateContainer().getValidStates().forEach((p_209551_1_) -> {
            IBakedModel ibakedmodel = this.bakedModelStore.put(p_209551_1_, this.modelManager.func_174953_a(func_209554_c(p_209551_1_)));
         });
      }

   }

   public static ModelResourceLocation func_209554_c(IBlockState p_209554_0_) {
      return func_209553_a(IRegistry.field_212618_g.func_177774_c(p_209554_0_.getBlock()), p_209554_0_);
   }

   public static ModelResourceLocation func_209553_a(ResourceLocation p_209553_0_, IBlockState p_209553_1_) {
      return new ModelResourceLocation(p_209553_0_, getPropertyMapString(p_209553_1_.getValues()));
   }

   public static String getPropertyMapString(Map<IProperty<?>, Comparable<?>> p_209552_0_) {
      StringBuilder stringbuilder = new StringBuilder();

      for(Entry<IProperty<?>, Comparable<?>> entry : p_209552_0_.entrySet()) {
         if (stringbuilder.length() != 0) {
            stringbuilder.append(',');
         }

         IProperty<?> iproperty = entry.getKey();
         stringbuilder.append(iproperty.getName());
         stringbuilder.append('=');
         stringbuilder.append(getPropertyValueString(iproperty, entry.getValue()));
      }

      return stringbuilder.toString();
   }

   private static <T extends Comparable<T>> String getPropertyValueString(IProperty<T> p_209555_0_, Comparable<?> p_209555_1_) {
      return p_209555_0_.getName((T)p_209555_1_);
   }
}
