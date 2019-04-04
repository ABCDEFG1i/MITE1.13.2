package net.minecraft.entity.item;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.INameable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class EntityMinecart extends Entity implements INameable {
   private static final DataParameter<Integer> ROLLING_AMPLITUDE = EntityDataManager.createKey(EntityMinecart.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> ROLLING_DIRECTION = EntityDataManager.createKey(EntityMinecart.class, DataSerializers.VARINT);
   private static final DataParameter<Float> DAMAGE = EntityDataManager.createKey(EntityMinecart.class, DataSerializers.FLOAT);
   private static final DataParameter<Integer> DISPLAY_TILE = EntityDataManager.createKey(EntityMinecart.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> DISPLAY_TILE_OFFSET = EntityDataManager.createKey(EntityMinecart.class, DataSerializers.VARINT);
   private static final DataParameter<Boolean> SHOW_BLOCK = EntityDataManager.createKey(EntityMinecart.class, DataSerializers.BOOLEAN);
   private boolean isInReverse;
   private static final int[][][] MATRIX = new int[][][]{{{0, 0, -1}, {0, 0, 1}}, {{-1, 0, 0}, {1, 0, 0}}, {{-1, -1, 0}, {1, 0, 0}}, {{-1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}}, {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, {-1, 0, 0}}, {{0, 0, -1}, {-1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};
   private int turnProgress;
   private double minecartX;
   private double minecartY;
   private double minecartZ;
   private double minecartYaw;
   private double minecartPitch;
   @OnlyIn(Dist.CLIENT)
   private double velocityX;
   @OnlyIn(Dist.CLIENT)
   private double velocityY;
   @OnlyIn(Dist.CLIENT)
   private double velocityZ;

   protected EntityMinecart(EntityType<?> p_i48538_1_, World p_i48538_2_) {
      super(p_i48538_1_, p_i48538_2_);
      this.preventEntitySpawning = true;
      this.setSize(0.98F, 0.7F);
   }

   protected EntityMinecart(EntityType<?> p_i48539_1_, World p_i48539_2_, double p_i48539_3_, double p_i48539_5_, double p_i48539_7_) {
      this(p_i48539_1_, p_i48539_2_);
      this.setPosition(p_i48539_3_, p_i48539_5_, p_i48539_7_);
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
      this.prevPosX = p_i48539_3_;
      this.prevPosY = p_i48539_5_;
      this.prevPosZ = p_i48539_7_;
   }

   public static EntityMinecart create(World p_184263_0_, double p_184263_1_, double p_184263_3_, double p_184263_5_, EntityMinecart.Type p_184263_7_) {
      switch(p_184263_7_) {
      case CHEST:
         return new EntityMinecartChest(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_);
      case FURNACE:
         return new EntityMinecartFurnace(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_);
      case TNT:
         return new EntityMinecartTNT(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_);
      case SPAWNER:
         return new EntityMinecartMobSpawner(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_);
      case HOPPER:
         return new EntityMinecartHopper(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_);
      case COMMAND_BLOCK:
         return new EntityMinecartCommandBlock(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_);
      default:
         return new EntityMinecartEmpty(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_);
      }
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected void registerData() {
      this.dataManager.register(ROLLING_AMPLITUDE, 0);
      this.dataManager.register(ROLLING_DIRECTION, 1);
      this.dataManager.register(DAMAGE, 0.0F);
      this.dataManager.register(DISPLAY_TILE, Block.getStateId(Blocks.AIR.getDefaultState()));
      this.dataManager.register(DISPLAY_TILE_OFFSET, 6);
      this.dataManager.register(SHOW_BLOCK, false);
   }

   @Nullable
   public AxisAlignedBB getCollisionBox(Entity p_70114_1_) {
      return p_70114_1_.canBePushed() ? p_70114_1_.getEntityBoundingBox() : null;
   }

   public boolean canBePushed() {
      return true;
   }

   public double getMountedYOffset() {
      return 0.0D;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (!this.world.isRemote && !this.isDead) {
         if (this.isInvulnerableTo(p_70097_1_)) {
            return false;
         } else {
            this.setRollingDirection(-this.getRollingDirection());
            this.setRollingAmplitude(10);
            this.markVelocityChanged();
            this.setDamage(this.getDamage() + p_70097_2_ * 10.0F);
            boolean flag = p_70097_1_.getTrueSource() instanceof EntityPlayer && ((EntityPlayer)p_70097_1_.getTrueSource()).capabilities.isCreativeMode;
            if (flag || this.getDamage() > 40.0F) {
               this.removePassengers();
               if (flag && !this.hasCustomName()) {
                  this.setDead();
               } else {
                  this.killMinecart(p_70097_1_);
               }
            }

            return true;
         }
      } else {
         return true;
      }
   }

   public void killMinecart(DamageSource p_94095_1_) {
      this.setDead();
      if (this.world.getGameRules().getBoolean("doEntityDrops")) {
         ItemStack itemstack = new ItemStack(Items.MINECART);
         if (this.hasCustomName()) {
            itemstack.setDisplayName(this.getCustomName());
         }

         this.entityDropItem(itemstack);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void performHurtAnimation() {
      this.setRollingDirection(-this.getRollingDirection());
      this.setRollingAmplitude(10);
      this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
   }

   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   public EnumFacing getAdjustedHorizontalFacing() {
      return this.isInReverse ? this.getHorizontalFacing().getOpposite().rotateY() : this.getHorizontalFacing().rotateY();
   }

   public void tick() {
      if (this.getRollingAmplitude() > 0) {
         this.setRollingAmplitude(this.getRollingAmplitude() - 1);
      }

      if (this.getDamage() > 0.0F) {
         this.setDamage(this.getDamage() - 1.0F);
      }

      if (this.posY < -64.0D) {
         this.outOfWorld();
      }

      if (!this.world.isRemote && this.world instanceof WorldServer) {
         this.world.profiler.startSection("portal");
         MinecraftServer minecraftserver = this.world.getServer();
         int i = this.getMaxInPortalTime();
         if (this.inPortal) {
            if (minecraftserver.getAllowNether()) {
               if (!this.isRiding() && this.portalCounter++ >= i) {
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

         if (this.timeUntilPortal > 0) {
            --this.timeUntilPortal;
         }

         this.world.profiler.endSection();
      }

      if (this.world.isRemote) {
         if (this.turnProgress > 0) {
            double d4 = this.posX + (this.minecartX - this.posX) / (double)this.turnProgress;
            double d5 = this.posY + (this.minecartY - this.posY) / (double)this.turnProgress;
            double d6 = this.posZ + (this.minecartZ - this.posZ) / (double)this.turnProgress;
            double d1 = MathHelper.wrapDegrees(this.minecartYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d1 / (double)this.turnProgress);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.minecartPitch - (double)this.rotationPitch) / (double)this.turnProgress);
            --this.turnProgress;
            this.setPosition(d4, d5, d6);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         } else {
            this.setPosition(this.posX, this.posY, this.posZ);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         }

      } else {
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         if (!this.hasNoGravity()) {
            this.motionY -= (double)0.04F;
         }

         int j = MathHelper.floor(this.posX);
         int k = MathHelper.floor(this.posY);
         int l = MathHelper.floor(this.posZ);
         if (this.world.getBlockState(new BlockPos(j, k - 1, l)).isIn(BlockTags.RAILS)) {
            --k;
         }

         BlockPos blockpos = new BlockPos(j, k, l);
         IBlockState iblockstate = this.world.getBlockState(blockpos);
         if (iblockstate.isIn(BlockTags.RAILS)) {
            this.moveAlongTrack(blockpos, iblockstate);
            if (iblockstate.getBlock() == Blocks.ACTIVATOR_RAIL) {
               this.onActivatorRailPass(j, k, l, iblockstate.get(BlockRailPowered.POWERED));
            }
         } else {
            this.moveDerailedMinecart();
         }

         this.doBlockCollisions();
         this.rotationPitch = 0.0F;
         double d0 = this.prevPosX - this.posX;
         double d2 = this.prevPosZ - this.posZ;
         if (d0 * d0 + d2 * d2 > 0.001D) {
            this.rotationYaw = (float)(MathHelper.atan2(d2, d0) * 180.0D / Math.PI);
            if (this.isInReverse) {
               this.rotationYaw += 180.0F;
            }
         }

         double d3 = (double)MathHelper.wrapDegrees(this.rotationYaw - this.prevRotationYaw);
         if (d3 < -170.0D || d3 >= 170.0D) {
            this.rotationYaw += 180.0F;
            this.isInReverse = !this.isInReverse;
         }

         this.setRotation(this.rotationYaw, this.rotationPitch);
         if (this.getMinecartType() == EntityMinecart.Type.RIDEABLE && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01D) {
            List<Entity> list = this.world.func_175674_a(this, this.getEntityBoundingBox().grow((double)0.2F, 0.0D, (double)0.2F), EntitySelectors.func_200823_a(this));
            if (!list.isEmpty()) {
               for(int i1 = 0; i1 < list.size(); ++i1) {
                  Entity entity1 = list.get(i1);
                  if (!(entity1 instanceof EntityPlayer) && !(entity1 instanceof EntityIronGolem) && !(entity1 instanceof EntityMinecart) && !this.isBeingRidden() && !entity1.isRiding()) {
                     entity1.startRiding(this);
                  } else {
                     entity1.applyEntityCollision(this);
                  }
               }
            }
         } else {
            for(Entity entity : this.world.func_72839_b(this, this.getEntityBoundingBox().grow((double)0.2F, 0.0D, (double)0.2F))) {
               if (!this.isPassenger(entity) && entity.canBePushed() && entity instanceof EntityMinecart) {
                  entity.applyEntityCollision(this);
               }
            }
         }

         this.handleWaterMovement();
      }
   }

   protected double getMaximumSpeed() {
      return 0.4D;
   }

   public void onActivatorRailPass(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_) {
   }

   protected void moveDerailedMinecart() {
      double d0 = this.getMaximumSpeed();
      this.motionX = MathHelper.clamp(this.motionX, -d0, d0);
      this.motionZ = MathHelper.clamp(this.motionZ, -d0, d0);
      if (this.onGround) {
         this.motionX *= 0.5D;
         this.motionY *= 0.5D;
         this.motionZ *= 0.5D;
      }

      this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
      if (!this.onGround) {
         this.motionX *= (double)0.95F;
         this.motionY *= (double)0.95F;
         this.motionZ *= (double)0.95F;
      }

   }

   protected void moveAlongTrack(BlockPos p_180460_1_, IBlockState p_180460_2_) {
      this.fallDistance = 0.0F;
      Vec3d vec3d = this.getPos(this.posX, this.posY, this.posZ);
      this.posY = (double)p_180460_1_.getY();
      boolean flag = false;
      boolean flag1 = false;
      BlockRailBase blockrailbase = (BlockRailBase)p_180460_2_.getBlock();
      if (blockrailbase == Blocks.POWERED_RAIL) {
         flag = p_180460_2_.get(BlockRailPowered.POWERED);
         flag1 = !flag;
      }

      double d0 = 0.0078125D;
      RailShape railshape = p_180460_2_.get(blockrailbase.getShapeProperty());
      switch(railshape) {
      case ASCENDING_EAST:
         this.motionX -= 0.0078125D;
         ++this.posY;
         break;
      case ASCENDING_WEST:
         this.motionX += 0.0078125D;
         ++this.posY;
         break;
      case ASCENDING_NORTH:
         this.motionZ += 0.0078125D;
         ++this.posY;
         break;
      case ASCENDING_SOUTH:
         this.motionZ -= 0.0078125D;
         ++this.posY;
      }

      int[][] aint = MATRIX[railshape.func_208091_a()];
      double d1 = (double)(aint[1][0] - aint[0][0]);
      double d2 = (double)(aint[1][2] - aint[0][2]);
      double d3 = Math.sqrt(d1 * d1 + d2 * d2);
      double d4 = this.motionX * d1 + this.motionZ * d2;
      if (d4 < 0.0D) {
         d1 = -d1;
         d2 = -d2;
      }

      double d5 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      if (d5 > 2.0D) {
         d5 = 2.0D;
      }

      this.motionX = d5 * d1 / d3;
      this.motionZ = d5 * d2 / d3;
      Entity entity = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
      if (entity instanceof EntityPlayer) {
         double d6 = (double)((EntityPlayer)entity).moveForward;
         if (d6 > 0.0D) {
            double d7 = -Math.sin((double)(entity.rotationYaw * ((float)Math.PI / 180F)));
            double d8 = Math.cos((double)(entity.rotationYaw * ((float)Math.PI / 180F)));
            double d9 = this.motionX * this.motionX + this.motionZ * this.motionZ;
            if (d9 < 0.01D) {
               this.motionX += d7 * 0.1D;
               this.motionZ += d8 * 0.1D;
               flag1 = false;
            }
         }
      }

      if (flag1) {
         double d17 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (d17 < 0.03D) {
            this.motionX *= 0.0D;
            this.motionY *= 0.0D;
            this.motionZ *= 0.0D;
         } else {
            this.motionX *= 0.5D;
            this.motionY *= 0.0D;
            this.motionZ *= 0.5D;
         }
      }

      double d18 = (double)p_180460_1_.getX() + 0.5D + (double)aint[0][0] * 0.5D;
      double d19 = (double)p_180460_1_.getZ() + 0.5D + (double)aint[0][2] * 0.5D;
      double d20 = (double)p_180460_1_.getX() + 0.5D + (double)aint[1][0] * 0.5D;
      double d21 = (double)p_180460_1_.getZ() + 0.5D + (double)aint[1][2] * 0.5D;
      d1 = d20 - d18;
      d2 = d21 - d19;
      double d10;
      if (d1 == 0.0D) {
         this.posX = (double)p_180460_1_.getX() + 0.5D;
         d10 = this.posZ - (double)p_180460_1_.getZ();
      } else if (d2 == 0.0D) {
         this.posZ = (double)p_180460_1_.getZ() + 0.5D;
         d10 = this.posX - (double)p_180460_1_.getX();
      } else {
         double d11 = this.posX - d18;
         double d12 = this.posZ - d19;
         d10 = (d11 * d1 + d12 * d2) * 2.0D;
      }

      this.posX = d18 + d1 * d10;
      this.posZ = d19 + d2 * d10;
      this.setPosition(this.posX, this.posY, this.posZ);
      double d22 = this.motionX;
      double d23 = this.motionZ;
      if (this.isBeingRidden()) {
         d22 *= 0.75D;
         d23 *= 0.75D;
      }

      double d13 = this.getMaximumSpeed();
      d22 = MathHelper.clamp(d22, -d13, d13);
      d23 = MathHelper.clamp(d23, -d13, d13);
      this.move(MoverType.SELF, d22, 0.0D, d23);
      if (aint[0][1] != 0 && MathHelper.floor(this.posX) - p_180460_1_.getX() == aint[0][0] && MathHelper.floor(this.posZ) - p_180460_1_.getZ() == aint[0][2]) {
         this.setPosition(this.posX, this.posY + (double)aint[0][1], this.posZ);
      } else if (aint[1][1] != 0 && MathHelper.floor(this.posX) - p_180460_1_.getX() == aint[1][0] && MathHelper.floor(this.posZ) - p_180460_1_.getZ() == aint[1][2]) {
         this.setPosition(this.posX, this.posY + (double)aint[1][1], this.posZ);
      }

      this.applyDrag();
      Vec3d vec3d1 = this.getPos(this.posX, this.posY, this.posZ);
      if (vec3d1 != null && vec3d != null) {
         double d14 = (vec3d.y - vec3d1.y) * 0.05D;
         d5 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (d5 > 0.0D) {
            this.motionX = this.motionX / d5 * (d5 + d14);
            this.motionZ = this.motionZ / d5 * (d5 + d14);
         }

         this.setPosition(this.posX, vec3d1.y, this.posZ);
      }

      int j = MathHelper.floor(this.posX);
      int i = MathHelper.floor(this.posZ);
      if (j != p_180460_1_.getX() || i != p_180460_1_.getZ()) {
         d5 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.motionX = d5 * (double)(j - p_180460_1_.getX());
         this.motionZ = d5 * (double)(i - p_180460_1_.getZ());
      }

      if (flag) {
         double d15 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (d15 > 0.01D) {
            double d16 = 0.06D;
            this.motionX += this.motionX / d15 * 0.06D;
            this.motionZ += this.motionZ / d15 * 0.06D;
         } else if (railshape == RailShape.EAST_WEST) {
            if (this.world.getBlockState(p_180460_1_.west()).isNormalCube()) {
               this.motionX = 0.02D;
            } else if (this.world.getBlockState(p_180460_1_.east()).isNormalCube()) {
               this.motionX = -0.02D;
            }
         } else if (railshape == RailShape.NORTH_SOUTH) {
            if (this.world.getBlockState(p_180460_1_.north()).isNormalCube()) {
               this.motionZ = 0.02D;
            } else if (this.world.getBlockState(p_180460_1_.south()).isNormalCube()) {
               this.motionZ = -0.02D;
            }
         }
      }

   }

   protected void applyDrag() {
      if (this.isBeingRidden()) {
         this.motionX *= (double)0.997F;
         this.motionY *= 0.0D;
         this.motionZ *= (double)0.997F;
      } else {
         this.motionX *= (double)0.96F;
         this.motionY *= 0.0D;
         this.motionZ *= (double)0.96F;
      }

   }

   public void setPosition(double p_70107_1_, double p_70107_3_, double p_70107_5_) {
      this.posX = p_70107_1_;
      this.posY = p_70107_3_;
      this.posZ = p_70107_5_;
      float f = this.width / 2.0F;
      float f1 = this.height;
      this.setEntityBoundingBox(new AxisAlignedBB(p_70107_1_ - (double)f, p_70107_3_, p_70107_5_ - (double)f, p_70107_1_ + (double)f, p_70107_3_ + (double)f1, p_70107_5_ + (double)f));
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Vec3d getPosOffset(double p_70495_1_, double p_70495_3_, double p_70495_5_, double p_70495_7_) {
      int i = MathHelper.floor(p_70495_1_);
      int j = MathHelper.floor(p_70495_3_);
      int k = MathHelper.floor(p_70495_5_);
      if (this.world.getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS)) {
         --j;
      }

      IBlockState iblockstate = this.world.getBlockState(new BlockPos(i, j, k));
      if (iblockstate.isIn(BlockTags.RAILS)) {
         RailShape railshape = iblockstate.get(((BlockRailBase)iblockstate.getBlock()).getShapeProperty());
         p_70495_3_ = (double)j;
         if (railshape.isAscending()) {
            p_70495_3_ = (double)(j + 1);
         }

         int[][] aint = MATRIX[railshape.func_208091_a()];
         double d0 = (double)(aint[1][0] - aint[0][0]);
         double d1 = (double)(aint[1][2] - aint[0][2]);
         double d2 = Math.sqrt(d0 * d0 + d1 * d1);
         d0 = d0 / d2;
         d1 = d1 / d2;
         p_70495_1_ = p_70495_1_ + d0 * p_70495_7_;
         p_70495_5_ = p_70495_5_ + d1 * p_70495_7_;
         if (aint[0][1] != 0 && MathHelper.floor(p_70495_1_) - i == aint[0][0] && MathHelper.floor(p_70495_5_) - k == aint[0][2]) {
            p_70495_3_ += (double)aint[0][1];
         } else if (aint[1][1] != 0 && MathHelper.floor(p_70495_1_) - i == aint[1][0] && MathHelper.floor(p_70495_5_) - k == aint[1][2]) {
            p_70495_3_ += (double)aint[1][1];
         }

         return this.getPos(p_70495_1_, p_70495_3_, p_70495_5_);
      } else {
         return null;
      }
   }

   @Nullable
   public Vec3d getPos(double p_70489_1_, double p_70489_3_, double p_70489_5_) {
      int i = MathHelper.floor(p_70489_1_);
      int j = MathHelper.floor(p_70489_3_);
      int k = MathHelper.floor(p_70489_5_);
      if (this.world.getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS)) {
         --j;
      }

      IBlockState iblockstate = this.world.getBlockState(new BlockPos(i, j, k));
      if (iblockstate.isIn(BlockTags.RAILS)) {
         RailShape railshape = iblockstate.get(((BlockRailBase)iblockstate.getBlock()).getShapeProperty());
         int[][] aint = MATRIX[railshape.func_208091_a()];
         double d0 = (double)i + 0.5D + (double)aint[0][0] * 0.5D;
         double d1 = (double)j + 0.0625D + (double)aint[0][1] * 0.5D;
         double d2 = (double)k + 0.5D + (double)aint[0][2] * 0.5D;
         double d3 = (double)i + 0.5D + (double)aint[1][0] * 0.5D;
         double d4 = (double)j + 0.0625D + (double)aint[1][1] * 0.5D;
         double d5 = (double)k + 0.5D + (double)aint[1][2] * 0.5D;
         double d6 = d3 - d0;
         double d7 = (d4 - d1) * 2.0D;
         double d8 = d5 - d2;
         double d9;
         if (d6 == 0.0D) {
            d9 = p_70489_5_ - (double)k;
         } else if (d8 == 0.0D) {
            d9 = p_70489_1_ - (double)i;
         } else {
            double d10 = p_70489_1_ - d0;
            double d11 = p_70489_5_ - d2;
            d9 = (d10 * d6 + d11 * d8) * 2.0D;
         }

         p_70489_1_ = d0 + d6 * d9;
         p_70489_3_ = d1 + d7 * d9;
         p_70489_5_ = d2 + d8 * d9;
         if (d7 < 0.0D) {
            ++p_70489_3_;
         }

         if (d7 > 0.0D) {
            p_70489_3_ += 0.5D;
         }

         return new Vec3d(p_70489_1_, p_70489_3_, p_70489_5_);
      } else {
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
      return this.hasDisplayTile() ? axisalignedbb.grow((double)Math.abs(this.getDisplayTileOffset()) / 16.0D) : axisalignedbb;
   }

   protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      if (p_70037_1_.getBoolean("CustomDisplayTile")) {
         this.setDisplayTile(NBTUtil.readBlockState(p_70037_1_.getCompoundTag("DisplayState")));
         this.setDisplayTileOffset(p_70037_1_.getInteger("DisplayOffset"));
      }

   }

   protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      if (this.hasDisplayTile()) {
         p_70014_1_.setBoolean("CustomDisplayTile", true);
         p_70014_1_.setTag("DisplayState", NBTUtil.writeBlockState(this.getDisplayTile()));
         p_70014_1_.setInteger("DisplayOffset", this.getDisplayTileOffset());
      }

   }

   public void applyEntityCollision(Entity p_70108_1_) {
      if (!this.world.isRemote) {
         if (!p_70108_1_.noClip && !this.noClip) {
            if (!this.isPassenger(p_70108_1_)) {
               double d0 = p_70108_1_.posX - this.posX;
               double d1 = p_70108_1_.posZ - this.posZ;
               double d2 = d0 * d0 + d1 * d1;
               if (d2 >= (double)1.0E-4F) {
                  d2 = (double)MathHelper.sqrt(d2);
                  d0 = d0 / d2;
                  d1 = d1 / d2;
                  double d3 = 1.0D / d2;
                  if (d3 > 1.0D) {
                     d3 = 1.0D;
                  }

                  d0 = d0 * d3;
                  d1 = d1 * d3;
                  d0 = d0 * (double)0.1F;
                  d1 = d1 * (double)0.1F;
                  d0 = d0 * (double)(1.0F - this.entityCollisionReduction);
                  d1 = d1 * (double)(1.0F - this.entityCollisionReduction);
                  d0 = d0 * 0.5D;
                  d1 = d1 * 0.5D;
                  if (p_70108_1_ instanceof EntityMinecart) {
                     double d4 = p_70108_1_.posX - this.posX;
                     double d5 = p_70108_1_.posZ - this.posZ;
                     Vec3d vec3d = (new Vec3d(d4, 0.0D, d5)).normalize();
                     Vec3d vec3d1 = (new Vec3d((double)MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F)), 0.0D, (double)MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F)))).normalize();
                     double d6 = Math.abs(vec3d.dotProduct(vec3d1));
                     if (d6 < (double)0.8F) {
                        return;
                     }

                     double d7 = p_70108_1_.motionX + this.motionX;
                     double d8 = p_70108_1_.motionZ + this.motionZ;
                     if (((EntityMinecart)p_70108_1_).getMinecartType() == EntityMinecart.Type.FURNACE && this.getMinecartType() != EntityMinecart.Type.FURNACE) {
                        this.motionX *= (double)0.2F;
                        this.motionZ *= (double)0.2F;
                        this.addVelocity(p_70108_1_.motionX - d0, 0.0D, p_70108_1_.motionZ - d1);
                        p_70108_1_.motionX *= (double)0.95F;
                        p_70108_1_.motionZ *= (double)0.95F;
                     } else if (((EntityMinecart)p_70108_1_).getMinecartType() != EntityMinecart.Type.FURNACE && this.getMinecartType() == EntityMinecart.Type.FURNACE) {
                        p_70108_1_.motionX *= (double)0.2F;
                        p_70108_1_.motionZ *= (double)0.2F;
                        p_70108_1_.addVelocity(this.motionX + d0, 0.0D, this.motionZ + d1);
                        this.motionX *= (double)0.95F;
                        this.motionZ *= (double)0.95F;
                     } else {
                        d7 = d7 / 2.0D;
                        d8 = d8 / 2.0D;
                        this.motionX *= (double)0.2F;
                        this.motionZ *= (double)0.2F;
                        this.addVelocity(d7 - d0, 0.0D, d8 - d1);
                        p_70108_1_.motionX *= (double)0.2F;
                        p_70108_1_.motionZ *= (double)0.2F;
                        p_70108_1_.addVelocity(d7 + d0, 0.0D, d8 + d1);
                     }
                  } else {
                     this.addVelocity(-d0, 0.0D, -d1);
                     p_70108_1_.addVelocity(d0 / 4.0D, 0.0D, d1 / 4.0D);
                  }
               }

            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
      this.minecartX = p_180426_1_;
      this.minecartY = p_180426_3_;
      this.minecartZ = p_180426_5_;
      this.minecartYaw = (double)p_180426_7_;
      this.minecartPitch = (double)p_180426_8_;
      this.turnProgress = p_180426_9_ + 2;
      this.motionX = this.velocityX;
      this.motionY = this.velocityY;
      this.motionZ = this.velocityZ;
   }

   @OnlyIn(Dist.CLIENT)
   public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
      this.motionX = p_70016_1_;
      this.motionY = p_70016_3_;
      this.motionZ = p_70016_5_;
      this.velocityX = this.motionX;
      this.velocityY = this.motionY;
      this.velocityZ = this.motionZ;
   }

   public void setDamage(float p_70492_1_) {
      this.dataManager.set(DAMAGE, p_70492_1_);
   }

   public float getDamage() {
      return this.dataManager.get(DAMAGE);
   }

   public void setRollingAmplitude(int p_70497_1_) {
      this.dataManager.set(ROLLING_AMPLITUDE, p_70497_1_);
   }

   public int getRollingAmplitude() {
      return this.dataManager.get(ROLLING_AMPLITUDE);
   }

   public void setRollingDirection(int p_70494_1_) {
      this.dataManager.set(ROLLING_DIRECTION, p_70494_1_);
   }

   public int getRollingDirection() {
      return this.dataManager.get(ROLLING_DIRECTION);
   }

   public abstract EntityMinecart.Type getMinecartType();

   public IBlockState getDisplayTile() {
      return !this.hasDisplayTile() ? this.getDefaultDisplayTile() : Block.getStateById(this.getDataManager().get(DISPLAY_TILE));
   }

   public IBlockState getDefaultDisplayTile() {
      return Blocks.AIR.getDefaultState();
   }

   public int getDisplayTileOffset() {
      return !this.hasDisplayTile() ? this.getDefaultDisplayTileOffset() : this.getDataManager().get(DISPLAY_TILE_OFFSET);
   }

   public int getDefaultDisplayTileOffset() {
      return 6;
   }

   public void setDisplayTile(IBlockState p_174899_1_) {
      this.getDataManager().set(DISPLAY_TILE, Block.getStateId(p_174899_1_));
      this.setHasDisplayTile(true);
   }

   public void setDisplayTileOffset(int p_94086_1_) {
      this.getDataManager().set(DISPLAY_TILE_OFFSET, p_94086_1_);
      this.setHasDisplayTile(true);
   }

   public boolean hasDisplayTile() {
      return this.getDataManager().get(SHOW_BLOCK);
   }

   public void setHasDisplayTile(boolean p_94096_1_) {
      this.getDataManager().set(SHOW_BLOCK, p_94096_1_);
   }

   public enum Type {
      RIDEABLE(0),
      CHEST(1),
      FURNACE(2),
      TNT(3),
      SPAWNER(4),
      HOPPER(5),
      COMMAND_BLOCK(6);

      private static final EntityMinecart.Type[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(EntityMinecart.Type::getId)).toArray((p_199766_0_) -> {
         return new EntityMinecart.Type[p_199766_0_];
      });
      private final int id;

      Type(int p_i48595_3_) {
         this.id = p_i48595_3_;
      }

      public int getId() {
         return this.id;
      }

      @OnlyIn(Dist.CLIENT)
      public static EntityMinecart.Type getById(int p_184955_0_) {
         return p_184955_0_ >= 0 && p_184955_0_ < BY_ID.length ? BY_ID[p_184955_0_] : RIDEABLE;
      }
   }
}
