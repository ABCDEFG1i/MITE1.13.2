package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FurnaceRecipe implements IRecipe {
   private final ResourceLocation id;
   private final String group;
   private final Ingredient input;
   private final ItemStack output;
   private final float experience;
   private final int cookingTime;

   public FurnaceRecipe(ResourceLocation p_i48715_1_, String p_i48715_2_, Ingredient p_i48715_3_, ItemStack p_i48715_4_, float p_i48715_5_, int p_i48715_6_) {
      this.id = p_i48715_1_;
      this.group = p_i48715_2_;
      this.input = p_i48715_3_;
      this.output = p_i48715_4_;
      this.experience = p_i48715_5_;
      this.cookingTime = p_i48715_6_;
   }

   public boolean matches(IInventory p_77569_1_, World p_77569_2_) {
      return p_77569_1_ instanceof TileEntityFurnace && this.input.test(p_77569_1_.getStackInSlot(0));
   }

   public ItemStack getCraftingResult(IInventory p_77572_1_) {
      return this.output.copy();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return true;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.SMELTING;
   }

   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> nonnulllist = NonNullList.create();
      nonnulllist.add(this.input);
      return nonnulllist;
   }

   public float getExperience() {
      return this.experience;
   }

   public ItemStack getRecipeOutput() {
      return this.output;
   }

   @OnlyIn(Dist.CLIENT)
   public String getGroup() {
      return this.group;
   }

   public int getCookingTime() {
      return this.cookingTime;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public static class Serializer implements IRecipeSerializer<FurnaceRecipe> {
      public FurnaceRecipe read(ResourceLocation p_199425_1_, JsonObject p_199425_2_) {
         String s = JsonUtils.getString(p_199425_2_, "group", "");
         Ingredient ingredient;
         if (JsonUtils.isJsonArray(p_199425_2_, "ingredient")) {
            ingredient = Ingredient.fromJson(JsonUtils.getJsonArray(p_199425_2_, "ingredient"));
         } else {
            ingredient = Ingredient.fromJson(JsonUtils.getJsonObject(p_199425_2_, "ingredient"));
         }

         String s1 = JsonUtils.getString(p_199425_2_, "result");
         Item item = IRegistry.field_212630_s.func_212608_b(new ResourceLocation(s1));
         if (item != null) {
            ItemStack itemstack = new ItemStack(item);
            float lvt_8_1_ = JsonUtils.getFloat(p_199425_2_, "experience", 0.0F);
            int lvt_9_1_ = JsonUtils.getInt(p_199425_2_, "cookingtime", 200);
            return new FurnaceRecipe(p_199425_1_, s, ingredient, itemstack, lvt_8_1_, lvt_9_1_);
         } else {
            throw new IllegalStateException(s1 + " did not exist");
         }
      }

      public FurnaceRecipe read(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_) {
         String s = p_199426_2_.readString(32767);
         Ingredient ingredient = Ingredient.fromBuffer(p_199426_2_);
         ItemStack itemstack = p_199426_2_.readItemStack();
         float f = p_199426_2_.readFloat();
         int i = p_199426_2_.readVarInt();
         return new FurnaceRecipe(p_199426_1_, s, ingredient, itemstack, f, i);
      }

      public void write(PacketBuffer p_199427_1_, FurnaceRecipe p_199427_2_) {
         p_199427_1_.writeString(p_199427_2_.group);
         p_199427_2_.input.writeToBuffer(p_199427_1_);
         p_199427_1_.writeItemStack(p_199427_2_.output);
         p_199427_1_.writeFloat(p_199427_2_.experience);
         p_199427_1_.writeVarInt(p_199427_2_.cookingTime);
      }

      public String getId() {
         return "smelting";
      }
   }
}
