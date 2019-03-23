package net.minecraft.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;

public abstract class NodeProcessor {
   protected IBlockReader blockaccess;
   protected EntityLiving entity;
   protected final IntHashMap<PathPoint> pointMap = new IntHashMap<>();
   protected int entitySizeX;
   protected int entitySizeY;
   protected int entitySizeZ;
   protected boolean canEnterDoors;
   protected boolean canOpenDoors;
   protected boolean canSwim;

   public void init(IBlockReader p_186315_1_, EntityLiving p_186315_2_) {
      this.blockaccess = p_186315_1_;
      this.entity = p_186315_2_;
      this.pointMap.clearMap();
      this.entitySizeX = MathHelper.floor(p_186315_2_.width + 1.0F);
      this.entitySizeY = MathHelper.floor(p_186315_2_.height + 1.0F);
      this.entitySizeZ = MathHelper.floor(p_186315_2_.width + 1.0F);
   }

   public void postProcess() {
      this.blockaccess = null;
      this.entity = null;
   }

   protected PathPoint openPoint(int p_176159_1_, int p_176159_2_, int p_176159_3_) {
      int i = PathPoint.makeHash(p_176159_1_, p_176159_2_, p_176159_3_);
      PathPoint pathpoint = this.pointMap.lookup(i);
      if (pathpoint == null) {
         pathpoint = new PathPoint(p_176159_1_, p_176159_2_, p_176159_3_);
         this.pointMap.addKey(i, pathpoint);
      }

      return pathpoint;
   }

   public abstract PathPoint getStart();

   public abstract PathPoint getPathPointToCoords(double p_186325_1_, double p_186325_3_, double p_186325_5_);

   public abstract int findPathOptions(PathPoint[] p_186320_1_, PathPoint p_186320_2_, PathPoint p_186320_3_, float p_186320_4_);

   public abstract PathNodeType getPathNodeType(IBlockReader p_186319_1_, int p_186319_2_, int p_186319_3_, int p_186319_4_, EntityLiving p_186319_5_, int p_186319_6_, int p_186319_7_, int p_186319_8_, boolean p_186319_9_, boolean p_186319_10_);

   public abstract PathNodeType getPathNodeType(IBlockReader p_186330_1_, int p_186330_2_, int p_186330_3_, int p_186330_4_);

   public void setCanEnterDoors(boolean p_186317_1_) {
      this.canEnterDoors = p_186317_1_;
   }

   public void setCanOpenDoors(boolean p_186321_1_) {
      this.canOpenDoors = p_186321_1_;
   }

   public void setCanSwim(boolean p_186316_1_) {
      this.canSwim = p_186316_1_;
   }

   public boolean getCanEnterDoors() {
      return this.canEnterDoors;
   }

   public boolean getCanOpenDoors() {
      return this.canOpenDoors;
   }

   public boolean getCanSwim() {
      return this.canSwim;
   }
}
