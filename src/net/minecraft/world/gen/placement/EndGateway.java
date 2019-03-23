package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class EndGateway extends BasePlacement<NoPlacementConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld p_201491_1_, IChunkGenerator<? extends IChunkGenSettings> p_201491_2_, Random p_201491_3_, BlockPos p_201491_4_, NoPlacementConfig p_201491_5_, Feature<C> p_201491_6_, C p_201491_7_) {
      boolean flag = false;
      if (p_201491_3_.nextInt(700) == 0) {
         int i = p_201491_3_.nextInt(16);
         int j = p_201491_3_.nextInt(16);
         int k = p_201491_1_.getHeight(Heightmap.Type.MOTION_BLOCKING, p_201491_4_.add(i, 0, j)).getY();
         if (k > 0) {
            int l = k + 3 + p_201491_3_.nextInt(7);
            BlockPos blockpos = p_201491_4_.add(i, l, j);
            p_201491_6_.func_212245_a(p_201491_1_, p_201491_2_, p_201491_3_, blockpos, p_201491_7_);
            TileEntity tileentity = p_201491_1_.getTileEntity(p_201491_4_);
            if (tileentity instanceof TileEntityEndGateway) {
               TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway)tileentity;
               tileentityendgateway.setExitPortal(((ChunkGeneratorEnd)p_201491_2_).getSpawnPoint());
            }
         }
      }

      return false;
   }
}
