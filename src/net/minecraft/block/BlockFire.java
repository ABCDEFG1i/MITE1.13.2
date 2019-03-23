package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.EndDimension;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockFire extends Block {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_15;
   public static final BooleanProperty NORTH = BlockSixWay.NORTH;
   public static final BooleanProperty EAST = BlockSixWay.EAST;
   public static final BooleanProperty SOUTH = BlockSixWay.SOUTH;
   public static final BooleanProperty WEST = BlockSixWay.WEST;
   public static final BooleanProperty UP = BlockSixWay.UP;
   private static final Map<EnumFacing, BooleanProperty> field_196449_B = BlockSixWay.FACING_TO_PROPERTY_MAP.entrySet().stream().filter((p_199776_0_) -> {
      return p_199776_0_.getKey() != EnumFacing.DOWN;
   }).collect(Util.toMapCollector());
   private final Object2IntMap<Block> encouragements = new Object2IntOpenHashMap<>();
   private final Object2IntMap<Block> flammabilities = new Object2IntOpenHashMap<>();

   protected BlockFire(Block.Properties p_i48397_1_) {
      super(p_i48397_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)).with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(UP, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return VoxelShapes.func_197880_a();
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return this.isValidPosition(p_196271_1_, p_196271_4_, p_196271_5_) ? this.getStateForPlacement(p_196271_4_, p_196271_5_).with(AGE, p_196271_1_.get(AGE)) : Blocks.AIR.getDefaultState();
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getStateForPlacement(p_196258_1_.getWorld(), p_196258_1_.getPos());
   }

   public IBlockState getStateForPlacement(IBlockReader p_196448_1_, BlockPos p_196448_2_) {
      IBlockState iblockstate = p_196448_1_.getBlockState(p_196448_2_.down());
      if (!iblockstate.isTopSolid() && !this.func_196446_i(iblockstate)) {
         IBlockState iblockstate1 = this.getDefaultState();

         for(EnumFacing enumfacing : EnumFacing.values()) {
            BooleanProperty booleanproperty = field_196449_B.get(enumfacing);
            if (booleanproperty != null) {
               iblockstate1 = iblockstate1.with(booleanproperty, Boolean.valueOf(this.func_196446_i(p_196448_1_.getBlockState(p_196448_2_.offset(enumfacing)))));
            }
         }

         return iblockstate1;
      } else {
         return this.getDefaultState();
      }
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      return p_196260_2_.getBlockState(p_196260_3_.down()).isTopSolid() || this.func_196447_a(p_196260_2_, p_196260_3_);
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return 0;
   }

   public int tickRate(IWorldReaderBase p_149738_1_) {
      return 30;
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (p_196267_2_.getGameRules().getBoolean("doFireTick")) {
         if (!p_196267_1_.isValidPosition(p_196267_2_, p_196267_3_)) {
            p_196267_2_.removeBlock(p_196267_3_);
         }

         Block block = p_196267_2_.getBlockState(p_196267_3_.down()).getBlock();
         boolean flag = p_196267_2_.dimension instanceof EndDimension && block == Blocks.BEDROCK || block == Blocks.NETHERRACK || block == Blocks.MAGMA_BLOCK;
         int i = p_196267_1_.get(AGE);
         if (!flag && p_196267_2_.isRaining() && this.canDie(p_196267_2_, p_196267_3_) && p_196267_4_.nextFloat() < 0.2F + (float)i * 0.03F) {
            p_196267_2_.removeBlock(p_196267_3_);
         } else {
            int j = Math.min(15, i + p_196267_4_.nextInt(3) / 2);
            if (i != j) {
               p_196267_1_ = p_196267_1_.with(AGE, Integer.valueOf(j));
               p_196267_2_.setBlockState(p_196267_3_, p_196267_1_, 4);
            }

            if (!flag) {
               p_196267_2_.getPendingBlockTicks().scheduleTick(p_196267_3_, this, this.tickRate(p_196267_2_) + p_196267_4_.nextInt(10));
               if (!this.func_196447_a(p_196267_2_, p_196267_3_)) {
                  if (!p_196267_2_.getBlockState(p_196267_3_.down()).isTopSolid() || i > 3) {
                     p_196267_2_.removeBlock(p_196267_3_);
                  }

                  return;
               }

               if (i == 15 && p_196267_4_.nextInt(4) == 0 && !this.func_196446_i(p_196267_2_.getBlockState(p_196267_3_.down()))) {
                  p_196267_2_.removeBlock(p_196267_3_);
                  return;
               }
            }

            boolean flag1 = p_196267_2_.isBlockinHighHumidity(p_196267_3_);
            int k = flag1 ? -50 : 0;
            this.catchOnFire(p_196267_2_, p_196267_3_.east(), 300 + k, p_196267_4_, i);
            this.catchOnFire(p_196267_2_, p_196267_3_.west(), 300 + k, p_196267_4_, i);
            this.catchOnFire(p_196267_2_, p_196267_3_.down(), 250 + k, p_196267_4_, i);
            this.catchOnFire(p_196267_2_, p_196267_3_.up(), 250 + k, p_196267_4_, i);
            this.catchOnFire(p_196267_2_, p_196267_3_.north(), 300 + k, p_196267_4_, i);
            this.catchOnFire(p_196267_2_, p_196267_3_.south(), 300 + k, p_196267_4_, i);
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int l = -1; l <= 1; ++l) {
               for(int i1 = -1; i1 <= 1; ++i1) {
                  for(int j1 = -1; j1 <= 4; ++j1) {
                     if (l != 0 || j1 != 0 || i1 != 0) {
                        int k1 = 100;
                        if (j1 > 1) {
                           k1 += (j1 - 1) * 100;
                        }

                        blockpos$mutableblockpos.setPos(p_196267_3_).move(l, j1, i1);
                        int l1 = this.getNeighborEncouragement(p_196267_2_, blockpos$mutableblockpos);
                        if (l1 > 0) {
                           int i2 = (l1 + 40 + p_196267_2_.getDifficulty().getId() * 7) / (i + 30);
                           if (flag1) {
                              i2 /= 2;
                           }

                           if (i2 > 0 && p_196267_4_.nextInt(k1) <= i2 && (!p_196267_2_.isRaining() || !this.canDie(p_196267_2_, blockpos$mutableblockpos))) {
                              int j2 = Math.min(15, i + p_196267_4_.nextInt(5) / 4);
                              p_196267_2_.setBlockState(blockpos$mutableblockpos, this.getStateForPlacement(p_196267_2_, blockpos$mutableblockpos).with(AGE, Integer.valueOf(j2)), 3);
                           }
                        }
                     }
                  }
               }
            }

         }
      }
   }

   protected boolean canDie(World p_176537_1_, BlockPos p_176537_2_) {
      return p_176537_1_.isRainingAt(p_176537_2_) || p_176537_1_.isRainingAt(p_176537_2_.west()) || p_176537_1_.isRainingAt(p_176537_2_.east()) || p_176537_1_.isRainingAt(p_176537_2_.north()) || p_176537_1_.isRainingAt(p_176537_2_.south());
   }

   public int getFlammability(Block p_176532_1_) {
      return this.flammabilities.getInt(p_176532_1_);
   }

   public int getEncouragement(Block p_176534_1_) {
      return this.encouragements.getInt(p_176534_1_);
   }

   private void catchOnFire(World p_176536_1_, BlockPos p_176536_2_, int p_176536_3_, Random p_176536_4_, int p_176536_5_) {
      int i = this.getFlammability(p_176536_1_.getBlockState(p_176536_2_).getBlock());
      if (p_176536_4_.nextInt(p_176536_3_) < i) {
         IBlockState iblockstate = p_176536_1_.getBlockState(p_176536_2_);
         if (p_176536_4_.nextInt(p_176536_5_ + 10) < 5 && !p_176536_1_.isRainingAt(p_176536_2_)) {
            int j = Math.min(p_176536_5_ + p_176536_4_.nextInt(5) / 4, 15);
            p_176536_1_.setBlockState(p_176536_2_, this.getStateForPlacement(p_176536_1_, p_176536_2_).with(AGE, Integer.valueOf(j)), 3);
         } else {
            p_176536_1_.removeBlock(p_176536_2_);
         }

         Block block = iblockstate.getBlock();
         if (block instanceof BlockTNT) {
            ((BlockTNT)block).explode(p_176536_1_, p_176536_2_);
         }
      }

   }

   private boolean func_196447_a(IBlockReader p_196447_1_, BlockPos p_196447_2_) {
      for(EnumFacing enumfacing : EnumFacing.values()) {
         if (this.func_196446_i(p_196447_1_.getBlockState(p_196447_2_.offset(enumfacing)))) {
            return true;
         }
      }

      return false;
   }

   private int getNeighborEncouragement(IWorldReaderBase p_176538_1_, BlockPos p_176538_2_) {
      if (!p_176538_1_.isAirBlock(p_176538_2_)) {
         return 0;
      } else {
         int i = 0;

         for(EnumFacing enumfacing : EnumFacing.values()) {
            i = Math.max(this.getEncouragement(p_176538_1_.getBlockState(p_176538_2_.offset(enumfacing)).getBlock()), i);
         }

         return i;
      }
   }

   public boolean isCollidable() {
      return false;
   }

   public boolean func_196446_i(IBlockState p_196446_1_) {
      return this.getEncouragement(p_196446_1_.getBlock()) > 0;
   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      if (p_196259_4_.getBlock() != p_196259_1_.getBlock()) {
         if (p_196259_2_.dimension.getType() != DimensionType.OVERWORLD && p_196259_2_.dimension.getType() != DimensionType.NETHER || !((BlockPortal)Blocks.NETHER_PORTAL).trySpawnPortal(p_196259_2_, p_196259_3_)) {
            if (!p_196259_1_.isValidPosition(p_196259_2_, p_196259_3_)) {
               p_196259_2_.removeBlock(p_196259_3_);
            } else {
               p_196259_2_.getPendingBlockTicks().scheduleTick(p_196259_3_, this, this.tickRate(p_196259_2_) + p_196259_2_.rand.nextInt(10));
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_4_.nextInt(24) == 0) {
         p_180655_2_.playSound((double)((float)p_180655_3_.getX() + 0.5F), (double)((float)p_180655_3_.getY() + 0.5F), (double)((float)p_180655_3_.getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + p_180655_4_.nextFloat(), p_180655_4_.nextFloat() * 0.7F + 0.3F, false);
      }

      if (!p_180655_2_.getBlockState(p_180655_3_.down()).isTopSolid() && !this.func_196446_i(p_180655_2_.getBlockState(p_180655_3_.down()))) {
         if (this.func_196446_i(p_180655_2_.getBlockState(p_180655_3_.west()))) {
            for(int j = 0; j < 2; ++j) {
               double d3 = (double)p_180655_3_.getX() + p_180655_4_.nextDouble() * (double)0.1F;
               double d8 = (double)p_180655_3_.getY() + p_180655_4_.nextDouble();
               double d13 = (double)p_180655_3_.getZ() + p_180655_4_.nextDouble();
               p_180655_2_.spawnParticle(Particles.LARGE_SMOKE, d3, d8, d13, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.func_196446_i(p_180655_2_.getBlockState(p_180655_3_.east()))) {
            for(int k = 0; k < 2; ++k) {
               double d4 = (double)(p_180655_3_.getX() + 1) - p_180655_4_.nextDouble() * (double)0.1F;
               double d9 = (double)p_180655_3_.getY() + p_180655_4_.nextDouble();
               double d14 = (double)p_180655_3_.getZ() + p_180655_4_.nextDouble();
               p_180655_2_.spawnParticle(Particles.LARGE_SMOKE, d4, d9, d14, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.func_196446_i(p_180655_2_.getBlockState(p_180655_3_.north()))) {
            for(int l = 0; l < 2; ++l) {
               double d5 = (double)p_180655_3_.getX() + p_180655_4_.nextDouble();
               double d10 = (double)p_180655_3_.getY() + p_180655_4_.nextDouble();
               double d15 = (double)p_180655_3_.getZ() + p_180655_4_.nextDouble() * (double)0.1F;
               p_180655_2_.spawnParticle(Particles.LARGE_SMOKE, d5, d10, d15, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.func_196446_i(p_180655_2_.getBlockState(p_180655_3_.south()))) {
            for(int i1 = 0; i1 < 2; ++i1) {
               double d6 = (double)p_180655_3_.getX() + p_180655_4_.nextDouble();
               double d11 = (double)p_180655_3_.getY() + p_180655_4_.nextDouble();
               double d16 = (double)(p_180655_3_.getZ() + 1) - p_180655_4_.nextDouble() * (double)0.1F;
               p_180655_2_.spawnParticle(Particles.LARGE_SMOKE, d6, d11, d16, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.func_196446_i(p_180655_2_.getBlockState(p_180655_3_.up()))) {
            for(int j1 = 0; j1 < 2; ++j1) {
               double d7 = (double)p_180655_3_.getX() + p_180655_4_.nextDouble();
               double d12 = (double)(p_180655_3_.getY() + 1) - p_180655_4_.nextDouble() * (double)0.1F;
               double d17 = (double)p_180655_3_.getZ() + p_180655_4_.nextDouble();
               p_180655_2_.spawnParticle(Particles.LARGE_SMOKE, d7, d12, d17, 0.0D, 0.0D, 0.0D);
            }
         }
      } else {
         for(int i = 0; i < 3; ++i) {
            double d0 = (double)p_180655_3_.getX() + p_180655_4_.nextDouble();
            double d1 = (double)p_180655_3_.getY() + p_180655_4_.nextDouble() * 0.5D + 0.5D;
            double d2 = (double)p_180655_3_.getZ() + p_180655_4_.nextDouble();
            p_180655_2_.spawnParticle(Particles.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(AGE, NORTH, EAST, SOUTH, WEST, UP);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   public void setFireInfo(Block p_180686_1_, int p_180686_2_, int p_180686_3_) {
      this.encouragements.put(p_180686_1_, p_180686_2_);
      this.flammabilities.put(p_180686_1_, p_180686_3_);
   }

   public static void init() {
      BlockFire blockfire = (BlockFire)Blocks.FIRE;
      blockfire.setFireInfo(Blocks.OAK_PLANKS, 5, 20);
      blockfire.setFireInfo(Blocks.SPRUCE_PLANKS, 5, 20);
      blockfire.setFireInfo(Blocks.BIRCH_PLANKS, 5, 20);
      blockfire.setFireInfo(Blocks.JUNGLE_PLANKS, 5, 20);
      blockfire.setFireInfo(Blocks.ACACIA_PLANKS, 5, 20);
      blockfire.setFireInfo(Blocks.DARK_OAK_PLANKS, 5, 20);
      blockfire.setFireInfo(Blocks.OAK_SLAB, 5, 20);
      blockfire.setFireInfo(Blocks.SPRUCE_SLAB, 5, 20);
      blockfire.setFireInfo(Blocks.BIRCH_SLAB, 5, 20);
      blockfire.setFireInfo(Blocks.JUNGLE_SLAB, 5, 20);
      blockfire.setFireInfo(Blocks.ACACIA_SLAB, 5, 20);
      blockfire.setFireInfo(Blocks.DARK_OAK_SLAB, 5, 20);
      blockfire.setFireInfo(Blocks.OAK_FENCE_GATE, 5, 20);
      blockfire.setFireInfo(Blocks.SPRUCE_FENCE_GATE, 5, 20);
      blockfire.setFireInfo(Blocks.BIRCH_FENCE_GATE, 5, 20);
      blockfire.setFireInfo(Blocks.JUNGLE_FENCE_GATE, 5, 20);
      blockfire.setFireInfo(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
      blockfire.setFireInfo(Blocks.ACACIA_FENCE_GATE, 5, 20);
      blockfire.setFireInfo(Blocks.OAK_FENCE, 5, 20);
      blockfire.setFireInfo(Blocks.SPRUCE_FENCE, 5, 20);
      blockfire.setFireInfo(Blocks.BIRCH_FENCE, 5, 20);
      blockfire.setFireInfo(Blocks.JUNGLE_FENCE, 5, 20);
      blockfire.setFireInfo(Blocks.DARK_OAK_FENCE, 5, 20);
      blockfire.setFireInfo(Blocks.ACACIA_FENCE, 5, 20);
      blockfire.setFireInfo(Blocks.OAK_STAIRS, 5, 20);
      blockfire.setFireInfo(Blocks.BIRCH_STAIRS, 5, 20);
      blockfire.setFireInfo(Blocks.SPRUCE_STAIRS, 5, 20);
      blockfire.setFireInfo(Blocks.JUNGLE_STAIRS, 5, 20);
      blockfire.setFireInfo(Blocks.ACACIA_STAIRS, 5, 20);
      blockfire.setFireInfo(Blocks.DARK_OAK_STAIRS, 5, 20);
      blockfire.setFireInfo(Blocks.OAK_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.SPRUCE_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.BIRCH_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.JUNGLE_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.ACACIA_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.DARK_OAK_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_OAK_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_SPRUCE_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_BIRCH_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_JUNGLE_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_ACACIA_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_DARK_OAK_LOG, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_OAK_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_SPRUCE_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_BIRCH_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_JUNGLE_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_ACACIA_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.STRIPPED_DARK_OAK_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.OAK_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.SPRUCE_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.BIRCH_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.JUNGLE_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.ACACIA_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.DARK_OAK_WOOD, 5, 5);
      blockfire.setFireInfo(Blocks.OAK_LEAVES, 30, 60);
      blockfire.setFireInfo(Blocks.SPRUCE_LEAVES, 30, 60);
      blockfire.setFireInfo(Blocks.BIRCH_LEAVES, 30, 60);
      blockfire.setFireInfo(Blocks.JUNGLE_LEAVES, 30, 60);
      blockfire.setFireInfo(Blocks.ACACIA_LEAVES, 30, 60);
      blockfire.setFireInfo(Blocks.DARK_OAK_LEAVES, 30, 60);
      blockfire.setFireInfo(Blocks.BOOKSHELF, 30, 20);
      blockfire.setFireInfo(Blocks.TNT, 15, 100);
      blockfire.setFireInfo(Blocks.GRASS, 60, 100);
      blockfire.setFireInfo(Blocks.FERN, 60, 100);
      blockfire.setFireInfo(Blocks.DEAD_BUSH, 60, 100);
      blockfire.setFireInfo(Blocks.SUNFLOWER, 60, 100);
      blockfire.setFireInfo(Blocks.LILAC, 60, 100);
      blockfire.setFireInfo(Blocks.ROSE_BUSH, 60, 100);
      blockfire.setFireInfo(Blocks.PEONY, 60, 100);
      blockfire.setFireInfo(Blocks.TALL_GRASS, 60, 100);
      blockfire.setFireInfo(Blocks.LARGE_FERN, 60, 100);
      blockfire.setFireInfo(Blocks.DANDELION, 60, 100);
      blockfire.setFireInfo(Blocks.POPPY, 60, 100);
      blockfire.setFireInfo(Blocks.BLUE_ORCHID, 60, 100);
      blockfire.setFireInfo(Blocks.ALLIUM, 60, 100);
      blockfire.setFireInfo(Blocks.AZURE_BLUET, 60, 100);
      blockfire.setFireInfo(Blocks.RED_TULIP, 60, 100);
      blockfire.setFireInfo(Blocks.ORANGE_TULIP, 60, 100);
      blockfire.setFireInfo(Blocks.WHITE_TULIP, 60, 100);
      blockfire.setFireInfo(Blocks.PINK_TULIP, 60, 100);
      blockfire.setFireInfo(Blocks.OXEYE_DAISY, 60, 100);
      blockfire.setFireInfo(Blocks.WHITE_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.ORANGE_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.MAGENTA_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.LIGHT_BLUE_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.YELLOW_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.LIME_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.PINK_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.GRAY_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.LIGHT_GRAY_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.CYAN_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.PURPLE_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.BLUE_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.BROWN_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.GREEN_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.RED_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.BLACK_WOOL, 30, 60);
      blockfire.setFireInfo(Blocks.VINE, 15, 100);
      blockfire.setFireInfo(Blocks.COAL_BLOCK, 5, 5);
      blockfire.setFireInfo(Blocks.HAY_BLOCK, 60, 20);
      blockfire.setFireInfo(Blocks.WHITE_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.ORANGE_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.MAGENTA_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.LIGHT_BLUE_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.YELLOW_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.LIME_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.PINK_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.GRAY_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.LIGHT_GRAY_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.CYAN_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.PURPLE_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.BLUE_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.BROWN_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.GREEN_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.RED_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.BLACK_CARPET, 60, 20);
      blockfire.setFireInfo(Blocks.DRIED_KELP_BLOCK, 30, 60);
   }
}
