package net.minecraft.util;

import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingPrintStream extends PrintStream {
   protected static final Logger LOGGER = LogManager.getLogger();
   protected final String domain;

   public LoggingPrintStream(String p_i45927_1_, OutputStream p_i45927_2_) {
      super(p_i45927_2_);
      this.domain = p_i45927_1_;
   }

   public void println(String p_println_1_) {
      this.logString(p_println_1_);
   }

   public void println(Object p_println_1_) {
      this.logString(String.valueOf(p_println_1_));
   }

   protected void logString(String p_179882_1_) {
      LOGGER.info("[{}]: {}", this.domain, p_179882_1_);
   }
}
