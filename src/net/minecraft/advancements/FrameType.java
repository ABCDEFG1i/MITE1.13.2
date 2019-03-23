package net.minecraft.advancements;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum FrameType {
   TASK("task", 0, TextFormatting.GREEN),
   CHALLENGE("challenge", 26, TextFormatting.DARK_PURPLE),
   GOAL("goal", 52, TextFormatting.GREEN);

   private final String name;
   private final int icon;
   private final TextFormatting format;

   private FrameType(String p_i47585_3_, int p_i47585_4_, TextFormatting p_i47585_5_) {
      this.name = p_i47585_3_;
      this.icon = p_i47585_4_;
      this.format = p_i47585_5_;
   }

   public String getName() {
      return this.name;
   }

   @OnlyIn(Dist.CLIENT)
   public int getIcon() {
      return this.icon;
   }

   public static FrameType byName(String p_192308_0_) {
      for(FrameType frametype : values()) {
         if (frametype.name.equals(p_192308_0_)) {
            return frametype;
         }
      }

      throw new IllegalArgumentException("Unknown frame type '" + p_192308_0_ + "'");
   }

   public TextFormatting getFormat() {
      return this.format;
   }
}
