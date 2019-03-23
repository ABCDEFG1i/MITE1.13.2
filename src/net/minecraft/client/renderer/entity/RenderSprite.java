package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderSprite<T extends Entity> extends Render<T> {
   protected final Item item;
   private final ItemRenderer itemRenderer;

   public RenderSprite(RenderManager p_i46137_1_, Item p_i46137_2_, ItemRenderer p_i46137_3_) {
      super(p_i46137_1_);
      this.item = p_i46137_2_;
      this.itemRenderer = p_i46137_3_;
   }

   public void doRender(T p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)p_76986_2_, (float)p_76986_4_, (float)p_76986_6_);
      GlStateManager.enableRescaleNormal();
      GlStateManager.rotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
      this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(p_76986_1_));
      }

      this.itemRenderer.func_181564_a(this.getStackToRender(p_76986_1_), ItemCameraTransforms.TransformType.GROUND);
      if (this.renderOutlines) {
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   public ItemStack getStackToRender(T p_177082_1_) {
      return new ItemStack(this.item);
   }

   protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
      return TextureMap.LOCATION_BLOCKS_TEXTURE;
   }
}
