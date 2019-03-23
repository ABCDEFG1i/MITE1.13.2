package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketAdvancementInfo implements Packet<INetHandlerPlayClient> {
   private boolean firstSync;
   private Map<ResourceLocation, Advancement.Builder> advancementsToAdd;
   private Set<ResourceLocation> advancementsToRemove;
   private Map<ResourceLocation, AdvancementProgress> progressUpdates;

   public SPacketAdvancementInfo() {
   }

   public SPacketAdvancementInfo(boolean p_i47519_1_, Collection<Advancement> p_i47519_2_, Set<ResourceLocation> p_i47519_3_, Map<ResourceLocation, AdvancementProgress> p_i47519_4_) {
      this.firstSync = p_i47519_1_;
      this.advancementsToAdd = Maps.newHashMap();

      for(Advancement advancement : p_i47519_2_) {
         this.advancementsToAdd.put(advancement.getId(), advancement.copy());
      }

      this.advancementsToRemove = p_i47519_3_;
      this.progressUpdates = Maps.newHashMap(p_i47519_4_);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleAdvancementInfo(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.firstSync = p_148837_1_.readBoolean();
      this.advancementsToAdd = Maps.newHashMap();
      this.advancementsToRemove = Sets.newLinkedHashSet();
      this.progressUpdates = Maps.newHashMap();
      int i = p_148837_1_.readVarInt();

      for(int j = 0; j < i; ++j) {
         ResourceLocation resourcelocation = p_148837_1_.readResourceLocation();
         Advancement.Builder advancement$builder = Advancement.Builder.readFrom(p_148837_1_);
         this.advancementsToAdd.put(resourcelocation, advancement$builder);
      }

      i = p_148837_1_.readVarInt();

      for(int k = 0; k < i; ++k) {
         ResourceLocation resourcelocation1 = p_148837_1_.readResourceLocation();
         this.advancementsToRemove.add(resourcelocation1);
      }

      i = p_148837_1_.readVarInt();

      for(int l = 0; l < i; ++l) {
         ResourceLocation resourcelocation2 = p_148837_1_.readResourceLocation();
         this.progressUpdates.put(resourcelocation2, AdvancementProgress.fromNetwork(p_148837_1_));
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBoolean(this.firstSync);
      p_148840_1_.writeVarInt(this.advancementsToAdd.size());

      for(Entry<ResourceLocation, Advancement.Builder> entry : this.advancementsToAdd.entrySet()) {
         ResourceLocation resourcelocation = entry.getKey();
         Advancement.Builder advancement$builder = entry.getValue();
         p_148840_1_.writeResourceLocation(resourcelocation);
         advancement$builder.writeTo(p_148840_1_);
      }

      p_148840_1_.writeVarInt(this.advancementsToRemove.size());

      for(ResourceLocation resourcelocation1 : this.advancementsToRemove) {
         p_148840_1_.writeResourceLocation(resourcelocation1);
      }

      p_148840_1_.writeVarInt(this.progressUpdates.size());

      for(Entry<ResourceLocation, AdvancementProgress> entry1 : this.progressUpdates.entrySet()) {
         p_148840_1_.writeResourceLocation(entry1.getKey());
         entry1.getValue().serializeToNetwork(p_148840_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public Map<ResourceLocation, Advancement.Builder> getAdvancementsToAdd() {
      return this.advancementsToAdd;
   }

   @OnlyIn(Dist.CLIENT)
   public Set<ResourceLocation> getAdvancementsToRemove() {
      return this.advancementsToRemove;
   }

   @OnlyIn(Dist.CLIENT)
   public Map<ResourceLocation, AdvancementProgress> getProgressUpdates() {
      return this.progressUpdates;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isFirstSync() {
      return this.firstSync;
   }
}
