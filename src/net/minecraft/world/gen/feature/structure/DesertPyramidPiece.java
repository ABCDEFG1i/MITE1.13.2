package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class DesertPyramidPiece extends ScatteredStructurePiece {
   private final boolean[] field_202598_e = new boolean[4];

   public static void registerPieces() {
      StructureIO.registerStructureComponent(DesertPyramidPiece.class, "TeDP");
   }

   public DesertPyramidPiece() {
   }

   public DesertPyramidPiece(Random p_i48658_1_, int p_i48658_2_, int p_i48658_3_) {
      super(p_i48658_1_, p_i48658_2_, 64, p_i48658_3_, 21, 15, 21);
   }

   protected void writeStructureToNBT(NBTTagCompound p_143012_1_) {
      super.writeStructureToNBT(p_143012_1_);
      p_143012_1_.setBoolean("hasPlacedChest0", this.field_202598_e[0]);
      p_143012_1_.setBoolean("hasPlacedChest1", this.field_202598_e[1]);
      p_143012_1_.setBoolean("hasPlacedChest2", this.field_202598_e[2]);
      p_143012_1_.setBoolean("hasPlacedChest3", this.field_202598_e[3]);
   }

   protected void readStructureFromNBT(NBTTagCompound p_143011_1_, TemplateManager p_143011_2_) {
      super.readStructureFromNBT(p_143011_1_, p_143011_2_);
      this.field_202598_e[0] = p_143011_1_.getBoolean("hasPlacedChest0");
      this.field_202598_e[1] = p_143011_1_.getBoolean("hasPlacedChest1");
      this.field_202598_e[2] = p_143011_1_.getBoolean("hasPlacedChest2");
      this.field_202598_e[3] = p_143011_1_.getBoolean("hasPlacedChest3");
   }

   public boolean addComponentParts(IWorld p_74875_1_, Random p_74875_2_, MutableBoundingBox p_74875_3_, ChunkPos p_74875_4_) {
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);

      for(int i = 1; i <= 9; ++i) {
         this.fillWithBlocks(p_74875_1_, p_74875_3_, i, i, i, this.width - 1 - i, i, this.depth - 1 - i, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, i + 1, i, i + 1, this.width - 2 - i, i, this.depth - 2 - i, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      }

      for(int k1 = 0; k1 < this.width; ++k1) {
         for(int j = 0; j < this.depth; ++j) {
            int k = -5;
            this.replaceAirAndLiquidDownwards(p_74875_1_, Blocks.SANDSTONE.getDefaultState(), k1, -5, j, p_74875_3_);
         }
      }

      IBlockState iblockstate1 = Blocks.SANDSTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH);
      IBlockState iblockstate2 = Blocks.SANDSTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.SOUTH);
      IBlockState iblockstate3 = Blocks.SANDSTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.EAST);
      IBlockState iblockstate = Blocks.SANDSTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.WEST);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.setBlockState(p_74875_1_, iblockstate1, 2, 10, 0, p_74875_3_);
      this.setBlockState(p_74875_1_, iblockstate2, 2, 10, 4, p_74875_3_);
      this.setBlockState(p_74875_1_, iblockstate3, 0, 10, 2, p_74875_3_);
      this.setBlockState(p_74875_1_, iblockstate, 4, 10, 2, p_74875_3_);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.setBlockState(p_74875_1_, iblockstate1, this.width - 3, 10, 0, p_74875_3_);
      this.setBlockState(p_74875_1_, iblockstate2, this.width - 3, 10, 4, p_74875_3_);
      this.setBlockState(p_74875_1_, iblockstate3, this.width - 5, 10, 2, p_74875_3_);
      this.setBlockState(p_74875_1_, iblockstate, this.width - 1, 10, 2, p_74875_3_);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 9, 1, 0, 11, 3, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 9, 1, 1, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 9, 2, 1, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 9, 3, 1, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 10, 3, 1, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 11, 3, 1, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 11, 2, 1, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 11, 1, 1, p_74875_3_);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 4, 1, 2, 8, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 12, 1, 2, 16, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 9, 4, 9, 11, 4, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 8, 1, 8, 8, 3, 8, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 12, 1, 8, 12, 3, 8, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 8, 1, 12, 8, 3, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 12, 1, 12, 12, 3, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 5, 5, 9, 5, 7, 11, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, this.width - 6, 5, 9, this.width - 6, 7, 11, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
      this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 5, 5, 10, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 5, 6, 10, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 6, 6, 10, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), this.width - 6, 5, 10, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), this.width - 6, 6, 10, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), this.width - 7, 6, 10, p_74875_3_);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 2, 4, 4, 2, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.setBlockState(p_74875_1_, iblockstate1, 2, 4, 5, p_74875_3_);
      this.setBlockState(p_74875_1_, iblockstate1, 2, 3, 4, p_74875_3_);
      this.setBlockState(p_74875_1_, iblockstate1, this.width - 3, 4, 5, p_74875_3_);
      this.setBlockState(p_74875_1_, iblockstate1, this.width - 3, 3, 4, p_74875_3_);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.setBlockState(p_74875_1_, Blocks.SANDSTONE.getDefaultState(), 1, 1, 2, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.SANDSTONE.getDefaultState(), this.width - 2, 1, 2, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.SANDSTONE_SLAB.getDefaultState(), 1, 2, 2, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.SANDSTONE_SLAB.getDefaultState(), this.width - 2, 2, 2, p_74875_3_);
      this.setBlockState(p_74875_1_, iblockstate, 2, 1, 2, p_74875_3_);
      this.setBlockState(p_74875_1_, iblockstate3, this.width - 3, 1, 2, p_74875_3_);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 4, 3, 5, 4, 3, 17, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 3, 1, 5, 4, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);

      for(int l = 5; l <= 17; l += 2) {
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 4, 1, l, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), 4, 2, l, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), this.width - 5, 1, l, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), this.width - 5, 2, l, p_74875_3_);
      }

      this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 7, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 8, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 9, 0, 9, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 11, 0, 9, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 8, 0, 10, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 12, 0, 10, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 7, 0, 10, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 13, 0, 10, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 9, 0, 11, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 11, 0, 11, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 12, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 13, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.BLUE_TERRACOTTA.getDefaultState(), 10, 0, 10, p_74875_3_);

      for(int l1 = 0; l1 <= this.width - 1; l1 += this.width - 1) {
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), l1, 2, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l1, 2, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), l1, 2, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), l1, 3, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l1, 3, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), l1, 3, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l1, 4, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), l1, 4, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l1, 4, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), l1, 5, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l1, 5, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), l1, 5, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l1, 6, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), l1, 6, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l1, 6, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l1, 7, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l1, 7, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), l1, 7, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), l1, 8, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), l1, 8, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), l1, 8, 3, p_74875_3_);
      }

      for(int i2 = 2; i2 <= this.width - 3; i2 += this.width - 3 - 2) {
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), i2 - 1, 2, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), i2, 2, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), i2 + 1, 2, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), i2 - 1, 3, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), i2, 3, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), i2 + 1, 3, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), i2 - 1, 4, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), i2, 4, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), i2 + 1, 4, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), i2 - 1, 5, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), i2, 5, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), i2 + 1, 5, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), i2 - 1, 6, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), i2, 6, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), i2 + 1, 6, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), i2 - 1, 7, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), i2, 7, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), i2 + 1, 7, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), i2 - 1, 8, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), i2, 8, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), i2 + 1, 8, 0, p_74875_3_);
      }

      this.fillWithBlocks(p_74875_1_, p_74875_3_, 8, 4, 0, 12, 6, 0, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
      this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 8, 6, 0, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 12, 6, 0, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 9, 5, 0, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), 10, 5, 0, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 11, 5, 0, p_74875_3_);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 8, -14, 8, 12, -11, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 8, -10, 8, 12, -10, 12, Blocks.CHISELED_SANDSTONE.getDefaultState(), Blocks.CHISELED_SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 8, -9, 8, 12, -9, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 9, -11, 9, 11, -1, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.setBlockState(p_74875_1_, Blocks.STONE_PRESSURE_PLATE.getDefaultState(), 10, -11, 10, p_74875_3_);
      this.fillWithBlocks(p_74875_1_, p_74875_3_, 9, -13, 9, 11, -13, 11, Blocks.TNT.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 8, -11, 10, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 8, -10, 10, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), 7, -10, 10, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 7, -11, 10, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 12, -11, 10, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 12, -10, 10, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), 13, -10, 10, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 13, -11, 10, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 10, -11, 8, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 10, -10, 8, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), 10, -10, 7, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 10, -11, 7, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 10, -11, 12, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 10, -10, 12, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), 10, -10, 13, p_74875_3_);
      this.setBlockState(p_74875_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 10, -11, 13, p_74875_3_);

      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         if (!this.field_202598_e[enumfacing.getHorizontalIndex()]) {
            int i1 = enumfacing.getXOffset() * 2;
            int j1 = enumfacing.getZOffset() * 2;
            this.field_202598_e[enumfacing.getHorizontalIndex()] = this.generateChest(p_74875_1_, p_74875_3_, p_74875_2_, 10 + i1, -11, 10 + j1, LootTableList.CHESTS_DESERT_PYRAMID);
         }
      }

      return true;
   }
}
