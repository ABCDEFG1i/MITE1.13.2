package net.minecraft.tileentity;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.gen.feature.EndGatewayConfig;
import net.minecraft.world.gen.feature.EndIslandFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileEntityEndGateway extends TileEntityEndPortal implements ITickable {
   private static final Logger LOGGER = LogManager.getLogger();
   private long age;
   private int teleportCooldown;
   private BlockPos exitPortal;
   private boolean exactTeleport;

   public TileEntityEndGateway() {
      super(TileEntityType.END_GATEWAY);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
      super.writeToNBT(p_189515_1_);
      p_189515_1_.setLong("Age", this.age);
      if (this.exitPortal != null) {
         p_189515_1_.setTag("ExitPortal", NBTUtil.createPosTag(this.exitPortal));
      }

      if (this.exactTeleport) {
         p_189515_1_.setBoolean("ExactTeleport", this.exactTeleport);
      }

      return p_189515_1_;
   }

   public void readFromNBT(NBTTagCompound p_145839_1_) {
      super.readFromNBT(p_145839_1_);
      this.age = p_145839_1_.getLong("Age");
      if (p_145839_1_.hasKey("ExitPortal", 10)) {
         this.exitPortal = NBTUtil.getPosFromTag(p_145839_1_.getCompoundTag("ExitPortal"));
      }

      this.exactTeleport = p_145839_1_.getBoolean("ExactTeleport");
   }

   @OnlyIn(Dist.CLIENT)
   public double getMaxRenderDistanceSquared() {
      return 65536.0D;
   }

   public void tick() {
      boolean flag = this.isSpawning();
      boolean flag1 = this.isCoolingDown();
      ++this.age;
      if (flag1) {
         --this.teleportCooldown;
      } else if (!this.world.isRemote) {
         List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.getPos()));
         if (!list.isEmpty()) {
            this.teleportEntity(list.get(0));
         }

         if (this.age % 2400L == 0L) {
            this.triggerCooldown();
         }
      }

      if (flag != this.isSpawning() || flag1 != this.isCoolingDown()) {
         this.markDirty();
      }

   }

   public boolean isSpawning() {
      return this.age < 200L;
   }

   public boolean isCoolingDown() {
      return this.teleportCooldown > 0;
   }

   @OnlyIn(Dist.CLIENT)
   public float getSpawnPercent(float p_195497_1_) {
      return MathHelper.clamp(((float)this.age + p_195497_1_) / 200.0F, 0.0F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public float getCooldownPercent(float p_195491_1_) {
      return 1.0F - MathHelper.clamp(((float)this.teleportCooldown - p_195491_1_) / 40.0F, 0.0F, 1.0F);
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 8, this.getUpdateTag());
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public void triggerCooldown() {
      if (!this.world.isRemote) {
         this.teleportCooldown = 40;
         this.world.addBlockEvent(this.getPos(), this.getBlockState().getBlock(), 1, 0);
         this.markDirty();
      }

   }

   public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_) {
      if (p_145842_1_ == 1) {
         this.teleportCooldown = 40;
         return true;
      } else {
         return super.receiveClientEvent(p_145842_1_, p_145842_2_);
      }
   }

   public void teleportEntity(Entity p_195496_1_) {
      if (!this.world.isRemote && !this.isCoolingDown()) {
         this.teleportCooldown = 100;
         if (this.exitPortal == null && this.world.dimension instanceof EndDimension) {
            this.findExitPortal();
         }

         if (this.exitPortal != null) {
            BlockPos blockpos = this.exactTeleport ? this.exitPortal : this.findExitPosition();
            p_195496_1_.setPositionAndUpdate((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D);
         }

         this.triggerCooldown();
      }
   }

   private BlockPos findExitPosition() {
      BlockPos blockpos = findHighestBlock(this.world, this.exitPortal, 5, false);
      LOGGER.debug("Best exit position for portal at {} is {}", this.exitPortal, blockpos);
      return blockpos.up();
   }

   private void findExitPortal() {
      Vec3d vec3d = (new Vec3d((double)this.getPos().getX(), 0.0D, (double)this.getPos().getZ())).normalize();
      Vec3d vec3d1 = vec3d.scale(1024.0D);

      for(int i = 16; getChunk(this.world, vec3d1).getTopFilledSegment() > 0 && i-- > 0; vec3d1 = vec3d1.add(vec3d.scale(-16.0D))) {
         LOGGER.debug("Skipping backwards past nonempty chunk at {}", (Object)vec3d1);
      }

      for(int j = 16; getChunk(this.world, vec3d1).getTopFilledSegment() == 0 && j-- > 0; vec3d1 = vec3d1.add(vec3d.scale(16.0D))) {
         LOGGER.debug("Skipping forward past empty chunk at {}", (Object)vec3d1);
      }

      LOGGER.debug("Found chunk at {}", (Object)vec3d1);
      Chunk chunk = getChunk(this.world, vec3d1);
      this.exitPortal = findSpawnpointInChunk(chunk);
      if (this.exitPortal == null) {
         this.exitPortal = new BlockPos(vec3d1.x + 0.5D, 75.0D, vec3d1.z + 0.5D);
         LOGGER.debug("Failed to find suitable block, settling on {}", (Object)this.exitPortal);
         (new EndIslandFeature()).func_212245_a(this.world, this.world.getChunkProvider().getChunkGenerator(), new Random(this.exitPortal.toLong()), this.exitPortal, IFeatureConfig.NO_FEATURE_CONFIG);
      } else {
         LOGGER.debug("Found block at {}", (Object)this.exitPortal);
      }

      this.exitPortal = findHighestBlock(this.world, this.exitPortal, 16, true);
      LOGGER.debug("Creating portal at {}", (Object)this.exitPortal);
      this.exitPortal = this.exitPortal.up(10);
      this.createExitPortal(this.exitPortal);
      this.markDirty();
   }

   private static BlockPos findHighestBlock(IBlockReader p_195494_0_, BlockPos p_195494_1_, int p_195494_2_, boolean p_195494_3_) {
      BlockPos blockpos = null;

      for(int i = -p_195494_2_; i <= p_195494_2_; ++i) {
         for(int j = -p_195494_2_; j <= p_195494_2_; ++j) {
            if (i != 0 || j != 0 || p_195494_3_) {
               for(int k = 255; k > (blockpos == null ? 0 : blockpos.getY()); --k) {
                  BlockPos blockpos1 = new BlockPos(p_195494_1_.getX() + i, k, p_195494_1_.getZ() + j);
                  IBlockState iblockstate = p_195494_0_.getBlockState(blockpos1);
                  if (iblockstate.isBlockNormalCube() && (p_195494_3_ || iblockstate.getBlock() != Blocks.BEDROCK)) {
                     blockpos = blockpos1;
                     break;
                  }
               }
            }
         }
      }

      return blockpos == null ? p_195494_1_ : blockpos;
   }

   private static Chunk getChunk(World p_195495_0_, Vec3d p_195495_1_) {
      return p_195495_0_.getChunk(MathHelper.floor(p_195495_1_.x / 16.0D), MathHelper.floor(p_195495_1_.z / 16.0D));
   }

   @Nullable
   private static BlockPos findSpawnpointInChunk(Chunk p_195498_0_) {
      BlockPos blockpos = new BlockPos(p_195498_0_.x * 16, 30, p_195498_0_.z * 16);
      int i = p_195498_0_.getTopFilledSegment() + 16 - 1;
      BlockPos blockpos1 = new BlockPos(p_195498_0_.x * 16 + 16 - 1, i, p_195498_0_.z * 16 + 16 - 1);
      BlockPos blockpos2 = null;
      double d0 = 0.0D;

      for(BlockPos blockpos3 : BlockPos.getAllInBox(blockpos, blockpos1)) {
         IBlockState iblockstate = p_195498_0_.getBlockState(blockpos3);
         if (iblockstate.getBlock() == Blocks.END_STONE && !p_195498_0_.getBlockState(blockpos3.up(1)).isBlockNormalCube() && !p_195498_0_.getBlockState(blockpos3.up(2)).isBlockNormalCube()) {
            double d1 = blockpos3.distanceSqToCenter(0.0D, 0.0D, 0.0D);
            if (blockpos2 == null || d1 < d0) {
               blockpos2 = blockpos3;
               d0 = d1;
            }
         }
      }

      return blockpos2;
   }

   private void createExitPortal(BlockPos p_195492_1_) {
      Feature.END_GATEWAY.func_212245_a(this.world, this.world.getChunkProvider().getChunkGenerator(), new Random(), p_195492_1_, new EndGatewayConfig(false));
      TileEntity tileentity = this.world.getTileEntity(p_195492_1_);
      if (tileentity instanceof TileEntityEndGateway) {
         TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway)tileentity;
         tileentityendgateway.exitPortal = new BlockPos(this.getPos());
         tileentityendgateway.markDirty();
      } else {
         LOGGER.warn("Couldn't save exit portal at {}", (Object)p_195492_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderFace(EnumFacing p_184313_1_) {
      return Block.shouldSideBeRendered(this.getBlockState(), this.world, this.getPos(), p_184313_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public int getParticleAmount() {
      int i = 0;

      for(EnumFacing enumfacing : EnumFacing.values()) {
         i += this.shouldRenderFace(enumfacing) ? 1 : 0;
      }

      return i;
   }

   public void setExitPortal(BlockPos p_195489_1_) {
      this.exactTeleport = true;
      this.exitPortal = p_195489_1_;
   }
}
