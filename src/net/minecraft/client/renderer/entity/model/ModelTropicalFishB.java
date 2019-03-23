package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelTropicalFishB extends ModelBase {
   private final ModelRenderer field_204240_a;
   private final ModelRenderer field_204241_b;
   private final ModelRenderer field_204242_c;
   private final ModelRenderer field_204243_d;
   private final ModelRenderer field_204244_e;
   private final ModelRenderer field_204245_f;

   public ModelTropicalFishB() {
      this(0.0F);
   }

   public ModelTropicalFishB(float p_i48891_1_) {
      this.textureWidth = 32;
      this.textureHeight = 32;
      int i = 19;
      this.field_204240_a = new ModelRenderer(this, 0, 20);
      this.field_204240_a.addBox(-1.0F, -3.0F, -3.0F, 2, 6, 6, p_i48891_1_);
      this.field_204240_a.setRotationPoint(0.0F, 19.0F, 0.0F);
      this.field_204241_b = new ModelRenderer(this, 21, 16);
      this.field_204241_b.addBox(0.0F, -3.0F, 0.0F, 0, 6, 5, p_i48891_1_);
      this.field_204241_b.setRotationPoint(0.0F, 19.0F, 3.0F);
      this.field_204242_c = new ModelRenderer(this, 2, 16);
      this.field_204242_c.addBox(-2.0F, 0.0F, 0.0F, 2, 2, 0, p_i48891_1_);
      this.field_204242_c.setRotationPoint(-1.0F, 20.0F, 0.0F);
      this.field_204242_c.rotateAngleY = ((float)Math.PI / 4F);
      this.field_204243_d = new ModelRenderer(this, 2, 12);
      this.field_204243_d.addBox(0.0F, 0.0F, 0.0F, 2, 2, 0, p_i48891_1_);
      this.field_204243_d.setRotationPoint(1.0F, 20.0F, 0.0F);
      this.field_204243_d.rotateAngleY = (-(float)Math.PI / 4F);
      this.field_204244_e = new ModelRenderer(this, 20, 11);
      this.field_204244_e.addBox(0.0F, -4.0F, 0.0F, 0, 4, 6, p_i48891_1_);
      this.field_204244_e.setRotationPoint(0.0F, 16.0F, -3.0F);
      this.field_204245_f = new ModelRenderer(this, 20, 21);
      this.field_204245_f.addBox(0.0F, 0.0F, 0.0F, 0, 4, 6, p_i48891_1_);
      this.field_204245_f.setRotationPoint(0.0F, 22.0F, -3.0F);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
      this.field_204240_a.render(p_78088_7_);
      this.field_204241_b.render(p_78088_7_);
      this.field_204242_c.render(p_78088_7_);
      this.field_204243_d.render(p_78088_7_);
      this.field_204244_e.render(p_78088_7_);
      this.field_204245_f.render(p_78088_7_);
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      float f = 1.0F;
      if (!p_78087_7_.isInWater()) {
         f = 1.5F;
      }

      this.field_204241_b.rotateAngleY = -f * 0.45F * MathHelper.sin(0.6F * p_78087_3_);
   }
}
