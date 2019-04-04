package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.IWorldReaderBase;

public class EntityAIBeg extends EntityAIBase {
   private final EntityWolf wolf;
   private EntityPlayer player;
   private final IWorldReaderBase world;
   private final float minPlayerDistance;
   private int timeoutCounter;

   public EntityAIBeg(EntityWolf p_i1617_1_, float p_i1617_2_) {
      this.wolf = p_i1617_1_;
      this.world = p_i1617_1_.world;
      this.minPlayerDistance = p_i1617_2_;
      this.setMutexBits(2);
   }

   public boolean shouldExecute() {
      this.player = this.world.getClosestPlayerToEntity(this.wolf, (double)this.minPlayerDistance);
      return this.player != null && this.hasTemptationItemInHand(this.player);
   }

   public boolean shouldContinueExecuting() {
      if (!this.player.isEntityAlive()) {
         return false;
      } else if (this.wolf.getDistanceSq(this.player) > (double)(this.minPlayerDistance * this.minPlayerDistance)) {
         return false;
      } else {
         return this.timeoutCounter > 0 && this.hasTemptationItemInHand(this.player);
      }
   }

   public void startExecuting() {
      this.wolf.setBegging(true);
      this.timeoutCounter = 40 + this.wolf.getRNG().nextInt(40);
   }

   public void resetTask() {
      this.wolf.setBegging(false);
      this.player = null;
   }

   public void updateTask() {
      this.wolf.getLookHelper().setLookPosition(this.player.posX, this.player.posY + (double)this.player.getEyeHeight(), this.player.posZ, 10.0F, (float)this.wolf.getVerticalFaceSpeed());
      --this.timeoutCounter;
   }

   private boolean hasTemptationItemInHand(EntityPlayer p_75382_1_) {
      for(EnumHand enumhand : EnumHand.values()) {
         ItemStack itemstack = p_75382_1_.getHeldItem(enumhand);
         if (this.wolf.isTamed() && itemstack.getItem() == Items.BONE) {
            return true;
         }

         if (this.wolf.isBreedingItem(itemstack)) {
            return true;
         }
      }

      return false;
   }
}
