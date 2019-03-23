package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelShulker extends ModelBase {
   private final ModelRenderer base;
   private final ModelRenderer lid;
   private final ModelRenderer head;

   public ModelShulker() {
      this.textureHeight = 64;
      this.textureWidth = 64;
      this.lid = new ModelRenderer(this);
      this.base = new ModelRenderer(this);
      this.head = new ModelRenderer(this);
      this.lid.setTextureOffset(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16, 12, 16);
      this.lid.setRotationPoint(0.0F, 24.0F, 0.0F);
      this.base.setTextureOffset(0, 28).addBox(-8.0F, -8.0F, -8.0F, 16, 8, 16);
      this.base.setRotationPoint(0.0F, 24.0F, 0.0F);
      this.head.setTextureOffset(0, 52).addBox(-3.0F, 0.0F, -3.0F, 6, 6, 6);
      this.head.setRotationPoint(0.0F, 12.0F, 0.0F);
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      EntityShulker entityshulker = (EntityShulker)p_78087_7_;
      float f = p_78087_3_ - (float)entityshulker.ticksExisted;
      float f1 = (0.5F + entityshulker.getClientPeekAmount(f)) * (float)Math.PI;
      float f2 = -1.0F + MathHelper.sin(f1);
      float f3 = 0.0F;
      if (f1 > (float)Math.PI) {
         f3 = MathHelper.sin(p_78087_3_ * 0.1F) * 0.7F;
      }

      this.lid.setRotationPoint(0.0F, 16.0F + MathHelper.sin(f1) * 8.0F + f3, 0.0F);
      if (entityshulker.getClientPeekAmount(f) > 0.3F) {
         this.lid.rotateAngleY = f2 * f2 * f2 * f2 * (float)Math.PI * 0.125F;
      } else {
         this.lid.rotateAngleY = 0.0F;
      }

      this.head.rotateAngleX = p_78087_5_ * ((float)Math.PI / 180F);
      this.head.rotateAngleY = p_78087_4_ * ((float)Math.PI / 180F);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.base.render(p_78088_7_);
      this.lid.render(p_78088_7_);
   }

   public ModelRenderer func_205069_a() {
      return this.base;
   }

   public ModelRenderer func_205068_b() {
      return this.lid;
   }

   public ModelRenderer func_205067_c() {
      return this.head;
   }
}
