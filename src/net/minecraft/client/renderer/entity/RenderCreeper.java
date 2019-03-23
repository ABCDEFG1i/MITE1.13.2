package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerCreeperCharge;
import net.minecraft.client.renderer.entity.model.ModelCreeper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderCreeper extends RenderLiving<EntityCreeper> {
   private static final ResourceLocation CREEPER_TEXTURES = new ResourceLocation("textures/entity/creeper/creeper.png");

   public RenderCreeper(RenderManager p_i46186_1_) {
      super(p_i46186_1_, new ModelCreeper(), 0.5F);
      this.addLayer(new LayerCreeperCharge(this));
   }

   protected void preRenderCallback(EntityCreeper p_77041_1_, float p_77041_2_) {
      float f = p_77041_1_.getCreeperFlashIntensity(p_77041_2_);
      float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = f * f;
      f = f * f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      GlStateManager.scalef(f2, f3, f2);
   }

   protected int getColorMultiplier(EntityCreeper p_77030_1_, float p_77030_2_, float p_77030_3_) {
      float f = p_77030_1_.getCreeperFlashIntensity(p_77030_3_);
      if ((int)(f * 10.0F) % 2 == 0) {
         return 0;
      } else {
         int i = (int)(f * 0.2F * 255.0F);
         i = MathHelper.clamp(i, 0, 255);
         return i << 24 | 822083583;
      }
   }

   protected ResourceLocation getEntityTexture(EntityCreeper p_110775_1_) {
      return CREEPER_TEXTURES;
   }
}
