package net.minecraft.world.gen.feature.structure;

import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class TemplateStructurePiece extends StructurePiece {
   private static final PlacementSettings DEFAULT_PLACE_SETTINGS = new PlacementSettings();
   protected Template template;
   protected PlacementSettings placeSettings = DEFAULT_PLACE_SETTINGS.setIgnoreEntities(true).setReplacedBlock(Blocks.AIR);
   protected BlockPos templatePosition;

   public TemplateStructurePiece() {
   }

   public TemplateStructurePiece(int p_i46662_1_) {
      super(p_i46662_1_);
   }

   protected void setup(Template p_186173_1_, BlockPos p_186173_2_, PlacementSettings p_186173_3_) {
      this.template = p_186173_1_;
      this.setCoordBaseMode(EnumFacing.NORTH);
      this.templatePosition = p_186173_2_;
      this.placeSettings = p_186173_3_;
      this.setBoundingBoxFromTemplate();
   }

   protected void writeStructureToNBT(NBTTagCompound p_143012_1_) {
      p_143012_1_.setInteger("TPX", this.templatePosition.getX());
      p_143012_1_.setInteger("TPY", this.templatePosition.getY());
      p_143012_1_.setInteger("TPZ", this.templatePosition.getZ());
   }

   protected void readStructureFromNBT(NBTTagCompound p_143011_1_, TemplateManager p_143011_2_) {
      this.templatePosition = new BlockPos(p_143011_1_.getInteger("TPX"), p_143011_1_.getInteger("TPY"), p_143011_1_.getInteger("TPZ"));
   }

   public boolean addComponentParts(IWorld p_74875_1_, Random p_74875_2_, MutableBoundingBox p_74875_3_, ChunkPos p_74875_4_) {
      this.placeSettings.setBoundingBox(p_74875_3_);
      if (this.template.addBlocksToWorld(p_74875_1_, this.templatePosition, this.placeSettings, 2)) {
         Map<BlockPos, String> map = this.template.getDataBlocks(this.templatePosition, this.placeSettings);

         for(Entry<BlockPos, String> entry : map.entrySet()) {
            String s = entry.getValue();
            this.handleDataMarker(s, entry.getKey(), p_74875_1_, p_74875_2_, p_74875_3_);
         }
      }

      return true;
   }

   protected abstract void handleDataMarker(String p_186175_1_, BlockPos p_186175_2_, IWorld p_186175_3_, Random p_186175_4_, MutableBoundingBox p_186175_5_);

   private void setBoundingBoxFromTemplate() {
      Rotation rotation = this.placeSettings.getRotation();
      BlockPos blockpos = this.placeSettings.func_207664_d();
      BlockPos blockpos1 = this.template.transformedSize(rotation);
      Mirror mirror = this.placeSettings.getMirror();
      int i = blockpos.getX();
      int j = blockpos.getZ();
      int k = blockpos1.getX() - 1;
      int l = blockpos1.getY() - 1;
      int i1 = blockpos1.getZ() - 1;
      switch(rotation) {
      case NONE:
         this.boundingBox = new MutableBoundingBox(0, 0, 0, k, l, i1);
         break;
      case CLOCKWISE_180:
         this.boundingBox = new MutableBoundingBox(i + i - k, 0, j + j - i1, i + i, l, j + j);
         break;
      case COUNTERCLOCKWISE_90:
         this.boundingBox = new MutableBoundingBox(i - j, 0, i + j - i1, i - j + k, l, i + j);
         break;
      case CLOCKWISE_90:
         this.boundingBox = new MutableBoundingBox(i + j - k, 0, j - i, i + j, l, j - i + i1);
      }

      switch(mirror) {
      case NONE:
      default:
         break;
      case FRONT_BACK:
         BlockPos blockpos3 = BlockPos.ORIGIN;
         if (rotation != Rotation.CLOCKWISE_90 && rotation != Rotation.COUNTERCLOCKWISE_90) {
            if (rotation == Rotation.CLOCKWISE_180) {
               blockpos3 = blockpos3.offset(EnumFacing.EAST, k);
            } else {
               blockpos3 = blockpos3.offset(EnumFacing.WEST, k);
            }
         } else {
            blockpos3 = blockpos3.offset(rotation.rotate(EnumFacing.WEST), i1);
         }

         this.boundingBox.offset(blockpos3.getX(), 0, blockpos3.getZ());
         break;
      case LEFT_RIGHT:
         BlockPos blockpos2 = BlockPos.ORIGIN;
         if (rotation != Rotation.CLOCKWISE_90 && rotation != Rotation.COUNTERCLOCKWISE_90) {
            if (rotation == Rotation.CLOCKWISE_180) {
               blockpos2 = blockpos2.offset(EnumFacing.SOUTH, i1);
            } else {
               blockpos2 = blockpos2.offset(EnumFacing.NORTH, i1);
            }
         } else {
            blockpos2 = blockpos2.offset(rotation.rotate(EnumFacing.NORTH), k);
         }

         this.boundingBox.offset(blockpos2.getX(), 0, blockpos2.getZ());
      }

      this.boundingBox.offset(this.templatePosition.getX(), this.templatePosition.getY(), this.templatePosition.getZ());
   }

   public void offset(int p_181138_1_, int p_181138_2_, int p_181138_3_) {
      super.offset(p_181138_1_, p_181138_2_, p_181138_3_);
      this.templatePosition = this.templatePosition.add(p_181138_1_, p_181138_2_, p_181138_3_);
   }
}
