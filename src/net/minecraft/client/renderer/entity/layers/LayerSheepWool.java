package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSheep;
import net.minecraft.client.renderer.entity.model.ModelSheepWool;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerSheepWool implements LayerRenderer<EntitySheep> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
   private final RenderSheep sheepRenderer;
   private final ModelSheepWool sheepModel = new ModelSheepWool();

   public LayerSheepWool(RenderSheep p_i46112_1_) {
      this.sheepRenderer = p_i46112_1_;
   }

   public void doRenderLayer(EntitySheep p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
      if (!p_177141_1_.getSheared() && !p_177141_1_.isInvisible()) {
         this.sheepRenderer.bindTexture(TEXTURE);
         if (p_177141_1_.hasCustomName() && "jeb_".equals(p_177141_1_.getName().getUnformattedComponentText())) {
            int i1 = 25;
            int i = p_177141_1_.ticksExisted / 25 + p_177141_1_.getEntityId();
            int j = EnumDyeColor.values().length;
            int k = i % j;
            int l = (i + 1) % j;
            float f = ((float)(p_177141_1_.ticksExisted % 25) + p_177141_4_) / 25.0F;
            float[] afloat1 = EntitySheep.getDyeRgb(EnumDyeColor.byId(k));
            float[] afloat2 = EntitySheep.getDyeRgb(EnumDyeColor.byId(l));
            GlStateManager.color3f(afloat1[0] * (1.0F - f) + afloat2[0] * f, afloat1[1] * (1.0F - f) + afloat2[1] * f, afloat1[2] * (1.0F - f) + afloat2[2] * f);
         } else {
            float[] afloat = EntitySheep.getDyeRgb(p_177141_1_.getFleeceColor());
            GlStateManager.color3f(afloat[0], afloat[1], afloat[2]);
         }

         this.sheepModel.setModelAttributes(this.sheepRenderer.getMainModel());
         this.sheepModel.setLivingAnimations(p_177141_1_, p_177141_2_, p_177141_3_, p_177141_4_);
         this.sheepModel.render(p_177141_1_, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_);
      }
   }

   public boolean shouldCombineTextures() {
      return true;
   }
}
