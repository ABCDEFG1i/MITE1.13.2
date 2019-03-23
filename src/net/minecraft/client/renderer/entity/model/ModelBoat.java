package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelBoat extends ModelBase implements IMultipassModel {
   private final ModelRenderer[] boatSides = new ModelRenderer[5];
   private final ModelRenderer[] paddles = new ModelRenderer[2];
   private final ModelRenderer noWater;

   public ModelBoat() {
      this.boatSides[0] = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
      this.boatSides[1] = (new ModelRenderer(this, 0, 19)).setTextureSize(128, 64);
      this.boatSides[2] = (new ModelRenderer(this, 0, 27)).setTextureSize(128, 64);
      this.boatSides[3] = (new ModelRenderer(this, 0, 35)).setTextureSize(128, 64);
      this.boatSides[4] = (new ModelRenderer(this, 0, 43)).setTextureSize(128, 64);
      int i = 32;
      int j = 6;
      int k = 20;
      int l = 4;
      int i1 = 28;
      this.boatSides[0].addBox(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
      this.boatSides[0].setRotationPoint(0.0F, 3.0F, 1.0F);
      this.boatSides[1].addBox(-13.0F, -7.0F, -1.0F, 18, 6, 2, 0.0F);
      this.boatSides[1].setRotationPoint(-15.0F, 4.0F, 4.0F);
      this.boatSides[2].addBox(-8.0F, -7.0F, -1.0F, 16, 6, 2, 0.0F);
      this.boatSides[2].setRotationPoint(15.0F, 4.0F, 0.0F);
      this.boatSides[3].addBox(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
      this.boatSides[3].setRotationPoint(0.0F, 4.0F, -9.0F);
      this.boatSides[4].addBox(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
      this.boatSides[4].setRotationPoint(0.0F, 4.0F, 9.0F);
      this.boatSides[0].rotateAngleX = ((float)Math.PI / 2F);
      this.boatSides[1].rotateAngleY = ((float)Math.PI * 1.5F);
      this.boatSides[2].rotateAngleY = ((float)Math.PI / 2F);
      this.boatSides[3].rotateAngleY = (float)Math.PI;
      this.paddles[0] = this.makePaddle(true);
      this.paddles[0].setRotationPoint(3.0F, -5.0F, 9.0F);
      this.paddles[1] = this.makePaddle(false);
      this.paddles[1].setRotationPoint(3.0F, -5.0F, -9.0F);
      this.paddles[1].rotateAngleY = (float)Math.PI;
      this.paddles[0].rotateAngleZ = 0.19634955F;
      this.paddles[1].rotateAngleZ = 0.19634955F;
      this.noWater = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
      this.noWater.addBox(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
      this.noWater.setRotationPoint(0.0F, -3.0F, 1.0F);
      this.noWater.rotateAngleX = ((float)Math.PI / 2F);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
      EntityBoat entityboat = (EntityBoat)p_78088_1_;
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);

      for(int i = 0; i < 5; ++i) {
         this.boatSides[i].render(p_78088_7_);
      }

      this.renderPaddle(entityboat, 0, p_78088_7_, p_78088_2_);
      this.renderPaddle(entityboat, 1, p_78088_7_, p_78088_2_);
   }

   public void renderMultipass(Entity p_187054_1_, float p_187054_2_, float p_187054_3_, float p_187054_4_, float p_187054_5_, float p_187054_6_, float p_187054_7_) {
      GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.colorMask(false, false, false, false);
      this.noWater.render(p_187054_7_);
      GlStateManager.colorMask(true, true, true, true);
   }

   protected ModelRenderer makePaddle(boolean p_187056_1_) {
      ModelRenderer modelrenderer = (new ModelRenderer(this, 62, p_187056_1_ ? 0 : 20)).setTextureSize(128, 64);
      int i = 20;
      int j = 7;
      int k = 6;
      float f = -5.0F;
      modelrenderer.addBox(-1.0F, 0.0F, -5.0F, 2, 2, 18);
      modelrenderer.addBox(p_187056_1_ ? -1.001F : 0.001F, -3.0F, 8.0F, 1, 6, 7);
      return modelrenderer;
   }

   protected void renderPaddle(EntityBoat p_187055_1_, int p_187055_2_, float p_187055_3_, float p_187055_4_) {
      float f = p_187055_1_.getRowingTime(p_187055_2_, p_187055_4_);
      ModelRenderer modelrenderer = this.paddles[p_187055_2_];
      modelrenderer.rotateAngleX = (float)MathHelper.clampedLerp((double)(-(float)Math.PI / 3F), (double)-0.2617994F, (double)((MathHelper.sin(-f) + 1.0F) / 2.0F));
      modelrenderer.rotateAngleY = (float)MathHelper.clampedLerp((double)(-(float)Math.PI / 4F), (double)((float)Math.PI / 4F), (double)((MathHelper.sin(-f + 1.0F) + 1.0F) / 2.0F));
      if (p_187055_2_ == 1) {
         modelrenderer.rotateAngleY = (float)Math.PI - modelrenderer.rotateAngleY;
      }

      modelrenderer.render(p_187055_3_);
   }
}
