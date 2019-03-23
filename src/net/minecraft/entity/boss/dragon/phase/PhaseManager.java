package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.EntityDragon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PhaseManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final EntityDragon dragon;
   private final IPhase[] phases = new IPhase[PhaseType.getTotalPhases()];
   private IPhase phase;

   public PhaseManager(EntityDragon p_i46781_1_) {
      this.dragon = p_i46781_1_;
      this.setPhase(PhaseType.HOVER);
   }

   public void setPhase(PhaseType<?> p_188758_1_) {
      if (this.phase == null || p_188758_1_ != this.phase.getType()) {
         if (this.phase != null) {
            this.phase.removeAreaEffect();
         }

         this.phase = this.getPhase(p_188758_1_);
         if (!this.dragon.world.isRemote) {
            this.dragon.getDataManager().set(EntityDragon.PHASE, p_188758_1_.getId());
         }

         LOGGER.debug("Dragon is now in phase {} on the {}", p_188758_1_, this.dragon.world.isRemote ? "client" : "server");
         this.phase.initPhase();
      }
   }

   public IPhase getCurrentPhase() {
      return this.phase;
   }

   public <T extends IPhase> T getPhase(PhaseType<T> p_188757_1_) {
      int i = p_188757_1_.getId();
      if (this.phases[i] == null) {
         this.phases[i] = p_188757_1_.createPhase(this.dragon);
      }

      return (T)this.phases[i];
   }
}
