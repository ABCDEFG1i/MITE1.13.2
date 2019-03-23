package net.minecraft.entity.passive;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAIFollowGroupLeader;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public abstract class AbstractGroupFish extends AbstractFish {
   private AbstractGroupFish field_212813_a;
   private int field_212814_b = 1;

   public AbstractGroupFish(EntityType<?> p_i49856_1_, World p_i49856_2_) {
      super(p_i49856_1_, p_i49856_2_);
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(5, new EntityAIFollowGroupLeader(this));
   }

   public int getMaxSpawnedInChunk() {
      return this.func_203704_dv();
   }

   public int func_203704_dv() {
      return super.getMaxSpawnedInChunk();
   }

   protected boolean func_212800_dy() {
      return !this.func_212802_dB();
   }

   public boolean func_212802_dB() {
      return this.field_212813_a != null && this.field_212813_a.isEntityAlive();
   }

   public AbstractGroupFish func_212803_a(AbstractGroupFish p_212803_1_) {
      this.field_212813_a = p_212803_1_;
      p_212803_1_.func_212807_dH();
      return p_212803_1_;
   }

   public void func_212808_dC() {
      this.field_212813_a.func_212806_dI();
      this.field_212813_a = null;
   }

   private void func_212807_dH() {
      ++this.field_212814_b;
   }

   private void func_212806_dI() {
      --this.field_212814_b;
   }

   public boolean func_212811_dD() {
      return this.func_212812_dE() && this.field_212814_b < this.func_203704_dv();
   }

   public void tick() {
      super.tick();
      if (this.func_212812_dE() && this.world.rand.nextInt(200) == 1) {
         List<AbstractFish> list = this.world.getEntitiesWithinAABB(this.getClass(), this.getEntityBoundingBox().grow(8.0D, 8.0D, 8.0D));
         if (list.size() <= 1) {
            this.field_212814_b = 1;
         }
      }

   }

   public boolean func_212812_dE() {
      return this.field_212814_b > 1;
   }

   public boolean func_212809_dF() {
      return this.getDistanceSq(this.field_212813_a) <= 121.0D;
   }

   public void func_212805_dG() {
      if (this.func_212802_dB()) {
         this.getNavigator().tryMoveToEntityLiving(this.field_212813_a, 1.0D);
      }

   }

   public void func_212810_a(Stream<AbstractGroupFish> p_212810_1_) {
      p_212810_1_.limit((long)(this.func_203704_dv() - this.field_212814_b)).filter((p_212801_1_) -> {
         return p_212801_1_ != this;
      }).forEach((p_212804_1_) -> {
         p_212804_1_.func_212803_a(this);
      });
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      super.onInitialSpawn(p_204210_1_, p_204210_2_, p_204210_3_);
      if (p_204210_2_ == null) {
         p_204210_2_ = new AbstractGroupFish.GroupData(this);
      } else {
         this.func_212803_a(((AbstractGroupFish.GroupData)p_204210_2_).field_212822_a);
      }

      return p_204210_2_;
   }

   public static class GroupData implements IEntityLivingData {
      public final AbstractGroupFish field_212822_a;

      public GroupData(AbstractGroupFish p_i49858_1_) {
         this.field_212822_a = p_i49858_1_;
      }
   }
}
