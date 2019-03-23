package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelEvokerFangs;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderEvokerFangs extends Render<EntityEvokerFangs> {
   private static final ResourceLocation EVOKER_ILLAGER_FANGS = new ResourceLocation("textures/entity/illager/evoker_fangs.png");
   private final ModelEvokerFangs model = new ModelEvokerFangs();

   public RenderEvokerFangs(RenderManager p_i47208_1_) {
      super(p_i47208_1_);
   }

   public void doRender(EntityEvokerFangs p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      float f = p_76986_1_.getAnimationProgress(p_76986_9_);
      if (f != 0.0F) {
         float f1 = 2.0F;
         if (f > 0.9F) {
            f1 = (float)((double)f1 * ((1.0D - (double)f) / (double)0.1F));
         }

         GlStateManager.pushMatrix();
         GlStateManager.disableCull();
         GlStateManager.enableAlphaTest();
         this.bindEntityTexture(p_76986_1_);
         GlStateManager.translatef((float)p_76986_2_, (float)p_76986_4_, (float)p_76986_6_);
         GlStateManager.rotatef(90.0F - p_76986_1_.rotationYaw, 0.0F, 1.0F, 0.0F);
         GlStateManager.scalef(-f1, -f1, f1);
         float f2 = 0.03125F;
         GlStateManager.translatef(0.0F, -0.626F, 0.0F);
         this.model.render(p_76986_1_, f, 0.0F, 0.0F, p_76986_1_.rotationYaw, p_76986_1_.rotationPitch, 0.03125F);
         GlStateManager.popMatrix();
         GlStateManager.enableCull();
         super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
      }
   }

   protected ResourceLocation getEntityTexture(EntityEvokerFangs p_110775_1_) {
      return EVOKER_ILLAGER_FANGS;
   }
}
