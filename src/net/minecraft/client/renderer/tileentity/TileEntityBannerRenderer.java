package net.minecraft.client.renderer.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockBannerWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBanner;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityBannerRenderer extends TileEntityRenderer<TileEntityBanner> {
   private final ModelBanner bannerModel = new ModelBanner();

   public void render(TileEntityBanner p_199341_1_, double p_199341_2_, double p_199341_4_, double p_199341_6_, float p_199341_8_, int p_199341_9_) {
      float f = 0.6666667F;
      boolean flag = p_199341_1_.getWorld() == null;
      GlStateManager.pushMatrix();
      ModelRenderer modelrenderer = this.bannerModel.func_205057_b();
      long i;
      if (flag) {
         i = 0L;
         GlStateManager.translatef((float)p_199341_2_ + 0.5F, (float)p_199341_4_ + 0.5F, (float)p_199341_6_ + 0.5F);
         modelrenderer.showModel = true;
      } else {
         i = p_199341_1_.getWorld().getTotalWorldTime();
         IBlockState iblockstate = p_199341_1_.getBlockState();
         if (iblockstate.getBlock() instanceof BlockBanner) {
            GlStateManager.translatef((float)p_199341_2_ + 0.5F, (float)p_199341_4_ + 0.5F, (float)p_199341_6_ + 0.5F);
            GlStateManager.rotatef((float)(-iblockstate.get(BlockBanner.ROTATION) * 360) / 16.0F, 0.0F, 1.0F, 0.0F);
            modelrenderer.showModel = true;
         } else {
            GlStateManager.translatef((float)p_199341_2_ + 0.5F, (float)p_199341_4_ - 0.16666667F, (float)p_199341_6_ + 0.5F);
            GlStateManager.rotatef(-iblockstate.get(BlockBannerWall.HORIZONTAL_FACING).getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
            GlStateManager.translatef(0.0F, -0.3125F, -0.4375F);
            modelrenderer.showModel = false;
         }
      }

      BlockPos blockpos = p_199341_1_.getPos();
      float f1 = (float)((long)(blockpos.getX() * 7 + blockpos.getY() * 9 + blockpos.getZ() * 13) + i) + p_199341_8_;
      this.bannerModel.func_205056_c().rotateAngleX = (-0.0125F + 0.01F * MathHelper.cos(f1 * (float)Math.PI * 0.02F)) * (float)Math.PI;
      GlStateManager.enableRescaleNormal();
      ResourceLocation resourcelocation = this.getBannerResourceLocation(p_199341_1_);
      if (resourcelocation != null) {
         this.bindTexture(resourcelocation);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.6666667F, -0.6666667F, -0.6666667F);
         this.bannerModel.renderBanner();
         GlStateManager.popMatrix();
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   @Nullable
   private ResourceLocation getBannerResourceLocation(TileEntityBanner p_178463_1_) {
      return BannerTextures.BANNER_DESIGNS.getResourceLocation(p_178463_1_.getPatternResourceLocation(), p_178463_1_.getPatternList(), p_178463_1_.getColorList());
   }
}
