package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelHorseArmorChests extends ModelHorseArmorBase {
   private final ModelRenderer field_199057_c = new ModelRenderer(this, 26, 21);
   private final ModelRenderer field_199058_d;

   public ModelHorseArmorChests() {
      this.field_199057_c.addBox(-4.0F, 0.0F, -2.0F, 8, 8, 3);
      this.field_199058_d = new ModelRenderer(this, 26, 21);
      this.field_199058_d.addBox(-4.0F, 0.0F, -2.0F, 8, 8, 3);
      this.field_199057_c.rotateAngleY = (-(float)Math.PI / 2F);
      this.field_199058_d.rotateAngleY = ((float)Math.PI / 2F);
      this.field_199057_c.setRotationPoint(6.0F, -8.0F, 0.0F);
      this.field_199058_d.setRotationPoint(-6.0F, -8.0F, 0.0F);
      this.field_199049_a.addChild(this.field_199057_c);
      this.field_199049_a.addChild(this.field_199058_d);
   }

   protected void func_199047_a(ModelRenderer p_199047_1_) {
      ModelRenderer modelrenderer = new ModelRenderer(this, 0, 12);
      modelrenderer.addBox(-1.0F, -7.0F, 0.0F, 2, 7, 1);
      modelrenderer.setRotationPoint(1.25F, -10.0F, 4.0F);
      ModelRenderer modelrenderer1 = new ModelRenderer(this, 0, 12);
      modelrenderer1.addBox(-1.0F, -7.0F, 0.0F, 2, 7, 1);
      modelrenderer1.setRotationPoint(-1.25F, -10.0F, 4.0F);
      modelrenderer.rotateAngleX = 0.2617994F;
      modelrenderer.rotateAngleZ = 0.2617994F;
      modelrenderer1.rotateAngleX = 0.2617994F;
      modelrenderer1.rotateAngleZ = -0.2617994F;
      p_199047_1_.addChild(modelrenderer);
      p_199047_1_.addChild(modelrenderer1);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      if (((AbstractChestHorse)p_78088_1_).hasChest()) {
         this.field_199057_c.showModel = true;
         this.field_199058_d.showModel = true;
      } else {
         this.field_199057_c.showModel = false;
         this.field_199058_d.showModel = false;
      }

      super.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);
   }
}
