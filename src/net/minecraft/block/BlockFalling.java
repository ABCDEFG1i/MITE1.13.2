package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockFalling extends Block {
   public static boolean fallInstantly;

   public BlockFalling(Block.Properties p_i48401_1_) {
      super(p_i48401_1_);
   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      p_196259_2_.getPendingBlockTicks().scheduleTick(p_196259_3_, this, this.tickRate(p_196259_2_));
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, this.tickRate(p_196271_4_));
      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (!p_196267_2_.isRemote) {
         this.checkFallable(p_196267_2_, p_196267_3_);
      }

   }

   private void checkFallable(World p_176503_1_, BlockPos p_176503_2_) {
      if (canFallThrough(p_176503_1_.getBlockState(p_176503_2_.down())) && p_176503_2_.getY() >= 0) {
         int i = 32;
         if (!fallInstantly && p_176503_1_.isAreaLoaded(p_176503_2_.add(-32, -32, -32), p_176503_2_.add(32, 32, 32))) {
            if (!p_176503_1_.isRemote) {
               EntityFallingBlock entityfallingblock = new EntityFallingBlock(p_176503_1_, (double)p_176503_2_.getX() + 0.5D, (double)p_176503_2_.getY(), (double)p_176503_2_.getZ() + 0.5D, p_176503_1_.getBlockState(p_176503_2_));
               this.onStartFalling(entityfallingblock);
               p_176503_1_.spawnEntity(entityfallingblock);
            }
         } else {
            if (p_176503_1_.getBlockState(p_176503_2_).getBlock() == this) {
               p_176503_1_.removeBlock(p_176503_2_);
            }

            BlockPos blockpos;
            for(blockpos = p_176503_2_.down(); canFallThrough(p_176503_1_.getBlockState(blockpos)) && blockpos.getY() > 0; blockpos = blockpos.down()) {
               ;
            }

            if (blockpos.getY() > 0) {
               p_176503_1_.setBlockState(blockpos.up(), this.getDefaultState());
            }
         }

      }
   }

   protected void onStartFalling(EntityFallingBlock p_149829_1_) {
   }

   public int tickRate(IWorldReaderBase p_149738_1_) {
      return 2;
   }

   public static boolean canFallThrough(IBlockState p_185759_0_) {
      Block block = p_185759_0_.getBlock();
      Material material = p_185759_0_.getMaterial();
      return p_185759_0_.isAir() || block == Blocks.FIRE || material.isLiquid() || material.isReplaceable();
   }

   public void onEndFalling(World p_176502_1_, BlockPos p_176502_2_, IBlockState p_176502_3_, IBlockState p_176502_4_) {
   }

   public void onBroken(World p_190974_1_, BlockPos p_190974_2_) {
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_4_.nextInt(16) == 0) {
         BlockPos blockpos = p_180655_3_.down();
         if (canFallThrough(p_180655_2_.getBlockState(blockpos))) {
            double d0 = (double)((float)p_180655_3_.getX() + p_180655_4_.nextFloat());
            double d1 = (double)p_180655_3_.getY() - 0.05D;
            double d2 = (double)((float)p_180655_3_.getZ() + p_180655_4_.nextFloat());
            p_180655_2_.spawnParticle(new BlockParticleData(Particles.FALLING_DUST, p_180655_1_), d0, d1, d2, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(IBlockState p_189876_1_) {
      return -16777216;
   }
}
