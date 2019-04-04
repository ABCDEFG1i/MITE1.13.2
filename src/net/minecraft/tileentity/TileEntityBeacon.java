package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityBeacon extends TileEntityLockable implements ISidedInventory, ITickable {
   public static final Potion[][] EFFECTS_LIST = new Potion[][]{{MobEffects.SPEED, MobEffects.HASTE}, {MobEffects.RESISTANCE, MobEffects.JUMP_BOOST}, {MobEffects.STRENGTH}, {MobEffects.REGENERATION}};
   private static final Set<Potion> VALID_EFFECTS = Arrays.stream(EFFECTS_LIST).flatMap(Arrays::stream).collect(Collectors.toSet());
   private final List<TileEntityBeacon.BeamSegment> beamSegments = Lists.newArrayList();
   @OnlyIn(Dist.CLIENT)
   private long beamRenderCounter;
   @OnlyIn(Dist.CLIENT)
   private float beamRenderScale;
   private boolean isComplete;
   private boolean lastTickComplete;
   private int levels = -1;
   @Nullable
   private Potion primaryEffect;
   @Nullable
   private Potion secondaryEffect;
   private ItemStack payment = ItemStack.EMPTY;
   private ITextComponent customName;

   public TileEntityBeacon() {
      super(TileEntityType.BEACON);
   }

   public void tick() {
      if (this.world.getTotalWorldTime() % 80L == 0L) {
         this.updateBeacon();
         if (this.isComplete) {
            this.playSound(SoundEvents.BLOCK_BEACON_AMBIENT);
         }
      }

      if (!this.world.isRemote && this.isComplete != this.lastTickComplete) {
         this.lastTickComplete = this.isComplete;
         this.playSound(this.isComplete ? SoundEvents.BLOCK_BEACON_ACTIVATE : SoundEvents.BLOCK_BEACON_DEACTIVATE);
      }

   }

   public void updateBeacon() {
      if (this.world != null) {
         this.updateSegmentColors();
         this.addEffectsToPlayers();
      }

   }

   public void playSound(SoundEvent p_205736_1_) {
      this.world.playSound(null, this.pos, p_205736_1_, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   private void addEffectsToPlayers() {
      if (this.isComplete && this.levels > 0 && !this.world.isRemote && this.primaryEffect != null) {
         double d0 = (double)(this.levels * 10 + 10);
         int i = 0;
         if (this.levels >= 4 && this.primaryEffect == this.secondaryEffect) {
            i = 1;
         }

         int j = (9 + this.levels * 2) * 20;
         int k = this.pos.getX();
         int l = this.pos.getY();
         int i1 = this.pos.getZ();
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)k, (double)l, (double)i1, (double)(k + 1), (double)(l + 1), (double)(i1 + 1))).grow(d0).expand(0.0D, (double)this.world.getHeight(), 0.0D);
         List<EntityPlayer> list = this.world.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);

         for(EntityPlayer entityplayer : list) {
            entityplayer.addPotionEffect(new PotionEffect(this.primaryEffect, j, i, true, true));
         }

         if (this.levels >= 4 && this.primaryEffect != this.secondaryEffect && this.secondaryEffect != null) {
            for(EntityPlayer entityplayer1 : list) {
               entityplayer1.addPotionEffect(new PotionEffect(this.secondaryEffect, j, 0, true, true));
            }
         }
      }

   }

   private void updateSegmentColors() {
      int i = this.pos.getX();
      int j = this.pos.getY();
      int k = this.pos.getZ();
      int l = this.levels;
      this.levels = 0;
      this.beamSegments.clear();
      this.isComplete = true;
      TileEntityBeacon.BeamSegment tileentitybeacon$beamsegment = new TileEntityBeacon.BeamSegment(EnumDyeColor.WHITE.getColorComponentValues());
      this.beamSegments.add(tileentitybeacon$beamsegment);
      boolean flag = true;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int i1 = j + 1; i1 < 256; ++i1) {
         IBlockState iblockstate = this.world.getBlockState(blockpos$mutableblockpos.setPos(i, i1, k));
         Block block = iblockstate.getBlock();
         float[] afloat;
         if (block instanceof BlockStainedGlass) {
            afloat = ((BlockStainedGlass)block).getColor().getColorComponentValues();
         } else {
            if (!(block instanceof BlockStainedGlassPane)) {
               if (iblockstate.getOpacity(this.world, blockpos$mutableblockpos) >= 15 && block != Blocks.BEDROCK) {
                  this.isComplete = false;
                  this.beamSegments.clear();
                  break;
               }

               tileentitybeacon$beamsegment.incrementHeight();
               continue;
            }

            afloat = ((BlockStainedGlassPane)block).getColor().getColorComponentValues();
         }

         if (!flag) {
            afloat = new float[]{(tileentitybeacon$beamsegment.getColors()[0] + afloat[0]) / 2.0F, (tileentitybeacon$beamsegment.getColors()[1] + afloat[1]) / 2.0F, (tileentitybeacon$beamsegment.getColors()[2] + afloat[2]) / 2.0F};
         }

         if (Arrays.equals(afloat, tileentitybeacon$beamsegment.getColors())) {
            tileentitybeacon$beamsegment.incrementHeight();
         } else {
            tileentitybeacon$beamsegment = new TileEntityBeacon.BeamSegment(afloat);
            this.beamSegments.add(tileentitybeacon$beamsegment);
         }

         flag = false;
      }

      if (this.isComplete) {
         for(int k1 = 1; k1 <= 4; this.levels = k1++) {
            int l1 = j - k1;
            if (l1 < 0) {
               break;
            }

            boolean flag1 = true;

            for(int i2 = i - k1; i2 <= i + k1 && flag1; ++i2) {
               for(int j1 = k - k1; j1 <= k + k1; ++j1) {
                  Block block1 = this.world.getBlockState(new BlockPos(i2, l1, j1)).getBlock();
                  if (block1 != Blocks.EMERALD_BLOCK && block1 != Blocks.GOLD_BLOCK && block1 != Blocks.DIAMOND_BLOCK && block1 != Blocks.IRON_BLOCK) {
                     flag1 = false;
                     break;
                  }
               }
            }

            if (!flag1) {
               break;
            }
         }

         if (this.levels == 0) {
            this.isComplete = false;
         }
      }

      if (!this.world.isRemote && l < this.levels) {
         for(EntityPlayerMP entityplayermp : this.world.getEntitiesWithinAABB(EntityPlayerMP.class, (new AxisAlignedBB((double)i, (double)j, (double)k, (double)i, (double)(j - 4), (double)k)).grow(10.0D, 5.0D, 10.0D))) {
            CriteriaTriggers.CONSTRUCT_BEACON.trigger(entityplayermp, this);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public List<TileEntityBeacon.BeamSegment> getBeamSegments() {
      return this.beamSegments;
   }

   @OnlyIn(Dist.CLIENT)
   public float shouldBeamRender() {
      if (!this.isComplete) {
         return 0.0F;
      } else {
         int i = (int)(this.world.getTotalWorldTime() - this.beamRenderCounter);
         this.beamRenderCounter = this.world.getTotalWorldTime();
         if (i > 1) {
            this.beamRenderScale -= (float)i / 40.0F;
            if (this.beamRenderScale < 0.0F) {
               this.beamRenderScale = 0.0F;
            }
         }

         this.beamRenderScale += 0.025F;
         if (this.beamRenderScale > 1.0F) {
            this.beamRenderScale = 1.0F;
         }

         return this.beamRenderScale;
      }
   }

   public int getLevels() {
      return this.levels;
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   @OnlyIn(Dist.CLIENT)
   public double getMaxRenderDistanceSquared() {
      return 65536.0D;
   }

   @Nullable
   private static Potion isBeaconEffect(int p_184279_0_) {
      Potion potion = Potion.getPotionById(p_184279_0_);
      return VALID_EFFECTS.contains(potion) ? potion : null;
   }

   public void readFromNBT(NBTTagCompound p_145839_1_) {
      super.readFromNBT(p_145839_1_);
      this.primaryEffect = isBeaconEffect(p_145839_1_.getInteger("Primary"));
      this.secondaryEffect = isBeaconEffect(p_145839_1_.getInteger("Secondary"));
      this.levels = p_145839_1_.getInteger("Levels");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
      super.writeToNBT(p_189515_1_);
      p_189515_1_.setInteger("Primary", Potion.getIdFromPotion(this.primaryEffect));
      p_189515_1_.setInteger("Secondary", Potion.getIdFromPotion(this.secondaryEffect));
      p_189515_1_.setInteger("Levels", this.levels);
      return p_189515_1_;
   }

   public int getSizeInventory() {
      return 1;
   }

   public boolean isEmpty() {
      return this.payment.isEmpty();
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      return p_70301_1_ == 0 ? this.payment : ItemStack.EMPTY;
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      if (p_70298_1_ == 0 && !this.payment.isEmpty()) {
         if (p_70298_2_ >= this.payment.getCount()) {
            ItemStack itemstack = this.payment;
            this.payment = ItemStack.EMPTY;
            return itemstack;
         } else {
            return this.payment.split(p_70298_2_);
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      if (p_70304_1_ == 0) {
         ItemStack itemstack = this.payment;
         this.payment = ItemStack.EMPTY;
         return itemstack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      if (p_70299_1_ == 0) {
         this.payment = p_70299_2_;
      }

   }

   public ITextComponent getName() {
      return this.customName != null ? this.customName : new TextComponentTranslation("container.beacon");
   }

   public boolean hasCustomName() {
      return this.customName != null;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.customName;
   }

   public void setCustomName(@Nullable ITextComponent p_200227_1_) {
      this.customName = p_200227_1_;
   }

   public int getInventoryStackLimit() {
      return 1;
   }

   public boolean isUsableByPlayer(EntityPlayer p_70300_1_) {
      if (this.world.getTileEntity(this.pos) != this) {
         return false;
      } else {
         return !(p_70300_1_.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) > 64.0D);
      }
   }

   public void openInventory(EntityPlayer p_174889_1_) {
   }

   public void closeInventory(EntityPlayer p_174886_1_) {
   }

   public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
      return p_94041_2_.getItem() == Items.EMERALD || p_94041_2_.getItem() == Items.DIAMOND || p_94041_2_.getItem() == Items.GOLD_INGOT || p_94041_2_.getItem() == Items.IRON_INGOT;
   }

   public String getGuiID() {
      return "minecraft:beacon";
   }

   public Container createContainer(InventoryPlayer p_174876_1_, EntityPlayer p_174876_2_) {
      return new ContainerBeacon(p_174876_1_, this);
   }

   public int getField(int p_174887_1_) {
      switch(p_174887_1_) {
      case 0:
         return this.levels;
      case 1:
         return Potion.getIdFromPotion(this.primaryEffect);
      case 2:
         return Potion.getIdFromPotion(this.secondaryEffect);
      default:
         return 0;
      }
   }

   public void setField(int p_174885_1_, int p_174885_2_) {
      switch(p_174885_1_) {
      case 0:
         this.levels = p_174885_2_;
         break;
      case 1:
         this.primaryEffect = isBeaconEffect(p_174885_2_);
         break;
      case 2:
         this.secondaryEffect = isBeaconEffect(p_174885_2_);
      }

      if (!this.world.isRemote && p_174885_1_ == 1 && this.isComplete) {
         this.playSound(SoundEvents.BLOCK_BEACON_POWER_SELECT);
      }

   }

   public int getFieldCount() {
      return 3;
   }

   public void clear() {
      this.payment = ItemStack.EMPTY;
   }

   public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_) {
      if (p_145842_1_ == 1) {
         this.updateBeacon();
         return true;
      } else {
         return super.receiveClientEvent(p_145842_1_, p_145842_2_);
      }
   }

   public int[] getSlotsForFace(EnumFacing p_180463_1_) {
      return new int[0];
   }

   public boolean canInsertItem(int p_180462_1_, ItemStack p_180462_2_, @Nullable EnumFacing p_180462_3_) {
      return false;
   }

   public boolean canExtractItem(int p_180461_1_, ItemStack p_180461_2_, EnumFacing p_180461_3_) {
      return false;
   }

   public static class BeamSegment {
      private final float[] colors;
      private int height;

      public BeamSegment(float[] p_i45669_1_) {
         this.colors = p_i45669_1_;
         this.height = 1;
      }

      protected void incrementHeight() {
         ++this.height;
      }

      public float[] getColors() {
         return this.colors;
      }

      @OnlyIn(Dist.CLIENT)
      public int getHeight() {
         return this.height;
      }
   }
}
