package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Structure<C extends IFeatureConfig> extends Feature<C> {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final StructureStart NO_STRUCTURE = new StructureStart() {
      public boolean isSizeableStructure() {
         return false;
      }
   };

   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, C p_212245_5_) {
      if (!this.isEnabledIn(p_212245_1_)) {
         return false;
      } else {
         int i = this.getSize();
         int j = p_212245_4_.getX() >> 4;
         int k = p_212245_4_.getZ() >> 4;
         int l = j << 4;
         int i1 = k << 4;
         long j1 = ChunkPos.asLong(j, k);
         boolean flag = false;

         for(int k1 = j - i; k1 <= j + i; ++k1) {
            for(int l1 = k - i; l1 <= k + i; ++l1) {
               long i2 = ChunkPos.asLong(k1, l1);
               StructureStart structurestart = this.getStructureStart(p_212245_1_, p_212245_2_, (SharedSeedRandom)p_212245_3_, i2);
               //MITEMODDED Make structures spawn with conditions
               if (structurestart != NO_STRUCTURE && structurestart.getBoundingBox().intersectsWith(l, i1, l + 15, i1 + 15) && (p_212245_1_.getWorld().getWorldInfo().hadRequirements.contains(this.getRequirements()))) {
                  p_212245_2_.getStructurePositionToReferenceMap(this).computeIfAbsent(j1, (p_208203_0_) -> new LongOpenHashSet()).add(i2);
                  p_212245_1_.getChunkProvider().func_201713_d(j, k, true).addStructureReference(this.getStructureName(), i2);
                  structurestart.generateStructure(p_212245_1_, p_212245_3_, new MutableBoundingBox(l, i1, l + 15, i1 + 15), new ChunkPos(j, k));
                  structurestart.notifyPostProcessAt(new ChunkPos(j, k));
                  flag = true;
               }
            }
         }

         return flag;
      }
   }

   protected StructureStart getStart(IWorld p_202364_1_, BlockPos p_202364_2_) {
      label31:
      for(StructureStart structurestart : this.getStarts(p_202364_1_, p_202364_2_.getX() >> 4, p_202364_2_.getZ() >> 4)) {
         if (structurestart.isSizeableStructure() && structurestart.getBoundingBox().isVecInside(p_202364_2_)) {
            Iterator<StructurePiece> iterator = structurestart.getComponents().iterator();

            while(true) {
               if (!iterator.hasNext()) {
                  continue label31;
               }

               StructurePiece structurepiece = iterator.next();
               if (structurepiece.getBoundingBox().isVecInside(p_202364_2_)) {
                  break;
               }
            }

            return structurestart;
         }
      }

      return NO_STRUCTURE;
   }

   public boolean isPositionInStructure(IWorld p_175796_1_, BlockPos p_175796_2_) {
      for(StructureStart structurestart : this.getStarts(p_175796_1_, p_175796_2_.getX() >> 4, p_175796_2_.getZ() >> 4)) {
         if (structurestart.isSizeableStructure() && structurestart.getBoundingBox().isVecInside(p_175796_2_)) {
            return true;
         }
      }

      return false;
   }

   public boolean isPositionInsideStructure(IWorld p_202366_1_, BlockPos p_202366_2_) {
      return this.getStart(p_202366_1_, p_202366_2_).isSizeableStructure();
   }

   @Nullable
   public BlockPos func_211405_a(World p_211405_1_, IChunkGenerator<? extends IChunkGenSettings> p_211405_2_, BlockPos p_211405_3_, int p_211405_4_, boolean p_211405_5_) {
      if (!p_211405_2_.getBiomeProvider().hasStructure(this)) {
         return null;
      } else {
         int i = p_211405_3_.getX() >> 4;
         int j = p_211405_3_.getZ() >> 4;
         int k = 0;

         for(SharedSeedRandom sharedseedrandom = new SharedSeedRandom(); k <= p_211405_4_; ++k) {
            for(int l = -k; l <= k; ++l) {
               boolean flag = l == -k || l == k;

               for(int i1 = -k; i1 <= k; ++i1) {
                  boolean flag1 = i1 == -k || i1 == k;
                  if (flag || flag1) {
                     ChunkPos chunkpos = this.getStartPositionForPosition(p_211405_2_, sharedseedrandom, i, j, l, i1);
                     StructureStart structurestart = this.getStructureStart(p_211405_1_, p_211405_2_, sharedseedrandom, chunkpos.asLong());
                     if (structurestart != NO_STRUCTURE) {
                        if (p_211405_5_ && structurestart.func_212687_g()) {
                           structurestart.func_212685_h();
                           return structurestart.getPos();
                        }

                        if (!p_211405_5_) {
                           return structurestart.getPos();
                        }
                     }

                     if (k == 0) {
                        break;
                     }
                  }
               }

               if (k == 0) {
                  break;
               }
            }
         }

         return null;
      }
   }

   private List<StructureStart> getStarts(IWorld p_202371_1_, int p_202371_2_, int p_202371_3_) {
      List<StructureStart> list = Lists.newArrayList();
      Long2ObjectMap<StructureStart> long2objectmap = p_202371_1_.getChunkProvider().getChunkGenerator().getStructureReferenceToStartMap(this);
      Long2ObjectMap<LongSet> long2objectmap1 = p_202371_1_.getChunkProvider().getChunkGenerator().getStructurePositionToReferenceMap(this);
      long i = ChunkPos.asLong(p_202371_2_, p_202371_3_);
      LongSet longset = long2objectmap1.get(i);
      if (longset == null) {
         longset = p_202371_1_.getChunkProvider().func_201713_d(p_202371_2_, p_202371_3_, true).getStructureReferences(this.getStructureName());
         long2objectmap1.put(i, longset);
      }

      for(Long olong : longset) {
         StructureStart structurestart = long2objectmap.get(olong);
         if (structurestart != null) {
            list.add(structurestart);
         } else {
            ChunkPos chunkpos = new ChunkPos(olong);
            IChunk ichunk = p_202371_1_.getChunkProvider().func_201713_d(chunkpos.x, chunkpos.z, true);
            structurestart = ichunk.getStructureStart(this.getStructureName());
            if (structurestart != null) {
               long2objectmap.put(olong, structurestart);
               list.add(structurestart);
            }
         }
      }

      return list;
   }

   private StructureStart getStructureStart(IWorld p_202373_1_, IChunkGenerator<? extends IChunkGenSettings> p_202373_2_, SharedSeedRandom p_202373_3_, long p_202373_4_) {
      if (!p_202373_2_.getBiomeProvider().hasStructure(this)) {
         return NO_STRUCTURE;
      } else {
         Long2ObjectMap<StructureStart> long2objectmap = p_202373_2_.getStructureReferenceToStartMap(this);
         StructureStart structurestart = long2objectmap.get(p_202373_4_);
         if (structurestart != null) {
            return structurestart;
         } else {
            ChunkPos chunkpos = new ChunkPos(p_202373_4_);
            IChunk ichunk = p_202373_1_.getChunkProvider().func_201713_d(chunkpos.x, chunkpos.z, false);
            if (ichunk != null) {
               structurestart = ichunk.getStructureStart(this.getStructureName());
               if (structurestart != null) {
                  long2objectmap.put(p_202373_4_, structurestart);
                  return structurestart;
               }
            }

            if (this.hasStartAt(p_202373_2_, p_202373_3_, chunkpos.x, chunkpos.z)) {
                  StructureStart structurestart1 = this.makeStart(p_202373_1_, p_202373_2_, p_202373_3_, chunkpos.x, chunkpos.z);
                  structurestart = structurestart1.isSizeableStructure() ? structurestart1 : NO_STRUCTURE;
            } else {
               structurestart = NO_STRUCTURE;
            }

            if (structurestart.isSizeableStructure()) {
               p_202373_1_.getChunkProvider().func_201713_d(chunkpos.x, chunkpos.z, true).putStructureStart(this.getStructureName(), structurestart);
            }

            long2objectmap.put(p_202373_4_, structurestart);
            return structurestart;
         }
      }
   }

   protected ChunkPos getStartPositionForPosition(IChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
      return new ChunkPos(p_211744_3_ + p_211744_5_, p_211744_4_ + p_211744_6_);
   }

   protected abstract boolean hasStartAt(IChunkGenerator<?> p_202372_1_, Random p_202372_2_, int p_202372_3_, int p_202372_4_);

   protected abstract boolean isEnabledIn(IWorld p_202365_1_);

   protected abstract StructureStart makeStart(IWorld p_202369_1_, IChunkGenerator<?> p_202369_2_, SharedSeedRandom p_202369_3_, int p_202369_4_, int p_202369_5_);

   protected abstract String getStructureName();

   public abstract int getSize();

   public abstract Item getSymbolItem();

   public StructureRequirements getRequirements(){
      return StructureRequirements.ALL;
   }
}
