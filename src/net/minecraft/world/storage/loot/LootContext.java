package net.minecraft.world.storage.loot;

import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class LootContext {
   private final float luck;
   private final WorldServer world;
   private final LootTableManager lootTableManager;
   @Nullable
   private final Entity lootedEntity;
   @Nullable
   private final EntityPlayer player;
   @Nullable
   private final DamageSource damageSource;
   @Nullable
   private final BlockPos pos;
   private final Set<LootTable> lootTables = Sets.newLinkedHashSet();

   public LootContext(float p_i48874_1_, WorldServer p_i48874_2_, LootTableManager p_i48874_3_, @Nullable Entity p_i48874_4_, @Nullable EntityPlayer p_i48874_5_, @Nullable DamageSource p_i48874_6_, @Nullable BlockPos p_i48874_7_) {
      this.luck = p_i48874_1_;
      this.world = p_i48874_2_;
      this.lootTableManager = p_i48874_3_;
      this.lootedEntity = p_i48874_4_;
      this.player = p_i48874_5_;
      this.damageSource = p_i48874_6_;
      this.pos = p_i48874_7_;
   }

   @Nullable
   public Entity getLootedEntity() {
      return this.lootedEntity;
   }

   @Nullable
   public Entity getKillerPlayer() {
      return this.player;
   }

   @Nullable
   public Entity getKiller() {
      return this.damageSource == null ? null : this.damageSource.getTrueSource();
   }

   @Nullable
   public BlockPos getPos() {
      return this.pos;
   }

   public boolean addLootTable(LootTable p_186496_1_) {
      return this.lootTables.add(p_186496_1_);
   }

   public void removeLootTable(LootTable p_186490_1_) {
      this.lootTables.remove(p_186490_1_);
   }

   public LootTableManager getLootTableManager() {
      return this.lootTableManager;
   }

   public float getLuck() {
      return this.luck;
   }

   public WorldServer getWorld() {
      return this.world;
   }

   @Nullable
   public Entity getEntity(LootContext.EntityTarget p_186494_1_) {
      switch(p_186494_1_) {
      case THIS:
         return this.getLootedEntity();
      case KILLER:
         return this.getKiller();
      case KILLER_PLAYER:
         return this.getKillerPlayer();
      default:
         return null;
      }
   }

   public static class Builder {
      private final WorldServer world;
      private float luck;
      private Entity lootedEntity;
      private EntityPlayer player;
      private DamageSource damageSource;
      private BlockPos pos;

      public Builder(WorldServer p_i46993_1_) {
         this.world = p_i46993_1_;
      }

      public LootContext.Builder withLuck(float p_186469_1_) {
         this.luck = p_186469_1_;
         return this;
      }

      public LootContext.Builder withLootedEntity(Entity p_186472_1_) {
         this.lootedEntity = p_186472_1_;
         return this;
      }

      public LootContext.Builder withPlayer(EntityPlayer p_186470_1_) {
         this.player = p_186470_1_;
         return this;
      }

      public LootContext.Builder withDamageSource(DamageSource p_186473_1_) {
         this.damageSource = p_186473_1_;
         return this;
      }

      public LootContext.Builder withPosition(BlockPos p_204313_1_) {
         this.pos = p_204313_1_;
         return this;
      }

      public LootContext build() {
         return new LootContext(this.luck, this.world, this.world.getServer().getLootTableManager(), this.lootedEntity, this.player, this.damageSource, this.pos);
      }
   }

   public static enum EntityTarget {
      THIS("this"),
      KILLER("killer"),
      KILLER_PLAYER("killer_player");

      private final String targetType;

      private EntityTarget(String p_i46992_3_) {
         this.targetType = p_i46992_3_;
      }

      public static LootContext.EntityTarget fromString(String p_186482_0_) {
         for(LootContext.EntityTarget lootcontext$entitytarget : values()) {
            if (lootcontext$entitytarget.targetType.equals(p_186482_0_)) {
               return lootcontext$entitytarget;
            }
         }

         throw new IllegalArgumentException("Invalid entity target " + p_186482_0_);
      }

      public static class Serializer extends TypeAdapter<LootContext.EntityTarget> {
         public void write(JsonWriter p_write_1_, LootContext.EntityTarget p_write_2_) throws IOException {
            p_write_1_.value(p_write_2_.targetType);
         }

         public LootContext.EntityTarget read(JsonReader p_read_1_) throws IOException {
            return LootContext.EntityTarget.fromString(p_read_1_.nextString());
         }
      }
   }
}
