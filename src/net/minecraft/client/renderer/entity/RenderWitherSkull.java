package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelSkeletonHead;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderWitherSkull extends Render<EntityWitherSkull> {
   private static final ResourceLocation INVULNERABLE_WITHER_TEXTURES = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
   private static final ResourceLocation WITHER_TEXTURES = new ResourceLocation("textures/entity/wither/wither.png");
   private final ModelSkeletonHead skeletonHeadModel = new ModelSkeletonHead();

   public RenderWitherSkull(RenderManager p_i46129_1_) {
      super(p_i46129_1_);
   }

   private float getRenderYaw(float p_82400_1_, float p_82400_2_, float p_82400_3_) {
      float f;
      for(f = p_82400_2_ - p_82400_1_; f < -180.0F; f += 360.0F) {
         ;
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return p_82400_1_ + p_82400_3_ * f;
   }

   public void doRender(EntityWitherSkull p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      float f = this.getRenderYaw(p_76986_1_.prevRotationYaw, p_76986_1_.rotationYaw, p_76986_9_);
      float f1 = p_76986_1_.prevRotationPitch + (p_76986_1_.rotationPitch - p_76986_1_.prevRotationPitch) * p_76986_9_;
      GlStateManager.translatef((float)p_76986_2_, (float)p_76986_4_, (float)p_76986_6_);
      float f2 = 0.0625F;
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlphaTest();
      this.bindEntityTexture(p_76986_1_);
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(p_76986_1_));
      }

      this.skeletonHeadModel.render(p_76986_1_, 0.0F, 0.0F, 0.0F, f, f1, 0.0625F);
      if (this.renderOutlines) {
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   protected ResourceLocation getEntityTexture(EntityWitherSkull p_110775_1_) {
      return p_110775_1_.isSkullInvulnerable() ? INVULNERABLE_WITHER_TEXTURES : WITHER_TEXTURES;
   }
}
