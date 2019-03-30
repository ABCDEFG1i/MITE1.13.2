package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockTallGrass extends BlockBush implements IGrowable {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

   protected BlockTallGrass(Block.Properties p_i48310_1_) {
      super(p_i48310_1_);
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPE;
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
        return worldIn.rand.nextInt(8) == 0 ? Items.WHEAT_SEEDS : Items.AIR;
   }

   public int getItemsToDropCount(IBlockState p_196251_1_, int p_196251_2_, World p_196251_3_, BlockPos p_196251_4_, Random p_196251_5_) {
      return 1 + p_196251_5_.nextInt(p_196251_2_ * 2 + 1);
   }

   public void harvestBlock(World p_180657_1_, EntityPlayer p_180657_2_, BlockPos p_180657_3_, IBlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      if (!p_180657_1_.isRemote && p_180657_6_.getItem() == Items.SHEARS) {
         p_180657_2_.func_71029_a(StatList.BLOCK_MINED.func_199076_b(this));
         p_180657_2_.addExhaustion(0.005F);
         spawnAsEntity(p_180657_1_, p_180657_3_, new ItemStack(this));
      } else {
         super.harvestBlock(p_180657_1_, p_180657_2_, p_180657_3_, p_180657_4_, p_180657_5_, p_180657_6_);
      }

   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, IBlockState p_176473_3_, boolean p_176473_4_) {
      return true;
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, IBlockState p_180670_4_) {
      return true;
   }

   public void grow(World p_176474_1_, Random p_176474_2_, BlockPos p_176474_3_, IBlockState p_176474_4_) {
      BlockDoublePlant blockdoubleplant = (BlockDoublePlant)(this == Blocks.FERN ? Blocks.LARGE_FERN : Blocks.TALL_GRASS);
      if (blockdoubleplant.getDefaultState().isValidPosition(p_176474_1_, p_176474_3_) && p_176474_1_.isAirBlock(p_176474_3_.up())) {
         blockdoubleplant.placeAt(p_176474_1_, p_176474_3_, 2);
      }

   }

   public Block.EnumOffsetType getOffsetType() {
      return Block.EnumOffsetType.XYZ;
   }
}
