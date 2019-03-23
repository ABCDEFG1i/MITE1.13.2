package net.minecraft.client.audio;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElytraSound extends MovingSound {
   private final EntityPlayerSP player;
   private int time;

   public ElytraSound(EntityPlayerSP p_i47113_1_) {
      super(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.PLAYERS);
      this.player = p_i47113_1_;
      this.field_147659_g = true;
      this.field_147665_h = 0;
      this.field_147662_b = 0.1F;
   }

   public void tick() {
      ++this.time;
      if (!this.player.isDead && (this.time <= 20 || this.player.isElytraFlying())) {
         this.field_147660_d = (float)this.player.posX;
         this.field_147661_e = (float)this.player.posY;
         this.field_147658_f = (float)this.player.posZ;
         float f = MathHelper.sqrt(this.player.motionX * this.player.motionX + this.player.motionZ * this.player.motionZ + this.player.motionY * this.player.motionY);
         float f1 = f / 2.0F;
         if ((double)f >= 0.01D) {
            this.field_147662_b = MathHelper.clamp(f1 * f1, 0.0F, 1.0F);
         } else {
            this.field_147662_b = 0.0F;
         }

         if (this.time < 20) {
            this.field_147662_b = 0.0F;
         } else if (this.time < 40) {
            this.field_147662_b = (float)((double)this.field_147662_b * ((double)(this.time - 20) / 20.0D));
         }

         float f2 = 0.8F;
         if (this.field_147662_b > 0.8F) {
            this.field_147663_c = 1.0F + (this.field_147662_b - 0.8F);
         } else {
            this.field_147663_c = 1.0F;
         }

      } else {
         this.donePlaying = true;
      }
   }
}
