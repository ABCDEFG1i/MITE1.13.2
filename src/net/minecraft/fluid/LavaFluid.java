package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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

public abstract class LavaFluid extends FlowingFluid {
   public Fluid getFlowingFluid() {
      return Fluids.FLOWING_LAVA;
   }

   public Fluid getStillFluid() {
      return Fluids.LAVA;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.SOLID;
   }

   public Item getFilledBucket() {
      return Items.LAVA_BUCKET;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(World p_204522_1_, BlockPos p_204522_2_, IFluidState p_204522_3_, Random p_204522_4_) {
      BlockPos blockpos = p_204522_2_.up();
      if (p_204522_1_.getBlockState(blockpos).isAir() && !p_204522_1_.getBlockState(blockpos).isOpaqueCube(p_204522_1_, blockpos)) {
         if (p_204522_4_.nextInt(100) == 0) {
            double d0 = (double)((float)p_204522_2_.getX() + p_204522_4_.nextFloat());
            double d1 = (double)(p_204522_2_.getY() + 1);
            double d2 = (double)((float)p_204522_2_.getZ() + p_204522_4_.nextFloat());
            p_204522_1_.spawnParticle(Particles.LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            p_204522_1_.playSound(d0, d1, d2, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F + p_204522_4_.nextFloat() * 0.2F, 0.9F + p_204522_4_.nextFloat() * 0.15F, false);
         }

         if (p_204522_4_.nextInt(200) == 0) {
            p_204522_1_.playSound((double)p_204522_2_.getX(), (double)p_204522_2_.getY(), (double)p_204522_2_.getZ(), SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + p_204522_4_.nextFloat() * 0.2F, 0.9F + p_204522_4_.nextFloat() * 0.15F, false);
         }
      }

   }

   public void randomTick(World p_207186_1_, BlockPos p_207186_2_, IFluidState p_207186_3_, Random p_207186_4_) {
      if (p_207186_1_.getGameRules().getBoolean("doFireTick")) {
         int i = p_207186_4_.nextInt(3);
         if (i > 0) {
            BlockPos blockpos = p_207186_2_;

            for(int j = 0; j < i; ++j) {
               blockpos = blockpos.add(p_207186_4_.nextInt(3) - 1, 1, p_207186_4_.nextInt(3) - 1);
               if (!p_207186_1_.isBlockPresent(blockpos)) {
                  return;
               }

               IBlockState iblockstate = p_207186_1_.getBlockState(blockpos);
               if (iblockstate.isAir()) {
                  if (this.isSurroundingBlockFlammable(p_207186_1_, blockpos)) {
                     p_207186_1_.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
                     return;
                  }
               } else if (iblockstate.getMaterial().blocksMovement()) {
                  return;
               }
            }
         } else {
            for(int k = 0; k < 3; ++k) {
               BlockPos blockpos1 = p_207186_2_.add(p_207186_4_.nextInt(3) - 1, 0, p_207186_4_.nextInt(3) - 1);
               if (!p_207186_1_.isBlockPresent(blockpos1)) {
                  return;
               }

               if (p_207186_1_.isAirBlock(blockpos1.up()) && this.getCanBlockBurn(p_207186_1_, blockpos1)) {
                  p_207186_1_.setBlockState(blockpos1.up(), Blocks.FIRE.getDefaultState());
               }
            }
         }

      }
   }

   private boolean isSurroundingBlockFlammable(IWorldReaderBase p_176369_1_, BlockPos p_176369_2_) {
      for(EnumFacing enumfacing : EnumFacing.values()) {
         if (this.getCanBlockBurn(p_176369_1_, p_176369_2_.offset(enumfacing))) {
            return true;
         }
      }

      return false;
   }

   private boolean getCanBlockBurn(IWorldReaderBase p_176368_1_, BlockPos p_176368_2_) {
      return (p_176368_2_.getY() < 0 || p_176368_2_.getY() >= 256 || p_176368_1_.isBlockLoaded(
              p_176368_2_)) && p_176368_1_.getBlockState(p_176368_2_).getMaterial().isFlammable();
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public IParticleData getDripParticleData() {
      return Particles.DRIPPING_LAVA;
   }

   protected void beforeReplacingBlock(IWorld p_205580_1_, BlockPos p_205580_2_, IBlockState p_205580_3_) {
      this.triggerEffects(p_205580_1_, p_205580_2_);
   }

   public int getSlopeFindDistance(IWorldReaderBase p_185698_1_) {
      return p_185698_1_.getDimension().doesWaterVaporize() ? 4 : 2;
   }

   public IBlockState getBlockState(IFluidState p_204527_1_) {
      return Blocks.LAVA.getDefaultState().with(BlockFlowingFluid.LEVEL, Integer.valueOf(getLevelFromState(p_204527_1_)));
   }

   public boolean isEquivalentTo(Fluid p_207187_1_) {
      return p_207187_1_ == Fluids.LAVA || p_207187_1_ == Fluids.FLOWING_LAVA;
   }

   public int getLevelDecreasePerBlock(IWorldReaderBase p_204528_1_) {
      return p_204528_1_.getDimension().doesWaterVaporize() ? 1 : 2;
   }

   public boolean canOtherFlowInto(IFluidState p_211757_1_, Fluid p_211757_2_, EnumFacing p_211757_3_) {
      return p_211757_1_.getHeight() >= 0.44444445F && p_211757_2_.isIn(FluidTags.WATER);
   }

   public int getTickRate(IWorldReaderBase p_205569_1_) {
      return p_205569_1_.getDimension().isNether() ? 10 : 30;
   }

   public int getTickRate(World p_205578_1_, IFluidState p_205578_2_, IFluidState p_205578_3_) {
      int i = this.getTickRate(p_205578_1_);
      if (!p_205578_2_.isEmpty() && !p_205578_3_.isEmpty() && !p_205578_2_.get(FALLING) && !p_205578_3_.get(FALLING) && p_205578_3_.getHeight() > p_205578_2_.getHeight() && p_205578_1_.getRandom().nextInt(4) != 0) {
         i *= 4;
      }

      return i;
   }

   protected void triggerEffects(IWorld p_205581_1_, BlockPos p_205581_2_) {
      double d0 = (double)p_205581_2_.getX();
      double d1 = (double)p_205581_2_.getY();
      double d2 = (double)p_205581_2_.getZ();
      p_205581_1_.playSound(null, p_205581_2_, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (p_205581_1_.getRandom().nextFloat() - p_205581_1_.getRandom().nextFloat()) * 0.8F);

      for(int i = 0; i < 8; ++i) {
         p_205581_1_.spawnParticle(Particles.LARGE_SMOKE, d0 + Math.random(), d1 + 1.2D, d2 + Math.random(), 0.0D, 0.0D, 0.0D);
      }

   }

   protected boolean canSourcesMultiply() {
      return false;
   }

   protected void flowInto(IWorld p_205574_1_, BlockPos p_205574_2_, IBlockState p_205574_3_, EnumFacing p_205574_4_, IFluidState p_205574_5_) {
      if (p_205574_4_ == EnumFacing.DOWN) {
         IFluidState ifluidstate = p_205574_1_.getFluidState(p_205574_2_);
         if (this.isIn(FluidTags.LAVA) && ifluidstate.isTagged(FluidTags.WATER)) {
            if (p_205574_3_.getBlock() instanceof BlockFlowingFluid) {
               p_205574_1_.setBlockState(p_205574_2_, Blocks.STONE.getDefaultState(), 3);
            }

            this.triggerEffects(p_205574_1_, p_205574_2_);
            return;
         }
      }

      super.flowInto(p_205574_1_, p_205574_2_, p_205574_3_, p_205574_4_, p_205574_5_);
   }

   protected boolean getTickRandomly() {
      return true;
   }

   protected float getExplosionResistance() {
      return 100.0F;
   }

   public static class Flowing extends LavaFluid {
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

   public static class Source extends LavaFluid {
      public int getLevel(IFluidState p_207192_1_) {
         return 8;
      }

      public boolean isSource(IFluidState p_207193_1_) {
         return true;
      }
   }
}
