package net.minecraft.client.renderer.tileentity;

import java.util.Random;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityPistonRenderer extends TileEntityRenderer<TileEntityPiston> {
   private final BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();

   public void render(TileEntityPiston p_199341_1_, double p_199341_2_, double p_199341_4_, double p_199341_6_, float p_199341_8_, int p_199341_9_) {
      BlockPos blockpos = p_199341_1_.getPos().offset(p_199341_1_.getMotionDirection().getOpposite());
      IBlockState iblockstate = p_199341_1_.getPistonState();
      if (!iblockstate.isAir() && !(p_199341_1_.getProgress(p_199341_8_) >= 1.0F)) {
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         RenderHelper.disableStandardItemLighting();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         GlStateManager.enableBlend();
         GlStateManager.disableCull();
         if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(7425);
         } else {
            GlStateManager.shadeModel(7424);
         }

         bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
         bufferbuilder.setTranslation(p_199341_2_ - (double)blockpos.getX() + (double)p_199341_1_.getOffsetX(p_199341_8_), p_199341_4_ - (double)blockpos.getY() + (double)p_199341_1_.getOffsetY(p_199341_8_), p_199341_6_ - (double)blockpos.getZ() + (double)p_199341_1_.getOffsetZ(p_199341_8_));
         World world = this.getWorld();
         if (iblockstate.getBlock() == Blocks.PISTON_HEAD && p_199341_1_.getProgress(p_199341_8_) <= 4.0F) {
            iblockstate = iblockstate.with(BlockPistonExtension.SHORT, Boolean.valueOf(true));
            this.renderStateModel(blockpos, iblockstate, bufferbuilder, world, false);
         } else if (p_199341_1_.shouldPistonHeadBeRendered() && !p_199341_1_.isExtending()) {
            PistonType pistontype = iblockstate.getBlock() == Blocks.STICKY_PISTON ? PistonType.STICKY : PistonType.DEFAULT;
            IBlockState iblockstate1 = Blocks.PISTON_HEAD.getDefaultState().with(BlockPistonExtension.TYPE, pistontype).with(BlockPistonExtension.FACING, iblockstate.get(BlockPistonBase.FACING));
            iblockstate1 = iblockstate1.with(BlockPistonExtension.SHORT, Boolean.valueOf(p_199341_1_.getProgress(p_199341_8_) >= 0.5F));
            this.renderStateModel(blockpos, iblockstate1, bufferbuilder, world, false);
            BlockPos blockpos1 = blockpos.offset(p_199341_1_.getMotionDirection());
            bufferbuilder.setTranslation(p_199341_2_ - (double)blockpos1.getX(), p_199341_4_ - (double)blockpos1.getY(), p_199341_6_ - (double)blockpos1.getZ());
            iblockstate = iblockstate.with(BlockPistonBase.EXTENDED, Boolean.valueOf(true));
            this.renderStateModel(blockpos1, iblockstate, bufferbuilder, world, true);
         } else {
            this.renderStateModel(blockpos, iblockstate, bufferbuilder, world, false);
         }

         bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
         tessellator.draw();
         RenderHelper.enableStandardItemLighting();
      }
   }

   private boolean renderStateModel(BlockPos p_188186_1_, IBlockState p_188186_2_, BufferBuilder p_188186_3_, World p_188186_4_, boolean p_188186_5_) {
      return this.blockRenderer.getBlockModelRenderer().func_199324_a(p_188186_4_, this.blockRenderer.func_184389_a(p_188186_2_), p_188186_2_, p_188186_1_, p_188186_3_, p_188186_5_, new Random(), p_188186_2_.getPositionRandom(p_188186_1_));
   }
}
