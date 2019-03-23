package net.minecraft.potion;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Potion {
   private final Map<IAttribute, AttributeModifier> attributeModifierMap = Maps.newHashMap();
   private final boolean isBadEffect;
   private final int liquidColor;
   @Nullable
   private String name;
   private int statusIconIndex = -1;
   private double effectiveness;
   private boolean beneficial;

   @Nullable
   public static Potion getPotionById(int p_188412_0_) {
      return IRegistry.field_212631_t.func_148754_a(p_188412_0_);
   }

   public static int getIdFromPotion(Potion p_188409_0_) {
      return IRegistry.field_212631_t.func_148757_b(p_188409_0_);
   }

   protected Potion(boolean p_i46815_1_, int p_i46815_2_) {
      this.isBadEffect = p_i46815_1_;
      if (p_i46815_1_) {
         this.effectiveness = 0.5D;
      } else {
         this.effectiveness = 1.0D;
      }

      this.liquidColor = p_i46815_2_;
   }

   protected Potion setIconIndex(int p_76399_1_, int p_76399_2_) {
      this.statusIconIndex = p_76399_1_ + p_76399_2_ * 12;
      return this;
   }

   public void performEffect(EntityLivingBase p_76394_1_, int p_76394_2_) {
      if (this == MobEffects.REGENERATION) {
         if (p_76394_1_.getHealth() < p_76394_1_.getMaxHealth()) {
            p_76394_1_.heal(1.0F);
         }
      } else if (this == MobEffects.POISON) {
         if (p_76394_1_.getHealth() > 1.0F) {
            p_76394_1_.attackEntityFrom(DamageSource.MAGIC, 1.0F);
         }
      } else if (this == MobEffects.WITHER) {
         p_76394_1_.attackEntityFrom(DamageSource.WITHER, 1.0F);
      } else if (this == MobEffects.HUNGER && p_76394_1_ instanceof EntityPlayer) {
         ((EntityPlayer)p_76394_1_).addExhaustion(0.005F * (float)(p_76394_2_ + 1));
      } else if (this == MobEffects.SATURATION && p_76394_1_ instanceof EntityPlayer) {
         if (!p_76394_1_.world.isRemote) {
            ((EntityPlayer)p_76394_1_).getFoodStats().addStats(p_76394_2_ + 1, 1.0F);
         }
      } else if ((this != MobEffects.INSTANT_HEALTH || p_76394_1_.isEntityUndead()) && (this != MobEffects.INSTANT_DAMAGE || !p_76394_1_.isEntityUndead())) {
         if (this == MobEffects.INSTANT_DAMAGE && !p_76394_1_.isEntityUndead() || this == MobEffects.INSTANT_HEALTH && p_76394_1_.isEntityUndead()) {
            p_76394_1_.attackEntityFrom(DamageSource.MAGIC, (float)(6 << p_76394_2_));
         }
      } else {
         p_76394_1_.heal((float)Math.max(4 << p_76394_2_, 0));
      }

   }

   public void affectEntity(@Nullable Entity p_180793_1_, @Nullable Entity p_180793_2_, EntityLivingBase p_180793_3_, int p_180793_4_, double p_180793_5_) {
      if ((this != MobEffects.INSTANT_HEALTH || p_180793_3_.isEntityUndead()) && (this != MobEffects.INSTANT_DAMAGE || !p_180793_3_.isEntityUndead())) {
         if (this == MobEffects.INSTANT_DAMAGE && !p_180793_3_.isEntityUndead() || this == MobEffects.INSTANT_HEALTH && p_180793_3_.isEntityUndead()) {
            int j = (int)(p_180793_5_ * (double)(6 << p_180793_4_) + 0.5D);
            if (p_180793_1_ == null) {
               p_180793_3_.attackEntityFrom(DamageSource.MAGIC, (float)j);
            } else {
               p_180793_3_.attackEntityFrom(DamageSource.causeIndirectMagicDamage(p_180793_1_, p_180793_2_), (float)j);
            }
         } else {
            this.performEffect(p_180793_3_, p_180793_4_);
         }
      } else {
         int i = (int)(p_180793_5_ * (double)(4 << p_180793_4_) + 0.5D);
         p_180793_3_.heal((float)i);
      }

   }

   public boolean isReady(int p_76397_1_, int p_76397_2_) {
      if (this == MobEffects.REGENERATION) {
         int k = 50 >> p_76397_2_;
         if (k > 0) {
            return p_76397_1_ % k == 0;
         } else {
            return true;
         }
      } else if (this == MobEffects.POISON) {
         int j = 25 >> p_76397_2_;
         if (j > 0) {
            return p_76397_1_ % j == 0;
         } else {
            return true;
         }
      } else if (this == MobEffects.WITHER) {
         int i = 40 >> p_76397_2_;
         if (i > 0) {
            return p_76397_1_ % i == 0;
         } else {
            return true;
         }
      } else {
         return this == MobEffects.HUNGER;
      }
   }

   public boolean isInstant() {
      return false;
   }

   protected String func_210758_b() {
      if (this.name == null) {
         this.name = Util.makeTranslationKey("effect", IRegistry.field_212631_t.func_177774_c(this));
      }

      return this.name;
   }

   public String getName() {
      return this.func_210758_b();
   }

   public ITextComponent func_199286_c() {
      return new TextComponentTranslation(this.getName());
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasStatusIcon() {
      return this.statusIconIndex >= 0;
   }

   @OnlyIn(Dist.CLIENT)
   public int getStatusIconIndex() {
      return this.statusIconIndex;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isBadEffect() {
      return this.isBadEffect;
   }

   protected Potion setEffectiveness(double p_76404_1_) {
      this.effectiveness = p_76404_1_;
      return this;
   }

   public int getLiquidColor() {
      return this.liquidColor;
   }

   public Potion registerPotionAttributeModifier(IAttribute p_111184_1_, String p_111184_2_, double p_111184_3_, int p_111184_5_) {
      AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(p_111184_2_), this::getName, p_111184_3_, p_111184_5_);
      this.attributeModifierMap.put(p_111184_1_, attributemodifier);
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public Map<IAttribute, AttributeModifier> getAttributeModifierMap() {
      return this.attributeModifierMap;
   }

   public void removeAttributesModifiersFromEntity(EntityLivingBase p_111187_1_, AbstractAttributeMap p_111187_2_, int p_111187_3_) {
      for(Entry<IAttribute, AttributeModifier> entry : this.attributeModifierMap.entrySet()) {
         IAttributeInstance iattributeinstance = p_111187_2_.getAttributeInstance(entry.getKey());
         if (iattributeinstance != null) {
            iattributeinstance.removeModifier(entry.getValue());
         }
      }

   }

   public void applyAttributesModifiersToEntity(EntityLivingBase p_111185_1_, AbstractAttributeMap p_111185_2_, int p_111185_3_) {
      for(Entry<IAttribute, AttributeModifier> entry : this.attributeModifierMap.entrySet()) {
         IAttributeInstance iattributeinstance = p_111185_2_.getAttributeInstance(entry.getKey());
         if (iattributeinstance != null) {
            AttributeModifier attributemodifier = entry.getValue();
            iattributeinstance.removeModifier(attributemodifier);
            iattributeinstance.applyModifier(new AttributeModifier(attributemodifier.getID(), this.getName() + " " + p_111185_3_, this.getAttributeModifierAmount(p_111185_3_, attributemodifier), attributemodifier.getOperation()));
         }
      }

   }

   public double getAttributeModifierAmount(int p_111183_1_, AttributeModifier p_111183_2_) {
      return p_111183_2_.getAmount() * (double)(p_111183_1_ + 1);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isBeneficial() {
      return this.beneficial;
   }

   public Potion setBeneficial() {
      this.beneficial = true;
      return this;
   }

   public static void registerPotions() {
      register(1, "speed", (new Potion(false, 8171462)).setIconIndex(0, 0).registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", (double)0.2F, 2).setBeneficial());
      register(2, "slowness", (new Potion(true, 5926017)).setIconIndex(1, 0).registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", (double)-0.15F, 2));
      register(3, "haste", (new Potion(false, 14270531)).setIconIndex(2, 0).setEffectiveness(1.5D).setBeneficial().registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", (double)0.1F, 2));
      register(4, "mining_fatigue", (new Potion(true, 4866583)).setIconIndex(3, 0).registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", (double)-0.1F, 2));
      register(5, "strength", (new PotionAttackDamage(false, 9643043, 3.0D)).setIconIndex(4, 0).registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0D, 0).setBeneficial());
      register(6, "instant_health", (new PotionInstant(false, 16262179)).setBeneficial());
      register(7, "instant_damage", (new PotionInstant(true, 4393481)).setBeneficial());
      register(8, "jump_boost", (new Potion(false, 2293580)).setIconIndex(2, 1).setBeneficial());
      register(9, "nausea", (new Potion(true, 5578058)).setIconIndex(3, 1).setEffectiveness(0.25D));
      register(10, "regeneration", (new Potion(false, 13458603)).setIconIndex(7, 0).setEffectiveness(0.25D).setBeneficial());
      register(11, "resistance", (new Potion(false, 10044730)).setIconIndex(6, 1).setBeneficial());
      register(12, "fire_resistance", (new Potion(false, 14981690)).setIconIndex(7, 1).setBeneficial());
      register(13, "water_breathing", (new Potion(false, 3035801)).setIconIndex(0, 2).setBeneficial());
      register(14, "invisibility", (new Potion(false, 8356754)).setIconIndex(0, 1).setBeneficial());
      register(15, "blindness", (new Potion(true, 2039587)).setIconIndex(5, 1).setEffectiveness(0.25D));
      register(16, "night_vision", (new Potion(false, 2039713)).setIconIndex(4, 1).setBeneficial());
      register(17, "hunger", (new Potion(true, 5797459)).setIconIndex(1, 1));
      register(18, "weakness", (new PotionAttackDamage(true, 4738376, -4.0D)).setIconIndex(5, 0).registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 0.0D, 0));
      register(19, "poison", (new Potion(true, 5149489)).setIconIndex(6, 0).setEffectiveness(0.25D));
      register(20, "wither", (new Potion(true, 3484199)).setIconIndex(1, 2).setEffectiveness(0.25D));
      register(21, "health_boost", (new PotionHealthBoost(false, 16284963)).setIconIndex(7, 2).registerPotionAttributeModifier(SharedMonsterAttributes.MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0D, 0).setBeneficial());
      register(22, "absorption", (new PotionAbsorption(false, 2445989)).setIconIndex(2, 2).setBeneficial());
      register(23, "saturation", (new PotionInstant(false, 16262179)).setBeneficial());
      register(24, "glowing", (new Potion(false, 9740385)).setIconIndex(4, 2));
      register(25, "levitation", (new Potion(true, 13565951)).setIconIndex(3, 2));
      register(26, "luck", (new Potion(false, 3381504)).setIconIndex(5, 2).setBeneficial().registerPotionAttributeModifier(SharedMonsterAttributes.LUCK, "03C3C89D-7037-4B42-869F-B146BCB64D2E", 1.0D, 0));
      register(27, "unluck", (new Potion(true, 12624973)).setIconIndex(6, 2).registerPotionAttributeModifier(SharedMonsterAttributes.LUCK, "CC5AF142-2BD2-4215-B636-2605AED11727", -1.0D, 0));
      register(28, "slow_falling", (new Potion(false, 16773073)).setIconIndex(8, 0).setBeneficial());
      register(29, "conduit_power", (new Potion(false, 1950417)).setIconIndex(9, 0).setBeneficial());
      register(30, "dolphins_grace", (new Potion(false, 8954814)).setIconIndex(10, 0).setBeneficial());
   }

   private static void register(int p_210759_0_, String p_210759_1_, Potion p_210759_2_) {
      IRegistry.field_212631_t.func_177775_a(p_210759_0_, new ResourceLocation(p_210759_1_), p_210759_2_);
   }
}
