package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.math.Vec3d;

public class PhaseHover extends PhaseBase {
   private Vec3d targetLocation;

   public PhaseHover(EntityDragon p_i46790_1_) {
      super(p_i46790_1_);
   }

   public void serverTick() {
      if (this.targetLocation == null) {
         this.targetLocation = new Vec3d(this.dragon.posX, this.dragon.posY, this.dragon.posZ);
      }

   }

   public boolean getIsStationary() {
      return true;
   }

   public void initPhase() {
      this.targetLocation = null;
   }

   public float getMaxRiseOrFall() {
      return 1.0F;
   }

   @Nullable
   public Vec3d getTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<PhaseHover> getType() {
      return PhaseType.HOVER;
   }
}
