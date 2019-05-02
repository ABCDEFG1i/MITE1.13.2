package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class VillagePieces {
   public static void registerVillagePieces() {
      StructureIO.registerStructureComponent(VillagePieces.House1.class, "ViBH");
      StructureIO.registerStructureComponent(VillagePieces.Field1.class, "ViDF");
      StructureIO.registerStructureComponent(VillagePieces.Field2.class, "ViF");
      StructureIO.registerStructureComponent(VillagePieces.Torch.class, "ViL");
      StructureIO.registerStructureComponent(VillagePieces.Hall.class, "ViPH");
      StructureIO.registerStructureComponent(VillagePieces.House4Garden.class, "ViSH");
      StructureIO.registerStructureComponent(VillagePieces.WoodHut.class, "ViSmH");
      StructureIO.registerStructureComponent(VillagePieces.Church.class, "ViST");
      StructureIO.registerStructureComponent(VillagePieces.House2.class, "ViS");
      StructureIO.registerStructureComponent(VillagePieces.Start.class, "ViStart");
      StructureIO.registerStructureComponent(VillagePieces.Path.class, "ViSR");
      StructureIO.registerStructureComponent(VillagePieces.House3.class, "ViTRH");
      StructureIO.registerStructureComponent(VillagePieces.Well.class, "ViW");
   }

   public static List<VillagePieces.PieceWeight> getStructureVillageWeightedPieceList(Random p_75084_0_, int p_75084_1_) {
      List<VillagePieces.PieceWeight> list = Lists.newArrayList();
      list.add(new VillagePieces.PieceWeight(VillagePieces.House4Garden.class, 4, MathHelper.nextInt(p_75084_0_, 2 + p_75084_1_, 4 + p_75084_1_ * 2)));
      list.add(new VillagePieces.PieceWeight(VillagePieces.Church.class, 20, MathHelper.nextInt(p_75084_0_, p_75084_1_, 1 + p_75084_1_)));
      list.add(new VillagePieces.PieceWeight(VillagePieces.House1.class, 20, MathHelper.nextInt(p_75084_0_, p_75084_1_, 2 + p_75084_1_)));
      list.add(new VillagePieces.PieceWeight(VillagePieces.WoodHut.class, 3, MathHelper.nextInt(p_75084_0_, 2 + p_75084_1_, 5 + p_75084_1_ * 3)));
      list.add(new VillagePieces.PieceWeight(VillagePieces.Hall.class, 15, MathHelper.nextInt(p_75084_0_, p_75084_1_, 2 + p_75084_1_)));
      list.add(new VillagePieces.PieceWeight(VillagePieces.Field1.class, 3, MathHelper.nextInt(p_75084_0_, 1 + p_75084_1_, 4 + p_75084_1_)));
      list.add(new VillagePieces.PieceWeight(VillagePieces.Field2.class, 3, MathHelper.nextInt(p_75084_0_, 2 + p_75084_1_, 4 + p_75084_1_ * 2)));
      list.add(new VillagePieces.PieceWeight(VillagePieces.House2.class, 15, MathHelper.nextInt(p_75084_0_, 0, 1 + p_75084_1_)));
      list.add(new VillagePieces.PieceWeight(VillagePieces.House3.class, 8, MathHelper.nextInt(p_75084_0_, p_75084_1_, 3 + p_75084_1_ * 2)));

      list.removeIf(pieceWeight -> (pieceWeight).villagePiecesLimit == 0);

      return list;
   }

   private static int updatePieceWeight(List<VillagePieces.PieceWeight> p_75079_0_) {
      boolean flag = false;
      int i = 0;

      for(VillagePieces.PieceWeight villagepieces$pieceweight : p_75079_0_) {
         if (villagepieces$pieceweight.villagePiecesLimit > 0 && villagepieces$pieceweight.villagePiecesSpawned < villagepieces$pieceweight.villagePiecesLimit) {
            flag = true;
         }

         i += villagepieces$pieceweight.villagePieceWeight;
      }

      return flag ? i : -1;
   }

   private static VillagePieces.Village findAndCreateComponentFactory(VillagePieces.Start p_176065_0_, VillagePieces.PieceWeight p_176065_1_, List<StructurePiece> p_176065_2_, Random p_176065_3_, int p_176065_4_, int p_176065_5_, int p_176065_6_, EnumFacing p_176065_7_, int p_176065_8_) {
      Class<? extends VillagePieces.Village> oclass = p_176065_1_.villagePieceClass;
      VillagePieces.Village villagepieces$village = null;
      if (oclass == VillagePieces.House4Garden.class) {
         villagepieces$village = VillagePieces.House4Garden.createPiece(p_176065_0_, p_176065_2_, p_176065_3_, p_176065_4_, p_176065_5_, p_176065_6_, p_176065_7_, p_176065_8_);
      } else if (oclass == VillagePieces.Church.class) {
         villagepieces$village = VillagePieces.Church.createPiece(p_176065_0_, p_176065_2_, p_176065_3_, p_176065_4_, p_176065_5_, p_176065_6_, p_176065_7_, p_176065_8_);
      } else if (oclass == VillagePieces.House1.class) {
         villagepieces$village = VillagePieces.House1.createPiece(p_176065_0_, p_176065_2_, p_176065_3_, p_176065_4_, p_176065_5_, p_176065_6_, p_176065_7_, p_176065_8_);
      } else if (oclass == VillagePieces.WoodHut.class) {
         villagepieces$village = VillagePieces.WoodHut.createPiece(p_176065_0_, p_176065_2_, p_176065_3_, p_176065_4_, p_176065_5_, p_176065_6_, p_176065_7_, p_176065_8_);
      } else if (oclass == VillagePieces.Hall.class) {
         villagepieces$village = VillagePieces.Hall.createPiece(p_176065_0_, p_176065_2_, p_176065_3_, p_176065_4_, p_176065_5_, p_176065_6_, p_176065_7_, p_176065_8_);
      } else if (oclass == VillagePieces.Field1.class) {
         villagepieces$village = VillagePieces.Field1.createPiece(p_176065_0_, p_176065_2_, p_176065_3_, p_176065_4_, p_176065_5_, p_176065_6_, p_176065_7_, p_176065_8_);
      } else if (oclass == VillagePieces.Field2.class) {
         villagepieces$village = VillagePieces.Field2.createPiece(p_176065_0_, p_176065_2_, p_176065_3_, p_176065_4_, p_176065_5_, p_176065_6_, p_176065_7_, p_176065_8_);
      } else if (oclass == VillagePieces.House2.class) {
         villagepieces$village = VillagePieces.House2.createPiece(p_176065_0_, p_176065_2_, p_176065_3_, p_176065_4_, p_176065_5_, p_176065_6_, p_176065_7_, p_176065_8_);
      } else if (oclass == VillagePieces.House3.class) {
         villagepieces$village = VillagePieces.House3.createPiece(p_176065_0_, p_176065_2_, p_176065_3_, p_176065_4_, p_176065_5_, p_176065_6_, p_176065_7_, p_176065_8_);
      }

      return villagepieces$village;
   }

   private static VillagePieces.Village generateComponent(VillagePieces.Start p_176067_0_, List<StructurePiece> p_176067_1_, Random p_176067_2_, int p_176067_3_, int p_176067_4_, int p_176067_5_, EnumFacing p_176067_6_, int p_176067_7_) {
      int i = updatePieceWeight(p_176067_0_.structureVillageWeightedPieceList);
      if (i <= 0) {
         return null;
      } else {
         int j = 0;

         while(j < 5) {
            ++j;
            int k = p_176067_2_.nextInt(i);

            for(VillagePieces.PieceWeight villagepieces$pieceweight : p_176067_0_.structureVillageWeightedPieceList) {
               k -= villagepieces$pieceweight.villagePieceWeight;
               if (k < 0) {
                  if (!villagepieces$pieceweight.canSpawnMoreVillagePiecesOfType(p_176067_7_) || villagepieces$pieceweight == p_176067_0_.lastPlaced && p_176067_0_.structureVillageWeightedPieceList.size() > 1) {
                     break;
                  }

                  VillagePieces.Village villagepieces$village = findAndCreateComponentFactory(p_176067_0_, villagepieces$pieceweight, p_176067_1_, p_176067_2_, p_176067_3_, p_176067_4_, p_176067_5_, p_176067_6_, p_176067_7_);
                  if (villagepieces$village != null) {
                     ++villagepieces$pieceweight.villagePiecesSpawned;
                     p_176067_0_.lastPlaced = villagepieces$pieceweight;
                     if (!villagepieces$pieceweight.canSpawnMoreVillagePieces()) {
                        p_176067_0_.structureVillageWeightedPieceList.remove(villagepieces$pieceweight);
                     }

                     return villagepieces$village;
                  }
               }
            }
         }

         MutableBoundingBox mutableboundingbox = VillagePieces.Torch.findPieceBox(p_176067_0_, p_176067_1_, p_176067_2_, p_176067_3_, p_176067_4_, p_176067_5_, p_176067_6_);
         if (mutableboundingbox != null) {
            return new VillagePieces.Torch(p_176067_0_, p_176067_7_, p_176067_2_, mutableboundingbox, p_176067_6_);
         } else {
            return null;
         }
      }
   }

   private static StructurePiece generateAndAddComponent(VillagePieces.Start p_176066_0_, List<StructurePiece> p_176066_1_, Random p_176066_2_, int p_176066_3_, int p_176066_4_, int p_176066_5_, EnumFacing p_176066_6_, int p_176066_7_) {
      if (p_176066_7_ > 50) {
         return null;
      } else if (Math.abs(p_176066_3_ - p_176066_0_.getBoundingBox().minX) <= 112 && Math.abs(p_176066_5_ - p_176066_0_.getBoundingBox().minZ) <= 112) {
         StructurePiece structurepiece = generateComponent(p_176066_0_, p_176066_1_, p_176066_2_, p_176066_3_, p_176066_4_, p_176066_5_, p_176066_6_, p_176066_7_ + 1);
         if (structurepiece != null) {
            p_176066_1_.add(structurepiece);
            p_176066_0_.pendingHouses.add(structurepiece);
            return structurepiece;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static StructurePiece generateAndAddRoadPiece(VillagePieces.Start p_176069_0_, List<StructurePiece> p_176069_1_, Random p_176069_2_, int p_176069_3_, int p_176069_4_, int p_176069_5_, EnumFacing p_176069_6_, int p_176069_7_) {
      if (p_176069_7_ > 3 + p_176069_0_.terrainType) {
         return null;
      } else if (Math.abs(p_176069_3_ - p_176069_0_.getBoundingBox().minX) <= 112 && Math.abs(p_176069_5_ - p_176069_0_.getBoundingBox().minZ) <= 112) {
         MutableBoundingBox mutableboundingbox = VillagePieces.Path.findPieceBox(p_176069_0_, p_176069_1_, p_176069_2_, p_176069_3_, p_176069_4_, p_176069_5_, p_176069_6_);
         if (mutableboundingbox != null && mutableboundingbox.minY > 10) {
            StructurePiece structurepiece = new VillagePieces.Path(p_176069_0_, p_176069_7_, p_176069_2_, mutableboundingbox, p_176069_6_);
            p_176069_1_.add(structurepiece);
            p_176069_0_.pendingRoads.add(structurepiece);
            return structurepiece;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static class Church extends VillagePieces.Village {
      public Church() {
      }

      public Church(VillagePieces.Start p_i45564_1_, int p_i45564_2_, Random p_i45564_3_, MutableBoundingBox p_i45564_4_, EnumFacing p_i45564_5_) {
         super(p_i45564_1_, p_i45564_2_);
         this.setCoordBaseMode(p_i45564_5_);
         this.boundingBox = p_i45564_4_;
      }

      public static VillagePieces.Church createPiece(VillagePieces.Start p_175854_0_, List<StructurePiece> p_175854_1_, Random p_175854_2_, int p_175854_3_, int p_175854_4_, int p_175854_5_, EnumFacing p_175854_6_, int p_175854_7_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175854_3_, p_175854_4_, p_175854_5_, 0, 0, 0, 5, 12, 9, p_175854_6_);
         return canVillageGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175854_1_, mutableboundingbox) == null ? new VillagePieces.Church(p_175854_0_, p_175854_7_, p_175854_2_, mutableboundingbox, p_175854_6_) : null;
      }

      public boolean addComponentParts(IWorld p_74875_1_, Random p_74875_2_, MutableBoundingBox p_74875_3_, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(p_74875_1_, p_74875_3_);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 12 - 1, 0);
         }

         IBlockState iblockstate = Blocks.COBBLESTONE.getDefaultState();
         IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH));
         IBlockState iblockstate2 = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.WEST));
         IBlockState iblockstate3 = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.EAST));
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 1, 1, 3, 3, 7, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 5, 1, 3, 9, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 0, 0, 3, 0, 8, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 1, 0, 3, 10, 0, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 1, 1, 0, 10, 3, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 4, 1, 1, 4, 10, 3, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 0, 4, 0, 4, 7, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 4, 0, 4, 4, 4, 7, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 1, 8, 3, 4, 8, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 5, 4, 3, 10, 4, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 5, 5, 3, 5, 7, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 9, 0, 4, 9, 4, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 4, 0, 4, 4, 4, iblockstate, iblockstate, false);
         this.setBlockState(p_74875_1_, iblockstate, 0, 11, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 4, 11, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 2, 11, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 2, 11, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 1, 1, 6, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 1, 1, 7, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 2, 1, 7, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 3, 1, 6, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 3, 1, 7, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 1, 1, 5, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 2, 1, 6, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 3, 1, 5, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate2, 1, 2, 7, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate3, 3, 2, 7, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.TRUE).with(BlockGlassPane.NORTH,
                 Boolean.TRUE), 0, 2, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.TRUE).with(BlockGlassPane.NORTH,
                 Boolean.TRUE), 0, 3, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.TRUE).with(BlockGlassPane.NORTH,
                 Boolean.TRUE), 4, 2, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.TRUE).with(BlockGlassPane.NORTH,
                 Boolean.TRUE), 4, 3, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.TRUE).with(BlockGlassPane.NORTH,
                 Boolean.TRUE), 0, 6, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.TRUE).with(BlockGlassPane.NORTH,
                 Boolean.TRUE), 0, 7, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.TRUE).with(BlockGlassPane.NORTH,
                 Boolean.TRUE), 4, 6, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.TRUE).with(BlockGlassPane.NORTH,
                 Boolean.TRUE), 4, 7, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.TRUE).with(BlockGlassPane.WEST,
                 Boolean.TRUE), 2, 6, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.TRUE).with(BlockGlassPane.WEST,
                 Boolean.TRUE), 2, 7, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.TRUE).with(BlockGlassPane.WEST,
                 Boolean.TRUE), 2, 6, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.TRUE).with(BlockGlassPane.WEST,
                 Boolean.TRUE), 2, 7, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.TRUE).with(BlockGlassPane.NORTH,
                 Boolean.TRUE), 0, 3, 6, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.TRUE).with(BlockGlassPane.NORTH,
                 Boolean.TRUE), 4, 3, 6, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.TRUE).with(BlockGlassPane.WEST,
                 Boolean.TRUE), 2, 3, 8, p_74875_3_);
         this.placeTorch(p_74875_1_, EnumFacing.SOUTH, 2, 4, 7, p_74875_3_);
         this.placeTorch(p_74875_1_, EnumFacing.EAST, 1, 4, 6, p_74875_3_);
         this.placeTorch(p_74875_1_, EnumFacing.WEST, 3, 4, 6, p_74875_3_);
         this.placeTorch(p_74875_1_, EnumFacing.NORTH, 2, 4, 5, p_74875_3_);
         IBlockState iblockstate4 = Blocks.LADDER.getDefaultState().with(BlockLadder.FACING, EnumFacing.WEST);

         for(int i = 1; i <= 9; ++i) {
            this.setBlockState(p_74875_1_, iblockstate4, 3, i, 3, p_74875_3_);
         }

         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 2, 1, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 2, 2, 0, p_74875_3_);
         this.createVillageDoor(p_74875_1_, p_74875_3_, p_74875_2_, 2, 1, 0, EnumFacing.NORTH);
         if (this.getBlockStateFromPos(p_74875_1_, 2, 0, -1, p_74875_3_).isAir() && !this.getBlockStateFromPos(p_74875_1_, 2, -1, -1, p_74875_3_).isAir()) {
            this.setBlockState(p_74875_1_, iblockstate1, 2, 0, -1, p_74875_3_);
            if (this.getBlockStateFromPos(p_74875_1_, 2, -1, -1, p_74875_3_).getBlock() == Blocks.GRASS_PATH) {
               this.setBlockState(p_74875_1_, Blocks.GRASS_BLOCK.getDefaultState(), 2, -1, -1, p_74875_3_);
            }
         }

         for(int k = 0; k < 9; ++k) {
            for(int j = 0; j < 5; ++j) {
               this.clearCurrentPositionBlocksUpwards(p_74875_1_, j, 12, k, p_74875_3_);
               this.replaceAirAndLiquidDownwards(p_74875_1_, iblockstate, j, -1, k, p_74875_3_);
            }
         }

         this.spawnVillagers(p_74875_1_, p_74875_3_, 2, 1, 2, 1);
         return true;
      }

      protected int chooseProfession(int p_180779_1_, int p_180779_2_) {
         return 2;
      }
   }

   public static class Field1 extends VillagePieces.Village {
      private IBlockState cropTypeA;
      private IBlockState cropTypeB;
      private IBlockState cropTypeC;
      private IBlockState cropTypeD;

      public Field1() {
      }

      public Field1(VillagePieces.Start p_i45570_1_, int p_i45570_2_, Random p_i45570_3_, MutableBoundingBox p_i45570_4_, EnumFacing p_i45570_5_) {
         super(p_i45570_1_, p_i45570_2_);
         this.setCoordBaseMode(p_i45570_5_);
         this.boundingBox = p_i45570_4_;
         this.cropTypeA = VillagePieces.Field2.genCropType(p_i45570_3_);
         this.cropTypeB = VillagePieces.Field2.genCropType(p_i45570_3_);
         this.cropTypeC = VillagePieces.Field2.genCropType(p_i45570_3_);
         this.cropTypeD = VillagePieces.Field2.genCropType(p_i45570_3_);
      }

      protected void writeStructureToNBT(NBTTagCompound p_143012_1_) {
         super.writeStructureToNBT(p_143012_1_);
         p_143012_1_.setTag("CA", NBTUtil.writeBlockState(this.cropTypeA));
         p_143012_1_.setTag("CB", NBTUtil.writeBlockState(this.cropTypeB));
         p_143012_1_.setTag("CC", NBTUtil.writeBlockState(this.cropTypeC));
         p_143012_1_.setTag("CD", NBTUtil.writeBlockState(this.cropTypeD));
      }

      protected void readStructureFromNBT(NBTTagCompound p_143011_1_, TemplateManager p_143011_2_) {
         super.readStructureFromNBT(p_143011_1_, p_143011_2_);
         this.cropTypeA = NBTUtil.readBlockState(p_143011_1_.getCompoundTag("CA"));
         this.cropTypeB = NBTUtil.readBlockState(p_143011_1_.getCompoundTag("CB"));
         this.cropTypeC = NBTUtil.readBlockState(p_143011_1_.getCompoundTag("CC"));
         this.cropTypeD = NBTUtil.readBlockState(p_143011_1_.getCompoundTag("CD"));
         if (!(this.cropTypeA.getBlock() instanceof BlockCrops)) {
            this.cropTypeA = Blocks.WHEAT.getDefaultState();
         }

         if (!(this.cropTypeB.getBlock() instanceof BlockCrops)) {
            this.cropTypeB = Blocks.CARROTS.getDefaultState();
         }

         if (!(this.cropTypeC.getBlock() instanceof BlockCrops)) {
            this.cropTypeC = Blocks.POTATOES.getDefaultState();
         }

         if (!(this.cropTypeD.getBlock() instanceof BlockCrops)) {
            this.cropTypeD = Blocks.BEETROOTS.getDefaultState();
         }

      }

      public static VillagePieces.Field1 createPiece(VillagePieces.Start p_175851_0_, List<StructurePiece> p_175851_1_, Random p_175851_2_, int p_175851_3_, int p_175851_4_, int p_175851_5_, EnumFacing p_175851_6_, int p_175851_7_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175851_3_, p_175851_4_, p_175851_5_, 0, 0, 0, 13, 4, 9, p_175851_6_);
         return canVillageGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175851_1_, mutableboundingbox) == null ? new VillagePieces.Field1(p_175851_0_, p_175851_7_, p_175851_2_, mutableboundingbox, p_175851_6_) : null;
      }

      public boolean addComponentParts(IWorld p_74875_1_, Random p_74875_2_, MutableBoundingBox p_74875_3_, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(p_74875_1_, p_74875_3_);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 4 - 1, 0);
         }

         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.OAK_LOG.getDefaultState());
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 1, 0, 12, 4, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 0, 1, 2, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 4, 0, 1, 5, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 7, 0, 1, 8, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 10, 0, 1, 11, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 0, 0, 0, 0, 8, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 6, 0, 0, 6, 0, 8, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 12, 0, 0, 12, 0, 8, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 0, 0, 11, 0, 0, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 0, 8, 11, 0, 8, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 3, 0, 1, 3, 0, 7, Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 9, 0, 1, 9, 0, 7, Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);

         //MITEMODDED Make plants need to grow when found village too
         for(int i = 1; i <= 7; ++i) {
            this.setBlockState(p_74875_1_, this.cropTypeA, 1, 1, i, p_74875_3_);
            this.setBlockState(p_74875_1_, this.cropTypeA, 2, 1, i, p_74875_3_);
            this.setBlockState(p_74875_1_, this.cropTypeB, 4, 1, i, p_74875_3_);
            this.setBlockState(p_74875_1_, this.cropTypeB, 5, 1, i, p_74875_3_);
            this.setBlockState(p_74875_1_, this.cropTypeC, 7, 1, i, p_74875_3_);
            this.setBlockState(p_74875_1_, this.cropTypeC, 8, 1, i, p_74875_3_);
            this.setBlockState(p_74875_1_, this.cropTypeD, 10, 1, i, p_74875_3_);
            this.setBlockState(p_74875_1_, this.cropTypeD, 11, 1, i, p_74875_3_);
         }

         for(int j2 = 0; j2 < 9; ++j2) {
            for(int k2 = 0; k2 < 13; ++k2) {
               this.clearCurrentPositionBlocksUpwards(p_74875_1_, k2, 4, j2, p_74875_3_);
               this.replaceAirAndLiquidDownwards(p_74875_1_, Blocks.DIRT.getDefaultState(), k2, -1, j2, p_74875_3_);
            }
         }

         return true;
      }
   }

   public static class Field2 extends VillagePieces.Village {
      private IBlockState cropTypeA;
      private IBlockState cropTypeB;

      public Field2() {
      }

      public Field2(VillagePieces.Start p_i45569_1_, int p_i45569_2_, Random p_i45569_3_, MutableBoundingBox p_i45569_4_, EnumFacing p_i45569_5_) {
         super(p_i45569_1_, p_i45569_2_);
         this.setCoordBaseMode(p_i45569_5_);
         this.boundingBox = p_i45569_4_;
         this.cropTypeA = genCropType(p_i45569_3_);
         this.cropTypeB = genCropType(p_i45569_3_);
      }

      protected void writeStructureToNBT(NBTTagCompound p_143012_1_) {
         super.writeStructureToNBT(p_143012_1_);
         p_143012_1_.setTag("CA", NBTUtil.writeBlockState(this.cropTypeA));
         p_143012_1_.setTag("CB", NBTUtil.writeBlockState(this.cropTypeB));
      }

      protected void readStructureFromNBT(NBTTagCompound p_143011_1_, TemplateManager p_143011_2_) {
         super.readStructureFromNBT(p_143011_1_, p_143011_2_);
         this.cropTypeA = NBTUtil.readBlockState(p_143011_1_.getCompoundTag("CA"));
         this.cropTypeB = NBTUtil.readBlockState(p_143011_1_.getCompoundTag("CB"));
      }

      private static IBlockState genCropType(Random p_197529_0_) {
         switch (p_197529_0_.nextInt(10)) {
            case 0:
            case 1:
               return Blocks.CARROTS.getDefaultState();
            case 2:
            case 3:
               return Blocks.POTATOES.getDefaultState();
            case 4:
               return Blocks.BEETROOTS.getDefaultState();
            default:
               return Blocks.WHEAT.getDefaultState();
         }
      }

      public static VillagePieces.Field2 createPiece(VillagePieces.Start p_175852_0_, List<StructurePiece> p_175852_1_, Random p_175852_2_, int p_175852_3_, int p_175852_4_, int p_175852_5_, EnumFacing p_175852_6_, int p_175852_7_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175852_3_, p_175852_4_, p_175852_5_, 0, 0, 0, 7, 4, 9, p_175852_6_);
         return canVillageGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175852_1_, mutableboundingbox) == null ? new VillagePieces.Field2(p_175852_0_, p_175852_7_, p_175852_2_, mutableboundingbox, p_175852_6_) : null;
      }

      public boolean addComponentParts(IWorld p_74875_1_, Random p_74875_2_, MutableBoundingBox p_74875_3_, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(p_74875_1_, p_74875_3_);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 4 - 1, 0);
         }

         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.OAK_LOG.getDefaultState());
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 1, 0, 6, 4, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 0, 1, 2, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 4, 0, 1, 5, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 0, 0, 0, 0, 8, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 6, 0, 0, 6, 0, 8, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 0, 0, 5, 0, 0, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 0, 8, 5, 0, 8, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 3, 0, 1, 3, 0, 7, Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);

         //MITEMODDED Make plants need to grow when found village too
         for(int i = 1; i <= 7; ++i) {
            this.setBlockState(p_74875_1_, this.cropTypeA, 1, 1, i, p_74875_3_);
            this.setBlockState(p_74875_1_, this.cropTypeA, 2, 1, i, p_74875_3_);
            this.setBlockState(p_74875_1_, this.cropTypeB, 4, 1, i, p_74875_3_);
            this.setBlockState(p_74875_1_, this.cropTypeB, 5, 1, i, p_74875_3_);
         }

         for(int j1 = 0; j1 < 9; ++j1) {
            for(int k1 = 0; k1 < 7; ++k1) {
               this.clearCurrentPositionBlocksUpwards(p_74875_1_, k1, 4, j1, p_74875_3_);
               this.replaceAirAndLiquidDownwards(p_74875_1_, Blocks.DIRT.getDefaultState(), k1, -1, j1, p_74875_3_);
            }
         }

         return true;
      }
   }

   public static class Hall extends VillagePieces.Village {
      public Hall() {
      }

      public Hall(VillagePieces.Start p_i45567_1_, int p_i45567_2_, Random p_i45567_3_, MutableBoundingBox p_i45567_4_, EnumFacing p_i45567_5_) {
         super(p_i45567_1_, p_i45567_2_);
         this.setCoordBaseMode(p_i45567_5_);
         this.boundingBox = p_i45567_4_;
      }

      public static VillagePieces.Hall createPiece(VillagePieces.Start p_175857_0_, List<StructurePiece> p_175857_1_, Random p_175857_2_, int p_175857_3_, int p_175857_4_, int p_175857_5_, EnumFacing p_175857_6_, int p_175857_7_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175857_3_, p_175857_4_, p_175857_5_, 0, 0, 0, 9, 7, 11, p_175857_6_);
         return canVillageGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175857_1_, mutableboundingbox) == null ? new VillagePieces.Hall(p_175857_0_, p_175857_7_, p_175857_2_, mutableboundingbox, p_175857_6_) : null;
      }

      public boolean addComponentParts(IWorld p_74875_1_, Random p_74875_2_, MutableBoundingBox p_74875_3_, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(p_74875_1_, p_74875_3_);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 7 - 1, 0);
         }

         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
         IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH));
         IBlockState iblockstate2 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.SOUTH));
         IBlockState iblockstate3 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.WEST));
         IBlockState iblockstate4 = this.getBiomeSpecificBlockState(Blocks.OAK_PLANKS.getDefaultState());
         IBlockState iblockstate5 = this.getBiomeSpecificBlockState(Blocks.OAK_LOG.getDefaultState());
         IBlockState iblockstate6 = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 1, 1, 7, 4, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 2, 1, 6, 8, 4, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 2, 0, 6, 8, 0, 10, Blocks.DIRT.getDefaultState(), Blocks.DIRT.getDefaultState(), false);
         this.setBlockState(p_74875_1_, iblockstate, 6, 0, 6, p_74875_3_);
         IBlockState iblockstate7 = iblockstate6.with(BlockFence.NORTH, Boolean.valueOf(true)).with(BlockFence.SOUTH, Boolean.valueOf(true));
         IBlockState iblockstate8 = iblockstate6.with(BlockFence.WEST, Boolean.valueOf(true)).with(BlockFence.EAST, Boolean.valueOf(true));
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 2, 1, 6, 2, 1, 9, iblockstate7, iblockstate7, false);
         this.setBlockState(p_74875_1_, iblockstate6.with(BlockFence.SOUTH, Boolean.valueOf(true)).with(BlockFence.EAST, Boolean.valueOf(true)), 2, 1, 10, p_74875_3_);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 8, 1, 6, 8, 1, 9, iblockstate7, iblockstate7, false);
         this.setBlockState(p_74875_1_, iblockstate6.with(BlockFence.SOUTH, Boolean.valueOf(true)).with(BlockFence.WEST, Boolean.valueOf(true)), 8, 1, 10, p_74875_3_);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 3, 1, 10, 7, 1, 10, iblockstate8, iblockstate8, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 0, 1, 7, 0, 4, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 0, 0, 0, 3, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 8, 0, 0, 8, 3, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 0, 0, 7, 1, 0, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 0, 5, 7, 1, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 2, 0, 7, 3, 0, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 2, 5, 7, 3, 5, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 4, 1, 8, 4, 1, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 4, 4, 8, 4, 4, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 5, 2, 8, 5, 3, iblockstate4, iblockstate4, false);
         this.setBlockState(p_74875_1_, iblockstate4, 0, 4, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate4, 0, 4, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate4, 8, 4, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate4, 8, 4, 3, p_74875_3_);
         IBlockState iblockstate9 = iblockstate1;
         IBlockState iblockstate10 = iblockstate2;

         for(int i = -1; i <= 2; ++i) {
            for(int j = 0; j <= 8; ++j) {
               this.setBlockState(p_74875_1_, iblockstate9, j, 4 + i, i, p_74875_3_);
               this.setBlockState(p_74875_1_, iblockstate10, j, 4 + i, 5 - i, p_74875_3_);
            }
         }

         this.setBlockState(p_74875_1_, iblockstate5, 0, 2, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate5, 0, 2, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate5, 8, 2, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate5, 8, 2, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 2, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 2, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 2, 2, 5, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 3, 2, 5, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 5, 2, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate6, 2, 1, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.OAK_PRESSURE_PLATE.getDefaultState(), 2, 2, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate4, 1, 1, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate9, 2, 1, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate3, 1, 1, 3, p_74875_3_);
         IBlockState iblockstate11 = Blocks.STONE_SLAB.getDefaultState().with(BlockSlab.TYPE, SlabType.DOUBLE);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 5, 0, 1, 7, 0, 3, iblockstate11, iblockstate11, false);
         this.setBlockState(p_74875_1_, iblockstate11, 6, 1, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate11, 6, 1, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 2, 1, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 2, 2, 0, p_74875_3_);
         this.placeTorch(p_74875_1_, EnumFacing.NORTH, 2, 3, 1, p_74875_3_);
         this.createVillageDoor(p_74875_1_, p_74875_3_, p_74875_2_, 2, 1, 0, EnumFacing.NORTH);
         if (this.getBlockStateFromPos(p_74875_1_, 2, 0, -1, p_74875_3_).isAir() && !this.getBlockStateFromPos(p_74875_1_, 2, -1, -1, p_74875_3_).isAir()) {
            this.setBlockState(p_74875_1_, iblockstate9, 2, 0, -1, p_74875_3_);
            if (this.getBlockStateFromPos(p_74875_1_, 2, -1, -1, p_74875_3_).getBlock() == Blocks.GRASS_PATH) {
               this.setBlockState(p_74875_1_, Blocks.GRASS_BLOCK.getDefaultState(), 2, -1, -1, p_74875_3_);
            }
         }

         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 6, 1, 5, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 6, 2, 5, p_74875_3_);
         this.placeTorch(p_74875_1_, EnumFacing.SOUTH, 6, 3, 4, p_74875_3_);
         this.createVillageDoor(p_74875_1_, p_74875_3_, p_74875_2_, 6, 1, 5, EnumFacing.SOUTH);

         for(int l = 0; l < 5; ++l) {
            for(int k = 0; k < 9; ++k) {
               this.clearCurrentPositionBlocksUpwards(p_74875_1_, k, 7, l, p_74875_3_);
               this.replaceAirAndLiquidDownwards(p_74875_1_, iblockstate, k, -1, l, p_74875_3_);
            }
         }

         this.spawnVillagers(p_74875_1_, p_74875_3_, 4, 1, 2, 2);
         return true;
      }

      protected int chooseProfession(int p_180779_1_, int p_180779_2_) {
         return p_180779_1_ == 0 ? 4 : super.chooseProfession(p_180779_1_, p_180779_2_);
      }
   }

   public static class House1 extends VillagePieces.Village {
      public House1() {
      }

      public House1(VillagePieces.Start p_i45571_1_, int p_i45571_2_, Random p_i45571_3_, MutableBoundingBox p_i45571_4_, EnumFacing p_i45571_5_) {
         super(p_i45571_1_, p_i45571_2_);
         this.setCoordBaseMode(p_i45571_5_);
         this.boundingBox = p_i45571_4_;
      }

      public static VillagePieces.House1 createPiece(VillagePieces.Start p_175850_0_, List<StructurePiece> p_175850_1_, Random p_175850_2_, int p_175850_3_, int p_175850_4_, int p_175850_5_, EnumFacing p_175850_6_, int p_175850_7_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175850_3_, p_175850_4_, p_175850_5_, 0, 0, 0, 9, 9, 6, p_175850_6_);
         return canVillageGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175850_1_, mutableboundingbox) == null ? new VillagePieces.House1(p_175850_0_, p_175850_7_, p_175850_2_, mutableboundingbox, p_175850_6_) : null;
      }

      public boolean addComponentParts(IWorld p_74875_1_, Random p_74875_2_, MutableBoundingBox p_74875_3_, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(p_74875_1_, p_74875_3_);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 9 - 1, 0);
         }

         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
         IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH));
         IBlockState iblockstate2 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.SOUTH));
         IBlockState iblockstate3 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.EAST));
         IBlockState iblockstate4 = this.getBiomeSpecificBlockState(Blocks.OAK_PLANKS.getDefaultState());
         IBlockState iblockstate5 = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH));
         IBlockState iblockstate6 = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 1, 1, 7, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 0, 0, 8, 0, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 5, 0, 8, 5, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 6, 1, 8, 6, 4, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 7, 2, 8, 7, 3, iblockstate, iblockstate, false);

         for(int i = -1; i <= 2; ++i) {
            for(int j = 0; j <= 8; ++j) {
               this.setBlockState(p_74875_1_, iblockstate1, j, 6 + i, i, p_74875_3_);
               this.setBlockState(p_74875_1_, iblockstate2, j, 6 + i, 5 - i, p_74875_3_);
            }
         }

         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 1, 0, 0, 1, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 1, 5, 8, 1, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 8, 1, 0, 8, 1, 4, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 2, 1, 0, 7, 1, 0, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 2, 0, 0, 4, 0, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 2, 5, 0, 4, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 8, 2, 5, 8, 4, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 8, 2, 0, 8, 4, 0, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 2, 1, 0, 4, 4, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 2, 5, 7, 4, 5, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 8, 2, 1, 8, 4, 4, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 2, 0, 7, 4, 0, iblockstate4, iblockstate4, false);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 4, 2, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 5, 2, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 6, 2, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 4, 3, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 5, 3, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 6, 3, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 3, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 3, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 2, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 2, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 3, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 3, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 2, 2, 5, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 3, 2, 5, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 5, 2, 5, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 6, 2, 5, p_74875_3_);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 4, 1, 7, 4, 1, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 4, 4, 7, 4, 4, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 3, 4, 7, 3, 4, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
         this.setBlockState(p_74875_1_, iblockstate4, 7, 1, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate3, 7, 1, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 6, 1, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 5, 1, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 4, 1, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 3, 1, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate6, 6, 1, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.OAK_PRESSURE_PLATE.getDefaultState(), 6, 2, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate6, 4, 1, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.OAK_PRESSURE_PLATE.getDefaultState(), 4, 2, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.COPPER_CRAFTING_TABLE.getDefaultState(), 7, 1, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 1, 1, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 1, 2, 0, p_74875_3_);
         this.createVillageDoor(p_74875_1_, p_74875_3_, p_74875_2_, 1, 1, 0, EnumFacing.NORTH);
         if (this.getBlockStateFromPos(p_74875_1_, 1, 0, -1, p_74875_3_).isAir() && !this.getBlockStateFromPos(p_74875_1_, 1, -1, -1, p_74875_3_).isAir()) {
            this.setBlockState(p_74875_1_, iblockstate5, 1, 0, -1, p_74875_3_);
            if (this.getBlockStateFromPos(p_74875_1_, 1, -1, -1, p_74875_3_).getBlock() == Blocks.GRASS_PATH) {
               this.setBlockState(p_74875_1_, Blocks.GRASS_BLOCK.getDefaultState(), 1, -1, -1, p_74875_3_);
            }
         }

         for(int l = 0; l < 6; ++l) {
            for(int k = 0; k < 9; ++k) {
               this.clearCurrentPositionBlocksUpwards(p_74875_1_, k, 9, l, p_74875_3_);
               this.replaceAirAndLiquidDownwards(p_74875_1_, iblockstate, k, -1, l, p_74875_3_);
            }
         }

         this.spawnVillagers(p_74875_1_, p_74875_3_, 2, 1, 2, 1);
         return true;
      }

      protected int chooseProfession(int p_180779_1_, int p_180779_2_) {
         return 1;
      }
   }

   public static class House2 extends VillagePieces.Village {
      private boolean hasMadeChest;

      public House2() {
      }

      public House2(VillagePieces.Start p_i45563_1_, int p_i45563_2_, Random p_i45563_3_, MutableBoundingBox p_i45563_4_, EnumFacing p_i45563_5_) {
         super(p_i45563_1_, p_i45563_2_);
         this.setCoordBaseMode(p_i45563_5_);
         this.boundingBox = p_i45563_4_;
      }

      public static VillagePieces.House2 createPiece(VillagePieces.Start p_175855_0_, List<StructurePiece> p_175855_1_, Random p_175855_2_, int p_175855_3_, int p_175855_4_, int p_175855_5_, EnumFacing p_175855_6_, int p_175855_7_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175855_3_, p_175855_4_, p_175855_5_, 0, 0, 0, 10, 6, 7, p_175855_6_);
         return canVillageGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175855_1_, mutableboundingbox) == null ? new VillagePieces.House2(p_175855_0_, p_175855_7_, p_175855_2_, mutableboundingbox, p_175855_6_) : null;
      }

      protected void writeStructureToNBT(NBTTagCompound p_143012_1_) {
         super.writeStructureToNBT(p_143012_1_);
         p_143012_1_.setBoolean("Chest", this.hasMadeChest);
      }

      protected void readStructureFromNBT(NBTTagCompound p_143011_1_, TemplateManager p_143011_2_) {
         super.readStructureFromNBT(p_143011_1_, p_143011_2_);
         this.hasMadeChest = p_143011_1_.getBoolean("Chest");
      }

      public boolean addComponentParts(IWorld p_74875_1_, Random p_74875_2_, MutableBoundingBox p_74875_3_, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(p_74875_1_, p_74875_3_);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 6 - 1, 0);
         }

         IBlockState iblockstate = Blocks.COBBLESTONE.getDefaultState();
         IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH));
         IBlockState iblockstate2 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.WEST));
         IBlockState iblockstate3 = this.getBiomeSpecificBlockState(Blocks.OAK_PLANKS.getDefaultState());
         IBlockState iblockstate4 = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH));
         IBlockState iblockstate5 = this.getBiomeSpecificBlockState(Blocks.OAK_LOG.getDefaultState());
         IBlockState iblockstate6 = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 1, 0, 9, 4, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 0, 0, 9, 0, 6, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 4, 0, 9, 4, 6, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 5, 0, 9, 5, 6, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 5, 1, 8, 5, 5, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 1, 0, 2, 3, 0, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 1, 0, 0, 4, 0, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 3, 1, 0, 3, 4, 0, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 1, 6, 0, 4, 6, iblockstate5, iblockstate5, false);
         this.setBlockState(p_74875_1_, iblockstate3, 3, 3, 1, p_74875_3_);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 3, 1, 2, 3, 3, 2, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 4, 1, 3, 5, 3, 3, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 1, 1, 0, 3, 5, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 1, 6, 5, 3, 6, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 5, 1, 0, 5, 3, 0, iblockstate6, iblockstate6, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 9, 1, 0, 9, 3, 0, iblockstate6, iblockstate6, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 6, 1, 4, 9, 4, 6, iblockstate, iblockstate, false);
         this.setBlockState(p_74875_1_, Blocks.LAVA.getDefaultState(), 7, 1, 5, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.LAVA.getDefaultState(), 8, 1, 5, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.IRON_BARS.getDefaultState().with(BlockPane.NORTH, Boolean.valueOf(true)).with(BlockPane.SOUTH, Boolean.valueOf(true)), 9, 2, 5, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.IRON_BARS.getDefaultState().with(BlockPane.NORTH, Boolean.valueOf(true)), 9, 2, 4, p_74875_3_);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 7, 2, 4, 8, 2, 5, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.setBlockState(p_74875_1_, iblockstate, 6, 1, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.COBBLESTONE_FURNACE.getDefaultState().with(BlockFurnace.FACING, EnumFacing.SOUTH), 6, 2, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.COBBLESTONE_FURNACE.getDefaultState().with(BlockFurnace.FACING, EnumFacing.SOUTH), 6, 3, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.STONE_SLAB.getDefaultState().with(BlockSlab.TYPE, SlabType.DOUBLE), 8, 1, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 2, 2, 6, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 4, 2, 6, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate6, 2, 1, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.OAK_PRESSURE_PLATE.getDefaultState(), 2, 2, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate3, 1, 1, 5, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 2, 1, 5, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate2, 1, 1, 4, p_74875_3_);
         if (!this.hasMadeChest && p_74875_3_.isVecInside(new BlockPos(this.getXWithOffset(5, 5), this.getYWithOffset(1), this.getZWithOffset(5, 5)))) {
            this.hasMadeChest = true;
            this.generateChest(p_74875_1_, p_74875_3_, p_74875_2_, 5, 1, 5, LootTableList.CHESTS_VILLAGE_BLACKSMITH);
         }

         for(int i = 6; i <= 8; ++i) {
            if (this.getBlockStateFromPos(p_74875_1_, i, 0, -1, p_74875_3_).isAir() && !this.getBlockStateFromPos(p_74875_1_, i, -1, -1, p_74875_3_).isAir()) {
               this.setBlockState(p_74875_1_, iblockstate4, i, 0, -1, p_74875_3_);
               if (this.getBlockStateFromPos(p_74875_1_, i, -1, -1, p_74875_3_).getBlock() == Blocks.GRASS_PATH) {
                  this.setBlockState(p_74875_1_, Blocks.GRASS_BLOCK.getDefaultState(), i, -1, -1, p_74875_3_);
               }
            }
         }

         for(int k = 0; k < 7; ++k) {
            for(int j = 0; j < 10; ++j) {
               this.clearCurrentPositionBlocksUpwards(p_74875_1_, j, 6, k, p_74875_3_);
               this.replaceAirAndLiquidDownwards(p_74875_1_, iblockstate, j, -1, k, p_74875_3_);
            }
         }

         this.spawnVillagers(p_74875_1_, p_74875_3_, 7, 1, 1, 1);
         return true;
      }

      protected int chooseProfession(int p_180779_1_, int p_180779_2_) {
         return 3;
      }
   }

   public static class House3 extends VillagePieces.Village {
      public House3() {
      }

      public House3(VillagePieces.Start p_i45561_1_, int p_i45561_2_, Random p_i45561_3_, MutableBoundingBox p_i45561_4_, EnumFacing p_i45561_5_) {
         super(p_i45561_1_, p_i45561_2_);
         this.setCoordBaseMode(p_i45561_5_);
         this.boundingBox = p_i45561_4_;
      }

      public static VillagePieces.House3 createPiece(VillagePieces.Start p_175849_0_, List<StructurePiece> p_175849_1_, Random p_175849_2_, int p_175849_3_, int p_175849_4_, int p_175849_5_, EnumFacing p_175849_6_, int p_175849_7_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175849_3_, p_175849_4_, p_175849_5_, 0, 0, 0, 9, 7, 12, p_175849_6_);
         return canVillageGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175849_1_, mutableboundingbox) == null ? new VillagePieces.House3(p_175849_0_, p_175849_7_, p_175849_2_, mutableboundingbox, p_175849_6_) : null;
      }

      public boolean addComponentParts(IWorld p_74875_1_, Random p_74875_2_, MutableBoundingBox p_74875_3_, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(p_74875_1_, p_74875_3_);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 7 - 1, 0);
         }

         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
         IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH));
         IBlockState iblockstate2 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.SOUTH));
         IBlockState iblockstate3 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.EAST));
         IBlockState iblockstate4 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.WEST));
         IBlockState iblockstate5 = this.getBiomeSpecificBlockState(Blocks.OAK_PLANKS.getDefaultState());
         IBlockState iblockstate6 = this.getBiomeSpecificBlockState(Blocks.OAK_LOG.getDefaultState());
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 1, 1, 7, 4, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 2, 1, 6, 8, 4, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 2, 0, 5, 8, 0, 10, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 0, 1, 7, 0, 4, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 0, 0, 0, 3, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 8, 0, 0, 8, 3, 10, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 0, 0, 7, 2, 0, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 0, 5, 2, 1, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 2, 0, 6, 2, 3, 10, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 3, 0, 10, 7, 3, 10, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 2, 0, 7, 3, 0, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 2, 5, 2, 3, 5, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 4, 1, 8, 4, 1, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 4, 4, 3, 4, 4, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 5, 2, 8, 5, 3, iblockstate5, iblockstate5, false);
         this.setBlockState(p_74875_1_, iblockstate5, 0, 4, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate5, 0, 4, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate5, 8, 4, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate5, 8, 4, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate5, 8, 4, 4, p_74875_3_);
         IBlockState iblockstate7 = iblockstate1;
         IBlockState iblockstate8 = iblockstate2;
         IBlockState iblockstate9 = iblockstate4;
         IBlockState iblockstate10 = iblockstate3;

         for(int i = -1; i <= 2; ++i) {
            for(int j = 0; j <= 8; ++j) {
               this.setBlockState(p_74875_1_, iblockstate7, j, 4 + i, i, p_74875_3_);
               if ((i > -1 || j <= 1) && (i > 0 || j <= 3) && (i > 1 || j <= 4 || j >= 6)) {
                  this.setBlockState(p_74875_1_, iblockstate8, j, 4 + i, 5 - i, p_74875_3_);
               }
            }
         }

         this.fillWithBlocks(p_74875_1_, p_74875_3_, 3, 4, 5, 3, 4, 10, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 7, 4, 2, 7, 4, 10, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 4, 5, 4, 4, 5, 10, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 6, 5, 4, 6, 5, 10, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 5, 6, 3, 5, 6, 10, iblockstate5, iblockstate5, false);

         for(int k = 4; k >= 1; --k) {
            this.setBlockState(p_74875_1_, iblockstate5, k, 2 + k, 7 - k, p_74875_3_);

            for(int k1 = 8 - k; k1 <= 10; ++k1) {
               this.setBlockState(p_74875_1_, iblockstate10, k, 2 + k, k1, p_74875_3_);
            }
         }

         this.setBlockState(p_74875_1_, iblockstate5, 6, 6, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate5, 7, 5, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate4, 6, 6, 4, p_74875_3_);

         for(int l = 6; l <= 8; ++l) {
            for(int l1 = 5; l1 <= 10; ++l1) {
               this.setBlockState(p_74875_1_, iblockstate9, l, 12 - l, l1, p_74875_3_);
            }
         }

         this.setBlockState(p_74875_1_, iblockstate6, 0, 2, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate6, 0, 2, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate6, 4, 2, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 5, 2, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate6, 6, 2, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate6, 8, 2, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 2, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 2, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate6, 8, 2, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate5, 8, 2, 5, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate6, 8, 2, 6, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 2, 7, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 2, 8, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate6, 8, 2, 9, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate6, 2, 2, 6, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 2, 2, 7, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 2, 2, 8, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate6, 2, 2, 9, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate6, 4, 4, 10, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 5, 4, 10, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate6, 6, 4, 10, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate5, 5, 5, 10, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 2, 1, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 2, 2, 0, p_74875_3_);
         this.placeTorch(p_74875_1_, EnumFacing.NORTH, 2, 3, 1, p_74875_3_);
         this.createVillageDoor(p_74875_1_, p_74875_3_, p_74875_2_, 2, 1, 0, EnumFacing.NORTH);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 0, -1, 3, 2, -1, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         if (this.getBlockStateFromPos(p_74875_1_, 2, 0, -1, p_74875_3_).isAir() && !this.getBlockStateFromPos(p_74875_1_, 2, -1, -1, p_74875_3_).isAir()) {
            this.setBlockState(p_74875_1_, iblockstate7, 2, 0, -1, p_74875_3_);
            if (this.getBlockStateFromPos(p_74875_1_, 2, -1, -1, p_74875_3_).getBlock() == Blocks.GRASS_PATH) {
               this.setBlockState(p_74875_1_, Blocks.GRASS_BLOCK.getDefaultState(), 2, -1, -1, p_74875_3_);
            }
         }

         for(int i1 = 0; i1 < 5; ++i1) {
            for(int i2 = 0; i2 < 9; ++i2) {
               this.clearCurrentPositionBlocksUpwards(p_74875_1_, i2, 7, i1, p_74875_3_);
               this.replaceAirAndLiquidDownwards(p_74875_1_, iblockstate, i2, -1, i1, p_74875_3_);
            }
         }

         for(int j1 = 5; j1 < 11; ++j1) {
            for(int j2 = 2; j2 < 9; ++j2) {
               this.clearCurrentPositionBlocksUpwards(p_74875_1_, j2, 7, j1, p_74875_3_);
               this.replaceAirAndLiquidDownwards(p_74875_1_, iblockstate, j2, -1, j1, p_74875_3_);
            }
         }

         this.spawnVillagers(p_74875_1_, p_74875_3_, 4, 1, 2, 2);
         return true;
      }
   }

   public static class House4Garden extends VillagePieces.Village {
      private boolean isRoofAccessible;

      public House4Garden() {
      }

      public House4Garden(VillagePieces.Start p_i45566_1_, int p_i45566_2_, Random p_i45566_3_, MutableBoundingBox p_i45566_4_, EnumFacing p_i45566_5_) {
         super(p_i45566_1_, p_i45566_2_);
         this.setCoordBaseMode(p_i45566_5_);
         this.boundingBox = p_i45566_4_;
         this.isRoofAccessible = p_i45566_3_.nextBoolean();
      }

      protected void writeStructureToNBT(NBTTagCompound p_143012_1_) {
         super.writeStructureToNBT(p_143012_1_);
         p_143012_1_.setBoolean("Terrace", this.isRoofAccessible);
      }

      protected void readStructureFromNBT(NBTTagCompound p_143011_1_, TemplateManager p_143011_2_) {
         super.readStructureFromNBT(p_143011_1_, p_143011_2_);
         this.isRoofAccessible = p_143011_1_.getBoolean("Terrace");
      }

      public static VillagePieces.House4Garden createPiece(VillagePieces.Start p_175858_0_, List<StructurePiece> p_175858_1_, Random p_175858_2_, int p_175858_3_, int p_175858_4_, int p_175858_5_, EnumFacing p_175858_6_, int p_175858_7_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175858_3_, p_175858_4_, p_175858_5_, 0, 0, 0, 5, 6, 5, p_175858_6_);
         return StructurePiece.findIntersecting(p_175858_1_, mutableboundingbox) != null ? null : new VillagePieces.House4Garden(p_175858_0_, p_175858_7_, p_175858_2_, mutableboundingbox, p_175858_6_);
      }

      public boolean addComponentParts(IWorld p_74875_1_, Random p_74875_2_, MutableBoundingBox p_74875_3_, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(p_74875_1_, p_74875_3_);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 6 - 1, 0);
         }

         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
         IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.OAK_PLANKS.getDefaultState());
         IBlockState iblockstate2 = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH));
         IBlockState iblockstate3 = this.getBiomeSpecificBlockState(Blocks.OAK_LOG.getDefaultState());
         IBlockState iblockstate4 = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 0, 0, 4, 0, 4, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 4, 0, 4, 4, 4, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 4, 1, 3, 4, 3, iblockstate1, iblockstate1, false);
         this.setBlockState(p_74875_1_, iblockstate, 0, 1, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 0, 2, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 0, 3, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 4, 1, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 4, 2, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 4, 3, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 0, 1, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 0, 2, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 0, 3, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 4, 1, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 4, 2, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 4, 3, 4, p_74875_3_);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 1, 1, 0, 3, 3, iblockstate1, iblockstate1, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 4, 1, 1, 4, 3, 3, iblockstate1, iblockstate1, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 1, 4, 3, 3, 4, iblockstate1, iblockstate1, false);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 2, 2, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 4, 2, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 1, 1, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 1, 2, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 1, 3, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 2, 3, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 3, 3, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 3, 2, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 3, 1, 0, p_74875_3_);
         if (this.getBlockStateFromPos(p_74875_1_, 2, 0, -1, p_74875_3_).isAir() && !this.getBlockStateFromPos(p_74875_1_, 2, -1, -1, p_74875_3_).isAir()) {
            this.setBlockState(p_74875_1_, iblockstate2, 2, 0, -1, p_74875_3_);
            if (this.getBlockStateFromPos(p_74875_1_, 2, -1, -1, p_74875_3_).getBlock() == Blocks.GRASS_PATH) {
               this.setBlockState(p_74875_1_, Blocks.GRASS_BLOCK.getDefaultState(), 2, -1, -1, p_74875_3_);
            }
         }

         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 1, 1, 3, 3, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         if (this.isRoofAccessible) {
            int i = 0;
            int j = 4;

            for(int k = 0; k <= 4; ++k) {
               for(int l = 0; l <= 4; ++l) {
                  boolean flag = k == 0 || k == 4;
                  boolean flag1 = l == 0 || l == 4;
                  if (flag || flag1) {
                     boolean flag2 = k == 0 || k == 4;
                     boolean flag3 = l == 0 || l == 4;
                     IBlockState iblockstate5 = iblockstate4.with(BlockFence.SOUTH, Boolean.valueOf(flag2 && l != 0)).with(BlockFence.NORTH, Boolean.valueOf(flag2 && l != 4)).with(BlockFence.WEST, Boolean.valueOf(flag3 && k != 0)).with(BlockFence.EAST, Boolean.valueOf(flag3 && k != 4));
                     this.setBlockState(p_74875_1_, iblockstate5, k, 5, l, p_74875_3_);
                  }
               }
            }
         }

         if (this.isRoofAccessible) {
            IBlockState iblockstate6 = Blocks.LADDER.getDefaultState().with(BlockLadder.FACING, EnumFacing.SOUTH);
            this.setBlockState(p_74875_1_, iblockstate6, 3, 1, 3, p_74875_3_);
            this.setBlockState(p_74875_1_, iblockstate6, 3, 2, 3, p_74875_3_);
            this.setBlockState(p_74875_1_, iblockstate6, 3, 3, 3, p_74875_3_);
            this.setBlockState(p_74875_1_, iblockstate6, 3, 4, 3, p_74875_3_);
         }

         this.placeTorch(p_74875_1_, EnumFacing.NORTH, 2, 3, 1, p_74875_3_);

         for(int i1 = 0; i1 < 5; ++i1) {
            for(int j1 = 0; j1 < 5; ++j1) {
               this.clearCurrentPositionBlocksUpwards(p_74875_1_, j1, 6, i1, p_74875_3_);
               this.replaceAirAndLiquidDownwards(p_74875_1_, iblockstate, j1, -1, i1, p_74875_3_);
            }
         }

         this.spawnVillagers(p_74875_1_, p_74875_3_, 1, 1, 2, 1);
         return true;
      }
   }

   public static class Path extends VillagePieces.Road {
      private int length;

      public Path() {
      }

      public Path(VillagePieces.Start p_i45562_1_, int p_i45562_2_, Random p_i45562_3_, MutableBoundingBox p_i45562_4_, EnumFacing p_i45562_5_) {
         super(p_i45562_1_, p_i45562_2_);
         this.setCoordBaseMode(p_i45562_5_);
         this.boundingBox = p_i45562_4_;
         this.length = Math.max(p_i45562_4_.getXSize(), p_i45562_4_.getZSize());
      }

      protected void writeStructureToNBT(NBTTagCompound p_143012_1_) {
         super.writeStructureToNBT(p_143012_1_);
         p_143012_1_.setInteger("Length", this.length);
      }

      protected void readStructureFromNBT(NBTTagCompound p_143011_1_, TemplateManager p_143011_2_) {
         super.readStructureFromNBT(p_143011_1_, p_143011_2_);
         this.length = p_143011_1_.getInteger("Length");
      }

      public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         boolean flag = false;

         for(int i = p_74861_3_.nextInt(5); i < this.length - 8; i += 2 + p_74861_3_.nextInt(5)) {
            StructurePiece structurepiece = this.getNextComponentNN((VillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, i);
            if (structurepiece != null) {
               i += Math.max(structurepiece.boundingBox.getXSize(), structurepiece.boundingBox.getZSize());
               flag = true;
            }
         }

         for(int j = p_74861_3_.nextInt(5); j < this.length - 8; j += 2 + p_74861_3_.nextInt(5)) {
            StructurePiece structurepiece1 = this.getNextComponentPP((VillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, j);
            if (structurepiece1 != null) {
               j += Math.max(structurepiece1.boundingBox.getXSize(), structurepiece1.boundingBox.getZSize());
               flag = true;
            }
         }

         EnumFacing enumfacing = this.getCoordBaseMode();
         if (flag && p_74861_3_.nextInt(3) > 0 && enumfacing != null) {
            switch(enumfacing) {
            case NORTH:
            default:
               VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.WEST, this.getComponentType());
               break;
            case SOUTH:
               VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 2, EnumFacing.WEST, this.getComponentType());
               break;
            case WEST:
               VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, this.getComponentType());
               break;
            case EAST:
               VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.maxX - 2, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, this.getComponentType());
            }
         }

         if (flag && p_74861_3_.nextInt(3) > 0 && enumfacing != null) {
            switch(enumfacing) {
            case NORTH:
            default:
               VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.EAST, this.getComponentType());
               break;
            case SOUTH:
               VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 2, EnumFacing.EAST, this.getComponentType());
               break;
            case WEST:
               VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, this.getComponentType());
               break;
            case EAST:
               VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.maxX - 2, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, this.getComponentType());
            }
         }

      }

      public static MutableBoundingBox findPieceBox(VillagePieces.Start p_175848_0_, List<StructurePiece> p_175848_1_, Random p_175848_2_, int p_175848_3_, int p_175848_4_, int p_175848_5_, EnumFacing p_175848_6_) {
         for(int i = 7 * MathHelper.nextInt(p_175848_2_, 3, 5); i >= 7; i -= 7) {
            MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175848_3_, p_175848_4_, p_175848_5_, 0, 0, 0, 3, 3, i, p_175848_6_);
            if (StructurePiece.findIntersecting(p_175848_1_, mutableboundingbox) == null) {
               return mutableboundingbox;
            }
         }

         return null;
      }

      public boolean addComponentParts(IWorld p_74875_1_, Random p_74875_2_, MutableBoundingBox p_74875_3_, ChunkPos p_74875_4_) {
         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.GRASS_PATH.getDefaultState());
         IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.OAK_PLANKS.getDefaultState());
         IBlockState iblockstate2 = this.getBiomeSpecificBlockState(Blocks.GRAVEL.getDefaultState());
         IBlockState iblockstate3 = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
         this.boundingBox.minY = 1000;
         this.boundingBox.maxY = 0;

         for(int i = this.boundingBox.minX; i <= this.boundingBox.maxX; ++i) {
            for(int j = this.boundingBox.minZ; j <= this.boundingBox.maxZ; ++j) {
               blockpos$mutableblockpos.setPos(i, 64, j);
               if (p_74875_3_.isVecInside(blockpos$mutableblockpos)) {
                  int k = p_74875_1_.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos$mutableblockpos.getX(), blockpos$mutableblockpos.getZ());
                  blockpos$mutableblockpos.setPos(blockpos$mutableblockpos.getX(), k, blockpos$mutableblockpos.getZ()).move(EnumFacing.DOWN);
                  if (blockpos$mutableblockpos.getY() < p_74875_1_.getSeaLevel()) {
                     blockpos$mutableblockpos.setY(p_74875_1_.getSeaLevel() - 1);
                  }

                  while(blockpos$mutableblockpos.getY() >= p_74875_1_.getSeaLevel() - 1) {
                     IBlockState iblockstate4 = p_74875_1_.getBlockState(blockpos$mutableblockpos);
                     Block block = iblockstate4.getBlock();
                     if (block == Blocks.GRASS_BLOCK && p_74875_1_.isAirBlock(blockpos$mutableblockpos.up())) {
                        p_74875_1_.setBlockState(blockpos$mutableblockpos, iblockstate, 2);
                        break;
                     }

                     if (iblockstate4.getMaterial().isLiquid()) {
                        p_74875_1_.setBlockState(new BlockPos(blockpos$mutableblockpos), iblockstate1, 2);
                        break;
                     }

                     if (block == Blocks.SAND || block == Blocks.RED_SAND || block == Blocks.SANDSTONE || block == Blocks.CHISELED_SANDSTONE || block == Blocks.CUT_SANDSTONE || block == Blocks.RED_SANDSTONE || block == Blocks.CHISELED_SANDSTONE || block == Blocks.CUT_SANDSTONE) {
                        p_74875_1_.setBlockState(blockpos$mutableblockpos, iblockstate2, 2);
                        p_74875_1_.setBlockState(blockpos$mutableblockpos.down(), iblockstate3, 2);
                        break;
                     }

                     blockpos$mutableblockpos.move(EnumFacing.DOWN);
                  }

                  this.boundingBox.minY = Math.min(this.boundingBox.minY, blockpos$mutableblockpos.getY());
                  this.boundingBox.maxY = Math.max(this.boundingBox.maxY, blockpos$mutableblockpos.getY());
               }
            }
         }

         return true;
      }
   }

   public static class PieceWeight {
      public Class<? extends VillagePieces.Village> villagePieceClass;
      public final int villagePieceWeight;
      public int villagePiecesSpawned;
      public int villagePiecesLimit;

      public PieceWeight(Class<? extends VillagePieces.Village> p_i2098_1_, int p_i2098_2_, int p_i2098_3_) {
         this.villagePieceClass = p_i2098_1_;
         this.villagePieceWeight = p_i2098_2_;
         this.villagePiecesLimit = p_i2098_3_;
      }

      public boolean canSpawnMoreVillagePiecesOfType(int p_75085_1_) {
         return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
      }

      public boolean canSpawnMoreVillagePieces() {
         return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
      }
   }

   public abstract static class Road extends VillagePieces.Village {
      public Road() {
      }

      protected Road(VillagePieces.Start p_i2108_1_, int p_i2108_2_) {
         super(p_i2108_1_, p_i2108_2_);
      }
   }

   public static class Start extends VillagePieces.Well {
      public int terrainType;
      public VillagePieces.PieceWeight lastPlaced;
      public List<VillagePieces.PieceWeight> structureVillageWeightedPieceList;
      public List<StructurePiece> pendingHouses = Lists.newArrayList();
      public List<StructurePiece> pendingRoads = Lists.newArrayList();

      public Start() {
      }

      public Start(int p_i48769_1_, Random p_i48769_2_, int p_i48769_3_, int p_i48769_4_, List<VillagePieces.PieceWeight> p_i48769_5_, VillageConfig p_i48769_6_) {
         super(null, 0, p_i48769_2_, p_i48769_3_, p_i48769_4_);
         this.structureVillageWeightedPieceList = p_i48769_5_;
         this.terrainType = p_i48769_6_.field_202461_a;
         this.structureType = p_i48769_6_.type;
         this.func_202579_a(this.structureType);
         this.isZombieInfested = p_i48769_2_.nextInt(50) == 0;
      }
   }

   public static class Torch extends VillagePieces.Village {
      public Torch() {
      }

      public Torch(VillagePieces.Start p_i45568_1_, int p_i45568_2_, Random p_i45568_3_, MutableBoundingBox p_i45568_4_, EnumFacing p_i45568_5_) {
         super(p_i45568_1_, p_i45568_2_);
         this.setCoordBaseMode(p_i45568_5_);
         this.boundingBox = p_i45568_4_;
      }

      public static MutableBoundingBox findPieceBox(VillagePieces.Start p_175856_0_, List<StructurePiece> p_175856_1_, Random p_175856_2_, int p_175856_3_, int p_175856_4_, int p_175856_5_, EnumFacing p_175856_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175856_3_, p_175856_4_, p_175856_5_, 0, 0, 0, 3, 4, 2, p_175856_6_);
         return StructurePiece.findIntersecting(p_175856_1_, mutableboundingbox) != null ? null : mutableboundingbox;
      }

      public boolean addComponentParts(IWorld p_74875_1_, Random p_74875_2_, MutableBoundingBox p_74875_3_, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(p_74875_1_, p_74875_3_);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 4 - 1, 0);
         }

         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 0, 0, 2, 3, 1, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.setBlockState(p_74875_1_, iblockstate, 1, 0, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 1, 1, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate, 1, 2, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.BLACK_WOOL.getDefaultState(), 1, 3, 0, p_74875_3_);
         this.placeTorch(p_74875_1_, EnumFacing.EAST, 2, 3, 0, p_74875_3_);
         this.placeTorch(p_74875_1_, EnumFacing.NORTH, 1, 3, 1, p_74875_3_);
         this.placeTorch(p_74875_1_, EnumFacing.WEST, 0, 3, 0, p_74875_3_);
         this.placeTorch(p_74875_1_, EnumFacing.SOUTH, 1, 3, -1, p_74875_3_);
         return true;
      }
   }

   public enum Type {
      OAK(0),
      SANDSTONE(1),
      ACACIA(2),
      SPRUCE(3);

      private final int field_202605_e;

      Type(int p_i48768_3_) {
         this.field_202605_e = p_i48768_3_;
      }

      public int func_202604_a() {
         return this.field_202605_e;
      }

      public static VillagePieces.Type func_202603_a(int p_202603_0_) {
         VillagePieces.Type[] avillagepieces$type = values();
         return p_202603_0_ >= 0 && p_202603_0_ < avillagepieces$type.length ? avillagepieces$type[p_202603_0_] : OAK;
      }
   }

   abstract static class Village extends StructurePiece {
      protected int averageGroundLvl = -1;
      private int villagersSpawned;
      protected VillagePieces.Type structureType;
      protected boolean isZombieInfested;

      public Village() {
      }

      protected Village(VillagePieces.Start p_i2107_1_, int p_i2107_2_) {
         super(p_i2107_2_);
         if (p_i2107_1_ != null) {
            this.structureType = p_i2107_1_.structureType;
            this.isZombieInfested = p_i2107_1_.isZombieInfested;
         }

      }

      protected void writeStructureToNBT(NBTTagCompound p_143012_1_) {
         p_143012_1_.setInteger("HPos", this.averageGroundLvl);
         p_143012_1_.setInteger("VCount", this.villagersSpawned);
         p_143012_1_.setByte("Type", (byte)this.structureType.func_202604_a());
         p_143012_1_.setBoolean("Zombie", this.isZombieInfested);
      }

      protected void readStructureFromNBT(NBTTagCompound p_143011_1_, TemplateManager p_143011_2_) {
         this.averageGroundLvl = p_143011_1_.getInteger("HPos");
         this.villagersSpawned = p_143011_1_.getInteger("VCount");
         this.structureType = VillagePieces.Type.func_202603_a(p_143011_1_.getByte("Type"));
         if (p_143011_1_.getBoolean("Desert")) {
            this.structureType = VillagePieces.Type.SANDSTONE;
         }

         this.isZombieInfested = p_143011_1_.getBoolean("Zombie");
      }

      @Nullable
      protected StructurePiece getNextComponentNN(VillagePieces.Start p_74891_1_, List<StructurePiece> p_74891_2_, Random p_74891_3_, int p_74891_4_, int p_74891_5_) {
         EnumFacing enumfacing = this.getCoordBaseMode();
         if (enumfacing != null) {
            switch(enumfacing) {
            case NORTH:
            default:
               return VillagePieces.generateAndAddComponent(p_74891_1_, p_74891_2_, p_74891_3_, this.boundingBox.minX - 1, this.boundingBox.minY + p_74891_4_, this.boundingBox.minZ + p_74891_5_, EnumFacing.WEST, this.getComponentType());
            case SOUTH:
               return VillagePieces.generateAndAddComponent(p_74891_1_, p_74891_2_, p_74891_3_, this.boundingBox.minX - 1, this.boundingBox.minY + p_74891_4_, this.boundingBox.minZ + p_74891_5_, EnumFacing.WEST, this.getComponentType());
            case WEST:
               return VillagePieces.generateAndAddComponent(p_74891_1_, p_74891_2_, p_74891_3_, this.boundingBox.minX + p_74891_5_, this.boundingBox.minY + p_74891_4_, this.boundingBox.minZ - 1, EnumFacing.NORTH, this.getComponentType());
            case EAST:
               return VillagePieces.generateAndAddComponent(p_74891_1_, p_74891_2_, p_74891_3_, this.boundingBox.minX + p_74891_5_, this.boundingBox.minY + p_74891_4_, this.boundingBox.minZ - 1, EnumFacing.NORTH, this.getComponentType());
            }
         } else {
            return null;
         }
      }

      @Nullable
      protected StructurePiece getNextComponentPP(VillagePieces.Start p_74894_1_, List<StructurePiece> p_74894_2_, Random p_74894_3_, int p_74894_4_, int p_74894_5_) {
         EnumFacing enumfacing = this.getCoordBaseMode();
         if (enumfacing != null) {
            switch(enumfacing) {
            case NORTH:
            default:
               return VillagePieces.generateAndAddComponent(p_74894_1_, p_74894_2_, p_74894_3_, this.boundingBox.maxX + 1, this.boundingBox.minY + p_74894_4_, this.boundingBox.minZ + p_74894_5_, EnumFacing.EAST, this.getComponentType());
            case SOUTH:
               return VillagePieces.generateAndAddComponent(p_74894_1_, p_74894_2_, p_74894_3_, this.boundingBox.maxX + 1, this.boundingBox.minY + p_74894_4_, this.boundingBox.minZ + p_74894_5_, EnumFacing.EAST, this.getComponentType());
            case WEST:
               return VillagePieces.generateAndAddComponent(p_74894_1_, p_74894_2_, p_74894_3_, this.boundingBox.minX + p_74894_5_, this.boundingBox.minY + p_74894_4_, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, this.getComponentType());
            case EAST:
               return VillagePieces.generateAndAddComponent(p_74894_1_, p_74894_2_, p_74894_3_, this.boundingBox.minX + p_74894_5_, this.boundingBox.minY + p_74894_4_, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, this.getComponentType());
            }
         } else {
            return null;
         }
      }

      protected int getAverageGroundLevel(IWorld p_74889_1_, MutableBoundingBox p_74889_2_) {
         int i = 0;
         int j = 0;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; ++k) {
            for(int l = this.boundingBox.minX; l <= this.boundingBox.maxX; ++l) {
               blockpos$mutableblockpos.setPos(l, 64, k);
               if (p_74889_2_.isVecInside(blockpos$mutableblockpos)) {
                  i += p_74889_1_.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos$mutableblockpos).getY();
                  ++j;
               }
            }
         }

         if (j == 0) {
            return -1;
         } else {
            return i / j;
         }
      }

      protected static boolean canVillageGoDeeper(MutableBoundingBox p_74895_0_) {
         return p_74895_0_ != null && p_74895_0_.minY > 10;
      }

      protected void spawnVillagers(IWorld p_74893_1_, MutableBoundingBox p_74893_2_, int p_74893_3_, int p_74893_4_, int p_74893_5_, int p_74893_6_) {
         if (this.villagersSpawned < p_74893_6_) {
            for(int i = this.villagersSpawned; i < p_74893_6_; ++i) {
               int j = this.getXWithOffset(p_74893_3_ + i, p_74893_5_);
               int k = this.getYWithOffset(p_74893_4_);
               int l = this.getZWithOffset(p_74893_3_ + i, p_74893_5_);
               if (!p_74893_2_.isVecInside(new BlockPos(j, k, l))) {
                  break;
               }

               ++this.villagersSpawned;
               if (this.isZombieInfested) {
                  EntityZombieVillager entityzombievillager = new EntityZombieVillager(p_74893_1_.getWorld());
                  entityzombievillager.setLocationAndAngles((double)j + 0.5D, (double)k, (double)l + 0.5D, 0.0F, 0.0F);
                  entityzombievillager.onInitialSpawn(p_74893_1_.getDifficultyForLocation(new BlockPos(entityzombievillager)),
                          null,
                          null);
                  entityzombievillager.setProfession(this.chooseProfession(i, 0));
                  entityzombievillager.enablePersistence();
                  p_74893_1_.spawnEntity(entityzombievillager);
               } else {
                  EntityVillager entityvillager = new EntityVillager(p_74893_1_.getWorld());
                  entityvillager.setLocationAndAngles((double)j + 0.5D, (double)k, (double)l + 0.5D, 0.0F, 0.0F);
                  entityvillager.setProfession(this.chooseProfession(i, p_74893_1_.getRandom().nextInt(6)));
                  entityvillager.finalizeMobSpawn(p_74893_1_.getDifficultyForLocation(new BlockPos(entityvillager)),
                          null,
                          null, false);
                  p_74893_1_.spawnEntity(entityvillager);
               }
            }

         }
      }

      protected int chooseProfession(int p_180779_1_, int p_180779_2_) {
         return p_180779_2_;
      }

      protected IBlockState getBiomeSpecificBlockState(IBlockState p_175847_1_) {
         Block block = p_175847_1_.getBlock();
         if (this.structureType == VillagePieces.Type.SANDSTONE) {
            if (block.isIn(BlockTags.LOGS) || block == Blocks.COBBLESTONE) {
               return Blocks.SANDSTONE.getDefaultState();
            }

            if (block.isIn(BlockTags.PLANKS)) {
               return Blocks.CUT_SANDSTONE.getDefaultState();
            }

            if (block == Blocks.OAK_STAIRS) {
               return Blocks.SANDSTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, p_175847_1_.get(BlockStairs.FACING));
            }

            if (block == Blocks.COBBLESTONE_STAIRS) {
               return Blocks.SANDSTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, p_175847_1_.get(BlockStairs.FACING));
            }

            if (block == Blocks.GRAVEL) {
               return Blocks.SANDSTONE.getDefaultState();
            }

            if (block == Blocks.OAK_PRESSURE_PLATE) {
               return Blocks.BIRCH_PRESSURE_PLATE.getDefaultState();
            }
         } else if (this.structureType == VillagePieces.Type.SPRUCE) {
            if (block.isIn(BlockTags.LOGS)) {
               return Blocks.SPRUCE_LOG.getDefaultState().with(BlockLog.AXIS, p_175847_1_.get(BlockLog.AXIS));
            }

            if (block.isIn(BlockTags.PLANKS)) {
               return Blocks.SPRUCE_PLANKS.getDefaultState();
            }

            if (block == Blocks.OAK_STAIRS) {
               return Blocks.SPRUCE_STAIRS.getDefaultState().with(BlockStairs.FACING, p_175847_1_.get(BlockStairs.FACING));
            }

            if (block == Blocks.OAK_FENCE) {
               return Blocks.SPRUCE_FENCE.getDefaultState();
            }

            if (block == Blocks.OAK_PRESSURE_PLATE) {
               return Blocks.SPRUCE_PRESSURE_PLATE.getDefaultState();
            }
         } else if (this.structureType == VillagePieces.Type.ACACIA) {
            if (block.isIn(BlockTags.LOGS)) {
               return Blocks.ACACIA_LOG.getDefaultState().with(BlockLog.AXIS, p_175847_1_.get(BlockLog.AXIS));
            }

            if (block.isIn(BlockTags.PLANKS)) {
               return Blocks.ACACIA_PLANKS.getDefaultState();
            }

            if (block == Blocks.OAK_STAIRS) {
               return Blocks.ACACIA_STAIRS.getDefaultState().with(BlockStairs.FACING, p_175847_1_.get(BlockStairs.FACING));
            }

            if (block == Blocks.COBBLESTONE) {
               return Blocks.ACACIA_LOG.getDefaultState().with(BlockLog.AXIS, EnumFacing.Axis.Y);
            }

            if (block == Blocks.OAK_FENCE) {
               return Blocks.ACACIA_FENCE.getDefaultState();
            }

            if (block == Blocks.OAK_PRESSURE_PLATE) {
               return Blocks.ACACIA_PRESSURE_PLATE.getDefaultState();
            }
         }

         return p_175847_1_;
      }

      protected BlockDoor biomeDoor() {
         if (this.structureType == VillagePieces.Type.ACACIA) {
            return (BlockDoor)Blocks.ACACIA_DOOR;
         } else {
            return this.structureType == VillagePieces.Type.SPRUCE ? (BlockDoor)Blocks.SPRUCE_DOOR : (BlockDoor)Blocks.OAK_DOOR;
         }
      }

      protected void createVillageDoor(IWorld p_189927_1_, MutableBoundingBox p_189927_2_, Random p_189927_3_, int p_189927_4_, int p_189927_5_, int p_189927_6_, EnumFacing p_189927_7_) {
         if (!this.isZombieInfested) {
            this.generateDoor(p_189927_1_, p_189927_2_, p_189927_3_, p_189927_4_, p_189927_5_, p_189927_6_, EnumFacing.NORTH, this.biomeDoor());
         }

      }

      protected void placeTorch(IWorld p_189926_1_, EnumFacing p_189926_2_, int p_189926_3_, int p_189926_4_, int p_189926_5_, MutableBoundingBox p_189926_6_) {
         if (!this.isZombieInfested) {
            this.setBlockState(p_189926_1_, Blocks.WALL_TORCH.getDefaultState().with(BlockTorchWall.HORIZONTAL_FACING, p_189926_2_), p_189926_3_, p_189926_4_, p_189926_5_, p_189926_6_);
         }

      }

      protected void replaceAirAndLiquidDownwards(IWorld p_175808_1_, IBlockState p_175808_2_, int p_175808_3_, int p_175808_4_, int p_175808_5_, MutableBoundingBox p_175808_6_) {
         IBlockState iblockstate = this.getBiomeSpecificBlockState(p_175808_2_);
         super.replaceAirAndLiquidDownwards(p_175808_1_, iblockstate, p_175808_3_, p_175808_4_, p_175808_5_, p_175808_6_);
      }

      protected void func_202579_a(VillagePieces.Type p_202579_1_) {
         this.structureType = p_202579_1_;
      }
   }

   public static class Well extends VillagePieces.Village {
      public Well() {
      }

      public Well(VillagePieces.Start p_i2109_1_, int p_i2109_2_, Random p_i2109_3_, int p_i2109_4_, int p_i2109_5_) {
         super(p_i2109_1_, p_i2109_2_);
         this.setCoordBaseMode(EnumFacing.Plane.HORIZONTAL.random(p_i2109_3_));
         if (this.getCoordBaseMode().getAxis() == EnumFacing.Axis.Z) {
            this.boundingBox = new MutableBoundingBox(p_i2109_4_, 64, p_i2109_5_, p_i2109_4_ + 6 - 1, 78, p_i2109_5_ + 6 - 1);
         } else {
            this.boundingBox = new MutableBoundingBox(p_i2109_4_, 64, p_i2109_5_, p_i2109_4_ + 6 - 1, 78, p_i2109_5_ + 6 - 1);
         }

      }

      public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX - 1, this.boundingBox.maxY - 4, this.boundingBox.minZ + 1, EnumFacing.WEST, this.getComponentType());
         VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.maxX + 1, this.boundingBox.maxY - 4, this.boundingBox.minZ + 1, EnumFacing.EAST, this.getComponentType());
         VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX + 1, this.boundingBox.maxY - 4, this.boundingBox.minZ - 1, EnumFacing.NORTH, this.getComponentType());
         VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX + 1, this.boundingBox.maxY - 4, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, this.getComponentType());
      }

      public boolean addComponentParts(IWorld p_74875_1_, Random p_74875_2_, MutableBoundingBox p_74875_3_, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(p_74875_1_, p_74875_3_);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 3, 0);
         }

         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
         IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 0, 1, 4, 12, 4, iblockstate, Blocks.WATER.getDefaultState(), false);
         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 2, 12, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 3, 12, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 2, 12, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 3, 12, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 1, 13, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 1, 14, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 4, 13, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 4, 14, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 1, 13, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 1, 14, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 4, 13, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate1, 4, 14, 4, p_74875_3_);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 15, 1, 4, 15, 4, iblockstate, iblockstate, false);

         for(int i = 0; i <= 5; ++i) {
            for(int j = 0; j <= 5; ++j) {
               if (j == 0 || j == 5 || i == 0 || i == 5) {
                  this.setBlockState(p_74875_1_, iblockstate, j, 11, i, p_74875_3_);
                  this.clearCurrentPositionBlocksUpwards(p_74875_1_, j, 12, i, p_74875_3_);
               }
            }
         }

         return true;
      }
   }

   public static class WoodHut extends VillagePieces.Village {
      private boolean isTallHouse;
      private int tablePosition;

      public WoodHut() {
      }

      public WoodHut(VillagePieces.Start p_i45565_1_, int p_i45565_2_, Random p_i45565_3_, MutableBoundingBox p_i45565_4_, EnumFacing p_i45565_5_) {
         super(p_i45565_1_, p_i45565_2_);
         this.setCoordBaseMode(p_i45565_5_);
         this.boundingBox = p_i45565_4_;
         this.isTallHouse = p_i45565_3_.nextBoolean();
         this.tablePosition = p_i45565_3_.nextInt(3);
      }

      protected void writeStructureToNBT(NBTTagCompound p_143012_1_) {
         super.writeStructureToNBT(p_143012_1_);
         p_143012_1_.setInteger("T", this.tablePosition);
         p_143012_1_.setBoolean("C", this.isTallHouse);
      }

      protected void readStructureFromNBT(NBTTagCompound p_143011_1_, TemplateManager p_143011_2_) {
         super.readStructureFromNBT(p_143011_1_, p_143011_2_);
         this.tablePosition = p_143011_1_.getInteger("T");
         this.isTallHouse = p_143011_1_.getBoolean("C");
      }

      public static VillagePieces.WoodHut createPiece(VillagePieces.Start p_175853_0_, List<StructurePiece> p_175853_1_, Random p_175853_2_, int p_175853_3_, int p_175853_4_, int p_175853_5_, EnumFacing p_175853_6_, int p_175853_7_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175853_3_, p_175853_4_, p_175853_5_, 0, 0, 0, 4, 6, 5, p_175853_6_);
         return canVillageGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175853_1_, mutableboundingbox) == null ? new VillagePieces.WoodHut(p_175853_0_, p_175853_7_, p_175853_2_, mutableboundingbox, p_175853_6_) : null;
      }

      public boolean addComponentParts(IWorld p_74875_1_, Random p_74875_2_, MutableBoundingBox p_74875_3_, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(p_74875_1_, p_74875_3_);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 6 - 1, 0);
         }

         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
         IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.OAK_PLANKS.getDefaultState());
         IBlockState iblockstate2 = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH));
         IBlockState iblockstate3 = this.getBiomeSpecificBlockState(Blocks.OAK_LOG.getDefaultState());
         IBlockState iblockstate4 = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 1, 1, 3, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 0, 0, 3, 0, 4, iblockstate, iblockstate, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 0, 1, 2, 0, 3, Blocks.DIRT.getDefaultState(), Blocks.DIRT.getDefaultState(), false);
         if (this.isTallHouse) {
            this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 4, 1, 2, 4, 3, iblockstate3, iblockstate3, false);
         } else {
            this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 5, 1, 2, 5, 3, iblockstate3, iblockstate3, false);
         }

         this.setBlockState(p_74875_1_, iblockstate3, 1, 4, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate3, 2, 4, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate3, 1, 4, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate3, 2, 4, 4, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate3, 0, 4, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate3, 0, 4, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate3, 0, 4, 3, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate3, 3, 4, 1, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate3, 3, 4, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, iblockstate3, 3, 4, 3, p_74875_3_);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 1, 0, 0, 3, 0, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 3, 1, 0, 3, 3, 0, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 1, 4, 0, 3, 4, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 3, 1, 4, 3, 3, 4, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 0, 1, 1, 0, 3, 3, iblockstate1, iblockstate1, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 3, 1, 1, 3, 3, 3, iblockstate1, iblockstate1, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 1, 0, 2, 3, 0, iblockstate1, iblockstate1, false);
         this.fillWithBlocks(p_74875_1_, p_74875_3_, 1, 1, 4, 2, 3, 4, iblockstate1, iblockstate1, false);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 2, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 3, 2, 2, p_74875_3_);
         if (this.tablePosition > 0) {
            this.setBlockState(p_74875_1_, iblockstate4.with(BlockFence.NORTH, Boolean.valueOf(true)).with(this.tablePosition == 1 ? BlockFence.WEST : BlockFence.EAST, Boolean.valueOf(true)), this.tablePosition, 1, 3, p_74875_3_);
            this.setBlockState(p_74875_1_, Blocks.OAK_PRESSURE_PLATE.getDefaultState(), this.tablePosition, 2, 3, p_74875_3_);
         }

         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 1, 1, 0, p_74875_3_);
         this.setBlockState(p_74875_1_, Blocks.AIR.getDefaultState(), 1, 2, 0, p_74875_3_);
         this.createVillageDoor(p_74875_1_, p_74875_3_, p_74875_2_, 1, 1, 0, EnumFacing.NORTH);
         if (this.getBlockStateFromPos(p_74875_1_, 1, 0, -1, p_74875_3_).isAir() && !this.getBlockStateFromPos(p_74875_1_, 1, -1, -1, p_74875_3_).isAir()) {
            this.setBlockState(p_74875_1_, iblockstate2, 1, 0, -1, p_74875_3_);
            if (this.getBlockStateFromPos(p_74875_1_, 1, -1, -1, p_74875_3_).getBlock() == Blocks.GRASS_PATH) {
               this.setBlockState(p_74875_1_, Blocks.GRASS_BLOCK.getDefaultState(), 1, -1, -1, p_74875_3_);
            }
         }

         for(int i = 0; i < 5; ++i) {
            for(int j = 0; j < 4; ++j) {
               this.clearCurrentPositionBlocksUpwards(p_74875_1_, j, 6, i, p_74875_3_);
               this.replaceAirAndLiquidDownwards(p_74875_1_, iblockstate, j, -1, i, p_74875_3_);
            }
         }

         this.spawnVillagers(p_74875_1_, p_74875_3_, 1, 1, 2, 1);
         return true;
      }
   }
}
