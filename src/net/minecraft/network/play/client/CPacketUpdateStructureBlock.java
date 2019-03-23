package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketUpdateStructureBlock implements Packet<INetHandlerPlayServer> {
   private BlockPos pos;
   private TileEntityStructure.UpdateCommand field_210392_b;
   private StructureMode mode;
   private String name;
   private BlockPos field_210395_e;
   private BlockPos size;
   private Mirror mirror;
   private Rotation rotation;
   private String field_210399_i;
   private boolean field_210400_j;
   private boolean field_210401_k;
   private boolean field_210402_l;
   private float integrity;
   private long seed;

   public CPacketUpdateStructureBlock() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketUpdateStructureBlock(BlockPos p_i49541_1_, TileEntityStructure.UpdateCommand p_i49541_2_, StructureMode p_i49541_3_, String p_i49541_4_, BlockPos p_i49541_5_, BlockPos p_i49541_6_, Mirror p_i49541_7_, Rotation p_i49541_8_, String p_i49541_9_, boolean p_i49541_10_, boolean p_i49541_11_, boolean p_i49541_12_, float p_i49541_13_, long p_i49541_14_) {
      this.pos = p_i49541_1_;
      this.field_210392_b = p_i49541_2_;
      this.mode = p_i49541_3_;
      this.name = p_i49541_4_;
      this.field_210395_e = p_i49541_5_;
      this.size = p_i49541_6_;
      this.mirror = p_i49541_7_;
      this.rotation = p_i49541_8_;
      this.field_210399_i = p_i49541_9_;
      this.field_210400_j = p_i49541_10_;
      this.field_210401_k = p_i49541_11_;
      this.field_210402_l = p_i49541_12_;
      this.integrity = p_i49541_13_;
      this.seed = p_i49541_14_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.pos = p_148837_1_.readBlockPos();
      this.field_210392_b = p_148837_1_.readEnumValue(TileEntityStructure.UpdateCommand.class);
      this.mode = p_148837_1_.readEnumValue(StructureMode.class);
      this.name = p_148837_1_.readString(32767);
      this.field_210395_e = new BlockPos(MathHelper.clamp(p_148837_1_.readByte(), -32, 32), MathHelper.clamp(p_148837_1_.readByte(), -32, 32), MathHelper.clamp(p_148837_1_.readByte(), -32, 32));
      this.size = new BlockPos(MathHelper.clamp(p_148837_1_.readByte(), 0, 32), MathHelper.clamp(p_148837_1_.readByte(), 0, 32), MathHelper.clamp(p_148837_1_.readByte(), 0, 32));
      this.mirror = p_148837_1_.readEnumValue(Mirror.class);
      this.rotation = p_148837_1_.readEnumValue(Rotation.class);
      this.field_210399_i = p_148837_1_.readString(12);
      this.integrity = MathHelper.clamp(p_148837_1_.readFloat(), 0.0F, 1.0F);
      this.seed = p_148837_1_.readVarLong();
      int i = p_148837_1_.readByte();
      this.field_210400_j = (i & 1) != 0;
      this.field_210401_k = (i & 2) != 0;
      this.field_210402_l = (i & 4) != 0;
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.pos);
      p_148840_1_.writeEnumValue(this.field_210392_b);
      p_148840_1_.writeEnumValue(this.mode);
      p_148840_1_.writeString(this.name);
      p_148840_1_.writeByte(this.field_210395_e.getX());
      p_148840_1_.writeByte(this.field_210395_e.getY());
      p_148840_1_.writeByte(this.field_210395_e.getZ());
      p_148840_1_.writeByte(this.size.getX());
      p_148840_1_.writeByte(this.size.getY());
      p_148840_1_.writeByte(this.size.getZ());
      p_148840_1_.writeEnumValue(this.mirror);
      p_148840_1_.writeEnumValue(this.rotation);
      p_148840_1_.writeString(this.field_210399_i);
      p_148840_1_.writeFloat(this.integrity);
      p_148840_1_.writeVarLong(this.seed);
      int i = 0;
      if (this.field_210400_j) {
         i |= 1;
      }

      if (this.field_210401_k) {
         i |= 2;
      }

      if (this.field_210402_l) {
         i |= 4;
      }

      p_148840_1_.writeByte(i);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processUpdateStructureBlock(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public TileEntityStructure.UpdateCommand func_210384_b() {
      return this.field_210392_b;
   }

   public StructureMode getMode() {
      return this.mode;
   }

   public String getName() {
      return this.name;
   }

   public BlockPos func_210383_e() {
      return this.field_210395_e;
   }

   public BlockPos getSize() {
      return this.size;
   }

   public Mirror getMirror() {
      return this.mirror;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public String func_210388_i() {
      return this.field_210399_i;
   }

   public boolean func_210389_j() {
      return this.field_210400_j;
   }

   public boolean func_210390_k() {
      return this.field_210401_k;
   }

   public boolean func_210387_l() {
      return this.field_210402_l;
   }

   public float getIntegrity() {
      return this.integrity;
   }

   public long getSeed() {
      return this.seed;
   }
}
