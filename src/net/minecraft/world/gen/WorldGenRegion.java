package net.minecraft.world.gen;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.ITickList;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldGenTickList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenRegion implements IWorld {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ChunkPrimer[] chunkPrimers;
   private final int mainChunkX;
   private final int mainChunkZ;
   private final int sizeX;
   private final int sizeZ;
   private final World world;
   private final long seed;
   private final int seaLevel;
   private final WorldInfo worldInfo;
   private final Random random;
   private final Dimension dimension;
   private final IChunkGenSettings chunkGenSettings;
   private final ITickList<Block> pendingBlockTickList = new WorldGenTickList<>((p_205335_1_) -> {
      return this.getChunkDefault(p_205335_1_).getBlocksToBeTicked();
   });
   private final ITickList<Fluid> pendingFluidTickList = new WorldGenTickList<>((p_205334_1_) -> {
      return this.getChunkDefault(p_205334_1_).func_212247_j();
   });

   public WorldGenRegion(ChunkPrimer[] p_i49387_1_, int p_i49387_2_, int p_i49387_3_, int p_i49387_4_, int p_i49387_5_, World p_i49387_6_) {
      this.chunkPrimers = p_i49387_1_;
      this.mainChunkX = p_i49387_4_;
      this.mainChunkZ = p_i49387_5_;
      this.sizeX = p_i49387_2_;
      this.sizeZ = p_i49387_3_;
      this.world = p_i49387_6_;
      this.seed = p_i49387_6_.getSeed();
      this.chunkGenSettings = p_i49387_6_.getChunkProvider().getChunkGenerator().getSettings();
      this.seaLevel = p_i49387_6_.getSeaLevel();
      this.worldInfo = p_i49387_6_.getWorldInfo();
      this.random = p_i49387_6_.getRandom();
      this.dimension = p_i49387_6_.getDimension();
   }

   public int getMainChunkX() {
      return this.mainChunkX;
   }

   public int getMainChunkZ() {
      return this.mainChunkZ;
   }

   public boolean isChunkInBounds(int p_201678_1_, int p_201678_2_) {
      IChunk ichunk = this.chunkPrimers[0];
      IChunk ichunk1 = this.chunkPrimers[this.chunkPrimers.length - 1];
      return p_201678_1_ >= ichunk.getPos().x && p_201678_1_ <= ichunk1.getPos().x && p_201678_2_ >= ichunk.getPos().z && p_201678_2_ <= ichunk1.getPos().z;
   }

   public IChunk getChunk(int p_72964_1_, int p_72964_2_) {
      if (this.isChunkInBounds(p_72964_1_, p_72964_2_)) {
         int i = p_72964_1_ - this.chunkPrimers[0].getPos().x;
         int j = p_72964_2_ - this.chunkPrimers[0].getPos().z;
         return this.chunkPrimers[i + j * this.sizeX];
      } else {
         IChunk ichunk = this.chunkPrimers[0];
         IChunk ichunk1 = this.chunkPrimers[this.chunkPrimers.length - 1];
         LOGGER.error("Requested chunk : {} {}", p_72964_1_, p_72964_2_);
         LOGGER.error("Region bounds : {} {} | {} {}", ichunk.getPos().x, ichunk.getPos().z, ichunk1.getPos().x, ichunk1.getPos().z);
         throw new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", p_72964_1_, p_72964_2_));
      }
   }

   public IBlockState getBlockState(BlockPos p_180495_1_) {
      return this.getChunkDefault(p_180495_1_).getBlockState(p_180495_1_);
   }

   public IFluidState getFluidState(BlockPos p_204610_1_) {
      return this.getChunkDefault(p_204610_1_).getFluidState(p_204610_1_);
   }

   @Nullable
   public EntityPlayer getClosestPlayer(double p_190525_1_, double p_190525_3_, double p_190525_5_, double p_190525_7_, Predicate<Entity> p_190525_9_) {
      return null;
   }

   public int getSkylightSubtracted() {
      return 0;
   }

   public boolean isAirBlock(BlockPos p_175623_1_) {
      return this.getBlockState(p_175623_1_).isAir();
   }

   public Biome getBiome(BlockPos p_180494_1_) {
      Biome biome = this.getChunkDefault(p_180494_1_).getBiomes()[p_180494_1_.getX() & 15 | (p_180494_1_.getZ() & 15) << 4];
      if (biome == null) {
         throw new RuntimeException(String.format("Biome is null @ %s", p_180494_1_));
      } else {
         return biome;
      }
   }

   public int getLightFor(EnumLightType p_175642_1_, BlockPos p_175642_2_) {
      IChunk ichunk = this.getChunkDefault(p_175642_2_);
      return ichunk.getLight(p_175642_1_, p_175642_2_, this.getDimension().hasSkyLight());
   }

   public int getLightSubtracted(BlockPos p_201669_1_, int p_201669_2_) {
      return this.getChunkDefault(p_201669_1_).getLightSubtracted(p_201669_1_, p_201669_2_, this.getDimension().hasSkyLight());
   }

   public boolean isChunkLoaded(int p_175680_1_, int p_175680_2_, boolean p_175680_3_) {
      return this.isChunkInBounds(p_175680_1_, p_175680_2_);
   }

   public boolean destroyBlock(BlockPos p_175655_1_, boolean p_175655_2_) {
      IBlockState iblockstate = this.getBlockState(p_175655_1_);
      if (iblockstate.isAir()) {
         return false;
      } else {
         if (p_175655_2_) {
            iblockstate.dropBlockAsItem(this.world, p_175655_1_, 0);
         }

         return this.setBlockState(p_175655_1_, Blocks.AIR.getDefaultState(), 3);
      }
   }

   public boolean canSeeSky(BlockPos p_175678_1_) {
      return this.getChunkDefault(p_175678_1_).canSeeSky(p_175678_1_);
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos p_175625_1_) {
      IChunk ichunk = this.getChunkDefault(p_175625_1_);
      TileEntity tileentity = ichunk.getTileEntity(p_175625_1_);
      if (tileentity != null) {
         return tileentity;
      } else {
         NBTTagCompound nbttagcompound = ichunk.getDeferredTileEntity(p_175625_1_);
         if (nbttagcompound != null) {
            if ("DUMMY".equals(nbttagcompound.getString("id"))) {
               tileentity = ((ITileEntityProvider)this.getBlockState(p_175625_1_).getBlock()).createNewTileEntity(this.world);
            } else {
               tileentity = TileEntity.create(nbttagcompound);
            }

            if (tileentity != null) {
               ichunk.addTileEntity(p_175625_1_, tileentity);
               return tileentity;
            }
         }

         if (ichunk.getBlockState(p_175625_1_).getBlock() instanceof ITileEntityProvider) {
            LOGGER.warn("Tried to access a block entity before it was created. {}", p_175625_1_);
         }

         return null;
      }
   }

   public boolean setBlockState(BlockPos p_180501_1_, IBlockState p_180501_2_, int p_180501_3_) {
      IChunk ichunk = this.getChunkDefault(p_180501_1_);
      IBlockState iblockstate = ichunk.setBlockState(p_180501_1_, p_180501_2_, false);
      Block block = p_180501_2_.getBlock();
      if (block.hasTileEntity()) {
         if (ichunk.getStatus().getType() == ChunkStatus.Type.LEVELCHUNK) {
            ichunk.addTileEntity(p_180501_1_, ((ITileEntityProvider)block).createNewTileEntity(this));
         } else {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInteger("x", p_180501_1_.getX());
            nbttagcompound.setInteger("y", p_180501_1_.getY());
            nbttagcompound.setInteger("z", p_180501_1_.getZ());
            nbttagcompound.setString("id", "DUMMY");
            ichunk.addTileEntity(nbttagcompound);
         }
      } else if (iblockstate != null && iblockstate.getBlock().hasTileEntity()) {
         ichunk.removeTileEntity(p_180501_1_);
      }

      if (p_180501_2_.blockNeedsPostProcessing(this, p_180501_1_)) {
         this.markBlockForPostprocessing(p_180501_1_);
      }

      return true;
   }

   private void markBlockForPostprocessing(BlockPos p_201683_1_) {
      this.getChunkDefault(p_201683_1_).markBlockForPostprocessing(p_201683_1_);
   }

   public boolean spawnEntity(Entity p_72838_1_) {
      int i = MathHelper.floor(p_72838_1_.posX / 16.0D);
      int j = MathHelper.floor(p_72838_1_.posZ / 16.0D);
      this.getChunk(i, j).addEntity(p_72838_1_);
      return true;
   }

   public boolean removeBlock(BlockPos p_175698_1_) {
      return this.setBlockState(p_175698_1_, Blocks.AIR.getDefaultState(), 3);
   }

   public void setLightFor(EnumLightType p_175653_1_, BlockPos p_175653_2_, int p_175653_3_) {
      this.getChunkDefault(p_175653_2_).setLightFor(p_175653_1_, this.dimension.hasSkyLight(), p_175653_2_, p_175653_3_);
   }

   public WorldBorder getWorldBorder() {
      return this.world.getWorldBorder();
   }

   public boolean checkNoEntityCollision(@Nullable Entity p_195585_1_, VoxelShape p_195585_2_) {
      return true;
   }

   public int getStrongPower(BlockPos p_175627_1_, EnumFacing p_175627_2_) {
      return this.getBlockState(p_175627_1_).getStrongPower(this, p_175627_1_, p_175627_2_);
   }

   public boolean isRemote() {
      return false;
   }

   @Deprecated
   public World getWorld() {
      return this.world;
   }

   public WorldInfo getWorldInfo() {
      return this.worldInfo;
   }

   public DifficultyInstance getDifficultyForLocation(BlockPos p_175649_1_) {
      if (!this.isChunkInBounds(p_175649_1_.getX() >> 4, p_175649_1_.getZ() >> 4)) {
         throw new RuntimeException("We are asking a region for a chunk out of bound");
      } else {
         return new DifficultyInstance(this.world.getDifficulty(), this.world.getWorldTime(), 0L, this.world.getCurrentMoonPhaseFactor());
      }
   }

   @Nullable
   public WorldSavedDataStorage func_175693_T() {
      return this.world.func_175693_T();
   }

   public IChunkProvider getChunkProvider() {
      return this.world.getChunkProvider();
   }

   public ISaveHandler getSaveHandler() {
      return this.world.getSaveHandler();
   }

   public long getSeed() {
      return this.seed;
   }

   public ITickList<Block> getPendingBlockTicks() {
      return this.pendingBlockTickList;
   }

   public ITickList<Fluid> getPendingFluidTicks() {
      return this.pendingFluidTickList;
   }

   public int getSeaLevel() {
      return this.seaLevel;
   }

   public Random getRandom() {
      return this.random;
   }

   public void notifyNeighbors(BlockPos p_195592_1_, Block p_195592_2_) {
   }

   public int getHeight(Heightmap.Type p_201676_1_, int p_201676_2_, int p_201676_3_) {
      return this.getChunk(p_201676_2_ >> 4, p_201676_3_ >> 4).getTopBlockY(p_201676_1_, p_201676_2_ & 15, p_201676_3_ & 15) + 1;
   }

   public void playSound(@Nullable EntityPlayer p_184133_1_, BlockPos p_184133_2_, SoundEvent p_184133_3_, SoundCategory p_184133_4_, float p_184133_5_, float p_184133_6_) {
   }

   public void spawnParticle(IParticleData p_195594_1_, double p_195594_2_, double p_195594_4_, double p_195594_6_, double p_195594_8_, double p_195594_10_, double p_195594_12_) {
   }

   public BlockPos getSpawnPoint() {
      return this.world.getSpawnPoint();
   }

   public Dimension getDimension() {
      return this.dimension;
   }
}
