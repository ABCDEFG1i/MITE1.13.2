package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityMooshroom extends EntityCow {
   public EntityMooshroom(World p_i1687_1_) {
      super(EntityType.MOOSHROOM, p_i1687_1_);
      this.setSize(0.9F, 1.4F);
      this.spawnableBlock = Blocks.MYCELIUM;
   }

   public boolean processInteract(EntityPlayer p_184645_1_, EnumHand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
      if (itemstack.getItem() == Items.BOWL && this.getGrowingAge() >= 0 && !p_184645_1_.capabilities.isCreativeMode) {
         itemstack.shrink(1);
         if (itemstack.isEmpty()) {
            p_184645_1_.setHeldItem(p_184645_2_, new ItemStack(Items.MUSHROOM_STEW));
         } else if (!p_184645_1_.inventory.addItemStackToInventory(new ItemStack(Items.MUSHROOM_STEW))) {
            p_184645_1_.dropItem(new ItemStack(Items.MUSHROOM_STEW), false);
         }

         return true;
      } else if (itemstack.getItem() == Items.SHEARS && this.getGrowingAge() >= 0) {
         this.world.spawnParticle(Particles.EXPLOSION, this.posX, this.posY + (double)(this.height / 2.0F), this.posZ, 0.0D, 0.0D, 0.0D);
         if (!this.world.isRemote) {
            this.setDead();
            EntityCow entitycow = new EntityCow(this.world);
            entitycow.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            entitycow.setHealth(this.getHealth());
            entitycow.renderYawOffset = this.renderYawOffset;
            if (this.hasCustomName()) {
               entitycow.setCustomName(this.getCustomName());
            }

            this.world.spawnEntity(entitycow);

            for(int i = 0; i < 5; ++i) {
               this.world.spawnEntity(new EntityItem(this.world, this.posX, this.posY + (double)this.height, this.posZ, new ItemStack(Blocks.RED_MUSHROOM)));
            }

            itemstack.damageItem(1, p_184645_1_);
            this.playSound(SoundEvents.ENTITY_MOOSHROOM_SHEAR, 1.0F, 1.0F);
         }

         return true;
      } else {
         return super.processInteract(p_184645_1_, p_184645_2_);
      }
   }

   public EntityMooshroom createChild(EntityAgeable p_90011_1_) {
      return new EntityMooshroom(this.world);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_MUSHROOM_COW;
   }
}
