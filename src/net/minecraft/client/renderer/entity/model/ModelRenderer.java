package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelRenderer {
   public float textureWidth = 64.0F;
   public float textureHeight = 32.0F;
   private int textureOffsetX;
   private int textureOffsetY;
   public float rotationPointX;
   public float rotationPointY;
   public float rotationPointZ;
   public float rotateAngleX;
   public float rotateAngleY;
   public float rotateAngleZ;
   private boolean compiled;
   private int displayList;
   public boolean mirror;
   public boolean showModel = true;
   public boolean isHidden;
   public List<ModelBox> cubeList = Lists.newArrayList();
   public List<ModelRenderer> childModels;
   public final String boxName;
   private final ModelBase baseModel;
   public float offsetX;
   public float offsetY;
   public float offsetZ;

   public ModelRenderer(ModelBase p_i1172_1_, String p_i1172_2_) {
      this.baseModel = p_i1172_1_;
      p_i1172_1_.boxList.add(this);
      this.boxName = p_i1172_2_;
      this.setTextureSize(p_i1172_1_.textureWidth, p_i1172_1_.textureHeight);
   }

   public ModelRenderer(ModelBase p_i1173_1_) {
      this(p_i1173_1_, null);
   }

   public ModelRenderer(ModelBase p_i46358_1_, int p_i46358_2_, int p_i46358_3_) {
      this(p_i46358_1_);
      this.setTextureOffset(p_i46358_2_, p_i46358_3_);
   }

   public void addChild(ModelRenderer p_78792_1_) {
      if (this.childModels == null) {
         this.childModels = Lists.newArrayList();
      }

      this.childModels.add(p_78792_1_);
   }

   public ModelRenderer setTextureOffset(int p_78784_1_, int p_78784_2_) {
      this.textureOffsetX = p_78784_1_;
      this.textureOffsetY = p_78784_2_;
      return this;
   }

   public ModelRenderer addBox(String p_78786_1_, float p_78786_2_, float p_78786_3_, float p_78786_4_, int p_78786_5_, int p_78786_6_, int p_78786_7_) {
      p_78786_1_ = this.boxName + "." + p_78786_1_;
      TextureOffset textureoffset = this.baseModel.getTextureOffset(p_78786_1_);
      this.setTextureOffset(textureoffset.textureOffsetX, textureoffset.textureOffsetY);
      this.cubeList.add((new ModelBox(this, this.textureOffsetX, this.textureOffsetY, p_78786_2_, p_78786_3_, p_78786_4_, p_78786_5_, p_78786_6_, p_78786_7_, 0.0F)).setBoxName(p_78786_1_));
      return this;
   }

   public ModelRenderer addBox(float p_78789_1_, float p_78789_2_, float p_78789_3_, int p_78789_4_, int p_78789_5_, int p_78789_6_) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, p_78789_1_, p_78789_2_, p_78789_3_, p_78789_4_, p_78789_5_, p_78789_6_, 0.0F));
      return this;
   }

   public ModelRenderer addBox(float p_178769_1_, float p_178769_2_, float p_178769_3_, int p_178769_4_, int p_178769_5_, int p_178769_6_, boolean p_178769_7_) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, p_178769_1_, p_178769_2_, p_178769_3_, p_178769_4_, p_178769_5_, p_178769_6_, 0.0F, p_178769_7_));
      return this;
   }

   public void addBox(float p_78790_1_, float p_78790_2_, float p_78790_3_, int p_78790_4_, int p_78790_5_, int p_78790_6_, float p_78790_7_) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, p_78790_1_, p_78790_2_, p_78790_3_, p_78790_4_, p_78790_5_, p_78790_6_, p_78790_7_));
   }

   public void func_205345_a(float p_205345_1_, float p_205345_2_, float p_205345_3_, int p_205345_4_, int p_205345_5_, int p_205345_6_, float p_205345_7_, boolean p_205345_8_) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, p_205345_1_, p_205345_2_, p_205345_3_, p_205345_4_, p_205345_5_, p_205345_6_, p_205345_7_, p_205345_8_));
   }

   public void setRotationPoint(float p_78793_1_, float p_78793_2_, float p_78793_3_) {
      this.rotationPointX = p_78793_1_;
      this.rotationPointY = p_78793_2_;
      this.rotationPointZ = p_78793_3_;
   }

   public void render(float p_78785_1_) {
      if (!this.isHidden) {
         if (this.showModel) {
            if (!this.compiled) {
               this.compileDisplayList(p_78785_1_);
            }

            GlStateManager.translatef(this.offsetX, this.offsetY, this.offsetZ);
            if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
               if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F) {
                  GlStateManager.callList(this.displayList);
                  if (this.childModels != null) {
                     for(int k = 0; k < this.childModels.size(); ++k) {
                        this.childModels.get(k).render(p_78785_1_);
                     }
                  }
               } else {
                  GlStateManager.translatef(this.rotationPointX * p_78785_1_, this.rotationPointY * p_78785_1_, this.rotationPointZ * p_78785_1_);
                  GlStateManager.callList(this.displayList);
                  if (this.childModels != null) {
                     for(int j = 0; j < this.childModels.size(); ++j) {
                        this.childModels.get(j).render(p_78785_1_);
                     }
                  }

                  GlStateManager.translatef(-this.rotationPointX * p_78785_1_, -this.rotationPointY * p_78785_1_, -this.rotationPointZ * p_78785_1_);
               }
            } else {
               GlStateManager.pushMatrix();
               GlStateManager.translatef(this.rotationPointX * p_78785_1_, this.rotationPointY * p_78785_1_, this.rotationPointZ * p_78785_1_);
               if (this.rotateAngleZ != 0.0F) {
                  GlStateManager.rotatef(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
               }

               if (this.rotateAngleY != 0.0F) {
                  GlStateManager.rotatef(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
               }

               if (this.rotateAngleX != 0.0F) {
                  GlStateManager.rotatef(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
               }

               GlStateManager.callList(this.displayList);
               if (this.childModels != null) {
                  for(int i = 0; i < this.childModels.size(); ++i) {
                     this.childModels.get(i).render(p_78785_1_);
                  }
               }

               GlStateManager.popMatrix();
            }

            GlStateManager.translatef(-this.offsetX, -this.offsetY, -this.offsetZ);
         }
      }
   }

   public void renderWithRotation(float p_78791_1_) {
      if (!this.isHidden) {
         if (this.showModel) {
            if (!this.compiled) {
               this.compileDisplayList(p_78791_1_);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.rotationPointX * p_78791_1_, this.rotationPointY * p_78791_1_, this.rotationPointZ * p_78791_1_);
            if (this.rotateAngleY != 0.0F) {
               GlStateManager.rotatef(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (this.rotateAngleX != 0.0F) {
               GlStateManager.rotatef(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            }

            if (this.rotateAngleZ != 0.0F) {
               GlStateManager.rotatef(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
            }

            GlStateManager.callList(this.displayList);
            GlStateManager.popMatrix();
         }
      }
   }

   public void postRender(float p_78794_1_) {
      if (!this.isHidden) {
         if (this.showModel) {
            if (!this.compiled) {
               this.compileDisplayList(p_78794_1_);
            }

            if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
               if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
                  GlStateManager.translatef(this.rotationPointX * p_78794_1_, this.rotationPointY * p_78794_1_, this.rotationPointZ * p_78794_1_);
               }
            } else {
               GlStateManager.translatef(this.rotationPointX * p_78794_1_, this.rotationPointY * p_78794_1_, this.rotationPointZ * p_78794_1_);
               if (this.rotateAngleZ != 0.0F) {
                  GlStateManager.rotatef(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
               }

               if (this.rotateAngleY != 0.0F) {
                  GlStateManager.rotatef(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
               }

               if (this.rotateAngleX != 0.0F) {
                  GlStateManager.rotatef(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
               }
            }

         }
      }
   }

   private void compileDisplayList(float p_78788_1_) {
      this.displayList = GLAllocation.generateDisplayLists(1);
      GlStateManager.newList(this.displayList, 4864);
      BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();

      for(int i = 0; i < this.cubeList.size(); ++i) {
         this.cubeList.get(i).render(bufferbuilder, p_78788_1_);
      }

      GlStateManager.endList();
      this.compiled = true;
   }

   public ModelRenderer setTextureSize(int p_78787_1_, int p_78787_2_) {
      this.textureWidth = (float)p_78787_1_;
      this.textureHeight = (float)p_78787_2_;
      return this;
   }
}
