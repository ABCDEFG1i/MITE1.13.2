package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.client.renderer.entity.model.ModelShulker;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderShulker extends RenderLiving<EntityShulker> {
   public static final ResourceLocation field_204402_a = new ResourceLocation("textures/entity/shulker/shulker.png");
   public static final ResourceLocation[] SHULKER_ENDERGOLEM_TEXTURE = new ResourceLocation[]{new ResourceLocation("textures/entity/shulker/shulker_white.png"), new ResourceLocation("textures/entity/shulker/shulker_orange.png"), new ResourceLocation("textures/entity/shulker/shulker_magenta.png"), new ResourceLocation("textures/entity/shulker/shulker_light_blue.png"), new ResourceLocation("textures/entity/shulker/shulker_yellow.png"), new ResourceLocation("textures/entity/shulker/shulker_lime.png"), new ResourceLocation("textures/entity/shulker/shulker_pink.png"), new ResourceLocation("textures/entity/shulker/shulker_gray.png"), new ResourceLocation("textures/entity/shulker/shulker_light_gray.png"), new ResourceLocation("textures/entity/shulker/shulker_cyan.png"), new ResourceLocation("textures/entity/shulker/shulker_purple.png"), new ResourceLocation("textures/entity/shulker/shulker_blue.png"), new ResourceLocation("textures/entity/shulker/shulker_brown.png"), new ResourceLocation("textures/entity/shulker/shulker_green.png"), new ResourceLocation("textures/entity/shulker/shulker_red.png"), new ResourceLocation("textures/entity/shulker/shulker_black.png")};

   public RenderShulker(RenderManager p_i47194_1_) {
      super(p_i47194_1_, new ModelShulker(), 0.0F);
      this.addLayer(new RenderShulker.HeadLayer());
   }

   public ModelShulker getMainModel() {
      return (ModelShulker)super.getMainModel();
   }

   public void doRender(EntityShulker p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      int i = p_76986_1_.getClientTeleportInterp();
      if (i > 0 && p_76986_1_.isAttachedToBlock()) {
         BlockPos blockpos = p_76986_1_.getAttachmentPos();
         BlockPos blockpos1 = p_76986_1_.getOldAttachPos();
         double d0 = (double)((float)i - p_76986_9_) / 6.0D;
         d0 = d0 * d0;
         double d1 = (double)(blockpos.getX() - blockpos1.getX()) * d0;
         double d2 = (double)(blockpos.getY() - blockpos1.getY()) * d0;
         double d3 = (double)(blockpos.getZ() - blockpos1.getZ()) * d0;
         super.doRender(p_76986_1_, p_76986_2_ - d1, p_76986_4_ - d2, p_76986_6_ - d3, p_76986_8_, p_76986_9_);
      } else {
         super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
      }

   }

   public boolean shouldRender(EntityShulker p_177071_1_, ICamera p_177071_2_, double p_177071_3_, double p_177071_5_, double p_177071_7_) {
      if (super.shouldRender(p_177071_1_, p_177071_2_, p_177071_3_, p_177071_5_, p_177071_7_)) {
         return true;
      } else {
         if (p_177071_1_.getClientTeleportInterp() > 0 && p_177071_1_.isAttachedToBlock()) {
            BlockPos blockpos = p_177071_1_.getOldAttachPos();
            BlockPos blockpos1 = p_177071_1_.getAttachmentPos();
            Vec3d vec3d = new Vec3d((double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ());
            Vec3d vec3d1 = new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
            if (p_177071_2_.isBoundingBoxInFrustum(new AxisAlignedBB(vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y, vec3d.z))) {
               return true;
            }
         }

         return false;
      }
   }

   protected ResourceLocation getEntityTexture(EntityShulker p_110775_1_) {
      return p_110775_1_.getColor() == null ? field_204402_a : SHULKER_ENDERGOLEM_TEXTURE[p_110775_1_.getColor().getId()];
   }

   protected void applyRotations(EntityShulker p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
      super.applyRotations(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
      switch(p_77043_1_.getAttachmentFacing()) {
      case DOWN:
      default:
         break;
      case EAST:
         GlStateManager.translatef(0.5F, 0.5F, 0.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
         break;
      case WEST:
         GlStateManager.translatef(-0.5F, 0.5F, 0.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
         break;
      case NORTH:
         GlStateManager.translatef(0.0F, 0.5F, -0.5F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         break;
      case SOUTH:
         GlStateManager.translatef(0.0F, 0.5F, 0.5F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         break;
      case UP:
         GlStateManager.translatef(0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
      }

   }

   protected void preRenderCallback(EntityShulker p_77041_1_, float p_77041_2_) {
      float f = 0.999F;
      GlStateManager.scalef(0.999F, 0.999F, 0.999F);
   }

   @OnlyIn(Dist.CLIENT)
   class HeadLayer implements LayerRenderer<EntityShulker> {
      private HeadLayer() {
      }

      public void doRenderLayer(EntityShulker p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
         GlStateManager.pushMatrix();
         switch(p_177141_1_.getAttachmentFacing()) {
         case DOWN:
         default:
            break;
         case EAST:
            GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translatef(1.0F, -1.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            break;
         case WEST:
            GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translatef(-1.0F, -1.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            break;
         case NORTH:
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translatef(0.0F, -1.0F, -1.0F);
            break;
         case SOUTH:
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translatef(0.0F, -1.0F, 1.0F);
            break;
         case UP:
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translatef(0.0F, -2.0F, 0.0F);
         }

         ModelRenderer modelrenderer = RenderShulker.this.getMainModel().func_205067_c();
         modelrenderer.rotateAngleY = p_177141_6_ * ((float)Math.PI / 180F);
         modelrenderer.rotateAngleX = p_177141_7_ * ((float)Math.PI / 180F);
         EnumDyeColor enumdyecolor = p_177141_1_.getColor();
         if (enumdyecolor == null) {
            RenderShulker.this.bindTexture(RenderShulker.field_204402_a);
         } else {
            RenderShulker.this.bindTexture(RenderShulker.SHULKER_ENDERGOLEM_TEXTURE[enumdyecolor.getId()]);
         }

         modelrenderer.render(p_177141_8_);
         GlStateManager.popMatrix();
      }

      public boolean shouldCombineTextures() {
         return false;
      }
   }
}
