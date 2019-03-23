package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockIce extends BlockBreakable {
   public BlockIce(Block.Properties p_i48375_1_) {
      super(p_i48375_1_);
   }

   public int getOpacity(IBlockState p_200011_1_, IBlockReader p_200011_2_, BlockPos p_200011_3_) {
      return Blocks.WATER.getDefaultState().getOpacity(p_200011_2_, p_200011_3_);
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public void harvestBlock(World p_180657_1_, EntityPlayer p_180657_2_, BlockPos p_180657_3_, IBlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      p_180657_2_.func_71029_a(StatList.BLOCK_MINED.func_199076_b(this));
      p_180657_2_.addExhaustion(0.005F);
      if (this.canSilkHarvest() && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, p_180657_6_) > 0) {
         spawnAsEntity(p_180657_1_, p_180657_3_, this.getSilkTouchDrop(p_180657_4_));
      } else {
         if (p_180657_1_.dimension.doesWaterVaporize()) {
            p_180657_1_.removeBlock(p_180657_3_);
            return;
         }

         int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, p_180657_6_);
         p_180657_4_.dropBlockAsItem(p_180657_1_, p_180657_3_, i);
         Material material = p_180657_1_.getBlockState(p_180657_3_.down()).getMaterial();
         if (material.blocksMovement() || material.isLiquid()) {
            p_180657_1_.setBlockState(p_180657_3_, Blocks.WATER.getDefaultState());
         }
      }

   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return 0;
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (p_196267_2_.getLightFor(EnumLightType.BLOCK, p_196267_3_) > 11 - p_196267_1_.getOpacity(p_196267_2_, p_196267_3_)) {
         this.turnIntoWater(p_196267_1_, p_196267_2_, p_196267_3_);
      }

   }

   protected void turnIntoWater(IBlockState p_196454_1_, World p_196454_2_, BlockPos p_196454_3_) {
      if (p_196454_2_.dimension.doesWaterVaporize()) {
         p_196454_2_.removeBlock(p_196454_3_);
      } else {
         p_196454_1_.dropBlockAsItem(p_196454_2_, p_196454_3_, 0);
         p_196454_2_.setBlockState(p_196454_3_, Blocks.WATER.getDefaultState());
         p_196454_2_.neighborChanged(p_196454_3_, Blocks.WATER, p_196454_3_);
      }
   }

   public EnumPushReaction getPushReaction(IBlockState p_149656_1_) {
      return EnumPushReaction.NORMAL;
   }
}
