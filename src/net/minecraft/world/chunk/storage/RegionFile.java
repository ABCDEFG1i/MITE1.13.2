package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;
import net.minecraft.util.Util;

public class RegionFile {
   private static final byte[] EMPTY_SECTOR = new byte[4096];
   private final File fileName;
   private RandomAccessFile dataFile;
   private final int[] offsets = new int[1024];
   private final int[] chunkTimestamps = new int[1024];
   private List<Boolean> sectorFree;
   private int sizeDelta;
   private long lastModified;

   public RegionFile(File p_i2001_1_) {
      this.fileName = p_i2001_1_;
      this.sizeDelta = 0;

      try {
         if (p_i2001_1_.exists()) {
            this.lastModified = p_i2001_1_.lastModified();
         }

         this.dataFile = new RandomAccessFile(p_i2001_1_, "rw");
         if (this.dataFile.length() < 4096L) {
            this.dataFile.write(EMPTY_SECTOR);
            this.dataFile.write(EMPTY_SECTOR);
            this.sizeDelta += 8192;
         }

         if ((this.dataFile.length() & 4095L) != 0L) {
            for(int i = 0; (long)i < (this.dataFile.length() & 4095L); ++i) {
               this.dataFile.write(0);
            }
         }

         int i1 = (int)this.dataFile.length() / 4096;
         this.sectorFree = Lists.newArrayListWithCapacity(i1);

         for(int j = 0; j < i1; ++j) {
            this.sectorFree.add(true);
         }

         this.sectorFree.set(0, false);
         this.sectorFree.set(1, false);
         this.dataFile.seek(0L);

         for(int j1 = 0; j1 < 1024; ++j1) {
            int k = this.dataFile.readInt();
            this.offsets[j1] = k;
            if (k != 0 && (k >> 8) + (k & 255) <= this.sectorFree.size()) {
               for(int l = 0; l < (k & 255); ++l) {
                  this.sectorFree.set((k >> 8) + l, false);
               }
            }
         }

         for(int k1 = 0; k1 < 1024; ++k1) {
            int l1 = this.dataFile.readInt();
            this.chunkTimestamps[k1] = l1;
         }
      } catch (IOException ioexception) {
         ioexception.printStackTrace();
      }

   }

   @Nullable
   public synchronized DataInputStream getChunkDataInputStream(int p_76704_1_, int p_76704_2_) {
      if (this.outOfBounds(p_76704_1_, p_76704_2_)) {
         return null;
      } else {
         try {
            int i = this.getOffset(p_76704_1_, p_76704_2_);
            if (i == 0) {
               return null;
            } else {
               int j = i >> 8;
               int k = i & 255;
               if (j + k > this.sectorFree.size()) {
                  return null;
               } else {
                  this.dataFile.seek((long)(j * 4096));
                  int l = this.dataFile.readInt();
                  if (l > 4096 * k) {
                     return null;
                  } else if (l <= 0) {
                     return null;
                  } else {
                     byte b0 = this.dataFile.readByte();
                     if (b0 == 1) {
                        byte[] abyte1 = new byte[l - 1];
                        this.dataFile.read(abyte1);
                        return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(abyte1))));
                     } else if (b0 == 2) {
                        byte[] abyte = new byte[l - 1];
                        this.dataFile.read(abyte);
                        return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(abyte))));
                     } else {
                        return null;
                     }
                  }
               }
            }
         } catch (IOException var9) {
            return null;
         }
      }
   }

   public boolean func_212167_b(int p_212167_1_, int p_212167_2_) {
      if (this.outOfBounds(p_212167_1_, p_212167_2_)) {
         return false;
      } else {
         int i = this.getOffset(p_212167_1_, p_212167_2_);
         if (i == 0) {
            return false;
         } else {
            int j = i >> 8;
            int k = i & 255;
            if (j + k > this.sectorFree.size()) {
               return false;
            } else {
               try {
                  this.dataFile.seek((long)(j * 4096));
                  int l = this.dataFile.readInt();
                  if (l > 4096 * k) {
                     return false;
                  } else {
                     return l > 0;
                  }
               } catch (IOException var7) {
                  return false;
               }
            }
         }
      }
   }

   @Nullable
   public DataOutputStream getChunkDataOutputStream(int p_76710_1_, int p_76710_2_) {
      return this.outOfBounds(p_76710_1_, p_76710_2_) ? null : new DataOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new RegionFile.ChunkBuffer(p_76710_1_, p_76710_2_))));
   }

   protected synchronized void write(int p_76706_1_, int p_76706_2_, byte[] p_76706_3_, int p_76706_4_) {
      try {
         int i = this.getOffset(p_76706_1_, p_76706_2_);
         int j = i >> 8;
         int k = i & 255;
         int l = (p_76706_4_ + 5) / 4096 + 1;
         if (l >= 256) {
            return;
         }

         if (j != 0 && k == l) {
            this.write(j, p_76706_3_, p_76706_4_);
         } else {
            for(int i1 = 0; i1 < k; ++i1) {
               this.sectorFree.set(j + i1, true);
            }

            int l1 = this.sectorFree.indexOf(true);
            int j1 = 0;
            if (l1 != -1) {
               for(int k1 = l1; k1 < this.sectorFree.size(); ++k1) {
                  if (j1 != 0) {
                     if (this.sectorFree.get(k1)) {
                        ++j1;
                     } else {
                        j1 = 0;
                     }
                  } else if (this.sectorFree.get(k1)) {
                     l1 = k1;
                     j1 = 1;
                  }

                  if (j1 >= l) {
                     break;
                  }
               }
            }

            if (j1 >= l) {
               j = l1;
               this.setOffset(p_76706_1_, p_76706_2_, l1 << 8 | l);

               for(int j2 = 0; j2 < l; ++j2) {
                  this.sectorFree.set(j + j2, false);
               }

               this.write(j, p_76706_3_, p_76706_4_);
            } else {
               this.dataFile.seek(this.dataFile.length());
               j = this.sectorFree.size();

               for(int i2 = 0; i2 < l; ++i2) {
                  this.dataFile.write(EMPTY_SECTOR);
                  this.sectorFree.add(false);
               }

               this.sizeDelta += 4096 * l;
               this.write(j, p_76706_3_, p_76706_4_);
               this.setOffset(p_76706_1_, p_76706_2_, j << 8 | l);
            }
         }

         this.setChunkTimestamp(p_76706_1_, p_76706_2_, (int)(Util.millisecondsSinceEpoch() / 1000L));
      } catch (IOException ioexception) {
         ioexception.printStackTrace();
      }

   }

   private void write(int p_76712_1_, byte[] p_76712_2_, int p_76712_3_) throws IOException {
      this.dataFile.seek((long)(p_76712_1_ * 4096));
      this.dataFile.writeInt(p_76712_3_ + 1);
      this.dataFile.writeByte(2);
      this.dataFile.write(p_76712_2_, 0, p_76712_3_);
   }

   private boolean outOfBounds(int p_76705_1_, int p_76705_2_) {
      return p_76705_1_ < 0 || p_76705_1_ >= 32 || p_76705_2_ < 0 || p_76705_2_ >= 32;
   }

   private int getOffset(int p_76707_1_, int p_76707_2_) {
      return this.offsets[p_76707_1_ + p_76707_2_ * 32];
   }

   public boolean isChunkSaved(int p_76709_1_, int p_76709_2_) {
      return this.getOffset(p_76709_1_, p_76709_2_) != 0;
   }

   private void setOffset(int p_76711_1_, int p_76711_2_, int p_76711_3_) throws IOException {
      this.offsets[p_76711_1_ + p_76711_2_ * 32] = p_76711_3_;
      this.dataFile.seek((long)((p_76711_1_ + p_76711_2_ * 32) * 4));
      this.dataFile.writeInt(p_76711_3_);
   }

   private void setChunkTimestamp(int p_76713_1_, int p_76713_2_, int p_76713_3_) throws IOException {
      this.chunkTimestamps[p_76713_1_ + p_76713_2_ * 32] = p_76713_3_;
      this.dataFile.seek((long)(4096 + (p_76713_1_ + p_76713_2_ * 32) * 4));
      this.dataFile.writeInt(p_76713_3_);
   }

   public void close() throws IOException {
      if (this.dataFile != null) {
         this.dataFile.close();
      }

   }

   class ChunkBuffer extends ByteArrayOutputStream {
      private final int chunkX;
      private final int chunkZ;

      public ChunkBuffer(int p_i2000_2_, int p_i2000_3_) {
         super(8096);
         this.chunkX = p_i2000_2_;
         this.chunkZ = p_i2000_3_;
      }

      public void close() {
         RegionFile.this.write(this.chunkX, this.chunkZ, this.buf, this.count);
      }
   }
}
