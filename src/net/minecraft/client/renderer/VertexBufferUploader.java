package net.minecraft.client.renderer;

import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VertexBufferUploader extends WorldVertexBufferUploader {
   private VertexBuffer vertexBuffer;

   public void draw(BufferBuilder p_181679_1_) {
      p_181679_1_.reset();
      this.vertexBuffer.bufferData(p_181679_1_.getByteBuffer());
   }

   public void setVertexBuffer(VertexBuffer p_178178_1_) {
      this.vertexBuffer = p_178178_1_;
   }
}
