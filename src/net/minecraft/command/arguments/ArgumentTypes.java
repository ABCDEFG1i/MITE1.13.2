package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.serializers.BrigadierSerializers;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArgumentTypes {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<Class<?>, ArgumentTypes.Entry<?>> CLASS_TYPE_MAP = Maps.newHashMap();
   private static final Map<ResourceLocation, ArgumentTypes.Entry<?>> ID_TYPE_MAP = Maps.newHashMap();

   public static <T extends ArgumentType<?>> void register(ResourceLocation p_197487_0_, Class<T> p_197487_1_, IArgumentSerializer<T> p_197487_2_) {
      if (CLASS_TYPE_MAP.containsKey(p_197487_1_)) {
         throw new IllegalArgumentException("Class " + p_197487_1_.getName() + " already has a serializer!");
      } else if (ID_TYPE_MAP.containsKey(p_197487_0_)) {
         throw new IllegalArgumentException("'" + p_197487_0_ + "' is already a registered serializer!");
      } else {
         ArgumentTypes.Entry<T> entry = new ArgumentTypes.Entry<>(p_197487_1_, p_197487_2_, p_197487_0_);
         CLASS_TYPE_MAP.put(p_197487_1_, entry);
         ID_TYPE_MAP.put(p_197487_0_, entry);
      }
   }

   public static void registerArgumentTypes() {
      BrigadierSerializers.registerArgumentTypes();
      register(new ResourceLocation("minecraft:entity"), EntityArgument.class, new EntityArgument.Serializer());
      register(new ResourceLocation("minecraft:game_profile"), GameProfileArgument.class, new ArgumentSerializer<>(GameProfileArgument::gameProfile));
      register(new ResourceLocation("minecraft:block_pos"), BlockPosArgument.class, new ArgumentSerializer<>(BlockPosArgument::blockPos));
      register(new ResourceLocation("minecraft:column_pos"), ColumnPosArgument.class, new ArgumentSerializer<>(ColumnPosArgument::func_212603_a));
      register(new ResourceLocation("minecraft:vec3"), Vec3Argument.class, new ArgumentSerializer<>(Vec3Argument::vec3));
      register(new ResourceLocation("minecraft:vec2"), Vec2Argument.class, new ArgumentSerializer<>(Vec2Argument::vec2));
      register(new ResourceLocation("minecraft:block_state"), BlockStateArgument.class, new ArgumentSerializer<>(BlockStateArgument::blockState));
      register(new ResourceLocation("minecraft:block_predicate"), BlockPredicateArgument.class, new ArgumentSerializer<>(BlockPredicateArgument::blockPredicateArgument));
      register(new ResourceLocation("minecraft:item_stack"), ItemArgument.class, new ArgumentSerializer<>(ItemArgument::itemStack));
      register(new ResourceLocation("minecraft:item_predicate"), ItemPredicateArgument.class, new ArgumentSerializer<>(ItemPredicateArgument::itemPredicate));
      register(new ResourceLocation("minecraft:color"), ColorArgument.class, new ArgumentSerializer<>(ColorArgument::color));
      register(new ResourceLocation("minecraft:component"), ComponentArgument.class, new ArgumentSerializer<>(ComponentArgument::component));
      register(new ResourceLocation("minecraft:message"), MessageArgument.class, new ArgumentSerializer<>(MessageArgument::message));
      register(new ResourceLocation("minecraft:nbt"), NBTArgument.class, new ArgumentSerializer<>(NBTArgument::nbt));
      register(new ResourceLocation("minecraft:nbt_path"), NBTPathArgument.class, new ArgumentSerializer<>(NBTPathArgument::nbtPath));
      register(new ResourceLocation("minecraft:objective"), ObjectiveArgument.class, new ArgumentSerializer<>(ObjectiveArgument::objective));
      register(new ResourceLocation("minecraft:objective_criteria"), ObjectiveCriteriaArgument.class, new ArgumentSerializer<>(ObjectiveCriteriaArgument::objectiveCriteria));
      register(new ResourceLocation("minecraft:operation"), OperationArgument.class, new ArgumentSerializer<>(OperationArgument::operation));
      register(new ResourceLocation("minecraft:particle"), ParticleArgument.class, new ArgumentSerializer<>(ParticleArgument::particle));
      register(new ResourceLocation("minecraft:rotation"), RotationArgument.class, new ArgumentSerializer<>(RotationArgument::rotation));
      register(new ResourceLocation("minecraft:scoreboard_slot"), ScoreboardSlotArgument.class, new ArgumentSerializer<>(ScoreboardSlotArgument::scoreboardSlot));
      register(new ResourceLocation("minecraft:score_holder"), ScoreHolderArgument.class, new ScoreHolderArgument.Serializer());
      register(new ResourceLocation("minecraft:swizzle"), SwizzleArgument.class, new ArgumentSerializer<>(SwizzleArgument::swizzle));
      register(new ResourceLocation("minecraft:team"), TeamArgument.class, new ArgumentSerializer<>(TeamArgument::team));
      register(new ResourceLocation("minecraft:item_slot"), SlotArgument.class, new ArgumentSerializer<>(SlotArgument::itemSlot));
      register(new ResourceLocation("minecraft:resource_location"), ResourceLocationArgument.class, new ArgumentSerializer<>(ResourceLocationArgument::resourceLocation));
      register(new ResourceLocation("minecraft:mob_effect"), PotionArgument.class, new ArgumentSerializer<>(PotionArgument::mobEffect));
      register(new ResourceLocation("minecraft:function"), FunctionArgument.class, new ArgumentSerializer<>(FunctionArgument::function));
      register(new ResourceLocation("minecraft:entity_anchor"), EntityAnchorArgument.class, new ArgumentSerializer<>(EntityAnchorArgument::entityAnchor));
      register(new ResourceLocation("minecraft:int_range"), RangeArgument.IntRange.class, new RangeArgument.IntRange.Serializer());
      register(new ResourceLocation("minecraft:float_range"), RangeArgument.FloatRange.class, new RangeArgument.FloatRange.Serializer());
      register(new ResourceLocation("minecraft:item_enchantment"), EnchantmentArgument.class, new ArgumentSerializer<>(EnchantmentArgument::itemEnchantment));
      register(new ResourceLocation("minecraft:entity_summon"), EntitySummonArgument.class, new ArgumentSerializer<>(EntitySummonArgument::entitySummon));
      register(new ResourceLocation("minecraft:dimension"), DimensionArgument.class, new ArgumentSerializer<>(DimensionArgument::func_212595_a));
   }

   @Nullable
   private static ArgumentTypes.Entry<?> get(ResourceLocation p_197482_0_) {
      return ID_TYPE_MAP.get(p_197482_0_);
   }

   @Nullable
   private static ArgumentTypes.Entry<?> get(ArgumentType<?> p_201040_0_) {
      return CLASS_TYPE_MAP.get(p_201040_0_.getClass());
   }

   public static <T extends ArgumentType<?>> void serialize(PacketBuffer p_197484_0_, T p_197484_1_) {
      ArgumentTypes.Entry<T> entry = (ArgumentTypes.Entry<T>)get(p_197484_1_);
      if (entry == null) {
         LOGGER.error("Could not serialize {} ({}) - will not be sent to client!", p_197484_1_, p_197484_1_.getClass());
         p_197484_0_.writeResourceLocation(new ResourceLocation(""));
      } else {
         p_197484_0_.writeResourceLocation(entry.id);
         entry.serializer.write(p_197484_1_, p_197484_0_);
      }
   }

   @Nullable
   public static ArgumentType<?> deserialize(PacketBuffer p_197486_0_) {
      ResourceLocation resourcelocation = p_197486_0_.readResourceLocation();
      ArgumentTypes.Entry<?> entry = get(resourcelocation);
      if (entry == null) {
         LOGGER.error("Could not deserialize {}", resourcelocation);
         return null;
      } else {
         return entry.serializer.read(p_197486_0_);
      }
   }

   private static <T extends ArgumentType<?>> void serialize(JsonObject p_201042_0_, T p_201042_1_) {
      ArgumentTypes.Entry<T> entry = (ArgumentTypes.Entry<T>)get(p_201042_1_);
      if (entry == null) {
         LOGGER.error("Could not serialize argument {} ({})!", p_201042_1_, p_201042_1_.getClass());
         p_201042_0_.addProperty("type", "unknown");
      } else {
         p_201042_0_.addProperty("type", "argument");
         p_201042_0_.addProperty("parser", entry.id.toString());
         JsonObject jsonobject = new JsonObject();
         entry.serializer.func_212244_a(p_201042_1_, jsonobject);
         if (jsonobject.size() > 0) {
            p_201042_0_.add("properties", jsonobject);
         }
      }

   }

   public static <S> JsonObject serialize(CommandDispatcher<S> p_200388_0_, CommandNode<S> p_200388_1_) {
      JsonObject jsonobject = new JsonObject();
      if (p_200388_1_ instanceof RootCommandNode) {
         jsonobject.addProperty("type", "root");
      } else if (p_200388_1_ instanceof LiteralCommandNode) {
         jsonobject.addProperty("type", "literal");
      } else if (p_200388_1_ instanceof ArgumentCommandNode) {
         serialize(jsonobject, ((ArgumentCommandNode)p_200388_1_).getType());
      } else {
         LOGGER.error("Could not serialize node {} ({})!", p_200388_1_, p_200388_1_.getClass());
         jsonobject.addProperty("type", "unknown");
      }

      JsonObject jsonobject1 = new JsonObject();

      for(CommandNode<S> commandnode : p_200388_1_.getChildren()) {
         jsonobject1.add(commandnode.getName(), serialize(p_200388_0_, commandnode));
      }

      if (jsonobject1.size() > 0) {
         jsonobject.add("children", jsonobject1);
      }

      if (p_200388_1_.getCommand() != null) {
         jsonobject.addProperty("executable", true);
      }

      if (p_200388_1_.getRedirect() != null) {
         Collection<String> collection = p_200388_0_.getPath(p_200388_1_.getRedirect());
         if (!collection.isEmpty()) {
            JsonArray jsonarray = new JsonArray();

            for(String s : collection) {
               jsonarray.add(s);
            }

            jsonobject.add("redirect", jsonarray);
         }
      }

      return jsonobject;
   }

   static class Entry<T extends ArgumentType<?>> {
      public final Class<T> argumentClass;
      public final IArgumentSerializer<T> serializer;
      public final ResourceLocation id;

      private Entry(Class<T> p_i48088_1_, IArgumentSerializer<T> p_i48088_2_, ResourceLocation p_i48088_3_) {
         this.argumentClass = p_i48088_1_;
         this.serializer = p_i48088_2_;
         this.id = p_i48088_3_;
      }
   }
}
