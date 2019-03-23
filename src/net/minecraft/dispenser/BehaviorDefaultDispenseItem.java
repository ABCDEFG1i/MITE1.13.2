package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorDefaultDispenseItem implements IBehaviorDispenseItem {
   public final ItemStack dispense(IBlockSource p_dispense_1_, ItemStack p_dispense_2_) {
      ItemStack itemstack = this.dispenseStack(p_dispense_1_, p_dispense_2_);
      this.playDispenseSound(p_dispense_1_);
      this.spawnDispenseParticles(p_dispense_1_, p_dispense_1_.getBlockState().get(BlockDispenser.FACING));
      return itemstack;
   }

   protected ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
      EnumFacing enumfacing = p_82487_1_.getBlockState().get(BlockDispenser.FACING);
      IPosition iposition = BlockDispenser.getDispensePosition(p_82487_1_);
      ItemStack itemstack = p_82487_2_.split(1);
      doDispense(p_82487_1_.getWorld(), itemstack, 6, enumfacing, iposition);
      return p_82487_2_;
   }

   public static void doDispense(World p_82486_0_, ItemStack p_82486_1_, int p_82486_2_, EnumFacing p_82486_3_, IPosition p_82486_4_) {
      double d0 = p_82486_4_.getX();
      double d1 = p_82486_4_.getY();
      double d2 = p_82486_4_.getZ();
      if (p_82486_3_.getAxis() == EnumFacing.Axis.Y) {
         d1 = d1 - 0.125D;
      } else {
         d1 = d1 - 0.15625D;
      }

      EntityItem entityitem = new EntityItem(p_82486_0_, d0, d1, d2, p_82486_1_);
      double d3 = p_82486_0_.rand.nextDouble() * 0.1D + 0.2D;
      entityitem.motionX = (double)p_82486_3_.getXOffset() * d3;
      entityitem.motionY = (double)0.2F;
      entityitem.motionZ = (double)p_82486_3_.getZOffset() * d3;
      entityitem.motionX += p_82486_0_.rand.nextGaussian() * (double)0.0075F * (double)p_82486_2_;
      entityitem.motionY += p_82486_0_.rand.nextGaussian() * (double)0.0075F * (double)p_82486_2_;
      entityitem.motionZ += p_82486_0_.rand.nextGaussian() * (double)0.0075F * (double)p_82486_2_;
      p_82486_0_.spawnEntity(entityitem);
   }

   protected void playDispenseSound(IBlockSource p_82485_1_) {
      p_82485_1_.getWorld().playEvent(1000, p_82485_1_.getBlockPos(), 0);
   }

   protected void spawnDispenseParticles(IBlockSource p_82489_1_, EnumFacing p_82489_2_) {
      p_82489_1_.getWorld().playEvent(2000, p_82489_1_.getBlockPos(), p_82489_2_.getIndex());
   }
}
