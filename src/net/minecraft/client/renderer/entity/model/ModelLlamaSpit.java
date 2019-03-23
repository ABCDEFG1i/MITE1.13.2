package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelLlamaSpit extends ModelBase {
   private final ModelRenderer main = new ModelRenderer(this);

   public ModelLlamaSpit() {
      this(0.0F);
   }

   public ModelLlamaSpit(float p_i47225_1_) {
      int i = 2;
      this.main.setTextureOffset(0, 0).addBox(-4.0F, 0.0F, 0.0F, 2, 2, 2, p_i47225_1_);
      this.main.setTextureOffset(0, 0).addBox(0.0F, -4.0F, 0.0F, 2, 2, 2, p_i47225_1_);
      this.main.setTextureOffset(0, 0).addBox(0.0F, 0.0F, -4.0F, 2, 2, 2, p_i47225_1_);
      this.main.setTextureOffset(0, 0).addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, p_i47225_1_);
      this.main.setTextureOffset(0, 0).addBox(2.0F, 0.0F, 0.0F, 2, 2, 2, p_i47225_1_);
      this.main.setTextureOffset(0, 0).addBox(0.0F, 2.0F, 0.0F, 2, 2, 2, p_i47225_1_);
      this.main.setTextureOffset(0, 0).addBox(0.0F, 0.0F, 2.0F, 2, 2, 2, p_i47225_1_);
      this.main.setRotationPoint(0.0F, 0.0F, 0.0F);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
      this.main.render(p_78088_7_);
   }
}
