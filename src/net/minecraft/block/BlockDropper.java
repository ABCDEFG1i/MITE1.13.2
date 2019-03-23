package net.minecraft.block;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BlockSourceImpl;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockDropper extends BlockDispenser {
   private static final IBehaviorDispenseItem DISPENSE_BEHAVIOR = new BehaviorDefaultDispenseItem();

   public BlockDropper(Block.Properties p_i48410_1_) {
      super(p_i48410_1_);
   }

   protected IBehaviorDispenseItem getBehavior(ItemStack p_149940_1_) {
      return DISPENSE_BEHAVIOR;
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntityDropper();
   }

   protected void dispense(World p_176439_1_, BlockPos p_176439_2_) {
      BlockSourceImpl blocksourceimpl = new BlockSourceImpl(p_176439_1_, p_176439_2_);
      TileEntityDispenser tileentitydispenser = blocksourceimpl.getBlockTileEntity();
      int i = tileentitydispenser.getDispenseSlot();
      if (i < 0) {
         p_176439_1_.playEvent(1001, p_176439_2_, 0);
      } else {
         ItemStack itemstack = tileentitydispenser.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            EnumFacing enumfacing = p_176439_1_.getBlockState(p_176439_2_).get(FACING);
            IInventory iinventory = TileEntityHopper.getInventoryAtPosition(p_176439_1_, p_176439_2_.offset(enumfacing));
            ItemStack itemstack1;
            if (iinventory == null) {
               itemstack1 = DISPENSE_BEHAVIOR.dispense(blocksourceimpl, itemstack);
            } else {
               itemstack1 = TileEntityHopper.putStackInInventoryAllSlots(tileentitydispenser, iinventory, itemstack.copy().split(1), enumfacing.getOpposite());
               if (itemstack1.isEmpty()) {
                  itemstack1 = itemstack.copy();
                  itemstack1.shrink(1);
               } else {
                  itemstack1 = itemstack.copy();
               }
            }

            tileentitydispenser.setInventorySlotContents(i, itemstack1);
         }
      }
   }
}
