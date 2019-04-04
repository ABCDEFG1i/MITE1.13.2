package net.minecraft.world.end;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.EndCrystalTowerFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.EndSpikes;

public enum DragonSpawnState {
   START {
      public void process(WorldServer p_186079_1_, DragonFightManager p_186079_2_, List<EntityEnderCrystal> p_186079_3_, int p_186079_4_, BlockPos p_186079_5_) {
         BlockPos blockpos = new BlockPos(0, 128, 0);

         for(EntityEnderCrystal entityendercrystal : p_186079_3_) {
            entityendercrystal.setBeamTarget(blockpos);
         }

         p_186079_2_.setRespawnState(PREPARING_TO_SUMMON_PILLARS);
      }
   },
   PREPARING_TO_SUMMON_PILLARS {
      public void process(WorldServer p_186079_1_, DragonFightManager p_186079_2_, List<EntityEnderCrystal> p_186079_3_, int p_186079_4_, BlockPos p_186079_5_) {
         if (p_186079_4_ < 100) {
            if (p_186079_4_ == 0 || p_186079_4_ == 50 || p_186079_4_ == 51 || p_186079_4_ == 52 || p_186079_4_ >= 95) {
               p_186079_1_.playEvent(3001, new BlockPos(0, 128, 0), 0);
            }
         } else {
            p_186079_2_.setRespawnState(SUMMONING_PILLARS);
         }

      }
   },
   SUMMONING_PILLARS {
      public void process(WorldServer p_186079_1_, DragonFightManager p_186079_2_, List<EntityEnderCrystal> p_186079_3_, int p_186079_4_, BlockPos p_186079_5_) {
         int i = 40;
         boolean flag = p_186079_4_ % 40 == 0;
         boolean flag1 = p_186079_4_ % 40 == 39;
         if (flag || flag1) {
            EndCrystalTowerFeature.EndSpike[] aendcrystaltowerfeature$endspike = EndSpikes.getSpikes(p_186079_1_);
            int j = p_186079_4_ / 40;
            if (j < aendcrystaltowerfeature$endspike.length) {
               EndCrystalTowerFeature.EndSpike endcrystaltowerfeature$endspike = aendcrystaltowerfeature$endspike[j];
               if (flag) {
                  for(EntityEnderCrystal entityendercrystal : p_186079_3_) {
                     entityendercrystal.setBeamTarget(new BlockPos(endcrystaltowerfeature$endspike.getCenterX(), endcrystaltowerfeature$endspike.getHeight() + 1, endcrystaltowerfeature$endspike.getCenterZ()));
                  }
               } else {
                  int k = 10;

                  for(BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(new BlockPos(endcrystaltowerfeature$endspike.getCenterX() - 10, endcrystaltowerfeature$endspike.getHeight() - 10, endcrystaltowerfeature$endspike.getCenterZ() - 10), new BlockPos(endcrystaltowerfeature$endspike.getCenterX() + 10, endcrystaltowerfeature$endspike.getHeight() + 10, endcrystaltowerfeature$endspike.getCenterZ() + 10))) {
                     p_186079_1_.removeBlock(blockpos$mutableblockpos);
                  }

                  p_186079_1_.createExplosion(null, (double)((float)endcrystaltowerfeature$endspike.getCenterX() + 0.5F), (double)endcrystaltowerfeature$endspike.getHeight(), (double)((float)endcrystaltowerfeature$endspike.getCenterZ() + 0.5F), 5.0F, true);
                  EndCrystalTowerFeature endcrystaltowerfeature = new EndCrystalTowerFeature();
                  endcrystaltowerfeature.setSpike(endcrystaltowerfeature$endspike);
                  endcrystaltowerfeature.setCrystalInvulnerable(true);
                  endcrystaltowerfeature.setBeamTarget(new BlockPos(0, 128, 0));
                  endcrystaltowerfeature.func_212245_a(p_186079_1_, p_186079_1_.getChunkProvider().getChunkGenerator(), new Random(), new BlockPos(endcrystaltowerfeature$endspike.getCenterX(), 45, endcrystaltowerfeature$endspike.getCenterZ()), IFeatureConfig.NO_FEATURE_CONFIG);
               }
            } else if (flag) {
               p_186079_2_.setRespawnState(SUMMONING_DRAGON);
            }
         }

      }
   },
   SUMMONING_DRAGON {
      public void process(WorldServer p_186079_1_, DragonFightManager p_186079_2_, List<EntityEnderCrystal> p_186079_3_, int p_186079_4_, BlockPos p_186079_5_) {
         if (p_186079_4_ >= 100) {
            p_186079_2_.setRespawnState(END);
            p_186079_2_.resetSpikeCrystals();

            for(EntityEnderCrystal entityendercrystal : p_186079_3_) {
               entityendercrystal.setBeamTarget(null);
               p_186079_1_.createExplosion(entityendercrystal, entityendercrystal.posX, entityendercrystal.posY, entityendercrystal.posZ, 6.0F, false);
               entityendercrystal.setDead();
            }
         } else if (p_186079_4_ >= 80) {
            p_186079_1_.playEvent(3001, new BlockPos(0, 128, 0), 0);
         } else if (p_186079_4_ == 0) {
            for(EntityEnderCrystal entityendercrystal1 : p_186079_3_) {
               entityendercrystal1.setBeamTarget(new BlockPos(0, 128, 0));
            }
         } else if (p_186079_4_ < 5) {
            p_186079_1_.playEvent(3001, new BlockPos(0, 128, 0), 0);
         }

      }
   },
   END {
      public void process(WorldServer p_186079_1_, DragonFightManager p_186079_2_, List<EntityEnderCrystal> p_186079_3_, int p_186079_4_, BlockPos p_186079_5_) {
      }
   };

   DragonSpawnState() {
   }

   public abstract void process(WorldServer p_186079_1_, DragonFightManager p_186079_2_, List<EntityEnderCrystal> p_186079_3_, int p_186079_4_, BlockPos p_186079_5_);
}
