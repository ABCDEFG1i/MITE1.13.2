package net.minecraft.client.audio;

import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISound {
   ResourceLocation getSoundLocation();

   @Nullable
   SoundEventAccessor createAccessor(SoundHandler p_184366_1_);

   Sound getSound();

   SoundCategory getCategory();

   boolean canRepeat();

   boolean isPriority();

   int getRepeatDelay();

   float getVolume();

   float getPitch();

   float getX();

   float getY();

   float getZ();

   ISound.AttenuationType getAttenuationType();

   default boolean canBeSilent() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   enum AttenuationType {
      NONE(0),
      LINEAR(2);

      private final int type;

      AttenuationType(int p_i45110_3_) {
         this.type = p_i45110_3_;
      }

      public int getTypeInt() {
         return this.type;
      }
   }
}
