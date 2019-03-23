package net.minecraft.entity;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFence;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityLeashKnot extends EntityHanging {
   public EntityLeashKnot(World p_i1592_1_) {
      super(EntityType.LEASH_KNOT, p_i1592_1_);
   }

   public EntityLeashKnot(World p_i45851_1_, BlockPos p_i45851_2_) {
      super(EntityType.LEASH_KNOT, p_i45851_1_, p_i45851_2_);
      this.setPosition((double)p_i45851_2_.getX() + 0.5D, (double)p_i45851_2_.getY() + 0.5D, (double)p_i45851_2_.getZ() + 0.5D);
      float f = 0.125F;
      float f1 = 0.1875F;
      float f2 = 0.25F;
      this.setEntityBoundingBox(new AxisAlignedBB(this.posX - 0.1875D, this.posY - 0.25D + 0.125D, this.posZ - 0.1875D, this.posX + 0.1875D, this.posY + 0.25D + 0.125D, this.posZ + 0.1875D));
      this.forceSpawn = true;
   }

   public void setPosition(double p_70107_1_, double p_70107_3_, double p_70107_5_) {
      super.setPosition((double)MathHelper.floor(p_70107_1_) + 0.5D, (double)MathHelper.floor(p_70107_3_) + 0.5D, (double)MathHelper.floor(p_70107_5_) + 0.5D);
   }

   protected void updateBoundingBox() {
      this.posX = (double)this.hangingPosition.getX() + 0.5D;
      this.posY = (double)this.hangingPosition.getY() + 0.5D;
      this.posZ = (double)this.hangingPosition.getZ() + 0.5D;
   }

   public void updateFacingWithBoundingBox(EnumFacing p_174859_1_) {
   }

   public int getWidthPixels() {
      return 9;
   }

   public int getHeightPixels() {
      return 9;
   }

   public float getEyeHeight() {
      return -0.0625F;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      return p_70112_1_ < 1024.0D;
   }

   public void onBroken(@Nullable Entity p_110128_1_) {
      this.playSound(SoundEvents.ENTITY_LEASH_KNOT_BREAK, 1.0F, 1.0F);
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
   }

   public boolean processInitialInteract(EntityPlayer p_184230_1_, EnumHand p_184230_2_) {
      if (this.world.isRemote) {
         return true;
      } else {
         boolean flag = false;
         double d0 = 7.0D;
         List<EntityLiving> list = this.world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(this.posX - 7.0D, this.posY - 7.0D, this.posZ - 7.0D, this.posX + 7.0D, this.posY + 7.0D, this.posZ + 7.0D));

         for(EntityLiving entityliving : list) {
            if (entityliving.getLeashed() && entityliving.getLeashHolder() == p_184230_1_) {
               entityliving.setLeashHolder(this, true);
               flag = true;
            }
         }

         if (!flag) {
            this.setDead();
            if (p_184230_1_.capabilities.isCreativeMode) {
               for(EntityLiving entityliving1 : list) {
                  if (entityliving1.getLeashed() && entityliving1.getLeashHolder() == this) {
                     entityliving1.clearLeashed(true, false);
                  }
               }
            }
         }

         return true;
      }
   }

   public boolean onValidSurface() {
      return this.world.getBlockState(this.hangingPosition).getBlock() instanceof BlockFence;
   }

   public static EntityLeashKnot createKnot(World p_174862_0_, BlockPos p_174862_1_) {
      EntityLeashKnot entityleashknot = new EntityLeashKnot(p_174862_0_, p_174862_1_);
      p_174862_0_.spawnEntity(entityleashknot);
      entityleashknot.playPlaceSound();
      return entityleashknot;
   }

   @Nullable
   public static EntityLeashKnot getKnotForPosition(World p_174863_0_, BlockPos p_174863_1_) {
      int i = p_174863_1_.getX();
      int j = p_174863_1_.getY();
      int k = p_174863_1_.getZ();

      for(EntityLeashKnot entityleashknot : p_174863_0_.getEntitiesWithinAABB(EntityLeashKnot.class, new AxisAlignedBB((double)i - 1.0D, (double)j - 1.0D, (double)k - 1.0D, (double)i + 1.0D, (double)j + 1.0D, (double)k + 1.0D))) {
         if (entityleashknot.getHangingPosition().equals(p_174863_1_)) {
            return entityleashknot;
         }
      }

      return null;
   }

   public void playPlaceSound() {
      this.playSound(SoundEvents.ENTITY_LEASH_KNOT_PLACE, 1.0F, 1.0F);
   }
}
