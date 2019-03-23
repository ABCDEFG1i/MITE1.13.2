package net.minecraft.client.renderer.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelMinecart;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderMinecart<T extends EntityMinecart> extends Render<T> {
   private static final ResourceLocation MINECART_TEXTURES = new ResourceLocation("textures/entity/minecart.png");
   protected ModelBase modelMinecart = new ModelMinecart();

   public RenderMinecart(RenderManager p_i46155_1_) {
      super(p_i46155_1_);
      this.shadowSize = 0.5F;
   }

   public void doRender(T p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      GlStateManager.pushMatrix();
      this.bindEntityTexture(p_76986_1_);
      long i = (long)p_76986_1_.getEntityId() * 493286711L;
      i = i * i * 4392167121L + i * 98761L;
      float f = (((float)(i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float f1 = (((float)(i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float f2 = (((float)(i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      GlStateManager.translatef(f, f1, f2);
      double d0 = p_76986_1_.lastTickPosX + (p_76986_1_.posX - p_76986_1_.lastTickPosX) * (double)p_76986_9_;
      double d1 = p_76986_1_.lastTickPosY + (p_76986_1_.posY - p_76986_1_.lastTickPosY) * (double)p_76986_9_;
      double d2 = p_76986_1_.lastTickPosZ + (p_76986_1_.posZ - p_76986_1_.lastTickPosZ) * (double)p_76986_9_;
      double d3 = (double)0.3F;
      Vec3d vec3d = p_76986_1_.getPos(d0, d1, d2);
      float f3 = p_76986_1_.prevRotationPitch + (p_76986_1_.rotationPitch - p_76986_1_.prevRotationPitch) * p_76986_9_;
      if (vec3d != null) {
         Vec3d vec3d1 = p_76986_1_.getPosOffset(d0, d1, d2, (double)0.3F);
         Vec3d vec3d2 = p_76986_1_.getPosOffset(d0, d1, d2, (double)-0.3F);
         if (vec3d1 == null) {
            vec3d1 = vec3d;
         }

         if (vec3d2 == null) {
            vec3d2 = vec3d;
         }

         p_76986_2_ += vec3d.x - d0;
         p_76986_4_ += (vec3d1.y + vec3d2.y) / 2.0D - d1;
         p_76986_6_ += vec3d.z - d2;
         Vec3d vec3d3 = vec3d2.add(-vec3d1.x, -vec3d1.y, -vec3d1.z);
         if (vec3d3.length() != 0.0D) {
            vec3d3 = vec3d3.normalize();
            p_76986_8_ = (float)(Math.atan2(vec3d3.z, vec3d3.x) * 180.0D / Math.PI);
            f3 = (float)(Math.atan(vec3d3.y) * 73.0D);
         }
      }

      GlStateManager.translatef((float)p_76986_2_, (float)p_76986_4_ + 0.375F, (float)p_76986_6_);
      GlStateManager.rotatef(180.0F - p_76986_8_, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-f3, 0.0F, 0.0F, 1.0F);
      float f5 = (float)p_76986_1_.getRollingAmplitude() - p_76986_9_;
      float f6 = p_76986_1_.getDamage() - p_76986_9_;
      if (f6 < 0.0F) {
         f6 = 0.0F;
      }

      if (f5 > 0.0F) {
         GlStateManager.rotatef(MathHelper.sin(f5) * f5 * f6 / 10.0F * (float)p_76986_1_.getRollingDirection(), 1.0F, 0.0F, 0.0F);
      }

      int j = p_76986_1_.getDisplayTileOffset();
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(p_76986_1_));
      }

      IBlockState iblockstate = p_76986_1_.getDisplayTile();
      if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
         GlStateManager.pushMatrix();
         this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         float f4 = 0.75F;
         GlStateManager.scalef(0.75F, 0.75F, 0.75F);
         GlStateManager.translatef(-0.5F, (float)(j - 8) / 16.0F, 0.5F);
         this.renderCartContents(p_76986_1_, p_76986_9_, iblockstate);
         GlStateManager.popMatrix();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindEntityTexture(p_76986_1_);
      }

      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      this.modelMinecart.render(p_76986_1_, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      if (this.renderOutlines) {
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      }

      super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   protected ResourceLocation getEntityTexture(T p_110775_1_) {
      return MINECART_TEXTURES;
   }

   protected void renderCartContents(T p_188319_1_, float p_188319_2_, IBlockState p_188319_3_) {
      GlStateManager.pushMatrix();
      Minecraft.getInstance().getBlockRendererDispatcher().renderBlockBrightness(p_188319_3_, p_188319_1_.getBrightness());
      GlStateManager.popMatrix();
   }
}
