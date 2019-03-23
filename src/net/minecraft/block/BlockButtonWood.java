package net.minecraft.block;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

public class BlockButtonWood extends BlockButton {
   protected BlockButtonWood(Block.Properties p_i48291_1_) {
      super(true, p_i48291_1_);
   }

   protected SoundEvent getSoundEvent(boolean p_196369_1_) {
      return p_196369_1_ ? SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON : SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF;
   }
}
