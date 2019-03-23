package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemAir extends Item {
   private final Block block;

   public ItemAir(Block p_i48535_1_, Item.Properties p_i48535_2_) {
      super(p_i48535_2_);
      this.block = p_i48535_1_;
   }

   public String getTranslationKey() {
      return this.block.getTranslationKey();
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      super.addInformation(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
      this.block.addInformation(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
   }
}
