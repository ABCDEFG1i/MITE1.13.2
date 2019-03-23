package net.minecraft.entity.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityXPOrb extends Entity {
   public int xpColor;
   public int xpOrbAge;
   public int delayBeforeCanPickup;
   private int xpOrbHealth = 5;
   public int xpValue;
   private EntityPlayer closestPlayer;
   private int xpTargetColor;

   public EntityXPOrb(World p_i1585_1_, double p_i1585_2_, double p_i1585_4_, double p_i1585_6_, int p_i1585_8_) {
      super(EntityType.EXPERIENCE_ORB, p_i1585_1_);
      this.setSize(0.5F, 0.5F);
      this.setPosition(p_i1585_2_, p_i1585_4_, p_i1585_6_);
      this.rotationYaw = (float)(Math.random() * 360.0D);
      this.motionX = (double)((float)(Math.random() * (double)0.2F - (double)0.1F) * 2.0F);
      this.motionY = (double)((float)(Math.random() * 0.2D) * 2.0F);
      this.motionZ = (double)((float)(Math.random() * (double)0.2F - (double)0.1F) * 2.0F);
      this.xpValue = p_i1585_8_;
   }

   public EntityXPOrb(World p_i1586_1_) {
      super(EntityType.EXPERIENCE_ORB, p_i1586_1_);
      this.setSize(0.25F, 0.25F);
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected void registerData() {
   }

   @OnlyIn(Dist.CLIENT)
   public int getBrightnessForRender() {
      float f = 0.5F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      int i = super.getBrightnessForRender();
      int j = i & 255;
      int k = i >> 16 & 255;
      j = j + (int)(f * 15.0F * 16.0F);
      if (j > 240) {
         j = 240;
      }

      return j | k << 16;
   }

   public void tick() {
      super.tick();
      if (this.delayBeforeCanPickup > 0) {
         --this.delayBeforeCanPickup;
      }

      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.areEyesInFluid(FluidTags.WATER)) {
         this.func_205711_k();
      } else if (!this.hasNoGravity()) {
         this.motionY -= (double)0.03F;
      }

      if (this.world.getFluidState(new BlockPos(this)).isTagged(FluidTags.LAVA)) {
         this.motionY = (double)0.2F;
         this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
         this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
         this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
      }

      this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0D, this.posZ);
      double d0 = 8.0D;
      if (this.xpTargetColor < this.xpColor - 20 + this.getEntityId() % 100) {
         if (this.closestPlayer == null || this.closestPlayer.getDistanceSq(this) > 64.0D) {
            this.closestPlayer = this.world.getClosestPlayerToEntity(this, 8.0D);
         }

         this.xpTargetColor = this.xpColor;
      }

      if (this.closestPlayer != null && this.closestPlayer.isSpectator()) {
         this.closestPlayer = null;
      }

      if (this.closestPlayer != null) {
         double d1 = (this.closestPlayer.posX - this.posX) / 8.0D;
         double d2 = (this.closestPlayer.posY + (double)this.closestPlayer.getEyeHeight() / 2.0D - this.posY) / 8.0D;
         double d3 = (this.closestPlayer.posZ - this.posZ) / 8.0D;
         double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
         double d5 = 1.0D - d4;
         if (d5 > 0.0D) {
            d5 = d5 * d5;
            this.motionX += d1 / d4 * d5 * 0.1D;
            this.motionY += d2 / d4 * d5 * 0.1D;
            this.motionZ += d3 / d4 * d5 * 0.1D;
         }
      }

      this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
      float f = 0.98F;
      if (this.onGround) {
         f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().getSlipperiness() * 0.98F;
      }

      this.motionX *= (double)f;
      this.motionY *= (double)0.98F;
      this.motionZ *= (double)f;
      if (this.onGround) {
         this.motionY *= (double)-0.9F;
      }

      ++this.xpColor;
      ++this.xpOrbAge;
      if (this.xpOrbAge >= 6000) {
         this.setDead();
      }

   }

   private void func_205711_k() {
      this.motionY += (double)5.0E-4F;
      this.motionY = Math.min(this.motionY, (double)0.06F);
      this.motionX *= (double)0.99F;
      this.motionZ *= (double)0.99F;
   }

   protected void doWaterSplashEffect() {
   }

   protected void dealFireDamage(int p_70081_1_) {
      this.attackEntityFrom(DamageSource.IN_FIRE, (float)p_70081_1_);
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         this.markVelocityChanged();
         this.xpOrbHealth = (int)((float)this.xpOrbHealth - p_70097_2_);
         if (this.xpOrbHealth <= 0) {
            this.setDead();
         }

         return false;
      }
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      p_70014_1_.setShort("Health", (short)this.xpOrbHealth);
      p_70014_1_.setShort("Age", (short)this.xpOrbAge);
      p_70014_1_.setShort("Value", (short)this.xpValue);
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      this.xpOrbHealth = p_70037_1_.getShort("Health");
      this.xpOrbAge = p_70037_1_.getShort("Age");
      this.xpValue = p_70037_1_.getShort("Value");
   }

   public void onCollideWithPlayer(EntityPlayer p_70100_1_) {
      if (!this.world.isRemote) {
         if (this.delayBeforeCanPickup == 0 && p_70100_1_.xpCooldown == 0) {
            p_70100_1_.xpCooldown = 2;
            p_70100_1_.onItemPickup(this, 1);
            ItemStack itemstack = EnchantmentHelper.getEnchantedItem(Enchantments.MENDING, p_70100_1_);
            if (!itemstack.isEmpty() && itemstack.isDamaged()) {
               int i = Math.min(this.xpToDurability(this.xpValue), itemstack.getDamage());
               this.xpValue -= this.durabilityToXp(i);
               itemstack.setDamage(itemstack.getDamage() - i);
            }

            if (this.xpValue > 0) {
               p_70100_1_.func_195068_e(this.xpValue);
            }

            this.setDead();
         }

      }
   }

   private int durabilityToXp(int p_184515_1_) {
      return p_184515_1_ / 2;
   }

   private int xpToDurability(int p_184514_1_) {
      return p_184514_1_ * 2;
   }

   public int getXpValue() {
      return this.xpValue;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTextureByXP() {
      if (this.xpValue >= 2477) {
         return 10;
      } else if (this.xpValue >= 1237) {
         return 9;
      } else if (this.xpValue >= 617) {
         return 8;
      } else if (this.xpValue >= 307) {
         return 7;
      } else if (this.xpValue >= 149) {
         return 6;
      } else if (this.xpValue >= 73) {
         return 5;
      } else if (this.xpValue >= 37) {
         return 4;
      } else if (this.xpValue >= 17) {
         return 3;
      } else if (this.xpValue >= 7) {
         return 2;
      } else {
         return this.xpValue >= 3 ? 1 : 0;
      }
   }

   public static int getXPSplit(int p_70527_0_) {
      if (p_70527_0_ >= 2477) {
         return 2477;
      } else if (p_70527_0_ >= 1237) {
         return 1237;
      } else if (p_70527_0_ >= 617) {
         return 617;
      } else if (p_70527_0_ >= 307) {
         return 307;
      } else if (p_70527_0_ >= 149) {
         return 149;
      } else if (p_70527_0_ >= 73) {
         return 73;
      } else if (p_70527_0_ >= 37) {
         return 37;
      } else if (p_70527_0_ >= 17) {
         return 17;
      } else if (p_70527_0_ >= 7) {
         return 7;
      } else {
         return p_70527_0_ >= 3 ? 3 : 1;
      }
   }

   public boolean canBeAttackedWithItem() {
      return false;
   }
}
