package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityMinecartHopper extends EntityMinecartContainer implements IHopper {
   private boolean isBlocked = true;
   private int transferTicker = -1;
   private final BlockPos lastPosition = BlockPos.ORIGIN;

   public EntityMinecartHopper(World p_i1720_1_) {
      super(EntityType.HOPPER_MINECART, p_i1720_1_);
   }

   public EntityMinecartHopper(World p_i1721_1_, double p_i1721_2_, double p_i1721_4_, double p_i1721_6_) {
      super(EntityType.HOPPER_MINECART, p_i1721_2_, p_i1721_4_, p_i1721_6_, p_i1721_1_);
   }

   public EntityMinecart.Type getMinecartType() {
      return EntityMinecart.Type.HOPPER;
   }

   public IBlockState getDefaultDisplayTile() {
      return Blocks.HOPPER.getDefaultState();
   }

   public int getDefaultDisplayTileOffset() {
      return 1;
   }

   public int getSizeInventory() {
      return 5;
   }

   public boolean processInitialInteract(EntityPlayer p_184230_1_, EnumHand p_184230_2_) {
      if (!this.world.isRemote) {
         p_184230_1_.displayGUIChest(this);
      }

      return true;
   }

   public void onActivatorRailPass(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_) {
      boolean flag = !p_96095_4_;
      if (flag != this.getBlocked()) {
         this.setBlocked(flag);
      }

   }

   public boolean getBlocked() {
      return this.isBlocked;
   }

   public void setBlocked(boolean p_96110_1_) {
      this.isBlocked = p_96110_1_;
   }

   public World getWorld() {
      return this.world;
   }

   public double getXPos() {
      return this.posX;
   }

   public double getYPos() {
      return this.posY + 0.5D;
   }

   public double getZPos() {
      return this.posZ;
   }

   public void tick() {
      super.tick();
      if (!this.world.isRemote && this.isEntityAlive() && this.getBlocked()) {
         BlockPos blockpos = new BlockPos(this);
         if (blockpos.equals(this.lastPosition)) {
            --this.transferTicker;
         } else {
            this.setTransferTicker(0);
         }

         if (!this.canTransfer()) {
            this.setTransferTicker(0);
            if (this.captureDroppedItems()) {
               this.setTransferTicker(4);
               this.markDirty();
            }
         }
      }

   }

   public boolean captureDroppedItems() {
      if (TileEntityHopper.pullItems(this)) {
         return true;
      } else {
         List<EntityItem> list = this.world.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().grow(0.25D, 0.0D, 0.25D), EntitySelectors.IS_ALIVE);
         if (!list.isEmpty()) {
            TileEntityHopper.captureItem(this, list.get(0));
         }

         return false;
      }
   }

   public void killMinecart(DamageSource p_94095_1_) {
      super.killMinecart(p_94095_1_);
      if (this.world.getGameRules().getBoolean("doEntityDrops")) {
         this.entityDropItem(Blocks.HOPPER);
      }

   }

   protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("TransferCooldown", this.transferTicker);
      p_70014_1_.setBoolean("Enabled", this.isBlocked);
   }

   protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.transferTicker = p_70037_1_.getInteger("TransferCooldown");
      this.isBlocked = !p_70037_1_.hasKey("Enabled") || p_70037_1_.getBoolean("Enabled");
   }

   public void setTransferTicker(int p_98042_1_) {
      this.transferTicker = p_98042_1_;
   }

   public boolean canTransfer() {
      return this.transferTicker > 0;
   }

   public String getGuiID() {
      return "minecraft:hopper";
   }

   public Container createContainer(InventoryPlayer p_174876_1_, EntityPlayer p_174876_2_) {
      return new ContainerHopper(p_174876_1_, this, p_174876_2_);
   }
}
