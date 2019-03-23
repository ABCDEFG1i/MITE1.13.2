package net.minecraft.block.state.pattern;

import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorldReaderBase;

public class BlockPattern {
   private final Predicate<BlockWorldState>[][][] blockMatches;
   private final int fingerLength;
   private final int thumbLength;
   private final int palmLength;

   public BlockPattern(Predicate<BlockWorldState>[][][] p_i48279_1_) {
      this.blockMatches = p_i48279_1_;
      this.fingerLength = p_i48279_1_.length;
      if (this.fingerLength > 0) {
         this.thumbLength = p_i48279_1_[0].length;
         if (this.thumbLength > 0) {
            this.palmLength = p_i48279_1_[0][0].length;
         } else {
            this.palmLength = 0;
         }
      } else {
         this.thumbLength = 0;
         this.palmLength = 0;
      }

   }

   public int getFingerLength() {
      return this.fingerLength;
   }

   public int getThumbLength() {
      return this.thumbLength;
   }

   public int getPalmLength() {
      return this.palmLength;
   }

   @Nullable
   private BlockPattern.PatternHelper checkPatternAt(BlockPos p_177682_1_, EnumFacing p_177682_2_, EnumFacing p_177682_3_, LoadingCache<BlockPos, BlockWorldState> p_177682_4_) {
      for(int i = 0; i < this.palmLength; ++i) {
         for(int j = 0; j < this.thumbLength; ++j) {
            for(int k = 0; k < this.fingerLength; ++k) {
               if (!this.blockMatches[k][j][i].test(p_177682_4_.getUnchecked(translateOffset(p_177682_1_, p_177682_2_, p_177682_3_, i, j, k)))) {
                  return null;
               }
            }
         }
      }

      return new BlockPattern.PatternHelper(p_177682_1_, p_177682_2_, p_177682_3_, p_177682_4_, this.palmLength, this.thumbLength, this.fingerLength);
   }

   @Nullable
   public BlockPattern.PatternHelper match(IWorldReaderBase p_177681_1_, BlockPos p_177681_2_) {
      LoadingCache<BlockPos, BlockWorldState> loadingcache = createLoadingCache(p_177681_1_, false);
      int i = Math.max(Math.max(this.palmLength, this.thumbLength), this.fingerLength);

      for(BlockPos blockpos : BlockPos.getAllInBox(p_177681_2_, p_177681_2_.add(i - 1, i - 1, i - 1))) {
         for(EnumFacing enumfacing : EnumFacing.values()) {
            for(EnumFacing enumfacing1 : EnumFacing.values()) {
               if (enumfacing1 != enumfacing && enumfacing1 != enumfacing.getOpposite()) {
                  BlockPattern.PatternHelper blockpattern$patternhelper = this.checkPatternAt(blockpos, enumfacing, enumfacing1, loadingcache);
                  if (blockpattern$patternhelper != null) {
                     return blockpattern$patternhelper;
                  }
               }
            }
         }
      }

      return null;
   }

   public static LoadingCache<BlockPos, BlockWorldState> createLoadingCache(IWorldReaderBase p_181627_0_, boolean p_181627_1_) {
      return CacheBuilder.newBuilder().build(new BlockPattern.CacheLoader(p_181627_0_, p_181627_1_));
   }

   protected static BlockPos translateOffset(BlockPos p_177683_0_, EnumFacing p_177683_1_, EnumFacing p_177683_2_, int p_177683_3_, int p_177683_4_, int p_177683_5_) {
      if (p_177683_1_ != p_177683_2_ && p_177683_1_ != p_177683_2_.getOpposite()) {
         Vec3i vec3i = new Vec3i(p_177683_1_.getXOffset(), p_177683_1_.getYOffset(), p_177683_1_.getZOffset());
         Vec3i vec3i1 = new Vec3i(p_177683_2_.getXOffset(), p_177683_2_.getYOffset(), p_177683_2_.getZOffset());
         Vec3i vec3i2 = vec3i.crossProduct(vec3i1);
         return p_177683_0_.add(vec3i1.getX() * -p_177683_4_ + vec3i2.getX() * p_177683_3_ + vec3i.getX() * p_177683_5_, vec3i1.getY() * -p_177683_4_ + vec3i2.getY() * p_177683_3_ + vec3i.getY() * p_177683_5_, vec3i1.getZ() * -p_177683_4_ + vec3i2.getZ() * p_177683_3_ + vec3i.getZ() * p_177683_5_);
      } else {
         throw new IllegalArgumentException("Invalid forwards & up combination");
      }
   }

   static class CacheLoader extends com.google.common.cache.CacheLoader<BlockPos, BlockWorldState> {
      private final IWorldReaderBase world;
      private final boolean forceLoad;

      public CacheLoader(IWorldReaderBase p_i48983_1_, boolean p_i48983_2_) {
         this.world = p_i48983_1_;
         this.forceLoad = p_i48983_2_;
      }

      public BlockWorldState load(BlockPos p_load_1_) throws Exception {
         return new BlockWorldState(this.world, p_load_1_, this.forceLoad);
      }
   }

   public static class PatternHelper {
      private final BlockPos frontTopLeft;
      private final EnumFacing forwards;
      private final EnumFacing up;
      private final LoadingCache<BlockPos, BlockWorldState> lcache;
      private final int width;
      private final int height;
      private final int depth;

      public PatternHelper(BlockPos p_i46378_1_, EnumFacing p_i46378_2_, EnumFacing p_i46378_3_, LoadingCache<BlockPos, BlockWorldState> p_i46378_4_, int p_i46378_5_, int p_i46378_6_, int p_i46378_7_) {
         this.frontTopLeft = p_i46378_1_;
         this.forwards = p_i46378_2_;
         this.up = p_i46378_3_;
         this.lcache = p_i46378_4_;
         this.width = p_i46378_5_;
         this.height = p_i46378_6_;
         this.depth = p_i46378_7_;
      }

      public BlockPos getFrontTopLeft() {
         return this.frontTopLeft;
      }

      public EnumFacing getForwards() {
         return this.forwards;
      }

      public EnumFacing getUp() {
         return this.up;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public BlockWorldState translateOffset(int p_177670_1_, int p_177670_2_, int p_177670_3_) {
         return this.lcache.getUnchecked(BlockPattern.translateOffset(this.frontTopLeft, this.getForwards(), this.getUp(), p_177670_1_, p_177670_2_, p_177670_3_));
      }

      public String toString() {
         return MoreObjects.toStringHelper(this).add("up", this.up).add("forwards", this.forwards).add("frontTopLeft", this.frontTopLeft).toString();
      }
   }
}
