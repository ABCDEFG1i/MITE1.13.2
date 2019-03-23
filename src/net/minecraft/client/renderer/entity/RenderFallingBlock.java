package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderFallingBlock extends Render<EntityFallingBlock> {
   public RenderFallingBlock(RenderManager p_i46177_1_) {
      super(p_i46177_1_);
      this.shadowSize = 0.5F;
   }

   public void doRender(EntityFallingBlock p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      IBlockState iblockstate = p_76986_1_.func_195054_l();
      if (iblockstate.getRenderType() == EnumBlockRenderType.MODEL) {
         World world = p_76986_1_.getWorldObj();
         if (iblockstate != world.getBlockState(new BlockPos(p_76986_1_)) && iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            if (this.renderOutlines) {
               GlStateManager.enableColorMaterial();
               GlStateManager.enableOutlineMode(this.getTeamColor(p_76986_1_));
            }

            bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
            BlockPos blockpos = new BlockPos(p_76986_1_.posX, p_76986_1_.getEntityBoundingBox().maxY, p_76986_1_.posZ);
            GlStateManager.translatef((float)(p_76986_2_ - (double)blockpos.getX() - 0.5D), (float)(p_76986_4_ - (double)blockpos.getY()), (float)(p_76986_6_ - (double)blockpos.getZ() - 0.5D));
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
            blockrendererdispatcher.getBlockModelRenderer().func_199324_a(world, blockrendererdispatcher.func_184389_a(iblockstate), iblockstate, blockpos, bufferbuilder, false, new Random(), iblockstate.getPositionRandom(p_76986_1_.getOrigin()));
            tessellator.draw();
            if (this.renderOutlines) {
               GlStateManager.disableOutlineMode();
               GlStateManager.disableColorMaterial();
            }

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
            super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
         }
      }
   }

   protected ResourceLocation getEntityTexture(EntityFallingBlock p_110775_1_) {
      return TextureMap.LOCATION_BLOCKS_TEXTURE;
   }
}
