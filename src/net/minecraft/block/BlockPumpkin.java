package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPumpkin extends BlockStemGrown {
   protected BlockPumpkin(Block.Properties p_i48347_1_) {
      super(p_i48347_1_);
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      ItemStack itemstack = p_196250_4_.getHeldItem(p_196250_5_);
      if (itemstack.getItem() == Items.SHEARS) {
         if (!p_196250_2_.isRemote) {
            EnumFacing enumfacing = p_196250_6_.getAxis() == EnumFacing.Axis.Y ? p_196250_4_.getHorizontalFacing().getOpposite() : p_196250_6_;
            p_196250_2_.playSound((EntityPlayer)null, p_196250_3_, SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            p_196250_2_.setBlockState(p_196250_3_, Blocks.CARVED_PUMPKIN.getDefaultState().with(BlockCarvedPumpkin.FACING, enumfacing), 11);
            EntityItem entityitem = new EntityItem(p_196250_2_, (double)p_196250_3_.getX() + 0.5D + (double)enumfacing.getXOffset() * 0.65D, (double)p_196250_3_.getY() + 0.1D, (double)p_196250_3_.getZ() + 0.5D + (double)enumfacing.getZOffset() * 0.65D, new ItemStack(Items.PUMPKIN_SEEDS, 4));
            entityitem.motionX = 0.05D * (double)enumfacing.getXOffset() + p_196250_2_.rand.nextDouble() * 0.02D;
            entityitem.motionY = 0.05D;
            entityitem.motionZ = 0.05D * (double)enumfacing.getZOffset() + p_196250_2_.rand.nextDouble() * 0.02D;
            p_196250_2_.spawnEntity(entityitem);
            itemstack.damageItem(1, p_196250_4_);
         }

         return true;
      } else {
         return super.onBlockActivated(p_196250_1_, p_196250_2_, p_196250_3_, p_196250_4_, p_196250_5_, p_196250_6_, p_196250_7_, p_196250_8_, p_196250_9_);
      }
   }

   public BlockStem getStem() {
      return (BlockStem)Blocks.PUMPKIN_STEM;
   }

   public BlockAttachedStem getAttachedStem() {
      return (BlockAttachedStem)Blocks.ATTACHED_PUMPKIN_STEM;
   }
}
