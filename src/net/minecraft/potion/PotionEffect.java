package net.minecraft.potion;

import com.google.common.collect.ComparisonChain;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PotionEffect implements Comparable<PotionEffect> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Potion potion;
   private int duration;
   private int amplifier;
   private boolean isSplashPotion;
   private boolean ambient;
   @OnlyIn(Dist.CLIENT)
   private boolean isPotionDurationMax;
   private boolean showParticles;
   private boolean field_205349_i;

   public PotionEffect(Potion p_i46811_1_) {
      this(p_i46811_1_, 0, 0);
   }

   public PotionEffect(Potion p_i46812_1_, int p_i46812_2_) {
      this(p_i46812_1_, p_i46812_2_, 0);
   }

   public PotionEffect(Potion p_i46813_1_, int p_i46813_2_, int p_i46813_3_) {
      this(p_i46813_1_, p_i46813_2_, p_i46813_3_, false, true);
   }

   public PotionEffect(Potion p_i46814_1_, int p_i46814_2_, int p_i46814_3_, boolean p_i46814_4_, boolean p_i46814_5_) {
      this(p_i46814_1_, p_i46814_2_, p_i46814_3_, p_i46814_4_, p_i46814_5_, p_i46814_5_);
   }

   public PotionEffect(Potion p_i48980_1_, int p_i48980_2_, int p_i48980_3_, boolean p_i48980_4_, boolean p_i48980_5_, boolean p_i48980_6_) {
      this.potion = p_i48980_1_;
      this.duration = p_i48980_2_;
      this.amplifier = p_i48980_3_;
      this.ambient = p_i48980_4_;
      this.showParticles = p_i48980_5_;
      this.field_205349_i = p_i48980_6_;
   }

   public PotionEffect(PotionEffect p_i1577_1_) {
      this.potion = p_i1577_1_.potion;
      this.duration = p_i1577_1_.duration;
      this.amplifier = p_i1577_1_.amplifier;
      this.ambient = p_i1577_1_.ambient;
      this.showParticles = p_i1577_1_.showParticles;
      this.field_205349_i = p_i1577_1_.field_205349_i;
   }

   public boolean func_199308_a(PotionEffect p_199308_1_) {
      if (this.potion != p_199308_1_.potion) {
         LOGGER.warn("This method should only be called for matching effects!");
      }

      boolean flag = false;
      if (p_199308_1_.amplifier > this.amplifier) {
         this.amplifier = p_199308_1_.amplifier;
         this.duration = p_199308_1_.duration;
         flag = true;
      } else if (p_199308_1_.amplifier == this.amplifier && this.duration < p_199308_1_.duration) {
         this.duration = p_199308_1_.duration;
         flag = true;
      }

      if (!p_199308_1_.ambient && this.ambient || flag) {
         this.ambient = p_199308_1_.ambient;
         flag = true;
      }

      if (p_199308_1_.showParticles != this.showParticles) {
         this.showParticles = p_199308_1_.showParticles;
         flag = true;
      }

      if (p_199308_1_.field_205349_i != this.field_205349_i) {
         this.field_205349_i = p_199308_1_.field_205349_i;
         flag = true;
      }

      return flag;
   }

   public Potion getPotion() {
      return this.potion;
   }

   public int getDuration() {
      return this.duration;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public boolean isAmbient() {
      return this.ambient;
   }

   public boolean doesShowParticles() {
      return this.showParticles;
   }

   public boolean func_205348_f() {
      return this.field_205349_i;
   }

   public boolean tick(EntityLivingBase p_76455_1_) {
      if (this.duration > 0) {
         if (this.potion.isReady(this.duration, this.amplifier)) {
            this.performEffect(p_76455_1_);
         }

         this.deincrementDuration();
      }

      return this.duration > 0;
   }

   private int deincrementDuration() {
      return --this.duration;
   }

   public void performEffect(EntityLivingBase p_76457_1_) {
      if (this.duration > 0) {
         this.potion.performEffect(p_76457_1_, this.amplifier);
      }

   }

   public String getEffectName() {
      return this.potion.getName();
   }

   public String toString() {
      String s;
      if (this.amplifier > 0) {
         s = this.getEffectName() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
      } else {
         s = this.getEffectName() + ", Duration: " + this.duration;
      }

      if (this.isSplashPotion) {
         s = s + ", Splash: true";
      }

      if (!this.showParticles) {
         s = s + ", Particles: false";
      }

      if (!this.field_205349_i) {
         s = s + ", Show Icon: false";
      }

      return s;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof PotionEffect)) {
         return false;
      } else {
         PotionEffect potioneffect = (PotionEffect)p_equals_1_;
         return this.duration == potioneffect.duration && this.amplifier == potioneffect.amplifier && this.isSplashPotion == potioneffect.isSplashPotion && this.ambient == potioneffect.ambient && this.potion.equals(potioneffect.potion);
      }
   }

   public int hashCode() {
      int i = this.potion.hashCode();
      i = 31 * i + this.duration;
      i = 31 * i + this.amplifier;
      i = 31 * i + (this.isSplashPotion ? 1 : 0);
      i = 31 * i + (this.ambient ? 1 : 0);
      return i;
   }

   public NBTTagCompound write(NBTTagCompound p_82719_1_) {
      p_82719_1_.setByte("Id", (byte)Potion.getIdFromPotion(this.getPotion()));
      p_82719_1_.setByte("Amplifier", (byte)this.getAmplifier());
      p_82719_1_.setInteger("Duration", this.getDuration());
      p_82719_1_.setBoolean("Ambient", this.isAmbient());
      p_82719_1_.setBoolean("ShowParticles", this.doesShowParticles());
      p_82719_1_.setBoolean("ShowIcon", this.func_205348_f());
      return p_82719_1_;
   }

   public static PotionEffect read(NBTTagCompound p_82722_0_) {
      int i = p_82722_0_.getByte("Id");
      Potion potion = Potion.getPotionById(i);
      if (potion == null) {
         return null;
      } else {
         int j = p_82722_0_.getByte("Amplifier");
         int k = p_82722_0_.getInteger("Duration");
         boolean flag = p_82722_0_.getBoolean("Ambient");
         boolean flag1 = true;
         if (p_82722_0_.hasKey("ShowParticles", 1)) {
            flag1 = p_82722_0_.getBoolean("ShowParticles");
         }

         boolean flag2 = flag1;
         if (p_82722_0_.hasKey("ShowIcon", 1)) {
            flag2 = p_82722_0_.getBoolean("ShowIcon");
         }

         return new PotionEffect(potion, k, j < 0 ? 0 : j, flag, flag1, flag2);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void setPotionDurationMax(boolean p_100012_1_) {
      this.isPotionDurationMax = p_100012_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean getIsPotionDurationMax() {
      return this.isPotionDurationMax;
   }

   public int compareTo(PotionEffect p_compareTo_1_) {
      int i = 32147;
      return (this.getDuration() <= 32147 || p_compareTo_1_.getDuration() <= 32147) && (!this.isAmbient() || !p_compareTo_1_.isAmbient()) ? ComparisonChain.start().compare(this.isAmbient(), p_compareTo_1_.isAmbient()).compare(this.getDuration(), p_compareTo_1_.getDuration()).compare(this.getPotion().getLiquidColor(), p_compareTo_1_.getPotion().getLiquidColor()).result() : ComparisonChain.start().compare(this.isAmbient(), p_compareTo_1_.isAmbient()).compare(this.getPotion().getLiquidColor(), p_compareTo_1_.getPotion().getLiquidColor()).result();
   }
}
