package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.properties.ChestType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityChest extends TileEntityLockableLoot implements IChestLid, ITickable {
   private NonNullList<ItemStack> chestContents = NonNullList.withSize(27, ItemStack.EMPTY);
   protected float lidAngle;
   protected float prevLidAngle;
   protected int numPlayersUsing;
   private int ticksSinceSync;

   protected TileEntityChest(TileEntityType<?> p_i48287_1_) {
      super(p_i48287_1_);
   }

   public TileEntityChest() {
      this(TileEntityType.CHEST);
   }

   public int getSizeInventory() {
      return 27;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.chestContents) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ITextComponent getName() {
      ITextComponent itextcomponent = this.getCustomName();
      return itextcomponent != null ? itextcomponent : new TextComponentTranslation("container.chest");
   }

   public void readFromNBT(NBTTagCompound p_145839_1_) {
      super.readFromNBT(p_145839_1_);
      this.chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (!this.checkLootAndRead(p_145839_1_)) {
         ItemStackHelper.loadAllItems(p_145839_1_, this.chestContents);
      }

      if (p_145839_1_.hasKey("CustomName", 8)) {
         this.customName = ITextComponent.Serializer.fromJson(p_145839_1_.getString("CustomName"));
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
      super.writeToNBT(p_189515_1_);
      if (!this.checkLootAndWrite(p_189515_1_)) {
         ItemStackHelper.saveAllItems(p_189515_1_, this.chestContents);
      }

      ITextComponent itextcomponent = this.getCustomName();
      if (itextcomponent != null) {
         p_189515_1_.setString("CustomName", ITextComponent.Serializer.toJson(itextcomponent));
      }

      return p_189515_1_;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void tick() {
      int i = this.pos.getX();
      int j = this.pos.getY();
      int k = this.pos.getZ();
      ++this.ticksSinceSync;
      if (!this.world.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0) {
         this.numPlayersUsing = 0;
         float f = 5.0F;

         for(EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((double)((float)i - 5.0F), (double)((float)j - 5.0F), (double)((float)k - 5.0F), (double)((float)(i + 1) + 5.0F), (double)((float)(j + 1) + 5.0F), (double)((float)(k + 1) + 5.0F)))) {
            if (entityplayer.openContainer instanceof ContainerChest) {
               IInventory iinventory = ((ContainerChest)entityplayer.openContainer).getLowerChestInventory();
               if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest)iinventory).isPartOfLargeChest(this)) {
                  ++this.numPlayersUsing;
               }
            }
         }
      }

      this.prevLidAngle = this.lidAngle;
      float f1 = 0.1F;
      if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
         this.playSound(SoundEvents.BLOCK_CHEST_OPEN);
      }

      if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
         float f2 = this.lidAngle;
         if (this.numPlayersUsing > 0) {
            this.lidAngle += 0.1F;
         } else {
            this.lidAngle -= 0.1F;
         }

         if (this.lidAngle > 1.0F) {
            this.lidAngle = 1.0F;
         }

         float f3 = 0.5F;
         if (this.lidAngle < 0.5F && f2 >= 0.5F) {
            this.playSound(SoundEvents.BLOCK_CHEST_CLOSE);
         }

         if (this.lidAngle < 0.0F) {
            this.lidAngle = 0.0F;
         }
      }

   }

   private void playSound(SoundEvent p_195483_1_) {
      ChestType chesttype = this.getBlockState().get(BlockChest.TYPE);
      if (chesttype != ChestType.LEFT) {
         double d0 = (double)this.pos.getX() + 0.5D;
         double d1 = (double)this.pos.getY() + 0.5D;
         double d2 = (double)this.pos.getZ() + 0.5D;
         if (chesttype == ChestType.RIGHT) {
            EnumFacing enumfacing = BlockChest.getDirectionToAttached(this.getBlockState());
            d0 += (double)enumfacing.getXOffset() * 0.5D;
            d2 += (double)enumfacing.getZOffset() * 0.5D;
         }

         this.world.playSound(null, d0, d1, d2, p_195483_1_, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
      }
   }

   public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_) {
      if (p_145842_1_ == 1) {
         this.numPlayersUsing = p_145842_2_;
         return true;
      } else {
         return super.receiveClientEvent(p_145842_1_, p_145842_2_);
      }
   }

   public void openInventory(EntityPlayer p_174889_1_) {
      if (!p_174889_1_.isSpectator()) {
         if (this.numPlayersUsing < 0) {
            this.numPlayersUsing = 0;
         }

         ++this.numPlayersUsing;
         this.onOpenOrClose();
      }

   }

   public void closeInventory(EntityPlayer p_174886_1_) {
      if (!p_174886_1_.isSpectator()) {
         --this.numPlayersUsing;
         this.onOpenOrClose();
      }

   }

   protected void onOpenOrClose() {
      Block block = this.getBlockState().getBlock();
      if (block instanceof BlockChest) {
         this.world.addBlockEvent(this.pos, block, 1, this.numPlayersUsing);
         this.world.notifyNeighborsOfStateChange(this.pos, block);
      }

   }

   public String getGuiID() {
      return "minecraft:chest";
   }

   public Container createContainer(InventoryPlayer p_174876_1_, EntityPlayer p_174876_2_) {
      this.fillWithLoot(p_174876_2_);
      return new ContainerChest(p_174876_1_, this, p_174876_2_);
   }

   protected NonNullList<ItemStack> getItems() {
      return this.chestContents;
   }

   protected void setItems(NonNullList<ItemStack> p_199721_1_) {
      this.chestContents = p_199721_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public float getLidAngle(float p_195480_1_) {
      return this.prevLidAngle + (this.lidAngle - this.prevLidAngle) * p_195480_1_;
   }

   public static int getPlayersUsing(IBlockReader p_195481_0_, BlockPos p_195481_1_) {
      IBlockState iblockstate = p_195481_0_.getBlockState(p_195481_1_);
      if (iblockstate.getBlock().hasTileEntity()) {
         TileEntity tileentity = p_195481_0_.getTileEntity(p_195481_1_);
         if (tileentity instanceof TileEntityChest) {
            return ((TileEntityChest)tileentity).numPlayersUsing;
         }
      }

      return 0;
   }

   public static void swapContents(TileEntityChest p_199722_0_, TileEntityChest p_199722_1_) {
      NonNullList<ItemStack> nonnulllist = p_199722_0_.getItems();
      p_199722_0_.setItems(p_199722_1_.getItems());
      p_199722_1_.setItems(nonnulllist);
   }
}
