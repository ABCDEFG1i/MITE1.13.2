package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

public interface IWorldEventListener {
   void notifyBlockUpdate(IBlockReader p_184376_1_, BlockPos p_184376_2_, IBlockState p_184376_3_, IBlockState p_184376_4_, int p_184376_5_);

   void notifyLightSet(BlockPos p_174959_1_);

   void markBlockRangeForRenderUpdate(int p_147585_1_, int p_147585_2_, int p_147585_3_, int p_147585_4_, int p_147585_5_, int p_147585_6_);

   void playSoundToAllNearExcept(@Nullable EntityPlayer p_184375_1_, SoundEvent p_184375_2_, SoundCategory p_184375_3_, double p_184375_4_, double p_184375_6_, double p_184375_8_, float p_184375_10_, float p_184375_11_);

   void playRecord(SoundEvent p_184377_1_, BlockPos p_184377_2_);

   void addParticle(IParticleData p_195461_1_, boolean p_195461_2_, double p_195461_3_, double p_195461_5_, double p_195461_7_, double p_195461_9_, double p_195461_11_, double p_195461_13_);

   void addParticle(IParticleData p_195462_1_, boolean p_195462_2_, boolean p_195462_3_, double p_195462_4_, double p_195462_6_, double p_195462_8_, double p_195462_10_, double p_195462_12_, double p_195462_14_);

   void onEntityAdded(Entity p_72703_1_);

   void onEntityRemoved(Entity p_72709_1_);

   void broadcastSound(int p_180440_1_, BlockPos p_180440_2_, int p_180440_3_);

   void playEvent(EntityPlayer p_180439_1_, int p_180439_2_, BlockPos p_180439_3_, int p_180439_4_);

   void sendBlockBreakProgress(int p_180441_1_, BlockPos p_180441_2_, int p_180441_3_);
}
