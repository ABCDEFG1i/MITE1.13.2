package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class PotionUtil {
   @OnlyIn(Dist.CLIENT)
   public static String getPotionDurationString(PotionEffect p_188410_0_, float p_188410_1_) {
      if (p_188410_0_.getIsPotionDurationMax()) {
         return "**:**";
      } else {
         int i = MathHelper.floor((float)p_188410_0_.getDuration() * p_188410_1_);
         return StringUtils.ticksToElapsedTime(i);
      }
   }

   public static boolean func_205135_a(EntityLivingBase p_205135_0_) {
      return p_205135_0_.isPotionActive(MobEffects.HASTE) || p_205135_0_.isPotionActive(MobEffects.CONDUIT_POWER);
   }

   public static int func_205134_b(EntityLivingBase p_205134_0_) {
      int i = 0;
      int j = 0;
      if (p_205134_0_.isPotionActive(MobEffects.HASTE)) {
         i = p_205134_0_.getActivePotionEffect(MobEffects.HASTE).getAmplifier();
      }

      if (p_205134_0_.isPotionActive(MobEffects.CONDUIT_POWER)) {
         j = p_205134_0_.getActivePotionEffect(MobEffects.CONDUIT_POWER).getAmplifier();
      }

      return Math.max(i, j);
   }

   public static boolean canBreatheUnderwater(EntityLivingBase p_205133_0_) {
      return p_205133_0_.isPotionActive(MobEffects.WATER_BREATHING) || p_205133_0_.isPotionActive(MobEffects.CONDUIT_POWER);
   }
}
