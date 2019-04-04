package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityCommandBlock extends TileEntity {
   private boolean powered;
   private boolean auto;
   private boolean conditionMet;
   private boolean sendToClient;
   private final CommandBlockBaseLogic commandBlockLogic = new CommandBlockBaseLogic() {
      public void setCommand(String p_145752_1_) {
         super.setCommand(p_145752_1_);
         TileEntityCommandBlock.this.markDirty();
      }

      public WorldServer getWorld() {
         return (WorldServer)TileEntityCommandBlock.this.world;
      }

      public void updateCommand() {
         IBlockState iblockstate = TileEntityCommandBlock.this.world.getBlockState(TileEntityCommandBlock.this.pos);
         this.getWorld().notifyBlockUpdate(TileEntityCommandBlock.this.pos, iblockstate, iblockstate, 3);
      }

      @OnlyIn(Dist.CLIENT)
      public Vec3d getPositionVector() {
         return new Vec3d((double)TileEntityCommandBlock.this.pos.getX() + 0.5D, (double)TileEntityCommandBlock.this.pos.getY() + 0.5D, (double)TileEntityCommandBlock.this.pos.getZ() + 0.5D);
      }

      public CommandSource getCommandSource() {
         return new CommandSource(this, new Vec3d((double)TileEntityCommandBlock.this.pos.getX() + 0.5D, (double)TileEntityCommandBlock.this.pos.getY() + 0.5D, (double)TileEntityCommandBlock.this.pos.getZ() + 0.5D), Vec2f.ZERO, this.getWorld(), 2, this.getName().getString(), this.getName(), this.getWorld().getServer(),
                 null);
      }
   };

   public TileEntityCommandBlock() {
      super(TileEntityType.COMMAND_BLOCK);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
      super.writeToNBT(p_189515_1_);
      this.commandBlockLogic.writeToNBT(p_189515_1_);
      p_189515_1_.setBoolean("powered", this.isPowered());
      p_189515_1_.setBoolean("conditionMet", this.isConditionMet());
      p_189515_1_.setBoolean("auto", this.isAuto());
      return p_189515_1_;
   }

   public void readFromNBT(NBTTagCompound p_145839_1_) {
      super.readFromNBT(p_145839_1_);
      this.commandBlockLogic.readDataFromNBT(p_145839_1_);
      this.powered = p_145839_1_.getBoolean("powered");
      this.conditionMet = p_145839_1_.getBoolean("conditionMet");
      this.setAuto(p_145839_1_.getBoolean("auto"));
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      if (this.isSendToClient()) {
         this.setSendToClient(false);
         NBTTagCompound nbttagcompound = this.writeToNBT(new NBTTagCompound());
         return new SPacketUpdateTileEntity(this.pos, 2, nbttagcompound);
      } else {
         return null;
      }
   }

   public boolean onlyOpsCanSetNbt() {
      return true;
   }

   public CommandBlockBaseLogic getCommandBlockLogic() {
      return this.commandBlockLogic;
   }

   public void setPowered(boolean p_184250_1_) {
      this.powered = p_184250_1_;
   }

   public boolean isPowered() {
      return this.powered;
   }

   public boolean isAuto() {
      return this.auto;
   }

   public void setAuto(boolean p_184253_1_) {
      boolean flag = this.auto;
      this.auto = p_184253_1_;
      if (!flag && p_184253_1_ && !this.powered && this.world != null && this.getMode() != TileEntityCommandBlock.Mode.SEQUENCE) {
         Block block = this.getBlockState().getBlock();
         if (block instanceof BlockCommandBlock) {
            this.setConditionMet();
            this.world.getPendingBlockTicks().scheduleTick(this.pos, block, block.tickRate(this.world));
         }
      }

   }

   public boolean isConditionMet() {
      return this.conditionMet;
   }

   public boolean setConditionMet() {
      this.conditionMet = true;
      if (this.isConditional()) {
         BlockPos blockpos = this.pos.offset(this.world.getBlockState(this.pos).get(BlockCommandBlock.FACING).getOpposite());
         if (this.world.getBlockState(blockpos).getBlock() instanceof BlockCommandBlock) {
            TileEntity tileentity = this.world.getTileEntity(blockpos);
            this.conditionMet = tileentity instanceof TileEntityCommandBlock && ((TileEntityCommandBlock)tileentity).getCommandBlockLogic().getSuccessCount() > 0;
         } else {
            this.conditionMet = false;
         }
      }

      return this.conditionMet;
   }

   public boolean isSendToClient() {
      return this.sendToClient;
   }

   public void setSendToClient(boolean p_184252_1_) {
      this.sendToClient = p_184252_1_;
   }

   public TileEntityCommandBlock.Mode getMode() {
      Block block = this.getBlockState().getBlock();
      if (block == Blocks.COMMAND_BLOCK) {
         return TileEntityCommandBlock.Mode.REDSTONE;
      } else if (block == Blocks.REPEATING_COMMAND_BLOCK) {
         return TileEntityCommandBlock.Mode.AUTO;
      } else {
         return block == Blocks.CHAIN_COMMAND_BLOCK ? TileEntityCommandBlock.Mode.SEQUENCE : TileEntityCommandBlock.Mode.REDSTONE;
      }
   }

   public boolean isConditional() {
      IBlockState iblockstate = this.world.getBlockState(this.getPos());
      return iblockstate.getBlock() instanceof BlockCommandBlock ? iblockstate.get(BlockCommandBlock.CONDITIONAL) : false;
   }

   public void validate() {
      this.updateContainingBlockInfo();
      super.validate();
   }

   public enum Mode {
      SEQUENCE,
      AUTO,
      REDSTONE
   }
}
