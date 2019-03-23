package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BehaviorProjectileDispense extends BehaviorDefaultDispenseItem {
   public ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
      World world = p_82487_1_.getWorld();
      IPosition iposition = BlockDispenser.getDispensePosition(p_82487_1_);
      EnumFacing enumfacing = p_82487_1_.getBlockState().get(BlockDispenser.FACING);
      IProjectile iprojectile = this.getProjectileEntity(world, iposition, p_82487_2_);
      iprojectile.shoot((double)enumfacing.getXOffset(), (double)((float)enumfacing.getYOffset() + 0.1F), (double)enumfacing.getZOffset(), this.getProjectileVelocity(), this.getProjectileInaccuracy());
      world.spawnEntity((Entity)iprojectile);
      p_82487_2_.shrink(1);
      return p_82487_2_;
   }

   protected void playDispenseSound(IBlockSource p_82485_1_) {
      p_82485_1_.getWorld().playEvent(1002, p_82485_1_.getBlockPos(), 0);
   }

   protected abstract IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_, ItemStack p_82499_3_);

   protected float getProjectileInaccuracy() {
      return 6.0F;
   }

   protected float getProjectileVelocity() {
      return 1.1F;
   }
}
