package net.minecraft.client.multiplayer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPickItem;
import net.minecraft.network.play.client.CPacketPlaceRecipe;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerControllerMP {
   private final Minecraft mc;
   private final NetHandlerPlayClient connection;
   private BlockPos currentBlock = new BlockPos(-1, -1, -1);
   private ItemStack currentItemHittingBlock = ItemStack.EMPTY;
   private float curBlockDamageMP;
   private float stepSoundTickCounter;
   private int blockHitDelay;
   private boolean isHittingBlock;
   private GameType currentGameType = GameType.SURVIVAL;
   private int currentPlayerItem;

   public PlayerControllerMP(Minecraft p_i45062_1_, NetHandlerPlayClient p_i45062_2_) {
      this.mc = p_i45062_1_;
      this.connection = p_i45062_2_;
   }

   public static void clickBlockCreative(Minecraft p_178891_0_, PlayerControllerMP p_178891_1_, BlockPos p_178891_2_, EnumFacing p_178891_3_) {
      if (!p_178891_0_.world.extinguishFire(p_178891_0_.player, p_178891_2_, p_178891_3_)) {
         p_178891_1_.onPlayerDestroyBlock(p_178891_2_);
      }

   }

   public void setPlayerCapabilities(EntityPlayer p_78748_1_) {
      this.currentGameType.configurePlayerCapabilities(p_78748_1_.capabilities);
   }

   public void setGameType(GameType p_78746_1_) {
      this.currentGameType = p_78746_1_;
      this.currentGameType.configurePlayerCapabilities(this.mc.player.capabilities);
   }

   public void flipPlayer(EntityPlayer p_78745_1_) {
      p_78745_1_.rotationYaw = -180.0F;
   }

   public boolean shouldDrawHUD() {
      return this.currentGameType.isSurvivalOrAdventure();
   }

   public boolean onPlayerDestroyBlock(BlockPos p_187103_1_) {
      if (this.currentGameType.hasLimitedInteractions()) {
         if (this.currentGameType == GameType.SPECTATOR) {
            return false;
         }

         if (!this.mc.player.isAllowEdit()) {
            ItemStack itemstack = this.mc.player.getHeldItemMainhand();
            if (itemstack.isEmpty()) {
               return false;
            }

            BlockWorldState blockworldstate = new BlockWorldState(this.mc.world, p_187103_1_, false);
            if (!itemstack.canDestroy(this.mc.world.getTags(), blockworldstate)) {
               return false;
            }
         }
      }

      World world = this.mc.world;
      IBlockState iblockstate = world.getBlockState(p_187103_1_);
      if (!this.mc.player.getHeldItemMainhand().getItem().canPlayerBreakBlockWhileHolding(iblockstate, world, p_187103_1_, this.mc.player)) {
         return false;
      } else {
         Block block = iblockstate.getBlock();
         if ((block instanceof BlockCommandBlock || block instanceof BlockStructure) && !this.mc.player.canUseCommandBlock()) {
            return false;
         } else if (iblockstate.isAir()) {
            return false;
         } else {
            block.onBlockHarvested(world, p_187103_1_, iblockstate, this.mc.player);
            IFluidState ifluidstate = world.getFluidState(p_187103_1_);
            boolean flag = world.setBlockState(p_187103_1_, ifluidstate.getBlockState(), 11);
            if (flag) {
               block.onPlayerDestroy(world, p_187103_1_, iblockstate);
            }

            this.currentBlock = new BlockPos(this.currentBlock.getX(), -1, this.currentBlock.getZ());
            if (!this.currentGameType.isCreative()) {
               ItemStack itemstack1 = this.mc.player.getHeldItemMainhand();
               if (!itemstack1.isEmpty()) {
                  itemstack1.onBlockDestroyed(world, iblockstate, p_187103_1_, this.mc.player);
                  if (itemstack1.isEmpty()) {
                     this.mc.player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                  }
               }
            }

            return flag;
         }
      }
   }

   public boolean clickBlock(BlockPos p_180511_1_, EnumFacing p_180511_2_) {
      if (this.currentGameType.hasLimitedInteractions()) {
         if (this.currentGameType == GameType.SPECTATOR) {
            return false;
         }

         if (!this.mc.player.isAllowEdit()) {
            ItemStack itemstack = this.mc.player.getHeldItemMainhand();
            if (itemstack.isEmpty()) {
               return false;
            }

            BlockWorldState blockworldstate = new BlockWorldState(this.mc.world, p_180511_1_, false);
            if (!itemstack.canDestroy(this.mc.world.getTags(), blockworldstate)) {
               return false;
            }
         }
      }

      if (!this.mc.world.getWorldBorder().contains(p_180511_1_)) {
         return false;
      } else {
         if (this.currentGameType.isCreative()) {
            this.mc.getTutorial().onHitBlock(this.mc.world, p_180511_1_, this.mc.world.getBlockState(p_180511_1_), 1.0F);
            this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, p_180511_1_, p_180511_2_));
            clickBlockCreative(this.mc, this, p_180511_1_, p_180511_2_);
            this.blockHitDelay = 5;
         } else if (!this.isHittingBlock || !this.isHittingPosition(p_180511_1_)) {
            if (this.isHittingBlock) {
               this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, p_180511_2_));
            }

            IBlockState iblockstate = this.mc.world.getBlockState(p_180511_1_);
            this.mc.getTutorial().onHitBlock(this.mc.world, p_180511_1_, iblockstate, 0.0F);
            this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, p_180511_1_, p_180511_2_));
            boolean flag = !iblockstate.isAir();
            if (flag && this.curBlockDamageMP == 0.0F) {
               iblockstate.onBlockClicked(this.mc.world, p_180511_1_, this.mc.player);
            }

            if (flag && iblockstate.getPlayerRelativeBlockHardness(this.mc.player, this.mc.player.world, p_180511_1_) >= 1.0F) {
               this.onPlayerDestroyBlock(p_180511_1_);
            } else {
               this.isHittingBlock = true;
               this.currentBlock = p_180511_1_;
               this.currentItemHittingBlock = this.mc.player.getHeldItemMainhand();
               this.curBlockDamageMP = 0.0F;
               this.stepSoundTickCounter = 0.0F;
               this.mc.world.sendBlockBreakProgress(this.mc.player.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
            }
         }

         return true;
      }
   }

   public void resetBlockRemoving() {
      if (this.isHittingBlock) {
         this.mc.getTutorial().onHitBlock(this.mc.world, this.currentBlock, this.mc.world.getBlockState(this.currentBlock), -1.0F);
         this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, EnumFacing.DOWN));
         this.isHittingBlock = false;
         this.curBlockDamageMP = 0.0F;
         this.mc.world.sendBlockBreakProgress(this.mc.player.getEntityId(), this.currentBlock, -1);
         this.mc.player.resetCooldown();
      }

   }

   public boolean onPlayerDamageBlock(BlockPos p_180512_1_, EnumFacing p_180512_2_) {
      this.syncCurrentPlayItem();
      if (this.blockHitDelay > 0) {
         --this.blockHitDelay;
         return true;
      } else if (this.currentGameType.isCreative() && this.mc.world.getWorldBorder().contains(p_180512_1_)) {
         this.blockHitDelay = 5;
         this.mc.getTutorial().onHitBlock(this.mc.world, p_180512_1_, this.mc.world.getBlockState(p_180512_1_), 1.0F);
         this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, p_180512_1_, p_180512_2_));
         clickBlockCreative(this.mc, this, p_180512_1_, p_180512_2_);
         return true;
      } else if (this.isHittingPosition(p_180512_1_)) {
         IBlockState iblockstate = this.mc.world.getBlockState(p_180512_1_);
         Block block = iblockstate.getBlock();
         if (iblockstate.isAir()) {
            this.isHittingBlock = false;
            return false;
         } else {
            this.curBlockDamageMP += iblockstate.getPlayerRelativeBlockHardness(this.mc.player, this.mc.player.world, p_180512_1_);
            if (this.stepSoundTickCounter % 4.0F == 0.0F) {
               SoundType soundtype = block.getSoundType();
               this.mc.getSoundHandler().play(new SimpleSound(soundtype.getHitSound(), SoundCategory.NEUTRAL, (soundtype.getVolume() + 1.0F) / 8.0F, soundtype.getPitch() * 0.5F, p_180512_1_));
            }

            ++this.stepSoundTickCounter;
            this.mc.getTutorial().onHitBlock(this.mc.world, p_180512_1_, iblockstate, MathHelper.clamp(this.curBlockDamageMP, 0.0F, 1.0F));
            if (this.curBlockDamageMP >= 1.0F) {
               this.isHittingBlock = false;
               this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, p_180512_1_, p_180512_2_));
               this.onPlayerDestroyBlock(p_180512_1_);
               this.curBlockDamageMP = 0.0F;
               this.stepSoundTickCounter = 0.0F;
               this.blockHitDelay = 5;
            }

            this.mc.world.sendBlockBreakProgress(this.mc.player.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
            return true;
         }
      } else {
         return this.clickBlock(p_180512_1_, p_180512_2_);
      }
   }

   public float getBlockReachDistance() {
      return this.currentGameType.isCreative() ? 5.0F : 4.5F;
   }

   public void tick() {
      this.syncCurrentPlayItem();
      if (this.connection.getNetworkManager().isChannelOpen()) {
         this.connection.getNetworkManager().tick();
      } else {
         this.connection.getNetworkManager().handleDisconnection();
      }

   }

   private boolean isHittingPosition(BlockPos p_178893_1_) {
      ItemStack itemstack = this.mc.player.getHeldItemMainhand();
      boolean flag = this.currentItemHittingBlock.isEmpty() && itemstack.isEmpty();
      if (!this.currentItemHittingBlock.isEmpty() && !itemstack.isEmpty()) {
         flag = itemstack.getItem() == this.currentItemHittingBlock.getItem() && ItemStack.areItemStackTagsEqual(itemstack, this.currentItemHittingBlock) && (itemstack.isDamageable() || itemstack.getDamage() == this.currentItemHittingBlock.getDamage());
      }

      return p_178893_1_.equals(this.currentBlock) && flag;
   }

   private void syncCurrentPlayItem() {
      int i = this.mc.player.inventory.currentItem;
      if (i != this.currentPlayerItem) {
         this.currentPlayerItem = i;
         this.connection.sendPacket(new CPacketHeldItemChange(this.currentPlayerItem));
      }

   }

   public EnumActionResult processRightClickBlock(EntityPlayerSP p_187099_1_, WorldClient p_187099_2_, BlockPos p_187099_3_, EnumFacing p_187099_4_, Vec3d p_187099_5_, EnumHand p_187099_6_) {
      this.syncCurrentPlayItem();
      if (!this.mc.world.getWorldBorder().contains(p_187099_3_)) {
         return EnumActionResult.FAIL;
      } else {
         ItemStack itemstack = p_187099_1_.getHeldItem(p_187099_6_);
         float f = (float)(p_187099_5_.x - (double)p_187099_3_.getX());
         float f1 = (float)(p_187099_5_.y - (double)p_187099_3_.getY());
         float f2 = (float)(p_187099_5_.z - (double)p_187099_3_.getZ());
         if (this.currentGameType == GameType.SPECTATOR) {
            this.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(p_187099_3_, p_187099_4_, p_187099_6_, f, f1, f2));
            return EnumActionResult.SUCCESS;
         } else {
            boolean flag = !p_187099_1_.getHeldItemMainhand().isEmpty() || !p_187099_1_.getHeldItemOffhand().isEmpty();
            boolean flag1 = p_187099_1_.isSneaking() && flag;
            if (!flag1 && p_187099_2_.getBlockState(p_187099_3_).onBlockActivated(p_187099_2_, p_187099_3_, p_187099_1_, p_187099_6_, p_187099_4_, f, f1, f2)) {
               this.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(p_187099_3_, p_187099_4_, p_187099_6_, f, f1, f2));
               return EnumActionResult.SUCCESS;
            } else {
               this.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(p_187099_3_, p_187099_4_, p_187099_6_, f, f1, f2));
               if (!itemstack.isEmpty() && !p_187099_1_.getCooldownTracker().hasCooldown(itemstack.getItem())) {
                  ItemUseContext itemusecontext = new ItemUseContext(p_187099_1_, p_187099_1_.getHeldItem(p_187099_6_), p_187099_3_, p_187099_4_, f, f1, f2);
                  EnumActionResult enumactionresult;
                  if (this.currentGameType.isCreative()) {
                     int i = itemstack.getCount();
                     enumactionresult = itemstack.onItemUse(itemusecontext);
                     itemstack.setCount(i);
                  } else {
                     enumactionresult = itemstack.onItemUse(itemusecontext);
                  }

                  return enumactionresult;
               } else {
                  return EnumActionResult.PASS;
               }
            }
         }
      }
   }

   public EnumActionResult processRightClick(EntityPlayer p_187101_1_, World p_187101_2_, EnumHand p_187101_3_) {
      if (this.currentGameType == GameType.SPECTATOR) {
         return EnumActionResult.PASS;
      } else {
         this.syncCurrentPlayItem();
         this.connection.sendPacket(new CPacketPlayerTryUseItem(p_187101_3_));
         ItemStack itemstack = p_187101_1_.getHeldItem(p_187101_3_);
         if (p_187101_1_.getCooldownTracker().hasCooldown(itemstack.getItem())) {
            return EnumActionResult.PASS;
         } else {
            int i = itemstack.getCount();
            ActionResult<ItemStack> actionresult = itemstack.useItemRightClick(p_187101_2_, p_187101_1_, p_187101_3_);
            ItemStack itemstack1 = actionresult.getResult();
            if (itemstack1 != itemstack || itemstack1.getCount() != i) {
               p_187101_1_.setHeldItem(p_187101_3_, itemstack1);
            }

            return actionresult.getType();
         }
      }
   }

   public EntityPlayerSP createPlayer(World p_199681_1_, StatisticsManager p_199681_2_, RecipeBookClient p_199681_3_) {
      return new EntityPlayerSP(this.mc, p_199681_1_, this.connection, p_199681_2_, p_199681_3_);
   }

   public void attackEntity(EntityPlayer p_78764_1_, Entity p_78764_2_) {
      this.syncCurrentPlayItem();
      this.connection.sendPacket(new CPacketUseEntity(p_78764_2_));
      if (this.currentGameType != GameType.SPECTATOR) {
         p_78764_1_.attackTargetEntityWithCurrentItem(p_78764_2_);
         p_78764_1_.resetCooldown();
      }

   }

   public EnumActionResult interactWithEntity(EntityPlayer p_187097_1_, Entity p_187097_2_, EnumHand p_187097_3_) {
      this.syncCurrentPlayItem();
      this.connection.sendPacket(new CPacketUseEntity(p_187097_2_, p_187097_3_));
      return this.currentGameType == GameType.SPECTATOR ? EnumActionResult.PASS : p_187097_1_.interactOn(p_187097_2_, p_187097_3_);
   }

   public EnumActionResult interactWithEntity(EntityPlayer p_187102_1_, Entity p_187102_2_, RayTraceResult p_187102_3_, EnumHand p_187102_4_) {
      this.syncCurrentPlayItem();
      Vec3d vec3d = new Vec3d(p_187102_3_.hitVec.x - p_187102_2_.posX, p_187102_3_.hitVec.y - p_187102_2_.posY, p_187102_3_.hitVec.z - p_187102_2_.posZ);
      this.connection.sendPacket(new CPacketUseEntity(p_187102_2_, p_187102_4_, vec3d));
      return this.currentGameType == GameType.SPECTATOR ? EnumActionResult.PASS : p_187102_2_.applyPlayerInteraction(p_187102_1_, vec3d, p_187102_4_);
   }

   public ItemStack windowClick(int p_187098_1_, int p_187098_2_, int p_187098_3_, ClickType p_187098_4_, EntityPlayer p_187098_5_) {
      short short1 = p_187098_5_.openContainer.getNextTransactionID(p_187098_5_.inventory);
      ItemStack itemstack = p_187098_5_.openContainer.slotClick(p_187098_2_, p_187098_3_, p_187098_4_, p_187098_5_);
      this.connection.sendPacket(new CPacketClickWindow(p_187098_1_, p_187098_2_, p_187098_3_, p_187098_4_, itemstack, short1));
      return itemstack;
   }

   public void func_203413_a(int p_203413_1_, IRecipe p_203413_2_, boolean p_203413_3_) {
      this.connection.sendPacket(new CPacketPlaceRecipe(p_203413_1_, p_203413_2_, p_203413_3_));
   }

   public void sendEnchantPacket(int p_78756_1_, int p_78756_2_) {
      this.connection.sendPacket(new CPacketEnchantItem(p_78756_1_, p_78756_2_));
   }

   public void sendSlotPacket(ItemStack p_78761_1_, int p_78761_2_) {
      if (this.currentGameType.isCreative()) {
         this.connection.sendPacket(new CPacketCreativeInventoryAction(p_78761_2_, p_78761_1_));
      }

   }

   public void sendPacketDropItem(ItemStack p_78752_1_) {
      if (this.currentGameType.isCreative() && !p_78752_1_.isEmpty()) {
         this.connection.sendPacket(new CPacketCreativeInventoryAction(-1, p_78752_1_));
      }

   }

   public void onStoppedUsingItem(EntityPlayer p_78766_1_) {
      this.syncCurrentPlayItem();
      this.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
      p_78766_1_.stopActiveHand();
   }

   public boolean gameIsSurvivalOrAdventure() {
      return this.currentGameType.isSurvivalOrAdventure();
   }

   public boolean isNotCreative() {
      return !this.currentGameType.isCreative();
   }

   public boolean isInCreativeMode() {
      return this.currentGameType.isCreative();
   }

   public boolean extendedReach() {
      return this.currentGameType.isCreative();
   }

   public boolean isRidingHorse() {
      return this.mc.player.isRiding() && this.mc.player.getRidingEntity() instanceof AbstractHorse;
   }

   public boolean isSpectatorMode() {
      return this.currentGameType == GameType.SPECTATOR;
   }

   public GameType getCurrentGameType() {
      return this.currentGameType;
   }

   public boolean getIsHittingBlock() {
      return this.isHittingBlock;
   }

   public void pickItem(int p_187100_1_) {
      this.connection.sendPacket(new CPacketPickItem(p_187100_1_));
   }
}
