package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockPressurePlateWeighted extends BlockBasePressurePlate {
   public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
   private final int maxWeight;

   protected BlockPressurePlateWeighted(int p_i48295_1_, Block.Properties p_i48295_2_) {
      super(p_i48295_2_);
      this.setDefaultState(this.stateContainer.getBaseState().with(POWER, Integer.valueOf(0)));
      this.maxWeight = p_i48295_1_;
   }

   protected int computeRedstoneStrength(World p_180669_1_, BlockPos p_180669_2_) {
      int i = Math.min(p_180669_1_.getEntitiesWithinAABB(Entity.class, PRESSURE_AABB.offset(p_180669_2_)).size(), this.maxWeight);
      if (i > 0) {
         float f = (float)Math.min(this.maxWeight, i) / (float)this.maxWeight;
         return MathHelper.ceil(f * 15.0F);
      } else {
         return 0;
      }
   }

   protected void playClickOnSound(IWorld p_185507_1_, BlockPos p_185507_2_) {
      p_185507_1_.playSound(null, p_185507_2_, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.90000004F);
   }

   protected void playClickOffSound(IWorld p_185508_1_, BlockPos p_185508_2_) {
      p_185508_1_.playSound(null, p_185508_2_, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.75F);
   }

   protected int getRedstoneStrength(IBlockState p_176576_1_) {
      return p_176576_1_.get(POWER);
   }

   protected IBlockState setRedstoneStrength(IBlockState p_176575_1_, int p_176575_2_) {
      return p_176575_1_.with(POWER, Integer.valueOf(p_176575_2_));
   }

   public int tickRate(IWorldReaderBase p_149738_1_) {
      return 10;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(POWER);
   }
}
