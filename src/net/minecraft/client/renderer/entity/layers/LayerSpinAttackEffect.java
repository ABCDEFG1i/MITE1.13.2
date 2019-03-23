package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerSpinAttackEffect implements LayerRenderer<AbstractClientPlayer> {
   public static final ResourceLocation field_204836_a = new ResourceLocation("textures/entity/trident_riptide.png");
   private final RenderPlayer field_204837_b;
   private final LayerSpinAttackEffect.Model field_204838_c;

   public LayerSpinAttackEffect(RenderPlayer p_i48920_1_) {
      this.field_204837_b = p_i48920_1_;
      this.field_204838_c = new LayerSpinAttackEffect.Model();
   }

   public void doRenderLayer(AbstractClientPlayer p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
      if (p_177141_1_.isSpinAttacking()) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_204837_b.bindTexture(field_204836_a);

         for(int i = 0; i < 3; ++i) {
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(p_177141_5_ * (float)(-(45 + i * 5)), 0.0F, 1.0F, 0.0F);
            float f = 0.75F * (float)i;
            GlStateManager.scalef(f, f, f);
            GlStateManager.translatef(0.0F, -0.2F + 0.6F * (float)i, 0.0F);
            this.field_204838_c.render(p_177141_1_, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_);
            GlStateManager.popMatrix();
         }

      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   static class Model extends ModelBase {
      private final ModelRenderer field_204834_a;

      public Model() {
         this.textureWidth = 64;
         this.textureHeight = 64;
         this.field_204834_a = new ModelRenderer(this, 0, 0);
         this.field_204834_a.addBox(-8.0F, -16.0F, -8.0F, 16, 32, 16);
      }

      public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
         this.field_204834_a.render(p_78088_7_);
      }
   }
}
