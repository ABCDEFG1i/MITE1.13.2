package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemOverrideList {
   public static final ItemOverrideList field_188022_a = new ItemOverrideList();
   private final List<ItemOverride> field_188023_b = Lists.newArrayList();
   private final List<IBakedModel> field_209582_c;

   protected ItemOverrideList() {
      this.field_209582_c = Collections.emptyList();
   }

   public ItemOverrideList(ModelBlock p_i49525_1_, Function<ResourceLocation, IUnbakedModel> p_i49525_2_, Function<ResourceLocation, TextureAtlasSprite> p_i49525_3_, List<ItemOverride> p_i49525_4_) {
      this.field_209582_c = p_i49525_4_.stream().map((p_209580_3_) -> {
         IUnbakedModel iunbakedmodel = p_i49525_2_.apply(p_209580_3_.func_188026_a());
         return Objects.equals(iunbakedmodel, p_i49525_1_) ? null : iunbakedmodel.func_209558_a(p_i49525_2_, p_i49525_3_, ModelRotation.X0_Y0, false);
      }).collect(Collectors.toList());
      Collections.reverse(this.field_209582_c);

      for(int i = p_i49525_4_.size() - 1; i >= 0; --i) {
         this.field_188023_b.add(p_i49525_4_.get(i));
      }

   }

   @Nullable
   public IBakedModel func_209581_a(IBakedModel p_209581_1_, ItemStack p_209581_2_, @Nullable World p_209581_3_, @Nullable EntityLivingBase p_209581_4_) {
      if (!this.field_188023_b.isEmpty()) {
         for(int i = 0; i < this.field_188023_b.size(); ++i) {
            ItemOverride itemoverride = this.field_188023_b.get(i);
            if (itemoverride.func_188027_a(p_209581_2_, p_209581_3_, p_209581_4_)) {
               IBakedModel ibakedmodel = this.field_209582_c.get(i);
               if (ibakedmodel == null) {
                  return p_209581_1_;
               }

               return ibakedmodel;
            }
         }
      }

      return p_209581_1_;
   }
}
