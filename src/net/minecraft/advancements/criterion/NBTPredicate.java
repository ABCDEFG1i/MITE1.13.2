package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.JsonUtils;

public class NBTPredicate {
   public static final NBTPredicate ANY = new NBTPredicate((NBTTagCompound)null);
   @Nullable
   private final NBTTagCompound tag;

   public NBTPredicate(@Nullable NBTTagCompound p_i47536_1_) {
      this.tag = p_i47536_1_;
   }

   public boolean test(ItemStack p_193478_1_) {
      return this == ANY ? true : this.test(p_193478_1_.getTag());
   }

   public boolean test(Entity p_193475_1_) {
      return this == ANY ? true : this.test(writeToNBTWithSelectedItem(p_193475_1_));
   }

   public boolean test(@Nullable INBTBase p_193477_1_) {
      if (p_193477_1_ == null) {
         return this == ANY;
      } else {
         return this.tag == null || NBTUtil.areNBTEquals(this.tag, p_193477_1_, true);
      }
   }

   public JsonElement serialize() {
      return (JsonElement)(this != ANY && this.tag != null ? new JsonPrimitive(this.tag.toString()) : JsonNull.INSTANCE);
   }

   public static NBTPredicate deserialize(@Nullable JsonElement p_193476_0_) {
      if (p_193476_0_ != null && !p_193476_0_.isJsonNull()) {
         NBTTagCompound nbttagcompound;
         try {
            nbttagcompound = JsonToNBT.getTagFromJson(JsonUtils.getString(p_193476_0_, "nbt"));
         } catch (CommandSyntaxException commandsyntaxexception) {
            throw new JsonSyntaxException("Invalid nbt tag: " + commandsyntaxexception.getMessage());
         }

         return new NBTPredicate(nbttagcompound);
      } else {
         return ANY;
      }
   }

   public static NBTTagCompound writeToNBTWithSelectedItem(Entity p_196981_0_) {
      NBTTagCompound nbttagcompound = p_196981_0_.writeToNBT(new NBTTagCompound());
      if (p_196981_0_ instanceof EntityPlayer) {
         ItemStack itemstack = ((EntityPlayer)p_196981_0_).inventory.getCurrentItem();
         if (!itemstack.isEmpty()) {
            nbttagcompound.setTag("SelectedItem", itemstack.write(new NBTTagCompound()));
         }
      }

      return nbttagcompound;
   }
}
