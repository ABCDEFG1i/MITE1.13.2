package net.minecraft.client.renderer.tileentity;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBed;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityBedRenderer extends TileEntityRenderer<TileEntityBed> {
   private static final ResourceLocation[] TEXTURES = Arrays.stream(EnumDyeColor.values()).sorted(Comparator.comparingInt(EnumDyeColor::getId)).map((p_199742_0_) -> {
      return new ResourceLocation("textures/entity/bed/" + p_199742_0_.getTranslationKey() + ".png");
   }).toArray((p_199743_0_) -> {
      return new ResourceLocation[p_199743_0_];
   });
   private final ModelBed model = new ModelBed();

   public void render(TileEntityBed p_199341_1_, double p_199341_2_, double p_199341_4_, double p_199341_6_, float p_199341_8_, int p_199341_9_) {
      if (p_199341_9_ >= 0) {
         this.bindTexture(DESTROY_STAGES[p_199341_9_]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(4.0F, 4.0F, 1.0F);
         GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         ResourceLocation resourcelocation = TEXTURES[p_199341_1_.getColor().getId()];
         if (resourcelocation != null) {
            this.bindTexture(resourcelocation);
         }
      }

      if (p_199341_1_.hasWorld()) {
         IBlockState iblockstate = p_199341_1_.getBlockState();
         this.func_199343_a(iblockstate.get(BlockBed.PART) == BedPart.HEAD, p_199341_2_, p_199341_4_, p_199341_6_, iblockstate.get(BlockBed.HORIZONTAL_FACING));
      } else {
         this.func_199343_a(true, p_199341_2_, p_199341_4_, p_199341_6_, EnumFacing.SOUTH);
         this.func_199343_a(false, p_199341_2_, p_199341_4_, p_199341_6_ - 1.0D, EnumFacing.SOUTH);
      }

      if (p_199341_9_ >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

   }

   private void func_199343_a(boolean p_199343_1_, double p_199343_2_, double p_199343_4_, double p_199343_6_, EnumFacing p_199343_8_) {
      this.model.preparePiece(p_199343_1_);
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)p_199343_2_, (float)p_199343_4_ + 0.5625F, (float)p_199343_6_);
      GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translatef(0.5F, 0.5F, 0.5F);
      GlStateManager.rotatef(180.0F + p_199343_8_.getHorizontalAngle(), 0.0F, 0.0F, 1.0F);
      GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
      GlStateManager.enableRescaleNormal();
      this.model.render();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }
}
