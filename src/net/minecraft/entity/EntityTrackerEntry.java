package net.minecraft.entity;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.IAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.projectile.EntityTrident;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTrackerEntry {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Entity trackedEntity;
   private final int range;
   private int maxRange;
   private final int updateInterval;
   private long encodedPosX;
   private long encodedPosY;
   private long encodedPosZ;
   private int encodedRotationYaw;
   private int encodedRotationPitch;
   private int lastHeadMotion;
   private double lastTrackedEntityMotionX;
   private double lastTrackedEntityMotionY;
   private double motionZ;
   public int updateCounter;
   private double lastTrackedEntityPosX;
   private double lastTrackedEntityPosY;
   private double lastTrackedEntityPosZ;
   private boolean updatedPlayerVisibility;
   private final boolean sendVelocityUpdates;
   private int ticksSinceLastForcedTeleport;
   private List<Entity> passengers = Collections.emptyList();
   private boolean ridingEntity;
   private boolean onGround;
   public boolean playerEntitiesUpdated;
   public final Set<EntityPlayerMP> trackingPlayers = Sets.newHashSet();

   public EntityTrackerEntry(Entity p_i46837_1_, int p_i46837_2_, int p_i46837_3_, int p_i46837_4_, boolean p_i46837_5_) {
      this.trackedEntity = p_i46837_1_;
      this.range = p_i46837_2_;
      this.maxRange = p_i46837_3_;
      this.updateInterval = p_i46837_4_;
      this.sendVelocityUpdates = p_i46837_5_;
      this.encodedPosX = EntityTracker.getPositionLong(p_i46837_1_.posX);
      this.encodedPosY = EntityTracker.getPositionLong(p_i46837_1_.posY);
      this.encodedPosZ = EntityTracker.getPositionLong(p_i46837_1_.posZ);
      this.encodedRotationYaw = MathHelper.floor(p_i46837_1_.rotationYaw * 256.0F / 360.0F);
      this.encodedRotationPitch = MathHelper.floor(p_i46837_1_.rotationPitch * 256.0F / 360.0F);
      this.lastHeadMotion = MathHelper.floor(p_i46837_1_.getRotationYawHead() * 256.0F / 360.0F);
      this.onGround = p_i46837_1_.onGround;
   }

   public boolean equals(Object p_equals_1_) {
      if (p_equals_1_ instanceof EntityTrackerEntry) {
         return ((EntityTrackerEntry)p_equals_1_).trackedEntity.getEntityId() == this.trackedEntity.getEntityId();
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.trackedEntity.getEntityId();
   }

   public void tick(List<EntityPlayer> p_73122_1_) {
      this.playerEntitiesUpdated = false;
      if (!this.updatedPlayerVisibility || this.trackedEntity.getDistanceSq(this.lastTrackedEntityPosX, this.lastTrackedEntityPosY, this.lastTrackedEntityPosZ) > 16.0D) {
         this.lastTrackedEntityPosX = this.trackedEntity.posX;
         this.lastTrackedEntityPosY = this.trackedEntity.posY;
         this.lastTrackedEntityPosZ = this.trackedEntity.posZ;
         this.updatedPlayerVisibility = true;
         this.playerEntitiesUpdated = true;
         this.updatePlayers(p_73122_1_);
      }

      List<Entity> list = this.trackedEntity.getPassengers();
      if (!list.equals(this.passengers)) {
         this.passengers = list;
         this.sendPacketToTrackedPlayers(new SPacketSetPassengers(this.trackedEntity));
      }

      if (this.trackedEntity instanceof EntityItemFrame && this.updateCounter % 10 == 0) {
         EntityItemFrame entityitemframe = (EntityItemFrame)this.trackedEntity;
         ItemStack itemstack = entityitemframe.getDisplayedItem();
         if (itemstack.getItem() instanceof ItemMap) {
            MapData mapdata = ItemMap.getMapData(itemstack, this.trackedEntity.world);

            for(EntityPlayer entityplayer : p_73122_1_) {
               EntityPlayerMP entityplayermp = (EntityPlayerMP)entityplayer;
               mapdata.updateVisiblePlayers(entityplayermp, itemstack);
               Packet<?> packet = ((ItemMap)itemstack.getItem()).getUpdatePacket(itemstack, this.trackedEntity.world, entityplayermp);
               if (packet != null) {
                  entityplayermp.connection.sendPacket(packet);
               }
            }
         }

         this.sendMetadata();
      }

      if (this.updateCounter % this.updateInterval == 0 || this.trackedEntity.isAirBorne || this.trackedEntity.getDataManager().isDirty()) {
         if (this.trackedEntity.isRiding()) {
            int j1 = MathHelper.floor(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
            int l1 = MathHelper.floor(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
            boolean flag3 = Math.abs(j1 - this.encodedRotationYaw) >= 1 || Math.abs(l1 - this.encodedRotationPitch) >= 1;
            if (flag3) {
               this.sendPacketToTrackedPlayers(new SPacketEntity.Look(this.trackedEntity.getEntityId(), (byte)j1, (byte)l1, this.trackedEntity.onGround));
               this.encodedRotationYaw = j1;
               this.encodedRotationPitch = l1;
            }

            this.encodedPosX = EntityTracker.getPositionLong(this.trackedEntity.posX);
            this.encodedPosY = EntityTracker.getPositionLong(this.trackedEntity.posY);
            this.encodedPosZ = EntityTracker.getPositionLong(this.trackedEntity.posZ);
            this.sendMetadata();
            this.ridingEntity = true;
         } else {
            ++this.ticksSinceLastForcedTeleport;
            long i1 = EntityTracker.getPositionLong(this.trackedEntity.posX);
            long i2 = EntityTracker.getPositionLong(this.trackedEntity.posY);
            long j2 = EntityTracker.getPositionLong(this.trackedEntity.posZ);
            int k2 = MathHelper.floor(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
            int i = MathHelper.floor(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
            long j = i1 - this.encodedPosX;
            long k = i2 - this.encodedPosY;
            long l = j2 - this.encodedPosZ;
            Packet<?> packet1 = null;
            boolean flag = j * j + k * k + l * l >= 128L || this.updateCounter % 60 == 0;
            boolean flag1 = Math.abs(k2 - this.encodedRotationYaw) >= 1 || Math.abs(i - this.encodedRotationPitch) >= 1;
            if (this.updateCounter > 0 || this.trackedEntity instanceof EntityArrow) {
               if (j >= -32768L && j < 32768L && k >= -32768L && k < 32768L && l >= -32768L && l < 32768L && this.ticksSinceLastForcedTeleport <= 400 && !this.ridingEntity && this.onGround == this.trackedEntity.onGround) {
                  if ((!flag || !flag1) && !(this.trackedEntity instanceof EntityArrow)) {
                     if (flag) {
                        packet1 = new SPacketEntity.RelMove(this.trackedEntity.getEntityId(), j, k, l, this.trackedEntity.onGround);
                     } else if (flag1) {
                        packet1 = new SPacketEntity.Look(this.trackedEntity.getEntityId(), (byte)k2, (byte)i, this.trackedEntity.onGround);
                     }
                  } else {
                     packet1 = new SPacketEntity.Move(this.trackedEntity.getEntityId(), j, k, l, (byte)k2, (byte)i, this.trackedEntity.onGround);
                  }
               } else {
                  this.onGround = this.trackedEntity.onGround;
                  this.ticksSinceLastForcedTeleport = 0;
                  this.resetPlayerVisibility();
                  packet1 = new SPacketEntityTeleport(this.trackedEntity);
               }
            }

            boolean flag2 = this.sendVelocityUpdates || this.trackedEntity.isAirBorne;
            if (this.trackedEntity instanceof EntityLivingBase && ((EntityLivingBase)this.trackedEntity).isElytraFlying()) {
               flag2 = true;
            }

            if (flag2 && this.updateCounter > 0) {
               double d0 = this.trackedEntity.motionX - this.lastTrackedEntityMotionX;
               double d1 = this.trackedEntity.motionY - this.lastTrackedEntityMotionY;
               double d2 = this.trackedEntity.motionZ - this.motionZ;
               double d3 = 0.02D;
               double d4 = d0 * d0 + d1 * d1 + d2 * d2;
               if (d4 > 4.0E-4D || d4 > 0.0D && this.trackedEntity.motionX == 0.0D && this.trackedEntity.motionY == 0.0D && this.trackedEntity.motionZ == 0.0D) {
                  this.lastTrackedEntityMotionX = this.trackedEntity.motionX;
                  this.lastTrackedEntityMotionY = this.trackedEntity.motionY;
                  this.motionZ = this.trackedEntity.motionZ;
                  this.sendPacketToTrackedPlayers(new SPacketEntityVelocity(this.trackedEntity.getEntityId(), this.lastTrackedEntityMotionX, this.lastTrackedEntityMotionY, this.motionZ));
               }
            }

            if (packet1 != null) {
               this.sendPacketToTrackedPlayers(packet1);
            }

            this.sendMetadata();
            if (flag) {
               this.encodedPosX = i1;
               this.encodedPosY = i2;
               this.encodedPosZ = j2;
            }

            if (flag1) {
               this.encodedRotationYaw = k2;
               this.encodedRotationPitch = i;
            }

            this.ridingEntity = false;
         }

         int k1 = MathHelper.floor(this.trackedEntity.getRotationYawHead() * 256.0F / 360.0F);
         if (Math.abs(k1 - this.lastHeadMotion) >= 1) {
            this.sendPacketToTrackedPlayers(new SPacketEntityHeadLook(this.trackedEntity, (byte)k1));
            this.lastHeadMotion = k1;
         }

         this.trackedEntity.isAirBorne = false;
      }

      ++this.updateCounter;
      if (this.trackedEntity.velocityChanged) {
         this.sendToTrackingAndSelf(new SPacketEntityVelocity(this.trackedEntity));
         this.trackedEntity.velocityChanged = false;
      }

   }

   private void sendMetadata() {
      EntityDataManager entitydatamanager = this.trackedEntity.getDataManager();
      if (entitydatamanager.isDirty()) {
         this.sendToTrackingAndSelf(new SPacketEntityMetadata(this.trackedEntity.getEntityId(), entitydatamanager, false));
      }

      if (this.trackedEntity instanceof EntityLivingBase) {
         AttributeMap attributemap = (AttributeMap)((EntityLivingBase)this.trackedEntity).getAttributeMap();
         Set<IAttributeInstance> set = attributemap.getDirtyInstances();
         if (!set.isEmpty()) {
            this.sendToTrackingAndSelf(new SPacketEntityProperties(this.trackedEntity.getEntityId(), set));
         }

         set.clear();
      }

   }

   public void sendPacketToTrackedPlayers(Packet<?> p_151259_1_) {
      for(EntityPlayerMP entityplayermp : this.trackingPlayers) {
         entityplayermp.connection.sendPacket(p_151259_1_);
      }

   }

   public void sendToTrackingAndSelf(Packet<?> p_151261_1_) {
      this.sendPacketToTrackedPlayers(p_151261_1_);
      if (this.trackedEntity instanceof EntityPlayerMP) {
         ((EntityPlayerMP)this.trackedEntity).connection.sendPacket(p_151261_1_);
      }

   }

   public void sendDestroyEntityPacketToTrackedPlayers() {
      for(EntityPlayerMP entityplayermp : this.trackingPlayers) {
         this.trackedEntity.removeTrackingPlayer(entityplayermp);
         entityplayermp.removeEntity(this.trackedEntity);
      }

   }

   public void removeFromTrackedPlayers(EntityPlayerMP p_73118_1_) {
      if (this.trackingPlayers.contains(p_73118_1_)) {
         this.trackedEntity.removeTrackingPlayer(p_73118_1_);
         p_73118_1_.removeEntity(this.trackedEntity);
         this.trackingPlayers.remove(p_73118_1_);
      }

   }

   public void updatePlayerEntity(EntityPlayerMP p_73117_1_) {
      if (p_73117_1_ != this.trackedEntity) {
         if (this.isVisibleTo(p_73117_1_)) {
            if (!this.trackingPlayers.contains(p_73117_1_) && (this.isPlayerWatchingThisChunk(p_73117_1_) || this.trackedEntity.forceSpawn)) {
               this.trackingPlayers.add(p_73117_1_);
               Packet<?> packet = this.createSpawnPacket();
               p_73117_1_.connection.sendPacket(packet);
               if (!this.trackedEntity.getDataManager().isEmpty()) {
                  p_73117_1_.connection.sendPacket(new SPacketEntityMetadata(this.trackedEntity.getEntityId(), this.trackedEntity.getDataManager(), true));
               }

               boolean flag = this.sendVelocityUpdates;
               if (this.trackedEntity instanceof EntityLivingBase) {
                  AttributeMap attributemap = (AttributeMap)((EntityLivingBase)this.trackedEntity).getAttributeMap();
                  Collection<IAttributeInstance> collection = attributemap.getWatchedAttributes();
                  if (!collection.isEmpty()) {
                     p_73117_1_.connection.sendPacket(new SPacketEntityProperties(this.trackedEntity.getEntityId(), collection));
                  }

                  if (((EntityLivingBase)this.trackedEntity).isElytraFlying()) {
                     flag = true;
                  }
               }

               this.lastTrackedEntityMotionX = this.trackedEntity.motionX;
               this.lastTrackedEntityMotionY = this.trackedEntity.motionY;
               this.motionZ = this.trackedEntity.motionZ;
               if (flag && !(packet instanceof SPacketSpawnMob)) {
                  p_73117_1_.connection.sendPacket(new SPacketEntityVelocity(this.trackedEntity.getEntityId(), this.trackedEntity.motionX, this.trackedEntity.motionY, this.trackedEntity.motionZ));
               }

               if (this.trackedEntity instanceof EntityLivingBase) {
                  for(EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values()) {
                     ItemStack itemstack = ((EntityLivingBase)this.trackedEntity).getItemStackFromSlot(entityequipmentslot);
                     if (!itemstack.isEmpty()) {
                        p_73117_1_.connection.sendPacket(new SPacketEntityEquipment(this.trackedEntity.getEntityId(), entityequipmentslot, itemstack));
                     }
                  }
               }

               if (this.trackedEntity instanceof EntityPlayer) {
                  EntityPlayer entityplayer = (EntityPlayer)this.trackedEntity;
                  if (entityplayer.isPlayerSleeping()) {
                     p_73117_1_.connection.sendPacket(new SPacketUseBed(entityplayer, new BlockPos(this.trackedEntity)));
                  }
               }

               if (this.trackedEntity instanceof EntityLivingBase) {
                  EntityLivingBase entitylivingbase = (EntityLivingBase)this.trackedEntity;

                  for(PotionEffect potioneffect : entitylivingbase.getActivePotionEffects()) {
                     p_73117_1_.connection.sendPacket(new SPacketEntityEffect(this.trackedEntity.getEntityId(), potioneffect));
                  }
               }

               if (!this.trackedEntity.getPassengers().isEmpty()) {
                  p_73117_1_.connection.sendPacket(new SPacketSetPassengers(this.trackedEntity));
               }

               if (this.trackedEntity.isRiding()) {
                  p_73117_1_.connection.sendPacket(new SPacketSetPassengers(this.trackedEntity.getRidingEntity()));
               }

               this.trackedEntity.addTrackingPlayer(p_73117_1_);
               p_73117_1_.addEntity(this.trackedEntity);
            }
         } else if (this.trackingPlayers.contains(p_73117_1_)) {
            this.trackingPlayers.remove(p_73117_1_);
            this.trackedEntity.removeTrackingPlayer(p_73117_1_);
            p_73117_1_.removeEntity(this.trackedEntity);
         }

      }
   }

   public boolean isVisibleTo(EntityPlayerMP p_180233_1_) {
      double d0 = p_180233_1_.posX - (double)this.encodedPosX / 4096.0D;
      double d1 = p_180233_1_.posZ - (double)this.encodedPosZ / 4096.0D;
      int i = Math.min(this.range, this.maxRange);
      return d0 >= (double)(-i) && d0 <= (double)i && d1 >= (double)(-i) && d1 <= (double)i && this.trackedEntity.isSpectatedByPlayer(p_180233_1_);
   }

   private boolean isPlayerWatchingThisChunk(EntityPlayerMP p_73121_1_) {
      return p_73121_1_.getServerWorld().getPlayerChunkMap().isPlayerWatchingChunk(p_73121_1_, this.trackedEntity.chunkCoordX, this.trackedEntity.chunkCoordZ);
   }

   public void updatePlayers(List<EntityPlayer> p_73125_1_) {
      for(int i = 0; i < p_73125_1_.size(); ++i) {
         this.updatePlayerEntity((EntityPlayerMP)p_73125_1_.get(i));
      }

   }

   private Packet<?> createSpawnPacket() {
      if (this.trackedEntity.isDead) {
         LOGGER.warn("Fetching addPacket for removed entity");
      }

      if (this.trackedEntity instanceof EntityPlayerMP) {
         return new SPacketSpawnPlayer((EntityPlayer)this.trackedEntity);
      } else if (this.trackedEntity instanceof IAnimal) {
         this.lastHeadMotion = MathHelper.floor(this.trackedEntity.getRotationYawHead() * 256.0F / 360.0F);
         return new SPacketSpawnMob((EntityLivingBase)this.trackedEntity);
      } else if (this.trackedEntity instanceof EntityPainting) {
         return new SPacketSpawnPainting((EntityPainting)this.trackedEntity);
      } else if (this.trackedEntity instanceof EntityItem) {
         return new SPacketSpawnObject(this.trackedEntity, 2, 1);
      } else if (this.trackedEntity instanceof EntityMinecart) {
         EntityMinecart entityminecart = (EntityMinecart)this.trackedEntity;
         return new SPacketSpawnObject(this.trackedEntity, 10, entityminecart.getMinecartType().getId());
      } else if (this.trackedEntity instanceof EntityBoat) {
         return new SPacketSpawnObject(this.trackedEntity, 1);
      } else if (this.trackedEntity instanceof EntityXPOrb) {
         return new SPacketSpawnExperienceOrb((EntityXPOrb)this.trackedEntity);
      } else if (this.trackedEntity instanceof EntityFishHook) {
         Entity entity3 = ((EntityFishHook)this.trackedEntity).getAngler();
         return new SPacketSpawnObject(this.trackedEntity, 90, entity3 == null ? this.trackedEntity.getEntityId() : entity3.getEntityId());
      } else if (this.trackedEntity instanceof EntitySpectralArrow) {
         Entity entity2 = ((EntitySpectralArrow)this.trackedEntity).func_212360_k();
         return new SPacketSpawnObject(this.trackedEntity, 91, 1 + (entity2 == null ? this.trackedEntity.getEntityId() : entity2.getEntityId()));
      } else if (this.trackedEntity instanceof EntityTippedArrow) {
         Entity entity1 = ((EntityArrow)this.trackedEntity).func_212360_k();
         return new SPacketSpawnObject(this.trackedEntity, 60, 1 + (entity1 == null ? this.trackedEntity.getEntityId() : entity1.getEntityId()));
      } else if (this.trackedEntity instanceof EntitySnowball) {
         return new SPacketSpawnObject(this.trackedEntity, 61);
      } else if (this.trackedEntity instanceof EntityTrident) {
         Entity entity = ((EntityArrow)this.trackedEntity).func_212360_k();
         return new SPacketSpawnObject(this.trackedEntity, 94, 1 + (entity == null ? this.trackedEntity.getEntityId() : entity.getEntityId()));
      } else if (this.trackedEntity instanceof EntityLlamaSpit) {
         return new SPacketSpawnObject(this.trackedEntity, 68);
      } else if (this.trackedEntity instanceof EntityPotion) {
         return new SPacketSpawnObject(this.trackedEntity, 73);
      } else if (this.trackedEntity instanceof EntityExpBottle) {
         return new SPacketSpawnObject(this.trackedEntity, 75);
      } else if (this.trackedEntity instanceof EntityEnderPearl) {
         return new SPacketSpawnObject(this.trackedEntity, 65);
      } else if (this.trackedEntity instanceof EntityEnderEye) {
         return new SPacketSpawnObject(this.trackedEntity, 72);
      } else if (this.trackedEntity instanceof EntityFireworkRocket) {
         return new SPacketSpawnObject(this.trackedEntity, 76);
      } else if (this.trackedEntity instanceof EntityFireball) {
         EntityFireball entityfireball = (EntityFireball)this.trackedEntity;
         int i = 63;
         if (this.trackedEntity instanceof EntitySmallFireball) {
            i = 64;
         } else if (this.trackedEntity instanceof EntityDragonFireball) {
            i = 93;
         } else if (this.trackedEntity instanceof EntityWitherSkull) {
            i = 66;
         }

         SPacketSpawnObject spacketspawnobject;
         if (entityfireball.shootingEntity == null) {
            spacketspawnobject = new SPacketSpawnObject(this.trackedEntity, i, 0);
         } else {
            spacketspawnobject = new SPacketSpawnObject(this.trackedEntity, i, ((EntityFireball)this.trackedEntity).shootingEntity.getEntityId());
         }

         spacketspawnobject.setSpeedX((int)(entityfireball.accelerationX * 8000.0D));
         spacketspawnobject.setSpeedY((int)(entityfireball.accelerationY * 8000.0D));
         spacketspawnobject.setSpeedZ((int)(entityfireball.accelerationZ * 8000.0D));
         return spacketspawnobject;
      } else if (this.trackedEntity instanceof EntityShulkerBullet) {
         SPacketSpawnObject spacketspawnobject1 = new SPacketSpawnObject(this.trackedEntity, 67, 0);
         spacketspawnobject1.setSpeedX((int)(this.trackedEntity.motionX * 8000.0D));
         spacketspawnobject1.setSpeedY((int)(this.trackedEntity.motionY * 8000.0D));
         spacketspawnobject1.setSpeedZ((int)(this.trackedEntity.motionZ * 8000.0D));
         return spacketspawnobject1;
      } else if (this.trackedEntity instanceof EntityEgg) {
         return new SPacketSpawnObject(this.trackedEntity, 62);
      } else if (this.trackedEntity instanceof EntityEvokerFangs) {
         return new SPacketSpawnObject(this.trackedEntity, 79);
      } else if (this.trackedEntity instanceof EntityTNTPrimed) {
         return new SPacketSpawnObject(this.trackedEntity, 50);
      } else if (this.trackedEntity instanceof EntityEnderCrystal) {
         return new SPacketSpawnObject(this.trackedEntity, 51);
      } else if (this.trackedEntity instanceof EntityFallingBlock) {
         EntityFallingBlock entityfallingblock = (EntityFallingBlock)this.trackedEntity;
         return new SPacketSpawnObject(this.trackedEntity, 70, Block.getStateId(entityfallingblock.func_195054_l()));
      } else if (this.trackedEntity instanceof EntityArmorStand) {
         return new SPacketSpawnObject(this.trackedEntity, 78);
      } else if (this.trackedEntity instanceof EntityItemFrame) {
         EntityItemFrame entityitemframe = (EntityItemFrame)this.trackedEntity;
         return new SPacketSpawnObject(this.trackedEntity, 71, entityitemframe.facingDirection.getIndex(), entityitemframe.getHangingPosition());
      } else if (this.trackedEntity instanceof EntityLeashKnot) {
         EntityLeashKnot entityleashknot = (EntityLeashKnot)this.trackedEntity;
         return new SPacketSpawnObject(this.trackedEntity, 77, 0, entityleashknot.getHangingPosition());
      } else if (this.trackedEntity instanceof EntityAreaEffectCloud) {
         return new SPacketSpawnObject(this.trackedEntity, 3);
      } else {
         throw new IllegalArgumentException("Don't know how to add " + this.trackedEntity.getClass() + "!");
      }
   }

   public void removeTrackedPlayerSymmetric(EntityPlayerMP p_73123_1_) {
      if (this.trackingPlayers.contains(p_73123_1_)) {
         this.trackingPlayers.remove(p_73123_1_);
         this.trackedEntity.removeTrackingPlayer(p_73123_1_);
         p_73123_1_.removeEntity(this.trackedEntity);
      }

   }

   public Entity getTrackedEntity() {
      return this.trackedEntity;
   }

   public void setMaxRange(int p_187259_1_) {
      this.maxRange = p_187259_1_;
   }

   public void resetPlayerVisibility() {
      this.updatedPlayerVisibility = false;
   }
}
