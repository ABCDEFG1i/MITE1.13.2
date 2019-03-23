package net.minecraft.world;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;

public class GameRules {
   private static final TreeMap<String, GameRules.ValueDefinition> DEFINITIONS = Util.make(new TreeMap<>(), (p_209363_0_) -> {
      p_209363_0_.put("doFireTick", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("mobGriefing", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("keepInventory", new GameRules.ValueDefinition("false", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("doMobSpawning", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("doMobLoot", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("doTileDrops", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("doEntityDrops", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("commandBlockOutput", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("naturalRegeneration", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("doDaylightCycle", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("logAdminCommands", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("showDeathMessages", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("randomTickSpeed", new GameRules.ValueDefinition("3", GameRules.ValueType.NUMERICAL_VALUE));
      p_209363_0_.put("sendCommandFeedback", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("reducedDebugInfo", new GameRules.ValueDefinition("false", GameRules.ValueType.BOOLEAN_VALUE, (p_209364_0_, p_209364_1_) -> {
         byte b0 = (byte)(p_209364_1_.getBoolean() ? 22 : 23);

         for(EntityPlayerMP entityplayermp : p_209364_0_.getPlayerList().getPlayers()) {
            entityplayermp.connection.sendPacket(new SPacketEntityStatus(entityplayermp, b0));
         }

      }));
      p_209363_0_.put("spectatorsGenerateChunks", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("spawnRadius", new GameRules.ValueDefinition("10", GameRules.ValueType.NUMERICAL_VALUE));
      p_209363_0_.put("disableElytraMovementCheck", new GameRules.ValueDefinition("false", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("maxEntityCramming", new GameRules.ValueDefinition("24", GameRules.ValueType.NUMERICAL_VALUE));
      p_209363_0_.put("doWeatherCycle", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("doLimitedCrafting", new GameRules.ValueDefinition("false", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("maxCommandChainLength", new GameRules.ValueDefinition("65536", GameRules.ValueType.NUMERICAL_VALUE));
      p_209363_0_.put("announceAdvancements", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
   });
   private final TreeMap<String, GameRules.Value> rules = new TreeMap<>();

   public GameRules() {
      for(Entry<String, GameRules.ValueDefinition> entry : DEFINITIONS.entrySet()) {
         this.rules.put(entry.getKey(), entry.getValue().createValue());
      }

   }

   public void setOrCreateGameRule(String p_82764_1_, String p_82764_2_, @Nullable MinecraftServer p_82764_3_) {
      GameRules.Value gamerules$value = this.rules.get(p_82764_1_);
      if (gamerules$value != null) {
         gamerules$value.setValue(p_82764_2_, p_82764_3_);
      }

   }

   public boolean getBoolean(String p_82766_1_) {
      GameRules.Value gamerules$value = this.rules.get(p_82766_1_);
      return gamerules$value != null ? gamerules$value.getBoolean() : false;
   }

   public int getInt(String p_180263_1_) {
      GameRules.Value gamerules$value = this.rules.get(p_180263_1_);
      return gamerules$value != null ? gamerules$value.getInt() : 0;
   }

   public NBTTagCompound writeToNBT() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();

      for(String s : this.rules.keySet()) {
         GameRules.Value gamerules$value = this.rules.get(s);
         nbttagcompound.setString(s, gamerules$value.getString());
      }

      return nbttagcompound;
   }

   public void readFromNBT(NBTTagCompound p_82768_1_) {
      for(String s : p_82768_1_.getKeySet()) {
         this.setOrCreateGameRule(s, p_82768_1_.getString(s), (MinecraftServer)null);
      }

   }

   public GameRules.Value get(String p_196230_1_) {
      return this.rules.get(p_196230_1_);
   }

   public static TreeMap<String, GameRules.ValueDefinition> getDefinitions() {
      return DEFINITIONS;
   }

   public static class Value {
      private String valueString;
      private boolean valueBoolean;
      private int valueInteger;
      private double valueDouble;
      private final GameRules.ValueType type;
      private final BiConsumer<MinecraftServer, GameRules.Value> changeCallback;

      public Value(String p_i48618_1_, GameRules.ValueType p_i48618_2_, BiConsumer<MinecraftServer, GameRules.Value> p_i48618_3_) {
         this.type = p_i48618_2_;
         this.changeCallback = p_i48618_3_;
         this.setValue(p_i48618_1_, (MinecraftServer)null);
      }

      public void setValue(String p_201200_1_, @Nullable MinecraftServer p_201200_2_) {
         this.valueString = p_201200_1_;
         this.valueBoolean = Boolean.parseBoolean(p_201200_1_);
         this.valueInteger = this.valueBoolean ? 1 : 0;

         try {
            this.valueInteger = Integer.parseInt(p_201200_1_);
         } catch (NumberFormatException var5) {
            ;
         }

         try {
            this.valueDouble = Double.parseDouble(p_201200_1_);
         } catch (NumberFormatException var4) {
            ;
         }

         if (p_201200_2_ != null) {
            this.changeCallback.accept(p_201200_2_, this);
         }

      }

      public String getString() {
         return this.valueString;
      }

      public boolean getBoolean() {
         return this.valueBoolean;
      }

      public int getInt() {
         return this.valueInteger;
      }

      public GameRules.ValueType getType() {
         return this.type;
      }
   }

   public static class ValueDefinition {
      private final GameRules.ValueType type;
      private final String name;
      private final BiConsumer<MinecraftServer, GameRules.Value> changeCallback;

      public ValueDefinition(String p_i48178_1_, GameRules.ValueType p_i48178_2_) {
         this(p_i48178_1_, p_i48178_2_, (p_201202_0_, p_201202_1_) -> {
         });
      }

      public ValueDefinition(String p_i48617_1_, GameRules.ValueType p_i48617_2_, BiConsumer<MinecraftServer, GameRules.Value> p_i48617_3_) {
         this.type = p_i48617_2_;
         this.name = p_i48617_1_;
         this.changeCallback = p_i48617_3_;
      }

      public GameRules.Value createValue() {
         return new GameRules.Value(this.name, this.type, this.changeCallback);
      }

      public GameRules.ValueType getType() {
         return this.type;
      }
   }

   public static enum ValueType {
      ANY_VALUE(StringArgumentType::greedyString, (p_196224_0_, p_196224_1_) -> {
         return p_196224_0_.getArgument(p_196224_1_, String.class);
      }),
      BOOLEAN_VALUE(BoolArgumentType::bool, (p_196227_0_, p_196227_1_) -> {
         return p_196227_0_.getArgument(p_196227_1_, Boolean.class).toString();
      }),
      NUMERICAL_VALUE(IntegerArgumentType::integer, (p_196226_0_, p_196226_1_) -> {
         return p_196226_0_.getArgument(p_196226_1_, Integer.class).toString();
      });

      private final Supplier<ArgumentType<?>> argumentType;
      private final BiFunction<CommandContext<CommandSource>, String, String> argumentGetter;

      private ValueType(Supplier<ArgumentType<?>> p_i47626_3_, BiFunction<CommandContext<CommandSource>, String, String> p_i47626_4_) {
         this.argumentType = p_i47626_3_;
         this.argumentGetter = p_i47626_4_;
      }

      public RequiredArgumentBuilder<CommandSource, ?> createArgument(String p_199809_1_) {
         return Commands.argument(p_199809_1_, this.argumentType.get());
      }

      public void updateValue(CommandContext<CommandSource> p_196222_1_, String p_196222_2_, GameRules.Value p_196222_3_) {
         p_196222_3_.setValue(this.argumentGetter.apply(p_196222_1_, p_196222_2_), p_196222_1_.getSource().getServer());
      }
   }
}
