package net.minecraft.item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemSimpleFoiled extends Item {
   public ItemSimpleFoiled(Item.Properties p_i48467_1_) {
      super(p_i48467_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect(ItemStack p_77636_1_) {
      return true;
   }
}
