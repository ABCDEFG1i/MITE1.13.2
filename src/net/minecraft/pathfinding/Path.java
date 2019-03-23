package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Path {
   private final PathPoint[] points;
   private PathPoint[] openSet = new PathPoint[0];
   private PathPoint[] closedSet = new PathPoint[0];
   private PathPoint target;
   private int currentPathIndex;
   private int pathLength;

   public Path(PathPoint[] p_i2136_1_) {
      this.points = p_i2136_1_;
      this.pathLength = p_i2136_1_.length;
   }

   public void incrementPathIndex() {
      ++this.currentPathIndex;
   }

   public boolean isFinished() {
      return this.currentPathIndex >= this.pathLength;
   }

   @Nullable
   public PathPoint getFinalPathPoint() {
      return this.pathLength > 0 ? this.points[this.pathLength - 1] : null;
   }

   public PathPoint getPathPointFromIndex(int p_75877_1_) {
      return this.points[p_75877_1_];
   }

   public void setPoint(int p_186309_1_, PathPoint p_186309_2_) {
      this.points[p_186309_1_] = p_186309_2_;
   }

   public int getCurrentPathLength() {
      return this.pathLength;
   }

   public void setCurrentPathLength(int p_75871_1_) {
      this.pathLength = p_75871_1_;
   }

   public int getCurrentPathIndex() {
      return this.currentPathIndex;
   }

   public void setCurrentPathIndex(int p_75872_1_) {
      this.currentPathIndex = p_75872_1_;
   }

   public Vec3d getVectorFromIndex(Entity p_75881_1_, int p_75881_2_) {
      double d0 = (double)this.points[p_75881_2_].x + (double)((int)(p_75881_1_.width + 1.0F)) * 0.5D;
      double d1 = (double)this.points[p_75881_2_].y;
      double d2 = (double)this.points[p_75881_2_].z + (double)((int)(p_75881_1_.width + 1.0F)) * 0.5D;
      return new Vec3d(d0, d1, d2);
   }

   public Vec3d getPosition(Entity p_75878_1_) {
      return this.getVectorFromIndex(p_75878_1_, this.currentPathIndex);
   }

   public Vec3d getCurrentPos() {
      PathPoint pathpoint = this.points[this.currentPathIndex];
      return new Vec3d((double)pathpoint.x, (double)pathpoint.y, (double)pathpoint.z);
   }

   public boolean isSamePath(Path p_75876_1_) {
      if (p_75876_1_ == null) {
         return false;
      } else if (p_75876_1_.points.length != this.points.length) {
         return false;
      } else {
         for(int i = 0; i < this.points.length; ++i) {
            if (this.points[i].x != p_75876_1_.points[i].x || this.points[i].y != p_75876_1_.points[i].y || this.points[i].z != p_75876_1_.points[i].z) {
               return false;
            }
         }

         return true;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public PathPoint[] getOpenSet() {
      return this.openSet;
   }

   @OnlyIn(Dist.CLIENT)
   public PathPoint[] getClosedSet() {
      return this.closedSet;
   }

   @Nullable
   public PathPoint getTarget() {
      return this.target;
   }

   @OnlyIn(Dist.CLIENT)
   public static Path read(PacketBuffer p_186311_0_) {
      int i = p_186311_0_.readInt();
      PathPoint pathpoint = PathPoint.createFromBuffer(p_186311_0_);
      PathPoint[] apathpoint = new PathPoint[p_186311_0_.readInt()];

      for(int j = 0; j < apathpoint.length; ++j) {
         apathpoint[j] = PathPoint.createFromBuffer(p_186311_0_);
      }

      PathPoint[] apathpoint1 = new PathPoint[p_186311_0_.readInt()];

      for(int k = 0; k < apathpoint1.length; ++k) {
         apathpoint1[k] = PathPoint.createFromBuffer(p_186311_0_);
      }

      PathPoint[] apathpoint2 = new PathPoint[p_186311_0_.readInt()];

      for(int l = 0; l < apathpoint2.length; ++l) {
         apathpoint2[l] = PathPoint.createFromBuffer(p_186311_0_);
      }

      Path path = new Path(apathpoint);
      path.openSet = apathpoint1;
      path.closedSet = apathpoint2;
      path.target = pathpoint;
      path.currentPathIndex = i;
      return path;
   }
}
