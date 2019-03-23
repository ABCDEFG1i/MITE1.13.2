package net.minecraft.entity.passive;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class EntityAnimal extends EntityAgeable implements IAnimal {
   protected Block spawnableBlock = Blocks.GRASS_BLOCK;
   private int inLove;
   private UUID playerInLove;

   protected EntityAnimal(EntityType<?> p_i48568_1_, World p_i48568_2_) {
      super(p_i48568_1_, p_i48568_2_);
   }

   protected void updateAITasks() {
      if (this.getGrowingAge() != 0) {
         this.inLove = 0;
      }

      super.updateAITasks();
   }

   public void livingTick() {
      super.livingTick();
      if (this.getGrowingAge() != 0) {
         this.inLove = 0;
      }

      if (this.inLove > 0) {
         --this.inLove;
         if (this.inLove % 10 == 0) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(Particles.HEART, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
         }
      }

   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         this.inLove = 0;
         return super.attackEntityFrom(p_70097_1_, p_70097_2_);
      }
   }

   public float getBlockPathWeight(BlockPos p_205022_1_, IWorldReaderBase p_205022_2_) {
      return p_205022_2_.getBlockState(p_205022_1_.down()).getBlock() == this.spawnableBlock ? 10.0F : p_205022_2_.getBrightness(p_205022_1_) - 0.5F;
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("InLove", this.inLove);
      if (this.playerInLove != null) {
         p_70014_1_.setUniqueId("LoveCause", this.playerInLove);
      }

   }

   public double getYOffset() {
      return 0.14D;
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.inLove = p_70037_1_.getInteger("InLove");
      this.playerInLove = p_70037_1_.hasUniqueId("LoveCause") ? p_70037_1_.getUniqueId("LoveCause") : null;
   }

   public boolean func_205020_a(IWorld p_205020_1_, boolean p_205020_2_) {
      int i = MathHelper.floor(this.posX);
      int j = MathHelper.floor(this.getEntityBoundingBox().minY);
      int k = MathHelper.floor(this.posZ);
      BlockPos blockpos = new BlockPos(i, j, k);
      return p_205020_1_.getBlockState(blockpos.down()).getBlock() == this.spawnableBlock && p_205020_1_.getLightSubtracted(blockpos, 0) > 8 && super.func_205020_a(p_205020_1_, p_205020_2_);
   }

   public int getTalkInterval() {
      return 120;
   }

   public boolean canDespawn() {
      return false;
   }

   protected int getExperiencePoints(EntityPlayer p_70693_1_) {
      return 1 + this.world.rand.nextInt(3);
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return p_70877_1_.getItem() == Items.WHEAT;
   }

   public boolean processInteract(EntityPlayer p_184645_1_, EnumHand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
      if (this.isBreedingItem(itemstack)) {
         if (this.getGrowingAge() == 0 && this.canBreed()) {
            this.consumeItemFromStack(p_184645_1_, itemstack);
            this.setInLove(p_184645_1_);
            return true;
         }

         if (this.isChild()) {
            this.consumeItemFromStack(p_184645_1_, itemstack);
            this.ageUp((int)((float)(-this.getGrowingAge() / 20) * 0.1F), true);
            return true;
         }
      }

      return super.processInteract(p_184645_1_, p_184645_2_);
   }

   protected void consumeItemFromStack(EntityPlayer p_175505_1_, ItemStack p_175505_2_) {
      if (!p_175505_1_.capabilities.isCreativeMode) {
         p_175505_2_.shrink(1);
      }

   }

   public boolean canBreed() {
      return this.inLove <= 0;
   }

   public void setInLove(@Nullable EntityPlayer p_146082_1_) {
      this.inLove = 600;
      if (p_146082_1_ != null) {
         this.playerInLove = p_146082_1_.getUniqueID();
      }

      this.world.setEntityState(this, (byte)18);
   }

   public void func_204700_e(int p_204700_1_) {
      this.inLove = p_204700_1_;
   }

   @Nullable
   public EntityPlayerMP getLoveCause() {
      if (this.playerInLove == null) {
         return null;
      } else {
         EntityPlayer entityplayer = this.world.getPlayerEntityByUUID(this.playerInLove);
         return entityplayer instanceof EntityPlayerMP ? (EntityPlayerMP)entityplayer : null;
      }
   }

   public boolean isInLove() {
      return this.inLove > 0;
   }

   public void resetInLove() {
      this.inLove = 0;
   }

   public boolean canMateWith(EntityAnimal p_70878_1_) {
      if (p_70878_1_ == this) {
         return false;
      } else if (p_70878_1_.getClass() != this.getClass()) {
         return false;
      } else {
         return this.isInLove() && p_70878_1_.isInLove();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 18) {
         for(int i = 0; i < 7; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(Particles.HEART, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
         }
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }
}
