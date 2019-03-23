package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityDragonFireball extends EntityFireball {
   public EntityDragonFireball(World p_i46774_1_) {
      super(EntityType.DRAGON_FIREBALL, p_i46774_1_, 1.0F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public EntityDragonFireball(World p_i46775_1_, double p_i46775_2_, double p_i46775_4_, double p_i46775_6_, double p_i46775_8_, double p_i46775_10_, double p_i46775_12_) {
      super(EntityType.DRAGON_FIREBALL, p_i46775_2_, p_i46775_4_, p_i46775_6_, p_i46775_8_, p_i46775_10_, p_i46775_12_, p_i46775_1_, 1.0F, 1.0F);
   }

   public EntityDragonFireball(World p_i46776_1_, EntityLivingBase p_i46776_2_, double p_i46776_3_, double p_i46776_5_, double p_i46776_7_) {
      super(EntityType.DRAGON_FIREBALL, p_i46776_2_, p_i46776_3_, p_i46776_5_, p_i46776_7_, p_i46776_1_, 1.0F, 1.0F);
   }

   protected void onImpact(RayTraceResult p_70227_1_) {
      if (p_70227_1_.entity == null || !p_70227_1_.entity.isEntityEqual(this.shootingEntity)) {
         if (!this.world.isRemote) {
            List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(4.0D, 2.0D, 4.0D));
            EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.world, this.posX, this.posY, this.posZ);
            entityareaeffectcloud.setOwner(this.shootingEntity);
            entityareaeffectcloud.func_195059_a(Particles.DRAGON_BREATH);
            entityareaeffectcloud.setRadius(3.0F);
            entityareaeffectcloud.setDuration(600);
            entityareaeffectcloud.setRadiusPerTick((7.0F - entityareaeffectcloud.getRadius()) / (float)entityareaeffectcloud.getDuration());
            entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, 1, 1));
            if (!list.isEmpty()) {
               for(EntityLivingBase entitylivingbase : list) {
                  double d0 = this.getDistanceSq(entitylivingbase);
                  if (d0 < 16.0D) {
                     entityareaeffectcloud.setPosition(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ);
                     break;
                  }
               }
            }

            this.world.playEvent(2006, new BlockPos(this.posX, this.posY, this.posZ), 0);
            this.world.spawnEntity(entityareaeffectcloud);
            this.setDead();
         }

      }
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      return false;
   }

   protected IParticleData func_195057_f() {
      return Particles.DRAGON_BREATH;
   }

   protected boolean isFireballFiery() {
      return false;
   }
}
