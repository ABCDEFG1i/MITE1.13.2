package net.minecraft.entity.item;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.end.DragonFightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityEnderCrystal extends Entity {
   private static final DataParameter<Optional<BlockPos>> BEAM_TARGET = EntityDataManager.createKey(EntityEnderCrystal.class, DataSerializers.OPTIONAL_BLOCK_POS);
   private static final DataParameter<Boolean> SHOW_BOTTOM = EntityDataManager.createKey(EntityEnderCrystal.class, DataSerializers.BOOLEAN);
   public int innerRotation;

   public EntityEnderCrystal(World p_i1698_1_) {
      super(EntityType.END_CRYSTAL, p_i1698_1_);
      this.preventEntitySpawning = true;
      this.setSize(2.0F, 2.0F);
      this.innerRotation = this.rand.nextInt(100000);
   }

   public EntityEnderCrystal(World p_i1699_1_, double p_i1699_2_, double p_i1699_4_, double p_i1699_6_) {
      this(p_i1699_1_);
      this.setPosition(p_i1699_2_, p_i1699_4_, p_i1699_6_);
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected void registerData() {
      this.getDataManager().register(BEAM_TARGET, Optional.empty());
      this.getDataManager().register(SHOW_BOTTOM, true);
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      ++this.innerRotation;
      if (!this.world.isRemote) {
         BlockPos blockpos = new BlockPos(this);
         if (this.world.dimension instanceof EndDimension && this.world.getBlockState(blockpos).isAir()) {
            this.world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
         }
      }

   }

   protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      if (this.getBeamTarget() != null) {
         p_70014_1_.setTag("BeamTarget", NBTUtil.createPosTag(this.getBeamTarget()));
      }

      p_70014_1_.setBoolean("ShowBottom", this.shouldShowBottom());
   }

   protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      if (p_70037_1_.hasKey("BeamTarget", 10)) {
         this.setBeamTarget(NBTUtil.getPosFromTag(p_70037_1_.getCompoundTag("BeamTarget")));
      }

      if (p_70037_1_.hasKey("ShowBottom", 1)) {
         this.setShowBottom(p_70037_1_.getBoolean("ShowBottom"));
      }

   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else if (p_70097_1_.getTrueSource() instanceof EntityDragon) {
         return false;
      } else {
         if (!this.isDead && !this.world.isRemote) {
            this.setDead();
            if (!this.world.isRemote) {
               if (!p_70097_1_.isExplosion()) {
                  this.world.createExplosion(null, this.posX, this.posY, this.posZ, 6.0F, true);
               }

               this.onCrystalDestroyed(p_70097_1_);
            }
         }

         return true;
      }
   }

   public void onKillCommand() {
      this.onCrystalDestroyed(DamageSource.GENERIC);
      super.onKillCommand();
   }

   private void onCrystalDestroyed(DamageSource p_184519_1_) {
      if (this.world.dimension instanceof EndDimension) {
         EndDimension enddimension = (EndDimension)this.world.dimension;
         DragonFightManager dragonfightmanager = enddimension.getDragonFightManager();
         if (dragonfightmanager != null) {
            dragonfightmanager.onCrystalDestroyed(this, p_184519_1_);
         }
      }

   }

   public void setBeamTarget(@Nullable BlockPos p_184516_1_) {
      this.getDataManager().set(BEAM_TARGET, Optional.ofNullable(p_184516_1_));
   }

   @Nullable
   public BlockPos getBeamTarget() {
      return this.getDataManager().get(BEAM_TARGET).orElse(null);
   }

   public void setShowBottom(boolean p_184517_1_) {
      this.getDataManager().set(SHOW_BOTTOM, p_184517_1_);
   }

   public boolean shouldShowBottom() {
      return this.getDataManager().get(SHOW_BOTTOM);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      return super.isInRangeToRenderDist(p_70112_1_) || this.getBeamTarget() != null;
   }
}
