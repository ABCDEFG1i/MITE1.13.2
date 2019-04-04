package net.minecraft.client.renderer.vertex;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class VertexFormat {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<VertexFormatElement> elements = Lists.newArrayList();
   private final List<Integer> offsets = Lists.newArrayList();
   private int vertexSize;
   private int colorElementOffset = -1;
   private final List<Integer> uvOffsetsById = Lists.newArrayList();
   private int normalElementOffset = -1;

   public VertexFormat(VertexFormat p_i46097_1_) {
      this();

      for(int i = 0; i < p_i46097_1_.getElementCount(); ++i) {
         this.addElement(p_i46097_1_.getElement(i));
      }

      this.vertexSize = p_i46097_1_.getSize();
   }

   public VertexFormat() {
   }

   public void clear() {
      this.elements.clear();
      this.offsets.clear();
      this.colorElementOffset = -1;
      this.uvOffsetsById.clear();
      this.normalElementOffset = -1;
      this.vertexSize = 0;
   }

   public VertexFormat addElement(VertexFormatElement p_181721_1_) {
      if (p_181721_1_.isPositionElement() && this.hasPosition()) {
         LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
         return this;
      } else {
         this.elements.add(p_181721_1_);
         this.offsets.add(this.vertexSize);
         switch(p_181721_1_.getUsage()) {
         case NORMAL:
            this.normalElementOffset = this.vertexSize;
            break;
         case COLOR:
            this.colorElementOffset = this.vertexSize;
            break;
         case UV:
            this.uvOffsetsById.add(p_181721_1_.getIndex(), this.vertexSize);
         }

         this.vertexSize += p_181721_1_.getSize();
         return this;
      }
   }

   public boolean hasNormal() {
      return this.normalElementOffset >= 0;
   }

   public int getNormalOffset() {
      return this.normalElementOffset;
   }

   public boolean hasColor() {
      return this.colorElementOffset >= 0;
   }

   public int getColorOffset() {
      return this.colorElementOffset;
   }

   public boolean hasUv(int p_207750_1_) {
      return this.uvOffsetsById.size() - 1 >= p_207750_1_;
   }

   public int getUvOffsetById(int p_177344_1_) {
      return this.uvOffsetsById.get(p_177344_1_);
   }

   public String toString() {
      String s = "format: " + this.elements.size() + " elements: ";

      for(int i = 0; i < this.elements.size(); ++i) {
         s = s + this.elements.get(i).toString();
         if (i != this.elements.size() - 1) {
            s = s + " ";
         }
      }

      return s;
   }

   private boolean hasPosition() {
      int i = 0;

      for(int j = this.elements.size(); i < j; ++i) {
         VertexFormatElement vertexformatelement = this.elements.get(i);
         if (vertexformatelement.isPositionElement()) {
            return true;
         }
      }

      return false;
   }

   public int getIntegerSize() {
      return this.getSize() / 4;
   }

   public int getSize() {
      return this.vertexSize;
   }

   public List<VertexFormatElement> getElements() {
      return this.elements;
   }

   public int getElementCount() {
      return this.elements.size();
   }

   public VertexFormatElement getElement(int p_177348_1_) {
      return this.elements.get(p_177348_1_);
   }

   public int getOffset(int p_181720_1_) {
      return this.offsets.get(p_181720_1_);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         VertexFormat vertexformat = (VertexFormat)p_equals_1_;
         if (this.vertexSize != vertexformat.vertexSize) {
            return false;
         } else {
            return this.elements.equals(vertexformat.elements) && this.offsets.equals(vertexformat.offsets);
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = this.elements.hashCode();
      i = 31 * i + this.offsets.hashCode();
      i = 31 * i + this.vertexSize;
      return i;
   }
}
