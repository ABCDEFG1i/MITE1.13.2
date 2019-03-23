package net.minecraft.client.audio;

import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardianSound extends MovingSound {
   private final EntityGuardian guardian;

   public GuardianSound(EntityGuardian p_i46071_1_) {
      super(SoundEvents.ENTITY_GUARDIAN_ATTACK, SoundCategory.HOSTILE);
      this.guardian = p_i46071_1_;
      this.field_147666_i = ISound.AttenuationType.NONE;
      this.field_147659_g = true;
      this.field_147665_h = 0;
   }

   public void tick() {
      if (!this.guardian.isDead && this.guardian.hasTargetedEntity()) {
         this.field_147660_d = (float)this.guardian.posX;
         this.field_147661_e = (float)this.guardian.posY;
         this.field_147658_f = (float)this.guardian.posZ;
         float f = this.guardian.getAttackAnimationScale(0.0F);
         this.field_147662_b = 0.0F + 1.0F * f * f;
         this.field_147663_c = 0.7F + 0.5F * f;
      } else {
         this.donePlaying = true;
      }
   }
}
