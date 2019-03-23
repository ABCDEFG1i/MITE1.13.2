package net.minecraft.block.state;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;

public class BlockWorldState {
   private final IWorldReaderBase world;
   private final BlockPos pos;
   private final boolean forceLoad;
   private IBlockState state;
   private TileEntity tileEntity;
   private boolean tileEntityInitialized;

   public BlockWorldState(IWorldReaderBase p_i48968_1_, BlockPos p_i48968_2_, boolean p_i48968_3_) {
      this.world = p_i48968_1_;
      this.pos = p_i48968_2_;
      this.forceLoad = p_i48968_3_;
   }

   public IBlockState getBlockState() {
      if (this.state == null && (this.forceLoad || this.world.isBlockLoaded(this.pos))) {
         this.state = this.world.getBlockState(this.pos);
      }

      return this.state;
   }

   @Nullable
   public TileEntity getTileEntity() {
      if (this.tileEntity == null && !this.tileEntityInitialized) {
         this.tileEntity = this.world.getTileEntity(this.pos);
         this.tileEntityInitialized = true;
      }

      return this.tileEntity;
   }

   public IWorldReaderBase getWorld() {
      return this.world;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public static Predicate<BlockWorldState> hasState(Predicate<IBlockState> p_177510_0_) {
      return (p_201002_1_) -> {
         return p_201002_1_ != null && p_177510_0_.test(p_201002_1_.getBlockState());
      };
   }
}
