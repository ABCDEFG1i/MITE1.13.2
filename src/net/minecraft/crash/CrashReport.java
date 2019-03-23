package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String description;
   private final Throwable cause;
   private final CrashReportCategory systemDetailsCategory = new CrashReportCategory(this, "System Details");
   private final List<CrashReportCategory> crashReportSections = Lists.newArrayList();
   private File crashReportFile;
   private boolean firstCategoryInCrashReport = true;
   private StackTraceElement[] stacktrace = new StackTraceElement[0];

   public CrashReport(String p_i1348_1_, Throwable p_i1348_2_) {
      this.description = p_i1348_1_;
      this.cause = p_i1348_2_;
      this.populateEnvironment();
   }

   private void populateEnvironment() {
      this.systemDetailsCategory.addDetail("Minecraft Version", () -> {
         return "1.13.2";
      });
      this.systemDetailsCategory.addDetail("Operating System", () -> {
         return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
      });
      this.systemDetailsCategory.addDetail("Java Version", () -> {
         return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
      });
      this.systemDetailsCategory.addDetail("Java VM Version", () -> {
         return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
      });
      this.systemDetailsCategory.addDetail("Memory", () -> {
         Runtime runtime = Runtime.getRuntime();
         long i = runtime.maxMemory();
         long j = runtime.totalMemory();
         long k = runtime.freeMemory();
         long l = i / 1024L / 1024L;
         long i1 = j / 1024L / 1024L;
         long j1 = k / 1024L / 1024L;
         return k + " bytes (" + j1 + " MB) / " + j + " bytes (" + i1 + " MB) up to " + i + " bytes (" + l + " MB)";
      });
      this.systemDetailsCategory.addDetail("JVM Flags", () -> {
         List<String> list = Util.getJvmFlags().collect(Collectors.toList());
         return String.format("%d total; %s", list.size(), list.stream().collect(Collectors.joining(" ")));
      });
   }

   public String getDescription() {
      return this.description;
   }

   public Throwable getCrashCause() {
      return this.cause;
   }

   public void getSectionsInStringBuilder(StringBuilder p_71506_1_) {
      if ((this.stacktrace == null || this.stacktrace.length <= 0) && !this.crashReportSections.isEmpty()) {
         this.stacktrace = ArrayUtils.subarray(this.crashReportSections.get(0).getStackTrace(), 0, 1);
      }

      if (this.stacktrace != null && this.stacktrace.length > 0) {
         p_71506_1_.append("-- Head --\n");
         p_71506_1_.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
         p_71506_1_.append("Stacktrace:\n");

         for(StackTraceElement stacktraceelement : this.stacktrace) {
            p_71506_1_.append("\t").append("at ").append((Object)stacktraceelement);
            p_71506_1_.append("\n");
         }

         p_71506_1_.append("\n");
      }

      for(CrashReportCategory crashreportcategory : this.crashReportSections) {
         crashreportcategory.appendToStringBuilder(p_71506_1_);
         p_71506_1_.append("\n\n");
      }

      this.systemDetailsCategory.appendToStringBuilder(p_71506_1_);
   }

   public String getCauseStackTraceOrString() {
      StringWriter stringwriter = null;
      PrintWriter printwriter = null;
      Throwable throwable = this.cause;
      if (throwable.getMessage() == null) {
         if (throwable instanceof NullPointerException) {
            throwable = new NullPointerException(this.description);
         } else if (throwable instanceof StackOverflowError) {
            throwable = new StackOverflowError(this.description);
         } else if (throwable instanceof OutOfMemoryError) {
            throwable = new OutOfMemoryError(this.description);
         }

         throwable.setStackTrace(this.cause.getStackTrace());
      }

      String s;
      try {
         stringwriter = new StringWriter();
         printwriter = new PrintWriter(stringwriter);
         throwable.printStackTrace(printwriter);
         s = stringwriter.toString();
      } finally {
         IOUtils.closeQuietly((Writer)stringwriter);
         IOUtils.closeQuietly((Writer)printwriter);
      }

      return s;
   }

   public String getCompleteReport() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("---- Minecraft Crash Report ----\n");
      stringbuilder.append("// ");
      stringbuilder.append(getWittyComment());
      stringbuilder.append("\n\n");
      stringbuilder.append("Time: ");
      stringbuilder.append((new SimpleDateFormat()).format(new Date()));
      stringbuilder.append("\n");
      stringbuilder.append("Description: ");
      stringbuilder.append(this.description);
      stringbuilder.append("\n\n");
      stringbuilder.append(this.getCauseStackTraceOrString());
      stringbuilder.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

      for(int i = 0; i < 87; ++i) {
         stringbuilder.append("-");
      }

      stringbuilder.append("\n\n");
      this.getSectionsInStringBuilder(stringbuilder);
      return stringbuilder.toString();
   }

   @OnlyIn(Dist.CLIENT)
   public File getFile() {
      return this.crashReportFile;
   }

   public boolean saveToFile(File p_147149_1_) {
      if (this.crashReportFile != null) {
         return false;
      } else {
         if (p_147149_1_.getParentFile() != null) {
            p_147149_1_.getParentFile().mkdirs();
         }

         Writer writer = null;

         boolean flag1;
         try {
            writer = new OutputStreamWriter(new FileOutputStream(p_147149_1_), StandardCharsets.UTF_8);
            writer.write(this.getCompleteReport());
            this.crashReportFile = p_147149_1_;
            boolean lvt_3_1_ = true;
            return lvt_3_1_;
         } catch (Throwable throwable) {
            LOGGER.error("Could not save crash report to {}", p_147149_1_, throwable);
            flag1 = false;
         } finally {
            IOUtils.closeQuietly(writer);
         }

         return flag1;
      }
   }

   public CrashReportCategory getCategory() {
      return this.systemDetailsCategory;
   }

   public CrashReportCategory makeCategory(String p_85058_1_) {
      return this.makeCategoryDepth(p_85058_1_, 1);
   }

   public CrashReportCategory makeCategoryDepth(String p_85057_1_, int p_85057_2_) {
      CrashReportCategory crashreportcategory = new CrashReportCategory(this, p_85057_1_);
      if (this.firstCategoryInCrashReport) {
         int i = crashreportcategory.getPrunedStackTrace(p_85057_2_);
         StackTraceElement[] astacktraceelement = this.cause.getStackTrace();
         StackTraceElement stacktraceelement = null;
         StackTraceElement stacktraceelement1 = null;
         int j = astacktraceelement.length - i;
         if (j < 0) {
            System.out.println("Negative index in crash report handler (" + astacktraceelement.length + "/" + i + ")");
         }

         if (astacktraceelement != null && 0 <= j && j < astacktraceelement.length) {
            stacktraceelement = astacktraceelement[j];
            if (astacktraceelement.length + 1 - i < astacktraceelement.length) {
               stacktraceelement1 = astacktraceelement[astacktraceelement.length + 1 - i];
            }
         }

         this.firstCategoryInCrashReport = crashreportcategory.firstTwoElementsOfStackTraceMatch(stacktraceelement, stacktraceelement1);
         if (i > 0 && !this.crashReportSections.isEmpty()) {
            CrashReportCategory crashreportcategory1 = this.crashReportSections.get(this.crashReportSections.size() - 1);
            crashreportcategory1.trimStackTraceEntriesFromBottom(i);
         } else if (astacktraceelement != null && astacktraceelement.length >= i && 0 <= j && j < astacktraceelement.length) {
            this.stacktrace = new StackTraceElement[j];
            System.arraycopy(astacktraceelement, 0, this.stacktrace, 0, this.stacktrace.length);
         } else {
            this.firstCategoryInCrashReport = false;
         }
      }

      this.crashReportSections.add(crashreportcategory);
      return crashreportcategory;
   }

   private static String getWittyComment() {
      String[] astring = new String[]{"Who set us up the TNT?", "Everything's going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I'm sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don't be sad. I'll do better next time, I promise!", "Don't be sad, have a hug! <3", "I just don't know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn't worry myself about that.", "I bet Cylons wouldn't have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I'm Minecraft, and I'm a crashaholic.", "Ooh. Shiny.", "This doesn't make any sense!", "Why is it breaking :(", "Don't do that.", "Ouch. That hurt :(", "You're mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!", "But it works on my machine."};

      try {
         return astring[(int)(Util.nanoTime() % (long)astring.length)];
      } catch (Throwable var2) {
         return "Witty comment unavailable :(";
      }
   }

   public static CrashReport makeCrashReport(Throwable p_85055_0_, String p_85055_1_) {
      CrashReport crashreport;
      if (p_85055_0_ instanceof ReportedException) {
         crashreport = ((ReportedException)p_85055_0_).getCrashReport();
      } else {
         crashreport = new CrashReport(p_85055_1_, p_85055_0_);
      }

      return crashreport;
   }
}
