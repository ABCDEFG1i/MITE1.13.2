package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class BlockBeacon extends BlockContainer {
   public BlockBeacon(Block.Properties p_i48443_1_) {
      super(p_i48443_1_);
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntityBeacon();
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (p_196250_2_.isRemote) {
         return true;
      } else {
         TileEntity tileentity = p_196250_2_.getTileEntity(p_196250_3_);
         if (tileentity instanceof TileEntityBeacon) {
            p_196250_4_.displayGUIChest((TileEntityBeacon)tileentity);
            p_196250_4_.addStat(StatList.BEACON_INTERACTION);
         }

         return true;
      }
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
      return EnumBlockRenderType.MODEL;
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, IBlockState p_180633_3_, EntityLivingBase p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasDisplayName()) {
         TileEntity tileentity = p_180633_1_.getTileEntity(p_180633_2_);
         if (tileentity instanceof TileEntityBeacon) {
            ((TileEntityBeacon)tileentity).setCustomName(p_180633_5_.getDisplayName());
         }
      }

   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public static void updateColorAsync(World p_176450_0_, BlockPos p_176450_1_) {
      HttpUtil.DOWNLOADER_EXECUTOR.submit(() -> {
         Chunk chunk = p_176450_0_.getChunk(p_176450_1_);

         for(int i = p_176450_1_.getY() - 1; i >= 0; --i) {
            BlockPos blockpos = new BlockPos(p_176450_1_.getX(), i, p_176450_1_.getZ());
            if (!chunk.canSeeSky(blockpos)) {
               break;
            }

            IBlockState iblockstate = p_176450_0_.getBlockState(blockpos);
            if (iblockstate.getBlock() == Blocks.BEACON) {
               ((WorldServer)p_176450_0_).addScheduledTask(() -> {
                  TileEntity tileentity = p_176450_0_.getTileEntity(blockpos);
                  if (tileentity instanceof TileEntityBeacon) {
                     ((TileEntityBeacon)tileentity).updateBeacon();
                     p_176450_0_.addBlockEvent(blockpos, Blocks.BEACON, 1, 0);
                  }

               });
            }
         }

      });
   }
}
