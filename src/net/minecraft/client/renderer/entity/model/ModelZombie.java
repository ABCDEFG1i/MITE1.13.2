package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelZombie extends ModelBiped {
   public ModelZombie() {
      this(0.0F, false);
   }

   protected ModelZombie(float p_i48914_1_, float p_i48914_2_, int p_i48914_3_, int p_i48914_4_) {
      super(p_i48914_1_, p_i48914_2_, p_i48914_3_, p_i48914_4_);
   }

   public ModelZombie(float p_i1168_1_, boolean p_i1168_2_) {
      super(p_i1168_1_, 0.0F, 64, p_i1168_2_ ? 32 : 64);
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
      boolean flag = p_78087_7_ instanceof EntityZombie && ((EntityZombie)p_78087_7_).isArmsRaised();
      float f = MathHelper.sin(this.swingProgress * (float)Math.PI);
      float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float)Math.PI);
      this.bipedRightArm.rotateAngleZ = 0.0F;
      this.bipedLeftArm.rotateAngleZ = 0.0F;
      this.bipedRightArm.rotateAngleY = -(0.1F - f * 0.6F);
      this.bipedLeftArm.rotateAngleY = 0.1F - f * 0.6F;
      float f2 = -(float)Math.PI / (flag ? 1.5F : 2.25F);
      this.bipedRightArm.rotateAngleX = f2;
      this.bipedLeftArm.rotateAngleX = f2;
      this.bipedRightArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
      this.bipedLeftArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
      this.bipedRightArm.rotateAngleZ += MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
      this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
      this.bipedRightArm.rotateAngleX += MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;
      this.bipedLeftArm.rotateAngleX -= MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;
   }
}
