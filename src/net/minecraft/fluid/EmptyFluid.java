package net.minecraft.fluid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReaderBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EmptyFluid extends Fluid {
   @OnlyIn(Dist.CLIENT)
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.SOLID;
   }

   public Item getFilledBucket() {
      return Items.AIR;
   }

   public boolean canOtherFlowInto(IFluidState p_211757_1_, Fluid p_211757_2_, EnumFacing p_211757_3_) {
      return true;
   }

   public Vec3d getFlow(IWorldReaderBase p_205564_1_, BlockPos p_205564_2_, IFluidState p_205564_3_) {
      return Vec3d.ZERO;
   }

   public int getTickRate(IWorldReaderBase p_205569_1_) {
      return 0;
   }

   protected boolean isEmpty() {
      return true;
   }

   protected float getExplosionResistance() {
      return 0.0F;
   }

   public float getHeight(IFluidState p_207181_1_) {
      return 0.0F;
   }

   protected IBlockState getBlockState(IFluidState p_204527_1_) {
      return Blocks.AIR.getDefaultState();
   }

   public boolean isSource(IFluidState p_207193_1_) {
      return false;
   }

   public int getLevel(IFluidState p_207192_1_) {
      return 0;
   }
}
