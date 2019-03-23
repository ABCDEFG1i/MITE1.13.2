package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockPistonMoving extends BlockContainer {
   public static final DirectionProperty FACING = BlockPistonExtension.FACING;
   public static final EnumProperty<PistonType> TYPE = BlockPistonExtension.TYPE;

   public BlockPistonMoving(Block.Properties p_i48282_1_) {
      super(p_i48282_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(TYPE, PistonType.DEFAULT));
   }

   @Nullable
   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return null;
   }

   public static TileEntity createTilePiston(IBlockState p_196343_0_, EnumFacing p_196343_1_, boolean p_196343_2_, boolean p_196343_3_) {
      return new TileEntityPiston(p_196343_0_, p_196343_1_, p_196343_2_, p_196343_3_);
   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         TileEntity tileentity = p_196243_2_.getTileEntity(p_196243_3_);
         if (tileentity instanceof TileEntityPiston) {
            ((TileEntityPiston)tileentity).clearPistonTileEntity();
         } else {
            super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
         }

      }
   }

   public void onPlayerDestroy(IWorld p_176206_1_, BlockPos p_176206_2_, IBlockState p_176206_3_) {
      BlockPos blockpos = p_176206_2_.offset(p_176206_3_.get(FACING).getOpposite());
      IBlockState iblockstate = p_176206_1_.getBlockState(blockpos);
      if (iblockstate.getBlock() instanceof BlockPistonBase && iblockstate.get(BlockPistonBase.EXTENDED)) {
         p_176206_1_.removeBlock(blockpos);
      }

   }

   public boolean isSolid(IBlockState p_200124_1_) {
      return false;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (!p_196250_2_.isRemote && p_196250_2_.getTileEntity(p_196250_3_) == null) {
         p_196250_2_.removeBlock(p_196250_3_);
         return true;
      } else {
         return false;
      }
   }

   public IItemProvider getItemDropped(IBlockState p_199769_1_, World p_199769_2_, BlockPos p_199769_3_, int p_199769_4_) {
      return Items.AIR;
   }

   public void dropBlockAsItemWithChance(IBlockState p_196255_1_, World p_196255_2_, BlockPos p_196255_3_, float p_196255_4_, int p_196255_5_) {
      if (!p_196255_2_.isRemote) {
         TileEntityPiston tileentitypiston = this.getTilePistonAt(p_196255_2_, p_196255_3_);
         if (tileentitypiston != null) {
            tileentitypiston.getPistonState().dropBlockAsItem(p_196255_2_, p_196255_3_, 0);
         }
      }
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return VoxelShapes.func_197880_a();
   }

   public VoxelShape getCollisionShape(IBlockState p_196268_1_, IBlockReader p_196268_2_, BlockPos p_196268_3_) {
      TileEntityPiston tileentitypiston = this.getTilePistonAt(p_196268_2_, p_196268_3_);
      return tileentitypiston != null ? tileentitypiston.func_195508_a(p_196268_2_, p_196268_3_) : VoxelShapes.func_197880_a();
   }

   @Nullable
   private TileEntityPiston getTilePistonAt(IBlockReader p_196342_1_, BlockPos p_196342_2_) {
      TileEntity tileentity = p_196342_1_.getTileEntity(p_196342_2_);
      return tileentity instanceof TileEntityPiston ? (TileEntityPiston)tileentity : null;
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, IBlockState p_185473_3_) {
      return ItemStack.EMPTY;
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.with(FACING, p_185499_2_.rotate(p_185499_1_.get(FACING)));
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation(p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(FACING, TYPE);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
