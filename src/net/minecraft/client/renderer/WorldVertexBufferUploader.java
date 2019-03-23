package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldVertexBufferUploader {
   public void draw(BufferBuilder p_181679_1_) {
      if (p_181679_1_.getVertexCount() > 0) {
         VertexFormat vertexformat = p_181679_1_.getVertexFormat();
         int i = vertexformat.getSize();
         ByteBuffer bytebuffer = p_181679_1_.getByteBuffer();
         List<VertexFormatElement> list = vertexformat.getElements();

         for(int j = 0; j < list.size(); ++j) {
            VertexFormatElement vertexformatelement = list.get(j);
            VertexFormatElement.EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();
            int k = vertexformatelement.getType().getGlConstant();
            int l = vertexformatelement.getIndex();
            bytebuffer.position(vertexformat.getOffset(j));
            switch(vertexformatelement$enumusage) {
            case POSITION:
               GlStateManager.vertexPointer(vertexformatelement.getElementCount(), k, i, bytebuffer);
               GlStateManager.enableClientState(32884);
               break;
            case UV:
               OpenGlHelper.glClientActiveTexture(OpenGlHelper.GL_TEXTURE0 + l);
               GlStateManager.texCoordPointer(vertexformatelement.getElementCount(), k, i, bytebuffer);
               GlStateManager.enableClientState(32888);
               OpenGlHelper.glClientActiveTexture(OpenGlHelper.GL_TEXTURE0);
               break;
            case COLOR:
               GlStateManager.colorPointer(vertexformatelement.getElementCount(), k, i, bytebuffer);
               GlStateManager.enableClientState(32886);
               break;
            case NORMAL:
               GlStateManager.normalPointer(k, i, bytebuffer);
               GlStateManager.enableClientState(32885);
            }
         }

         GlStateManager.drawArrays(p_181679_1_.getDrawMode(), 0, p_181679_1_.getVertexCount());
         int i1 = 0;

         for(int j1 = list.size(); i1 < j1; ++i1) {
            VertexFormatElement vertexformatelement1 = list.get(i1);
            VertexFormatElement.EnumUsage vertexformatelement$enumusage1 = vertexformatelement1.getUsage();
            int k1 = vertexformatelement1.getIndex();
            switch(vertexformatelement$enumusage1) {
            case POSITION:
               GlStateManager.disableClientState(32884);
               break;
            case UV:
               OpenGlHelper.glClientActiveTexture(OpenGlHelper.GL_TEXTURE0 + k1);
               GlStateManager.disableClientState(32888);
               OpenGlHelper.glClientActiveTexture(OpenGlHelper.GL_TEXTURE0);
               break;
            case COLOR:
               GlStateManager.disableClientState(32886);
               GlStateManager.resetColor();
               break;
            case NORMAL:
               GlStateManager.disableClientState(32885);
            }
         }
      }

      p_181679_1_.reset();
   }
}
