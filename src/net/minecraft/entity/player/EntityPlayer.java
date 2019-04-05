package net.minecraft.entity.player;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBubbleColumn;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtil;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

public abstract class EntityPlayer extends EntityLivingBase {
    protected static final DataParameter<NBTTagCompound> LEFT_SHOULDER_ENTITY =
            EntityDataManager.createKey(EntityPlayer.class, DataSerializers.COMPOUND_TAG);
    protected static final DataParameter<Byte> MAIN_HAND = EntityDataManager.createKey(EntityPlayer.class,
                                                                                       DataSerializers.BYTE);
    protected static final DataParameter<Byte> PLAYER_MODEL_FLAG = EntityDataManager.createKey(EntityPlayer.class,
                                                                                               DataSerializers.BYTE);
    protected static final DataParameter<NBTTagCompound> RIGHT_SHOULDER_ENTITY = EntityDataManager.createKey(
            EntityPlayer.class,
            DataSerializers.COMPOUND_TAG);
    private static final DataParameter<Float> ABSORPTION = EntityDataManager.createKey(EntityPlayer.class,
                                                                                       DataSerializers.FLOAT);
    private static final DataParameter<Integer> PLAYER_SCORE = EntityDataManager.createKey(EntityPlayer.class,
                                                                                           DataSerializers.VARINT);
    private final CooldownTracker cooldownTracker = this.createCooldownTracker();
    private final GameProfile gameProfile;
    public BlockPos bedLocation;
    public float cameraYaw;
    public PlayerCapabilities capabilities = new PlayerCapabilities();
    public double chasingPosX;
    public double chasingPosY;
    public double chasingPosZ;
    public float experience;
    public int experienceLevel;
    public int experienceTotal;
    @Nullable
    public EntityFishHook fishEntity;
    public InventoryPlayer inventory = new InventoryPlayer(this);
    public Container inventoryContainer;
    public Container openContainer;
    public float prevCameraYaw;
    public double prevChasingPosX;
    public double prevChasingPosY;
    public double prevChasingPosZ;
    public float renderOffsetX;
    @OnlyIn(Dist.CLIENT)
    public float renderOffsetY;
    public float renderOffsetZ;
    public int xpCooldown;
    protected InventoryEnderChest enderChest = new InventoryEnderChest();
    protected boolean eyesInWaterPlayer;
    protected int flyToggleTimer;
    protected FoodStats foodStats = new FoodStats();
    protected boolean sleeping;
    protected boolean spawnForced;
    protected BlockPos spawnPos;
    protected float speedInAir = 0.02F;
    protected int xpSeed;
    @OnlyIn(Dist.CLIENT)
    private boolean hasReducedDebug;
    private boolean inBubbleColumn;
    private ItemStack itemStackMainHand = ItemStack.EMPTY;
    private int lastXPSound;
    private int naturalHealSpeed = 1280;
    private int sleepTimer;
    public int respawnXpLevel;

    public EntityPlayer(World p_i45324_1_, GameProfile p_i45324_2_) {
        super(EntityType.PLAYER, p_i45324_1_);
        this.setUniqueId(getUUID(p_i45324_2_));
        this.gameProfile = p_i45324_2_;
        this.inventoryContainer = new ContainerPlayer(this.inventory, this);
        this.openContainer = this.inventoryContainer;
        BlockPos blockpos = p_i45324_1_.getSpawnPoint();
        this.setLocationAndAngles((double) blockpos.getX() + 0.5D,
                                  (double) (blockpos.getY() + 1),
                                  (double) blockpos.getZ() + 0.5D,
                                  0.0F,
                                  0.0F);
        this.unused180 = 180.0F;
    }

    @Nullable
    public static BlockPos getBedSpawnLocation(IBlockReader p_180467_0_, BlockPos p_180467_1_, boolean p_180467_2_) {
        Block block = p_180467_0_.getBlockState(p_180467_1_).getBlock();
        if (!(block instanceof BlockBed)) {
            if (!p_180467_2_) {
                return null;
            } else {
                boolean flag = block.canSpawnInBlock();
                boolean flag1 = p_180467_0_.getBlockState(p_180467_1_.up()).getBlock().canSpawnInBlock();
                return flag && flag1 ? p_180467_1_ : null;
            }
        } else {
            return BlockBed.getSafeExitLocation(p_180467_0_, p_180467_1_, 0);
        }
    }

    public static UUID getOfflineUUID(String p_175147_0_) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + p_175147_0_).getBytes(StandardCharsets.UTF_8));
    }

    public static UUID getUUID(GameProfile p_146094_0_) {
        UUID uuid = p_146094_0_.getId();
        if (uuid == null) {
            uuid = getOfflineUUID(p_146094_0_.getName());
        }

        return uuid;
    }

    public void addExhaustion(float p_71020_1_) {
        if (!this.capabilities.disableDamage) {
            if (!this.world.isRemote) {
                this.foodStats.addExhaustion(p_71020_1_);
            }

        }
    }

    public void addExperienceLevel(int p_82242_1_) {
        this.experienceLevel += p_82242_1_;
        this.onLevelUpdate(this.experienceLevel);


        if (p_82242_1_ > 0 && this.experienceLevel % 5 == 0 &&
            (float) this.lastXPSound < (float) this.ticksExisted - 100.0F) {
            float f = this.experienceLevel > 30 ? 1.0F : (float) this.experienceLevel / 30.0F;
            this.world.playSound(null,
                                 this.posX,
                                 this.posY,
                                 this.posZ,
                                 SoundEvents.ENTITY_PLAYER_LEVELUP,
                                 this.getSoundCategory(),
                                 f * 0.75F,
                                 1.0F);
            this.lastXPSound = this.ticksExisted;
        }

    }

    public boolean addItemStackToInventory(ItemStack p_191521_1_) {
        this.playEquipSound(p_191521_1_);
        return this.inventory.addItemStackToInventory(p_191521_1_);
    }

    private void addMountedMovementStat(double p_71015_1_, double p_71015_3_, double p_71015_5_) {
        if (this.isRiding()) {
            int i = Math.round(
                    MathHelper.sqrt(p_71015_1_ * p_71015_1_ + p_71015_3_ * p_71015_3_ + p_71015_5_ * p_71015_5_) *
                    100.0F);
            if (i > 0) {
                if (this.getRidingEntity() instanceof EntityMinecart) {
                    this.func_195067_a(StatList.MINECART_ONE_CM, i);
                } else if (this.getRidingEntity() instanceof EntityBoat) {
                    this.func_195067_a(StatList.BOAT_ONE_CM, i);
                } else if (this.getRidingEntity() instanceof EntityPig) {
                    this.func_195067_a(StatList.PIG_ONE_CM, i);
                } else if (this.getRidingEntity() instanceof AbstractHorse) {
                    this.func_195067_a(StatList.HORSE_ONE_CM, i);
                }
            }
        }

    }

    public void addMovementStat(double p_71000_1_, double p_71000_3_, double p_71000_5_) {
        if (!this.isRiding()) {
            if (this.isSwimming()) {
                int i = Math.round(
                        MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_3_ * p_71000_3_ + p_71000_5_ * p_71000_5_) *
                        100.0F);
                if (i > 0) {
                    this.func_195067_a(StatList.SWIM_ONE_CM, i);
                    this.addExhaustion(0.01F * (float) i * 0.01F);
                }
            } else if (this.areEyesInFluid(FluidTags.WATER)) {
                int j = Math.round(
                        MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_3_ * p_71000_3_ + p_71000_5_ * p_71000_5_) *
                        100.0F);
                if (j > 0) {
                    this.func_195067_a(StatList.WALK_UNDER_WATER_ONE_CM, j);
                    this.addExhaustion(0.01F * (float) j * 0.01F);
                }
            } else if (this.isInWater()) {
                int k = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0F);
                if (k > 0) {
                    this.func_195067_a(StatList.WALK_ON_WATER_ONE_CM, k);
                    this.addExhaustion(0.01F * (float) k * 0.01F);
                }
            } else if (this.isOnLadder()) {
                if (p_71000_3_ > 0.0D) {
                    this.func_195067_a(StatList.CLIMB_ONE_CM, (int) Math.round(p_71000_3_ * 100.0D));
                }
            } else if (this.onGround) {
                int l = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0F);
                if (l > 0) {
                    if (this.isSprinting()) {
                        this.func_195067_a(StatList.SPRINT_ONE_CM, l);
                        this.addExhaustion(0.1F * (float) l * 0.01F);
                    } else if (this.isSneaking()) {
                        this.func_195067_a(StatList.CROUCH_ONE_CM, l);
                        this.addExhaustion(0.0F * (float) l * 0.01F);
                    } else {
                        this.func_195067_a(StatList.WALK_ONE_CM, l);
                        this.addExhaustion(0.0F * (float) l * 0.01F);
                    }
                }
            } else if (this.isElytraFlying()) {
                int i1 = Math.round(
                        MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_3_ * p_71000_3_ + p_71000_5_ * p_71000_5_) *
                        100.0F);
                this.func_195067_a(StatList.AVIATE_ONE_CM, i1);
            } else {
                int j1 = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0F);
                if (j1 > 25) {
                    this.func_195067_a(StatList.FLY_ONE_CM, j1);
                }
            }

        }
    }

    public void addScore(int p_85039_1_) {
        int i = this.getScore();
        this.dataManager.set(PLAYER_SCORE, i + p_85039_1_);
    }

    public boolean addShoulderEntity(NBTTagCompound p_192027_1_) {
        if (!this.isRiding() && this.onGround && !this.isInWater()) {
            if (this.getLeftShoulderEntity().isEmpty()) {
                this.setLeftShoulderEntity(p_192027_1_);
                return true;
            } else if (this.getRightShoulderEntity().isEmpty()) {
                this.setRightShoulderEntity(p_192027_1_);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void addStat(ResourceLocation p_195066_1_) {
        this.func_71029_a(StatList.CUSTOM.func_199076_b(p_195066_1_));
    }

    public void attackTargetEntityWithCurrentItem(Entity p_71059_1_) {
        if (p_71059_1_.canBeAttackedWithItem()) {
            if (!p_71059_1_.hitByEntity(this)) {
                float f = (float) this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                float f1;
                if (p_71059_1_ instanceof EntityLivingBase) {
                    f1 = EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(),
                                                                  ((EntityLivingBase) p_71059_1_)
                                                                          .getCreatureAttribute());
                } else {
                    f1 = EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(),
                                                                  CreatureAttribute.UNDEFINED);
                }

                float f2 = this.getCooledAttackStrength(0.5F);
                f = f * (0.2F + f2 * f2 * 0.8F);
                f1 = f1 * f2;
                this.resetCooldown();
                if (f > 0.0F || f1 > 0.0F) {
                    boolean flag = f2 > 0.9F;
                    boolean flag1 = false;
                    int i = 0;
                    i = i + EnchantmentHelper.getKnockbackModifier(this);
                    if (this.isSprinting() && flag) {
                        this.world.playSound(null,
                                             this.posX,
                                             this.posY,
                                             this.posZ,
                                             SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK,
                                             this.getSoundCategory(),
                                             1.0F,
                                             1.0F);
                        ++i;
                        flag1 = true;
                    }

                    boolean flag2 = flag && this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() &&
                                    !this.isInWater() && !this.isPotionActive(MobEffects.BLINDNESS) &&
                                    !this.isRiding() && p_71059_1_ instanceof EntityLivingBase;
                    flag2 = flag2 && !this.isSprinting();
                    if (flag2) {
                        f *= 1.5F;
                    }

                    f = f + f1;
                    boolean flag3 = false;
                    double d0 = (double) (this.distanceWalkedModified - this.prevDistanceWalkedModified);
                    if (flag && !flag2 && !flag1 && this.onGround && d0 < (double) this.getAIMoveSpeed()) {
                        ItemStack itemstack = this.getHeldItem(EnumHand.MAIN_HAND);
                        if (itemstack.getItem() instanceof ItemSword) {
                            flag3 = true;
                        }
                    }

                    float f4 = 0.0F;
                    boolean flag4 = false;
                    int j = EnchantmentHelper.getFireAspectModifier(this);
                    if (p_71059_1_ instanceof EntityLivingBase) {
                        f4 = ((EntityLivingBase) p_71059_1_).getHealth();
                        if (j > 0 && !p_71059_1_.isBurning()) {
                            flag4 = true;
                            p_71059_1_.setFire(1);
                        }
                    }

                    double d1 = p_71059_1_.motionX;
                    double d2 = p_71059_1_.motionY;
                    double d3 = p_71059_1_.motionZ;
                    boolean flag5 = p_71059_1_.attackEntityFrom(DamageSource.causePlayerDamage(this), f);
                    if (flag5) {
                        if (i > 0) {
                            if (p_71059_1_ instanceof EntityLivingBase) {
                                ((EntityLivingBase) p_71059_1_).knockBack(this,
                                                                          (float) i * 0.5F,
                                                                          (double) MathHelper.sin(this.rotationYaw *
                                                                                                  ((float) Math.PI /
                                                                                                   180F)),
                                                                          (double) (-MathHelper.cos(this.rotationYaw *
                                                                                                    ((float) Math.PI /
                                                                                                     180F))));
                            } else {
                                p_71059_1_.addVelocity((double) (-MathHelper.sin(
                                        this.rotationYaw * ((float) Math.PI / 180F)) * (float) i * 0.5F),
                                                       0.1D,
                                                       (double) (MathHelper.cos(this.rotationYaw *
                                                                                ((float) Math.PI / 180F)) * (float) i *
                                                                 0.5F));
                            }

                            this.motionX *= 0.6D;
                            this.motionZ *= 0.6D;
                            this.setSprinting(false);
                        }

                        if (flag3) {
                            float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(this) * f;

                            for (EntityLivingBase entitylivingbase : this.world
                                    .getEntitiesWithinAABB(EntityLivingBase.class,
                                                           p_71059_1_.getEntityBoundingBox().grow(1.0D, 0.25D, 1.0D))) {
                                if (entitylivingbase != this && entitylivingbase != p_71059_1_ && !this.isOnSameTeam(
                                        entitylivingbase) && (!(entitylivingbase instanceof EntityArmorStand) ||
                                                              !((EntityArmorStand) entitylivingbase).hasMarker()) &&
                                    this.getDistanceSq(entitylivingbase) < 9.0D) {
                                    entitylivingbase.knockBack(this,
                                                               0.4F,
                                                               (double) MathHelper.sin(this.rotationYaw *
                                                                                       ((float) Math.PI / 180F)),
                                                               (double) (-MathHelper.cos(this.rotationYaw *
                                                                                         ((float) Math.PI / 180F))));
                                    entitylivingbase.attackEntityFrom(DamageSource.causePlayerDamage(this), f3);
                                }
                            }

                            this.world.playSound(null,
                                                 this.posX,
                                                 this.posY,
                                                 this.posZ,
                                                 SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                                                 this.getSoundCategory(),
                                                 1.0F,
                                                 1.0F);
                            this.spawnSweepParticles();
                        }

                        if (p_71059_1_ instanceof EntityPlayerMP && p_71059_1_.velocityChanged) {
                            ((EntityPlayerMP) p_71059_1_).connection.sendPacket(new SPacketEntityVelocity(p_71059_1_));
                            p_71059_1_.velocityChanged = false;
                            p_71059_1_.motionX = d1;
                            p_71059_1_.motionY = d2;
                            p_71059_1_.motionZ = d3;
                        }

                        if (flag2) {
                            this.world.playSound(null,
                                                 this.posX,
                                                 this.posY,
                                                 this.posZ,
                                                 SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
                                                 this.getSoundCategory(),
                                                 1.0F,
                                                 1.0F);
                            this.onCriticalHit(p_71059_1_);
                        }

                        if (!flag2 && !flag3) {
                            if (flag) {
                                this.world.playSound(null,
                                                     this.posX,
                                                     this.posY,
                                                     this.posZ,
                                                     SoundEvents.ENTITY_PLAYER_ATTACK_STRONG,
                                                     this.getSoundCategory(),
                                                     1.0F,
                                                     1.0F);
                            } else {
                                this.world.playSound(null,
                                                     this.posX,
                                                     this.posY,
                                                     this.posZ,
                                                     SoundEvents.ENTITY_PLAYER_ATTACK_WEAK,
                                                     this.getSoundCategory(),
                                                     1.0F,
                                                     1.0F);
                            }
                        }

                        if (f1 > 0.0F) {
                            this.onEnchantmentCritical(p_71059_1_);
                        }

                        this.setLastAttackedEntity(p_71059_1_);
                        if (p_71059_1_ instanceof EntityLivingBase) {
                            EnchantmentHelper.applyThornEnchantments((EntityLivingBase) p_71059_1_, this);
                        }

                        EnchantmentHelper.applyArthropodEnchantments(this, p_71059_1_);
                        ItemStack itemstack1 = this.getHeldItemMainhand();
                        Entity entity = p_71059_1_;
                        if (p_71059_1_ instanceof MultiPartEntityPart) {
                            IEntityMultiPart ientitymultipart = ((MultiPartEntityPart) p_71059_1_).parent;
                            if (ientitymultipart instanceof EntityLivingBase) {
                                entity = (EntityLivingBase) ientitymultipart;
                            }
                        }

                        if (!itemstack1.isEmpty() && entity instanceof EntityLivingBase) {
                            itemstack1.hitEntity((EntityLivingBase) entity, this);
                            if (itemstack1.isEmpty()) {
                                this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                            }
                        }

                        if (p_71059_1_ instanceof EntityLivingBase) {
                            float f5 = f4 - ((EntityLivingBase) p_71059_1_).getHealth();
                            this.func_195067_a(StatList.DAMAGE_DEALT, Math.round(f5 * 10.0F));
                            if (j > 0) {
                                p_71059_1_.setFire(j * 4);
                            }

                            if (this.world instanceof WorldServer && f5 > 2.0F) {
                                int k = (int) ((double) f5 * 0.5D);
                                ((WorldServer) this.world).spawnParticle(Particles.DAMAGE_INDICATOR,
                                                                         p_71059_1_.posX,
                                                                         p_71059_1_.posY +
                                                                         (double) (p_71059_1_.height * 0.5F),
                                                                         p_71059_1_.posZ,
                                                                         k,
                                                                         0.1D,
                                                                         0.0D,
                                                                         0.1D,
                                                                         0.2D);
                            }
                        }

                        this.addExhaustion(0.1F);
                    } else {
                        this.world.playSound(null,
                                             this.posX,
                                             this.posY,
                                             this.posZ,
                                             SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE,
                                             this.getSoundCategory(),
                                             1.0F,
                                             1.0F);
                        if (flag4) {
                            p_71059_1_.extinguish();
                        }
                    }
                }

            }
        }
    }

    private boolean bedInRange(BlockPos bedPos, EnumFacing clickedFace) {
        if (Math.abs(this.posX - (double) bedPos.getX()) <= 2.0D &&
            Math.abs(this.posY - (double) bedPos.getY()) <= 2.0D &&
            Math.abs(this.posZ - (double) bedPos.getZ()) <= 2.0D) {
            return true;
        } else {
            BlockPos blockpos = bedPos.offset(clickedFace.getOpposite());
            if (Math.abs(this.posX - (double) blockpos.getX()) <= 2.0D) {
                if (Math.abs(this.posY - (double) blockpos.getY()) <= 2.0D) {
                    return Math.abs(this.posZ - (double) blockpos.getZ()) <= 2.0D;
                }
            }
            return false;
        }
    }

    public boolean canAttackPlayer(EntityPlayer p_96122_1_) {
        Team team = this.getTeam();
        Team team1 = p_96122_1_.getTeam();
        if (team == null) {
            return true;
        } else {
            return !team.isSameTeam(team1) || team.getAllowFriendlyFire();
        }
    }

    public boolean canEat(boolean p_71043_1_) {
        return !this.capabilities.disableDamage && (p_71043_1_ || this.foodStats.needFood());
    }

    public boolean canHarvestBlock(IBlockState p_184823_1_) {
        return p_184823_1_.getMaterial().isToolNotRequired() || this.inventory.canHarvestBlock(p_184823_1_);
    }

    public boolean canOpen(LockCode p_175146_1_) {
        if (p_175146_1_.isEmpty()) {
            return true;
        } else {
            ItemStack itemstack = this.getHeldItemMainhand();
            return (!itemstack.isEmpty() && itemstack.hasDisplayName()) && itemstack.getDisplayName().getString().equals(
                    p_175146_1_.getLock());
        }
    }

    public boolean canPlayerEdit(BlockPos p_175151_1_, EnumFacing p_175151_2_, ItemStack p_175151_3_) {
        if (this.capabilities.allowEdit) {
            return true;
        } else {
            BlockPos blockpos = p_175151_1_.offset(p_175151_2_.getOpposite());
            BlockWorldState blockworldstate = new BlockWorldState(this.world, blockpos, false);
            return p_175151_3_.canPlaceOn(this.world.getTags(), blockworldstate);
        }
    }

    public boolean canUseCommandBlock() {
        return this.capabilities.isCreativeMode && this.getPermissionLevel() >= 2;
    }

    public void closeScreen() {
        this.openContainer = this.inventoryContainer;
    }

    private void collideWithPlayer(Entity p_71044_1_) {
        p_71044_1_.onCollideWithPlayer(this);
    }

    protected CooldownTracker createCooldownTracker() {
        return new CooldownTracker();
    }

    protected void destroyVanishingCursedItems() {
        for (int i = 0; i < this.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.inventory.getStackInSlot(i);
            if (!itemstack.isEmpty() && EnchantmentHelper.hasVanishingCurse(itemstack)) {
                this.inventory.removeStackFromSlot(i);
            }
        }

    }

    public void disableShield(boolean p_190777_1_) {
        float f = 0.25F + (float) EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;
        if (p_190777_1_) {
            f += 0.75F;
        }

        if (this.rand.nextFloat() < f) {
            this.getCooldownTracker().setCooldown(Items.SHIELD, 100);
            this.resetActiveHand();
            this.world.setEntityState(this, (byte) 30);
        }

    }

    public void displayGUIChest(IInventory p_71007_1_) {
    }

    public void displayGui(IInteractionObject p_180468_1_) {
    }

    public void displayGuiCommandBlock(TileEntityCommandBlock p_184824_1_) {
    }

    public void displayGuiEditCommandCart(CommandBlockBaseLogic p_184809_1_) {
    }

    public void displayVillagerTradeGui(IMerchant p_180472_1_) {
    }

    @Nullable
    public EntityItem dropItem(boolean p_71040_1_) {
        return this.dropItem(this.inventory.decrStackSize(this.inventory.currentItem,
                                                          p_71040_1_ &&
                                                          !this.inventory.getCurrentItem().isEmpty() ? this.inventory
                                                                  .getCurrentItem().getCount() : 1), false, true);
    }

    @Nullable
    public EntityItem dropItem(ItemStack p_71019_1_, boolean p_71019_2_) {
        return this.dropItem(p_71019_1_, false, p_71019_2_);
    }

    @Nullable
    public EntityItem dropItem(ItemStack p_146097_1_, boolean p_146097_2_, boolean p_146097_3_) {
        if (p_146097_1_.isEmpty()) {
            return null;
        } else {
            double d0 = this.posY - (double) 0.3F + (double) this.getEyeHeight();
            EntityItem entityitem = new EntityItem(this.world, this.posX, d0, this.posZ, p_146097_1_);
            entityitem.setPickupDelay(40);
            if (p_146097_3_) {
                entityitem.setThrowerId(this.getUniqueID());
            }

            if (p_146097_2_) {
                float f = this.rand.nextFloat() * 0.5F;
                float f1 = this.rand.nextFloat() * ((float) Math.PI * 2F);
                entityitem.motionX = (double) (-MathHelper.sin(f1) * f);
                entityitem.motionZ = (double) (MathHelper.cos(f1) * f);
                entityitem.motionY = (double) 0.2F;
            } else {
                float f2 = 0.3F;
                entityitem.motionX = (double) (-MathHelper.sin(this.rotationYaw * ((float) Math.PI / 180F)) *
                                               MathHelper.cos(this.rotationPitch * ((float) Math.PI / 180F)) * f2);
                entityitem.motionZ = (double) (MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F)) *
                                               MathHelper.cos(this.rotationPitch * ((float) Math.PI / 180F)) * f2);
                entityitem.motionY = (double) (-MathHelper.sin(this.rotationPitch * ((float) Math.PI / 180F)) * f2 +
                                               0.1F);
                float f3 = this.rand.nextFloat() * ((float) Math.PI * 2F);
                f2 = 0.02F * this.rand.nextFloat();
                entityitem.motionX += Math.cos((double) f3) * (double) f2;
                entityitem.motionY += (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
                entityitem.motionZ += Math.sin((double) f3) * (double) f2;
            }

            ItemStack itemstack = this.dropItemAndGetStack(entityitem);
            if (p_146097_3_) {
                if (!itemstack.isEmpty()) {
                    this.func_71064_a(StatList.ITEM_DROPPED.func_199076_b(itemstack.getItem()), p_146097_1_.getCount());
                }

                this.addStat(StatList.DROP);
            }

            return entityitem;
        }
    }

    public ItemStack dropItemAndGetStack(EntityItem p_184816_1_) {
        this.world.spawnEntity(p_184816_1_);
        return p_184816_1_.getItem();
    }

    public void func_175145_a(Stat<?> p_175145_1_) {
    }

    public void func_195067_a(ResourceLocation p_195067_1_, int p_195067_2_) {
        this.func_71064_a(StatList.CUSTOM.func_199076_b(p_195067_1_), p_195067_2_);
    }

    public void addXpValue(int p_195068_1_) {
        this.addScore(p_195068_1_);
        //MITEMODDED Support minus levels
        this.experience +=  ((float) p_195068_1_ / (float) Math.abs(this.xpBarCap()));
        this.experienceTotal = MathHelper.clamp(this.experienceTotal + p_195068_1_, 0, Integer.MAX_VALUE);

        while (this.experience < 0.0F) {
            float f = this.experience * (float) this.xpBarCap();
            if (this.experienceLevel > 0) {
                this.addExperienceLevel(-1);
                this.experience = 1.0F + f / (float) this.xpBarCap();
            } else {
                this.addExperienceLevel(-1);
                this.experience = 0.0F;
            }
        }

        while (this.experience >= 1.0F) {
            this.experience = (this.experience - 1.0F) * (float) this.xpBarCap();
            this.addExperienceLevel(1);
            this.experience /= (float) this.xpBarCap();
        }

    }

    @OnlyIn(Dist.CLIENT)
    protected boolean func_207402_f(BlockPos p_207402_1_) {
        return this.isNormalCube(p_207402_1_) && !this.world.getBlockState(p_207402_1_.up()).isNormalCube();
    }

    private ITextComponent func_208016_c(ITextComponent p_208016_1_) {
        String s = this.getGameProfile().getName();
        return p_208016_1_.applyTextStyle((p_211521_2_) -> {
            p_211521_2_.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + s + " "))
                       .setHoverEvent(this.getHoverEvent()).setInsertion(s);
        });
    }

    public ITextComponent func_208017_dF() {
        return (new TextComponentString("")).appendSibling(this.getName()).appendText(" (").appendText(this.gameProfile
                                                                                                               .getId()
                                                                                                               .toString())
                                            .appendText(")");
    }

    public void func_71029_a(Stat<?> p_71029_1_) {
        this.func_71064_a(p_71029_1_, 1);
    }

    public void func_71064_a(Stat<?> p_71064_1_, int p_71064_2_) {
    }

    public float getArmorVisibility() {
        int i = 0;

        for (ItemStack itemstack : this.inventory.armorInventory) {
            if (!itemstack.isEmpty()) {
                ++i;
            }
        }

        return (float) i / (float) this.inventory.armorInventory.size();
    }

    public BlockPos getBedLocation() {
        return this.spawnPos;
    }

    @OnlyIn(Dist.CLIENT)
    public float getBedOrientationInDegrees() {
        if (this.bedLocation != null) {
            EnumFacing enumfacing = this.world.getBlockState(this.bedLocation).get(BlockHorizontal.HORIZONTAL_FACING);
            switch (enumfacing) {
                case SOUTH:
                    return 90.0F;
                case WEST:
                    return 0.0F;
                case NORTH:
                    return 270.0F;
                case EAST:
                    return 180.0F;
            }
        }

        return 0.0F;
    }

    public float getCooldownPeriod() {
        return (float) (1.0D / this.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * 20.0D);
    }

    public CooldownTracker getCooldownTracker() {
        return this.cooldownTracker;
    }

    public float getCooledAttackStrength(float p_184825_1_) {
        return MathHelper.clamp(((float) this.ticksSinceLastSwing + p_184825_1_) / this.getCooldownPeriod(),
                                0.0F,
                                1.0F);
    }

    public float getDigSpeed(IBlockState targetBlock) {
        float destroySpeed = this.inventory.getDestroySpeed(targetBlock);
        if (destroySpeed > 1.0F) {
            int i = EnchantmentHelper.getEfficiencyModifier(this);
            ItemStack itemstack = this.getHeldItemMainhand();
            if (i > 0 && !itemstack.isEmpty()) {
                destroySpeed += (float) (i * i + 1);
            }
        }

        if (PotionUtil.func_205135_a(this)) {
            destroySpeed *= 1.0F + (float) (PotionUtil.func_205134_b(this) + 1) * 0.2F;
        }

        if (this.isPotionActive(MobEffects.MINING_FATIGUE)) {
            float f1;
            switch (this.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;
                case 1:
                    f1 = 0.09F;
                    break;
                case 2:
                    f1 = 0.0027F;
                    break;
                case 3:
                default:
                    f1 = 8.1E-4F;
            }

            destroySpeed *= f1;
        }

        if (this.areEyesInFluid(FluidTags.WATER) && !EnchantmentHelper.getAquaAffinityModifier(this)) {
            destroySpeed /= 5.0F;
        }

        if (!this.onGround) {
            destroySpeed /= 5.0F;
        }

        //MITEMODDED Slowly destroying
        destroySpeed /= 4;
        destroySpeed *= 1+0.02*experienceLevel;

        return destroySpeed;
    }

    public FoodStats getFoodStats() {
        return this.foodStats;
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public InventoryEnderChest getInventoryEnderChest() {
        return this.enderChest;
    }

    public NBTTagCompound getLeftShoulderEntity() {
        return this.dataManager.get(LEFT_SHOULDER_ENTITY);
    }

    protected void setLeftShoulderEntity(NBTTagCompound p_192029_1_) {
        this.dataManager.set(LEFT_SHOULDER_ENTITY, p_192029_1_);
    }

    public float getLuck() {
        return (float) this.getAttribute(SharedMonsterAttributes.LUCK).getAttributeValue();
    }

    public int getNaturalHealSpeed() {
        return naturalHealSpeed;
    }

    public void setNaturalHealSpeed(int naturalHealSpeed) {
        this.naturalHealSpeed = naturalHealSpeed;
    }

    public NBTTagCompound getRightShoulderEntity() {
        return this.dataManager.get(RIGHT_SHOULDER_ENTITY);
    }

    protected void setRightShoulderEntity(NBTTagCompound p_192031_1_) {
        this.dataManager.set(RIGHT_SHOULDER_ENTITY, p_192031_1_);
    }

    public int getScore() {
        return this.dataManager.get(PLAYER_SCORE);
    }

    public void setScore(int p_85040_1_) {
        this.dataManager.set(PLAYER_SCORE, p_85040_1_);
    }

    @OnlyIn(Dist.CLIENT)
    public int getSleepTimer() {
        return this.sleepTimer;
    }

    public Scoreboard getWorldScoreboard() {
        return this.world.getScoreboard();
    }

    public int getXPSeed() {
        return this.xpSeed;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasReducedDebug() {
        return this.hasReducedDebug;
    }

    public EnumActionResult interactOn(Entity p_190775_1_, EnumHand p_190775_2_) {
        if (this.isSpectator()) {
            if (p_190775_1_ instanceof IInventory) {
                this.displayGUIChest((IInventory) p_190775_1_);
            }

            return EnumActionResult.PASS;
        } else {
            ItemStack itemstack = this.getHeldItem(p_190775_2_);
            ItemStack itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
            if (p_190775_1_.processInitialInteract(this, p_190775_2_)) {
                if (this.capabilities.isCreativeMode && itemstack == this.getHeldItem(p_190775_2_) &&
                    itemstack.getCount() < itemstack1.getCount()) {
                    itemstack.setCount(itemstack1.getCount());
                }

                return EnumActionResult.SUCCESS;
            } else {
                if (!itemstack.isEmpty() && p_190775_1_ instanceof EntityLivingBase) {
                    if (this.capabilities.isCreativeMode) {
                        itemstack = itemstack1;
                    }

                    if (itemstack.interactWithEntity(this, (EntityLivingBase) p_190775_1_, p_190775_2_)) {
                        if (itemstack.isEmpty() && !this.capabilities.isCreativeMode) {
                            this.setHeldItem(p_190775_2_, ItemStack.EMPTY);
                        }

                        return EnumActionResult.SUCCESS;
                    }
                }

                return EnumActionResult.PASS;
            }
        }
    }

    public boolean isAllowEdit() {
        return this.capabilities.allowEdit;
    }

    public abstract boolean isCreative();

    private boolean isInBed() {
        return this.world.getBlockState(this.bedLocation).getBlock() instanceof BlockBed;
    }

    @OnlyIn(Dist.CLIENT)
    protected boolean isNormalCube(BlockPos p_207401_1_) {
        return !this.world.getBlockState(p_207401_1_).isNormalCube();
    }

    public boolean isPlayerFullyAsleep() {
        return this.sleeping && this.sleepTimer >= 100;
    }

    public boolean isSpawnForced() {
        return this.spawnForced;
    }

    public abstract boolean isSpectator();

    public boolean isUser() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isWearing(EnumPlayerModelParts p_175148_1_) {
        return (this.getDataManager().get(PLAYER_MODEL_FLAG) & p_175148_1_.getPartMask()) == p_175148_1_.getPartMask();
    }

    public void onCriticalHit(Entity p_71009_1_) {
    }

    public void onEnchant(ItemStack p_192024_1_, int spentLevel) {
        this.experienceLevel -= spentLevel;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experience = 0.0F;
            this.experienceTotal = 0;
        }
        this.onLevelUpdate(experienceLevel);

        this.xpSeed = this.rand.nextInt();
    }

    public void onEnchantmentCritical(Entity p_71047_1_) {
    }

    //MITEMODDED Add
    protected void onLevelUpdate(int experienceLevel) {
        double modifiedHealth = (double) (experienceLevel - (experienceLevel % 5)) / 5 * 2 + 6;
        if (modifiedHealth < 6) {
            this.setMaxHealth(6);
            this.foodStats.setMaxFoodLevel(6);
        } else if (modifiedHealth <= 20) {
            this.setMaxHealth(modifiedHealth);
            this.foodStats.setMaxFoodLevel((int) modifiedHealth);
        } else {
            this.setMaxHealth(20);
            this.foodStats.setMaxFoodLevel(20);
        }

        if (this.getHealth() > getMaxHealth()) {
            this.setHealth(getMaxHealth());
        }
        if (this.foodStats.getFoodLevel() > this.foodStats.getMaxFoodLevel()) {
            this.foodStats.setFoodLevel(this.foodStats.getMaxFoodLevel());
        }
        this.sendPlayerAbilities();
    }

    public void openBook(ItemStack p_184814_1_, EnumHand p_184814_2_) {
    }

    public void openEditSign(TileEntitySign p_175141_1_) {
    }

    public void openEditStructure(TileEntityStructure p_189807_1_) {
    }

    public void openGuiHorseInventory(AbstractHorse p_184826_1_, IInventory p_184826_2_) {
    }

    private void playShoulderEntityAmbientSound(@Nullable NBTTagCompound p_192028_1_) {
        if (p_192028_1_ != null && !p_192028_1_.hasKey("Silent") || !p_192028_1_.getBoolean("Silent")) {
            String s = p_192028_1_.getString("id");
            if (EntityType.getById(s) == EntityType.PARROT) {
                EntityParrot.playAmbientSound(this.world, this);
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void preparePlayerToSpawn() {
        this.setSize(0.6F, 1.8F);
        super.preparePlayerToSpawn();
        this.setHealth(this.getMaxHealth());
        this.deathTime = 0;
    }

    public void setDead() {
        super.setDead();
        this.inventoryContainer.onContainerClosed(this);
        if (this.openContainer != null) {
            this.openContainer.onContainerClosed(this);
        }

    }

    public int getMaxInPortalTime() {
        return this.capabilities.disableDamage ? 1 : 80;
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_PLAYER_SWIM;
    }

    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_PLAYER_SPLASH;
    }

    protected SoundEvent getHighspeedSplashSound() {
        return SoundEvents.ENTITY_PLAYER_SPLASH_HIGH_SPEED;
    }

    public void playSound(SoundEvent p_184185_1_, float p_184185_2_, float p_184185_3_) {
        this.world.playSound(this,
                             this.posX,
                             this.posY,
                             this.posZ,
                             p_184185_1_,
                             this.getSoundCategory(),
                             p_184185_2_,
                             p_184185_3_);
    }

    protected boolean canTriggerWalking() {
        return !this.capabilities.isFlying;
    }

    public void updateSwimming() {
        if (this.capabilities.isFlying) {
            this.setSwimming(false);
        } else {
            super.updateSwimming();
        }

    }

    protected void doWaterSplashEffect() {
        if (!this.isSpectator()) {
            super.doWaterSplashEffect();
        }

    }

    public void applyEntityCollision(Entity p_70108_1_) {
        if (!this.isPlayerSleeping()) {
            super.applyEntityCollision(p_70108_1_);
        }

    }

    public boolean isEntityInsideOpaqueBlock() {
        return !this.sleeping && super.isEntityInsideOpaqueBlock();
    }

    public double getYOffset() {
        return -0.35D;
    }

    public int getPortalCooldown() {
        return 10;
    }

    public Iterable<ItemStack> getHeldEquipment() {
        return Lists.newArrayList(this.getHeldItemMainhand(), this.getHeldItemOffhand());
    }

    public boolean isSwimming() {
        return !this.capabilities.isFlying && !this.isSpectator() && super.isSwimming();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isInvisibleToPlayer(EntityPlayer p_98034_1_) {
        if (!this.isInvisible()) {
            return false;
        } else if (p_98034_1_.isSpectator()) {
            return false;
        } else {
            Team team = this.getTeam();
            return team == null || p_98034_1_ == null || p_98034_1_.getTeam() != team ||
                   !team.getSeeFriendlyInvisiblesEnabled();
        }
    }

    public void onKillEntity(EntityLivingBase p_70074_1_) {
        this.func_71029_a(StatList.ENTITY_KILLED.func_199076_b(p_70074_1_.getType()));
    }

    public void setInWeb() {
        if (!this.capabilities.isFlying) {
            super.setInWeb();
        }

    }

    public ITextComponent getName() {
        return new TextComponentString(this.gameProfile.getName());
    }

    public String getScoreboardName() {
        return this.getGameProfile().getName();
    }

    public boolean isPushedByWater() {
        return !this.capabilities.isFlying;
    }

    public ITextComponent getDisplayName() {
        ITextComponent itextcomponent = ScorePlayerTeam.formatMemberName(this.getTeam(), this.getName());
        return this.func_208016_c(itextcomponent);
    }

    public float getEyeHeight() {
        float f = 1.62F;
        if (this.isPlayerSleeping()) {
            f = 0.2F;
        } else if (!this.isSwimming() && !this.isElytraFlying() && this.height != 0.6F) {
            if (this.isSneaking() || this.height == 1.65F) {
                f -= 0.08F;
            }
        } else {
            f = 0.4F;
        }

        return f;
    }

    public boolean replaceItemInInventory(int p_174820_1_, ItemStack p_174820_2_) {
        if (p_174820_1_ >= 0 && p_174820_1_ < this.inventory.mainInventory.size()) {
            this.inventory.setInventorySlotContents(p_174820_1_, p_174820_2_);
            return true;
        } else {
            EntityEquipmentSlot entityequipmentslot;
            if (p_174820_1_ == 100 + EntityEquipmentSlot.HEAD.getIndex()) {
                entityequipmentslot = EntityEquipmentSlot.HEAD;
            } else if (p_174820_1_ == 100 + EntityEquipmentSlot.CHEST.getIndex()) {
                entityequipmentslot = EntityEquipmentSlot.CHEST;
            } else if (p_174820_1_ == 100 + EntityEquipmentSlot.LEGS.getIndex()) {
                entityequipmentslot = EntityEquipmentSlot.LEGS;
            } else if (p_174820_1_ == 100 + EntityEquipmentSlot.FEET.getIndex()) {
                entityequipmentslot = EntityEquipmentSlot.FEET;
            } else {
                entityequipmentslot = null;
            }

            if (p_174820_1_ == 98) {
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, p_174820_2_);
                return true;
            } else if (p_174820_1_ == 99) {
                this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, p_174820_2_);
                return true;
            } else if (entityequipmentslot == null) {
                int i = p_174820_1_ - 200;
                if (i >= 0 && i < this.enderChest.getSizeInventory()) {
                    this.enderChest.setInventorySlotContents(i, p_174820_2_);
                    return true;
                } else {
                    return false;
                }
            } else {
                if (!p_174820_2_.isEmpty()) {
                    if (!(p_174820_2_.getItem() instanceof ItemArmor) &&
                        !(p_174820_2_.getItem() instanceof ItemElytra)) {
                        if (entityequipmentslot != EntityEquipmentSlot.HEAD) {
                            return false;
                        }
                    } else if (EntityLiving.getSlotForItemStack(p_174820_2_) != entityequipmentslot) {
                        return false;
                    }
                }

                this.inventory.setInventorySlotContents(
                        entityequipmentslot.getIndex() + this.inventory.mainInventory.size(), p_174820_2_);
                return true;
            }
        }
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.PLAYERS;
    }

    protected int getFireImmuneTicks() {
        return 20;
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(ABSORPTION, 0.0F);
        this.dataManager.register(PLAYER_SCORE, 0);
        this.dataManager.register(PLAYER_MODEL_FLAG, (byte) 0);
        this.dataManager.register(MAIN_HAND, (byte) 1);
        this.dataManager.register(LEFT_SHOULDER_ENTITY, new NBTTagCompound());
        this.dataManager.register(RIGHT_SHOULDER_ENTITY, new NBTTagCompound());
    }

    protected void registerAttributes() {
        super.registerAttributes();
        this.setMaxHealth(6f);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double) 0.1F);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_SPEED);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.LUCK);
    }

    protected int getExperiencePoints(EntityPlayer p_70693_1_) {
        if (!this.world.getGameRules().getBoolean("keepInventory") && !this.isSpectator()) {
            int n = this.experienceLevel;
            //MITEMODDED Drop fewer Xp
            return (5 * n * n+ 15 * n) /3;
        } else {
            return 0;
        }
    }

    protected boolean isPlayer() {
        return true;
    }

    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        super.writeEntityToNBT(p_70014_1_);
        p_70014_1_.setInteger("DataVersion", 1631);
        p_70014_1_.setTag("Inventory", this.inventory.writeToNBT(new NBTTagList()));
        p_70014_1_.setInteger("SelectedItemSlot", this.inventory.currentItem);
        p_70014_1_.setBoolean("Sleeping", this.sleeping);
        p_70014_1_.setShort("SleepTimer", (short) this.sleepTimer);
        p_70014_1_.setFloat("XpP", this.experience);
        p_70014_1_.setInteger("XpLevel", this.experienceLevel);
        p_70014_1_.setInteger("XpTotal", this.experienceTotal);
        p_70014_1_.setInteger("XpSeed", this.xpSeed);
        p_70014_1_.setInteger("Score", this.getScore());
        if (this.spawnPos != null) {
            p_70014_1_.setInteger("SpawnX", this.spawnPos.getX());
            p_70014_1_.setInteger("SpawnY", this.spawnPos.getY());
            p_70014_1_.setInteger("SpawnZ", this.spawnPos.getZ());
            p_70014_1_.setBoolean("SpawnForced", this.spawnForced);
        }

        this.foodStats.writeNBT(p_70014_1_);
        this.capabilities.writeCapabilitiesToNBT(p_70014_1_);
        p_70014_1_.setTag("EnderItems", this.enderChest.write());
        if (!this.getLeftShoulderEntity().isEmpty()) {
            p_70014_1_.setTag("ShoulderEntityLeft", this.getLeftShoulderEntity());
        }

        if (!this.getRightShoulderEntity().isEmpty()) {
            p_70014_1_.setTag("ShoulderEntityRight", this.getRightShoulderEntity());
        }

    }

    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        super.readEntityFromNBT(p_70037_1_);
        this.setUniqueId(getUUID(this.gameProfile));
        NBTTagList nbttaglist = p_70037_1_.getTagList("Inventory", 10);
        this.inventory.readFromNBT(nbttaglist);
        this.inventory.currentItem = p_70037_1_.getInteger("SelectedItemSlot");
        this.sleeping = p_70037_1_.getBoolean("Sleeping");
        this.sleepTimer = p_70037_1_.getShort("SleepTimer");
        this.experience = p_70037_1_.getFloat("XpP");
        this.experienceLevel = p_70037_1_.getInteger("XpLevel");
        this.experienceTotal = p_70037_1_.getInteger("XpTotal");
        this.xpSeed = p_70037_1_.getInteger("XpSeed");
        if (this.xpSeed == 0) {
            this.xpSeed = this.rand.nextInt();
        }

        this.setScore(p_70037_1_.getInteger("Score"));
        if (this.sleeping) {
            this.bedLocation = new BlockPos(this);
            this.wakeUpPlayer(true, true, false);
        }


        if (p_70037_1_.hasKey("SpawnX", 99) && p_70037_1_.hasKey("SpawnY", 99) && p_70037_1_.hasKey("SpawnZ", 99)) {
            this.spawnPos = new BlockPos(p_70037_1_.getInteger("SpawnX"),
                                         p_70037_1_.getInteger("SpawnY"),
                                         p_70037_1_.getInteger("SpawnZ"));
            this.spawnForced = p_70037_1_.getBoolean("SpawnForced");
        }
        this.foodStats.readNBT(p_70037_1_);
        this.capabilities.readCapabilitiesFromNBT(p_70037_1_);
        if (p_70037_1_.hasKey("EnderItems", 9)) {
            this.enderChest.read(p_70037_1_.getTagList("EnderItems", 10));
        }

        if (p_70037_1_.hasKey("ShoulderEntityLeft", 10)) {
            this.setLeftShoulderEntity(p_70037_1_.getCompoundTag("ShoulderEntityLeft"));
        }

        if (p_70037_1_.hasKey("ShoulderEntityRight", 10)) {
            this.setRightShoulderEntity(p_70037_1_.getCompoundTag("ShoulderEntityRight"));
        }
        this.onLevelUpdate(experienceLevel);

    }

    public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
        if (this.isInvulnerableTo(p_70097_1_)) {
            return false;
        } else if (this.capabilities.disableDamage && !p_70097_1_.canHarmInCreative()) {
            return false;
        } else {
            this.idleTime = 0;
            if (this.getHealth() <= 0.0F) {
                return false;
            } else {
                if (this.isPlayerSleeping() && !this.world.isRemote) {
                    this.wakeUpPlayer(true, true, false);
                }

                this.spawnShoulderEntities();
                if (p_70097_1_.isDifficultyScaled()) {
                    if (this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
                        p_70097_2_ = 0.0F;
                    }

                    if (this.world.getDifficulty() == EnumDifficulty.EASY) {
                        p_70097_2_ = Math.min(p_70097_2_ / 2.0F + 1.0F, p_70097_2_);
                    }

                    if (this.world.getDifficulty() == EnumDifficulty.HARD) {
                        p_70097_2_ = p_70097_2_ * 3.0F / 2.0F;
                    }
                }

                return p_70097_2_ != 0.0F && super.attackEntityFrom(p_70097_1_, p_70097_2_);
            }
        }
    }

    protected void blockUsingShield(EntityLivingBase p_190629_1_) {
        super.blockUsingShield(p_190629_1_);
        if (p_190629_1_.getHeldItemMainhand().getItem() instanceof ItemAxe) {
            this.disableShield(true);
        }

    }

    public void onDeath(DamageSource p_70645_1_) {
        super.onDeath(p_70645_1_);
        this.setSize(0.2F, 0.2F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionY = (double) 0.1F;
        if ("Notch".equals(this.getName().getString())) {
            this.dropItem(new ItemStack(Items.APPLE), true, false);
        }

        if (!this.world.getGameRules().getBoolean("keepInventory") && !this.isSpectator()) {
            this.destroyVanishingCursedItems();
            this.inventory.dropAllItems();
        }

        if (p_70645_1_ != null) {
            this.motionX = (double) (-MathHelper.cos(
                    (this.attackedAtYaw + this.rotationYaw) * ((float) Math.PI / 180F)) * 0.1F);
            this.motionZ = (double) (-MathHelper.sin(
                    (this.attackedAtYaw + this.rotationYaw) * ((float) Math.PI / 180F)) * 0.1F);
        } else {
            this.motionX = 0.0D;
            this.motionZ = 0.0D;
        }

        this.addStat(StatList.DEATHS);
        this.func_175145_a(StatList.CUSTOM.func_199076_b(StatList.TIME_SINCE_DEATH));
        this.func_175145_a(StatList.CUSTOM.func_199076_b(StatList.TIME_SINCE_REST));
        this.extinguish();
        this.setFlag(0, false);
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        if (p_184601_1_ == DamageSource.ON_FIRE) {
            return SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE;
        } else {
            return p_184601_1_ ==
                   DamageSource.DROWN ? SoundEvents.ENTITY_PLAYER_HURT_DROWN : SoundEvents.ENTITY_PLAYER_HURT;
        }
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PLAYER_DEATH;
    }

    protected SoundEvent getFallSound(int p_184588_1_) {
        return p_184588_1_ > 4 ? SoundEvents.ENTITY_PLAYER_BIG_FALL : SoundEvents.ENTITY_PLAYER_SMALL_FALL;
    }

    public void fall(float p_180430_1_, float p_180430_2_) {
        if (!this.capabilities.allowFlying) {
            if (p_180430_1_ >= 2.0F) {
                this.func_195067_a(StatList.FALL_ONE_CM, (int) Math.round((double) p_180430_1_ * 100.0D));
            }

            super.fall(p_180430_1_, p_180430_2_);
        }
    }

    protected void damageArmor(float p_70675_1_) {
        this.inventory.damageArmor(p_70675_1_);
    }

    protected void damageShield(float p_184590_1_) {
        if (p_184590_1_ >= 3.0F && this.activeItemStack.getItem() == Items.SHIELD) {
            int i = 1 + MathHelper.floor(p_184590_1_);
            this.activeItemStack.damageItem(i, this);
            if (this.activeItemStack.isEmpty()) {
                EnumHand enumhand = this.getActiveHand();
                if (enumhand == EnumHand.MAIN_HAND) {
                    this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
                } else {
                    this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }

                this.activeItemStack = ItemStack.EMPTY;
                this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.rand.nextFloat() * 0.4F);
            }
        }

    }

    protected void damageEntity(DamageSource p_70665_1_, float p_70665_2_) {
        if (!this.isInvulnerableTo(p_70665_1_)) {
            p_70665_2_ = this.applyArmorCalculations(p_70665_1_, p_70665_2_);
            p_70665_2_ = this.applyPotionDamageCalculations(p_70665_1_, p_70665_2_);
            float f = p_70665_2_;
            p_70665_2_ = Math.max(p_70665_2_ - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (f - p_70665_2_));
            float f1 = f - p_70665_2_;
            if (f1 > 0.0F && f1 < 3.4028235E37F) {
                this.func_195067_a(StatList.field_212738_J, Math.round(f1 * 10.0F));
            }

            if (p_70665_2_ != 0.0F) {
                this.addExhaustion(p_70665_1_.getHungerDamage());
                float f2 = this.getHealth();
                this.setHealth(this.getHealth() - p_70665_2_);
                this.getCombatTracker().trackDamage(p_70665_1_, f2, p_70665_2_);
                if (p_70665_2_ < 3.4028235E37F) {
                    this.func_195067_a(StatList.DAMAGE_TAKEN, Math.round(p_70665_2_ * 10.0F));
                }

            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte p_70103_1_) {
        if (p_70103_1_ == 9) {
            this.onItemUseFinish();
        } else if (p_70103_1_ == 23) {
            this.hasReducedDebug = false;
        } else if (p_70103_1_ == 22) {
            this.hasReducedDebug = true;
        } else {
            super.handleStatusUpdate(p_70103_1_);
        }

    }

    public Iterable<ItemStack> getArmorInventoryList() {
        return this.inventory.armorInventory;
    }

    public ItemStack getItemStackFromSlot(EntityEquipmentSlot p_184582_1_) {
        if (p_184582_1_ == EntityEquipmentSlot.MAINHAND) {
            return this.inventory.getCurrentItem();
        } else if (p_184582_1_ == EntityEquipmentSlot.OFFHAND) {
            return this.inventory.offHandInventory.get(0);
        } else {
            return p_184582_1_.getSlotType() == EntityEquipmentSlot.Type.ARMOR ? this.inventory.armorInventory.get(
                    p_184582_1_.getIndex()) : ItemStack.EMPTY;
        }
    }

    public void setItemStackToSlot(EntityEquipmentSlot p_184201_1_, ItemStack p_184201_2_) {
        if (p_184201_1_ == EntityEquipmentSlot.MAINHAND) {
            this.playEquipSound(p_184201_2_);
            this.inventory.mainInventory.set(this.inventory.currentItem, p_184201_2_);
        } else if (p_184201_1_ == EntityEquipmentSlot.OFFHAND) {
            this.playEquipSound(p_184201_2_);
            this.inventory.offHandInventory.set(0, p_184201_2_);
        } else if (p_184201_1_.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
            this.playEquipSound(p_184201_2_);
            this.inventory.armorInventory.set(p_184201_1_.getIndex(), p_184201_2_);
        }

    }

    protected boolean isMovementBlocked() {
        return this.getHealth() <= 0.0F || this.isPlayerSleeping();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    public void jump() {
        super.jump();
        this.addStat(StatList.JUMP);
        if (this.isSprinting()) {
            this.addExhaustion(0.2F);
        } else {
            this.addExhaustion(0.05F);
        }

    }

    public void travel(float p_191986_1_, float p_191986_2_, float p_191986_3_) {
        double d0 = this.posX;
        double d1 = this.posY;
        double d2 = this.posZ;
        if (this.isSwimming() && !this.isRiding()) {
            double d3 = this.getLookVec().y;
            double d4 = d3 < -0.2D ? 0.085D : 0.06D;
            if (d3 <= 0.0D || this.isJumping || !this.world.getBlockState(new BlockPos(this.posX,
                                                                                       this.posY + 1.0D - 0.1D,
                                                                                       this.posZ)).getFluidState()
                                                           .isEmpty()) {
                this.motionY += (d3 - this.motionY) * d4;
            }
        }

        if (this.capabilities.isFlying && !this.isRiding()) {
            double d5 = this.motionY;
            float f = this.jumpMovementFactor;
            this.jumpMovementFactor = this.capabilities.getFlySpeed() * (float) (this.isSprinting() ? 2 : 1);
            super.travel(p_191986_1_, p_191986_2_, p_191986_3_);
            this.motionY = d5 * 0.6D;
            this.jumpMovementFactor = f;
            this.fallDistance = 0.0F;
            this.setFlag(7, false);
        } else {
            super.travel(p_191986_1_, p_191986_2_, p_191986_3_);
        }

        this.addMovementStat(this.posX - d0, this.posY - d1, this.posZ - d2);
    }

    public float getAIMoveSpeed() {
        return (float) this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
    }

    public boolean isPlayerSleeping() {
        return this.sleeping;
    }

    public void tick() {
        this.noClip = this.isSpectator();
        if (this.isSpectator()) {
            this.onGround = false;
        }

        if (this.xpCooldown > 0) {
            --this.xpCooldown;
        }

        if (this.isPlayerSleeping()) {
            ++this.sleepTimer;
            if (this.sleepTimer > 100) {
                this.sleepTimer = 100;
            }

            if (!this.world.isRemote) {
                if (!this.isInBed()) {
                    this.wakeUpPlayer(true, true, false);
                }
            }
        } else if (this.sleepTimer > 0) {
            ++this.sleepTimer;
            if (this.sleepTimer >= 110) {
                this.sleepTimer = 0;
            }
        }

        this.updateInBubbleColumn();
        this.updateEyesInWaterPlayer();
        super.tick();
        if (!this.world.isRemote && this.openContainer != null && !this.openContainer.canInteractWith(this)) {
            this.closeScreen();
            this.openContainer = this.inventoryContainer;
        }

        if (this.isBurning() && this.capabilities.disableDamage) {
            this.extinguish();
        }

        this.updateCape();
        if (!this.world.isRemote) {

            this.foodStats.tick(this);
            this.addStat(StatList.PLAY_ONE_MINUTE);
            if (this.isEntityAlive()) {
                this.addStat(StatList.TIME_SINCE_DEATH);
            }

            if (this.isSneaking()) {
                this.addStat(StatList.SNEAK_TIME);
            }

            if (!this.isPlayerSleeping()) {
                this.addStat(StatList.TIME_SINCE_REST);
            }
        }

        double d0 = MathHelper.clamp(this.posX, -2.9999999E7D, 2.9999999E7D);
        double d1 = MathHelper.clamp(this.posZ, -2.9999999E7D, 2.9999999E7D);
        if (d0 != this.posX || d1 != this.posZ) {
            this.setPosition(d0, this.posY, d1);
        }

        ++this.ticksSinceLastSwing;
        ItemStack itemstack = this.getHeldItemMainhand();
        if (!ItemStack.areItemStacksEqual(this.itemStackMainHand, itemstack)) {
            if (!ItemStack.areItemsEqualIgnoreDurability(this.itemStackMainHand, itemstack)) {
                this.resetCooldown();
            }

            this.itemStackMainHand = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
        }

        this.updateTurtleHelmet();
        this.cooldownTracker.tick();
        this.updateSize();
    }

    public void livingTick() {
        if (this.flyToggleTimer > 0) {
            --this.flyToggleTimer;
        }

        if (this.world.getDifficulty() == EnumDifficulty.PEACEFUL && this.world.getGameRules().getBoolean(
                "naturalRegeneration")) {
            if (this.getHealth() < this.getMaxHealth() && this.ticksExisted % 20 == 0) {
                this.heal(1.0F);
            }

            if (this.foodStats.needFood() && this.ticksExisted % 10 == 0) {
                this.foodStats.setFoodLevel(this.foodStats.getFoodLevel() + 1);
            }
        }

        this.inventory.tick();
        this.prevCameraYaw = this.cameraYaw;
        super.livingTick();
        IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        if (!this.world.isRemote) {
            iattributeinstance.setBaseValue((double) this.capabilities.getWalkSpeed());
        }

        this.jumpMovementFactor = this.speedInAir;
        if (this.isSprinting()) {
            this.jumpMovementFactor = (float) ((double) this.jumpMovementFactor + (double) this.speedInAir * 0.3D);
        }

        this.setAIMoveSpeed((float) iattributeinstance.getAttributeValue());
        float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        float f1 = (float) (Math.atan(-this.motionY * (double) 0.2F) * 15.0D);
        if (f > 0.1F) {
            f = 0.1F;
        }

        if (!this.onGround || this.getHealth() <= 0.0F || this.isSwimming()) {
            f = 0.0F;
        }

        if (this.onGround || this.getHealth() <= 0.0F) {
            f1 = 0.0F;
        }

        this.cameraYaw += (f - this.cameraYaw) * 0.4F;
        this.cameraPitch += (f1 - this.cameraPitch) * 0.8F;
        if (this.getHealth() > 0.0F && !this.isSpectator()) {
            AxisAlignedBB axisalignedbb;
            if (this.isRiding() && !this.getRidingEntity().isDead) {
                axisalignedbb = this.getEntityBoundingBox().union(this.getRidingEntity().getEntityBoundingBox()).grow(
                        1.0D,
                        0.0D,
                        1.0D);
            } else {
                axisalignedbb = this.getEntityBoundingBox().grow(1.0D, 0.5D, 1.0D);
            }

            List<Entity> list = this.world.func_72839_b(this, axisalignedbb);

            for (int i = 0; i < list.size(); ++i) {
                Entity entity = list.get(i);
                if (!entity.isDead) {
                    this.collideWithPlayer(entity);
                }
            }
        }

        this.playShoulderEntityAmbientSound(this.getLeftShoulderEntity());
        this.playShoulderEntityAmbientSound(this.getRightShoulderEntity());
        if (!this.world.isRemote && (this.fallDistance > 0.5F || this.isInWater() || this.isRiding()) ||
            this.capabilities.isFlying) {
            this.spawnShoulderEntities();
        }

    }

    protected void updateEntityActionState() {
        super.updateEntityActionState();
        this.updateArmSwingProgress();
        this.rotationYawHead = this.rotationYaw;
    }

    protected void spinAttack(EntityLivingBase p_204804_1_) {
        this.attackTargetEntityWithCurrentItem(p_204804_1_);
    }

    public void dismountRidingEntity() {
        super.dismountRidingEntity();
        this.rideCooldown = 0;
    }

    public void updateRidden() {
        if (!this.world.isRemote && this.isSneaking() && this.isRiding()) {
            this.dismountRidingEntity();
            this.setSneaking(false);
        } else {
            double d0 = this.posX;
            double d1 = this.posY;
            double d2 = this.posZ;
            float f = this.rotationYaw;
            float f1 = this.rotationPitch;
            super.updateRidden();
            this.prevCameraYaw = this.cameraYaw;
            this.cameraYaw = 0.0F;
            this.addMountedMovementStat(this.posX - d0, this.posY - d1, this.posZ - d2);
            if (this.getRidingEntity() instanceof EntityPig) {
                this.rotationPitch = f1;
                this.rotationYaw = f;
                this.renderYawOffset = ((EntityPig) this.getRidingEntity()).renderYawOffset;
            }

        }
    }

    public float getAbsorptionAmount() {
        return this.getDataManager().get(ABSORPTION);
    }

    public void setAbsorptionAmount(float p_110149_1_) {
        if (p_110149_1_ < 0.0F) {
            p_110149_1_ = 0.0F;
        }

        this.getDataManager().set(ABSORPTION, p_110149_1_);
    }

    public EnumHandSide getPrimaryHand() {
        return this.dataManager.get(MAIN_HAND) == 0 ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
    }

    public void setPrimaryHand(EnumHandSide p_184819_1_) {
        this.dataManager.set(MAIN_HAND, (byte) (p_184819_1_ == EnumHandSide.LEFT ? 0 : 1));
    }

    public void resetCooldown() {
        this.ticksSinceLastSwing = 0;
    }

    public int resetRecipes(Collection<IRecipe> p_195069_1_) {
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public void respawnPlayer() {
    }

    public void sendPlayerAbilities() {
    }

    public void sendStatusMessage(ITextComponent p_146105_1_, boolean p_146105_2_) {
    }

    public void setGameType(GameType p_71033_1_) {
    }

    public void setMaxHealth(double health) {
        this.getAttributeMap().replaceAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
    }

    @OnlyIn(Dist.CLIENT)
    public void setReducedDebug(boolean p_175150_1_) {
        this.hasReducedDebug = p_175150_1_;
    }

    private void setRenderOffsetForSleep(EnumFacing p_175139_1_) {
        this.renderOffsetX = -1.8F * (float) p_175139_1_.getXOffset();
        this.renderOffsetZ = -1.8F * (float) p_175139_1_.getZOffset();
    }

    public void setSpawnPoint(BlockPos p_180473_1_, boolean p_180473_2_) {
        if (p_180473_1_ != null) {
            this.spawnPos = p_180473_1_;
            this.spawnForced = p_180473_2_;
        } else {
            this.spawnPos = null;
            this.spawnForced = false;
        }

    }

    public boolean shouldHeal() {
        return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
    }

    protected void spawnShoulderEntities() {
        this.spawnShoulderEntity(this.getLeftShoulderEntity());
        this.setLeftShoulderEntity(new NBTTagCompound());
        this.spawnShoulderEntity(this.getRightShoulderEntity());
        this.setRightShoulderEntity(new NBTTagCompound());
    }

    private void spawnShoulderEntity(@Nullable NBTTagCompound p_192026_1_) {
        if (!this.world.isRemote && !p_192026_1_.isEmpty()) {
            Entity entity = EntityType.create(p_192026_1_, this.world);
            if (entity instanceof EntityTameable) {
                ((EntityTameable) entity).setOwnerId(this.entityUniqueID);
            }

            entity.setPosition(this.posX, this.posY + (double) 0.7F, this.posZ);
            this.world.spawnEntity(entity);
        }

    }

    public void spawnSweepParticles() {
        double d0 = (double) (-MathHelper.sin(this.rotationYaw * ((float) Math.PI / 180F)));
        double d1 = (double) MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F));
        if (this.world instanceof WorldServer) {
            ((WorldServer) this.world).spawnParticle(Particles.SWEEP_ATTACK,
                                                     this.posX + d0,
                                                     this.posY + (double) this.height * 0.5D,
                                                     this.posZ + d1,
                                                     0,
                                                     d0,
                                                     0.0D,
                                                     d1,
                                                     0.0D);
        }

    }

    public EntityPlayer.SleepResult trySleep(BlockPos bedPos) {
        EnumFacing enumfacing = this.world.getBlockState(bedPos).get(BlockHorizontal.HORIZONTAL_FACING);
        if (!this.world.isRemote) {
            if (this.isPlayerSleeping() || !this.isEntityAlive()) {
                return EntityPlayer.SleepResult.OTHER_PROBLEM;
            }

            if (!this.world.dimension.isSurfaceWorld()) {
                return EntityPlayer.SleepResult.NOT_POSSIBLE_HERE;
            }

            if (!this.bedInRange(bedPos, enumfacing)) {
                return EntityPlayer.SleepResult.TOO_FAR_AWAY;
            }

            if (!this.isCreative()) {
                List<EntityMob> list = this.world.getEntitiesWithinAABB(EntityMob.class,
                                                                        new AxisAlignedBB(
                                                                                (double) bedPos.getX() - 16.0D,
                                                                                (double) bedPos.getY() - 5.0D,
                                                                                (double) bedPos.getZ() - 16.0D,
                                                                                (double) bedPos.getX() + 16.0D,
                                                                                (double) bedPos.getY() + 5.0D,
                                                                                (double) bedPos.getZ() + 16.0D),
                                                                        new EntityPlayer.SleepEnemyPredicate(this));
                if (!list.isEmpty()|this.world.canSeeSky(new BlockPos(posX,posY,posZ))) {
                    return EntityPlayer.SleepResult.NOT_SAFE;
                }
            }
        }

        if (this.isRiding()) {
            this.dismountRidingEntity();
        }

        this.spawnShoulderEntities();
        this.func_175145_a(StatList.CUSTOM.func_199076_b(StatList.TIME_SINCE_REST));
        this.setSize(0.2F, 0.2F);
        if (this.world.isBlockLoaded(bedPos)) {
            float f1 = 0.5F + (float) enumfacing.getXOffset() * 0.4F;
            float f = 0.5F + (float) enumfacing.getZOffset() * 0.4F;
            this.setRenderOffsetForSleep(enumfacing);
            this.setPosition((double) ((float) bedPos.getX() + f1),
                             (double) ((float) bedPos.getY() + 0.6875F),
                             (double) ((float) bedPos.getZ() + f));
        } else {
            this.setPosition((double) ((float) bedPos.getX() + 0.5F),
                             (double) ((float) bedPos.getY() + 0.6875F),
                             (double) ((float) bedPos.getZ() + 0.5F));
        }

        this.sleeping = true;
        this.sleepTimer = 0;
        this.bedLocation = bedPos;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        if (!this.world.isRemote) {
            this.world.updateAllPlayersSleepingFlag();
        }

        return EntityPlayer.SleepResult.OK;
    }

    public int unlockRecipes(Collection<IRecipe> p_195065_1_) {
        return 0;
    }

    public void unlockRecipes(ResourceLocation[] p_193102_1_) {
    }

    private void updateCape() {
        this.prevChasingPosX = this.chasingPosX;
        this.prevChasingPosY = this.chasingPosY;
        this.prevChasingPosZ = this.chasingPosZ;
        double d0 = this.posX - this.chasingPosX;
        double d1 = this.posY - this.chasingPosY;
        double d2 = this.posZ - this.chasingPosZ;
        double d3 = 10.0D;
        if (d0 > 10.0D) {
            this.chasingPosX = this.posX;
            this.prevChasingPosX = this.chasingPosX;
        }

        if (d2 > 10.0D) {
            this.chasingPosZ = this.posZ;
            this.prevChasingPosZ = this.chasingPosZ;
        }

        if (d1 > 10.0D) {
            this.chasingPosY = this.posY;
            this.prevChasingPosY = this.chasingPosY;
        }

        if (d0 < -10.0D) {
            this.chasingPosX = this.posX;
            this.prevChasingPosX = this.chasingPosX;
        }

        if (d2 < -10.0D) {
            this.chasingPosZ = this.posZ;
            this.prevChasingPosZ = this.chasingPosZ;
        }

        if (d1 < -10.0D) {
            this.chasingPosY = this.posY;
            this.prevChasingPosY = this.chasingPosY;
        }

        this.chasingPosX += d0 * 0.25D;
        this.chasingPosZ += d2 * 0.25D;
        this.chasingPosY += d1 * 0.25D;
    }

    protected boolean updateEyesInWaterPlayer() {
        this.eyesInWaterPlayer = this.areEyesInFluid(FluidTags.WATER);
        return this.eyesInWaterPlayer;
    }

    private void updateInBubbleColumn() {
        IBlockState iblockstate = this.world.findBlockstateInArea(this.getEntityBoundingBox()
                                                                      .grow(0.0D, (double) -0.4F, 0.0D).shrink(0.001D),
                                                                  Blocks.BUBBLE_COLUMN);
        if (iblockstate != null) {
            if (!this.inBubbleColumn && !this.firstUpdate && iblockstate.getBlock() == Blocks.BUBBLE_COLUMN &&
                !this.isSpectator()) {
                boolean flag = iblockstate.get(BlockBubbleColumn.DRAG);
                if (flag) {
                    this.world.playSound(this.posX,
                                         this.posY,
                                         this.posZ,
                                         SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE,
                                         this.getSoundCategory(),
                                         1.0F,
                                         1.0F,
                                         false);
                } else {
                    this.world.playSound(this.posX,
                                         this.posY,
                                         this.posZ,
                                         SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE,
                                         this.getSoundCategory(),
                                         1.0F,
                                         1.0F,
                                         false);
                }
            }

            this.inBubbleColumn = true;
        } else {
            this.inBubbleColumn = false;
        }

    }

    protected void updateSize() {
        float f;
        float f1;
        if (this.isElytraFlying()) {
            f = 0.6F;
            f1 = 0.6F;
        } else if (this.isPlayerSleeping()) {
            f = 0.2F;
            f1 = 0.2F;
        } else if (!this.isSwimming() && !this.isSpinAttacking()) {
            if (this.isSneaking()) {
                f = 0.6F;
                f1 = 1.65F;
            } else {
                f = 0.6F;
                f1 = 1.8F;
            }
        } else {
            f = 0.6F;
            f1 = 0.6F;
        }

        if (f != this.width || f1 != this.height) {
            AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
            axisalignedbb = new AxisAlignedBB(axisalignedbb.minX,
                                              axisalignedbb.minY,
                                              axisalignedbb.minZ,
                                              axisalignedbb.minX + (double) f,
                                              axisalignedbb.minY + (double) f1,
                                              axisalignedbb.minZ + (double) f);
            if (this.world.isCollisionBoxesEmpty(null, axisalignedbb)) {
                this.setSize(f, f1);
            }
        }

    }

    private void updateTurtleHelmet() {
        ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (itemstack.getItem() == Items.TURTLE_HELMET && !this.areEyesInFluid(FluidTags.WATER)) {
            this.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 200, 0, false, false, true));
        }

    }

    public void wakeUpPlayer(boolean p_70999_1_, boolean p_70999_2_, boolean p_70999_3_) {
        this.setSize(0.6F, 1.8F);
        IBlockState iblockstate = this.world.getBlockState(this.bedLocation);
        if (this.bedLocation != null && iblockstate.getBlock() instanceof BlockBed) {
            this.world.setBlockState(this.bedLocation, iblockstate.with(BlockBed.OCCUPIED, Boolean.valueOf(false)), 4);
            BlockPos blockpos = BlockBed.getSafeExitLocation(this.world, this.bedLocation, 0);
            if (blockpos == null) {
                blockpos = this.bedLocation.up();
            }

            this.setPosition((double) ((float) blockpos.getX() + 0.5F),
                             (double) ((float) blockpos.getY() + 0.1F),
                             (double) ((float) blockpos.getZ() + 0.5F));
        }

        this.naturalHealSpeed = 1280;
        this.sleeping = false;
        if (!this.world.isRemote && p_70999_2_) {
            this.world.updateAllPlayersSleepingFlag();
        }

        this.sleepTimer = p_70999_1_ ? 0 : 100;
        if (p_70999_3_) {
            this.setSpawnPoint(this.bedLocation, false);
        }

    }

    public int xpBarCap() {
        //MITEMODDED harder to upgrade
        return this.experienceLevel==-1 ? 10 : 10 * this.experienceLevel + 10;
    }

    public enum EnumChatVisibility {
        FULL(0, "options.chat.visibility.full"), SYSTEM(1, "options.chat.visibility.system"), HIDDEN(2,
                                                                                                     "options.chat.visibility.hidden");

        private static final EntityPlayer.EnumChatVisibility[] ID_LOOKUP = Arrays.stream(values()).sorted(Comparator
                                                                                                                  .comparingInt(
                                                                                                                          EntityPlayer.EnumChatVisibility::getChatVisibility))
                                                                                 .toArray((p_199765_0_) -> {
                                                                                     return new EntityPlayer.EnumChatVisibility[p_199765_0_];
                                                                                 });
        private final int chatVisibility;
        private final String resourceKey;

        EnumChatVisibility(int p_i45323_3_, String p_i45323_4_) {
            this.chatVisibility = p_i45323_3_;
            this.resourceKey = p_i45323_4_;
        }

        @OnlyIn(Dist.CLIENT)
        public static EntityPlayer.EnumChatVisibility getEnumChatVisibility(int p_151426_0_) {
            return ID_LOOKUP[p_151426_0_ % ID_LOOKUP.length];
        }

        public int getChatVisibility() {
            return this.chatVisibility;
        }

        @OnlyIn(Dist.CLIENT)
        public String getResourceKey() {
            return this.resourceKey;
        }
    }

    public enum SleepResult {
        OK, NOT_POSSIBLE_HERE, NOT_POSSIBLE_NOW, TOO_FAR_AWAY, OTHER_PROBLEM, NOT_SAFE
    }

    static class SleepEnemyPredicate implements Predicate<EntityMob> {
        private final EntityPlayer player;

        private SleepEnemyPredicate(EntityPlayer p_i47461_1_) {
            this.player = p_i47461_1_;
        }

        public boolean test(@Nullable EntityMob p_test_1_) {
            return p_test_1_.isPreventingPlayerRest(this.player);
        }
    }
}
