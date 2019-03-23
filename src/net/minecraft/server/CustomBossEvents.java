package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CustomBossEvents {
   private final MinecraftServer server;
   private final Map<ResourceLocation, CustomBossEvent> bars = Maps.newHashMap();

   public CustomBossEvents(MinecraftServer p_i48619_1_) {
      this.server = p_i48619_1_;
   }

   @Nullable
   public CustomBossEvent get(ResourceLocation p_201384_1_) {
      return this.bars.get(p_201384_1_);
   }

   public CustomBossEvent add(ResourceLocation p_201379_1_, ITextComponent p_201379_2_) {
      CustomBossEvent custombossevent = new CustomBossEvent(p_201379_1_, p_201379_2_);
      this.bars.put(p_201379_1_, custombossevent);
      return custombossevent;
   }

   public void remove(CustomBossEvent p_201385_1_) {
      this.bars.remove(p_201385_1_.getId());
   }

   public Collection<ResourceLocation> getIDs() {
      return this.bars.keySet();
   }

   public Collection<CustomBossEvent> getBossbars() {
      return this.bars.values();
   }

   public NBTTagCompound write() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();

      for(CustomBossEvent custombossevent : this.bars.values()) {
         nbttagcompound.setTag(custombossevent.getId().toString(), custombossevent.write());
      }

      return nbttagcompound;
   }

   public void read(NBTTagCompound p_201381_1_) {
      for(String s : p_201381_1_.getKeySet()) {
         ResourceLocation resourcelocation = new ResourceLocation(s);
         this.bars.put(resourcelocation, CustomBossEvent.read(p_201381_1_.getCompoundTag(s), resourcelocation));
      }

   }

   public void onPlayerLogin(EntityPlayerMP p_201383_1_) {
      for(CustomBossEvent custombossevent : this.bars.values()) {
         custombossevent.onPlayerLogin(p_201383_1_);
      }

   }

   public void onPlayerLogout(EntityPlayerMP p_201382_1_) {
      for(CustomBossEvent custombossevent : this.bars.values()) {
         custombossevent.onPlayerLogout(p_201382_1_);
      }

   }
}
