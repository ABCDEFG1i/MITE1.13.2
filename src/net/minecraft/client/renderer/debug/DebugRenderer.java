package net.minecraft.client.renderer.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugRenderer {
   public final DebugRendererPathfinding pathfinding;
   public final DebugRenderer.IDebugRenderer water;
   public final DebugRenderer.IDebugRenderer chunkBorder;
   public final DebugRenderer.IDebugRenderer heightMap;
   public final DebugRenderer.IDebugRenderer collisionBox;
   public final DebugRenderer.IDebugRenderer neighborsUpdate;
   public final DebugRendererCave cave;
   public final DebugRendererStructure structure;
   public final DebugRenderer.IDebugRenderer light;
   public final DebugRenderer.IDebugRenderer worldGenAttempts;
   public final DebugRenderer.IDebugRenderer solidFace;
   private boolean chunkBorderEnabled;
   private boolean pathfindingEnabled = true;
   private boolean waterEnabled;
   private boolean heightMapEnabled;
   private boolean collisionBoxEnabled;
   private boolean neighborsUpdateEnabled = true;
   private boolean caveEnabled;
   private boolean structureEnabled;
   private boolean lightEnabled;
   private boolean worldGenAttemptsEnabled;
   private boolean solidFaceEnabled;

   public DebugRenderer(Minecraft p_i46557_1_) {
      this.pathfinding = new DebugRendererPathfinding(p_i46557_1_);
      this.water = new DebugRendererWater(p_i46557_1_);
      this.chunkBorder = new DebugRendererChunkBorder(p_i46557_1_);
      this.heightMap = new DebugRendererHeightMap(p_i46557_1_);
      this.collisionBox = new DebugRendererCollisionBox(p_i46557_1_);
      this.neighborsUpdate = new DebugRendererNeighborsUpdate(p_i46557_1_);
      this.cave = new DebugRendererCave(p_i46557_1_);
      this.structure = new DebugRendererStructure(p_i46557_1_);
      this.light = new DebugRendererLight(p_i46557_1_);
      this.worldGenAttempts = new DebugRendererWorldGenAttempts(p_i46557_1_);
      this.solidFace = new DebugRendererSolidFace(p_i46557_1_);
   }

   public boolean shouldRender() {
      return this.chunkBorderEnabled || this.pathfindingEnabled || this.waterEnabled || this.heightMapEnabled || this.collisionBoxEnabled || this.neighborsUpdateEnabled || this.lightEnabled || this.worldGenAttemptsEnabled || this.solidFaceEnabled;
   }

   public boolean toggleChunkBorders() {
      this.chunkBorderEnabled = !this.chunkBorderEnabled;
      return this.chunkBorderEnabled;
   }

   public void renderDebug(float p_190073_1_, long p_190073_2_) {
      if (this.pathfindingEnabled) {
         this.pathfinding.render(p_190073_1_, p_190073_2_);
      }

      if (this.chunkBorderEnabled && !Minecraft.getInstance().isReducedDebug()) {
         this.chunkBorder.render(p_190073_1_, p_190073_2_);
      }

      if (this.waterEnabled) {
         this.water.render(p_190073_1_, p_190073_2_);
      }

      if (this.heightMapEnabled) {
         this.heightMap.render(p_190073_1_, p_190073_2_);
      }

      if (this.collisionBoxEnabled) {
         this.collisionBox.render(p_190073_1_, p_190073_2_);
      }

      if (this.neighborsUpdateEnabled) {
         this.neighborsUpdate.render(p_190073_1_, p_190073_2_);
      }

      if (this.caveEnabled) {
         this.cave.render(p_190073_1_, p_190073_2_);
      }

      if (this.structureEnabled) {
         this.structure.render(p_190073_1_, p_190073_2_);
      }

      if (this.lightEnabled) {
         this.light.render(p_190073_1_, p_190073_2_);
      }

      if (this.worldGenAttemptsEnabled) {
         this.worldGenAttempts.render(p_190073_1_, p_190073_2_);
      }

      if (this.solidFaceEnabled) {
         this.solidFace.render(p_190073_1_, p_190073_2_);
      }

   }

   public static void renderDebugText(String p_191556_0_, int p_191556_1_, int p_191556_2_, int p_191556_3_, float p_191556_4_, int p_191556_5_) {
      renderDebugText(p_191556_0_, (double)p_191556_1_ + 0.5D, (double)p_191556_2_ + 0.5D, (double)p_191556_3_ + 0.5D, p_191556_4_, p_191556_5_);
   }

   public static void renderDebugText(String p_190076_0_, double p_190076_1_, double p_190076_3_, double p_190076_5_, float p_190076_7_, int p_190076_8_) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player != null && minecraft.getRenderManager() != null && minecraft.getRenderManager().options != null) {
         FontRenderer fontrenderer = minecraft.fontRenderer;
         EntityPlayer entityplayer = minecraft.player;
         double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)p_190076_7_;
         double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)p_190076_7_;
         double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)p_190076_7_;
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)(p_190076_1_ - d0), (float)(p_190076_3_ - d1) + 0.07F, (float)(p_190076_5_ - d2));
         GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.scalef(0.02F, -0.02F, 0.02F);
         RenderManager rendermanager = minecraft.getRenderManager();
         GlStateManager.rotatef(-rendermanager.playerViewY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef((float)(rendermanager.options.thirdPersonView == 2 ? 1 : -1) * rendermanager.playerViewX, 1.0F, 0.0F, 0.0F);
         GlStateManager.disableLighting();
         GlStateManager.enableTexture2D();
         GlStateManager.enableDepthTest();
         GlStateManager.depthMask(true);
         GlStateManager.scalef(-1.0F, 1.0F, 1.0F);
         fontrenderer.drawString(p_190076_0_, (float)(-fontrenderer.getStringWidth(p_190076_0_) / 2), 0.0F, p_190076_8_);
         GlStateManager.enableLighting();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface IDebugRenderer {
      void render(float p_190060_1_, long p_190060_2_);
   }
}
