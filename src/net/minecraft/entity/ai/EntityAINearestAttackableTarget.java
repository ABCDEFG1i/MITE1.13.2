package net.minecraft.entity.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAINearestAttackableTarget<T extends EntityLivingBase> extends EntityAITarget {
   protected final Class<T> targetClass;
   private final int targetChance;
   protected final EntityAINearestAttackableTarget.Sorter sorter;
   protected final Predicate<? super T> targetEntitySelector;
   protected T targetEntity;

   public EntityAINearestAttackableTarget(EntityCreature p_i45878_1_, Class<T> p_i45878_2_, boolean p_i45878_3_) {
      this(p_i45878_1_, p_i45878_2_, p_i45878_3_, false);
   }

   public EntityAINearestAttackableTarget(EntityCreature p_i45879_1_, Class<T> p_i45879_2_, boolean p_i45879_3_, boolean p_i45879_4_) {
      this(p_i45879_1_, p_i45879_2_, 10, p_i45879_3_, p_i45879_4_, (Predicate<T>)null);
   }

   public EntityAINearestAttackableTarget(EntityCreature p_i48572_1_, Class<T> p_i48572_2_, int p_i48572_3_, boolean p_i48572_4_, boolean p_i48572_5_, @Nullable Predicate<? super T> p_i48572_6_) {
      super(p_i48572_1_, p_i48572_4_, p_i48572_5_);
      this.targetClass = p_i48572_2_;
      this.targetChance = p_i48572_3_;
      this.sorter = new EntityAINearestAttackableTarget.Sorter(p_i48572_1_);
      this.setMutexBits(1);
      this.targetEntitySelector = (p_210294_2_) -> {
         if (p_210294_2_ == null) {
            return false;
         } else if (p_i48572_6_ != null && !p_i48572_6_.test(p_210294_2_)) {
            return false;
         } else {
            return !EntitySelectors.NOT_SPECTATING.test(p_210294_2_) ? false : this.isSuitableTarget(p_210294_2_, false);
         }
      };
   }

   public boolean shouldExecute() {
      if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
         return false;
      } else if (this.targetClass != EntityPlayer.class && this.targetClass != EntityPlayerMP.class) {
         List<T> list = this.taskOwner.world.getEntitiesWithinAABB(this.targetClass, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
         if (list.isEmpty()) {
            return false;
         } else {
            Collections.sort(list, this.sorter);
            this.targetEntity = list.get(0);
            return true;
         }
      } else {
         this.targetEntity = (T)this.taskOwner.world.getNearestAttackablePlayer(this.taskOwner.posX, this.taskOwner.posY + (double)this.taskOwner.getEyeHeight(), this.taskOwner.posZ, this.getTargetDistance(), this.getTargetDistance(), new Function<EntityPlayer, Double>() {
            @Nullable
            public Double apply(@Nullable EntityPlayer p_apply_1_) {
               ItemStack itemstack = p_apply_1_.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
               return (!(EntityAINearestAttackableTarget.this.taskOwner instanceof EntitySkeleton) || itemstack.getItem() != Items.SKELETON_SKULL) && (!(EntityAINearestAttackableTarget.this.taskOwner instanceof EntityZombie) || itemstack.getItem() != Items.ZOMBIE_HEAD) && (!(EntityAINearestAttackableTarget.this.taskOwner instanceof EntityCreeper) || itemstack.getItem() != Items.CREEPER_HEAD) ? 1.0D : 0.5D;
            }
         }, (Predicate<EntityPlayer>)this.targetEntitySelector);
         return this.targetEntity != null;
      }
   }

   protected AxisAlignedBB getTargetableArea(double p_188511_1_) {
      return this.taskOwner.getEntityBoundingBox().grow(p_188511_1_, 4.0D, p_188511_1_);
   }

   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.targetEntity);
      super.startExecuting();
   }

   public static class Sorter implements Comparator<Entity> {
      private final Entity entity;

      public Sorter(Entity p_i1662_1_) {
         this.entity = p_i1662_1_;
      }

      public int compare(Entity p_compare_1_, Entity p_compare_2_) {
         double d0 = this.entity.getDistanceSq(p_compare_1_);
         double d1 = this.entity.getDistanceSq(p_compare_2_);
         if (d0 < d1) {
            return -1;
         } else {
            return d0 > d1 ? 1 : 0;
         }
      }
   }
}
