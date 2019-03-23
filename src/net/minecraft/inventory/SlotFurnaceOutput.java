package net.minecraft.inventory;

import java.util.Map.Entry;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class SlotFurnaceOutput extends Slot {
   private final EntityPlayer player;
   private int removeCount;

   public SlotFurnaceOutput(EntityPlayer p_i45793_1_, IInventory p_i45793_2_, int p_i45793_3_, int p_i45793_4_, int p_i45793_5_) {
      super(p_i45793_2_, p_i45793_3_, p_i45793_4_, p_i45793_5_);
      this.player = p_i45793_1_;
   }

   public boolean isItemValid(ItemStack other) {
      return false;
   }

   public ItemStack decrStackSize(int p_75209_1_) {
      if (this.getHasStack()) {
         this.removeCount += Math.min(p_75209_1_, this.getStack().getCount());
      }

      return super.decrStackSize(p_75209_1_);
   }

   public ItemStack onTake(EntityPlayer p_190901_1_, ItemStack p_190901_2_) {
      this.onCrafting(p_190901_2_);
      super.onTake(p_190901_1_, p_190901_2_);
      return p_190901_2_;
   }

   protected void onCrafting(ItemStack p_75210_1_, int p_75210_2_) {
      this.removeCount += p_75210_2_;
      this.onCrafting(p_75210_1_);
   }

   protected void onCrafting(ItemStack p_75208_1_) {
      p_75208_1_.onCrafting(this.player.world, this.player, this.removeCount);
      if (!this.player.world.isRemote) {
         for(Entry<ResourceLocation, Integer> entry : ((TileEntityFurnace)this.inventory).getRecipeUseCounts().entrySet()) {
            FurnaceRecipe furnacerecipe = (FurnaceRecipe)this.player.world.getRecipeManager().getRecipe(entry.getKey());
            float f;
            if (furnacerecipe != null) {
               f = furnacerecipe.getExperience();
            } else {
               f = 0.0F;
            }

            int i = entry.getValue();
            if (f == 0.0F) {
               i = 0;
            } else if (f < 1.0F) {
               int j = MathHelper.floor((float)i * f);
               if (j < MathHelper.ceil((float)i * f) && Math.random() < (double)((float)i * f - (float)j)) {
                  ++j;
               }

               i = j;
            }

            while(i > 0) {
               int k = EntityXPOrb.getXPSplit(i);
               i -= k;
               this.player.world.spawnEntity(new EntityXPOrb(this.player.world, this.player.posX, this.player.posY + 0.5D, this.player.posZ + 0.5D, k));
            }
         }

         ((IRecipeHolder)this.inventory).onCrafting(this.player);
      }

      this.removeCount = 0;
   }
}
