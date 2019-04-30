package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.FunctionObject;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.loot.LootContext;

public class AdvancementRewards {
   public static final AdvancementRewards EMPTY = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], FunctionObject.CacheableFunction.EMPTY);
   private final int experience;
   private final ResourceLocation[] loot;
   private final ResourceLocation[] recipes;
   private final FunctionObject.CacheableFunction function;

   public AdvancementRewards(int experience, ResourceLocation[] loot, ResourceLocation[] recipes, FunctionObject.CacheableFunction function) {
      this.experience = experience;
      this.loot = loot;
      this.recipes = recipes;
      this.function = function;
   }

   public void apply(EntityPlayerMP p_192113_1_) {
      p_192113_1_.addXpValue(this.experience);
      LootContext lootcontext = (new LootContext.Builder(p_192113_1_.getServerWorld())).withLootedEntity(p_192113_1_).withPosition(new BlockPos(p_192113_1_)).build();
      boolean flag = false;

      for(ResourceLocation resourcelocation : this.loot) {
         for(ItemStack itemstack : p_192113_1_.server.getLootTableManager().getLootTableFromLocation(resourcelocation).generateLootForPools(p_192113_1_.getRNG(), lootcontext)) {
            if (p_192113_1_.addItemStackToInventory(itemstack)) {
               p_192113_1_.world.playSound(null, p_192113_1_.posX, p_192113_1_.posY, p_192113_1_.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((p_192113_1_.getRNG().nextFloat() - p_192113_1_.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
               flag = true;
            } else {
               EntityItem entityitem = p_192113_1_.dropItem(itemstack, false);
               if (entityitem != null) {
                  entityitem.setNoPickupDelay();
                  entityitem.setOwnerId(p_192113_1_.getUniqueID());
               }
            }
         }
      }

      if (flag) {
         p_192113_1_.inventoryContainer.detectAndSendChanges();
      }

      if (this.recipes.length > 0) {
         p_192113_1_.unlockRecipes(this.recipes);
      }

      MinecraftServer minecraftserver = p_192113_1_.server;
      FunctionObject functionobject = this.function.get(minecraftserver.getFunctionManager());
      if (functionobject != null) {
         minecraftserver.getFunctionManager().execute(functionobject, p_192113_1_.getCommandSource().withFeedbackDisabled().withPermissionLevel(2));
      }

   }

   public String toString() {
      return "AdvancementRewards{experience=" + this.experience + ", loot=" + Arrays.toString(this.loot) + ", recipes=" + Arrays.toString(
              this.recipes) + ", function=" + this.function + '}';
   }

   public JsonElement serialize() {
      if (this == EMPTY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.experience != 0) {
            jsonobject.addProperty("experience", this.experience);
         }

         if (this.loot.length > 0) {
            JsonArray jsonarray = new JsonArray();

            for(ResourceLocation resourcelocation : this.loot) {
               jsonarray.add(resourcelocation.toString());
            }

            jsonobject.add("loot", jsonarray);
         }

         if (this.recipes.length > 0) {
            JsonArray jsonarray1 = new JsonArray();

            for(ResourceLocation resourcelocation1 : this.recipes) {
               jsonarray1.add(resourcelocation1.toString());
            }

            jsonobject.add("recipes", jsonarray1);
         }

         if (this.function.getId() != null) {
            jsonobject.addProperty("function", this.function.getId().toString());
         }

         return jsonobject;
      }
   }

   public static class Builder {
      private int experience;
      private final List<ResourceLocation> loot = Lists.newArrayList();
      private final List<ResourceLocation> recipes = Lists.newArrayList();
      @Nullable
      private ResourceLocation function;

      public static AdvancementRewards.Builder experience(int p_203907_0_) {
         return (new AdvancementRewards.Builder()).addExperience(p_203907_0_);
      }

      public AdvancementRewards.Builder addExperience(int p_203906_1_) {
         this.experience += p_203906_1_;
         return this;
      }

      public static AdvancementRewards.Builder recipe(ResourceLocation p_200280_0_) {
         return (new AdvancementRewards.Builder()).addRecipe(p_200280_0_);
      }

      public AdvancementRewards.Builder addRecipe(ResourceLocation p_200279_1_) {
         this.recipes.add(p_200279_1_);
         return this;
      }

      public AdvancementRewards build() {
         return new AdvancementRewards(this.experience, this.loot.toArray(new ResourceLocation[0]), this.recipes.toArray(new ResourceLocation[0]), this.function == null ? FunctionObject.CacheableFunction.EMPTY : new FunctionObject.CacheableFunction(this.function));
      }
   }

   public static class Deserializer implements JsonDeserializer<AdvancementRewards> {
      public AdvancementRewards deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "rewards");
         int i = JsonUtils.getInt(jsonobject, "experience", 0);
         JsonArray jsonarray = JsonUtils.getJsonArray(jsonobject, "loot", new JsonArray());
         ResourceLocation[] aresourcelocation = new ResourceLocation[jsonarray.size()];

         for(int j = 0; j < aresourcelocation.length; ++j) {
            aresourcelocation[j] = new ResourceLocation(JsonUtils.getString(jsonarray.get(j), "loot[" + j + "]"));
         }

         JsonArray jsonarray1 = JsonUtils.getJsonArray(jsonobject, "recipes", new JsonArray());
         ResourceLocation[] aresourcelocation1 = new ResourceLocation[jsonarray1.size()];

         for(int k = 0; k < aresourcelocation1.length; ++k) {
            aresourcelocation1[k] = new ResourceLocation(JsonUtils.getString(jsonarray1.get(k), "recipes[" + k + "]"));
         }

         FunctionObject.CacheableFunction functionobject$cacheablefunction;
         if (jsonobject.has("function")) {
            functionobject$cacheablefunction = new FunctionObject.CacheableFunction(new ResourceLocation(JsonUtils.getString(jsonobject, "function")));
         } else {
            functionobject$cacheablefunction = FunctionObject.CacheableFunction.EMPTY;
         }

         return new AdvancementRewards(i, aresourcelocation, aresourcelocation1, functionobject$cacheablefunction);
      }
   }
}
