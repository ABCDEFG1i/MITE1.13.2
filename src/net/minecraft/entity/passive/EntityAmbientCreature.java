package net.minecraft.entity.passive;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class EntityAmbientCreature extends EntityLiving implements IAnimal {
   protected EntityAmbientCreature(EntityType<?> p_i48570_1_, World p_i48570_2_) {
      super(p_i48570_1_, p_i48570_2_);
   }

   public boolean canBeLeashedTo(EntityPlayer p_184652_1_) {
      return false;
   }
}
