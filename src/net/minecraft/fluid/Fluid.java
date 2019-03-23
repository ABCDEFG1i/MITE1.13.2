package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.Tag;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Fluid {
   public static final ObjectIntIdentityMap<IFluidState> STATE_REGISTRY = new ObjectIntIdentityMap<>();
   protected final StateContainer<Fluid, IFluidState> stateContainer;
   private IFluidState defaultState;

   protected Fluid() {
      StateContainer.Builder<Fluid, IFluidState> builder = new StateContainer.Builder<>(this);
      this.fillStateContainer(builder);
      this.stateContainer = builder.create(FluidState::new);
      this.setDefaultState(this.stateContainer.getBaseState());
   }

   protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> p_207184_1_) {
   }

   public StateContainer<Fluid, IFluidState> getStateContainer() {
      return this.stateContainer;
   }

   protected final void setDefaultState(IFluidState p_207183_1_) {
      this.defaultState = p_207183_1_;
   }

   public final IFluidState getDefaultState() {
      return this.defaultState;
   }

   @OnlyIn(Dist.CLIENT)
   public abstract BlockRenderLayer getRenderLayer();

   public abstract Item getFilledBucket();

   @OnlyIn(Dist.CLIENT)
   protected void animateTick(World p_204522_1_, BlockPos p_204522_2_, IFluidState p_204522_3_, Random p_204522_4_) {
   }

   protected void tick(World p_207191_1_, BlockPos p_207191_2_, IFluidState p_207191_3_) {
   }

   protected void randomTick(World p_207186_1_, BlockPos p_207186_2_, IFluidState p_207186_3_, Random p_207186_4_) {
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   protected IParticleData getDripParticleData() {
      return null;
   }

   protected abstract boolean canOtherFlowInto(IFluidState p_211757_1_, Fluid p_211757_2_, EnumFacing p_211757_3_);

   protected abstract Vec3d getFlow(IWorldReaderBase p_205564_1_, BlockPos p_205564_2_, IFluidState p_205564_3_);

   public abstract int getTickRate(IWorldReaderBase p_205569_1_);

   protected boolean getTickRandomly() {
      return false;
   }

   protected boolean isEmpty() {
      return false;
   }

   protected abstract float getExplosionResistance();

   public abstract float getHeight(IFluidState p_207181_1_);

   protected abstract IBlockState getBlockState(IFluidState p_204527_1_);

   public abstract boolean isSource(IFluidState p_207193_1_);

   public abstract int getLevel(IFluidState p_207192_1_);

   public boolean isEquivalentTo(Fluid p_207187_1_) {
      return p_207187_1_ == this;
   }

   public boolean isIn(Tag<Fluid> p_207185_1_) {
      return p_207185_1_.contains(this);
   }

   public static void registerAll() {
      register(IRegistry.field_212619_h.func_212609_b(), new EmptyFluid());
      register("flowing_water", new WaterFluid.Flowing());
      register("water", new WaterFluid.Source());
      register("flowing_lava", new LavaFluid.Flowing());
      register("lava", new LavaFluid.Source());

      for(Fluid fluid : IRegistry.field_212619_h) {
         for(IFluidState ifluidstate : fluid.getStateContainer().getValidStates()) {
            STATE_REGISTRY.add(ifluidstate);
         }
      }

   }

   private static void register(String p_207198_0_, Fluid p_207198_1_) {
      register(new ResourceLocation(p_207198_0_), p_207198_1_);
   }

   private static void register(ResourceLocation p_207194_0_, Fluid p_207194_1_) {
      IRegistry.field_212619_h.func_82595_a(p_207194_0_, p_207194_1_);
   }
}
