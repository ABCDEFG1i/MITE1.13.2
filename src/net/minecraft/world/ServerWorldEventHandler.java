package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.particles.IParticleData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class ServerWorldEventHandler implements IWorldEventListener {
   private final MinecraftServer server;
   private final WorldServer world;

   public ServerWorldEventHandler(MinecraftServer p_i1517_1_, WorldServer p_i1517_2_) {
      this.server = p_i1517_1_;
      this.world = p_i1517_2_;
   }

   public void addParticle(IParticleData p_195461_1_, boolean p_195461_2_, double p_195461_3_, double p_195461_5_, double p_195461_7_, double p_195461_9_, double p_195461_11_, double p_195461_13_) {
   }

   public void addParticle(IParticleData p_195462_1_, boolean p_195462_2_, boolean p_195462_3_, double p_195462_4_, double p_195462_6_, double p_195462_8_, double p_195462_10_, double p_195462_12_, double p_195462_14_) {
   }

   public void onEntityAdded(Entity p_72703_1_) {
      this.world.getEntityTracker().track(p_72703_1_);
      if (p_72703_1_ instanceof EntityPlayerMP) {
         this.world.dimension.onPlayerAdded((EntityPlayerMP)p_72703_1_);
      }

   }

   public void onEntityRemoved(Entity p_72709_1_) {
      this.world.getEntityTracker().untrack(p_72709_1_);
      this.world.getScoreboard().removeEntity(p_72709_1_);
      if (p_72709_1_ instanceof EntityPlayerMP) {
         this.world.dimension.onPlayerRemoved((EntityPlayerMP)p_72709_1_);
      }

   }

   public void playSoundToAllNearExcept(@Nullable EntityPlayer p_184375_1_, SoundEvent p_184375_2_, SoundCategory p_184375_3_, double p_184375_4_, double p_184375_6_, double p_184375_8_, float p_184375_10_, float p_184375_11_) {
      this.server.getPlayerList().func_148543_a(p_184375_1_, p_184375_4_, p_184375_6_, p_184375_8_, p_184375_10_ > 1.0F ? (double)(16.0F * p_184375_10_) : 16.0D, this.world.dimension.getType(), new SPacketSoundEffect(p_184375_2_, p_184375_3_, p_184375_4_, p_184375_6_, p_184375_8_, p_184375_10_, p_184375_11_));
   }

   public void markBlockRangeForRenderUpdate(int p_147585_1_, int p_147585_2_, int p_147585_3_, int p_147585_4_, int p_147585_5_, int p_147585_6_) {
   }

   public void notifyBlockUpdate(IBlockReader p_184376_1_, BlockPos p_184376_2_, IBlockState p_184376_3_, IBlockState p_184376_4_, int p_184376_5_) {
      this.world.getPlayerChunkMap().markBlockForUpdate(p_184376_2_);
   }

   public void notifyLightSet(BlockPos p_174959_1_) {
   }

   public void playRecord(SoundEvent p_184377_1_, BlockPos p_184377_2_) {
   }

   public void playEvent(EntityPlayer p_180439_1_, int p_180439_2_, BlockPos p_180439_3_, int p_180439_4_) {
      this.server.getPlayerList().func_148543_a(p_180439_1_, (double)p_180439_3_.getX(), (double)p_180439_3_.getY(), (double)p_180439_3_.getZ(), 64.0D, this.world.dimension.getType(), new SPacketEffect(p_180439_2_, p_180439_3_, p_180439_4_, false));
   }

   public void broadcastSound(int p_180440_1_, BlockPos p_180440_2_, int p_180440_3_) {
      this.server.getPlayerList().sendPacketToAllPlayers(new SPacketEffect(p_180440_1_, p_180440_2_, p_180440_3_, true));
   }

   public void sendBlockBreakProgress(int p_180441_1_, BlockPos p_180441_2_, int p_180441_3_) {
      for(EntityPlayerMP entityplayermp : this.server.getPlayerList().getPlayers()) {
         if (entityplayermp != null && entityplayermp.world == this.world && entityplayermp.getEntityId() != p_180441_1_) {
            double d0 = (double)p_180441_2_.getX() - entityplayermp.posX;
            double d1 = (double)p_180441_2_.getY() - entityplayermp.posY;
            double d2 = (double)p_180441_2_.getZ() - entityplayermp.posZ;
            if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D) {
               entityplayermp.connection.sendPacket(new SPacketBlockBreakAnim(p_180441_1_, p_180441_2_, p_180441_3_));
            }
         }
      }

   }
}
