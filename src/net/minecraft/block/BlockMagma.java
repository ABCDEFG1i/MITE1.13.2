package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockMagma extends Block {
   public BlockMagma(Block.Properties p_i48366_1_) {
      super(p_i48366_1_);
   }

   public void onEntityWalk(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {
      if (!p_176199_3_.isImmuneToFire() && p_176199_3_ instanceof EntityLivingBase && !EnchantmentHelper.hasFrostWalker((EntityLivingBase)p_176199_3_)) {
         p_176199_3_.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F);
      }

      super.onEntityWalk(p_176199_1_, p_176199_2_, p_176199_3_);
   }

   @OnlyIn(Dist.CLIENT)
   public int getPackedLightmapCoords(IBlockState p_185484_1_, IWorldReader p_185484_2_, BlockPos p_185484_3_) {
      return 15728880;
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      BlockBubbleColumn.placeBubbleColumn(p_196267_2_, p_196267_3_.up(), true);
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ == EnumFacing.UP && p_196271_3_.getBlock() == Blocks.WATER) {
         p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, this.tickRate(p_196271_4_));
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public void randomTick(IBlockState p_196265_1_, World p_196265_2_, BlockPos p_196265_3_, Random p_196265_4_) {
      BlockPos blockpos = p_196265_3_.up();
      if (p_196265_2_.getFluidState(p_196265_3_).isTagged(FluidTags.WATER)) {
         p_196265_2_.playSound(null, p_196265_3_, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (p_196265_2_.rand.nextFloat() - p_196265_2_.rand.nextFloat()) * 0.8F);
         if (p_196265_2_ instanceof WorldServer) {
            ((WorldServer)p_196265_2_).spawnParticle(Particles.LARGE_SMOKE, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.25D, (double)blockpos.getZ() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D);
         }
      }

   }

   public int tickRate(IWorldReaderBase p_149738_1_) {
      return 20;
   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      p_196259_2_.getPendingBlockTicks().scheduleTick(p_196259_3_, this, this.tickRate(p_196259_2_));
   }

   public boolean canEntitySpawn(IBlockState p_189872_1_, Entity p_189872_2_) {
      return p_189872_2_.isImmuneToFire();
   }

   public boolean needsPostProcessing(IBlockState p_201783_1_, IBlockReader p_201783_2_, BlockPos p_201783_3_) {
      return true;
   }
}
