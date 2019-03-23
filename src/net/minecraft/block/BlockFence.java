package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockFence extends BlockFourWay {
   private final VoxelShape[] field_199609_B;

   public BlockFence(Block.Properties p_i48399_1_) {
      super(2.0F, 2.0F, 16.0F, 16.0F, 24.0F, p_i48399_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
      this.field_199609_B = this.func_196408_a(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
   }

   public VoxelShape getRenderShape(IBlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_) {
      return this.field_199609_B[this.getIndex(p_196247_1_)];
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   public boolean attachesTo(IBlockState p_196416_1_, BlockFaceShape p_196416_2_) {
      Block block = p_196416_1_.getBlock();
      boolean flag = p_196416_2_ == BlockFaceShape.MIDDLE_POLE && (p_196416_1_.getMaterial() == this.material || block instanceof BlockFenceGate);
      return !isExcepBlockForAttachWithPiston(block) && p_196416_2_ == BlockFaceShape.SOLID || flag;
   }

   public static boolean isExcepBlockForAttachWithPiston(Block p_194142_0_) {
      return Block.isExceptBlockForAttachWithPiston(p_194142_0_) || p_194142_0_ == Blocks.BARRIER || p_194142_0_ == Blocks.MELON || p_194142_0_ == Blocks.PUMPKIN || p_194142_0_ == Blocks.CARVED_PUMPKIN || p_194142_0_ == Blocks.JACK_O_LANTERN || p_194142_0_ == Blocks.FROSTED_ICE || p_194142_0_ == Blocks.TNT;
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (!p_196250_2_.isRemote) {
         return ItemLead.attachToFence(p_196250_4_, p_196250_2_, p_196250_3_);
      } else {
         ItemStack itemstack = p_196250_4_.getHeldItem(p_196250_5_);
         return itemstack.getItem() == Items.LEAD || itemstack.isEmpty();
      }
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockReader iblockreader = p_196258_1_.getWorld();
      BlockPos blockpos = p_196258_1_.getPos();
      IFluidState ifluidstate = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      BlockPos blockpos1 = blockpos.north();
      BlockPos blockpos2 = blockpos.east();
      BlockPos blockpos3 = blockpos.south();
      BlockPos blockpos4 = blockpos.west();
      IBlockState iblockstate = iblockreader.getBlockState(blockpos1);
      IBlockState iblockstate1 = iblockreader.getBlockState(blockpos2);
      IBlockState iblockstate2 = iblockreader.getBlockState(blockpos3);
      IBlockState iblockstate3 = iblockreader.getBlockState(blockpos4);
      return super.getStateForPlacement(p_196258_1_).with(NORTH, Boolean.valueOf(this.attachesTo(iblockstate, iblockstate.getBlockFaceShape(iblockreader, blockpos1, EnumFacing.SOUTH)))).with(EAST, Boolean.valueOf(this.attachesTo(iblockstate1, iblockstate1.getBlockFaceShape(iblockreader, blockpos2, EnumFacing.WEST)))).with(SOUTH, Boolean.valueOf(this.attachesTo(iblockstate2, iblockstate2.getBlockFaceShape(iblockreader, blockpos3, EnumFacing.NORTH)))).with(WEST, Boolean.valueOf(this.attachesTo(iblockstate3, iblockstate3.getBlockFaceShape(iblockreader, blockpos4, EnumFacing.EAST)))).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_1_.get(WATERLOGGED)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      return p_196271_2_.getAxis().getPlane() == EnumFacing.Plane.HORIZONTAL ? p_196271_1_.with(FACING_TO_PROPERTY_MAP.get(p_196271_2_), Boolean.valueOf(this.attachesTo(p_196271_3_, p_196271_3_.getBlockFaceShape(p_196271_4_, p_196271_6_, p_196271_2_.getOpposite())))) : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return p_193383_4_ != EnumFacing.UP && p_193383_4_ != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.CENTER;
   }
}
