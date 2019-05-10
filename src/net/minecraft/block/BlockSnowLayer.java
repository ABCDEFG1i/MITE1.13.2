package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.*;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockSnowLayer extends Block {
   public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS_1_8;
   protected static final VoxelShape[] SHAPES = new VoxelShape[]{VoxelShapes.func_197880_a(), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

   protected BlockSnowLayer(Block.Properties p_i48328_1_) {
      super(p_i48328_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(LAYERS, 1));
   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      if (p_196266_4_ == PathType.LAND) {
         return p_196266_1_.get(LAYERS) < 5;
      }
      return false;
   }

   @Override
   public float getBlockHardness(IBlockState p_176195_1_, IBlockReader p_176195_2_, BlockPos p_176195_3_) {
      return p_176195_1_.get(LAYERS)*0.4F;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return p_149686_1_.get(LAYERS) == 8;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return p_193383_4_ == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPES[p_196244_1_.get(LAYERS)];
   }

   public VoxelShape getCollisionShape(IBlockState p_196268_1_, IBlockReader p_196268_2_, BlockPos p_196268_3_) {
      return SHAPES[p_196268_1_.get(LAYERS) - 1];
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      IBlockState iblockstate = p_196260_2_.getBlockState(p_196260_3_.down());
      Block block = iblockstate.getBlock();
      if (block != Blocks.ICE && block != Blocks.PACKED_ICE && block != Blocks.BARRIER) {
         BlockFaceShape blockfaceshape = iblockstate.getBlockFaceShape(p_196260_2_, p_196260_3_.down(), EnumFacing.UP);
         return blockfaceshape == BlockFaceShape.SOLID || iblockstate.isIn(BlockTags.LEAVES) || block == this && iblockstate.get(LAYERS) == 8;
      } else {
         return false;
      }
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public void harvestBlock(World p_180657_1_, EntityPlayer p_180657_2_, BlockPos p_180657_3_, IBlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      Integer integer = p_180657_4_.get(LAYERS);
      if (this.canSilkHarvest() && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, p_180657_6_) > 0) {
         if (integer == 8) {
            spawnAsEntity(p_180657_1_, p_180657_3_, new ItemStack(Blocks.SNOW_BLOCK));
         } else {
            for(int i = 0; i < integer; ++i) {
               spawnAsEntity(p_180657_1_, p_180657_3_, this.getSilkTouchDrop(p_180657_4_));
            }
         }
      } else {
         spawnAsEntity(p_180657_1_, p_180657_3_, new ItemStack(Items.SNOWBALL, integer));
      }

      p_180657_1_.removeBlock(p_180657_3_);
      p_180657_2_.func_71029_a(StatList.BLOCK_MINED.func_199076_b(this));
      p_180657_2_.addExhaustion(0.005F);
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Items.AIR;
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (p_196267_2_.getLightFor(EnumLightType.BLOCK, p_196267_3_) > 11) {
         p_196267_1_.dropBlockAsItem(p_196267_2_, p_196267_3_, 0);
         p_196267_2_.removeBlock(p_196267_3_);
      }

   }

   public boolean isReplaceable(IBlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      int i = p_196253_1_.get(LAYERS);
      if (p_196253_2_.getItem().getItem() == this.asItem() && i < 8) {
         if (p_196253_2_.func_196012_c()) {
            return p_196253_2_.getFace() == EnumFacing.UP;
         } else {
            return true;
         }
      } else {
         return i == 1;
      }
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockState iblockstate = p_196258_1_.getWorld().getBlockState(p_196258_1_.getPos());
      if (iblockstate.getBlock() == this) {
         int i = iblockstate.get(LAYERS);
         return iblockstate.with(LAYERS, Integer.valueOf(Math.min(8, i + 1)));
      } else {
         return super.getStateForPlacement(p_196258_1_);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(LAYERS);
   }

   protected boolean canSilkHarvest() {
      return true;
   }
}
