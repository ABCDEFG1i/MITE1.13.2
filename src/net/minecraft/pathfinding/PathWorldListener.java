package net.minecraft.pathfinding;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldEventListener;

public class PathWorldListener implements IWorldEventListener {
   private final List<PathNavigate> navigations = Lists.newArrayList();

   public void notifyBlockUpdate(IBlockReader p_184376_1_, BlockPos p_184376_2_, IBlockState p_184376_3_, IBlockState p_184376_4_, int p_184376_5_) {
      if (this.didBlockChange(p_184376_1_, p_184376_2_, p_184376_3_, p_184376_4_)) {
         int i = 0;

         for(int j = this.navigations.size(); i < j; ++i) {
            PathNavigate pathnavigate = this.navigations.get(i);
            if (pathnavigate != null && !pathnavigate.canUpdatePathOnTimeout()) {
               Path path = pathnavigate.getPath();
               if (path != null && !path.isFinished() && path.getCurrentPathLength() != 0) {
                  PathPoint pathpoint = pathnavigate.currentPath.getFinalPathPoint();
                  double d0 = p_184376_2_.distanceSq(((double)pathpoint.x + pathnavigate.entity.posX) / 2.0D, ((double)pathpoint.y + pathnavigate.entity.posY) / 2.0D, ((double)pathpoint.z + pathnavigate.entity.posZ) / 2.0D);
                  int k = (path.getCurrentPathLength() - path.getCurrentPathIndex()) * (path.getCurrentPathLength() - path.getCurrentPathIndex());
                  if (d0 < (double)k) {
                     pathnavigate.updatePath();
                  }
               }
            }
         }

      }
   }

   protected boolean didBlockChange(IBlockReader p_184378_1_, BlockPos p_184378_2_, IBlockState p_184378_3_, IBlockState p_184378_4_) {
      VoxelShape voxelshape = p_184378_3_.getCollisionShape(p_184378_1_, p_184378_2_);
      VoxelShape voxelshape1 = p_184378_4_.getCollisionShape(p_184378_1_, p_184378_2_);
      return VoxelShapes.func_197879_c(voxelshape, voxelshape1, IBooleanFunction.NOT_SAME);
   }

   public void notifyLightSet(BlockPos p_174959_1_) {
   }

   public void markBlockRangeForRenderUpdate(int p_147585_1_, int p_147585_2_, int p_147585_3_, int p_147585_4_, int p_147585_5_, int p_147585_6_) {
   }

   public void playSoundToAllNearExcept(@Nullable EntityPlayer p_184375_1_, SoundEvent p_184375_2_, SoundCategory p_184375_3_, double p_184375_4_, double p_184375_6_, double p_184375_8_, float p_184375_10_, float p_184375_11_) {
   }

   public void addParticle(IParticleData p_195461_1_, boolean p_195461_2_, double p_195461_3_, double p_195461_5_, double p_195461_7_, double p_195461_9_, double p_195461_11_, double p_195461_13_) {
   }

   public void addParticle(IParticleData p_195462_1_, boolean p_195462_2_, boolean p_195462_3_, double p_195462_4_, double p_195462_6_, double p_195462_8_, double p_195462_10_, double p_195462_12_, double p_195462_14_) {
   }

   public void onEntityAdded(Entity p_72703_1_) {
      if (p_72703_1_ instanceof EntityLiving) {
         this.navigations.add(((EntityLiving)p_72703_1_).getNavigator());
      }

   }

   public void onEntityRemoved(Entity p_72709_1_) {
      if (p_72709_1_ instanceof EntityLiving) {
         this.navigations.remove(((EntityLiving)p_72709_1_).getNavigator());
      }

   }

   public void playRecord(SoundEvent p_184377_1_, BlockPos p_184377_2_) {
   }

   public void broadcastSound(int p_180440_1_, BlockPos p_180440_2_, int p_180440_3_) {
   }

   public void playEvent(EntityPlayer p_180439_1_, int p_180439_2_, BlockPos p_180439_3_, int p_180439_4_) {
   }

   public void sendBlockBreakProgress(int p_180441_1_, BlockPos p_180441_2_, int p_180441_3_) {
   }
}
