package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBook;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityEnchantmentTableRenderer extends TileEntityRenderer<TileEntityEnchantmentTable> {
   private static final ResourceLocation TEXTURE_BOOK = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private final ModelBook modelBook = new ModelBook();

   public void render(TileEntityEnchantmentTable p_199341_1_, double p_199341_2_, double p_199341_4_, double p_199341_6_, float p_199341_8_, int p_199341_9_) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)p_199341_2_ + 0.5F, (float)p_199341_4_ + 0.75F, (float)p_199341_6_ + 0.5F);
      float f = (float)p_199341_1_.field_195522_a + p_199341_8_;
      GlStateManager.translatef(0.0F, 0.1F + MathHelper.sin(f * 0.1F) * 0.01F, 0.0F);

      float f1;
      for(f1 = p_199341_1_.field_195529_l - p_199341_1_.field_195530_m; f1 >= (float)Math.PI; f1 -= ((float)Math.PI * 2F)) {
         ;
      }

      while(f1 < -(float)Math.PI) {
         f1 += ((float)Math.PI * 2F);
      }

      float f2 = p_199341_1_.field_195530_m + f1 * p_199341_8_;
      GlStateManager.rotatef(-f2 * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(80.0F, 0.0F, 0.0F, 1.0F);
      this.bindTexture(TEXTURE_BOOK);
      float f3 = p_199341_1_.field_195524_g + (p_199341_1_.field_195523_f - p_199341_1_.field_195524_g) * p_199341_8_ + 0.25F;
      float f4 = p_199341_1_.field_195524_g + (p_199341_1_.field_195523_f - p_199341_1_.field_195524_g) * p_199341_8_ + 0.75F;
      f3 = (f3 - (float)MathHelper.fastFloor((double)f3)) * 1.6F - 0.3F;
      f4 = (f4 - (float)MathHelper.fastFloor((double)f4)) * 1.6F - 0.3F;
      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      if (f4 < 0.0F) {
         f4 = 0.0F;
      }

      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      if (f4 > 1.0F) {
         f4 = 1.0F;
      }

      float f5 = p_199341_1_.field_195528_k + (p_199341_1_.field_195527_j - p_199341_1_.field_195528_k) * p_199341_8_;
      GlStateManager.enableCull();
      this.modelBook.render((Entity)null, f, f3, f4, f5, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
   }
}
