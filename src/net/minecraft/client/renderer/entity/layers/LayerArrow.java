package net.minecraft.client.renderer.entity.layers;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.model.ModelBox;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerArrow implements LayerRenderer<EntityLivingBase> {
   private final RenderLivingBase<?> renderer;

   public LayerArrow(RenderLivingBase<?> p_i46124_1_) {
      this.renderer = p_i46124_1_;
   }

   public void doRenderLayer(EntityLivingBase p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
      int i = p_177141_1_.getArrowCountInEntity();
      if (i > 0) {
         Entity entity = new EntityTippedArrow(p_177141_1_.world, p_177141_1_.posX, p_177141_1_.posY, p_177141_1_.posZ);
         Random random = new Random((long)p_177141_1_.getEntityId());
         RenderHelper.disableStandardItemLighting();

         for(int j = 0; j < i; ++j) {
            GlStateManager.pushMatrix();
            ModelRenderer modelrenderer = this.renderer.getMainModel().getRandomModelBox(random);
            ModelBox modelbox = modelrenderer.cubeList.get(random.nextInt(modelrenderer.cubeList.size()));
            modelrenderer.postRender(0.0625F);
            float f = random.nextFloat();
            float f1 = random.nextFloat();
            float f2 = random.nextFloat();
            float f3 = (modelbox.posX1 + (modelbox.posX2 - modelbox.posX1) * f) / 16.0F;
            float f4 = (modelbox.posY1 + (modelbox.posY2 - modelbox.posY1) * f1) / 16.0F;
            float f5 = (modelbox.posZ1 + (modelbox.posZ2 - modelbox.posZ1) * f2) / 16.0F;
            GlStateManager.translatef(f3, f4, f5);
            f = f * 2.0F - 1.0F;
            f1 = f1 * 2.0F - 1.0F;
            f2 = f2 * 2.0F - 1.0F;
            f = f * -1.0F;
            f1 = f1 * -1.0F;
            f2 = f2 * -1.0F;
            float f6 = MathHelper.sqrt(f * f + f2 * f2);
            entity.rotationYaw = (float)(Math.atan2((double)f, (double)f2) * (double)(180F / (float)Math.PI));
            entity.rotationPitch = (float)(Math.atan2((double)f1, (double)f6) * (double)(180F / (float)Math.PI));
            entity.prevRotationYaw = entity.rotationYaw;
            entity.prevRotationPitch = entity.rotationPitch;
            double d0 = 0.0D;
            double d1 = 0.0D;
            double d2 = 0.0D;
            this.renderer.getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, p_177141_4_, false);
            GlStateManager.popMatrix();
         }

         RenderHelper.enableStandardItemLighting();
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
