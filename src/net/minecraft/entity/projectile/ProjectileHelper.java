package net.minecraft.entity.projectile;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class ProjectileHelper {
   public static RayTraceResult forwardsRaycast(Entity p_188802_0_, boolean p_188802_1_, boolean p_188802_2_, @Nullable Entity p_188802_3_) {
      double d0 = p_188802_0_.posX;
      double d1 = p_188802_0_.posY;
      double d2 = p_188802_0_.posZ;
      double d3 = p_188802_0_.motionX;
      double d4 = p_188802_0_.motionY;
      double d5 = p_188802_0_.motionZ;
      World world = p_188802_0_.world;
      Vec3d vec3d = new Vec3d(d0, d1, d2);
      if (!world.isCollisionBoxesEmpty(p_188802_0_, p_188802_0_.getEntityBoundingBox(),
              !p_188802_2_ && p_188802_3_ != null ? func_211325_a(p_188802_3_) : ImmutableSet.of())) {
         return new RayTraceResult(RayTraceResult.Type.BLOCK, vec3d, EnumFacing.getFacingFromVector(d3, d4, d5), new BlockPos(p_188802_0_));
      } else {
         Vec3d vec3d1 = new Vec3d(d0 + d3, d1 + d4, d2 + d5);
         RayTraceResult raytraceresult = world.rayTraceBlocks(vec3d, vec3d1, RayTraceFluidMode.NEVER, true, false);
         if (p_188802_1_) {
            if (raytraceresult != null) {
               vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
            }

            Entity entity = null;
            List<Entity> list = world.func_72839_b(p_188802_0_, p_188802_0_.getEntityBoundingBox().expand(d3, d4, d5).grow(1.0D));
            double d6 = 0.0D;

            for(int i = 0; i < list.size(); ++i) {
               Entity entity1 = list.get(i);
               if (entity1.canBeCollidedWith() && (p_188802_2_ || !entity1.isEntityEqual(p_188802_3_)) && !entity1.noClip) {
                  AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double)0.3F);
                  RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);
                  if (raytraceresult1 != null) {
                     double d7 = vec3d.squareDistanceTo(raytraceresult1.hitVec);
                     if (d7 < d6 || d6 == 0.0D) {
                        entity = entity1;
                        d6 = d7;
                     }
                  }
               }
            }

            if (entity != null) {
               raytraceresult = new RayTraceResult(entity);
            }
         }

         return raytraceresult;
      }
   }

   private static Set<Entity> func_211325_a(Entity p_211325_0_) {
      Entity entity = p_211325_0_.getRidingEntity();
      return entity != null ? ImmutableSet.of(p_211325_0_, entity) : ImmutableSet.of(p_211325_0_);
   }

   public static final void rotateTowardsMovement(Entity p_188803_0_, float p_188803_1_) {
      double d0 = p_188803_0_.motionX;
      double d1 = p_188803_0_.motionY;
      double d2 = p_188803_0_.motionZ;
      float f = MathHelper.sqrt(d0 * d0 + d2 * d2);
      p_188803_0_.rotationYaw = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) + 90.0F;

      for(p_188803_0_.rotationPitch = (float)(MathHelper.atan2((double)f, d1) * (double)(180F / (float)Math.PI)) - 90.0F; p_188803_0_.rotationPitch - p_188803_0_.prevRotationPitch < -180.0F; p_188803_0_.prevRotationPitch -= 360.0F) {
      }

      while(p_188803_0_.rotationPitch - p_188803_0_.prevRotationPitch >= 180.0F) {
         p_188803_0_.prevRotationPitch += 360.0F;
      }

      while(p_188803_0_.rotationYaw - p_188803_0_.prevRotationYaw < -180.0F) {
         p_188803_0_.prevRotationYaw -= 360.0F;
      }

      while(p_188803_0_.rotationYaw - p_188803_0_.prevRotationYaw >= 180.0F) {
         p_188803_0_.prevRotationYaw += 360.0F;
      }

      p_188803_0_.rotationPitch = p_188803_0_.prevRotationPitch + (p_188803_0_.rotationPitch - p_188803_0_.prevRotationPitch) * p_188803_1_;
      p_188803_0_.rotationYaw = p_188803_0_.prevRotationYaw + (p_188803_0_.rotationYaw - p_188803_0_.prevRotationYaw) * p_188803_1_;
   }
}
