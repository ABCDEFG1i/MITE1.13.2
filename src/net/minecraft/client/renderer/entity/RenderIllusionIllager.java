package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.model.ModelIllager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderIllusionIllager extends RenderLiving<EntityMob> {
   private static final ResourceLocation ILLUSIONIST = new ResourceLocation("textures/entity/illager/illusioner.png");

   public RenderIllusionIllager(RenderManager p_i47477_1_) {
      super(p_i47477_1_, new ModelIllager(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new LayerHeldItem(this) {
         public void doRenderLayer(EntityLivingBase p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
            if (((EntityIllusionIllager)p_177141_1_).isSpellcasting() || ((EntityIllusionIllager)p_177141_1_).isAggressive()) {
               super.doRenderLayer(p_177141_1_, p_177141_2_, p_177141_3_, p_177141_4_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_);
            }

         }

         protected void translateToHand(EnumHandSide p_191361_1_) {
            ((ModelIllager)this.livingEntityRenderer.getMainModel()).getArm(p_191361_1_).postRender(0.0625F);
         }
      });
      ((ModelIllager)this.getMainModel()).func_205062_a().showModel = true;
   }

   protected ResourceLocation getEntityTexture(EntityMob p_110775_1_) {
      return ILLUSIONIST;
   }

   protected void preRenderCallback(EntityMob p_77041_1_, float p_77041_2_) {
      float f = 0.9375F;
      GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
   }

   public void doRender(EntityMob p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      if (p_76986_1_.isInvisible()) {
         Vec3d[] avec3d = ((EntityIllusionIllager)p_76986_1_).getRenderLocations(p_76986_9_);
         float f = this.handleRotationFloat(p_76986_1_, p_76986_9_);

         for(int i = 0; i < avec3d.length; ++i) {
            super.doRender(p_76986_1_, p_76986_2_ + avec3d[i].x + (double)MathHelper.cos((float)i + f * 0.5F) * 0.025D, p_76986_4_ + avec3d[i].y + (double)MathHelper.cos((float)i + f * 0.75F) * 0.0125D, p_76986_6_ + avec3d[i].z + (double)MathHelper.cos((float)i + f * 0.7F) * 0.025D, p_76986_8_, p_76986_9_);
         }
      } else {
         super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
      }

   }

   protected boolean isVisible(EntityMob p_193115_1_) {
      return true;
   }
}
