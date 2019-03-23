package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface IPhase {
   boolean getIsStationary();

   void clientTick();

   void serverTick();

   void onCrystalDestroyed(EntityEnderCrystal p_188655_1_, BlockPos p_188655_2_, DamageSource p_188655_3_, @Nullable EntityPlayer p_188655_4_);

   void initPhase();

   void removeAreaEffect();

   float getMaxRiseOrFall();

   float getYawFactor();

   PhaseType<? extends IPhase> getType();

   @Nullable
   Vec3d getTargetLocation();

   float getAdjustedDamage(MultiPartEntityPart p_188656_1_, DamageSource p_188656_2_, float p_188656_3_);
}
