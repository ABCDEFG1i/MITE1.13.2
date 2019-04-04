package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipeBook extends RecipeBook {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RecipeManager recipeManager;

   public ServerRecipeBook(RecipeManager p_i48175_1_) {
      this.recipeManager = p_i48175_1_;
   }

   public int add(Collection<IRecipe> p_197926_1_, EntityPlayerMP p_197926_2_) {
      List<ResourceLocation> list = Lists.newArrayList();
      int i = 0;

      for(IRecipe irecipe : p_197926_1_) {
         ResourceLocation resourcelocation = irecipe.getId();
         if (!this.recipes.contains(resourcelocation) && !irecipe.isDynamic()) {
            this.unlock(resourcelocation);
            this.markNew(resourcelocation);
            list.add(resourcelocation);
            CriteriaTriggers.RECIPE_UNLOCKED.trigger(p_197926_2_, irecipe);
            ++i;
         }
      }

      this.sendPacket(SPacketRecipeBook.State.ADD, p_197926_2_, list);
      return i;
   }

   public int remove(Collection<IRecipe> p_197925_1_, EntityPlayerMP p_197925_2_) {
      List<ResourceLocation> list = Lists.newArrayList();
      int i = 0;

      for(IRecipe irecipe : p_197925_1_) {
         ResourceLocation resourcelocation = irecipe.getId();
         if (this.recipes.contains(resourcelocation)) {
            this.lock(resourcelocation);
            list.add(resourcelocation);
            ++i;
         }
      }

      this.sendPacket(SPacketRecipeBook.State.REMOVE, p_197925_2_, list);
      return i;
   }

   private void sendPacket(SPacketRecipeBook.State p_194081_1_, EntityPlayerMP p_194081_2_, List<ResourceLocation> p_194081_3_) {
      p_194081_2_.connection.sendPacket(new SPacketRecipeBook(p_194081_1_, p_194081_3_, Collections.emptyList(), this.isGuiOpen, this.isFilteringCraftable, this.isFurnaceGuiOpen, this.isFurnaceFilteringCraftable));
   }

   public NBTTagCompound write() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setBoolean("isGuiOpen", this.isGuiOpen);
      nbttagcompound.setBoolean("isFilteringCraftable", this.isFilteringCraftable);
      nbttagcompound.setBoolean("isFurnaceGuiOpen", this.isFurnaceGuiOpen);
      nbttagcompound.setBoolean("isFurnaceFilteringCraftable", this.isFurnaceFilteringCraftable);
      NBTTagList nbttaglist = new NBTTagList();

      for(ResourceLocation resourcelocation : this.recipes) {
         nbttaglist.add(new NBTTagString(resourcelocation.toString()));
      }

      nbttagcompound.setTag("recipes", nbttaglist);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(ResourceLocation resourcelocation1 : this.newRecipes) {
         nbttaglist1.add(new NBTTagString(resourcelocation1.toString()));
      }

      nbttagcompound.setTag("toBeDisplayed", nbttaglist1);
      return nbttagcompound;
   }

   public void read(NBTTagCompound p_192825_1_) {
      this.isGuiOpen = p_192825_1_.getBoolean("isGuiOpen");
      this.isFilteringCraftable = p_192825_1_.getBoolean("isFilteringCraftable");
      this.isFurnaceGuiOpen = p_192825_1_.getBoolean("isFurnaceGuiOpen");
      this.isFurnaceFilteringCraftable = p_192825_1_.getBoolean("isFurnaceFilteringCraftable");
      NBTTagList nbttaglist = p_192825_1_.getTagList("recipes", 8);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         ResourceLocation resourcelocation = new ResourceLocation(nbttaglist.getStringTagAt(i));
         IRecipe irecipe = this.recipeManager.getRecipe(resourcelocation);
         if (irecipe == null) {
            LOGGER.error("Tried to load unrecognized recipe: {} removed now.", resourcelocation);
         } else {
            this.unlock(irecipe);
         }
      }

      NBTTagList nbttaglist1 = p_192825_1_.getTagList("toBeDisplayed", 8);

      for(int j = 0; j < nbttaglist1.size(); ++j) {
         ResourceLocation resourcelocation1 = new ResourceLocation(nbttaglist1.getStringTagAt(j));
         IRecipe irecipe1 = this.recipeManager.getRecipe(resourcelocation1);
         if (irecipe1 == null) {
            LOGGER.error("Tried to load unrecognized recipe: {} removed now.", resourcelocation1);
         } else {
            this.markNew(irecipe1);
         }
      }

   }

   public void init(EntityPlayerMP p_192826_1_) {
      p_192826_1_.connection.sendPacket(new SPacketRecipeBook(SPacketRecipeBook.State.INIT, this.recipes, this.newRecipes, this.isGuiOpen, this.isFilteringCraftable, this.isFurnaceGuiOpen, this.isFurnaceFilteringCraftable));
   }
}
