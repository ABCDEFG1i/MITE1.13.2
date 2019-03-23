package net.minecraft.item;

import net.minecraft.init.Blocks;

public class ItemString extends ItemBlock {
   public ItemString(Item.Properties p_i48461_1_) {
      super(Blocks.TRIPWIRE, p_i48461_1_);
   }

   public String getTranslationKey() {
      return this.getDefaultTranslationKey();
   }
}
