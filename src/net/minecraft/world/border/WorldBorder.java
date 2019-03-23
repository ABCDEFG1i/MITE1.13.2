package net.minecraft.world.border;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WorldBorder {
   private final List<IBorderListener> listeners = Lists.newArrayList();
   private double damageAmount = 0.2D;
   private double damageBuffer = 5.0D;
   private int warningTime = 15;
   private int warningDistance = 5;
   private double centerX;
   private double centerZ;
   private int worldSize = 29999984;
   private WorldBorder.IBorderInfo field_212674_i = new WorldBorder.StationaryBorderInfo(6.0E7D);

   public boolean contains(BlockPos p_177746_1_) {
      return (double)(p_177746_1_.getX() + 1) > this.minX() && (double)p_177746_1_.getX() < this.maxX() && (double)(p_177746_1_.getZ() + 1) > this.minZ() && (double)p_177746_1_.getZ() < this.maxZ();
   }

   public boolean contains(ChunkPos p_177730_1_) {
      return (double)p_177730_1_.getXEnd() > this.minX() && (double)p_177730_1_.getXStart() < this.maxX() && (double)p_177730_1_.getZEnd() > this.minZ() && (double)p_177730_1_.getZStart() < this.maxZ();
   }

   public boolean contains(AxisAlignedBB p_177743_1_) {
      return p_177743_1_.maxX > this.minX() && p_177743_1_.minX < this.maxX() && p_177743_1_.maxZ > this.minZ() && p_177743_1_.minZ < this.maxZ();
   }

   public double getClosestDistance(Entity p_177745_1_) {
      return this.getClosestDistance(p_177745_1_.posX, p_177745_1_.posZ);
   }

   public double getClosestDistance(double p_177729_1_, double p_177729_3_) {
      double d0 = p_177729_3_ - this.minZ();
      double d1 = this.maxZ() - p_177729_3_;
      double d2 = p_177729_1_ - this.minX();
      double d3 = this.maxX() - p_177729_1_;
      double d4 = Math.min(d2, d3);
      d4 = Math.min(d4, d0);
      return Math.min(d4, d1);
   }

   @OnlyIn(Dist.CLIENT)
   public EnumBorderStatus getStatus() {
      return this.field_212674_i.func_212655_i();
   }

   public double minX() {
      return this.field_212674_i.func_212658_a();
   }

   public double minZ() {
      return this.field_212674_i.func_212656_c();
   }

   public double maxX() {
      return this.field_212674_i.func_212654_b();
   }

   public double maxZ() {
      return this.field_212674_i.func_212648_d();
   }

   public double getCenterX() {
      return this.centerX;
   }

   public double getCenterZ() {
      return this.centerZ;
   }

   public void setCenter(double p_177739_1_, double p_177739_3_) {
      this.centerX = p_177739_1_;
      this.centerZ = p_177739_3_;
      this.field_212674_i.func_212653_k();

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onCenterChanged(this, p_177739_1_, p_177739_3_);
      }

   }

   public double getDiameter() {
      return this.field_212674_i.func_212647_e();
   }

   public long getTimeUntilTarget() {
      return this.field_212674_i.func_212657_g();
   }

   public double getTargetSize() {
      return this.field_212674_i.func_212650_h();
   }

   public void setTransition(double p_177750_1_) {
      this.field_212674_i = new WorldBorder.StationaryBorderInfo(p_177750_1_);

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onSizeChanged(this, p_177750_1_);
      }

   }

   public void setTransition(double p_177738_1_, double p_177738_3_, long p_177738_5_) {
      this.field_212674_i = (WorldBorder.IBorderInfo)(p_177738_1_ != p_177738_3_ ? new WorldBorder.MovingBorderInfo(p_177738_1_, p_177738_3_, p_177738_5_) : new WorldBorder.StationaryBorderInfo(p_177738_3_));

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onTransitionStarted(this, p_177738_1_, p_177738_3_, p_177738_5_);
      }

   }

   protected List<IBorderListener> getListeners() {
      return Lists.newArrayList(this.listeners);
   }

   public void addListener(IBorderListener p_177737_1_) {
      this.listeners.add(p_177737_1_);
   }

   public void setSize(int p_177725_1_) {
      this.worldSize = p_177725_1_;
      this.field_212674_i.func_212652_j();
   }

   public int getSize() {
      return this.worldSize;
   }

   public double getDamageBuffer() {
      return this.damageBuffer;
   }

   public void setDamageBuffer(double p_177724_1_) {
      this.damageBuffer = p_177724_1_;

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onDamageBufferChanged(this, p_177724_1_);
      }

   }

   public double getDamageAmount() {
      return this.damageAmount;
   }

   public void setDamageAmount(double p_177744_1_) {
      this.damageAmount = p_177744_1_;

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onDamageAmountChanged(this, p_177744_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public double getResizeSpeed() {
      return this.field_212674_i.func_212649_f();
   }

   public int getWarningTime() {
      return this.warningTime;
   }

   public void setWarningTime(int p_177723_1_) {
      this.warningTime = p_177723_1_;

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onWarningTimeChanged(this, p_177723_1_);
      }

   }

   public int getWarningDistance() {
      return this.warningDistance;
   }

   public void setWarningDistance(int p_177747_1_) {
      this.warningDistance = p_177747_1_;

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onWarningDistanceChanged(this, p_177747_1_);
      }

   }

   public void func_212673_r() {
      this.field_212674_i = this.field_212674_i.func_212651_l();
   }

   interface IBorderInfo {
      double func_212658_a();

      double func_212654_b();

      double func_212656_c();

      double func_212648_d();

      double func_212647_e();

      @OnlyIn(Dist.CLIENT)
      double func_212649_f();

      long func_212657_g();

      double func_212650_h();

      @OnlyIn(Dist.CLIENT)
      EnumBorderStatus func_212655_i();

      void func_212652_j();

      void func_212653_k();

      WorldBorder.IBorderInfo func_212651_l();
   }

   class MovingBorderInfo implements WorldBorder.IBorderInfo {
      private final double field_212660_b;
      private final double field_212661_c;
      private final long field_212662_d;
      private final long field_212663_e;
      private final double field_212664_f;

      private MovingBorderInfo(double p_i49838_2_, double p_i49838_4_, long p_i49838_6_) {
         this.field_212660_b = p_i49838_2_;
         this.field_212661_c = p_i49838_4_;
         this.field_212664_f = (double)p_i49838_6_;
         this.field_212663_e = Util.milliTime();
         this.field_212662_d = this.field_212663_e + p_i49838_6_;
      }

      public double func_212658_a() {
         return Math.max(WorldBorder.this.getCenterX() - this.func_212647_e() / 2.0D, (double)(-WorldBorder.this.worldSize));
      }

      public double func_212656_c() {
         return Math.max(WorldBorder.this.getCenterZ() - this.func_212647_e() / 2.0D, (double)(-WorldBorder.this.worldSize));
      }

      public double func_212654_b() {
         return Math.min(WorldBorder.this.getCenterX() + this.func_212647_e() / 2.0D, (double)WorldBorder.this.worldSize);
      }

      public double func_212648_d() {
         return Math.min(WorldBorder.this.getCenterZ() + this.func_212647_e() / 2.0D, (double)WorldBorder.this.worldSize);
      }

      public double func_212647_e() {
         double d0 = (double)(Util.milliTime() - this.field_212663_e) / this.field_212664_f;
         return d0 < 1.0D ? this.field_212660_b + (this.field_212661_c - this.field_212660_b) * d0 : this.field_212661_c;
      }

      @OnlyIn(Dist.CLIENT)
      public double func_212649_f() {
         return Math.abs(this.field_212660_b - this.field_212661_c) / (double)(this.field_212662_d - this.field_212663_e);
      }

      public long func_212657_g() {
         return this.field_212662_d - Util.milliTime();
      }

      public double func_212650_h() {
         return this.field_212661_c;
      }

      @OnlyIn(Dist.CLIENT)
      public EnumBorderStatus func_212655_i() {
         return this.field_212661_c < this.field_212660_b ? EnumBorderStatus.SHRINKING : EnumBorderStatus.GROWING;
      }

      public void func_212653_k() {
      }

      public void func_212652_j() {
      }

      public WorldBorder.IBorderInfo func_212651_l() {
         return (WorldBorder.IBorderInfo)(this.func_212657_g() <= 0L ? WorldBorder.this.new StationaryBorderInfo(this.field_212661_c) : this);
      }
   }

   class StationaryBorderInfo implements WorldBorder.IBorderInfo {
      private final double field_212667_b;
      private double field_212668_c;
      private double field_212669_d;
      private double field_212670_e;
      private double field_212671_f;

      public StationaryBorderInfo(double p_i49837_2_) {
         this.field_212667_b = p_i49837_2_;
         this.func_212665_m();
      }

      public double func_212658_a() {
         return this.field_212668_c;
      }

      public double func_212654_b() {
         return this.field_212670_e;
      }

      public double func_212656_c() {
         return this.field_212669_d;
      }

      public double func_212648_d() {
         return this.field_212671_f;
      }

      public double func_212647_e() {
         return this.field_212667_b;
      }

      @OnlyIn(Dist.CLIENT)
      public EnumBorderStatus func_212655_i() {
         return EnumBorderStatus.STATIONARY;
      }

      @OnlyIn(Dist.CLIENT)
      public double func_212649_f() {
         return 0.0D;
      }

      public long func_212657_g() {
         return 0L;
      }

      public double func_212650_h() {
         return this.field_212667_b;
      }

      private void func_212665_m() {
         this.field_212668_c = Math.max(WorldBorder.this.getCenterX() - this.field_212667_b / 2.0D, (double)(-WorldBorder.this.worldSize));
         this.field_212669_d = Math.max(WorldBorder.this.getCenterZ() - this.field_212667_b / 2.0D, (double)(-WorldBorder.this.worldSize));
         this.field_212670_e = Math.min(WorldBorder.this.getCenterX() + this.field_212667_b / 2.0D, (double)WorldBorder.this.worldSize);
         this.field_212671_f = Math.min(WorldBorder.this.getCenterZ() + this.field_212667_b / 2.0D, (double)WorldBorder.this.worldSize);
      }

      public void func_212652_j() {
         this.func_212665_m();
      }

      public void func_212653_k() {
         this.func_212665_m();
      }

      public WorldBorder.IBorderInfo func_212651_l() {
         return this;
      }
   }
}
