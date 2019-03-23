package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import java.nio.FloatBuffer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public abstract class RenderLivingBase<T extends EntityLivingBase> extends Render<T> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DynamicTexture TEXTURE_BRIGHTNESS = Util.make(new DynamicTexture(16, 16, false), (p_203414_0_) -> {
      p_203414_0_.func_195414_e().untrack();

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            p_203414_0_.func_195414_e().setPixelRGBA(j, i, -1);
         }
      }

      p_203414_0_.updateDynamicTexture();
   });
   protected ModelBase mainModel;
   protected FloatBuffer brightnessBuffer = GLAllocation.createDirectFloatBuffer(4);
   protected List<LayerRenderer<T>> layerRenderers = Lists.newArrayList();
   protected boolean renderMarker;

   public RenderLivingBase(RenderManager p_i46156_1_, ModelBase p_i46156_2_, float p_i46156_3_) {
      super(p_i46156_1_);
      this.mainModel = p_i46156_2_;
      this.shadowSize = p_i46156_3_;
   }

   public <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean addLayer(U p_177094_1_) {
      return this.layerRenderers.add((LayerRenderer<T>)p_177094_1_);
   }

   public ModelBase getMainModel() {
      return this.mainModel;
   }

   protected float interpolateRotation(float p_77034_1_, float p_77034_2_, float p_77034_3_) {
      float f;
      for(f = p_77034_2_ - p_77034_1_; f < -180.0F; f += 360.0F) {
         ;
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return p_77034_1_ + p_77034_3_ * f;
   }

   public void doRender(T p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      this.mainModel.swingProgress = this.getSwingProgress(p_76986_1_, p_76986_9_);
      this.mainModel.isRiding = p_76986_1_.isRiding();
      this.mainModel.isChild = p_76986_1_.isChild();

      try {
         float f = this.interpolateRotation(p_76986_1_.prevRenderYawOffset, p_76986_1_.renderYawOffset, p_76986_9_);
         float f1 = this.interpolateRotation(p_76986_1_.prevRotationYawHead, p_76986_1_.rotationYawHead, p_76986_9_);
         float f2 = f1 - f;
         if (p_76986_1_.isRiding() && p_76986_1_.getRidingEntity() instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)p_76986_1_.getRidingEntity();
            f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, p_76986_9_);
            f2 = f1 - f;
            float f3 = MathHelper.wrapDegrees(f2);
            if (f3 < -85.0F) {
               f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
               f3 = 85.0F;
            }

            f = f1 - f3;
            if (f3 * f3 > 2500.0F) {
               f += f3 * 0.2F;
            }

            f2 = f1 - f;
         }

         float f7 = p_76986_1_.prevRotationPitch + (p_76986_1_.rotationPitch - p_76986_1_.prevRotationPitch) * p_76986_9_;
         this.renderLivingAt(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_);
         float f8 = this.handleRotationFloat(p_76986_1_, p_76986_9_);
         this.applyRotations(p_76986_1_, f8, f, p_76986_9_);
         float f4 = this.prepareScale(p_76986_1_, p_76986_9_);
         float f5 = 0.0F;
         float f6 = 0.0F;
         if (!p_76986_1_.isRiding()) {
            f5 = p_76986_1_.prevLimbSwingAmount + (p_76986_1_.limbSwingAmount - p_76986_1_.prevLimbSwingAmount) * p_76986_9_;
            f6 = p_76986_1_.limbSwing - p_76986_1_.limbSwingAmount * (1.0F - p_76986_9_);
            if (p_76986_1_.isChild()) {
               f6 *= 3.0F;
            }

            if (f5 > 1.0F) {
               f5 = 1.0F;
            }
         }

         GlStateManager.enableAlphaTest();
         this.mainModel.setLivingAnimations(p_76986_1_, f6, f5, p_76986_9_);
         this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, f4, p_76986_1_);
         if (this.renderOutlines) {
            boolean flag1 = this.setScoreTeamColor(p_76986_1_);
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(p_76986_1_));
            if (!this.renderMarker) {
               this.renderModel(p_76986_1_, f6, f5, f8, f2, f7, f4);
            }

            if (!(p_76986_1_ instanceof EntityPlayer) || !((EntityPlayer)p_76986_1_).isSpectator()) {
               this.renderLayers(p_76986_1_, f6, f5, p_76986_9_, f8, f2, f7, f4);
            }

            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
            if (flag1) {
               this.unsetScoreTeamColor();
            }
         } else {
            boolean flag = this.setDoRenderBrightness(p_76986_1_, p_76986_9_);
            this.renderModel(p_76986_1_, f6, f5, f8, f2, f7, f4);
            if (flag) {
               this.unsetBrightness();
            }

            GlStateManager.depthMask(true);
            if (!(p_76986_1_ instanceof EntityPlayer) || !((EntityPlayer)p_76986_1_).isSpectator()) {
               this.renderLayers(p_76986_1_, f6, f5, p_76986_9_, f8, f2, f7, f4);
            }
         }

         GlStateManager.disableRescaleNormal();
      } catch (Exception exception) {
         LOGGER.error("Couldn't render entity", (Throwable)exception);
      }

      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE1);
      GlStateManager.enableTexture2D();
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
      GlStateManager.enableCull();
      GlStateManager.popMatrix();
      super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   public float prepareScale(T p_188322_1_, float p_188322_2_) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      this.preRenderCallback(p_188322_1_, p_188322_2_);
      float f = 0.0625F;
      GlStateManager.translatef(0.0F, -1.501F, 0.0F);
      return 0.0625F;
   }

   protected boolean setScoreTeamColor(T p_177088_1_) {
      GlStateManager.disableLighting();
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE1);
      GlStateManager.disableTexture2D();
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
      return true;
   }

   protected void unsetScoreTeamColor() {
      GlStateManager.enableLighting();
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE1);
      GlStateManager.enableTexture2D();
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
   }

   protected void renderModel(T p_77036_1_, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_) {
      boolean flag = this.isVisible(p_77036_1_);
      boolean flag1 = !flag && !p_77036_1_.isInvisibleToPlayer(Minecraft.getInstance().player);
      if (flag || flag1) {
         if (!this.bindEntityTexture(p_77036_1_)) {
            return;
         }

         if (flag1) {
            GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
         }

         this.mainModel.render(p_77036_1_, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
         if (flag1) {
            GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
         }
      }

   }

   protected boolean isVisible(T p_193115_1_) {
      return !p_193115_1_.isInvisible() || this.renderOutlines;
   }

   protected boolean setDoRenderBrightness(T p_177090_1_, float p_177090_2_) {
      return this.setBrightness(p_177090_1_, p_177090_2_, true);
   }

   protected boolean setBrightness(T p_177092_1_, float p_177092_2_, boolean p_177092_3_) {
      float f = p_177092_1_.getBrightness();
      int i = this.getColorMultiplier(p_177092_1_, f, p_177092_2_);
      boolean flag = (i >> 24 & 255) > 0;
      boolean flag1 = p_177092_1_.hurtTime > 0 || p_177092_1_.deathTime > 0;
      if (!flag && !flag1) {
         return false;
      } else if (!flag && !p_177092_3_) {
         return false;
      } else {
         GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
         GlStateManager.enableTexture2D();
         GlStateManager.texEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_TEXTURE0);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_TEXTURE0);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
         GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE1);
         GlStateManager.enableTexture2D();
         GlStateManager.texEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, OpenGlHelper.GL_INTERPOLATE);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_CONSTANT);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE2_RGB, OpenGlHelper.GL_CONSTANT);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND2_RGB, 770);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
         this.brightnessBuffer.position(0);
         if (flag1) {
            this.brightnessBuffer.put(1.0F);
            this.brightnessBuffer.put(0.0F);
            this.brightnessBuffer.put(0.0F);
            this.brightnessBuffer.put(0.3F);
         } else {
            float f1 = (float)(i >> 24 & 255) / 255.0F;
            float f2 = (float)(i >> 16 & 255) / 255.0F;
            float f3 = (float)(i >> 8 & 255) / 255.0F;
            float f4 = (float)(i & 255) / 255.0F;
            this.brightnessBuffer.put(f2);
            this.brightnessBuffer.put(f3);
            this.brightnessBuffer.put(f4);
            this.brightnessBuffer.put(1.0F - f1);
         }

         this.brightnessBuffer.flip();
         GlStateManager.texEnvfv(8960, 8705, this.brightnessBuffer);
         GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE2);
         GlStateManager.enableTexture2D();
         GlStateManager.bindTexture(TEXTURE_BRIGHTNESS.getGlTextureId());
         GlStateManager.texEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_PREVIOUS);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_TEXTURE1);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
         GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
         GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
         return true;
      }
   }

   protected void unsetBrightness() {
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
      GlStateManager.enableTexture2D();
      GlStateManager.texEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_TEXTURE0);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_TEXTURE0);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE1_ALPHA, OpenGlHelper.GL_PRIMARY_COLOR);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND1_ALPHA, 770);
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE1);
      GlStateManager.texEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE2);
      GlStateManager.disableTexture2D();
      GlStateManager.bindTexture(0);
      GlStateManager.texEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GlStateManager.texEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
   }

   protected void renderLivingAt(T p_77039_1_, double p_77039_2_, double p_77039_4_, double p_77039_6_) {
      GlStateManager.translatef((float)p_77039_2_, (float)p_77039_4_, (float)p_77039_6_);
   }

   protected void applyRotations(T p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
      GlStateManager.rotatef(180.0F - p_77043_3_, 0.0F, 1.0F, 0.0F);
      if (p_77043_1_.deathTime > 0) {
         float f = ((float)p_77043_1_.deathTime + p_77043_4_ - 1.0F) / 20.0F * 1.6F;
         f = MathHelper.sqrt(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         GlStateManager.rotatef(f * this.getDeathMaxRotation(p_77043_1_), 0.0F, 0.0F, 1.0F);
      } else if (p_77043_1_.isSpinAttacking()) {
         GlStateManager.rotatef(-90.0F - p_77043_1_.rotationPitch, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(((float)p_77043_1_.ticksExisted + p_77043_4_) * -75.0F, 0.0F, 1.0F, 0.0F);
      } else if (p_77043_1_.hasCustomName() || p_77043_1_ instanceof EntityPlayer) {
         String s = TextFormatting.getTextWithoutFormattingCodes(p_77043_1_.getName().getString());
         if (s != null && ("Dinnerbone".equals(s) || "Grumm".equals(s)) && (!(p_77043_1_ instanceof EntityPlayer) || ((EntityPlayer)p_77043_1_).isWearing(EnumPlayerModelParts.CAPE))) {
            GlStateManager.translatef(0.0F, p_77043_1_.height + 0.1F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         }
      }

   }

   protected float getSwingProgress(T p_77040_1_, float p_77040_2_) {
      return p_77040_1_.getSwingProgress(p_77040_2_);
   }

   protected float handleRotationFloat(T p_77044_1_, float p_77044_2_) {
      return (float)p_77044_1_.ticksExisted + p_77044_2_;
   }

   protected void renderLayers(T p_177093_1_, float p_177093_2_, float p_177093_3_, float p_177093_4_, float p_177093_5_, float p_177093_6_, float p_177093_7_, float p_177093_8_) {
      for(LayerRenderer<T> layerrenderer : this.layerRenderers) {
         boolean flag = this.setBrightness(p_177093_1_, p_177093_4_, layerrenderer.shouldCombineTextures());
         layerrenderer.doRenderLayer(p_177093_1_, p_177093_2_, p_177093_3_, p_177093_4_, p_177093_5_, p_177093_6_, p_177093_7_, p_177093_8_);
         if (flag) {
            this.unsetBrightness();
         }
      }

   }

   protected float getDeathMaxRotation(T p_77037_1_) {
      return 90.0F;
   }

   protected int getColorMultiplier(T p_77030_1_, float p_77030_2_, float p_77030_3_) {
      return 0;
   }

   protected void preRenderCallback(T p_77041_1_, float p_77041_2_) {
   }

   public void renderName(T p_177067_1_, double p_177067_2_, double p_177067_4_, double p_177067_6_) {
      if (this.canRenderName(p_177067_1_)) {
         double d0 = p_177067_1_.getDistanceSq(this.renderManager.renderViewEntity);
         float f = p_177067_1_.isSneaking() ? 32.0F : 64.0F;
         if (!(d0 >= (double)(f * f))) {
            String s = p_177067_1_.getDisplayName().getFormattedText();
            GlStateManager.alphaFunc(516, 0.1F);
            this.renderEntityName(p_177067_1_, p_177067_2_, p_177067_4_, p_177067_6_, s, d0);
         }
      }
   }

   protected boolean canRenderName(T p_177070_1_) {
      EntityPlayerSP entityplayersp = Minecraft.getInstance().player;
      boolean flag = !p_177070_1_.isInvisibleToPlayer(entityplayersp);
      if (p_177070_1_ != entityplayersp) {
         Team team = p_177070_1_.getTeam();
         Team team1 = entityplayersp.getTeam();
         if (team != null) {
            Team.EnumVisible team$enumvisible = team.getNameTagVisibility();
            switch(team$enumvisible) {
            case ALWAYS:
               return flag;
            case NEVER:
               return false;
            case HIDE_FOR_OTHER_TEAMS:
               return team1 == null ? flag : team.isSameTeam(team1) && (team.getSeeFriendlyInvisiblesEnabled() || flag);
            case HIDE_FOR_OWN_TEAM:
               return team1 == null ? flag : !team.isSameTeam(team1) && flag;
            default:
               return true;
            }
         }
      }

      return Minecraft.isGuiEnabled() && p_177070_1_ != this.renderManager.renderViewEntity && flag && !p_177070_1_.isBeingRidden();
   }
}
