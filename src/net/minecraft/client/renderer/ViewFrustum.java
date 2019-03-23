package net.minecraft.client.renderer;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ViewFrustum {
   protected final WorldRenderer renderGlobal;
   protected final World world;
   protected int countChunksY;
   protected int countChunksX;
   protected int countChunksZ;
   public RenderChunk[] renderChunks;

   public ViewFrustum(World p_i46246_1_, int p_i46246_2_, WorldRenderer p_i46246_3_, IRenderChunkFactory p_i46246_4_) {
      this.renderGlobal = p_i46246_3_;
      this.world = p_i46246_1_;
      this.setCountChunksXYZ(p_i46246_2_);
      this.createRenderChunks(p_i46246_4_);
   }

   protected void createRenderChunks(IRenderChunkFactory p_178158_1_) {
      int i = this.countChunksX * this.countChunksY * this.countChunksZ;
      this.renderChunks = new RenderChunk[i];

      for(int j = 0; j < this.countChunksX; ++j) {
         for(int k = 0; k < this.countChunksY; ++k) {
            for(int l = 0; l < this.countChunksZ; ++l) {
               int i1 = this.func_212478_a(j, k, l);
               this.renderChunks[i1] = p_178158_1_.create(this.world, this.renderGlobal);
               this.renderChunks[i1].setPosition(j * 16, k * 16, l * 16);
            }
         }
      }

   }

   public void deleteGlResources() {
      for(RenderChunk renderchunk : this.renderChunks) {
         renderchunk.deleteGlResources();
      }

   }

   private int func_212478_a(int p_212478_1_, int p_212478_2_, int p_212478_3_) {
      return (p_212478_3_ * this.countChunksY + p_212478_2_) * this.countChunksX + p_212478_1_;
   }

   protected void setCountChunksXYZ(int p_178159_1_) {
      int i = p_178159_1_ * 2 + 1;
      this.countChunksX = i;
      this.countChunksY = 16;
      this.countChunksZ = i;
   }

   public void updateChunkPositions(double p_178163_1_, double p_178163_3_) {
      int i = MathHelper.floor(p_178163_1_) - 8;
      int j = MathHelper.floor(p_178163_3_) - 8;
      int k = this.countChunksX * 16;

      for(int l = 0; l < this.countChunksX; ++l) {
         int i1 = this.getBaseCoordinate(i, k, l);

         for(int j1 = 0; j1 < this.countChunksZ; ++j1) {
            int k1 = this.getBaseCoordinate(j, k, j1);

            for(int l1 = 0; l1 < this.countChunksY; ++l1) {
               int i2 = l1 * 16;
               RenderChunk renderchunk = this.renderChunks[this.func_212478_a(l, l1, j1)];
               renderchunk.setPosition(i1, i2, k1);
            }
         }
      }

   }

   private int getBaseCoordinate(int p_178157_1_, int p_178157_2_, int p_178157_3_) {
      int i = p_178157_3_ * 16;
      int j = i - p_178157_1_ + p_178157_2_ / 2;
      if (j < 0) {
         j -= p_178157_2_ - 1;
      }

      return i - j / p_178157_2_ * p_178157_2_;
   }

   public void markBlocksForUpdate(int p_187474_1_, int p_187474_2_, int p_187474_3_, int p_187474_4_, int p_187474_5_, int p_187474_6_, boolean p_187474_7_) {
      int i = MathHelper.intFloorDiv(p_187474_1_, 16);
      int j = MathHelper.intFloorDiv(p_187474_2_, 16);
      int k = MathHelper.intFloorDiv(p_187474_3_, 16);
      int l = MathHelper.intFloorDiv(p_187474_4_, 16);
      int i1 = MathHelper.intFloorDiv(p_187474_5_, 16);
      int j1 = MathHelper.intFloorDiv(p_187474_6_, 16);

      for(int k1 = i; k1 <= l; ++k1) {
         int l1 = MathHelper.normalizeAngle(k1, this.countChunksX);

         for(int i2 = j; i2 <= i1; ++i2) {
            int j2 = MathHelper.normalizeAngle(i2, this.countChunksY);

            for(int k2 = k; k2 <= j1; ++k2) {
               int l2 = MathHelper.normalizeAngle(k2, this.countChunksZ);
               RenderChunk renderchunk = this.renderChunks[this.func_212478_a(l1, j2, l2)];
               renderchunk.setNeedsUpdate(p_187474_7_);
            }
         }
      }

   }

   @Nullable
   protected RenderChunk getRenderChunk(BlockPos p_178161_1_) {
      int i = MathHelper.intFloorDiv(p_178161_1_.getX(), 16);
      int j = MathHelper.intFloorDiv(p_178161_1_.getY(), 16);
      int k = MathHelper.intFloorDiv(p_178161_1_.getZ(), 16);
      if (j >= 0 && j < this.countChunksY) {
         i = MathHelper.normalizeAngle(i, this.countChunksX);
         k = MathHelper.normalizeAngle(k, this.countChunksZ);
         return this.renderChunks[this.func_212478_a(i, j, k)];
      } else {
         return null;
      }
   }
}
