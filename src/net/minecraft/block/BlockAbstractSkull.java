package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class BlockAbstractSkull extends BlockContainer {
   private final BlockSkull.ISkullType skullType;

   public BlockAbstractSkull(BlockSkull.ISkullType p_i48452_1_, Block.Properties p_i48452_2_) {
      super(p_i48452_2_);
      this.skullType = p_i48452_1_;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasCustomBreakingProgress(IBlockState p_190946_1_) {
      return true;
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntitySkull();
   }

   public void dropBlockAsItemWithChance(IBlockState p_196255_1_, World p_196255_2_, BlockPos p_196255_3_, float p_196255_4_, int p_196255_5_) {
   }

   public void onBlockHarvested(World p_176208_1_, BlockPos p_176208_2_, IBlockState p_176208_3_, EntityPlayer p_176208_4_) {
      if (!p_176208_1_.isRemote && p_176208_4_.capabilities.isCreativeMode) {
         TileEntitySkull.disableDrop(p_176208_1_, p_176208_2_);
      }

      super.onBlockHarvested(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock() && !p_196243_2_.isRemote) {
         TileEntity tileentity = p_196243_2_.getTileEntity(p_196243_3_);
         if (tileentity instanceof TileEntitySkull) {
            TileEntitySkull tileentityskull = (TileEntitySkull)tileentity;
            if (tileentityskull.shouldDrop()) {
               ItemStack itemstack = this.getItem(p_196243_2_, p_196243_3_, p_196243_1_);
               Block block = tileentityskull.getBlockState().getBlock();
               if ((block == Blocks.PLAYER_HEAD || block == Blocks.PLAYER_WALL_HEAD) && tileentityskull.getPlayerProfile() != null) {
                  NBTTagCompound nbttagcompound = new NBTTagCompound();
                  NBTUtil.writeGameProfile(nbttagcompound, tileentityskull.getPlayerProfile());
                  itemstack.getOrCreateTag().setTag("SkullOwner", nbttagcompound);
               }

               spawnAsEntity(p_196243_2_, p_196243_3_, itemstack);
            }
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public BlockSkull.ISkullType getSkullType() {
      return this.skullType;
   }
}
