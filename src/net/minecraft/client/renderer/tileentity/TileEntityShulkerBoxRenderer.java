package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderShulker;
import net.minecraft.client.renderer.entity.model.ModelShulker;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityShulkerBoxRenderer extends TileEntityRenderer<TileEntityShulkerBox> {
   private final ModelShulker model;

   public TileEntityShulkerBoxRenderer(ModelShulker p_i47216_1_) {
      this.model = p_i47216_1_;
   }

   public void render(TileEntityShulkerBox p_199341_1_, double p_199341_2_, double p_199341_4_, double p_199341_6_, float p_199341_8_, int p_199341_9_) {
      EnumFacing enumfacing = EnumFacing.UP;
      if (p_199341_1_.hasWorld()) {
         IBlockState iblockstate = this.getWorld().getBlockState(p_199341_1_.getPos());
         if (iblockstate.getBlock() instanceof BlockShulkerBox) {
            enumfacing = iblockstate.get(BlockShulkerBox.FACING);
         }
      }

      GlStateManager.enableDepthTest();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      GlStateManager.disableCull();
      if (p_199341_9_ >= 0) {
         this.bindTexture(DESTROY_STAGES[p_199341_9_]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(4.0F, 4.0F, 1.0F);
         GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         EnumDyeColor enumdyecolor = p_199341_1_.getColor();
         if (enumdyecolor == null) {
            this.bindTexture(RenderShulker.field_204402_a);
         } else {
            this.bindTexture(RenderShulker.SHULKER_ENDERGOLEM_TEXTURE[enumdyecolor.getId()]);
         }
      }

      GlStateManager.pushMatrix();
      GlStateManager.enableRescaleNormal();
      if (p_199341_9_ < 0) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      GlStateManager.translatef((float)p_199341_2_ + 0.5F, (float)p_199341_4_ + 1.5F, (float)p_199341_6_ + 0.5F);
      GlStateManager.scalef(1.0F, -1.0F, -1.0F);
      GlStateManager.translatef(0.0F, 1.0F, 0.0F);
      float f = 0.9995F;
      GlStateManager.scalef(0.9995F, 0.9995F, 0.9995F);
      GlStateManager.translatef(0.0F, -1.0F, 0.0F);
      switch(enumfacing) {
      case DOWN:
         GlStateManager.translatef(0.0F, 2.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
      case UP:
      default:
         break;
      case NORTH:
         GlStateManager.translatef(0.0F, 1.0F, 1.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         break;
      case SOUTH:
         GlStateManager.translatef(0.0F, 1.0F, -1.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         break;
      case WEST:
         GlStateManager.translatef(-1.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
         break;
      case EAST:
         GlStateManager.translatef(1.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
      }

      this.model.func_205069_a().render(0.0625F);
      GlStateManager.translatef(0.0F, -p_199341_1_.getProgress(p_199341_8_) * 0.5F, 0.0F);
      GlStateManager.rotatef(270.0F * p_199341_1_.getProgress(p_199341_8_), 0.0F, 1.0F, 0.0F);
      this.model.func_205068_b().render(0.0625F);
      GlStateManager.enableCull();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (p_199341_9_ >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

   }
}
