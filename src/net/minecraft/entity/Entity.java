package net.minecraft.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.BlockWall;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.INameable;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity implements INameable, ICommandSource {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final List<ItemStack> EMPTY_EQUIPMENT = Collections.emptyList();
   private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   private static double renderDistanceWeight = 1.0D;
   private static int nextEntityID;
   private final EntityType<?> type;
   private int entityId;
   public boolean preventEntitySpawning;
   private final List<Entity> riddenByEntities;
   protected int rideCooldown;
   private Entity ridingEntity;
   public boolean forceSpawn;
   public World world;
   public double prevPosX;
   public double prevPosY;
   public double prevPosZ;
   public double posX;
   public double posY;
   public double posZ;
   public double motionX;
   public double motionY;
   public double motionZ;
   public float rotationYaw;
   public float rotationPitch;
   public float prevRotationYaw;
   public float prevRotationPitch;
   private AxisAlignedBB boundingBox;
   public boolean onGround;
   public boolean collidedHorizontally;
   public boolean collidedVertically;
   public boolean collided;
   public boolean velocityChanged;
   protected boolean isInWeb;
   private boolean isOutsideBorder;
   public boolean isDead;
   public float width;
   public float height;
   public float prevDistanceWalkedModified;
   public float distanceWalkedModified;
   public float distanceWalkedOnStepModified;
   public float fallDistance;
   private float nextStepDistance;
   private float nextFlap;
   public double lastTickPosX;
   public double lastTickPosY;
   public double lastTickPosZ;
   public float stepHeight;
   public boolean noClip;
   public float entityCollisionReduction;
   protected Random rand;
   public int ticksExisted;
   private int fire;
   protected boolean inWater;
   protected double submergedHeight;
   protected boolean eyesInWater;
   public int hurtResistantTime;
   protected boolean firstUpdate;
   protected boolean isImmuneToFire;
   protected EntityDataManager dataManager;
   protected static final DataParameter<Byte> FLAGS = EntityDataManager.createKey(Entity.class, DataSerializers.BYTE);
   private static final DataParameter<Integer> AIR = EntityDataManager.createKey(Entity.class, DataSerializers.VARINT);
   private static final DataParameter<Optional<ITextComponent>> CUSTOM_NAME = EntityDataManager.createKey(Entity.class, DataSerializers.OPTIONAL_TEXT_COMPONENT);
   private static final DataParameter<Boolean> CUSTOM_NAME_VISIBLE = EntityDataManager.createKey(Entity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> SILENT = EntityDataManager.createKey(Entity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> NO_GRAVITY = EntityDataManager.createKey(Entity.class, DataSerializers.BOOLEAN);
   public boolean addedToChunk;
   public int chunkCoordX;
   public int chunkCoordY;
   public int chunkCoordZ;
   @OnlyIn(Dist.CLIENT)
   public long serverPosX;
   @OnlyIn(Dist.CLIENT)
   public long serverPosY;
   @OnlyIn(Dist.CLIENT)
   public long serverPosZ;
   public boolean ignoreFrustumCheck;
   public boolean isAirBorne;
   public int timeUntilPortal;
   protected boolean inPortal;
   protected int portalCounter;
   public DimensionType dimension;
   protected BlockPos lastPortalPos;
   protected Vec3d lastPortalVec;
   protected EnumFacing teleportDirection;
   private boolean invulnerable;
   protected UUID entityUniqueID;
   protected String cachedUniqueIdString;
   protected boolean glowing;
   private final Set<String> tags;
   private boolean isPositionDirty;
   private final double[] pistonDeltas;
   private long pistonDeltasGameTime;

   public Entity(EntityType<?> p_i48580_1_, World p_i48580_2_) {
      this.entityId = nextEntityID++;
      this.riddenByEntities = Lists.newArrayList();
      this.boundingBox = ZERO_AABB;
      this.width = 0.6F;
      this.height = 1.8F;
      this.nextStepDistance = 1.0F;
      this.nextFlap = 1.0F;
      this.rand = new Random();
      this.fire = -this.getFireImmuneTicks();
      this.firstUpdate = true;
      this.entityUniqueID = MathHelper.getRandomUUID(this.rand);
      this.cachedUniqueIdString = this.entityUniqueID.toString();
      this.tags = Sets.newHashSet();
      this.pistonDeltas = new double[]{0.0D, 0.0D, 0.0D};
      this.type = p_i48580_1_;
      this.world = p_i48580_2_;
      this.setPosition(0.0D, 0.0D, 0.0D);
      if (p_i48580_2_ != null) {
         this.dimension = p_i48580_2_.dimension.getType();
      }

      this.dataManager = new EntityDataManager(this);
      this.dataManager.register(FLAGS, (byte)0);
      this.dataManager.register(AIR, this.getMaxAir());
      this.dataManager.register(CUSTOM_NAME_VISIBLE, false);
      this.dataManager.register(CUSTOM_NAME, Optional.empty());
      this.dataManager.register(SILENT, false);
      this.dataManager.register(NO_GRAVITY, false);
      this.registerData();
   }

   public EntityType<?> getType() {
      return this.type;
   }

   public int getEntityId() {
      return this.entityId;
   }

   public void setEntityId(int p_145769_1_) {
      this.entityId = p_145769_1_;
   }

   public Set<String> getTags() {
      return this.tags;
   }

   public boolean addTag(String p_184211_1_) {
      return this.tags.size() >= 1024 ? false : this.tags.add(p_184211_1_);
   }

   public boolean removeTag(String p_184197_1_) {
      return this.tags.remove(p_184197_1_);
   }

   public void onKillCommand() {
      this.setDead();
   }

   protected abstract void registerData();

   public EntityDataManager getDataManager() {
      return this.dataManager;
   }

   public boolean equals(Object p_equals_1_) {
      if (p_equals_1_ instanceof Entity) {
         return ((Entity)p_equals_1_).entityId == this.entityId;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   protected void preparePlayerToSpawn() {
      if (this.world != null) {
         while(this.posY > 0.0D && this.posY < 256.0D) {
            this.setPosition(this.posX, this.posY, this.posZ);
            if (this.world.isCollisionBoxesEmpty(this, this.getEntityBoundingBox())) {
               break;
            }

            ++this.posY;
         }

         this.motionX = 0.0D;
         this.motionY = 0.0D;
         this.motionZ = 0.0D;
         this.rotationPitch = 0.0F;
      }
   }

   public void setDead() {
      this.isDead = true;
   }

   public void setDropItemsWhenDead(boolean p_184174_1_) {
   }

   protected void setSize(float p_70105_1_, float p_70105_2_) {
      if (p_70105_1_ != this.width || p_70105_2_ != this.height) {
         float f = this.width;
         this.width = p_70105_1_;
         this.height = p_70105_2_;
         if (this.width < f) {
            double d0 = (double)p_70105_1_ / 2.0D;
            this.setEntityBoundingBox(new AxisAlignedBB(this.posX - d0, this.posY, this.posZ - d0, this.posX + d0, this.posY + (double)this.height, this.posZ + d0));
            return;
         }

         AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
         this.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)this.width, axisalignedbb.minY + (double)this.height, axisalignedbb.minZ + (double)this.width));
         if (this.width > f && !this.firstUpdate && !this.world.isRemote) {
            this.move(MoverType.SELF, (double)(f - this.width), 0.0D, (double)(f - this.width));
         }
      }

   }

   protected void setRotation(float p_70101_1_, float p_70101_2_) {
      this.rotationYaw = p_70101_1_ % 360.0F;
      this.rotationPitch = p_70101_2_ % 360.0F;
   }

   public void setPosition(double p_70107_1_, double p_70107_3_, double p_70107_5_) {
      this.posX = p_70107_1_;
      this.posY = p_70107_3_;
      this.posZ = p_70107_5_;
      float f = this.width / 2.0F;
      float f1 = this.height;
      this.setEntityBoundingBox(new AxisAlignedBB(p_70107_1_ - (double)f, p_70107_3_, p_70107_5_ - (double)f, p_70107_1_ + (double)f, p_70107_3_ + (double)f1, p_70107_5_ + (double)f));
   }

   @OnlyIn(Dist.CLIENT)
   public void rotateTowards(double p_195049_1_, double p_195049_3_) {
      double d0 = p_195049_3_ * 0.15D;
      double d1 = p_195049_1_ * 0.15D;
      this.rotationPitch = (float)((double)this.rotationPitch + d0);
      this.rotationYaw = (float)((double)this.rotationYaw + d1);
      this.rotationPitch = MathHelper.clamp(this.rotationPitch, -90.0F, 90.0F);
      this.prevRotationPitch = (float)((double)this.prevRotationPitch + d0);
      this.prevRotationYaw = (float)((double)this.prevRotationYaw + d1);
      this.prevRotationPitch = MathHelper.clamp(this.prevRotationPitch, -90.0F, 90.0F);
      if (this.ridingEntity != null) {
         this.ridingEntity.applyOrientationToEntity(this);
      }

   }

   public void tick() {
      if (!this.world.isRemote) {
         this.setFlag(6, this.isGlowing());
      }

      this.baseTick();
   }

   public void baseTick() {
      this.world.profiler.startSection("entityBaseTick");
      if (this.isRiding() && this.getRidingEntity().isDead) {
         this.dismountRidingEntity();
      }

      if (this.rideCooldown > 0) {
         --this.rideCooldown;
      }

      this.prevDistanceWalkedModified = this.distanceWalkedModified;
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.prevRotationPitch = this.rotationPitch;
      this.prevRotationYaw = this.rotationYaw;
      if (!this.world.isRemote && this.world instanceof WorldServer) {
         this.world.profiler.startSection("portal");
         if (this.inPortal) {
            MinecraftServer minecraftserver = this.world.getServer();
            if (minecraftserver.getAllowNether()) {
               if (!this.isRiding()) {
                  int i = this.getMaxInPortalTime();
                  if (this.portalCounter++ >= i) {
                     this.portalCounter = i;
                     this.timeUntilPortal = this.getPortalCooldown();
                     DimensionType dimensiontype;
                     if (this.world.dimension.getType() == DimensionType.NETHER) {
                        dimensiontype = DimensionType.OVERWORLD;
                     } else {
                        dimensiontype = DimensionType.NETHER;
                     }

                     this.func_212321_a(dimensiontype);
                  }
               }

               this.inPortal = false;
            }
         } else {
            if (this.portalCounter > 0) {
               this.portalCounter -= 4;
            }

            if (this.portalCounter < 0) {
               this.portalCounter = 0;
            }
         }

         this.decrementTimeUntilPortal();
         this.world.profiler.endSection();
      }

      this.spawnRunningParticles();
      this.updateAquatics();
      if (this.world.isRemote) {
         this.extinguish();
      } else if (this.fire > 0) {
         if (this.isImmuneToFire) {
            this.fire -= 4;
            if (this.fire < 0) {
               this.extinguish();
            }
         } else {
            if (this.fire % 20 == 0) {
               this.attackEntityFrom(DamageSource.ON_FIRE, 1.0F);
            }

            --this.fire;
         }
      }

      if (this.isInLava()) {
         this.setOnFireFromLava();
         this.fallDistance *= 0.5F;
      }

      if (this.posY < -64.0D) {
         this.outOfWorld();
      }

      if (!this.world.isRemote) {
         this.setFlag(0, this.fire > 0);
      }

      this.firstUpdate = false;
      this.world.profiler.endSection();
   }

   protected void decrementTimeUntilPortal() {
      if (this.timeUntilPortal > 0) {
         --this.timeUntilPortal;
      }

   }

   public int getMaxInPortalTime() {
      return 1;
   }

   protected void setOnFireFromLava() {
      if (!this.isImmuneToFire) {
         this.setFire(15);
         this.attackEntityFrom(DamageSource.LAVA, 4.0F);
      }
   }

   public void setFire(int p_70015_1_) {
      int i = p_70015_1_ * 20;
      if (this instanceof EntityLivingBase) {
         i = EnchantmentProtection.getFireTimeForEntity((EntityLivingBase)this, i);
      }

      if (this.fire < i) {
         this.fire = i;
      }

   }

   public void extinguish() {
      this.fire = 0;
   }

   protected void outOfWorld() {
      this.setDead();
   }

   public boolean isOffsetPositionInLiquid(double p_70038_1_, double p_70038_3_, double p_70038_5_) {
      return this.isLiquidPresentInAABB(this.getEntityBoundingBox().offset(p_70038_1_, p_70038_3_, p_70038_5_));
   }

   private boolean isLiquidPresentInAABB(AxisAlignedBB p_174809_1_) {
      return this.world.isCollisionBoxesEmpty(this, p_174809_1_) && !this.world.containsAnyLiquid(p_174809_1_);
   }

   public void move(MoverType p_70091_1_, double p_70091_2_, double p_70091_4_, double p_70091_6_) {
      if (this.noClip) {
         this.setEntityBoundingBox(this.getEntityBoundingBox().offset(p_70091_2_, p_70091_4_, p_70091_6_));
         this.resetPositionToBB();
      } else {
         if (p_70091_1_ == MoverType.PISTON) {
            long i = this.world.getTotalWorldTime();
            if (i != this.pistonDeltasGameTime) {
               Arrays.fill(this.pistonDeltas, 0.0D);
               this.pistonDeltasGameTime = i;
            }

            if (p_70091_2_ != 0.0D) {
               int j = EnumFacing.Axis.X.ordinal();
               double d0 = MathHelper.clamp(p_70091_2_ + this.pistonDeltas[j], -0.51D, 0.51D);
               p_70091_2_ = d0 - this.pistonDeltas[j];
               this.pistonDeltas[j] = d0;
               if (Math.abs(p_70091_2_) <= (double)1.0E-5F) {
                  return;
               }
            } else if (p_70091_4_ != 0.0D) {
               int l = EnumFacing.Axis.Y.ordinal();
               double d19 = MathHelper.clamp(p_70091_4_ + this.pistonDeltas[l], -0.51D, 0.51D);
               p_70091_4_ = d19 - this.pistonDeltas[l];
               this.pistonDeltas[l] = d19;
               if (Math.abs(p_70091_4_) <= (double)1.0E-5F) {
                  return;
               }
            } else {
               if (p_70091_6_ == 0.0D) {
                  return;
               }

               int i1 = EnumFacing.Axis.Z.ordinal();
               double d20 = MathHelper.clamp(p_70091_6_ + this.pistonDeltas[i1], -0.51D, 0.51D);
               p_70091_6_ = d20 - this.pistonDeltas[i1];
               this.pistonDeltas[i1] = d20;
               if (Math.abs(p_70091_6_) <= (double)1.0E-5F) {
                  return;
               }
            }
         }

         this.world.profiler.startSection("move");
         double d17 = this.posX;
         double d18 = this.posY;
         double d1 = this.posZ;
         if (this.isInWeb) {
            this.isInWeb = false;
            p_70091_2_ *= 0.25D;
            p_70091_4_ *= (double)0.05F;
            p_70091_6_ *= 0.25D;
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
         }

         double d2 = p_70091_2_;
         double d3 = p_70091_4_;
         double d4 = p_70091_6_;
         if ((p_70091_1_ == MoverType.SELF || p_70091_1_ == MoverType.PLAYER) && this.onGround && this.isSneaking() && this instanceof EntityPlayer) {
            for(double d5 = 0.05D; p_70091_2_ != 0.0D && this.world.isCollisionBoxesEmpty(this, this.getEntityBoundingBox().offset(p_70091_2_, (double)(-this.stepHeight), 0.0D)); d2 = p_70091_2_) {
               if (p_70091_2_ < 0.05D && p_70091_2_ >= -0.05D) {
                  p_70091_2_ = 0.0D;
               } else if (p_70091_2_ > 0.0D) {
                  p_70091_2_ -= 0.05D;
               } else {
                  p_70091_2_ += 0.05D;
               }
            }

            for(; p_70091_6_ != 0.0D && this.world.isCollisionBoxesEmpty(this, this.getEntityBoundingBox().offset(0.0D, (double)(-this.stepHeight), p_70091_6_)); d4 = p_70091_6_) {
               if (p_70091_6_ < 0.05D && p_70091_6_ >= -0.05D) {
                  p_70091_6_ = 0.0D;
               } else if (p_70091_6_ > 0.0D) {
                  p_70091_6_ -= 0.05D;
               } else {
                  p_70091_6_ += 0.05D;
               }
            }

            for(; p_70091_2_ != 0.0D && p_70091_6_ != 0.0D && this.world.isCollisionBoxesEmpty(this, this.getEntityBoundingBox().offset(p_70091_2_, (double)(-this.stepHeight), p_70091_6_)); d4 = p_70091_6_) {
               if (p_70091_2_ < 0.05D && p_70091_2_ >= -0.05D) {
                  p_70091_2_ = 0.0D;
               } else if (p_70091_2_ > 0.0D) {
                  p_70091_2_ -= 0.05D;
               } else {
                  p_70091_2_ += 0.05D;
               }

               d2 = p_70091_2_;
               if (p_70091_6_ < 0.05D && p_70091_6_ >= -0.05D) {
                  p_70091_6_ = 0.0D;
               } else if (p_70091_6_ > 0.0D) {
                  p_70091_6_ -= 0.05D;
               } else {
                  p_70091_6_ += 0.05D;
               }
            }
         }

         AxisAlignedBB axisalignedbb4 = this.getEntityBoundingBox();
         if (p_70091_2_ != 0.0D || p_70091_4_ != 0.0D || p_70091_6_ != 0.0D) {
            ReuseableStream<VoxelShape> reuseablestream = new ReuseableStream<>(this.world.func_199406_a(this, this.getEntityBoundingBox(), p_70091_2_, p_70091_4_, p_70091_6_));
            if (p_70091_4_ != 0.0D) {
               p_70091_4_ = VoxelShapes.func_212437_a(EnumFacing.Axis.Y, this.getEntityBoundingBox(), reuseablestream.func_212761_a(), p_70091_4_);
               this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, p_70091_4_, 0.0D));
            }

            if (p_70091_2_ != 0.0D) {
               p_70091_2_ = VoxelShapes.func_212437_a(EnumFacing.Axis.X, this.getEntityBoundingBox(), reuseablestream.func_212761_a(), p_70091_2_);
               if (p_70091_2_ != 0.0D) {
                  this.setEntityBoundingBox(this.getEntityBoundingBox().offset(p_70091_2_, 0.0D, 0.0D));
               }
            }

            if (p_70091_6_ != 0.0D) {
               p_70091_6_ = VoxelShapes.func_212437_a(EnumFacing.Axis.Z, this.getEntityBoundingBox(), reuseablestream.func_212761_a(), p_70091_6_);
               if (p_70091_6_ != 0.0D) {
                  this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, 0.0D, p_70091_6_));
               }
            }
         }

         boolean flag = this.onGround || d3 != p_70091_4_ && d3 < 0.0D;
         if (this.stepHeight > 0.0F && flag && (d2 != p_70091_2_ || d4 != p_70091_6_)) {
            double d6 = p_70091_2_;
            double d7 = p_70091_4_;
            double d8 = p_70091_6_;
            AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
            this.setEntityBoundingBox(axisalignedbb4);
            p_70091_2_ = d2;
            p_70091_4_ = (double)this.stepHeight;
            p_70091_6_ = d4;
            if (d2 != 0.0D || p_70091_4_ != 0.0D || d4 != 0.0D) {
               ReuseableStream<VoxelShape> reuseablestream1 = new ReuseableStream<>(this.world.func_199406_a(this, this.getEntityBoundingBox(), d2, p_70091_4_, d4));
               AxisAlignedBB axisalignedbb1 = this.getEntityBoundingBox();
               AxisAlignedBB axisalignedbb2 = axisalignedbb1.expand(d2, 0.0D, d4);
               double d9 = VoxelShapes.func_212437_a(EnumFacing.Axis.Y, axisalignedbb2, reuseablestream1.func_212761_a(), p_70091_4_);
               if (d9 != 0.0D) {
                  axisalignedbb1 = axisalignedbb1.offset(0.0D, d9, 0.0D);
               }

               double d10 = VoxelShapes.func_212437_a(EnumFacing.Axis.X, axisalignedbb1, reuseablestream1.func_212761_a(), d2);
               if (d10 != 0.0D) {
                  axisalignedbb1 = axisalignedbb1.offset(d10, 0.0D, 0.0D);
               }

               double d11 = VoxelShapes.func_212437_a(EnumFacing.Axis.Z, axisalignedbb1, reuseablestream1.func_212761_a(), d4);
               if (d11 != 0.0D) {
                  axisalignedbb1 = axisalignedbb1.offset(0.0D, 0.0D, d11);
               }

               AxisAlignedBB axisalignedbb3 = this.getEntityBoundingBox();
               double d12 = VoxelShapes.func_212437_a(EnumFacing.Axis.Y, axisalignedbb3, reuseablestream1.func_212761_a(), p_70091_4_);
               if (d12 != 0.0D) {
                  axisalignedbb3 = axisalignedbb3.offset(0.0D, d12, 0.0D);
               }

               double d13 = VoxelShapes.func_212437_a(EnumFacing.Axis.X, axisalignedbb3, reuseablestream1.func_212761_a(), d2);
               if (d13 != 0.0D) {
                  axisalignedbb3 = axisalignedbb3.offset(d13, 0.0D, 0.0D);
               }

               double d14 = VoxelShapes.func_212437_a(EnumFacing.Axis.Z, axisalignedbb3, reuseablestream1.func_212761_a(), d4);
               if (d14 != 0.0D) {
                  axisalignedbb3 = axisalignedbb3.offset(0.0D, 0.0D, d14);
               }

               double d15 = d10 * d10 + d11 * d11;
               double d16 = d13 * d13 + d14 * d14;
               if (d15 > d16) {
                  p_70091_2_ = d10;
                  p_70091_6_ = d11;
                  p_70091_4_ = -d9;
                  this.setEntityBoundingBox(axisalignedbb1);
               } else {
                  p_70091_2_ = d13;
                  p_70091_6_ = d14;
                  p_70091_4_ = -d12;
                  this.setEntityBoundingBox(axisalignedbb3);
               }

               p_70091_4_ = VoxelShapes.func_212437_a(EnumFacing.Axis.Y, this.getEntityBoundingBox(), reuseablestream1.func_212761_a(), p_70091_4_);
               if (p_70091_4_ != 0.0D) {
                  this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, p_70091_4_, 0.0D));
               }
            }

            if (d6 * d6 + d8 * d8 >= p_70091_2_ * p_70091_2_ + p_70091_6_ * p_70091_6_) {
               p_70091_2_ = d6;
               p_70091_4_ = d7;
               p_70091_6_ = d8;
               this.setEntityBoundingBox(axisalignedbb);
            }
         }

         this.world.profiler.endSection();
         this.world.profiler.startSection("rest");
         this.resetPositionToBB();
         this.collidedHorizontally = d2 != p_70091_2_ || d4 != p_70091_6_;
         this.collidedVertically = d3 != p_70091_4_;
         this.onGround = this.collidedVertically && d3 < 0.0D;
         this.collided = this.collidedHorizontally || this.collidedVertically;
         int j1 = MathHelper.floor(this.posX);
         int k = MathHelper.floor(this.posY - (double)0.2F);
         int k1 = MathHelper.floor(this.posZ);
         BlockPos blockpos = new BlockPos(j1, k, k1);
         IBlockState iblockstate = this.world.getBlockState(blockpos);
         if (iblockstate.isAir()) {
            BlockPos blockpos1 = blockpos.down();
            IBlockState iblockstate1 = this.world.getBlockState(blockpos1);
            Block block1 = iblockstate1.getBlock();
            if (block1 instanceof BlockFence || block1 instanceof BlockWall || block1 instanceof BlockFenceGate) {
               iblockstate = iblockstate1;
               blockpos = blockpos1;
            }
         }

         this.updateFallState(p_70091_4_, this.onGround, iblockstate, blockpos);
         if (d2 != p_70091_2_) {
            this.motionX = 0.0D;
         }

         if (d4 != p_70091_6_) {
            this.motionZ = 0.0D;
         }

         Block block = iblockstate.getBlock();
         if (d3 != p_70091_4_) {
            block.onLanded(this.world, this);
         }

         if (this.canTriggerWalking() && (!this.onGround || !this.isSneaking() || !(this instanceof EntityPlayer)) && !this.isRiding()) {
            double d21 = this.posX - d17;
            double d22 = this.posY - d18;
            double d23 = this.posZ - d1;
            if (block != Blocks.LADDER) {
               d22 = 0.0D;
            }

            if (block != null && this.onGround) {
               block.onEntityWalk(this.world, blockpos, this);
            }

            this.distanceWalkedModified = (float)((double)this.distanceWalkedModified + (double)MathHelper.sqrt(d21 * d21 + d23 * d23) * 0.6D);
            this.distanceWalkedOnStepModified = (float)((double)this.distanceWalkedOnStepModified + (double)MathHelper.sqrt(d21 * d21 + d22 * d22 + d23 * d23) * 0.6D);
            if (this.distanceWalkedOnStepModified > this.nextStepDistance && !iblockstate.isAir()) {
               this.nextStepDistance = this.determineNextStepDistance();
               if (this.isInWater()) {
                  Entity entity = this.isBeingRidden() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
                  float f = entity == this ? 0.35F : 0.4F;
                  float f1 = MathHelper.sqrt(entity.motionX * entity.motionX * (double)0.2F + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ * (double)0.2F) * f;
                  if (f1 > 1.0F) {
                     f1 = 1.0F;
                  }

                  this.playSwimSound(f1);
               } else {
                  this.playStepSound(blockpos, iblockstate);
               }
            } else if (this.distanceWalkedOnStepModified > this.nextFlap && this.makeFlySound() && iblockstate.isAir()) {
               this.nextFlap = this.playFlySound(this.distanceWalkedOnStepModified);
            }
         }

         try {
            this.doBlockCollisions();
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
            this.fillCrashReport(crashreportcategory);
            throw new ReportedException(crashreport);
         }

         boolean flag1 = this.isInWaterRainOrBubbleColumn();
         if (this.world.isFlammableWithin(this.getEntityBoundingBox().shrink(0.001D))) {
            if (!flag1) {
               ++this.fire;
               if (this.fire == 0) {
                  this.setFire(8);
               }
            }

            this.dealFireDamage(1);
         } else if (this.fire <= 0) {
            this.fire = -this.getFireImmuneTicks();
         }

         if (flag1 && this.isBurning()) {
            this.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
            this.fire = -this.getFireImmuneTicks();
         }

         this.world.profiler.endSection();
      }
   }

   protected float determineNextStepDistance() {
      return (float)((int)this.distanceWalkedOnStepModified + 1);
   }

   public void resetPositionToBB() {
      AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
      this.posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0D;
      this.posY = axisalignedbb.minY;
      this.posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_GENERIC_SWIM;
   }

   protected SoundEvent getSplashSound() {
      return SoundEvents.ENTITY_GENERIC_SPLASH;
   }

   protected SoundEvent getHighspeedSplashSound() {
      return SoundEvents.ENTITY_GENERIC_SPLASH;
   }

   protected void doBlockCollisions() {
      AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();

      try (
         BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain(axisalignedbb.minX + 0.001D, axisalignedbb.minY + 0.001D, axisalignedbb.minZ + 0.001D);
         BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos1 = BlockPos.PooledMutableBlockPos.retain(axisalignedbb.maxX - 0.001D, axisalignedbb.maxY - 0.001D, axisalignedbb.maxZ - 0.001D);
         BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos2 = BlockPos.PooledMutableBlockPos.retain();
      ) {
         if (this.world.isAreaLoaded(blockpos$pooledmutableblockpos, blockpos$pooledmutableblockpos1)) {
            for(int i = blockpos$pooledmutableblockpos.getX(); i <= blockpos$pooledmutableblockpos1.getX(); ++i) {
               for(int j = blockpos$pooledmutableblockpos.getY(); j <= blockpos$pooledmutableblockpos1.getY(); ++j) {
                  for(int k = blockpos$pooledmutableblockpos.getZ(); k <= blockpos$pooledmutableblockpos1.getZ(); ++k) {
                     blockpos$pooledmutableblockpos2.setPos(i, j, k);
                     IBlockState iblockstate = this.world.getBlockState(blockpos$pooledmutableblockpos2);

                     try {
                        iblockstate.onEntityCollision(this.world, blockpos$pooledmutableblockpos2, this);
                        this.onInsideBlock(iblockstate);
                     } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Colliding entity with block");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being collided with");
                        CrashReportCategory.addBlockInfo(crashreportcategory, blockpos$pooledmutableblockpos2, iblockstate);
                        throw new ReportedException(crashreport);
                     }
                  }
               }
            }
         }
      }

   }

   protected void onInsideBlock(IBlockState p_191955_1_) {
   }

   protected void playStepSound(BlockPos p_180429_1_, IBlockState p_180429_2_) {
      if (!p_180429_2_.getMaterial().isLiquid()) {
         SoundType soundtype = this.world.getBlockState(p_180429_1_.up()).getBlock() == Blocks.SNOW ? Blocks.SNOW.getSoundType() : p_180429_2_.getBlock().getSoundType();
         this.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
      }
   }

   protected void playSwimSound(float p_203006_1_) {
      this.playSound(this.getSwimSound(), p_203006_1_, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
   }

   protected float playFlySound(float p_191954_1_) {
      return 0.0F;
   }

   protected boolean makeFlySound() {
      return false;
   }

   public void playSound(SoundEvent p_184185_1_, float p_184185_2_, float p_184185_3_) {
      if (!this.isSilent()) {
         this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, p_184185_1_, this.getSoundCategory(), p_184185_2_, p_184185_3_);
      }

   }

   public boolean isSilent() {
      return this.dataManager.get(SILENT);
   }

   public void setSilent(boolean p_174810_1_) {
      this.dataManager.set(SILENT, p_174810_1_);
   }

   public boolean hasNoGravity() {
      return this.dataManager.get(NO_GRAVITY);
   }

   public void setNoGravity(boolean p_189654_1_) {
      this.dataManager.set(NO_GRAVITY, p_189654_1_);
   }

   protected boolean canTriggerWalking() {
      return true;
   }

   protected void updateFallState(double p_184231_1_, boolean p_184231_3_, IBlockState p_184231_4_, BlockPos p_184231_5_) {
      if (p_184231_3_) {
         if (this.fallDistance > 0.0F) {
            p_184231_4_.getBlock().onFallenUpon(this.world, p_184231_5_, this, this.fallDistance);
         }

         this.fallDistance = 0.0F;
      } else if (p_184231_1_ < 0.0D) {
         this.fallDistance = (float)((double)this.fallDistance - p_184231_1_);
      }

   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox() {
      return null;
   }

   protected void dealFireDamage(int p_70081_1_) {
      if (!this.isImmuneToFire) {
         this.attackEntityFrom(DamageSource.IN_FIRE, (float)p_70081_1_);
      }

   }

   public final boolean isImmuneToFire() {
      return this.isImmuneToFire;
   }

   public void fall(float p_180430_1_, float p_180430_2_) {
      if (this.isBeingRidden()) {
         for(Entity entity : this.getPassengers()) {
            entity.fall(p_180430_1_, p_180430_2_);
         }
      }

   }

   public boolean isInWater() {
      return this.inWater;
   }

   private boolean isInRain() {
      boolean flag;
      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain(this)) {
         flag = this.world.isRainingAt(blockpos$pooledmutableblockpos) || this.world.isRainingAt(blockpos$pooledmutableblockpos.setPos(this.posX, this.posY + (double)this.height, this.posZ));
      }

      return flag;
   }

   private boolean isInBubbleColumn() {
      return this.world.getBlockState(new BlockPos(this)).getBlock() == Blocks.BUBBLE_COLUMN;
   }

   public boolean isWet() {
      return this.isInWater() || this.isInRain();
   }

   public boolean isInWaterRainOrBubbleColumn() {
      return this.isInWater() || this.isInRain() || this.isInBubbleColumn();
   }

   public boolean isInWaterOrBubbleColumn() {
      return this.isInWater() || this.isInBubbleColumn();
   }

   public boolean canSwim() {
      return this.eyesInWater && this.isInWater();
   }

   private void updateAquatics() {
      this.handleWaterMovement();
      this.updateEyesInWater();
      this.updateSwimming();
   }

   public void updateSwimming() {
      if (this.isSwimming()) {
         this.setSwimming(this.isSprinting() && this.isInWater() && !this.isRiding());
      } else {
         this.setSwimming(this.isSprinting() && this.canSwim() && !this.isRiding());
      }

   }

   public boolean handleWaterMovement() {
      if (this.getRidingEntity() instanceof EntityBoat) {
         this.inWater = false;
      } else if (this.handleFluidAcceleration(FluidTags.WATER)) {
         if (!this.inWater && !this.firstUpdate) {
            this.doWaterSplashEffect();
         }

         this.fallDistance = 0.0F;
         this.inWater = true;
         this.extinguish();
      } else {
         this.inWater = false;
      }

      return this.inWater;
   }

   private void updateEyesInWater() {
      this.eyesInWater = this.areEyesInFluid(FluidTags.WATER);
   }

   protected void doWaterSplashEffect() {
      Entity entity = this.isBeingRidden() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
      float f = entity == this ? 0.2F : 0.9F;
      float f1 = MathHelper.sqrt(entity.motionX * entity.motionX * (double)0.2F + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ * (double)0.2F) * f;
      if (f1 > 1.0F) {
         f1 = 1.0F;
      }

      if ((double)f1 < 0.25D) {
         this.playSound(this.getSplashSound(), f1, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
      } else {
         this.playSound(this.getHighspeedSplashSound(), f1, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
      }

      float f2 = (float)MathHelper.floor(this.getEntityBoundingBox().minY);

      for(int i = 0; (float)i < 1.0F + this.width * 20.0F; ++i) {
         float f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         float f4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         this.world.spawnParticle(Particles.BUBBLE, this.posX + (double)f3, (double)(f2 + 1.0F), this.posZ + (double)f4, this.motionX, this.motionY - (double)(this.rand.nextFloat() * 0.2F), this.motionZ);
      }

      for(int j = 0; (float)j < 1.0F + this.width * 20.0F; ++j) {
         float f5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         float f6 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         this.world.spawnParticle(Particles.SPLASH, this.posX + (double)f5, (double)(f2 + 1.0F), this.posZ + (double)f6, this.motionX, this.motionY, this.motionZ);
      }

   }

   public void spawnRunningParticles() {
      if (this.isSprinting() && !this.isInWater()) {
         this.createRunningParticles();
      }

   }

   protected void createRunningParticles() {
      int i = MathHelper.floor(this.posX);
      int j = MathHelper.floor(this.posY - (double)0.2F);
      int k = MathHelper.floor(this.posZ);
      BlockPos blockpos = new BlockPos(i, j, k);
      IBlockState iblockstate = this.world.getBlockState(blockpos);
      if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
         this.world.spawnParticle(new BlockParticleData(Particles.BLOCK, iblockstate), this.posX + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, -this.motionX * 4.0D, 1.5D, -this.motionZ * 4.0D);
      }

   }

   public boolean areEyesInFluid(Tag<Fluid> p_208600_1_) {
      if (this.getRidingEntity() instanceof EntityBoat) {
         return false;
      } else {
         double d0 = this.posY + (double)this.getEyeHeight();
         BlockPos blockpos = new BlockPos(this.posX, d0, this.posZ);
         IFluidState ifluidstate = this.world.getFluidState(blockpos);
         return ifluidstate.isTagged(p_208600_1_) && d0 < (double)((float)blockpos.getY() + ifluidstate.getHeight() + 0.11111111F);
      }
   }

   public boolean isInLava() {
      return this.world.isMaterialInBB(this.getEntityBoundingBox().shrink((double)0.1F, (double)0.4F, (double)0.1F), Material.LAVA);
   }

   public void moveRelative(float p_191958_1_, float p_191958_2_, float p_191958_3_, float p_191958_4_) {
      float f = p_191958_1_ * p_191958_1_ + p_191958_2_ * p_191958_2_ + p_191958_3_ * p_191958_3_;
      if (!(f < 1.0E-4F)) {
         f = MathHelper.sqrt(f);
         if (f < 1.0F) {
            f = 1.0F;
         }

         f = p_191958_4_ / f;
         p_191958_1_ = p_191958_1_ * f;
         p_191958_2_ = p_191958_2_ * f;
         p_191958_3_ = p_191958_3_ * f;
         float f1 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F));
         float f2 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F));
         this.motionX += (double)(p_191958_1_ * f2 - p_191958_3_ * f1);
         this.motionY += (double)p_191958_2_;
         this.motionZ += (double)(p_191958_3_ * f2 + p_191958_1_ * f1);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public int getBrightnessForRender() {
      BlockPos blockpos = new BlockPos(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
      return this.world.isBlockLoaded(blockpos) ? this.world.getCombinedLight(blockpos, 0) : 0;
   }

   public float getBrightness() {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(MathHelper.floor(this.posX), 0, MathHelper.floor(this.posZ));
      if (this.world.isBlockLoaded(blockpos$mutableblockpos)) {
         blockpos$mutableblockpos.setY(MathHelper.floor(this.posY + (double)this.getEyeHeight()));
         return this.world.getBrightness(blockpos$mutableblockpos);
      } else {
         return 0.0F;
      }
   }

   public void setWorld(World p_70029_1_) {
      this.world = p_70029_1_;
   }

   public void setPositionAndRotation(double p_70080_1_, double p_70080_3_, double p_70080_5_, float p_70080_7_, float p_70080_8_) {
      this.posX = MathHelper.clamp(p_70080_1_, -3.0E7D, 3.0E7D);
      this.posY = p_70080_3_;
      this.posZ = MathHelper.clamp(p_70080_5_, -3.0E7D, 3.0E7D);
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      p_70080_8_ = MathHelper.clamp(p_70080_8_, -90.0F, 90.0F);
      this.rotationYaw = p_70080_7_;
      this.rotationPitch = p_70080_8_;
      this.prevRotationYaw = this.rotationYaw;
      this.prevRotationPitch = this.rotationPitch;
      double d0 = (double)(this.prevRotationYaw - p_70080_7_);
      if (d0 < -180.0D) {
         this.prevRotationYaw += 360.0F;
      }

      if (d0 >= 180.0D) {
         this.prevRotationYaw -= 360.0F;
      }

      this.setPosition(this.posX, this.posY, this.posZ);
      this.setRotation(p_70080_7_, p_70080_8_);
   }

   public void moveToBlockPosAndAngles(BlockPos p_174828_1_, float p_174828_2_, float p_174828_3_) {
      this.setLocationAndAngles((double)p_174828_1_.getX() + 0.5D, (double)p_174828_1_.getY(), (double)p_174828_1_.getZ() + 0.5D, p_174828_2_, p_174828_3_);
   }

   public void setLocationAndAngles(double p_70012_1_, double p_70012_3_, double p_70012_5_, float p_70012_7_, float p_70012_8_) {
      this.posX = p_70012_1_;
      this.posY = p_70012_3_;
      this.posZ = p_70012_5_;
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.lastTickPosX = this.posX;
      this.lastTickPosY = this.posY;
      this.lastTickPosZ = this.posZ;
      this.rotationYaw = p_70012_7_;
      this.rotationPitch = p_70012_8_;
      this.setPosition(this.posX, this.posY, this.posZ);
   }

   public float getDistance(Entity p_70032_1_) {
      float f = (float)(this.posX - p_70032_1_.posX);
      float f1 = (float)(this.posY - p_70032_1_.posY);
      float f2 = (float)(this.posZ - p_70032_1_.posZ);
      return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
   }

   public double getDistanceSq(double p_70092_1_, double p_70092_3_, double p_70092_5_) {
      double d0 = this.posX - p_70092_1_;
      double d1 = this.posY - p_70092_3_;
      double d2 = this.posZ - p_70092_5_;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public double getDistanceSq(BlockPos p_174818_1_) {
      return p_174818_1_.distanceSq(this.posX, this.posY, this.posZ);
   }

   public double getDistanceSqToCenter(BlockPos p_174831_1_) {
      return p_174831_1_.distanceSqToCenter(this.posX, this.posY, this.posZ);
   }

   public double getDistance(double p_70011_1_, double p_70011_3_, double p_70011_5_) {
      double d0 = this.posX - p_70011_1_;
      double d1 = this.posY - p_70011_3_;
      double d2 = this.posZ - p_70011_5_;
      return (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
   }

   public double getDistanceSq(Entity p_70068_1_) {
      double d0 = this.posX - p_70068_1_.posX;
      double d1 = this.posY - p_70068_1_.posY;
      double d2 = this.posZ - p_70068_1_.posZ;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public double getDistanceSq(Vec3d p_195048_1_) {
      double d0 = this.posX - p_195048_1_.x;
      double d1 = this.posY - p_195048_1_.y;
      double d2 = this.posZ - p_195048_1_.z;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public void onCollideWithPlayer(EntityPlayer p_70100_1_) {
   }

   public void applyEntityCollision(Entity p_70108_1_) {
      if (!this.isRidingSameEntity(p_70108_1_)) {
         if (!p_70108_1_.noClip && !this.noClip) {
            double d0 = p_70108_1_.posX - this.posX;
            double d1 = p_70108_1_.posZ - this.posZ;
            double d2 = MathHelper.absMax(d0, d1);
            if (d2 >= (double)0.01F) {
               d2 = (double)MathHelper.sqrt(d2);
               d0 = d0 / d2;
               d1 = d1 / d2;
               double d3 = 1.0D / d2;
               if (d3 > 1.0D) {
                  d3 = 1.0D;
               }

               d0 = d0 * d3;
               d1 = d1 * d3;
               d0 = d0 * (double)0.05F;
               d1 = d1 * (double)0.05F;
               d0 = d0 * (double)(1.0F - this.entityCollisionReduction);
               d1 = d1 * (double)(1.0F - this.entityCollisionReduction);
               if (!this.isBeingRidden()) {
                  this.addVelocity(-d0, 0.0D, -d1);
               }

               if (!p_70108_1_.isBeingRidden()) {
                  p_70108_1_.addVelocity(d0, 0.0D, d1);
               }
            }

         }
      }
   }

   public void addVelocity(double p_70024_1_, double p_70024_3_, double p_70024_5_) {
      this.motionX += p_70024_1_;
      this.motionY += p_70024_3_;
      this.motionZ += p_70024_5_;
      this.isAirBorne = true;
   }

   protected void markVelocityChanged() {
      this.velocityChanged = true;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         this.markVelocityChanged();
         return false;
      }
   }

   public final Vec3d getLook(float p_70676_1_) {
      return this.getVectorForRotation(this.getPitch(p_70676_1_), this.getYaw(p_70676_1_));
   }

   public float getPitch(float p_195050_1_) {
      return p_195050_1_ == 1.0F ? this.rotationPitch : this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * p_195050_1_;
   }

   public float getYaw(float p_195046_1_) {
      return p_195046_1_ == 1.0F ? this.rotationYaw : this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * p_195046_1_;
   }

   protected final Vec3d getVectorForRotation(float p_174806_1_, float p_174806_2_) {
      float f = p_174806_1_ * ((float)Math.PI / 180F);
      float f1 = -p_174806_2_ * ((float)Math.PI / 180F);
      float f2 = MathHelper.cos(f1);
      float f3 = MathHelper.sin(f1);
      float f4 = MathHelper.cos(f);
      float f5 = MathHelper.sin(f);
      return new Vec3d((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
   }

   public Vec3d getEyePosition(float p_174824_1_) {
      if (p_174824_1_ == 1.0F) {
         return new Vec3d(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
      } else {
         double d0 = this.prevPosX + (this.posX - this.prevPosX) * (double)p_174824_1_;
         double d1 = this.prevPosY + (this.posY - this.prevPosY) * (double)p_174824_1_ + (double)this.getEyeHeight();
         double d2 = this.prevPosZ + (this.posZ - this.prevPosZ) * (double)p_174824_1_;
         return new Vec3d(d0, d1, d2);
      }
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public RayTraceResult rayTrace(double p_174822_1_, float p_174822_3_, RayTraceFluidMode p_174822_4_) {
      Vec3d vec3d = this.getEyePosition(p_174822_3_);
      Vec3d vec3d1 = this.getLook(p_174822_3_);
      Vec3d vec3d2 = vec3d.add(vec3d1.x * p_174822_1_, vec3d1.y * p_174822_1_, vec3d1.z * p_174822_1_);
      return this.world.rayTraceBlocks(vec3d, vec3d2, p_174822_4_, false, true);
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean canBePushed() {
      return false;
   }

   public void awardKillScore(Entity p_191956_1_, int p_191956_2_, DamageSource p_191956_3_) {
      if (p_191956_1_ instanceof EntityPlayerMP) {
         CriteriaTriggers.ENTITY_KILLED_PLAYER.trigger((EntityPlayerMP)p_191956_1_, this, p_191956_3_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRender3d(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
      double d0 = this.posX - p_145770_1_;
      double d1 = this.posY - p_145770_3_;
      double d2 = this.posZ - p_145770_5_;
      double d3 = d0 * d0 + d1 * d1 + d2 * d2;
      return this.isInRangeToRenderDist(d3);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      double d0 = this.getEntityBoundingBox().getAverageEdgeLength();
      if (Double.isNaN(d0)) {
         d0 = 1.0D;
      }

      d0 = d0 * 64.0D * renderDistanceWeight;
      return p_70112_1_ < d0 * d0;
   }

   public boolean writeToNBTAtomically(NBTTagCompound p_184198_1_) {
      String s = this.getEntityString();
      if (!this.isDead && s != null) {
         p_184198_1_.setString("id", s);
         this.writeToNBT(p_184198_1_);
         return true;
      } else {
         return false;
      }
   }

   public boolean writeToNBTOptional(NBTTagCompound p_70039_1_) {
      return this.isRiding() ? false : this.writeToNBTAtomically(p_70039_1_);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189511_1_) {
      try {
         p_189511_1_.setTag("Pos", this.newDoubleNBTList(this.posX, this.posY, this.posZ));
         p_189511_1_.setTag("Motion", this.newDoubleNBTList(this.motionX, this.motionY, this.motionZ));
         p_189511_1_.setTag("Rotation", this.newFloatNBTList(this.rotationYaw, this.rotationPitch));
         p_189511_1_.setFloat("FallDistance", this.fallDistance);
         p_189511_1_.setShort("Fire", (short)this.fire);
         p_189511_1_.setShort("Air", (short)this.getAir());
         p_189511_1_.setBoolean("OnGround", this.onGround);
         p_189511_1_.setInteger("Dimension", this.dimension.getId());
         p_189511_1_.setBoolean("Invulnerable", this.invulnerable);
         p_189511_1_.setInteger("PortalCooldown", this.timeUntilPortal);
         p_189511_1_.setUniqueId("UUID", this.getUniqueID());
         ITextComponent itextcomponent = this.getCustomName();
         if (itextcomponent != null) {
            p_189511_1_.setString("CustomName", ITextComponent.Serializer.toJson(itextcomponent));
         }

         if (this.isCustomNameVisible()) {
            p_189511_1_.setBoolean("CustomNameVisible", this.isCustomNameVisible());
         }

         if (this.isSilent()) {
            p_189511_1_.setBoolean("Silent", this.isSilent());
         }

         if (this.hasNoGravity()) {
            p_189511_1_.setBoolean("NoGravity", this.hasNoGravity());
         }

         if (this.glowing) {
            p_189511_1_.setBoolean("Glowing", this.glowing);
         }

         if (!this.tags.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();

            for(String s : this.tags) {
               nbttaglist.add((INBTBase)(new NBTTagString(s)));
            }

            p_189511_1_.setTag("Tags", nbttaglist);
         }

         this.writeEntityToNBT(p_189511_1_);
         if (this.isBeingRidden()) {
            NBTTagList nbttaglist1 = new NBTTagList();

            for(Entity entity : this.getPassengers()) {
               NBTTagCompound nbttagcompound = new NBTTagCompound();
               if (entity.writeToNBTAtomically(nbttagcompound)) {
                  nbttaglist1.add((INBTBase)nbttagcompound);
               }
            }

            if (!nbttaglist1.isEmpty()) {
               p_189511_1_.setTag("Passengers", nbttaglist1);
            }
         }

         return p_189511_1_;
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Saving entity NBT");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being saved");
         this.fillCrashReport(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   public void readFromNBT(NBTTagCompound p_70020_1_) {
      try {
         NBTTagList nbttaglist = p_70020_1_.getTagList("Pos", 6);
         NBTTagList nbttaglist2 = p_70020_1_.getTagList("Motion", 6);
         NBTTagList nbttaglist3 = p_70020_1_.getTagList("Rotation", 5);
         this.motionX = nbttaglist2.getDoubleAt(0);
         this.motionY = nbttaglist2.getDoubleAt(1);
         this.motionZ = nbttaglist2.getDoubleAt(2);
         if (Math.abs(this.motionX) > 10.0D) {
            this.motionX = 0.0D;
         }

         if (Math.abs(this.motionY) > 10.0D) {
            this.motionY = 0.0D;
         }

         if (Math.abs(this.motionZ) > 10.0D) {
            this.motionZ = 0.0D;
         }

         this.posX = nbttaglist.getDoubleAt(0);
         this.posY = nbttaglist.getDoubleAt(1);
         this.posZ = nbttaglist.getDoubleAt(2);
         this.lastTickPosX = this.posX;
         this.lastTickPosY = this.posY;
         this.lastTickPosZ = this.posZ;
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         this.rotationYaw = nbttaglist3.getFloatAt(0);
         this.rotationPitch = nbttaglist3.getFloatAt(1);
         this.prevRotationYaw = this.rotationYaw;
         this.prevRotationPitch = this.rotationPitch;
         this.setRotationYawHead(this.rotationYaw);
         this.setRenderYawOffset(this.rotationYaw);
         this.fallDistance = p_70020_1_.getFloat("FallDistance");
         this.fire = p_70020_1_.getShort("Fire");
         this.setAir(p_70020_1_.getShort("Air"));
         this.onGround = p_70020_1_.getBoolean("OnGround");
         if (p_70020_1_.hasKey("Dimension")) {
            this.dimension = DimensionType.getById(p_70020_1_.getInteger("Dimension"));
         }

         this.invulnerable = p_70020_1_.getBoolean("Invulnerable");
         this.timeUntilPortal = p_70020_1_.getInteger("PortalCooldown");
         if (p_70020_1_.hasUniqueId("UUID")) {
            this.entityUniqueID = p_70020_1_.getUniqueId("UUID");
            this.cachedUniqueIdString = this.entityUniqueID.toString();
         }

         this.setPosition(this.posX, this.posY, this.posZ);
         this.setRotation(this.rotationYaw, this.rotationPitch);
         if (p_70020_1_.hasKey("CustomName", 8)) {
            this.setCustomName(ITextComponent.Serializer.fromJson(p_70020_1_.getString("CustomName")));
         }

         this.setCustomNameVisible(p_70020_1_.getBoolean("CustomNameVisible"));
         this.setSilent(p_70020_1_.getBoolean("Silent"));
         this.setNoGravity(p_70020_1_.getBoolean("NoGravity"));
         this.setGlowing(p_70020_1_.getBoolean("Glowing"));
         if (p_70020_1_.hasKey("Tags", 9)) {
            this.tags.clear();
            NBTTagList nbttaglist1 = p_70020_1_.getTagList("Tags", 8);
            int i = Math.min(nbttaglist1.size(), 1024);

            for(int j = 0; j < i; ++j) {
               this.tags.add(nbttaglist1.getStringTagAt(j));
            }
         }

         this.readEntityFromNBT(p_70020_1_);
         if (this.shouldSetPosAfterLoading()) {
            this.setPosition(this.posX, this.posY, this.posZ);
         }

      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Loading entity NBT");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being loaded");
         this.fillCrashReport(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   protected boolean shouldSetPosAfterLoading() {
      return true;
   }

   @Nullable
   public final String getEntityString() {
      EntityType<?> entitytype = this.getType();
      ResourceLocation resourcelocation = EntityType.getId(entitytype);
      return entitytype.isSerializable() && resourcelocation != null ? resourcelocation.toString() : null;
   }

   protected abstract void readEntityFromNBT(NBTTagCompound p_70037_1_);

   protected abstract void writeEntityToNBT(NBTTagCompound p_70014_1_);

   protected NBTTagList newDoubleNBTList(double... p_70087_1_) {
      NBTTagList nbttaglist = new NBTTagList();

      for(double d0 : p_70087_1_) {
         nbttaglist.add((INBTBase)(new NBTTagDouble(d0)));
      }

      return nbttaglist;
   }

   protected NBTTagList newFloatNBTList(float... p_70049_1_) {
      NBTTagList nbttaglist = new NBTTagList();

      for(float f : p_70049_1_) {
         nbttaglist.add((INBTBase)(new NBTTagFloat(f)));
      }

      return nbttaglist;
   }

   @Nullable
   public EntityItem entityDropItem(IItemProvider p_199703_1_) {
      return this.entityDropItem(p_199703_1_, 0);
   }

   @Nullable
   public EntityItem entityDropItem(IItemProvider p_199702_1_, int p_199702_2_) {
      return this.entityDropItem(new ItemStack(p_199702_1_), (float)p_199702_2_);
   }

   @Nullable
   public EntityItem entityDropItem(ItemStack p_199701_1_) {
      return this.entityDropItem(p_199701_1_, 0.0F);
   }

   @Nullable
   public EntityItem entityDropItem(ItemStack p_70099_1_, float p_70099_2_) {
      if (p_70099_1_.isEmpty()) {
         return null;
      } else {
         EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY + (double)p_70099_2_, this.posZ, p_70099_1_);
         entityitem.setDefaultPickupDelay();
         this.world.spawnEntity(entityitem);
         return entityitem;
      }
   }

   public boolean isEntityAlive() {
      return !this.isDead;
   }

   public boolean isEntityInsideOpaqueBlock() {
      if (this.noClip) {
         return false;
      } else {
         try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
            for(int i = 0; i < 8; ++i) {
               int j = MathHelper.floor(this.posY + (double)(((float)((i >> 0) % 2) - 0.5F) * 0.1F) + (double)this.getEyeHeight());
               int k = MathHelper.floor(this.posX + (double)(((float)((i >> 1) % 2) - 0.5F) * this.width * 0.8F));
               int l = MathHelper.floor(this.posZ + (double)(((float)((i >> 2) % 2) - 0.5F) * this.width * 0.8F));
               if (blockpos$pooledmutableblockpos.getX() != k || blockpos$pooledmutableblockpos.getY() != j || blockpos$pooledmutableblockpos.getZ() != l) {
                  blockpos$pooledmutableblockpos.setPos(k, j, l);
                  if (this.world.getBlockState(blockpos$pooledmutableblockpos).causesSuffocation()) {
                     boolean flag = true;
                     return flag;
                  }
               }
            }

            return false;
         }
      }
   }

   public boolean processInitialInteract(EntityPlayer p_184230_1_, EnumHand p_184230_2_) {
      return false;
   }

   @Nullable
   public AxisAlignedBB getCollisionBox(Entity p_70114_1_) {
      return null;
   }

   public void updateRidden() {
      Entity entity = this.getRidingEntity();
      if (this.isRiding() && entity.isDead) {
         this.dismountRidingEntity();
      } else {
         this.motionX = 0.0D;
         this.motionY = 0.0D;
         this.motionZ = 0.0D;
         this.tick();
         if (this.isRiding()) {
            entity.updatePassenger(this);
         }
      }
   }

   public void updatePassenger(Entity p_184232_1_) {
      if (this.isPassenger(p_184232_1_)) {
         p_184232_1_.setPosition(this.posX, this.posY + this.getMountedYOffset() + p_184232_1_.getYOffset(), this.posZ);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void applyOrientationToEntity(Entity p_184190_1_) {
   }

   public double getYOffset() {
      return 0.0D;
   }

   public double getMountedYOffset() {
      return (double)this.height * 0.75D;
   }

   public boolean startRiding(Entity p_184220_1_) {
      return this.startRiding(p_184220_1_, false);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isLiving() {
      return this instanceof EntityLivingBase;
   }

   public boolean startRiding(Entity p_184205_1_, boolean p_184205_2_) {
      for(Entity entity = p_184205_1_; entity.ridingEntity != null; entity = entity.ridingEntity) {
         if (entity.ridingEntity == this) {
            return false;
         }
      }

      if (p_184205_2_ || this.canBeRidden(p_184205_1_) && p_184205_1_.canFitPassenger(this)) {
         if (this.isRiding()) {
            this.dismountRidingEntity();
         }

         this.ridingEntity = p_184205_1_;
         this.ridingEntity.addPassenger(this);
         return true;
      } else {
         return false;
      }
   }

   protected boolean canBeRidden(Entity p_184228_1_) {
      return this.rideCooldown <= 0;
   }

   public void removePassengers() {
      for(int i = this.riddenByEntities.size() - 1; i >= 0; --i) {
         this.riddenByEntities.get(i).dismountRidingEntity();
      }

   }

   public void dismountRidingEntity() {
      if (this.ridingEntity != null) {
         Entity entity = this.ridingEntity;
         this.ridingEntity = null;
         entity.removePassenger(this);
      }

   }

   protected void addPassenger(Entity p_184200_1_) {
      if (p_184200_1_.getRidingEntity() != this) {
         throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
      } else {
         if (!this.world.isRemote && p_184200_1_ instanceof EntityPlayer && !(this.getControllingPassenger() instanceof EntityPlayer)) {
            this.riddenByEntities.add(0, p_184200_1_);
         } else {
            this.riddenByEntities.add(p_184200_1_);
         }

      }
   }

   protected void removePassenger(Entity p_184225_1_) {
      if (p_184225_1_.getRidingEntity() == this) {
         throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
      } else {
         this.riddenByEntities.remove(p_184225_1_);
         p_184225_1_.rideCooldown = 60;
      }
   }

   protected boolean canFitPassenger(Entity p_184219_1_) {
      return this.getPassengers().size() < 1;
   }

   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
      this.setPosition(p_180426_1_, p_180426_3_, p_180426_5_);
      this.setRotation(p_180426_7_, p_180426_8_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setHeadRotation(float p_208000_1_, int p_208000_2_) {
      this.setRotationYawHead(p_208000_1_);
   }

   public float getCollisionBorderSize() {
      return 0.0F;
   }

   public Vec3d getLookVec() {
      return this.getVectorForRotation(this.rotationPitch, this.rotationYaw);
   }

   public Vec2f getPitchYaw() {
      return new Vec2f(this.rotationPitch, this.rotationYaw);
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d getForward() {
      return Vec3d.fromPitchYaw(this.getPitchYaw());
   }

   public void setPortal(BlockPos p_181015_1_) {
      if (this.timeUntilPortal > 0) {
         this.timeUntilPortal = this.getPortalCooldown();
      } else {
         if (!this.world.isRemote && !p_181015_1_.equals(this.lastPortalPos)) {
            this.lastPortalPos = new BlockPos(p_181015_1_);
            BlockPattern.PatternHelper blockpattern$patternhelper = ((BlockPortal)Blocks.NETHER_PORTAL).createPatternHelper(this.world, this.lastPortalPos);
            double d0 = blockpattern$patternhelper.getForwards().getAxis() == EnumFacing.Axis.X ? (double)blockpattern$patternhelper.getFrontTopLeft().getZ() : (double)blockpattern$patternhelper.getFrontTopLeft().getX();
            double d1 = blockpattern$patternhelper.getForwards().getAxis() == EnumFacing.Axis.X ? this.posZ : this.posX;
            d1 = Math.abs(MathHelper.pct(d1 - (double)(blockpattern$patternhelper.getForwards().rotateY().getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? 1 : 0), d0, d0 - (double)blockpattern$patternhelper.getWidth()));
            double d2 = MathHelper.pct(this.posY - 1.0D, (double)blockpattern$patternhelper.getFrontTopLeft().getY(), (double)(blockpattern$patternhelper.getFrontTopLeft().getY() - blockpattern$patternhelper.getHeight()));
            this.lastPortalVec = new Vec3d(d1, d2, 0.0D);
            this.teleportDirection = blockpattern$patternhelper.getForwards();
         }

         this.inPortal = true;
      }
   }

   public int getPortalCooldown() {
      return 300;
   }

   @OnlyIn(Dist.CLIENT)
   public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
      this.motionX = p_70016_1_;
      this.motionY = p_70016_3_;
      this.motionZ = p_70016_5_;
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
   }

   @OnlyIn(Dist.CLIENT)
   public void performHurtAnimation() {
   }

   public Iterable<ItemStack> getHeldEquipment() {
      return EMPTY_EQUIPMENT;
   }

   public Iterable<ItemStack> getArmorInventoryList() {
      return EMPTY_EQUIPMENT;
   }

   public Iterable<ItemStack> getEquipmentAndArmor() {
      return Iterables.concat(this.getHeldEquipment(), this.getArmorInventoryList());
   }

   public void setItemStackToSlot(EntityEquipmentSlot p_184201_1_, ItemStack p_184201_2_) {
   }

   public boolean isBurning() {
      boolean flag = this.world != null && this.world.isRemote;
      return !this.isImmuneToFire && (this.fire > 0 || flag && this.getFlag(0));
   }

   public boolean isRiding() {
      return this.getRidingEntity() != null;
   }

   public boolean isBeingRidden() {
      return !this.getPassengers().isEmpty();
   }

   public boolean canBeRiddenInWater() {
      return true;
   }

   public boolean isSneaking() {
      return this.getFlag(1);
   }

   public void setSneaking(boolean p_70095_1_) {
      this.setFlag(1, p_70095_1_);
   }

   public boolean isSprinting() {
      return this.getFlag(3);
   }

   public void setSprinting(boolean p_70031_1_) {
      this.setFlag(3, p_70031_1_);
   }

   public boolean isSwimming() {
      return this.getFlag(4);
   }

   public void setSwimming(boolean p_204711_1_) {
      this.setFlag(4, p_204711_1_);
   }

   public boolean isGlowing() {
      return this.glowing || this.world.isRemote && this.getFlag(6);
   }

   public void setGlowing(boolean p_184195_1_) {
      this.glowing = p_184195_1_;
      if (!this.world.isRemote) {
         this.setFlag(6, this.glowing);
      }

   }

   public boolean isInvisible() {
      return this.getFlag(5);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInvisibleToPlayer(EntityPlayer p_98034_1_) {
      if (p_98034_1_.isSpectator()) {
         return false;
      } else {
         Team team = this.getTeam();
         return team != null && p_98034_1_ != null && p_98034_1_.getTeam() == team && team.getSeeFriendlyInvisiblesEnabled() ? false : this.isInvisible();
      }
   }

   @Nullable
   public Team getTeam() {
      return this.world.getScoreboard().getPlayersTeam(this.getScoreboardName());
   }

   public boolean isOnSameTeam(Entity p_184191_1_) {
      return this.isOnScoreboardTeam(p_184191_1_.getTeam());
   }

   public boolean isOnScoreboardTeam(Team p_184194_1_) {
      return this.getTeam() != null ? this.getTeam().isSameTeam(p_184194_1_) : false;
   }

   public void setInvisible(boolean p_82142_1_) {
      this.setFlag(5, p_82142_1_);
   }

   protected boolean getFlag(int p_70083_1_) {
      return (this.dataManager.get(FLAGS) & 1 << p_70083_1_) != 0;
   }

   protected void setFlag(int p_70052_1_, boolean p_70052_2_) {
      byte b0 = this.dataManager.get(FLAGS);
      if (p_70052_2_) {
         this.dataManager.set(FLAGS, (byte)(b0 | 1 << p_70052_1_));
      } else {
         this.dataManager.set(FLAGS, (byte)(b0 & ~(1 << p_70052_1_)));
      }

   }

   public int getMaxAir() {
      return 300;
   }

   public int getAir() {
      return this.dataManager.get(AIR);
   }

   public void setAir(int p_70050_1_) {
      this.dataManager.set(AIR, p_70050_1_);
   }

   public void onStruckByLightning(EntityLightningBolt p_70077_1_) {
      ++this.fire;
      if (this.fire == 0) {
         this.setFire(8);
      }

      this.attackEntityFrom(DamageSource.LIGHTNING_BOLT, 5.0F);
   }

   public void onEnterBubbleColumnWithAirAbove(boolean p_203002_1_) {
      if (p_203002_1_) {
         this.motionY = Math.max(-0.9D, this.motionY - 0.03D);
      } else {
         this.motionY = Math.min(1.8D, this.motionY + 0.1D);
      }

   }

   public void onEnterBubbleColumn(boolean p_203004_1_) {
      if (p_203004_1_) {
         this.motionY = Math.max(-0.3D, this.motionY - 0.03D);
      } else {
         this.motionY = Math.min(0.7D, this.motionY + 0.06D);
      }

      this.fallDistance = 0.0F;
   }

   public void onKillEntity(EntityLivingBase p_70074_1_) {
   }

   protected boolean pushOutOfBlocks(double p_145771_1_, double p_145771_3_, double p_145771_5_) {
      BlockPos blockpos = new BlockPos(p_145771_1_, p_145771_3_, p_145771_5_);
      double d0 = p_145771_1_ - (double)blockpos.getX();
      double d1 = p_145771_3_ - (double)blockpos.getY();
      double d2 = p_145771_5_ - (double)blockpos.getZ();
      if (this.world.isCollisionBoxesEmpty((Entity)null, this.getEntityBoundingBox())) {
         return false;
      } else {
         EnumFacing enumfacing = EnumFacing.UP;
         double d3 = Double.MAX_VALUE;
         if (!this.world.isBlockFullCube(blockpos.west()) && d0 < d3) {
            d3 = d0;
            enumfacing = EnumFacing.WEST;
         }

         if (!this.world.isBlockFullCube(blockpos.east()) && 1.0D - d0 < d3) {
            d3 = 1.0D - d0;
            enumfacing = EnumFacing.EAST;
         }

         if (!this.world.isBlockFullCube(blockpos.north()) && d2 < d3) {
            d3 = d2;
            enumfacing = EnumFacing.NORTH;
         }

         if (!this.world.isBlockFullCube(blockpos.south()) && 1.0D - d2 < d3) {
            d3 = 1.0D - d2;
            enumfacing = EnumFacing.SOUTH;
         }

         if (!this.world.isBlockFullCube(blockpos.up()) && 1.0D - d1 < d3) {
            d3 = 1.0D - d1;
            enumfacing = EnumFacing.UP;
         }

         float f = this.rand.nextFloat() * 0.2F + 0.1F;
         float f1 = (float)enumfacing.getAxisDirection().getOffset();
         if (enumfacing.getAxis() == EnumFacing.Axis.X) {
            this.motionX = (double)(f1 * f);
            this.motionY *= 0.75D;
            this.motionZ *= 0.75D;
         } else if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            this.motionX *= 0.75D;
            this.motionY = (double)(f1 * f);
            this.motionZ *= 0.75D;
         } else if (enumfacing.getAxis() == EnumFacing.Axis.Z) {
            this.motionX *= 0.75D;
            this.motionY *= 0.75D;
            this.motionZ = (double)(f1 * f);
         }

         return true;
      }
   }

   public void setInWeb() {
      this.isInWeb = true;
      this.fallDistance = 0.0F;
   }

   private static void func_207712_c(ITextComponent p_207712_0_) {
      p_207712_0_.applyTextStyle((p_211515_0_) -> {
         p_211515_0_.setClickEvent((ClickEvent)null);
      }).getSiblings().forEach(Entity::func_207712_c);
   }

   public ITextComponent getName() {
      ITextComponent itextcomponent = this.getCustomName();
      if (itextcomponent != null) {
         ITextComponent itextcomponent1 = itextcomponent.func_212638_h();
         func_207712_c(itextcomponent1);
         return itextcomponent1;
      } else {
         return this.type.func_212546_e();
      }
   }

   @Nullable
   public Entity[] getParts() {
      return null;
   }

   public boolean isEntityEqual(Entity p_70028_1_) {
      return this == p_70028_1_;
   }

   public float getRotationYawHead() {
      return 0.0F;
   }

   public void setRotationYawHead(float p_70034_1_) {
   }

   public void setRenderYawOffset(float p_181013_1_) {
   }

   public boolean canBeAttackedWithItem() {
      return true;
   }

   public boolean hitByEntity(Entity p_85031_1_) {
      return false;
   }

   public String toString() {
      return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getName().getUnformattedComponentText(), this.entityId, this.world == null ? "~NULL~" : this.world.getWorldInfo().getWorldName(), this.posX, this.posY, this.posZ);
   }

   public boolean isInvulnerableTo(DamageSource p_180431_1_) {
      return this.invulnerable && p_180431_1_ != DamageSource.OUT_OF_WORLD && !p_180431_1_.isCreativePlayer();
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public void setInvulnerable(boolean p_184224_1_) {
      this.invulnerable = p_184224_1_;
   }

   public void copyLocationAndAnglesFrom(Entity p_82149_1_) {
      this.setLocationAndAngles(p_82149_1_.posX, p_82149_1_.posY, p_82149_1_.posZ, p_82149_1_.rotationYaw, p_82149_1_.rotationPitch);
   }

   public void copyDataFromOld(Entity p_180432_1_) {
      NBTTagCompound nbttagcompound = p_180432_1_.writeToNBT(new NBTTagCompound());
      nbttagcompound.removeTag("Dimension");
      this.readFromNBT(nbttagcompound);
      this.timeUntilPortal = p_180432_1_.timeUntilPortal;
      this.lastPortalPos = p_180432_1_.lastPortalPos;
      this.lastPortalVec = p_180432_1_.lastPortalVec;
      this.teleportDirection = p_180432_1_.teleportDirection;
   }

   @Nullable
   public Entity func_212321_a(DimensionType p_212321_1_) {
      if (!this.world.isRemote && !this.isDead) {
         this.world.profiler.startSection("changeDimension");
         MinecraftServer minecraftserver = this.getServer();
         DimensionType dimensiontype = this.dimension;
         WorldServer worldserver = minecraftserver.func_71218_a(dimensiontype);
         WorldServer worldserver1 = minecraftserver.func_71218_a(p_212321_1_);
         this.dimension = p_212321_1_;
         if (dimensiontype == DimensionType.THE_END && p_212321_1_ == DimensionType.THE_END) {
            worldserver1 = minecraftserver.func_71218_a(DimensionType.OVERWORLD);
            this.dimension = DimensionType.OVERWORLD;
         }

         this.world.removeEntity(this);
         this.isDead = false;
         this.world.profiler.startSection("reposition");
         BlockPos blockpos;
         if (p_212321_1_ == DimensionType.THE_END) {
            blockpos = worldserver1.getSpawnCoordinate();
         } else {
            double d0 = this.posX;
            double d1 = this.posZ;
            double d2 = 8.0D;
            if (p_212321_1_ == DimensionType.NETHER) {
               d0 = MathHelper.clamp(d0 / 8.0D, worldserver1.getWorldBorder().minX() + 16.0D, worldserver1.getWorldBorder().maxX() - 16.0D);
               d1 = MathHelper.clamp(d1 / 8.0D, worldserver1.getWorldBorder().minZ() + 16.0D, worldserver1.getWorldBorder().maxZ() - 16.0D);
            } else if (p_212321_1_ == DimensionType.OVERWORLD) {
               d0 = MathHelper.clamp(d0 * 8.0D, worldserver1.getWorldBorder().minX() + 16.0D, worldserver1.getWorldBorder().maxX() - 16.0D);
               d1 = MathHelper.clamp(d1 * 8.0D, worldserver1.getWorldBorder().minZ() + 16.0D, worldserver1.getWorldBorder().maxZ() - 16.0D);
            }

            d0 = (double)MathHelper.clamp((int)d0, -29999872, 29999872);
            d1 = (double)MathHelper.clamp((int)d1, -29999872, 29999872);
            float f = this.rotationYaw;
            this.setLocationAndAngles(d0, this.posY, d1, 90.0F, 0.0F);
            Teleporter teleporter = worldserver1.getDefaultTeleporter();
            teleporter.placeInExistingPortal(this, f);
            blockpos = new BlockPos(this);
         }

         worldserver.updateEntityWithOptionalForce(this, false);
         this.world.profiler.endStartSection("reloading");
         Entity entity = this.getType().create(worldserver1);
         if (entity != null) {
            entity.copyDataFromOld(this);
            if (dimensiontype == DimensionType.THE_END && p_212321_1_ == DimensionType.THE_END) {
               BlockPos blockpos1 = worldserver1.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, worldserver1.getSpawnPoint());
               entity.moveToBlockPosAndAngles(blockpos1, entity.rotationYaw, entity.rotationPitch);
            } else {
               entity.moveToBlockPosAndAngles(blockpos, entity.rotationYaw, entity.rotationPitch);
            }

            boolean flag = entity.forceSpawn;
            entity.forceSpawn = true;
            worldserver1.spawnEntity(entity);
            entity.forceSpawn = flag;
            worldserver1.updateEntityWithOptionalForce(entity, false);
         }

         this.isDead = true;
         this.world.profiler.endSection();
         worldserver.resetUpdateEntityTick();
         worldserver1.resetUpdateEntityTick();
         this.world.profiler.endSection();
         return entity;
      } else {
         return null;
      }
   }

   public boolean isNonBoss() {
      return true;
   }

   public float getExplosionResistance(Explosion p_180428_1_, IBlockReader p_180428_2_, BlockPos p_180428_3_, IBlockState p_180428_4_, IFluidState p_180428_5_, float p_180428_6_) {
      return p_180428_6_;
   }

   public boolean canExplosionDestroyBlock(Explosion p_174816_1_, IBlockReader p_174816_2_, BlockPos p_174816_3_, IBlockState p_174816_4_, float p_174816_5_) {
      return true;
   }

   public int getMaxFallHeight() {
      return 3;
   }

   public Vec3d getLastPortalVec() {
      return this.lastPortalVec;
   }

   public EnumFacing getTeleportDirection() {
      return this.teleportDirection;
   }

   public boolean doesEntityNotTriggerPressurePlate() {
      return false;
   }

   public void fillCrashReport(CrashReportCategory p_85029_1_) {
      p_85029_1_.addDetail("Entity Type", () -> {
         return EntityType.getId(this.getType()) + " (" + this.getClass().getCanonicalName() + ")";
      });
      p_85029_1_.addCrashSection("Entity ID", this.entityId);
      p_85029_1_.addDetail("Entity Name", () -> {
         return this.getName().getString();
      });
      p_85029_1_.addCrashSection("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.posX, this.posY, this.posZ));
      p_85029_1_.addCrashSection("Entity's Block location", CrashReportCategory.getCoordinateInfo(MathHelper.floor(this.posX), MathHelper.floor(this.posY), MathHelper.floor(this.posZ)));
      p_85029_1_.addCrashSection("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.motionX, this.motionY, this.motionZ));
      p_85029_1_.addDetail("Entity's Passengers", () -> {
         return this.getPassengers().toString();
      });
      p_85029_1_.addDetail("Entity's Vehicle", () -> {
         return this.getRidingEntity().toString();
      });
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canRenderOnFire() {
      return this.isBurning();
   }

   public void setUniqueId(UUID p_184221_1_) {
      this.entityUniqueID = p_184221_1_;
      this.cachedUniqueIdString = this.entityUniqueID.toString();
   }

   public UUID getUniqueID() {
      return this.entityUniqueID;
   }

   public String getCachedUniqueIdString() {
      return this.cachedUniqueIdString;
   }

   public String getScoreboardName() {
      return this.cachedUniqueIdString;
   }

   public boolean isPushedByWater() {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public static double getRenderDistanceWeight() {
      return renderDistanceWeight;
   }

   @OnlyIn(Dist.CLIENT)
   public static void setRenderDistanceWeight(double p_184227_0_) {
      renderDistanceWeight = p_184227_0_;
   }

   public ITextComponent getDisplayName() {
      return ScorePlayerTeam.formatMemberName(this.getTeam(), this.getName()).applyTextStyle((p_211516_1_) -> {
         p_211516_1_.setHoverEvent(this.getHoverEvent()).setInsertion(this.getCachedUniqueIdString());
      });
   }

   public void setCustomName(@Nullable ITextComponent p_200203_1_) {
      this.dataManager.set(CUSTOM_NAME, Optional.ofNullable(p_200203_1_));
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.dataManager.get(CUSTOM_NAME).orElse((ITextComponent)null);
   }

   public boolean hasCustomName() {
      return this.dataManager.get(CUSTOM_NAME).isPresent();
   }

   public void setCustomNameVisible(boolean p_174805_1_) {
      this.dataManager.set(CUSTOM_NAME_VISIBLE, p_174805_1_);
   }

   public boolean isCustomNameVisible() {
      return this.dataManager.get(CUSTOM_NAME_VISIBLE);
   }

   public void setPositionAndUpdate(double p_70634_1_, double p_70634_3_, double p_70634_5_) {
      this.isPositionDirty = true;
      this.setLocationAndAngles(p_70634_1_, p_70634_3_, p_70634_5_, this.rotationYaw, this.rotationPitch);
      this.world.updateEntityWithOptionalForce(this, false);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean getAlwaysRenderNameTagForRender() {
      return this.isCustomNameVisible();
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
   }

   public EnumFacing getHorizontalFacing() {
      return EnumFacing.fromAngle((double)this.rotationYaw);
   }

   public EnumFacing getAdjustedHorizontalFacing() {
      return this.getHorizontalFacing();
   }

   protected HoverEvent getHoverEvent() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      ResourceLocation resourcelocation = EntityType.getId(this.getType());
      nbttagcompound.setString("id", this.getCachedUniqueIdString());
      if (resourcelocation != null) {
         nbttagcompound.setString("type", resourcelocation.toString());
      }

      nbttagcompound.setString("name", ITextComponent.Serializer.toJson(this.getName()));
      return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new TextComponentString(nbttagcompound.toString()));
   }

   public boolean isSpectatedByPlayer(EntityPlayerMP p_174827_1_) {
      return true;
   }

   public AxisAlignedBB getEntityBoundingBox() {
      return this.boundingBox;
   }

   @OnlyIn(Dist.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return this.getEntityBoundingBox();
   }

   public void setEntityBoundingBox(AxisAlignedBB p_174826_1_) {
      this.boundingBox = p_174826_1_;
   }

   public float getEyeHeight() {
      return this.height * 0.85F;
   }

   public boolean isOutsideBorder() {
      return this.isOutsideBorder;
   }

   public void setOutsideBorder(boolean p_174821_1_) {
      this.isOutsideBorder = p_174821_1_;
   }

   public boolean replaceItemInInventory(int p_174820_1_, ItemStack p_174820_2_) {
      return false;
   }

   public void sendMessage(ITextComponent p_145747_1_) {
   }

   public BlockPos getPosition() {
      return new BlockPos(this);
   }

   public Vec3d getPositionVector() {
      return new Vec3d(this.posX, this.posY, this.posZ);
   }

   public World getEntityWorld() {
      return this.world;
   }

   @Nullable
   public MinecraftServer getServer() {
      return this.world.getServer();
   }

   public EnumActionResult applyPlayerInteraction(EntityPlayer p_184199_1_, Vec3d p_184199_2_, EnumHand p_184199_3_) {
      return EnumActionResult.PASS;
   }

   public boolean isImmuneToExplosions() {
      return false;
   }

   protected void applyEnchantments(EntityLivingBase p_174815_1_, Entity p_174815_2_) {
      if (p_174815_2_ instanceof EntityLivingBase) {
         EnchantmentHelper.applyThornEnchantments((EntityLivingBase)p_174815_2_, p_174815_1_);
      }

      EnchantmentHelper.applyArthropodEnchantments(p_174815_1_, p_174815_2_);
   }

   public void addTrackingPlayer(EntityPlayerMP p_184178_1_) {
   }

   public void removeTrackingPlayer(EntityPlayerMP p_184203_1_) {
   }

   public float getRotatedYaw(Rotation p_184229_1_) {
      float f = MathHelper.wrapDegrees(this.rotationYaw);
      switch(p_184229_1_) {
      case CLOCKWISE_180:
         return f + 180.0F;
      case COUNTERCLOCKWISE_90:
         return f + 270.0F;
      case CLOCKWISE_90:
         return f + 90.0F;
      default:
         return f;
      }
   }

   public float getMirroredYaw(Mirror p_184217_1_) {
      float f = MathHelper.wrapDegrees(this.rotationYaw);
      switch(p_184217_1_) {
      case LEFT_RIGHT:
         return -f;
      case FRONT_BACK:
         return 180.0F - f;
      default:
         return f;
      }
   }

   public boolean ignoreItemEntityData() {
      return false;
   }

   public boolean setPositionNonDirty() {
      boolean flag = this.isPositionDirty;
      this.isPositionDirty = false;
      return flag;
   }

   @Nullable
   public Entity getControllingPassenger() {
      return null;
   }

   public List<Entity> getPassengers() {
      return (List<Entity>)(this.riddenByEntities.isEmpty() ? Collections.emptyList() : Lists.newArrayList(this.riddenByEntities));
   }

   public boolean isPassenger(Entity p_184196_1_) {
      for(Entity entity : this.getPassengers()) {
         if (entity.equals(p_184196_1_)) {
            return true;
         }
      }

      return false;
   }

   public boolean isPassenger(Class<? extends Entity> p_205708_1_) {
      for(Entity entity : this.getPassengers()) {
         if (p_205708_1_.isAssignableFrom(entity.getClass())) {
            return true;
         }
      }

      return false;
   }

   public Collection<Entity> getRecursivePassengers() {
      Set<Entity> set = Sets.newHashSet();

      for(Entity entity : this.getPassengers()) {
         set.add(entity);
         entity.getRecursivePassengers(false, set);
      }

      return set;
   }

   public boolean isOnePlayerRiding() {
      Set<Entity> set = Sets.newHashSet();
      this.getRecursivePassengers(true, set);
      return set.size() == 1;
   }

   private void getRecursivePassengers(boolean p_200604_1_, Set<Entity> p_200604_2_) {
      for(Entity entity : this.getPassengers()) {
         if (!p_200604_1_ || EntityPlayerMP.class.isAssignableFrom(entity.getClass())) {
            p_200604_2_.add(entity);
         }

         entity.getRecursivePassengers(p_200604_1_, p_200604_2_);
      }

   }

   public Entity getLowestRidingEntity() {
      Entity entity;
      for(entity = this; entity.isRiding(); entity = entity.getRidingEntity()) {
         ;
      }

      return entity;
   }

   public boolean isRidingSameEntity(Entity p_184223_1_) {
      return this.getLowestRidingEntity() == p_184223_1_.getLowestRidingEntity();
   }

   public boolean isRidingOrBeingRiddenBy(Entity p_184215_1_) {
      for(Entity entity : this.getPassengers()) {
         if (entity.equals(p_184215_1_)) {
            return true;
         }

         if (entity.isRidingOrBeingRiddenBy(p_184215_1_)) {
            return true;
         }
      }

      return false;
   }

   public boolean canPassengerSteer() {
      Entity entity = this.getControllingPassenger();
      if (entity instanceof EntityPlayer) {
         return ((EntityPlayer)entity).isUser();
      } else {
         return !this.world.isRemote;
      }
   }

   @Nullable
   public Entity getRidingEntity() {
      return this.ridingEntity;
   }

   public EnumPushReaction getPushReaction() {
      return EnumPushReaction.NORMAL;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.NEUTRAL;
   }

   protected int getFireImmuneTicks() {
      return 1;
   }

   public CommandSource getCommandSource() {
      return new CommandSource(this, new Vec3d(this.posX, this.posY, this.posZ), this.getPitchYaw(), this.world instanceof WorldServer ? (WorldServer)this.world : null, this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.world.getServer(), this);
   }

   protected int getPermissionLevel() {
      return 0;
   }

   public boolean hasPermissionLevel(int p_211513_1_) {
      return this.getPermissionLevel() >= p_211513_1_;
   }

   public boolean shouldReceiveFeedback() {
      return this.world.getGameRules().getBoolean("sendCommandFeedback");
   }

   public boolean shouldReceiveErrors() {
      return true;
   }

   public boolean allowLogging() {
      return true;
   }

   public void func_200602_a(EntityAnchorArgument.Type p_200602_1_, Vec3d p_200602_2_) {
      Vec3d vec3d = p_200602_1_.func_201017_a(this);
      double d0 = p_200602_2_.x - vec3d.x;
      double d1 = p_200602_2_.y - vec3d.y;
      double d2 = p_200602_2_.z - vec3d.z;
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      this.rotationPitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI))));
      this.rotationYaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F);
      this.setRotationYawHead(this.rotationYaw);
      this.prevRotationPitch = this.rotationPitch;
      this.prevRotationYaw = this.rotationYaw;
   }

   public boolean handleFluidAcceleration(Tag<Fluid> p_210500_1_) {
      AxisAlignedBB axisalignedbb = this.getEntityBoundingBox().shrink(0.001D);
      int i = MathHelper.floor(axisalignedbb.minX);
      int j = MathHelper.ceil(axisalignedbb.maxX);
      int k = MathHelper.floor(axisalignedbb.minY);
      int l = MathHelper.ceil(axisalignedbb.maxY);
      int i1 = MathHelper.floor(axisalignedbb.minZ);
      int j1 = MathHelper.ceil(axisalignedbb.maxZ);
      if (!this.world.isAreaLoaded(i, k, i1, j, l, j1, true)) {
         return false;
      } else {
         double d0 = 0.0D;
         boolean flag = this.isPushedByWater();
         boolean flag1 = false;
         Vec3d vec3d = Vec3d.ZERO;
         int k1 = 0;

         try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
            for(int l1 = i; l1 < j; ++l1) {
               for(int i2 = k; i2 < l; ++i2) {
                  for(int j2 = i1; j2 < j1; ++j2) {
                     blockpos$pooledmutableblockpos.setPos(l1, i2, j2);
                     IFluidState ifluidstate = this.world.getFluidState(blockpos$pooledmutableblockpos);
                     if (ifluidstate.isTagged(p_210500_1_)) {
                        double d1 = (double)((float)i2 + ifluidstate.getHeight());
                        if (d1 >= axisalignedbb.minY) {
                           flag1 = true;
                           d0 = Math.max(d1 - axisalignedbb.minY, d0);
                           if (flag) {
                              Vec3d vec3d1 = ifluidstate.getFlow(this.world, blockpos$pooledmutableblockpos);
                              if (d0 < 0.4D) {
                                 vec3d1 = vec3d1.scale(d0);
                              }

                              vec3d = vec3d.add(vec3d1);
                              ++k1;
                           }
                        }
                     }
                  }
               }
            }
         }

         if (vec3d.length() > 0.0D) {
            if (k1 > 0) {
               vec3d = vec3d.scale(1.0D / (double)k1);
            }

            if (!(this instanceof EntityPlayer)) {
               vec3d = vec3d.normalize();
            }

            double d2 = 0.014D;
            this.motionX += vec3d.x * 0.014D;
            this.motionY += vec3d.y * 0.014D;
            this.motionZ += vec3d.z * 0.014D;
         }

         this.submergedHeight = d0;
         return flag1;
      }
   }

   public double getSubmergedHeight() {
      return this.submergedHeight;
   }
}
