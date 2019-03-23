package net.minecraft.world.gen.feature.template;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;

public class PlacementSettings {
   private Mirror mirror = Mirror.NONE;
   private Rotation rotation = Rotation.NONE;
   private BlockPos centerOffset = new BlockPos(0, 0, 0);
   private boolean ignoreEntities;
   @Nullable
   private Block replacedBlock;
   @Nullable
   private ChunkPos chunk;
   @Nullable
   private MutableBoundingBox boundingBox;
   private boolean ignoreStructureBlock = true;
   private boolean field_204765_h = true;
   private float integrity = 1.0F;
   @Nullable
   private Random random;
   @Nullable
   private Long setSeed;
   @Nullable
   private Integer field_204766_l;
   private int field_204767_m;

   public PlacementSettings copy() {
      PlacementSettings placementsettings = new PlacementSettings();
      placementsettings.mirror = this.mirror;
      placementsettings.rotation = this.rotation;
      placementsettings.centerOffset = this.centerOffset;
      placementsettings.ignoreEntities = this.ignoreEntities;
      placementsettings.replacedBlock = this.replacedBlock;
      placementsettings.chunk = this.chunk;
      placementsettings.boundingBox = this.boundingBox;
      placementsettings.ignoreStructureBlock = this.ignoreStructureBlock;
      placementsettings.field_204765_h = this.field_204765_h;
      placementsettings.integrity = this.integrity;
      placementsettings.random = this.random;
      placementsettings.setSeed = this.setSeed;
      placementsettings.field_204766_l = this.field_204766_l;
      placementsettings.field_204767_m = this.field_204767_m;
      return placementsettings;
   }

   public PlacementSettings setMirror(Mirror p_186214_1_) {
      this.mirror = p_186214_1_;
      return this;
   }

   public PlacementSettings setRotation(Rotation p_186220_1_) {
      this.rotation = p_186220_1_;
      return this;
   }

   public PlacementSettings setCenterOffset(BlockPos p_207665_1_) {
      this.centerOffset = p_207665_1_;
      return this;
   }

   public PlacementSettings setIgnoreEntities(boolean p_186222_1_) {
      this.ignoreEntities = p_186222_1_;
      return this;
   }

   public PlacementSettings setReplacedBlock(Block p_186225_1_) {
      this.replacedBlock = p_186225_1_;
      return this;
   }

   public PlacementSettings setChunk(ChunkPos p_186218_1_) {
      this.chunk = p_186218_1_;
      return this;
   }

   public PlacementSettings setBoundingBox(MutableBoundingBox p_186223_1_) {
      this.boundingBox = p_186223_1_;
      return this;
   }

   public PlacementSettings setSeed(@Nullable Long p_189949_1_) {
      this.setSeed = p_189949_1_;
      return this;
   }

   public PlacementSettings setRandom(@Nullable Random p_189950_1_) {
      this.random = p_189950_1_;
      return this;
   }

   public PlacementSettings setIntegrity(float p_189946_1_) {
      this.integrity = p_189946_1_;
      return this;
   }

   public Mirror getMirror() {
      return this.mirror;
   }

   public PlacementSettings setIgnoreStructureBlock(boolean p_186226_1_) {
      this.ignoreStructureBlock = p_186226_1_;
      return this;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public BlockPos func_207664_d() {
      return this.centerOffset;
   }

   public Random getRandom(@Nullable BlockPos p_189947_1_) {
      if (this.random != null) {
         return this.random;
      } else if (this.setSeed != null) {
         return this.setSeed == 0L ? new Random(Util.milliTime()) : new Random(this.setSeed);
      } else {
         return p_189947_1_ == null ? new Random(Util.milliTime()) : SharedSeedRandom.func_205190_a(p_189947_1_.getX(), p_189947_1_.getZ(), 0L, 987234911L);
      }
   }

   public float getIntegrity() {
      return this.integrity;
   }

   public boolean getIgnoreEntities() {
      return this.ignoreEntities;
   }

   @Nullable
   public Block getReplacedBlock() {
      return this.replacedBlock;
   }

   @Nullable
   public MutableBoundingBox getBoundingBox() {
      if (this.boundingBox == null && this.chunk != null) {
         this.setBoundingBoxFromChunk();
      }

      return this.boundingBox;
   }

   public boolean getIgnoreStructureBlock() {
      return this.ignoreStructureBlock;
   }

   void setBoundingBoxFromChunk() {
      if (this.chunk != null) {
         this.boundingBox = this.getBoundingBoxFromChunk(this.chunk);
      }

   }

   public boolean func_204763_l() {
      return this.field_204765_h;
   }

   public List<Template.BlockInfo> func_204764_a(List<List<Template.BlockInfo>> p_204764_1_, @Nullable BlockPos p_204764_2_) {
      this.field_204766_l = 8;
      if (this.field_204766_l != null && this.field_204766_l >= 0 && this.field_204766_l < p_204764_1_.size()) {
         return p_204764_1_.get(this.field_204766_l);
      } else {
         this.field_204766_l = this.getRandom(p_204764_2_).nextInt(p_204764_1_.size());
         return p_204764_1_.get(this.field_204766_l);
      }
   }

   @Nullable
   private MutableBoundingBox getBoundingBoxFromChunk(@Nullable ChunkPos p_186216_1_) {
      if (p_186216_1_ == null) {
         return this.boundingBox;
      } else {
         int i = p_186216_1_.x * 16;
         int j = p_186216_1_.z * 16;
         return new MutableBoundingBox(i, 0, j, i + 16 - 1, 255, j + 16 - 1);
      }
   }
}
