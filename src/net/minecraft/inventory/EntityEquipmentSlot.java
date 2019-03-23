package net.minecraft.inventory;

public enum EntityEquipmentSlot {
   MAINHAND(EntityEquipmentSlot.Type.HAND, 0, 0, "mainhand"),
   OFFHAND(EntityEquipmentSlot.Type.HAND, 1, 5, "offhand"),
   FEET(EntityEquipmentSlot.Type.ARMOR, 0, 1, "feet"),
   LEGS(EntityEquipmentSlot.Type.ARMOR, 1, 2, "legs"),
   CHEST(EntityEquipmentSlot.Type.ARMOR, 2, 3, "chest"),
   HEAD(EntityEquipmentSlot.Type.ARMOR, 3, 4, "head");

   private final EntityEquipmentSlot.Type slotType;
   private final int index;
   private final int slotIndex;
   private final String name;

   private EntityEquipmentSlot(EntityEquipmentSlot.Type p_i46808_3_, int p_i46808_4_, int p_i46808_5_, String p_i46808_6_) {
      this.slotType = p_i46808_3_;
      this.index = p_i46808_4_;
      this.slotIndex = p_i46808_5_;
      this.name = p_i46808_6_;
   }

   public EntityEquipmentSlot.Type getSlotType() {
      return this.slotType;
   }

   public int getIndex() {
      return this.index;
   }

   public int getSlotIndex() {
      return this.slotIndex;
   }

   public String getName() {
      return this.name;
   }

   public static EntityEquipmentSlot fromString(String p_188451_0_) {
      for(EntityEquipmentSlot entityequipmentslot : values()) {
         if (entityequipmentslot.getName().equals(p_188451_0_)) {
            return entityequipmentslot;
         }
      }

      throw new IllegalArgumentException("Invalid slot '" + p_188451_0_ + "'");
   }

   public static enum Type {
      HAND,
      ARMOR;
   }
}
