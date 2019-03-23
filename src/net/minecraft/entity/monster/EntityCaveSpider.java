package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityCaveSpider extends EntitySpider {
   public EntityCaveSpider(World p_i1732_1_) {
      super(EntityType.CAVE_SPIDER, p_i1732_1_);
      this.setSize(0.7F, 0.5F);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      if (super.attackEntityAsMob(p_70652_1_)) {
         if (p_70652_1_ instanceof EntityLivingBase) {
            int i = 0;
            if (this.world.getDifficulty() == EnumDifficulty.NORMAL) {
               i = 7;
            } else if (this.world.getDifficulty() == EnumDifficulty.HARD) {
               i = 15;
            }

            if (i > 0) {
               ((EntityLivingBase)p_70652_1_).addPotionEffect(new PotionEffect(MobEffects.POISON, i * 20, 0));
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      return p_204210_2_;
   }

   public float getEyeHeight() {
      return 0.45F;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_CAVE_SPIDER;
   }
}
