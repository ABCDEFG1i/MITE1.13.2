package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerHeldItem implements LayerRenderer<EntityLivingBase> {
   protected final RenderLivingBase<?> livingEntityRenderer;

   public LayerHeldItem(RenderLivingBase<?> p_i46115_1_) {
      this.livingEntityRenderer = p_i46115_1_;
   }

   public void doRenderLayer(EntityLivingBase p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
      boolean flag = p_177141_1_.getPrimaryHand() == EnumHandSide.RIGHT;
      ItemStack itemstack = flag ? p_177141_1_.getHeldItemOffhand() : p_177141_1_.getHeldItemMainhand();
      ItemStack itemstack1 = flag ? p_177141_1_.getHeldItemMainhand() : p_177141_1_.getHeldItemOffhand();
      if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {
         GlStateManager.pushMatrix();
         if (this.livingEntityRenderer.getMainModel().isChild) {
            float f = 0.5F;
            GlStateManager.translatef(0.0F, 0.75F, 0.0F);
            GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         }

         this.func_188358_a(p_177141_1_, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
         this.func_188358_a(p_177141_1_, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
         GlStateManager.popMatrix();
      }
   }

   private void func_188358_a(EntityLivingBase p_188358_1_, ItemStack p_188358_2_, ItemCameraTransforms.TransformType p_188358_3_, EnumHandSide p_188358_4_) {
      if (!p_188358_2_.isEmpty()) {
         GlStateManager.pushMatrix();
         this.translateToHand(p_188358_4_);
         if (p_188358_1_.isSneaking()) {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
         }

         GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
         boolean flag = p_188358_4_ == EnumHandSide.LEFT;
         GlStateManager.translatef((float)(flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);
         Minecraft.getInstance().getFirstPersonRenderer().func_187462_a(p_188358_1_, p_188358_2_, p_188358_3_, flag);
         GlStateManager.popMatrix();
      }
   }

   protected void translateToHand(EnumHandSide p_191361_1_) {
      ((ModelBiped)this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625F, p_191361_1_);
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
