package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelEnderCrystal;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderEnderCrystal extends Render<EntityEnderCrystal> {
   private static final ResourceLocation ENDER_CRYSTAL_TEXTURES = new ResourceLocation("textures/entity/end_crystal/end_crystal.png");
   private final ModelBase modelEnderCrystal = new ModelEnderCrystal(0.0F, true);
   private final ModelBase modelEnderCrystalNoBase = new ModelEnderCrystal(0.0F, false);

   public RenderEnderCrystal(RenderManager p_i46184_1_) {
      super(p_i46184_1_);
      this.shadowSize = 0.5F;
   }

   public void doRender(EntityEnderCrystal p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      float f = (float)p_76986_1_.innerRotation + p_76986_9_;
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)p_76986_2_, (float)p_76986_4_, (float)p_76986_6_);
      this.bindTexture(ENDER_CRYSTAL_TEXTURES);
      float f1 = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
      f1 = f1 * f1 + f1;
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(p_76986_1_));
      }

      if (p_76986_1_.shouldShowBottom()) {
         this.modelEnderCrystal.render(p_76986_1_, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
      } else {
         this.modelEnderCrystalNoBase.render(p_76986_1_, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
      }

      if (this.renderOutlines) {
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      BlockPos blockpos = p_76986_1_.getBeamTarget();
      if (blockpos != null) {
         this.bindTexture(RenderDragon.ENDERCRYSTAL_BEAM_TEXTURES);
         float f2 = (float)blockpos.getX() + 0.5F;
         float f3 = (float)blockpos.getY() + 0.5F;
         float f4 = (float)blockpos.getZ() + 0.5F;
         double d0 = (double)f2 - p_76986_1_.posX;
         double d1 = (double)f3 - p_76986_1_.posY;
         double d2 = (double)f4 - p_76986_1_.posZ;
         RenderDragon.renderCrystalBeams(p_76986_2_ + d0, p_76986_4_ - 0.3D + (double)(f1 * 0.4F) + d1, p_76986_6_ + d2, p_76986_9_, (double)f2, (double)f3, (double)f4, p_76986_1_.innerRotation, p_76986_1_.posX, p_76986_1_.posY, p_76986_1_.posZ);
      }

      super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   protected ResourceLocation getEntityTexture(EntityEnderCrystal p_110775_1_) {
      return ENDER_CRYSTAL_TEXTURES;
   }

   public boolean shouldRender(EntityEnderCrystal p_177071_1_, ICamera p_177071_2_, double p_177071_3_, double p_177071_5_, double p_177071_7_) {
      return super.shouldRender(p_177071_1_, p_177071_2_, p_177071_3_, p_177071_5_, p_177071_7_) || p_177071_1_.getBeamTarget() != null;
   }
}
