package net.minecraft.world;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IWorld extends IWorldReaderBase, ISaveDataAccess, IWorldWriter {
   long getSeed();

   default float getCurrentMoonPhaseFactor() {
      return Dimension.MOON_PHASE_FACTORS[this.getDimension().getMoonPhase(this.getWorldInfo().getWorldTime())];
   }

   default float getCelestialAngle(float p_72826_1_) {
      return this.getDimension().calculateCelestialAngle(this.getWorldInfo().getWorldTime(), p_72826_1_);
   }

   @OnlyIn(Dist.CLIENT)
   default int getMoonPhase() {
      return this.getDimension().getMoonPhase(this.getWorldInfo().getWorldTime());
   }

   ITickList<Block> getPendingBlockTicks();

   ITickList<Fluid> getPendingFluidTicks();

   default IChunk getChunkDefault(BlockPos p_205771_1_) {
      return this.getChunk(p_205771_1_.getX() >> 4, p_205771_1_.getZ() >> 4);
   }

   IChunk getChunk(int p_72964_1_, int p_72964_2_);

   World getWorld();

   WorldInfo getWorldInfo();

   DifficultyInstance getDifficultyForLocation(BlockPos p_175649_1_);

   default EnumDifficulty getDifficulty() {
      return this.getWorldInfo().getDifficulty();
   }

   IChunkProvider getChunkProvider();

   ISaveHandler getSaveHandler();

   Random getRandom();

   void notifyNeighbors(BlockPos p_195592_1_, Block p_195592_2_);

   BlockPos getSpawnPoint();

   void playSound(@Nullable EntityPlayer p_184133_1_, BlockPos p_184133_2_, SoundEvent p_184133_3_, SoundCategory p_184133_4_, float p_184133_5_, float p_184133_6_);

   void spawnParticle(IParticleData p_195594_1_, double p_195594_2_, double p_195594_4_, double p_195594_6_, double p_195594_8_, double p_195594_10_, double p_195594_12_);
}
