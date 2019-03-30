package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityTurtle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockTurtleEgg extends Block {
   private static final VoxelShape field_203172_c = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 12.0D, 7.0D, 12.0D);
   private static final VoxelShape field_206843_t = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 7.0D, 15.0D);
   public static final IntegerProperty HATCH = BlockStateProperties.HATCH_0_2;
   public static final IntegerProperty EGGS = BlockStateProperties.EGGS_1_4;

   public BlockTurtleEgg(Block.Properties p_i48778_1_) {
      super(p_i48778_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(HATCH, Integer.valueOf(0)).with(EGGS, Integer.valueOf(1)));
   }

   public void onEntityWalk(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {
      this.tryTrample(p_176199_1_, p_176199_2_, p_176199_3_, 100);
      super.onEntityWalk(p_176199_1_, p_176199_2_, p_176199_3_);
   }

   public void onFallenUpon(World p_180658_1_, BlockPos p_180658_2_, Entity p_180658_3_, float p_180658_4_) {
      if (!(p_180658_3_ instanceof EntityZombie)) {
         this.tryTrample(p_180658_1_, p_180658_2_, p_180658_3_, 3);
      }

      super.onFallenUpon(p_180658_1_, p_180658_2_, p_180658_3_, p_180658_4_);
   }

   private void tryTrample(World p_203167_1_, BlockPos p_203167_2_, Entity p_203167_3_, int p_203167_4_) {
      if (!this.func_212570_a(p_203167_1_, p_203167_3_)) {
         super.onEntityWalk(p_203167_1_, p_203167_2_, p_203167_3_);
      } else {
         if (!p_203167_1_.isRemote && p_203167_1_.rand.nextInt(p_203167_4_) == 0) {
            this.removeOneEgg(p_203167_1_, p_203167_2_, p_203167_1_.getBlockState(p_203167_2_));
         }

      }
   }

   private void removeOneEgg(World p_203166_1_, BlockPos p_203166_2_, IBlockState p_203166_3_) {
      p_203166_1_.playSound(null, p_203166_2_, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + p_203166_1_.rand.nextFloat() * 0.2F);
      int i = p_203166_3_.get(EGGS);
      if (i <= 1) {
         p_203166_1_.destroyBlock(p_203166_2_, false);
      } else {
         p_203166_1_.setBlockState(p_203166_2_, p_203166_3_.with(EGGS, Integer.valueOf(i - 1)), 2);
         p_203166_1_.playEvent(2001, p_203166_2_, Block.getStateId(p_203166_3_));
      }

   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (this.canGrow(p_196267_2_) && this.hasProperHabitat(p_196267_2_, p_196267_3_)) {
         int i = p_196267_1_.get(HATCH);
         if (i < 2) {
            p_196267_2_.playSound(null, p_196267_3_, SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7F, 0.9F + p_196267_4_.nextFloat() * 0.2F);
            p_196267_2_.setBlockState(p_196267_3_, p_196267_1_.with(HATCH, Integer.valueOf(i + 1)), 2);
         } else {
            p_196267_2_.playSound(null, p_196267_3_, SoundEvents.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7F, 0.9F + p_196267_4_.nextFloat() * 0.2F);
            p_196267_2_.removeBlock(p_196267_3_);
            if (!p_196267_2_.isRemote) {
               for(int j = 0; j < p_196267_1_.get(EGGS); ++j) {
                  p_196267_2_.playEvent(2001, p_196267_3_, Block.getStateId(p_196267_1_));
                  EntityTurtle entityturtle = new EntityTurtle(p_196267_2_);
                  entityturtle.setGrowingAge(-24000);
                  entityturtle.setHome(p_196267_3_);
                  entityturtle.setLocationAndAngles((double)p_196267_3_.getX() + 0.3D + (double)j * 0.2D, (double)p_196267_3_.getY(), (double)p_196267_3_.getZ() + 0.3D, 0.0F, 0.0F);
                  p_196267_2_.spawnEntity(entityturtle);
               }
            }
         }
      }

   }

   private boolean hasProperHabitat(IBlockReader p_203168_1_, BlockPos p_203168_2_) {
      return p_203168_1_.getBlockState(p_203168_2_.down()).getBlock() == Blocks.SAND;
   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      if (this.hasProperHabitat(p_196259_2_, p_196259_3_) && !p_196259_2_.isRemote) {
         p_196259_2_.playEvent(2005, p_196259_3_, 0);
      }

   }

   private boolean canGrow(World p_203169_1_) {
      float f = p_203169_1_.getCelestialAngle(1.0F);
      if ((double)f < 0.69D && (double)f > 0.65D) {
         return true;
      } else {
         return p_203169_1_.rand.nextInt(500) == 0;
      }
   }

   protected boolean canSilkHarvest() {
      return true;
   }

   public void harvestBlock(World p_180657_1_, EntityPlayer p_180657_2_, BlockPos p_180657_3_, IBlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      super.harvestBlock(p_180657_1_, p_180657_2_, p_180657_3_, p_180657_4_, p_180657_5_, p_180657_6_);
      this.removeOneEgg(p_180657_1_, p_180657_3_, p_180657_4_);
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Items.AIR;
   }

   public boolean isReplaceable(IBlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      return p_196253_2_.getItem().getItem() == this.asItem() && p_196253_1_.get(EGGS) < 4 || super.isReplaceable(p_196253_1_, p_196253_2_);
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockState iblockstate = p_196258_1_.getWorld().getBlockState(p_196258_1_.getPos());
      return iblockstate.getBlock() == this ? iblockstate.with(EGGS, Integer.valueOf(Math.min(4, iblockstate.get(EGGS) + 1))) : super.getStateForPlacement(p_196258_1_);
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return p_196244_1_.get(EGGS) > 1 ? field_206843_t : field_203172_c;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(HATCH, EGGS);
   }

   private boolean func_212570_a(World p_212570_1_, Entity p_212570_2_) {
      if (p_212570_2_ instanceof EntityTurtle) {
         return false;
      } else {
         return !(p_212570_2_ instanceof EntityLivingBase) || p_212570_2_ instanceof EntityPlayer || p_212570_1_.getGameRules().getBoolean("mobGriefing");
      }
   }
}
