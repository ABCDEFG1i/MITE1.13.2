package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IWorldReaderBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Render<T extends Entity> {
   private static final ResourceLocation SHADOW_TEXTURES = new ResourceLocation("textures/misc/shadow.png");
   protected final RenderManager renderManager;
   protected float shadowSize;
   protected float shadowOpaque = 1.0F;
   protected boolean renderOutlines;

   protected Render(RenderManager p_i46179_1_) {
      this.renderManager = p_i46179_1_;
   }

   public void setRenderOutlines(boolean p_188297_1_) {
      this.renderOutlines = p_188297_1_;
   }

   public boolean shouldRender(T p_177071_1_, ICamera p_177071_2_, double p_177071_3_, double p_177071_5_, double p_177071_7_) {
      AxisAlignedBB axisalignedbb = p_177071_1_.getRenderBoundingBox().grow(0.5D);
      if (axisalignedbb.hasNaN() || axisalignedbb.getAverageEdgeLength() == 0.0D) {
         axisalignedbb = new AxisAlignedBB(p_177071_1_.posX - 2.0D, p_177071_1_.posY - 2.0D, p_177071_1_.posZ - 2.0D, p_177071_1_.posX + 2.0D, p_177071_1_.posY + 2.0D, p_177071_1_.posZ + 2.0D);
      }

      return p_177071_1_.isInRangeToRender3d(p_177071_3_, p_177071_5_, p_177071_7_) && (p_177071_1_.ignoreFrustumCheck || p_177071_2_.isBoundingBoxInFrustum(axisalignedbb));
   }

   public void doRender(T p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      if (!this.renderOutlines) {
         this.renderName(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_);
      }

   }

   protected int getTeamColor(T p_188298_1_) {
      ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam)p_188298_1_.getTeam();
      return scoreplayerteam != null && scoreplayerteam.getColor().getColor() != null ? scoreplayerteam.getColor().getColor() : 16777215;
   }

   protected void renderName(T p_177067_1_, double p_177067_2_, double p_177067_4_, double p_177067_6_) {
      if (this.canRenderName(p_177067_1_)) {
         this.renderLivingLabel(p_177067_1_, p_177067_1_.getDisplayName().getFormattedText(), p_177067_2_, p_177067_4_, p_177067_6_, 64);
      }
   }

   protected boolean canRenderName(T p_177070_1_) {
      return p_177070_1_.getAlwaysRenderNameTagForRender() && p_177070_1_.hasCustomName();
   }

   protected void renderEntityName(T p_188296_1_, double p_188296_2_, double p_188296_4_, double p_188296_6_, String p_188296_8_, double p_188296_9_) {
      this.renderLivingLabel(p_188296_1_, p_188296_8_, p_188296_2_, p_188296_4_, p_188296_6_, 64);
   }

   @Nullable
   protected abstract ResourceLocation getEntityTexture(T p_110775_1_);

   protected boolean bindEntityTexture(T p_180548_1_) {
      ResourceLocation resourcelocation = this.getEntityTexture(p_180548_1_);
      if (resourcelocation == null) {
         return false;
      } else {
         this.bindTexture(resourcelocation);
         return true;
      }
   }

   public void bindTexture(ResourceLocation p_110776_1_) {
      this.renderManager.textureManager.bindTexture(p_110776_1_);
   }

   private void renderEntityOnFire(Entity p_76977_1_, double p_76977_2_, double p_76977_4_, double p_76977_6_, float p_76977_8_) {
      GlStateManager.disableLighting();
      TextureMap texturemap = Minecraft.getInstance().getTextureMap();
      TextureAtlasSprite textureatlassprite = texturemap.getSprite(ModelBakery.field_207763_a);
      TextureAtlasSprite textureatlassprite1 = texturemap.getSprite(ModelBakery.field_207764_b);
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)p_76977_2_, (float)p_76977_4_, (float)p_76977_6_);
      float f = p_76977_1_.width * 1.4F;
      GlStateManager.scalef(f, f, f);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      float f1 = 0.5F;
      float f2 = 0.0F;
      float f3 = p_76977_1_.height / f;
      float f4 = (float)(p_76977_1_.posY - p_76977_1_.getEntityBoundingBox().minY);
      GlStateManager.rotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(0.0F, 0.0F, -0.3F + (float)((int)f3) * 0.02F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f5 = 0.0F;
      int i = 0;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

      while(f3 > 0.0F) {
         TextureAtlasSprite textureatlassprite2 = i % 2 == 0 ? textureatlassprite : textureatlassprite1;
         this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         float f6 = textureatlassprite2.getMinU();
         float f7 = textureatlassprite2.getMinV();
         float f8 = textureatlassprite2.getMaxU();
         float f9 = textureatlassprite2.getMaxV();
         if (i / 2 % 2 == 0) {
            float f10 = f8;
            f8 = f6;
            f6 = f10;
         }

         bufferbuilder.pos((double)(f1 - 0.0F), (double)(0.0F - f4), (double)f5).tex((double)f8, (double)f9).endVertex();
         bufferbuilder.pos((double)(-f1 - 0.0F), (double)(0.0F - f4), (double)f5).tex((double)f6, (double)f9).endVertex();
         bufferbuilder.pos((double)(-f1 - 0.0F), (double)(1.4F - f4), (double)f5).tex((double)f6, (double)f7).endVertex();
         bufferbuilder.pos((double)(f1 - 0.0F), (double)(1.4F - f4), (double)f5).tex((double)f8, (double)f7).endVertex();
         f3 -= 0.45F;
         f4 -= 0.45F;
         f1 *= 0.9F;
         f5 += 0.03F;
         ++i;
      }

      tessellator.draw();
      GlStateManager.popMatrix();
      GlStateManager.enableLighting();
   }

   private void renderShadow(Entity p_76975_1_, double p_76975_2_, double p_76975_4_, double p_76975_6_, float p_76975_8_, float p_76975_9_) {
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      this.renderManager.textureManager.bindTexture(SHADOW_TEXTURES);
      IWorldReaderBase iworldreaderbase = this.getWorldFromRenderManager();
      GlStateManager.depthMask(false);
      float f = this.shadowSize;
      if (p_76975_1_ instanceof EntityLiving) {
         EntityLiving entityliving = (EntityLiving)p_76975_1_;
         f *= entityliving.getRenderSizeModifier();
         if (entityliving.isChild()) {
            f *= 0.5F;
         }
      }

      double d5 = p_76975_1_.lastTickPosX + (p_76975_1_.posX - p_76975_1_.lastTickPosX) * (double)p_76975_9_;
      double d0 = p_76975_1_.lastTickPosY + (p_76975_1_.posY - p_76975_1_.lastTickPosY) * (double)p_76975_9_;
      double d1 = p_76975_1_.lastTickPosZ + (p_76975_1_.posZ - p_76975_1_.lastTickPosZ) * (double)p_76975_9_;
      int i = MathHelper.floor(d5 - (double)f);
      int j = MathHelper.floor(d5 + (double)f);
      int k = MathHelper.floor(d0 - (double)f);
      int l = MathHelper.floor(d0);
      int i1 = MathHelper.floor(d1 - (double)f);
      int j1 = MathHelper.floor(d1 + (double)f);
      double d2 = p_76975_2_ - d5;
      double d3 = p_76975_4_ - d0;
      double d4 = p_76975_6_ - d1;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

      for(BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(i, k, i1), new BlockPos(j, l, j1))) {
         IBlockState iblockstate = iworldreaderbase.getBlockState(blockpos.down());
         if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE && iworldreaderbase.getLight(blockpos) > 3) {
            this.renderShadowSingle(iblockstate, p_76975_2_, p_76975_4_, p_76975_6_, blockpos, p_76975_8_, f, d2, d3, d4);
         }
      }

      tessellator.draw();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
   }

   private IWorldReaderBase getWorldFromRenderManager() {
      return this.renderManager.world;
   }

   private void renderShadowSingle(IBlockState p_188299_1_, double p_188299_2_, double p_188299_4_, double p_188299_6_, BlockPos p_188299_8_, float p_188299_9_, float p_188299_10_, double p_188299_11_, double p_188299_13_, double p_188299_15_) {
      if (p_188299_1_.isFullCube()) {
         VoxelShape voxelshape = p_188299_1_.getShape(this.getWorldFromRenderManager(), p_188299_8_.down());
         if (!voxelshape.isEmpty()) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            double d0 = ((double)p_188299_9_ - (p_188299_4_ - ((double)p_188299_8_.getY() + p_188299_13_)) / 2.0D) * 0.5D * (double)this.getWorldFromRenderManager().getBrightness(p_188299_8_);
            if (!(d0 < 0.0D)) {
               if (d0 > 1.0D) {
                  d0 = 1.0D;
               }

               AxisAlignedBB axisalignedbb = voxelshape.getBoundingBox();
               double d1 = (double)p_188299_8_.getX() + axisalignedbb.minX + p_188299_11_;
               double d2 = (double)p_188299_8_.getX() + axisalignedbb.maxX + p_188299_11_;
               double d3 = (double)p_188299_8_.getY() + axisalignedbb.minY + p_188299_13_ + 0.015625D;
               double d4 = (double)p_188299_8_.getZ() + axisalignedbb.minZ + p_188299_15_;
               double d5 = (double)p_188299_8_.getZ() + axisalignedbb.maxZ + p_188299_15_;
               float f = (float)((p_188299_2_ - d1) / 2.0D / (double)p_188299_10_ + 0.5D);
               float f1 = (float)((p_188299_2_ - d2) / 2.0D / (double)p_188299_10_ + 0.5D);
               float f2 = (float)((p_188299_6_ - d4) / 2.0D / (double)p_188299_10_ + 0.5D);
               float f3 = (float)((p_188299_6_ - d5) / 2.0D / (double)p_188299_10_ + 0.5D);
               bufferbuilder.pos(d1, d3, d4).tex((double)f, (double)f2).color(1.0F, 1.0F, 1.0F, (float)d0).endVertex();
               bufferbuilder.pos(d1, d3, d5).tex((double)f, (double)f3).color(1.0F, 1.0F, 1.0F, (float)d0).endVertex();
               bufferbuilder.pos(d2, d3, d5).tex((double)f1, (double)f3).color(1.0F, 1.0F, 1.0F, (float)d0).endVertex();
               bufferbuilder.pos(d2, d3, d4).tex((double)f1, (double)f2).color(1.0F, 1.0F, 1.0F, (float)d0).endVertex();
            }
         }
      }
   }

   public static void renderOffsetAABB(AxisAlignedBB p_76978_0_, double p_76978_1_, double p_76978_3_, double p_76978_5_) {
      GlStateManager.disableTexture2D();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      bufferbuilder.setTranslation(p_76978_1_, p_76978_3_, p_76978_5_);
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_NORMAL);
      bufferbuilder.pos(p_76978_0_.minX, p_76978_0_.maxY, p_76978_0_.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.maxX, p_76978_0_.maxY, p_76978_0_.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.maxX, p_76978_0_.minY, p_76978_0_.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.minX, p_76978_0_.minY, p_76978_0_.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.minX, p_76978_0_.minY, p_76978_0_.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.maxX, p_76978_0_.minY, p_76978_0_.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.maxX, p_76978_0_.maxY, p_76978_0_.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.minX, p_76978_0_.maxY, p_76978_0_.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.minX, p_76978_0_.minY, p_76978_0_.minZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.maxX, p_76978_0_.minY, p_76978_0_.minZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.maxX, p_76978_0_.minY, p_76978_0_.maxZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.minX, p_76978_0_.minY, p_76978_0_.maxZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.minX, p_76978_0_.maxY, p_76978_0_.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.maxX, p_76978_0_.maxY, p_76978_0_.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.maxX, p_76978_0_.maxY, p_76978_0_.minZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.minX, p_76978_0_.maxY, p_76978_0_.minZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.minX, p_76978_0_.minY, p_76978_0_.maxZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.minX, p_76978_0_.maxY, p_76978_0_.maxZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.minX, p_76978_0_.maxY, p_76978_0_.minZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.minX, p_76978_0_.minY, p_76978_0_.minZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.maxX, p_76978_0_.minY, p_76978_0_.minZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.maxX, p_76978_0_.maxY, p_76978_0_.minZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.maxX, p_76978_0_.maxY, p_76978_0_.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      bufferbuilder.pos(p_76978_0_.maxX, p_76978_0_.minY, p_76978_0_.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      tessellator.draw();
      bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
      GlStateManager.enableTexture2D();
   }

   public void doRenderShadowAndFire(Entity p_76979_1_, double p_76979_2_, double p_76979_4_, double p_76979_6_, float p_76979_8_, float p_76979_9_) {
      if (this.renderManager.options != null) {
         if (this.renderManager.options.entityShadows && this.shadowSize > 0.0F && !p_76979_1_.isInvisible() && this.renderManager.isRenderShadow()) {
            double d0 = this.renderManager.getDistanceToCamera(p_76979_1_.posX, p_76979_1_.posY, p_76979_1_.posZ);
            float f = (float)((1.0D - d0 / 256.0D) * (double)this.shadowOpaque);
            if (f > 0.0F) {
               this.renderShadow(p_76979_1_, p_76979_2_, p_76979_4_, p_76979_6_, f, p_76979_9_);
            }
         }

         if (p_76979_1_.canRenderOnFire() && (!(p_76979_1_ instanceof EntityPlayer) || !((EntityPlayer)p_76979_1_).isSpectator())) {
            this.renderEntityOnFire(p_76979_1_, p_76979_2_, p_76979_4_, p_76979_6_, p_76979_9_);
         }

      }
   }

   public FontRenderer getFontRendererFromRenderManager() {
      return this.renderManager.getFontRenderer();
   }

   protected void renderLivingLabel(T p_147906_1_, String p_147906_2_, double p_147906_3_, double p_147906_5_, double p_147906_7_, int p_147906_9_) {
      double d0 = p_147906_1_.getDistanceSq(this.renderManager.renderViewEntity);
      if (!(d0 > (double)(p_147906_9_ * p_147906_9_))) {
         boolean flag = p_147906_1_.isSneaking();
         float f = this.renderManager.playerViewY;
         float f1 = this.renderManager.playerViewX;
         boolean flag1 = this.renderManager.options.thirdPersonView == 2;
         float f2 = p_147906_1_.height + 0.5F - (flag ? 0.25F : 0.0F);
         int i = "deadmau5".equals(p_147906_2_) ? -10 : 0;
         GameRenderer.func_189692_a(this.getFontRendererFromRenderManager(), p_147906_2_, (float)p_147906_3_, (float)p_147906_5_ + f2, (float)p_147906_7_, i, f, f1, flag1, flag);
      }
   }

   public RenderManager getRenderManager() {
      return this.renderManager;
   }

   public boolean isMultipass() {
      return false;
   }

   public void renderMultipass(T p_188300_1_, double p_188300_2_, double p_188300_4_, double p_188300_6_, float p_188300_8_, float p_188300_9_) {
   }
}
