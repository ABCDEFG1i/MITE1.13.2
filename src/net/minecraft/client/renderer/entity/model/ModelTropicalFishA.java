package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelTropicalFishA extends ModelBase {
   private final ModelRenderer field_204235_a;
   private final ModelRenderer field_204236_b;
   private final ModelRenderer field_204237_c;
   private final ModelRenderer field_204238_d;
   private final ModelRenderer field_204239_e;

   public ModelTropicalFishA() {
      this(0.0F);
   }

   public ModelTropicalFishA(float p_i48892_1_) {
      this.textureWidth = 32;
      this.textureHeight = 32;
      int i = 22;
      this.field_204235_a = new ModelRenderer(this, 0, 0);
      this.field_204235_a.addBox(-1.0F, -1.5F, -3.0F, 2, 3, 6, p_i48892_1_);
      this.field_204235_a.setRotationPoint(0.0F, 22.0F, 0.0F);
      this.field_204236_b = new ModelRenderer(this, 22, -6);
      this.field_204236_b.addBox(0.0F, -1.5F, 0.0F, 0, 3, 6, p_i48892_1_);
      this.field_204236_b.setRotationPoint(0.0F, 22.0F, 3.0F);
      this.field_204237_c = new ModelRenderer(this, 2, 16);
      this.field_204237_c.addBox(-2.0F, -1.0F, 0.0F, 2, 2, 0, p_i48892_1_);
      this.field_204237_c.setRotationPoint(-1.0F, 22.5F, 0.0F);
      this.field_204237_c.rotateAngleY = ((float)Math.PI / 4F);
      this.field_204238_d = new ModelRenderer(this, 2, 12);
      this.field_204238_d.addBox(0.0F, -1.0F, 0.0F, 2, 2, 0, p_i48892_1_);
      this.field_204238_d.setRotationPoint(1.0F, 22.5F, 0.0F);
      this.field_204238_d.rotateAngleY = (-(float)Math.PI / 4F);
      this.field_204239_e = new ModelRenderer(this, 10, -5);
      this.field_204239_e.addBox(0.0F, -3.0F, 0.0F, 0, 3, 6, p_i48892_1_);
      this.field_204239_e.setRotationPoint(0.0F, 20.5F, -3.0F);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
      this.field_204235_a.render(p_78088_7_);
      this.field_204236_b.render(p_78088_7_);
      this.field_204237_c.render(p_78088_7_);
      this.field_204238_d.render(p_78088_7_);
      this.field_204239_e.render(p_78088_7_);
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      float f = 1.0F;
      if (!p_78087_7_.isInWater()) {
         f = 1.5F;
      }

      this.field_204236_b.rotateAngleY = -f * 0.45F * MathHelper.sin(0.6F * p_78087_3_);
   }
}
