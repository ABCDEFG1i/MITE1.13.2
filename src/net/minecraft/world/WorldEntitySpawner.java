package net.minecraft.world;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathType;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class WorldEntitySpawner {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int MOB_COUNT_DIV = (int)Math.pow(17.0D, 2.0D);
   private final Set<ChunkPos> eligibleChunksForSpawning = Sets.newHashSet();

   public int findChunksForSpawning(WorldServer p_77192_1_, boolean p_77192_2_, boolean p_77192_3_, boolean p_77192_4_) {
      if (!p_77192_2_ && !p_77192_3_) {
         return 0;
      } else {
         this.eligibleChunksForSpawning.clear();
         int i = 0;

         for(EntityPlayer entityplayer : p_77192_1_.playerEntities) {
            if (!entityplayer.isSpectator()) {
               int j = MathHelper.floor(entityplayer.posX / 16.0D);
               int k = MathHelper.floor(entityplayer.posZ / 16.0D);
               int l = 8;

               for(int i1 = -8; i1 <= 8; ++i1) {
                  for(int j1 = -8; j1 <= 8; ++j1) {
                     boolean flag = i1 == -8 || i1 == 8 || j1 == -8 || j1 == 8;
                     ChunkPos chunkpos = new ChunkPos(i1 + j, j1 + k);
                     if (!this.eligibleChunksForSpawning.contains(chunkpos)) {
                        ++i;
                        if (!flag && p_77192_1_.getWorldBorder().contains(chunkpos)) {
                           PlayerChunkMapEntry playerchunkmapentry = p_77192_1_.getPlayerChunkMap().getEntry(chunkpos.x, chunkpos.z);
                           if (playerchunkmapentry != null && playerchunkmapentry.isSentToPlayers()) {
                              this.eligibleChunksForSpawning.add(chunkpos);
                           }
                        }
                     }
                  }
               }
            }
         }

         int k4 = 0;
         BlockPos blockpos1 = p_77192_1_.getSpawnPoint();

         for(EnumCreatureType enumcreaturetype : EnumCreatureType.values()) {
            if ((!enumcreaturetype.getPeacefulCreature() || p_77192_3_) && (enumcreaturetype.getPeacefulCreature() || p_77192_2_) && (!enumcreaturetype.getAnimal() || p_77192_4_)) {
               int l4 = enumcreaturetype.getMaxNumberOfCreature() * i / MOB_COUNT_DIV;
               int i5 = p_77192_1_.func_72907_a(enumcreaturetype.getCreatureClass(), l4);
               if (i5 <= l4) {
                  BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                  label158:
                  for(ChunkPos chunkpos1 : this.eligibleChunksForSpawning) {
                     BlockPos blockpos = getRandomChunkPosition(p_77192_1_, chunkpos1.x, chunkpos1.z);
                     int k1 = blockpos.getX();
                     int l1 = blockpos.getY();
                     int i2 = blockpos.getZ();
                     IBlockState iblockstate = p_77192_1_.getBlockState(blockpos);
                     if (!iblockstate.isNormalCube()) {
                        int j2 = 0;

                        for(int k2 = 0; k2 < 3; ++k2) {
                           int l2 = k1;
                           int i3 = l1;
                           int j3 = i2;
                           int k3 = 6;
                           Biome.SpawnListEntry biome$spawnlistentry = null;
                           IEntityLivingData ientitylivingdata = null;
                           int l3 = MathHelper.ceil(Math.random() * 4.0D);
                           int i4 = 0;

                           for(int j4 = 0; j4 < l3; ++j4) {
                              l2 += p_77192_1_.rand.nextInt(6) - p_77192_1_.rand.nextInt(6);
                              i3 += p_77192_1_.rand.nextInt(1) - p_77192_1_.rand.nextInt(1);
                              j3 += p_77192_1_.rand.nextInt(6) - p_77192_1_.rand.nextInt(6);
                              blockpos$mutableblockpos.setPos(l2, i3, j3);
                              float f = (float)l2 + 0.5F;
                              float f1 = (float)j3 + 0.5F;
                              EntityPlayer entityplayer1 = p_77192_1_.func_212817_a((double)f, (double)f1, -1.0D);
                              if (entityplayer1 != null) {
                                 double d0 = entityplayer1.getDistanceSq((double)f, (double)i3, (double)f1);
                                 if (!(d0 <= 576.0D) && !(blockpos1.distanceSq((double)f, (double)i3, (double)f1) < 576.0D)) {
                                    if (biome$spawnlistentry == null) {
                                       biome$spawnlistentry = p_77192_1_.getSpawnListEntryForTypeAt(enumcreaturetype, blockpos$mutableblockpos);
                                       if (biome$spawnlistentry == null) {
                                          break;
                                       }

                                       l3 = biome$spawnlistentry.minGroupCount + p_77192_1_.rand.nextInt(1 + biome$spawnlistentry.maxGroupCount - biome$spawnlistentry.minGroupCount);
                                    }

                                    if (p_77192_1_.canCreatureTypeSpawnHere(enumcreaturetype, biome$spawnlistentry, blockpos$mutableblockpos)) {
                                       EntitySpawnPlacementRegistry.SpawnPlacementType entityspawnplacementregistry$spawnplacementtype = EntitySpawnPlacementRegistry.getPlacementType(biome$spawnlistentry.entityType);
                                       if (entityspawnplacementregistry$spawnplacementtype != null && func_209382_a(entityspawnplacementregistry$spawnplacementtype, p_77192_1_, blockpos$mutableblockpos, biome$spawnlistentry.entityType)) {
                                          EntityLiving entityliving;
                                          try {
                                             entityliving = biome$spawnlistentry.entityType.create(p_77192_1_);
                                          } catch (Exception exception) {
                                             LOGGER.warn("Failed to create mob", exception);
                                             return k4;
                                          }

                                          entityliving.setLocationAndAngles((double)f, (double)i3, (double)f1, p_77192_1_.rand.nextFloat() * 360.0F, 0.0F);
                                          if ((d0 <= 16384.0D || !entityliving.canDespawn()) && entityliving.func_205020_a(p_77192_1_, false) && entityliving.isNotColliding(p_77192_1_)) {
                                             ientitylivingdata = entityliving.onInitialSpawn(p_77192_1_.getDifficultyForLocation(new BlockPos(entityliving)), ientitylivingdata,
                                                     null);
                                             if (entityliving.isNotColliding(p_77192_1_)) {
                                                ++j2;
                                                ++i4;
                                                p_77192_1_.spawnEntity(entityliving);
                                             } else {
                                                entityliving.setDead();
                                             }

                                             if (j2 >= entityliving.getMaxSpawnedInChunk()) {
                                                continue label158;
                                             }

                                             if (entityliving.func_204209_c(i4)) {
                                                break;
                                             }
                                          }

                                          k4 += j2;
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return k4;
      }
   }

   private static BlockPos getRandomChunkPosition(World p_180621_0_, int p_180621_1_, int p_180621_2_) {
      Chunk chunk = p_180621_0_.getChunk(p_180621_1_, p_180621_2_);
      int i = p_180621_1_ * 16 + p_180621_0_.rand.nextInt(16);
      int j = p_180621_2_ * 16 + p_180621_0_.rand.nextInt(16);
      int k = chunk.getTopBlockY(Heightmap.Type.LIGHT_BLOCKING, i, j) + 1;
      int l = p_180621_0_.rand.nextInt(k + 1);
      return new BlockPos(i, l, j);
   }

   public static boolean func_206851_a(IBlockState p_206851_0_, IFluidState p_206851_1_) {
      if (p_206851_0_.isBlockNormalCube()) {
         return false;
      } else if (p_206851_0_.canProvidePower()) {
         return false;
      } else if (!p_206851_1_.isEmpty()) {
         return false;
      } else {
         return !p_206851_0_.isIn(BlockTags.RAILS);
      }
   }

   public static boolean func_209382_a(EntitySpawnPlacementRegistry.SpawnPlacementType p_209382_0_, IWorldReaderBase p_209382_1_, BlockPos p_209382_2_, @Nullable EntityType<? extends EntityLiving> p_209382_3_) {
      if (p_209382_3_ != null && p_209382_1_.getWorldBorder().contains(p_209382_2_)) {
         IBlockState iblockstate = p_209382_1_.getBlockState(p_209382_2_);
         IFluidState ifluidstate = p_209382_1_.getFluidState(p_209382_2_);
         switch(p_209382_0_) {
         case IN_WATER:
            return ifluidstate.isTagged(FluidTags.WATER) && p_209382_1_.getFluidState(p_209382_2_.down()).isTagged(FluidTags.WATER) && !p_209382_1_.getBlockState(p_209382_2_.up()).isNormalCube();
         case ON_GROUND:
         default:
            IBlockState iblockstate1 = p_209382_1_.getBlockState(p_209382_2_.down());
            if (iblockstate1.isTopSolid() || p_209382_3_ != null && EntitySpawnPlacementRegistry.func_209345_a(p_209382_3_, iblockstate1)) {
               Block block = iblockstate1.getBlock();
               boolean flag = block != Blocks.BEDROCK && block != Blocks.BARRIER;
               return flag && func_206851_a(iblockstate, ifluidstate) && func_206851_a(p_209382_1_.getBlockState(p_209382_2_.up()), p_209382_1_.getFluidState(p_209382_2_.up()));
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public static void performWorldGenSpawning(IWorld p_77191_0_, Biome p_77191_1_, int p_77191_2_, int p_77191_3_, Random p_77191_4_) {
      List<Biome.SpawnListEntry> list = p_77191_1_.getSpawnableList(EnumCreatureType.CREATURE);
      if (!list.isEmpty()) {
         int i = p_77191_2_ << 4;
         int j = p_77191_3_ << 4;

         while(p_77191_4_.nextFloat() < p_77191_1_.getSpawningChance()) {
            Biome.SpawnListEntry biome$spawnlistentry = WeightedRandom.getRandomItem(p_77191_4_, list);
            int k = biome$spawnlistentry.minGroupCount + p_77191_4_.nextInt(1 + biome$spawnlistentry.maxGroupCount - biome$spawnlistentry.minGroupCount);
            IEntityLivingData ientitylivingdata = null;
            int l = i + p_77191_4_.nextInt(16);
            int i1 = j + p_77191_4_.nextInt(16);
            int j1 = l;
            int k1 = i1;

            for(int l1 = 0; l1 < k; ++l1) {
               boolean flag = false;

               for(int i2 = 0; !flag && i2 < 4; ++i2) {
                  BlockPos blockpos = func_208498_a(p_77191_0_, biome$spawnlistentry.entityType, l, i1);
                  if (func_209382_a(EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, p_77191_0_, blockpos, biome$spawnlistentry.entityType)) {
                     EntityLiving entityliving;
                     try {
                        entityliving = biome$spawnlistentry.entityType.create(p_77191_0_.getWorld());
                     } catch (Exception exception) {
                        LOGGER.warn("Failed to create mob", exception);
                        continue;
                     }

                     double d0 = MathHelper.clamp((double)l, (double)i + (double)entityliving.width, (double)i + 16.0D - (double)entityliving.width);
                     double d1 = MathHelper.clamp((double)i1, (double)j + (double)entityliving.width, (double)j + 16.0D - (double)entityliving.width);
                     entityliving.setLocationAndAngles(d0, (double)blockpos.getY(), d1, p_77191_4_.nextFloat() * 360.0F, 0.0F);
                     if (entityliving.func_205020_a(p_77191_0_, false) && entityliving.isNotColliding(p_77191_0_)) {
                        ientitylivingdata = entityliving.onInitialSpawn(p_77191_0_.getDifficultyForLocation(new BlockPos(entityliving)), ientitylivingdata,
                                null);
                        p_77191_0_.spawnEntity(entityliving);
                        flag = true;
                     }
                  }

                  l += p_77191_4_.nextInt(5) - p_77191_4_.nextInt(5);

                  for(i1 += p_77191_4_.nextInt(5) - p_77191_4_.nextInt(5); l < i || l >= i + 16 || i1 < j || i1 >= j + 16; i1 = k1 + p_77191_4_.nextInt(5) - p_77191_4_.nextInt(5)) {
                     l = j1 + p_77191_4_.nextInt(5) - p_77191_4_.nextInt(5);
                  }
               }
            }
         }

      }
   }

   private static BlockPos func_208498_a(IWorld p_208498_0_, @Nullable EntityType<? extends EntityLiving> p_208498_1_, int p_208498_2_, int p_208498_3_) {
      BlockPos blockpos = new BlockPos(p_208498_2_, p_208498_0_.getHeight(EntitySpawnPlacementRegistry.func_209342_b(p_208498_1_), p_208498_2_, p_208498_3_), p_208498_3_);
      BlockPos blockpos1 = blockpos.down();
      return p_208498_0_.getBlockState(blockpos1).allowsMovement(p_208498_0_, blockpos1, PathType.LAND) ? blockpos1 : blockpos;
   }
}
