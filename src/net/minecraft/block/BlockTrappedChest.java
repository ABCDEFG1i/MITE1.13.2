package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityTrappedChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;

public class BlockTrappedChest extends BlockChest {
   public BlockTrappedChest(Block.Properties p_i48306_1_) {
      super(p_i48306_1_);
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntityTrappedChest();
   }

   protected Stat<ResourceLocation> func_196310_d() {
      return StatList.CUSTOM.func_199076_b(StatList.TRIGGER_TRAPPED_CHEST);
   }

   public boolean canProvidePower(IBlockState p_149744_1_) {
      return true;
   }

   public int getWeakPower(IBlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, EnumFacing p_180656_4_) {
      return MathHelper.clamp(TileEntityChest.getPlayersUsing(p_180656_2_, p_180656_3_), 0, 15);
   }

   public int getStrongPower(IBlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, EnumFacing p_176211_4_) {
      return p_176211_4_ == EnumFacing.UP ? p_176211_1_.getWeakPower(p_176211_2_, p_176211_3_, p_176211_4_) : 0;
   }
}
