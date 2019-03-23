package net.minecraft.client.renderer.debug;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Util;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugRendererCollisionBox implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private double lastUpdate = Double.MIN_VALUE;
   private List<VoxelShape> collisionData = Collections.emptyList();

   public DebugRendererCollisionBox(Minecraft p_i47215_1_) {
      this.minecraft = p_i47215_1_;
   }

   public void render(float p_190060_1_, long p_190060_2_) {
      EntityPlayer entityplayer = this.minecraft.player;
      double d0 = (double)Util.nanoTime();
      if (d0 - this.lastUpdate > 1.0E8D) {
         this.lastUpdate = d0;
         this.collisionData = entityplayer.world.func_212388_b(entityplayer, entityplayer.getEntityBoundingBox().grow(6.0D)).collect(Collectors.toList());
      }

      double d1 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)p_190060_1_;
      double d2 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)p_190060_1_;
      double d3 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)p_190060_1_;
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.lineWidth(2.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.depthMask(false);

      for(VoxelShape voxelshape : this.collisionData) {
         WorldRenderer.func_195470_a(voxelshape, -d1, -d2, -d3, 1.0F, 1.0F, 1.0F, 1.0F);
      }

      GlStateManager.depthMask(true);
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }
}
