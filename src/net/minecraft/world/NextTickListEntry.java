package net.minecraft.world;

import net.minecraft.util.math.BlockPos;

public class NextTickListEntry<T> implements Comparable<NextTickListEntry<T>> {
   private static long nextTickEntryID;
   private final T target;
   public final BlockPos position;
   public final long scheduledTime;
   public final TickPriority priority;
   private final long tickEntryID;

   public NextTickListEntry(BlockPos p_i48977_1_, T p_i48977_2_) {
      this(p_i48977_1_, p_i48977_2_, 0L, TickPriority.NORMAL);
   }

   public NextTickListEntry(BlockPos p_i48978_1_, T p_i48978_2_, long p_i48978_3_, TickPriority p_i48978_5_) {
      this.tickEntryID = (long)(nextTickEntryID++);
      this.position = p_i48978_1_.toImmutable();
      this.target = p_i48978_2_;
      this.scheduledTime = p_i48978_3_;
      this.priority = p_i48978_5_;
   }

   public boolean equals(Object p_equals_1_) {
      if (!(p_equals_1_ instanceof NextTickListEntry)) {
         return false;
      } else {
         NextTickListEntry nextticklistentry = (NextTickListEntry)p_equals_1_;
         return this.position.equals(nextticklistentry.position) && this.target == nextticklistentry.target;
      }
   }

   public int hashCode() {
      return this.position.hashCode();
   }

   public int compareTo(NextTickListEntry p_compareTo_1_) {
      if (this.scheduledTime < p_compareTo_1_.scheduledTime) {
         return -1;
      } else if (this.scheduledTime > p_compareTo_1_.scheduledTime) {
         return 1;
      } else if (this.priority.ordinal() < p_compareTo_1_.priority.ordinal()) {
         return -1;
      } else if (this.priority.ordinal() > p_compareTo_1_.priority.ordinal()) {
         return 1;
      } else if (this.tickEntryID < p_compareTo_1_.tickEntryID) {
         return -1;
      } else {
         return this.tickEntryID > p_compareTo_1_.tickEntryID ? 1 : 0;
      }
   }

   public String toString() {
      return this.target + ": " + this.position + ", " + this.scheduledTime + ", " + this.priority + ", " + this.tickEntryID;
   }

   public T getTarget() {
      return this.target;
   }
}
