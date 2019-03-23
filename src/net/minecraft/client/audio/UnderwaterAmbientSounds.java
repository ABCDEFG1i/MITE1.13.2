package net.minecraft.client.audio;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UnderwaterAmbientSounds {
   @OnlyIn(Dist.CLIENT)
   public static class SubSound extends MovingSound {
      private final EntityPlayerSP player;

      protected SubSound(EntityPlayerSP p_i48884_1_, SoundEvent p_i48884_2_) {
         super(p_i48884_2_, SoundCategory.AMBIENT);
         this.player = p_i48884_1_;
         this.field_147659_g = false;
         this.field_147665_h = 0;
         this.field_147662_b = 1.0F;
         this.field_204201_l = true;
      }

      public void tick() {
         if (!this.player.isDead && this.player.canSwim()) {
            this.field_147660_d = (float)this.player.posX;
            this.field_147661_e = (float)this.player.posY;
            this.field_147658_f = (float)this.player.posZ;
         } else {
            this.donePlaying = true;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class UnderWaterSound extends MovingSound {
      private final EntityPlayerSP player;
      private int ticksInWater;

      public UnderWaterSound(EntityPlayerSP p_i48883_1_) {
         super(SoundEvents.AMBIENT_UNDERWATER_LOOP, SoundCategory.AMBIENT);
         this.player = p_i48883_1_;
         this.field_147659_g = true;
         this.field_147665_h = 0;
         this.field_147662_b = 1.0F;
         this.field_204201_l = true;
      }

      public void tick() {
         if (!this.player.isDead && this.ticksInWater >= 0) {
            this.field_147660_d = (float)this.player.posX;
            this.field_147661_e = (float)this.player.posY;
            this.field_147658_f = (float)this.player.posZ;
            if (this.player.canSwim()) {
               ++this.ticksInWater;
            } else {
               this.ticksInWater -= 2;
            }

            this.ticksInWater = Math.min(this.ticksInWater, 40);
            this.field_147662_b = Math.max(0.0F, Math.min((float)this.ticksInWater / 40.0F, 1.0F));
         } else {
            this.donePlaying = true;
         }
      }
   }
}
