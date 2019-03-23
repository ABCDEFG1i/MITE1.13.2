package net.minecraft.client.audio;

import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSound implements ISound {
   protected Sound field_184367_a;
   @Nullable
   private SoundEventAccessor field_184369_l;
   protected SoundCategory field_184368_b;
   protected ResourceLocation field_147664_a;
   protected float field_147662_b = 1.0F;
   protected float field_147663_c = 1.0F;
   protected float field_147660_d;
   protected float field_147661_e;
   protected float field_147658_f;
   protected boolean field_147659_g;
   protected int field_147665_h;
   protected ISound.AttenuationType field_147666_i = ISound.AttenuationType.LINEAR;
   protected boolean field_204201_l;

   protected AbstractSound(SoundEvent p_i46533_1_, SoundCategory p_i46533_2_) {
      this(p_i46533_1_.getSoundName(), p_i46533_2_);
   }

   protected AbstractSound(ResourceLocation p_i46534_1_, SoundCategory p_i46534_2_) {
      this.field_147664_a = p_i46534_1_;
      this.field_184368_b = p_i46534_2_;
   }

   public ResourceLocation getSoundLocation() {
      return this.field_147664_a;
   }

   public SoundEventAccessor createAccessor(SoundHandler p_184366_1_) {
      this.field_184369_l = p_184366_1_.getAccessor(this.field_147664_a);
      if (this.field_184369_l == null) {
         this.field_184367_a = SoundHandler.MISSING_SOUND;
      } else {
         this.field_184367_a = this.field_184369_l.cloneEntry();
      }

      return this.field_184369_l;
   }

   public Sound getSound() {
      return this.field_184367_a;
   }

   public SoundCategory getCategory() {
      return this.field_184368_b;
   }

   public boolean canRepeat() {
      return this.field_147659_g;
   }

   public int getRepeatDelay() {
      return this.field_147665_h;
   }

   public float getVolume() {
      return this.field_147662_b * this.field_184367_a.getVolume();
   }

   public float getPitch() {
      return this.field_147663_c * this.field_184367_a.getPitch();
   }

   public float getX() {
      return this.field_147660_d;
   }

   public float getY() {
      return this.field_147661_e;
   }

   public float getZ() {
      return this.field_147658_f;
   }

   public ISound.AttenuationType getAttenuationType() {
      return this.field_147666_i;
   }

   public boolean isPriority() {
      return this.field_204201_l;
   }
}
