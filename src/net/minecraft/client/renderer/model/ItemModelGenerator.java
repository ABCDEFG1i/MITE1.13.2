package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemModelGenerator {
   public static final List<String> field_178398_a = Lists.newArrayList("layer0", "layer1", "layer2", "layer3", "layer4");

   public ModelBlock func_209579_a(Function<ResourceLocation, TextureAtlasSprite> p_209579_1_, ModelBlock p_209579_2_) {
      Map<String, String> map = Maps.newHashMap();
      List<BlockPart> list = Lists.newArrayList();

      for(int i = 0; i < field_178398_a.size(); ++i) {
         String s = field_178398_a.get(i);
         if (!p_209579_2_.func_178300_b(s)) {
            break;
         }

         String s1 = p_209579_2_.func_178308_c(s);
         map.put(s, s1);
         TextureAtlasSprite textureatlassprite = p_209579_1_.apply(new ResourceLocation(s1));
         list.addAll(this.func_178394_a(i, s, textureatlassprite));
      }

      map.put("particle", p_209579_2_.func_178300_b("particle") ? p_209579_2_.func_178308_c("particle") : map.get("layer0"));
      ModelBlock modelblock = new ModelBlock(null, list, map, false, false, p_209579_2_.func_181682_g(), p_209579_2_.func_187966_f());
      modelblock.field_178317_b = p_209579_2_.field_178317_b;
      return modelblock;
   }

   private List<BlockPart> func_178394_a(int p_178394_1_, String p_178394_2_, TextureAtlasSprite p_178394_3_) {
      Map<EnumFacing, BlockPartFace> map = Maps.newHashMap();
      map.put(EnumFacing.SOUTH, new BlockPartFace(
              null, p_178394_1_, p_178394_2_, new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
      map.put(EnumFacing.NORTH, new BlockPartFace(
              null, p_178394_1_, p_178394_2_, new BlockFaceUV(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
      List<BlockPart> list = Lists.newArrayList();
      list.add(new BlockPart(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), map, null, true));
      list.addAll(this.func_178397_a(p_178394_3_, p_178394_2_, p_178394_1_));
      return list;
   }

   private List<BlockPart> func_178397_a(TextureAtlasSprite p_178397_1_, String p_178397_2_, int p_178397_3_) {
      float f = (float)p_178397_1_.getWidth();
      float f1 = (float)p_178397_1_.getHeight();
      List<BlockPart> list = Lists.newArrayList();

      for(ItemModelGenerator.Span itemmodelgenerator$span : this.func_178393_a(p_178397_1_)) {
         float f2 = 0.0F;
         float f3 = 0.0F;
         float f4 = 0.0F;
         float f5 = 0.0F;
         float f6 = 0.0F;
         float f7 = 0.0F;
         float f8 = 0.0F;
         float f9 = 0.0F;
         float f10 = 0.0F;
         float f11 = 0.0F;
         float f12 = (float)itemmodelgenerator$span.func_178385_b();
         float f13 = (float)itemmodelgenerator$span.func_178384_c();
         float f14 = (float)itemmodelgenerator$span.func_178381_d();
         ItemModelGenerator.SpanFacing itemmodelgenerator$spanfacing = itemmodelgenerator$span.func_178383_a();
         switch(itemmodelgenerator$spanfacing) {
         case UP:
            f6 = f12;
            f2 = f12;
            f4 = f7 = f13 + 1.0F;
            f8 = f14;
            f3 = f14;
            f9 = f14;
            f5 = f14;
            f10 = 16.0F / f;
            f11 = 16.0F / (f1 - 1.0F);
            break;
         case DOWN:
            f9 = f14;
            f8 = f14;
            f6 = f12;
            f2 = f12;
            f4 = f7 = f13 + 1.0F;
            f3 = f14 + 1.0F;
            f5 = f14 + 1.0F;
            f10 = 16.0F / f;
            f11 = 16.0F / (f1 - 1.0F);
            break;
         case LEFT:
            f6 = f14;
            f2 = f14;
            f7 = f14;
            f4 = f14;
            f9 = f12;
            f3 = f12;
            f5 = f8 = f13 + 1.0F;
            f10 = 16.0F / (f - 1.0F);
            f11 = 16.0F / f1;
            break;
         case RIGHT:
            f7 = f14;
            f6 = f14;
            f2 = f14 + 1.0F;
            f4 = f14 + 1.0F;
            f9 = f12;
            f3 = f12;
            f5 = f8 = f13 + 1.0F;
            f10 = 16.0F / (f - 1.0F);
            f11 = 16.0F / f1;
         }

         float f15 = 16.0F / f;
         float f16 = 16.0F / f1;
         f2 = f2 * f15;
         f4 = f4 * f15;
         f3 = f3 * f16;
         f5 = f5 * f16;
         f3 = 16.0F - f3;
         f5 = 16.0F - f5;
         f6 = f6 * f10;
         f7 = f7 * f10;
         f8 = f8 * f11;
         f9 = f9 * f11;
         Map<EnumFacing, BlockPartFace> map = Maps.newHashMap();
         map.put(itemmodelgenerator$spanfacing.func_178367_a(), new BlockPartFace(
                 null, p_178397_3_, p_178397_2_, new BlockFaceUV(new float[]{f6, f8, f7, f9}, 0)));
         switch(itemmodelgenerator$spanfacing) {
         case UP:
            list.add(new BlockPart(new Vector3f(f2, f3, 7.5F), new Vector3f(f4, f3, 8.5F), map, null, true));
            break;
         case DOWN:
            list.add(new BlockPart(new Vector3f(f2, f5, 7.5F), new Vector3f(f4, f5, 8.5F), map, null, true));
            break;
         case LEFT:
            list.add(new BlockPart(new Vector3f(f2, f3, 7.5F), new Vector3f(f2, f5, 8.5F), map, null, true));
            break;
         case RIGHT:
            list.add(new BlockPart(new Vector3f(f4, f3, 7.5F), new Vector3f(f4, f5, 8.5F), map, null, true));
         }
      }

      return list;
   }

   private List<ItemModelGenerator.Span> func_178393_a(TextureAtlasSprite p_178393_1_) {
      int i = p_178393_1_.getWidth();
      int j = p_178393_1_.getHeight();
      List<ItemModelGenerator.Span> list = Lists.newArrayList();

      for(int k = 0; k < p_178393_1_.getFrameCount(); ++k) {
         for(int l = 0; l < j; ++l) {
            for(int i1 = 0; i1 < i; ++i1) {
               boolean flag = !this.func_199339_a(p_178393_1_, k, i1, l, i, j);
               this.func_199338_a(ItemModelGenerator.SpanFacing.UP, list, p_178393_1_, k, i1, l, i, j, flag);
               this.func_199338_a(ItemModelGenerator.SpanFacing.DOWN, list, p_178393_1_, k, i1, l, i, j, flag);
               this.func_199338_a(ItemModelGenerator.SpanFacing.LEFT, list, p_178393_1_, k, i1, l, i, j, flag);
               this.func_199338_a(ItemModelGenerator.SpanFacing.RIGHT, list, p_178393_1_, k, i1, l, i, j, flag);
            }
         }
      }

      return list;
   }

   private void func_199338_a(ItemModelGenerator.SpanFacing p_199338_1_, List<ItemModelGenerator.Span> p_199338_2_, TextureAtlasSprite p_199338_3_, int p_199338_4_, int p_199338_5_, int p_199338_6_, int p_199338_7_, int p_199338_8_, boolean p_199338_9_) {
      boolean flag = this.func_199339_a(p_199338_3_, p_199338_4_, p_199338_5_ + p_199338_1_.func_178372_b(), p_199338_6_ + p_199338_1_.func_178371_c(), p_199338_7_, p_199338_8_) && p_199338_9_;
      if (flag) {
         this.func_178395_a(p_199338_2_, p_199338_1_, p_199338_5_, p_199338_6_);
      }

   }

   private void func_178395_a(List<ItemModelGenerator.Span> p_178395_1_, ItemModelGenerator.SpanFacing p_178395_2_, int p_178395_3_, int p_178395_4_) {
      ItemModelGenerator.Span itemmodelgenerator$span = null;

      for(ItemModelGenerator.Span itemmodelgenerator$span1 : p_178395_1_) {
         if (itemmodelgenerator$span1.func_178383_a() == p_178395_2_) {
            int i = p_178395_2_.func_178369_d() ? p_178395_4_ : p_178395_3_;
            if (itemmodelgenerator$span1.func_178381_d() == i) {
               itemmodelgenerator$span = itemmodelgenerator$span1;
               break;
            }
         }
      }

      int j = p_178395_2_.func_178369_d() ? p_178395_4_ : p_178395_3_;
      int k = p_178395_2_.func_178369_d() ? p_178395_3_ : p_178395_4_;
      if (itemmodelgenerator$span == null) {
         p_178395_1_.add(new ItemModelGenerator.Span(p_178395_2_, k, j));
      } else {
         itemmodelgenerator$span.func_178382_a(k);
      }

   }

   private boolean func_199339_a(TextureAtlasSprite p_199339_1_, int p_199339_2_, int p_199339_3_, int p_199339_4_, int p_199339_5_, int p_199339_6_) {
      return p_199339_3_ < 0 || p_199339_4_ < 0 || p_199339_3_ >= p_199339_5_ || p_199339_4_ >= p_199339_6_ || p_199339_1_.isPixelTransparent(
              p_199339_2_, p_199339_3_, p_199339_4_);
   }

   @OnlyIn(Dist.CLIENT)
   static class Span {
      private final ItemModelGenerator.SpanFacing field_178389_a;
      private int field_178387_b;
      private int field_178388_c;
      private final int field_178386_d;

      public Span(ItemModelGenerator.SpanFacing p_i46216_1_, int p_i46216_2_, int p_i46216_3_) {
         this.field_178389_a = p_i46216_1_;
         this.field_178387_b = p_i46216_2_;
         this.field_178388_c = p_i46216_2_;
         this.field_178386_d = p_i46216_3_;
      }

      public void func_178382_a(int p_178382_1_) {
         if (p_178382_1_ < this.field_178387_b) {
            this.field_178387_b = p_178382_1_;
         } else if (p_178382_1_ > this.field_178388_c) {
            this.field_178388_c = p_178382_1_;
         }

      }

      public ItemModelGenerator.SpanFacing func_178383_a() {
         return this.field_178389_a;
      }

      public int func_178385_b() {
         return this.field_178387_b;
      }

      public int func_178384_c() {
         return this.field_178388_c;
      }

      public int func_178381_d() {
         return this.field_178386_d;
      }
   }

   @OnlyIn(Dist.CLIENT)
   enum SpanFacing {
      UP(EnumFacing.UP, 0, -1),
      DOWN(EnumFacing.DOWN, 0, 1),
      LEFT(EnumFacing.EAST, -1, 0),
      RIGHT(EnumFacing.WEST, 1, 0);

      private final EnumFacing field_178376_e;
      private final int field_178373_f;
      private final int field_178374_g;

      SpanFacing(EnumFacing p_i46215_3_, int p_i46215_4_, int p_i46215_5_) {
         this.field_178376_e = p_i46215_3_;
         this.field_178373_f = p_i46215_4_;
         this.field_178374_g = p_i46215_5_;
      }

      public EnumFacing func_178367_a() {
         return this.field_178376_e;
      }

      public int func_178372_b() {
         return this.field_178373_f;
      }

      public int func_178371_c() {
         return this.field_178374_g;
      }

      private boolean func_178369_d() {
         return this == DOWN || this == UP;
      }
   }
}
