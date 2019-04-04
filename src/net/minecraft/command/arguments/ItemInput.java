package net.minecraft.command.arguments;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;

public class ItemInput implements Predicate<ItemStack> {
   private static final Dynamic2CommandExceptionType field_197322_a = new Dynamic2CommandExceptionType((p_208695_0_, p_208695_1_) -> {
      return new TextComponentTranslation("arguments.item.overstacked", p_208695_0_, p_208695_1_);
   });
   private final Item item;
   @Nullable
   private final NBTTagCompound tag;

   public ItemInput(Item p_i47961_1_, @Nullable NBTTagCompound p_i47961_2_) {
      this.item = p_i47961_1_;
      this.tag = p_i47961_2_;
   }

   public Item getItem() {
      return this.item;
   }

   public boolean test(ItemStack p_test_1_) {
      return p_test_1_.getItem() == this.item && NBTUtil.areNBTEquals(this.tag, p_test_1_.getTag(), true);
   }

   public ItemStack createStack(int p_197320_1_, boolean p_197320_2_) throws CommandSyntaxException {
      ItemStack itemstack = new ItemStack(this.item, p_197320_1_);
      if (this.tag != null) {
         itemstack.setTag(this.tag);
      }

      if (p_197320_2_ && p_197320_1_ > itemstack.getMaxStackSize()) {
         throw field_197322_a.create(IRegistry.field_212630_s.func_177774_c(this.item), itemstack.getMaxStackSize());
      } else {
         return itemstack;
      }
   }

   public String func_197321_c() {
      StringBuilder stringbuilder = new StringBuilder(IRegistry.field_212630_s.func_148757_b(this.item));
      if (this.tag != null) {
         stringbuilder.append(this.tag);
      }

      return stringbuilder.toString();
   }
}
