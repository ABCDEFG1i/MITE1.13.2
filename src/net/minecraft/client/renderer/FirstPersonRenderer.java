package net.minecraft.client.renderer;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FirstPersonRenderer {
   private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
   private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");
   private final Minecraft mc;
   private ItemStack itemStackMainHand = ItemStack.EMPTY;
   private ItemStack itemStackOffHand = ItemStack.EMPTY;
   private float equippedProgressMainHand;
   private float prevEquippedProgressMainHand;
   private float equippedProgressOffHand;
   private float prevEquippedProgressOffHand;
   private final RenderManager renderManager;
   private final ItemRenderer itemRenderer;

   public FirstPersonRenderer(Minecraft p_i1247_1_) {
      this.mc = p_i1247_1_;
      this.renderManager = p_i1247_1_.getRenderManager();
      this.itemRenderer = p_i1247_1_.getItemRenderer();
   }

   public void func_178099_a(EntityLivingBase p_178099_1_, ItemStack p_178099_2_, ItemCameraTransforms.TransformType p_178099_3_) {
      this.func_187462_a(p_178099_1_, p_178099_2_, p_178099_3_, false);
   }

   public void func_187462_a(EntityLivingBase p_187462_1_, ItemStack p_187462_2_, ItemCameraTransforms.TransformType p_187462_3_, boolean p_187462_4_) {
      if (!p_187462_2_.isEmpty()) {
         Item item = p_187462_2_.getItem();
         Block block = Block.getBlockFromItem(item);
         GlStateManager.pushMatrix();
         boolean flag = this.itemRenderer.shouldRenderItemIn3D(p_187462_2_) && block.getRenderLayer() == BlockRenderLayer.TRANSLUCENT;
         if (flag) {
            GlStateManager.depthMask(false);
         }

         this.itemRenderer.func_184392_a(p_187462_2_, p_187462_1_, p_187462_3_, p_187462_4_);
         if (flag) {
            GlStateManager.depthMask(true);
         }

         GlStateManager.popMatrix();
      }
   }

   private void rotateArroundXAndY(float p_178101_1_, float p_178101_2_) {
      GlStateManager.pushMatrix();
      GlStateManager.rotatef(p_178101_1_, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(p_178101_2_, 0.0F, 1.0F, 0.0F);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.popMatrix();
   }

   private void setLightmap() {
      AbstractClientPlayer abstractclientplayer = this.mc.player;
      int i = this.mc.world.getCombinedLight(new BlockPos(abstractclientplayer.posX, abstractclientplayer.posY + (double)abstractclientplayer.getEyeHeight(), abstractclientplayer.posZ), 0);
      float f = (float)(i & '\uffff');
      float f1 = (float)(i >> 16);
      OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, f, f1);
   }

   private void rotateArm(float p_187458_1_) {
      EntityPlayerSP entityplayersp = this.mc.player;
      float f = entityplayersp.prevRenderArmPitch + (entityplayersp.renderArmPitch - entityplayersp.prevRenderArmPitch) * p_187458_1_;
      float f1 = entityplayersp.prevRenderArmYaw + (entityplayersp.renderArmYaw - entityplayersp.prevRenderArmYaw) * p_187458_1_;
      GlStateManager.rotatef((entityplayersp.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef((entityplayersp.rotationYaw - f1) * 0.1F, 0.0F, 1.0F, 0.0F);
   }

   private float getMapAngleFromPitch(float p_178100_1_) {
      float f = 1.0F - p_178100_1_ / 45.0F + 0.1F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = -MathHelper.cos(f * (float)Math.PI) * 0.5F + 0.5F;
      return f;
   }

   private void renderArms() {
      if (!this.mc.player.isInvisible()) {
         GlStateManager.disableCull();
         GlStateManager.pushMatrix();
         GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
         this.renderArm(EnumHandSide.RIGHT);
         this.renderArm(EnumHandSide.LEFT);
         GlStateManager.popMatrix();
         GlStateManager.enableCull();
      }
   }

   private void renderArm(EnumHandSide p_187455_1_) {
      this.mc.getTextureManager().bindTexture(this.mc.player.getLocationSkin());
      Render<AbstractClientPlayer> render = this.renderManager.getEntityRenderObject(this.mc.player);
      RenderPlayer renderplayer = (RenderPlayer)render;
      GlStateManager.pushMatrix();
      float f = p_187455_1_ == EnumHandSide.RIGHT ? 1.0F : -1.0F;
      GlStateManager.rotatef(92.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(f * -41.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translatef(f * 0.3F, -1.1F, 0.45F);
      if (p_187455_1_ == EnumHandSide.RIGHT) {
         renderplayer.renderRightArm(this.mc.player);
      } else {
         renderplayer.renderLeftArm(this.mc.player);
      }

      GlStateManager.popMatrix();
   }

   private void renderMapFirstPersonSide(float p_187465_1_, EnumHandSide p_187465_2_, float p_187465_3_, ItemStack p_187465_4_) {
      float f = p_187465_2_ == EnumHandSide.RIGHT ? 1.0F : -1.0F;
      GlStateManager.translatef(f * 0.125F, -0.125F, 0.0F);
      if (!this.mc.player.isInvisible()) {
         GlStateManager.pushMatrix();
         GlStateManager.rotatef(f * 10.0F, 0.0F, 0.0F, 1.0F);
         this.renderArmFirstPerson(p_187465_1_, p_187465_3_, p_187465_2_);
         GlStateManager.popMatrix();
      }

      GlStateManager.pushMatrix();
      GlStateManager.translatef(f * 0.51F, -0.08F + p_187465_1_ * -1.2F, -0.75F);
      float f1 = MathHelper.sqrt(p_187465_3_);
      float f2 = MathHelper.sin(f1 * (float)Math.PI);
      float f3 = -0.5F * f2;
      float f4 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
      float f5 = -0.3F * MathHelper.sin(p_187465_3_ * (float)Math.PI);
      GlStateManager.translatef(f * f3, f4 - 0.3F * f2, f5);
      GlStateManager.rotatef(f2 * -45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(f * f2 * -30.0F, 0.0F, 1.0F, 0.0F);
      this.renderMapFirstPerson(p_187465_4_);
      GlStateManager.popMatrix();
   }

   private void renderMapFirstPerson(float p_187463_1_, float p_187463_2_, float p_187463_3_) {
      float f = MathHelper.sqrt(p_187463_3_);
      float f1 = -0.2F * MathHelper.sin(p_187463_3_ * (float)Math.PI);
      float f2 = -0.4F * MathHelper.sin(f * (float)Math.PI);
      GlStateManager.translatef(0.0F, -f1 / 2.0F, f2);
      float f3 = this.getMapAngleFromPitch(p_187463_1_);
      GlStateManager.translatef(0.0F, 0.04F + p_187463_2_ * -1.2F + f3 * -0.5F, -0.72F);
      GlStateManager.rotatef(f3 * -85.0F, 1.0F, 0.0F, 0.0F);
      this.renderArms();
      float f4 = MathHelper.sin(f * (float)Math.PI);
      GlStateManager.rotatef(f4 * 20.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scalef(2.0F, 2.0F, 2.0F);
      this.renderMapFirstPerson(this.itemStackMainHand);
   }

   private void renderMapFirstPerson(ItemStack p_187461_1_) {
      GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.scalef(0.38F, 0.38F, 0.38F);
      GlStateManager.disableLighting();
      this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      GlStateManager.translatef(-0.5F, -0.5F, 0.0F);
      GlStateManager.scalef(0.0078125F, 0.0078125F, 0.0078125F);
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos(-7.0D, 135.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
      bufferbuilder.pos(135.0D, 135.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
      bufferbuilder.pos(135.0D, -7.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
      bufferbuilder.pos(-7.0D, -7.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
      tessellator.draw();
      MapData mapdata = ItemMap.getMapData(p_187461_1_, this.mc.world);
      if (mapdata != null) {
         this.mc.entityRenderer.func_147701_i().renderMap(mapdata, false);
      }

      GlStateManager.enableLighting();
   }

   private void renderArmFirstPerson(float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_) {
      boolean flag = p_187456_3_ != EnumHandSide.LEFT;
      float f = flag ? 1.0F : -1.0F;
      float f1 = MathHelper.sqrt(p_187456_2_);
      float f2 = -0.3F * MathHelper.sin(f1 * (float)Math.PI);
      float f3 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
      float f4 = -0.4F * MathHelper.sin(p_187456_2_ * (float)Math.PI);
      GlStateManager.translatef(f * (f2 + 0.64000005F), f3 + -0.6F + p_187456_1_ * -0.6F, f4 + -0.71999997F);
      GlStateManager.rotatef(f * 45.0F, 0.0F, 1.0F, 0.0F);
      float f5 = MathHelper.sin(p_187456_2_ * p_187456_2_ * (float)Math.PI);
      float f6 = MathHelper.sin(f1 * (float)Math.PI);
      GlStateManager.rotatef(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
      AbstractClientPlayer abstractclientplayer = this.mc.player;
      this.mc.getTextureManager().bindTexture(abstractclientplayer.getLocationSkin());
      GlStateManager.translatef(f * -1.0F, 3.6F, 3.5F);
      GlStateManager.rotatef(f * 120.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotatef(200.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(f * -135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(f * 5.6F, 0.0F, 0.0F);
      RenderPlayer renderplayer = (RenderPlayer)this.renderManager.<AbstractClientPlayer>getEntityRenderObject(abstractclientplayer);
      GlStateManager.disableCull();
      if (flag) {
         renderplayer.renderRightArm(abstractclientplayer);
      } else {
         renderplayer.renderLeftArm(abstractclientplayer);
      }

      GlStateManager.enableCull();
   }

   private void transformEatFirstPerson(float p_187454_1_, EnumHandSide p_187454_2_, ItemStack p_187454_3_) {
      float f = (float)this.mc.player.getItemInUseCount() - p_187454_1_ + 1.0F;
      float f1 = f / (float)p_187454_3_.getUseDuration();
      if (f1 < 0.8F) {
         float f2 = MathHelper.abs(MathHelper.cos(f / 4.0F * (float)Math.PI) * 0.1F);
         GlStateManager.translatef(0.0F, f2, 0.0F);
      }

      float f3 = 1.0F - (float)Math.pow((double)f1, 27.0D);
      int i = p_187454_2_ == EnumHandSide.RIGHT ? 1 : -1;
      GlStateManager.translatef(f3 * 0.6F * (float)i, f3 * -0.5F, f3 * 0.0F);
      GlStateManager.rotatef((float)i * f3 * 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(f3 * 10.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef((float)i * f3 * 30.0F, 0.0F, 0.0F, 1.0F);
   }

   private void transformFirstPerson(EnumHandSide p_187453_1_, float p_187453_2_) {
      int i = p_187453_1_ == EnumHandSide.RIGHT ? 1 : -1;
      float f = MathHelper.sin(p_187453_2_ * p_187453_2_ * (float)Math.PI);
      GlStateManager.rotatef((float)i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
      float f1 = MathHelper.sin(MathHelper.sqrt(p_187453_2_) * (float)Math.PI);
      GlStateManager.rotatef((float)i * f1 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotatef(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef((float)i * -45.0F, 0.0F, 1.0F, 0.0F);
   }

   private void transformSideFirstPerson(EnumHandSide p_187459_1_, float p_187459_2_) {
      int i = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
      GlStateManager.translatef((float)i * 0.56F, -0.52F + p_187459_2_ * -0.6F, -0.72F);
   }

   public void renderItemInFirstPerson(float p_78440_1_) {
      AbstractClientPlayer abstractclientplayer = this.mc.player;
      float f = abstractclientplayer.getSwingProgress(p_78440_1_);
      EnumHand enumhand = MoreObjects.firstNonNull(abstractclientplayer.swingingHand, EnumHand.MAIN_HAND);
      float f1 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * p_78440_1_;
      float f2 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * p_78440_1_;
      boolean flag = true;
      boolean flag1 = true;
      if (abstractclientplayer.isHandActive()) {
         ItemStack itemstack = abstractclientplayer.getActiveItemStack();
         if (itemstack.getItem() == Items.BOW) {
            flag = abstractclientplayer.getActiveHand() == EnumHand.MAIN_HAND;
            flag1 = !flag;
         }
      }

      this.rotateArroundXAndY(f1, f2);
      this.setLightmap();
      this.rotateArm(p_78440_1_);
      GlStateManager.enableRescaleNormal();
      if (flag) {
         float f4 = enumhand == EnumHand.MAIN_HAND ? f : 0.0F;
         float f3 = 1.0F - (this.prevEquippedProgressMainHand + (this.equippedProgressMainHand - this.prevEquippedProgressMainHand) * p_78440_1_);
         this.renderItemInFirstPerson(abstractclientplayer, p_78440_1_, f1, EnumHand.MAIN_HAND, f4, this.itemStackMainHand, f3);
      }

      if (flag1) {
         float f5 = enumhand == EnumHand.OFF_HAND ? f : 0.0F;
         float f6 = 1.0F - (this.prevEquippedProgressOffHand + (this.equippedProgressOffHand - this.prevEquippedProgressOffHand) * p_78440_1_);
         this.renderItemInFirstPerson(abstractclientplayer, p_78440_1_, f1, EnumHand.OFF_HAND, f5, this.itemStackOffHand, f6);
      }

      GlStateManager.disableRescaleNormal();
      RenderHelper.disableStandardItemLighting();
   }

   public void renderItemInFirstPerson(AbstractClientPlayer p_187457_1_, float p_187457_2_, float p_187457_3_, EnumHand p_187457_4_, float p_187457_5_, ItemStack p_187457_6_, float p_187457_7_) {
      boolean flag = p_187457_4_ == EnumHand.MAIN_HAND;
      EnumHandSide enumhandside = flag ? p_187457_1_.getPrimaryHand() : p_187457_1_.getPrimaryHand().opposite();
      GlStateManager.pushMatrix();
      if (p_187457_6_.isEmpty()) {
         if (flag && !p_187457_1_.isInvisible()) {
            this.renderArmFirstPerson(p_187457_7_, p_187457_5_, enumhandside);
         }
      } else if (p_187457_6_.getItem() == Items.FILLED_MAP) {
         if (flag && this.itemStackOffHand.isEmpty()) {
            this.renderMapFirstPerson(p_187457_3_, p_187457_7_, p_187457_5_);
         } else {
            this.renderMapFirstPersonSide(p_187457_7_, enumhandside, p_187457_5_, p_187457_6_);
         }
      } else {
         boolean flag1 = enumhandside == EnumHandSide.RIGHT;
         if (p_187457_1_.isHandActive() && p_187457_1_.getItemInUseCount() > 0 && p_187457_1_.getActiveHand() == p_187457_4_) {
            int k = flag1 ? 1 : -1;
            switch(p_187457_6_.getUseAction()) {
            case NONE:
               this.transformSideFirstPerson(enumhandside, p_187457_7_);
               break;
            case EAT:
            case DRINK:
               this.transformEatFirstPerson(p_187457_2_, enumhandside, p_187457_6_);
               this.transformSideFirstPerson(enumhandside, p_187457_7_);
               break;
            case BLOCK:
               this.transformSideFirstPerson(enumhandside, p_187457_7_);
               break;
            case BOW:
               this.transformSideFirstPerson(enumhandside, p_187457_7_);
               GlStateManager.translatef((float)k * -0.2785682F, 0.18344387F, 0.15731531F);
               GlStateManager.rotatef(-13.935F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotatef((float)k * 35.3F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotatef((float)k * -9.785F, 0.0F, 0.0F, 1.0F);
               float f6 = (float)p_187457_6_.getUseDuration() - ((float)this.mc.player.getItemInUseCount() - p_187457_2_ + 1.0F);
               float f8 = f6 / 20.0F;
               f8 = (f8 * f8 + f8 * 2.0F) / 3.0F;
               if (f8 > 1.0F) {
                  f8 = 1.0F;
               }

               if (f8 > 0.1F) {
                  float f10 = MathHelper.sin((f6 - 0.1F) * 1.3F);
                  float f11 = f8 - 0.1F;
                  float f12 = f10 * f11;
                  GlStateManager.translatef(f12 * 0.0F, f12 * 0.004F, f12 * 0.0F);
               }

               GlStateManager.translatef(f8 * 0.0F, f8 * 0.0F, f8 * 0.04F);
               GlStateManager.scalef(1.0F, 1.0F, 1.0F + f8 * 0.2F);
               GlStateManager.rotatef((float)k * 45.0F, 0.0F, -1.0F, 0.0F);
               break;
            case SPEAR:
               this.transformSideFirstPerson(enumhandside, p_187457_7_);
               GlStateManager.translatef((float)k * -0.5F, 0.7F, 0.1F);
               GlStateManager.rotatef(-55.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotatef((float)k * 35.3F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotatef((float)k * -9.785F, 0.0F, 0.0F, 1.0F);
               float f5 = (float)p_187457_6_.getUseDuration() - ((float)this.mc.player.getItemInUseCount() - p_187457_2_ + 1.0F);
               float f7 = f5 / 10.0F;
               if (f7 > 1.0F) {
                  f7 = 1.0F;
               }

               if (f7 > 0.1F) {
                  float f9 = MathHelper.sin((f5 - 0.1F) * 1.3F);
                  float f2 = f7 - 0.1F;
                  float f3 = f9 * f2;
                  GlStateManager.translatef(f3 * 0.0F, f3 * 0.004F, f3 * 0.0F);
               }

               GlStateManager.translatef(0.0F, 0.0F, f7 * 0.2F);
               GlStateManager.scalef(1.0F, 1.0F, 1.0F + f7 * 0.2F);
               GlStateManager.rotatef((float)k * 45.0F, 0.0F, -1.0F, 0.0F);
            }
         } else if (p_187457_1_.isSpinAttacking()) {
            this.transformSideFirstPerson(enumhandside, p_187457_7_);
            int i = flag1 ? 1 : -1;
            GlStateManager.translatef((float)i * -0.4F, 0.8F, 0.3F);
            GlStateManager.rotatef((float)i * 65.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef((float)i * -85.0F, 0.0F, 0.0F, 1.0F);
         } else {
            float f4 = -0.4F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * (float)Math.PI);
            float f = 0.2F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * ((float)Math.PI * 2F));
            float f1 = -0.2F * MathHelper.sin(p_187457_5_ * (float)Math.PI);
            int j = flag1 ? 1 : -1;
            GlStateManager.translatef((float)j * f4, f, f1);
            this.transformSideFirstPerson(enumhandside, p_187457_7_);
            this.transformFirstPerson(enumhandside, p_187457_5_);
         }

         this.func_187462_a(p_187457_1_, p_187457_6_, flag1 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag1);
      }

      GlStateManager.popMatrix();
   }

   public void renderOverlays(float p_78447_1_) {
      GlStateManager.disableAlphaTest();
      if (this.mc.player.isEntityInsideOpaqueBlock()) {
         IBlockState iblockstate = this.mc.world.getBlockState(new BlockPos(this.mc.player));
         EntityPlayer entityplayer = this.mc.player;

         for(int i = 0; i < 8; ++i) {
            double d0 = entityplayer.posX + (double)(((float)((i >> 0) % 2) - 0.5F) * entityplayer.width * 0.8F);
            double d1 = entityplayer.posY + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
            double d2 = entityplayer.posZ + (double)(((float)((i >> 2) % 2) - 0.5F) * entityplayer.width * 0.8F);
            BlockPos blockpos = new BlockPos(d0, d1 + (double)entityplayer.getEyeHeight(), d2);
            IBlockState iblockstate1 = this.mc.world.getBlockState(blockpos);
            if (iblockstate1.causesSuffocation()) {
               iblockstate = iblockstate1;
            }
         }

         if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
            this.renderSuffocationOverlay(this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(iblockstate));
         }
      }

      if (!this.mc.player.isSpectator()) {
         if (this.mc.player.areEyesInFluid(FluidTags.WATER)) {
            this.renderWaterOverlayTexture(p_78447_1_);
         }

         if (this.mc.player.isBurning()) {
            this.renderFireInFirstPerson();
         }
      }

      GlStateManager.enableAlphaTest();
   }

   private void renderSuffocationOverlay(TextureAtlasSprite p_178108_1_) {
      this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      float f = 0.1F;
      GlStateManager.color4f(0.1F, 0.1F, 0.1F, 0.5F);
      GlStateManager.pushMatrix();
      float f1 = -1.0F;
      float f2 = 1.0F;
      float f3 = -1.0F;
      float f4 = 1.0F;
      float f5 = -0.5F;
      float f6 = p_178108_1_.getMinU();
      float f7 = p_178108_1_.getMaxU();
      float f8 = p_178108_1_.getMinV();
      float f9 = p_178108_1_.getMaxV();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos(-1.0D, -1.0D, -0.5D).tex((double)f7, (double)f9).endVertex();
      bufferbuilder.pos(1.0D, -1.0D, -0.5D).tex((double)f6, (double)f9).endVertex();
      bufferbuilder.pos(1.0D, 1.0D, -0.5D).tex((double)f6, (double)f8).endVertex();
      bufferbuilder.pos(-1.0D, 1.0D, -0.5D).tex((double)f7, (double)f8).endVertex();
      tessellator.draw();
      GlStateManager.popMatrix();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderWaterOverlayTexture(float p_78448_1_) {
      this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      float f = this.mc.player.getBrightness();
      GlStateManager.color4f(f, f, f, 0.1F);
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.pushMatrix();
      float f1 = 4.0F;
      float f2 = -1.0F;
      float f3 = 1.0F;
      float f4 = -1.0F;
      float f5 = 1.0F;
      float f6 = -0.5F;
      float f7 = -this.mc.player.rotationYaw / 64.0F;
      float f8 = this.mc.player.rotationPitch / 64.0F;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos(-1.0D, -1.0D, -0.5D).tex((double)(4.0F + f7), (double)(4.0F + f8)).endVertex();
      bufferbuilder.pos(1.0D, -1.0D, -0.5D).tex((double)(0.0F + f7), (double)(4.0F + f8)).endVertex();
      bufferbuilder.pos(1.0D, 1.0D, -0.5D).tex((double)(0.0F + f7), (double)(0.0F + f8)).endVertex();
      bufferbuilder.pos(-1.0D, 1.0D, -0.5D).tex((double)(4.0F + f7), (double)(0.0F + f8)).endVertex();
      tessellator.draw();
      GlStateManager.popMatrix();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
   }

   private void renderFireInFirstPerson() {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.9F);
      GlStateManager.depthFunc(519);
      GlStateManager.depthMask(false);
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      float f = 1.0F;

      for(int i = 0; i < 2; ++i) {
         GlStateManager.pushMatrix();
         TextureAtlasSprite textureatlassprite = this.mc.getTextureMap().getSprite(ModelBakery.field_207764_b);
         this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         float f1 = textureatlassprite.getMinU();
         float f2 = textureatlassprite.getMaxU();
         float f3 = textureatlassprite.getMinV();
         float f4 = textureatlassprite.getMaxV();
         float f5 = -0.5F;
         float f6 = 0.5F;
         float f7 = -0.5F;
         float f8 = 0.5F;
         float f9 = -0.5F;
         GlStateManager.translatef((float)(-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
         GlStateManager.rotatef((float)(i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
         bufferbuilder.pos(-0.5D, -0.5D, -0.5D).tex((double)f2, (double)f4).endVertex();
         bufferbuilder.pos(0.5D, -0.5D, -0.5D).tex((double)f1, (double)f4).endVertex();
         bufferbuilder.pos(0.5D, 0.5D, -0.5D).tex((double)f1, (double)f3).endVertex();
         bufferbuilder.pos(-0.5D, 0.5D, -0.5D).tex((double)f2, (double)f3).endVertex();
         tessellator.draw();
         GlStateManager.popMatrix();
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
      GlStateManager.depthFunc(515);
   }

   public void tick() {
      this.prevEquippedProgressMainHand = this.equippedProgressMainHand;
      this.prevEquippedProgressOffHand = this.equippedProgressOffHand;
      EntityPlayerSP entityplayersp = this.mc.player;
      ItemStack itemstack = entityplayersp.getHeldItemMainhand();
      ItemStack itemstack1 = entityplayersp.getHeldItemOffhand();
      if (entityplayersp.isRowingBoat()) {
         this.equippedProgressMainHand = MathHelper.clamp(this.equippedProgressMainHand - 0.4F, 0.0F, 1.0F);
         this.equippedProgressOffHand = MathHelper.clamp(this.equippedProgressOffHand - 0.4F, 0.0F, 1.0F);
      } else {
         float f = entityplayersp.getCooledAttackStrength(1.0F);
         this.equippedProgressMainHand += MathHelper.clamp((Objects.equals(this.itemStackMainHand, itemstack) ? f * f * f : 0.0F) - this.equippedProgressMainHand, -0.4F, 0.4F);
         this.equippedProgressOffHand += MathHelper.clamp((float)(Objects.equals(this.itemStackOffHand, itemstack1) ? 1 : 0) - this.equippedProgressOffHand, -0.4F, 0.4F);
      }

      if (this.equippedProgressMainHand < 0.1F) {
         this.itemStackMainHand = itemstack;
      }

      if (this.equippedProgressOffHand < 0.1F) {
         this.itemStackOffHand = itemstack1;
      }

   }

   public void resetEquippedProgress(EnumHand p_187460_1_) {
      if (p_187460_1_ == EnumHand.MAIN_HAND) {
         this.equippedProgressMainHand = 0.0F;
      } else {
         this.equippedProgressOffHand = 0.0F;
      }

   }
}
