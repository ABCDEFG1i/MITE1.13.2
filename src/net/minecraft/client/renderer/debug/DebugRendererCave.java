package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
public class DebugRendererCave implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private final Map<BlockPos, BlockPos> subCaves = Maps.newHashMap();
   private final Map<BlockPos, Float> sizes = Maps.newHashMap();
   private final List<BlockPos> caves = Lists.newArrayList();

   public DebugRendererCave(Minecraft p_i48766_1_) {
      this.minecraft = p_i48766_1_;
   }

   public void addCave(BlockPos p_201742_1_, List<BlockPos> p_201742_2_, List<Float> p_201742_3_) {
      for(int i = 0; i < p_201742_2_.size(); ++i) {
         this.subCaves.put(p_201742_2_.get(i), p_201742_1_);
         this.sizes.put(p_201742_2_.get(i), p_201742_3_.get(i));
      }

      this.caves.add(p_201742_1_);
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
      BlockPos blockpos = new BlockPos(entityplayer.posX, 0.0D, entityplayer.posZ);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

      for(Entry<BlockPos, BlockPos> entry : this.subCaves.entrySet()) {
         BlockPos blockpos1 = entry.getKey();
         BlockPos blockpos2 = entry.getValue();
         float f = (float)(blockpos2.getX() * 128 % 256) / 256.0F;
         float f1 = (float)(blockpos2.getY() * 128 % 256) / 256.0F;
         float f2 = (float)(blockpos2.getZ() * 128 % 256) / 256.0F;
         float f3 = this.sizes.get(blockpos1);
         if (blockpos.getDistance(blockpos1) < 160.0D) {
            WorldRenderer.func_189693_b(bufferbuilder, (double)((float)blockpos1.getX() + 0.5F) - d0 - (double)f3, (double)((float)blockpos1.getY() + 0.5F) - d1 - (double)f3, (double)((float)blockpos1.getZ() + 0.5F) - d2 - (double)f3, (double)((float)blockpos1.getX() + 0.5F) - d0 + (double)f3, (double)((float)blockpos1.getY() + 0.5F) - d1 + (double)f3, (double)((float)blockpos1.getZ() + 0.5F) - d2 + (double)f3, f, f1, f2, 0.5F);
         }
      }

      for(BlockPos blockpos3 : this.caves) {
         if (blockpos.getDistance(blockpos3) < 160.0D) {
            WorldRenderer.func_189693_b(bufferbuilder, (double)blockpos3.getX() - d0, (double)blockpos3.getY() - d1, (double)blockpos3.getZ() - d2, (double)((float)blockpos3.getX() + 1.0F) - d0, (double)((float)blockpos3.getY() + 1.0F) - d1, (double)((float)blockpos3.getZ() + 1.0F) - d2, 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }

      tessellator.draw();
      GlStateManager.enableDepthTest();
      GlStateManager.enableTexture2D();
      GlStateManager.popMatrix();
   }
}
