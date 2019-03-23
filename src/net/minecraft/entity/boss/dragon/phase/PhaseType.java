package net.minecraft.entity.boss.dragon.phase;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import net.minecraft.entity.boss.EntityDragon;

public class PhaseType<T extends IPhase> {
   private static PhaseType<?>[] phases = new PhaseType[0];
   public static final PhaseType<PhaseHoldingPattern> HOLDING_PATTERN = create(PhaseHoldingPattern.class, "HoldingPattern");
   public static final PhaseType<PhaseStrafePlayer> STRAFE_PLAYER = create(PhaseStrafePlayer.class, "StrafePlayer");
   public static final PhaseType<PhaseLandingApproach> LANDING_APPROACH = create(PhaseLandingApproach.class, "LandingApproach");
   public static final PhaseType<PhaseLanding> LANDING = create(PhaseLanding.class, "Landing");
   public static final PhaseType<PhaseTakeoff> TAKEOFF = create(PhaseTakeoff.class, "Takeoff");
   public static final PhaseType<PhaseSittingFlaming> SITTING_FLAMING = create(PhaseSittingFlaming.class, "SittingFlaming");
   public static final PhaseType<PhaseSittingScanning> SITTING_SCANNING = create(PhaseSittingScanning.class, "SittingScanning");
   public static final PhaseType<PhaseSittingAttacking> SITTING_ATTACKING = create(PhaseSittingAttacking.class, "SittingAttacking");
   public static final PhaseType<PhaseChargingPlayer> CHARGING_PLAYER = create(PhaseChargingPlayer.class, "ChargingPlayer");
   public static final PhaseType<PhaseDying> DYING = create(PhaseDying.class, "Dying");
   public static final PhaseType<PhaseHover> HOVER = create(PhaseHover.class, "Hover");
   private final Class<? extends IPhase> clazz;
   private final int id;
   private final String name;

   private PhaseType(int p_i46782_1_, Class<? extends IPhase> p_i46782_2_, String p_i46782_3_) {
      this.id = p_i46782_1_;
      this.clazz = p_i46782_2_;
      this.name = p_i46782_3_;
   }

   public IPhase createPhase(EntityDragon p_188736_1_) {
      try {
         Constructor<? extends IPhase> constructor = this.getConstructor();
         return constructor.newInstance(p_188736_1_);
      } catch (Exception exception) {
         throw new Error(exception);
      }
   }

   protected Constructor<? extends IPhase> getConstructor() throws NoSuchMethodException {
      return this.clazz.getConstructor(EntityDragon.class);
   }

   public int getId() {
      return this.id;
   }

   public String toString() {
      return this.name + " (#" + this.id + ")";
   }

   public static PhaseType<?> getById(int p_188738_0_) {
      return p_188738_0_ >= 0 && p_188738_0_ < phases.length ? phases[p_188738_0_] : HOLDING_PATTERN;
   }

   public static int getTotalPhases() {
      return phases.length;
   }

   private static <T extends IPhase> PhaseType<T> create(Class<T> p_188735_0_, String p_188735_1_) {
      PhaseType<T> phasetype = new PhaseType<>(phases.length, p_188735_0_, p_188735_1_);
      phases = Arrays.copyOf(phases, phases.length + 1);
      phases[phasetype.getId()] = phasetype;
      return phasetype;
   }
}
