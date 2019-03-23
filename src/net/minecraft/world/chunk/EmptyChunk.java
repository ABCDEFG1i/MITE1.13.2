package net.minecraft.world.chunk;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EmptyChunk extends Chunk {
   private static final Biome[] BIOMES = Util.make(new Biome[256], (p_203406_0_) -> {
      Arrays.fill(p_203406_0_, Biomes.PLAINS);
   });

   public EmptyChunk(World p_i1994_1_, int p_i1994_2_, int p_i1994_3_) {
      super(p_i1994_1_, p_i1994_2_, p_i1994_3_, BIOMES);
   }

   public boolean isAtLocation(int p_76600_1_, int p_76600_2_) {
      return p_76600_1_ == this.x && p_76600_2_ == this.z;
   }

   public void generateHeightMap() {
   }

   public void generateSkylightMap() {
   }

   public IBlockState getBlockState(BlockPos p_180495_1_) {
      return Blocks.VOID_AIR.getDefaultState();
   }

   public int getLight(EnumLightType p_201587_1_, BlockPos p_201587_2_, boolean p_201587_3_) {
      return p_201587_1_.defaultLightValue;
   }

   public void setLightFor(EnumLightType p_201580_1_, boolean p_201580_2_, BlockPos p_201580_3_, int p_201580_4_) {
   }

   public int getLightSubtracted(BlockPos p_201586_1_, int p_201586_2_, boolean p_201586_3_) {
      return 0;
   }

   public void addEntity(Entity p_76612_1_) {
   }

   public void removeEntity(Entity p_76622_1_) {
   }

   public void removeEntityAtIndex(Entity p_76608_1_, int p_76608_2_) {
   }

   public boolean canSeeSky(BlockPos p_177444_1_) {
      return false;
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos p_177424_1_, Chunk.EnumCreateEntityType p_177424_2_) {
      return null;
   }

   public void addTileEntity(TileEntity p_150813_1_) {
   }

   public void addTileEntity(BlockPos p_177426_1_, TileEntity p_177426_2_) {
   }

   public void removeTileEntity(BlockPos p_177425_1_) {
   }

   public void onLoad() {
   }

   public void onUnload() {
   }

   public void markDirty() {
   }

   public void getEntitiesWithinAABBForEntity(@Nullable Entity p_177414_1_, AxisAlignedBB p_177414_2_, List<Entity> p_177414_3_, Predicate<? super Entity> p_177414_4_) {
   }

   public <T extends Entity> void getEntitiesOfTypeWithinAABB(Class<? extends T> p_177430_1_, AxisAlignedBB p_177430_2_, List<T> p_177430_3_, Predicate<? super T> p_177430_4_) {
   }

   public boolean needsSaving(boolean p_76601_1_) {
      return false;
   }

   public boolean isEmpty() {
      return true;
   }

   public boolean isEmptyBetween(int p_76606_1_, int p_76606_2_) {
      return true;
   }
}
