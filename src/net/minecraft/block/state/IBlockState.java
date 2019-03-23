package net.minecraft.block.state;

import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IStateHolder;
import net.minecraft.tags.Tag;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IBlockState extends IStateHolder<IBlockState> {
   ThreadLocal<Object2ByteMap<IBlockState>> field_208776_a = ThreadLocal.withInitial(() -> {
      Object2ByteOpenHashMap<IBlockState> object2byteopenhashmap = new Object2ByteOpenHashMap<>();
      object2byteopenhashmap.defaultReturnValue((byte)127);
      return object2byteopenhashmap;
   });
   ThreadLocal<Object2ByteMap<IBlockState>> OPACITY_CACHE = ThreadLocal.withInitial(() -> {
      Object2ByteOpenHashMap<IBlockState> object2byteopenhashmap = new Object2ByteOpenHashMap<>();
      object2byteopenhashmap.defaultReturnValue((byte)127);
      return object2byteopenhashmap;
   });
   ThreadLocal<Object2ByteMap<IBlockState>> OPAQUE_CUBE_CACHE = ThreadLocal.withInitial(() -> {
      Object2ByteOpenHashMap<IBlockState> object2byteopenhashmap = new Object2ByteOpenHashMap<>();
      object2byteopenhashmap.defaultReturnValue((byte)127);
      return object2byteopenhashmap;
   });

   Block getBlock();

   default Material getMaterial() {
      return this.getBlock().getMaterial(this);
   }

   default boolean canEntitySpawn(Entity p_189884_1_) {
      return this.getBlock().canEntitySpawn(this, p_189884_1_);
   }

   default boolean func_200131_a(IBlockReader p_200131_1_, BlockPos p_200131_2_) {
      Block block = this.getBlock();
      Object2ByteMap<IBlockState> object2bytemap = block.isVariableOpacity() ? null : field_208776_a.get();
      if (object2bytemap != null) {
         byte b0 = object2bytemap.getByte(this);
         if (b0 != object2bytemap.defaultReturnValue()) {
            return b0 != 0;
         }
      }

      boolean flag = block.func_200123_i(this, p_200131_1_, p_200131_2_);
      if (object2bytemap != null) {
         object2bytemap.put(this, (byte)(flag ? 1 : 0));
      }

      return flag;
   }

   default int getOpacity(IBlockReader p_200016_1_, BlockPos p_200016_2_) {
      Block block = this.getBlock();
      Object2ByteMap<IBlockState> object2bytemap = block.isVariableOpacity() ? null : OPACITY_CACHE.get();
      if (object2bytemap != null) {
         byte b0 = object2bytemap.getByte(this);
         if (b0 != object2bytemap.defaultReturnValue()) {
            return b0;
         }
      }

      int i = block.getOpacity(this, p_200016_1_, p_200016_2_);
      if (object2bytemap != null) {
         object2bytemap.put(this, (byte)Math.min(i, p_200016_1_.getMaxLightLevel()));
      }

      return i;
   }

   default int getLightValue() {
      return this.getBlock().getLightValue(this);
   }

   default boolean isAir() {
      return this.getBlock().isAir(this);
   }

   default boolean useNeighborBrightness(IBlockReader p_200130_1_, BlockPos p_200130_2_) {
      return this.getBlock().useNeighborBrightness(this, p_200130_1_, p_200130_2_);
   }

   default MaterialColor func_185909_g(IBlockReader p_185909_1_, BlockPos p_185909_2_) {
      return this.getBlock().func_180659_g(this, p_185909_1_, p_185909_2_);
   }

   default IBlockState rotate(Rotation p_185907_1_) {
      return this.getBlock().rotate(this, p_185907_1_);
   }

   default IBlockState mirror(Mirror p_185902_1_) {
      return this.getBlock().mirror(this, p_185902_1_);
   }

   default boolean isFullCube() {
      return this.getBlock().isFullCube(this);
   }

   @OnlyIn(Dist.CLIENT)
   default boolean hasCustomBreakingProgress() {
      return this.getBlock().hasCustomBreakingProgress(this);
   }

   default EnumBlockRenderType getRenderType() {
      return this.getBlock().getRenderType(this);
   }

   @OnlyIn(Dist.CLIENT)
   default int getPackedLightmapCoords(IWorldReader p_185889_1_, BlockPos p_185889_2_) {
      return this.getBlock().getPackedLightmapCoords(this, p_185889_1_, p_185889_2_);
   }

   @OnlyIn(Dist.CLIENT)
   default float getAmbientOcclusionLightValue() {
      return this.getBlock().getAmbientOcclusionLightValue(this);
   }

   default boolean isBlockNormalCube() {
      return this.getBlock().isBlockNormalCube(this);
   }

   default boolean isNormalCube() {
      return this.getBlock().isNormalCube(this);
   }

   default boolean canProvidePower() {
      return this.getBlock().canProvidePower(this);
   }

   default int getWeakPower(IBlockReader p_185911_1_, BlockPos p_185911_2_, EnumFacing p_185911_3_) {
      return this.getBlock().getWeakPower(this, p_185911_1_, p_185911_2_, p_185911_3_);
   }

   default boolean hasComparatorInputOverride() {
      return this.getBlock().hasComparatorInputOverride(this);
   }

   default int getComparatorInputOverride(World p_185888_1_, BlockPos p_185888_2_) {
      return this.getBlock().getComparatorInputOverride(this, p_185888_1_, p_185888_2_);
   }

   default float getBlockHardness(IBlockReader p_185887_1_, BlockPos p_185887_2_) {
      return this.getBlock().getBlockHardness(this, p_185887_1_, p_185887_2_);
   }

   default float getPlayerRelativeBlockHardness(EntityPlayer p_185903_1_, IBlockReader p_185903_2_, BlockPos p_185903_3_) {
      return this.getBlock().getPlayerRelativeBlockHardness(this, p_185903_1_, p_185903_2_, p_185903_3_);
   }

   default int getStrongPower(IBlockReader p_185893_1_, BlockPos p_185893_2_, EnumFacing p_185893_3_) {
      return this.getBlock().getStrongPower(this, p_185893_1_, p_185893_2_, p_185893_3_);
   }

   default EnumPushReaction getPushReaction() {
      return this.getBlock().getPushReaction(this);
   }

   default boolean isOpaqueCube(IBlockReader p_200015_1_, BlockPos p_200015_2_) {
      Block block = this.getBlock();
      Object2ByteMap<IBlockState> object2bytemap = block.isVariableOpacity() ? null : OPAQUE_CUBE_CACHE.get();
      if (object2bytemap != null) {
         byte b0 = object2bytemap.getByte(this);
         if (b0 != object2bytemap.defaultReturnValue()) {
            return b0 != 0;
         }
      }

      boolean flag = block.isOpaqueCube(this, p_200015_1_, p_200015_2_);
      if (object2bytemap != null) {
         object2bytemap.put(this, (byte)(flag ? 1 : 0));
      }

      return flag;
   }

   default boolean isSolid() {
      return this.getBlock().isSolid(this);
   }

   @OnlyIn(Dist.CLIENT)
   default boolean isSideInvisible(IBlockState p_200017_1_, EnumFacing p_200017_2_) {
      return this.getBlock().isSideInvisible(this, p_200017_1_, p_200017_2_);
   }

   default VoxelShape getShape(IBlockReader p_196954_1_, BlockPos p_196954_2_) {
      return this.getBlock().getShape(this, p_196954_1_, p_196954_2_);
   }

   default VoxelShape getCollisionShape(IBlockReader p_196952_1_, BlockPos p_196952_2_) {
      return this.getBlock().getCollisionShape(this, p_196952_1_, p_196952_2_);
   }

   default VoxelShape getRenderShape(IBlockReader p_196951_1_, BlockPos p_196951_2_) {
      return this.getBlock().getRenderShape(this, p_196951_1_, p_196951_2_);
   }

   default VoxelShape getRaytraceShape(IBlockReader p_199611_1_, BlockPos p_199611_2_) {
      return this.getBlock().getRaytraceShape(this, p_199611_1_, p_199611_2_);
   }

   default boolean isTopSolid() {
      return this.getBlock().isTopSolid(this);
   }

   default Vec3d getOffset(IBlockReader p_191059_1_, BlockPos p_191059_2_) {
      return this.getBlock().getOffset(this, p_191059_1_, p_191059_2_);
   }

   default boolean onBlockEventReceived(World p_189547_1_, BlockPos p_189547_2_, int p_189547_3_, int p_189547_4_) {
      return this.getBlock().eventReceived(this, p_189547_1_, p_189547_2_, p_189547_3_, p_189547_4_);
   }

   default void neighborChanged(World p_189546_1_, BlockPos p_189546_2_, Block p_189546_3_, BlockPos p_189546_4_) {
      this.getBlock().neighborChanged(this, p_189546_1_, p_189546_2_, p_189546_3_, p_189546_4_);
   }

   default void updateNeighbors(IWorld p_196946_1_, BlockPos p_196946_2_, int p_196946_3_) {
      this.getBlock().updateNeighbors(this, p_196946_1_, p_196946_2_, p_196946_3_);
   }

   default void updateDiagonalNeighbors(IWorld p_196948_1_, BlockPos p_196948_2_, int p_196948_3_) {
      this.getBlock().updateDiagonalNeighbors(this, p_196948_1_, p_196948_2_, p_196948_3_);
   }

   default void onBlockAdded(World p_196945_1_, BlockPos p_196945_2_, IBlockState p_196945_3_) {
      this.getBlock().onBlockAdded(this, p_196945_1_, p_196945_2_, p_196945_3_);
   }

   default void onReplaced(World p_196947_1_, BlockPos p_196947_2_, IBlockState p_196947_3_, boolean p_196947_4_) {
      this.getBlock().onReplaced(this, p_196947_1_, p_196947_2_, p_196947_3_, p_196947_4_);
   }

   default void tick(World p_196940_1_, BlockPos p_196940_2_, Random p_196940_3_) {
      this.getBlock().tick(this, p_196940_1_, p_196940_2_, p_196940_3_);
   }

   default void randomTick(World p_196944_1_, BlockPos p_196944_2_, Random p_196944_3_) {
      this.getBlock().randomTick(this, p_196944_1_, p_196944_2_, p_196944_3_);
   }

   default void onEntityCollision(World p_196950_1_, BlockPos p_196950_2_, Entity p_196950_3_) {
      this.getBlock().onEntityCollision(this, p_196950_1_, p_196950_2_, p_196950_3_);
   }

   default void dropBlockAsItem(World p_196949_1_, BlockPos p_196949_2_, int p_196949_3_) {
      this.dropBlockAsItemWithChance(p_196949_1_, p_196949_2_, 1.0F, p_196949_3_);
   }

   default void dropBlockAsItemWithChance(World p_196941_1_, BlockPos p_196941_2_, float p_196941_3_, int p_196941_4_) {
      this.getBlock().dropBlockAsItemWithChance(this, p_196941_1_, p_196941_2_, p_196941_3_, p_196941_4_);
   }

   default boolean onBlockActivated(World p_196943_1_, BlockPos p_196943_2_, EntityPlayer p_196943_3_, EnumHand p_196943_4_, EnumFacing p_196943_5_, float p_196943_6_, float p_196943_7_, float p_196943_8_) {
      return this.getBlock().onBlockActivated(this, p_196943_1_, p_196943_2_, p_196943_3_, p_196943_4_, p_196943_5_, p_196943_6_, p_196943_7_, p_196943_8_);
   }

   default void onBlockClicked(World p_196942_1_, BlockPos p_196942_2_, EntityPlayer p_196942_3_) {
      this.getBlock().onBlockClicked(this, p_196942_1_, p_196942_2_, p_196942_3_);
   }

   default boolean causesSuffocation() {
      return this.getBlock().causesSuffocation(this);
   }

   default BlockFaceShape getBlockFaceShape(IBlockReader p_193401_1_, BlockPos p_193401_2_, EnumFacing p_193401_3_) {
      return this.getBlock().getBlockFaceShape(p_193401_1_, this, p_193401_2_, p_193401_3_);
   }

   default IBlockState updatePostPlacement(EnumFacing p_196956_1_, IBlockState p_196956_2_, IWorld p_196956_3_, BlockPos p_196956_4_, BlockPos p_196956_5_) {
      return this.getBlock().updatePostPlacement(this, p_196956_1_, p_196956_2_, p_196956_3_, p_196956_4_, p_196956_5_);
   }

   default boolean allowsMovement(IBlockReader p_196957_1_, BlockPos p_196957_2_, PathType p_196957_3_) {
      return this.getBlock().allowsMovement(this, p_196957_1_, p_196957_2_, p_196957_3_);
   }

   default boolean isReplaceable(BlockItemUseContext p_196953_1_) {
      return this.getBlock().isReplaceable(this, p_196953_1_);
   }

   default boolean isValidPosition(IWorldReaderBase p_196955_1_, BlockPos p_196955_2_) {
      return this.getBlock().isValidPosition(this, p_196955_1_, p_196955_2_);
   }

   default boolean blockNeedsPostProcessing(IBlockReader p_202065_1_, BlockPos p_202065_2_) {
      return this.getBlock().needsPostProcessing(this, p_202065_1_, p_202065_2_);
   }

   default boolean isIn(Tag<Block> p_203425_1_) {
      return this.getBlock().isIn(p_203425_1_);
   }

   default IFluidState getFluidState() {
      return this.getBlock().getFluidState(this);
   }

   default boolean needsRandomTick() {
      return this.getBlock().getTickRandomly(this);
   }

   @OnlyIn(Dist.CLIENT)
   default long getPositionRandom(BlockPos p_209533_1_) {
      return this.getBlock().getPositionRandom(this, p_209533_1_);
   }
}
