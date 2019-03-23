package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelSnowMan extends ModelBase {
   private final ModelRenderer body;
   private final ModelRenderer bottomBody;
   private final ModelRenderer head;
   private final ModelRenderer rightHand;
   private final ModelRenderer leftHand;

   public ModelSnowMan() {
      float f = 4.0F;
      float f1 = 0.0F;
      this.head = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, -0.5F);
      this.head.setRotationPoint(0.0F, 4.0F, 0.0F);
      this.rightHand = (new ModelRenderer(this, 32, 0)).setTextureSize(64, 64);
      this.rightHand.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, -0.5F);
      this.rightHand.setRotationPoint(0.0F, 6.0F, 0.0F);
      this.leftHand = (new ModelRenderer(this, 32, 0)).setTextureSize(64, 64);
      this.leftHand.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, -0.5F);
      this.leftHand.setRotationPoint(0.0F, 6.0F, 0.0F);
      this.body = (new ModelRenderer(this, 0, 16)).setTextureSize(64, 64);
      this.body.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, -0.5F);
      this.body.setRotationPoint(0.0F, 13.0F, 0.0F);
      this.bottomBody = (new ModelRenderer(this, 0, 36)).setTextureSize(64, 64);
      this.bottomBody.addBox(-6.0F, -12.0F, -6.0F, 12, 12, 12, -0.5F);
      this.bottomBody.setRotationPoint(0.0F, 24.0F, 0.0F);
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
      this.head.rotateAngleY = p_78087_4_ * ((float)Math.PI / 180F);
      this.head.rotateAngleX = p_78087_5_ * ((float)Math.PI / 180F);
      this.body.rotateAngleY = p_78087_4_ * ((float)Math.PI / 180F) * 0.25F;
      float f = MathHelper.sin(this.body.rotateAngleY);
      float f1 = MathHelper.cos(this.body.rotateAngleY);
      this.rightHand.rotateAngleZ = 1.0F;
      this.leftHand.rotateAngleZ = -1.0F;
      this.rightHand.rotateAngleY = 0.0F + this.body.rotateAngleY;
      this.leftHand.rotateAngleY = (float)Math.PI + this.body.rotateAngleY;
      this.rightHand.rotationPointX = f1 * 5.0F;
      this.rightHand.rotationPointZ = -f * 5.0F;
      this.leftHand.rotationPointX = -f1 * 5.0F;
      this.leftHand.rotationPointZ = f * 5.0F;
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
      this.body.render(p_78088_7_);
      this.bottomBody.render(p_78088_7_);
      this.head.render(p_78088_7_);
      this.rightHand.render(p_78088_7_);
      this.leftHand.render(p_78088_7_);
   }

   public ModelRenderer func_205070_a() {
      return this.head;
   }
}
