package net.minecraft.potion;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class PotionType {
   private final String baseName;
   private final ImmutableList<PotionEffect> effects;

   public static PotionType getPotionTypeForName(String p_185168_0_) {
      return IRegistry.field_212621_j.func_82594_a(ResourceLocation.makeResourceLocation(p_185168_0_));
   }

   public PotionType(PotionEffect... p_i46739_1_) {
      this(null, p_i46739_1_);
   }

   public PotionType(@Nullable String p_i46740_1_, PotionEffect... p_i46740_2_) {
      this.baseName = p_i46740_1_;
      this.effects = ImmutableList.copyOf(p_i46740_2_);
   }

   public String getNamePrefixed(String p_185174_1_) {
      return p_185174_1_ + (this.baseName == null ? IRegistry.field_212621_j.func_177774_c(this).getPath() : this.baseName);
   }

   public List<PotionEffect> getEffects() {
      return this.effects;
   }

   public static void registerPotionTypes() {
      registerPotionType("empty", new PotionType());
      registerPotionType("water", new PotionType());
      registerPotionType("mundane", new PotionType());
      registerPotionType("thick", new PotionType());
      registerPotionType("awkward", new PotionType());
      registerPotionType("night_vision", new PotionType(new PotionEffect(MobEffects.NIGHT_VISION, 3600)));
      registerPotionType("long_night_vision", new PotionType("night_vision", new PotionEffect(MobEffects.NIGHT_VISION, 9600)));
      registerPotionType("invisibility", new PotionType(new PotionEffect(MobEffects.INVISIBILITY, 3600)));
      registerPotionType("long_invisibility", new PotionType("invisibility", new PotionEffect(MobEffects.INVISIBILITY, 9600)));
      registerPotionType("leaping", new PotionType(new PotionEffect(MobEffects.JUMP_BOOST, 3600)));
      registerPotionType("long_leaping", new PotionType("leaping", new PotionEffect(MobEffects.JUMP_BOOST, 9600)));
      registerPotionType("strong_leaping", new PotionType("leaping", new PotionEffect(MobEffects.JUMP_BOOST, 1800, 1)));
      registerPotionType("fire_resistance", new PotionType(new PotionEffect(MobEffects.FIRE_RESISTANCE, 3600)));
      registerPotionType("long_fire_resistance", new PotionType("fire_resistance", new PotionEffect(MobEffects.FIRE_RESISTANCE, 9600)));
      registerPotionType("swiftness", new PotionType(new PotionEffect(MobEffects.SPEED, 3600)));
      registerPotionType("long_swiftness", new PotionType("swiftness", new PotionEffect(MobEffects.SPEED, 9600)));
      registerPotionType("strong_swiftness", new PotionType("swiftness", new PotionEffect(MobEffects.SPEED, 1800, 1)));
      registerPotionType("slowness", new PotionType(new PotionEffect(MobEffects.SLOWNESS, 1800)));
      registerPotionType("long_slowness", new PotionType("slowness", new PotionEffect(MobEffects.SLOWNESS, 4800)));
      registerPotionType("strong_slowness", new PotionType("slowness", new PotionEffect(MobEffects.SLOWNESS, 400, 3)));
      registerPotionType("turtle_master", new PotionType("turtle_master", new PotionEffect(MobEffects.SLOWNESS, 400, 3), new PotionEffect(MobEffects.RESISTANCE, 400, 2)));
      registerPotionType("long_turtle_master", new PotionType("turtle_master", new PotionEffect(MobEffects.SLOWNESS, 800, 3), new PotionEffect(MobEffects.RESISTANCE, 800, 2)));
      registerPotionType("strong_turtle_master", new PotionType("turtle_master", new PotionEffect(MobEffects.SLOWNESS, 400, 5), new PotionEffect(MobEffects.RESISTANCE, 400, 3)));
      registerPotionType("water_breathing", new PotionType(new PotionEffect(MobEffects.WATER_BREATHING, 3600)));
      registerPotionType("long_water_breathing", new PotionType("water_breathing", new PotionEffect(MobEffects.WATER_BREATHING, 9600)));
      registerPotionType("healing", new PotionType(new PotionEffect(MobEffects.INSTANT_HEALTH, 1)));
      registerPotionType("strong_healing", new PotionType("healing", new PotionEffect(MobEffects.INSTANT_HEALTH, 1, 1)));
      registerPotionType("harming", new PotionType(new PotionEffect(MobEffects.INSTANT_DAMAGE, 1)));
      registerPotionType("strong_harming", new PotionType("harming", new PotionEffect(MobEffects.INSTANT_DAMAGE, 1, 1)));
      registerPotionType("poison", new PotionType(new PotionEffect(MobEffects.POISON, 900)));
      registerPotionType("long_poison", new PotionType("poison", new PotionEffect(MobEffects.POISON, 1800)));
      registerPotionType("strong_poison", new PotionType("poison", new PotionEffect(MobEffects.POISON, 432, 1)));
      registerPotionType("regeneration", new PotionType(new PotionEffect(MobEffects.REGENERATION, 900)));
      registerPotionType("long_regeneration", new PotionType("regeneration", new PotionEffect(MobEffects.REGENERATION, 1800)));
      registerPotionType("strong_regeneration", new PotionType("regeneration", new PotionEffect(MobEffects.REGENERATION, 450, 1)));
      registerPotionType("strength", new PotionType(new PotionEffect(MobEffects.STRENGTH, 3600)));
      registerPotionType("long_strength", new PotionType("strength", new PotionEffect(MobEffects.STRENGTH, 9600)));
      registerPotionType("strong_strength", new PotionType("strength", new PotionEffect(MobEffects.STRENGTH, 1800, 1)));
      registerPotionType("weakness", new PotionType(new PotionEffect(MobEffects.WEAKNESS, 1800)));
      registerPotionType("long_weakness", new PotionType("weakness", new PotionEffect(MobEffects.WEAKNESS, 4800)));
      registerPotionType("luck", new PotionType("luck", new PotionEffect(MobEffects.LUCK, 6000)));
      registerPotionType("slow_falling", new PotionType(new PotionEffect(MobEffects.SLOW_FALLING, 1800)));
      registerPotionType("long_slow_falling", new PotionType("slow_falling", new PotionEffect(MobEffects.SLOW_FALLING, 4800)));
   }

   protected static void registerPotionType(String p_185173_0_, PotionType p_185173_1_) {
      IRegistry.field_212621_j.func_82595_a(new ResourceLocation(p_185173_0_), p_185173_1_);
   }

   public boolean hasInstantEffect() {
      if (!this.effects.isEmpty()) {
         for(PotionEffect potioneffect : this.effects) {
            if (potioneffect.getPotion().isInstant()) {
               return true;
            }
         }
      }

      return false;
   }
}
