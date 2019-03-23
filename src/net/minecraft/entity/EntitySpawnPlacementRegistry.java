package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.gen.Heightmap;

public class EntitySpawnPlacementRegistry {
   private static final Map<EntityType<?>, EntitySpawnPlacementRegistry.Entry> REGISTRY = Maps.newHashMap();

   private static void register(EntityType<?> p_209343_0_, EntitySpawnPlacementRegistry.SpawnPlacementType p_209343_1_, Heightmap.Type p_209343_2_) {
      register(p_209343_0_, p_209343_1_, p_209343_2_, (Tag<Block>)null);
   }

   public static void register(EntityType<?> p_209346_0_, EntitySpawnPlacementRegistry.SpawnPlacementType p_209346_1_, Heightmap.Type p_209346_2_, @Nullable Tag<Block> p_209346_3_) {
      REGISTRY.put(p_209346_0_, new EntitySpawnPlacementRegistry.Entry(p_209346_2_, p_209346_1_, p_209346_3_));
   }

   @Nullable
   public static EntitySpawnPlacementRegistry.SpawnPlacementType getPlacementType(EntityType<? extends EntityLiving> p_209344_0_) {
      EntitySpawnPlacementRegistry.Entry entityspawnplacementregistry$entry = REGISTRY.get(p_209344_0_);
      return entityspawnplacementregistry$entry == null ? null : entityspawnplacementregistry$entry.placementType;
   }

   public static Heightmap.Type func_209342_b(@Nullable EntityType<? extends EntityLiving> p_209342_0_) {
      EntitySpawnPlacementRegistry.Entry entityspawnplacementregistry$entry = REGISTRY.get(p_209342_0_);
      return entityspawnplacementregistry$entry == null ? Heightmap.Type.MOTION_BLOCKING_NO_LEAVES : entityspawnplacementregistry$entry.type;
   }

   public static boolean func_209345_a(EntityType<? extends EntityLiving> p_209345_0_, IBlockState p_209345_1_) {
      EntitySpawnPlacementRegistry.Entry entityspawnplacementregistry$entry = REGISTRY.get(p_209345_0_);
      if (entityspawnplacementregistry$entry == null) {
         return false;
      } else {
         return entityspawnplacementregistry$entry.spawnBlockTag != null && p_209345_1_.isIn(entityspawnplacementregistry$entry.spawnBlockTag);
      }
   }

   static {
      register(EntityType.COD, EntitySpawnPlacementRegistry.SpawnPlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.DOLPHIN, EntitySpawnPlacementRegistry.SpawnPlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.DROWNED, EntitySpawnPlacementRegistry.SpawnPlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.GUARDIAN, EntitySpawnPlacementRegistry.SpawnPlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.PUFFERFISH, EntitySpawnPlacementRegistry.SpawnPlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.SALMON, EntitySpawnPlacementRegistry.SpawnPlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.SQUID, EntitySpawnPlacementRegistry.SpawnPlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.TROPICAL_FISH, EntitySpawnPlacementRegistry.SpawnPlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.OCELOT, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, BlockTags.LEAVES);
      register(EntityType.PARROT, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, BlockTags.LEAVES);
      register(EntityType.POLAR_BEAR, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BlockTags.ICE);
      register(EntityType.BAT, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.BLAZE, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.CAVE_SPIDER, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.CHICKEN, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.COW, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.CREEPER, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.DONKEY, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.ENDERMAN, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.ENDERMITE, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.ENDER_DRAGON, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.GHAST, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.GIANT, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.HORSE, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.HUSK, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.LLAMA, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.MAGMA_CUBE, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.MOOSHROOM, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.MULE, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.PIG, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.RABBIT, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.SHEEP, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.SILVERFISH, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.SKELETON, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.SKELETON_HORSE, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.SLIME, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.SNOW_GOLEM, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.SPIDER, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.STRAY, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.TURTLE, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.VILLAGER, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.IRON_GOLEM, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.WITCH, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.WITHER, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.WITHER_SKELETON, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.WOLF, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.ZOMBIE, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.ZOMBIE_HORSE, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.ZOMBIE_PIGMAN, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.ZOMBIE_VILLAGER, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
   }

   static class Entry {
      private final Heightmap.Type type;
      private final EntitySpawnPlacementRegistry.SpawnPlacementType placementType;
      @Nullable
      private final Tag<Block> spawnBlockTag;

      public Entry(Heightmap.Type p_i49397_1_, EntitySpawnPlacementRegistry.SpawnPlacementType p_i49397_2_, @Nullable Tag<Block> p_i49397_3_) {
         this.type = p_i49397_1_;
         this.placementType = p_i49397_2_;
         this.spawnBlockTag = p_i49397_3_;
      }
   }

   public static enum SpawnPlacementType {
      ON_GROUND,
      IN_WATER;
   }
}
