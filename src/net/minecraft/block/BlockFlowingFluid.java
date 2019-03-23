package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockFlowingFluid extends Block implements IBucketPickupHandler {
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_0_15;
   protected final FlowingFluid fluid;
   private final List<IFluidState> field_212565_c;
   private final Map<IBlockState, VoxelShape> stateToShapeCache = Maps.newIdentityHashMap();

   protected BlockFlowingFluid(FlowingFluid p_i49014_1_, Block.Properties p_i49014_2_) {
      super(p_i49014_2_);
      this.fluid = p_i49014_1_;
      this.field_212565_c = Lists.newArrayList();
      this.field_212565_c.add(p_i49014_1_.getStillFluidState(false));

      for(int i = 1; i < 8; ++i) {
         this.field_212565_c.add(p_i49014_1_.getFlowingFluidState(8 - i, false));
      }

      this.field_212565_c.add(p_i49014_1_.getFlowingFluidState(8, true));
      this.setDefaultState(this.stateContainer.getBaseState().with(LEVEL, Integer.valueOf(0)));
   }

   public void randomTick(IBlockState p_196265_1_, World p_196265_2_, BlockPos p_196265_3_, Random p_196265_4_) {
      p_196265_2_.getFluidState(p_196265_3_).randomTick(p_196265_2_, p_196265_3_, p_196265_4_);
   }

   public boolean func_200123_i(IBlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return false;
   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return !this.fluid.isIn(FluidTags.LAVA);
   }

   public IFluidState getFluidState(IBlockState p_204507_1_) {
      int i = p_204507_1_.get(LEVEL);
      return this.field_212565_c.get(Math.min(i, 8));
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public boolean isCollidable(IBlockState p_200293_1_) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSideInvisible(IBlockState p_200122_1_, IBlockState p_200122_2_, EnumFacing p_200122_3_) {
      return p_200122_2_.getFluidState().getFluid().isEquivalentTo(this.fluid) ? true : super.isSolid(p_200122_1_);
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      IFluidState ifluidstate = p_196244_2_.getFluidState(p_196244_3_.up());
      return ifluidstate.getFluid().isEquivalentTo(this.fluid) ? VoxelShapes.func_197868_b() : this.stateToShapeCache.computeIfAbsent(p_196244_1_, (p_209903_0_) -> {
         IFluidState ifluidstate1 = p_209903_0_.getFluidState();
         return VoxelShapes.func_197873_a(0.0D, 0.0D, 0.0D, 1.0D, (double)ifluidstate1.getHeight(), 1.0D);
      });
   }

   public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public IItemProvider getItemDropped(IBlockState p_199769_1_, World p_199769_2_, BlockPos p_199769_3_, int p_199769_4_) {
      return Items.AIR;
   }

   public int tickRate(IWorldReaderBase p_149738_1_) {
      return this.fluid.getTickRate(p_149738_1_);
   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      if (this.reactWithNeighbors(p_196259_2_, p_196259_3_, p_196259_1_)) {
         p_196259_2_.getPendingFluidTicks().scheduleTick(p_196259_3_, p_196259_1_.getFluidState().getFluid(), this.tickRate(p_196259_2_));
      }

   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_1_.getFluidState().isSource() || p_196271_3_.getFluidState().isSource()) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, p_196271_1_.getFluidState().getFluid(), this.tickRate(p_196271_4_));
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
      if (this.reactWithNeighbors(p_189540_2_, p_189540_3_, p_189540_1_)) {
         p_189540_2_.getPendingFluidTicks().scheduleTick(p_189540_3_, p_189540_1_.getFluidState().getFluid(), this.tickRate(p_189540_2_));
      }

   }

   public boolean reactWithNeighbors(World p_204515_1_, BlockPos p_204515_2_, IBlockState p_204515_3_) {
      if (this.fluid.isIn(FluidTags.LAVA)) {
         boolean flag = false;

         for(EnumFacing enumfacing : EnumFacing.values()) {
            if (enumfacing != EnumFacing.DOWN && p_204515_1_.getFluidState(p_204515_2_.offset(enumfacing)).isTagged(FluidTags.WATER)) {
               flag = true;
               break;
            }
         }

         if (flag) {
            IFluidState ifluidstate = p_204515_1_.getFluidState(p_204515_2_);
            if (ifluidstate.isSource()) {
               p_204515_1_.setBlockState(p_204515_2_, Blocks.OBSIDIAN.getDefaultState());
               this.triggerMixEffects(p_204515_1_, p_204515_2_);
               return false;
            }

            if (ifluidstate.getHeight() >= 0.44444445F) {
               p_204515_1_.setBlockState(p_204515_2_, Blocks.COBBLESTONE.getDefaultState());
               this.triggerMixEffects(p_204515_1_, p_204515_2_);
               return false;
            }
         }
      }

      return true;
   }

   protected void triggerMixEffects(IWorld p_180688_1_, BlockPos p_180688_2_) {
      double d0 = (double)p_180688_2_.getX();
      double d1 = (double)p_180688_2_.getY();
      double d2 = (double)p_180688_2_.getZ();
      p_180688_1_.playSound((EntityPlayer)null, p_180688_2_, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (p_180688_1_.getRandom().nextFloat() - p_180688_1_.getRandom().nextFloat()) * 0.8F);

      for(int i = 0; i < 8; ++i) {
         p_180688_1_.spawnParticle(Particles.LARGE_SMOKE, d0 + Math.random(), d1 + 1.2D, d2 + Math.random(), 0.0D, 0.0D, 0.0D);
      }

   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(LEVEL);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   public Fluid pickupFluid(IWorld p_204508_1_, BlockPos p_204508_2_, IBlockState p_204508_3_) {
      if (p_204508_3_.get(LEVEL) == 0) {
         p_204508_1_.setBlockState(p_204508_2_, Blocks.AIR.getDefaultState(), 11);
         return this.fluid;
      } else {
         return Fluids.EMPTY;
      }
   }
}
