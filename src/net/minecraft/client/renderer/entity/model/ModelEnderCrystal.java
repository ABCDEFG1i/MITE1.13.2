package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelEnderCrystal extends ModelBase {
   private final ModelRenderer cube;
   private final ModelRenderer glass = new ModelRenderer(this, "glass");
   private final ModelRenderer base;

   public ModelEnderCrystal(float p_i1170_1_, boolean p_i1170_2_) {
      this.glass.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      this.cube = new ModelRenderer(this, "cube");
      this.cube.setTextureOffset(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      if (p_i1170_2_) {
         this.base = new ModelRenderer(this, "base");
         this.base.setTextureOffset(0, 16).addBox(-6.0F, 0.0F, -6.0F, 12, 4, 12);
      } else {
         this.base = null;
      }

   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      GlStateManager.pushMatrix();
      GlStateManager.scalef(2.0F, 2.0F, 2.0F);
      GlStateManager.translatef(0.0F, -0.5F, 0.0F);
      if (this.base != null) {
         this.base.render(p_78088_7_);
      }

      GlStateManager.rotatef(p_78088_3_, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(0.0F, 0.8F + p_78088_4_, 0.0F);
      GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
      this.glass.render(p_78088_7_);
      float f = 0.875F;
      GlStateManager.scalef(0.875F, 0.875F, 0.875F);
      GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
      GlStateManager.rotatef(p_78088_3_, 0.0F, 1.0F, 0.0F);
      this.glass.render(p_78088_7_);
      GlStateManager.scalef(0.875F, 0.875F, 0.875F);
      GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
      GlStateManager.rotatef(p_78088_3_, 0.0F, 1.0F, 0.0F);
      this.cube.render(p_78088_7_);
      GlStateManager.popMatrix();
   }
}
