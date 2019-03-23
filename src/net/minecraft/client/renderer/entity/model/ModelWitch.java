package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelWitch extends ModelVillager {
   private boolean holdingItem;
   private final ModelRenderer mole = (new ModelRenderer(this)).setTextureSize(64, 128);
   private final ModelRenderer witchHat;

   public ModelWitch(float p_i46361_1_) {
      super(p_i46361_1_, 0.0F, 64, 128);
      this.mole.setRotationPoint(0.0F, -2.0F, 0.0F);
      this.mole.setTextureOffset(0, 0).addBox(0.0F, 3.0F, -6.75F, 1, 1, 1, -0.25F);
      this.villagerNose.addChild(this.mole);
      this.witchHat = (new ModelRenderer(this)).setTextureSize(64, 128);
      this.witchHat.setRotationPoint(-5.0F, -10.03125F, -5.0F);
      this.witchHat.setTextureOffset(0, 64).addBox(0.0F, 0.0F, 0.0F, 10, 2, 10);
      this.villagerHead.addChild(this.witchHat);
      ModelRenderer modelrenderer = (new ModelRenderer(this)).setTextureSize(64, 128);
      modelrenderer.setRotationPoint(1.75F, -4.0F, 2.0F);
      modelrenderer.setTextureOffset(0, 76).addBox(0.0F, 0.0F, 0.0F, 7, 4, 7);
      modelrenderer.rotateAngleX = -0.05235988F;
      modelrenderer.rotateAngleZ = 0.02617994F;
      this.witchHat.addChild(modelrenderer);
      ModelRenderer modelrenderer1 = (new ModelRenderer(this)).setTextureSize(64, 128);
      modelrenderer1.setRotationPoint(1.75F, -4.0F, 2.0F);
      modelrenderer1.setTextureOffset(0, 87).addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
      modelrenderer1.rotateAngleX = -0.10471976F;
      modelrenderer1.rotateAngleZ = 0.05235988F;
      modelrenderer.addChild(modelrenderer1);
      ModelRenderer modelrenderer2 = (new ModelRenderer(this)).setTextureSize(64, 128);
      modelrenderer2.setRotationPoint(1.75F, -2.0F, 2.0F);
      modelrenderer2.setTextureOffset(0, 95).addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.25F);
      modelrenderer2.rotateAngleX = -0.20943952F;
      modelrenderer2.rotateAngleZ = 0.10471976F;
      modelrenderer1.addChild(modelrenderer2);
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
      this.villagerNose.offsetX = 0.0F;
      this.villagerNose.offsetY = 0.0F;
      this.villagerNose.offsetZ = 0.0F;
      float f = 0.01F * (float)(p_78087_7_.getEntityId() % 10);
      this.villagerNose.rotateAngleX = MathHelper.sin((float)p_78087_7_.ticksExisted * f) * 4.5F * ((float)Math.PI / 180F);
      this.villagerNose.rotateAngleY = 0.0F;
      this.villagerNose.rotateAngleZ = MathHelper.cos((float)p_78087_7_.ticksExisted * f) * 2.5F * ((float)Math.PI / 180F);
      if (this.holdingItem) {
         this.villagerNose.rotateAngleX = -0.9F;
         this.villagerNose.offsetZ = -0.09375F;
         this.villagerNose.offsetY = 0.1875F;
      }

   }

   public ModelRenderer func_205073_b() {
      return this.villagerNose;
   }

   public void func_205074_a(boolean p_205074_1_) {
      this.holdingItem = p_205074_1_;
   }
}
