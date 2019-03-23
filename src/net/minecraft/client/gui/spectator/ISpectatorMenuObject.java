package net.minecraft.client.gui.spectator;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISpectatorMenuObject {
   void selectItem(SpectatorMenu p_178661_1_);

   ITextComponent getSpectatorName();

   void renderIcon(float p_178663_1_, int p_178663_2_);

   boolean isEnabled();
}
