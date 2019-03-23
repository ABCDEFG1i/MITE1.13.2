package net.minecraft.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IProgressUpdate {
   void func_200210_a(ITextComponent p_200210_1_);

   @OnlyIn(Dist.CLIENT)
   void func_200211_b(ITextComponent p_200211_1_);

   void func_200209_c(ITextComponent p_200209_1_);

   void setLoadingProgress(int p_73718_1_);

   @OnlyIn(Dist.CLIENT)
   void setDoneWorking();
}
