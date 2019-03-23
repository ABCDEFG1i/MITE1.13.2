package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockModelRenderer {
   private final BlockColors blockColors;
   private static final ThreadLocal<Object2IntLinkedOpenHashMap<BlockPos>> field_210267_b = ThreadLocal.withInitial(() -> {
      Object2IntLinkedOpenHashMap<BlockPos> object2intlinkedopenhashmap = new Object2IntLinkedOpenHashMap<BlockPos>(50) {
         protected void rehash(int p_rehash_1_) {
         }
      };
      object2intlinkedopenhashmap.defaultReturnValue(Integer.MAX_VALUE);
      return object2intlinkedopenhashmap;
   });
   private static final ThreadLocal<Boolean> field_211848_c = ThreadLocal.withInitial(() -> {
      return false;
   });

   public BlockModelRenderer(BlockColors p_i46575_1_) {
      this.blockColors = p_i46575_1_;
   }

   public boolean func_199324_a(IWorldReader p_199324_1_, IBakedModel p_199324_2_, IBlockState p_199324_3_, BlockPos p_199324_4_, BufferBuilder p_199324_5_, boolean p_199324_6_, Random p_199324_7_, long p_199324_8_) {
      boolean flag = Minecraft.isAmbientOcclusionEnabled() && p_199324_3_.getLightValue() == 0 && p_199324_2_.func_177555_b();

      try {
         return flag ? this.func_199326_b(p_199324_1_, p_199324_2_, p_199324_3_, p_199324_4_, p_199324_5_, p_199324_6_, p_199324_7_, p_199324_8_) : this.func_199325_c(p_199324_1_, p_199324_2_, p_199324_3_, p_199324_4_, p_199324_5_, p_199324_6_, p_199324_7_, p_199324_8_);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block model");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Block model being tesselated");
         CrashReportCategory.addBlockInfo(crashreportcategory, p_199324_4_, p_199324_3_);
         crashreportcategory.addCrashSection("Using AO", flag);
         throw new ReportedException(crashreport);
      }
   }

   public boolean func_199326_b(IWorldReader p_199326_1_, IBakedModel p_199326_2_, IBlockState p_199326_3_, BlockPos p_199326_4_, BufferBuilder p_199326_5_, boolean p_199326_6_, Random p_199326_7_, long p_199326_8_) {
      boolean flag = false;
      float[] afloat = new float[EnumFacing.values().length * 2];
      BitSet bitset = new BitSet(3);
      BlockModelRenderer.AmbientOcclusionFace blockmodelrenderer$ambientocclusionface = new BlockModelRenderer.AmbientOcclusionFace();

      for(EnumFacing enumfacing : EnumFacing.values()) {
         p_199326_7_.setSeed(p_199326_8_);
         List<BakedQuad> list = p_199326_2_.func_200117_a(p_199326_3_, enumfacing, p_199326_7_);
         if (!list.isEmpty() && (!p_199326_6_ || Block.shouldSideBeRendered(p_199326_3_, p_199326_1_, p_199326_4_, enumfacing))) {
            this.renderQuadsSmooth(p_199326_1_, p_199326_3_, p_199326_4_, p_199326_5_, list, afloat, bitset, blockmodelrenderer$ambientocclusionface);
            flag = true;
         }
      }

      p_199326_7_.setSeed(p_199326_8_);
      List<BakedQuad> list1 = p_199326_2_.func_200117_a(p_199326_3_, (EnumFacing)null, p_199326_7_);
      if (!list1.isEmpty()) {
         this.renderQuadsSmooth(p_199326_1_, p_199326_3_, p_199326_4_, p_199326_5_, list1, afloat, bitset, blockmodelrenderer$ambientocclusionface);
         flag = true;
      }

      return flag;
   }

   public boolean func_199325_c(IWorldReader p_199325_1_, IBakedModel p_199325_2_, IBlockState p_199325_3_, BlockPos p_199325_4_, BufferBuilder p_199325_5_, boolean p_199325_6_, Random p_199325_7_, long p_199325_8_) {
      boolean flag = false;
      BitSet bitset = new BitSet(3);

      for(EnumFacing enumfacing : EnumFacing.values()) {
         p_199325_7_.setSeed(p_199325_8_);
         List<BakedQuad> list = p_199325_2_.func_200117_a(p_199325_3_, enumfacing, p_199325_7_);
         if (!list.isEmpty() && (!p_199325_6_ || Block.shouldSideBeRendered(p_199325_3_, p_199325_1_, p_199325_4_, enumfacing))) {
            int i = p_199325_3_.getPackedLightmapCoords(p_199325_1_, p_199325_4_.offset(enumfacing));
            this.renderQuadsFlat(p_199325_1_, p_199325_3_, p_199325_4_, i, false, p_199325_5_, list, bitset);
            flag = true;
         }
      }

      p_199325_7_.setSeed(p_199325_8_);
      List<BakedQuad> list1 = p_199325_2_.func_200117_a(p_199325_3_, (EnumFacing)null, p_199325_7_);
      if (!list1.isEmpty()) {
         this.renderQuadsFlat(p_199325_1_, p_199325_3_, p_199325_4_, -1, true, p_199325_5_, list1, bitset);
         flag = true;
      }

      return flag;
   }

   private void renderQuadsSmooth(IWorldReader p_187492_1_, IBlockState p_187492_2_, BlockPos p_187492_3_, BufferBuilder p_187492_4_, List<BakedQuad> p_187492_5_, float[] p_187492_6_, BitSet p_187492_7_, BlockModelRenderer.AmbientOcclusionFace p_187492_8_) {
      Vec3d vec3d = p_187492_2_.getOffset(p_187492_1_, p_187492_3_);
      double d0 = (double)p_187492_3_.getX() + vec3d.x;
      double d1 = (double)p_187492_3_.getY() + vec3d.y;
      double d2 = (double)p_187492_3_.getZ() + vec3d.z;
      int i = 0;

      for(int j = p_187492_5_.size(); i < j; ++i) {
         BakedQuad bakedquad = p_187492_5_.get(i);
         this.fillQuadBounds(p_187492_2_, bakedquad.func_178209_a(), bakedquad.func_178210_d(), p_187492_6_, p_187492_7_);
         p_187492_8_.updateVertexBrightness(p_187492_1_, p_187492_2_, p_187492_3_, bakedquad.func_178210_d(), p_187492_6_, p_187492_7_);
         p_187492_4_.addVertexData(bakedquad.func_178209_a());
         p_187492_4_.putBrightness4(p_187492_8_.vertexBrightness[0], p_187492_8_.vertexBrightness[1], p_187492_8_.vertexBrightness[2], p_187492_8_.vertexBrightness[3]);
         if (bakedquad.func_178212_b()) {
            int k = this.blockColors.getColor(p_187492_2_, p_187492_1_, p_187492_3_, bakedquad.func_178211_c());
            float f = (float)(k >> 16 & 255) / 255.0F;
            float f1 = (float)(k >> 8 & 255) / 255.0F;
            float f2 = (float)(k & 255) / 255.0F;
            p_187492_4_.putColorMultiplier(p_187492_8_.vertexColorMultiplier[0] * f, p_187492_8_.vertexColorMultiplier[0] * f1, p_187492_8_.vertexColorMultiplier[0] * f2, 4);
            p_187492_4_.putColorMultiplier(p_187492_8_.vertexColorMultiplier[1] * f, p_187492_8_.vertexColorMultiplier[1] * f1, p_187492_8_.vertexColorMultiplier[1] * f2, 3);
            p_187492_4_.putColorMultiplier(p_187492_8_.vertexColorMultiplier[2] * f, p_187492_8_.vertexColorMultiplier[2] * f1, p_187492_8_.vertexColorMultiplier[2] * f2, 2);
            p_187492_4_.putColorMultiplier(p_187492_8_.vertexColorMultiplier[3] * f, p_187492_8_.vertexColorMultiplier[3] * f1, p_187492_8_.vertexColorMultiplier[3] * f2, 1);
         } else {
            p_187492_4_.putColorMultiplier(p_187492_8_.vertexColorMultiplier[0], p_187492_8_.vertexColorMultiplier[0], p_187492_8_.vertexColorMultiplier[0], 4);
            p_187492_4_.putColorMultiplier(p_187492_8_.vertexColorMultiplier[1], p_187492_8_.vertexColorMultiplier[1], p_187492_8_.vertexColorMultiplier[1], 3);
            p_187492_4_.putColorMultiplier(p_187492_8_.vertexColorMultiplier[2], p_187492_8_.vertexColorMultiplier[2], p_187492_8_.vertexColorMultiplier[2], 2);
            p_187492_4_.putColorMultiplier(p_187492_8_.vertexColorMultiplier[3], p_187492_8_.vertexColorMultiplier[3], p_187492_8_.vertexColorMultiplier[3], 1);
         }

         p_187492_4_.putPosition(d0, d1, d2);
      }

   }

   private void fillQuadBounds(IBlockState p_187494_1_, int[] p_187494_2_, EnumFacing p_187494_3_, @Nullable float[] p_187494_4_, BitSet p_187494_5_) {
      float f = 32.0F;
      float f1 = 32.0F;
      float f2 = 32.0F;
      float f3 = -32.0F;
      float f4 = -32.0F;
      float f5 = -32.0F;

      for(int i = 0; i < 4; ++i) {
         float f6 = Float.intBitsToFloat(p_187494_2_[i * 7]);
         float f7 = Float.intBitsToFloat(p_187494_2_[i * 7 + 1]);
         float f8 = Float.intBitsToFloat(p_187494_2_[i * 7 + 2]);
         f = Math.min(f, f6);
         f1 = Math.min(f1, f7);
         f2 = Math.min(f2, f8);
         f3 = Math.max(f3, f6);
         f4 = Math.max(f4, f7);
         f5 = Math.max(f5, f8);
      }

      if (p_187494_4_ != null) {
         p_187494_4_[EnumFacing.WEST.getIndex()] = f;
         p_187494_4_[EnumFacing.EAST.getIndex()] = f3;
         p_187494_4_[EnumFacing.DOWN.getIndex()] = f1;
         p_187494_4_[EnumFacing.UP.getIndex()] = f4;
         p_187494_4_[EnumFacing.NORTH.getIndex()] = f2;
         p_187494_4_[EnumFacing.SOUTH.getIndex()] = f5;
         int j = EnumFacing.values().length;
         p_187494_4_[EnumFacing.WEST.getIndex() + j] = 1.0F - f;
         p_187494_4_[EnumFacing.EAST.getIndex() + j] = 1.0F - f3;
         p_187494_4_[EnumFacing.DOWN.getIndex() + j] = 1.0F - f1;
         p_187494_4_[EnumFacing.UP.getIndex() + j] = 1.0F - f4;
         p_187494_4_[EnumFacing.NORTH.getIndex() + j] = 1.0F - f2;
         p_187494_4_[EnumFacing.SOUTH.getIndex() + j] = 1.0F - f5;
      }

      float f9 = 1.0E-4F;
      float f10 = 0.9999F;
      switch(p_187494_3_) {
      case DOWN:
         p_187494_5_.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
         p_187494_5_.set(0, (f1 < 1.0E-4F || p_187494_1_.isFullCube()) && f1 == f4);
         break;
      case UP:
         p_187494_5_.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
         p_187494_5_.set(0, (f4 > 0.9999F || p_187494_1_.isFullCube()) && f1 == f4);
         break;
      case NORTH:
         p_187494_5_.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
         p_187494_5_.set(0, (f2 < 1.0E-4F || p_187494_1_.isFullCube()) && f2 == f5);
         break;
      case SOUTH:
         p_187494_5_.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
         p_187494_5_.set(0, (f5 > 0.9999F || p_187494_1_.isFullCube()) && f2 == f5);
         break;
      case WEST:
         p_187494_5_.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
         p_187494_5_.set(0, (f < 1.0E-4F || p_187494_1_.isFullCube()) && f == f3);
         break;
      case EAST:
         p_187494_5_.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
         p_187494_5_.set(0, (f3 > 0.9999F || p_187494_1_.isFullCube()) && f == f3);
      }

   }

   private void renderQuadsFlat(IWorldReader p_187496_1_, IBlockState p_187496_2_, BlockPos p_187496_3_, int p_187496_4_, boolean p_187496_5_, BufferBuilder p_187496_6_, List<BakedQuad> p_187496_7_, BitSet p_187496_8_) {
      Vec3d vec3d = p_187496_2_.getOffset(p_187496_1_, p_187496_3_);
      double d0 = (double)p_187496_3_.getX() + vec3d.x;
      double d1 = (double)p_187496_3_.getY() + vec3d.y;
      double d2 = (double)p_187496_3_.getZ() + vec3d.z;
      int i = 0;

      for(int j = p_187496_7_.size(); i < j; ++i) {
         BakedQuad bakedquad = p_187496_7_.get(i);
         if (p_187496_5_) {
            this.fillQuadBounds(p_187496_2_, bakedquad.func_178209_a(), bakedquad.func_178210_d(), (float[])null, p_187496_8_);
            BlockPos blockpos = p_187496_8_.get(0) ? p_187496_3_.offset(bakedquad.func_178210_d()) : p_187496_3_;
            p_187496_4_ = p_187496_2_.getPackedLightmapCoords(p_187496_1_, blockpos);
         }

         p_187496_6_.addVertexData(bakedquad.func_178209_a());
         p_187496_6_.putBrightness4(p_187496_4_, p_187496_4_, p_187496_4_, p_187496_4_);
         if (bakedquad.func_178212_b()) {
            int k = this.blockColors.getColor(p_187496_2_, p_187496_1_, p_187496_3_, bakedquad.func_178211_c());
            float f = (float)(k >> 16 & 255) / 255.0F;
            float f1 = (float)(k >> 8 & 255) / 255.0F;
            float f2 = (float)(k & 255) / 255.0F;
            p_187496_6_.putColorMultiplier(f, f1, f2, 4);
            p_187496_6_.putColorMultiplier(f, f1, f2, 3);
            p_187496_6_.putColorMultiplier(f, f1, f2, 2);
            p_187496_6_.putColorMultiplier(f, f1, f2, 1);
         }

         p_187496_6_.putPosition(d0, d1, d2);
      }

   }

   public void func_178262_a(IBakedModel p_178262_1_, float p_178262_2_, float p_178262_3_, float p_178262_4_, float p_178262_5_) {
      this.func_187495_a((IBlockState)null, p_178262_1_, p_178262_2_, p_178262_3_, p_178262_4_, p_178262_5_);
   }

   public void func_187495_a(@Nullable IBlockState p_187495_1_, IBakedModel p_187495_2_, float p_187495_3_, float p_187495_4_, float p_187495_5_, float p_187495_6_) {
      Random random = new Random();
      long i = 42L;

      for(EnumFacing enumfacing : EnumFacing.values()) {
         random.setSeed(42L);
         this.renderModelBrightnessColorQuads(p_187495_3_, p_187495_4_, p_187495_5_, p_187495_6_, p_187495_2_.func_200117_a(p_187495_1_, enumfacing, random));
      }

      random.setSeed(42L);
      this.renderModelBrightnessColorQuads(p_187495_3_, p_187495_4_, p_187495_5_, p_187495_6_, p_187495_2_.func_200117_a(p_187495_1_, (EnumFacing)null, random));
   }

   public void func_178266_a(IBakedModel p_178266_1_, IBlockState p_178266_2_, float p_178266_3_, boolean p_178266_4_) {
      GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
      int i = this.blockColors.getColor(p_178266_2_, (IWorldReaderBase)null, (BlockPos)null, 0);
      float f = (float)(i >> 16 & 255) / 255.0F;
      float f1 = (float)(i >> 8 & 255) / 255.0F;
      float f2 = (float)(i & 255) / 255.0F;
      if (!p_178266_4_) {
         GlStateManager.color4f(p_178266_3_, p_178266_3_, p_178266_3_, 1.0F);
      }

      this.func_187495_a(p_178266_2_, p_178266_1_, p_178266_3_, f, f1, f2);
   }

   private void renderModelBrightnessColorQuads(float p_178264_1_, float p_178264_2_, float p_178264_3_, float p_178264_4_, List<BakedQuad> p_178264_5_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      int i = 0;

      for(int j = p_178264_5_.size(); i < j; ++i) {
         BakedQuad bakedquad = p_178264_5_.get(i);
         bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
         bufferbuilder.addVertexData(bakedquad.func_178209_a());
         if (bakedquad.func_178212_b()) {
            bufferbuilder.putColorRGB_F4(p_178264_2_ * p_178264_1_, p_178264_3_ * p_178264_1_, p_178264_4_ * p_178264_1_);
         } else {
            bufferbuilder.putColorRGB_F4(p_178264_1_, p_178264_1_, p_178264_1_);
         }

         Vec3i vec3i = bakedquad.func_178210_d().getDirectionVec();
         bufferbuilder.putNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
         tessellator.draw();
      }

   }

   public static void func_211847_a() {
      field_211848_c.set(true);
   }

   public static void func_210266_a() {
      field_210267_b.get().clear();
      field_211848_c.set(false);
   }

   private static int func_210264_b(IBlockState p_210264_0_, IWorldReader p_210264_1_, BlockPos p_210264_2_) {
      Boolean obool = field_211848_c.get();
      Object2IntLinkedOpenHashMap<BlockPos> object2intlinkedopenhashmap = null;
      if (obool) {
         object2intlinkedopenhashmap = field_210267_b.get();
         int i = object2intlinkedopenhashmap.getInt(p_210264_2_);
         if (i != Integer.MAX_VALUE) {
            return i;
         }
      }

      int j = p_210264_0_.getPackedLightmapCoords(p_210264_1_, p_210264_2_);
      if (object2intlinkedopenhashmap != null) {
         if (object2intlinkedopenhashmap.size() == 50) {
            object2intlinkedopenhashmap.removeFirstInt();
         }

         object2intlinkedopenhashmap.put(p_210264_2_.toImmutable(), j);
      }

      return j;
   }

   @OnlyIn(Dist.CLIENT)
   class AmbientOcclusionFace {
      private final float[] vertexColorMultiplier = new float[4];
      private final int[] vertexBrightness = new int[4];

      public void updateVertexBrightness(IWorldReader p_187491_1_, IBlockState p_187491_2_, BlockPos p_187491_3_, EnumFacing p_187491_4_, float[] p_187491_5_, BitSet p_187491_6_) {
         BlockPos blockpos = p_187491_6_.get(0) ? p_187491_3_.offset(p_187491_4_) : p_187491_3_;
         BlockModelRenderer.EnumNeighborInfo blockmodelrenderer$enumneighborinfo = BlockModelRenderer.EnumNeighborInfo.getNeighbourInfo(p_187491_4_);
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[0]);
         int i = BlockModelRenderer.func_210264_b(p_187491_2_, p_187491_1_, blockpos$mutableblockpos);
         float f = p_187491_1_.getBlockState(blockpos$mutableblockpos).getAmbientOcclusionLightValue();
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[1]);
         int j = BlockModelRenderer.func_210264_b(p_187491_2_, p_187491_1_, blockpos$mutableblockpos);
         float f1 = p_187491_1_.getBlockState(blockpos$mutableblockpos).getAmbientOcclusionLightValue();
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[2]);
         int k = BlockModelRenderer.func_210264_b(p_187491_2_, p_187491_1_, blockpos$mutableblockpos);
         float f2 = p_187491_1_.getBlockState(blockpos$mutableblockpos).getAmbientOcclusionLightValue();
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[3]);
         int l = BlockModelRenderer.func_210264_b(p_187491_2_, p_187491_1_, blockpos$mutableblockpos);
         float f3 = p_187491_1_.getBlockState(blockpos$mutableblockpos).getAmbientOcclusionLightValue();
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[0]).move(p_187491_4_);
         boolean flag = p_187491_1_.getBlockState(blockpos$mutableblockpos).getOpacity(p_187491_1_, blockpos$mutableblockpos) == 0;
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[1]).move(p_187491_4_);
         boolean flag1 = p_187491_1_.getBlockState(blockpos$mutableblockpos).getOpacity(p_187491_1_, blockpos$mutableblockpos) == 0;
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[2]).move(p_187491_4_);
         boolean flag2 = p_187491_1_.getBlockState(blockpos$mutableblockpos).getOpacity(p_187491_1_, blockpos$mutableblockpos) == 0;
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[3]).move(p_187491_4_);
         boolean flag3 = p_187491_1_.getBlockState(blockpos$mutableblockpos).getOpacity(p_187491_1_, blockpos$mutableblockpos) == 0;
         float f4;
         int i1;
         if (!flag2 && !flag) {
            f4 = f;
            i1 = i;
         } else {
            blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[0]).move(blockmodelrenderer$enumneighborinfo.corners[2]);
            f4 = p_187491_1_.getBlockState(blockpos$mutableblockpos).getAmbientOcclusionLightValue();
            i1 = BlockModelRenderer.func_210264_b(p_187491_2_, p_187491_1_, blockpos$mutableblockpos);
         }

         float f5;
         int j1;
         if (!flag3 && !flag) {
            f5 = f;
            j1 = i;
         } else {
            blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[0]).move(blockmodelrenderer$enumneighborinfo.corners[3]);
            f5 = p_187491_1_.getBlockState(blockpos$mutableblockpos).getAmbientOcclusionLightValue();
            j1 = BlockModelRenderer.func_210264_b(p_187491_2_, p_187491_1_, blockpos$mutableblockpos);
         }

         float f6;
         int k1;
         if (!flag2 && !flag1) {
            f6 = f1;
            k1 = j;
         } else {
            blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[1]).move(blockmodelrenderer$enumneighborinfo.corners[2]);
            f6 = p_187491_1_.getBlockState(blockpos$mutableblockpos).getAmbientOcclusionLightValue();
            k1 = BlockModelRenderer.func_210264_b(p_187491_2_, p_187491_1_, blockpos$mutableblockpos);
         }

         float f7;
         int l1;
         if (!flag3 && !flag1) {
            f7 = f1;
            l1 = j;
         } else {
            blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[1]).move(blockmodelrenderer$enumneighborinfo.corners[3]);
            f7 = p_187491_1_.getBlockState(blockpos$mutableblockpos).getAmbientOcclusionLightValue();
            l1 = BlockModelRenderer.func_210264_b(p_187491_2_, p_187491_1_, blockpos$mutableblockpos);
         }

         int i2 = BlockModelRenderer.func_210264_b(p_187491_2_, p_187491_1_, p_187491_3_);
         blockpos$mutableblockpos.setPos(p_187491_3_).move(p_187491_4_);
         if (p_187491_6_.get(0) || !p_187491_1_.getBlockState(blockpos$mutableblockpos).isOpaqueCube(p_187491_1_, blockpos$mutableblockpos)) {
            i2 = BlockModelRenderer.func_210264_b(p_187491_2_, p_187491_1_, blockpos$mutableblockpos);
         }

         float f8 = p_187491_6_.get(0) ? p_187491_1_.getBlockState(blockpos).getAmbientOcclusionLightValue() : p_187491_1_.getBlockState(p_187491_3_).getAmbientOcclusionLightValue();
         BlockModelRenderer.VertexTranslations blockmodelrenderer$vertextranslations = BlockModelRenderer.VertexTranslations.getVertexTranslations(p_187491_4_);
         if (p_187491_6_.get(1) && blockmodelrenderer$enumneighborinfo.doNonCubicWeight) {
            float f29 = (f3 + f + f5 + f8) * 0.25F;
            float f30 = (f2 + f + f4 + f8) * 0.25F;
            float f31 = (f2 + f1 + f6 + f8) * 0.25F;
            float f32 = (f3 + f1 + f7 + f8) * 0.25F;
            float f13 = p_187491_5_[blockmodelrenderer$enumneighborinfo.vert0Weights[0].shape] * p_187491_5_[blockmodelrenderer$enumneighborinfo.vert0Weights[1].shape];
            float f14 = p_187491_5_[blockmodelrenderer$enumneighborinfo.vert0Weights[2].shape] * p_187491_5_[blockmodelrenderer$enumneighborinfo.vert0Weights[3].shape];
            float f15 = p_187491_5_[blockmodelrenderer$enumneighborinfo.vert0Weights[4].shape] * p_187491_5_[blockmodelrenderer$enumneighborinfo.vert0Weights[5].shape];
            float f16 = p_187491_5_[blockmodelrenderer$enumneighborinfo.vert0Weights[6].shape] * p_187491_5_[blockmodelrenderer$enumneighborinfo.vert0Weights[7].shape];
            float f17 = p_187491_5_[blockmodelrenderer$enumneighborinfo.vert1Weights[0].shape] * p_187491_5_[blockmodelrenderer$enumneighborinfo.vert1Weights[1].shape];
            float f18 = p_187491_5_[blockmodelrenderer$enumneighborinfo.vert1Weights[2].shape] * p_187491_5_[blockmodelrenderer$enumneighborinfo.vert1Weights[3].shape];
            float f19 = p_187491_5_[blockmodelrenderer$enumneighborinfo.vert1Weights[4].shape] * p_187491_5_[blockmodelrenderer$enumneighborinfo.vert1Weights[5].shape];
            float f20 = p_187491_5_[blockmodelrenderer$enumneighborinfo.vert1Weights[6].shape] * p_187491_5_[blockmodelrenderer$enumneighborinfo.vert1Weights[7].shape];
            float f21 = p_187491_5_[blockmodelrenderer$enumneighborinfo.vert2Weights[0].shape] * p_187491_5_[blockmodelrenderer$enumneighborinfo.vert2Weights[1].shape];
            float f22 = p_187491_5_[blockmodelrenderer$enumneighborinfo.vert2Weights[2].shape] * p_187491_5_[blockmodelrenderer$enumneighborinfo.vert2Weights[3].shape];
            float f23 = p_187491_5_[blockmodelrenderer$enumneighborinfo.vert2Weights[4].shape] * p_187491_5_[blockmodelrenderer$enumneighborinfo.vert2Weights[5].shape];
            float f24 = p_187491_5_[blockmodelrenderer$enumneighborinfo.vert2Weights[6].shape] * p_187491_5_[blockmodelrenderer$enumneighborinfo.vert2Weights[7].shape];
            float f25 = p_187491_5_[blockmodelrenderer$enumneighborinfo.vert3Weights[0].shape] * p_187491_5_[blockmodelrenderer$enumneighborinfo.vert3Weights[1].shape];
            float f26 = p_187491_5_[blockmodelrenderer$enumneighborinfo.vert3Weights[2].shape] * p_187491_5_[blockmodelrenderer$enumneighborinfo.vert3Weights[3].shape];
            float f27 = p_187491_5_[blockmodelrenderer$enumneighborinfo.vert3Weights[4].shape] * p_187491_5_[blockmodelrenderer$enumneighborinfo.vert3Weights[5].shape];
            float f28 = p_187491_5_[blockmodelrenderer$enumneighborinfo.vert3Weights[6].shape] * p_187491_5_[blockmodelrenderer$enumneighborinfo.vert3Weights[7].shape];
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert0] = f29 * f13 + f30 * f14 + f31 * f15 + f32 * f16;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert1] = f29 * f17 + f30 * f18 + f31 * f19 + f32 * f20;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert2] = f29 * f21 + f30 * f22 + f31 * f23 + f32 * f24;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert3] = f29 * f25 + f30 * f26 + f31 * f27 + f32 * f28;
            int j2 = this.getAoBrightness(l, i, j1, i2);
            int k2 = this.getAoBrightness(k, i, i1, i2);
            int l2 = this.getAoBrightness(k, j, k1, i2);
            int i3 = this.getAoBrightness(l, j, l1, i2);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert0] = this.getVertexBrightness(j2, k2, l2, i3, f13, f14, f15, f16);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert1] = this.getVertexBrightness(j2, k2, l2, i3, f17, f18, f19, f20);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert2] = this.getVertexBrightness(j2, k2, l2, i3, f21, f22, f23, f24);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert3] = this.getVertexBrightness(j2, k2, l2, i3, f25, f26, f27, f28);
         } else {
            float f9 = (f3 + f + f5 + f8) * 0.25F;
            float f10 = (f2 + f + f4 + f8) * 0.25F;
            float f11 = (f2 + f1 + f6 + f8) * 0.25F;
            float f12 = (f3 + f1 + f7 + f8) * 0.25F;
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert0] = this.getAoBrightness(l, i, j1, i2);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert1] = this.getAoBrightness(k, i, i1, i2);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert2] = this.getAoBrightness(k, j, k1, i2);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert3] = this.getAoBrightness(l, j, l1, i2);
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert0] = f9;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert1] = f10;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert2] = f11;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert3] = f12;
         }

      }

      private int getAoBrightness(int p_147778_1_, int p_147778_2_, int p_147778_3_, int p_147778_4_) {
         if (p_147778_1_ == 0) {
            p_147778_1_ = p_147778_4_;
         }

         if (p_147778_2_ == 0) {
            p_147778_2_ = p_147778_4_;
         }

         if (p_147778_3_ == 0) {
            p_147778_3_ = p_147778_4_;
         }

         return p_147778_1_ + p_147778_2_ + p_147778_3_ + p_147778_4_ >> 2 & 16711935;
      }

      private int getVertexBrightness(int p_178203_1_, int p_178203_2_, int p_178203_3_, int p_178203_4_, float p_178203_5_, float p_178203_6_, float p_178203_7_, float p_178203_8_) {
         int i = (int)((float)(p_178203_1_ >> 16 & 255) * p_178203_5_ + (float)(p_178203_2_ >> 16 & 255) * p_178203_6_ + (float)(p_178203_3_ >> 16 & 255) * p_178203_7_ + (float)(p_178203_4_ >> 16 & 255) * p_178203_8_) & 255;
         int j = (int)((float)(p_178203_1_ & 255) * p_178203_5_ + (float)(p_178203_2_ & 255) * p_178203_6_ + (float)(p_178203_3_ & 255) * p_178203_7_ + (float)(p_178203_4_ & 255) * p_178203_8_) & 255;
         return i << 16 | j;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum EnumNeighborInfo {
      DOWN(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.5F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.SOUTH}),
      UP(new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH}, 1.0F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.SOUTH}),
      NORTH(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST}),
      SOUTH(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST}),
      WEST(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH}),
      EAST(new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH});

      private final EnumFacing[] corners;
      private final boolean doNonCubicWeight;
      private final BlockModelRenderer.Orientation[] vert0Weights;
      private final BlockModelRenderer.Orientation[] vert1Weights;
      private final BlockModelRenderer.Orientation[] vert2Weights;
      private final BlockModelRenderer.Orientation[] vert3Weights;
      private static final BlockModelRenderer.EnumNeighborInfo[] VALUES = Util.make(new BlockModelRenderer.EnumNeighborInfo[6], (p_209260_0_) -> {
         p_209260_0_[EnumFacing.DOWN.getIndex()] = DOWN;
         p_209260_0_[EnumFacing.UP.getIndex()] = UP;
         p_209260_0_[EnumFacing.NORTH.getIndex()] = NORTH;
         p_209260_0_[EnumFacing.SOUTH.getIndex()] = SOUTH;
         p_209260_0_[EnumFacing.WEST.getIndex()] = WEST;
         p_209260_0_[EnumFacing.EAST.getIndex()] = EAST;
      });

      private EnumNeighborInfo(EnumFacing[] p_i46236_3_, float p_i46236_4_, boolean p_i46236_5_, BlockModelRenderer.Orientation[] p_i46236_6_, BlockModelRenderer.Orientation[] p_i46236_7_, BlockModelRenderer.Orientation[] p_i46236_8_, BlockModelRenderer.Orientation[] p_i46236_9_) {
         this.corners = p_i46236_3_;
         this.doNonCubicWeight = p_i46236_5_;
         this.vert0Weights = p_i46236_6_;
         this.vert1Weights = p_i46236_7_;
         this.vert2Weights = p_i46236_8_;
         this.vert3Weights = p_i46236_9_;
      }

      public static BlockModelRenderer.EnumNeighborInfo getNeighbourInfo(EnumFacing p_178273_0_) {
         return VALUES[p_178273_0_.getIndex()];
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Orientation {
      DOWN(EnumFacing.DOWN, false),
      UP(EnumFacing.UP, false),
      NORTH(EnumFacing.NORTH, false),
      SOUTH(EnumFacing.SOUTH, false),
      WEST(EnumFacing.WEST, false),
      EAST(EnumFacing.EAST, false),
      FLIP_DOWN(EnumFacing.DOWN, true),
      FLIP_UP(EnumFacing.UP, true),
      FLIP_NORTH(EnumFacing.NORTH, true),
      FLIP_SOUTH(EnumFacing.SOUTH, true),
      FLIP_WEST(EnumFacing.WEST, true),
      FLIP_EAST(EnumFacing.EAST, true);

      private final int shape;

      private Orientation(EnumFacing p_i46233_3_, boolean p_i46233_4_) {
         this.shape = p_i46233_3_.getIndex() + (p_i46233_4_ ? EnumFacing.values().length : 0);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum VertexTranslations {
      DOWN(0, 1, 2, 3),
      UP(2, 3, 0, 1),
      NORTH(3, 0, 1, 2),
      SOUTH(0, 1, 2, 3),
      WEST(3, 0, 1, 2),
      EAST(1, 2, 3, 0);

      private final int vert0;
      private final int vert1;
      private final int vert2;
      private final int vert3;
      private static final BlockModelRenderer.VertexTranslations[] VALUES = Util.make(new BlockModelRenderer.VertexTranslations[6], (p_209261_0_) -> {
         p_209261_0_[EnumFacing.DOWN.getIndex()] = DOWN;
         p_209261_0_[EnumFacing.UP.getIndex()] = UP;
         p_209261_0_[EnumFacing.NORTH.getIndex()] = NORTH;
         p_209261_0_[EnumFacing.SOUTH.getIndex()] = SOUTH;
         p_209261_0_[EnumFacing.WEST.getIndex()] = WEST;
         p_209261_0_[EnumFacing.EAST.getIndex()] = EAST;
      });

      private VertexTranslations(int p_i46234_3_, int p_i46234_4_, int p_i46234_5_, int p_i46234_6_) {
         this.vert0 = p_i46234_3_;
         this.vert1 = p_i46234_4_;
         this.vert2 = p_i46234_5_;
         this.vert3 = p_i46234_6_;
      }

      public static BlockModelRenderer.VertexTranslations getVertexTranslations(EnumFacing p_178184_0_) {
         return VALUES[p_178184_0_.getIndex()];
      }
   }
}
