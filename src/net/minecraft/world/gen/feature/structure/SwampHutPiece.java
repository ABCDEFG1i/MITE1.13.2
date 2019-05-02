package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class SwampHutPiece extends ScatteredStructurePiece {
   private boolean witch;

   public static void registerPieces() {
      StructureIO.registerStructureComponent(SwampHutPiece.class, "TeSH");
   }

   public SwampHutPiece() {
   }

   public SwampHutPiece(Random p_i48652_1_, int p_i48652_2_, int p_i48652_3_) {
      super(p_i48652_1_, p_i48652_2_, 64, p_i48652_3_, 7, 7, 9);
   }

   protected void writeStructureToNBT(NBTTagCompound p_143012_1_) {
      super.writeStructureToNBT(p_143012_1_);
      p_143012_1_.setBoolean("Witch", this.witch);
   }

   protected void readStructureFromNBT(NBTTagCompound p_143011_1_, TemplateManager p_143011_2_) {
      super.readStructureFromNBT(p_143011_1_, p_143011_2_);
      this.witch = p_143011_1_.getBoolean("Witch");
   }

   public boolean addComponentParts(IWorld p_74875_1_, Random p_74875_2_, MutableBoundingBox p_74875_3_, ChunkPos p_74875_4_) {
      if (!this.func_202580_a(p_74875_1_, p_74875_3_, 0)) {
         return false;
      } else {
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 1, 1, 5, 1, 7, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 4, 2, 5, 4, 7, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 2, 1, 0, 4, 1, 0, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 2, 2, 2, 3, 3, 2, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 2, 3, 1, 3, 6, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 5, 2, 3, 5, 3, 6, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 2, 2, 7, 4, 3, 7, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 0, 2, 1, 3, 2, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 5, 0, 2, 5, 3, 2, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 0, 7, 1, 3, 7, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 5, 0, 7, 5, 3, 7, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
         this.setBlockState(p_74875_1_, Blocks.OAK_FENCE.getDefaultState(), 2, 3, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.OAK_FENCE.getDefaultState(), 3, 3, 7, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 1, 3, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 5, 3, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 5, 3, 5, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.POTTED_RED_MUSHROOM.getDefaultState(), 1, 3, 5, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.FLINT_CRAFTING_TABLE.getDefaultState(), 3, 2, 6, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.CAULDRON.getDefaultState(), 4, 2, 6, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.OAK_FENCE.getDefaultState(), 1, 2, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.OAK_FENCE.getDefaultState(), 5, 2, 1, p_74875_3_);
         IBlockState iblockstate = Blocks.SPRUCE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH);
         IBlockState iblockstate1 = Blocks.SPRUCE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.EAST);
         IBlockState iblockstate2 = Blocks.SPRUCE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.WEST);
         IBlockState iblockstate3 = Blocks.SPRUCE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.SOUTH);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 4, 1, 6, 4, 1, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 4, 2, 0, 4, 7, iblockstate1, iblockstate1, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 6, 4, 2, 6, 4, 7, iblockstate2, iblockstate2, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 4, 8, 6, 4, 8, iblockstate3, iblockstate3, false);
         this.setBlockState(p_74875_1_, iblockstate.with(BlockStairs.SHAPE, StairsShape.OUTER_RIGHT), 0, 4, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate.with(BlockStairs.SHAPE, StairsShape.OUTER_LEFT), 6, 4, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate3.with(BlockStairs.SHAPE, StairsShape.OUTER_LEFT), 0, 4, 8, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate3.with(BlockStairs.SHAPE, StairsShape.OUTER_RIGHT), 6, 4, 8, p_74875_3_);

         for(int i = 2; i <= 7; i += 5) {
            for(int j = 1; j <= 5; j += 4) {
               this.replaceAirAndLiquidDownwards(p_74875_1_, Blocks.OAK_LOG.getDefaultState(), j, -1, i, p_74875_3_);
            }
         }

         if (!this.witch) {
            int l = this.getXWithOffset(2, 5);
            int i1 = this.getYWithOffset(2);
            int k = this.getZWithOffset(2, 5);
            if (p_74875_3_.isVecInside(new BlockPos(l, i1, k))) {
               this.witch = true;
               EntityWitch entitywitch = new EntityWitch(p_74875_1_.getWorld());
               entitywitch.enablePersistence();
               entitywitch.setLocationAndAngles((double)l + 0.5D, (double)i1, (double)k + 0.5D, 0.0F, 0.0F);
               entitywitch.onInitialSpawn(p_74875_1_.getDifficultyForLocation(new BlockPos(l, i1, k)), null,
                       null);
               p_74875_1_.spawnEntity(entitywitch);
            }
         }

         return true;
      }
   }
}
