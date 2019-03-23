package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderElderGuardian extends RenderGuardian {
   private static final ResourceLocation GUARDIAN_ELDER_TEXTURE = new ResourceLocation("textures/entity/guardian_elder.png");

   public RenderElderGuardian(RenderManager p_i47209_1_) {
      super(p_i47209_1_);
   }

   protected void preRenderCallback(EntityGuardian p_77041_1_, float p_77041_2_) {
      GlStateManager.scalef(2.35F, 2.35F, 2.35F);
   }

   protected ResourceLocation getEntityTexture(EntityGuardian p_110775_1_) {
      return GUARDIAN_ELDER_TEXTURE;
   }
}
