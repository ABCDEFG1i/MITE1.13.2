package net.minecraft.crash;

public class ReportedException extends RuntimeException {
   private final CrashReport crashReport;

   public ReportedException(CrashReport p_i1356_1_) {
      this.crashReport = p_i1356_1_;
   }

   public CrashReport getCrashReport() {
      return this.crashReport;
   }

   public Throwable getCause() {
      return this.crashReport.getCrashCause();
   }

   public String getMessage() {
      return this.crashReport.getDescription();
   }
}
