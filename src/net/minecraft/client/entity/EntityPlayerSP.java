package net.minecraft.client.entity;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class EntityPlayerSP extends AbstractClientPlayer {
   public final NetHandlerPlayClient connection;
   private final StatisticsManager statWriter;
   private final RecipeBookClient recipeBook;
   private final List<IAmbientSoundHandler> ambientSoundHandlers = Lists.newArrayList();
   private int permissionLevel = 0;
   private double lastReportedPosX;
   private double lastReportedPosY;
   private double lastReportedPosZ;
   private float lastReportedYaw;
   private float lastReportedPitch;
   private boolean prevOnGround;
   private boolean serverSneakState;
   private boolean serverSprintState;
   private int positionUpdateTicks;
   private boolean hasValidHealth;
   private String serverBrand;
   public MovementInput movementInput;
   protected Minecraft mc;
   protected int sprintToggleTimer;
   public int sprintingTicksLeft;
   public float renderArmYaw;
   public float renderArmPitch;
   public float prevRenderArmYaw;
   public float prevRenderArmPitch;
   private int horseJumpPowerCounter;
   private float horseJumpPower;
   public float timeInPortal;
   public float prevTimeInPortal;
   private boolean handActive;
   private EnumHand activeHand;
   private boolean rowingBoat;
   private boolean autoJumpEnabled = true;
   private int autoJumpTime;
   private boolean wasFallFlying;
   private int field_203720_cz;

   public EntityPlayerSP(Minecraft p_i48190_1_, World p_i48190_2_, NetHandlerPlayClient p_i48190_3_, StatisticsManager p_i48190_4_, RecipeBookClient p_i48190_5_) {
      super(p_i48190_2_, p_i48190_3_.getGameProfile());
      this.connection = p_i48190_3_;
      this.statWriter = p_i48190_4_;
      this.recipeBook = p_i48190_5_;
      this.mc = p_i48190_1_;
      this.dimension = DimensionType.OVERWORLD;
      this.ambientSoundHandlers.add(new UnderwaterAmbientSoundHandler(this, p_i48190_1_.getSoundHandler()));
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      return false;
   }

   public void heal(float p_70691_1_) {
   }

   public boolean startRiding(Entity p_184205_1_, boolean p_184205_2_) {
      if (!super.startRiding(p_184205_1_, p_184205_2_)) {
         return false;
      } else {
         if (p_184205_1_ instanceof EntityMinecart) {
            this.mc.getSoundHandler().play(new MovingSoundMinecartRiding(this, (EntityMinecart)p_184205_1_));
         }

         if (p_184205_1_ instanceof EntityBoat) {
            this.prevRotationYaw = p_184205_1_.rotationYaw;
            this.rotationYaw = p_184205_1_.rotationYaw;
            this.setRotationYawHead(p_184205_1_.rotationYaw);
         }

         return true;
      }
   }

   public void dismountRidingEntity() {
      super.dismountRidingEntity();
      this.rowingBoat = false;
   }

   public float getPitch(float p_195050_1_) {
      return this.rotationPitch;
   }

   public float getYaw(float p_195046_1_) {
      return this.isRiding() ? super.getYaw(p_195046_1_) : this.rotationYaw;
   }

   public void tick() {
      if (this.world.isBlockLoaded(new BlockPos(this.posX, 0.0D, this.posZ))) {
         super.tick();
         if (this.isRiding()) {
            this.connection.sendPacket(new CPacketPlayer.Rotation(this.rotationYaw, this.rotationPitch, this.onGround));
            this.connection.sendPacket(new CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
            Entity entity = this.getLowestRidingEntity();
            if (entity != this && entity.canPassengerSteer()) {
               this.connection.sendPacket(new CPacketVehicleMove(entity));
            }
         } else {
            this.onUpdateWalkingPlayer();
         }

         for(IAmbientSoundHandler iambientsoundhandler : this.ambientSoundHandlers) {
            iambientsoundhandler.tick();
         }

      }
   }

   private void onUpdateWalkingPlayer() {
      boolean flag = this.isSprinting();
      if (flag != this.serverSprintState) {
         if (flag) {
            this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SPRINTING));
         } else {
            this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SPRINTING));
         }

         this.serverSprintState = flag;
      }

      boolean flag1 = this.isSneaking();
      if (flag1 != this.serverSneakState) {
         if (flag1) {
            this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SNEAKING));
         } else {
            this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SNEAKING));
         }

         this.serverSneakState = flag1;
      }

      if (this.isCurrentViewEntity()) {
         AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
         double d0 = this.posX - this.lastReportedPosX;
         double d1 = axisalignedbb.minY - this.lastReportedPosY;
         double d2 = this.posZ - this.lastReportedPosZ;
         double d3 = (double)(this.rotationYaw - this.lastReportedYaw);
         double d4 = (double)(this.rotationPitch - this.lastReportedPitch);
         ++this.positionUpdateTicks;
         boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || this.positionUpdateTicks >= 20;
         boolean flag3 = d3 != 0.0D || d4 != 0.0D;
         if (this.isRiding()) {
            this.connection.sendPacket(new CPacketPlayer.PositionRotation(this.motionX, -999.0D, this.motionZ, this.rotationYaw, this.rotationPitch, this.onGround));
            flag2 = false;
         } else if (flag2 && flag3) {
            this.connection.sendPacket(new CPacketPlayer.PositionRotation(this.posX, axisalignedbb.minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround));
         } else if (flag2) {
            this.connection.sendPacket(new CPacketPlayer.Position(this.posX, axisalignedbb.minY, this.posZ, this.onGround));
         } else if (flag3) {
            this.connection.sendPacket(new CPacketPlayer.Rotation(this.rotationYaw, this.rotationPitch, this.onGround));
         } else if (this.prevOnGround != this.onGround) {
            this.connection.sendPacket(new CPacketPlayer(this.onGround));
         }

         if (flag2) {
            this.lastReportedPosX = this.posX;
            this.lastReportedPosY = axisalignedbb.minY;
            this.lastReportedPosZ = this.posZ;
            this.positionUpdateTicks = 0;
         }

         if (flag3) {
            this.lastReportedYaw = this.rotationYaw;
            this.lastReportedPitch = this.rotationPitch;
         }

         this.prevOnGround = this.onGround;
         this.autoJumpEnabled = this.mc.gameSettings.autoJump;
      }

   }

   @Nullable
   public EntityItem dropItem(boolean p_71040_1_) {
      CPacketPlayerDigging.Action cpacketplayerdigging$action = p_71040_1_ ? CPacketPlayerDigging.Action.DROP_ALL_ITEMS : CPacketPlayerDigging.Action.DROP_ITEM;
      this.connection.sendPacket(new CPacketPlayerDigging(cpacketplayerdigging$action, BlockPos.ORIGIN, EnumFacing.DOWN));
      this.inventory.decrStackSize(this.inventory.currentItem, p_71040_1_ && !this.inventory.getCurrentItem().isEmpty() ? this.inventory.getCurrentItem().getCount() : 1);
      return null;
   }

   public ItemStack dropItemAndGetStack(EntityItem p_184816_1_) {
      return ItemStack.EMPTY;
   }

   public void sendChatMessage(String p_71165_1_) {
      this.connection.sendPacket(new CPacketChatMessage(p_71165_1_));
   }

   public void swingArm(EnumHand p_184609_1_) {
      super.swingArm(p_184609_1_);
      this.connection.sendPacket(new CPacketAnimation(p_184609_1_));
   }

   public void respawnPlayer() {
      this.connection.sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
   }

   protected void damageEntity(DamageSource p_70665_1_, float p_70665_2_) {
      if (!this.isInvulnerableTo(p_70665_1_)) {
         this.setHealth(this.getHealth() - p_70665_2_);
      }
   }

   public void closeScreen() {
      this.connection.sendPacket(new CPacketCloseWindow(this.openContainer.windowId));
      this.closeScreenAndDropStack();
   }

   public void closeScreenAndDropStack() {
      this.inventory.setItemStack(ItemStack.EMPTY);
      super.closeScreen();
      this.mc.displayGuiScreen(null);
   }

   public void setPlayerSPHealth(float p_71150_1_) {
      if (this.hasValidHealth) {
         float f = this.getHealth() - p_71150_1_;
         if (f <= 0.0F) {
            this.setHealth(p_71150_1_);
            if (f < 0.0F) {
               this.hurtResistantTime = this.maxHurtResistantTime / 2;
            }
         } else {
            this.lastDamage = f;
            this.setHealth(this.getHealth());
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.damageEntity(DamageSource.GENERIC, f);
            this.maxHurtTime = 10;
            this.hurtTime = this.maxHurtTime;
         }
      } else {
         this.setHealth(p_71150_1_);
         this.hasValidHealth = true;
      }

   }

   public void sendPlayerAbilities() {
      this.connection.sendPacket(new CPacketPlayerAbilities(this.capabilities));
   }

   public boolean isUser() {
      return true;
   }

   protected void sendHorseJump() {
      this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_RIDING_JUMP, MathHelper.floor(this.getHorseJumpPower() * 100.0F)));
   }

   public void sendHorseInventory() {
      this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.OPEN_INVENTORY));
   }

   public void setServerBrand(String p_175158_1_) {
      this.serverBrand = p_175158_1_;
   }

   public String getServerBrand() {
      return this.serverBrand;
   }

   public StatisticsManager getStatFileWriter() {
      return this.statWriter;
   }

   public RecipeBookClient getRecipeBook() {
      return this.recipeBook;
   }

   public void removeRecipeHighlight(IRecipe p_193103_1_) {
      if (this.recipeBook.isNew(p_193103_1_)) {
         this.recipeBook.markSeen(p_193103_1_);
         this.connection.sendPacket(new CPacketRecipeInfo(p_193103_1_));
      }

   }

   protected int getPermissionLevel() {
      return this.permissionLevel;
   }

   public void setPermissionLevel(int p_184839_1_) {
      this.permissionLevel = p_184839_1_;
   }

   public void sendStatusMessage(ITextComponent p_146105_1_, boolean p_146105_2_) {
      if (p_146105_2_) {
         this.mc.ingameGUI.setOverlayMessage(p_146105_1_, false);
      } else {
         this.mc.ingameGUI.getChatGUI().printChatMessage(p_146105_1_);
      }

   }

   protected boolean pushOutOfBlocks(double p_145771_1_, double p_145771_3_, double p_145771_5_) {
      if (this.noClip) {
         return false;
      } else {
         BlockPos blockpos = new BlockPos(p_145771_1_, p_145771_3_, p_145771_5_);
         double d0 = p_145771_1_ - (double)blockpos.getX();
         double d1 = p_145771_5_ - (double)blockpos.getZ();
         if (this.func_205027_h(blockpos)) {
            int i = -1;
            double d2 = 9999.0D;
            if (this.func_207402_f(blockpos.west()) && d0 < d2) {
               d2 = d0;
               i = 0;
            }

            if (this.func_207402_f(blockpos.east()) && 1.0D - d0 < d2) {
               d2 = 1.0D - d0;
               i = 1;
            }

            if (this.func_207402_f(blockpos.north()) && d1 < d2) {
               d2 = d1;
               i = 4;
            }

            if (this.func_207402_f(blockpos.south()) && 1.0D - d1 < d2) {
               d2 = 1.0D - d1;
               i = 5;
            }

            float f = 0.1F;
            if (i == 0) {
               this.motionX = (double)-0.1F;
            }

            if (i == 1) {
               this.motionX = (double)0.1F;
            }

            if (i == 4) {
               this.motionZ = (double)-0.1F;
            }

            if (i == 5) {
               this.motionZ = (double)0.1F;
            }
         }

         return false;
      }
   }

   private boolean func_205027_h(BlockPos p_205027_1_) {
      if (this.isSwimming()) {
         return !this.isNormalCube(p_205027_1_);
      } else {
         return !this.func_207402_f(p_205027_1_);
      }
   }

   public void setSprinting(boolean p_70031_1_) {
      super.setSprinting(p_70031_1_);
      this.sprintingTicksLeft = 0;
   }

   public void setXPStats(float p_71152_1_, int p_71152_2_, int p_71152_3_) {
      this.experience = p_71152_1_;
      this.experienceTotal = p_71152_2_;
      this.experienceLevel = p_71152_3_;
      super.onLevelUpdate(experienceLevel);
   }

   public void sendMessage(ITextComponent p_145747_1_) {
      this.mc.ingameGUI.getChatGUI().printChatMessage(p_145747_1_);
   }

   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ >= 24 && p_70103_1_ <= 28) {
         this.setPermissionLevel(p_70103_1_ - 24);
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   public void playSound(SoundEvent p_184185_1_, float p_184185_2_, float p_184185_3_) {
      this.world.playSound(this.posX, this.posY, this.posZ, p_184185_1_, this.getSoundCategory(), p_184185_2_, p_184185_3_, false);
   }

   public boolean isServerWorld() {
      return true;
   }

   public void setActiveHand(EnumHand p_184598_1_) {
      ItemStack itemstack = this.getHeldItem(p_184598_1_);
      if (!itemstack.isEmpty() && !this.isHandActive()) {
         super.setActiveHand(p_184598_1_);
         this.handActive = true;
         this.activeHand = p_184598_1_;
      }
   }

   public boolean isHandActive() {
      return this.handActive;
   }

   public void resetActiveHand() {
      super.resetActiveHand();
      this.handActive = false;
   }

   public EnumHand getActiveHand() {
      return this.activeHand;
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      super.notifyDataManagerChange(p_184206_1_);
      if (HAND_STATES.equals(p_184206_1_)) {
         boolean flag = (this.dataManager.get(HAND_STATES) & 1) > 0;
         EnumHand enumhand = (this.dataManager.get(HAND_STATES) & 2) > 0 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
         if (flag && !this.handActive) {
            this.setActiveHand(enumhand);
         } else if (!flag && this.handActive) {
            this.resetActiveHand();
         }
      }

      if (FLAGS.equals(p_184206_1_) && this.isElytraFlying() && !this.wasFallFlying) {
         this.mc.getSoundHandler().play(new ElytraSound(this));
      }

   }

   public boolean isRidingHorse() {
      Entity entity = this.getRidingEntity();
      return this.isRiding() && entity instanceof IJumpingMount && ((IJumpingMount)entity).canJump();
   }

   public float getHorseJumpPower() {
      return this.horseJumpPower;
   }

   public void openEditSign(TileEntitySign p_175141_1_) {
      this.mc.displayGuiScreen(new GuiEditSign(p_175141_1_));
   }

   public void displayGuiEditCommandCart(CommandBlockBaseLogic p_184809_1_) {
      this.mc.displayGuiScreen(new GuiEditCommandBlockMinecart(p_184809_1_));
   }

   public void displayGuiCommandBlock(TileEntityCommandBlock p_184824_1_) {
      this.mc.displayGuiScreen(new GuiCommandBlock(p_184824_1_));
   }

   public void openEditStructure(TileEntityStructure p_189807_1_) {
      this.mc.displayGuiScreen(new GuiEditStructure(p_189807_1_));
   }

   public void openBook(ItemStack p_184814_1_, EnumHand p_184814_2_) {
      Item item = p_184814_1_.getItem();
      if (item == Items.WRITABLE_BOOK) {
         this.mc.displayGuiScreen(new GuiScreenBook(this, p_184814_1_, true, p_184814_2_));
      }

   }

   public void displayGUIChest(IInventory p_71007_1_) {
      String s = p_71007_1_ instanceof IInteractionObject ? ((IInteractionObject) p_71007_1_).getGuiID() : "minecraft:container";
      switch (s) {
         case "minecraft:hopper":
            this.mc.displayGuiScreen(new GuiHopper(this.inventory, p_71007_1_));
            break;
         case "minecraft:furnace":
            this.mc.displayGuiScreen(new GuiFurnace(this.inventory, p_71007_1_));
            break;
         case "minecraft:brewing_stand":
            this.mc.displayGuiScreen(new GuiBrewingStand(this.inventory, p_71007_1_));
            break;
         case "minecraft:beacon":
            this.mc.displayGuiScreen(new GuiBeacon(this.inventory, p_71007_1_));
            break;
         case "minecraft:dispenser":
         case "minecraft:dropper":
            this.mc.displayGuiScreen(new GuiDispenser(this.inventory, p_71007_1_));
            break;
         case "minecraft:shulker_box":
            this.mc.displayGuiScreen(new GuiShulkerBox(this.inventory, p_71007_1_));
            break;
         default:
            this.mc.displayGuiScreen(new GuiChest(this.inventory, p_71007_1_));
            break;
      }

   }

   public void openGuiHorseInventory(AbstractHorse p_184826_1_, IInventory p_184826_2_) {
      this.mc.displayGuiScreen(new GuiScreenHorseInventory(this.inventory, p_184826_2_, p_184826_1_));
   }

   public void displayGui(IInteractionObject p_180468_1_) {
      String s = p_180468_1_.getGuiID();
      switch (s) {
         case "minecraft:crafting_table":
            this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.world,p_180468_1_.getGuiLevel()));
            break;
         case "minecraft:enchanting_table":
            this.mc.displayGuiScreen(new GuiEnchantment(this.inventory, this.world, p_180468_1_));
            break;
         case "minecraft:anvil":
            this.mc.displayGuiScreen(new GuiRepair(this.inventory, this.world, p_180468_1_.getGuiLevel()));
            break;
      }

   }

   public void displayVillagerTradeGui(IMerchant p_180472_1_) {
      this.mc.displayGuiScreen(new GuiMerchant(this.inventory, p_180472_1_, this.world));
   }

   public void onCriticalHit(Entity p_71009_1_) {
      this.mc.effectRenderer.addParticleEmitter(p_71009_1_, Particles.CRIT);
   }

   public void onEnchantmentCritical(Entity p_71047_1_) {
      this.mc.effectRenderer.addParticleEmitter(p_71047_1_, Particles.ENCHANTED_HIT);
   }

   public boolean isSneaking() {
      boolean flag = this.movementInput != null && this.movementInput.sneak;
      return flag && !this.sleeping;
   }

   public void updateEntityActionState() {
      super.updateEntityActionState();
      if (this.isCurrentViewEntity()) {
         this.moveStrafing = this.movementInput.moveStrafe;
         this.moveForward = this.movementInput.moveForward;
         this.isJumping = this.movementInput.jump;
         this.prevRenderArmYaw = this.renderArmYaw;
         this.prevRenderArmPitch = this.renderArmPitch;
         this.renderArmPitch = (float)((double)this.renderArmPitch + (double)(this.rotationPitch - this.renderArmPitch) * 0.5D);
         this.renderArmYaw = (float)((double)this.renderArmYaw + (double)(this.rotationYaw - this.renderArmYaw) * 0.5D);
      }

   }

   protected boolean isCurrentViewEntity() {
      return this.mc.getRenderViewEntity() == this;
   }

   public void livingTick() {
      ++this.sprintingTicksLeft;
      if (this.sprintToggleTimer > 0) {
         --this.sprintToggleTimer;
      }

      this.prevTimeInPortal = this.timeInPortal;
      if (this.inPortal) {
         if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame()) {
            if (this.mc.currentScreen instanceof GuiContainer) {
               this.closeScreen();
            }

            this.mc.displayGuiScreen(null);
         }

         if (this.timeInPortal == 0.0F) {
            this.mc.getSoundHandler().play(SimpleSound.func_184371_a(SoundEvents.BLOCK_PORTAL_TRIGGER, this.rand.nextFloat() * 0.4F + 0.8F));
         }

         this.timeInPortal += 0.0125F;
         if (this.timeInPortal >= 1.0F) {
            this.timeInPortal = 1.0F;
         }

         this.inPortal = false;
      } else if (this.isPotionActive(MobEffects.NAUSEA) && this.getActivePotionEffect(MobEffects.NAUSEA).getDuration() > 60) {
         this.timeInPortal += 0.006666667F;
         if (this.timeInPortal > 1.0F) {
            this.timeInPortal = 1.0F;
         }
      } else {
         if (this.timeInPortal > 0.0F) {
            this.timeInPortal -= 0.05F;
         }

         if (this.timeInPortal < 0.0F) {
            this.timeInPortal = 0.0F;
         }
      }

      if (this.timeUntilPortal > 0) {
         --this.timeUntilPortal;
      }

      boolean flag = this.movementInput.jump;
      boolean flag1 = this.movementInput.sneak;
      float f = 0.8F;
      boolean flag2 = this.movementInput.moveForward >= 0.8F;
      this.movementInput.updatePlayerMoveState();
      this.mc.getTutorial().handleMovement(this.movementInput);
      if (this.isHandActive() && !this.isRiding()) {
         this.movementInput.moveStrafe *= 0.2F;
         this.movementInput.moveForward *= 0.2F;
         this.sprintToggleTimer = 0;
      }

      boolean flag3 = false;
      if (this.autoJumpTime > 0) {
         --this.autoJumpTime;
         flag3 = true;
         this.movementInput.jump = true;
      }

      AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
      this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, axisalignedbb.minY + 0.5D, this.posZ + (double)this.width * 0.35D);
      this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, axisalignedbb.minY + 0.5D, this.posZ - (double)this.width * 0.35D);
      this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, axisalignedbb.minY + 0.5D, this.posZ - (double)this.width * 0.35D);
      this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, axisalignedbb.minY + 0.5D, this.posZ + (double)this.width * 0.35D);
      boolean flag4 = this.getFoodStats().getSaturationLevel() > 0.0F || (float)this.getFoodStats().getFoodLevel() > 0.0F || this.capabilities.allowFlying;
      if ((this.onGround || this.canSwim()) && !flag1 && !flag2 && this.movementInput.moveForward >= 0.8F && !this.isSprinting() && flag4 && !this.isHandActive() && !this.isPotionActive(MobEffects.BLINDNESS)) {
         if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown()) {
            this.sprintToggleTimer = 7;
         } else {
            this.setSprinting(true);
         }
      }

      if (!this.isSprinting() && (!this.isInWater() || this.canSwim()) && this.movementInput.moveForward >= 0.8F && flag4 && !this.isHandActive() && !this.isPotionActive(MobEffects.BLINDNESS) && this.mc.gameSettings.keyBindSprint.isKeyDown()) {
         this.setSprinting(true);
      }

      if (this.isSprinting()) {
         boolean flag5 = this.movementInput.moveForward < 0.8F || !flag4;
         boolean flag6 = flag5 || this.collidedHorizontally || this.isInWater() && !this.canSwim();
         if (this.isSwimming()) {
            if (!this.onGround && !this.movementInput.sneak && flag5 || !this.isInWater()) {
               this.setSprinting(false);
            }
         } else if (flag6) {
            this.setSprinting(false);
         }
      }

      if (this.capabilities.allowFlying) {
         if (this.mc.playerController.isSpectatorMode()) {
            if (!this.capabilities.isFlying) {
               this.capabilities.isFlying = true;
               this.sendPlayerAbilities();
            }
         } else if (!flag && this.movementInput.jump && !flag3) {
            if (this.flyToggleTimer == 0) {
               this.flyToggleTimer = 7;
            } else if (!this.isSwimming()) {
               this.capabilities.isFlying = !this.capabilities.isFlying;
               this.sendPlayerAbilities();
               this.flyToggleTimer = 0;
            }
         }
      }

      if (this.movementInput.jump && !flag && !this.onGround && this.motionY < 0.0D && !this.isElytraFlying() && !this.capabilities.isFlying) {
         ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
         if (itemstack.getItem() == Items.ELYTRA && ItemElytra.isUsable(itemstack)) {
            this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_FALL_FLYING));
         }
      }

      this.wasFallFlying = this.isElytraFlying();
      if (this.isInWater() && this.movementInput.sneak) {
         this.func_203010_cG();
      }

      if (this.areEyesInFluid(FluidTags.WATER)) {
         int i = this.isSpectator() ? 10 : 1;
         this.field_203720_cz = MathHelper.clamp(this.field_203720_cz + i, 0, 600);
      } else if (this.field_203720_cz > 0) {
         this.areEyesInFluid(FluidTags.WATER);
         this.field_203720_cz = MathHelper.clamp(this.field_203720_cz - 10, 0, 600);
      }

      if (this.capabilities.isFlying && this.isCurrentViewEntity()) {
         if (this.movementInput.sneak) {
            this.movementInput.moveStrafe = (float)((double)this.movementInput.moveStrafe / 0.3D);
            this.movementInput.moveForward = (float)((double)this.movementInput.moveForward / 0.3D);
            this.motionY -= (double)(this.capabilities.getFlySpeed() * 3.0F);
         }

         if (this.movementInput.jump) {
            this.motionY += (double)(this.capabilities.getFlySpeed() * 3.0F);
         }
      }

      if (this.isRidingHorse()) {
         IJumpingMount ijumpingmount = (IJumpingMount)this.getRidingEntity();
         if (this.horseJumpPowerCounter < 0) {
            ++this.horseJumpPowerCounter;
            if (this.horseJumpPowerCounter == 0) {
               this.horseJumpPower = 0.0F;
            }
         }

         if (flag && !this.movementInput.jump) {
            this.horseJumpPowerCounter = -10;
            ijumpingmount.setJumpPower(MathHelper.floor(this.getHorseJumpPower() * 100.0F));
            this.sendHorseJump();
         } else if (!flag && this.movementInput.jump) {
            this.horseJumpPowerCounter = 0;
            this.horseJumpPower = 0.0F;
         } else if (flag) {
            ++this.horseJumpPowerCounter;
            if (this.horseJumpPowerCounter < 10) {
               this.horseJumpPower = (float)this.horseJumpPowerCounter * 0.1F;
            } else {
               this.horseJumpPower = 0.8F + 2.0F / (float)(this.horseJumpPowerCounter - 9) * 0.1F;
            }
         }
      } else {
         this.horseJumpPower = 0.0F;
      }

      super.livingTick();
      if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
         this.capabilities.isFlying = false;
         this.sendPlayerAbilities();
      }

   }

   public void updateRidden() {
      super.updateRidden();
      this.rowingBoat = false;
      if (this.getRidingEntity() instanceof EntityBoat) {
         EntityBoat entityboat = (EntityBoat)this.getRidingEntity();
         entityboat.updateInputs(this.movementInput.leftKeyDown, this.movementInput.rightKeyDown, this.movementInput.forwardKeyDown, this.movementInput.backKeyDown);
         this.rowingBoat |= this.movementInput.leftKeyDown || this.movementInput.rightKeyDown || this.movementInput.forwardKeyDown || this.movementInput.backKeyDown;
      }

   }

   public boolean isRowingBoat() {
      return this.rowingBoat;
   }

   @Nullable
   public PotionEffect removeActivePotionEffect(@Nullable Potion p_184596_1_) {
      if (p_184596_1_ == MobEffects.NAUSEA) {
         this.prevTimeInPortal = 0.0F;
         this.timeInPortal = 0.0F;
      }

      return super.removeActivePotionEffect(p_184596_1_);
   }

   public void move(MoverType p_70091_1_, double p_70091_2_, double p_70091_4_, double p_70091_6_) {
      double d0 = this.posX;
      double d1 = this.posZ;
      super.move(p_70091_1_, p_70091_2_, p_70091_4_, p_70091_6_);
      this.updateAutoJump((float)(this.posX - d0), (float)(this.posZ - d1));
   }

   public boolean isAutoJumpEnabled() {
      return this.autoJumpEnabled;
   }

   protected void updateAutoJump(float p_189810_1_, float p_189810_2_) {
      if (this.isAutoJumpEnabled()) {
         if (this.autoJumpTime <= 0 && this.onGround && !this.isSneaking() && !this.isRiding()) {
            Vec2f vec2f = this.movementInput.getMoveVector();
            if (vec2f.x != 0.0F || vec2f.y != 0.0F) {
               Vec3d vec3d = new Vec3d(this.posX, this.getEntityBoundingBox().minY, this.posZ);
               double d0 = this.posX + (double)p_189810_1_;
               double d1 = this.posZ + (double)p_189810_2_;
               Vec3d vec3d1 = new Vec3d(d0, this.getEntityBoundingBox().minY, d1);
               Vec3d vec3d2 = new Vec3d((double)p_189810_1_, 0.0D, (double)p_189810_2_);
               float f = this.getAIMoveSpeed();
               float f1 = (float)vec3d2.lengthSquared();
               if (f1 <= 0.001F) {
                  float f2 = f * vec2f.x;
                  float f3 = f * vec2f.y;
                  float f4 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F));
                  float f5 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F));
                  vec3d2 = new Vec3d((double)(f2 * f5 - f3 * f4), vec3d2.y, (double)(f3 * f5 + f2 * f4));
                  f1 = (float)vec3d2.lengthSquared();
                  if (f1 <= 0.001F) {
                     return;
                  }
               }

               float f12 = (float)MathHelper.fastInvSqrt((double)f1);
               Vec3d vec3d12 = vec3d2.scale((double)f12);
               Vec3d vec3d13 = this.getForward();
               float f13 = (float)(vec3d13.x * vec3d12.x + vec3d13.z * vec3d12.z);
               if (!(f13 < -0.15F)) {
                  BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().maxY, this.posZ);
                  IBlockState iblockstate = this.world.getBlockState(blockpos);
                  if (iblockstate.getCollisionShape(this.world, blockpos).isEmpty()) {
                     blockpos = blockpos.up();
                     IBlockState iblockstate1 = this.world.getBlockState(blockpos);
                     if (iblockstate1.getCollisionShape(this.world, blockpos).isEmpty()) {
                        float f6 = 7.0F;
                        float f7 = 1.2F;
                        if (this.isPotionActive(MobEffects.JUMP_BOOST)) {
                           f7 += (float)(this.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.75F;
                        }

                        float f8 = Math.max(f * 7.0F, 1.0F / f12);
                        Vec3d vec3d4 = vec3d1.add(vec3d12.scale((double)f8));
                        float f9 = this.width;
                        float f10 = this.height;
                        AxisAlignedBB axisalignedbb = (new AxisAlignedBB(vec3d, vec3d4.add(0.0D, (double)f10, 0.0D))).grow((double)f9, 0.0D, (double)f9);
                        Vec3d lvt_19_1_ = vec3d.add(0.0D, (double)0.51F, 0.0D);
                        vec3d4 = vec3d4.add(0.0D, (double)0.51F, 0.0D);
                        Vec3d vec3d5 = vec3d12.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D));
                        Vec3d vec3d6 = vec3d5.scale((double)(f9 * 0.5F));
                        Vec3d vec3d7 = lvt_19_1_.subtract(vec3d6);
                        Vec3d vec3d8 = vec3d4.subtract(vec3d6);
                        Vec3d vec3d9 = lvt_19_1_.add(vec3d6);
                        Vec3d vec3d10 = vec3d4.add(vec3d6);
                        Iterator<AxisAlignedBB> iterator = this.world.func_212388_b(this, axisalignedbb).flatMap((p_212329_0_) -> {
                           return p_212329_0_.toBoundingBoxList().stream();
                        }).iterator();
                        float f11 = Float.MIN_VALUE;

                        while(iterator.hasNext()) {
                           AxisAlignedBB axisalignedbb1 = iterator.next();
                           if (axisalignedbb1.intersects(vec3d7, vec3d8) || axisalignedbb1.intersects(vec3d9, vec3d10)) {
                              f11 = (float)axisalignedbb1.maxY;
                              Vec3d vec3d11 = axisalignedbb1.getCenter();
                              BlockPos blockpos1 = new BlockPos(vec3d11);

                              for(int i = 1; (float)i < f7; ++i) {
                                 BlockPos blockpos2 = blockpos1.up(i);
                                 IBlockState iblockstate2 = this.world.getBlockState(blockpos2);
                                 VoxelShape voxelshape;
                                 if (!(voxelshape = iblockstate2.getCollisionShape(this.world, blockpos2)).isEmpty()) {
                                    f11 = (float)voxelshape.getEnd(EnumFacing.Axis.Y) + (float)blockpos2.getY();
                                    if ((double)f11 - this.getEntityBoundingBox().minY > (double)f7) {
                                       return;
                                    }
                                 }

                                 if (i > 1) {
                                    blockpos = blockpos.up();
                                    IBlockState iblockstate3 = this.world.getBlockState(blockpos);
                                    if (!iblockstate3.getCollisionShape(this.world, blockpos).isEmpty()) {
                                       return;
                                    }
                                 }
                              }
                              break;
                           }
                        }

                        if (f11 != Float.MIN_VALUE) {
                           float f14 = (float)((double)f11 - this.getEntityBoundingBox().minY);
                           if (!(f14 <= 0.5F) && !(f14 > f7)) {
                              this.autoJumpTime = 1;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public float func_203719_J() {
      if (!this.areEyesInFluid(FluidTags.WATER)) {
         return 0.0F;
      } else {
         float f = 600.0F;
         float f1 = 100.0F;
         if ((float)this.field_203720_cz >= 600.0F) {
            return 1.0F;
         } else {
            float f2 = MathHelper.clamp((float)this.field_203720_cz / 100.0F, 0.0F, 1.0F);
            float f3 = (float)this.field_203720_cz < 100.0F ? 0.0F : MathHelper.clamp(((float)this.field_203720_cz - 100.0F) / 500.0F, 0.0F, 1.0F);
            return f2 * 0.6F + f3 * 0.39999998F;
         }
      }
   }

   public boolean canSwim() {
      return this.eyesInWaterPlayer;
   }

   protected boolean updateEyesInWaterPlayer() {
      boolean flag = this.eyesInWaterPlayer;
      boolean flag1 = super.updateEyesInWaterPlayer();
      if (this.isSpectator()) {
         return this.eyesInWaterPlayer;
      } else {
         if (!flag && flag1) {
            this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
            this.mc.getSoundHandler().play(new UnderwaterAmbientSounds.UnderWaterSound(this));
         }

         if (flag && !flag1) {
            this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
         }

         return this.eyesInWaterPlayer;
      }
   }
}
