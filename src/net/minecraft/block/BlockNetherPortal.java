package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockNetherPortal extends Block {
   public static final EnumProperty<EnumFacing.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
   protected static final VoxelShape X_AABB = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
   protected static final VoxelShape Z_AABB = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

   public BlockNetherPortal(Block.Properties p_i48352_1_) {
      super(p_i48352_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(AXIS, EnumFacing.Axis.X));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      switch(p_196244_1_.get(AXIS)) {
      case Z:
         return Z_AABB;
      case X:
      default:
         return X_AABB;
      }
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (p_196267_2_.dimension.isSurfaceWorld() && p_196267_2_.getGameRules().getBoolean("doMobSpawning") && p_196267_4_.nextInt(2000) < p_196267_2_.getDifficulty().getId()) {
         int i = p_196267_3_.getY();

         BlockPos blockpos;
         for(blockpos = p_196267_3_; !p_196267_2_.getBlockState(blockpos).isTopSolid() && blockpos.getY() > 0; blockpos = blockpos.down()) {
         }

         if (i > 0 && !p_196267_2_.getBlockState(blockpos.up()).isNormalCube()) {
            Entity entity = EntityType.ZOMBIE_PIGMAN.spawnEntity(p_196267_2_, null, null,
                    null, blockpos.up(), false, false);
            if (entity != null) {
               entity.timeUntilPortal = entity.getPortalCooldown();
            }
         }
      }

   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public boolean trySpawnPortal(IWorld p_176548_1_, BlockPos p_176548_2_) {
      BlockNetherPortal.Size blockportal$size = this.func_201816_b(p_176548_1_, p_176548_2_);
      if (blockportal$size != null) {
         blockportal$size.placePortalBlocks();
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public BlockNetherPortal.Size func_201816_b(IWorld p_201816_1_, BlockPos p_201816_2_) {
      BlockNetherPortal.Size blockportal$size = new BlockNetherPortal.Size(p_201816_1_, p_201816_2_, EnumFacing.Axis.X);
      if (blockportal$size.isValid() && blockportal$size.portalBlockCount == 0) {
         return blockportal$size;
      } else {
         BlockNetherPortal.Size blockportal$size1 = new BlockNetherPortal.Size(p_201816_1_, p_201816_2_, EnumFacing.Axis.Z);
         return blockportal$size1.isValid() && blockportal$size1.portalBlockCount == 0 ? blockportal$size1 : null;
      }
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      EnumFacing.Axis enumfacing$axis = p_196271_2_.getAxis();
      EnumFacing.Axis enumfacing$axis1 = p_196271_1_.get(AXIS);
      boolean flag = enumfacing$axis1 != enumfacing$axis && enumfacing$axis.isHorizontal();
      return !flag && p_196271_3_.getBlock() != this && !(new BlockNetherPortal.Size(p_196271_4_, p_196271_5_, enumfacing$axis1)).func_208508_f() ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return 0;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public void onEntityCollision(IBlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_4_.isRiding() && !p_196262_4_.isBeingRidden() && p_196262_4_.isNonBoss()) {
         p_196262_4_.setPortal(p_196262_3_,DimensionType.NETHER);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_4_.nextInt(100) == 0) {
         p_180655_2_.playSound((double)p_180655_3_.getX() + 0.5D, (double)p_180655_3_.getY() + 0.5D, (double)p_180655_3_.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, p_180655_4_.nextFloat() * 0.4F + 0.8F, false);
      }

      for(int i = 0; i < 4; ++i) {
         double d0 = (double)((float)p_180655_3_.getX() + p_180655_4_.nextFloat());
         double d1 = (double)((float)p_180655_3_.getY() + p_180655_4_.nextFloat());
         double d2 = (double)((float)p_180655_3_.getZ() + p_180655_4_.nextFloat());
         double d3 = ((double)p_180655_4_.nextFloat() - 0.5D) * 0.5D;
         double d4 = ((double)p_180655_4_.nextFloat() - 0.5D) * 0.5D;
         double d5 = ((double)p_180655_4_.nextFloat() - 0.5D) * 0.5D;
         int j = p_180655_4_.nextInt(2) * 2 - 1;
         if (p_180655_2_.getBlockState(p_180655_3_.west()).getBlock() != this && p_180655_2_.getBlockState(p_180655_3_.east()).getBlock() != this) {
            d0 = (double)p_180655_3_.getX() + 0.5D + 0.25D * (double)j;
            d3 = (double)(p_180655_4_.nextFloat() * 2.0F * (float)j);
         } else {
            d2 = (double)p_180655_3_.getZ() + 0.5D + 0.25D * (double)j;
            d5 = (double)(p_180655_4_.nextFloat() * 2.0F * (float)j);
         }

         p_180655_2_.spawnParticle(Particles.PORTAL, d0, d1, d2, d3, d4, d5);
      }

   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, IBlockState p_185473_3_) {
      return ItemStack.EMPTY;
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      switch(p_185499_2_) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         switch(p_185499_1_.get(AXIS)) {
         case Z:
            return p_185499_1_.with(AXIS, EnumFacing.Axis.X);
         case X:
            return p_185499_1_.with(AXIS, EnumFacing.Axis.Z);
         default:
            return p_185499_1_;
         }
      default:
         return p_185499_1_;
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(AXIS);
   }

   public BlockPattern.PatternHelper createPatternHelper(IWorld p_181089_1_, BlockPos p_181089_2_) {
      EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.Z;
      BlockNetherPortal.Size blockportal$size = new BlockNetherPortal.Size(p_181089_1_, p_181089_2_, EnumFacing.Axis.X);
      LoadingCache<BlockPos, BlockWorldState> loadingcache = BlockPattern.createLoadingCache(p_181089_1_, true);
      if (!blockportal$size.isValid()) {
         enumfacing$axis = EnumFacing.Axis.X;
         blockportal$size = new BlockNetherPortal.Size(p_181089_1_, p_181089_2_, EnumFacing.Axis.Z);
      }

      if (!blockportal$size.isValid()) {
         return new BlockPattern.PatternHelper(p_181089_2_, EnumFacing.NORTH, EnumFacing.UP, loadingcache, 1, 1, 1);
      } else {
         int[] aint = new int[EnumFacing.AxisDirection.values().length];
         EnumFacing enumfacing = blockportal$size.rightDir.rotateYCCW();
         BlockPos blockpos = blockportal$size.bottomLeft.up(blockportal$size.getHeight() - 1);

         for(EnumFacing.AxisDirection enumfacing$axisdirection : EnumFacing.AxisDirection.values()) {
            BlockPattern.PatternHelper blockpattern$patternhelper = new BlockPattern.PatternHelper(enumfacing.getAxisDirection() == enumfacing$axisdirection ? blockpos : blockpos.offset(blockportal$size.rightDir, blockportal$size.getWidth() - 1), EnumFacing.getFacingFromAxis(enumfacing$axisdirection, enumfacing$axis), EnumFacing.UP, loadingcache, blockportal$size.getWidth(), blockportal$size.getHeight(), 1);

            for(int i = 0; i < blockportal$size.getWidth(); ++i) {
               for(int j = 0; j < blockportal$size.getHeight(); ++j) {
                  BlockWorldState blockworldstate = blockpattern$patternhelper.translateOffset(i, j, 1);
                  if (!blockworldstate.getBlockState().isAir()) {
                     ++aint[enumfacing$axisdirection.ordinal()];
                  }
               }
            }
         }

         EnumFacing.AxisDirection enumfacing$axisdirection1 = EnumFacing.AxisDirection.POSITIVE;

         for(EnumFacing.AxisDirection enumfacing$axisdirection2 : EnumFacing.AxisDirection.values()) {
            if (aint[enumfacing$axisdirection2.ordinal()] < aint[enumfacing$axisdirection1.ordinal()]) {
               enumfacing$axisdirection1 = enumfacing$axisdirection2;
            }
         }

         return new BlockPattern.PatternHelper(enumfacing.getAxisDirection() == enumfacing$axisdirection1 ? blockpos : blockpos.offset(blockportal$size.rightDir, blockportal$size.getWidth() - 1), EnumFacing.getFacingFromAxis(enumfacing$axisdirection1, enumfacing$axis), EnumFacing.UP, loadingcache, blockportal$size.getWidth(), blockportal$size.getHeight(), 1);
      }
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   public static class Size {
      final IWorld world;
      final EnumFacing.Axis axis;
      final EnumFacing rightDir;
      final EnumFacing leftDir;
      int portalBlockCount;
      BlockPos bottomLeft;
      int height;
      int width;

      public Size(IWorld p_i48740_1_, BlockPos p_i48740_2_, EnumFacing.Axis p_i48740_3_) {
         this.world = p_i48740_1_;
         this.axis = p_i48740_3_;
         if (p_i48740_3_ == EnumFacing.Axis.X) {
            this.leftDir = EnumFacing.EAST;
            this.rightDir = EnumFacing.WEST;
         } else {
            this.leftDir = EnumFacing.NORTH;
            this.rightDir = EnumFacing.SOUTH;
         }

         for(BlockPos blockpos = p_i48740_2_; p_i48740_2_.getY() > blockpos.getY() - 21 && p_i48740_2_.getY() > 0 && this.func_196900_a(p_i48740_1_.getBlockState(p_i48740_2_.down())); p_i48740_2_ = p_i48740_2_.down()) {
         }

         int i = this.getDistanceUntilEdge(p_i48740_2_, this.leftDir) - 1;
         if (i >= 0) {
            this.bottomLeft = p_i48740_2_.offset(this.leftDir, i);
            this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
            if (this.width < 2 || this.width > 21) {
               this.bottomLeft = null;
               this.width = 0;
            }
         }

         if (this.bottomLeft != null) {
            this.height = this.calculatePortalHeight();
         }

      }

      protected int getDistanceUntilEdge(BlockPos p_180120_1_, EnumFacing p_180120_2_) {
         int i;
         for(i = 0; i < 22; ++i) {
            BlockPos blockpos = p_180120_1_.offset(p_180120_2_, i);
            if (!this.func_196900_a(this.world.getBlockState(blockpos)) || this.world.getBlockState(blockpos.down()).getBlock() != Blocks.OBSIDIAN) {
               break;
            }
         }

         Block block = this.world.getBlockState(p_180120_1_.offset(p_180120_2_, i)).getBlock();
         return block == Blocks.OBSIDIAN ? i : 0;
      }

      public int getHeight() {
         return this.height;
      }

      public int getWidth() {
         return this.width;
      }

      protected int calculatePortalHeight() {
         label56:
         for(this.height = 0; this.height < 21; ++this.height) {
            for(int i = 0; i < this.width; ++i) {
               BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i).up(this.height);
               IBlockState iblockstate = this.world.getBlockState(blockpos);
               if (!this.func_196900_a(iblockstate)) {
                  break label56;
               }

               Block block = iblockstate.getBlock();
               if (block == Blocks.NETHER_PORTAL) {
                  ++this.portalBlockCount;
               }

               if (i == 0) {
                  block = this.world.getBlockState(blockpos.offset(this.leftDir)).getBlock();
                  if (block != Blocks.OBSIDIAN) {
                     break label56;
                  }
               } else if (i == this.width - 1) {
                  block = this.world.getBlockState(blockpos.offset(this.rightDir)).getBlock();
                  if (block != Blocks.OBSIDIAN) {
                     break label56;
                  }
               }
            }
         }

         for(int j = 0; j < this.width; ++j) {
            if (this.world.getBlockState(this.bottomLeft.offset(this.rightDir, j).up(this.height)).getBlock() != Blocks.OBSIDIAN) {
               this.height = 0;
               break;
            }
         }

         if (this.height <= 21 && this.height >= 3) {
            return this.height;
         } else {
            this.bottomLeft = null;
            this.width = 0;
            this.height = 0;
            return 0;
         }
      }

      protected boolean func_196900_a(IBlockState p_196900_1_) {
         Block block = p_196900_1_.getBlock();
         return p_196900_1_.isAir() || block == Blocks.FIRE || block == Blocks.NETHER_PORTAL;
      }

      public boolean isValid() {
         return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
      }

      public void placePortalBlocks() {
         for(int i = 0; i < this.width; ++i) {
            BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i);

            for(int j = 0; j < this.height; ++j) {
               this.world.setBlockState(blockpos.up(j), Blocks.NETHER_PORTAL.getDefaultState().with(BlockNetherPortal.AXIS, this.axis), 18);
            }
         }

      }

      private boolean func_196899_f() {
         return this.portalBlockCount >= this.width * this.height;
      }

      public boolean func_208508_f() {
         return this.isValid() && this.func_196899_f();
      }
   }
}
