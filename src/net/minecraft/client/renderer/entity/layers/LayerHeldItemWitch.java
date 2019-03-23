package net.minecraft.client.renderer.entity.layers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderWitch;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerHeldItemWitch implements LayerRenderer<EntityWitch> {
   private final RenderWitch witchRenderer;

   public LayerHeldItemWitch(RenderWitch p_i46106_1_) {
      this.witchRenderer = p_i46106_1_;
   }

   public void doRenderLayer(EntityWitch p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
      ItemStack itemstack = p_177141_1_.getHeldItemMainhand();
      if (!itemstack.isEmpty()) {
         GlStateManager.color3f(1.0F, 1.0F, 1.0F);
         GlStateManager.pushMatrix();
         if (this.witchRenderer.getMainModel().isChild) {
            GlStateManager.translatef(0.0F, 0.625F, 0.0F);
            GlStateManager.rotatef(-20.0F, -1.0F, 0.0F, 0.0F);
            float f = 0.5F;
            GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         }

         this.witchRenderer.getMainModel().func_205073_b().postRender(0.0625F);
         GlStateManager.translatef(-0.0625F, 0.53125F, 0.21875F);
         Item item = itemstack.getItem();
         Minecraft minecraft = Minecraft.getInstance();
         if (Block.getBlockFromItem(item).getDefaultState().getRenderType() == EnumBlockRenderType.ENTITYBLOCK_ANIMATED) {
            GlStateManager.translatef(0.0F, 0.0625F, -0.25F);
            GlStateManager.rotatef(30.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(-5.0F, 0.0F, 1.0F, 0.0F);
            float f1 = 0.375F;
            GlStateManager.scalef(0.375F, -0.375F, 0.375F);
         } else if (item == Items.BOW) {
            GlStateManager.translatef(0.0F, 0.125F, -0.125F);
            GlStateManager.rotatef(-45.0F, 0.0F, 1.0F, 0.0F);
            float f2 = 0.625F;
            GlStateManager.scalef(0.625F, -0.625F, 0.625F);
            GlStateManager.rotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(-20.0F, 0.0F, 1.0F, 0.0F);
         } else {
            GlStateManager.translatef(0.1875F, 0.1875F, 0.0F);
            float f3 = 0.875F;
            GlStateManager.scalef(0.875F, 0.875F, 0.875F);
            GlStateManager.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(-60.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(-30.0F, 0.0F, 0.0F, 1.0F);
         }

         GlStateManager.rotatef(-15.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(40.0F, 0.0F, 0.0F, 1.0F);
         minecraft.getFirstPersonRenderer().func_178099_a(p_177141_1_, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
         GlStateManager.popMatrix();
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
