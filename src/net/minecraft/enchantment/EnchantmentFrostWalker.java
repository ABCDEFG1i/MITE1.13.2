package net.minecraft.enchantment;

import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EnchantmentFrostWalker extends Enchantment {
   public EnchantmentFrostWalker(Enchantment.Rarity p_i46728_1_, EntityEquipmentSlot... p_i46728_2_) {
      super(p_i46728_1_, EnumEnchantmentType.ARMOR_FEET, p_i46728_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return p_77321_1_ * 10;
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return this.getMinEnchantability(p_77317_1_) + 15;
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   public int getMaxLevel() {
      return 2;
   }

   public static void freezeNearby(EntityLivingBase p_185266_0_, World p_185266_1_, BlockPos p_185266_2_, int p_185266_3_) {
      if (p_185266_0_.onGround) {
         IBlockState iblockstate = Blocks.FROSTED_ICE.getDefaultState();
         float f = (float)Math.min(16, 2 + p_185266_3_);
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(0, 0, 0);

         for(BlockPos.MutableBlockPos blockpos$mutableblockpos1 : BlockPos.getAllInBoxMutable(p_185266_2_.add((double)(-f), -1.0D, (double)(-f)), p_185266_2_.add((double)f, -1.0D, (double)f))) {
            if (blockpos$mutableblockpos1.distanceSqToCenter(p_185266_0_.posX, p_185266_0_.posY, p_185266_0_.posZ) <= (double)(f * f)) {
               blockpos$mutableblockpos.setPos(blockpos$mutableblockpos1.getX(), blockpos$mutableblockpos1.getY() + 1, blockpos$mutableblockpos1.getZ());
               IBlockState iblockstate1 = p_185266_1_.getBlockState(blockpos$mutableblockpos);
               if (iblockstate1.isAir()) {
                  IBlockState iblockstate2 = p_185266_1_.getBlockState(blockpos$mutableblockpos1);
                  if (iblockstate2.getMaterial() == Material.WATER && iblockstate2.get(BlockFlowingFluid.LEVEL) == 0 && iblockstate.isValidPosition(p_185266_1_, blockpos$mutableblockpos1) && p_185266_1_.checkNoEntityCollision(iblockstate, blockpos$mutableblockpos1)) {
                     p_185266_1_.setBlockState(blockpos$mutableblockpos1, iblockstate);
                     p_185266_1_.getPendingBlockTicks().scheduleTick(blockpos$mutableblockpos1.toImmutable(), Blocks.FROSTED_ICE, MathHelper.nextInt(p_185266_0_.getRNG(), 60, 120));
                  }
               }
            }
         }

      }
   }

   public boolean canApplyTogether(Enchantment p_77326_1_) {
      return super.canApplyTogether(p_77326_1_) && p_77326_1_ != Enchantments.DEPTH_STRIDER;
   }
}
