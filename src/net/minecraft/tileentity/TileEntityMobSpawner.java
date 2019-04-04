package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityMobSpawner extends TileEntity implements ITickable {
   private final MobSpawnerBaseLogic spawnerLogic = new MobSpawnerBaseLogic() {
      public void broadcastEvent(int p_98267_1_) {
         TileEntityMobSpawner.this.world.addBlockEvent(TileEntityMobSpawner.this.pos, Blocks.SPAWNER, p_98267_1_, 0);
      }

      public World getWorld() {
         return TileEntityMobSpawner.this.world;
      }

      public BlockPos getSpawnerPosition() {
         return TileEntityMobSpawner.this.pos;
      }

      public void setNextSpawnData(WeightedSpawnerEntity p_184993_1_) {
         super.setNextSpawnData(p_184993_1_);
         if (this.getWorld() != null) {
            IBlockState iblockstate = this.getWorld().getBlockState(this.getSpawnerPosition());
            this.getWorld().notifyBlockUpdate(TileEntityMobSpawner.this.pos, iblockstate, iblockstate, 4);
         }

      }
   };

   public TileEntityMobSpawner() {
      super(TileEntityType.MOB_SPAWNER);
   }

   public void readFromNBT(NBTTagCompound p_145839_1_) {
      super.readFromNBT(p_145839_1_);
      this.spawnerLogic.readFromNBT(p_145839_1_);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
      super.writeToNBT(p_189515_1_);
      this.spawnerLogic.writeToNBT(p_189515_1_);
      return p_189515_1_;
   }

   public void tick() {
      this.spawnerLogic.tick();
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 1, this.getUpdateTag());
   }

   public NBTTagCompound getUpdateTag() {
      NBTTagCompound nbttagcompound = this.writeToNBT(new NBTTagCompound());
      nbttagcompound.removeTag("SpawnPotentials");
      return nbttagcompound;
   }

   public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_) {
      return this.spawnerLogic.setDelayToMin(p_145842_1_) || super.receiveClientEvent(p_145842_1_, p_145842_2_);
   }

   public boolean onlyOpsCanSetNbt() {
      return true;
   }

   public MobSpawnerBaseLogic getSpawnerBaseLogic() {
      return this.spawnerLogic;
   }
}
