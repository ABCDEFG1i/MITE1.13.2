package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelVex;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderVex extends RenderBiped<EntityVex> {
   private static final ResourceLocation VEX_TEXTURE = new ResourceLocation("textures/entity/illager/vex.png");
   private static final ResourceLocation VEX_CHARGING_TEXTURE = new ResourceLocation("textures/entity/illager/vex_charging.png");

   public RenderVex(RenderManager p_i47190_1_) {
      super(p_i47190_1_, new ModelVex(), 0.3F);
   }

   protected ResourceLocation getEntityTexture(EntityVex p_110775_1_) {
      return p_110775_1_.isCharging() ? VEX_CHARGING_TEXTURE : VEX_TEXTURE;
   }

   protected void preRenderCallback(EntityVex p_77041_1_, float p_77041_2_) {
      GlStateManager.scalef(0.4F, 0.4F, 0.4F);
   }
}
