package net.minecraft.world;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.ISaveHandler;

public class WorldServerMulti extends WorldServer {
   public WorldServerMulti(MinecraftServer p_i49820_1_, ISaveHandler p_i49820_2_, DimensionType p_i49820_3_, WorldServer p_i49820_4_, Profiler p_i49820_5_) {
      super(p_i49820_1_, p_i49820_2_, p_i49820_4_.func_175693_T(), new DerivedWorldInfo(p_i49820_4_.getWorldInfo()), p_i49820_3_, p_i49820_5_);
      p_i49820_4_.getWorldBorder().addListener(new IBorderListener() {
         public void onSizeChanged(WorldBorder p_177694_1_, double p_177694_2_) {
            WorldServerMulti.this.getWorldBorder().setTransition(p_177694_2_);
         }

         public void onTransitionStarted(WorldBorder p_177692_1_, double p_177692_2_, double p_177692_4_, long p_177692_6_) {
            WorldServerMulti.this.getWorldBorder().setTransition(p_177692_2_, p_177692_4_, p_177692_6_);
         }

         public void onCenterChanged(WorldBorder p_177693_1_, double p_177693_2_, double p_177693_4_) {
            WorldServerMulti.this.getWorldBorder().setCenter(p_177693_2_, p_177693_4_);
         }

         public void onWarningTimeChanged(WorldBorder p_177691_1_, int p_177691_2_) {
            WorldServerMulti.this.getWorldBorder().setWarningTime(p_177691_2_);
         }

         public void onWarningDistanceChanged(WorldBorder p_177690_1_, int p_177690_2_) {
            WorldServerMulti.this.getWorldBorder().setWarningDistance(p_177690_2_);
         }

         public void onDamageAmountChanged(WorldBorder p_177696_1_, double p_177696_2_) {
            WorldServerMulti.this.getWorldBorder().setDamageAmount(p_177696_2_);
         }

         public void onDamageBufferChanged(WorldBorder p_177695_1_, double p_177695_2_) {
            WorldServerMulti.this.getWorldBorder().setDamageBuffer(p_177695_2_);
         }
      });
   }

   protected void saveLevel() {
   }

   public WorldServerMulti func_212251_i__() {
      String s = VillageCollection.fileNameForProvider(this.dimension);
      VillageCollection villagecollection = this.func_212411_a(DimensionType.OVERWORLD, VillageCollection::new, s);
      if (villagecollection == null) {
         this.villageCollection = new VillageCollection(this);
         this.func_212409_a(DimensionType.OVERWORLD, s, this.villageCollection);
      } else {
         this.villageCollection = villagecollection;
         this.villageCollection.setWorldsForAll(this);
      }

      return this;
   }

   public void saveAdditionalData() {
      this.dimension.onWorldSave();
   }
}
