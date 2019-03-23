package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugRendererWorldGenAttempts implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private final List<BlockPos> locations = Lists.newArrayList();
   private final List<Float> sizes = Lists.newArrayList();
   private final List<Float> alphas = Lists.newArrayList();
   private final List<Float> reds = Lists.newArrayList();
   private final List<Float> greens = Lists.newArrayList();
   private final List<Float> blues = Lists.newArrayList();

   public DebugRendererWorldGenAttempts(Minecraft p_i48763_1_) {
      this.minecraft = p_i48763_1_;
   }

   public void addAttempt(BlockPos p_201734_1_, float p_201734_2_, float p_201734_3_, float p_201734_4_, float p_201734_5_, float p_201734_6_) {
      this.locations.add(p_201734_1_);
      this.sizes.add(p_201734_2_);
      this.alphas.add(p_201734_6_);
      this.reds.add(p_201734_3_);
      this.greens.add(p_201734_4_);
      this.blues.add(p_201734_5_);
   }

   public void render(float p_190060_1_, long p_190060_2_) {
      EntityPlayer entityplayer = this.minecraft.player;
      IBlockReader iblockreader = this.minecraft.world;
      double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)p_190060_1_;
      double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)p_190060_1_;
      double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)p_190060_1_;
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture2D();
      new BlockPos(entityplayer.posX, 0.0D, entityplayer.posZ);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

      for(int i = 0; i < this.locations.size(); ++i) {
         BlockPos blockpos = this.locations.get(i);
         Float f = this.sizes.get(i);
         float f1 = f / 2.0F;
         WorldRenderer.func_189693_b(bufferbuilder, (double)((float)blockpos.getX() + 0.5F - f1) - d0, (double)((float)blockpos.getY() + 0.5F - f1) - d1, (double)((float)blockpos.getZ() + 0.5F - f1) - d2, (double)((float)blockpos.getX() + 0.5F + f1) - d0, (double)((float)blockpos.getY() + 0.5F + f1) - d1, (double)((float)blockpos.getZ() + 0.5F + f1) - d2, this.reds.get(i), this.greens.get(i), this.blues.get(i), this.alphas.get(i));
      }

      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.popMatrix();
   }
}
