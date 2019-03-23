package net.minecraft.entity.player;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ServerRecipeBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerLook;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketSignEditorOpen;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.network.play.server.SPacketWindowProperty;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.CooldownTrackerServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.GameType;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.loot.ILootContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityPlayerMP extends EntityPlayer implements IContainerListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private String language = "en_US";
   public NetHandlerPlayServer connection;
   public final MinecraftServer server;
   public final PlayerInteractionManager interactionManager;
   public double managedPosX;
   public double managedPosZ;
   private final List<Integer> entityRemoveQueue = Lists.newLinkedList();
   private final PlayerAdvancements advancements;
   private final StatisticsManagerServer stats;
   private float lastHealthScore = Float.MIN_VALUE;
   private int lastFoodScore = Integer.MIN_VALUE;
   private int lastAirScore = Integer.MIN_VALUE;
   private int lastArmorScore = Integer.MIN_VALUE;
   private int lastLevelScore = Integer.MIN_VALUE;
   private int lastExperienceScore = Integer.MIN_VALUE;
   private float lastHealth = -1.0E8F;
   private int lastFoodLevel = -99999999;
   private boolean wasHungry = true;
   private int lastExperience = -99999999;
   private int respawnInvulnerabilityTicks = 60;
   private EntityPlayer.EnumChatVisibility chatVisibility;
   private boolean chatColours = true;
   private long playerLastActiveTime = Util.milliTime();
   private Entity spectatingEntity;
   private boolean invulnerableDimensionChange;
   private boolean seenCredits;
   private final ServerRecipeBook recipeBook;
   private Vec3d levitationStartPos;
   private int levitatingSince;
   private boolean disconnected;
   private Vec3d enteredNetherPosition;
   public int currentWindowId;
   public boolean isChangingQuantityOnly;
   public int ping;
   public boolean queuedEndExit;

   public EntityPlayerMP(MinecraftServer p_i45285_1_, WorldServer p_i45285_2_, GameProfile p_i45285_3_, PlayerInteractionManager p_i45285_4_) {
      super(p_i45285_2_, p_i45285_3_);
      p_i45285_4_.player = this;
      this.interactionManager = p_i45285_4_;
      this.server = p_i45285_1_;
      this.recipeBook = new ServerRecipeBook(p_i45285_1_.getRecipeManager());
      this.stats = p_i45285_1_.getPlayerList().getPlayerStats(this);
      this.advancements = p_i45285_1_.getPlayerList().getPlayerAdvancements(this);
      this.stepHeight = 1.0F;
      this.func_205734_a(p_i45285_2_);
   }

   private void func_205734_a(WorldServer p_205734_1_) {
      BlockPos blockpos = p_205734_1_.getSpawnPoint();
      if (p_205734_1_.dimension.hasSkyLight() && p_205734_1_.getWorldInfo().getGameType() != GameType.ADVENTURE) {
         int i = Math.max(0, this.server.getSpawnRadius(p_205734_1_));
         int j = MathHelper.floor(p_205734_1_.getWorldBorder().getClosestDistance((double)blockpos.getX(), (double)blockpos.getZ()));
         if (j < i) {
            i = j;
         }

         if (j <= 1) {
            i = 1;
         }

         int k = (i * 2 + 1) * (i * 2 + 1);
         int l = this.func_205735_q(k);
         int i1 = (new Random()).nextInt(k);

         for(int j1 = 0; j1 < k; ++j1) {
            int k1 = (i1 + l * j1) % k;
            int l1 = k1 % (i * 2 + 1);
            int i2 = k1 / (i * 2 + 1);
            BlockPos blockpos1 = p_205734_1_.getDimension().findSpawn(blockpos.getX() + l1 - i, blockpos.getZ() + i2 - i, false);
            if (blockpos1 != null) {
               this.moveToBlockPosAndAngles(blockpos1, 0.0F, 0.0F);
               if (p_205734_1_.isCollisionBoxesEmpty(this, this.getEntityBoundingBox())) {
                  break;
               }
            }
         }
      } else {
         this.moveToBlockPosAndAngles(blockpos, 0.0F, 0.0F);

         while(!p_205734_1_.isCollisionBoxesEmpty(this, this.getEntityBoundingBox()) && this.posY < 255.0D) {
            this.setPosition(this.posX, this.posY + 1.0D, this.posZ);
         }
      }

   }

   private int func_205735_q(int p_205735_1_) {
      return p_205735_1_ <= 16 ? p_205735_1_ - 1 : 17;
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      if (p_70037_1_.hasKey("playerGameType", 99)) {
         if (this.getServer().getForceGamemode()) {
            this.interactionManager.setGameType(this.getServer().getGameType());
         } else {
            this.interactionManager.setGameType(GameType.getByID(p_70037_1_.getInteger("playerGameType")));
         }
      }

      if (p_70037_1_.hasKey("enteredNetherPosition", 10)) {
         NBTTagCompound nbttagcompound = p_70037_1_.getCompoundTag("enteredNetherPosition");
         this.enteredNetherPosition = new Vec3d(nbttagcompound.getDouble("x"), nbttagcompound.getDouble("y"), nbttagcompound.getDouble("z"));
      }

      this.seenCredits = p_70037_1_.getBoolean("seenCredits");
      if (p_70037_1_.hasKey("recipeBook", 10)) {
         this.recipeBook.read(p_70037_1_.getCompoundTag("recipeBook"));
      }

   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("playerGameType", this.interactionManager.getGameType().getID());
      p_70014_1_.setBoolean("seenCredits", this.seenCredits);
      if (this.enteredNetherPosition != null) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.setDouble("x", this.enteredNetherPosition.x);
         nbttagcompound.setDouble("y", this.enteredNetherPosition.y);
         nbttagcompound.setDouble("z", this.enteredNetherPosition.z);
         p_70014_1_.setTag("enteredNetherPosition", nbttagcompound);
      }

      Entity entity1 = this.getLowestRidingEntity();
      Entity entity = this.getRidingEntity();
      if (entity != null && entity1 != this && entity1.isOnePlayerRiding()) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         NBTTagCompound nbttagcompound2 = new NBTTagCompound();
         entity1.writeToNBTOptional(nbttagcompound2);
         nbttagcompound1.setUniqueId("Attach", entity.getUniqueID());
         nbttagcompound1.setTag("Entity", nbttagcompound2);
         p_70014_1_.setTag("RootVehicle", nbttagcompound1);
      }

      p_70014_1_.setTag("recipeBook", this.recipeBook.write());
   }

   public void func_195394_a(int p_195394_1_) {
      float f = (float)this.xpBarCap();
      float f1 = (f - 1.0F) / f;
      this.experience = MathHelper.clamp((float)p_195394_1_ / f, 0.0F, f1);
      this.lastExperience = -1;
   }

   public void func_195399_b(int p_195399_1_) {
      this.experienceLevel = p_195399_1_;
      onLevelUpdate(experienceLevel);
      this.lastExperience = -1;
   }

   public void addExperienceLevel(int p_82242_1_) {
      super.addExperienceLevel(p_82242_1_);
      onLevelUpdate(experienceLevel);
      this.lastExperience = -1;
   }

   public void onEnchant(ItemStack p_192024_1_, int spentLevel) {
      super.onEnchant(p_192024_1_, spentLevel);
      this.lastExperience = -1;
   }

   public void addSelfToInternalCraftingInventory() {
      this.openContainer.addListener(this);
   }

   public void sendEnterCombat() {
      super.sendEnterCombat();
      this.connection.sendPacket(new SPacketCombatEvent(this.getCombatTracker(), SPacketCombatEvent.Event.ENTER_COMBAT));
   }

   public void sendEndCombat() {
      super.sendEndCombat();
      this.connection.sendPacket(new SPacketCombatEvent(this.getCombatTracker(), SPacketCombatEvent.Event.END_COMBAT));
   }

   protected void onInsideBlock(IBlockState p_191955_1_) {
      CriteriaTriggers.ENTER_BLOCK.trigger(this, p_191955_1_);
   }

   protected CooldownTracker createCooldownTracker() {
      return new CooldownTrackerServer(this);
   }

   public void tick() {
      this.interactionManager.tick();
      --this.respawnInvulnerabilityTicks;
      if (this.hurtResistantTime > 0) {
         --this.hurtResistantTime;
      }

      this.openContainer.detectAndSendChanges();
      if (!this.world.isRemote && !this.openContainer.canInteractWith(this)) {
         this.closeScreen();
         this.openContainer = this.inventoryContainer;
      }

      while(!this.entityRemoveQueue.isEmpty()) {
         int i = Math.min(this.entityRemoveQueue.size(), Integer.MAX_VALUE);
         int[] aint = new int[i];
         Iterator<Integer> iterator = this.entityRemoveQueue.iterator();
         int j = 0;

         while(iterator.hasNext() && j < i) {
            aint[j++] = iterator.next();
            iterator.remove();
         }

         this.connection.sendPacket(new SPacketDestroyEntities(aint));
      }

      Entity entity = this.getSpectatingEntity();
      if (entity != this) {
         if (entity.isEntityAlive()) {
            this.setPositionAndRotation(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
            this.server.getPlayerList().serverUpdateMovingPlayer(this);
            if (this.isSneaking()) {
               this.setSpectatingEntity(this);
            }
         } else {
            this.setSpectatingEntity(this);
         }
      }

      CriteriaTriggers.TICK.trigger(this);
      if (this.levitationStartPos != null) {
         CriteriaTriggers.LEVITATION.trigger(this, this.levitationStartPos, this.ticksExisted - this.levitatingSince);
      }

      this.advancements.flushDirty(this);
   }

   public void playerTick() {
      try {
         super.tick();

         for(int i = 0; i < this.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.inventory.getStackInSlot(i);
            if (itemstack.getItem().isComplex()) {
               Packet<?> packet = ((ItemMapBase)itemstack.getItem()).getUpdatePacket(itemstack, this.world, this);
               if (packet != null) {
                  this.connection.sendPacket(packet);
               }
            }
         }

         if (this.getHealth() != this.lastHealth || this.lastFoodLevel != this.foodStats.getFoodLevel() || this.foodStats.getSaturationLevel() == 0.0F != this.wasHungry) {
            this.connection.sendPacket(new SPacketUpdateHealth(this.getHealth(), this.foodStats.getFoodLevel(), this.foodStats.getSaturationLevel(),this.foodStats.getMaxFoodLevel()));
            this.lastHealth = this.getHealth();
            this.lastFoodLevel = this.foodStats.getFoodLevel();
            this.wasHungry = this.foodStats.getSaturationLevel() == 0.0F;
         }

         if (this.getHealth() + this.getAbsorptionAmount() != this.lastHealthScore) {
            this.lastHealthScore = this.getHealth() + this.getAbsorptionAmount();
            this.func_184849_a(ScoreCriteria.field_96638_f, MathHelper.ceil(this.lastHealthScore));
         }

         if (this.foodStats.getFoodLevel() != this.lastFoodScore) {
            this.lastFoodScore = this.foodStats.getFoodLevel();
            this.func_184849_a(ScoreCriteria.field_186698_h, MathHelper.ceil((float)this.lastFoodScore));
         }

         if (this.getAir() != this.lastAirScore) {
            this.lastAirScore = this.getAir();
            this.func_184849_a(ScoreCriteria.field_186699_i, MathHelper.ceil((float)this.lastAirScore));
         }

         if (this.getTotalArmorValue() != this.lastArmorScore) {
            this.lastArmorScore = this.getTotalArmorValue();
            this.func_184849_a(ScoreCriteria.field_186700_j, MathHelper.ceil((float)this.lastArmorScore));
         }

         if (this.experienceTotal != this.lastExperienceScore) {
            this.lastExperienceScore = this.experienceTotal;
            this.func_184849_a(ScoreCriteria.field_186701_k, MathHelper.ceil((float)this.lastExperienceScore));
         }

         if (this.experienceLevel != this.lastLevelScore) {
            this.lastLevelScore = this.experienceLevel;
            this.func_184849_a(ScoreCriteria.field_186702_l, MathHelper.ceil((float)this.lastLevelScore));
         }

         if (this.experienceTotal != this.lastExperience) {
            this.lastExperience = this.experienceTotal;
            this.connection.sendPacket(new SPacketSetExperience(this.experience, this.experienceTotal, this.experienceLevel));
         }

         if (this.ticksExisted % 20 == 0) {
            CriteriaTriggers.LOCATION.trigger(this);
         }

      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking player");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Player being ticked");
         this.fillCrashReport(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   private void func_184849_a(ScoreCriteria p_184849_1_, int p_184849_2_) {
      this.getWorldScoreboard().func_197893_a(p_184849_1_, this.getScoreboardName(), (p_195397_1_) -> {
         p_195397_1_.setScorePoints(p_184849_2_);
      });
   }

   public void onDeath(DamageSource p_70645_1_) {
      boolean flag = this.world.getGameRules().getBoolean("showDeathMessages");
      if (flag) {
         ITextComponent itextcomponent = this.getCombatTracker().getDeathMessage();
         this.connection.sendPacket(new SPacketCombatEvent(this.getCombatTracker(), SPacketCombatEvent.Event.ENTITY_DIED, itextcomponent), (p_212356_2_) -> {
            if (!p_212356_2_.isSuccess()) {
               int i = 256;
               String s = itextcomponent.func_212636_a(256);
               ITextComponent itextcomponent1 = new TextComponentTranslation("death.attack.message_too_long", (new TextComponentString(s)).applyTextStyle(TextFormatting.YELLOW));
               ITextComponent itextcomponent2 = (new TextComponentTranslation("death.attack.even_more_magic", this.getDisplayName())).applyTextStyle((p_212357_1_) -> {
                  p_212357_1_.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, itextcomponent1));
               });
               this.connection.sendPacket(new SPacketCombatEvent(this.getCombatTracker(), SPacketCombatEvent.Event.ENTITY_DIED, itextcomponent2));
            }

         });
         Team team = this.getTeam();
         if (team != null && team.getDeathMessageVisibility() != Team.EnumVisible.ALWAYS) {
            if (team.getDeathMessageVisibility() == Team.EnumVisible.HIDE_FOR_OTHER_TEAMS) {
               this.server.getPlayerList().sendMessageToAllTeamMembers(this, itextcomponent);
            } else if (team.getDeathMessageVisibility() == Team.EnumVisible.HIDE_FOR_OWN_TEAM) {
               this.server.getPlayerList().sendMessageToTeamOrAllPlayers(this, itextcomponent);
            }
         } else {
            this.server.getPlayerList().sendMessage(itextcomponent);
         }
      } else {
         this.connection.sendPacket(new SPacketCombatEvent(this.getCombatTracker(), SPacketCombatEvent.Event.ENTITY_DIED));
      }

      this.spawnShoulderEntities();
      if (!this.world.getGameRules().getBoolean("keepInventory") && !this.isSpectator()) {
         this.destroyVanishingCursedItems();
         this.inventory.dropAllItems();
      }

      this.getWorldScoreboard().func_197893_a(ScoreCriteria.field_96642_c, this.getScoreboardName(), Score::incrementScore);
      EntityLivingBase entitylivingbase = this.getAttackingEntity();
      if (entitylivingbase != null) {
         this.func_71029_a(StatList.ENTITY_KILLED_BY.func_199076_b(entitylivingbase.getType()));
         entitylivingbase.awardKillScore(this, this.scoreValue, p_70645_1_);
      }

      this.addStat(StatList.DEATHS);
      this.func_175145_a(StatList.CUSTOM.func_199076_b(StatList.TIME_SINCE_DEATH));
      this.func_175145_a(StatList.CUSTOM.func_199076_b(StatList.TIME_SINCE_REST));
      this.extinguish();
      this.setFlag(0, false);
      this.getCombatTracker().reset();
   }

   public void awardKillScore(Entity p_191956_1_, int p_191956_2_, DamageSource p_191956_3_) {
      if (p_191956_1_ != this) {
         super.awardKillScore(p_191956_1_, p_191956_2_, p_191956_3_);
         this.addScore(p_191956_2_);
         String s = this.getScoreboardName();
         String s1 = p_191956_1_.getScoreboardName();
         this.getWorldScoreboard().func_197893_a(ScoreCriteria.field_96640_e, s, Score::incrementScore);
         if (p_191956_1_ instanceof EntityPlayer) {
            this.addStat(StatList.PLAYER_KILLS);
            this.getWorldScoreboard().func_197893_a(ScoreCriteria.field_96639_d, s, Score::incrementScore);
         } else {
            this.addStat(StatList.MOB_KILLS);
         }

         this.func_195398_a(s, s1, ScoreCriteria.field_197913_m);
         this.func_195398_a(s1, s, ScoreCriteria.field_197914_n);
         CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger(this, p_191956_1_, p_191956_3_);
      }
   }

   private void func_195398_a(String p_195398_1_, String p_195398_2_, ScoreCriteria[] p_195398_3_) {
      ScorePlayerTeam scoreplayerteam = this.getWorldScoreboard().getPlayersTeam(p_195398_2_);
      if (scoreplayerteam != null) {
         int i = scoreplayerteam.getColor().getColorIndex();
         if (i >= 0 && i < p_195398_3_.length) {
            this.getWorldScoreboard().func_197893_a(p_195398_3_[i], p_195398_1_, Score::incrementScore);
         }
      }

   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         boolean flag = this.server.isDedicatedServer() && this.canPlayersAttack() && "fall".equals(p_70097_1_.damageType);
         if (!flag && this.respawnInvulnerabilityTicks > 0 && p_70097_1_ != DamageSource.OUT_OF_WORLD) {
            return false;
         } else {
            if (p_70097_1_ instanceof EntityDamageSource) {
               Entity entity = p_70097_1_.getTrueSource();
               if (entity instanceof EntityPlayer && !this.canAttackPlayer((EntityPlayer)entity)) {
                  return false;
               }

               if (entity instanceof EntityArrow) {
                  EntityArrow entityarrow = (EntityArrow)entity;
                  Entity entity1 = entityarrow.func_212360_k();
                  if (entity1 instanceof EntityPlayer && !this.canAttackPlayer((EntityPlayer)entity1)) {
                     return false;
                  }
               }
            }

            return super.attackEntityFrom(p_70097_1_, p_70097_2_);
         }
      }
   }

   public boolean canAttackPlayer(EntityPlayer p_96122_1_) {
      return !this.canPlayersAttack() ? false : super.canAttackPlayer(p_96122_1_);
   }

   private boolean canPlayersAttack() {
      return this.server.isPVPEnabled();
   }

   @Nullable
   public Entity func_212321_a(DimensionType p_212321_1_) {
      this.invulnerableDimensionChange = true;
      if (this.dimension == DimensionType.OVERWORLD && p_212321_1_ == DimensionType.NETHER) {
         this.enteredNetherPosition = new Vec3d(this.posX, this.posY, this.posZ);
      } else if (this.dimension != DimensionType.NETHER && p_212321_1_ != DimensionType.OVERWORLD) {
         this.enteredNetherPosition = null;
      }

      if (this.dimension == DimensionType.THE_END && p_212321_1_ == DimensionType.THE_END) {
         this.world.removeEntity(this);
         if (!this.queuedEndExit) {
            this.queuedEndExit = true;
            this.connection.sendPacket(new SPacketChangeGameState(4, this.seenCredits ? 0.0F : 1.0F));
            this.seenCredits = true;
         }

         return this;
      } else {
         if (this.dimension == DimensionType.OVERWORLD && p_212321_1_ == DimensionType.THE_END) {
            p_212321_1_ = DimensionType.THE_END;
         }

         this.server.getPlayerList().func_187242_a(this, p_212321_1_);
         this.connection.sendPacket(new SPacketEffect(1032, BlockPos.ORIGIN, 0, false));
         this.lastExperience = -1;
         this.lastHealth = -1.0F;
         this.lastFoodLevel = -1;
         return this;
      }
   }

   public boolean isSpectatedByPlayer(EntityPlayerMP p_174827_1_) {
      if (p_174827_1_.isSpectator()) {
         return this.getSpectatingEntity() == this;
      } else {
         return this.isSpectator() ? false : super.isSpectatedByPlayer(p_174827_1_);
      }
   }

   private void sendTileEntityUpdate(TileEntity p_147097_1_) {
      if (p_147097_1_ != null) {
         SPacketUpdateTileEntity spacketupdatetileentity = p_147097_1_.getUpdatePacket();
         if (spacketupdatetileentity != null) {
            this.connection.sendPacket(spacketupdatetileentity);
         }
      }

   }

   public void onItemPickup(Entity p_71001_1_, int p_71001_2_) {
      super.onItemPickup(p_71001_1_, p_71001_2_);
      this.openContainer.detectAndSendChanges();
   }

   public EntityPlayer.SleepResult trySleep(BlockPos bedPos) {
      EntityPlayer.SleepResult sleepResult = super.trySleep(bedPos);
      if (sleepResult == EntityPlayer.SleepResult.OK) {
         this.addStat(StatList.SLEEP_IN_BED);
         Packet<?> packet = new SPacketUseBed(this, bedPos);
         this.getServerWorld().getEntityTracker().sendToTracking(this, packet);
         this.connection.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         this.connection.sendPacket(packet);
         CriteriaTriggers.SLEPT_IN_BED.trigger(this);
      }

      return sleepResult;
   }

   public void wakeUpPlayer(boolean p_70999_1_, boolean p_70999_2_, boolean p_70999_3_) {
      if (this.isPlayerSleeping()) {
         this.getServerWorld().getEntityTracker().sendToTrackingAndSelf(this, new SPacketAnimation(this, 2));
      }

      super.wakeUpPlayer(p_70999_1_, p_70999_2_, p_70999_3_);
      if (this.connection != null) {
         this.connection.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
      }

   }

   public boolean startRiding(Entity p_184205_1_, boolean p_184205_2_) {
      Entity entity = this.getRidingEntity();
      if (!super.startRiding(p_184205_1_, p_184205_2_)) {
         return false;
      } else {
         Entity entity1 = this.getRidingEntity();
         if (entity1 != entity && this.connection != null) {
            this.connection.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         }

         return true;
      }
   }

   public void dismountRidingEntity() {
      Entity entity = this.getRidingEntity();
      super.dismountRidingEntity();
      Entity entity1 = this.getRidingEntity();
      if (entity1 != entity && this.connection != null) {
         this.connection.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
      }

   }

   public boolean isInvulnerableTo(DamageSource p_180431_1_) {
      return super.isInvulnerableTo(p_180431_1_) || this.isInvulnerableDimensionChange();
   }

   protected void updateFallState(double p_184231_1_, boolean p_184231_3_, IBlockState p_184231_4_, BlockPos p_184231_5_) {
   }

   protected void frostWalk(BlockPos p_184594_1_) {
      if (!this.isSpectator()) {
         super.frostWalk(p_184594_1_);
      }

   }

   public void handleFalling(double p_71122_1_, boolean p_71122_3_) {
      int i = MathHelper.floor(this.posX);
      int j = MathHelper.floor(this.posY - (double)0.2F);
      int k = MathHelper.floor(this.posZ);
      BlockPos blockpos = new BlockPos(i, j, k);
      IBlockState iblockstate = this.world.getBlockState(blockpos);
      if (iblockstate.isAir()) {
         BlockPos blockpos1 = blockpos.down();
         IBlockState iblockstate1 = this.world.getBlockState(blockpos1);
         Block block = iblockstate1.getBlock();
         if (block instanceof BlockFence || block instanceof BlockWall || block instanceof BlockFenceGate) {
            blockpos = blockpos1;
            iblockstate = iblockstate1;
         }
      }

      super.updateFallState(p_71122_1_, p_71122_3_, iblockstate, blockpos);
   }

   public void openEditSign(TileEntitySign p_175141_1_) {
      p_175141_1_.setPlayer(this);
      this.connection.sendPacket(new SPacketSignEditorOpen(p_175141_1_.getPos()));
   }

   public void getNextWindowId() {
      this.currentWindowId = this.currentWindowId % 100 + 1;
   }

   public void displayGui(IInteractionObject p_180468_1_) {
      if (p_180468_1_ instanceof ILootContainer && ((ILootContainer)p_180468_1_).getLootTable() != null && this.isSpectator()) {
         this.sendStatusMessage((new TextComponentTranslation("container.spectatorCantOpen")).applyTextStyle(TextFormatting.RED), true);
      } else {
         this.getNextWindowId();
         this.connection.sendPacket(new SPacketOpenWindow(this.currentWindowId, p_180468_1_.getGuiID(), p_180468_1_.getDisplayName()));
         this.openContainer = p_180468_1_.createContainer(this.inventory, this);
         this.openContainer.windowId = this.currentWindowId;
         this.openContainer.addListener(this);
      }
   }

   public void displayGUIChest(IInventory p_71007_1_) {
      if (p_71007_1_ instanceof ILootContainer && ((ILootContainer)p_71007_1_).getLootTable() != null && this.isSpectator()) {
         this.sendStatusMessage((new TextComponentTranslation("container.spectatorCantOpen")).applyTextStyle(TextFormatting.RED), true);
      } else {
         if (this.openContainer != this.inventoryContainer) {
            this.closeScreen();
         }

         if (p_71007_1_ instanceof ILockableContainer) {
            ILockableContainer ilockablecontainer = (ILockableContainer)p_71007_1_;
            if (ilockablecontainer.isLocked() && !this.canOpen(ilockablecontainer.getLockCode()) && !this.isSpectator()) {
               this.connection.sendPacket(new SPacketChat(new TextComponentTranslation("container.isLocked", p_71007_1_.getDisplayName()), ChatType.GAME_INFO));
               this.connection.sendPacket(new SPacketSoundEffect(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, this.posX, this.posY, this.posZ, 1.0F, 1.0F));
               return;
            }
         }

         this.getNextWindowId();
         if (p_71007_1_ instanceof IInteractionObject) {
            this.connection.sendPacket(new SPacketOpenWindow(this.currentWindowId, ((IInteractionObject)p_71007_1_).getGuiID(), p_71007_1_.getDisplayName(), p_71007_1_.getSizeInventory()));
            this.openContainer = ((IInteractionObject)p_71007_1_).createContainer(this.inventory, this);
         } else {
            this.connection.sendPacket(new SPacketOpenWindow(this.currentWindowId, "minecraft:container", p_71007_1_.getDisplayName(), p_71007_1_.getSizeInventory()));
            this.openContainer = new ContainerChest(this.inventory, p_71007_1_, this);
         }

         this.openContainer.windowId = this.currentWindowId;
         this.openContainer.addListener(this);
      }
   }

   public void displayVillagerTradeGui(IMerchant p_180472_1_) {
      this.getNextWindowId();
      this.openContainer = new ContainerMerchant(this.inventory, p_180472_1_, this.world);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.addListener(this);
      IInventory iinventory = ((ContainerMerchant)this.openContainer).getMerchantInventory();
      ITextComponent itextcomponent = p_180472_1_.getDisplayName();
      this.connection.sendPacket(new SPacketOpenWindow(this.currentWindowId, "minecraft:villager", itextcomponent, iinventory.getSizeInventory()));
      MerchantRecipeList merchantrecipelist = p_180472_1_.getRecipes(this);
      if (merchantrecipelist != null) {
         PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
         packetbuffer.writeInt(this.currentWindowId);
         merchantrecipelist.writeToBuf(packetbuffer);
         this.connection.sendPacket(new SPacketCustomPayload(SPacketCustomPayload.TRADER_LIST, packetbuffer));
      }

   }

   public void openGuiHorseInventory(AbstractHorse p_184826_1_, IInventory p_184826_2_) {
      if (this.openContainer != this.inventoryContainer) {
         this.closeScreen();
      }

      this.getNextWindowId();
      this.connection.sendPacket(new SPacketOpenWindow(this.currentWindowId, "EntityHorse", p_184826_2_.getDisplayName(), p_184826_2_.getSizeInventory(), p_184826_1_.getEntityId()));
      this.openContainer = new ContainerHorseInventory(this.inventory, p_184826_2_, p_184826_1_, this);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.addListener(this);
   }

   public void openBook(ItemStack p_184814_1_, EnumHand p_184814_2_) {
      Item item = p_184814_1_.getItem();
      if (item == Items.WRITTEN_BOOK) {
         PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
         packetbuffer.writeEnumValue(p_184814_2_);
         this.connection.sendPacket(new SPacketCustomPayload(SPacketCustomPayload.BOOK_OPEN, packetbuffer));
      }

   }

   public void displayGuiCommandBlock(TileEntityCommandBlock p_184824_1_) {
      p_184824_1_.setSendToClient(true);
      this.sendTileEntityUpdate(p_184824_1_);
   }

   public void sendSlotContents(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_) {
      if (!(p_71111_1_.getSlot(p_71111_2_) instanceof SlotCrafting)) {
         if (p_71111_1_ == this.inventoryContainer) {
            CriteriaTriggers.INVENTORY_CHANGED.trigger(this, this.inventory);
         }

         if (!this.isChangingQuantityOnly) {
            this.connection.sendPacket(new SPacketSetSlot(p_71111_1_.windowId, p_71111_2_, p_71111_3_));
         }
      }
   }

   public void sendContainerToPlayer(Container p_71120_1_) {
      this.sendAllContents(p_71120_1_, p_71120_1_.getInventory());
   }

   public void sendAllContents(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_) {
      this.connection.sendPacket(new SPacketWindowItems(p_71110_1_.windowId, p_71110_2_));
      this.connection.sendPacket(new SPacketSetSlot(-1, -1, this.inventory.getItemStack()));
   }

   public void sendWindowProperty(Container p_71112_1_, int p_71112_2_, int p_71112_3_) {
      this.connection.sendPacket(new SPacketWindowProperty(p_71112_1_.windowId, p_71112_2_, p_71112_3_));
   }

   public void sendAllWindowProperties(Container p_175173_1_, IInventory p_175173_2_) {
      for(int i = 0; i < p_175173_2_.getFieldCount(); ++i) {
         this.connection.sendPacket(new SPacketWindowProperty(p_175173_1_.windowId, i, p_175173_2_.getField(i)));
      }

   }

   public void closeScreen() {
      this.connection.sendPacket(new SPacketCloseWindow(this.openContainer.windowId));
      this.closeContainer();
   }

   public void updateHeldItem() {
      if (!this.isChangingQuantityOnly) {
         this.connection.sendPacket(new SPacketSetSlot(-1, -1, this.inventory.getItemStack()));
      }
   }

   public void closeContainer() {
      this.openContainer.onContainerClosed(this);
      this.openContainer = this.inventoryContainer;
   }

   public void setEntityActionState(float p_110430_1_, float p_110430_2_, boolean p_110430_3_, boolean p_110430_4_) {
      if (this.isRiding()) {
         if (p_110430_1_ >= -1.0F && p_110430_1_ <= 1.0F) {
            this.moveStrafing = p_110430_1_;
         }

         if (p_110430_2_ >= -1.0F && p_110430_2_ <= 1.0F) {
            this.moveForward = p_110430_2_;
         }

         this.isJumping = p_110430_3_;
         this.setSneaking(p_110430_4_);
      }

   }

   public void func_71064_a(Stat<?> p_71064_1_, int p_71064_2_) {
      this.stats.func_150871_b(this, p_71064_1_, p_71064_2_);
      this.getWorldScoreboard().func_197893_a(p_71064_1_, this.getScoreboardName(), (p_195396_1_) -> {
         p_195396_1_.increaseScore(p_71064_2_);
      });
   }

   public void func_175145_a(Stat<?> p_175145_1_) {
      this.stats.func_150873_a(this, p_175145_1_, 0);
      this.getWorldScoreboard().func_197893_a(p_175145_1_, this.getScoreboardName(), Score::func_197891_c);
   }

   public int unlockRecipes(Collection<IRecipe> p_195065_1_) {
      return this.recipeBook.add(p_195065_1_, this);
   }

   public void unlockRecipes(ResourceLocation[] p_193102_1_) {
      List<IRecipe> list = Lists.newArrayList();

      for(ResourceLocation resourcelocation : p_193102_1_) {
         IRecipe irecipe = this.server.getRecipeManager().getRecipe(resourcelocation);
         if (irecipe != null) {
            list.add(irecipe);
         }
      }

      this.unlockRecipes(list);
   }

   public int resetRecipes(Collection<IRecipe> p_195069_1_) {
      return this.recipeBook.remove(p_195069_1_, this);
   }

   public void func_195068_e(int p_195068_1_) {
      super.func_195068_e(p_195068_1_);
      this.lastExperience = -1;
   }

   public void mountEntityAndWakeUp() {
      this.disconnected = true;
      this.removePassengers();
      if (this.sleeping) {
         this.wakeUpPlayer(true, false, false);
      }

   }

   public boolean hasDisconnected() {
      return this.disconnected;
   }

   public void setPlayerHealthUpdated() {
      this.lastHealth = -1.0E8F;
   }

   public void sendStatusMessage(ITextComponent p_146105_1_, boolean p_146105_2_) {
      this.connection.sendPacket(new SPacketChat(p_146105_1_, p_146105_2_ ? ChatType.GAME_INFO : ChatType.CHAT));
   }

   protected void onItemUseFinish() {
      if (!this.activeItemStack.isEmpty() && this.isHandActive()) {
         this.connection.sendPacket(new SPacketEntityStatus(this, (byte)9));
         super.onItemUseFinish();
      }

   }

   public void func_200602_a(EntityAnchorArgument.Type p_200602_1_, Vec3d p_200602_2_) {
      super.func_200602_a(p_200602_1_, p_200602_2_);
      this.connection.sendPacket(new SPacketPlayerLook(p_200602_1_, p_200602_2_.x, p_200602_2_.y, p_200602_2_.z));
   }

   public void func_200618_a(EntityAnchorArgument.Type p_200618_1_, Entity p_200618_2_, EntityAnchorArgument.Type p_200618_3_) {
      Vec3d vec3d = p_200618_3_.func_201017_a(p_200618_2_);
      super.func_200602_a(p_200618_1_, vec3d);
      this.connection.sendPacket(new SPacketPlayerLook(p_200618_1_, p_200618_2_, p_200618_3_));
   }

   public void copyFrom(EntityPlayerMP p_193104_1_, boolean p_193104_2_) {
      if (p_193104_2_) {
         this.inventory.copyInventory(p_193104_1_.inventory);
         this.setHealth(p_193104_1_.getHealth());
         this.foodStats = p_193104_1_.foodStats;
         this.experienceLevel = p_193104_1_.experienceLevel;
         this.experienceTotal = p_193104_1_.experienceTotal;
         this.experience = p_193104_1_.experience;
         this.setScore(p_193104_1_.getScore());
         this.lastPortalPos = p_193104_1_.lastPortalPos;
         this.lastPortalVec = p_193104_1_.lastPortalVec;
         this.teleportDirection = p_193104_1_.teleportDirection;
      } else if (this.world.getGameRules().getBoolean("keepInventory") || p_193104_1_.isSpectator()) {
         this.inventory.copyInventory(p_193104_1_.inventory);
         this.experienceLevel = p_193104_1_.experienceLevel;
         this.experienceTotal = p_193104_1_.experienceTotal;
         this.experience = p_193104_1_.experience;
         this.setScore(p_193104_1_.getScore());
      }

      this.xpSeed = p_193104_1_.xpSeed;
      this.enderChest = p_193104_1_.enderChest;
      this.getDataManager().set(PLAYER_MODEL_FLAG, p_193104_1_.getDataManager().get(PLAYER_MODEL_FLAG));
      this.lastExperience = -1;
      this.lastHealth = -1.0F;
      this.lastFoodLevel = -1;
      this.recipeBook.copyFrom(p_193104_1_.recipeBook);
      this.entityRemoveQueue.addAll(p_193104_1_.entityRemoveQueue);
      this.seenCredits = p_193104_1_.seenCredits;
      this.enteredNetherPosition = p_193104_1_.enteredNetherPosition;
      this.setLeftShoulderEntity(p_193104_1_.getLeftShoulderEntity());
      this.setRightShoulderEntity(p_193104_1_.getRightShoulderEntity());
   }

   protected void onNewPotionEffect(PotionEffect p_70670_1_) {
      super.onNewPotionEffect(p_70670_1_);
      this.connection.sendPacket(new SPacketEntityEffect(this.getEntityId(), p_70670_1_));
      if (p_70670_1_.getPotion() == MobEffects.LEVITATION) {
         this.levitatingSince = this.ticksExisted;
         this.levitationStartPos = new Vec3d(this.posX, this.posY, this.posZ);
      }

      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   protected void onChangedPotionEffect(PotionEffect p_70695_1_, boolean p_70695_2_) {
      super.onChangedPotionEffect(p_70695_1_, p_70695_2_);
      this.connection.sendPacket(new SPacketEntityEffect(this.getEntityId(), p_70695_1_));
      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   protected void onFinishedPotionEffect(PotionEffect p_70688_1_) {
      super.onFinishedPotionEffect(p_70688_1_);
      this.connection.sendPacket(new SPacketRemoveEntityEffect(this.getEntityId(), p_70688_1_.getPotion()));
      if (p_70688_1_.getPotion() == MobEffects.LEVITATION) {
         this.levitationStartPos = null;
      }

      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   public void setPositionAndUpdate(double p_70634_1_, double p_70634_3_, double p_70634_5_) {
      this.connection.setPlayerLocation(p_70634_1_, p_70634_3_, p_70634_5_, this.rotationYaw, this.rotationPitch);
   }

   public void onCriticalHit(Entity p_71009_1_) {
      this.getServerWorld().getEntityTracker().sendToTrackingAndSelf(this, new SPacketAnimation(p_71009_1_, 4));
   }

   public void onEnchantmentCritical(Entity p_71047_1_) {
      this.getServerWorld().getEntityTracker().sendToTrackingAndSelf(this, new SPacketAnimation(p_71047_1_, 5));
   }

   public void sendPlayerAbilities() {
      if (this.connection != null) {
         this.connection.sendPacket(new SPacketPlayerAbilities(this.capabilities));
         this.updatePotionMetadata();
      }
   }

   public WorldServer getServerWorld() {
      return (WorldServer)this.world;
   }

   public void setGameType(GameType p_71033_1_) {
      this.interactionManager.setGameType(p_71033_1_);
      this.connection.sendPacket(new SPacketChangeGameState(3, (float)p_71033_1_.getID()));
      if (p_71033_1_ == GameType.SPECTATOR) {
         this.spawnShoulderEntities();
         this.dismountRidingEntity();
      } else {
         this.setSpectatingEntity(this);
      }

      this.sendPlayerAbilities();
      this.markPotionsDirty();
   }

   public boolean isSpectator() {
      return this.interactionManager.getGameType() == GameType.SPECTATOR;
   }

   public boolean isCreative() {
      return this.interactionManager.getGameType() == GameType.CREATIVE;
   }

   public void sendMessage(ITextComponent p_145747_1_) {
      this.sendMessage(p_145747_1_, ChatType.SYSTEM);
   }

   public void sendMessage(ITextComponent p_195395_1_, ChatType p_195395_2_) {
      this.connection.sendPacket(new SPacketChat(p_195395_1_, p_195395_2_), (p_211144_3_) -> {
         if (!p_211144_3_.isSuccess() && (p_195395_2_ == ChatType.GAME_INFO || p_195395_2_ == ChatType.SYSTEM)) {
            int i = 256;
            String s = p_195395_1_.func_212636_a(256);
            ITextComponent itextcomponent = (new TextComponentString(s)).applyTextStyle(TextFormatting.YELLOW);
            this.connection.sendPacket(new SPacketChat((new TextComponentTranslation("multiplayer.message_not_delivered", itextcomponent)).applyTextStyle(TextFormatting.RED), ChatType.SYSTEM));
         }

      });
   }

   public String getPlayerIP() {
      String s = this.connection.netManager.getRemoteAddress().toString();
      s = s.substring(s.indexOf("/") + 1);
      s = s.substring(0, s.indexOf(":"));
      return s;
   }

   public void handleClientSettings(CPacketClientSettings p_147100_1_) {
      this.language = p_147100_1_.getLang();
      this.chatVisibility = p_147100_1_.getChatVisibility();
      this.chatColours = p_147100_1_.isColorsEnabled();
      this.getDataManager().set(PLAYER_MODEL_FLAG, (byte)p_147100_1_.getModelPartFlags());
      this.getDataManager().set(MAIN_HAND, (byte)(p_147100_1_.getMainHand() == EnumHandSide.LEFT ? 0 : 1));
   }

   public EntityPlayer.EnumChatVisibility getChatVisibility() {
      return this.chatVisibility;
   }

   public void loadResourcePack(String p_175397_1_, String p_175397_2_) {
      this.connection.sendPacket(new SPacketResourcePackSend(p_175397_1_, p_175397_2_));
   }

   protected int getPermissionLevel() {
      return this.server.getPermissionLevel(this.getGameProfile());
   }

   public void markPlayerActive() {
      this.playerLastActiveTime = Util.milliTime();
   }

   public StatisticsManagerServer getStats() {
      return this.stats;
   }

   public ServerRecipeBook getRecipeBook() {
      return this.recipeBook;
   }

   public void removeEntity(Entity p_152339_1_) {
      if (p_152339_1_ instanceof EntityPlayer) {
         this.connection.sendPacket(new SPacketDestroyEntities(p_152339_1_.getEntityId()));
      } else {
         this.entityRemoveQueue.add(p_152339_1_.getEntityId());
      }

   }

   public void addEntity(Entity p_184848_1_) {
      this.entityRemoveQueue.remove(Integer.valueOf(p_184848_1_.getEntityId()));
   }

   protected void updatePotionMetadata() {
      if (this.isSpectator()) {
         this.resetPotionEffectMetadata();
         this.setInvisible(true);
      } else {
         super.updatePotionMetadata();
      }

      this.getServerWorld().getEntityTracker().updateVisibility(this);
   }

   public Entity getSpectatingEntity() {
      return (Entity)(this.spectatingEntity == null ? this : this.spectatingEntity);
   }

   public void setSpectatingEntity(Entity p_175399_1_) {
      Entity entity = this.getSpectatingEntity();
      this.spectatingEntity = (Entity)(p_175399_1_ == null ? this : p_175399_1_);
      if (entity != this.spectatingEntity) {
         this.connection.sendPacket(new SPacketCamera(this.spectatingEntity));
         this.setPositionAndUpdate(this.spectatingEntity.posX, this.spectatingEntity.posY, this.spectatingEntity.posZ);
      }

   }

   protected void decrementTimeUntilPortal() {
      if (this.timeUntilPortal > 0 && !this.invulnerableDimensionChange) {
         --this.timeUntilPortal;
      }

   }

   public void attackTargetEntityWithCurrentItem(Entity p_71059_1_) {
      if (this.interactionManager.getGameType() == GameType.SPECTATOR) {
         this.setSpectatingEntity(p_71059_1_);
      } else {
         super.attackTargetEntityWithCurrentItem(p_71059_1_);
      }

   }

   public long getLastActiveTime() {
      return this.playerLastActiveTime;
   }

   @Nullable
   public ITextComponent getTabListDisplayName() {
      return null;
   }

   public void swingArm(EnumHand p_184609_1_) {
      super.swingArm(p_184609_1_);
      this.resetCooldown();
   }

   public boolean isInvulnerableDimensionChange() {
      return this.invulnerableDimensionChange;
   }

   public void clearInvulnerableDimensionChange() {
      this.invulnerableDimensionChange = false;
   }

   public void setElytraFlying() {
      this.setFlag(7, true);
   }

   public void clearElytraFlying() {
      this.setFlag(7, true);
      this.setFlag(7, false);
   }

   public PlayerAdvancements getAdvancements() {
      return this.advancements;
   }

   @Nullable
   public Vec3d getEnteredNetherPosition() {
      return this.enteredNetherPosition;
   }

   public void teleport(WorldServer p_200619_1_, double p_200619_2_, double p_200619_4_, double p_200619_6_, float p_200619_8_, float p_200619_9_) {
      this.setSpectatingEntity(this);
      this.dismountRidingEntity();
      if (p_200619_1_ == this.world) {
         this.connection.setPlayerLocation(p_200619_2_, p_200619_4_, p_200619_6_, p_200619_8_, p_200619_9_);
      } else {
         WorldServer worldserver = this.getServerWorld();
         this.dimension = p_200619_1_.dimension.getType();
         this.connection.sendPacket(new SPacketRespawn(this.dimension, worldserver.getDifficulty(), worldserver.getWorldInfo().getTerrainType(), this.interactionManager.getGameType()));
         this.server.getPlayerList().updatePermissionLevel(this);
         worldserver.removeEntityDangerously(this);
         this.isDead = false;
         this.setLocationAndAngles(p_200619_2_, p_200619_4_, p_200619_6_, p_200619_8_, p_200619_9_);
         if (this.isEntityAlive()) {
            worldserver.updateEntityWithOptionalForce(this, false);
            p_200619_1_.spawnEntity(this);
            p_200619_1_.updateEntityWithOptionalForce(this, false);
         }

         this.setWorld(p_200619_1_);
         this.server.getPlayerList().preparePlayer(this, worldserver);
         this.connection.setPlayerLocation(p_200619_2_, p_200619_4_, p_200619_6_, p_200619_8_, p_200619_9_);
         this.interactionManager.setWorld(p_200619_1_);
         this.server.getPlayerList().updateTimeAndWeatherForPlayer(this, p_200619_1_);
         this.server.getPlayerList().syncPlayerInventory(this);
      }

   }
}
