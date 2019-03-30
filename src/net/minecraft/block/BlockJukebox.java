package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityJukebox;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockJukebox extends BlockContainer {
   public static final BooleanProperty HAS_RECORD = BlockStateProperties.HAS_RECORD;

   protected BlockJukebox(Block.Properties p_i48372_1_) {
      super(p_i48372_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(HAS_RECORD, Boolean.valueOf(false)));
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (p_196250_1_.get(HAS_RECORD)) {
         this.func_203419_a(p_196250_2_, p_196250_3_);
         p_196250_1_ = p_196250_1_.with(HAS_RECORD, Boolean.valueOf(false));
         p_196250_2_.setBlockState(p_196250_3_, p_196250_1_, 2);
         return true;
      } else {
         return false;
      }
   }

   public void insertRecord(IWorld p_176431_1_, BlockPos p_176431_2_, IBlockState p_176431_3_, ItemStack p_176431_4_) {
      TileEntity tileentity = p_176431_1_.getTileEntity(p_176431_2_);
      if (tileentity instanceof TileEntityJukebox) {
         ((TileEntityJukebox)tileentity).setRecord(p_176431_4_.copy());
         p_176431_1_.setBlockState(p_176431_2_, p_176431_3_.with(HAS_RECORD, Boolean.valueOf(true)), 2);
      }
   }

   private void func_203419_a(World p_203419_1_, BlockPos p_203419_2_) {
      if (!p_203419_1_.isRemote) {
         TileEntity tileentity = p_203419_1_.getTileEntity(p_203419_2_);
         if (tileentity instanceof TileEntityJukebox) {
            TileEntityJukebox tileentityjukebox = (TileEntityJukebox)tileentity;
            ItemStack itemstack = tileentityjukebox.getRecord();
            if (!itemstack.isEmpty()) {
               p_203419_1_.playEvent(1010, p_203419_2_, 0);
               p_203419_1_.playRecord(p_203419_2_, null);
               tileentityjukebox.setRecord(ItemStack.EMPTY);
               float f = 0.7F;
               double d0 = (double)(p_203419_1_.rand.nextFloat() * 0.7F) + (double)0.15F;
               double d1 = (double)(p_203419_1_.rand.nextFloat() * 0.7F) + (double)0.060000002F + 0.6D;
               double d2 = (double)(p_203419_1_.rand.nextFloat() * 0.7F) + (double)0.15F;
               ItemStack itemstack1 = itemstack.copy();
               EntityItem entityitem = new EntityItem(p_203419_1_, (double)p_203419_2_.getX() + d0, (double)p_203419_2_.getY() + d1, (double)p_203419_2_.getZ() + d2, itemstack1);
               entityitem.setDefaultPickupDelay();
               p_203419_1_.spawnEntity(entityitem);
            }
         }
      }
   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         this.func_203419_a(p_196243_2_, p_196243_3_);
         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

    public void dropBlockAsItemWithChance(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, float chanceToDrop, int fortuneLevel) {
        if (!worldIn.isRemote) {
            super.dropBlockAsItemWithChance(blockCurrentState, worldIn, blockAt, chanceToDrop, 0);
      }
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntityJukebox();
   }

   public boolean hasComparatorInputOverride(IBlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      TileEntity tileentity = p_180641_2_.getTileEntity(p_180641_3_);
      if (tileentity instanceof TileEntityJukebox) {
         Item item = ((TileEntityJukebox)tileentity).getRecord().getItem();
         if (item instanceof ItemRecord) {
            return ((ItemRecord)item).getComparatorValue();
         }
      }

      return 0;
   }

   public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
      return EnumBlockRenderType.MODEL;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(HAS_RECORD);
   }
}
