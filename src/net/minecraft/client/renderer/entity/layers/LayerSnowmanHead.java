package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSnowMan;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerSnowmanHead implements LayerRenderer<EntitySnowman> {
   private final RenderSnowMan snowManRenderer;

   public LayerSnowmanHead(RenderSnowMan p_i46110_1_) {
      this.snowManRenderer = p_i46110_1_;
   }

   public void doRenderLayer(EntitySnowman p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
      if (!p_177141_1_.isInvisible() && p_177141_1_.isPumpkinEquipped()) {
         GlStateManager.pushMatrix();
         this.snowManRenderer.getMainModel().func_205070_a().postRender(0.0625F);
         float f = 0.625F;
         GlStateManager.translatef(0.0F, -0.34375F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.scalef(0.625F, -0.625F, -0.625F);
         Minecraft.getInstance().getFirstPersonRenderer().func_178099_a(p_177141_1_, new ItemStack(Blocks.CARVED_PUMPKIN), ItemCameraTransforms.TransformType.HEAD);
         GlStateManager.popMatrix();
      }
   }

   public boolean shouldCombineTextures() {
      return true;
   }
}
