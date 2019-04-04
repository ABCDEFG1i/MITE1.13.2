package net.minecraft.server.management;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

public class PlayerInteractionManager {
   public World world;
   public EntityPlayerMP player;
   private GameType gameType = GameType.NOT_SET;
   private boolean isDestroyingBlock;
   private int initialDamage;
   private BlockPos destroyPos = BlockPos.ORIGIN;
   private int ticks;
   private boolean receivedFinishDiggingPacket;
   private BlockPos delayedDestroyPos = BlockPos.ORIGIN;
   private int initialBlockDamage;
   private int durabilityRemainingOnBlock = -1;
   private final StopWatch blockHarvestTimer = new StopWatch();

   public PlayerInteractionManager(World p_i1524_1_) {
      this.world = p_i1524_1_;
   }

   public void setGameType(GameType p_73076_1_) {
      this.gameType = p_73076_1_;
      p_73076_1_.configurePlayerCapabilities(this.player.capabilities);
      this.player.sendPlayerAbilities();
      this.player.server.getPlayerList().sendPacketToAllPlayers(new SPacketPlayerListItem(SPacketPlayerListItem.Action.UPDATE_GAME_MODE, this.player));
      this.world.updateAllPlayersSleepingFlag();
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public boolean survivalOrAdventure() {
      return this.gameType.isSurvivalOrAdventure();
   }

   public boolean isCreative() {
      return this.gameType.isCreative();
   }

   public void initializeGameType(GameType p_73077_1_) {
      if (this.gameType == GameType.NOT_SET) {
         this.gameType = p_73077_1_;
      }

      this.setGameType(this.gameType);
   }

   public void tick() {
      ++this.ticks;
      if (this.receivedFinishDiggingPacket) {
         int i = this.ticks - this.initialBlockDamage;
         IBlockState iblockstate = this.world.getBlockState(this.delayedDestroyPos);
         if (iblockstate.isAir()) {
            this.receivedFinishDiggingPacket = false;
         } else {
            float f = iblockstate.getPlayerRelativeBlockHardness(this.player, this.player.world, this.delayedDestroyPos) * (float)(i + 1);
            int j = (int)(f * 10.0F);
            if (j != this.durabilityRemainingOnBlock) {
               this.world.sendBlockBreakProgress(this.player.getEntityId(), this.delayedDestroyPos, j);
               this.durabilityRemainingOnBlock = j;
            }

            if (f >= 1.0F) {
               this.receivedFinishDiggingPacket = false;
               this.tryHarvestBlock(this.delayedDestroyPos);
            }
         }
      } else if (this.isDestroyingBlock) {
         IBlockState iblockstate1 = this.world.getBlockState(this.destroyPos);
         if (iblockstate1.isAir()) {
            this.world.sendBlockBreakProgress(this.player.getEntityId(), this.destroyPos, -1);
            this.durabilityRemainingOnBlock = -1;
            this.isDestroyingBlock = false;
         } else {
            int k = this.ticks - this.initialDamage;
            float f1 = iblockstate1.getPlayerRelativeBlockHardness(this.player, this.player.world, this.delayedDestroyPos) * (float)(k + 1);
            int l = (int)(f1 * 10.0F);
            if (l != this.durabilityRemainingOnBlock) {
               this.world.sendBlockBreakProgress(this.player.getEntityId(), this.destroyPos, l);
               this.durabilityRemainingOnBlock = l;
            }
         }
      }

   }

   public void startDestroyBlock(BlockPos p_180784_1_, EnumFacing p_180784_2_) {
      synchronized(blockHarvestTimer) {
         if (SharedConstants.developmentMode && !blockHarvestTimer.isStarted()) {
            System.out.println("Start to mine block:" + this.world.getBlockState(p_180784_1_).getBlock() + ".");
            blockHarvestTimer.start();
         }
      }
      if (this.isCreative()) {
         if (!this.world.extinguishFire(null, p_180784_1_, p_180784_2_)) {
            this.tryHarvestBlock(p_180784_1_);
         }

      } else {
         if (this.gameType.hasLimitedInteractions()) {
            if (this.gameType == GameType.SPECTATOR) {
               return;
            }

            if (!this.player.isAllowEdit()) {
               ItemStack itemstack = this.player.getHeldItemMainhand();
               if (itemstack.isEmpty()) {
                  return;
               }

               BlockWorldState blockworldstate = new BlockWorldState(this.world, p_180784_1_, false);
               if (!itemstack.canDestroy(this.world.getTags(), blockworldstate)) {
                  return;
               }
            }
         }

         this.world.extinguishFire(null, p_180784_1_, p_180784_2_);
         this.initialDamage = this.ticks;
         float f = 1.0F;
         IBlockState iblockstate = this.world.getBlockState(p_180784_1_);
         if (!iblockstate.isAir()) {
            iblockstate.onBlockClicked(this.world, p_180784_1_, this.player);
            f = iblockstate.getPlayerRelativeBlockHardness(this.player, this.player.world, p_180784_1_);
         }

         if (!iblockstate.isAir() && f >= 1.0F) {
            this.tryHarvestBlock(p_180784_1_);
         } else {
            this.isDestroyingBlock = true;
            this.destroyPos = p_180784_1_;
            int i = (int)(f * 10.0F);
            this.world.sendBlockBreakProgress(this.player.getEntityId(), p_180784_1_, i);
            this.player.connection.sendPacket(new SPacketBlockChange(this.world, p_180784_1_));
            this.durabilityRemainingOnBlock = i;
         }

      }
   }

   public void stopDestroyBlock(BlockPos p_180785_1_) {
      if (p_180785_1_.equals(this.destroyPos)) {
         int i = this.ticks - this.initialDamage;
         IBlockState iblockstate = this.world.getBlockState(p_180785_1_);
         if (!iblockstate.isAir()) {
            float f = iblockstate.getPlayerRelativeBlockHardness(this.player, this.player.world, p_180785_1_) * (float)(i + 1);
            if (f >= 0.7F) {
               this.isDestroyingBlock = false;
               this.world.sendBlockBreakProgress(this.player.getEntityId(), p_180785_1_, -1);
               this.tryHarvestBlock(p_180785_1_);
            } else if (!this.receivedFinishDiggingPacket) {
               this.isDestroyingBlock = false;
               this.receivedFinishDiggingPacket = true;
               this.delayedDestroyPos = p_180785_1_;
               this.initialBlockDamage = this.initialDamage;
            }
         }
      }

   }

   public void abortDestroyBlock() {
      synchronized(blockHarvestTimer) {
         if (SharedConstants.developmentMode && blockHarvestTimer.isStarted()) {
            blockHarvestTimer.reset();
         }
      }
      this.isDestroyingBlock = false;
      this.world.sendBlockBreakProgress(this.player.getEntityId(), this.destroyPos, -1);
   }

   private boolean removeBlock(BlockPos p_180235_1_) {
      IBlockState iblockstate = this.world.getBlockState(p_180235_1_);
      iblockstate.getBlock().onBlockHarvested(this.world, p_180235_1_, iblockstate, this.player);
      boolean flag = this.world.removeBlock(p_180235_1_);
      if (flag) {
         iblockstate.getBlock().onPlayerDestroy(this.world, p_180235_1_, iblockstate);
      }

      return flag;
   }

   public boolean tryHarvestBlock(BlockPos p_180237_1_) {
      IBlockState iblockstate = this.world.getBlockState(p_180237_1_);
      if (!this.player.getHeldItemMainhand().getItem().canPlayerBreakBlockWhileHolding(iblockstate, this.world, p_180237_1_, this.player)) {
         return false;
      } else {
         TileEntity tileentity = this.world.getTileEntity(p_180237_1_);
         Block block = iblockstate.getBlock();
         if ((block instanceof BlockCommandBlock || block instanceof BlockStructure) && !this.player.canUseCommandBlock()) {
            this.world.notifyBlockUpdate(p_180237_1_, iblockstate, iblockstate, 3);
            return false;
         } else {
            if (this.gameType.hasLimitedInteractions()) {
               if (this.gameType == GameType.SPECTATOR) {
                  return false;
               }

               if (!this.player.isAllowEdit()) {
                  ItemStack itemstack = this.player.getHeldItemMainhand();
                  if (itemstack.isEmpty()) {
                     return false;
                  }

                  BlockWorldState blockworldstate = new BlockWorldState(this.world, p_180237_1_, false);
                  if (!itemstack.canDestroy(this.world.getTags(), blockworldstate)) {
                     return false;
                  }
               }
            }

            boolean flag1 = this.removeBlock(p_180237_1_);
            if (!this.isCreative()) {
               ItemStack itemstack2 = this.player.getHeldItemMainhand();
               boolean flag = this.player.canHarvestBlock(iblockstate);
               itemstack2.onBlockDestroyed(this.world, iblockstate, p_180237_1_, this.player);
               if (flag1 && flag) {
                  ItemStack itemstack1 = itemstack2.isEmpty() ? ItemStack.EMPTY : itemstack2.copy();
                  synchronized(blockHarvestTimer) {
                     if (SharedConstants.developmentMode & blockHarvestTimer.isStarted()) {
                        System.out.println("Block  " + iblockstate.getBlock() + " mined used time:" + blockHarvestTimer.getTime(
                                TimeUnit.MILLISECONDS));
                        blockHarvestTimer.stop();
                        blockHarvestTimer.reset();
                     }
                  }
                  iblockstate.getBlock().harvestBlock(this.world, this.player, p_180237_1_, iblockstate, tileentity, itemstack1);
               }
            }

            return flag1;
         }
      }
   }

   public EnumActionResult processRightClick(EntityPlayer p_187250_1_, World p_187250_2_, ItemStack p_187250_3_, EnumHand p_187250_4_) {
      if (this.gameType == GameType.SPECTATOR) {
         return EnumActionResult.PASS;
      } else if (p_187250_1_.getCooldownTracker().hasCooldown(p_187250_3_.getItem())) {
         return EnumActionResult.PASS;
      } else {
         int i = p_187250_3_.getCount();
         int j = p_187250_3_.getDamage();
         ActionResult<ItemStack> actionresult = p_187250_3_.useItemRightClick(p_187250_2_, p_187250_1_, p_187250_4_);
         ItemStack itemstack = actionresult.getResult();
         if (itemstack == p_187250_3_ && itemstack.getCount() == i && itemstack.getUseDuration() <= 0 && itemstack.getDamage() == j) {
            return actionresult.getType();
         } else if (actionresult.getType() == EnumActionResult.FAIL && itemstack.getUseDuration() > 0 && !p_187250_1_.isHandActive()) {
            return actionresult.getType();
         } else {
            p_187250_1_.setHeldItem(p_187250_4_, itemstack);
            if (this.isCreative()) {
               itemstack.setCount(i);
               if (itemstack.isDamageable()) {
                  itemstack.setDamage(j);
               }
            }

            if (itemstack.isEmpty()) {
               p_187250_1_.setHeldItem(p_187250_4_, ItemStack.EMPTY);
            }

            if (!p_187250_1_.isHandActive()) {
               ((EntityPlayerMP)p_187250_1_).sendContainerToPlayer(p_187250_1_.inventoryContainer);
            }

            return actionresult.getType();
         }
      }
   }

   public EnumActionResult processRightClickBlock(EntityPlayer p_187251_1_, World p_187251_2_, ItemStack p_187251_3_, EnumHand p_187251_4_, BlockPos p_187251_5_, EnumFacing p_187251_6_, float p_187251_7_, float p_187251_8_, float p_187251_9_) {
      IBlockState iblockstate = p_187251_2_.getBlockState(p_187251_5_);
      if (this.gameType == GameType.SPECTATOR) {
         TileEntity tileentity = p_187251_2_.getTileEntity(p_187251_5_);
         if (tileentity instanceof ILockableContainer) {
            Block block = iblockstate.getBlock();
            ILockableContainer ilockablecontainer = (ILockableContainer)tileentity;
            if (ilockablecontainer instanceof TileEntityChest && block instanceof BlockChest) {
               ilockablecontainer = ((BlockChest)block).getContainer(iblockstate, p_187251_2_, p_187251_5_, false);
            }

            if (ilockablecontainer != null) {
               p_187251_1_.displayGUIChest(ilockablecontainer);
               return EnumActionResult.SUCCESS;
            }
         } else if (tileentity instanceof IInventory) {
            p_187251_1_.displayGUIChest((IInventory)tileentity);
            return EnumActionResult.SUCCESS;
         }

         return EnumActionResult.PASS;
      } else {
         boolean flag = !p_187251_1_.getHeldItemMainhand().isEmpty() || !p_187251_1_.getHeldItemOffhand().isEmpty();
         boolean flag1 = p_187251_1_.isSneaking() && flag;
         if (!flag1 && iblockstate.onBlockActivated(p_187251_2_, p_187251_5_, p_187251_1_, p_187251_4_, p_187251_6_, p_187251_7_, p_187251_8_, p_187251_9_)) {
            return EnumActionResult.SUCCESS;
         } else if (!p_187251_3_.isEmpty() && !p_187251_1_.getCooldownTracker().hasCooldown(p_187251_3_.getItem())) {
            ItemUseContext itemusecontext = new ItemUseContext(p_187251_1_, p_187251_1_.getHeldItem(p_187251_4_), p_187251_5_, p_187251_6_, p_187251_7_, p_187251_8_, p_187251_9_);
            if (this.isCreative()) {
               int i = p_187251_3_.getCount();
               EnumActionResult enumactionresult = p_187251_3_.onItemUse(itemusecontext);
               p_187251_3_.setCount(i);
               return enumactionresult;
            } else {
               return p_187251_3_.onItemUse(itemusecontext);
            }
         } else {
            return EnumActionResult.PASS;
         }
      }
   }

   public void setWorld(WorldServer p_73080_1_) {
      this.world = p_73080_1_;
   }
}
