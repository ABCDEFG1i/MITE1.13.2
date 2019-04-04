package net.minecraft.world;

public enum TickPriority {
   EXTREMELY_HIGH(-3),
   VERY_HIGH(-2),
   HIGH(-1),
   NORMAL(0),
   LOW(1),
   VERY_LOW(2),
   EXTREMELY_LOW(3);

   private final int priority;

   TickPriority(int p_i48976_3_) {
      this.priority = p_i48976_3_;
   }

   public static TickPriority getPriority(int p_205397_0_) {
      for(TickPriority tickpriority : values()) {
         if (tickpriority.priority == p_205397_0_) {
            return tickpriority;
         }
      }

      if (p_205397_0_ < EXTREMELY_HIGH.priority) {
         return EXTREMELY_HIGH;
      } else {
         return EXTREMELY_LOW;
      }
   }

   public int getPriority() {
      return this.priority;
   }
}
