package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemEnderEye extends Item {
   public ItemEnderEye(Item.Properties p_i48502_1_) {
      super(p_i48502_1_);
   }

   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos();
      IBlockState iblockstate = world.getBlockState(blockpos);
      if (iblockstate.getBlock() == Blocks.END_PORTAL_FRAME && !iblockstate.get(BlockEndPortalFrame.EYE)) {
         if (world.isRemote) {
            return EnumActionResult.SUCCESS;
         } else {
            IBlockState iblockstate1 = iblockstate.with(BlockEndPortalFrame.EYE, Boolean.valueOf(true));
            Block.func_199601_a(iblockstate, iblockstate1, world, blockpos);
            world.setBlockState(blockpos, iblockstate1, 2);
            world.updateComparatorOutputLevel(blockpos, Blocks.END_PORTAL_FRAME);
            p_195939_1_.getItem().shrink(1);

            for(int i = 0; i < 16; ++i) {
               double d0 = (double)((float)blockpos.getX() + (5.0F + random.nextFloat() * 6.0F) / 16.0F);
               double d1 = (double)((float)blockpos.getY() + 0.8125F);
               double d2 = (double)((float)blockpos.getZ() + (5.0F + random.nextFloat() * 6.0F) / 16.0F);
               double d3 = 0.0D;
               double d4 = 0.0D;
               double d5 = 0.0D;
               world.spawnParticle(Particles.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }

            world.playSound(null, blockpos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            BlockPattern.PatternHelper blockpattern$patternhelper = BlockEndPortalFrame.getOrCreatePortalShape().match(world, blockpos);
            if (blockpattern$patternhelper != null) {
               BlockPos blockpos1 = blockpattern$patternhelper.getFrontTopLeft().add(-3, 0, -3);

               for(int j = 0; j < 3; ++j) {
                  for(int k = 0; k < 3; ++k) {
                     world.setBlockState(blockpos1.add(j, 0, k), Blocks.END_PORTAL.getDefaultState(), 2);
                  }
               }

               world.playBroadcastSound(1038, blockpos1.add(1, 0, 1), 0);
            }

            return EnumActionResult.SUCCESS;
         }
      } else {
         return EnumActionResult.PASS;
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      RayTraceResult raytraceresult = this.rayTrace(p_77659_1_, p_77659_2_, false);
      if (raytraceresult != null && raytraceresult.type == RayTraceResult.Type.BLOCK && p_77659_1_.getBlockState(raytraceresult.getBlockPos()).getBlock() == Blocks.END_PORTAL_FRAME) {
         return new ActionResult<>(EnumActionResult.PASS, itemstack);
      } else {
         p_77659_2_.setActiveHand(p_77659_3_);
         if (!p_77659_1_.isRemote) {
            BlockPos blockpos = ((WorldServer)p_77659_1_).getChunkProvider().func_211268_a(p_77659_1_, "Stronghold", new BlockPos(p_77659_2_), 100, false);
            if (blockpos != null) {
               EntityEnderEye entityendereye = new EntityEnderEye(p_77659_1_, p_77659_2_.posX, p_77659_2_.posY + (double)(p_77659_2_.height / 2.0F), p_77659_2_.posZ);
               entityendereye.moveTowards(blockpos);
               p_77659_1_.spawnEntity(entityendereye);
               if (p_77659_2_ instanceof EntityPlayerMP) {
                  CriteriaTriggers.USED_ENDER_EYE.trigger((EntityPlayerMP)p_77659_2_, blockpos);
               }

               p_77659_1_.playSound(null, p_77659_2_.posX, p_77659_2_.posY, p_77659_2_.posZ, SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
               p_77659_1_.playEvent(null, 1003, new BlockPos(p_77659_2_), 0);
               if (!p_77659_2_.capabilities.isCreativeMode) {
                  itemstack.shrink(1);
               }

               p_77659_2_.func_71029_a(StatList.ITEM_USED.func_199076_b(this));
               return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
            }
         }

         return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
      }
   }
}
