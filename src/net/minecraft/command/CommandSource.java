package net.minecraft.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

public class CommandSource implements ISuggestionProvider {
   public static final SimpleCommandExceptionType REQUIRES_PLAYER_EXCEPTION_TYPE = new SimpleCommandExceptionType(new TextComponentTranslation("permissions.requires.player"));
   public static final SimpleCommandExceptionType REQUIRES_ENTITY_EXCEPTION_TYPE = new SimpleCommandExceptionType(new TextComponentTranslation("permissions.requires.entity"));
   private final ICommandSource source;
   private final Vec3d pos;
   private final WorldServer world;
   private final int permissionLevel;
   private final String name;
   private final ITextComponent displayName;
   private final MinecraftServer server;
   private final boolean feedbackDisabled;
   @Nullable
   private final Entity entity;
   private final ResultConsumer<CommandSource> resultConsumer;
   private final EntityAnchorArgument.Type entityAnchorType;
   private final Vec2f pitchYaw;

   public CommandSource(ICommandSource p_i49552_1_, Vec3d p_i49552_2_, Vec2f p_i49552_3_, WorldServer p_i49552_4_, int p_i49552_5_, String p_i49552_6_, ITextComponent p_i49552_7_, MinecraftServer p_i49552_8_, @Nullable Entity p_i49552_9_) {
      this(p_i49552_1_, p_i49552_2_, p_i49552_3_, p_i49552_4_, p_i49552_5_, p_i49552_6_, p_i49552_7_, p_i49552_8_, p_i49552_9_, false, (p_197032_0_, p_197032_1_, p_197032_2_) -> {
      }, EntityAnchorArgument.Type.FEET);
   }

   protected CommandSource(ICommandSource p_i49553_1_, Vec3d p_i49553_2_, Vec2f p_i49553_3_, WorldServer p_i49553_4_, int p_i49553_5_, String p_i49553_6_, ITextComponent p_i49553_7_, MinecraftServer p_i49553_8_, @Nullable Entity p_i49553_9_, boolean p_i49553_10_, ResultConsumer<CommandSource> p_i49553_11_, EntityAnchorArgument.Type p_i49553_12_) {
      this.source = p_i49553_1_;
      this.pos = p_i49553_2_;
      this.world = p_i49553_4_;
      this.feedbackDisabled = p_i49553_10_;
      this.entity = p_i49553_9_;
      this.permissionLevel = p_i49553_5_;
      this.name = p_i49553_6_;
      this.displayName = p_i49553_7_;
      this.server = p_i49553_8_;
      this.resultConsumer = p_i49553_11_;
      this.entityAnchorType = p_i49553_12_;
      this.pitchYaw = p_i49553_3_;
   }

   public CommandSource withEntity(Entity p_197024_1_) {
      return this.entity == p_197024_1_ ? this : new CommandSource(this.source, this.pos, this.pitchYaw, this.world, this.permissionLevel, p_197024_1_.getName().getString(), p_197024_1_.getDisplayName(), this.server, p_197024_1_, this.feedbackDisabled, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withPos(Vec3d p_201009_1_) {
      return this.pos.equals(p_201009_1_) ? this : new CommandSource(this.source, p_201009_1_, this.pitchYaw, this.world, this.permissionLevel, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withPitchYaw(Vec2f p_201007_1_) {
      return this.pitchYaw.equals(p_201007_1_) ? this : new CommandSource(this.source, this.pos, p_201007_1_, this.world, this.permissionLevel, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withResultConsumer(ResultConsumer<CommandSource> p_197029_1_) {
      return this.resultConsumer.equals(p_197029_1_) ? this : new CommandSource(this.source, this.pos, this.pitchYaw, this.world, this.permissionLevel, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, p_197029_1_, this.entityAnchorType);
   }

   public CommandSource withResultConsumer(ResultConsumer<CommandSource> p_209550_1_, BinaryOperator<ResultConsumer<CommandSource>> p_209550_2_) {
      ResultConsumer<CommandSource> resultconsumer = p_209550_2_.apply(this.resultConsumer, p_209550_1_);
      return this.withResultConsumer(resultconsumer);
   }

   public CommandSource withFeedbackDisabled() {
      return this.feedbackDisabled ? this : new CommandSource(this.source, this.pos, this.pitchYaw, this.world, this.permissionLevel, this.name, this.displayName, this.server, this.entity, true, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withPermissionLevel(int p_197033_1_) {
      return p_197033_1_ == this.permissionLevel ? this : new CommandSource(this.source, this.pos, this.pitchYaw, this.world, p_197033_1_, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withMinPermissionLevel(int p_197026_1_) {
      return p_197026_1_ <= this.permissionLevel ? this : new CommandSource(this.source, this.pos, this.pitchYaw, this.world, p_197026_1_, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withEntityAnchorType(EntityAnchorArgument.Type p_201010_1_) {
      return p_201010_1_ == this.entityAnchorType ? this : new CommandSource(this.source, this.pos, this.pitchYaw, this.world, this.permissionLevel, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, this.resultConsumer, p_201010_1_);
   }

   public CommandSource withWorld(WorldServer p_201003_1_) {
      return p_201003_1_ == this.world ? this : new CommandSource(this.source, this.pos, this.pitchYaw, p_201003_1_, this.permissionLevel, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withPitchYaw(Entity p_201006_1_, EntityAnchorArgument.Type p_201006_2_) throws CommandSyntaxException {
      return this.withPitchYaw(p_201006_2_.func_201017_a(p_201006_1_));
   }

   public CommandSource withPitchYaw(Vec3d p_201005_1_) throws CommandSyntaxException {
      Vec3d vec3d = this.entityAnchorType.func_201015_a(this);
      double d0 = p_201005_1_.x - vec3d.x;
      double d1 = p_201005_1_.y - vec3d.y;
      double d2 = p_201005_1_.z - vec3d.z;
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      float f = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI))));
      float f1 = MathHelper.wrapDegrees((float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F);
      return this.withPitchYaw(new Vec2f(f, f1));
   }

   public ITextComponent getDisplayName() {
      return this.displayName;
   }

   public String getName() {
      return this.name;
   }

   public boolean hasPermissionLevel(int p_197034_1_) {
      return this.permissionLevel >= p_197034_1_;
   }

   public Vec3d getPos() {
      return this.pos;
   }

   public WorldServer getWorld() {
      return this.world;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }

   public Entity assertIsEntity() throws CommandSyntaxException {
      if (this.entity == null) {
         throw REQUIRES_ENTITY_EXCEPTION_TYPE.create();
      } else {
         return this.entity;
      }
   }

   public EntityPlayerMP asPlayer() throws CommandSyntaxException {
      if (!(this.entity instanceof EntityPlayerMP)) {
         throw REQUIRES_PLAYER_EXCEPTION_TYPE.create();
      } else {
         return (EntityPlayerMP)this.entity;
      }
   }

   public Vec2f getPitchYaw() {
      return this.pitchYaw;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public EntityAnchorArgument.Type getEntityAnchorType() {
      return this.entityAnchorType;
   }

   public void sendFeedback(ITextComponent p_197030_1_, boolean p_197030_2_) {
      if (this.source.shouldReceiveFeedback() && !this.feedbackDisabled) {
         this.source.sendMessage(p_197030_1_);
      }

      if (p_197030_2_ && this.source.allowLogging() && !this.feedbackDisabled) {
         this.logFeedback(p_197030_1_);
      }

   }

   private void logFeedback(ITextComponent p_197020_1_) {
      ITextComponent itextcomponent = (new TextComponentTranslation("chat.type.admin", this.getDisplayName(), p_197020_1_)).applyTextStyles(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC});
      if (this.server.getGameRules().getBoolean("sendCommandFeedback")) {
         for(EntityPlayerMP entityplayermp : this.server.getPlayerList().getPlayers()) {
            if (entityplayermp != this.source && this.server.getPlayerList().canSendCommands(entityplayermp.getGameProfile())) {
               entityplayermp.sendMessage(itextcomponent);
            }
         }
      }

      if (this.source != this.server && this.server.getGameRules().getBoolean("logAdminCommands")) {
         this.server.sendMessage(itextcomponent);
      }

   }

   public void sendErrorMessage(ITextComponent p_197021_1_) {
      if (this.source.shouldReceiveErrors() && !this.feedbackDisabled) {
         this.source.sendMessage((new TextComponentString("")).appendSibling(p_197021_1_).applyTextStyle(TextFormatting.RED));
      }

   }

   public void onCommandComplete(CommandContext<CommandSource> p_197038_1_, boolean p_197038_2_, int p_197038_3_) {
      if (this.resultConsumer != null) {
         this.resultConsumer.onCommandComplete(p_197038_1_, p_197038_2_, p_197038_3_);
      }

   }

   public Collection<String> getPlayerNames() {
      return Lists.newArrayList(this.server.getOnlinePlayerNames());
   }

   public Collection<String> getTeamNames() {
      return this.server.getWorldScoreboard().getTeamNames();
   }

   public Collection<ResourceLocation> getSoundResourceLocations() {
      return IRegistry.field_212633_v.func_148742_b();
   }

   public Collection<ResourceLocation> getRecipeResourceLocations() {
      return this.server.getRecipeManager().getIds();
   }

   public CompletableFuture<Suggestions> getSuggestionsFromServer(CommandContext<ISuggestionProvider> p_197009_1_, SuggestionsBuilder p_197009_2_) {
      return null;
   }

   public Collection<ISuggestionProvider.Coordinates> getCoordinates(boolean p_199613_1_) {
      return Collections.singleton(ISuggestionProvider.Coordinates.field_209005_b);
   }
}
