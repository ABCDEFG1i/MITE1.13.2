package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.client.renderer.entity.model.ModelZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderZombie extends RenderBiped<EntityZombie> {
   private static final ResourceLocation ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/zombie.png");

   public RenderZombie(RenderManager p_i49206_1_, ModelBiped p_i49206_2_) {
      super(p_i49206_1_, p_i49206_2_, 0.5F);
      this.addLayer(this.createArmorLayer());
   }

   public RenderZombie(RenderManager p_i46127_1_) {
      this(p_i46127_1_, new ModelZombie());
   }

   protected LayerBipedArmor createArmorLayer() {
      return new LayerBipedArmor(this) {
         protected void initArmor() {
            this.modelLeggings = new ModelZombie(0.5F, true);
            this.modelArmor = new ModelZombie(1.0F, true);
         }
      };
   }

   protected ResourceLocation getEntityTexture(EntityZombie p_110775_1_) {
      return ZOMBIE_TEXTURES;
   }

   protected void applyRotations(EntityZombie p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
      if (p_77043_1_.isDrowning()) {
         p_77043_3_ += (float)(Math.cos((double)p_77043_1_.ticksExisted * 3.25D) * Math.PI * 0.25D);
      }

      super.applyRotations(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
   }
}
