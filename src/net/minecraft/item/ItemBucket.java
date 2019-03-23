package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ItemBucket extends Item {
   private final Fluid containedBlock;

   public ItemBucket(Fluid p_i49025_1_, Item.Properties p_i49025_2_) {
      super(p_i49025_2_);
      this.containedBlock = p_i49025_1_;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      RayTraceResult raytraceresult = this.rayTrace(p_77659_1_, p_77659_2_, this.containedBlock == Fluids.EMPTY);
      if (raytraceresult == null) {
         return new ActionResult<>(EnumActionResult.PASS, itemstack);
      } else if (raytraceresult.type == RayTraceResult.Type.BLOCK) {
         BlockPos blockpos = raytraceresult.getBlockPos();
         if (p_77659_1_.isBlockModifiable(p_77659_2_, blockpos) && p_77659_2_.canPlayerEdit(blockpos, raytraceresult.sideHit, itemstack)) {
            if (this.containedBlock == Fluids.EMPTY) {
               IBlockState iblockstate1 = p_77659_1_.getBlockState(blockpos);
               if (iblockstate1.getBlock() instanceof IBucketPickupHandler) {
                  Fluid fluid = ((IBucketPickupHandler)iblockstate1.getBlock()).pickupFluid(p_77659_1_, blockpos, iblockstate1);
                  if (fluid != Fluids.EMPTY) {
                     p_77659_2_.func_71029_a(StatList.ITEM_USED.func_199076_b(this));
                     p_77659_2_.playSound(fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                     ItemStack itemstack1 = this.fillBucket(itemstack, p_77659_2_, fluid.getFilledBucket());
                     if (!p_77659_1_.isRemote) {
                        CriteriaTriggers.field_204813_j.func_204817_a((EntityPlayerMP)p_77659_2_, new ItemStack(fluid.getFilledBucket()));
                     }

                     return new ActionResult<>(EnumActionResult.SUCCESS, itemstack1);
                  }
               }

               return new ActionResult<>(EnumActionResult.FAIL, itemstack);
            } else {
               IBlockState iblockstate = p_77659_1_.getBlockState(blockpos);
               BlockPos blockpos1 = this.getPlacementPosition(iblockstate, blockpos, raytraceresult);
               if (this.tryPlaceContainedLiquid(p_77659_2_, p_77659_1_, blockpos1, raytraceresult)) {
                  this.onLiquidPlaced(p_77659_1_, itemstack, blockpos1);
                  if (p_77659_2_ instanceof EntityPlayerMP) {
                     CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)p_77659_2_, blockpos1, itemstack);
                  }

                  p_77659_2_.func_71029_a(StatList.ITEM_USED.func_199076_b(this));
                  return new ActionResult<>(EnumActionResult.SUCCESS, this.emptyBucket(itemstack, p_77659_2_));
               } else {
                  return new ActionResult<>(EnumActionResult.FAIL, itemstack);
               }
            }
         } else {
            return new ActionResult<>(EnumActionResult.FAIL, itemstack);
         }
      } else {
         return new ActionResult<>(EnumActionResult.PASS, itemstack);
      }
   }

   private BlockPos getPlacementPosition(IBlockState p_210768_1_, BlockPos p_210768_2_, RayTraceResult p_210768_3_) {
      return p_210768_1_.getBlock() instanceof ILiquidContainer ? p_210768_2_ : p_210768_3_.getBlockPos().offset(p_210768_3_.sideHit);
   }

   protected ItemStack emptyBucket(ItemStack p_203790_1_, EntityPlayer p_203790_2_) {
      return !p_203790_2_.capabilities.isCreativeMode ? new ItemStack(Items.BUCKET) : p_203790_1_;
   }

   public void onLiquidPlaced(World p_203792_1_, ItemStack p_203792_2_, BlockPos p_203792_3_) {
   }

   private ItemStack fillBucket(ItemStack p_150910_1_, EntityPlayer p_150910_2_, Item p_150910_3_) {
      if (p_150910_2_.capabilities.isCreativeMode) {
         return p_150910_1_;
      } else {
         p_150910_1_.shrink(1);
         if (p_150910_1_.isEmpty()) {
            return new ItemStack(p_150910_3_);
         } else {
            if (!p_150910_2_.inventory.addItemStackToInventory(new ItemStack(p_150910_3_))) {
               p_150910_2_.dropItem(new ItemStack(p_150910_3_), false);
            }

            return p_150910_1_;
         }
      }
   }

   public boolean tryPlaceContainedLiquid(@Nullable EntityPlayer p_180616_1_, World p_180616_2_, BlockPos p_180616_3_, @Nullable RayTraceResult p_180616_4_) {
      if (!(this.containedBlock instanceof FlowingFluid)) {
         return false;
      } else {
         IBlockState iblockstate = p_180616_2_.getBlockState(p_180616_3_);
         Material material = iblockstate.getMaterial();
         boolean flag = !material.isSolid();
         boolean flag1 = material.isReplaceable();
         if (p_180616_2_.isAirBlock(p_180616_3_) || flag || flag1 || iblockstate.getBlock() instanceof ILiquidContainer && ((ILiquidContainer)iblockstate.getBlock()).canContainFluid(p_180616_2_, p_180616_3_, iblockstate, this.containedBlock)) {
            if (p_180616_2_.dimension.doesWaterVaporize() && this.containedBlock.isIn(FluidTags.WATER)) {
               int i = p_180616_3_.getX();
               int j = p_180616_3_.getY();
               int k = p_180616_3_.getZ();
               p_180616_2_.playSound(p_180616_1_, p_180616_3_, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (p_180616_2_.rand.nextFloat() - p_180616_2_.rand.nextFloat()) * 0.8F);

               for(int l = 0; l < 8; ++l) {
                  p_180616_2_.spawnParticle(Particles.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
               }
            } else if (iblockstate.getBlock() instanceof ILiquidContainer) {
               if (((ILiquidContainer)iblockstate.getBlock()).receiveFluid(p_180616_2_, p_180616_3_, iblockstate, ((FlowingFluid)this.containedBlock).getStillFluidState(false))) {
                  this.playEmptySound(p_180616_1_, p_180616_2_, p_180616_3_);
               }
            } else {
               if (!p_180616_2_.isRemote && (flag || flag1) && !material.isLiquid()) {
                  p_180616_2_.destroyBlock(p_180616_3_, true);
               }

               this.playEmptySound(p_180616_1_, p_180616_2_, p_180616_3_);
               p_180616_2_.setBlockState(p_180616_3_, this.containedBlock.getDefaultState().getBlockState(), 11);
            }

            return true;
         } else {
            return p_180616_4_ == null ? false : this.tryPlaceContainedLiquid(p_180616_1_, p_180616_2_, p_180616_4_.getBlockPos().offset(p_180616_4_.sideHit), (RayTraceResult)null);
         }
      }
   }

   protected void playEmptySound(@Nullable EntityPlayer p_203791_1_, IWorld p_203791_2_, BlockPos p_203791_3_) {
      SoundEvent soundevent = this.containedBlock.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
      p_203791_2_.playSound(p_203791_1_, p_203791_3_, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }
}
