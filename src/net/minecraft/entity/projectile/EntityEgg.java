package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityEgg extends EntityThrowable {
   public EntityEgg(World p_i1779_1_) {
      super(EntityType.EGG, p_i1779_1_);
   }

   public EntityEgg(World p_i1780_1_, EntityLivingBase p_i1780_2_) {
      super(EntityType.EGG, p_i1780_2_, p_i1780_1_);
   }

   public EntityEgg(World p_i1781_1_, double p_i1781_2_, double p_i1781_4_, double p_i1781_6_) {
      super(EntityType.EGG, p_i1781_2_, p_i1781_4_, p_i1781_6_, p_i1781_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 3) {
         double d0 = 0.08D;

         for(int i = 0; i < 8; ++i) {
            this.world.spawnParticle(new ItemParticleData(Particles.ITEM, new ItemStack(Items.EGG)), this.posX, this.posY, this.posZ, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D);
         }
      }

   }

   protected void onImpact(RayTraceResult p_70184_1_) {
      if (p_70184_1_.entity != null) {
         p_70184_1_.entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
      }

      if (!this.world.isRemote) {
         if (this.rand.nextInt(8) == 0) {
            int i = 1;
            if (this.rand.nextInt(32) == 0) {
               i = 4;
            }

            for(int j = 0; j < i; ++j) {
               EntityChicken entitychicken = new EntityChicken(this.world);
               entitychicken.setGrowingAge(-24000);
               entitychicken.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
               this.world.spawnEntity(entitychicken);
            }
         }

         this.world.setEntityState(this, (byte)3);
         this.setDead();
      }

   }
}
