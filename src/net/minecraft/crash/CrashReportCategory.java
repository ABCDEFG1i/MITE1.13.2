package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CrashReportCategory {
   private final CrashReport crashReport;
   private final String name;
   private final List<CrashReportCategory.Entry> children = Lists.newArrayList();
   private StackTraceElement[] stackTrace = new StackTraceElement[0];

   public CrashReportCategory(CrashReport p_i1353_1_, String p_i1353_2_) {
      this.crashReport = p_i1353_1_;
      this.name = p_i1353_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public static String getCoordinateInfo(double p_85074_0_, double p_85074_2_, double p_85074_4_) {
      return String.format(Locale.ROOT, "%.2f,%.2f,%.2f - %s", p_85074_0_, p_85074_2_, p_85074_4_, getCoordinateInfo(new BlockPos(p_85074_0_, p_85074_2_, p_85074_4_)));
   }

   public static String getCoordinateInfo(BlockPos p_180522_0_) {
      return getCoordinateInfo(p_180522_0_.getX(), p_180522_0_.getY(), p_180522_0_.getZ());
   }

   public static String getCoordinateInfo(int p_184876_0_, int p_184876_1_, int p_184876_2_) {
      StringBuilder stringbuilder = new StringBuilder();

      try {
         stringbuilder.append(String.format("World: (%d,%d,%d)", p_184876_0_, p_184876_1_, p_184876_2_));
      } catch (Throwable var16) {
         stringbuilder.append("(Error finding world loc)");
      }

      stringbuilder.append(", ");

      try {
         int i = p_184876_0_ >> 4;
         int j = p_184876_2_ >> 4;
         int k = p_184876_0_ & 15;
         int l = p_184876_1_ >> 4;
         int i1 = p_184876_2_ & 15;
         int j1 = i << 4;
         int k1 = j << 4;
         int l1 = (i + 1 << 4) - 1;
         int i2 = (j + 1 << 4) - 1;
         stringbuilder.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", k, l, i1, i, j, j1, k1, l1, i2));
      } catch (Throwable var15) {
         stringbuilder.append("(Error finding chunk loc)");
      }

      stringbuilder.append(", ");

      try {
         int k2 = p_184876_0_ >> 9;
         int l2 = p_184876_2_ >> 9;
         int i3 = k2 << 5;
         int j3 = l2 << 5;
         int k3 = (k2 + 1 << 5) - 1;
         int l3 = (l2 + 1 << 5) - 1;
         int i4 = k2 << 9;
         int j4 = l2 << 9;
         int k4 = (k2 + 1 << 9) - 1;
         int j2 = (l2 + 1 << 9) - 1;
         stringbuilder.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", k2, l2, i3, j3, k3, l3, i4, j4, k4, j2));
      } catch (Throwable var14) {
         stringbuilder.append("(Error finding world loc)");
      }

      return stringbuilder.toString();
   }

   public void addDetail(String p_189529_1_, ICrashReportDetail<String> p_189529_2_) {
      try {
         this.addCrashSection(p_189529_1_, p_189529_2_.call());
      } catch (Throwable throwable) {
         this.addCrashSectionThrowable(p_189529_1_, throwable);
      }

   }

   public void addCrashSection(String p_71507_1_, Object p_71507_2_) {
      this.children.add(new CrashReportCategory.Entry(p_71507_1_, p_71507_2_));
   }

   public void addCrashSectionThrowable(String p_71499_1_, Throwable p_71499_2_) {
      this.addCrashSection(p_71499_1_, p_71499_2_);
   }

   public int getPrunedStackTrace(int p_85073_1_) {
      StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();
      if (astacktraceelement.length <= 0) {
         return 0;
      } else {
         this.stackTrace = new StackTraceElement[astacktraceelement.length - 3 - p_85073_1_];
         System.arraycopy(astacktraceelement, 3 + p_85073_1_, this.stackTrace, 0, this.stackTrace.length);
         return this.stackTrace.length;
      }
   }

   public boolean firstTwoElementsOfStackTraceMatch(StackTraceElement p_85069_1_, StackTraceElement p_85069_2_) {
      if (this.stackTrace.length != 0 && p_85069_1_ != null) {
         StackTraceElement stacktraceelement = this.stackTrace[0];
         if (stacktraceelement.isNativeMethod() == p_85069_1_.isNativeMethod() && stacktraceelement.getClassName().equals(p_85069_1_.getClassName()) && stacktraceelement.getFileName().equals(p_85069_1_.getFileName()) && stacktraceelement.getMethodName().equals(p_85069_1_.getMethodName())) {
            if (p_85069_2_ != null != this.stackTrace.length > 1) {
               return false;
            } else if (p_85069_2_ != null && !this.stackTrace[1].equals(p_85069_2_)) {
               return false;
            } else {
               this.stackTrace[0] = p_85069_1_;
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void trimStackTraceEntriesFromBottom(int p_85070_1_) {
      StackTraceElement[] astacktraceelement = new StackTraceElement[this.stackTrace.length - p_85070_1_];
      System.arraycopy(this.stackTrace, 0, astacktraceelement, 0, astacktraceelement.length);
      this.stackTrace = astacktraceelement;
   }

   public void appendToStringBuilder(StringBuilder p_85072_1_) {
      p_85072_1_.append("-- ").append(this.name).append(" --\n");
      p_85072_1_.append("Details:");

      for(CrashReportCategory.Entry crashreportcategory$entry : this.children) {
         p_85072_1_.append("\n\t");
         p_85072_1_.append(crashreportcategory$entry.getKey());
         p_85072_1_.append(": ");
         p_85072_1_.append(crashreportcategory$entry.getValue());
      }

      if (this.stackTrace != null && this.stackTrace.length > 0) {
         p_85072_1_.append("\nStacktrace:");

         for(StackTraceElement stacktraceelement : this.stackTrace) {
            p_85072_1_.append("\n\tat ");
            p_85072_1_.append(stacktraceelement);
         }
      }

   }

   public StackTraceElement[] getStackTrace() {
      return this.stackTrace;
   }

   public static void addBlockInfo(CrashReportCategory p_175750_0_, BlockPos p_175750_1_, @Nullable IBlockState p_175750_2_) {
      if (p_175750_2_ != null) {
         p_175750_0_.addDetail("Block", p_175750_2_::toString);
      }

      p_175750_0_.addDetail("Block location", () -> {
         return getCoordinateInfo(p_175750_1_);
      });
   }

   static class Entry {
      private final String key;
      private final String value;

      public Entry(String p_i1352_1_, Object p_i1352_2_) {
         this.key = p_i1352_1_;
         if (p_i1352_2_ == null) {
            this.value = "~~NULL~~";
         } else if (p_i1352_2_ instanceof Throwable) {
            Throwable throwable = (Throwable)p_i1352_2_;
            this.value = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
         } else {
            this.value = p_i1352_2_.toString();
         }

      }

      public String getKey() {
         return this.key;
      }

      public String getValue() {
         return this.value;
      }
   }
}
