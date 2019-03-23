package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelPufferFishBig;
import net.minecraft.client.renderer.entity.model.ModelPufferFishMedium;
import net.minecraft.client.renderer.entity.model.ModelPufferFishSmall;
import net.minecraft.entity.passive.EntityPufferFish;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderPufferFish extends RenderLiving<EntityPufferFish> {
   private static final ResourceLocation field_203771_a = new ResourceLocation("textures/entity/fish/pufferfish.png");
   private int field_203772_j;
   private final ModelPufferFishSmall field_203773_k = new ModelPufferFishSmall();
   private final ModelPufferFishMedium field_203774_l = new ModelPufferFishMedium();
   private final ModelPufferFishBig field_203775_m = new ModelPufferFishBig();

   public RenderPufferFish(RenderManager p_i48863_1_) {
      super(p_i48863_1_, new ModelPufferFishBig(), 0.1F);
      this.field_203772_j = 3;
   }

   @Nullable
   protected ResourceLocation getEntityTexture(EntityPufferFish p_110775_1_) {
      return field_203771_a;
   }

   public void doRender(EntityPufferFish p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      int i = p_76986_1_.getPuffState();
      if (i != this.field_203772_j) {
         if (i == 0) {
            this.mainModel = this.field_203773_k;
         } else if (i == 1) {
            this.mainModel = this.field_203774_l;
         } else {
            this.mainModel = this.field_203775_m;
         }
      }

      this.field_203772_j = i;
      this.shadowSize = 0.1F + 0.1F * (float)i;
      super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   protected void applyRotations(EntityPufferFish p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
      GlStateManager.translatef(0.0F, MathHelper.cos(p_77043_2_ * 0.05F) * 0.08F, 0.0F);
      super.applyRotations(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
   }
}
