package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemLead extends Item {
   public ItemLead(Item.Properties p_i48484_1_) {
      super(p_i48484_1_);
   }

   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos();
      Block block = world.getBlockState(blockpos).getBlock();
      if (block instanceof BlockFence) {
         EntityPlayer entityplayer = p_195939_1_.getPlayer();
         if (!world.isRemote && entityplayer != null) {
            attachToFence(entityplayer, world, blockpos);
         }

         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.PASS;
      }
   }

   public static boolean attachToFence(EntityPlayer p_180618_0_, World p_180618_1_, BlockPos p_180618_2_) {
      EntityLeashKnot entityleashknot = EntityLeashKnot.getKnotForPosition(p_180618_1_, p_180618_2_);
      boolean flag = false;
      double d0 = 7.0D;
      int i = p_180618_2_.getX();
      int j = p_180618_2_.getY();
      int k = p_180618_2_.getZ();

      for(EntityLiving entityliving : p_180618_1_.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB((double)i - 7.0D, (double)j - 7.0D, (double)k - 7.0D, (double)i + 7.0D, (double)j + 7.0D, (double)k + 7.0D))) {
         if (entityliving.getLeashed() && entityliving.getLeashHolder() == p_180618_0_) {
            if (entityleashknot == null) {
               entityleashknot = EntityLeashKnot.createKnot(p_180618_1_, p_180618_2_);
            }

            entityliving.setLeashHolder(entityleashknot, true);
            flag = true;
         }
      }

      return flag;
   }
}
