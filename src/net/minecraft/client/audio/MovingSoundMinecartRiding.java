package net.minecraft.client.audio;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MovingSoundMinecartRiding extends MovingSound {
   private final EntityPlayer player;
   private final EntityMinecart minecart;

   public MovingSoundMinecartRiding(EntityPlayer p_i48613_1_, EntityMinecart p_i48613_2_) {
      super(SoundEvents.ENTITY_MINECART_INSIDE, SoundCategory.NEUTRAL);
      this.player = p_i48613_1_;
      this.minecart = p_i48613_2_;
      this.field_147666_i = ISound.AttenuationType.NONE;
      this.field_147659_g = true;
      this.field_147665_h = 0;
   }

   public void tick() {
      if (!this.minecart.isDead && this.player.isRiding() && this.player.getRidingEntity() == this.minecart) {
         float f = MathHelper.sqrt(this.minecart.motionX * this.minecart.motionX + this.minecart.motionZ * this.minecart.motionZ);
         if ((double)f >= 0.01D) {
            this.field_147662_b = 0.0F + MathHelper.clamp(f, 0.0F, 1.0F) * 0.75F;
         } else {
            this.field_147662_b = 0.0F;
         }

      } else {
         this.donePlaying = true;
      }
   }
}
