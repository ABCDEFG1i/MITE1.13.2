package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class PhaseBase implements IPhase {
   protected final EntityDragon dragon;

   public PhaseBase(EntityDragon p_i46795_1_) {
      this.dragon = p_i46795_1_;
   }

   public boolean getIsStationary() {
      return false;
   }

   public void clientTick() {
   }

   public void serverTick() {
   }

   public void onCrystalDestroyed(EntityEnderCrystal p_188655_1_, BlockPos p_188655_2_, DamageSource p_188655_3_, @Nullable EntityPlayer p_188655_4_) {
   }

   public void initPhase() {
   }

   public void removeAreaEffect() {
   }

   public float getMaxRiseOrFall() {
      return 0.6F;
   }

   @Nullable
   public Vec3d getTargetLocation() {
      return null;
   }

   public float getAdjustedDamage(MultiPartEntityPart p_188656_1_, DamageSource p_188656_2_, float p_188656_3_) {
      return p_188656_3_;
   }

   public float getYawFactor() {
      float f = MathHelper.sqrt(this.dragon.motionX * this.dragon.motionX + this.dragon.motionZ * this.dragon.motionZ) + 1.0F;
      float f1 = Math.min(f, 40.0F);
      return 0.7F / f1 / f;
   }
}
