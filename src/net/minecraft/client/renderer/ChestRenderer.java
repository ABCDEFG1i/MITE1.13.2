package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChestRenderer {
   public void renderChestBrightness(Block p_178175_1_, float p_178175_2_) {
      GlStateManager.color4f(p_178175_2_, p_178175_2_, p_178175_2_, 1.0F);
      GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
      TileEntityItemStackRenderer.instance.renderByItem(new ItemStack(p_178175_1_));
   }
}
