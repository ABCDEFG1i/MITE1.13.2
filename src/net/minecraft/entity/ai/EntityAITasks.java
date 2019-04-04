package net.minecraft.entity.ai;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAITasks {
   private static final Logger LOGGER = LogManager.getLogger();
   public final Set<EntityAITasks.EntityAITaskEntry> taskEntries = Sets.newLinkedHashSet();
   private final Set<EntityAITasks.EntityAITaskEntry> executingTaskEntries = Sets.newLinkedHashSet();
   private final Profiler profiler;
   private int tickCount;
   private int tickRate = 3;
   private int disabledControlFlags;

   public EntityAITasks(Profiler p_i1628_1_) {
      this.profiler = p_i1628_1_;
   }

   public void addTask(int p_75776_1_, EntityAIBase p_75776_2_) {
      this.taskEntries.add(new EntityAITasks.EntityAITaskEntry(p_75776_1_, p_75776_2_));
   }

   public void removeTask(EntityAIBase p_85156_1_) {
      Iterator<EntityAITasks.EntityAITaskEntry> iterator = this.taskEntries.iterator();

      while(iterator.hasNext()) {
         EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry = iterator.next();
         EntityAIBase entityaibase = entityaitasks$entityaitaskentry.action;
         if (entityaibase == p_85156_1_) {
            if (entityaitasks$entityaitaskentry.using) {
               entityaitasks$entityaitaskentry.using = false;
               entityaitasks$entityaitaskentry.action.resetTask();
               this.executingTaskEntries.remove(entityaitasks$entityaitaskentry);
            }

            iterator.remove();
            return;
         }
      }

   }

   public void tick() {
      this.profiler.startSection("goalSetup");
      if (this.tickCount++ % this.tickRate == 0) {
         for(EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry : this.taskEntries) {
            if (entityaitasks$entityaitaskentry.using) {
               if (!this.canUse(entityaitasks$entityaitaskentry) || !this.canContinue(entityaitasks$entityaitaskentry)) {
                  entityaitasks$entityaitaskentry.using = false;
                  entityaitasks$entityaitaskentry.action.resetTask();
                  this.executingTaskEntries.remove(entityaitasks$entityaitaskentry);
               }
            } else if (this.canUse(entityaitasks$entityaitaskentry) && entityaitasks$entityaitaskentry.action.shouldExecute()) {
               entityaitasks$entityaitaskentry.using = true;
               entityaitasks$entityaitaskentry.action.startExecuting();
               this.executingTaskEntries.add(entityaitasks$entityaitaskentry);
            }
         }
      } else {
         Iterator<EntityAITasks.EntityAITaskEntry> iterator = this.executingTaskEntries.iterator();

         while(iterator.hasNext()) {
            EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry1 = iterator.next();
            if (!this.canContinue(entityaitasks$entityaitaskentry1)) {
               entityaitasks$entityaitaskentry1.using = false;
               entityaitasks$entityaitaskentry1.action.resetTask();
               iterator.remove();
            }
         }
      }

      this.profiler.endSection();
      if (!this.executingTaskEntries.isEmpty()) {
         this.profiler.startSection("goalTick");

         for(EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry2 : this.executingTaskEntries) {
            entityaitasks$entityaitaskentry2.action.updateTask();
         }

         this.profiler.endSection();
      }

   }

   private boolean canContinue(EntityAITasks.EntityAITaskEntry p_75773_1_) {
      return p_75773_1_.action.shouldContinueExecuting();
   }

   private boolean canUse(EntityAITasks.EntityAITaskEntry p_75775_1_) {
      if (this.executingTaskEntries.isEmpty()) {
         return true;
      } else if (this.isControlFlagDisabled(p_75775_1_.action.getMutexBits())) {
         return false;
      } else {
         for(EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry : this.executingTaskEntries) {
            if (entityaitasks$entityaitaskentry != p_75775_1_) {
               if (p_75775_1_.priority >= entityaitasks$entityaitaskentry.priority) {
                  if (!this.areTasksCompatible(p_75775_1_, entityaitasks$entityaitaskentry)) {
                     return false;
                  }
               } else if (!entityaitasks$entityaitaskentry.action.isInterruptible()) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   private boolean areTasksCompatible(EntityAITasks.EntityAITaskEntry p_75777_1_, EntityAITasks.EntityAITaskEntry p_75777_2_) {
      return (p_75777_1_.action.getMutexBits() & p_75777_2_.action.getMutexBits()) == 0;
   }

   public boolean isControlFlagDisabled(int p_188528_1_) {
      return (this.disabledControlFlags & p_188528_1_) > 0;
   }

   public void disableControlFlag(int p_188526_1_) {
      this.disabledControlFlags |= p_188526_1_;
   }

   public void enableControlFlag(int p_188525_1_) {
      this.disabledControlFlags &= ~p_188525_1_;
   }

   public void setControlFlag(int p_188527_1_, boolean p_188527_2_) {
      if (p_188527_2_) {
         this.enableControlFlag(p_188527_1_);
      } else {
         this.disableControlFlag(p_188527_1_);
      }

   }

   public class EntityAITaskEntry {
      public final EntityAIBase action;
      public final int priority;
      public boolean using;

      public EntityAITaskEntry(int p_i1627_2_, EntityAIBase p_i1627_3_) {
         this.priority = p_i1627_2_;
         this.action = p_i1627_3_;
      }

      public boolean equals(@Nullable Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else {
            return (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) && this.action.equals(
                    ((EntityAITaskEntry) p_equals_1_).action);
         }
      }

      public int hashCode() {
         return this.action.hashCode();
      }
   }
}
