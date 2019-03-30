package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockBarrier extends Block {
   protected BlockBarrier(Block.Properties p_i48447_1_) {
      super(p_i48447_1_);
   }

   public boolean func_200123_i(IBlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return true;
   }

   public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public boolean isSolid(IBlockState p_200124_1_) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public float getAmbientOcclusionLightValue(IBlockState p_185485_1_) {
      return 1.0F;
   }

    public void dropBlockAsItemWithChance(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, float chanceToDrop, int fortuneLevel) {
   }
}
