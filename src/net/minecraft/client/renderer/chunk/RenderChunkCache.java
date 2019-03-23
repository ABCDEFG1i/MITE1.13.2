package net.minecraft.client.renderer.chunk;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderChunkCache implements IWorldReader {
   protected final int field_212400_a;
   protected final int field_212401_b;
   protected final BlockPos field_212402_c;
   protected final int field_212403_d;
   protected final int field_212404_e;
   protected final int field_212405_f;
   protected final Chunk[][] field_212406_g;
   protected final RenderChunkCache.Entry[] field_212407_h;
   protected final World field_212408_i;

   @Nullable
   public static RenderChunkCache func_212397_a(World p_212397_0_, BlockPos p_212397_1_, BlockPos p_212397_2_, int p_212397_3_) {
      int i = p_212397_1_.getX() - p_212397_3_ >> 4;
      int j = p_212397_1_.getZ() - p_212397_3_ >> 4;
      int k = p_212397_2_.getX() + p_212397_3_ >> 4;
      int l = p_212397_2_.getZ() + p_212397_3_ >> 4;
      Chunk[][] achunk = new Chunk[k - i + 1][l - j + 1];

      for(int i1 = i; i1 <= k; ++i1) {
         for(int j1 = j; j1 <= l; ++j1) {
            achunk[i1 - i][j1 - j] = p_212397_0_.getChunk(i1, j1);
         }
      }

      boolean flag = true;

      for(int l1 = p_212397_1_.getX() >> 4; l1 <= p_212397_2_.getX() >> 4; ++l1) {
         for(int k1 = p_212397_1_.getZ() >> 4; k1 <= p_212397_2_.getZ() >> 4; ++k1) {
            Chunk chunk = achunk[l1 - i][k1 - j];
            if (!chunk.isEmptyBetween(p_212397_1_.getY(), p_212397_2_.getY())) {
               flag = false;
            }
         }
      }

      if (flag) {
         return null;
      } else {
         int i2 = 1;
         BlockPos blockpos = p_212397_1_.add(-1, -1, -1);
         BlockPos blockpos1 = p_212397_2_.add(1, 1, 1);
         return new RenderChunkCache(p_212397_0_, i, j, achunk, blockpos, blockpos1);
      }
   }

   public RenderChunkCache(World p_i49840_1_, int p_i49840_2_, int p_i49840_3_, Chunk[][] p_i49840_4_, BlockPos p_i49840_5_, BlockPos p_i49840_6_) {
      this.field_212408_i = p_i49840_1_;
      this.field_212400_a = p_i49840_2_;
      this.field_212401_b = p_i49840_3_;
      this.field_212406_g = p_i49840_4_;
      this.field_212402_c = p_i49840_5_;
      this.field_212403_d = p_i49840_6_.getX() - p_i49840_5_.getX() + 1;
      this.field_212404_e = p_i49840_6_.getY() - p_i49840_5_.getY() + 1;
      this.field_212405_f = p_i49840_6_.getZ() - p_i49840_5_.getZ() + 1;
      this.field_212407_h = new RenderChunkCache.Entry[this.field_212403_d * this.field_212404_e * this.field_212405_f];

      for(BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(p_i49840_5_, p_i49840_6_)) {
         this.field_212407_h[this.func_212398_a(blockpos$mutableblockpos)] = new RenderChunkCache.Entry(p_i49840_1_, blockpos$mutableblockpos);
      }

   }

   protected int func_212398_a(BlockPos p_212398_1_) {
      int i = p_212398_1_.getX() - this.field_212402_c.getX();
      int j = p_212398_1_.getY() - this.field_212402_c.getY();
      int k = p_212398_1_.getZ() - this.field_212402_c.getZ();
      return k * this.field_212403_d * this.field_212404_e + j * this.field_212403_d + i;
   }

   public IBlockState getBlockState(BlockPos p_180495_1_) {
      return this.field_212407_h[this.func_212398_a(p_180495_1_)].field_212495_a;
   }

   public IFluidState getFluidState(BlockPos p_204610_1_) {
      return this.field_212407_h[this.func_212398_a(p_204610_1_)].field_212496_b;
   }

   public Biome getBiome(BlockPos p_180494_1_) {
      int i = (p_180494_1_.getX() >> 4) - this.field_212400_a;
      int j = (p_180494_1_.getZ() >> 4) - this.field_212401_b;
      return this.field_212406_g[i][j].getBiome(p_180494_1_);
   }

   private int func_212396_b(EnumLightType p_212396_1_, BlockPos p_212396_2_) {
      return this.field_212407_h[this.func_212398_a(p_212396_2_)].func_212493_a(p_212396_1_, p_212396_2_);
   }

   public int getCombinedLight(BlockPos p_175626_1_, int p_175626_2_) {
      int i = this.func_212396_b(EnumLightType.SKY, p_175626_1_);
      int j = this.func_212396_b(EnumLightType.BLOCK, p_175626_1_);
      if (j < p_175626_2_) {
         j = p_175626_2_;
      }

      return i << 20 | j << 4;
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos p_175625_1_) {
      return this.func_212399_a(p_175625_1_, Chunk.EnumCreateEntityType.IMMEDIATE);
   }

   @Nullable
   public TileEntity func_212399_a(BlockPos p_212399_1_, Chunk.EnumCreateEntityType p_212399_2_) {
      int i = (p_212399_1_.getX() >> 4) - this.field_212400_a;
      int j = (p_212399_1_.getZ() >> 4) - this.field_212401_b;
      return this.field_212406_g[i][j].getTileEntity(p_212399_1_, p_212399_2_);
   }

   public float getBrightness(BlockPos p_205052_1_) {
      return this.field_212408_i.dimension.getLightBrightnessTable()[this.getLight(p_205052_1_)];
   }

   public int getNeighborAwareLightSubtracted(BlockPos p_205049_1_, int p_205049_2_) {
      if (this.getBlockState(p_205049_1_).useNeighborBrightness(this, p_205049_1_)) {
         int i = 0;

         for(EnumFacing enumfacing : EnumFacing.values()) {
            int j = this.getLightSubtracted(p_205049_1_.offset(enumfacing), p_205049_2_);
            if (j > i) {
               i = j;
            }

            if (i >= 15) {
               return i;
            }
         }

         return i;
      } else {
         return this.getLightSubtracted(p_205049_1_, p_205049_2_);
      }
   }

   public Dimension getDimension() {
      return this.field_212408_i.getDimension();
   }

   public int getLightSubtracted(BlockPos p_201669_1_, int p_201669_2_) {
      if (p_201669_1_.getX() >= -30000000 && p_201669_1_.getZ() >= -30000000 && p_201669_1_.getX() < 30000000 && p_201669_1_.getZ() <= 30000000) {
         if (p_201669_1_.getY() < 0) {
            return 0;
         } else if (p_201669_1_.getY() >= 256) {
            int k = 15 - p_201669_2_;
            if (k < 0) {
               k = 0;
            }

            return k;
         } else {
            int i = (p_201669_1_.getX() >> 4) - this.field_212400_a;
            int j = (p_201669_1_.getZ() >> 4) - this.field_212401_b;
            return this.field_212406_g[i][j].getLightSubtracted(p_201669_1_, p_201669_2_);
         }
      } else {
         return 15;
      }
   }

   public boolean isChunkLoaded(int p_175680_1_, int p_175680_2_, boolean p_175680_3_) {
      return this.func_212395_a(p_175680_1_, p_175680_2_);
   }

   public boolean canSeeSky(BlockPos p_175678_1_) {
      return false;
   }

   public boolean func_212395_a(int p_212395_1_, int p_212395_2_) {
      int i = p_212395_1_ - this.field_212400_a;
      int j = p_212395_2_ - this.field_212401_b;
      return i >= 0 && i < this.field_212406_g.length && j >= 0 && j < this.field_212406_g[i].length;
   }

   public int getHeight(Heightmap.Type p_201676_1_, int p_201676_2_, int p_201676_3_) {
      throw new RuntimeException("NOT IMPLEMENTED!");
   }

   public WorldBorder getWorldBorder() {
      return this.field_212408_i.getWorldBorder();
   }

   public boolean checkNoEntityCollision(@Nullable Entity p_195585_1_, VoxelShape p_195585_2_) {
      throw new RuntimeException("This method should never be called here. No entity logic inside Region");
   }

   @Nullable
   public EntityPlayer getClosestPlayer(double p_190525_1_, double p_190525_3_, double p_190525_5_, double p_190525_7_, Predicate<Entity> p_190525_9_) {
      throw new RuntimeException("This method should never be called here. No entity logic inside Region");
   }

   public int getSkylightSubtracted() {
      return 0;
   }

   public boolean isAirBlock(BlockPos p_175623_1_) {
      return this.getBlockState(p_175623_1_).isAir();
   }

   public int getLightFor(EnumLightType p_175642_1_, BlockPos p_175642_2_) {
      if (p_175642_2_.getY() >= 0 && p_175642_2_.getY() < 256) {
         int i = (p_175642_2_.getX() >> 4) - this.field_212400_a;
         int j = (p_175642_2_.getZ() >> 4) - this.field_212401_b;
         return this.field_212406_g[i][j].getLightFor(p_175642_1_, p_175642_2_);
      } else {
         return p_175642_1_.defaultLightValue;
      }
   }

   public int getStrongPower(BlockPos p_175627_1_, EnumFacing p_175627_2_) {
      return this.getBlockState(p_175627_1_).getStrongPower(this, p_175627_1_, p_175627_2_);
   }

   public boolean isRemote() {
      throw new RuntimeException("Not yet implemented");
   }

   public int getSeaLevel() {
      throw new RuntimeException("Not yet implemented");
   }

   @OnlyIn(Dist.CLIENT)
   public class Entry {
      protected final IBlockState field_212495_a;
      protected final IFluidState field_212496_b;
      private int[] field_212498_d;

      protected Entry(World p_i49805_2_, BlockPos p_i49805_3_) {
         this.field_212495_a = p_i49805_2_.getBlockState(p_i49805_3_);
         this.field_212496_b = p_i49805_2_.getFluidState(p_i49805_3_);
      }

      protected int func_212493_a(EnumLightType p_212493_1_, BlockPos p_212493_2_) {
         if (this.field_212498_d == null) {
            this.func_212492_a(p_212493_2_);
         }

         return this.field_212498_d[p_212493_1_.ordinal()];
      }

      private void func_212492_a(BlockPos p_212492_1_) {
         this.field_212498_d = new int[EnumLightType.values().length];

         for(EnumLightType enumlighttype : EnumLightType.values()) {
            this.field_212498_d[enumlighttype.ordinal()] = this.func_212494_b(enumlighttype, p_212492_1_);
         }

      }

      private int func_212494_b(EnumLightType p_212494_1_, BlockPos p_212494_2_) {
         if (p_212494_1_ == EnumLightType.SKY && !RenderChunkCache.this.field_212408_i.getDimension().hasSkyLight()) {
            return 0;
         } else if (p_212494_2_.getY() >= 0 && p_212494_2_.getY() < 256) {
            if (this.field_212495_a.useNeighborBrightness(RenderChunkCache.this, p_212494_2_)) {
               int l = 0;

               for(EnumFacing enumfacing : EnumFacing.values()) {
                  int k = RenderChunkCache.this.getLightFor(p_212494_1_, p_212494_2_.offset(enumfacing));
                  if (k > l) {
                     l = k;
                  }

                  if (l >= 15) {
                     return l;
                  }
               }

               return l;
            } else {
               int i = (p_212494_2_.getX() >> 4) - RenderChunkCache.this.field_212400_a;
               int j = (p_212494_2_.getZ() >> 4) - RenderChunkCache.this.field_212401_b;
               return RenderChunkCache.this.field_212406_g[i][j].getLightFor(p_212494_1_, p_212494_2_);
            }
         } else {
            return p_212494_1_.defaultLightValue;
         }
      }
   }
}
