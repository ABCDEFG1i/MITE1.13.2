package net.minecraft.entity.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityMinecartMobSpawner extends EntityMinecart {
   private final MobSpawnerBaseLogic mobSpawnerLogic = new MobSpawnerBaseLogic() {
      public void broadcastEvent(int p_98267_1_) {
         EntityMinecartMobSpawner.this.world.setEntityState(EntityMinecartMobSpawner.this, (byte)p_98267_1_);
      }

      public World getWorld() {
         return EntityMinecartMobSpawner.this.world;
      }

      public BlockPos getSpawnerPosition() {
         return new BlockPos(EntityMinecartMobSpawner.this);
      }
   };

   public EntityMinecartMobSpawner(World p_i46752_1_) {
      super(EntityType.SPAWNER_MINECART, p_i46752_1_);
   }

   public EntityMinecartMobSpawner(World p_i46753_1_, double p_i46753_2_, double p_i46753_4_, double p_i46753_6_) {
      super(EntityType.SPAWNER_MINECART, p_i46753_1_, p_i46753_2_, p_i46753_4_, p_i46753_6_);
   }

   public EntityMinecart.Type getMinecartType() {
      return EntityMinecart.Type.SPAWNER;
   }

   public IBlockState getDefaultDisplayTile() {
      return Blocks.SPAWNER.getDefaultState();
   }

   protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.mobSpawnerLogic.readFromNBT(p_70037_1_);
   }

   protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      this.mobSpawnerLogic.writeToNBT(p_70014_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      this.mobSpawnerLogic.setDelayToMin(p_70103_1_);
   }

   public void tick() {
      super.tick();
      this.mobSpawnerLogic.tick();
   }

   public boolean ignoreItemEntityData() {
      return true;
   }
}
