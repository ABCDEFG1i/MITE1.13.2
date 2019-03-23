package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemModelMesher {
   private final Int2ObjectMap<ModelResourceLocation> modelLocations = new Int2ObjectOpenHashMap<>(256);
   private final Int2ObjectMap<IBakedModel> itemModels = new Int2ObjectOpenHashMap<>(256);
   private final ModelManager modelManager;

   public ItemModelMesher(ModelManager p_i46250_1_) {
      this.modelManager = p_i46250_1_;
   }

   public TextureAtlasSprite getParticleIcon(IItemProvider p_199934_1_) {
      return this.getParticleIcon(new ItemStack(p_199934_1_));
   }

   public TextureAtlasSprite getParticleIcon(ItemStack p_199309_1_) {
      IBakedModel ibakedmodel = this.func_178089_a(p_199309_1_);
      return (ibakedmodel == this.modelManager.func_174951_a() || ibakedmodel.func_188618_c()) && p_199309_1_.getItem() instanceof ItemBlock ? this.modelManager.func_174954_c().getTexture(((ItemBlock)p_199309_1_.getItem()).getBlock().getDefaultState()) : ibakedmodel.func_177554_e();
   }

   public IBakedModel func_178089_a(ItemStack p_178089_1_) {
      IBakedModel ibakedmodel = this.func_199312_b(p_178089_1_.getItem());
      return ibakedmodel == null ? this.modelManager.func_174951_a() : ibakedmodel;
   }

   @Nullable
   public IBakedModel func_199312_b(Item p_199312_1_) {
      return this.itemModels.get(getIndex(p_199312_1_));
   }

   private static int getIndex(Item p_199310_0_) {
      return Item.getIdFromItem(p_199310_0_);
   }

   public void func_199311_a(Item p_199311_1_, ModelResourceLocation p_199311_2_) {
      this.modelLocations.put(getIndex(p_199311_1_), p_199311_2_);
      this.itemModels.put(getIndex(p_199311_1_), this.modelManager.func_174953_a(p_199311_2_));
   }

   public ModelManager func_178083_a() {
      return this.modelManager;
   }

   public void rebuildCache() {
      this.itemModels.clear();

      for(Entry<Integer, ModelResourceLocation> entry : this.modelLocations.entrySet()) {
         this.itemModels.put(entry.getKey(), this.modelManager.func_174953_a(entry.getValue()));
      }

   }
}
