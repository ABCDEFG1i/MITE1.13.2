package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TileEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private final TileEntityType<?> type;
   protected World world;
   protected BlockPos pos = BlockPos.ORIGIN;
   protected boolean tileEntityInvalid;
   @Nullable
   private IBlockState cachedBlockState;

   public TileEntity(TileEntityType<?> p_i48289_1_) {
      this.type = p_i48289_1_;
   }

   @Nullable
   public World getWorld() {
      return this.world;
   }

   public void setWorld(World p_145834_1_) {
      this.world = p_145834_1_;
   }

   public boolean hasWorld() {
      return this.world != null;
   }

   public void readFromNBT(NBTTagCompound p_145839_1_) {
      this.pos = new BlockPos(p_145839_1_.getInteger("x"), p_145839_1_.getInteger("y"), p_145839_1_.getInteger("z"));
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
      return this.writeInternal(p_189515_1_);
   }

   private NBTTagCompound writeInternal(NBTTagCompound p_189516_1_) {
      ResourceLocation resourcelocation = TileEntityType.getId(this.getType());
      if (resourcelocation == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      } else {
         p_189516_1_.setString("id", resourcelocation.toString());
         p_189516_1_.setInteger("x", this.pos.getX());
         p_189516_1_.setInteger("y", this.pos.getY());
         p_189516_1_.setInteger("z", this.pos.getZ());
         return p_189516_1_;
      }
   }

   @Nullable
   public static TileEntity create(NBTTagCompound p_203403_0_) {
      TileEntity tileentity = null;
      String s = p_203403_0_.getString("id");

      try {
         tileentity = TileEntityType.create(s);
      } catch (Throwable throwable1) {
         LOGGER.error("Failed to create block entity {}", s, throwable1);
      }

      if (tileentity != null) {
         try {
            tileentity.readFromNBT(p_203403_0_);
         } catch (Throwable throwable) {
            LOGGER.error("Failed to load data for block entity {}", s, throwable);
            tileentity = null;
         }
      } else {
         LOGGER.warn("Skipping BlockEntity with id {}", (Object)s);
      }

      return tileentity;
   }

   public void markDirty() {
      if (this.world != null) {
         this.cachedBlockState = this.world.getBlockState(this.pos);
         this.world.markChunkDirty(this.pos, this);
         if (!this.cachedBlockState.isAir()) {
            this.world.updateComparatorOutputLevel(this.pos, this.cachedBlockState.getBlock());
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public double getDistanceSq(double p_145835_1_, double p_145835_3_, double p_145835_5_) {
      double d0 = (double)this.pos.getX() + 0.5D - p_145835_1_;
      double d1 = (double)this.pos.getY() + 0.5D - p_145835_3_;
      double d2 = (double)this.pos.getZ() + 0.5D - p_145835_5_;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   @OnlyIn(Dist.CLIENT)
   public double getMaxRenderDistanceSquared() {
      return 4096.0D;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public IBlockState getBlockState() {
      if (this.cachedBlockState == null) {
         this.cachedBlockState = this.world.getBlockState(this.pos);
      }

      return this.cachedBlockState;
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return null;
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeInternal(new NBTTagCompound());
   }

   public boolean isInvalid() {
      return this.tileEntityInvalid;
   }

   public void invalidate() {
      this.tileEntityInvalid = true;
   }

   public void validate() {
      this.tileEntityInvalid = false;
   }

   public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_) {
      return false;
   }

   public void updateContainingBlockInfo() {
      this.cachedBlockState = null;
   }

   public void addInfoToCrashReport(CrashReportCategory p_145828_1_) {
      p_145828_1_.addDetail("Name", () -> {
         return IRegistry.field_212626_o.func_177774_c(this.getType()) + " // " + this.getClass().getCanonicalName();
      });
      if (this.world != null) {
         CrashReportCategory.addBlockInfo(p_145828_1_, this.pos, this.getBlockState());
         CrashReportCategory.addBlockInfo(p_145828_1_, this.pos, this.world.getBlockState(this.pos));
      }
   }

   public void setPos(BlockPos p_174878_1_) {
      this.pos = p_174878_1_.toImmutable();
   }

   public boolean onlyOpsCanSetNbt() {
      return false;
   }

   public void rotate(Rotation p_189667_1_) {
   }

   public void mirror(Mirror p_189668_1_) {
   }

   public TileEntityType<?> getType() {
      return this.type;
   }
}
