package net.minecraft.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemMinecart extends Item {
   private static final IBehaviorDispenseItem MINECART_DISPENSER_BEHAVIOR = new BehaviorDefaultDispenseItem() {
      private final BehaviorDefaultDispenseItem behaviourDefaultDispenseItem = new BehaviorDefaultDispenseItem();

      public ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
         EnumFacing enumfacing = p_82487_1_.getBlockState().get(BlockDispenser.FACING);
         World world = p_82487_1_.getWorld();
         double d0 = p_82487_1_.getX() + (double)enumfacing.getXOffset() * 1.125D;
         double d1 = Math.floor(p_82487_1_.getY()) + (double)enumfacing.getYOffset();
         double d2 = p_82487_1_.getZ() + (double)enumfacing.getZOffset() * 1.125D;
         BlockPos blockpos = p_82487_1_.getBlockPos().offset(enumfacing);
         IBlockState iblockstate = world.getBlockState(blockpos);
         RailShape railshape = iblockstate.getBlock() instanceof BlockRailBase ? iblockstate.get(((BlockRailBase)iblockstate.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
         double d3;
         if (iblockstate.isIn(BlockTags.RAILS)) {
            if (railshape.isAscending()) {
               d3 = 0.6D;
            } else {
               d3 = 0.1D;
            }
         } else {
            if (!iblockstate.isAir() || !world.getBlockState(blockpos.down()).isIn(BlockTags.RAILS)) {
               return this.behaviourDefaultDispenseItem.dispense(p_82487_1_, p_82487_2_);
            }

            IBlockState iblockstate1 = world.getBlockState(blockpos.down());
            RailShape railshape1 = iblockstate1.getBlock() instanceof BlockRailBase ? iblockstate1.get(((BlockRailBase)iblockstate1.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            if (enumfacing != EnumFacing.DOWN && railshape1.isAscending()) {
               d3 = -0.4D;
            } else {
               d3 = -0.9D;
            }
         }

         EntityMinecart entityminecart = EntityMinecart.create(world, d0, d1 + d3, d2, ((ItemMinecart)p_82487_2_.getItem()).minecartType);
         if (p_82487_2_.hasDisplayName()) {
            entityminecart.setCustomName(p_82487_2_.getDisplayName());
         }

         world.spawnEntity(entityminecart);
         p_82487_2_.shrink(1);
         return p_82487_2_;
      }

      protected void playDispenseSound(IBlockSource p_82485_1_) {
         p_82485_1_.getWorld().playEvent(1000, p_82485_1_.getBlockPos(), 0);
      }
   };
   private final EntityMinecart.Type minecartType;

   public ItemMinecart(EntityMinecart.Type p_i48480_1_, Item.Properties p_i48480_2_) {
      super(p_i48480_2_);
      this.minecartType = p_i48480_1_;
      BlockDispenser.registerDispenseBehavior(this, MINECART_DISPENSER_BEHAVIOR);
   }

   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos();
      IBlockState iblockstate = world.getBlockState(blockpos);
      if (!iblockstate.isIn(BlockTags.RAILS)) {
         return EnumActionResult.FAIL;
      } else {
         ItemStack itemstack = p_195939_1_.getItem();
         if (!world.isRemote) {
            RailShape railshape = iblockstate.getBlock() instanceof BlockRailBase ? iblockstate.get(((BlockRailBase)iblockstate.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            double d0 = 0.0D;
            if (railshape.isAscending()) {
               d0 = 0.5D;
            }

            EntityMinecart entityminecart = EntityMinecart.create(world, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.0625D + d0, (double)blockpos.getZ() + 0.5D, this.minecartType);
            if (itemstack.hasDisplayName()) {
               entityminecart.setCustomName(itemstack.getDisplayName());
            }

            world.spawnEntity(entityminecart);
         }

         itemstack.shrink(1);
         return EnumActionResult.SUCCESS;
      }
   }
}
