package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketChunkData implements Packet<INetHandlerPlayClient> {
   private int chunkX;
   private int chunkZ;
   private int availableSections;
   private byte[] buffer;
   private List<NBTTagCompound> tileEntityTags;
   private boolean fullChunk;

   public SPacketChunkData() {
   }

   public SPacketChunkData(Chunk p_i47124_1_, int p_i47124_2_) {
      this.chunkX = p_i47124_1_.x;
      this.chunkZ = p_i47124_1_.z;
      this.fullChunk = p_i47124_2_ == 65535;
      boolean flag = p_i47124_1_.getWorld().dimension.hasSkyLight();
      this.buffer = new byte[this.calculateChunkSize(p_i47124_1_, flag, p_i47124_2_)];
      this.availableSections = this.extractChunkData(new PacketBuffer(this.getWriteBuffer()), p_i47124_1_, flag, p_i47124_2_);
      this.tileEntityTags = Lists.newArrayList();

      for(Entry<BlockPos, TileEntity> entry : p_i47124_1_.getTileEntityMap().entrySet()) {
         BlockPos blockpos = entry.getKey();
         TileEntity tileentity = entry.getValue();
         int i = blockpos.getY() >> 4;
         if (this.isFullChunk() || (p_i47124_2_ & 1 << i) != 0) {
            NBTTagCompound nbttagcompound = tileentity.getUpdateTag();
            this.tileEntityTags.add(nbttagcompound);
         }
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.chunkX = p_148837_1_.readInt();
      this.chunkZ = p_148837_1_.readInt();
      this.fullChunk = p_148837_1_.readBoolean();
      this.availableSections = p_148837_1_.readVarInt();
      int i = p_148837_1_.readVarInt();
      if (i > 2097152) {
         throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
      } else {
         this.buffer = new byte[i];
         p_148837_1_.readBytes(this.buffer);
         int j = p_148837_1_.readVarInt();
         this.tileEntityTags = Lists.newArrayList();

         for(int k = 0; k < j; ++k) {
            this.tileEntityTags.add(p_148837_1_.readCompoundTag());
         }

      }
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(this.chunkX);
      p_148840_1_.writeInt(this.chunkZ);
      p_148840_1_.writeBoolean(this.fullChunk);
      p_148840_1_.writeVarInt(this.availableSections);
      p_148840_1_.writeVarInt(this.buffer.length);
      p_148840_1_.writeBytes(this.buffer);
      p_148840_1_.writeVarInt(this.tileEntityTags.size());

      for(NBTTagCompound nbttagcompound : this.tileEntityTags) {
         p_148840_1_.writeCompoundTag(nbttagcompound);
      }

   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleChunkData(this);
   }

   @OnlyIn(Dist.CLIENT)
   public PacketBuffer getReadBuffer() {
      return new PacketBuffer(Unpooled.wrappedBuffer(this.buffer));
   }

   private ByteBuf getWriteBuffer() {
      ByteBuf bytebuf = Unpooled.wrappedBuffer(this.buffer);
      bytebuf.writerIndex(0);
      return bytebuf;
   }

   public int extractChunkData(PacketBuffer p_189555_1_, Chunk p_189555_2_, boolean p_189555_3_, int p_189555_4_) {
      int i = 0;
      ChunkSection[] achunksection = p_189555_2_.getSections();
      int j = 0;

      for(int k = achunksection.length; j < k; ++j) {
         ChunkSection chunksection = achunksection[j];
         if (chunksection != Chunk.EMPTY_SECTION && (!this.isFullChunk() || !chunksection.isEmpty()) && (p_189555_4_ & 1 << j) != 0) {
            i |= 1 << j;
            chunksection.getData().write(p_189555_1_);
            p_189555_1_.writeBytes(chunksection.getBlockLight().getData());
            if (p_189555_3_) {
               p_189555_1_.writeBytes(chunksection.getSkyLight().getData());
            }
         }
      }

      if (this.isFullChunk()) {
         Biome[] abiome = p_189555_2_.getBiomes();

         for(int l = 0; l < abiome.length; ++l) {
            p_189555_1_.writeInt(IRegistry.field_212624_m.func_148757_b(abiome[l]));
         }
      }

      return i;
   }

   protected int calculateChunkSize(Chunk p_189556_1_, boolean p_189556_2_, int p_189556_3_) {
      int i = 0;
      ChunkSection[] achunksection = p_189556_1_.getSections();
      int j = 0;

      for(int k = achunksection.length; j < k; ++j) {
         ChunkSection chunksection = achunksection[j];
         if (chunksection != Chunk.EMPTY_SECTION && (!this.isFullChunk() || !chunksection.isEmpty()) && (p_189556_3_ & 1 << j) != 0) {
            i = i + chunksection.getData().getSerializedSize();
            i = i + chunksection.getBlockLight().getData().length;
            if (p_189556_2_) {
               i += chunksection.getSkyLight().getData().length;
            }
         }
      }

      if (this.isFullChunk()) {
         i += p_189556_1_.getBiomes().length * 4;
      }

      return i;
   }

   @OnlyIn(Dist.CLIENT)
   public int getChunkX() {
      return this.chunkX;
   }

   @OnlyIn(Dist.CLIENT)
   public int getChunkZ() {
      return this.chunkZ;
   }

   @OnlyIn(Dist.CLIENT)
   public int getExtractedSize() {
      return this.availableSections;
   }

   public boolean isFullChunk() {
      return this.fullChunk;
   }

   @OnlyIn(Dist.CLIENT)
   public List<NBTTagCompound> getTileEntityTags() {
      return this.tileEntityTags;
   }
}
