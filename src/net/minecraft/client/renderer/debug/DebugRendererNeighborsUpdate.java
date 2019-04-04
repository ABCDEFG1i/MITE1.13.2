package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugRendererNeighborsUpdate implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private final Map<Long, Map<BlockPos, Integer>> lastUpdate = Maps.newTreeMap(Ordering.natural().reverse());

   DebugRendererNeighborsUpdate(Minecraft p_i47365_1_) {
      this.minecraft = p_i47365_1_;
   }

   public void addUpdate(long p_191553_1_, BlockPos p_191553_3_) {
      Map<BlockPos, Integer> map = this.lastUpdate.get(p_191553_1_);
      if (map == null) {
         map = Maps.newHashMap();
         this.lastUpdate.put(p_191553_1_, map);
      }

      Integer integer = map.get(p_191553_3_);
      if (integer == null) {
         integer = 0;
      }

      map.put(p_191553_3_, integer + 1);
   }

   public void render(float p_190060_1_, long p_190060_2_) {
      long i = this.minecraft.world.getTotalWorldTime();
      EntityPlayer entityplayer = this.minecraft.player;
      double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)p_190060_1_;
      double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)p_190060_1_;
      double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)p_190060_1_;
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.lineWidth(2.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.depthMask(false);
      int j = 200;
      double d3 = 0.0025D;
      Set<BlockPos> set = Sets.newHashSet();
      Map<BlockPos, Integer> map = Maps.newHashMap();
      Iterator<Entry<Long, Map<BlockPos, Integer>>> iterator = this.lastUpdate.entrySet().iterator();

      while(iterator.hasNext()) {
         Entry<Long, Map<BlockPos, Integer>> entry = iterator.next();
         Long olong = entry.getKey();
         Map<BlockPos, Integer> map1 = entry.getValue();
         long k = i - olong;
         if (k > 200L) {
            iterator.remove();
         } else {
            for(Entry<BlockPos, Integer> entry1 : map1.entrySet()) {
               BlockPos blockpos = entry1.getKey();
               Integer integer = entry1.getValue();
               if (set.add(blockpos)) {
                  WorldRenderer.func_189697_a((new AxisAlignedBB(BlockPos.ORIGIN)).grow(0.002D).shrink(0.0025D * (double)k).offset((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ()).offset(-d0, -d1, -d2), 1.0F, 1.0F, 1.0F, 1.0F);
                  map.put(blockpos, integer);
               }
            }
         }
      }

      for(Entry<BlockPos, Integer> entry2 : map.entrySet()) {
         BlockPos blockpos1 = entry2.getKey();
         Integer integer1 = entry2.getValue();
         DebugRenderer.renderDebugText(String.valueOf(integer1), blockpos1.getX(), blockpos1.getY(), blockpos1.getZ(), p_190060_1_, -1);
      }

      GlStateManager.depthMask(true);
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }
}
