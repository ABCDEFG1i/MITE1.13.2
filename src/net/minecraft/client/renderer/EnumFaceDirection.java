package net.minecraft.client.renderer;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum EnumFaceDirection {
   DOWN(new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.NORTH_INDEX), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.NORTH_INDEX), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX)),
   UP(new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.NORTH_INDEX), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.NORTH_INDEX)),
   NORTH(new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.NORTH_INDEX), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.NORTH_INDEX), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.NORTH_INDEX), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.NORTH_INDEX)),
   SOUTH(new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX)),
   WEST(new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.NORTH_INDEX), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.NORTH_INDEX), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX)),
   EAST(new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.NORTH_INDEX), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.NORTH_INDEX));

   private static final EnumFaceDirection[] FACINGS = Util.make(new EnumFaceDirection[6], (p_209235_0_) -> {
      p_209235_0_[EnumFaceDirection.Constants.DOWN_INDEX] = DOWN;
      p_209235_0_[EnumFaceDirection.Constants.UP_INDEX] = UP;
      p_209235_0_[EnumFaceDirection.Constants.NORTH_INDEX] = NORTH;
      p_209235_0_[EnumFaceDirection.Constants.SOUTH_INDEX] = SOUTH;
      p_209235_0_[EnumFaceDirection.Constants.WEST_INDEX] = WEST;
      p_209235_0_[EnumFaceDirection.Constants.EAST_INDEX] = EAST;
   });
   private final EnumFaceDirection.VertexInformation[] vertexInfos;

   public static EnumFaceDirection getFacing(EnumFacing p_179027_0_) {
      return FACINGS[p_179027_0_.getIndex()];
   }

   private EnumFaceDirection(EnumFaceDirection.VertexInformation... p_i46272_3_) {
      this.vertexInfos = p_i46272_3_;
   }

   public EnumFaceDirection.VertexInformation getVertexInformation(int p_179025_1_) {
      return this.vertexInfos[p_179025_1_];
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Constants {
      public static final int SOUTH_INDEX = EnumFacing.SOUTH.getIndex();
      public static final int UP_INDEX = EnumFacing.UP.getIndex();
      public static final int EAST_INDEX = EnumFacing.EAST.getIndex();
      public static final int NORTH_INDEX = EnumFacing.NORTH.getIndex();
      public static final int DOWN_INDEX = EnumFacing.DOWN.getIndex();
      public static final int WEST_INDEX = EnumFacing.WEST.getIndex();
   }

   @OnlyIn(Dist.CLIENT)
   public static class VertexInformation {
      public final int xIndex;
      public final int yIndex;
      public final int zIndex;

      private VertexInformation(int p_i46270_1_, int p_i46270_2_, int p_i46270_3_) {
         this.xIndex = p_i46270_1_;
         this.yIndex = p_i46270_2_;
         this.zIndex = p_i46270_3_;
      }
   }
}
