package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelSalmon extends ModelBase {
   private final ModelRenderer field_203761_a;
   private final ModelRenderer field_203762_b;
   private final ModelRenderer field_203763_c;
   private final ModelRenderer field_203764_d;
   private final ModelRenderer field_203765_e;
   private final ModelRenderer field_203766_f;
   private final ModelRenderer field_203767_g;
   private final ModelRenderer field_203768_h;

   public ModelSalmon() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      int i = 20;
      this.field_203761_a = new ModelRenderer(this, 0, 0);
      this.field_203761_a.addBox(-1.5F, -2.5F, 0.0F, 3, 5, 8);
      this.field_203761_a.setRotationPoint(0.0F, 20.0F, 0.0F);
      this.field_203762_b = new ModelRenderer(this, 0, 13);
      this.field_203762_b.addBox(-1.5F, -2.5F, 0.0F, 3, 5, 8);
      this.field_203762_b.setRotationPoint(0.0F, 20.0F, 8.0F);
      this.field_203763_c = new ModelRenderer(this, 22, 0);
      this.field_203763_c.addBox(-1.0F, -2.0F, -3.0F, 2, 4, 3);
      this.field_203763_c.setRotationPoint(0.0F, 20.0F, 0.0F);
      this.field_203766_f = new ModelRenderer(this, 20, 10);
      this.field_203766_f.addBox(0.0F, -2.5F, 0.0F, 0, 5, 6);
      this.field_203766_f.setRotationPoint(0.0F, 0.0F, 8.0F);
      this.field_203762_b.addChild(this.field_203766_f);
      this.field_203764_d = new ModelRenderer(this, 2, 1);
      this.field_203764_d.addBox(0.0F, 0.0F, 0.0F, 0, 2, 3);
      this.field_203764_d.setRotationPoint(0.0F, -4.5F, 5.0F);
      this.field_203761_a.addChild(this.field_203764_d);
      this.field_203765_e = new ModelRenderer(this, 0, 2);
      this.field_203765_e.addBox(0.0F, 0.0F, 0.0F, 0, 2, 4);
      this.field_203765_e.setRotationPoint(0.0F, -4.5F, -1.0F);
      this.field_203762_b.addChild(this.field_203765_e);
      this.field_203767_g = new ModelRenderer(this, -4, 0);
      this.field_203767_g.addBox(-2.0F, 0.0F, 0.0F, 2, 0, 2);
      this.field_203767_g.setRotationPoint(-1.5F, 21.5F, 0.0F);
      this.field_203767_g.rotateAngleZ = (-(float)Math.PI / 4F);
      this.field_203768_h = new ModelRenderer(this, 0, 0);
      this.field_203768_h.addBox(0.0F, 0.0F, 0.0F, 2, 0, 2);
      this.field_203768_h.setRotationPoint(1.5F, 21.5F, 0.0F);
      this.field_203768_h.rotateAngleZ = ((float)Math.PI / 4F);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
      this.field_203761_a.render(p_78088_7_);
      this.field_203762_b.render(p_78088_7_);
      this.field_203763_c.render(p_78088_7_);
      this.field_203767_g.render(p_78088_7_);
      this.field_203768_h.render(p_78088_7_);
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      float f = 1.0F;
      float f1 = 1.0F;
      if (!p_78087_7_.isInWater()) {
         f = 1.3F;
         f1 = 1.7F;
      }

      this.field_203762_b.rotateAngleY = -f * 0.25F * MathHelper.sin(f1 * 0.6F * p_78087_3_);
   }
}
