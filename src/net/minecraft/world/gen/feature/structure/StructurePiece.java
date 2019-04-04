package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class StructurePiece {
   protected static final IBlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
   protected MutableBoundingBox boundingBox;
   @Nullable
   private EnumFacing coordBaseMode;
   private Mirror mirror;
   private Rotation rotation;
   protected int componentType;
   private static final Set<Block> BLOCKS_NEEDING_POSTPROCESSING = ImmutableSet.<Block>builder().add(Blocks.NETHER_BRICK_FENCE).add(Blocks.TORCH).add(Blocks.WALL_TORCH).add(Blocks.OAK_FENCE).add(Blocks.SPRUCE_FENCE).add(Blocks.DARK_OAK_FENCE).add(Blocks.ACACIA_FENCE).add(Blocks.BIRCH_FENCE).add(Blocks.JUNGLE_FENCE).add(Blocks.LADDER).add(Blocks.IRON_BARS).build();

   public StructurePiece() {
   }

   protected StructurePiece(int p_i2091_1_) {
      this.componentType = p_i2091_1_;
   }

   public final NBTTagCompound createStructureBaseNBT() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setString("id", StructureIO.getStructureComponentName(this));
      nbttagcompound.setTag("BB", this.boundingBox.toNBTTagIntArray());
      EnumFacing enumfacing = this.getCoordBaseMode();
      nbttagcompound.setInteger("O", enumfacing == null ? -1 : enumfacing.getHorizontalIndex());
      nbttagcompound.setInteger("GD", this.componentType);
      this.writeStructureToNBT(nbttagcompound);
      return nbttagcompound;
   }

   protected abstract void writeStructureToNBT(NBTTagCompound p_143012_1_);

   public void readStructureBaseNBT(IWorld p_143009_1_, NBTTagCompound p_143009_2_) {
      if (p_143009_2_.hasKey("BB")) {
         this.boundingBox = new MutableBoundingBox(p_143009_2_.getIntArray("BB"));
      }

      int i = p_143009_2_.getInteger("O");
      this.setCoordBaseMode(i == -1 ? null : EnumFacing.byHorizontalIndex(i));
      this.componentType = p_143009_2_.getInteger("GD");
      this.readStructureFromNBT(p_143009_2_, p_143009_1_.getSaveHandler().getStructureTemplateManager());
   }

   protected abstract void readStructureFromNBT(NBTTagCompound p_143011_1_, TemplateManager p_143011_2_);

   public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
   }

   public abstract boolean addComponentParts(IWorld p_74875_1_, Random p_74875_2_, MutableBoundingBox p_74875_3_, ChunkPos p_74875_4_);

   public MutableBoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public int getComponentType() {
      return this.componentType;
   }

   public static StructurePiece findIntersecting(List<StructurePiece> p_74883_0_, MutableBoundingBox p_74883_1_) {
      for(StructurePiece structurepiece : p_74883_0_) {
         if (structurepiece.getBoundingBox() != null && structurepiece.getBoundingBox().intersectsWith(p_74883_1_)) {
            return structurepiece;
         }
      }

      return null;
   }

   protected boolean isLiquidInStructureBoundingBox(IBlockReader p_74860_1_, MutableBoundingBox p_74860_2_) {
      int i = Math.max(this.boundingBox.minX - 1, p_74860_2_.minX);
      int j = Math.max(this.boundingBox.minY - 1, p_74860_2_.minY);
      int k = Math.max(this.boundingBox.minZ - 1, p_74860_2_.minZ);
      int l = Math.min(this.boundingBox.maxX + 1, p_74860_2_.maxX);
      int i1 = Math.min(this.boundingBox.maxY + 1, p_74860_2_.maxY);
      int j1 = Math.min(this.boundingBox.maxZ + 1, p_74860_2_.maxZ);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int k1 = i; k1 <= l; ++k1) {
         for(int l1 = k; l1 <= j1; ++l1) {
            if (p_74860_1_.getBlockState(blockpos$mutableblockpos.setPos(k1, j, l1)).getMaterial().isLiquid()) {
               return true;
            }

            if (p_74860_1_.getBlockState(blockpos$mutableblockpos.setPos(k1, i1, l1)).getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for(int i2 = i; i2 <= l; ++i2) {
         for(int k2 = j; k2 <= i1; ++k2) {
            if (p_74860_1_.getBlockState(blockpos$mutableblockpos.setPos(i2, k2, k)).getMaterial().isLiquid()) {
               return true;
            }

            if (p_74860_1_.getBlockState(blockpos$mutableblockpos.setPos(i2, k2, j1)).getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for(int j2 = k; j2 <= j1; ++j2) {
         for(int l2 = j; l2 <= i1; ++l2) {
            if (p_74860_1_.getBlockState(blockpos$mutableblockpos.setPos(i, l2, j2)).getMaterial().isLiquid()) {
               return true;
            }

            if (p_74860_1_.getBlockState(blockpos$mutableblockpos.setPos(l, l2, j2)).getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      return false;
   }

   protected int getXWithOffset(int p_74865_1_, int p_74865_2_) {
      EnumFacing enumfacing = this.getCoordBaseMode();
      if (enumfacing == null) {
         return p_74865_1_;
      } else {
         switch(enumfacing) {
         case NORTH:
         case SOUTH:
            return this.boundingBox.minX + p_74865_1_;
         case WEST:
            return this.boundingBox.maxX - p_74865_2_;
         case EAST:
            return this.boundingBox.minX + p_74865_2_;
         default:
            return p_74865_1_;
         }
      }
   }

   protected int getYWithOffset(int p_74862_1_) {
      return this.getCoordBaseMode() == null ? p_74862_1_ : p_74862_1_ + this.boundingBox.minY;
   }

   protected int getZWithOffset(int p_74873_1_, int p_74873_2_) {
      EnumFacing enumfacing = this.getCoordBaseMode();
      if (enumfacing == null) {
         return p_74873_2_;
      } else {
         switch(enumfacing) {
         case NORTH:
            return this.boundingBox.maxZ - p_74873_2_;
         case SOUTH:
            return this.boundingBox.minZ + p_74873_2_;
         case WEST:
         case EAST:
            return this.boundingBox.minZ + p_74873_1_;
         default:
            return p_74873_2_;
         }
      }
   }

   protected void setBlockState(IWorld p_175811_1_, IBlockState p_175811_2_, int p_175811_3_, int p_175811_4_, int p_175811_5_, MutableBoundingBox p_175811_6_) {
      BlockPos blockpos = new BlockPos(this.getXWithOffset(p_175811_3_, p_175811_5_), this.getYWithOffset(p_175811_4_), this.getZWithOffset(p_175811_3_, p_175811_5_));
      if (p_175811_6_.isVecInside(blockpos)) {
         if (this.mirror != Mirror.NONE) {
            p_175811_2_ = p_175811_2_.mirror(this.mirror);
         }

         if (this.rotation != Rotation.NONE) {
            p_175811_2_ = p_175811_2_.rotate(this.rotation);
         }

         p_175811_1_.setBlockState(blockpos, p_175811_2_, 2);
         IFluidState ifluidstate = p_175811_1_.getFluidState(blockpos);
         if (!ifluidstate.isEmpty()) {
            p_175811_1_.getPendingFluidTicks().scheduleTick(blockpos, ifluidstate.getFluid(), 0);
         }

         if (BLOCKS_NEEDING_POSTPROCESSING.contains(p_175811_2_.getBlock())) {
            p_175811_1_.getChunkDefault(blockpos).markBlockForPostprocessing(blockpos);
         }

      }
   }

   protected IBlockState getBlockStateFromPos(IBlockReader p_175807_1_, int p_175807_2_, int p_175807_3_, int p_175807_4_, MutableBoundingBox p_175807_5_) {
      int i = this.getXWithOffset(p_175807_2_, p_175807_4_);
      int j = this.getYWithOffset(p_175807_3_);
      int k = this.getZWithOffset(p_175807_2_, p_175807_4_);
      BlockPos blockpos = new BlockPos(i, j, k);
      return !p_175807_5_.isVecInside(blockpos) ? Blocks.AIR.getDefaultState() : p_175807_1_.getBlockState(blockpos);
   }

   protected boolean getSkyBrightness(IWorldReaderBase p_189916_1_, int p_189916_2_, int p_189916_3_, int p_189916_4_, MutableBoundingBox p_189916_5_) {
      int i = this.getXWithOffset(p_189916_2_, p_189916_4_);
      int j = this.getYWithOffset(p_189916_3_ + 1);
      int k = this.getZWithOffset(p_189916_2_, p_189916_4_);
      BlockPos blockpos = new BlockPos(i, j, k);
      if (!p_189916_5_.isVecInside(blockpos)) {
         return false;
      } else {
         return j < p_189916_1_.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, i, k);
      }
   }

   protected void fillWithAir(IWorld p_74878_1_, MutableBoundingBox p_74878_2_, int p_74878_3_, int p_74878_4_, int p_74878_5_, int p_74878_6_, int p_74878_7_, int p_74878_8_) {
      for(int i = p_74878_4_; i <= p_74878_7_; ++i) {
         for(int j = p_74878_3_; j <= p_74878_6_; ++j) {
            for(int k = p_74878_5_; k <= p_74878_8_; ++k) {
               this.setBlockState(p_74878_1_, Blocks.AIR.getDefaultState(), j, i, k, p_74878_2_);
            }
         }
      }

   }

   protected void fillWithBlocks(IWorld p_175804_1_, MutableBoundingBox p_175804_2_, int p_175804_3_, int p_175804_4_, int p_175804_5_, int p_175804_6_, int p_175804_7_, int p_175804_8_, IBlockState p_175804_9_, IBlockState p_175804_10_, boolean p_175804_11_) {
      for(int i = p_175804_4_; i <= p_175804_7_; ++i) {
         for(int j = p_175804_3_; j <= p_175804_6_; ++j) {
            for(int k = p_175804_5_; k <= p_175804_8_; ++k) {
               if (!p_175804_11_ || !this.getBlockStateFromPos(p_175804_1_, j, i, k, p_175804_2_).isAir()) {
                  if (i != p_175804_4_ && i != p_175804_7_ && j != p_175804_3_ && j != p_175804_6_ && k != p_175804_5_ && k != p_175804_8_) {
                     this.setBlockState(p_175804_1_, p_175804_10_, j, i, k, p_175804_2_);
                  } else {
                     this.setBlockState(p_175804_1_, p_175804_9_, j, i, k, p_175804_2_);
                  }
               }
            }
         }
      }

   }

   protected void fillWithRandomizedBlocks(IWorld p_74882_1_, MutableBoundingBox p_74882_2_, int p_74882_3_, int p_74882_4_, int p_74882_5_, int p_74882_6_, int p_74882_7_, int p_74882_8_, boolean p_74882_9_, Random p_74882_10_, StructurePiece.BlockSelector p_74882_11_) {
      for(int i = p_74882_4_; i <= p_74882_7_; ++i) {
         for(int j = p_74882_3_; j <= p_74882_6_; ++j) {
            for(int k = p_74882_5_; k <= p_74882_8_; ++k) {
               if (!p_74882_9_ || !this.getBlockStateFromPos(p_74882_1_, j, i, k, p_74882_2_).isAir()) {
                  p_74882_11_.selectBlocks(p_74882_10_, j, i, k, i == p_74882_4_ || i == p_74882_7_ || j == p_74882_3_ || j == p_74882_6_ || k == p_74882_5_ || k == p_74882_8_);
                  this.setBlockState(p_74882_1_, p_74882_11_.getBlockState(), j, i, k, p_74882_2_);
               }
            }
         }
      }

   }

   protected void generateMaybeBox(IWorld p_189914_1_, MutableBoundingBox p_189914_2_, Random p_189914_3_, float p_189914_4_, int p_189914_5_, int p_189914_6_, int p_189914_7_, int p_189914_8_, int p_189914_9_, int p_189914_10_, IBlockState p_189914_11_, IBlockState p_189914_12_, boolean p_189914_13_, boolean p_189914_14_) {
      for(int i = p_189914_6_; i <= p_189914_9_; ++i) {
         for(int j = p_189914_5_; j <= p_189914_8_; ++j) {
            for(int k = p_189914_7_; k <= p_189914_10_; ++k) {
               if (!(p_189914_3_.nextFloat() > p_189914_4_) && (!p_189914_13_ || !this.getBlockStateFromPos(p_189914_1_, j, i, k, p_189914_2_).isAir()) && (!p_189914_14_ || this.getSkyBrightness(p_189914_1_, j, i, k, p_189914_2_))) {
                  if (i != p_189914_6_ && i != p_189914_9_ && j != p_189914_5_ && j != p_189914_8_ && k != p_189914_7_ && k != p_189914_10_) {
                     this.setBlockState(p_189914_1_, p_189914_12_, j, i, k, p_189914_2_);
                  } else {
                     this.setBlockState(p_189914_1_, p_189914_11_, j, i, k, p_189914_2_);
                  }
               }
            }
         }
      }

   }

   protected void randomlyPlaceBlock(IWorld p_175809_1_, MutableBoundingBox p_175809_2_, Random p_175809_3_, float p_175809_4_, int p_175809_5_, int p_175809_6_, int p_175809_7_, IBlockState p_175809_8_) {
      if (p_175809_3_.nextFloat() < p_175809_4_) {
         this.setBlockState(p_175809_1_, p_175809_8_, p_175809_5_, p_175809_6_, p_175809_7_, p_175809_2_);
      }

   }

   protected void randomlyRareFillWithBlocks(IWorld p_180777_1_, MutableBoundingBox p_180777_2_, int p_180777_3_, int p_180777_4_, int p_180777_5_, int p_180777_6_, int p_180777_7_, int p_180777_8_, IBlockState p_180777_9_, boolean p_180777_10_) {
      float f = (float)(p_180777_6_ - p_180777_3_ + 1);
      float f1 = (float)(p_180777_7_ - p_180777_4_ + 1);
      float f2 = (float)(p_180777_8_ - p_180777_5_ + 1);
      float f3 = (float)p_180777_3_ + f / 2.0F;
      float f4 = (float)p_180777_5_ + f2 / 2.0F;

      for(int i = p_180777_4_; i <= p_180777_7_; ++i) {
         float f5 = (float)(i - p_180777_4_) / f1;

         for(int j = p_180777_3_; j <= p_180777_6_; ++j) {
            float f6 = ((float)j - f3) / (f * 0.5F);

            for(int k = p_180777_5_; k <= p_180777_8_; ++k) {
               float f7 = ((float)k - f4) / (f2 * 0.5F);
               if (!p_180777_10_ || !this.getBlockStateFromPos(p_180777_1_, j, i, k, p_180777_2_).isAir()) {
                  float f8 = f6 * f6 + f5 * f5 + f7 * f7;
                  if (f8 <= 1.05F) {
                     this.setBlockState(p_180777_1_, p_180777_9_, j, i, k, p_180777_2_);
                  }
               }
            }
         }
      }

   }

   protected void clearCurrentPositionBlocksUpwards(IWorld p_74871_1_, int p_74871_2_, int p_74871_3_, int p_74871_4_, MutableBoundingBox p_74871_5_) {
      BlockPos blockpos = new BlockPos(this.getXWithOffset(p_74871_2_, p_74871_4_), this.getYWithOffset(p_74871_3_), this.getZWithOffset(p_74871_2_, p_74871_4_));
      if (p_74871_5_.isVecInside(blockpos)) {
         while(!p_74871_1_.isAirBlock(blockpos) && blockpos.getY() < 255) {
            p_74871_1_.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 2);
            blockpos = blockpos.up();
         }

      }
   }

   protected void replaceAirAndLiquidDownwards(IWorld p_175808_1_, IBlockState p_175808_2_, int p_175808_3_, int p_175808_4_, int p_175808_5_, MutableBoundingBox p_175808_6_) {
      int i = this.getXWithOffset(p_175808_3_, p_175808_5_);
      int j = this.getYWithOffset(p_175808_4_);
      int k = this.getZWithOffset(p_175808_3_, p_175808_5_);
      if (p_175808_6_.isVecInside(new BlockPos(i, j, k))) {
         while((p_175808_1_.isAirBlock(new BlockPos(i, j, k)) || p_175808_1_.getBlockState(new BlockPos(i, j, k)).getMaterial().isLiquid()) && j > 1) {
            p_175808_1_.setBlockState(new BlockPos(i, j, k), p_175808_2_, 2);
            --j;
         }

      }
   }

   protected boolean generateChest(IWorld p_186167_1_, MutableBoundingBox p_186167_2_, Random p_186167_3_, int p_186167_4_, int p_186167_5_, int p_186167_6_, ResourceLocation p_186167_7_) {
      BlockPos blockpos = new BlockPos(this.getXWithOffset(p_186167_4_, p_186167_6_), this.getYWithOffset(p_186167_5_), this.getZWithOffset(p_186167_4_, p_186167_6_));
      return this.generateChest(p_186167_1_, p_186167_2_, p_186167_3_, blockpos, p_186167_7_, null);
   }

   public static IBlockState func_197528_a(IBlockReader p_197528_0_, BlockPos p_197528_1_, IBlockState p_197528_2_) {
      EnumFacing enumfacing = null;

      for(EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockpos = p_197528_1_.offset(enumfacing1);
         IBlockState iblockstate = p_197528_0_.getBlockState(blockpos);
         if (iblockstate.getBlock() == Blocks.CHEST) {
            return p_197528_2_;
         }

         if (iblockstate.isOpaqueCube(p_197528_0_, blockpos)) {
            if (enumfacing != null) {
               enumfacing = null;
               break;
            }

            enumfacing = enumfacing1;
         }
      }

      if (enumfacing != null) {
         return p_197528_2_.with(BlockHorizontal.HORIZONTAL_FACING, enumfacing.getOpposite());
      } else {
         EnumFacing enumfacing2 = p_197528_2_.get(BlockHorizontal.HORIZONTAL_FACING);
         BlockPos blockpos1 = p_197528_1_.offset(enumfacing2);
         if (p_197528_0_.getBlockState(blockpos1).isOpaqueCube(p_197528_0_, blockpos1)) {
            enumfacing2 = enumfacing2.getOpposite();
            blockpos1 = p_197528_1_.offset(enumfacing2);
         }

         if (p_197528_0_.getBlockState(blockpos1).isOpaqueCube(p_197528_0_, blockpos1)) {
            enumfacing2 = enumfacing2.rotateY();
            blockpos1 = p_197528_1_.offset(enumfacing2);
         }

         if (p_197528_0_.getBlockState(blockpos1).isOpaqueCube(p_197528_0_, blockpos1)) {
            enumfacing2 = enumfacing2.getOpposite();
            p_197528_1_.offset(enumfacing2);
         }

         return p_197528_2_.with(BlockHorizontal.HORIZONTAL_FACING, enumfacing2);
      }
   }

   protected boolean generateChest(IWorld p_191080_1_, MutableBoundingBox p_191080_2_, Random p_191080_3_, BlockPos p_191080_4_, ResourceLocation p_191080_5_, @Nullable IBlockState p_191080_6_) {
      if (p_191080_2_.isVecInside(p_191080_4_) && p_191080_1_.getBlockState(p_191080_4_).getBlock() != Blocks.CHEST) {
         if (p_191080_6_ == null) {
            p_191080_6_ = func_197528_a(p_191080_1_, p_191080_4_, Blocks.CHEST.getDefaultState());
         }

         p_191080_1_.setBlockState(p_191080_4_, p_191080_6_, 2);
         TileEntity tileentity = p_191080_1_.getTileEntity(p_191080_4_);
         if (tileentity instanceof TileEntityChest) {
            ((TileEntityChest)tileentity).setLootTable(p_191080_5_, p_191080_3_.nextLong());
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean createDispenser(IWorld p_189419_1_, MutableBoundingBox p_189419_2_, Random p_189419_3_, int p_189419_4_, int p_189419_5_, int p_189419_6_, EnumFacing p_189419_7_, ResourceLocation p_189419_8_) {
      BlockPos blockpos = new BlockPos(this.getXWithOffset(p_189419_4_, p_189419_6_), this.getYWithOffset(p_189419_5_), this.getZWithOffset(p_189419_4_, p_189419_6_));
      if (p_189419_2_.isVecInside(blockpos) && p_189419_1_.getBlockState(blockpos).getBlock() != Blocks.DISPENSER) {
         this.setBlockState(p_189419_1_, Blocks.DISPENSER.getDefaultState().with(BlockDispenser.FACING, p_189419_7_), p_189419_4_, p_189419_5_, p_189419_6_, p_189419_2_);
         TileEntity tileentity = p_189419_1_.getTileEntity(blockpos);
         if (tileentity instanceof TileEntityDispenser) {
            ((TileEntityDispenser)tileentity).setLootTable(p_189419_8_, p_189419_3_.nextLong());
         }

         return true;
      } else {
         return false;
      }
   }

   protected void generateDoor(IWorld p_189915_1_, MutableBoundingBox p_189915_2_, Random p_189915_3_, int p_189915_4_, int p_189915_5_, int p_189915_6_, EnumFacing p_189915_7_, BlockDoor p_189915_8_) {
      this.setBlockState(p_189915_1_, p_189915_8_.getDefaultState().with(BlockDoor.FACING, p_189915_7_), p_189915_4_, p_189915_5_, p_189915_6_, p_189915_2_);
      this.setBlockState(p_189915_1_, p_189915_8_.getDefaultState().with(BlockDoor.FACING, p_189915_7_).with(BlockDoor.HALF, DoubleBlockHalf.UPPER), p_189915_4_, p_189915_5_ + 1, p_189915_6_, p_189915_2_);
   }

   public void offset(int p_181138_1_, int p_181138_2_, int p_181138_3_) {
      this.boundingBox.offset(p_181138_1_, p_181138_2_, p_181138_3_);
   }

   @Nullable
   public EnumFacing getCoordBaseMode() {
      return this.coordBaseMode;
   }

   public void setCoordBaseMode(@Nullable EnumFacing p_186164_1_) {
      this.coordBaseMode = p_186164_1_;
      if (p_186164_1_ == null) {
         this.rotation = Rotation.NONE;
         this.mirror = Mirror.NONE;
      } else {
         switch(p_186164_1_) {
         case SOUTH:
            this.mirror = Mirror.LEFT_RIGHT;
            this.rotation = Rotation.NONE;
            break;
         case WEST:
            this.mirror = Mirror.LEFT_RIGHT;
            this.rotation = Rotation.CLOCKWISE_90;
            break;
         case EAST:
            this.mirror = Mirror.NONE;
            this.rotation = Rotation.CLOCKWISE_90;
            break;
         default:
            this.mirror = Mirror.NONE;
            this.rotation = Rotation.NONE;
         }
      }

   }

   public abstract static class BlockSelector {
      protected IBlockState blockstate = Blocks.AIR.getDefaultState();

      public abstract void selectBlocks(Random p_75062_1_, int p_75062_2_, int p_75062_3_, int p_75062_4_, boolean p_75062_5_);

      public IBlockState getBlockState() {
         return this.blockstate;
      }
   }
}
