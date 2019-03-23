package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemLilyPad extends ItemBlock {
   public ItemLilyPad(Block p_i48456_1_, Item.Properties p_i48456_2_) {
      super(p_i48456_1_, p_i48456_2_);
   }

   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      return EnumActionResult.PASS;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      RayTraceResult raytraceresult = this.rayTrace(p_77659_1_, p_77659_2_, true);
      if (raytraceresult == null) {
         return new ActionResult<>(EnumActionResult.PASS, itemstack);
      } else {
         if (raytraceresult.type == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = raytraceresult.getBlockPos();
            if (!p_77659_1_.isBlockModifiable(p_77659_2_, blockpos) || !p_77659_2_.canPlayerEdit(blockpos.offset(raytraceresult.sideHit), raytraceresult.sideHit, itemstack)) {
               return new ActionResult<>(EnumActionResult.FAIL, itemstack);
            }

            BlockPos blockpos1 = blockpos.up();
            IBlockState iblockstate = p_77659_1_.getBlockState(blockpos);
            Material material = iblockstate.getMaterial();
            IFluidState ifluidstate = p_77659_1_.getFluidState(blockpos);
            if ((ifluidstate.getFluid() == Fluids.WATER || material == Material.ICE) && p_77659_1_.isAirBlock(blockpos1)) {
               p_77659_1_.setBlockState(blockpos1, Blocks.LILY_PAD.getDefaultState(), 11);
               if (p_77659_2_ instanceof EntityPlayerMP) {
                  CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)p_77659_2_, blockpos1, itemstack);
               }

               if (!p_77659_2_.capabilities.isCreativeMode) {
                  itemstack.shrink(1);
               }

               p_77659_2_.func_71029_a(StatList.ITEM_USED.func_199076_b(this));
               p_77659_1_.playSound(p_77659_2_, blockpos, SoundEvents.BLOCK_LILY_PAD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
               return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
            }
         }

         return new ActionResult<>(EnumActionResult.FAIL, itemstack);
      }
   }
}
