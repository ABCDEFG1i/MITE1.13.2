package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelElytra extends ModelBase {
   private final ModelRenderer rightWing;
   private final ModelRenderer leftWing = new ModelRenderer(this, 22, 0);

   public ModelElytra() {
      this.leftWing.addBox(-10.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F);
      this.rightWing = new ModelRenderer(this, 22, 0);
      this.rightWing.mirror = true;
      this.rightWing.addBox(0.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableCull();
      if (p_78088_1_ instanceof EntityLivingBase && ((EntityLivingBase)p_78088_1_).isChild()) {
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 1.5F, -0.1F);
         this.leftWing.render(p_78088_7_);
         this.rightWing.render(p_78088_7_);
         GlStateManager.popMatrix();
      } else {
         this.leftWing.render(p_78088_7_);
         this.rightWing.render(p_78088_7_);
      }

   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
      float f = 0.2617994F;
      float f1 = -0.2617994F;
      float f2 = 0.0F;
      float f3 = 0.0F;
      if (p_78087_7_ instanceof EntityLivingBase && ((EntityLivingBase)p_78087_7_).isElytraFlying()) {
         float f4 = 1.0F;
         if (p_78087_7_.motionY < 0.0D) {
            Vec3d vec3d = (new Vec3d(p_78087_7_.motionX, p_78087_7_.motionY, p_78087_7_.motionZ)).normalize();
            f4 = 1.0F - (float)Math.pow(-vec3d.y, 1.5D);
         }

         f = f4 * 0.34906584F + (1.0F - f4) * f;
         f1 = f4 * (-(float)Math.PI / 2F) + (1.0F - f4) * f1;
      } else if (p_78087_7_.isSneaking()) {
         f = 0.6981317F;
         f1 = (-(float)Math.PI / 4F);
         f2 = 3.0F;
         f3 = 0.08726646F;
      }

      this.leftWing.rotationPointX = 5.0F;
      this.leftWing.rotationPointY = f2;
      if (p_78087_7_ instanceof AbstractClientPlayer) {
         AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer)p_78087_7_;
         abstractclientplayer.rotateElytraX = (float)((double)abstractclientplayer.rotateElytraX + (double)(f - abstractclientplayer.rotateElytraX) * 0.1D);
         abstractclientplayer.rotateElytraY = (float)((double)abstractclientplayer.rotateElytraY + (double)(f3 - abstractclientplayer.rotateElytraY) * 0.1D);
         abstractclientplayer.rotateElytraZ = (float)((double)abstractclientplayer.rotateElytraZ + (double)(f1 - abstractclientplayer.rotateElytraZ) * 0.1D);
         this.leftWing.rotateAngleX = abstractclientplayer.rotateElytraX;
         this.leftWing.rotateAngleY = abstractclientplayer.rotateElytraY;
         this.leftWing.rotateAngleZ = abstractclientplayer.rotateElytraZ;
      } else {
         this.leftWing.rotateAngleX = f;
         this.leftWing.rotateAngleZ = f1;
         this.leftWing.rotateAngleY = f3;
      }

      this.rightWing.rotationPointX = -this.leftWing.rotationPointX;
      this.rightWing.rotateAngleY = -this.leftWing.rotateAngleY;
      this.rightWing.rotationPointY = this.leftWing.rotationPointY;
      this.rightWing.rotateAngleX = this.leftWing.rotateAngleX;
      this.rightWing.rotateAngleZ = -this.leftWing.rotateAngleZ;
   }
}
