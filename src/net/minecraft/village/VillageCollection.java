package net.minecraft.village;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.WorldSavedData;

public class VillageCollection extends WorldSavedData {
   private World world;
   private final List<BlockPos> villagerPositionsList = Lists.newArrayList();
   private final List<VillageDoorInfo> newDoors = Lists.newArrayList();
   private final List<Village> villageList = Lists.newArrayList();
   private int tickCounter;

   public VillageCollection(String p_i1677_1_) {
      super(p_i1677_1_);
   }

   public VillageCollection(World p_i1678_1_) {
      super(fileNameForProvider(p_i1678_1_.dimension));
      this.world = p_i1678_1_;
      this.markDirty();
   }

   public void setWorldsForAll(World p_82566_1_) {
      this.world = p_82566_1_;

      for(Village village : this.villageList) {
         village.setWorld(p_82566_1_);
      }

   }

   public void addToVillagerPositionList(BlockPos p_176060_1_) {
      if (this.villagerPositionsList.size() <= 64) {
         if (!this.positionInList(p_176060_1_)) {
            this.villagerPositionsList.add(p_176060_1_);
         }

      }
   }

   public void tick() {
      ++this.tickCounter;

      for(Village village : this.villageList) {
         village.tick(this.tickCounter);
      }

      this.removeAnnihilatedVillages();
      this.dropOldestVillagerPosition();
      this.addNewDoorsToVillageOrCreateVillage();
      if (this.tickCounter % 400 == 0) {
         this.markDirty();
      }

   }

   private void removeAnnihilatedVillages() {
      Iterator<Village> iterator = this.villageList.iterator();

      while(iterator.hasNext()) {
         Village village = iterator.next();
         if (village.isAnnihilated()) {
            iterator.remove();
            this.markDirty();
         }
      }

   }

   public List<Village> getVillageList() {
      return this.villageList;
   }

   public Village getNearestVillage(BlockPos p_176056_1_, int p_176056_2_) {
      Village village = null;
      double d0 = (double)Float.MAX_VALUE;

      for(Village village1 : this.villageList) {
         double d1 = village1.getCenter().distanceSq(p_176056_1_);
         if (!(d1 >= d0)) {
            float f = (float)(p_176056_2_ + village1.getVillageRadius());
            if (!(d1 > (double)(f * f))) {
               village = village1;
               d0 = d1;
            }
         }
      }

      return village;
   }

   private void dropOldestVillagerPosition() {
      if (!this.villagerPositionsList.isEmpty()) {
         this.addDoorsAround(this.villagerPositionsList.remove(0));
      }
   }

   private void addNewDoorsToVillageOrCreateVillage() {
      for(int i = 0; i < this.newDoors.size(); ++i) {
         VillageDoorInfo villagedoorinfo = this.newDoors.get(i);
         Village village = this.getNearestVillage(villagedoorinfo.getDoorBlockPos(), 32);
         if (village == null) {
            village = new Village(this.world);
            this.villageList.add(village);
            this.markDirty();
         }

         village.addVillageDoorInfo(villagedoorinfo);
      }

      this.newDoors.clear();
   }

   private void addDoorsAround(BlockPos p_180609_1_) {
      int i = 16;
      int j = 4;
      int k = 16;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int l = -16; l < 16; ++l) {
         for(int i1 = -4; i1 < 4; ++i1) {
            for(int j1 = -16; j1 < 16; ++j1) {
               blockpos$mutableblockpos.setPos(p_180609_1_).move(l, i1, j1);
               IBlockState iblockstate = this.world.getBlockState(blockpos$mutableblockpos);
               if (this.func_195928_a(iblockstate)) {
                  VillageDoorInfo villagedoorinfo = this.checkDoorExistence(blockpos$mutableblockpos);
                  if (villagedoorinfo == null) {
                     this.func_195927_a(iblockstate, blockpos$mutableblockpos);
                  } else {
                     villagedoorinfo.setLastActivityTimestamp(this.tickCounter);
                  }
               }
            }
         }
      }

   }

   @Nullable
   private VillageDoorInfo checkDoorExistence(BlockPos p_176055_1_) {
      for(VillageDoorInfo villagedoorinfo : this.newDoors) {
         if (villagedoorinfo.getDoorBlockPos().getX() == p_176055_1_.getX() && villagedoorinfo.getDoorBlockPos().getZ() == p_176055_1_.getZ() && Math.abs(villagedoorinfo.getDoorBlockPos().getY() - p_176055_1_.getY()) <= 1) {
            return villagedoorinfo;
         }
      }

      for(Village village : this.villageList) {
         VillageDoorInfo villagedoorinfo1 = village.getExistedDoor(p_176055_1_);
         if (villagedoorinfo1 != null) {
            return villagedoorinfo1;
         }
      }

      return null;
   }

   private void func_195927_a(IBlockState p_195927_1_, BlockPos p_195927_2_) {
      EnumFacing enumfacing = p_195927_1_.get(BlockDoor.FACING);
      EnumFacing enumfacing1 = enumfacing.getOpposite();
      int i = this.countBlocksCanSeeSky(p_195927_2_, enumfacing, 5);
      int j = this.countBlocksCanSeeSky(p_195927_2_, enumfacing1, i + 1);
      if (i != j) {
         this.newDoors.add(new VillageDoorInfo(p_195927_2_, i < j ? enumfacing : enumfacing1, this.tickCounter));
      }

   }

   private int countBlocksCanSeeSky(BlockPos p_176061_1_, EnumFacing p_176061_2_, int p_176061_3_) {
      int i = 0;

      for(int j = 1; j <= 5; ++j) {
         if (this.world.canSeeSky(p_176061_1_.offset(p_176061_2_, j))) {
            ++i;
            if (i >= p_176061_3_) {
               return i;
            }
         }
      }

      return i;
   }

   private boolean positionInList(BlockPos p_176057_1_) {
      for(BlockPos blockpos : this.villagerPositionsList) {
         if (blockpos.equals(p_176057_1_)) {
            return true;
         }
      }

      return false;
   }

   private boolean func_195928_a(IBlockState p_195928_1_) {
      return p_195928_1_.getBlock() instanceof BlockDoor && p_195928_1_.getMaterial() == Material.WOOD;
   }

   public void readFromNBT(NBTTagCompound p_76184_1_) {
      this.tickCounter = p_76184_1_.getInteger("Tick");
      NBTTagList nbttaglist = p_76184_1_.getTagList("Villages", 10);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
         Village village = new Village();
         village.readVillageDataFromNBT(nbttagcompound);
         this.villageList.add(village);
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189551_1_) {
      p_189551_1_.setInteger("Tick", this.tickCounter);
      NBTTagList nbttaglist = new NBTTagList();

      for(Village village : this.villageList) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         village.writeVillageDataToNBT(nbttagcompound);
         nbttaglist.add((INBTBase)nbttagcompound);
      }

      p_189551_1_.setTag("Villages", nbttaglist);
      return p_189551_1_;
   }

   public static String fileNameForProvider(Dimension p_176062_0_) {
      return "villages" + p_176062_0_.getType().getSuffix();
   }
}
