package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;

public class MapData extends WorldSavedData {
   public int xCenter;
   public int zCenter;
   public DimensionType dimension;
   public boolean trackingPosition;
   public boolean unlimitedTracking;
   public byte scale;
   public byte[] colors = new byte[16384];
   public List<MapData.MapInfo> playersArrayList = Lists.newArrayList();
   private final Map<EntityPlayer, MapData.MapInfo> playersHashMap = Maps.newHashMap();
   private final Map<String, MapBanner> banners = Maps.newHashMap();
   public Map<String, MapDecoration> mapDecorations = Maps.newLinkedHashMap();
   private final Map<String, MapFrame> field_212442_l = Maps.newHashMap();

   public MapData(String p_i2140_1_) {
      super(p_i2140_1_);
   }

   public void func_212440_a(int p_212440_1_, int p_212440_2_, int p_212440_3_, boolean p_212440_4_, boolean p_212440_5_, DimensionType p_212440_6_) {
      this.scale = (byte)p_212440_3_;
      this.calculateMapCenter((double)p_212440_1_, (double)p_212440_2_, this.scale);
      this.dimension = p_212440_6_;
      this.trackingPosition = p_212440_4_;
      this.unlimitedTracking = p_212440_5_;
      this.markDirty();
   }

   public void calculateMapCenter(double p_176054_1_, double p_176054_3_, int p_176054_5_) {
      int i = 128 * (1 << p_176054_5_);
      int j = MathHelper.floor((p_176054_1_ + 64.0D) / (double)i);
      int k = MathHelper.floor((p_176054_3_ + 64.0D) / (double)i);
      this.xCenter = j * i + i / 2 - 64;
      this.zCenter = k * i + i / 2 - 64;
   }

   public void readFromNBT(NBTTagCompound p_76184_1_) {
      this.dimension = DimensionType.getById(p_76184_1_.getInteger("dimension"));
      this.xCenter = p_76184_1_.getInteger("xCenter");
      this.zCenter = p_76184_1_.getInteger("zCenter");
      this.scale = (byte)MathHelper.clamp(p_76184_1_.getByte("scale"), 0, 4);
      this.trackingPosition = !p_76184_1_.hasKey("trackingPosition", 1) || p_76184_1_.getBoolean("trackingPosition");
      this.unlimitedTracking = p_76184_1_.getBoolean("unlimitedTracking");
      this.colors = p_76184_1_.getByteArray("colors");
      if (this.colors.length != 16384) {
         this.colors = new byte[16384];
      }

      NBTTagList nbttaglist = p_76184_1_.getTagList("banners", 10);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         MapBanner mapbanner = MapBanner.fromNbt(nbttaglist.getCompoundTagAt(i));
         this.banners.put(mapbanner.getMapDecorationId(), mapbanner);
         this.updateDecorations(mapbanner.getDecorationType(), (IWorld)null, mapbanner.getMapDecorationId(), (double)mapbanner.getPos().getX(), (double)mapbanner.getPos().getZ(), 180.0D, mapbanner.getName());
      }

      NBTTagList nbttaglist1 = p_76184_1_.getTagList("frames", 10);

      for(int j = 0; j < nbttaglist1.size(); ++j) {
         MapFrame mapframe = MapFrame.func_212765_a(nbttaglist1.getCompoundTagAt(j));
         this.field_212442_l.put(mapframe.func_212767_e(), mapframe);
         this.updateDecorations(MapDecoration.Type.FRAME, (IWorld)null, "frame-" + mapframe.func_212769_d(), (double)mapframe.func_212764_b().getX(), (double)mapframe.func_212764_b().getZ(), (double)mapframe.func_212768_c(), (ITextComponent)null);
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189551_1_) {
      p_189551_1_.setInteger("dimension", this.dimension.getId());
      p_189551_1_.setInteger("xCenter", this.xCenter);
      p_189551_1_.setInteger("zCenter", this.zCenter);
      p_189551_1_.setByte("scale", this.scale);
      p_189551_1_.setByteArray("colors", this.colors);
      p_189551_1_.setBoolean("trackingPosition", this.trackingPosition);
      p_189551_1_.setBoolean("unlimitedTracking", this.unlimitedTracking);
      NBTTagList nbttaglist = new NBTTagList();

      for(MapBanner mapbanner : this.banners.values()) {
         nbttaglist.add((INBTBase)mapbanner.toNbt());
      }

      p_189551_1_.setTag("banners", nbttaglist);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(MapFrame mapframe : this.field_212442_l.values()) {
         nbttaglist1.add((INBTBase)mapframe.func_212770_a());
      }

      p_189551_1_.setTag("frames", nbttaglist1);
      return p_189551_1_;
   }

   public void updateVisiblePlayers(EntityPlayer p_76191_1_, ItemStack p_76191_2_) {
      if (!this.playersHashMap.containsKey(p_76191_1_)) {
         MapData.MapInfo mapdata$mapinfo = new MapData.MapInfo(p_76191_1_);
         this.playersHashMap.put(p_76191_1_, mapdata$mapinfo);
         this.playersArrayList.add(mapdata$mapinfo);
      }

      if (!p_76191_1_.inventory.hasItemStack(p_76191_2_)) {
         this.mapDecorations.remove(p_76191_1_.getName().getString());
      }

      for(int i = 0; i < this.playersArrayList.size(); ++i) {
         MapData.MapInfo mapdata$mapinfo1 = this.playersArrayList.get(i);
         String s = mapdata$mapinfo1.player.getName().getString();
         if (!mapdata$mapinfo1.player.isDead && (mapdata$mapinfo1.player.inventory.hasItemStack(p_76191_2_) || p_76191_2_.isOnItemFrame())) {
            if (!p_76191_2_.isOnItemFrame() && mapdata$mapinfo1.player.dimension == this.dimension && this.trackingPosition) {
               this.updateDecorations(MapDecoration.Type.PLAYER, mapdata$mapinfo1.player.world, s, mapdata$mapinfo1.player.posX, mapdata$mapinfo1.player.posZ, (double)mapdata$mapinfo1.player.rotationYaw, (ITextComponent)null);
            }
         } else {
            this.playersHashMap.remove(mapdata$mapinfo1.player);
            this.playersArrayList.remove(mapdata$mapinfo1);
            this.mapDecorations.remove(s);
         }
      }

      if (p_76191_2_.isOnItemFrame() && this.trackingPosition) {
         EntityItemFrame entityitemframe = p_76191_2_.getItemFrame();
         BlockPos blockpos = entityitemframe.getHangingPosition();
         MapFrame mapframe1 = this.field_212442_l.get(MapFrame.func_212766_a(blockpos));
         if (mapframe1 != null && entityitemframe.getEntityId() != mapframe1.func_212769_d() && this.field_212442_l.containsKey(mapframe1.func_212767_e())) {
            this.mapDecorations.remove("frame-" + mapframe1.func_212769_d());
         }

         MapFrame mapframe = new MapFrame(blockpos, entityitemframe.facingDirection.getHorizontalIndex() * 90, entityitemframe.getEntityId());
         this.updateDecorations(MapDecoration.Type.FRAME, p_76191_1_.world, "frame-" + entityitemframe.getEntityId(), (double)blockpos.getX(), (double)blockpos.getZ(), (double)(entityitemframe.facingDirection.getHorizontalIndex() * 90), (ITextComponent)null);
         this.field_212442_l.put(mapframe.func_212767_e(), mapframe);
      }

      NBTTagCompound nbttagcompound = p_76191_2_.getTag();
      if (nbttagcompound != null && nbttagcompound.hasKey("Decorations", 9)) {
         NBTTagList nbttaglist = nbttagcompound.getTagList("Decorations", 10);

         for(int j = 0; j < nbttaglist.size(); ++j) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(j);
            if (!this.mapDecorations.containsKey(nbttagcompound1.getString("id"))) {
               this.updateDecorations(MapDecoration.Type.byIcon(nbttagcompound1.getByte("type")), p_76191_1_.world, nbttagcompound1.getString("id"), nbttagcompound1.getDouble("x"), nbttagcompound1.getDouble("z"), nbttagcompound1.getDouble("rot"), (ITextComponent)null);
            }
         }
      }

   }

   public static void addTargetDecoration(ItemStack p_191094_0_, BlockPos p_191094_1_, String p_191094_2_, MapDecoration.Type p_191094_3_) {
      NBTTagList nbttaglist;
      if (p_191094_0_.hasTag() && p_191094_0_.getTag().hasKey("Decorations", 9)) {
         nbttaglist = p_191094_0_.getTag().getTagList("Decorations", 10);
      } else {
         nbttaglist = new NBTTagList();
         p_191094_0_.setTagInfo("Decorations", nbttaglist);
      }

      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setByte("type", p_191094_3_.getIcon());
      nbttagcompound.setString("id", p_191094_2_);
      nbttagcompound.setDouble("x", (double)p_191094_1_.getX());
      nbttagcompound.setDouble("z", (double)p_191094_1_.getZ());
      nbttagcompound.setDouble("rot", 180.0D);
      nbttaglist.add((INBTBase)nbttagcompound);
      if (p_191094_3_.hasMapColor()) {
         NBTTagCompound nbttagcompound1 = p_191094_0_.getOrCreateChildTag("display");
         nbttagcompound1.setInteger("MapColor", p_191094_3_.getMapColor());
      }

   }

   private void updateDecorations(MapDecoration.Type p_191095_1_, @Nullable IWorld p_191095_2_, String p_191095_3_, double p_191095_4_, double p_191095_6_, double p_191095_8_, @Nullable ITextComponent p_191095_10_) {
      int i = 1 << this.scale;
      float f = (float)(p_191095_4_ - (double)this.xCenter) / (float)i;
      float f1 = (float)(p_191095_6_ - (double)this.zCenter) / (float)i;
      byte b0 = (byte)((int)((double)(f * 2.0F) + 0.5D));
      byte b1 = (byte)((int)((double)(f1 * 2.0F) + 0.5D));
      int j = 63;
      byte b2;
      if (f >= -63.0F && f1 >= -63.0F && f <= 63.0F && f1 <= 63.0F) {
         p_191095_8_ = p_191095_8_ + (p_191095_8_ < 0.0D ? -8.0D : 8.0D);
         b2 = (byte)((int)(p_191095_8_ * 16.0D / 360.0D));
         if (this.dimension == DimensionType.NETHER && p_191095_2_ != null) {
            int l = (int)(p_191095_2_.getWorldInfo().getWorldTime() / 10L);
            b2 = (byte)(l * l * 34187121 + l * 121 >> 15 & 15);
         }
      } else {
         if (p_191095_1_ != MapDecoration.Type.PLAYER) {
            this.mapDecorations.remove(p_191095_3_);
            return;
         }

         int k = 320;
         if (Math.abs(f) < 320.0F && Math.abs(f1) < 320.0F) {
            p_191095_1_ = MapDecoration.Type.PLAYER_OFF_MAP;
         } else {
            if (!this.unlimitedTracking) {
               this.mapDecorations.remove(p_191095_3_);
               return;
            }

            p_191095_1_ = MapDecoration.Type.PLAYER_OFF_LIMITS;
         }

         b2 = 0;
         if (f <= -63.0F) {
            b0 = -128;
         }

         if (f1 <= -63.0F) {
            b1 = -128;
         }

         if (f >= 63.0F) {
            b0 = 127;
         }

         if (f1 >= 63.0F) {
            b1 = 127;
         }
      }

      this.mapDecorations.put(p_191095_3_, new MapDecoration(p_191095_1_, b0, b1, b2, p_191095_10_));
   }

   @Nullable
   public Packet<?> getMapPacket(ItemStack p_176052_1_, IBlockReader p_176052_2_, EntityPlayer p_176052_3_) {
      MapData.MapInfo mapdata$mapinfo = this.playersHashMap.get(p_176052_3_);
      return mapdata$mapinfo == null ? null : mapdata$mapinfo.getPacket(p_176052_1_);
   }

   public void updateMapData(int p_176053_1_, int p_176053_2_) {
      this.markDirty();

      for(MapData.MapInfo mapdata$mapinfo : this.playersArrayList) {
         mapdata$mapinfo.update(p_176053_1_, p_176053_2_);
      }

   }

   public MapData.MapInfo getMapInfo(EntityPlayer p_82568_1_) {
      MapData.MapInfo mapdata$mapinfo = this.playersHashMap.get(p_82568_1_);
      if (mapdata$mapinfo == null) {
         mapdata$mapinfo = new MapData.MapInfo(p_82568_1_);
         this.playersHashMap.put(p_82568_1_, mapdata$mapinfo);
         this.playersArrayList.add(mapdata$mapinfo);
      }

      return mapdata$mapinfo;
   }

   public void tryAddBanner(IWorld p_204269_1_, BlockPos p_204269_2_) {
      float f = (float)p_204269_2_.getX() + 0.5F;
      float f1 = (float)p_204269_2_.getZ() + 0.5F;
      int i = 1 << this.scale;
      float f2 = (f - (float)this.xCenter) / (float)i;
      float f3 = (f1 - (float)this.zCenter) / (float)i;
      int j = 63;
      boolean flag = false;
      if (f2 >= -63.0F && f3 >= -63.0F && f2 <= 63.0F && f3 <= 63.0F) {
         MapBanner mapbanner = MapBanner.fromWorld(p_204269_1_, p_204269_2_);
         if (mapbanner == null) {
            return;
         }

         boolean flag1 = true;
         if (this.banners.containsKey(mapbanner.getMapDecorationId()) && this.banners.get(mapbanner.getMapDecorationId()).equals(mapbanner)) {
            this.banners.remove(mapbanner.getMapDecorationId());
            this.mapDecorations.remove(mapbanner.getMapDecorationId());
            flag1 = false;
            flag = true;
         }

         if (flag1) {
            this.banners.put(mapbanner.getMapDecorationId(), mapbanner);
            this.updateDecorations(mapbanner.getDecorationType(), p_204269_1_, mapbanner.getMapDecorationId(), (double)f, (double)f1, 180.0D, mapbanner.getName());
            flag = true;
         }

         if (flag) {
            this.markDirty();
         }
      }

   }

   public void removeStaleBanners(IBlockReader p_204268_1_, int p_204268_2_, int p_204268_3_) {
      Iterator<MapBanner> iterator = this.banners.values().iterator();

      while(iterator.hasNext()) {
         MapBanner mapbanner = iterator.next();
         if (mapbanner.getPos().getX() == p_204268_2_ && mapbanner.getPos().getZ() == p_204268_3_) {
            MapBanner mapbanner1 = MapBanner.fromWorld(p_204268_1_, mapbanner.getPos());
            if (!mapbanner.equals(mapbanner1)) {
               iterator.remove();
               this.mapDecorations.remove(mapbanner.getMapDecorationId());
            }
         }
      }

   }

   public void func_212441_a(BlockPos p_212441_1_, int p_212441_2_) {
      this.mapDecorations.remove("frame-" + p_212441_2_);
      this.field_212442_l.remove(MapFrame.func_212766_a(p_212441_1_));
   }

   public class MapInfo {
      public final EntityPlayer player;
      private boolean isDirty = true;
      private int minX;
      private int minY;
      private int maxX = 127;
      private int maxY = 127;
      private int tick;
      public int step;

      public MapInfo(EntityPlayer p_i2138_2_) {
         this.player = p_i2138_2_;
      }

      @Nullable
      public Packet<?> getPacket(ItemStack p_176101_1_) {
         if (this.isDirty) {
            this.isDirty = false;
            return new SPacketMaps(ItemMap.getMapId(p_176101_1_), MapData.this.scale, MapData.this.trackingPosition, MapData.this.mapDecorations.values(), MapData.this.colors, this.minX, this.minY, this.maxX + 1 - this.minX, this.maxY + 1 - this.minY);
         } else {
            return this.tick++ % 5 == 0 ? new SPacketMaps(ItemMap.getMapId(p_176101_1_), MapData.this.scale, MapData.this.trackingPosition, MapData.this.mapDecorations.values(), MapData.this.colors, 0, 0, 0, 0) : null;
         }
      }

      public void update(int p_176102_1_, int p_176102_2_) {
         if (this.isDirty) {
            this.minX = Math.min(this.minX, p_176102_1_);
            this.minY = Math.min(this.minY, p_176102_2_);
            this.maxX = Math.max(this.maxX, p_176102_1_);
            this.maxY = Math.max(this.maxY, p_176102_2_);
         } else {
            this.isDirty = true;
            this.minX = p_176102_1_;
            this.minY = p_176102_2_;
            this.maxX = p_176102_1_;
            this.maxY = p_176102_2_;
         }

      }
   }
}
