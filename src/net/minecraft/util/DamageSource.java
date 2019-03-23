package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Explosion;

public class DamageSource {
   public static final DamageSource IN_FIRE = (new DamageSource("inFire")).setFireDamage();
   public static final DamageSource LIGHTNING_BOLT = new DamageSource("lightningBolt");
   public static final DamageSource ON_FIRE = (new DamageSource("onFire")).setDamageBypassesArmor().setFireDamage();
   public static final DamageSource LAVA = (new DamageSource("lava")).setFireDamage();
   public static final DamageSource HOT_FLOOR = (new DamageSource("hotFloor")).setFireDamage();
   public static final DamageSource IN_WALL = (new DamageSource("inWall")).setDamageBypassesArmor();
   public static final DamageSource CRAMMING = (new DamageSource("cramming")).setDamageBypassesArmor();
   public static final DamageSource DROWN = (new DamageSource("drown")).setDamageBypassesArmor();
   public static final DamageSource STARVE = (new DamageSource("starve")).setDamageBypassesArmor().setDamageIsAbsolute();
   public static final DamageSource CACTUS = new DamageSource("cactus");
   public static final DamageSource FALL = (new DamageSource("fall")).setDamageBypassesArmor();
   public static final DamageSource FLY_INTO_WALL = (new DamageSource("flyIntoWall")).setDamageBypassesArmor();
   public static final DamageSource OUT_OF_WORLD = (new DamageSource("outOfWorld")).setDamageBypassesArmor().setDamageAllowedInCreativeMode();
   public static final DamageSource GENERIC = (new DamageSource("generic")).setDamageBypassesArmor();
   public static final DamageSource MAGIC = (new DamageSource("magic")).setDamageBypassesArmor().setMagicDamage();
   public static final DamageSource WITHER = (new DamageSource("wither")).setDamageBypassesArmor();
   public static final DamageSource ANVIL = new DamageSource("anvil");
   public static final DamageSource FALLING_BLOCK = new DamageSource("fallingBlock");
   public static final DamageSource DRAGON_BREATH = (new DamageSource("dragonBreath")).setDamageBypassesArmor();
   public static final DamageSource FIREWORKS = (new DamageSource("fireworks")).setExplosion();
   public static final DamageSource DRYOUT = new DamageSource("dryout");
   private boolean isUnblockable;
   private boolean isDamageAllowedInCreativeMode;
   private boolean damageIsAbsolute;
   private float hungerDamage = 0.1F;
   private boolean fireDamage;
   private boolean projectile;
   private boolean difficultyScaled;
   private boolean magicDamage;
   private boolean explosion;
   public final String damageType;

   public static DamageSource causeMobDamage(EntityLivingBase p_76358_0_) {
      return new EntityDamageSource("mob", p_76358_0_);
   }

   public static DamageSource causeIndirectDamage(Entity p_188403_0_, EntityLivingBase p_188403_1_) {
      return new EntityDamageSourceIndirect("mob", p_188403_0_, p_188403_1_);
   }

   public static DamageSource causePlayerDamage(EntityPlayer p_76365_0_) {
      return new EntityDamageSource("player", p_76365_0_);
   }

   public static DamageSource causeArrowDamage(EntityArrow p_76353_0_, @Nullable Entity p_76353_1_) {
      return (new EntityDamageSourceIndirect("arrow", p_76353_0_, p_76353_1_)).setProjectile();
   }

   public static DamageSource causeTridentDamage(Entity p_203096_0_, @Nullable Entity p_203096_1_) {
      return (new EntityDamageSourceIndirect("trident", p_203096_0_, p_203096_1_)).setProjectile();
   }

   public static DamageSource causeFireballDamage(EntityFireball p_76362_0_, @Nullable Entity p_76362_1_) {
      return p_76362_1_ == null ? (new EntityDamageSourceIndirect("onFire", p_76362_0_, p_76362_0_)).setFireDamage().setProjectile() : (new EntityDamageSourceIndirect("fireball", p_76362_0_, p_76362_1_)).setFireDamage().setProjectile();
   }

   public static DamageSource causeThrownDamage(Entity p_76356_0_, @Nullable Entity p_76356_1_) {
      return (new EntityDamageSourceIndirect("thrown", p_76356_0_, p_76356_1_)).setProjectile();
   }

   public static DamageSource causeIndirectMagicDamage(Entity p_76354_0_, @Nullable Entity p_76354_1_) {
      return (new EntityDamageSourceIndirect("indirectMagic", p_76354_0_, p_76354_1_)).setDamageBypassesArmor().setMagicDamage();
   }

   public static DamageSource causeThornsDamage(Entity p_92087_0_) {
      return (new EntityDamageSource("thorns", p_92087_0_)).setIsThornsDamage().setMagicDamage();
   }

   public static DamageSource causeExplosionDamage(@Nullable Explosion p_94539_0_) {
      return p_94539_0_ != null && p_94539_0_.getExplosivePlacedBy() != null ? (new EntityDamageSource("explosion.player", p_94539_0_.getExplosivePlacedBy())).setDifficultyScaled().setExplosion() : (new DamageSource("explosion")).setDifficultyScaled().setExplosion();
   }

   public static DamageSource causeExplosionDamage(@Nullable EntityLivingBase p_188405_0_) {
      return p_188405_0_ != null ? (new EntityDamageSource("explosion.player", p_188405_0_)).setDifficultyScaled().setExplosion() : (new DamageSource("explosion")).setDifficultyScaled().setExplosion();
   }

   public static DamageSource func_199683_a() {
      return new DamageSourceNetherBed();
   }

   public boolean isProjectile() {
      return this.projectile;
   }

   public DamageSource setProjectile() {
      this.projectile = true;
      return this;
   }

   public boolean isExplosion() {
      return this.explosion;
   }

   public DamageSource setExplosion() {
      this.explosion = true;
      return this;
   }

   public boolean isUnblockable() {
      return this.isUnblockable;
   }

   public float getHungerDamage() {
      return this.hungerDamage;
   }

   public boolean canHarmInCreative() {
      return this.isDamageAllowedInCreativeMode;
   }

   public boolean isDamageAbsolute() {
      return this.damageIsAbsolute;
   }

   public DamageSource(String p_i1566_1_) {
      this.damageType = p_i1566_1_;
   }

   @Nullable
   public Entity getImmediateSource() {
      return this.getTrueSource();
   }

   @Nullable
   public Entity getTrueSource() {
      return null;
   }

   public DamageSource setDamageBypassesArmor() {
      this.isUnblockable = true;
      this.hungerDamage = 0.0F;
      return this;
   }

   public DamageSource setDamageAllowedInCreativeMode() {
      this.isDamageAllowedInCreativeMode = true;
      return this;
   }

   public DamageSource setDamageIsAbsolute() {
      this.damageIsAbsolute = true;
      this.hungerDamage = 0.0F;
      return this;
   }

   public DamageSource setFireDamage() {
      this.fireDamage = true;
      return this;
   }

   public ITextComponent getDeathMessage(EntityLivingBase p_151519_1_) {
      EntityLivingBase entitylivingbase = p_151519_1_.getAttackingEntity();
      String s = "death.attack." + this.damageType;
      String s1 = s + ".player";
      return entitylivingbase != null ? new TextComponentTranslation(s1, p_151519_1_.getDisplayName(), entitylivingbase.getDisplayName()) : new TextComponentTranslation(s, p_151519_1_.getDisplayName());
   }

   public boolean isFireDamage() {
      return this.fireDamage;
   }

   public String getDamageType() {
      return this.damageType;
   }

   public DamageSource setDifficultyScaled() {
      this.difficultyScaled = true;
      return this;
   }

   public boolean isDifficultyScaled() {
      return this.difficultyScaled;
   }

   public boolean isMagicDamage() {
      return this.magicDamage;
   }

   public DamageSource setMagicDamage() {
      this.magicDamage = true;
      return this;
   }

   public boolean isCreativePlayer() {
      Entity entity = this.getTrueSource();
      return entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode;
   }

   @Nullable
   public Vec3d getDamageLocation() {
      return null;
   }
}
