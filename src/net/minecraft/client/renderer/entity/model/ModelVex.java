package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelVex extends ModelBiped {
   private final ModelRenderer leftWing;
   private final ModelRenderer rightWing;

   public ModelVex() {
      this(0.0F);
   }

   public ModelVex(float p_i47224_1_) {
      super(p_i47224_1_, 0.0F, 64, 64);
      this.bipedLeftLeg.showModel = false;
      this.bipedHeadwear.showModel = false;
      this.bipedRightLeg = new ModelRenderer(this, 32, 0);
      this.bipedRightLeg.addBox(-1.0F, -1.0F, -2.0F, 6, 10, 4, 0.0F);
      this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
      this.rightWing = new ModelRenderer(this, 0, 32);
      this.rightWing.addBox(-20.0F, 0.0F, 0.0F, 20, 12, 1);
      this.leftWing = new ModelRenderer(this, 0, 32);
      this.leftWing.mirror = true;
      this.leftWing.addBox(0.0F, 0.0F, 0.0F, 20, 12, 1);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      super.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);
      this.rightWing.render(p_78088_7_);
      this.leftWing.render(p_78088_7_);
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
      EntityVex entityvex = (EntityVex)p_78087_7_;
      if (entityvex.isCharging()) {
         if (entityvex.getPrimaryHand() == EnumHandSide.RIGHT) {
            this.bipedRightArm.rotateAngleX = 3.7699115F;
         } else {
            this.bipedLeftArm.rotateAngleX = 3.7699115F;
         }
      }

      this.bipedRightLeg.rotateAngleX += ((float)Math.PI / 5F);
      this.rightWing.rotationPointZ = 2.0F;
      this.leftWing.rotationPointZ = 2.0F;
      this.rightWing.rotationPointY = 1.0F;
      this.leftWing.rotationPointY = 1.0F;
      this.rightWing.rotateAngleY = 0.47123894F + MathHelper.cos(p_78087_3_ * 0.8F) * (float)Math.PI * 0.05F;
      this.leftWing.rotateAngleY = -this.rightWing.rotateAngleY;
      this.leftWing.rotateAngleZ = -0.47123894F;
      this.leftWing.rotateAngleX = 0.47123894F;
      this.rightWing.rotateAngleX = 0.47123894F;
      this.rightWing.rotateAngleZ = 0.47123894F;
   }
}
