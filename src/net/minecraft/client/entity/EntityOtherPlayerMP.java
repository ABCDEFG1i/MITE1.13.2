package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityOtherPlayerMP extends AbstractClientPlayer {
   public EntityOtherPlayerMP(World p_i45075_1_, GameProfile p_i45075_2_) {
      super(p_i45075_1_, p_i45075_2_);
      this.stepHeight = 1.0F;
      this.noClip = true;
      this.renderOffsetY = 0.25F;
   }

   public boolean isInRangeToRenderDist(double p_70112_1_) {
      double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 10.0D;
      if (Double.isNaN(d0)) {
         d0 = 1.0D;
      }

      d0 = d0 * 64.0D * getRenderDistanceWeight();
      return p_70112_1_ < d0 * d0;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      return true;
   }

   public void tick() {
      this.renderOffsetY = 0.0F;
      super.tick();
      this.prevLimbSwingAmount = this.limbSwingAmount;
      double d0 = this.posX - this.prevPosX;
      double d1 = this.posZ - this.prevPosZ;
      float f = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      this.limbSwingAmount += (f - this.limbSwingAmount) * 0.4F;
      this.limbSwing += this.limbSwingAmount;
   }

   public void livingTick() {
      if (this.newPosRotationIncrements > 0) {
         double d0 = this.posX + (this.interpTargetX - this.posX) / (double)this.newPosRotationIncrements;
         double d1 = this.posY + (this.interpTargetY - this.posY) / (double)this.newPosRotationIncrements;
         double d2 = this.posZ + (this.interpTargetZ - this.posZ) / (double)this.newPosRotationIncrements;
         this.rotationYaw = (float)((double)this.rotationYaw + MathHelper.wrapDegrees(this.interpTargetYaw - (double)this.rotationYaw) / (double)this.newPosRotationIncrements);
         this.rotationPitch = (float)((double)this.rotationPitch + (this.interpTargetPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
         --this.newPosRotationIncrements;
         this.setPosition(d0, d1, d2);
         this.setRotation(this.rotationYaw, this.rotationPitch);
      }

      if (this.field_208002_br > 0) {
         this.rotationYawHead = (float)((double)this.rotationYawHead + MathHelper.wrapDegrees(this.field_208001_bq - (double)this.rotationYawHead) / (double)this.field_208002_br);
         --this.field_208002_br;
      }

      this.prevCameraYaw = this.cameraYaw;
      this.updateArmSwingProgress();
      float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      float f = (float)Math.atan(-this.motionY * (double)0.2F) * 15.0F;
      if (f1 > 0.1F) {
         f1 = 0.1F;
      }

      if (!this.onGround || this.getHealth() <= 0.0F) {
         f1 = 0.0F;
      }

      if (this.onGround || this.getHealth() <= 0.0F) {
         f = 0.0F;
      }

      this.cameraYaw += (f1 - this.cameraYaw) * 0.4F;
      this.cameraPitch += (f - this.cameraPitch) * 0.8F;
      this.world.profiler.startSection("push");
      this.collideWithNearbyEntities();
      this.world.profiler.endSection();
   }

   public void sendMessage(ITextComponent p_145747_1_) {
      Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(p_145747_1_);
   }
}
