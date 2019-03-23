package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.IMultipassModel;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelBoat;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBoat extends Render<EntityBoat> {
   private static final ResourceLocation[] BOAT_TEXTURES = new ResourceLocation[]{new ResourceLocation("textures/entity/boat/oak.png"), new ResourceLocation("textures/entity/boat/spruce.png"), new ResourceLocation("textures/entity/boat/birch.png"), new ResourceLocation("textures/entity/boat/jungle.png"), new ResourceLocation("textures/entity/boat/acacia.png"), new ResourceLocation("textures/entity/boat/dark_oak.png")};
   protected ModelBase modelBoat = new ModelBoat();

   public RenderBoat(RenderManager p_i46190_1_) {
      super(p_i46190_1_);
      this.shadowSize = 0.5F;
   }

   public void doRender(EntityBoat p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      GlStateManager.pushMatrix();
      this.setupTranslation(p_76986_2_, p_76986_4_, p_76986_6_);
      this.setupRotation(p_76986_1_, p_76986_8_, p_76986_9_);
      this.bindEntityTexture(p_76986_1_);
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(p_76986_1_));
      }

      this.modelBoat.render(p_76986_1_, p_76986_9_, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      if (this.renderOutlines) {
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   public void setupRotation(EntityBoat p_188311_1_, float p_188311_2_, float p_188311_3_) {
      GlStateManager.rotatef(180.0F - p_188311_2_, 0.0F, 1.0F, 0.0F);
      float f = (float)p_188311_1_.getTimeSinceHit() - p_188311_3_;
      float f1 = p_188311_1_.getDamageTaken() - p_188311_3_;
      if (f1 < 0.0F) {
         f1 = 0.0F;
      }

      if (f > 0.0F) {
         GlStateManager.rotatef(MathHelper.sin(f) * f * f1 / 10.0F * (float)p_188311_1_.getForwardDirection(), 1.0F, 0.0F, 0.0F);
      }

      float f2 = p_188311_1_.func_203056_b(p_188311_3_);
      if (!MathHelper.epsilonEquals(f2, 0.0F)) {
         GlStateManager.rotatef(p_188311_1_.func_203056_b(p_188311_3_), 1.0F, 0.0F, 1.0F);
      }

      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
   }

   public void setupTranslation(double p_188309_1_, double p_188309_3_, double p_188309_5_) {
      GlStateManager.translatef((float)p_188309_1_, (float)p_188309_3_ + 0.375F, (float)p_188309_5_);
   }

   protected ResourceLocation getEntityTexture(EntityBoat p_110775_1_) {
      return BOAT_TEXTURES[p_110775_1_.getBoatType().ordinal()];
   }

   public boolean isMultipass() {
      return true;
   }

   public void renderMultipass(EntityBoat p_188300_1_, double p_188300_2_, double p_188300_4_, double p_188300_6_, float p_188300_8_, float p_188300_9_) {
      GlStateManager.pushMatrix();
      this.setupTranslation(p_188300_2_, p_188300_4_, p_188300_6_);
      this.setupRotation(p_188300_1_, p_188300_8_, p_188300_9_);
      this.bindEntityTexture(p_188300_1_);
      ((IMultipassModel)this.modelBoat).renderMultipass(p_188300_1_, p_188300_9_, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
   }
}
