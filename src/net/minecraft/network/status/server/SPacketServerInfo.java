package net.minecraft.network.status.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketServerInfo implements Packet<INetHandlerStatusClient> {
   public static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(ServerStatusResponse.Version.class, new ServerStatusResponse.Version.Serializer()).registerTypeAdapter(ServerStatusResponse.Players.class, new ServerStatusResponse.Players.Serializer()).registerTypeAdapter(ServerStatusResponse.class, new ServerStatusResponse.Serializer()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new EnumTypeAdapterFactory()).create();
   private ServerStatusResponse response;

   public SPacketServerInfo() {
   }

   public SPacketServerInfo(ServerStatusResponse p_i46848_1_) {
      this.response = p_i46848_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.response = JsonUtils.fromJson(GSON, p_148837_1_.readString(32767), ServerStatusResponse.class);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeString(GSON.toJson(this.response));
   }

   public void processPacket(INetHandlerStatusClient p_148833_1_) {
      p_148833_1_.handleServerInfo(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ServerStatusResponse getResponse() {
      return this.response;
   }
}
