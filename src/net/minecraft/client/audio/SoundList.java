package net.minecraft.client.audio;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoundList {
   private final List<Sound> sounds;
   private final boolean replaceExisting;
   private final String subtitle;

   public SoundList(List<Sound> p_i46525_1_, boolean p_i46525_2_, String p_i46525_3_) {
      this.sounds = p_i46525_1_;
      this.replaceExisting = p_i46525_2_;
      this.subtitle = p_i46525_3_;
   }

   public List<Sound> getSounds() {
      return this.sounds;
   }

   public boolean canReplaceExisting() {
      return this.replaceExisting;
   }

   @Nullable
   public String getSubtitle() {
      return this.subtitle;
   }
}
