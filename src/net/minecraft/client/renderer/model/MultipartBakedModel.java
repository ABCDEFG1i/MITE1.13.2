package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

@OnlyIn(Dist.CLIENT)
public class MultipartBakedModel implements IBakedModel {
   private final List<Pair<Predicate<IBlockState>, IBakedModel>> field_188626_f;
   protected final boolean field_188621_a;
   protected final boolean field_188622_b;
   protected final TextureAtlasSprite field_188623_c;
   protected final ItemCameraTransforms field_188624_d;
   protected final ItemOverrideList field_188625_e;
   private final Map<IBlockState, BitSet> field_210277_g = new Object2ObjectOpenCustomHashMap<>(Util.func_212443_g());

   public MultipartBakedModel(List<Pair<Predicate<IBlockState>, IBakedModel>> p_i48273_1_) {
      this.field_188626_f = p_i48273_1_;
      IBakedModel ibakedmodel = p_i48273_1_.iterator().next().getRight();
      this.field_188621_a = ibakedmodel.func_177555_b();
      this.field_188622_b = ibakedmodel.func_177556_c();
      this.field_188623_c = ibakedmodel.func_177554_e();
      this.field_188624_d = ibakedmodel.func_177552_f();
      this.field_188625_e = ibakedmodel.func_188617_f();
   }

   public List<BakedQuad> func_200117_a(@Nullable IBlockState p_200117_1_, @Nullable EnumFacing p_200117_2_, Random p_200117_3_) {
      if (p_200117_1_ == null) {
         return Collections.emptyList();
      } else {
         BitSet bitset = this.field_210277_g.get(p_200117_1_);
         if (bitset == null) {
            bitset = new BitSet();

            for(int i = 0; i < this.field_188626_f.size(); ++i) {
               Pair<Predicate<IBlockState>, IBakedModel> pair = this.field_188626_f.get(i);
               if (pair.getLeft().test(p_200117_1_)) {
                  bitset.set(i);
               }
            }

            this.field_210277_g.put(p_200117_1_, bitset);
         }

         List<BakedQuad> list = Lists.newArrayList();
         long k = p_200117_3_.nextLong();

         for(int j = 0; j < bitset.length(); ++j) {
            if (bitset.get(j)) {
               list.addAll(this.field_188626_f.get(j).getRight().func_200117_a(p_200117_1_, p_200117_2_, new Random(k)));
            }
         }

         return list;
      }
   }

   public boolean func_177555_b() {
      return this.field_188621_a;
   }

   public boolean func_177556_c() {
      return this.field_188622_b;
   }

   public boolean func_188618_c() {
      return false;
   }

   public TextureAtlasSprite func_177554_e() {
      return this.field_188623_c;
   }

   public ItemCameraTransforms func_177552_f() {
      return this.field_188624_d;
   }

   public ItemOverrideList func_188617_f() {
      return this.field_188625_e;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<Pair<Predicate<IBlockState>, IBakedModel>> field_188649_a = Lists.newArrayList();

      public void func_188648_a(Predicate<IBlockState> p_188648_1_, IBakedModel p_188648_2_) {
         this.field_188649_a.add(Pair.of(p_188648_1_, p_188648_2_));
      }

      public IBakedModel func_188647_a() {
         return new MultipartBakedModel(this.field_188649_a);
      }
   }
}
