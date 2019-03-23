package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.IStateHolder;
import net.minecraft.tags.Tag;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IFluidState extends IStateHolder<IFluidState> {
   Fluid getFluid();

   default boolean isSource() {
      return this.getFluid().isSource(this);
   }

   default boolean isEmpty() {
      return this.getFluid().isEmpty();
   }

   default float getHeight() {
      return this.getFluid().getHeight(this);
   }

   default int getLevel() {
      return this.getFluid().getLevel(this);
   }

   @OnlyIn(Dist.CLIENT)
   default boolean shouldRenderSides(IBlockReader p_205586_1_, BlockPos p_205586_2_) {
      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            BlockPos blockpos = p_205586_2_.add(i, 0, j);
            IFluidState ifluidstate = p_205586_1_.getFluidState(blockpos);
            if (!ifluidstate.getFluid().isEquivalentTo(this.getFluid()) && !p_205586_1_.getBlockState(blockpos).isOpaqueCube(p_205586_1_, blockpos)) {
               return true;
            }
         }
      }

      return false;
   }

   default void tick(World p_206880_1_, BlockPos p_206880_2_) {
      this.getFluid().tick(p_206880_1_, p_206880_2_, this);
   }

   @OnlyIn(Dist.CLIENT)
   default void animateTick(World p_206881_1_, BlockPos p_206881_2_, Random p_206881_3_) {
      this.getFluid().animateTick(p_206881_1_, p_206881_2_, this, p_206881_3_);
   }

   default boolean getTickRandomly() {
      return this.getFluid().getTickRandomly();
   }

   default void randomTick(World p_206891_1_, BlockPos p_206891_2_, Random p_206891_3_) {
      this.getFluid().randomTick(p_206891_1_, p_206891_2_, this, p_206891_3_);
   }

   default Vec3d getFlow(IWorldReaderBase p_206887_1_, BlockPos p_206887_2_) {
      return this.getFluid().getFlow(p_206887_1_, p_206887_2_, this);
   }

   default IBlockState getBlockState() {
      return this.getFluid().getBlockState(this);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   default IParticleData getDripParticleData() {
      return this.getFluid().getDripParticleData();
   }

   @OnlyIn(Dist.CLIENT)
   default BlockRenderLayer getRenderLayer() {
      return this.getFluid().getRenderLayer();
   }

   default boolean isTagged(Tag<Fluid> p_206884_1_) {
      return this.getFluid().isIn(p_206884_1_);
   }

   default float getExplosionResistance() {
      return this.getFluid().getExplosionResistance();
   }

   default boolean canOtherFlowInto(Fluid p_211725_1_, EnumFacing p_211725_2_) {
      return this.getFluid().canOtherFlowInto(this, p_211725_1_, p_211725_2_);
   }
}
