package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityFurnace extends TileEntityLockable implements ISidedInventory, IRecipeHolder, IRecipeHelperPopulator, ITickable {
   private static final int[] SLOTS_TOP = new int[]{0};
   private static final int[] SLOTS_BOTTOM = new int[]{2, 1};
   private static final int[] SLOTS_SIDES = new int[]{1};
   private NonNullList<ItemStack> furnaceItemStacks = NonNullList.withSize(3, ItemStack.EMPTY);
   private int furnaceBurnTime;
   private int currentItemBurnTime;
   private int cookTime;
   private int totalCookTime;
   private ITextComponent furnaceCustomName;
   private final Map<ResourceLocation, Integer> recipeUseCounts = Maps.newHashMap();

   private static void setBurnTime(Map<Item, Integer> p_201563_0_, Tag<Item> p_201563_1_, int p_201563_2_) {
      for(Item item : p_201563_1_.getAllElements()) {
         p_201563_0_.put(item, p_201563_2_);
      }

   }

   private static void setBurnTime(Map<Item, Integer> p_203065_0_, IItemProvider p_203065_1_, int p_203065_2_) {
      p_203065_0_.put(p_203065_1_.asItem(), p_203065_2_);
   }

   public static Map<Item, Integer> getBurnTimes() {
      Map<Item, Integer> map = Maps.newLinkedHashMap();
      setBurnTime(map, Items.LAVA_BUCKET, 20000);
      setBurnTime(map, Blocks.COAL_BLOCK, 16000);
      setBurnTime(map, Items.BLAZE_ROD, 2400);
      setBurnTime(map, Items.COAL, 1600);
      setBurnTime(map, Items.CHARCOAL, 1600);
      setBurnTime(map, ItemTags.LOGS, 300);
      setBurnTime(map, ItemTags.PLANKS, 300);
      setBurnTime(map, ItemTags.WOODEN_STAIRS, 300);
      setBurnTime(map, ItemTags.WOODEN_SLABS, 150);
      setBurnTime(map, ItemTags.WOODEN_TRAPDOORS, 300);
      setBurnTime(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
      setBurnTime(map, Blocks.OAK_FENCE, 300);
      setBurnTime(map, Blocks.BIRCH_FENCE, 300);
      setBurnTime(map, Blocks.SPRUCE_FENCE, 300);
      setBurnTime(map, Blocks.JUNGLE_FENCE, 300);
      setBurnTime(map, Blocks.DARK_OAK_FENCE, 300);
      setBurnTime(map, Blocks.ACACIA_FENCE, 300);
      setBurnTime(map, Blocks.OAK_FENCE_GATE, 300);
      setBurnTime(map, Blocks.BIRCH_FENCE_GATE, 300);
      setBurnTime(map, Blocks.SPRUCE_FENCE_GATE, 300);
      setBurnTime(map, Blocks.JUNGLE_FENCE_GATE, 300);
      setBurnTime(map, Blocks.DARK_OAK_FENCE_GATE, 300);
      setBurnTime(map, Blocks.ACACIA_FENCE_GATE, 300);
      setBurnTime(map, Blocks.NOTE_BLOCK, 300);
      setBurnTime(map, Blocks.BOOKSHELF, 300);
      setBurnTime(map, Blocks.JUKEBOX, 300);
      setBurnTime(map, Blocks.CHEST, 300);
      setBurnTime(map, Blocks.TRAPPED_CHEST, 300);
      setBurnTime(map, Blocks.CRAFTING_TABLE, 300);
      setBurnTime(map, Blocks.DAYLIGHT_DETECTOR, 300);
      setBurnTime(map, ItemTags.BANNERS, 300);
      setBurnTime(map, Items.BOW, 300);
      setBurnTime(map, Items.FISHING_ROD, 300);
      setBurnTime(map, Blocks.LADDER, 300);
      setBurnTime(map, Items.SIGN, 200);
      setBurnTime(map, Items.WOODEN_SHOVEL, 200);
      setBurnTime(map, Items.WOODEN_SWORD, 200);
      setBurnTime(map, ItemTags.WOODEN_DOORS, 200);
      setBurnTime(map, ItemTags.BOATS, 200);
      setBurnTime(map, ItemTags.WOOL, 100);
      setBurnTime(map, ItemTags.WOODEN_BUTTONS, 100);
      setBurnTime(map, Items.STICK, 100);
      setBurnTime(map, ItemTags.SAPLINGS, 100);
      setBurnTime(map, Items.BOWL, 100);
      setBurnTime(map, ItemTags.CARPETS, 67);
      setBurnTime(map, Blocks.DRIED_KELP_BLOCK, 4001);
      return map;
   }

   public TileEntityFurnace() {
      super(TileEntityType.FURNACE);
   }

   public int getSizeInventory() {
      return this.furnaceItemStacks.size();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.furnaceItemStacks) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      return this.furnaceItemStacks.get(p_70301_1_);
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      return ItemStackHelper.getAndSplit(this.furnaceItemStacks, p_70298_1_, p_70298_2_);
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      return ItemStackHelper.getAndRemove(this.furnaceItemStacks, p_70304_1_);
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      ItemStack itemstack = this.furnaceItemStacks.get(p_70299_1_);
      boolean flag = !p_70299_2_.isEmpty() && p_70299_2_.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(p_70299_2_, itemstack);
      this.furnaceItemStacks.set(p_70299_1_, p_70299_2_);
      if (p_70299_2_.getCount() > this.getInventoryStackLimit()) {
         p_70299_2_.setCount(this.getInventoryStackLimit());
      }

      if (p_70299_1_ == 0 && !flag) {
         this.totalCookTime = this.getCookTime();
         this.cookTime = 0;
         this.markDirty();
      }

   }

   public ITextComponent getName() {
      return this.furnaceCustomName != null ? this.furnaceCustomName : new TextComponentTranslation("container.furnace");
   }

   public boolean hasCustomName() {
      return this.furnaceCustomName != null;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.furnaceCustomName;
   }

   public void setCustomName(@Nullable ITextComponent p_200225_1_) {
      this.furnaceCustomName = p_200225_1_;
   }

   public void readFromNBT(NBTTagCompound p_145839_1_) {
      super.readFromNBT(p_145839_1_);
      this.furnaceItemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(p_145839_1_, this.furnaceItemStacks);
      this.furnaceBurnTime = p_145839_1_.getShort("BurnTime");
      this.cookTime = p_145839_1_.getShort("CookTime");
      this.totalCookTime = p_145839_1_.getShort("CookTimeTotal");
      this.currentItemBurnTime = getItemBurnTime(this.furnaceItemStacks.get(1));
      int i = p_145839_1_.getShort("RecipesUsedSize");

      for(int j = 0; j < i; ++j) {
         ResourceLocation resourcelocation = new ResourceLocation(p_145839_1_.getString("RecipeLocation" + j));
         int k = p_145839_1_.getInteger("RecipeAmount" + j);
         this.recipeUseCounts.put(resourcelocation, k);
      }

      if (p_145839_1_.hasKey("CustomName", 8)) {
         this.furnaceCustomName = ITextComponent.Serializer.fromJson(p_145839_1_.getString("CustomName"));
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
      super.writeToNBT(p_189515_1_);
      p_189515_1_.setShort("BurnTime", (short)this.furnaceBurnTime);
      p_189515_1_.setShort("CookTime", (short)this.cookTime);
      p_189515_1_.setShort("CookTimeTotal", (short)this.totalCookTime);
      ItemStackHelper.saveAllItems(p_189515_1_, this.furnaceItemStacks);
      p_189515_1_.setShort("RecipesUsedSize", (short)this.recipeUseCounts.size());
      int i = 0;

      for(Entry<ResourceLocation, Integer> entry : this.recipeUseCounts.entrySet()) {
         p_189515_1_.setString("RecipeLocation" + i, entry.getKey().toString());
         p_189515_1_.setInteger("RecipeAmount" + i, entry.getValue());
         ++i;
      }

      if (this.furnaceCustomName != null) {
         p_189515_1_.setString("CustomName", ITextComponent.Serializer.toJson(this.furnaceCustomName));
      }

      return p_189515_1_;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   private boolean isBurning() {
      return this.furnaceBurnTime > 0;
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean isBurning(IInventory p_174903_0_) {
      return p_174903_0_.getField(0) > 0;
   }

   public void tick() {
      boolean flag = this.isBurning();
      boolean flag1 = false;
      if (this.isBurning()) {
         --this.furnaceBurnTime;
      }

      if (!this.world.isRemote) {
         ItemStack itemstack = this.furnaceItemStacks.get(1);
         if (this.isBurning() || !itemstack.isEmpty() && !this.furnaceItemStacks.get(0).isEmpty()) {
            IRecipe irecipe = this.world.getRecipeManager().getRecipe(this, this.world);
            if (!this.isBurning() && this.canSmelt(irecipe)) {
               this.furnaceBurnTime = getItemBurnTime(itemstack);
               this.currentItemBurnTime = this.furnaceBurnTime;
               if (this.isBurning()) {
                  flag1 = true;
                  if (!itemstack.isEmpty()) {
                     Item item = itemstack.getItem();
                     itemstack.shrink(1);
                     if (itemstack.isEmpty()) {
                        Item item1 = item.getContainerItem();
                        this.furnaceItemStacks.set(1, item1 == null ? ItemStack.EMPTY : new ItemStack(item1));
                     }
                  }
               }
            }

            if (this.isBurning() && this.canSmelt(irecipe)) {
               ++this.cookTime;
               if (this.cookTime == this.totalCookTime) {
                  this.cookTime = 0;
                  this.totalCookTime = this.getCookTime();
                  this.smeltItem(irecipe);
                  flag1 = true;
               }
            } else {
               this.cookTime = 0;
            }
         } else if (!this.isBurning() && this.cookTime > 0) {
            this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.totalCookTime);
         }

         if (flag != this.isBurning()) {
            flag1 = true;
            this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(BlockFurnace.LIT, Boolean.valueOf(this.isBurning())), 3);
         }
      }

      if (flag1) {
         this.markDirty();
      }

   }

   private int getCookTime() {
      FurnaceRecipe furnacerecipe = (FurnaceRecipe)this.world.getRecipeManager().getRecipe(this, this.world);
      return furnacerecipe != null ? furnacerecipe.getCookingTime() : 200;
   }

   private boolean canSmelt(@Nullable IRecipe p_201566_1_) {
      if (!this.furnaceItemStacks.get(0).isEmpty() && p_201566_1_ != null) {
         ItemStack itemstack = p_201566_1_.getRecipeOutput();
         if (itemstack.isEmpty()) {
            return false;
         } else {
            ItemStack itemstack1 = this.furnaceItemStacks.get(2);
            if (itemstack1.isEmpty()) {
               return true;
            } else if (!itemstack1.isItemEqual(itemstack)) {
               return false;
            } else if (itemstack1.getCount() < this.getInventoryStackLimit() && itemstack1.getCount() < itemstack1.getMaxStackSize()) {
               return true;
            } else {
               return itemstack1.getCount() < itemstack.getMaxStackSize();
            }
         }
      } else {
         return false;
      }
   }

   private void smeltItem(@Nullable IRecipe p_201565_1_) {
      if (p_201565_1_ != null && this.canSmelt(p_201565_1_)) {
         ItemStack itemstack = this.furnaceItemStacks.get(0);
         ItemStack itemstack1 = p_201565_1_.getRecipeOutput();
         ItemStack itemstack2 = this.furnaceItemStacks.get(2);
         if (itemstack2.isEmpty()) {
            this.furnaceItemStacks.set(2, itemstack1.copy());
         } else if (itemstack2.getItem() == itemstack1.getItem()) {
            itemstack2.grow(1);
         }

         if (!this.world.isRemote) {
            this.canUseRecipe(this.world, null, p_201565_1_);
         }

         if (itemstack.getItem() == Blocks.WET_SPONGE.asItem() && !this.furnaceItemStacks.get(1).isEmpty() && this.furnaceItemStacks.get(1).getItem() == Items.BUCKET) {
            this.furnaceItemStacks.set(1, new ItemStack(Items.WATER_BUCKET));
         }

         itemstack.shrink(1);
      }
   }

   private static int getItemBurnTime(ItemStack p_145952_0_) {
      if (p_145952_0_.isEmpty()) {
         return 0;
      } else {
         Item item = p_145952_0_.getItem();
         return getBurnTimes().getOrDefault(item, 0);
      }
   }

   public static boolean isItemFuel(ItemStack p_145954_0_) {
      return getBurnTimes().containsKey(p_145954_0_.getItem());
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
      if (p_94041_1_ == 2) {
         return false;
      } else if (p_94041_1_ != 1) {
         return true;
      } else {
         ItemStack itemstack = this.furnaceItemStacks.get(1);
         return isItemFuel(p_94041_2_) || SlotFurnaceFuel.isBucket(p_94041_2_) && itemstack.getItem() != Items.BUCKET;
      }
   }

   public int[] getSlotsForFace(EnumFacing p_180463_1_) {
      if (p_180463_1_ == EnumFacing.DOWN) {
         return SLOTS_BOTTOM;
      } else {
         return p_180463_1_ == EnumFacing.UP ? SLOTS_TOP : SLOTS_SIDES;
      }
   }

   public boolean canInsertItem(int p_180462_1_, ItemStack p_180462_2_, @Nullable EnumFacing p_180462_3_) {
      return this.isItemValidForSlot(p_180462_1_, p_180462_2_);
   }

   public boolean canExtractItem(int p_180461_1_, ItemStack p_180461_2_, EnumFacing p_180461_3_) {
      if (p_180461_3_ == EnumFacing.DOWN && p_180461_1_ == 1) {
         Item item = p_180461_2_.getItem();
          return item == Items.WATER_BUCKET || item == Items.BUCKET;
      }

      return true;
   }

   public String getGuiID() {
      return "minecraft:furnace";
   }

   public Container createContainer(InventoryPlayer p_174876_1_, EntityPlayer p_174876_2_) {
      return new ContainerFurnace(p_174876_1_, this);
   }

   public int getField(int p_174887_1_) {
      switch(p_174887_1_) {
      case 0:
         return this.furnaceBurnTime;
      case 1:
         return this.currentItemBurnTime;
      case 2:
         return this.cookTime;
      case 3:
         return this.totalCookTime;
      default:
         return 0;
      }
   }

   public void setField(int p_174885_1_, int p_174885_2_) {
      switch(p_174885_1_) {
      case 0:
         this.furnaceBurnTime = p_174885_2_;
         break;
      case 1:
         this.currentItemBurnTime = p_174885_2_;
         break;
      case 2:
         this.cookTime = p_174885_2_;
         break;
      case 3:
         this.totalCookTime = p_174885_2_;
      }

   }

   public int getFieldCount() {
      return 4;
   }

   public void clear() {
      this.furnaceItemStacks.clear();
   }

   public void fillStackedContents(RecipeItemHelper p_194018_1_) {
      for(ItemStack itemstack : this.furnaceItemStacks) {
         p_194018_1_.accountStack(itemstack);
      }

   }

   public void setRecipeUsed(IRecipe p_193056_1_) {
      if (this.recipeUseCounts.containsKey(p_193056_1_.getId())) {
         this.recipeUseCounts.put(p_193056_1_.getId(), this.recipeUseCounts.get(p_193056_1_.getId()) + 1);
      } else {
         this.recipeUseCounts.put(p_193056_1_.getId(), 1);
      }

   }

   @Nullable
   public IRecipe getRecipeUsed() {
      return null;
   }

   public Map<ResourceLocation, Integer> getRecipeUseCounts() {
      return this.recipeUseCounts;
   }

   public boolean canUseRecipe(World p_201561_1_, EntityPlayerMP p_201561_2_, @Nullable IRecipe p_201561_3_) {
      if (p_201561_3_ != null) {
         this.setRecipeUsed(p_201561_3_);
         return true;
      } else {
         return false;
      }
   }

   public void onCrafting(EntityPlayer p_201560_1_) {
      if (!this.world.getGameRules().getBoolean("doLimitedCrafting")) {
         List<IRecipe> list = Lists.newArrayList();

         for(ResourceLocation resourcelocation : this.recipeUseCounts.keySet()) {
            IRecipe irecipe = p_201560_1_.world.getRecipeManager().getRecipe(resourcelocation);
            if (irecipe != null) {
               list.add(irecipe);
            }
         }

         p_201560_1_.unlockRecipes(list);
      }

      this.recipeUseCounts.clear();
   }
}
