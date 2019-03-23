package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ActiveRenderInfo {
   private static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
   private static Vec3d position = new Vec3d(0.0D, 0.0D, 0.0D);
   private static float rotationX;
   private static float rotationXZ;
   private static float rotationZ;
   private static float rotationYZ;
   private static float rotationXY;

   public static void updateRenderInfo(EntityPlayer p_197924_0_, boolean p_197924_1_, float p_197924_2_) {
      MODELVIEW.clear();
      GlStateManager.getFloatv(2982, MODELVIEW);
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.read(MODELVIEW);
      matrix4f.func_195887_c();
      float f = 0.05F;
      float f1 = p_197924_2_ * MathHelper.SQRT_2;
      Vector4f vector4f = new Vector4f(0.0F, 0.0F, -2.0F * f1 * 0.05F / (f1 + 0.05F), 1.0F);
      vector4f.func_195908_a(matrix4f);
      position = new Vec3d((double)vector4f.getX(), (double)vector4f.getY(), (double)vector4f.getZ());
      float f2 = p_197924_0_.rotationPitch;
      float f3 = p_197924_0_.rotationYaw;
      int i = p_197924_1_ ? -1 : 1;
      rotationX = MathHelper.cos(f3 * ((float)Math.PI / 180F)) * (float)i;
      rotationZ = MathHelper.sin(f3 * ((float)Math.PI / 180F)) * (float)i;
      rotationYZ = -rotationZ * MathHelper.sin(f2 * ((float)Math.PI / 180F)) * (float)i;
      rotationXY = rotationX * MathHelper.sin(f2 * ((float)Math.PI / 180F)) * (float)i;
      rotationXZ = MathHelper.cos(f2 * ((float)Math.PI / 180F));
   }

   public static Vec3d projectViewFromEntity(Entity p_178806_0_, double p_178806_1_) {
      double d0 = p_178806_0_.prevPosX + (p_178806_0_.posX - p_178806_0_.prevPosX) * p_178806_1_;
      double d1 = p_178806_0_.prevPosY + (p_178806_0_.posY - p_178806_0_.prevPosY) * p_178806_1_;
      double d2 = p_178806_0_.prevPosZ + (p_178806_0_.posZ - p_178806_0_.prevPosZ) * p_178806_1_;
      double d3 = d0 + position.x;
      double d4 = d1 + position.y;
      double d5 = d2 + position.z;
      return new Vec3d(d3, d4, d5);
   }

   public static IBlockState getBlockStateAtEntityViewpoint(IBlockReader p_186703_0_, Entity p_186703_1_, float p_186703_2_) {
      Vec3d vec3d = projectViewFromEntity(p_186703_1_, (double)p_186703_2_);
      BlockPos blockpos = new BlockPos(vec3d);
      IBlockState iblockstate = p_186703_0_.getBlockState(blockpos);
      IFluidState ifluidstate = p_186703_0_.getFluidState(blockpos);
      if (!ifluidstate.isEmpty()) {
         float f = (float)blockpos.getY() + ifluidstate.getHeight() + 0.11111111F;
         if (vec3d.y >= (double)f) {
            iblockstate = p_186703_0_.getBlockState(blockpos.up());
         }
      }

      return iblockstate;
   }

   public static IFluidState getFluidStateAtEntityViewpoint(IBlockReader p_206243_0_, Entity p_206243_1_, float p_206243_2_) {
      Vec3d vec3d = projectViewFromEntity(p_206243_1_, (double)p_206243_2_);
      BlockPos blockpos = new BlockPos(vec3d);
      IFluidState ifluidstate = p_206243_0_.getFluidState(blockpos);
      if (!ifluidstate.isEmpty()) {
         float f = (float)blockpos.getY() + ifluidstate.getHeight() + 0.11111111F;
         if (vec3d.y >= (double)f) {
            ifluidstate = p_206243_0_.getFluidState(blockpos.up());
         }
      }

      return ifluidstate;
   }

   public static float getRotationX() {
      return rotationX;
   }

   public static float getRotationXZ() {
      return rotationXZ;
   }

   public static float getRotationZ() {
      return rotationZ;
   }

   public static float getRotationYZ() {
      return rotationYZ;
   }

   public static float getRotationXY() {
      return rotationXY;
   }
}
