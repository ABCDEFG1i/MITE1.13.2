package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WeightedBakedModel implements IBakedModel {
   private final int field_177567_a;
   private final List<WeightedBakedModel.WeightedModel> field_177565_b;
   private final IBakedModel field_177566_c;

   public WeightedBakedModel(List<WeightedBakedModel.WeightedModel> p_i46073_1_) {
      this.field_177565_b = p_i46073_1_;
      this.field_177567_a = WeightedRandom.getTotalWeight(p_i46073_1_);
      this.field_177566_c = (p_i46073_1_.get(0)).field_185281_b;
   }

   public List<BakedQuad> func_200117_a(@Nullable IBlockState p_200117_1_, @Nullable EnumFacing p_200117_2_, Random p_200117_3_) {
      return (WeightedRandom.getRandomItem(this.field_177565_b, Math.abs((int)p_200117_3_.nextLong()) % this.field_177567_a)).field_185281_b.func_200117_a(p_200117_1_, p_200117_2_, p_200117_3_);
   }

   public boolean func_177555_b() {
      return this.field_177566_c.func_177555_b();
   }

   public boolean func_177556_c() {
      return this.field_177566_c.func_177556_c();
   }

   public boolean func_188618_c() {
      return this.field_177566_c.func_188618_c();
   }

   public TextureAtlasSprite func_177554_e() {
      return this.field_177566_c.func_177554_e();
   }

   public ItemCameraTransforms func_177552_f() {
      return this.field_177566_c.func_177552_f();
   }

   public ItemOverrideList func_188617_f() {
      return this.field_177566_c.func_188617_f();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<WeightedBakedModel.WeightedModel> field_177678_a = Lists.newArrayList();

      public WeightedBakedModel.Builder func_177677_a(@Nullable IBakedModel p_177677_1_, int p_177677_2_) {
         if (p_177677_1_ != null) {
            this.field_177678_a.add(new WeightedBakedModel.WeightedModel(p_177677_1_, p_177677_2_));
         }

         return this;
      }

      @Nullable
      public IBakedModel func_209614_a() {
         if (this.field_177678_a.isEmpty()) {
            return null;
         } else {
            return (IBakedModel)(this.field_177678_a.size() == 1 ? (this.field_177678_a.get(0)).field_185281_b : new WeightedBakedModel(this.field_177678_a));
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class WeightedModel extends WeightedRandom.Item {
      protected final IBakedModel field_185281_b;

      public WeightedModel(IBakedModel p_i46763_1_, int p_i46763_2_) {
         super(p_i46763_2_);
         this.field_185281_b = p_i46763_1_;
      }
   }
}
