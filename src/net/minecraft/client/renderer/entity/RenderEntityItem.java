package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderEntityItem extends Render<EntityItem> {
   private final ItemRenderer itemRenderer;
   private final Random random = new Random();

   public RenderEntityItem(RenderManager p_i46167_1_, ItemRenderer p_i46167_2_) {
      super(p_i46167_1_);
      this.itemRenderer = p_i46167_2_;
      this.shadowSize = 0.15F;
      this.shadowOpaque = 0.75F;
   }

   private int func_177077_a(EntityItem p_177077_1_, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_) {
      ItemStack itemstack = p_177077_1_.getItem();
      Item item = itemstack.getItem();
      if (item == null) {
         return 0;
      } else {
         boolean flag = p_177077_9_.func_177556_c();
         int i = this.getModelCount(itemstack);
         float f = 0.25F;
         float f1 = MathHelper.sin(((float)p_177077_1_.getAge() + p_177077_8_) / 10.0F + p_177077_1_.hoverStart) * 0.1F + 0.1F;
         float f2 = p_177077_9_.func_177552_f().func_181688_b(ItemCameraTransforms.TransformType.GROUND).field_178363_d.getY();
         GlStateManager.translatef((float)p_177077_2_, (float)p_177077_4_ + f1 + 0.25F * f2, (float)p_177077_6_);
         if (flag || this.renderManager.options != null) {
            float f3 = (((float)p_177077_1_.getAge() + p_177077_8_) / 20.0F + p_177077_1_.hoverStart) * (180F / (float)Math.PI);
            GlStateManager.rotatef(f3, 0.0F, 1.0F, 0.0F);
         }

         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         return i;
      }
   }

   protected int getModelCount(ItemStack p_177078_1_) {
      int i = 1;
      if (p_177078_1_.getCount() > 48) {
         i = 5;
      } else if (p_177078_1_.getCount() > 32) {
         i = 4;
      } else if (p_177078_1_.getCount() > 16) {
         i = 3;
      } else if (p_177078_1_.getCount() > 1) {
         i = 2;
      }

      return i;
   }

   public void doRender(EntityItem p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      ItemStack itemstack = p_76986_1_.getItem();
      int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getDamage();
      this.random.setSeed((long)i);
      boolean flag = false;
      if (this.bindEntityTexture(p_76986_1_)) {
         this.renderManager.textureManager.getTexture(this.getEntityTexture(p_76986_1_)).setBlurMipmap(false, false);
         flag = true;
      }

      GlStateManager.enableRescaleNormal();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.pushMatrix();
      IBakedModel ibakedmodel = this.itemRenderer.func_184393_a(itemstack, p_76986_1_.world, null);
      int j = this.func_177077_a(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_9_, ibakedmodel);
      float f = ibakedmodel.func_177552_f().field_181699_o.field_178363_d.getX();
      float f1 = ibakedmodel.func_177552_f().field_181699_o.field_178363_d.getY();
      float f2 = ibakedmodel.func_177552_f().field_181699_o.field_178363_d.getZ();
      boolean flag1 = ibakedmodel.func_177556_c();
      if (!flag1) {
         float f3 = -0.0F * (float)(j - 1) * 0.5F * f;
         float f4 = -0.0F * (float)(j - 1) * 0.5F * f1;
         float f5 = -0.09375F * (float)(j - 1) * 0.5F * f2;
         GlStateManager.translatef(f3, f4, f5);
      }

      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(p_76986_1_));
      }

      for(int k = 0; k < j; ++k) {
         if (flag1) {
            GlStateManager.pushMatrix();
            if (k > 0) {
               float f7 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float f9 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float f6 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               GlStateManager.translatef(f7, f9, f6);
            }

            ibakedmodel.func_177552_f().func_181689_a(ItemCameraTransforms.TransformType.GROUND);
            this.itemRenderer.func_180454_a(itemstack, ibakedmodel);
            GlStateManager.popMatrix();
         } else {
            GlStateManager.pushMatrix();
            if (k > 0) {
               float f8 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               GlStateManager.translatef(f8, f10, 0.0F);
            }

            ibakedmodel.func_177552_f().func_181689_a(ItemCameraTransforms.TransformType.GROUND);
            this.itemRenderer.func_180454_a(itemstack, ibakedmodel);
            GlStateManager.popMatrix();
            GlStateManager.translatef(0.0F * f, 0.0F * f1, 0.09375F * f2);
         }
      }

      if (this.renderOutlines) {
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
      this.bindEntityTexture(p_76986_1_);
      if (flag) {
         this.renderManager.textureManager.getTexture(this.getEntityTexture(p_76986_1_)).restoreLastBlurMipmap();
      }

      super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   protected ResourceLocation getEntityTexture(EntityItem p_110775_1_) {
      return TextureMap.LOCATION_BLOCKS_TEXTURE;
   }
}
