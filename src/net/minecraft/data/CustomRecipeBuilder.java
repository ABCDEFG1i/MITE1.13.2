package net.minecraft.data;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.util.ResourceLocation;

public class CustomRecipeBuilder {
   private final RecipeSerializers.SimpleSerializer<?> serializer;

   public CustomRecipeBuilder(RecipeSerializers.SimpleSerializer<?> p_i48259_1_) {
      this.serializer = p_i48259_1_;
   }

   public static CustomRecipeBuilder customRecipe(RecipeSerializers.SimpleSerializer<?> p_200500_0_) {
      return new CustomRecipeBuilder(p_200500_0_);
   }

   public void build(Consumer<IFinishedRecipe> p_200499_1_, final String p_200499_2_) {
      p_200499_1_.accept(new IFinishedRecipe() {
         public JsonObject getRecipeJson() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("type", CustomRecipeBuilder.this.serializer.getId());
            return jsonobject;
         }

         public ResourceLocation getID() {
            return new ResourceLocation(p_200499_2_);
         }

         @Nullable
         public JsonObject getAdvancementJson() {
            return null;
         }

         @Nullable
         public ResourceLocation getAdvancementID() {
            return new ResourceLocation("");
         }
      });
   }
}
