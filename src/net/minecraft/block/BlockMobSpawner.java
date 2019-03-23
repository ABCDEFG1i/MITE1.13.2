package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockMobSpawner extends BlockContainer {
   protected BlockMobSpawner(Block.Properties p_i48364_1_) {
      super(p_i48364_1_);
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntityMobSpawner();
   }

   public IItemProvider getItemDropped(IBlockState p_199769_1_, World p_199769_2_, BlockPos p_199769_3_, int p_199769_4_) {
      return Items.AIR;
   }

   public void dropBlockAsItemWithChance(IBlockState p_196255_1_, World p_196255_2_, BlockPos p_196255_3_, float p_196255_4_, int p_196255_5_) {
      super.dropBlockAsItemWithChance(p_196255_1_, p_196255_2_, p_196255_3_, p_196255_4_, p_196255_5_);
      int i = 15 + p_196255_2_.rand.nextInt(15) + p_196255_2_.rand.nextInt(15);
      this.dropXpOnBlockBreak(p_196255_2_, p_196255_3_, i);
   }

   public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
      return EnumBlockRenderType.MODEL;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, IBlockState p_185473_3_) {
      return ItemStack.EMPTY;
   }
}
