package net.minecraft.entity.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityMinecartTNT extends EntityMinecart {
   private int minecartTNTFuse = -1;

   public EntityMinecartTNT(World p_i1727_1_) {
      super(EntityType.TNT_MINECART, p_i1727_1_);
   }

   public EntityMinecartTNT(World p_i1728_1_, double p_i1728_2_, double p_i1728_4_, double p_i1728_6_) {
      super(EntityType.TNT_MINECART, p_i1728_1_, p_i1728_2_, p_i1728_4_, p_i1728_6_);
   }

   public EntityMinecart.Type getMinecartType() {
      return EntityMinecart.Type.TNT;
   }

   public IBlockState getDefaultDisplayTile() {
      return Blocks.TNT.getDefaultState();
   }

   public void tick() {
      super.tick();
      if (this.minecartTNTFuse > 0) {
         --this.minecartTNTFuse;
         this.world.spawnParticle(Particles.SMOKE, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
      } else if (this.minecartTNTFuse == 0) {
         this.explodeCart(this.motionX * this.motionX + this.motionZ * this.motionZ);
      }

      if (this.collidedHorizontally) {
         double d0 = this.motionX * this.motionX + this.motionZ * this.motionZ;
         if (d0 >= (double)0.01F) {
            this.explodeCart(d0);
         }
      }

   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      Entity entity = p_70097_1_.getImmediateSource();
      if (entity instanceof EntityArrow) {
         EntityArrow entityarrow = (EntityArrow)entity;
         if (entityarrow.isBurning()) {
            this.explodeCart(entityarrow.motionX * entityarrow.motionX + entityarrow.motionY * entityarrow.motionY + entityarrow.motionZ * entityarrow.motionZ);
         }
      }

      return super.attackEntityFrom(p_70097_1_, p_70097_2_);
   }

   public void killMinecart(DamageSource p_94095_1_) {
      double d0 = this.motionX * this.motionX + this.motionZ * this.motionZ;
      if (!p_94095_1_.isFireDamage() && !p_94095_1_.isExplosion() && !(d0 >= (double)0.01F)) {
         super.killMinecart(p_94095_1_);
         if (!p_94095_1_.isExplosion() && this.world.getGameRules().getBoolean("doEntityDrops")) {
            this.entityDropItem(Blocks.TNT);
         }

      } else {
         if (this.minecartTNTFuse < 0) {
            this.ignite();
            this.minecartTNTFuse = this.rand.nextInt(20) + this.rand.nextInt(20);
         }

      }
   }

   protected void explodeCart(double p_94103_1_) {
      if (!this.world.isRemote) {
         double d0 = Math.sqrt(p_94103_1_);
         if (d0 > 5.0D) {
            d0 = 5.0D;
         }

         this.world.createExplosion(this, this.posX, this.posY, this.posZ, (float)(4.0D + this.rand.nextDouble() * 1.5D * d0), true);
         this.setDead();
      }

   }

   public void fall(float p_180430_1_, float p_180430_2_) {
      if (p_180430_1_ >= 3.0F) {
         float f = p_180430_1_ / 10.0F;
         this.explodeCart((double)(f * f));
      }

      super.fall(p_180430_1_, p_180430_2_);
   }

   public void onActivatorRailPass(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_) {
      if (p_96095_4_ && this.minecartTNTFuse < 0) {
         this.ignite();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 10) {
         this.ignite();
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   public void ignite() {
      this.minecartTNTFuse = 80;
      if (!this.world.isRemote) {
         this.world.setEntityState(this, (byte)10);
         if (!this.isSilent()) {
            this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getFuseTicks() {
      return this.minecartTNTFuse;
   }

   public boolean isIgnited() {
      return this.minecartTNTFuse > -1;
   }

   public float getExplosionResistance(Explosion p_180428_1_, IBlockReader p_180428_2_, BlockPos p_180428_3_, IBlockState p_180428_4_, IFluidState p_180428_5_, float p_180428_6_) {
      return !this.isIgnited() || !p_180428_4_.isIn(BlockTags.RAILS) && !p_180428_2_.getBlockState(p_180428_3_.up()).isIn(BlockTags.RAILS) ? super.getExplosionResistance(p_180428_1_, p_180428_2_, p_180428_3_, p_180428_4_, p_180428_5_, p_180428_6_) : 0.0F;
   }

   public boolean canExplosionDestroyBlock(Explosion p_174816_1_, IBlockReader p_174816_2_, BlockPos p_174816_3_, IBlockState p_174816_4_, float p_174816_5_) {
      return !this.isIgnited() || !p_174816_4_.isIn(BlockTags.RAILS) && !p_174816_2_.getBlockState(p_174816_3_.up()).isIn(BlockTags.RAILS) ? super.canExplosionDestroyBlock(p_174816_1_, p_174816_2_, p_174816_3_, p_174816_4_, p_174816_5_) : false;
   }

   protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      if (p_70037_1_.hasKey("TNTFuse", 99)) {
         this.minecartTNTFuse = p_70037_1_.getInteger("TNTFuse");
      }

   }

   protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("TNTFuse", this.minecartTNTFuse);
   }
}
