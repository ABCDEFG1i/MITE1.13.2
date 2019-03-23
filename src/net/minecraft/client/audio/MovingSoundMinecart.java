package net.minecraft.client.audio;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MovingSoundMinecart extends MovingSound {
   private final EntityMinecart minecart;
   private float distance = 0.0F;

   public MovingSoundMinecart(EntityMinecart p_i48614_1_) {
      super(SoundEvents.ENTITY_MINECART_RIDING, SoundCategory.NEUTRAL);
      this.minecart = p_i48614_1_;
      this.field_147659_g = true;
      this.field_147665_h = 0;
      this.field_147662_b = 0.0F;
      this.field_147660_d = (float)p_i48614_1_.posX;
      this.field_147661_e = (float)p_i48614_1_.posY;
      this.field_147658_f = (float)p_i48614_1_.posZ;
   }

   public boolean canBeSilent() {
      return true;
   }

   public void tick() {
      if (this.minecart.isDead) {
         this.donePlaying = true;
      } else {
         this.field_147660_d = (float)this.minecart.posX;
         this.field_147661_e = (float)this.minecart.posY;
         this.field_147658_f = (float)this.minecart.posZ;
         float f = MathHelper.sqrt(this.minecart.motionX * this.minecart.motionX + this.minecart.motionZ * this.minecart.motionZ);
         if ((double)f >= 0.01D) {
            this.distance = MathHelper.clamp(this.distance + 0.0025F, 0.0F, 1.0F);
            this.field_147662_b = 0.0F + MathHelper.clamp(f, 0.0F, 0.5F) * 0.7F;
         } else {
            this.distance = 0.0F;
            this.field_147662_b = 0.0F;
         }

      }
   }
}
