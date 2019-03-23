package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class WaterFluid extends FlowingFluid {
   public Fluid getFlowingFluid() {
      return Fluids.FLOWING_WATER;
   }

   public Fluid getStillFluid() {
      return Fluids.WATER;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public Item getFilledBucket() {
      return Items.WATER_BUCKET;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(World p_204522_1_, BlockPos p_204522_2_, IFluidState p_204522_3_, Random p_204522_4_) {
      if (!p_204522_3_.isSource() && !p_204522_3_.get(FALLING)) {
         if (p_204522_4_.nextInt(64) == 0) {
            p_204522_1_.playSound((double)p_204522_2_.getX() + 0.5D, (double)p_204522_2_.getY() + 0.5D, (double)p_204522_2_.getZ() + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, p_204522_4_.nextFloat() * 0.25F + 0.75F, p_204522_4_.nextFloat() + 0.5F, false);
         }
      } else if (p_204522_4_.nextInt(10) == 0) {
         p_204522_1_.spawnParticle(Particles.UNDERWATER, (double)((float)p_204522_2_.getX() + p_204522_4_.nextFloat()), (double)((float)p_204522_2_.getY() + p_204522_4_.nextFloat()), (double)((float)p_204522_2_.getZ() + p_204522_4_.nextFloat()), 0.0D, 0.0D, 0.0D);
      }

   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public IParticleData getDripParticleData() {
      return Particles.DRIPPING_WATER;
   }

   protected boolean canSourcesMultiply() {
      return true;
   }

   protected void beforeReplacingBlock(IWorld p_205580_1_, BlockPos p_205580_2_, IBlockState p_205580_3_) {
      p_205580_3_.dropBlockAsItem(p_205580_1_.getWorld(), p_205580_2_, 0);
   }

   public int getSlopeFindDistance(IWorldReaderBase p_185698_1_) {
      return 4;
   }

   public IBlockState getBlockState(IFluidState p_204527_1_) {
      return Blocks.WATER.getDefaultState().with(BlockFlowingFluid.LEVEL, Integer.valueOf(getLevelFromState(p_204527_1_)));
   }

   public boolean isEquivalentTo(Fluid p_207187_1_) {
      return p_207187_1_ == Fluids.WATER || p_207187_1_ == Fluids.FLOWING_WATER;
   }

   public int getLevelDecreasePerBlock(IWorldReaderBase p_204528_1_) {
      return 1;
   }

   public int getTickRate(IWorldReaderBase p_205569_1_) {
      return 5;
   }

   public boolean canOtherFlowInto(IFluidState p_211757_1_, Fluid p_211757_2_, EnumFacing p_211757_3_) {
      return p_211757_3_ == EnumFacing.DOWN && !p_211757_2_.isIn(FluidTags.WATER);
   }

   protected float getExplosionResistance() {
      return 100.0F;
   }

   public static class Flowing extends WaterFluid {
      protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> p_207184_1_) {
         super.fillStateContainer(p_207184_1_);
         p_207184_1_.add(LEVEL_1_TO_8);
      }

      public int getLevel(IFluidState p_207192_1_) {
         return p_207192_1_.get(LEVEL_1_TO_8);
      }

      public boolean isSource(IFluidState p_207193_1_) {
         return false;
      }
   }

   public static class Source extends WaterFluid {
      public int getLevel(IFluidState p_207192_1_) {
         return 8;
      }

      public boolean isSource(IFluidState p_207193_1_) {
         return true;
      }
   }
}
