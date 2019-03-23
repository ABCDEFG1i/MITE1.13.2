package net.minecraft.world.gen;

import net.minecraft.util.math.BlockPos;

public class EndGenSettings extends ChunkGenSettings {
   private BlockPos spawnPos;

   public EndGenSettings setSpawnPos(BlockPos p_205538_1_) {
      this.spawnPos = p_205538_1_;
      return this;
   }

   public BlockPos getSpawnPos() {
      return this.spawnPos;
   }
}
