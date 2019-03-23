package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TexturedQuad {
   public PositionTextureVertex[] vertexPositions;
   public int nVertices;
   private boolean invertNormal;

   public TexturedQuad(PositionTextureVertex[] p_i46364_1_) {
      this.vertexPositions = p_i46364_1_;
      this.nVertices = p_i46364_1_.length;
   }

   public TexturedQuad(PositionTextureVertex[] p_i1153_1_, int p_i1153_2_, int p_i1153_3_, int p_i1153_4_, int p_i1153_5_, float p_i1153_6_, float p_i1153_7_) {
      this(p_i1153_1_);
      float f = 0.0F / p_i1153_6_;
      float f1 = 0.0F / p_i1153_7_;
      p_i1153_1_[0] = p_i1153_1_[0].setTexturePosition((float)p_i1153_4_ / p_i1153_6_ - f, (float)p_i1153_3_ / p_i1153_7_ + f1);
      p_i1153_1_[1] = p_i1153_1_[1].setTexturePosition((float)p_i1153_2_ / p_i1153_6_ + f, (float)p_i1153_3_ / p_i1153_7_ + f1);
      p_i1153_1_[2] = p_i1153_1_[2].setTexturePosition((float)p_i1153_2_ / p_i1153_6_ + f, (float)p_i1153_5_ / p_i1153_7_ - f1);
      p_i1153_1_[3] = p_i1153_1_[3].setTexturePosition((float)p_i1153_4_ / p_i1153_6_ - f, (float)p_i1153_5_ / p_i1153_7_ - f1);
   }

   public void flipFace() {
      PositionTextureVertex[] apositiontexturevertex = new PositionTextureVertex[this.vertexPositions.length];

      for(int i = 0; i < this.vertexPositions.length; ++i) {
         apositiontexturevertex[i] = this.vertexPositions[this.vertexPositions.length - i - 1];
      }

      this.vertexPositions = apositiontexturevertex;
   }

   public void draw(BufferBuilder p_178765_1_, float p_178765_2_) {
      Vec3d vec3d = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[0].vector3D);
      Vec3d vec3d1 = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[2].vector3D);
      Vec3d vec3d2 = vec3d1.crossProduct(vec3d).normalize();
      float f = (float)vec3d2.x;
      float f1 = (float)vec3d2.y;
      float f2 = (float)vec3d2.z;
      if (this.invertNormal) {
         f = -f;
         f1 = -f1;
         f2 = -f2;
      }

      p_178765_1_.begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);

      for(int i = 0; i < 4; ++i) {
         PositionTextureVertex positiontexturevertex = this.vertexPositions[i];
         p_178765_1_.pos(positiontexturevertex.vector3D.x * (double)p_178765_2_, positiontexturevertex.vector3D.y * (double)p_178765_2_, positiontexturevertex.vector3D.z * (double)p_178765_2_).tex((double)positiontexturevertex.texturePositionX, (double)positiontexturevertex.texturePositionY).normal(f, f1, f2).endVertex();
      }

      Tessellator.getInstance().draw();
   }
}
