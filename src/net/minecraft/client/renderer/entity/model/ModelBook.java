package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelBook extends ModelBase {
   private final ModelRenderer coverRight = (new ModelRenderer(this)).setTextureOffset(0, 0).addBox(-6.0F, -5.0F, 0.0F, 6, 10, 0);
   private final ModelRenderer coverLeft = (new ModelRenderer(this)).setTextureOffset(16, 0).addBox(0.0F, -5.0F, 0.0F, 6, 10, 0);
   private final ModelRenderer pagesRight;
   private final ModelRenderer pagesLeft;
   private final ModelRenderer flippingPageRight;
   private final ModelRenderer flippingPageLeft;
   private final ModelRenderer bookSpine = (new ModelRenderer(this)).setTextureOffset(12, 0).addBox(-1.0F, -5.0F, 0.0F, 2, 10, 0);

   public ModelBook() {
      this.pagesRight = (new ModelRenderer(this)).setTextureOffset(0, 10).addBox(0.0F, -4.0F, -0.99F, 5, 8, 1);
      this.pagesLeft = (new ModelRenderer(this)).setTextureOffset(12, 10).addBox(0.0F, -4.0F, -0.01F, 5, 8, 1);
      this.flippingPageRight = (new ModelRenderer(this)).setTextureOffset(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
      this.flippingPageLeft = (new ModelRenderer(this)).setTextureOffset(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
      this.coverRight.setRotationPoint(0.0F, 0.0F, -1.0F);
      this.coverLeft.setRotationPoint(0.0F, 0.0F, 1.0F);
      this.bookSpine.rotateAngleY = ((float)Math.PI / 2F);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
      this.coverRight.render(p_78088_7_);
      this.coverLeft.render(p_78088_7_);
      this.bookSpine.render(p_78088_7_);
      this.pagesRight.render(p_78088_7_);
      this.pagesLeft.render(p_78088_7_);
      this.flippingPageRight.render(p_78088_7_);
      this.flippingPageLeft.render(p_78088_7_);
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      float f = (MathHelper.sin(p_78087_1_ * 0.02F) * 0.1F + 1.25F) * p_78087_4_;
      this.coverRight.rotateAngleY = (float)Math.PI + f;
      this.coverLeft.rotateAngleY = -f;
      this.pagesRight.rotateAngleY = f;
      this.pagesLeft.rotateAngleY = -f;
      this.flippingPageRight.rotateAngleY = f - f * 2.0F * p_78087_2_;
      this.flippingPageLeft.rotateAngleY = f - f * 2.0F * p_78087_3_;
      this.pagesRight.rotationPointX = MathHelper.sin(f);
      this.pagesLeft.rotationPointX = MathHelper.sin(f);
      this.flippingPageRight.rotationPointX = MathHelper.sin(f);
      this.flippingPageLeft.rotationPointX = MathHelper.sin(f);
   }
}
