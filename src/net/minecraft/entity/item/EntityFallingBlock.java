package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockConcretePowder;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityFallingBlock extends Entity {
   private IBlockState fallTile = Blocks.SAND.getDefaultState();
   public int fallTime;
   public boolean shouldDropItem = true;
   private boolean dontSetBlock;
   private boolean hurtEntities;
   private int fallHurtMax = 40;
   private float fallHurtAmount = 2.0F;
   public NBTTagCompound tileEntityData;
   protected static final DataParameter<BlockPos> ORIGIN = EntityDataManager.createKey(EntityFallingBlock.class, DataSerializers.BLOCK_POS);

   public EntityFallingBlock(World p_i1706_1_) {
      super(EntityType.FALLING_BLOCK, p_i1706_1_);
   }

   public EntityFallingBlock(World p_i45848_1_, double p_i45848_2_, double p_i45848_4_, double p_i45848_6_, IBlockState p_i45848_8_) {
      this(p_i45848_1_);
      this.fallTile = p_i45848_8_;
      this.preventEntitySpawning = true;
      this.setSize(0.98F, 0.98F);
      this.setPosition(p_i45848_2_, p_i45848_4_ + (double)((1.0F - this.height) / 2.0F), p_i45848_6_);
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
      this.prevPosX = p_i45848_2_;
      this.prevPosY = p_i45848_4_;
      this.prevPosZ = p_i45848_6_;
      this.setOrigin(new BlockPos(this));
   }

   public boolean canBeAttackedWithItem() {
      return false;
   }

   public void setOrigin(BlockPos p_184530_1_) {
      this.dataManager.set(ORIGIN, p_184530_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getOrigin() {
      return this.dataManager.get(ORIGIN);
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected void registerData() {
      this.dataManager.register(ORIGIN, BlockPos.ORIGIN);
   }

   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   public void tick() {
      if (this.fallTile.isAir()) {
         this.setDead();
      } else {
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         Block block = this.fallTile.getBlock();
         if (this.fallTime++ == 0) {
            BlockPos blockpos = new BlockPos(this);
            if (this.world.getBlockState(blockpos).getBlock() == block) {
               this.world.removeBlock(blockpos);
            } else if (!this.world.isRemote) {
               this.setDead();
               return;
            }
         }

         if (!this.hasNoGravity()) {
            this.motionY -= (double)0.04F;
         }

         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         if (!this.world.isRemote) {
            BlockPos blockpos1 = new BlockPos(this);
            boolean flag = this.fallTile.getBlock() instanceof BlockConcretePowder;
            boolean flag1 = flag && this.world.getFluidState(blockpos1).isTagged(FluidTags.WATER);
            double d0 = this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ;
            if (flag && d0 > 1.0D) {
               RayTraceResult raytraceresult = this.world.rayTraceBlocks(new Vec3d(this.prevPosX, this.prevPosY, this.prevPosZ), new Vec3d(this.posX, this.posY, this.posZ), RayTraceFluidMode.SOURCE_ONLY);
               if (raytraceresult != null && this.world.getFluidState(raytraceresult.getBlockPos()).isTagged(FluidTags.WATER)) {
                  blockpos1 = raytraceresult.getBlockPos();
                  flag1 = true;
               }
            }

            if (!this.onGround && !flag1) {
               if (this.fallTime > 100 && !this.world.isRemote && (blockpos1.getY() < 1 || blockpos1.getY() > 256) || this.fallTime > 600) {
                  if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                     this.entityDropItem(block);
                  }

                  this.setDead();
               }
            } else {
               IBlockState iblockstate = this.world.getBlockState(blockpos1);
               if (!flag1 && BlockFalling.canFallThrough(this.world.getBlockState(new BlockPos(this.posX, this.posY - (double)0.01F, this.posZ)))) {
                  this.onGround = false;
                  return;
               }

               this.motionX *= (double)0.7F;
               this.motionZ *= (double)0.7F;
               this.motionY *= -0.5D;
               if (iblockstate.getBlock() != Blocks.MOVING_PISTON) {
                  this.setDead();
                  if (!this.dontSetBlock) {
                     if (iblockstate.getMaterial().isReplaceable() && (flag1 || !BlockFalling.canFallThrough(this.world.getBlockState(blockpos1.down()))) && this.world.setBlockState(blockpos1, this.fallTile, 3)) {
                        if (block instanceof BlockFalling) {
                           ((BlockFalling)block).onEndFalling(this.world, blockpos1, this.fallTile, iblockstate);
                        }

                        if (this.tileEntityData != null && block instanceof ITileEntityProvider) {
                           TileEntity tileentity = this.world.getTileEntity(blockpos1);
                           if (tileentity != null) {
                              NBTTagCompound nbttagcompound = tileentity.writeToNBT(new NBTTagCompound());

                              for(String s : this.tileEntityData.getKeySet()) {
                                 INBTBase inbtbase = this.tileEntityData.getTag(s);
                                 if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s)) {
                                    nbttagcompound.setTag(s, inbtbase.copy());
                                 }
                              }

                              tileentity.readFromNBT(nbttagcompound);
                              tileentity.markDirty();
                           }
                        }
                     } else if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                        this.entityDropItem(block);
                     }
                  } else if (block instanceof BlockFalling) {
                     ((BlockFalling)block).onBroken(this.world, blockpos1);
                  }
               }
            }
         }

         this.motionX *= (double)0.98F;
         this.motionY *= (double)0.98F;
         this.motionZ *= (double)0.98F;
      }
   }

   public void fall(float p_180430_1_, float p_180430_2_) {
      if (this.hurtEntities) {
         int i = MathHelper.ceil(p_180430_1_ - 1.0F);
         if (i > 0) {
            List<Entity> list = Lists.newArrayList(this.world.func_72839_b(this, this.getEntityBoundingBox()));
            boolean flag = this.fallTile.isIn(BlockTags.ANVIL);
            DamageSource damagesource = flag ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;

            for(Entity entity : list) {
               entity.attackEntityFrom(damagesource, (float)Math.min(MathHelper.floor((float)i * this.fallHurtAmount), this.fallHurtMax));
            }

            if (flag && (double)this.rand.nextFloat() < (double)0.05F + (double)i * 0.05D) {
               IBlockState iblockstate = BlockAnvil.damage(this.fallTile);
               if (iblockstate == null) {
                  this.dontSetBlock = true;
               } else {
                  this.fallTile = iblockstate;
               }
            }
         }
      }

   }

   protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      p_70014_1_.setTag("BlockState", NBTUtil.writeBlockState(this.fallTile));
      p_70014_1_.setInteger("Time", this.fallTime);
      p_70014_1_.setBoolean("DropItem", this.shouldDropItem);
      p_70014_1_.setBoolean("HurtEntities", this.hurtEntities);
      p_70014_1_.setFloat("FallHurtAmount", this.fallHurtAmount);
      p_70014_1_.setInteger("FallHurtMax", this.fallHurtMax);
      if (this.tileEntityData != null) {
         p_70014_1_.setTag("TileEntityData", this.tileEntityData);
      }

   }

   protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      this.fallTile = NBTUtil.readBlockState(p_70037_1_.getCompoundTag("BlockState"));
      this.fallTime = p_70037_1_.getInteger("Time");
      if (p_70037_1_.hasKey("HurtEntities", 99)) {
         this.hurtEntities = p_70037_1_.getBoolean("HurtEntities");
         this.fallHurtAmount = p_70037_1_.getFloat("FallHurtAmount");
         this.fallHurtMax = p_70037_1_.getInteger("FallHurtMax");
      } else if (this.fallTile.isIn(BlockTags.ANVIL)) {
         this.hurtEntities = true;
      }

      if (p_70037_1_.hasKey("DropItem", 99)) {
         this.shouldDropItem = p_70037_1_.getBoolean("DropItem");
      }

      if (p_70037_1_.hasKey("TileEntityData", 10)) {
         this.tileEntityData = p_70037_1_.getCompoundTag("TileEntityData");
      }

      if (this.fallTile.isAir()) {
         this.fallTile = Blocks.SAND.getDefaultState();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public World getWorldObj() {
      return this.world;
   }

   public void setHurtEntities(boolean p_145806_1_) {
      this.hurtEntities = p_145806_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canRenderOnFire() {
      return false;
   }

   public void fillCrashReport(CrashReportCategory p_85029_1_) {
      super.fillCrashReport(p_85029_1_);
      p_85029_1_.addCrashSection("Immitating BlockState", this.fallTile.toString());
   }

   public IBlockState func_195054_l() {
      return this.fallTile;
   }

   public boolean ignoreItemEntityData() {
      return true;
   }
}
