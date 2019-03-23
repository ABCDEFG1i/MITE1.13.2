package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityDolphin;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DolphinModel extends ModelBase {
   private final ModelRenderer field_205081_a;
   private final ModelRenderer field_205082_b;
   private final ModelRenderer field_205083_c;
   private final ModelRenderer field_205084_d;

   public DolphinModel() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      float f = 18.0F;
      float f1 = -8.0F;
      this.field_205082_b = new ModelRenderer(this, 22, 0);
      this.field_205082_b.addBox(-4.0F, -7.0F, 0.0F, 8, 7, 13);
      this.field_205082_b.setRotationPoint(0.0F, 22.0F, -5.0F);
      ModelRenderer modelrenderer = new ModelRenderer(this, 51, 0);
      modelrenderer.addBox(-0.5F, 0.0F, 8.0F, 1, 4, 5);
      modelrenderer.rotateAngleX = ((float)Math.PI / 3F);
      this.field_205082_b.addChild(modelrenderer);
      ModelRenderer modelrenderer1 = new ModelRenderer(this, 48, 20);
      modelrenderer1.mirror = true;
      modelrenderer1.addBox(-0.5F, -4.0F, 0.0F, 1, 4, 7);
      modelrenderer1.setRotationPoint(2.0F, -2.0F, 4.0F);
      modelrenderer1.rotateAngleX = ((float)Math.PI / 3F);
      modelrenderer1.rotateAngleZ = 2.0943952F;
      this.field_205082_b.addChild(modelrenderer1);
      ModelRenderer modelrenderer2 = new ModelRenderer(this, 48, 20);
      modelrenderer2.addBox(-0.5F, -4.0F, 0.0F, 1, 4, 7);
      modelrenderer2.setRotationPoint(-2.0F, -2.0F, 4.0F);
      modelrenderer2.rotateAngleX = ((float)Math.PI / 3F);
      modelrenderer2.rotateAngleZ = -2.0943952F;
      this.field_205082_b.addChild(modelrenderer2);
      this.field_205083_c = new ModelRenderer(this, 0, 19);
      this.field_205083_c.addBox(-2.0F, -2.5F, 0.0F, 4, 5, 11);
      this.field_205083_c.setRotationPoint(0.0F, -2.5F, 11.0F);
      this.field_205083_c.rotateAngleX = -0.10471976F;
      this.field_205082_b.addChild(this.field_205083_c);
      this.field_205084_d = new ModelRenderer(this, 19, 20);
      this.field_205084_d.addBox(-5.0F, -0.5F, 0.0F, 10, 1, 6);
      this.field_205084_d.setRotationPoint(0.0F, 0.0F, 9.0F);
      this.field_205084_d.rotateAngleX = 0.0F;
      this.field_205083_c.addChild(this.field_205084_d);
      this.field_205081_a = new ModelRenderer(this, 0, 0);
      this.field_205081_a.addBox(-4.0F, -3.0F, -3.0F, 8, 7, 6);
      this.field_205081_a.setRotationPoint(0.0F, -4.0F, -3.0F);
      ModelRenderer modelrenderer3 = new ModelRenderer(this, 0, 13);
      modelrenderer3.addBox(-1.0F, 2.0F, -7.0F, 2, 2, 4);
      this.field_205081_a.addChild(modelrenderer3);
      this.field_205082_b.addChild(this.field_205081_a);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.field_205082_b.render(p_78088_7_);
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      this.field_205082_b.rotateAngleX = p_78087_5_ * ((float)Math.PI / 180F);
      this.field_205082_b.rotateAngleY = p_78087_4_ * ((float)Math.PI / 180F);
      if (p_78087_7_ instanceof EntityDolphin) {
         EntityDolphin entitydolphin = (EntityDolphin)p_78087_7_;
         if (entitydolphin.motionX != 0.0D || entitydolphin.motionZ != 0.0D) {
            this.field_205082_b.rotateAngleX += -0.05F + -0.05F * MathHelper.cos(p_78087_3_ * 0.3F);
            this.field_205083_c.rotateAngleX = -0.1F * MathHelper.cos(p_78087_3_ * 0.3F);
            this.field_205084_d.rotateAngleX = -0.2F * MathHelper.cos(p_78087_3_ * 0.3F);
         }
      }

   }
}
