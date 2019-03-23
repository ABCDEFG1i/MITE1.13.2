package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CompiledChunk {
   public static final CompiledChunk DUMMY = new CompiledChunk() {
      protected void setLayerUsed(BlockRenderLayer p_178486_1_) {
         throw new UnsupportedOperationException();
      }

      public void setLayerStarted(BlockRenderLayer p_178493_1_) {
         throw new UnsupportedOperationException();
      }

      public boolean isVisible(EnumFacing p_178495_1_, EnumFacing p_178495_2_) {
         return false;
      }
   };
   private final boolean[] layersUsed = new boolean[BlockRenderLayer.values().length];
   private final boolean[] layersStarted = new boolean[BlockRenderLayer.values().length];
   private boolean empty = true;
   private final List<TileEntity> tileEntities = Lists.newArrayList();
   private SetVisibility setVisibility = new SetVisibility();
   private BufferBuilder.State state;

   public boolean isEmpty() {
      return this.empty;
   }

   protected void setLayerUsed(BlockRenderLayer p_178486_1_) {
      this.empty = false;
      this.layersUsed[p_178486_1_.ordinal()] = true;
   }

   public boolean isLayerEmpty(BlockRenderLayer p_178491_1_) {
      return !this.layersUsed[p_178491_1_.ordinal()];
   }

   public void setLayerStarted(BlockRenderLayer p_178493_1_) {
      this.layersStarted[p_178493_1_.ordinal()] = true;
   }

   public boolean isLayerStarted(BlockRenderLayer p_178492_1_) {
      return this.layersStarted[p_178492_1_.ordinal()];
   }

   public List<TileEntity> getTileEntities() {
      return this.tileEntities;
   }

   public void addTileEntity(TileEntity p_178490_1_) {
      this.tileEntities.add(p_178490_1_);
   }

   public boolean isVisible(EnumFacing p_178495_1_, EnumFacing p_178495_2_) {
      return this.setVisibility.isVisible(p_178495_1_, p_178495_2_);
   }

   public void setVisibility(SetVisibility p_178488_1_) {
      this.setVisibility = p_178488_1_;
   }

   public BufferBuilder.State getState() {
      return this.state;
   }

   public void setState(BufferBuilder.State p_178494_1_) {
      this.state = p_178494_1_;
   }
}
