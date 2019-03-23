package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class ScatteredStructurePiece extends StructurePiece {
   protected int width;
   protected int height;
   protected int depth;
   protected int hPos = -1;

   public ScatteredStructurePiece() {
   }

   protected ScatteredStructurePiece(Random p_i48655_1_, int p_i48655_2_, int p_i48655_3_, int p_i48655_4_, int p_i48655_5_, int p_i48655_6_, int p_i48655_7_) {
      super(0);
      this.width = p_i48655_5_;
      this.height = p_i48655_6_;
      this.depth = p_i48655_7_;
      this.setCoordBaseMode(EnumFacing.Plane.HORIZONTAL.random(p_i48655_1_));
      if (this.getCoordBaseMode().getAxis() == EnumFacing.Axis.Z) {
         this.boundingBox = new MutableBoundingBox(p_i48655_2_, p_i48655_3_, p_i48655_4_, p_i48655_2_ + p_i48655_5_ - 1, p_i48655_3_ + p_i48655_6_ - 1, p_i48655_4_ + p_i48655_7_ - 1);
      } else {
         this.boundingBox = new MutableBoundingBox(p_i48655_2_, p_i48655_3_, p_i48655_4_, p_i48655_2_ + p_i48655_7_ - 1, p_i48655_3_ + p_i48655_6_ - 1, p_i48655_4_ + p_i48655_5_ - 1);
      }

   }

   protected void writeStructureToNBT(NBTTagCompound p_143012_1_) {
      p_143012_1_.setInteger("Width", this.width);
      p_143012_1_.setInteger("Height", this.height);
      p_143012_1_.setInteger("Depth", this.depth);
      p_143012_1_.setInteger("HPos", this.hPos);
   }

   protected void readStructureFromNBT(NBTTagCompound p_143011_1_, TemplateManager p_143011_2_) {
      this.width = p_143011_1_.getInteger("Width");
      this.height = p_143011_1_.getInteger("Height");
      this.depth = p_143011_1_.getInteger("Depth");
      this.hPos = p_143011_1_.getInteger("HPos");
   }

   protected boolean func_202580_a(IWorld p_202580_1_, MutableBoundingBox p_202580_2_, int p_202580_3_) {
      if (this.hPos >= 0) {
         return true;
      } else {
         int i = 0;
         int j = 0;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; ++k) {
            for(int l = this.boundingBox.minX; l <= this.boundingBox.maxX; ++l) {
               blockpos$mutableblockpos.setPos(l, 64, k);
               if (p_202580_2_.isVecInside(blockpos$mutableblockpos)) {
                  i += p_202580_1_.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos$mutableblockpos).getY();
                  ++j;
               }
            }
         }

         if (j == 0) {
            return false;
         } else {
            this.hPos = i / j;
            this.boundingBox.offset(0, this.hPos - this.boundingBox.minY + p_202580_3_, 0);
            return true;
         }
      }
   }
}
