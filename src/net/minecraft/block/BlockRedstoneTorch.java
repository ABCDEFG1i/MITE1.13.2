package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockRedstoneTorch extends BlockTorch {
   public static final BooleanProperty LIT = BlockStateProperties.LIT;
   private static final Map<IBlockReader, List<BlockRedstoneTorch.Toggle>> field_196529_b = Maps.newHashMap();

   protected BlockRedstoneTorch(Block.Properties p_i48342_1_) {
      super(p_i48342_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(LIT, Boolean.valueOf(true)));
   }

   public int tickRate(IWorldReaderBase p_149738_1_) {
      return 2;
   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      for(EnumFacing enumfacing : EnumFacing.values()) {
         p_196259_2_.notifyNeighborsOfStateChange(p_196259_3_.offset(enumfacing), this);
      }

   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_) {
         for(EnumFacing enumfacing : EnumFacing.values()) {
            p_196243_2_.notifyNeighborsOfStateChange(p_196243_3_.offset(enumfacing), this);
         }

      }
   }

   public int getWeakPower(IBlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, EnumFacing p_180656_4_) {
      return p_180656_1_.get(LIT) && EnumFacing.UP != p_180656_4_ ? 15 : 0;
   }

   protected boolean shouldBeOff(World p_176597_1_, BlockPos p_176597_2_, IBlockState p_176597_3_) {
      return p_176597_1_.isSidePowered(p_176597_2_.down(), EnumFacing.DOWN);
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      update(p_196267_1_, p_196267_2_, p_196267_3_, p_196267_4_, this.shouldBeOff(p_196267_2_, p_196267_3_, p_196267_1_));
   }

   public static void update(IBlockState p_196527_0_, World p_196527_1_, BlockPos p_196527_2_, Random p_196527_3_, boolean p_196527_4_) {
      List<BlockRedstoneTorch.Toggle> list = field_196529_b.get(p_196527_1_);

      while(list != null && !list.isEmpty() && p_196527_1_.getTotalWorldTime() - (list.get(0)).time > 60L) {
         list.remove(0);
      }

      if (p_196527_0_.get(LIT)) {
         if (p_196527_4_) {
            p_196527_1_.setBlockState(p_196527_2_, p_196527_0_.with(LIT, Boolean.valueOf(false)), 3);
            if (isBurnedOut(p_196527_1_, p_196527_2_, true)) {
               p_196527_1_.playSound((EntityPlayer)null, p_196527_2_, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5F, 2.6F + (p_196527_1_.rand.nextFloat() - p_196527_1_.rand.nextFloat()) * 0.8F);

               for(int i = 0; i < 5; ++i) {
                  double d0 = (double)p_196527_2_.getX() + p_196527_3_.nextDouble() * 0.6D + 0.2D;
                  double d1 = (double)p_196527_2_.getY() + p_196527_3_.nextDouble() * 0.6D + 0.2D;
                  double d2 = (double)p_196527_2_.getZ() + p_196527_3_.nextDouble() * 0.6D + 0.2D;
                  p_196527_1_.spawnParticle(Particles.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
               }

               p_196527_1_.getPendingBlockTicks().scheduleTick(p_196527_2_, p_196527_1_.getBlockState(p_196527_2_).getBlock(), 160);
            }
         }
      } else if (!p_196527_4_ && !isBurnedOut(p_196527_1_, p_196527_2_, false)) {
         p_196527_1_.setBlockState(p_196527_2_, p_196527_0_.with(LIT, Boolean.valueOf(true)), 3);
      }

   }

   public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
      if (p_189540_1_.get(LIT) == this.shouldBeOff(p_189540_2_, p_189540_3_, p_189540_1_) && !p_189540_2_.getPendingBlockTicks().isTickPending(p_189540_3_, this)) {
         p_189540_2_.getPendingBlockTicks().scheduleTick(p_189540_3_, this, this.tickRate(p_189540_2_));
      }

   }

   public int getStrongPower(IBlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, EnumFacing p_176211_4_) {
      return p_176211_4_ == EnumFacing.DOWN ? p_176211_1_.getWeakPower(p_176211_2_, p_176211_3_, p_176211_4_) : 0;
   }

   public boolean canProvidePower(IBlockState p_149744_1_) {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_1_.get(LIT)) {
         double d0 = (double)p_180655_3_.getX() + 0.5D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D;
         double d1 = (double)p_180655_3_.getY() + 0.7D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D;
         double d2 = (double)p_180655_3_.getZ() + 0.5D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D;
         p_180655_2_.spawnParticle(RedstoneParticleData.REDSTONE_DUST, d0, d1, d2, 0.0D, 0.0D, 0.0D);
      }
   }

   public int getLightValue(IBlockState p_149750_1_) {
      return p_149750_1_.get(LIT) ? super.getLightValue(p_149750_1_) : 0;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(LIT);
   }

   private static boolean isBurnedOut(World p_176598_0_, BlockPos p_176598_1_, boolean p_176598_2_) {
      List<BlockRedstoneTorch.Toggle> list = field_196529_b.get(p_176598_0_);
      if (list == null) {
         list = Lists.newArrayList();
         field_196529_b.put(p_176598_0_, list);
      }

      if (p_176598_2_) {
         list.add(new BlockRedstoneTorch.Toggle(p_176598_1_.toImmutable(), p_176598_0_.getTotalWorldTime()));
      }

      int i = 0;

      for(int j = 0; j < list.size(); ++j) {
         BlockRedstoneTorch.Toggle blockredstonetorch$toggle = list.get(j);
         if (blockredstonetorch$toggle.pos.equals(p_176598_1_)) {
            ++i;
            if (i >= 8) {
               return true;
            }
         }
      }

      return false;
   }

   public static class Toggle {
      private final BlockPos pos;
      private final long time;

      public Toggle(BlockPos p_i45688_1_, long p_i45688_2_) {
         this.pos = p_i45688_1_;
         this.time = p_i45688_2_;
      }
   }
}
