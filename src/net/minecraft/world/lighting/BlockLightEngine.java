package net.minecraft.world.lighting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.WorldGenRegion;

public class BlockLightEngine extends BaseLightEngine {
   public EnumLightType getLightType() {
      return EnumLightType.BLOCK;
   }

   public void calculateLight(WorldGenRegion p_202677_1_, IChunk p_202677_2_) {
      for(BlockPos blockpos : p_202677_2_.getLightBlockPositions()) {
         this.setLight(p_202677_1_, blockpos, this.getLightAt(p_202677_1_, blockpos));
         this.enqueueLightChange(p_202677_2_.getPos(), blockpos, this.getLightAt(p_202677_1_, blockpos));
      }

      this.func_202664_a(p_202677_1_, p_202677_2_.getPos());
   }
}
