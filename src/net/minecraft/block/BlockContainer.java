package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BlockContainer extends Block implements ITileEntityProvider {
   private static final Logger PRIVATE_LOGGER = LogManager.getLogger();

   protected BlockContainer(Block.Properties p_i48446_1_) {
      super(p_i48446_1_);
   }

   public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
         p_196243_2_.removeTileEntity(p_196243_3_);
      }
   }

   public void harvestBlock(World p_180657_1_, EntityPlayer p_180657_2_, BlockPos p_180657_3_, IBlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      if (p_180657_5_ instanceof INameable && ((INameable)p_180657_5_).hasCustomName()) {
         p_180657_2_.func_71029_a(StatList.BLOCK_MINED.func_199076_b(this));
         p_180657_2_.addExhaustion(0.005F);
         if (p_180657_1_.isRemote) {
            PRIVATE_LOGGER.debug("Never going to hit this!");
            return;
         }

         int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, p_180657_6_);
         Item item = this.getItemDropped(p_180657_4_, p_180657_1_, p_180657_3_, i).asItem();
         if (item == Items.AIR) {
            return;
         }

         ItemStack itemstack = new ItemStack(item, this.quantityDropped(p_180657_4_, p_180657_1_.rand));
         itemstack.setDisplayName(((INameable)p_180657_5_).getCustomName());
         spawnAsEntity(p_180657_1_, p_180657_3_, itemstack);
      } else {
         super.harvestBlock(p_180657_1_, p_180657_2_, p_180657_3_, p_180657_4_, (TileEntity)null, p_180657_6_);
      }

   }

   public boolean eventReceived(IBlockState p_189539_1_, World p_189539_2_, BlockPos p_189539_3_, int p_189539_4_, int p_189539_5_) {
      super.eventReceived(p_189539_1_, p_189539_2_, p_189539_3_, p_189539_4_, p_189539_5_);
      TileEntity tileentity = p_189539_2_.getTileEntity(p_189539_3_);
      return tileentity == null ? false : tileentity.receiveClientEvent(p_189539_4_, p_189539_5_);
   }
}
