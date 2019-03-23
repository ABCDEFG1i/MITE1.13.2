package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockLever extends BlockHorizontalFace {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   protected static final VoxelShape LEVER_NORTH_AABB = Block.makeCuboidShape(5.0D, 4.0D, 10.0D, 11.0D, 12.0D, 16.0D);
   protected static final VoxelShape LEVER_SOUTH_AABB = Block.makeCuboidShape(5.0D, 4.0D, 0.0D, 11.0D, 12.0D, 6.0D);
   protected static final VoxelShape LEVER_WEST_AABB = Block.makeCuboidShape(10.0D, 4.0D, 5.0D, 16.0D, 12.0D, 11.0D);
   protected static final VoxelShape LEVER_EAST_AABB = Block.makeCuboidShape(0.0D, 4.0D, 5.0D, 6.0D, 12.0D, 11.0D);
   protected static final VoxelShape field_209348_r = Block.makeCuboidShape(5.0D, 0.0D, 4.0D, 11.0D, 6.0D, 12.0D);
   protected static final VoxelShape field_209349_s = Block.makeCuboidShape(4.0D, 0.0D, 5.0D, 12.0D, 6.0D, 11.0D);
   protected static final VoxelShape field_209350_t = Block.makeCuboidShape(5.0D, 10.0D, 4.0D, 11.0D, 16.0D, 12.0D);
   protected static final VoxelShape field_209351_u = Block.makeCuboidShape(4.0D, 10.0D, 5.0D, 12.0D, 16.0D, 11.0D);

   protected BlockLever(Block.Properties p_i48369_1_) {
      super(p_i48369_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, EnumFacing.NORTH).with(POWERED, Boolean.valueOf(false)).with(FACE, AttachFace.WALL));
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      switch((AttachFace)p_196244_1_.get(FACE)) {
      case FLOOR:
         switch(p_196244_1_.get(HORIZONTAL_FACING).getAxis()) {
         case X:
            return field_209349_s;
         case Z:
         default:
            return field_209348_r;
         }
      case WALL:
         switch((EnumFacing)p_196244_1_.get(HORIZONTAL_FACING)) {
         case EAST:
            return LEVER_EAST_AABB;
         case WEST:
            return LEVER_WEST_AABB;
         case SOUTH:
            return LEVER_SOUTH_AABB;
         case NORTH:
         default:
            return LEVER_NORTH_AABB;
         }
      case CEILING:
      default:
         switch(p_196244_1_.get(HORIZONTAL_FACING).getAxis()) {
         case X:
            return field_209351_u;
         case Z:
         default:
            return field_209350_t;
         }
      }
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      p_196250_1_ = p_196250_1_.cycle(POWERED);
      boolean flag = p_196250_1_.get(POWERED);
      if (p_196250_2_.isRemote) {
         if (flag) {
            func_196379_a(p_196250_1_, p_196250_2_, p_196250_3_, 1.0F);
         }

         return true;
      } else {
         p_196250_2_.setBlockState(p_196250_3_, p_196250_1_, 3);
         float f = flag ? 0.6F : 0.5F;
         p_196250_2_.playSound((EntityPlayer)null, p_196250_3_, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, f);
         this.func_196378_d(p_196250_1_, p_196250_2_, p_196250_3_);
         return true;
      }
   }

   private static void func_196379_a(IBlockState p_196379_0_, IWorld p_196379_1_, BlockPos p_196379_2_, float p_196379_3_) {
      EnumFacing enumfacing = p_196379_0_.get(HORIZONTAL_FACING).getOpposite();
      EnumFacing enumfacing1 = func_196365_i(p_196379_0_).getOpposite();
      double d0 = (double)p_196379_2_.getX() + 0.5D + 0.1D * (double)enumfacing.getXOffset() + 0.2D * (double)enumfacing1.getXOffset();
      double d1 = (double)p_196379_2_.getY() + 0.5D + 0.1D * (double)enumfacing.getYOffset() + 0.2D * (double)enumfacing1.getYOffset();
      double d2 = (double)p_196379_2_.getZ() + 0.5D + 0.1D * (double)enumfacing.getZOffset() + 0.2D * (double)enumfacing1.getZOffset();
      p_196379_1_.spawnParticle(new RedstoneParticleData(1.0F, 0.0F, 0.0F, p_196379_3_), d0, d1, d2, 0.0D, 0.0D, 0.0D);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_1_.get(POWERED) && p_180655_4_.nextFloat() < 0.25F) {
         func_196379_a(p_180655_1_, p_180655_2_, p_180655_3_, 0.5F);
      }

   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         if (p_196243_1_.get(POWERED)) {
            this.func_196378_d(p_196243_1_, p_196243_2_, p_196243_3_);
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public int getWeakPower(IBlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, EnumFacing p_180656_4_) {
      return p_180656_1_.get(POWERED) ? 15 : 0;
   }

   public int getStrongPower(IBlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, EnumFacing p_176211_4_) {
      return p_176211_1_.get(POWERED) && func_196365_i(p_176211_1_) == p_176211_4_ ? 15 : 0;
   }

   public boolean canProvidePower(IBlockState p_149744_1_) {
      return true;
   }

   private void func_196378_d(IBlockState p_196378_1_, World p_196378_2_, BlockPos p_196378_3_) {
      p_196378_2_.notifyNeighborsOfStateChange(p_196378_3_, this);
      p_196378_2_.notifyNeighborsOfStateChange(p_196378_3_.offset(func_196365_i(p_196378_1_).getOpposite()), this);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(FACE, HORIZONTAL_FACING, POWERED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
