package net.minecraft.world;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Region implements IWorldReader {
   protected int field_72818_a;
   protected int field_72816_b;
   protected Chunk[][] field_72817_c;
   protected boolean field_72814_d;
   protected World field_72815_e;

   public Region(World p_i45746_1_, BlockPos p_i45746_2_, BlockPos p_i45746_3_, int p_i45746_4_) {
      this.field_72815_e = p_i45746_1_;
      this.field_72818_a = p_i45746_2_.getX() - p_i45746_4_ >> 4;
      this.field_72816_b = p_i45746_2_.getZ() - p_i45746_4_ >> 4;
      int i = p_i45746_3_.getX() + p_i45746_4_ >> 4;
      int j = p_i45746_3_.getZ() + p_i45746_4_ >> 4;
      this.field_72817_c = new Chunk[i - this.field_72818_a + 1][j - this.field_72816_b + 1];
      this.field_72814_d = true;

      for(int k = this.field_72818_a; k <= i; ++k) {
         for(int l = this.field_72816_b; l <= j; ++l) {
            this.field_72817_c[k - this.field_72818_a][l - this.field_72816_b] = p_i45746_1_.getChunk(k, l);
         }
      }

      for(int i1 = p_i45746_2_.getX() >> 4; i1 <= p_i45746_3_.getX() >> 4; ++i1) {
         for(int j1 = p_i45746_2_.getZ() >> 4; j1 <= p_i45746_3_.getZ() >> 4; ++j1) {
            Chunk chunk = this.field_72817_c[i1 - this.field_72818_a][j1 - this.field_72816_b];
            if (chunk != null && !chunk.isEmptyBetween(p_i45746_2_.getY(), p_i45746_3_.getY())) {
               this.field_72814_d = false;
            }
         }
      }

   }

   @Nullable
   public TileEntity getTileEntity(BlockPos p_175625_1_) {
      return this.func_190300_a(p_175625_1_, Chunk.EnumCreateEntityType.IMMEDIATE);
   }

   @Nullable
   public TileEntity func_190300_a(BlockPos p_190300_1_, Chunk.EnumCreateEntityType p_190300_2_) {
      int i = (p_190300_1_.getX() >> 4) - this.field_72818_a;
      int j = (p_190300_1_.getZ() >> 4) - this.field_72816_b;
      return this.field_72817_c[i][j].getTileEntity(p_190300_1_, p_190300_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public int getCombinedLight(BlockPos p_175626_1_, int p_175626_2_) {
      int i = this.func_175629_a(EnumLightType.SKY, p_175626_1_);
      int j = this.func_175629_a(EnumLightType.BLOCK, p_175626_1_);
      if (j < p_175626_2_) {
         j = p_175626_2_;
      }

      return i << 20 | j << 4;
   }

   public float getBrightness(BlockPos p_205052_1_) {
      return this.field_72815_e.dimension.getLightBrightnessTable()[this.getLight(p_205052_1_)];
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
      return this.field_72815_e.getDimension();
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
            int i = (p_201669_1_.getX() >> 4) - this.field_72818_a;
            int j = (p_201669_1_.getZ() >> 4) - this.field_72816_b;
            return this.field_72817_c[i][j].getLightSubtracted(p_201669_1_, p_201669_2_);
         }
      } else {
         return 15;
      }
   }

   public boolean isChunkLoaded(int p_175680_1_, int p_175680_2_, boolean p_175680_3_) {
      return this.func_205054_a(p_175680_1_, p_175680_2_);
   }

   public boolean canSeeSky(BlockPos p_175678_1_) {
      return false;
   }

   public boolean func_205054_a(int p_205054_1_, int p_205054_2_) {
      int i = p_205054_1_ - this.field_72818_a;
      int j = p_205054_2_ - this.field_72816_b;
      return i >= 0 && i < this.field_72817_c.length && j >= 0 && j < this.field_72817_c[i].length;
   }

   public int getHeight(Heightmap.Type p_201676_1_, int p_201676_2_, int p_201676_3_) {
      throw new RuntimeException("NOT IMPLEMENTED!");
   }

   public WorldBorder getWorldBorder() {
      return this.field_72815_e.getWorldBorder();
   }

   public boolean checkNoEntityCollision(@Nullable Entity p_195585_1_, VoxelShape p_195585_2_) {
      throw new RuntimeException("This method should never be called here. No entity logic inside Region");
   }

   @Nullable
   public EntityPlayer getClosestPlayer(double p_190525_1_, double p_190525_3_, double p_190525_5_, double p_190525_7_, Predicate<Entity> p_190525_9_) {
      throw new RuntimeException("This method should never be called here. No entity logic inside Region");
   }

   public IBlockState getBlockState(BlockPos p_180495_1_) {
      if (p_180495_1_.getY() >= 0 && p_180495_1_.getY() < 256) {
         int i = (p_180495_1_.getX() >> 4) - this.field_72818_a;
         int j = (p_180495_1_.getZ() >> 4) - this.field_72816_b;
         if (i >= 0 && i < this.field_72817_c.length && j >= 0 && j < this.field_72817_c[i].length) {
            Chunk chunk = this.field_72817_c[i][j];
            if (chunk != null) {
               return chunk.getBlockState(p_180495_1_);
            }
         }
      }

      return Blocks.AIR.getDefaultState();
   }

   public IFluidState getFluidState(BlockPos p_204610_1_) {
      if (p_204610_1_.getY() >= 0 && p_204610_1_.getY() < 256) {
         int i = (p_204610_1_.getX() >> 4) - this.field_72818_a;
         int j = (p_204610_1_.getZ() >> 4) - this.field_72816_b;
         if (i >= 0 && i < this.field_72817_c.length && j >= 0 && j < this.field_72817_c[i].length) {
            Chunk chunk = this.field_72817_c[i][j];
            if (chunk != null) {
               return chunk.getFluidState(p_204610_1_);
            }
         }
      }

      return Fluids.EMPTY.getDefaultState();
   }

   public int getSkylightSubtracted() {
      return 0;
   }

   public Biome getBiome(BlockPos p_180494_1_) {
      int i = (p_180494_1_.getX() >> 4) - this.field_72818_a;
      int j = (p_180494_1_.getZ() >> 4) - this.field_72816_b;
      return this.field_72817_c[i][j].getBiome(p_180494_1_);
   }

   @OnlyIn(Dist.CLIENT)
   private int func_175629_a(EnumLightType p_175629_1_, BlockPos p_175629_2_) {
      if (p_175629_1_ == EnumLightType.SKY && !this.field_72815_e.getDimension().hasSkyLight()) {
         return 0;
      } else if (p_175629_2_.getY() >= 0 && p_175629_2_.getY() < 256) {
         if (this.getBlockState(p_175629_2_).useNeighborBrightness(this, p_175629_2_)) {
            int l = 0;

            for(EnumFacing enumfacing : EnumFacing.values()) {
               int k = this.getLightFor(p_175629_1_, p_175629_2_.offset(enumfacing));
               if (k > l) {
                  l = k;
               }

               if (l >= 15) {
                  return l;
               }
            }

            return l;
         } else {
            int i = (p_175629_2_.getX() >> 4) - this.field_72818_a;
            int j = (p_175629_2_.getZ() >> 4) - this.field_72816_b;
            return this.field_72817_c[i][j].getLightFor(p_175629_1_, p_175629_2_);
         }
      } else {
         return p_175629_1_.defaultLightValue;
      }
   }

   public boolean isAirBlock(BlockPos p_175623_1_) {
      return this.getBlockState(p_175623_1_).isAir();
   }

   public int getLightFor(EnumLightType p_175642_1_, BlockPos p_175642_2_) {
      if (p_175642_2_.getY() >= 0 && p_175642_2_.getY() < 256) {
         int i = (p_175642_2_.getX() >> 4) - this.field_72818_a;
         int j = (p_175642_2_.getZ() >> 4) - this.field_72816_b;
         return this.field_72817_c[i][j].getLightFor(p_175642_1_, p_175642_2_);
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
}
