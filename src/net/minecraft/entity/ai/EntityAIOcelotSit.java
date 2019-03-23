package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.init.Blocks;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;

public class EntityAIOcelotSit extends EntityAIMoveToBlock {
   private final EntityOcelot ocelot;

   public EntityAIOcelotSit(EntityOcelot p_i45315_1_, double p_i45315_2_) {
      super(p_i45315_1_, p_i45315_2_, 8);
      this.ocelot = p_i45315_1_;
   }

   public boolean shouldExecute() {
      return this.ocelot.isTamed() && !this.ocelot.isSitting() && super.shouldExecute();
   }

   public void startExecuting() {
      super.startExecuting();
      this.ocelot.getAISit().setSitting(false);
   }

   public void resetTask() {
      super.resetTask();
      this.ocelot.setSitting(false);
   }

   public void updateTask() {
      super.updateTask();
      this.ocelot.getAISit().setSitting(false);
      if (!this.getIsAboveDestination()) {
         this.ocelot.setSitting(false);
      } else if (!this.ocelot.isSitting()) {
         this.ocelot.setSitting(true);
      }

   }

   protected boolean shouldMoveTo(IWorldReaderBase p_179488_1_, BlockPos p_179488_2_) {
      if (!p_179488_1_.isAirBlock(p_179488_2_.up())) {
         return false;
      } else {
         IBlockState iblockstate = p_179488_1_.getBlockState(p_179488_2_);
         Block block = iblockstate.getBlock();
         if (block == Blocks.CHEST) {
            return TileEntityChest.getPlayersUsing(p_179488_1_, p_179488_2_) < 1;
         } else if (block == Blocks.FURNACE && iblockstate.get(BlockFurnace.LIT)) {
            return true;
         } else {
            return block instanceof BlockBed && iblockstate.get(BlockBed.PART) != BedPart.HEAD;
         }
      }
   }
}
