package net.minecraft.client.renderer.debug;

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
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugRendererStructure implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private final Map<Integer, Map<String, MutableBoundingBox>> mainBoxes = Maps.newHashMap();
   private final Map<Integer, Map<String, MutableBoundingBox>> subBoxes = Maps.newHashMap();
   private final Map<Integer, Map<String, Boolean>> subBoxFlags = Maps.newHashMap();

   public DebugRendererStructure(Minecraft p_i48764_1_) {
      this.minecraft = p_i48764_1_;
   }

   public void render(float p_190060_1_, long p_190060_2_) {
      EntityPlayer entityplayer = this.minecraft.player;
      IWorld iworld = this.minecraft.world;
      int i = iworld.getWorldInfo().getDimension();
      double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)p_190060_1_;
      double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)p_190060_1_;
      double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)p_190060_1_;
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture2D();
      GlStateManager.disableDepthTest();
      BlockPos blockpos = new BlockPos(entityplayer.posX, 0.0D, entityplayer.posZ);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
      GlStateManager.lineWidth(1.0F);
      if (this.mainBoxes.containsKey(i)) {
         for(MutableBoundingBox mutableboundingbox : this.mainBoxes.get(i).values()) {
            if (blockpos.getDistance(mutableboundingbox.minX, mutableboundingbox.minY, mutableboundingbox.minZ) < 500.0D) {
               WorldRenderer.func_189698_a(bufferbuilder, (double)mutableboundingbox.minX - d0, (double)mutableboundingbox.minY - d1, (double)mutableboundingbox.minZ - d2, (double)(mutableboundingbox.maxX + 1) - d0, (double)(mutableboundingbox.maxY + 1) - d1, (double)(mutableboundingbox.maxZ + 1) - d2, 1.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      }

      if (this.subBoxes.containsKey(i)) {
         for(Entry<String, MutableBoundingBox> entry : this.subBoxes.get(i).entrySet()) {
            String s = entry.getKey();
            MutableBoundingBox mutableboundingbox1 = entry.getValue();
            Boolean obool = this.subBoxFlags.get(i).get(s);
            if (blockpos.getDistance(mutableboundingbox1.minX, mutableboundingbox1.minY, mutableboundingbox1.minZ) < 500.0D) {
               if (obool) {
                  WorldRenderer.func_189698_a(bufferbuilder, (double)mutableboundingbox1.minX - d0, (double)mutableboundingbox1.minY - d1, (double)mutableboundingbox1.minZ - d2, (double)(mutableboundingbox1.maxX + 1) - d0, (double)(mutableboundingbox1.maxY + 1) - d1, (double)(mutableboundingbox1.maxZ + 1) - d2, 0.0F, 1.0F, 0.0F, 1.0F);
               } else {
                  WorldRenderer.func_189698_a(bufferbuilder, (double)mutableboundingbox1.minX - d0, (double)mutableboundingbox1.minY - d1, (double)mutableboundingbox1.minZ - d2, (double)(mutableboundingbox1.maxX + 1) - d0, (double)(mutableboundingbox1.maxY + 1) - d1, (double)(mutableboundingbox1.maxZ + 1) - d2, 0.0F, 0.0F, 1.0F, 1.0F);
               }
            }
         }
      }

      tessellator.draw();
      GlStateManager.enableDepthTest();
      GlStateManager.enableTexture2D();
      GlStateManager.popMatrix();
   }

   public void addStructure(MutableBoundingBox p_201729_1_, List<MutableBoundingBox> p_201729_2_, List<Boolean> p_201729_3_, int p_201729_4_) {
      if (!this.mainBoxes.containsKey(p_201729_4_)) {
         this.mainBoxes.put(p_201729_4_, Maps.newHashMap());
      }

      if (!this.subBoxes.containsKey(p_201729_4_)) {
         this.subBoxes.put(p_201729_4_, Maps.newHashMap());
         this.subBoxFlags.put(p_201729_4_, Maps.newHashMap());
      }

      this.mainBoxes.get(p_201729_4_).put(p_201729_1_.toString(), p_201729_1_);

      for(int i = 0; i < p_201729_2_.size(); ++i) {
         MutableBoundingBox mutableboundingbox = p_201729_2_.get(i);
         Boolean obool = p_201729_3_.get(i);
         this.subBoxes.get(p_201729_4_).put(mutableboundingbox.toString(), mutableboundingbox);
         this.subBoxFlags.get(p_201729_4_).put(mutableboundingbox.toString(), obool);
      }

   }
}
