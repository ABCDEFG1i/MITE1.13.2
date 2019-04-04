package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketUpdateBossInfo implements Packet<INetHandlerPlayClient> {
   private UUID uniqueId;
   private SPacketUpdateBossInfo.Operation operation;
   private ITextComponent name;
   private float percent;
   private BossInfo.Color color;
   private BossInfo.Overlay overlay;
   private boolean darkenSky;
   private boolean playEndBossMusic;
   private boolean createFog;

   public SPacketUpdateBossInfo() {
   }

   public SPacketUpdateBossInfo(SPacketUpdateBossInfo.Operation p_i46964_1_, BossInfo p_i46964_2_) {
      this.operation = p_i46964_1_;
      this.uniqueId = p_i46964_2_.getUniqueId();
      this.name = p_i46964_2_.getName();
      this.percent = p_i46964_2_.getPercent();
      this.color = p_i46964_2_.getColor();
      this.overlay = p_i46964_2_.getOverlay();
      this.darkenSky = p_i46964_2_.shouldDarkenSky();
      this.playEndBossMusic = p_i46964_2_.shouldPlayEndBossMusic();
      this.createFog = p_i46964_2_.shouldCreateFog();
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.uniqueId = p_148837_1_.readUniqueId();
      this.operation = p_148837_1_.readEnumValue(SPacketUpdateBossInfo.Operation.class);
      switch(this.operation) {
      case ADD:
         this.name = p_148837_1_.readTextComponent();
         this.percent = p_148837_1_.readFloat();
         this.color = p_148837_1_.readEnumValue(BossInfo.Color.class);
         this.overlay = p_148837_1_.readEnumValue(BossInfo.Overlay.class);
         this.setFlags(p_148837_1_.readUnsignedByte());
      case REMOVE:
      default:
         break;
      case UPDATE_PCT:
         this.percent = p_148837_1_.readFloat();
         break;
      case UPDATE_NAME:
         this.name = p_148837_1_.readTextComponent();
         break;
      case UPDATE_STYLE:
         this.color = p_148837_1_.readEnumValue(BossInfo.Color.class);
         this.overlay = p_148837_1_.readEnumValue(BossInfo.Overlay.class);
         break;
      case UPDATE_PROPERTIES:
         this.setFlags(p_148837_1_.readUnsignedByte());
      }

   }

   private void setFlags(int p_186903_1_) {
      this.darkenSky = (p_186903_1_ & 1) > 0;
      this.playEndBossMusic = (p_186903_1_ & 2) > 0;
      this.createFog = (p_186903_1_ & 4) > 0;
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeUniqueId(this.uniqueId);
      p_148840_1_.writeEnumValue(this.operation);
      switch(this.operation) {
      case ADD:
         p_148840_1_.writeTextComponent(this.name);
         p_148840_1_.writeFloat(this.percent);
         p_148840_1_.writeEnumValue(this.color);
         p_148840_1_.writeEnumValue(this.overlay);
         p_148840_1_.writeByte(this.getFlags());
      case REMOVE:
      default:
         break;
      case UPDATE_PCT:
         p_148840_1_.writeFloat(this.percent);
         break;
      case UPDATE_NAME:
         p_148840_1_.writeTextComponent(this.name);
         break;
      case UPDATE_STYLE:
         p_148840_1_.writeEnumValue(this.color);
         p_148840_1_.writeEnumValue(this.overlay);
         break;
      case UPDATE_PROPERTIES:
         p_148840_1_.writeByte(this.getFlags());
      }

   }

   private int getFlags() {
      int i = 0;
      if (this.darkenSky) {
         i |= 1;
      }

      if (this.playEndBossMusic) {
         i |= 2;
      }

      if (this.createFog) {
         i |= 4;
      }

      return i;
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleUpdateBossInfo(this);
   }

   @OnlyIn(Dist.CLIENT)
   public UUID getUniqueId() {
      return this.uniqueId;
   }

   @OnlyIn(Dist.CLIENT)
   public SPacketUpdateBossInfo.Operation getOperation() {
      return this.operation;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getName() {
      return this.name;
   }

   @OnlyIn(Dist.CLIENT)
   public float getPercent() {
      return this.percent;
   }

   @OnlyIn(Dist.CLIENT)
   public BossInfo.Color getColor() {
      return this.color;
   }

   @OnlyIn(Dist.CLIENT)
   public BossInfo.Overlay getOverlay() {
      return this.overlay;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldDarkenSky() {
      return this.darkenSky;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldPlayEndBossMusic() {
      return this.playEndBossMusic;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldCreateFog() {
      return this.createFog;
   }

   public enum Operation {
      ADD,
      REMOVE,
      UPDATE_PCT,
      UPDATE_NAME,
      UPDATE_STYLE,
      UPDATE_PROPERTIES
   }
}
