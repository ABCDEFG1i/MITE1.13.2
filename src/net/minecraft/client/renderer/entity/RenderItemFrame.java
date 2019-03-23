package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderItemFrame extends Render<EntityItemFrame> {
   private static final ResourceLocation MAP_BACKGROUND_TEXTURES = new ResourceLocation("textures/map/map_background.png");
   private static final ModelResourceLocation field_209585_f = new ModelResourceLocation("item_frame", "map=false");
   private static final ModelResourceLocation field_209586_g = new ModelResourceLocation("item_frame", "map=true");
   private final Minecraft mc = Minecraft.getInstance();
   private final ItemRenderer itemRenderer;

   public RenderItemFrame(RenderManager p_i46166_1_, ItemRenderer p_i46166_2_) {
      super(p_i46166_1_);
      this.itemRenderer = p_i46166_2_;
   }

   public void doRender(EntityItemFrame p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      GlStateManager.pushMatrix();
      BlockPos blockpos = p_76986_1_.getHangingPosition();
      double d0 = (double)blockpos.getX() - p_76986_1_.posX + p_76986_2_;
      double d1 = (double)blockpos.getY() - p_76986_1_.posY + p_76986_4_;
      double d2 = (double)blockpos.getZ() - p_76986_1_.posZ + p_76986_6_;
      GlStateManager.translated(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D);
      GlStateManager.rotatef(p_76986_1_.rotationPitch, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(180.0F - p_76986_1_.rotationYaw, 0.0F, 1.0F, 0.0F);
      this.renderManager.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
      ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().func_178126_b();
      ModelResourceLocation modelresourcelocation = p_76986_1_.getDisplayedItem().getItem() == Items.FILLED_MAP ? field_209586_g : field_209585_f;
      GlStateManager.pushMatrix();
      GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(p_76986_1_));
      }

      blockrendererdispatcher.getBlockModelRenderer().func_178262_a(modelmanager.func_174953_a(modelresourcelocation), 1.0F, 1.0F, 1.0F, 1.0F);
      if (this.renderOutlines) {
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      GlStateManager.enableLighting();
      if (p_76986_1_.getDisplayedItem().getItem() == Items.FILLED_MAP) {
         GlStateManager.pushLightingAttrib();
         RenderHelper.enableStandardItemLighting();
      }

      GlStateManager.translatef(0.0F, 0.0F, 0.4375F);
      this.renderItem(p_76986_1_);
      if (p_76986_1_.getDisplayedItem().getItem() == Items.FILLED_MAP) {
         RenderHelper.disableStandardItemLighting();
         GlStateManager.popAttrib();
      }

      GlStateManager.enableLighting();
      GlStateManager.popMatrix();
      this.renderName(p_76986_1_, p_76986_2_ + (double)((float)p_76986_1_.facingDirection.getXOffset() * 0.3F), p_76986_4_ - 0.25D, p_76986_6_ + (double)((float)p_76986_1_.facingDirection.getZOffset() * 0.3F));
   }

   @Nullable
   protected ResourceLocation getEntityTexture(EntityItemFrame p_110775_1_) {
      return null;
   }

   private void renderItem(EntityItemFrame p_82402_1_) {
      ItemStack itemstack = p_82402_1_.getDisplayedItem();
      if (!itemstack.isEmpty()) {
         GlStateManager.pushMatrix();
         boolean flag = itemstack.getItem() == Items.FILLED_MAP;
         int i = flag ? p_82402_1_.getRotation() % 4 * 2 : p_82402_1_.getRotation();
         GlStateManager.rotatef((float)i * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
         if (flag) {
            GlStateManager.disableLighting();
            this.renderManager.textureManager.bindTexture(MAP_BACKGROUND_TEXTURES);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            float f = 0.0078125F;
            GlStateManager.scalef(0.0078125F, 0.0078125F, 0.0078125F);
            GlStateManager.translatef(-64.0F, -64.0F, 0.0F);
            MapData mapdata = ItemMap.getMapData(itemstack, p_82402_1_.world);
            GlStateManager.translatef(0.0F, 0.0F, -1.0F);
            if (mapdata != null) {
               this.mc.entityRenderer.func_147701_i().renderMap(mapdata, true);
            }
         } else {
            GlStateManager.scalef(0.5F, 0.5F, 0.5F);
            this.itemRenderer.func_181564_a(itemstack, ItemCameraTransforms.TransformType.FIXED);
         }

         GlStateManager.popMatrix();
      }
   }

   protected void renderName(EntityItemFrame p_177067_1_, double p_177067_2_, double p_177067_4_, double p_177067_6_) {
      if (Minecraft.isGuiEnabled() && !p_177067_1_.getDisplayedItem().isEmpty() && p_177067_1_.getDisplayedItem().hasDisplayName() && this.renderManager.pointedEntity == p_177067_1_) {
         double d0 = p_177067_1_.getDistanceSq(this.renderManager.renderViewEntity);
         float f = p_177067_1_.isSneaking() ? 32.0F : 64.0F;
         if (!(d0 >= (double)(f * f))) {
            String s = p_177067_1_.getDisplayedItem().getDisplayName().getFormattedText();
            this.renderLivingLabel(p_177067_1_, s, p_177067_2_, p_177067_4_, p_177067_6_, 64);
         }
      }
   }
}
