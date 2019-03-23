package net.minecraft.command.arguments;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextComponentTranslation;

public class EntityArgument implements ArgumentType<EntitySelector> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
   public static final SimpleCommandExceptionType TOO_MANY_ENTITIES = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.toomany"));
   public static final SimpleCommandExceptionType TOO_MANY_PLAYERS = new SimpleCommandExceptionType(new TextComponentTranslation("argument.player.toomany"));
   public static final SimpleCommandExceptionType ONLY_PLAYERS_ALLOWED = new SimpleCommandExceptionType(new TextComponentTranslation("argument.player.entities"));
   public static final SimpleCommandExceptionType ENTITY_NOT_FOUND = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.notfound.entity"));
   public static final SimpleCommandExceptionType PLAYER_NOT_FOUND = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.notfound.player"));
   public static final SimpleCommandExceptionType SELECTOR_NOT_ALLOWED = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.selector.not_allowed"));
   private final boolean single;
   private final boolean playersOnly;

   protected EntityArgument(boolean p_i47969_1_, boolean p_i47969_2_) {
      this.single = p_i47969_1_;
      this.playersOnly = p_i47969_2_;
   }

   public static EntityArgument singleEntity() {
      return new EntityArgument(true, false);
   }

   public static Entity getSingleEntity(CommandContext<CommandSource> p_197088_0_, String p_197088_1_) throws CommandSyntaxException {
      return p_197088_0_.getArgument(p_197088_1_, EntitySelector.class).selectOne(p_197088_0_.getSource());
   }

   public static EntityArgument multipleEntities() {
      return new EntityArgument(false, false);
   }

   public static Collection<? extends Entity> getEntities(CommandContext<CommandSource> p_197097_0_, String p_197097_1_) throws CommandSyntaxException {
      Collection<? extends Entity> collection = getEntitiesAllowingNone(p_197097_0_, p_197097_1_);
      if (collection.isEmpty()) {
         throw ENTITY_NOT_FOUND.create();
      } else {
         return collection;
      }
   }

   public static Collection<? extends Entity> getEntitiesAllowingNone(CommandContext<CommandSource> p_197087_0_, String p_197087_1_) throws CommandSyntaxException {
      return p_197087_0_.getArgument(p_197087_1_, EntitySelector.class).select(p_197087_0_.getSource());
   }

   public static Collection<EntityPlayerMP> getPlayersAllowingNone(CommandContext<CommandSource> p_201309_0_, String p_201309_1_) throws CommandSyntaxException {
      return p_201309_0_.getArgument(p_201309_1_, EntitySelector.class).selectPlayers(p_201309_0_.getSource());
   }

   public static EntityArgument singlePlayer() {
      return new EntityArgument(true, true);
   }

   public static EntityPlayerMP getOnePlayer(CommandContext<CommandSource> p_197089_0_, String p_197089_1_) throws CommandSyntaxException {
      return p_197089_0_.getArgument(p_197089_1_, EntitySelector.class).selectOnePlayer(p_197089_0_.getSource());
   }

   public static EntityArgument multiplePlayers() {
      return new EntityArgument(false, true);
   }

   public static Collection<EntityPlayerMP> getPlayers(CommandContext<CommandSource> p_197090_0_, String p_197090_1_) throws CommandSyntaxException {
      List<EntityPlayerMP> list = p_197090_0_.getArgument(p_197090_1_, EntitySelector.class).selectPlayers(p_197090_0_.getSource());
      if (list.isEmpty()) {
         throw PLAYER_NOT_FOUND.create();
      } else {
         return list;
      }
   }

   public EntitySelector parse(StringReader p_parse_1_) throws CommandSyntaxException {
      int i = 0;
      EntitySelectorParser entityselectorparser = new EntitySelectorParser(p_parse_1_);
      EntitySelector entityselector = entityselectorparser.parse();
      if (entityselector.func_197346_a() > 1 && this.single) {
         if (this.playersOnly) {
            p_parse_1_.setCursor(0);
            throw TOO_MANY_PLAYERS.createWithContext(p_parse_1_);
         } else {
            p_parse_1_.setCursor(0);
            throw TOO_MANY_ENTITIES.createWithContext(p_parse_1_);
         }
      } else if (entityselector.func_197351_b() && this.playersOnly && !entityselector.func_197352_c()) {
         p_parse_1_.setCursor(0);
         throw ONLY_PLAYERS_ALLOWED.createWithContext(p_parse_1_);
      } else {
         return entityselector;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      if (p_listSuggestions_1_.getSource() instanceof ISuggestionProvider) {
         StringReader stringreader = new StringReader(p_listSuggestions_2_.getInput());
         stringreader.setCursor(p_listSuggestions_2_.getStart());
         ISuggestionProvider isuggestionprovider = (ISuggestionProvider)p_listSuggestions_1_.getSource();
         EntitySelectorParser entityselectorparser = new EntitySelectorParser(stringreader, isuggestionprovider.hasPermissionLevel(2));

         try {
            entityselectorparser.parse();
         } catch (CommandSyntaxException var7) {
            ;
         }

         return entityselectorparser.func_201993_a(p_listSuggestions_2_, (p_201942_2_) -> {
            Collection<String> collection = isuggestionprovider.getPlayerNames();
            Iterable<String> iterable = (Iterable<String>)(this.playersOnly ? collection : Iterables.concat(collection, isuggestionprovider.getTargetedEntity()));
            ISuggestionProvider.suggest(iterable, p_201942_2_);
         });
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static class Serializer implements IArgumentSerializer<EntityArgument> {
      public void write(EntityArgument p_197072_1_, PacketBuffer p_197072_2_) {
         byte b0 = 0;
         if (p_197072_1_.single) {
            b0 = (byte)(b0 | 1);
         }

         if (p_197072_1_.playersOnly) {
            b0 = (byte)(b0 | 2);
         }

         p_197072_2_.writeByte(b0);
      }

      public EntityArgument read(PacketBuffer p_197071_1_) {
         byte b0 = p_197071_1_.readByte();
         return new EntityArgument((b0 & 1) != 0, (b0 & 2) != 0);
      }

      public void func_212244_a(EntityArgument p_212244_1_, JsonObject p_212244_2_) {
         p_212244_2_.addProperty("amount", p_212244_1_.single ? "single" : "multiple");
         p_212244_2_.addProperty("type", p_212244_1_.playersOnly ? "players" : "entities");
      }
   }
}
