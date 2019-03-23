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

   public IItemProvider getItemDropped(IBlockState p_199769_1_, World p_199769_2_, BlockPos p_199769_3_, int p_199769_4_) {
      if (p_199769_4_ > 3) {
         p_199769_4_ = 3;
      }

      return (IItemProvider)(p_199769_2_.rand.nextInt(10 - p_199769_4_ * 3) == 0 ? Items.FLINT : super.getItemDropped(p_199769_1_, p_199769_2_, p_199769_3_, p_199769_4_));
   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(IBlockState p_189876_1_) {
      return -8356741;
   }
}
