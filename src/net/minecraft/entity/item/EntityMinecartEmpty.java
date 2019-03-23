package net.minecraft.entity.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class EntityMinecartEmpty extends EntityMinecart {
   public EntityMinecartEmpty(World p_i1722_1_) {
      super(EntityType.MINECART, p_i1722_1_);
   }

   public EntityMinecartEmpty(World p_i1723_1_, double p_i1723_2_, double p_i1723_4_, double p_i1723_6_) {
      super(EntityType.MINECART, p_i1723_1_, p_i1723_2_, p_i1723_4_, p_i1723_6_);
   }

   public boolean processInitialInteract(EntityPlayer p_184230_1_, EnumHand p_184230_2_) {
      if (p_184230_1_.isSneaking()) {
         return false;
      } else if (this.isBeingRidden()) {
         return true;
      } else {
         if (!this.world.isRemote) {
            p_184230_1_.startRiding(this);
         }

         return true;
      }
   }

   public void onActivatorRailPass(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_) {
      if (p_96095_4_) {
         if (this.isBeingRidden()) {
            this.removePassengers();
         }

         if (this.getRollingAmplitude() == 0) {
            this.setRollingDirection(-this.getRollingDirection());
            this.setRollingAmplitude(10);
            this.setDamage(50.0F);
            this.markVelocityChanged();
         }
      }

   }

   public EntityMinecart.Type getMinecartType() {
      return EntityMinecart.Type.RIDEABLE;
   }
}
