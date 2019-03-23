package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Particles;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.StringUtils;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MobSpawnerBaseLogic {
   private static final Logger LOGGER = LogManager.getLogger();
   private int spawnDelay = 20;
   private final List<WeightedSpawnerEntity> potentialSpawns = Lists.newArrayList();
   private WeightedSpawnerEntity spawnData = new WeightedSpawnerEntity();
   private double mobRotation;
   private double prevMobRotation;
   private int minSpawnDelay = 200;
   private int maxSpawnDelay = 800;
   private int spawnCount = 4;
   private Entity cachedEntity;
   private int maxNearbyEntities = 6;
   private int activatingRangeFromPlayer = 16;
   private int spawnRange = 4;

   @Nullable
   private ResourceLocation getEntityId() {
      String s = this.spawnData.getNbt().getString("id");

      try {
         return StringUtils.isNullOrEmpty(s) ? null : new ResourceLocation(s);
      } catch (ResourceLocationException var4) {
         BlockPos blockpos = this.getSpawnerPosition();
         LOGGER.warn("Invalid entity id '{}' at spawner {}:[{},{},{}]", s, this.getWorld().dimension.getType(), blockpos.getX(), blockpos.getY(), blockpos.getZ());
         return null;
      }
   }

   public void setEntityType(EntityType<?> p_200876_1_) {
      this.spawnData.getNbt().setString("id", IRegistry.field_212629_r.func_177774_c(p_200876_1_).toString());
   }

   private boolean isActivated() {
      BlockPos blockpos = this.getSpawnerPosition();
      return this.getWorld().func_212417_b((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D, (double)this.activatingRangeFromPlayer);
   }

   public void tick() {
      if (!this.isActivated()) {
         this.prevMobRotation = this.mobRotation;
      } else {
         BlockPos blockpos = this.getSpawnerPosition();
         if (this.getWorld().isRemote) {
            double d3 = (double)((float)blockpos.getX() + this.getWorld().rand.nextFloat());
            double d4 = (double)((float)blockpos.getY() + this.getWorld().rand.nextFloat());
            double d5 = (double)((float)blockpos.getZ() + this.getWorld().rand.nextFloat());
            this.getWorld().spawnParticle(Particles.SMOKE, d3, d4, d5, 0.0D, 0.0D, 0.0D);
            this.getWorld().spawnParticle(Particles.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);
            if (this.spawnDelay > 0) {
               --this.spawnDelay;
            }

            this.prevMobRotation = this.mobRotation;
            this.mobRotation = (this.mobRotation + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0D;
         } else {
            if (this.spawnDelay == -1) {
               this.resetTimer();
            }

            if (this.spawnDelay > 0) {
               --this.spawnDelay;
               return;
            }

            boolean flag = false;

            for(int i = 0; i < this.spawnCount; ++i) {
               NBTTagCompound nbttagcompound = this.spawnData.getNbt();
               NBTTagList nbttaglist = nbttagcompound.getTagList("Pos", 6);
               World world = this.getWorld();
               int j = nbttaglist.size();
               double d0 = j >= 1 ? nbttaglist.getDoubleAt(0) : (double)blockpos.getX() + (world.rand.nextDouble() - world.rand.nextDouble()) * (double)this.spawnRange + 0.5D;
               double d1 = j >= 2 ? nbttaglist.getDoubleAt(1) : (double)(blockpos.getY() + world.rand.nextInt(3) - 1);
               double d2 = j >= 3 ? nbttaglist.getDoubleAt(2) : (double)blockpos.getZ() + (world.rand.nextDouble() - world.rand.nextDouble()) * (double)this.spawnRange + 0.5D;
               Entity entity = AnvilChunkLoader.readWorldEntityPos(nbttagcompound, world, d0, d1, d2, false);
               if (entity == null) {
                  this.resetTimer();
                  return;
               }

               int k = world.getEntitiesWithinAABB(entity.getClass(), (new AxisAlignedBB((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), (double)(blockpos.getX() + 1), (double)(blockpos.getY() + 1), (double)(blockpos.getZ() + 1))).grow((double)this.spawnRange)).size();
               if (k >= this.maxNearbyEntities) {
                  this.resetTimer();
                  return;
               }

               EntityLiving entityliving = entity instanceof EntityLiving ? (EntityLiving)entity : null;
               entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, world.rand.nextFloat() * 360.0F, 0.0F);
               if (entityliving == null || entityliving.func_205020_a(world, true) && entityliving.isNotColliding()) {
                  if (this.spawnData.getNbt().getSize() == 1 && this.spawnData.getNbt().hasKey("id", 8) && entity instanceof EntityLiving) {
                     ((EntityLiving)entity).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData)null, (NBTTagCompound)null);
                  }

                  AnvilChunkLoader.spawnEntity(entity, world);
                  world.playEvent(2004, blockpos, 0);
                  if (entityliving != null) {
                     entityliving.spawnExplosionParticle();
                  }

                  flag = true;
               }
            }

            if (flag) {
               this.resetTimer();
            }
         }

      }
   }

   private void resetTimer() {
      if (this.maxSpawnDelay <= this.minSpawnDelay) {
         this.spawnDelay = this.minSpawnDelay;
      } else {
         int i = this.maxSpawnDelay - this.minSpawnDelay;
         this.spawnDelay = this.minSpawnDelay + this.getWorld().rand.nextInt(i);
      }

      if (!this.potentialSpawns.isEmpty()) {
         this.setNextSpawnData(WeightedRandom.getRandomItem(this.getWorld().rand, this.potentialSpawns));
      }

      this.broadcastEvent(1);
   }

   public void readFromNBT(NBTTagCompound p_98270_1_) {
      this.spawnDelay = p_98270_1_.getShort("Delay");
      this.potentialSpawns.clear();
      if (p_98270_1_.hasKey("SpawnPotentials", 9)) {
         NBTTagList nbttaglist = p_98270_1_.getTagList("SpawnPotentials", 10);

         for(int i = 0; i < nbttaglist.size(); ++i) {
            this.potentialSpawns.add(new WeightedSpawnerEntity(nbttaglist.getCompoundTagAt(i)));
         }
      }

      if (p_98270_1_.hasKey("SpawnData", 10)) {
         this.setNextSpawnData(new WeightedSpawnerEntity(1, p_98270_1_.getCompoundTag("SpawnData")));
      } else if (!this.potentialSpawns.isEmpty()) {
         this.setNextSpawnData(WeightedRandom.getRandomItem(this.getWorld().rand, this.potentialSpawns));
      }

      if (p_98270_1_.hasKey("MinSpawnDelay", 99)) {
         this.minSpawnDelay = p_98270_1_.getShort("MinSpawnDelay");
         this.maxSpawnDelay = p_98270_1_.getShort("MaxSpawnDelay");
         this.spawnCount = p_98270_1_.getShort("SpawnCount");
      }

      if (p_98270_1_.hasKey("MaxNearbyEntities", 99)) {
         this.maxNearbyEntities = p_98270_1_.getShort("MaxNearbyEntities");
         this.activatingRangeFromPlayer = p_98270_1_.getShort("RequiredPlayerRange");
      }

      if (p_98270_1_.hasKey("SpawnRange", 99)) {
         this.spawnRange = p_98270_1_.getShort("SpawnRange");
      }

      if (this.getWorld() != null) {
         this.cachedEntity = null;
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189530_1_) {
      ResourceLocation resourcelocation = this.getEntityId();
      if (resourcelocation == null) {
         return p_189530_1_;
      } else {
         p_189530_1_.setShort("Delay", (short)this.spawnDelay);
         p_189530_1_.setShort("MinSpawnDelay", (short)this.minSpawnDelay);
         p_189530_1_.setShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
         p_189530_1_.setShort("SpawnCount", (short)this.spawnCount);
         p_189530_1_.setShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
         p_189530_1_.setShort("RequiredPlayerRange", (short)this.activatingRangeFromPlayer);
         p_189530_1_.setShort("SpawnRange", (short)this.spawnRange);
         p_189530_1_.setTag("SpawnData", this.spawnData.getNbt().copy());
         NBTTagList nbttaglist = new NBTTagList();
         if (this.potentialSpawns.isEmpty()) {
            nbttaglist.add((INBTBase)this.spawnData.toCompoundTag());
         } else {
            for(WeightedSpawnerEntity weightedspawnerentity : this.potentialSpawns) {
               nbttaglist.add((INBTBase)weightedspawnerentity.toCompoundTag());
            }
         }

         p_189530_1_.setTag("SpawnPotentials", nbttaglist);
         return p_189530_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public Entity getCachedEntity() {
      if (this.cachedEntity == null) {
         this.cachedEntity = AnvilChunkLoader.readWorldEntity(this.spawnData.getNbt(), this.getWorld(), false);
         if (this.spawnData.getNbt().getSize() == 1 && this.spawnData.getNbt().hasKey("id", 8) && this.cachedEntity instanceof EntityLiving) {
            ((EntityLiving)this.cachedEntity).onInitialSpawn(this.getWorld().getDifficultyForLocation(new BlockPos(this.cachedEntity)), (IEntityLivingData)null, (NBTTagCompound)null);
         }
      }

      return this.cachedEntity;
   }

   public boolean setDelayToMin(int p_98268_1_) {
      if (p_98268_1_ == 1 && this.getWorld().isRemote) {
         this.spawnDelay = this.minSpawnDelay;
         return true;
      } else {
         return false;
      }
   }

   public void setNextSpawnData(WeightedSpawnerEntity p_184993_1_) {
      this.spawnData = p_184993_1_;
   }

   public abstract void broadcastEvent(int p_98267_1_);

   public abstract World getWorld();

   public abstract BlockPos getSpawnerPosition();

   @OnlyIn(Dist.CLIENT)
   public double getMobRotation() {
      return this.mobRotation;
   }

   @OnlyIn(Dist.CLIENT)
   public double getPrevMobRotation() {
      return this.prevMobRotation;
   }
}
