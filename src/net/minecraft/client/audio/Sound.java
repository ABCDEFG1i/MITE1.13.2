package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Sound implements ISoundEventAccessor<Sound> {
   private final ResourceLocation name;
   private final float volume;
   private final float pitch;
   private final int weight;
   private final Sound.Type type;
   private final boolean streaming;
   private final boolean preload;
   private final int attenuationDistance;

   public Sound(String p_i49182_1_, float p_i49182_2_, float p_i49182_3_, int p_i49182_4_, Sound.Type p_i49182_5_, boolean p_i49182_6_, boolean p_i49182_7_, int p_i49182_8_) {
      this.name = new ResourceLocation(p_i49182_1_);
      this.volume = p_i49182_2_;
      this.pitch = p_i49182_3_;
      this.weight = p_i49182_4_;
      this.type = p_i49182_5_;
      this.streaming = p_i49182_6_;
      this.preload = p_i49182_7_;
      this.attenuationDistance = p_i49182_8_;
   }

   public ResourceLocation getSoundLocation() {
      return this.name;
   }

   public ResourceLocation getSoundAsOggLocation() {
      return new ResourceLocation(this.name.getNamespace(), "sounds/" + this.name.getPath() + ".ogg");
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public int getWeight() {
      return this.weight;
   }

   public Sound cloneEntry() {
      return this;
   }

   public Sound.Type getType() {
      return this.type;
   }

   public boolean isStreaming() {
      return this.streaming;
   }

   public boolean shouldPreload() {
      return this.preload;
   }

   public int getAttenuationDistance() {
      return this.attenuationDistance;
   }

   @OnlyIn(Dist.CLIENT)
   public enum Type {
      FILE("file"),
      SOUND_EVENT("event");

      private final String name;

      Type(String p_i46631_3_) {
         this.name = p_i46631_3_;
      }

      public static Sound.Type getByName(String p_188704_0_) {
         for(Sound.Type sound$type : values()) {
            if (sound$type.name.equals(p_188704_0_)) {
               return sound$type;
            }
         }

         return null;
      }
   }
}
