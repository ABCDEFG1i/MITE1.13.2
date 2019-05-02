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
      if (worldIn.rand.nextInt(31) <= 4) {
         return Items.FLINT_SHARD;
      }
      if (worldIn.rand.nextInt(17)== 0){
         return Items.COPPER_NUGGET;
      }
      if (worldIn.rand.nextInt(53)==0){
         return Items.SILVER_NUGGET;
      }
      if (worldIn.rand.nextInt(95)==0){
         return Items.FLINT;
      }
      if (worldIn.rand.nextInt(161)==0){
         return Items.GOLD_NUGGET;
      }
      if (worldIn.rand.nextInt(495)==0){
         return Items.OBSIDIAN_SHARD;
      }
      if (worldIn.rand.nextInt(1457)==0){
         return Items.EMERALD_SHARD;
      }
      if (worldIn.rand.nextInt(4373)==0){
         return Items.DIAMOND_SHARD;
      }
      if (worldIn.rand.nextInt(13121)==0){
         return Items.MITHRIL_NUGGET;
      }
      if (worldIn.rand.nextInt(26243)==0){
         return Items.ADAMANTIUM_NUGGET;
      }
      return super.getItemDropped(blockCurrentState, worldIn, blockAt, fortuneLevel);
   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(IBlockState p_189876_1_) {
      return -8356741;
   }
}
