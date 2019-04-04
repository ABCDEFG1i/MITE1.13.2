package net.minecraft.client.audio;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MusicTicker implements ITickable {
   private final Random rand = new Random();
   private final Minecraft mc;
   private ISound currentMusic;
   private int timeUntilNextMusic = 100;
   private boolean field_209508_e;

   public MusicTicker(Minecraft p_i45112_1_) {
      this.mc = p_i45112_1_;
   }

   public void tick() {
      MusicTicker.MusicType musicticker$musictype = this.mc.getAmbientMusicType();
      if (this.currentMusic != null) {
         if (!musicticker$musictype.getMusicLocation().getSoundName().equals(this.currentMusic.getSoundLocation())) {
            this.mc.getSoundHandler().stop(this.currentMusic);
            this.timeUntilNextMusic = MathHelper.nextInt(this.rand, 0, musicticker$musictype.getMinDelay() / 2);
            this.field_209508_e = false;
         }

         if (!this.field_209508_e && !this.mc.getSoundHandler().isPlaying(this.currentMusic)) {
            this.currentMusic = null;
            this.timeUntilNextMusic = Math.min(MathHelper.nextInt(this.rand, musicticker$musictype.getMinDelay(), musicticker$musictype.getMaxDelay()), this.timeUntilNextMusic);
         } else if (this.mc.getSoundHandler().isPlaying(this.currentMusic)) {
            this.field_209508_e = false;
         }
      }

      this.timeUntilNextMusic = Math.min(this.timeUntilNextMusic, musicticker$musictype.getMaxDelay());
      if (this.currentMusic == null && this.timeUntilNextMusic-- <= 0) {
         this.playMusic(musicticker$musictype);
      }

   }

   public void playMusic(MusicTicker.MusicType p_181558_1_) {
      this.currentMusic = SimpleSound.func_184370_a(p_181558_1_.getMusicLocation());
      this.mc.getSoundHandler().play(this.currentMusic);
      this.timeUntilNextMusic = Integer.MAX_VALUE;
      this.field_209508_e = true;
   }

   public void stopMusic() {
      if (this.currentMusic != null) {
         this.mc.getSoundHandler().stop(this.currentMusic);
         this.currentMusic = null;
         this.timeUntilNextMusic = 0;
         this.field_209508_e = false;
      }

   }

   public boolean isPlaying(MusicTicker.MusicType p_209100_1_) {
      return this.currentMusic != null && p_209100_1_.getMusicLocation().getSoundName().equals(
              this.currentMusic.getSoundLocation());
   }

   @OnlyIn(Dist.CLIENT)
   public enum MusicType {
      MENU(SoundEvents.MUSIC_MENU, 20, 600),
      GAME(SoundEvents.MUSIC_GAME, 12000, 24000),
      CREATIVE(SoundEvents.MUSIC_CREATIVE, 1200, 3600),
      CREDITS(SoundEvents.MUSIC_CREDITS, 0, 0),
      NETHER(SoundEvents.MUSIC_NETHER, 1200, 3600),
      END_BOSS(SoundEvents.MUSIC_DRAGON, 0, 0),
      END(SoundEvents.MUSIC_END, 6000, 24000),
      UNDER_WATER(SoundEvents.MUSIC_UNDER_WATER, 12000, 24000);

      private final SoundEvent musicLocation;
      private final int minDelay;
      private final int maxDelay;

      MusicType(SoundEvent p_i47050_3_, int p_i47050_4_, int p_i47050_5_) {
         this.musicLocation = p_i47050_3_;
         this.minDelay = p_i47050_4_;
         this.maxDelay = p_i47050_5_;
      }

      public SoundEvent getMusicLocation() {
         return this.musicLocation;
      }

      public int getMinDelay() {
         return this.minDelay;
      }

      public int getMaxDelay() {
         return this.maxDelay;
      }
   }
}
