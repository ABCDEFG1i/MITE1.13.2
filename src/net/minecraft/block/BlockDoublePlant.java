package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class BlockDoublePlant extends BlockBush {
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

   public BlockDoublePlant(Block.Properties p_i48412_1_) {
      super(p_i48412_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(HALF, DoubleBlockHalf.LOWER));
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      DoubleBlockHalf doubleblockhalf = p_196271_1_.get(HALF);
      if (p_196271_2_.getAxis() != EnumFacing.Axis.Y || doubleblockhalf == DoubleBlockHalf.LOWER != (p_196271_2_ == EnumFacing.UP) || p_196271_3_.getBlock() == this && p_196271_3_.get(HALF) != doubleblockhalf) {
         return doubleblockhalf == DoubleBlockHalf.LOWER && p_196271_2_ == EnumFacing.DOWN && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      } else {
         return Blocks.AIR.getDefaultState();
      }
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockPos blockpos = p_196258_1_.getPos();
      return blockpos.getY() < 255 && p_196258_1_.getWorld().getBlockState(blockpos.up()).isReplaceable(p_196258_1_) ? super.getStateForPlacement(p_196258_1_) : null;
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, IBlockState p_180633_3_, EntityLivingBase p_180633_4_, ItemStack p_180633_5_) {
      p_180633_1_.setBlockState(p_180633_2_.up(), this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER), 3);
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      if (p_196260_1_.get(HALF) != DoubleBlockHalf.UPPER) {
         return super.isValidPosition(p_196260_1_, p_196260_2_, p_196260_3_);
      } else {
         IBlockState iblockstate = p_196260_2_.getBlockState(p_196260_3_.down());
         return iblockstate.getBlock() == this && iblockstate.get(HALF) == DoubleBlockHalf.LOWER;
      }
   }

   public void placeAt(IWorld p_196390_1_, BlockPos p_196390_2_, int p_196390_3_) {
      p_196390_1_.setBlockState(p_196390_2_, this.getDefaultState().with(HALF, DoubleBlockHalf.LOWER), p_196390_3_);
      p_196390_1_.setBlockState(p_196390_2_.up(), this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER), p_196390_3_);
   }

   public void harvestBlock(World p_180657_1_, EntityPlayer p_180657_2_, BlockPos p_180657_3_, IBlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      super.harvestBlock(p_180657_1_, p_180657_2_, p_180657_3_, Blocks.AIR.getDefaultState(), p_180657_5_, p_180657_6_);
   }

   public void onBlockHarvested(World p_176208_1_, BlockPos p_176208_2_, IBlockState p_176208_3_, EntityPlayer p_176208_4_) {
      DoubleBlockHalf doubleblockhalf = p_176208_3_.get(HALF);
      boolean flag = doubleblockhalf == DoubleBlockHalf.LOWER;
      BlockPos blockpos = flag ? p_176208_2_.up() : p_176208_2_.down();
      IBlockState iblockstate = p_176208_1_.getBlockState(blockpos);
      if (iblockstate.getBlock() == this && iblockstate.get(HALF) != doubleblockhalf) {
         p_176208_1_.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
         p_176208_1_.playEvent(p_176208_4_, 2001, blockpos, Block.getStateId(iblockstate));
         if (!p_176208_1_.isRemote && !p_176208_4_.isCreative()) {
            if (flag) {
               this.harvest(p_176208_3_, p_176208_1_, p_176208_2_, p_176208_4_.getHeldItemMainhand());
            } else {
               this.harvest(iblockstate, p_176208_1_, blockpos, p_176208_4_.getHeldItemMainhand());
            }
         }
      }

      super.onBlockHarvested(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

   protected void harvest(IBlockState p_196391_1_, World p_196391_2_, BlockPos p_196391_3_, ItemStack p_196391_4_) {
      p_196391_1_.dropBlockAsItem(p_196391_2_, p_196391_3_, 0);
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
        return (blockCurrentState.get(HALF) == DoubleBlockHalf.LOWER ? super.getItemDropped(blockCurrentState, worldIn, blockAt, fortuneLevel) : Items.AIR);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(HALF);
   }

   public Block.EnumOffsetType getOffsetType() {
      return Block.EnumOffsetType.XZ;
   }

   @OnlyIn(Dist.CLIENT)
   public long getPositionRandom(IBlockState p_209900_1_, BlockPos p_209900_2_) {
      return MathHelper.getCoordinateRandom(p_209900_2_.getX(), p_209900_2_.down(p_209900_1_.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), p_209900_2_.getZ());
   }
}
