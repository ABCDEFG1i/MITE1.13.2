package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Sets;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderChunk {
   private volatile World world;
   private final WorldRenderer renderGlobal;
   public static int renderChunksUpdated;
   public CompiledChunk compiledChunk = CompiledChunk.DUMMY;
   private final ReentrantLock lockCompileTask = new ReentrantLock();
   private final ReentrantLock lockCompiledChunk = new ReentrantLock();
   private ChunkRenderTask compileTask;
   private final Set<TileEntity> setTileEntities = Sets.newHashSet();
   private final FloatBuffer modelviewMatrix = GLAllocation.createDirectFloatBuffer(16);
   private final VertexBuffer[] vertexBuffers = new VertexBuffer[BlockRenderLayer.values().length];
   public AxisAlignedBB boundingBox;
   private int frameIndex = -1;
   private boolean needsUpdate = true;
   private final BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos(-1, -1, -1);
   private final BlockPos.MutableBlockPos[] mapEnumFacing = Util.make(new BlockPos.MutableBlockPos[6], (p_205125_0_) -> {
      for(int j = 0; j < p_205125_0_.length; ++j) {
         p_205125_0_[j] = new BlockPos.MutableBlockPos();
      }

   });
   private boolean needsImmediateUpdate;

   public RenderChunk(World p_i49841_1_, WorldRenderer p_i49841_2_) {
      this.world = p_i49841_1_;
      this.renderGlobal = p_i49841_2_;
      if (OpenGlHelper.useVbo()) {
         for(int i = 0; i < BlockRenderLayer.values().length; ++i) {
            this.vertexBuffers[i] = new VertexBuffer(DefaultVertexFormats.BLOCK);
         }
      }

   }

   public boolean setFrameIndex(int p_178577_1_) {
      if (this.frameIndex == p_178577_1_) {
         return false;
      } else {
         this.frameIndex = p_178577_1_;
         return true;
      }
   }

   public VertexBuffer getVertexBufferByLayer(int p_178565_1_) {
      return this.vertexBuffers[p_178565_1_];
   }

   public void setPosition(int p_189562_1_, int p_189562_2_, int p_189562_3_) {
      if (p_189562_1_ != this.position.getX() || p_189562_2_ != this.position.getY() || p_189562_3_ != this.position.getZ()) {
         this.stopCompileTask();
         this.position.setPos(p_189562_1_, p_189562_2_, p_189562_3_);
         this.boundingBox = new AxisAlignedBB((double)p_189562_1_, (double)p_189562_2_, (double)p_189562_3_, (double)(p_189562_1_ + 16), (double)(p_189562_2_ + 16), (double)(p_189562_3_ + 16));

         for(EnumFacing enumfacing : EnumFacing.values()) {
            this.mapEnumFacing[enumfacing.ordinal()].setPos(this.position).move(enumfacing, 16);
         }

         this.initModelviewMatrix();
      }
   }

   public void resortTransparency(float p_178570_1_, float p_178570_2_, float p_178570_3_, ChunkRenderTask p_178570_4_) {
      CompiledChunk compiledchunk = p_178570_4_.getCompiledChunk();
      if (compiledchunk.getState() != null && !compiledchunk.isLayerEmpty(BlockRenderLayer.TRANSLUCENT)) {
         this.preRenderBlocks(p_178570_4_.getRegionRenderCacheBuilder().getBuilder(BlockRenderLayer.TRANSLUCENT), this.position);
         p_178570_4_.getRegionRenderCacheBuilder().getBuilder(BlockRenderLayer.TRANSLUCENT).setVertexState(compiledchunk.getState());
         this.postRenderBlocks(BlockRenderLayer.TRANSLUCENT, p_178570_1_, p_178570_2_, p_178570_3_, p_178570_4_.getRegionRenderCacheBuilder().getBuilder(BlockRenderLayer.TRANSLUCENT), compiledchunk);
      }
   }

   public void rebuildChunk(float p_178581_1_, float p_178581_2_, float p_178581_3_, ChunkRenderTask p_178581_4_) {
      CompiledChunk compiledchunk = new CompiledChunk();
      int i = 1;
      BlockPos blockpos = this.position.toImmutable();
      BlockPos blockpos1 = blockpos.add(15, 15, 15);
      World world = this.world;
      if (world != null) {
         p_178581_4_.getLock().lock();

         try {
            if (p_178581_4_.getStatus() != ChunkRenderTask.Status.COMPILING) {
               return;
            }

            p_178581_4_.setCompiledChunk(compiledchunk);
         } finally {
            p_178581_4_.getLock().unlock();
         }

         RenderChunkCache lvt_10_1_ = RenderChunkCache.func_212397_a(world, blockpos.add(-1, -1, -1), blockpos.add(16, 16, 16), 1);
         VisGraph lvt_11_1_ = new VisGraph();
         HashSet lvt_12_1_ = Sets.newHashSet();
         if (lvt_10_1_ != null) {
            ++renderChunksUpdated;
            boolean[] aboolean = new boolean[BlockRenderLayer.values().length];
            BlockModelRenderer.func_211847_a();
            Random random = new Random();
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();

            for(BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(blockpos, blockpos1)) {
               IBlockState iblockstate = lvt_10_1_.getBlockState(blockpos$mutableblockpos);
               Block block = iblockstate.getBlock();
               if (iblockstate.isOpaqueCube(lvt_10_1_, blockpos$mutableblockpos)) {
                  lvt_11_1_.setOpaqueCube(blockpos$mutableblockpos);
               }

               if (block.hasTileEntity()) {
                  TileEntity tileentity = lvt_10_1_.func_212399_a(blockpos$mutableblockpos, Chunk.EnumCreateEntityType.CHECK);
                  if (tileentity != null) {
                     TileEntityRenderer<TileEntity> tileentityrenderer = TileEntityRendererDispatcher.instance.getRenderer(tileentity);
                     if (tileentityrenderer != null) {
                        compiledchunk.addTileEntity(tileentity);
                        if (tileentityrenderer.isGlobalRenderer(tileentity)) {
                           lvt_12_1_.add(tileentity);
                        }
                     }
                  }
               }

               IFluidState ifluidstate = lvt_10_1_.getFluidState(blockpos$mutableblockpos);
               if (!ifluidstate.isEmpty()) {
                  BlockRenderLayer blockrenderlayer1 = ifluidstate.getRenderLayer();
                  int j = blockrenderlayer1.ordinal();
                  BufferBuilder bufferbuilder = p_178581_4_.getRegionRenderCacheBuilder().getBuilder(j);
                  if (!compiledchunk.isLayerStarted(blockrenderlayer1)) {
                     compiledchunk.setLayerStarted(blockrenderlayer1);
                     this.preRenderBlocks(bufferbuilder, blockpos);
                  }

                  aboolean[j] |= blockrendererdispatcher.func_205318_a(blockpos$mutableblockpos, lvt_10_1_, bufferbuilder, ifluidstate);
               }

               if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
                  BlockRenderLayer blockrenderlayer2 = block.getRenderLayer();
                  int k = blockrenderlayer2.ordinal();
                  BufferBuilder bufferbuilder1 = p_178581_4_.getRegionRenderCacheBuilder().getBuilder(k);
                  if (!compiledchunk.isLayerStarted(blockrenderlayer2)) {
                     compiledchunk.setLayerStarted(blockrenderlayer2);
                     this.preRenderBlocks(bufferbuilder1, blockpos);
                  }

                  aboolean[k] |= blockrendererdispatcher.func_195475_a(iblockstate, blockpos$mutableblockpos, lvt_10_1_, bufferbuilder1, random);
               }
            }

            for(BlockRenderLayer blockrenderlayer : BlockRenderLayer.values()) {
               if (aboolean[blockrenderlayer.ordinal()]) {
                  compiledchunk.setLayerUsed(blockrenderlayer);
               }

               if (compiledchunk.isLayerStarted(blockrenderlayer)) {
                  this.postRenderBlocks(blockrenderlayer, p_178581_1_, p_178581_2_, p_178581_3_, p_178581_4_.getRegionRenderCacheBuilder().getBuilder(blockrenderlayer), compiledchunk);
               }
            }

            BlockModelRenderer.func_210266_a();
         }

         compiledchunk.setVisibility(lvt_11_1_.computeVisibility());
         this.lockCompileTask.lock();

         try {
            Set<TileEntity> set = Sets.newHashSet(lvt_12_1_);
            Set<TileEntity> set1 = Sets.newHashSet(this.setTileEntities);
            set.removeAll(this.setTileEntities);
            set1.removeAll(lvt_12_1_);
            this.setTileEntities.clear();
            this.setTileEntities.addAll(lvt_12_1_);
            this.renderGlobal.func_181023_a(set1, set);
         } finally {
            this.lockCompileTask.unlock();
         }

      }
   }

   protected void finishCompileTask() {
      this.lockCompileTask.lock();

      try {
         if (this.compileTask != null && this.compileTask.getStatus() != ChunkRenderTask.Status.DONE) {
            this.compileTask.finish();
            this.compileTask = null;
         }
      } finally {
         this.lockCompileTask.unlock();
      }

   }

   public ReentrantLock getLockCompileTask() {
      return this.lockCompileTask;
   }

   public ChunkRenderTask makeCompileTaskChunk() {
      this.lockCompileTask.lock();

      ChunkRenderTask chunkrendertask;
      try {
         this.finishCompileTask();
         this.compileTask = new ChunkRenderTask(this, ChunkRenderTask.Type.REBUILD_CHUNK, this.getDistanceSq());
         chunkrendertask = this.compileTask;
      } finally {
         this.lockCompileTask.unlock();
      }

      return chunkrendertask;
   }

   @Nullable
   public ChunkRenderTask makeCompileTaskTransparency() {
      this.lockCompileTask.lock();

      ChunkRenderTask chunkrendertask;
      try {
         if (this.compileTask == null || this.compileTask.getStatus() != ChunkRenderTask.Status.PENDING) {
            if (this.compileTask != null && this.compileTask.getStatus() != ChunkRenderTask.Status.DONE) {
               this.compileTask.finish();
               this.compileTask = null;
            }

            this.compileTask = new ChunkRenderTask(this, ChunkRenderTask.Type.RESORT_TRANSPARENCY, this.getDistanceSq());
            this.compileTask.setCompiledChunk(this.compiledChunk);
            chunkrendertask = this.compileTask;
            return chunkrendertask;
         }

         chunkrendertask = null;
      } finally {
         this.lockCompileTask.unlock();
      }

      return chunkrendertask;
   }

   protected double getDistanceSq() {
      EntityPlayerSP entityplayersp = Minecraft.getInstance().player;
      double d0 = this.boundingBox.minX + 8.0D - entityplayersp.posX;
      double d1 = this.boundingBox.minY + 8.0D - entityplayersp.posY;
      double d2 = this.boundingBox.minZ + 8.0D - entityplayersp.posZ;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   private void preRenderBlocks(BufferBuilder p_178573_1_, BlockPos p_178573_2_) {
      p_178573_1_.begin(7, DefaultVertexFormats.BLOCK);
      p_178573_1_.setTranslation((double)(-p_178573_2_.getX()), (double)(-p_178573_2_.getY()), (double)(-p_178573_2_.getZ()));
   }

   private void postRenderBlocks(BlockRenderLayer p_178584_1_, float p_178584_2_, float p_178584_3_, float p_178584_4_, BufferBuilder p_178584_5_, CompiledChunk p_178584_6_) {
      if (p_178584_1_ == BlockRenderLayer.TRANSLUCENT && !p_178584_6_.isLayerEmpty(p_178584_1_)) {
         p_178584_5_.sortVertexData(p_178584_2_, p_178584_3_, p_178584_4_);
         p_178584_6_.setState(p_178584_5_.getVertexState());
      }

      p_178584_5_.finishDrawing();
   }

   private void initModelviewMatrix() {
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      float f = 1.000001F;
      GlStateManager.translatef(-8.0F, -8.0F, -8.0F);
      GlStateManager.scalef(1.000001F, 1.000001F, 1.000001F);
      GlStateManager.translatef(8.0F, 8.0F, 8.0F);
      GlStateManager.getFloatv(2982, this.modelviewMatrix);
      GlStateManager.popMatrix();
   }

   public void multModelviewMatrix() {
      GlStateManager.multMatrixf(this.modelviewMatrix);
   }

   public CompiledChunk getCompiledChunk() {
      return this.compiledChunk;
   }

   public void setCompiledChunk(CompiledChunk p_178580_1_) {
      this.lockCompiledChunk.lock();

      try {
         this.compiledChunk = p_178580_1_;
      } finally {
         this.lockCompiledChunk.unlock();
      }

   }

   public void stopCompileTask() {
      this.finishCompileTask();
      this.compiledChunk = CompiledChunk.DUMMY;
   }

   public void deleteGlResources() {
      this.stopCompileTask();
      this.world = null;

      for(int i = 0; i < BlockRenderLayer.values().length; ++i) {
         if (this.vertexBuffers[i] != null) {
            this.vertexBuffers[i].deleteGlBuffers();
         }
      }

   }

   public BlockPos getPosition() {
      return this.position;
   }

   public void setNeedsUpdate(boolean p_178575_1_) {
      if (this.needsUpdate) {
         p_178575_1_ |= this.needsImmediateUpdate;
      }

      this.needsUpdate = true;
      this.needsImmediateUpdate = p_178575_1_;
   }

   public void clearNeedsUpdate() {
      this.needsUpdate = false;
      this.needsImmediateUpdate = false;
   }

   public boolean needsUpdate() {
      return this.needsUpdate;
   }

   public boolean needsImmediateUpdate() {
      return this.needsUpdate && this.needsImmediateUpdate;
   }

   public BlockPos getBlockPosOffset16(EnumFacing p_181701_1_) {
      return this.mapEnumFacing[p_181701_1_.ordinal()];
   }

   public World getWorld() {
      return this.world;
   }
}
