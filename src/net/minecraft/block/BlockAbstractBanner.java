package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class BlockAbstractBanner extends BlockContainer {
   private final EnumDyeColor color;

   protected BlockAbstractBanner(EnumDyeColor p_i48453_1_, Block.Properties p_i48453_2_) {
      super(p_i48453_2_);
      this.color = p_i48453_1_;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public boolean canSpawnInBlock() {
      return true;
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntityBanner(this.color);
   }

   public IItemProvider getItemDropped(IBlockState p_199769_1_, World p_199769_2_, BlockPos p_199769_3_, int p_199769_4_) {
      return Items.WHITE_BANNER;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, IBlockState p_185473_3_) {
      TileEntity tileentity = p_185473_1_.getTileEntity(p_185473_2_);
      return tileentity instanceof TileEntityBanner ? ((TileEntityBanner)tileentity).getItem(p_185473_3_) : super.getItem(p_185473_1_, p_185473_2_, p_185473_3_);
   }

   public void dropBlockAsItemWithChance(IBlockState p_196255_1_, World p_196255_2_, BlockPos p_196255_3_, float p_196255_4_, int p_196255_5_) {
      spawnAsEntity(p_196255_2_, p_196255_3_, this.getItem(p_196255_2_, p_196255_3_, p_196255_1_));
   }

   public void harvestBlock(World p_180657_1_, EntityPlayer p_180657_2_, BlockPos p_180657_3_, IBlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      if (p_180657_5_ instanceof TileEntityBanner) {
         spawnAsEntity(p_180657_1_, p_180657_3_, ((TileEntityBanner)p_180657_5_).getItem(p_180657_4_));
         p_180657_2_.func_71029_a(StatList.BLOCK_MINED.func_199076_b(this));
      } else {
         super.harvestBlock(p_180657_1_, p_180657_2_, p_180657_3_, p_180657_4_, (TileEntity)null, p_180657_6_);
      }

   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, IBlockState p_180633_3_, @Nullable EntityLivingBase p_180633_4_, ItemStack p_180633_5_) {
      TileEntity tileentity = p_180633_1_.getTileEntity(p_180633_2_);
      if (tileentity instanceof TileEntityBanner) {
         ((TileEntityBanner)tileentity).loadFromItemStack(p_180633_5_, this.color);
      }

   }

   public EnumDyeColor getColor() {
      return this.color;
   }
}
