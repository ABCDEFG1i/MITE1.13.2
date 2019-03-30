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

public class ItemSeedFood extends ItemFood {
    private final IBlockState planetBlockState;

    public ItemSeedFood(int foodLevel, float foodSaturation, Block planetBlock, Item.Properties properties) {
        super(foodLevel, foodSaturation, false, properties);
        this.planetBlockState = planetBlock.getDefaultState();
   }

   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      IWorld iworld = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos().up();
       if (p_195939_1_.getFace() == EnumFacing.UP && iworld.isAirBlock(blockpos) && this.planetBlockState.isValidPosition(iworld, blockpos)) {
           iworld.setBlockState(blockpos, this.planetBlockState, 11);
         EntityPlayer entityplayer = p_195939_1_.getPlayer();
         ItemStack itemstack = p_195939_1_.getItem();
         if (entityplayer instanceof EntityPlayerMP) {
            CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)entityplayer, blockpos, itemstack);
         }

         itemstack.shrink(1);
         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.PASS;
      }
   }
}
