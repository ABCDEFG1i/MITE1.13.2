package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelGuardian extends ModelBase {
   private final ModelRenderer guardianBody;
   private final ModelRenderer guardianEye;
   private final ModelRenderer[] guardianSpines;
   private final ModelRenderer[] guardianTail;

   public ModelGuardian() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.guardianSpines = new ModelRenderer[12];
      this.guardianBody = new ModelRenderer(this);
      this.guardianBody.setTextureOffset(0, 0).addBox(-6.0F, 10.0F, -8.0F, 12, 12, 16);
      this.guardianBody.setTextureOffset(0, 28).addBox(-8.0F, 10.0F, -6.0F, 2, 12, 12);
      this.guardianBody.setTextureOffset(0, 28).addBox(6.0F, 10.0F, -6.0F, 2, 12, 12, true);
      this.guardianBody.setTextureOffset(16, 40).addBox(-6.0F, 8.0F, -6.0F, 12, 2, 12);
      this.guardianBody.setTextureOffset(16, 40).addBox(-6.0F, 22.0F, -6.0F, 12, 2, 12);

      for(int i = 0; i < this.guardianSpines.length; ++i) {
         this.guardianSpines[i] = new ModelRenderer(this, 0, 0);
         this.guardianSpines[i].addBox(-1.0F, -4.5F, -1.0F, 2, 9, 2);
         this.guardianBody.addChild(this.guardianSpines[i]);
      }

      this.guardianEye = new ModelRenderer(this, 8, 0);
      this.guardianEye.addBox(-1.0F, 15.0F, 0.0F, 2, 2, 1);
      this.guardianBody.addChild(this.guardianEye);
      this.guardianTail = new ModelRenderer[3];
      this.guardianTail[0] = new ModelRenderer(this, 40, 0);
      this.guardianTail[0].addBox(-2.0F, 14.0F, 7.0F, 4, 4, 8);
      this.guardianTail[1] = new ModelRenderer(this, 0, 54);
      this.guardianTail[1].addBox(0.0F, 14.0F, 0.0F, 3, 3, 7);
      this.guardianTail[2] = new ModelRenderer(this);
      this.guardianTail[2].setTextureOffset(41, 32).addBox(0.0F, 14.0F, 0.0F, 2, 2, 6);
      this.guardianTail[2].setTextureOffset(25, 19).addBox(1.0F, 10.5F, 3.0F, 1, 9, 9);
      this.guardianBody.addChild(this.guardianTail[0]);
      this.guardianTail[0].addChild(this.guardianTail[1]);
      this.guardianTail[1].addChild(this.guardianTail[2]);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
      this.guardianBody.render(p_78088_7_);
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      EntityGuardian entityguardian = (EntityGuardian)p_78087_7_;
      float f = p_78087_3_ - (float)entityguardian.ticksExisted;
      this.guardianBody.rotateAngleY = p_78087_4_ * ((float)Math.PI / 180F);
      this.guardianBody.rotateAngleX = p_78087_5_ * ((float)Math.PI / 180F);
      float[] afloat = new float[]{1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
      float[] afloat1 = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
      float[] afloat2 = new float[]{0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
      float[] afloat3 = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
      float[] afloat4 = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
      float[] afloat5 = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
      float f1 = (1.0F - entityguardian.getSpikesAnimation(f)) * 0.55F;

      for(int i = 0; i < 12; ++i) {
         this.guardianSpines[i].rotateAngleX = (float)Math.PI * afloat[i];
         this.guardianSpines[i].rotateAngleY = (float)Math.PI * afloat1[i];
         this.guardianSpines[i].rotateAngleZ = (float)Math.PI * afloat2[i];
         this.guardianSpines[i].rotationPointX = afloat3[i] * (1.0F + MathHelper.cos(p_78087_3_ * 1.5F + (float)i) * 0.01F - f1);
         this.guardianSpines[i].rotationPointY = 16.0F + afloat4[i] * (1.0F + MathHelper.cos(p_78087_3_ * 1.5F + (float)i) * 0.01F - f1);
         this.guardianSpines[i].rotationPointZ = afloat5[i] * (1.0F + MathHelper.cos(p_78087_3_ * 1.5F + (float)i) * 0.01F - f1);
      }

      this.guardianEye.rotationPointZ = -8.25F;
      Entity entity = Minecraft.getInstance().getRenderViewEntity();
      if (entityguardian.hasTargetedEntity()) {
         entity = entityguardian.getTargetedEntity();
      }

      if (entity != null) {
         Vec3d vec3d = entity.getEyePosition(0.0F);
         Vec3d vec3d1 = p_78087_7_.getEyePosition(0.0F);
         double d0 = vec3d.y - vec3d1.y;
         if (d0 > 0.0D) {
            this.guardianEye.rotationPointY = 0.0F;
         } else {
            this.guardianEye.rotationPointY = 1.0F;
         }

         Vec3d vec3d2 = p_78087_7_.getLook(0.0F);
         vec3d2 = new Vec3d(vec3d2.x, 0.0D, vec3d2.z);
         Vec3d vec3d3 = (new Vec3d(vec3d1.x - vec3d.x, 0.0D, vec3d1.z - vec3d.z)).normalize().rotateYaw(((float)Math.PI / 2F));
         double d1 = vec3d2.dotProduct(vec3d3);
         this.guardianEye.rotationPointX = MathHelper.sqrt((float)Math.abs(d1)) * 2.0F * (float)Math.signum(d1);
      }

      this.guardianEye.showModel = true;
      float f2 = entityguardian.getTailAnimation(f);
      this.guardianTail[0].rotateAngleY = MathHelper.sin(f2) * (float)Math.PI * 0.05F;
      this.guardianTail[1].rotateAngleY = MathHelper.sin(f2) * (float)Math.PI * 0.1F;
      this.guardianTail[1].rotationPointX = -1.5F;
      this.guardianTail[1].rotationPointY = 0.5F;
      this.guardianTail[1].rotationPointZ = 14.0F;
      this.guardianTail[2].rotateAngleY = MathHelper.sin(f2) * (float)Math.PI * 0.15F;
      this.guardianTail[2].rotationPointX = 0.5F;
      this.guardianTail[2].rotationPointY = 0.5F;
      this.guardianTail[2].rotationPointZ = 6.0F;
   }
}
