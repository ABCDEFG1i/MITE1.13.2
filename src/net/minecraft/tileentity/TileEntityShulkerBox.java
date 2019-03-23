package net.minecraft.tileentity;

import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityShulkerBox extends TileEntityLockableLoot implements ISidedInventory, ITickable {
   private static final int[] SLOTS = IntStream.range(0, 27).toArray();
   private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
   private boolean hasBeenCleared;
   private int openCount;
   private TileEntityShulkerBox.AnimationStatus animationStatus = TileEntityShulkerBox.AnimationStatus.CLOSED;
   private float progress;
   private float progressOld;
   private EnumDyeColor color;
   private boolean needsColorFromWorld;
   private boolean destroyedByCreativePlayer;

   public TileEntityShulkerBox(@Nullable EnumDyeColor p_i47242_1_) {
      super(TileEntityType.SHULKER_BOX);
      this.color = p_i47242_1_;
   }

   public TileEntityShulkerBox() {
      this((EnumDyeColor)null);
      this.needsColorFromWorld = true;
   }

   public void tick() {
      this.updateAnimation();
      if (this.animationStatus == TileEntityShulkerBox.AnimationStatus.OPENING || this.animationStatus == TileEntityShulkerBox.AnimationStatus.CLOSING) {
         this.moveCollidedEntities();
      }

   }

   protected void updateAnimation() {
      this.progressOld = this.progress;
      switch(this.animationStatus) {
      case CLOSED:
         this.progress = 0.0F;
         break;
      case OPENING:
         this.progress += 0.1F;
         if (this.progress >= 1.0F) {
            this.moveCollidedEntities();
            this.animationStatus = TileEntityShulkerBox.AnimationStatus.OPENED;
            this.progress = 1.0F;
         }
         break;
      case CLOSING:
         this.progress -= 0.1F;
         if (this.progress <= 0.0F) {
            this.animationStatus = TileEntityShulkerBox.AnimationStatus.CLOSED;
            this.progress = 0.0F;
         }
         break;
      case OPENED:
         this.progress = 1.0F;
      }

   }

   public TileEntityShulkerBox.AnimationStatus getAnimationStatus() {
      return this.animationStatus;
   }

   public AxisAlignedBB getBoundingBox(IBlockState p_190584_1_) {
      return this.getBoundingBox(p_190584_1_.get(BlockShulkerBox.FACING));
   }

   public AxisAlignedBB getBoundingBox(EnumFacing p_190587_1_) {
      return VoxelShapes.func_197868_b().getBoundingBox().expand((double)(0.5F * this.getProgress(1.0F) * (float)p_190587_1_.getXOffset()), (double)(0.5F * this.getProgress(1.0F) * (float)p_190587_1_.getYOffset()), (double)(0.5F * this.getProgress(1.0F) * (float)p_190587_1_.getZOffset()));
   }

   private AxisAlignedBB getTopBoundingBox(EnumFacing p_190588_1_) {
      EnumFacing enumfacing = p_190588_1_.getOpposite();
      return this.getBoundingBox(p_190588_1_).contract((double)enumfacing.getXOffset(), (double)enumfacing.getYOffset(), (double)enumfacing.getZOffset());
   }

   private void moveCollidedEntities() {
      IBlockState iblockstate = this.world.getBlockState(this.getPos());
      if (iblockstate.getBlock() instanceof BlockShulkerBox) {
         EnumFacing enumfacing = iblockstate.get(BlockShulkerBox.FACING);
         AxisAlignedBB axisalignedbb = this.getTopBoundingBox(enumfacing).offset(this.pos);
         List<Entity> list = this.world.func_72839_b((Entity)null, axisalignedbb);
         if (!list.isEmpty()) {
            for(int i = 0; i < list.size(); ++i) {
               Entity entity = list.get(i);
               if (entity.getPushReaction() != EnumPushReaction.IGNORE) {
                  double d0 = 0.0D;
                  double d1 = 0.0D;
                  double d2 = 0.0D;
                  AxisAlignedBB axisalignedbb1 = entity.getEntityBoundingBox();
                  switch(enumfacing.getAxis()) {
                  case X:
                     if (enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) {
                        d0 = axisalignedbb.maxX - axisalignedbb1.minX;
                     } else {
                        d0 = axisalignedbb1.maxX - axisalignedbb.minX;
                     }

                     d0 = d0 + 0.01D;
                     break;
                  case Y:
                     if (enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) {
                        d1 = axisalignedbb.maxY - axisalignedbb1.minY;
                     } else {
                        d1 = axisalignedbb1.maxY - axisalignedbb.minY;
                     }

                     d1 = d1 + 0.01D;
                     break;
                  case Z:
                     if (enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) {
                        d2 = axisalignedbb.maxZ - axisalignedbb1.minZ;
                     } else {
                        d2 = axisalignedbb1.maxZ - axisalignedbb.minZ;
                     }

                     d2 = d2 + 0.01D;
                  }

                  entity.move(MoverType.SHULKER_BOX, d0 * (double)enumfacing.getXOffset(), d1 * (double)enumfacing.getYOffset(), d2 * (double)enumfacing.getZOffset());
               }
            }

         }
      }
   }

   public int getSizeInventory() {
      return this.items.size();
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_) {
      if (p_145842_1_ == 1) {
         this.openCount = p_145842_2_;
         if (p_145842_2_ == 0) {
            this.animationStatus = TileEntityShulkerBox.AnimationStatus.CLOSING;
         }

         if (p_145842_2_ == 1) {
            this.animationStatus = TileEntityShulkerBox.AnimationStatus.OPENING;
         }

         return true;
      } else {
         return super.receiveClientEvent(p_145842_1_, p_145842_2_);
      }
   }

   public void openInventory(EntityPlayer p_174889_1_) {
      if (!p_174889_1_.isSpectator()) {
         if (this.openCount < 0) {
            this.openCount = 0;
         }

         ++this.openCount;
         this.world.addBlockEvent(this.pos, this.getBlockState().getBlock(), 1, this.openCount);
         if (this.openCount == 1) {
            this.world.playSound((EntityPlayer)null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
         }
      }

   }

   public void closeInventory(EntityPlayer p_174886_1_) {
      if (!p_174886_1_.isSpectator()) {
         --this.openCount;
         this.world.addBlockEvent(this.pos, this.getBlockState().getBlock(), 1, this.openCount);
         if (this.openCount <= 0) {
            this.world.playSound((EntityPlayer)null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
         }
      }

   }

   public Container createContainer(InventoryPlayer p_174876_1_, EntityPlayer p_174876_2_) {
      return new ContainerShulkerBox(p_174876_1_, this, p_174876_2_);
   }

   public String getGuiID() {
      return "minecraft:shulker_box";
   }

   public ITextComponent getName() {
      ITextComponent itextcomponent = this.getCustomName();
      return (ITextComponent)(itextcomponent != null ? itextcomponent : new TextComponentTranslation("container.shulkerBox"));
   }

   public void readFromNBT(NBTTagCompound p_145839_1_) {
      super.readFromNBT(p_145839_1_);
      this.loadFromNbt(p_145839_1_);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
      super.writeToNBT(p_189515_1_);
      return this.saveToNbt(p_189515_1_);
   }

   public void loadFromNbt(NBTTagCompound p_190586_1_) {
      this.items = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (!this.checkLootAndRead(p_190586_1_) && p_190586_1_.hasKey("Items", 9)) {
         ItemStackHelper.loadAllItems(p_190586_1_, this.items);
      }

      if (p_190586_1_.hasKey("CustomName", 8)) {
         this.customName = ITextComponent.Serializer.fromJson(p_190586_1_.getString("CustomName"));
      }

   }

   public NBTTagCompound saveToNbt(NBTTagCompound p_190580_1_) {
      if (!this.checkLootAndWrite(p_190580_1_)) {
         ItemStackHelper.saveAllItems(p_190580_1_, this.items, false);
      }

      ITextComponent itextcomponent = this.getCustomName();
      if (itextcomponent != null) {
         p_190580_1_.setString("CustomName", ITextComponent.Serializer.toJson(itextcomponent));
      }

      if (!p_190580_1_.hasKey("Lock") && this.isLocked()) {
         this.getLockCode().toNBT(p_190580_1_);
      }

      return p_190580_1_;
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> p_199721_1_) {
      this.items = p_199721_1_;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.items) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public int[] getSlotsForFace(EnumFacing p_180463_1_) {
      return SLOTS;
   }

   public boolean canInsertItem(int p_180462_1_, ItemStack p_180462_2_, @Nullable EnumFacing p_180462_3_) {
      return !(Block.getBlockFromItem(p_180462_2_.getItem()) instanceof BlockShulkerBox);
   }

   public boolean canExtractItem(int p_180461_1_, ItemStack p_180461_2_, EnumFacing p_180461_3_) {
      return true;
   }

   public void clear() {
      this.hasBeenCleared = true;
      super.clear();
   }

   public boolean isCleared() {
      return this.hasBeenCleared;
   }

   public float getProgress(float p_190585_1_) {
      return this.progressOld + (this.progress - this.progressOld) * p_190585_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public EnumDyeColor getColor() {
      if (this.needsColorFromWorld) {
         this.color = BlockShulkerBox.getColorFromBlock(this.getBlockState().getBlock());
         this.needsColorFromWorld = false;
      }

      return this.color;
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 10, this.getUpdateTag());
   }

   public boolean isDestroyedByCreativePlayer() {
      return this.destroyedByCreativePlayer;
   }

   public void setDestroyedByCreativePlayer(boolean p_190579_1_) {
      this.destroyedByCreativePlayer = p_190579_1_;
   }

   public boolean shouldDrop() {
      return !this.isDestroyedByCreativePlayer() || !this.isEmpty() || this.hasCustomName() || this.lootTable != null;
   }

   public static enum AnimationStatus {
      CLOSED,
      OPENING,
      OPENED,
      CLOSING;
   }
}
