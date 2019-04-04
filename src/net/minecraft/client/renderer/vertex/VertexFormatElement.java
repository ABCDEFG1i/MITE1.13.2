package net.minecraft.client.renderer.vertex;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class VertexFormatElement {
   private static final Logger LOGGER = LogManager.getLogger();
   private final VertexFormatElement.EnumType type;
   private final VertexFormatElement.EnumUsage usage;
   private final int index;
   private final int elementCount;

   public VertexFormatElement(int p_i46096_1_, VertexFormatElement.EnumType p_i46096_2_, VertexFormatElement.EnumUsage p_i46096_3_, int p_i46096_4_) {
      if (this.isFirstOrUV(p_i46096_1_, p_i46096_3_)) {
         this.usage = p_i46096_3_;
      } else {
         LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
         this.usage = VertexFormatElement.EnumUsage.UV;
      }

      this.type = p_i46096_2_;
      this.index = p_i46096_1_;
      this.elementCount = p_i46096_4_;
   }

   private final boolean isFirstOrUV(int p_177372_1_, VertexFormatElement.EnumUsage p_177372_2_) {
      return p_177372_1_ == 0 || p_177372_2_ == VertexFormatElement.EnumUsage.UV;
   }

   public final VertexFormatElement.EnumType getType() {
      return this.type;
   }

   public final VertexFormatElement.EnumUsage getUsage() {
      return this.usage;
   }

   public final int getElementCount() {
      return this.elementCount;
   }

   public final int getIndex() {
      return this.index;
   }

   public String toString() {
      return this.elementCount + "," + this.usage.getDisplayName() + "," + this.type.getDisplayName();
   }

   public final int getSize() {
      return this.type.getSize() * this.elementCount;
   }

   public final boolean isPositionElement() {
      return this.usage == VertexFormatElement.EnumUsage.POSITION;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         VertexFormatElement vertexformatelement = (VertexFormatElement)p_equals_1_;
         if (this.elementCount != vertexformatelement.elementCount) {
            return false;
         } else if (this.index != vertexformatelement.index) {
            return false;
         } else if (this.type != vertexformatelement.type) {
            return false;
         } else {
            return this.usage == vertexformatelement.usage;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = this.type.hashCode();
      i = 31 * i + this.usage.hashCode();
      i = 31 * i + this.index;
      i = 31 * i + this.elementCount;
      return i;
   }

   @OnlyIn(Dist.CLIENT)
   public enum EnumType {
      FLOAT(4, "Float", 5126),
      UBYTE(1, "Unsigned Byte", 5121),
      BYTE(1, "Byte", 5120),
      USHORT(2, "Unsigned Short", 5123),
      SHORT(2, "Short", 5122),
      UINT(4, "Unsigned Int", 5125),
      INT(4, "Int", 5124);

      private final int size;
      private final String displayName;
      private final int glConstant;

      EnumType(int p_i46095_3_, String p_i46095_4_, int p_i46095_5_) {
         this.size = p_i46095_3_;
         this.displayName = p_i46095_4_;
         this.glConstant = p_i46095_5_;
      }

      public int getSize() {
         return this.size;
      }

      public String getDisplayName() {
         return this.displayName;
      }

      public int getGlConstant() {
         return this.glConstant;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public enum EnumUsage {
      POSITION("Position"),
      NORMAL("Normal"),
      COLOR("Vertex Color"),
      UV("UV"),
      MATRIX("Bone Matrix"),
      BLEND_WEIGHT("Blend Weight"),
      PADDING("Padding");

      private final String displayName;

      EnumUsage(String p_i46094_3_) {
         this.displayName = p_i46094_3_;
      }

      public String getDisplayName() {
         return this.displayName;
      }
   }
}
