package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelCod extends ModelBase {
   private final ModelRenderer field_203723_a;
   private final ModelRenderer field_203724_b;
   private final ModelRenderer field_203725_c;
   private final ModelRenderer field_203726_d;
   private final ModelRenderer field_203727_e;
   private final ModelRenderer field_203728_f;
   private final ModelRenderer field_203729_g;

   public ModelCod() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      int i = 22;
      this.field_203723_a = new ModelRenderer(this, 0, 0);
      this.field_203723_a.addBox(-1.0F, -2.0F, 0.0F, 2, 4, 7);
      this.field_203723_a.setRotationPoint(0.0F, 22.0F, 0.0F);
      this.field_203725_c = new ModelRenderer(this, 11, 0);
      this.field_203725_c.addBox(-1.0F, -2.0F, -3.0F, 2, 4, 3);
      this.field_203725_c.setRotationPoint(0.0F, 22.0F, 0.0F);
      this.field_203726_d = new ModelRenderer(this, 0, 0);
      this.field_203726_d.addBox(-1.0F, -2.0F, -1.0F, 2, 3, 1);
      this.field_203726_d.setRotationPoint(0.0F, 22.0F, -3.0F);
      this.field_203727_e = new ModelRenderer(this, 22, 1);
      this.field_203727_e.addBox(-2.0F, 0.0F, -1.0F, 2, 0, 2);
      this.field_203727_e.setRotationPoint(-1.0F, 23.0F, 0.0F);
      this.field_203727_e.rotateAngleZ = (-(float)Math.PI / 4F);
      this.field_203728_f = new ModelRenderer(this, 22, 4);
      this.field_203728_f.addBox(0.0F, 0.0F, -1.0F, 2, 0, 2);
      this.field_203728_f.setRotationPoint(1.0F, 23.0F, 0.0F);
      this.field_203728_f.rotateAngleZ = ((float)Math.PI / 4F);
      this.field_203729_g = new ModelRenderer(this, 22, 3);
      this.field_203729_g.addBox(0.0F, -2.0F, 0.0F, 0, 4, 4);
      this.field_203729_g.setRotationPoint(0.0F, 22.0F, 7.0F);
      this.field_203724_b = new ModelRenderer(this, 20, -6);
      this.field_203724_b.addBox(0.0F, -1.0F, -1.0F, 0, 1, 6);
      this.field_203724_b.setRotationPoint(0.0F, 20.0F, 0.0F);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
      this.field_203723_a.render(p_78088_7_);
      this.field_203725_c.render(p_78088_7_);
      this.field_203726_d.render(p_78088_7_);
      this.field_203727_e.render(p_78088_7_);
      this.field_203728_f.render(p_78088_7_);
      this.field_203729_g.render(p_78088_7_);
      this.field_203724_b.render(p_78088_7_);
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      float f = 1.0F;
      if (!p_78087_7_.isInWater()) {
         f = 1.5F;
      }

      this.field_203729_g.rotateAngleY = -f * 0.45F * MathHelper.sin(0.6F * p_78087_3_);
   }
}
