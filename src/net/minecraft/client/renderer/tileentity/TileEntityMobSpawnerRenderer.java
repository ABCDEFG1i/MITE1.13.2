package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityMobSpawnerRenderer extends TileEntityRenderer<TileEntityMobSpawner> {
   public void render(TileEntityMobSpawner p_199341_1_, double p_199341_2_, double p_199341_4_, double p_199341_6_, float p_199341_8_, int p_199341_9_) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)p_199341_2_ + 0.5F, (float)p_199341_4_, (float)p_199341_6_ + 0.5F);
      renderMob(p_199341_1_.getSpawnerBaseLogic(), p_199341_2_, p_199341_4_, p_199341_6_, p_199341_8_);
      GlStateManager.popMatrix();
   }

   public static void renderMob(MobSpawnerBaseLogic p_147517_0_, double p_147517_1_, double p_147517_3_, double p_147517_5_, float p_147517_7_) {
      Entity entity = p_147517_0_.getCachedEntity();
      if (entity != null) {
         float f = 0.53125F;
         float f1 = Math.max(entity.width, entity.height);
         if ((double)f1 > 1.0D) {
            f /= f1;
         }

         GlStateManager.translatef(0.0F, 0.4F, 0.0F);
         GlStateManager.rotatef((float)(p_147517_0_.getPrevMobRotation() + (p_147517_0_.getMobRotation() - p_147517_0_.getPrevMobRotation()) * (double)p_147517_7_) * 10.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translatef(0.0F, -0.2F, 0.0F);
         GlStateManager.rotatef(-30.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.scalef(f, f, f);
         entity.setLocationAndAngles(p_147517_1_, p_147517_3_, p_147517_5_, 0.0F, 0.0F);
         Minecraft.getInstance().getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, p_147517_7_, false);
      }

   }
}
