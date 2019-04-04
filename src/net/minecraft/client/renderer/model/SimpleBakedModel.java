package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimpleBakedModel implements IBakedModel {
   protected final List<BakedQuad> field_177563_a;
   protected final Map<EnumFacing, List<BakedQuad>> field_177561_b;
   protected final boolean field_177562_c;
   protected final boolean field_177559_d;
   protected final TextureAtlasSprite field_177560_e;
   protected final ItemCameraTransforms field_177558_f;
   protected final ItemOverrideList field_188620_g;

   public SimpleBakedModel(List<BakedQuad> p_i46535_1_, Map<EnumFacing, List<BakedQuad>> p_i46535_2_, boolean p_i46535_3_, boolean p_i46535_4_, TextureAtlasSprite p_i46535_5_, ItemCameraTransforms p_i46535_6_, ItemOverrideList p_i46535_7_) {
      this.field_177563_a = p_i46535_1_;
      this.field_177561_b = p_i46535_2_;
      this.field_177562_c = p_i46535_3_;
      this.field_177559_d = p_i46535_4_;
      this.field_177560_e = p_i46535_5_;
      this.field_177558_f = p_i46535_6_;
      this.field_188620_g = p_i46535_7_;
   }

   public List<BakedQuad> func_200117_a(@Nullable IBlockState p_200117_1_, @Nullable EnumFacing p_200117_2_, Random p_200117_3_) {
      return p_200117_2_ == null ? this.field_177563_a : this.field_177561_b.get(p_200117_2_);
   }

   public boolean func_177555_b() {
      return this.field_177562_c;
   }

   public boolean func_177556_c() {
      return this.field_177559_d;
   }

   public boolean func_188618_c() {
      return false;
   }

   public TextureAtlasSprite func_177554_e() {
      return this.field_177560_e;
   }

   public ItemCameraTransforms func_177552_f() {
      return this.field_177558_f;
   }

   public ItemOverrideList func_188617_f() {
      return this.field_188620_g;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<BakedQuad> field_177656_a = Lists.newArrayList();
      private final Map<EnumFacing, List<BakedQuad>> field_177654_b = Maps.newEnumMap(EnumFacing.class);
      private final ItemOverrideList field_188646_c;
      private final boolean field_177655_c;
      private TextureAtlasSprite field_177652_d;
      private final boolean field_177653_e;
      private final ItemCameraTransforms field_177651_f;

      public Builder(ModelBlock p_i46988_1_, ItemOverrideList p_i46988_2_) {
         this(p_i46988_1_.func_178309_b(), p_i46988_1_.func_178311_c(), p_i46988_1_.func_181682_g(), p_i46988_2_);
      }

      public Builder(IBlockState p_i48189_1_, IBakedModel p_i48189_2_, TextureAtlasSprite p_i48189_3_, Random p_i48189_4_, long p_i48189_5_) {
         this(p_i48189_2_.func_177555_b(), p_i48189_2_.func_177556_c(), p_i48189_2_.func_177552_f(), p_i48189_2_.func_188617_f());
         this.field_177652_d = p_i48189_2_.func_177554_e();

         for(EnumFacing enumfacing : EnumFacing.values()) {
            p_i48189_4_.setSeed(p_i48189_5_);

            for(BakedQuad bakedquad : p_i48189_2_.func_200117_a(p_i48189_1_, enumfacing, p_i48189_4_)) {
               this.func_177650_a(enumfacing, new BakedQuadRetextured(bakedquad, p_i48189_3_));
            }
         }

         p_i48189_4_.setSeed(p_i48189_5_);

         for(BakedQuad bakedquad1 : p_i48189_2_.func_200117_a(p_i48189_1_, null, p_i48189_4_)) {
            this.func_177648_a(new BakedQuadRetextured(bakedquad1, p_i48189_3_));
         }

      }

      private Builder(boolean p_i46990_1_, boolean p_i46990_2_, ItemCameraTransforms p_i46990_3_, ItemOverrideList p_i46990_4_) {
         for(EnumFacing enumfacing : EnumFacing.values()) {
            this.field_177654_b.put(enumfacing, Lists.newArrayList());
         }

         this.field_188646_c = p_i46990_4_;
         this.field_177655_c = p_i46990_1_;
         this.field_177653_e = p_i46990_2_;
         this.field_177651_f = p_i46990_3_;
      }

      public SimpleBakedModel.Builder func_177650_a(EnumFacing p_177650_1_, BakedQuad p_177650_2_) {
         this.field_177654_b.get(p_177650_1_).add(p_177650_2_);
         return this;
      }

      public SimpleBakedModel.Builder func_177648_a(BakedQuad p_177648_1_) {
         this.field_177656_a.add(p_177648_1_);
         return this;
      }

      public SimpleBakedModel.Builder func_177646_a(TextureAtlasSprite p_177646_1_) {
         this.field_177652_d = p_177646_1_;
         return this;
      }

      public IBakedModel func_177645_b() {
         if (this.field_177652_d == null) {
            throw new RuntimeException("Missing particle!");
         } else {
            return new SimpleBakedModel(this.field_177656_a, this.field_177654_b, this.field_177655_c, this.field_177653_e, this.field_177652_d, this.field_177651_f, this.field_188646_c);
         }
      }
   }
}
