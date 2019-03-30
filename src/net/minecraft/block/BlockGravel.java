package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockGravel extends BlockFalling {
   public BlockGravel(Block.Properties p_i48384_1_) {
      super(p_i48384_1_);
   }

   public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      if (fortuneLevel > 3) {
         fortuneLevel = 3;
      }

      //Changed to make it more hard to get flint
      if (worldIn.rand.nextInt(32 - fortuneLevel * 6) == 0) {
         return Items.FLINT;
      } else {
         return super.getItemDropped(blockCurrentState, worldIn, blockAt, fortuneLevel);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(IBlockState p_189876_1_) {
      return -8356741;
   }
}
