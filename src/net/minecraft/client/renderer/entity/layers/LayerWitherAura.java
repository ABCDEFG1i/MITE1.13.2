package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderWither;
import net.minecraft.client.renderer.entity.model.ModelWither;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerWitherAura implements LayerRenderer<EntityWither> {
   private static final ResourceLocation WITHER_ARMOR = new ResourceLocation("textures/entity/wither/wither_armor.png");
   private final RenderWither witherRenderer;
   private final ModelWither witherModel = new ModelWither(0.5F);

   public LayerWitherAura(RenderWither p_i46105_1_) {
      this.witherRenderer = p_i46105_1_;
   }

   public void doRenderLayer(EntityWither p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
      if (p_177141_1_.isArmored()) {
         GlStateManager.depthMask(!p_177141_1_.isInvisible());
         this.witherRenderer.bindTexture(WITHER_ARMOR);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         float f = (float)p_177141_1_.ticksExisted + p_177141_4_;
         float f1 = MathHelper.cos(f * 0.02F) * 3.0F;
         float f2 = f * 0.01F;
         GlStateManager.translatef(f1, f2, 0.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.enableBlend();
         float f3 = 0.5F;
         GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
         this.witherModel.setLivingAnimations(p_177141_1_, p_177141_2_, p_177141_3_, p_177141_4_);
         this.witherModel.setModelAttributes(this.witherRenderer.getMainModel());
         Minecraft.getInstance().entityRenderer.func_191514_d(true);
         this.witherModel.render(p_177141_1_, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_);
         Minecraft.getInstance().entityRenderer.func_191514_d(false);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
