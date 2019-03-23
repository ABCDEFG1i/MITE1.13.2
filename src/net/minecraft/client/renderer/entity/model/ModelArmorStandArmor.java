package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelArmorStandArmor extends ModelBiped {
   public ModelArmorStandArmor() {
      this(0.0F);
   }

   public ModelArmorStandArmor(float p_i46307_1_) {
      this(p_i46307_1_, 64, 32);
   }

   protected ModelArmorStandArmor(float p_i46308_1_, int p_i46308_2_, int p_i46308_3_) {
      super(p_i46308_1_, 0.0F, p_i46308_2_, p_i46308_3_);
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      if (p_78087_7_ instanceof EntityArmorStand) {
         EntityArmorStand entityarmorstand = (EntityArmorStand)p_78087_7_;
         this.bipedHead.rotateAngleX = ((float)Math.PI / 180F) * entityarmorstand.getHeadRotation().getX();
         this.bipedHead.rotateAngleY = ((float)Math.PI / 180F) * entityarmorstand.getHeadRotation().getY();
         this.bipedHead.rotateAngleZ = ((float)Math.PI / 180F) * entityarmorstand.getHeadRotation().getZ();
         this.bipedHead.setRotationPoint(0.0F, 1.0F, 0.0F);
         this.bipedBody.rotateAngleX = ((float)Math.PI / 180F) * entityarmorstand.getBodyRotation().getX();
         this.bipedBody.rotateAngleY = ((float)Math.PI / 180F) * entityarmorstand.getBodyRotation().getY();
         this.bipedBody.rotateAngleZ = ((float)Math.PI / 180F) * entityarmorstand.getBodyRotation().getZ();
         this.bipedLeftArm.rotateAngleX = ((float)Math.PI / 180F) * entityarmorstand.getLeftArmRotation().getX();
         this.bipedLeftArm.rotateAngleY = ((float)Math.PI / 180F) * entityarmorstand.getLeftArmRotation().getY();
         this.bipedLeftArm.rotateAngleZ = ((float)Math.PI / 180F) * entityarmorstand.getLeftArmRotation().getZ();
         this.bipedRightArm.rotateAngleX = ((float)Math.PI / 180F) * entityarmorstand.getRightArmRotation().getX();
         this.bipedRightArm.rotateAngleY = ((float)Math.PI / 180F) * entityarmorstand.getRightArmRotation().getY();
         this.bipedRightArm.rotateAngleZ = ((float)Math.PI / 180F) * entityarmorstand.getRightArmRotation().getZ();
         this.bipedLeftLeg.rotateAngleX = ((float)Math.PI / 180F) * entityarmorstand.getLeftLegRotation().getX();
         this.bipedLeftLeg.rotateAngleY = ((float)Math.PI / 180F) * entityarmorstand.getLeftLegRotation().getY();
         this.bipedLeftLeg.rotateAngleZ = ((float)Math.PI / 180F) * entityarmorstand.getLeftLegRotation().getZ();
         this.bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
         this.bipedRightLeg.rotateAngleX = ((float)Math.PI / 180F) * entityarmorstand.getRightLegRotation().getX();
         this.bipedRightLeg.rotateAngleY = ((float)Math.PI / 180F) * entityarmorstand.getRightLegRotation().getY();
         this.bipedRightLeg.rotateAngleZ = ((float)Math.PI / 180F) * entityarmorstand.getRightLegRotation().getZ();
         this.bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
         copyModelAngles(this.bipedHead, this.bipedHeadwear);
      }
   }
}
