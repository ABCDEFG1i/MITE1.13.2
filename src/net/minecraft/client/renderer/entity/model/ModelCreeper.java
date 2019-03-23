package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelCreeper extends ModelBase {
   private final ModelRenderer head;
   private final ModelRenderer creeperArmor;
   private final ModelRenderer body;
   private final ModelRenderer leg1;
   private final ModelRenderer leg2;
   private final ModelRenderer leg3;
   private final ModelRenderer leg4;

   public ModelCreeper() {
      this(0.0F);
   }

   public ModelCreeper(float p_i46366_1_) {
      int i = 6;
      this.head = new ModelRenderer(this, 0, 0);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, p_i46366_1_);
      this.head.setRotationPoint(0.0F, 6.0F, 0.0F);
      this.creeperArmor = new ModelRenderer(this, 32, 0);
      this.creeperArmor.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, p_i46366_1_ + 0.5F);
      this.creeperArmor.setRotationPoint(0.0F, 6.0F, 0.0F);
      this.body = new ModelRenderer(this, 16, 16);
      this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, p_i46366_1_);
      this.body.setRotationPoint(0.0F, 6.0F, 0.0F);
      this.leg1 = new ModelRenderer(this, 0, 16);
      this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, p_i46366_1_);
      this.leg1.setRotationPoint(-2.0F, 18.0F, 4.0F);
      this.leg2 = new ModelRenderer(this, 0, 16);
      this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, p_i46366_1_);
      this.leg2.setRotationPoint(2.0F, 18.0F, 4.0F);
      this.leg3 = new ModelRenderer(this, 0, 16);
      this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, p_i46366_1_);
      this.leg3.setRotationPoint(-2.0F, 18.0F, -4.0F);
      this.leg4 = new ModelRenderer(this, 0, 16);
      this.leg4.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, p_i46366_1_);
      this.leg4.setRotationPoint(2.0F, 18.0F, -4.0F);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
      this.head.render(p_78088_7_);
      this.body.render(p_78088_7_);
      this.leg1.render(p_78088_7_);
      this.leg2.render(p_78088_7_);
      this.leg3.render(p_78088_7_);
      this.leg4.render(p_78088_7_);
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      this.head.rotateAngleY = p_78087_4_ * ((float)Math.PI / 180F);
      this.head.rotateAngleX = p_78087_5_ * ((float)Math.PI / 180F);
      this.leg1.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_;
      this.leg2.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_;
      this.leg3.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_;
      this.leg4.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_;
   }
}
