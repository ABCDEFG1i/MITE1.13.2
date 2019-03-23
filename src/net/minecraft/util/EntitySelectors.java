package net.minecraft.util;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;

public final class EntitySelectors {
   public static final Predicate<Entity> IS_ALIVE = Entity::isEntityAlive;
   public static final Predicate<EntityLivingBase> field_212545_b = EntityLivingBase::isEntityAlive;
   public static final Predicate<Entity> IS_STANDALONE = (p_200821_0_) -> {
      return p_200821_0_.isEntityAlive() && !p_200821_0_.isBeingRidden() && !p_200821_0_.isRiding();
   };
   public static final Predicate<Entity> HAS_INVENTORY = (p_200822_0_) -> {
      return p_200822_0_ instanceof IInventory && p_200822_0_.isEntityAlive();
   };
   public static final Predicate<Entity> CAN_AI_TARGET = (p_200824_0_) -> {
      return !(p_200824_0_ instanceof EntityPlayer) || !((EntityPlayer)p_200824_0_).isSpectator() && !((EntityPlayer)p_200824_0_).isCreative();
   };
   public static final Predicate<Entity> NOT_SPECTATING = (p_200818_0_) -> {
      return !(p_200818_0_ instanceof EntityPlayer) || !((EntityPlayer)p_200818_0_).isSpectator();
   };

   public static Predicate<Entity> withinRange(double p_188443_0_, double p_188443_2_, double p_188443_4_, double p_188443_6_) {
      double d0 = p_188443_6_ * p_188443_6_;
      return (p_200819_8_) -> {
         return p_200819_8_ != null && p_200819_8_.getDistanceSq(p_188443_0_, p_188443_2_, p_188443_4_) <= d0;
      };
   }

   public static Predicate<Entity> func_200823_a(Entity p_200823_0_) {
      Team team = p_200823_0_.getTeam();
      Team.CollisionRule team$collisionrule = team == null ? Team.CollisionRule.ALWAYS : team.getCollisionRule();
      return (Predicate<Entity>)(team$collisionrule == Team.CollisionRule.NEVER ? Predicates.alwaysFalse() : NOT_SPECTATING.and((p_210290_3_) -> {
         if (!p_210290_3_.canBePushed()) {
            return false;
         } else if (!p_200823_0_.world.isRemote || p_210290_3_ instanceof EntityPlayer && ((EntityPlayer)p_210290_3_).isUser()) {
            Team team1 = p_210290_3_.getTeam();
            Team.CollisionRule team$collisionrule1 = team1 == null ? Team.CollisionRule.ALWAYS : team1.getCollisionRule();
            if (team$collisionrule1 == Team.CollisionRule.NEVER) {
               return false;
            } else {
               boolean flag = team != null && team.isSameTeam(team1);
               if ((team$collisionrule == Team.CollisionRule.PUSH_OWN_TEAM || team$collisionrule1 == Team.CollisionRule.PUSH_OWN_TEAM) && flag) {
                  return false;
               } else {
                  return team$collisionrule != Team.CollisionRule.PUSH_OTHER_TEAMS && team$collisionrule1 != Team.CollisionRule.PUSH_OTHER_TEAMS || flag;
               }
            }
         } else {
            return false;
         }
      }));
   }

   public static Predicate<Entity> func_200820_b(Entity p_200820_0_) {
      return (p_210289_1_) -> {
         while(true) {
            if (p_210289_1_.isRiding()) {
               p_210289_1_ = p_210289_1_.getRidingEntity();
               if (p_210289_1_ != p_200820_0_) {
                  continue;
               }

               return false;
            }

            return true;
         }
      };
   }

   public static class ArmoredMob implements Predicate<Entity> {
      private final ItemStack armor;

      public ArmoredMob(ItemStack p_i1584_1_) {
         this.armor = p_i1584_1_;
      }

      public boolean test(@Nullable Entity p_test_1_) {
         if (!p_test_1_.isEntityAlive()) {
            return false;
         } else if (!(p_test_1_ instanceof EntityLivingBase)) {
            return false;
         } else {
            EntityLivingBase entitylivingbase = (EntityLivingBase)p_test_1_;
            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(this.armor);
            if (!entitylivingbase.getItemStackFromSlot(entityequipmentslot).isEmpty()) {
               return false;
            } else if (entitylivingbase instanceof EntityLiving) {
               return ((EntityLiving)entitylivingbase).canPickUpLoot();
            } else if (entitylivingbase instanceof EntityArmorStand) {
               return !((EntityArmorStand)entitylivingbase).isDisabled(entityequipmentslot);
            } else {
               return entitylivingbase instanceof EntityPlayer;
            }
         }
      }
   }
}
