package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class TileEntityRenderer<T extends TileEntity> {
   public static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[]{new ResourceLocation("textures/" + ModelBakery.field_207770_h.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.field_207771_i.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.field_207772_j.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.field_207773_k.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.field_207774_l.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.field_207775_m.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.field_207776_n.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.field_207777_o.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.field_207778_p.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.field_207779_q.getPath() + ".png")};
   protected TileEntityRendererDispatcher rendererDispatcher;

   public void render(T p_199341_1_, double p_199341_2_, double p_199341_4_, double p_199341_6_, float p_199341_8_, int p_199341_9_) {
      if (p_199341_1_ instanceof INameable && this.rendererDispatcher.cameraHitResult != null && p_199341_1_.getPos().equals(this.rendererDispatcher.cameraHitResult.getBlockPos())) {
         this.setLightmapDisabled(true);
         this.drawNameplate(p_199341_1_, ((INameable)p_199341_1_).getDisplayName().getFormattedText(), p_199341_2_, p_199341_4_, p_199341_6_, 12);
         this.setLightmapDisabled(false);
      }

   }

   protected void setLightmapDisabled(boolean p_190053_1_) {
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE1);
      if (p_190053_1_) {
         GlStateManager.disableTexture2D();
      } else {
         GlStateManager.enableTexture2D();
      }

      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
   }

   protected void bindTexture(ResourceLocation p_147499_1_) {
      TextureManager texturemanager = this.rendererDispatcher.textureManager;
      if (texturemanager != null) {
         texturemanager.bindTexture(p_147499_1_);
      }

   }

   protected World getWorld() {
      return this.rendererDispatcher.world;
   }

   public void setRendererDispatcher(TileEntityRendererDispatcher p_147497_1_) {
      this.rendererDispatcher = p_147497_1_;
   }

   public FontRenderer getFontRenderer() {
      return this.rendererDispatcher.getFontRenderer();
   }

   public boolean isGlobalRenderer(T p_188185_1_) {
      return false;
   }

   protected void drawNameplate(T p_190052_1_, String p_190052_2_, double p_190052_3_, double p_190052_5_, double p_190052_7_, int p_190052_9_) {
      Entity entity = this.rendererDispatcher.entity;
      double d0 = p_190052_1_.getDistanceSq(entity.posX, entity.posY, entity.posZ);
      if (!(d0 > (double)(p_190052_9_ * p_190052_9_))) {
         float f = this.rendererDispatcher.entityYaw;
         float f1 = this.rendererDispatcher.entityPitch;
         boolean flag = false;
         GameRenderer.func_189692_a(this.getFontRenderer(), p_190052_2_, (float)p_190052_3_ + 0.5F, (float)p_190052_5_ + 1.5F, (float)p_190052_7_ + 0.5F, 0, f, f1, false, false);
      }
   }
}
