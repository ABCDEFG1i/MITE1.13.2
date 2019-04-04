package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmorDyeable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class LayerArmorBase<T extends ModelBase> implements LayerRenderer<EntityLivingBase> {
   protected static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   protected T modelLeggings;
   protected T modelArmor;
   private final RenderLivingBase<?> renderer;
   private float alpha = 1.0F;
   private float colorR = 1.0F;
   private float colorG = 1.0F;
   private float colorB = 1.0F;
   private boolean skipRenderGlint;
   private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();

   public LayerArmorBase(RenderLivingBase<?> p_i46125_1_) {
      this.renderer = p_i46125_1_;
      this.initArmor();
   }

   public void doRenderLayer(EntityLivingBase p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
      this.renderArmorLayer(p_177141_1_, p_177141_2_, p_177141_3_, p_177141_4_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_, EntityEquipmentSlot.CHEST);
      this.renderArmorLayer(p_177141_1_, p_177141_2_, p_177141_3_, p_177141_4_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_, EntityEquipmentSlot.LEGS);
      this.renderArmorLayer(p_177141_1_, p_177141_2_, p_177141_3_, p_177141_4_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_, EntityEquipmentSlot.FEET);
      this.renderArmorLayer(p_177141_1_, p_177141_2_, p_177141_3_, p_177141_4_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_, EntityEquipmentSlot.HEAD);
   }

   public boolean shouldCombineTextures() {
      return false;
   }

   private void renderArmorLayer(EntityLivingBase p_188361_1_, float p_188361_2_, float p_188361_3_, float p_188361_4_, float p_188361_5_, float p_188361_6_, float p_188361_7_, float p_188361_8_, EntityEquipmentSlot p_188361_9_) {
      ItemStack itemstack = p_188361_1_.getItemStackFromSlot(p_188361_9_);
      if (itemstack.getItem() instanceof ItemArmor) {
         ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
         if (itemarmor.getEquipmentSlot() == p_188361_9_) {
            T t = this.getModelFromSlot(p_188361_9_);
            t.setModelAttributes(this.renderer.getMainModel());
            t.setLivingAnimations(p_188361_1_, p_188361_2_, p_188361_3_, p_188361_4_);
            this.setModelSlotVisible(t, p_188361_9_);
            boolean flag = this.isLegSlot(p_188361_9_);
            this.renderer.bindTexture(this.getArmorResource(itemarmor, flag));
            if (itemarmor instanceof ItemArmorDyeable) {
               int i = ((ItemArmorDyeable)itemarmor).getColor(itemstack);
               float f = (float)(i >> 16 & 255) / 255.0F;
               float f1 = (float)(i >> 8 & 255) / 255.0F;
               float f2 = (float)(i & 255) / 255.0F;
               GlStateManager.color4f(this.colorR * f, this.colorG * f1, this.colorB * f2, this.alpha);
               t.render(p_188361_1_, p_188361_2_, p_188361_3_, p_188361_5_, p_188361_6_, p_188361_7_, p_188361_8_);
               this.renderer.bindTexture(this.getArmorResource(itemarmor, flag, "overlay"));
            }

            GlStateManager.color4f(this.colorR, this.colorG, this.colorB, this.alpha);
            t.render(p_188361_1_, p_188361_2_, p_188361_3_, p_188361_5_, p_188361_6_, p_188361_7_, p_188361_8_);
            if (!this.skipRenderGlint && itemstack.isEnchanted()) {
               renderEnchantedGlint(this.renderer, p_188361_1_, t, p_188361_2_, p_188361_3_, p_188361_4_, p_188361_5_, p_188361_6_, p_188361_7_, p_188361_8_);
            }

         }
      }
   }

   public T getModelFromSlot(EntityEquipmentSlot p_188360_1_) {
      return this.isLegSlot(p_188360_1_) ? this.modelLeggings : this.modelArmor;
   }

   private boolean isLegSlot(EntityEquipmentSlot p_188363_1_) {
      return p_188363_1_ == EntityEquipmentSlot.LEGS;
   }

   public static void renderEnchantedGlint(RenderLivingBase<?> p_188364_0_, EntityLivingBase p_188364_1_, ModelBase p_188364_2_, float p_188364_3_, float p_188364_4_, float p_188364_5_, float p_188364_6_, float p_188364_7_, float p_188364_8_, float p_188364_9_) {
      float f = (float)p_188364_1_.ticksExisted + p_188364_5_;
      p_188364_0_.bindTexture(ENCHANTED_ITEM_GLINT_RES);
      Minecraft.getInstance().entityRenderer.func_191514_d(true);
      GlStateManager.enableBlend();
      GlStateManager.depthFunc(514);
      GlStateManager.depthMask(false);
      float f1 = 0.5F;
      GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);

      for(int i = 0; i < 2; ++i) {
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
         float f2 = 0.76F;
         GlStateManager.color4f(0.38F, 0.19F, 0.608F, 1.0F);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         float f3 = 0.33333334F;
         GlStateManager.scalef(0.33333334F, 0.33333334F, 0.33333334F);
         GlStateManager.rotatef(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.translatef(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
         GlStateManager.matrixMode(5888);
         p_188364_2_.render(p_188364_1_, p_188364_3_, p_188364_4_, p_188364_6_, p_188364_7_, p_188364_8_, p_188364_9_);
         GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      }

      GlStateManager.matrixMode(5890);
      GlStateManager.loadIdentity();
      GlStateManager.matrixMode(5888);
      GlStateManager.enableLighting();
      GlStateManager.depthMask(true);
      GlStateManager.depthFunc(515);
      GlStateManager.disableBlend();
      Minecraft.getInstance().entityRenderer.func_191514_d(false);
   }

   private ResourceLocation getArmorResource(ItemArmor p_177181_1_, boolean p_177181_2_) {
      return this.getArmorResource(p_177181_1_, p_177181_2_, null);
   }

   private ResourceLocation getArmorResource(ItemArmor p_177178_1_, boolean p_177178_2_, @Nullable String p_177178_3_) {
      String s = "textures/models/armor/" + p_177178_1_.getArmorMaterial().getName() + "_layer_" + (p_177178_2_ ? 2 : 1) + (p_177178_3_ == null ? "" : "_" + p_177178_3_) + ".png";
      return ARMOR_TEXTURE_RES_MAP.computeIfAbsent(s, ResourceLocation::new);
   }

   protected abstract void initArmor();

   protected abstract void setModelSlotVisible(T p_188359_1_, EntityEquipmentSlot p_188359_2_);
}
