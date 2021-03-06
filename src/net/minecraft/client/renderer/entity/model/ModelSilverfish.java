package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelSilverfish extends ModelBase {
   private final ModelRenderer[] silverfishBodyParts;
   private final ModelRenderer[] silverfishWings;
   private final float[] zPlacement = new float[7];
   private static final int[][] SILVERFISH_BOX_LENGTH = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
   private static final int[][] SILVERFISH_TEXTURE_POSITIONS = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

   public ModelSilverfish() {
      this.silverfishBodyParts = new ModelRenderer[7];
      float f = -3.5F;

      for(int i = 0; i < this.silverfishBodyParts.length; ++i) {
         this.silverfishBodyParts[i] = new ModelRenderer(this, SILVERFISH_TEXTURE_POSITIONS[i][0], SILVERFISH_TEXTURE_POSITIONS[i][1]);
         this.silverfishBodyParts[i].addBox((float)SILVERFISH_BOX_LENGTH[i][0] * -0.5F, 0.0F, (float)SILVERFISH_BOX_LENGTH[i][2] * -0.5F, SILVERFISH_BOX_LENGTH[i][0], SILVERFISH_BOX_LENGTH[i][1], SILVERFISH_BOX_LENGTH[i][2]);
         this.silverfishBodyParts[i].setRotationPoint(0.0F, (float)(24 - SILVERFISH_BOX_LENGTH[i][1]), f);
         this.zPlacement[i] = f;
         if (i < this.silverfishBodyParts.length - 1) {
            f += (float)(SILVERFISH_BOX_LENGTH[i][2] + SILVERFISH_BOX_LENGTH[i + 1][2]) * 0.5F;
         }
      }

      this.silverfishWings = new ModelRenderer[3];
      this.silverfishWings[0] = new ModelRenderer(this, 20, 0);
      this.silverfishWings[0].addBox(-5.0F, 0.0F, (float)SILVERFISH_BOX_LENGTH[2][2] * -0.5F, 10, 8, SILVERFISH_BOX_LENGTH[2][2]);
      this.silverfishWings[0].setRotationPoint(0.0F, 16.0F, this.zPlacement[2]);
      this.silverfishWings[1] = new ModelRenderer(this, 20, 11);
      this.silverfishWings[1].addBox(-3.0F, 0.0F, (float)SILVERFISH_BOX_LENGTH[4][2] * -0.5F, 6, 4, SILVERFISH_BOX_LENGTH[4][2]);
      this.silverfishWings[1].setRotationPoint(0.0F, 20.0F, this.zPlacement[4]);
      this.silverfishWings[2] = new ModelRenderer(this, 20, 18);
      this.silverfishWings[2].addBox(-3.0F, 0.0F, (float)SILVERFISH_BOX_LENGTH[4][2] * -0.5F, 6, 5, SILVERFISH_BOX_LENGTH[1][2]);
      this.silverfishWings[2].setRotationPoint(0.0F, 19.0F, this.zPlacement[1]);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);

      for(ModelRenderer modelrenderer : this.silverfishBodyParts) {
         modelrenderer.render(p_78088_7_);
      }

      for(ModelRenderer modelrenderer1 : this.silverfishWings) {
         modelrenderer1.render(p_78088_7_);
      }

   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      for(int i = 0; i < this.silverfishBodyParts.length; ++i) {
         this.silverfishBodyParts[i].rotateAngleY = MathHelper.cos(p_78087_3_ * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.05F * (float)(1 + Math.abs(i - 2));
         this.silverfishBodyParts[i].rotationPointX = MathHelper.sin(p_78087_3_ * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.2F * (float)Math.abs(i - 2);
      }

      this.silverfishWings[0].rotateAngleY = this.silverfishBodyParts[2].rotateAngleY;
      this.silverfishWings[1].rotateAngleY = this.silverfishBodyParts[4].rotateAngleY;
      this.silverfishWings[1].rotationPointX = this.silverfishBodyParts[4].rotationPointX;
      this.silverfishWings[2].rotateAngleY = this.silverfishBodyParts[1].rotateAngleY;
      this.silverfishWings[2].rotationPointX = this.silverfishBodyParts[1].rotationPointX;
   }
}
