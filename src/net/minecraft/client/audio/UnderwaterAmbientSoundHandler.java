package net.minecraft.client.audio;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UnderwaterAmbientSoundHandler implements IAmbientSoundHandler {
   private final EntityPlayerSP player;
   private final SoundHandler soundHandler;
   private int delay = 0;

   public UnderwaterAmbientSoundHandler(EntityPlayerSP p_i48885_1_, SoundHandler p_i48885_2_) {
      this.player = p_i48885_1_;
      this.soundHandler = p_i48885_2_;
   }

   public void tick() {
      --this.delay;
      if (this.delay <= 0 && this.player.canSwim()) {
         float f = this.player.world.rand.nextFloat();
         if (f < 1.0E-4F) {
            this.delay = 0;
            this.soundHandler.play(new UnderwaterAmbientSounds.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRARARE));
         } else if (f < 0.001F) {
            this.delay = 0;
            this.soundHandler.play(new UnderwaterAmbientSounds.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE));
         } else if (f < 0.01F) {
            this.delay = 0;
            this.soundHandler.play(new UnderwaterAmbientSounds.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS));
         }
      }

   }
}
