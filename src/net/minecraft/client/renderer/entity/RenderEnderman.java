package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.layers.LayerEndermanEyes;
import net.minecraft.client.renderer.entity.layers.LayerHeldBlock;
import net.minecraft.client.renderer.entity.model.ModelEnderman;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderEnderman extends RenderLiving<EntityEnderman> {
   private static final ResourceLocation ENDERMAN_TEXTURES = new ResourceLocation("textures/entity/enderman/enderman.png");
   private final Random rnd = new Random();

   public RenderEnderman(RenderManager p_i46182_1_) {
      super(p_i46182_1_, new ModelEnderman(0.0F), 0.5F);
      this.addLayer(new LayerEndermanEyes(this));
      this.addLayer(new LayerHeldBlock(this));
   }

   public ModelEnderman getMainModel() {
      return (ModelEnderman)super.getMainModel();
   }

   public void doRender(EntityEnderman p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      IBlockState iblockstate = p_76986_1_.func_195405_dq();
      ModelEnderman modelenderman = this.getMainModel();
      modelenderman.isCarrying = iblockstate != null;
      modelenderman.isAttacking = p_76986_1_.isScreaming();
      if (p_76986_1_.isScreaming()) {
         double d0 = 0.02D;
         p_76986_2_ += this.rnd.nextGaussian() * 0.02D;
         p_76986_6_ += this.rnd.nextGaussian() * 0.02D;
      }

      super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   protected ResourceLocation getEntityTexture(EntityEnderman p_110775_1_) {
      return ENDERMAN_TEXTURES;
   }
}
