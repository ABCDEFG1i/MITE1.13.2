package net.minecraft.client.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ITooltipFlag {
   boolean isAdvanced();

   @OnlyIn(Dist.CLIENT)
   enum TooltipFlags implements ITooltipFlag {
      NORMAL(false),
      ADVANCED(true);

      private final boolean isAdvanced;

      TooltipFlags(boolean p_i47611_3_) {
         this.isAdvanced = p_i47611_3_;
      }

      public boolean isAdvanced() {
         return this.isAdvanced;
      }
   }
}
