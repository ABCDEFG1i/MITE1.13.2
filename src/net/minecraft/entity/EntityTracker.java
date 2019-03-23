package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.IAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTracker {
   private static final Logger LOGGER = LogManager.getLogger();
   private final WorldServer world;
   private final Set<EntityTrackerEntry> entries = Sets.newHashSet();
   private final IntHashMap<EntityTrackerEntry> trackedEntityHashTable = new IntHashMap<>();
   private int maxTrackingDistanceThreshold;

   public EntityTracker(WorldServer p_i1516_1_) {
      this.world = p_i1516_1_;
      this.maxTrackingDistanceThreshold = p_i1516_1_.getServer().getPlayerList().getEntityViewDistance();
   }

   public static long getPositionLong(double p_187253_0_) {
      return MathHelper.lfloor(p_187253_0_ * 4096.0D);
   }

   @OnlyIn(Dist.CLIENT)
   public static void updateServerPosition(Entity p_187254_0_, double p_187254_1_, double p_187254_3_, double p_187254_5_) {
      p_187254_0_.serverPosX = getPositionLong(p_187254_1_);
      p_187254_0_.serverPosY = getPositionLong(p_187254_3_);
      p_187254_0_.serverPosZ = getPositionLong(p_187254_5_);
   }

   public void track(Entity p_72786_1_) {
      if (p_72786_1_ instanceof EntityPlayerMP) {
         this.track(p_72786_1_, 512, 2);
         EntityPlayerMP entityplayermp = (EntityPlayerMP)p_72786_1_;

         for(EntityTrackerEntry entitytrackerentry : this.entries) {
            if (entitytrackerentry.getTrackedEntity() != entityplayermp) {
               entitytrackerentry.updatePlayerEntity(entityplayermp);
            }
         }
      } else if (p_72786_1_ instanceof EntityFishHook) {
         this.track(p_72786_1_, 64, 5, true);
      } else if (p_72786_1_ instanceof EntityArrow) {
         this.track(p_72786_1_, 64, 20, false);
      } else if (p_72786_1_ instanceof EntitySmallFireball) {
         this.track(p_72786_1_, 64, 10, false);
      } else if (p_72786_1_ instanceof EntityFireball) {
         this.track(p_72786_1_, 64, 10, true);
      } else if (p_72786_1_ instanceof EntitySnowball) {
         this.track(p_72786_1_, 64, 10, true);
      } else if (p_72786_1_ instanceof EntityLlamaSpit) {
         this.track(p_72786_1_, 64, 10, false);
      } else if (p_72786_1_ instanceof EntityEnderPearl) {
         this.track(p_72786_1_, 64, 10, true);
      } else if (p_72786_1_ instanceof EntityEnderEye) {
         this.track(p_72786_1_, 64, 4, true);
      } else if (p_72786_1_ instanceof EntityEgg) {
         this.track(p_72786_1_, 64, 10, true);
      } else if (p_72786_1_ instanceof EntityPotion) {
         this.track(p_72786_1_, 64, 10, true);
      } else if (p_72786_1_ instanceof EntityExpBottle) {
         this.track(p_72786_1_, 64, 10, true);
      } else if (p_72786_1_ instanceof EntityFireworkRocket) {
         this.track(p_72786_1_, 64, 10, true);
      } else if (p_72786_1_ instanceof EntityItem) {
         this.track(p_72786_1_, 64, 20, true);
      } else if (p_72786_1_ instanceof EntityMinecart) {
         this.track(p_72786_1_, 80, 3, true);
      } else if (p_72786_1_ instanceof EntityBoat) {
         this.track(p_72786_1_, 80, 3, true);
      } else if (p_72786_1_ instanceof EntitySquid) {
         this.track(p_72786_1_, 64, 3, true);
      } else if (p_72786_1_ instanceof EntityWither) {
         this.track(p_72786_1_, 80, 3, false);
      } else if (p_72786_1_ instanceof EntityShulkerBullet) {
         this.track(p_72786_1_, 80, 3, true);
      } else if (p_72786_1_ instanceof EntityBat) {
         this.track(p_72786_1_, 80, 3, false);
      } else if (p_72786_1_ instanceof EntityDragon) {
         this.track(p_72786_1_, 160, 3, true);
      } else if (p_72786_1_ instanceof IAnimal) {
         this.track(p_72786_1_, 80, 3, true);
      } else if (p_72786_1_ instanceof EntityTNTPrimed) {
         this.track(p_72786_1_, 160, 10, true);
      } else if (p_72786_1_ instanceof EntityFallingBlock) {
         this.track(p_72786_1_, 160, 20, true);
      } else if (p_72786_1_ instanceof EntityHanging) {
         this.track(p_72786_1_, 160, Integer.MAX_VALUE, false);
      } else if (p_72786_1_ instanceof EntityArmorStand) {
         this.track(p_72786_1_, 160, 3, true);
      } else if (p_72786_1_ instanceof EntityXPOrb) {
         this.track(p_72786_1_, 160, 20, true);
      } else if (p_72786_1_ instanceof EntityAreaEffectCloud) {
         this.track(p_72786_1_, 160, Integer.MAX_VALUE, true);
      } else if (p_72786_1_ instanceof EntityEnderCrystal) {
         this.track(p_72786_1_, 256, Integer.MAX_VALUE, false);
      } else if (p_72786_1_ instanceof EntityEvokerFangs) {
         this.track(p_72786_1_, 160, 2, false);
      }

   }

   public void track(Entity p_72791_1_, int p_72791_2_, int p_72791_3_) {
      this.track(p_72791_1_, p_72791_2_, p_72791_3_, false);
   }

   public void track(Entity p_72785_1_, int p_72785_2_, int p_72785_3_, boolean p_72785_4_) {
      try {
         if (this.trackedEntityHashTable.containsItem(p_72785_1_.getEntityId())) {
            throw new IllegalStateException("Entity is already tracked!");
         }

         EntityTrackerEntry entitytrackerentry = new EntityTrackerEntry(p_72785_1_, p_72785_2_, this.maxTrackingDistanceThreshold, p_72785_3_, p_72785_4_);
         this.entries.add(entitytrackerentry);
         this.trackedEntityHashTable.addKey(p_72785_1_.getEntityId(), entitytrackerentry);
         entitytrackerentry.updatePlayers(this.world.playerEntities);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding entity to track");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity To Track");
         crashreportcategory.addCrashSection("Tracking range", p_72785_2_ + " blocks");
         crashreportcategory.addDetail("Update interval", () -> {
            String s = "Once per " + p_72785_3_ + " ticks";
            if (p_72785_3_ == Integer.MAX_VALUE) {
               s = "Maximum (" + s + ")";
            }

            return s;
         });
         p_72785_1_.fillCrashReport(crashreportcategory);
         this.trackedEntityHashTable.lookup(p_72785_1_.getEntityId()).getTrackedEntity().fillCrashReport(crashreport.makeCategory("Entity That Is Already Tracked"));

         try {
            throw new ReportedException(crashreport);
         } catch (ReportedException reportedexception) {
            LOGGER.error("\"Silently\" catching entity tracking error.", (Throwable)reportedexception);
         }
      }

   }

   public void untrack(Entity p_72790_1_) {
      if (p_72790_1_ instanceof EntityPlayerMP) {
         EntityPlayerMP entityplayermp = (EntityPlayerMP)p_72790_1_;

         for(EntityTrackerEntry entitytrackerentry : this.entries) {
            entitytrackerentry.removeFromTrackedPlayers(entityplayermp);
         }
      }

      EntityTrackerEntry entitytrackerentry1 = this.trackedEntityHashTable.removeObject(p_72790_1_.getEntityId());
      if (entitytrackerentry1 != null) {
         this.entries.remove(entitytrackerentry1);
         entitytrackerentry1.sendDestroyEntityPacketToTrackedPlayers();
      }

   }

   public void tick() {
      List<EntityPlayerMP> list = Lists.newArrayList();

      for(EntityTrackerEntry entitytrackerentry : this.entries) {
         entitytrackerentry.tick(this.world.playerEntities);
         if (entitytrackerentry.playerEntitiesUpdated) {
            Entity entity = entitytrackerentry.getTrackedEntity();
            if (entity instanceof EntityPlayerMP) {
               list.add((EntityPlayerMP)entity);
            }
         }
      }

      for(int i = 0; i < list.size(); ++i) {
         EntityPlayerMP entityplayermp = list.get(i);

         for(EntityTrackerEntry entitytrackerentry1 : this.entries) {
            if (entitytrackerentry1.getTrackedEntity() != entityplayermp) {
               entitytrackerentry1.updatePlayerEntity(entityplayermp);
            }
         }
      }

   }

   public void updateVisibility(EntityPlayerMP p_180245_1_) {
      for(EntityTrackerEntry entitytrackerentry : this.entries) {
         if (entitytrackerentry.getTrackedEntity() == p_180245_1_) {
            entitytrackerentry.updatePlayers(this.world.playerEntities);
         } else {
            entitytrackerentry.updatePlayerEntity(p_180245_1_);
         }
      }

   }

   public void sendToTracking(Entity p_151247_1_, Packet<?> p_151247_2_) {
      EntityTrackerEntry entitytrackerentry = this.trackedEntityHashTable.lookup(p_151247_1_.getEntityId());
      if (entitytrackerentry != null) {
         entitytrackerentry.sendPacketToTrackedPlayers(p_151247_2_);
      }

   }

   public void sendToTrackingAndSelf(Entity p_151248_1_, Packet<?> p_151248_2_) {
      EntityTrackerEntry entitytrackerentry = this.trackedEntityHashTable.lookup(p_151248_1_.getEntityId());
      if (entitytrackerentry != null) {
         entitytrackerentry.sendToTrackingAndSelf(p_151248_2_);
      }

   }

   public void removePlayerFromTrackers(EntityPlayerMP p_72787_1_) {
      for(EntityTrackerEntry entitytrackerentry : this.entries) {
         entitytrackerentry.removeTrackedPlayerSymmetric(p_72787_1_);
      }

   }

   public void sendLeashedEntitiesInChunk(EntityPlayerMP p_85172_1_, Chunk p_85172_2_) {
      List<Entity> list = Lists.newArrayList();
      List<Entity> list1 = Lists.newArrayList();

      for(EntityTrackerEntry entitytrackerentry : this.entries) {
         Entity entity = entitytrackerentry.getTrackedEntity();
         if (entity != p_85172_1_ && entity.chunkCoordX == p_85172_2_.x && entity.chunkCoordZ == p_85172_2_.z) {
            entitytrackerentry.updatePlayerEntity(p_85172_1_);
            if (entity instanceof EntityLiving && ((EntityLiving)entity).getLeashHolder() != null) {
               list.add(entity);
            }

            if (!entity.getPassengers().isEmpty()) {
               list1.add(entity);
            }
         }
      }

      if (!list.isEmpty()) {
         for(Entity entity1 : list) {
            p_85172_1_.connection.sendPacket(new SPacketEntityAttach(entity1, ((EntityLiving)entity1).getLeashHolder()));
         }
      }

      if (!list1.isEmpty()) {
         for(Entity entity2 : list1) {
            p_85172_1_.connection.sendPacket(new SPacketSetPassengers(entity2));
         }
      }

   }

   public void setViewDistance(int p_187252_1_) {
      this.maxTrackingDistanceThreshold = (p_187252_1_ - 1) * 16;

      for(EntityTrackerEntry entitytrackerentry : this.entries) {
         entitytrackerentry.setMaxRange(this.maxTrackingDistanceThreshold);
      }

   }
}
