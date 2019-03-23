package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ComparatorMode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

public class BlockRedstoneComparator extends BlockRedstoneDiode implements ITileEntityProvider {
   public static final EnumProperty<ComparatorMode> MODE = BlockStateProperties.COMPARATOR_MODE;

   public BlockRedstoneComparator(Block.Properties p_i48424_1_) {
      super(p_i48424_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, EnumFacing.NORTH).with(POWERED, Boolean.valueOf(false)).with(MODE, ComparatorMode.COMPARE));
   }

   protected int getDelay(IBlockState p_196346_1_) {
      return 2;
   }

   protected int getActiveSignal(IBlockReader p_176408_1_, BlockPos p_176408_2_, IBlockState p_176408_3_) {
      TileEntity tileentity = p_176408_1_.getTileEntity(p_176408_2_);
      return tileentity instanceof TileEntityComparator ? ((TileEntityComparator)tileentity).getOutputSignal() : 0;
   }

   private int calculateOutput(World p_176460_1_, BlockPos p_176460_2_, IBlockState p_176460_3_) {
      return p_176460_3_.get(MODE) == ComparatorMode.SUBTRACT ? Math.max(this.calculateInputStrength(p_176460_1_, p_176460_2_, p_176460_3_) - this.getPowerOnSides(p_176460_1_, p_176460_2_, p_176460_3_), 0) : this.calculateInputStrength(p_176460_1_, p_176460_2_, p_176460_3_);
   }

   protected boolean shouldBePowered(World p_176404_1_, BlockPos p_176404_2_, IBlockState p_176404_3_) {
      int i = this.calculateInputStrength(p_176404_1_, p_176404_2_, p_176404_3_);
      if (i >= 15) {
         return true;
      } else if (i == 0) {
         return false;
      } else {
         return i >= this.getPowerOnSides(p_176404_1_, p_176404_2_, p_176404_3_);
      }
   }

   protected void func_211326_a(World p_211326_1_, BlockPos p_211326_2_) {
      p_211326_1_.removeTileEntity(p_211326_2_);
   }

   protected int calculateInputStrength(World p_176397_1_, BlockPos p_176397_2_, IBlockState p_176397_3_) {
      int i = super.calculateInputStrength(p_176397_1_, p_176397_2_, p_176397_3_);
      EnumFacing enumfacing = p_176397_3_.get(HORIZONTAL_FACING);
      BlockPos blockpos = p_176397_2_.offset(enumfacing);
      IBlockState iblockstate = p_176397_1_.getBlockState(blockpos);
      if (iblockstate.hasComparatorInputOverride()) {
         i = iblockstate.getComparatorInputOverride(p_176397_1_, blockpos);
      } else if (i < 15 && iblockstate.isNormalCube()) {
         blockpos = blockpos.offset(enumfacing);
         iblockstate = p_176397_1_.getBlockState(blockpos);
         if (iblockstate.hasComparatorInputOverride()) {
            i = iblockstate.getComparatorInputOverride(p_176397_1_, blockpos);
         } else if (iblockstate.isAir()) {
            EntityItemFrame entityitemframe = this.findItemFrame(p_176397_1_, enumfacing, blockpos);
            if (entityitemframe != null) {
               i = entityitemframe.getAnalogOutput();
            }
         }
      }

      return i;
   }

   @Nullable
   private EntityItemFrame findItemFrame(World p_176461_1_, EnumFacing p_176461_2_, BlockPos p_176461_3_) {
      List<EntityItemFrame> list = p_176461_1_.getEntitiesWithinAABB(EntityItemFrame.class, new AxisAlignedBB((double)p_176461_3_.getX(), (double)p_176461_3_.getY(), (double)p_176461_3_.getZ(), (double)(p_176461_3_.getX() + 1), (double)(p_176461_3_.getY() + 1), (double)(p_176461_3_.getZ() + 1)), (p_210304_1_) -> {
         return p_210304_1_ != null && p_210304_1_.getHorizontalFacing() == p_176461_2_;
      });
      return list.size() == 1 ? list.get(0) : null;
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (!p_196250_4_.capabilities.allowEdit) {
         return false;
      } else {
         p_196250_1_ = p_196250_1_.cycle(MODE);
         float f = p_196250_1_.get(MODE) == ComparatorMode.SUBTRACT ? 0.55F : 0.5F;
         p_196250_2_.playSound(p_196250_4_, p_196250_3_, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, f);
         p_196250_2_.setBlockState(p_196250_3_, p_196250_1_, 2);
         this.onStateChange(p_196250_2_, p_196250_3_, p_196250_1_);
         return true;
      }
   }

   protected void updateState(World p_176398_1_, BlockPos p_176398_2_, IBlockState p_176398_3_) {
      if (!p_176398_1_.getPendingBlockTicks().isTickPending(p_176398_2_, this)) {
         int i = this.calculateOutput(p_176398_1_, p_176398_2_, p_176398_3_);
         TileEntity tileentity = p_176398_1_.getTileEntity(p_176398_2_);
         int j = tileentity instanceof TileEntityComparator ? ((TileEntityComparator)tileentity).getOutputSignal() : 0;
         if (i != j || p_176398_3_.get(POWERED) != this.shouldBePowered(p_176398_1_, p_176398_2_, p_176398_3_)) {
            TickPriority tickpriority = this.isFacingTowardsRepeater(p_176398_1_, p_176398_2_, p_176398_3_) ? TickPriority.HIGH : TickPriority.NORMAL;
            p_176398_1_.getPendingBlockTicks().scheduleTick(p_176398_2_, this, 2, tickpriority);
         }

      }
   }

   private void onStateChange(World p_176462_1_, BlockPos p_176462_2_, IBlockState p_176462_3_) {
      int i = this.calculateOutput(p_176462_1_, p_176462_2_, p_176462_3_);
      TileEntity tileentity = p_176462_1_.getTileEntity(p_176462_2_);
      int j = 0;
      if (tileentity instanceof TileEntityComparator) {
         TileEntityComparator tileentitycomparator = (TileEntityComparator)tileentity;
         j = tileentitycomparator.getOutputSignal();
         tileentitycomparator.setOutputSignal(i);
      }

      if (j != i || p_176462_3_.get(MODE) == ComparatorMode.COMPARE) {
         boolean flag1 = this.shouldBePowered(p_176462_1_, p_176462_2_, p_176462_3_);
         boolean flag = p_176462_3_.get(POWERED);
         if (flag && !flag1) {
            p_176462_1_.setBlockState(p_176462_2_, p_176462_3_.with(POWERED, Boolean.valueOf(false)), 2);
         } else if (!flag && flag1) {
            p_176462_1_.setBlockState(p_176462_2_, p_176462_3_.with(POWERED, Boolean.valueOf(true)), 2);
         }

         this.notifyNeighbors(p_176462_1_, p_176462_2_, p_176462_3_);
      }

   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      this.onStateChange(p_196267_2_, p_196267_3_, p_196267_1_);
   }

   public boolean eventReceived(IBlockState p_189539_1_, World p_189539_2_, BlockPos p_189539_3_, int p_189539_4_, int p_189539_5_) {
      super.eventReceived(p_189539_1_, p_189539_2_, p_189539_3_, p_189539_4_, p_189539_5_);
      TileEntity tileentity = p_189539_2_.getTileEntity(p_189539_3_);
      return tileentity != null && tileentity.receiveClientEvent(p_189539_4_, p_189539_5_);
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntityComparator();
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(HORIZONTAL_FACING, MODE, POWERED);
   }
}
