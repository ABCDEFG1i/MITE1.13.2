package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface IRecipeSerializer<T extends IRecipe> {
   T read(ResourceLocation p_199425_1_, JsonObject p_199425_2_);

   T read(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_);

   void write(PacketBuffer p_199427_1_, T p_199427_2_);

   String getId();
}
