package net.minecraft.client.renderer;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DestroyBlockProgress {
   private final int miningPlayerEntId;
   private final BlockPos position;
   private int partialBlockProgress;
   private int createdAtCloudUpdateTick;

   public DestroyBlockProgress(int p_i45925_1_, BlockPos p_i45925_2_) {
      this.miningPlayerEntId = p_i45925_1_;
      this.position = p_i45925_2_;
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public void setPartialBlockDamage(int p_73107_1_) {
      if (p_73107_1_ > 10) {
         p_73107_1_ = 10;
      }

      this.partialBlockProgress = p_73107_1_;
   }

   public int getPartialBlockDamage() {
      return this.partialBlockProgress;
   }

   public void setCloudUpdateTick(int p_82744_1_) {
      this.createdAtCloudUpdateTick = p_82744_1_;
   }

   public int getCreationCloudUpdateTick() {
      return this.createdAtCloudUpdateTick;
   }
}
