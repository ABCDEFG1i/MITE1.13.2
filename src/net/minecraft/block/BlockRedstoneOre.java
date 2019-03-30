package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockRedstoneOre extends Block {
   public static final BooleanProperty LIT = BlockRedstoneTorch.LIT;

   public BlockRedstoneOre(Block.Properties p_i48345_1_) {
      super(p_i48345_1_);
      this.setDefaultState(this.getDefaultState().with(LIT, Boolean.valueOf(false)));
   }

   public int getLightValue(IBlockState p_149750_1_) {
      return p_149750_1_.get(LIT) ? super.getLightValue(p_149750_1_) : 0;
   }

   public void onBlockClicked(IBlockState p_196270_1_, World p_196270_2_, BlockPos p_196270_3_, EntityPlayer p_196270_4_) {
      activate(p_196270_1_, p_196270_2_, p_196270_3_);
      super.onBlockClicked(p_196270_1_, p_196270_2_, p_196270_3_, p_196270_4_);
   }

   public void onEntityWalk(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {
      activate(p_176199_1_.getBlockState(p_176199_2_), p_176199_1_, p_176199_2_);
      super.onEntityWalk(p_176199_1_, p_176199_2_, p_176199_3_);
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      activate(p_196250_1_, p_196250_2_, p_196250_3_);
      return super.onBlockActivated(p_196250_1_, p_196250_2_, p_196250_3_, p_196250_4_, p_196250_5_, p_196250_6_, p_196250_7_, p_196250_8_, p_196250_9_);
   }

   private static void activate(IBlockState p_196500_0_, World p_196500_1_, BlockPos p_196500_2_) {
      spawnParticles(p_196500_1_, p_196500_2_);
      if (!p_196500_0_.get(LIT)) {
         p_196500_1_.setBlockState(p_196500_2_, p_196500_0_.with(LIT, Boolean.valueOf(true)), 3);
      }

   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (p_196267_1_.get(LIT)) {
         p_196267_2_.setBlockState(p_196267_3_, p_196267_1_.with(LIT, Boolean.valueOf(false)), 3);
      }

   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Items.REDSTONE;
   }

   public int getItemsToDropCount(IBlockState p_196251_1_, int p_196251_2_, World p_196251_3_, BlockPos p_196251_4_, Random p_196251_5_) {
      return this.quantityDropped(p_196251_1_, p_196251_5_) + p_196251_5_.nextInt(p_196251_2_ + 1);
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return 4 + p_196264_2_.nextInt(2);
   }

    public void dropBlockAsItemWithChance(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, float chanceToDrop, int fortuneLevel) {
        super.dropBlockAsItemWithChance(blockCurrentState, worldIn, blockAt, chanceToDrop, fortuneLevel);
        if (this.getItemDropped(blockCurrentState, worldIn, blockAt, fortuneLevel) != this) {
            int i = 1 + worldIn.rand.nextInt(5);
            this.dropXpOnBlockBreak(worldIn, blockAt, i);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_1_.get(LIT)) {
         spawnParticles(p_180655_2_, p_180655_3_);
      }

   }

   private static void spawnParticles(World p_180691_0_, BlockPos p_180691_1_) {
      double d0 = 0.5625D;
      Random random = p_180691_0_.rand;

      for(EnumFacing enumfacing : EnumFacing.values()) {
         BlockPos blockpos = p_180691_1_.offset(enumfacing);
         if (!p_180691_0_.getBlockState(blockpos).isOpaqueCube(p_180691_0_, blockpos)) {
            EnumFacing.Axis enumfacing$axis = enumfacing.getAxis();
            double d1 = enumfacing$axis == EnumFacing.Axis.X ? 0.5D + 0.5625D * (double)enumfacing.getXOffset() : (double)random.nextFloat();
            double d2 = enumfacing$axis == EnumFacing.Axis.Y ? 0.5D + 0.5625D * (double)enumfacing.getYOffset() : (double)random.nextFloat();
            double d3 = enumfacing$axis == EnumFacing.Axis.Z ? 0.5D + 0.5625D * (double)enumfacing.getZOffset() : (double)random.nextFloat();
            p_180691_0_.spawnParticle(RedstoneParticleData.REDSTONE_DUST, (double)p_180691_1_.getX() + d1, (double)p_180691_1_.getY() + d2, (double)p_180691_1_.getZ() + d3, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(LIT);
   }
}
