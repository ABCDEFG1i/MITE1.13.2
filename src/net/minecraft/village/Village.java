package net.minecraft.village;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathType;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class Village {
   private World world;
   private final List<VillageDoorInfo> villageDoorInfoList = Lists.newArrayList();
   private BlockPos centerHelper = BlockPos.ORIGIN;
   private BlockPos center = BlockPos.ORIGIN;
   private int villageRadius;
   private int lastAddDoorTimestamp;
   private int tickCounter;
   private int villagerCount;
   private int noBreedTicks;
   private final Map<String, Integer> playerReputation = Maps.newHashMap();
   private final List<Village.VillageAggressor> villageAgressors = Lists.newArrayList();
   private int golemCount;

   public Village() {
   }

   public Village(World p_i1675_1_) {
      this.world = p_i1675_1_;
   }

   public void setWorld(World p_82691_1_) {
      this.world = p_82691_1_;
   }

   public void tick(int p_75560_1_) {
      this.tickCounter = p_75560_1_;
      this.removeDeadAndOutOfRangeDoors();
      this.removeDeadAndOldAgressors();
      if (p_75560_1_ % 20 == 0) {
         this.updateVillagerCount();
      }

      if (p_75560_1_ % 30 == 0) {
         this.updateGolemCount();
      }

      int i = this.villagerCount / 10;
      if (this.golemCount < i && this.villageDoorInfoList.size() > 20 && this.world.rand.nextInt(7000) == 0) {
         Entity entity = this.func_208059_f(this.center);
         if (entity != null) {
            ++this.golemCount;
         }
      }

   }

   @Nullable
   private Entity func_208059_f(BlockPos p_208059_1_) {
      for(int i = 0; i < 10; ++i) {
         BlockPos blockpos = p_208059_1_.add(this.world.rand.nextInt(16) - 8, this.world.rand.nextInt(6) - 3, this.world.rand.nextInt(16) - 8);
         if (this.isBlockPosWithinSqVillageRadius(blockpos)) {
            EntityIronGolem entityirongolem = EntityType.IRON_GOLEM.makeEntity(this.world, null, null,
                    null, blockpos, false, false);
            if (entityirongolem != null) {
               if (entityirongolem.func_205020_a(this.world, false) && entityirongolem.isNotColliding(this.world)) {
                  this.world.spawnEntity(entityirongolem);
                  return entityirongolem;
               }

               entityirongolem.setDead();
            }
         }
      }

      return null;
   }

   private void updateGolemCount() {
      List<EntityIronGolem> list = this.world.getEntitiesWithinAABB(EntityIronGolem.class, new AxisAlignedBB((double)(this.center.getX() - this.villageRadius), (double)(this.center.getY() - 4), (double)(this.center.getZ() - this.villageRadius), (double)(this.center.getX() + this.villageRadius), (double)(this.center.getY() + 4), (double)(this.center.getZ() + this.villageRadius)));
      this.golemCount = list.size();
   }

   private void updateVillagerCount() {
      List<EntityVillager> list = this.world.getEntitiesWithinAABB(EntityVillager.class, new AxisAlignedBB((double)(this.center.getX() - this.villageRadius), (double)(this.center.getY() - 4), (double)(this.center.getZ() - this.villageRadius), (double)(this.center.getX() + this.villageRadius), (double)(this.center.getY() + 4), (double)(this.center.getZ() + this.villageRadius)));
      this.villagerCount = list.size();
      if (this.villagerCount == 0) {
         this.playerReputation.clear();
      }

   }

   public BlockPos getCenter() {
      return this.center;
   }

   public int getVillageRadius() {
      return this.villageRadius;
   }

   public int getNumVillageDoors() {
      return this.villageDoorInfoList.size();
   }

   public int getTicksSinceLastDoorAdding() {
      return this.tickCounter - this.lastAddDoorTimestamp;
   }

   public int getNumVillagers() {
      return this.villagerCount;
   }

   public boolean isBlockPosWithinSqVillageRadius(BlockPos p_179866_1_) {
      return this.center.distanceSq(p_179866_1_) < (double)(this.villageRadius * this.villageRadius);
   }

   public List<VillageDoorInfo> getVillageDoorInfoList() {
      return this.villageDoorInfoList;
   }

   public VillageDoorInfo getNearestDoor(BlockPos p_179865_1_) {
      VillageDoorInfo villagedoorinfo = null;
      int i = Integer.MAX_VALUE;

      for(VillageDoorInfo villagedoorinfo1 : this.villageDoorInfoList) {
         int j = villagedoorinfo1.getDistanceToDoorBlockSq(p_179865_1_);
         if (j < i) {
            villagedoorinfo = villagedoorinfo1;
            i = j;
         }
      }

      return villagedoorinfo;
   }

   public VillageDoorInfo getDoorInfo(BlockPos p_179863_1_) {
      VillageDoorInfo villagedoorinfo = null;
      int i = Integer.MAX_VALUE;

      for(VillageDoorInfo villagedoorinfo1 : this.villageDoorInfoList) {
         int j = villagedoorinfo1.getDistanceToDoorBlockSq(p_179863_1_);
         if (j > 256) {
            j = j * 1000;
         } else {
            j = villagedoorinfo1.getDoorOpeningRestrictionCounter();
         }

         if (j < i) {
            BlockPos blockpos = villagedoorinfo1.getDoorBlockPos();
            EnumFacing enumfacing = villagedoorinfo1.getInsideDirection();
            if (this.world.getBlockState(blockpos.offset(enumfacing, 1)).allowsMovement(this.world, blockpos.offset(enumfacing, 1), PathType.LAND) && this.world.getBlockState(blockpos.offset(enumfacing, -1)).allowsMovement(this.world, blockpos.offset(enumfacing, -1), PathType.LAND) && this.world.getBlockState(blockpos.up().offset(enumfacing, 1)).allowsMovement(this.world, blockpos.up().offset(enumfacing, 1), PathType.LAND) && this.world.getBlockState(blockpos.up().offset(enumfacing, -1)).allowsMovement(this.world, blockpos.up().offset(enumfacing, -1), PathType.LAND)) {
               villagedoorinfo = villagedoorinfo1;
               i = j;
            }
         }
      }

      return villagedoorinfo;
   }

   @Nullable
   public VillageDoorInfo getExistedDoor(BlockPos p_179864_1_) {
      if (this.center.distanceSq(p_179864_1_) > (double)(this.villageRadius * this.villageRadius)) {
         return null;
      } else {
         for(VillageDoorInfo villagedoorinfo : this.villageDoorInfoList) {
            if (villagedoorinfo.getDoorBlockPos().getX() == p_179864_1_.getX() && villagedoorinfo.getDoorBlockPos().getZ() == p_179864_1_.getZ() && Math.abs(villagedoorinfo.getDoorBlockPos().getY() - p_179864_1_.getY()) <= 1) {
               return villagedoorinfo;
            }
         }

         return null;
      }
   }

   public void addVillageDoorInfo(VillageDoorInfo p_75576_1_) {
      this.villageDoorInfoList.add(p_75576_1_);
      this.centerHelper = this.centerHelper.add(p_75576_1_.getDoorBlockPos());
      this.updateVillageRadiusAndCenter();
      this.lastAddDoorTimestamp = p_75576_1_.getLastActivityTimestamp();
   }

   public boolean isAnnihilated() {
      return this.villageDoorInfoList.isEmpty();
   }

   public void addOrRenewAgressor(EntityLivingBase p_75575_1_) {
      for(Village.VillageAggressor village$villageaggressor : this.villageAgressors) {
         if (village$villageaggressor.agressor == p_75575_1_) {
            village$villageaggressor.agressionTime = this.tickCounter;
            return;
         }
      }

      this.villageAgressors.add(new Village.VillageAggressor(p_75575_1_, this.tickCounter));
   }

   @Nullable
   public EntityLivingBase findNearestVillageAggressor(EntityLivingBase p_75571_1_) {
      double d0 = Double.MAX_VALUE;
      Village.VillageAggressor village$villageaggressor = null;

      for(int i = 0; i < this.villageAgressors.size(); ++i) {
         Village.VillageAggressor village$villageaggressor1 = this.villageAgressors.get(i);
         double d1 = village$villageaggressor1.agressor.getDistanceSq(p_75571_1_);
         if (!(d1 > d0)) {
            village$villageaggressor = village$villageaggressor1;
            d0 = d1;
         }
      }

      return village$villageaggressor == null ? null : village$villageaggressor.agressor;
   }

   public EntityPlayer getNearestTargetPlayer(EntityLivingBase p_82685_1_) {
      double d0 = Double.MAX_VALUE;
      EntityPlayer entityplayer = null;

      for(String s : this.playerReputation.keySet()) {
         if (this.isPlayerReputationTooLow(s)) {
            EntityPlayer entityplayer1 = this.world.getPlayerEntityByName(s);
            if (entityplayer1 != null) {
               double d1 = entityplayer1.getDistanceSq(p_82685_1_);
               if (!(d1 > d0)) {
                  entityplayer = entityplayer1;
                  d0 = d1;
               }
            }
         }
      }

      return entityplayer;
   }

   private void removeDeadAndOldAgressors() {
      Iterator<Village.VillageAggressor> iterator = this.villageAgressors.iterator();

      while(iterator.hasNext()) {
         Village.VillageAggressor village$villageaggressor = iterator.next();
         if (!village$villageaggressor.agressor.isEntityAlive() || Math.abs(this.tickCounter - village$villageaggressor.agressionTime) > 300) {
            iterator.remove();
         }
      }

   }

   private void removeDeadAndOutOfRangeDoors() {
      boolean flag = false;
      boolean flag1 = this.world.rand.nextInt(50) == 0;
      Iterator<VillageDoorInfo> iterator = this.villageDoorInfoList.iterator();

      while(iterator.hasNext()) {
         VillageDoorInfo villagedoorinfo = iterator.next();
         if (flag1) {
            villagedoorinfo.resetDoorOpeningRestrictionCounter();
         }

         if (!this.isWoodDoor(villagedoorinfo.getDoorBlockPos()) || Math.abs(this.tickCounter - villagedoorinfo.getLastActivityTimestamp()) > 1200) {
            this.centerHelper = this.centerHelper.subtract(villagedoorinfo.getDoorBlockPos());
            flag = true;
            villagedoorinfo.setIsDetachedFromVillageFlag(true);
            iterator.remove();
         }
      }

      if (flag) {
         this.updateVillageRadiusAndCenter();
      }

   }

   private boolean isWoodDoor(BlockPos p_179860_1_) {
      IBlockState iblockstate = this.world.getBlockState(p_179860_1_);
      Block block = iblockstate.getBlock();
      if (block instanceof BlockDoor) {
         return iblockstate.getMaterial() == Material.WOOD;
      } else {
         return false;
      }
   }

   private void updateVillageRadiusAndCenter() {
      int i = this.villageDoorInfoList.size();
      if (i == 0) {
         this.center = BlockPos.ORIGIN;
         this.villageRadius = 0;
      } else {
         this.center = new BlockPos(this.centerHelper.getX() / i, this.centerHelper.getY() / i, this.centerHelper.getZ() / i);
         int j = 0;

         for(VillageDoorInfo villagedoorinfo : this.villageDoorInfoList) {
            j = Math.max(villagedoorinfo.getDistanceToDoorBlockSq(this.center), j);
         }

         this.villageRadius = Math.max(32, (int)Math.sqrt((double)j) + 1);
      }
   }

   public int getPlayerReputation(String p_82684_1_) {
      Integer integer = this.playerReputation.get(p_82684_1_);
      return integer == null ? 0 : integer;
   }

   public int modifyPlayerReputation(String p_82688_1_, int p_82688_2_) {
      int i = this.getPlayerReputation(p_82688_1_);
      int j = MathHelper.clamp(i + p_82688_2_, -30, 10);
      this.playerReputation.put(p_82688_1_, j);
      return j;
   }

   public boolean isPlayerReputationTooLow(String p_82687_1_) {
      return this.getPlayerReputation(p_82687_1_) <= -15;
   }

   public void readVillageDataFromNBT(NBTTagCompound p_82690_1_) {
      this.villagerCount = p_82690_1_.getInteger("PopSize");
      this.villageRadius = p_82690_1_.getInteger("Radius");
      this.golemCount = p_82690_1_.getInteger("Golems");
      this.lastAddDoorTimestamp = p_82690_1_.getInteger("Stable");
      this.tickCounter = p_82690_1_.getInteger("Tick");
      this.noBreedTicks = p_82690_1_.getInteger("MTick");
      this.center = new BlockPos(p_82690_1_.getInteger("CX"), p_82690_1_.getInteger("CY"), p_82690_1_.getInteger("CZ"));
      this.centerHelper = new BlockPos(p_82690_1_.getInteger("ACX"), p_82690_1_.getInteger("ACY"), p_82690_1_.getInteger("ACZ"));
      NBTTagList nbttaglist = p_82690_1_.getTagList("Doors", 10);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
         VillageDoorInfo villagedoorinfo = new VillageDoorInfo(new BlockPos(nbttagcompound.getInteger("X"), nbttagcompound.getInteger("Y"), nbttagcompound.getInteger("Z")), nbttagcompound.getInteger("IDX"), nbttagcompound.getInteger("IDZ"), nbttagcompound.getInteger("TS"));
         this.villageDoorInfoList.add(villagedoorinfo);
      }

      NBTTagList nbttaglist1 = p_82690_1_.getTagList("Players", 10);

      for(int j = 0; j < nbttaglist1.size(); ++j) {
         NBTTagCompound nbttagcompound1 = nbttaglist1.getCompoundTagAt(j);
         if (nbttagcompound1.hasKey("UUID") && this.world != null && this.world.getServer() != null) {
            PlayerProfileCache playerprofilecache = this.world.getServer().getPlayerProfileCache();
            GameProfile gameprofile = playerprofilecache.getProfileByUUID(UUID.fromString(nbttagcompound1.getString("UUID")));
            if (gameprofile != null) {
               this.playerReputation.put(gameprofile.getName(), nbttagcompound1.getInteger("S"));
            }
         } else {
            this.playerReputation.put(nbttagcompound1.getString("Name"), nbttagcompound1.getInteger("S"));
         }
      }

   }

   public void writeVillageDataToNBT(NBTTagCompound p_82689_1_) {
      p_82689_1_.setInteger("PopSize", this.villagerCount);
      p_82689_1_.setInteger("Radius", this.villageRadius);
      p_82689_1_.setInteger("Golems", this.golemCount);
      p_82689_1_.setInteger("Stable", this.lastAddDoorTimestamp);
      p_82689_1_.setInteger("Tick", this.tickCounter);
      p_82689_1_.setInteger("MTick", this.noBreedTicks);
      p_82689_1_.setInteger("CX", this.center.getX());
      p_82689_1_.setInteger("CY", this.center.getY());
      p_82689_1_.setInteger("CZ", this.center.getZ());
      p_82689_1_.setInteger("ACX", this.centerHelper.getX());
      p_82689_1_.setInteger("ACY", this.centerHelper.getY());
      p_82689_1_.setInteger("ACZ", this.centerHelper.getZ());
      NBTTagList nbttaglist = new NBTTagList();

      for(VillageDoorInfo villagedoorinfo : this.villageDoorInfoList) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.setInteger("X", villagedoorinfo.getDoorBlockPos().getX());
         nbttagcompound.setInteger("Y", villagedoorinfo.getDoorBlockPos().getY());
         nbttagcompound.setInteger("Z", villagedoorinfo.getDoorBlockPos().getZ());
         nbttagcompound.setInteger("IDX", villagedoorinfo.getInsideOffsetX());
         nbttagcompound.setInteger("IDZ", villagedoorinfo.getInsideOffsetZ());
         nbttagcompound.setInteger("TS", villagedoorinfo.getLastActivityTimestamp());
         nbttaglist.add(nbttagcompound);
      }

      p_82689_1_.setTag("Doors", nbttaglist);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(String s : this.playerReputation.keySet()) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         PlayerProfileCache playerprofilecache = this.world.getServer().getPlayerProfileCache();

         try {
            GameProfile gameprofile = playerprofilecache.getGameProfileForUsername(s);
            if (gameprofile != null) {
               nbttagcompound1.setString("UUID", gameprofile.getId().toString());
               nbttagcompound1.setInteger("S", this.playerReputation.get(s));
               nbttaglist1.add(nbttagcompound1);
            }
         } catch (RuntimeException var9) {
         }
      }

      p_82689_1_.setTag("Players", nbttaglist1);
   }

   public void endMatingSeason() {
      this.noBreedTicks = this.tickCounter;
   }

   public boolean isMatingSeason() {
      return this.noBreedTicks == 0 || this.tickCounter - this.noBreedTicks >= 3600;
   }

   public void setDefaultPlayerReputation(int p_82683_1_) {
      for(String s : this.playerReputation.keySet()) {
         this.modifyPlayerReputation(s, p_82683_1_);
      }

   }

   class VillageAggressor {
      public EntityLivingBase agressor;
      public int agressionTime;

      VillageAggressor(EntityLivingBase p_i1674_2_, int p_i1674_3_) {
         this.agressor = p_i1674_2_;
         this.agressionTime = p_i1674_3_;
      }
   }
}
