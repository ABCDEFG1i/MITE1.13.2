package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class ItemSeeds extends Item {
   private final IBlockState field_195978_a;

   public ItemSeeds(Block p_i48472_1_, Item.Properties p_i48472_2_) {
      super(p_i48472_2_);
      this.field_195978_a = p_i48472_1_.getDefaultState();
   }

   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      IWorld iworld = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos().up();
      if (p_195939_1_.getFace() == EnumFacing.UP && iworld.isAirBlock(blockpos) && this.field_195978_a.isValidPosition(iworld, blockpos)) {
         iworld.setBlockState(blockpos, this.field_195978_a, 11);
         ItemStack itemstack = p_195939_1_.getItem();
         EntityPlayer entityplayer = p_195939_1_.getPlayer();
         if (entityplayer instanceof EntityPlayerMP) {
            CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)entityplayer, blockpos, itemstack);
         }

         itemstack.shrink(1);
         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.FAIL;
      }
   }
}
