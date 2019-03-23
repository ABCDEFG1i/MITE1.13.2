package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.command.arguments.ItemParser;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemParticleData implements IParticleData {
   public static final IParticleData.IDeserializer<ItemParticleData> DESERIALIZER = new IParticleData.IDeserializer<ItemParticleData>() {
      public ItemParticleData deserialize(ParticleType<ItemParticleData> p_197544_1_, StringReader p_197544_2_) throws CommandSyntaxException {
         p_197544_2_.expect(' ');
         ItemParser itemparser = (new ItemParser(p_197544_2_, false)).parse();
         ItemStack itemstack = (new ItemInput(itemparser.getItem(), itemparser.getNbt())).createStack(1, false);
         return new ItemParticleData(p_197544_1_, itemstack);
      }

      public ItemParticleData read(ParticleType<ItemParticleData> p_197543_1_, PacketBuffer p_197543_2_) {
         return new ItemParticleData(p_197543_1_, p_197543_2_.readItemStack());
      }
   };
   private final ParticleType<ItemParticleData> particleType;
   private final ItemStack itemStack;

   public ItemParticleData(ParticleType<ItemParticleData> p_i47952_1_, ItemStack p_i47952_2_) {
      this.particleType = p_i47952_1_;
      this.itemStack = p_i47952_2_;
   }

   public void write(PacketBuffer p_197553_1_) {
      p_197553_1_.writeItemStack(this.itemStack);
   }

   public String getParameters() {
      return this.getType().getId() + " " + (new ItemInput(this.itemStack.getItem(), this.itemStack.getTag())).func_197321_c();
   }

   public ParticleType<ItemParticleData> getType() {
      return this.particleType;
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItemStack() {
      return this.itemStack;
   }
}
