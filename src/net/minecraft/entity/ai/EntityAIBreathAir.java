package net.minecraft.entity.ai;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReaderBase;

public class EntityAIBreathAir extends EntityAIBase {
   private final EntityCreature field_205142_a;

   public EntityAIBreathAir(EntityCreature p_i48940_1_) {
      this.field_205142_a = p_i48940_1_;
      this.setMutexBits(3);
   }

   public boolean shouldExecute() {
      return this.field_205142_a.getAir() < 140;
   }

   public boolean shouldContinueExecuting() {
      return this.shouldExecute();
   }

   public boolean isInterruptible() {
      return false;
   }

   public void startExecuting() {
      this.func_205141_g();
   }

   private void func_205141_g() {
      Iterable<BlockPos.MutableBlockPos> iterable = BlockPos.MutableBlockPos.getAllInBoxMutable(MathHelper.floor(this.field_205142_a.posX - 1.0D), MathHelper.floor(this.field_205142_a.posY), MathHelper.floor(this.field_205142_a.posZ - 1.0D), MathHelper.floor(this.field_205142_a.posX + 1.0D), MathHelper.floor(this.field_205142_a.posY + 8.0D), MathHelper.floor(this.field_205142_a.posZ + 1.0D));
      BlockPos blockpos = null;

      for(BlockPos blockpos1 : iterable) {
         if (this.func_205140_a(this.field_205142_a.world, blockpos1)) {
            blockpos = blockpos1;
            break;
         }
      }

      if (blockpos == null) {
         blockpos = new BlockPos(this.field_205142_a.posX, this.field_205142_a.posY + 8.0D, this.field_205142_a.posZ);
      }

      this.field_205142_a.getNavigator().tryMoveToXYZ((double)blockpos.getX(), (double)(blockpos.getY() + 1), (double)blockpos.getZ(), 1.0D);
   }

   public void updateTask() {
      this.func_205141_g();
      this.field_205142_a.moveRelative(this.field_205142_a.moveStrafing, this.field_205142_a.moveVertical, this.field_205142_a.moveForward, 0.02F);
      this.field_205142_a.move(MoverType.SELF, this.field_205142_a.motionX, this.field_205142_a.motionY, this.field_205142_a.motionZ);
   }

   private boolean func_205140_a(IWorldReaderBase p_205140_1_, BlockPos p_205140_2_) {
      IBlockState iblockstate = p_205140_1_.getBlockState(p_205140_2_);
      return (p_205140_1_.getFluidState(p_205140_2_).isEmpty() || iblockstate.getBlock() == Blocks.BUBBLE_COLUMN) && iblockstate.allowsMovement(p_205140_1_, p_205140_2_, PathType.LAND);
   }
}
