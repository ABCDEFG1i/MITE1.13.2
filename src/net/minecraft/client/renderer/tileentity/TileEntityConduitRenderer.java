package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntityConduit;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityConduitRenderer extends TileEntityRenderer<TileEntityConduit> {
   private static final ResourceLocation field_205118_a = new ResourceLocation("textures/entity/conduit/base.png");
   private static final ResourceLocation field_205119_d = new ResourceLocation("textures/entity/conduit/cage.png");
   private static final ResourceLocation field_205120_e = new ResourceLocation("textures/entity/conduit/wind.png");
   private static final ResourceLocation field_205121_f = new ResourceLocation("textures/entity/conduit/wind_vertical.png");
   private static final ResourceLocation field_207746_g = new ResourceLocation("textures/entity/conduit/open_eye.png");
   private static final ResourceLocation field_207747_h = new ResourceLocation("textures/entity/conduit/closed_eye.png");
   private final ModelBase field_205122_g = new TileEntityConduitRenderer.ShellModel();
   private final ModelBase field_205123_h = new TileEntityConduitRenderer.CageModel();
   private final TileEntityConduitRenderer.WindModel field_205124_i = new TileEntityConduitRenderer.WindModel();
   private final TileEntityConduitRenderer.EyeModel field_207748_l = new TileEntityConduitRenderer.EyeModel();

   public void render(TileEntityConduit p_199341_1_, double p_199341_2_, double p_199341_4_, double p_199341_6_, float p_199341_8_, int p_199341_9_) {
      float f = (float)p_199341_1_.ticksExisted + p_199341_8_;
      if (!p_199341_1_.isActive()) {
         float f1 = p_199341_1_.func_205036_a(0.0F);
         this.bindTexture(field_205118_a);
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)p_199341_2_ + 0.5F, (float)p_199341_4_ + 0.5F, (float)p_199341_6_ + 0.5F);
         GlStateManager.rotatef(f1, 0.0F, 1.0F, 0.0F);
         this.field_205122_g.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         GlStateManager.popMatrix();
      } else if (p_199341_1_.isActive()) {
         float f3 = p_199341_1_.func_205036_a(p_199341_8_) * (180F / (float)Math.PI);
         float f2 = MathHelper.sin(f * 0.1F) / 2.0F + 0.5F;
         f2 = f2 * f2 + f2;
         this.bindTexture(field_205119_d);
         GlStateManager.disableCull();
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)p_199341_2_ + 0.5F, (float)p_199341_4_ + 0.3F + f2 * 0.2F, (float)p_199341_6_ + 0.5F);
         GlStateManager.rotatef(f3, 0.5F, 1.0F, 0.5F);
         this.field_205123_h.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         GlStateManager.popMatrix();
         int i = 3;
         int j = p_199341_1_.ticksExisted / 3 % TileEntityConduitRenderer.WindModel.field_205078_a;
         this.field_205124_i.func_205077_a(j);
         int k = p_199341_1_.ticksExisted / (3 * TileEntityConduitRenderer.WindModel.field_205078_a) % 3;
         switch(k) {
         case 0:
            this.bindTexture(field_205120_e);
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)p_199341_2_ + 0.5F, (float)p_199341_4_ + 0.5F, (float)p_199341_6_ + 0.5F);
            this.field_205124_i.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)p_199341_2_ + 0.5F, (float)p_199341_4_ + 0.5F, (float)p_199341_6_ + 0.5F);
            GlStateManager.scalef(0.875F, 0.875F, 0.875F);
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            this.field_205124_i.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            break;
         case 1:
            this.bindTexture(field_205121_f);
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)p_199341_2_ + 0.5F, (float)p_199341_4_ + 0.5F, (float)p_199341_6_ + 0.5F);
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            this.field_205124_i.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)p_199341_2_ + 0.5F, (float)p_199341_4_ + 0.5F, (float)p_199341_6_ + 0.5F);
            GlStateManager.scalef(0.875F, 0.875F, 0.875F);
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            this.field_205124_i.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            break;
         case 2:
            this.bindTexture(field_205120_e);
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)p_199341_2_ + 0.5F, (float)p_199341_4_ + 0.5F, (float)p_199341_6_ + 0.5F);
            GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
            this.field_205124_i.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)p_199341_2_ + 0.5F, (float)p_199341_4_ + 0.5F, (float)p_199341_6_ + 0.5F);
            GlStateManager.scalef(0.875F, 0.875F, 0.875F);
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            this.field_205124_i.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
         }

         Entity entity = Minecraft.getInstance().getRenderViewEntity();
         Vec2f vec2f = Vec2f.ZERO;
         if (entity != null) {
            vec2f = entity.getPitchYaw();
         }

         if (p_199341_1_.isEyeOpen()) {
            this.bindTexture(field_207746_g);
         } else {
            this.bindTexture(field_207747_h);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)p_199341_2_ + 0.5F, (float)p_199341_4_ + 0.3F + f2 * 0.2F, (float)p_199341_6_ + 0.5F);
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.rotatef(-vec2f.y, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(vec2f.x, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         this.field_207748_l.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.083333336F);
         GlStateManager.popMatrix();
      }

      super.render(p_199341_1_, p_199341_2_, p_199341_4_, p_199341_6_, p_199341_8_, p_199341_9_);
   }

   @OnlyIn(Dist.CLIENT)
   static class CageModel extends ModelBase {
      private final ModelRenderer field_205075_a;

      public CageModel() {
         this.textureWidth = 32;
         this.textureHeight = 16;
         this.field_205075_a = new ModelRenderer(this, 0, 0);
         this.field_205075_a.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      }

      public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
         this.field_205075_a.render(p_78088_7_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class EyeModel extends ModelBase {
      private final ModelRenderer field_207745_a;

      public EyeModel() {
         this.textureWidth = 8;
         this.textureHeight = 8;
         this.field_207745_a = new ModelRenderer(this, 0, 0);
         this.field_207745_a.addBox(-4.0F, -4.0F, 0.0F, 8, 8, 0, 0.01F);
      }

      public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
         this.field_207745_a.render(p_78088_7_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ShellModel extends ModelBase {
      private final ModelRenderer field_205076_a;

      public ShellModel() {
         this.textureWidth = 32;
         this.textureHeight = 16;
         this.field_205076_a = new ModelRenderer(this, 0, 0);
         this.field_205076_a.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6);
      }

      public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
         this.field_205076_a.render(p_78088_7_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class WindModel extends ModelBase {
      public static int field_205078_a = 22;
      private final ModelRenderer[] field_205079_b = new ModelRenderer[field_205078_a];
      private int field_205080_c;

      public WindModel() {
         this.textureWidth = 64;
         this.textureHeight = 1024;

         for(int i = 0; i < field_205078_a; ++i) {
            this.field_205079_b[i] = new ModelRenderer(this, 0, 32 * i);
            this.field_205079_b[i].addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
         }

      }

      public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
         this.field_205079_b[this.field_205080_c].render(p_78088_7_);
      }

      public void func_205077_a(int p_205077_1_) {
         this.field_205080_c = p_205077_1_;
      }
   }
}
