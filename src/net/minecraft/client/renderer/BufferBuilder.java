package net.minecraft.client.renderer;

import com.google.common.primitives.Floats;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.BitSet;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class BufferBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private ByteBuffer byteBuffer;
   private IntBuffer rawIntBuffer;
   private ShortBuffer rawShortBuffer;
   private FloatBuffer rawFloatBuffer;
   private int vertexCount;
   private VertexFormatElement vertexFormatElement;
   private int vertexFormatIndex;
   private boolean noColor;
   private int drawMode;
   private double xOffset;
   private double yOffset;
   private double zOffset;
   private VertexFormat vertexFormat;
   private boolean isDrawing;

   public BufferBuilder(int p_i46275_1_) {
      this.byteBuffer = GLAllocation.createDirectByteBuffer(p_i46275_1_ * 4);
      this.rawIntBuffer = this.byteBuffer.asIntBuffer();
      this.rawShortBuffer = this.byteBuffer.asShortBuffer();
      this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
   }

   private void growBuffer(int p_181670_1_) {
      if (this.vertexCount * this.vertexFormat.getSize() + p_181670_1_ > this.byteBuffer.capacity()) {
         int i = this.byteBuffer.capacity();
         int j = i + MathHelper.roundUp(p_181670_1_, 2097152);
         LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", i, j);
         int k = this.rawIntBuffer.position();
         ByteBuffer bytebuffer = GLAllocation.createDirectByteBuffer(j);
         this.byteBuffer.position(0);
         bytebuffer.put(this.byteBuffer);
         bytebuffer.rewind();
         this.byteBuffer = bytebuffer;
         this.rawFloatBuffer = this.byteBuffer.asFloatBuffer().asReadOnlyBuffer();
         this.rawIntBuffer = this.byteBuffer.asIntBuffer();
         this.rawIntBuffer.position(k);
         this.rawShortBuffer = this.byteBuffer.asShortBuffer();
         this.rawShortBuffer.position(k << 1);
      }
   }

   public void sortVertexData(float p_181674_1_, float p_181674_2_, float p_181674_3_) {
      int i = this.vertexCount / 4;
      float[] afloat = new float[i];

      for(int j = 0; j < i; ++j) {
         afloat[j] = getDistanceSq(this.rawFloatBuffer, (float)((double)p_181674_1_ + this.xOffset), (float)((double)p_181674_2_ + this.yOffset), (float)((double)p_181674_3_ + this.zOffset), this.vertexFormat.getIntegerSize(), j * this.vertexFormat.getSize());
      }

      Integer[] ainteger = new Integer[i];

      for(int k = 0; k < ainteger.length; ++k) {
         ainteger[k] = k;
      }

      Arrays.sort(ainteger, (p_210255_1_, p_210255_2_) -> {
         return Floats.compare(afloat[p_210255_2_], afloat[p_210255_1_]);
      });
      BitSet bitset = new BitSet();
      int l = this.vertexFormat.getSize();
      int[] aint = new int[l];

      for(int i1 = bitset.nextClearBit(0); i1 < ainteger.length; i1 = bitset.nextClearBit(i1 + 1)) {
         int j1 = ainteger[i1];
         if (j1 != i1) {
            this.rawIntBuffer.limit(j1 * l + l);
            this.rawIntBuffer.position(j1 * l);
            this.rawIntBuffer.get(aint);
            int k1 = j1;

            for(int l1 = ainteger[j1]; k1 != i1; l1 = ainteger[l1]) {
               this.rawIntBuffer.limit(l1 * l + l);
               this.rawIntBuffer.position(l1 * l);
               IntBuffer intbuffer = this.rawIntBuffer.slice();
               this.rawIntBuffer.limit(k1 * l + l);
               this.rawIntBuffer.position(k1 * l);
               this.rawIntBuffer.put(intbuffer);
               bitset.set(k1);
               k1 = l1;
            }

            this.rawIntBuffer.limit(i1 * l + l);
            this.rawIntBuffer.position(i1 * l);
            this.rawIntBuffer.put(aint);
         }

         bitset.set(i1);
      }

   }

   public BufferBuilder.State getVertexState() {
      this.rawIntBuffer.rewind();
      int i = this.getBufferSize();
      this.rawIntBuffer.limit(i);
      int[] aint = new int[i];
      this.rawIntBuffer.get(aint);
      this.rawIntBuffer.limit(this.rawIntBuffer.capacity());
      this.rawIntBuffer.position(i);
      return new BufferBuilder.State(aint, new VertexFormat(this.vertexFormat));
   }

   private int getBufferSize() {
      return this.vertexCount * this.vertexFormat.getIntegerSize();
   }

   private static float getDistanceSq(FloatBuffer p_181665_0_, float p_181665_1_, float p_181665_2_, float p_181665_3_, int p_181665_4_, int p_181665_5_) {
      float f = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 0);
      float f1 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 1);
      float f2 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 2);
      float f3 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 0);
      float f4 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 1);
      float f5 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 2);
      float f6 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 0);
      float f7 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 1);
      float f8 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 2);
      float f9 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 3 + 0);
      float f10 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 3 + 1);
      float f11 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 3 + 2);
      float f12 = (f + f3 + f6 + f9) * 0.25F - p_181665_1_;
      float f13 = (f1 + f4 + f7 + f10) * 0.25F - p_181665_2_;
      float f14 = (f2 + f5 + f8 + f11) * 0.25F - p_181665_3_;
      return f12 * f12 + f13 * f13 + f14 * f14;
   }

   public void setVertexState(BufferBuilder.State p_178993_1_) {
      this.rawIntBuffer.clear();
      this.growBuffer(p_178993_1_.getRawBuffer().length * 4);
      this.rawIntBuffer.put(p_178993_1_.getRawBuffer());
      this.vertexCount = p_178993_1_.getVertexCount();
      this.vertexFormat = new VertexFormat(p_178993_1_.getVertexFormat());
   }

   public void reset() {
      this.vertexCount = 0;
      this.vertexFormatElement = null;
      this.vertexFormatIndex = 0;
   }

   public void begin(int p_181668_1_, VertexFormat p_181668_2_) {
      if (this.isDrawing) {
         throw new IllegalStateException("Already building!");
      } else {
         this.isDrawing = true;
         this.reset();
         this.drawMode = p_181668_1_;
         this.vertexFormat = p_181668_2_;
         this.vertexFormatElement = p_181668_2_.getElement(this.vertexFormatIndex);
         this.noColor = false;
         this.byteBuffer.limit(this.byteBuffer.capacity());
      }
   }

   public BufferBuilder tex(double p_187315_1_, double p_187315_3_) {
      int i = this.vertexCount * this.vertexFormat.getSize() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch(this.vertexFormatElement.getType()) {
      case FLOAT:
         this.byteBuffer.putFloat(i, (float)p_187315_1_);
         this.byteBuffer.putFloat(i + 4, (float)p_187315_3_);
         break;
      case UINT:
      case INT:
         this.byteBuffer.putInt(i, (int)p_187315_1_);
         this.byteBuffer.putInt(i + 4, (int)p_187315_3_);
         break;
      case USHORT:
      case SHORT:
         this.byteBuffer.putShort(i, (short)((int)p_187315_3_));
         this.byteBuffer.putShort(i + 2, (short)((int)p_187315_1_));
         break;
      case UBYTE:
      case BYTE:
         this.byteBuffer.put(i, (byte)((int)p_187315_3_));
         this.byteBuffer.put(i + 1, (byte)((int)p_187315_1_));
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public BufferBuilder lightmap(int p_187314_1_, int p_187314_2_) {
      int i = this.vertexCount * this.vertexFormat.getSize() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch(this.vertexFormatElement.getType()) {
      case FLOAT:
         this.byteBuffer.putFloat(i, (float)p_187314_1_);
         this.byteBuffer.putFloat(i + 4, (float)p_187314_2_);
         break;
      case UINT:
      case INT:
         this.byteBuffer.putInt(i, p_187314_1_);
         this.byteBuffer.putInt(i + 4, p_187314_2_);
         break;
      case USHORT:
      case SHORT:
         this.byteBuffer.putShort(i, (short)p_187314_2_);
         this.byteBuffer.putShort(i + 2, (short)p_187314_1_);
         break;
      case UBYTE:
      case BYTE:
         this.byteBuffer.put(i, (byte)p_187314_2_);
         this.byteBuffer.put(i + 1, (byte)p_187314_1_);
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void putBrightness4(int p_178962_1_, int p_178962_2_, int p_178962_3_, int p_178962_4_) {
      int i = (this.vertexCount - 4) * this.vertexFormat.getIntegerSize() + this.vertexFormat.getUvOffsetById(1) / 4;
      int j = this.vertexFormat.getSize() >> 2;
      this.rawIntBuffer.put(i, p_178962_1_);
      this.rawIntBuffer.put(i + j, p_178962_2_);
      this.rawIntBuffer.put(i + j * 2, p_178962_3_);
      this.rawIntBuffer.put(i + j * 3, p_178962_4_);
   }

   public void putPosition(double p_178987_1_, double p_178987_3_, double p_178987_5_) {
      int i = this.vertexFormat.getIntegerSize();
      int j = (this.vertexCount - 4) * i;

      for(int k = 0; k < 4; ++k) {
         int l = j + k * i;
         int i1 = l + 1;
         int j1 = i1 + 1;
         this.rawIntBuffer.put(l, Float.floatToRawIntBits((float)(p_178987_1_ + this.xOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(l))));
         this.rawIntBuffer.put(i1, Float.floatToRawIntBits((float)(p_178987_3_ + this.yOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(i1))));
         this.rawIntBuffer.put(j1, Float.floatToRawIntBits((float)(p_178987_5_ + this.zOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(j1))));
      }

   }

   public int getColorIndex(int p_78909_1_) {
      return ((this.vertexCount - p_78909_1_) * this.vertexFormat.getSize() + this.vertexFormat.getColorOffset()) / 4;
   }

   public void putColorMultiplier(float p_178978_1_, float p_178978_2_, float p_178978_3_, int p_178978_4_) {
      int i = this.getColorIndex(p_178978_4_);
      int j = -1;
      if (!this.noColor) {
         j = this.rawIntBuffer.get(i);
         if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            int k = (int)((float)(j & 255) * p_178978_1_);
            int l = (int)((float)(j >> 8 & 255) * p_178978_2_);
            int i1 = (int)((float)(j >> 16 & 255) * p_178978_3_);
            j = j & -16777216;
            j = j | i1 << 16 | l << 8 | k;
         } else {
            int j1 = (int)((float)(j >> 24 & 255) * p_178978_1_);
            int k1 = (int)((float)(j >> 16 & 255) * p_178978_2_);
            int l1 = (int)((float)(j >> 8 & 255) * p_178978_3_);
            j = j & 255;
            j = j | j1 << 24 | k1 << 16 | l1 << 8;
         }
      }

      this.rawIntBuffer.put(i, j);
   }

   private void putColor(int p_192836_1_, int p_192836_2_) {
      int i = this.getColorIndex(p_192836_2_);
      int j = p_192836_1_ >> 16 & 255;
      int k = p_192836_1_ >> 8 & 255;
      int l = p_192836_1_ & 255;
      this.putColorRGBA(i, j, k, l);
   }

   public void putColorRGB_F(float p_178994_1_, float p_178994_2_, float p_178994_3_, int p_178994_4_) {
      int i = this.getColorIndex(p_178994_4_);
      int j = MathHelper.clamp((int)(p_178994_1_ * 255.0F), 0, 255);
      int k = MathHelper.clamp((int)(p_178994_2_ * 255.0F), 0, 255);
      int l = MathHelper.clamp((int)(p_178994_3_ * 255.0F), 0, 255);
      this.putColorRGBA(i, j, k, l);
   }

   public void putColorRGBA(int p_178972_1_, int p_178972_2_, int p_178972_3_, int p_178972_4_) {
      if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
         this.rawIntBuffer.put(p_178972_1_, -16777216 | p_178972_4_ << 16 | p_178972_3_ << 8 | p_178972_2_);
      } else {
         this.rawIntBuffer.put(p_178972_1_, p_178972_2_ << 24 | p_178972_3_ << 16 | p_178972_4_ << 8 | 255);
      }

   }

   public void noColor() {
      this.noColor = true;
   }

   public BufferBuilder color(float p_181666_1_, float p_181666_2_, float p_181666_3_, float p_181666_4_) {
      return this.color((int)(p_181666_1_ * 255.0F), (int)(p_181666_2_ * 255.0F), (int)(p_181666_3_ * 255.0F), (int)(p_181666_4_ * 255.0F));
   }

   public BufferBuilder color(int p_181669_1_, int p_181669_2_, int p_181669_3_, int p_181669_4_) {
      if (this.noColor) {
         return this;
      } else {
         int i = this.vertexCount * this.vertexFormat.getSize() + this.vertexFormat.getOffset(this.vertexFormatIndex);
         switch(this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(i, (float)p_181669_1_ / 255.0F);
            this.byteBuffer.putFloat(i + 4, (float)p_181669_2_ / 255.0F);
            this.byteBuffer.putFloat(i + 8, (float)p_181669_3_ / 255.0F);
            this.byteBuffer.putFloat(i + 12, (float)p_181669_4_ / 255.0F);
            break;
         case UINT:
         case INT:
            this.byteBuffer.putFloat(i, (float)p_181669_1_);
            this.byteBuffer.putFloat(i + 4, (float)p_181669_2_);
            this.byteBuffer.putFloat(i + 8, (float)p_181669_3_);
            this.byteBuffer.putFloat(i + 12, (float)p_181669_4_);
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(i, (short)p_181669_1_);
            this.byteBuffer.putShort(i + 2, (short)p_181669_2_);
            this.byteBuffer.putShort(i + 4, (short)p_181669_3_);
            this.byteBuffer.putShort(i + 6, (short)p_181669_4_);
            break;
         case UBYTE:
         case BYTE:
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
               this.byteBuffer.put(i, (byte)p_181669_1_);
               this.byteBuffer.put(i + 1, (byte)p_181669_2_);
               this.byteBuffer.put(i + 2, (byte)p_181669_3_);
               this.byteBuffer.put(i + 3, (byte)p_181669_4_);
            } else {
               this.byteBuffer.put(i, (byte)p_181669_4_);
               this.byteBuffer.put(i + 1, (byte)p_181669_3_);
               this.byteBuffer.put(i + 2, (byte)p_181669_2_);
               this.byteBuffer.put(i + 3, (byte)p_181669_1_);
            }
         }

         this.nextVertexFormatIndex();
         return this;
      }
   }

   public void addVertexData(int[] p_178981_1_) {
      this.growBuffer(p_178981_1_.length * 4 + this.vertexFormat.getSize());
      this.rawIntBuffer.position(this.getBufferSize());
      this.rawIntBuffer.put(p_178981_1_);
      this.vertexCount += p_178981_1_.length / this.vertexFormat.getIntegerSize();
   }

   public void endVertex() {
      ++this.vertexCount;
      this.growBuffer(this.vertexFormat.getSize());
   }

   public BufferBuilder pos(double p_181662_1_, double p_181662_3_, double p_181662_5_) {
      int i = this.vertexCount * this.vertexFormat.getSize() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch(this.vertexFormatElement.getType()) {
      case FLOAT:
         this.byteBuffer.putFloat(i, (float)(p_181662_1_ + this.xOffset));
         this.byteBuffer.putFloat(i + 4, (float)(p_181662_3_ + this.yOffset));
         this.byteBuffer.putFloat(i + 8, (float)(p_181662_5_ + this.zOffset));
         break;
      case UINT:
      case INT:
         this.byteBuffer.putInt(i, Float.floatToRawIntBits((float)(p_181662_1_ + this.xOffset)));
         this.byteBuffer.putInt(i + 4, Float.floatToRawIntBits((float)(p_181662_3_ + this.yOffset)));
         this.byteBuffer.putInt(i + 8, Float.floatToRawIntBits((float)(p_181662_5_ + this.zOffset)));
         break;
      case USHORT:
      case SHORT:
         this.byteBuffer.putShort(i, (short)((int)(p_181662_1_ + this.xOffset)));
         this.byteBuffer.putShort(i + 2, (short)((int)(p_181662_3_ + this.yOffset)));
         this.byteBuffer.putShort(i + 4, (short)((int)(p_181662_5_ + this.zOffset)));
         break;
      case UBYTE:
      case BYTE:
         this.byteBuffer.put(i, (byte)((int)(p_181662_1_ + this.xOffset)));
         this.byteBuffer.put(i + 1, (byte)((int)(p_181662_3_ + this.yOffset)));
         this.byteBuffer.put(i + 2, (byte)((int)(p_181662_5_ + this.zOffset)));
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void putNormal(float p_178975_1_, float p_178975_2_, float p_178975_3_) {
      int i = (byte)((int)(p_178975_1_ * 127.0F)) & 255;
      int j = (byte)((int)(p_178975_2_ * 127.0F)) & 255;
      int k = (byte)((int)(p_178975_3_ * 127.0F)) & 255;
      int l = i | j << 8 | k << 16;
      int i1 = this.vertexFormat.getSize() >> 2;
      int j1 = (this.vertexCount - 4) * i1 + this.vertexFormat.getNormalOffset() / 4;
      this.rawIntBuffer.put(j1, l);
      this.rawIntBuffer.put(j1 + i1, l);
      this.rawIntBuffer.put(j1 + i1 * 2, l);
      this.rawIntBuffer.put(j1 + i1 * 3, l);
   }

   private void nextVertexFormatIndex() {
      ++this.vertexFormatIndex;
      this.vertexFormatIndex %= this.vertexFormat.getElementCount();
      this.vertexFormatElement = this.vertexFormat.getElement(this.vertexFormatIndex);
      if (this.vertexFormatElement.getUsage() == VertexFormatElement.EnumUsage.PADDING) {
         this.nextVertexFormatIndex();
      }

   }

   public BufferBuilder normal(float p_181663_1_, float p_181663_2_, float p_181663_3_) {
      int i = this.vertexCount * this.vertexFormat.getSize() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch(this.vertexFormatElement.getType()) {
      case FLOAT:
         this.byteBuffer.putFloat(i, p_181663_1_);
         this.byteBuffer.putFloat(i + 4, p_181663_2_);
         this.byteBuffer.putFloat(i + 8, p_181663_3_);
         break;
      case UINT:
      case INT:
         this.byteBuffer.putInt(i, (int)p_181663_1_);
         this.byteBuffer.putInt(i + 4, (int)p_181663_2_);
         this.byteBuffer.putInt(i + 8, (int)p_181663_3_);
         break;
      case USHORT:
      case SHORT:
         this.byteBuffer.putShort(i, (short)((int)p_181663_1_ * 32767 & '\uffff'));
         this.byteBuffer.putShort(i + 2, (short)((int)p_181663_2_ * 32767 & '\uffff'));
         this.byteBuffer.putShort(i + 4, (short)((int)p_181663_3_ * 32767 & '\uffff'));
         break;
      case UBYTE:
      case BYTE:
         this.byteBuffer.put(i, (byte)((int)p_181663_1_ * 127 & 255));
         this.byteBuffer.put(i + 1, (byte)((int)p_181663_2_ * 127 & 255));
         this.byteBuffer.put(i + 2, (byte)((int)p_181663_3_ * 127 & 255));
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void setTranslation(double p_178969_1_, double p_178969_3_, double p_178969_5_) {
      this.xOffset = p_178969_1_;
      this.yOffset = p_178969_3_;
      this.zOffset = p_178969_5_;
   }

   public void finishDrawing() {
      if (!this.isDrawing) {
         throw new IllegalStateException("Not building!");
      } else {
         this.isDrawing = false;
         this.byteBuffer.position(0);
         this.byteBuffer.limit(this.getBufferSize() * 4);
      }
   }

   public ByteBuffer getByteBuffer() {
      return this.byteBuffer;
   }

   public VertexFormat getVertexFormat() {
      return this.vertexFormat;
   }

   public int getVertexCount() {
      return this.vertexCount;
   }

   public int getDrawMode() {
      return this.drawMode;
   }

   public void putColor4(int p_178968_1_) {
      for(int i = 0; i < 4; ++i) {
         this.putColor(p_178968_1_, i + 1);
      }

   }

   public void putColorRGB_F4(float p_178990_1_, float p_178990_2_, float p_178990_3_) {
      for(int i = 0; i < 4; ++i) {
         this.putColorRGB_F(p_178990_1_, p_178990_2_, p_178990_3_, i + 1);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public class State {
      private final int[] stateRawBuffer;
      private final VertexFormat stateVertexFormat;

      public State(int[] p_i46453_2_, VertexFormat p_i46453_3_) {
         this.stateRawBuffer = p_i46453_2_;
         this.stateVertexFormat = p_i46453_3_;
      }

      public int[] getRawBuffer() {
         return this.stateRawBuffer;
      }

      public int getVertexCount() {
         return this.stateRawBuffer.length / this.stateVertexFormat.getIntegerSize();
      }

      public VertexFormat getVertexFormat() {
         return this.stateVertexFormat;
      }
   }
}
