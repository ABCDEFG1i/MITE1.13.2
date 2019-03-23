package net.minecraft.item;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemBlock extends Item {
   @Deprecated
   private final Block block;

   public ItemBlock(Block p_i48527_1_, Item.Properties p_i48527_2_) {
      super(p_i48527_2_);
      this.block = p_i48527_1_;
   }

   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      return this.tryPlace(new BlockItemUseContext(p_195939_1_));
   }

   public EnumActionResult tryPlace(BlockItemUseContext p_195942_1_) {
      if (!p_195942_1_.func_196011_b()) {
         return EnumActionResult.FAIL;
      } else {
         IBlockState iblockstate = this.getStateForPlacement(p_195942_1_);
         if (iblockstate == null) {
            return EnumActionResult.FAIL;
         } else if (!this.placeBlock(p_195942_1_, iblockstate)) {
            return EnumActionResult.FAIL;
         } else {
            BlockPos blockpos = p_195942_1_.getPos();
            World world = p_195942_1_.getWorld();
            EntityPlayer entityplayer = p_195942_1_.getPlayer();
            ItemStack itemstack = p_195942_1_.getItem();
            IBlockState iblockstate1 = world.getBlockState(blockpos);
            Block block = iblockstate1.getBlock();
            if (block == iblockstate.getBlock()) {
               this.onBlockPlaced(blockpos, world, entityplayer, itemstack, iblockstate1);
               block.onBlockPlacedBy(world, blockpos, iblockstate1, entityplayer, itemstack);
               if (entityplayer instanceof EntityPlayerMP) {
                  CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)entityplayer, blockpos, itemstack);
               }
            }

            SoundType soundtype = block.getSoundType();
            world.playSound(entityplayer, blockpos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            itemstack.shrink(1);
            return EnumActionResult.SUCCESS;
         }
      }
   }

   protected boolean onBlockPlaced(BlockPos p_195943_1_, World p_195943_2_, @Nullable EntityPlayer p_195943_3_, ItemStack p_195943_4_, IBlockState p_195943_5_) {
      return setTileEntityNBT(p_195943_2_, p_195943_3_, p_195943_1_, p_195943_4_);
   }

   @Nullable
   protected IBlockState getStateForPlacement(BlockItemUseContext p_195945_1_) {
      IBlockState iblockstate = this.getBlock().getStateForPlacement(p_195945_1_);
      return iblockstate != null && this.canPlace(p_195945_1_, iblockstate) ? iblockstate : null;
   }

   protected boolean canPlace(BlockItemUseContext p_195944_1_, IBlockState p_195944_2_) {
      return p_195944_2_.isValidPosition(p_195944_1_.getWorld(), p_195944_1_.getPos()) && p_195944_1_.getWorld().checkNoEntityCollision(p_195944_2_, p_195944_1_.getPos());
   }

   protected boolean placeBlock(BlockItemUseContext p_195941_1_, IBlockState p_195941_2_) {
      return p_195941_1_.getWorld().setBlockState(p_195941_1_.getPos(), p_195941_2_, 11);
   }

   public static boolean setTileEntityNBT(World p_179224_0_, @Nullable EntityPlayer p_179224_1_, BlockPos p_179224_2_, ItemStack p_179224_3_) {
      MinecraftServer minecraftserver = p_179224_0_.getServer();
      if (minecraftserver == null) {
         return false;
      } else {
         NBTTagCompound nbttagcompound = p_179224_3_.getChildTag("BlockEntityTag");
         if (nbttagcompound != null) {
            TileEntity tileentity = p_179224_0_.getTileEntity(p_179224_2_);
            if (tileentity != null) {
               if (!p_179224_0_.isRemote && tileentity.onlyOpsCanSetNbt() && (p_179224_1_ == null || !p_179224_1_.canUseCommandBlock())) {
                  return false;
               }

               NBTTagCompound nbttagcompound1 = tileentity.writeToNBT(new NBTTagCompound());
               NBTTagCompound nbttagcompound2 = nbttagcompound1.copy();
               nbttagcompound1.merge(nbttagcompound);
               nbttagcompound1.setInteger("x", p_179224_2_.getX());
               nbttagcompound1.setInteger("y", p_179224_2_.getY());
               nbttagcompound1.setInteger("z", p_179224_2_.getZ());
               if (!nbttagcompound1.equals(nbttagcompound2)) {
                  tileentity.readFromNBT(nbttagcompound1);
                  tileentity.markDirty();
                  return true;
               }
            }
         }

         return false;
      }
   }

   public String getTranslationKey() {
      return this.getBlock().getTranslationKey();
   }

   public void fillItemGroup(ItemGroup p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
      if (this.isInGroup(p_150895_1_)) {
         this.getBlock().fillItemGroup(p_150895_1_, p_150895_2_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      super.addInformation(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
      this.getBlock().addInformation(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
   }

   public Block getBlock() {
      return this.block;
   }

   public void addToBlockToItemMap(Map<Block, Item> p_195946_1_, Item p_195946_2_) {
      p_195946_1_.put(this.getBlock(), p_195946_2_);
   }
}
