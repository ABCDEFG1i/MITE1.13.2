package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Particles;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockEnderChest extends BlockContainer implements IBucketPickupHandler, ILiquidContainer {
   public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

   protected BlockEnderChest(Block.Properties p_i48403_1_) {
      super(p_i48403_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPE;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasCustomBreakingProgress(IBlockState p_190946_1_) {
      return true;
   }

   public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
      return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Blocks.OBSIDIAN;
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return 8;
   }

   protected boolean canSilkHarvest() {
      return true;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IFluidState ifluidstate = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      return this.getDefaultState().with(FACING, p_196258_1_.getPlacementHorizontalFacing().getOpposite()).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      InventoryEnderChest inventoryenderchest = p_196250_4_.getInventoryEnderChest();
      TileEntity tileentity = p_196250_2_.getTileEntity(p_196250_3_);
      if (inventoryenderchest != null && tileentity instanceof TileEntityEnderChest) {
         if (p_196250_2_.getBlockState(p_196250_3_.up()).isNormalCube()) {
            return true;
         } else if (p_196250_2_.isRemote) {
            return true;
         } else {
            inventoryenderchest.setChestTileEntity((TileEntityEnderChest)tileentity);
            p_196250_4_.displayGUIChest(inventoryenderchest);
            p_196250_4_.addStat(StatList.OPEN_ENDERCHEST);
            return true;
         }
      } else {
         return true;
      }
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntityEnderChest();
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      for(int i = 0; i < 3; ++i) {
         int j = p_180655_4_.nextInt(2) * 2 - 1;
         int k = p_180655_4_.nextInt(2) * 2 - 1;
         double d0 = (double)p_180655_3_.getX() + 0.5D + 0.25D * (double)j;
         double d1 = (double)((float)p_180655_3_.getY() + p_180655_4_.nextFloat());
         double d2 = (double)p_180655_3_.getZ() + 0.5D + 0.25D * (double)k;
         double d3 = (double)(p_180655_4_.nextFloat() * (float)j);
         double d4 = ((double)p_180655_4_.nextFloat() - 0.5D) * 0.125D;
         double d5 = (double)(p_180655_4_.nextFloat() * (float)k);
         p_180655_2_.spawnParticle(Particles.PORTAL, d0, d1, d2, d3, d4, d5);
      }

   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.with(FACING, p_185499_2_.rotate(p_185499_1_.get(FACING)));
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation(p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(FACING, WATERLOGGED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   public Fluid pickupFluid(IWorld p_204508_1_, BlockPos p_204508_2_, IBlockState p_204508_3_) {
      if (p_204508_3_.get(WATERLOGGED)) {
         p_204508_1_.setBlockState(p_204508_2_, p_204508_3_.with(WATERLOGGED, Boolean.valueOf(false)), 3);
         return Fluids.WATER;
      } else {
         return Fluids.EMPTY;
      }
   }

   public IFluidState getFluidState(IBlockState p_204507_1_) {
      return p_204507_1_.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_204507_1_);
   }

   public boolean canContainFluid(IBlockReader p_204510_1_, BlockPos p_204510_2_, IBlockState p_204510_3_, Fluid p_204510_4_) {
      return !p_204510_3_.get(WATERLOGGED) && p_204510_4_ == Fluids.WATER;
   }

   public boolean receiveFluid(IWorld p_204509_1_, BlockPos p_204509_2_, IBlockState p_204509_3_, IFluidState p_204509_4_) {
      if (!p_204509_3_.get(WATERLOGGED) && p_204509_4_.getFluid() == Fluids.WATER) {
         if (!p_204509_1_.isRemote()) {
            p_204509_1_.setBlockState(p_204509_2_, p_204509_3_.with(WATERLOGGED, Boolean.valueOf(true)), 3);
            p_204509_1_.getPendingFluidTicks().scheduleTick(p_204509_2_, Fluids.WATER, Fluids.WATER.getTickRate(p_204509_1_));
         }

         return true;
      } else {
         return false;
      }
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_1_.get(WATERLOGGED)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
