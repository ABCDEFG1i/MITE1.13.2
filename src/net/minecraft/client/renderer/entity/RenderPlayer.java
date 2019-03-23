package net.minecraft.client.renderer.entity;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerEntityOnShoulder;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerSpinAttackEffect;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.client.renderer.entity.model.ModelPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderPlayer extends RenderLivingBase<AbstractClientPlayer> {
   private float field_205127_a;

   public RenderPlayer(RenderManager p_i46102_1_) {
      this(p_i46102_1_, false);
   }

   public RenderPlayer(RenderManager p_i46103_1_, boolean p_i46103_2_) {
      super(p_i46103_1_, new ModelPlayer(0.0F, p_i46103_2_), 0.5F);
      this.addLayer(new LayerBipedArmor(this));
      this.addLayer(new LayerHeldItem(this));
      this.addLayer(new LayerArrow(this));
      this.addLayer(new LayerDeadmau5Head(this));
      this.addLayer(new LayerCape(this));
      this.addLayer(new LayerCustomHead(this.getMainModel().bipedHead));
      this.addLayer(new LayerElytra(this));
      this.addLayer(new LayerEntityOnShoulder(p_i46103_1_));
      this.addLayer(new LayerSpinAttackEffect(this));
   }

   public ModelPlayer getMainModel() {
      return (ModelPlayer)super.getMainModel();
   }

   public void doRender(AbstractClientPlayer p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      if (!p_76986_1_.isUser() || this.renderManager.renderViewEntity == p_76986_1_) {
         double d0 = p_76986_4_;
         if (p_76986_1_.isSneaking()) {
            d0 = p_76986_4_ - 0.125D;
         }

         this.setModelVisibilities(p_76986_1_);
         GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
         super.doRender(p_76986_1_, p_76986_2_, d0, p_76986_6_, p_76986_8_, p_76986_9_);
         GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
      }
   }

   private void setModelVisibilities(AbstractClientPlayer p_177137_1_) {
      ModelPlayer modelplayer = this.getMainModel();
      if (p_177137_1_.isSpectator()) {
         modelplayer.setVisible(false);
         modelplayer.bipedHead.showModel = true;
         modelplayer.bipedHeadwear.showModel = true;
      } else {
         ItemStack itemstack = p_177137_1_.getHeldItemMainhand();
         ItemStack itemstack1 = p_177137_1_.getHeldItemOffhand();
         modelplayer.setVisible(true);
         modelplayer.bipedHeadwear.showModel = p_177137_1_.isWearing(EnumPlayerModelParts.HAT);
         modelplayer.bipedBodyWear.showModel = p_177137_1_.isWearing(EnumPlayerModelParts.JACKET);
         modelplayer.bipedLeftLegwear.showModel = p_177137_1_.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
         modelplayer.bipedRightLegwear.showModel = p_177137_1_.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
         modelplayer.bipedLeftArmwear.showModel = p_177137_1_.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
         modelplayer.bipedRightArmwear.showModel = p_177137_1_.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
         modelplayer.isSneak = p_177137_1_.isSneaking();
         ModelBiped.ArmPose modelbiped$armpose = this.func_212499_a(p_177137_1_, itemstack);
         ModelBiped.ArmPose modelbiped$armpose1 = this.func_212499_a(p_177137_1_, itemstack1);
         if (p_177137_1_.getPrimaryHand() == EnumHandSide.RIGHT) {
            modelplayer.rightArmPose = modelbiped$armpose;
            modelplayer.leftArmPose = modelbiped$armpose1;
         } else {
            modelplayer.rightArmPose = modelbiped$armpose1;
            modelplayer.leftArmPose = modelbiped$armpose;
         }
      }

   }

   public ResourceLocation getEntityTexture(AbstractClientPlayer p_110775_1_) {
      return p_110775_1_.getLocationSkin();
   }

   protected void preRenderCallback(AbstractClientPlayer p_77041_1_, float p_77041_2_) {
      float f = 0.9375F;
      GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
   }

   protected void renderEntityName(AbstractClientPlayer p_188296_1_, double p_188296_2_, double p_188296_4_, double p_188296_6_, String p_188296_8_, double p_188296_9_) {
      if (p_188296_9_ < 100.0D) {
         Scoreboard scoreboard = p_188296_1_.getWorldScoreboard();
         ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(2);
         if (scoreobjective != null) {
            Score score = scoreboard.getOrCreateScore(p_188296_1_.getScoreboardName(), scoreobjective);
            this.renderLivingLabel(p_188296_1_, score.getScorePoints() + " " + scoreobjective.getDisplayName().getFormattedText(), p_188296_2_, p_188296_4_, p_188296_6_, 64);
            p_188296_4_ += (double)((float)this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * 0.025F);
         }
      }

      super.renderEntityName(p_188296_1_, p_188296_2_, p_188296_4_, p_188296_6_, p_188296_8_, p_188296_9_);
   }

   public void renderRightArm(AbstractClientPlayer p_177138_1_) {
      float f = 1.0F;
      GlStateManager.color3f(1.0F, 1.0F, 1.0F);
      float f1 = 0.0625F;
      ModelPlayer modelplayer = this.getMainModel();
      this.setModelVisibilities(p_177138_1_);
      GlStateManager.enableBlend();
      modelplayer.swingProgress = 0.0F;
      modelplayer.isSneak = false;
      modelplayer.field_205061_a = 0.0F;
      modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, p_177138_1_);
      modelplayer.bipedRightArm.rotateAngleX = 0.0F;
      modelplayer.bipedRightArm.render(0.0625F);
      modelplayer.bipedRightArmwear.rotateAngleX = 0.0F;
      modelplayer.bipedRightArmwear.render(0.0625F);
      GlStateManager.disableBlend();
   }

   public void renderLeftArm(AbstractClientPlayer p_177139_1_) {
      float f = 1.0F;
      GlStateManager.color3f(1.0F, 1.0F, 1.0F);
      float f1 = 0.0625F;
      ModelPlayer modelplayer = this.getMainModel();
      this.setModelVisibilities(p_177139_1_);
      GlStateManager.enableBlend();
      modelplayer.isSneak = false;
      modelplayer.swingProgress = 0.0F;
      modelplayer.field_205061_a = 0.0F;
      modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, p_177139_1_);
      modelplayer.bipedLeftArm.rotateAngleX = 0.0F;
      modelplayer.bipedLeftArm.render(0.0625F);
      modelplayer.bipedLeftArmwear.rotateAngleX = 0.0F;
      modelplayer.bipedLeftArmwear.render(0.0625F);
      GlStateManager.disableBlend();
   }

   protected void renderLivingAt(AbstractClientPlayer p_77039_1_, double p_77039_2_, double p_77039_4_, double p_77039_6_) {
      if (p_77039_1_.isEntityAlive() && p_77039_1_.isPlayerSleeping()) {
         super.renderLivingAt(p_77039_1_, p_77039_2_ + (double)p_77039_1_.renderOffsetX, p_77039_4_ + (double)p_77039_1_.renderOffsetY, p_77039_6_ + (double)p_77039_1_.renderOffsetZ);
      } else {
         super.renderLivingAt(p_77039_1_, p_77039_2_, p_77039_4_, p_77039_6_);
      }

   }

   protected void applyRotations(AbstractClientPlayer p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
      float f = p_77043_1_.getSwimAnimation(p_77043_4_);
      if (p_77043_1_.isEntityAlive() && p_77043_1_.isPlayerSleeping()) {
         GlStateManager.rotatef(p_77043_1_.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(this.getDeathMaxRotation(p_77043_1_), 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(270.0F, 0.0F, 1.0F, 0.0F);
      } else if (p_77043_1_.isElytraFlying()) {
         super.applyRotations(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
         float f1 = (float)p_77043_1_.getTicksElytraFlying() + p_77043_4_;
         float f2 = MathHelper.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
         if (!p_77043_1_.isSpinAttacking()) {
            GlStateManager.rotatef(f2 * (-90.0F - p_77043_1_.rotationPitch), 1.0F, 0.0F, 0.0F);
         }

         Vec3d vec3d = p_77043_1_.getLook(p_77043_4_);
         double d0 = p_77043_1_.motionX * p_77043_1_.motionX + p_77043_1_.motionZ * p_77043_1_.motionZ;
         double d1 = vec3d.x * vec3d.x + vec3d.z * vec3d.z;
         if (d0 > 0.0D && d1 > 0.0D) {
            double d2 = (p_77043_1_.motionX * vec3d.x + p_77043_1_.motionZ * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
            double d3 = p_77043_1_.motionX * vec3d.z - p_77043_1_.motionZ * vec3d.x;
            GlStateManager.rotatef((float)(Math.signum(d3) * Math.acos(d2)) * 180.0F / (float)Math.PI, 0.0F, 1.0F, 0.0F);
         }
      } else if (f > 0.0F) {
         super.applyRotations(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
         float f3 = this.lerp(p_77043_1_.rotationPitch, -90.0F - p_77043_1_.rotationPitch, f);
         if (!p_77043_1_.isSwimming()) {
            f3 = this.interpolateRotation(this.field_205127_a, 0.0F, 1.0F - f);
         }

         GlStateManager.rotatef(f3, 1.0F, 0.0F, 0.0F);
         if (p_77043_1_.isSwimming()) {
            this.field_205127_a = f3;
            GlStateManager.translatef(0.0F, -1.0F, 0.3F);
         }
      } else {
         super.applyRotations(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
      }

   }

   private float lerp(float p_205126_1_, float p_205126_2_, float p_205126_3_) {
      return p_205126_1_ + (p_205126_2_ - p_205126_1_) * p_205126_3_;
   }

   private ModelBiped.ArmPose func_212499_a(AbstractClientPlayer p_212499_1_, ItemStack p_212499_2_) {
      if (p_212499_2_.isEmpty()) {
         return ModelBiped.ArmPose.EMPTY;
      } else {
         if (p_212499_1_.getItemInUseCount() > 0) {
            EnumAction enumaction = p_212499_2_.getUseAction();
            if (enumaction == EnumAction.BLOCK) {
               return ModelBiped.ArmPose.BLOCK;
            }

            if (enumaction == EnumAction.BOW) {
               return ModelBiped.ArmPose.BOW_AND_ARROW;
            }

            if (enumaction == EnumAction.SPEAR) {
               return ModelBiped.ArmPose.THROW_SPEAR;
            }
         }

         return ModelBiped.ArmPose.ITEM;
      }
   }
}
