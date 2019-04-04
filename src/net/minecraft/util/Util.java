package net.minecraft.util;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.Hash.Strategy;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.state.IProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
   public static LongSupplier nanoTimeSupplier = System::nanoTime;
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern RESERVED_WINDOWS_NAMES = Pattern.compile(".*\\.|(?:CON|PRN|AUX|NUL|COM1|COM2|COM3|COM4|COM5|COM6|COM7|COM8|COM9|LPT1|LPT2|LPT3|LPT4|LPT5|LPT6|LPT7|LPT8|LPT9)(?:\\..*)?", 2);

   public static <K, V> Collector<Entry<? extends K, ? extends V>, ?, Map<K, V>> toMapCollector() {
      return Collectors.toMap(Entry::getKey, Entry::getValue);
   }

   public static <T extends Comparable<T>> String getValueName(IProperty<T> p_200269_0_, Object p_200269_1_) {
      return p_200269_0_.getName((T)(p_200269_1_));
   }

   public static String makeTranslationKey(String p_200697_0_, @Nullable ResourceLocation p_200697_1_) {
      return p_200697_1_ == null ? p_200697_0_ + ".unregistered_sadface" : p_200697_0_ + '.' + p_200697_1_.getNamespace() + '.' + p_200697_1_.getPath().replace('/', '.');
   }

   public static long milliTime() {
      return nanoTime() / 1000000L;
   }

   public static long nanoTime() {
      return nanoTimeSupplier.getAsLong();
   }

   public static long millisecondsSinceEpoch() {
      return Instant.now().toEpochMilli();
   }

   public static Util.EnumOS getOSType() {
      String s = System.getProperty("os.name").toLowerCase(Locale.ROOT);
      if (s.contains("win")) {
         return Util.EnumOS.WINDOWS;
      } else if (s.contains("mac")) {
         return Util.EnumOS.OSX;
      } else if (s.contains("solaris")) {
         return Util.EnumOS.SOLARIS;
      } else if (s.contains("sunos")) {
         return Util.EnumOS.SOLARIS;
      } else if (s.contains("linux")) {
         return Util.EnumOS.LINUX;
      } else {
         return s.contains("unix") ? Util.EnumOS.LINUX : Util.EnumOS.UNKNOWN;
      }
   }

   public static Stream<String> getJvmFlags() {
      RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
      return runtimemxbean.getInputArguments().stream().filter((p_211566_0_) -> {
         return p_211566_0_.startsWith("-X");
      });
   }

   public static boolean isPathNormal(Path p_209537_0_) {
      Path path = p_209537_0_.normalize();
      return path.equals(p_209537_0_);
   }

   public static boolean isPathValidForWindows(Path p_209536_0_) {
      for(Path path : p_209536_0_) {
         if (RESERVED_WINDOWS_NAMES.matcher(path.toString()).matches()) {
            return false;
         }
      }

      return true;
   }

   public static Path func_209535_a(Path p_209535_0_, String p_209535_1_, String p_209535_2_) {
      String s = p_209535_1_ + p_209535_2_;
      Path path = Paths.get(s);
      if (path.endsWith(p_209535_2_)) {
         throw new InvalidPathException(s, "empty resource name");
      } else {
         return p_209535_0_.resolve(path);
      }
   }

   @Nullable
   public static <V> V runTask(FutureTask<V> p_181617_0_, Logger p_181617_1_) {
      try {
         p_181617_0_.run();
         return p_181617_0_.get();
      } catch (ExecutionException executionexception) {
         p_181617_1_.fatal("Error executing task", executionexception);
      } catch (InterruptedException interruptedexception) {
         p_181617_1_.fatal("Error executing task", interruptedexception);
      }

      return null;
   }

   public static <T> T getLastElement(List<T> p_184878_0_) {
      return p_184878_0_.get(p_184878_0_.size() - 1);
   }

   public static <T> T getElementAfter(Iterable<T> p_195647_0_, @Nullable T p_195647_1_) {
      Iterator<T> iterator = p_195647_0_.iterator();
      T t = iterator.next();
      if (p_195647_1_ != null) {
         T t1 = t;

         while(t1 != p_195647_1_) {
            if (iterator.hasNext()) {
               t1 = iterator.next();
            }
         }

         if (iterator.hasNext()) {
            return iterator.next();
         }
      }

      return t;
   }

   public static <T> T getElementBefore(Iterable<T> p_195648_0_, @Nullable T p_195648_1_) {
      Iterator<T> iterator = p_195648_0_.iterator();

      T t;
      T t1;
      for(t = null; iterator.hasNext(); t = t1) {
         t1 = iterator.next();
         if (t1 == p_195648_1_) {
            if (t == null) {
               t = iterator.hasNext() ? Iterators.getLast(iterator) : p_195648_1_;
            }
            break;
         }
      }

      return t;
   }

   public static <T> T make(Supplier<T> p_199748_0_) {
      return p_199748_0_.get();
   }

   public static <T> T make(T p_200696_0_, Consumer<T> p_200696_1_) {
      p_200696_1_.accept(p_200696_0_);
      return p_200696_0_;
   }

   public static <K> Strategy<K> func_212443_g() {
      return (Strategy<K>)Util.IdentityStrategy.INSTANCE;
   }

   public enum EnumOS {
      LINUX,
      SOLARIS,
      WINDOWS {
         @OnlyIn(Dist.CLIENT)
         protected String[] getOpenCommandLine(URL p_195643_1_) {
            return new String[]{"rundll32", "url.dll,FileProtocolHandler", p_195643_1_.toString()};
         }
      },
      OSX {
         @OnlyIn(Dist.CLIENT)
         protected String[] getOpenCommandLine(URL p_195643_1_) {
            return new String[]{"open", p_195643_1_.toString()};
         }
      },
      UNKNOWN;

      EnumOS() {
      }

      @OnlyIn(Dist.CLIENT)
      public void openURL(URL p_195639_1_) {
         try {
            Process process = AccessController.doPrivileged((PrivilegedExceptionAction<Process>)(() -> {
               return Runtime.getRuntime().exec(this.getOpenCommandLine(p_195639_1_));
            }));

            for(String s : IOUtils.readLines(process.getErrorStream())) {
               Util.LOGGER.error(s);
            }

            process.getInputStream().close();
            process.getErrorStream().close();
            process.getOutputStream().close();
         } catch (IOException | PrivilegedActionException privilegedactionexception) {
            Util.LOGGER.error("Couldn't open url '{}'", p_195639_1_, privilegedactionexception);
         }

      }

      @OnlyIn(Dist.CLIENT)
      public void openURI(URI p_195642_1_) {
         try {
            this.openURL(p_195642_1_.toURL());
         } catch (MalformedURLException malformedurlexception) {
            Util.LOGGER.error("Couldn't open uri '{}'", p_195642_1_, malformedurlexception);
         }

      }

      @OnlyIn(Dist.CLIENT)
      public void openFile(File p_195641_1_) {
         try {
            this.openURL(p_195641_1_.toURI().toURL());
         } catch (MalformedURLException malformedurlexception) {
            Util.LOGGER.error("Couldn't open file '{}'", p_195641_1_, malformedurlexception);
         }

      }

      @OnlyIn(Dist.CLIENT)
      protected String[] getOpenCommandLine(URL p_195643_1_) {
         String s = p_195643_1_.toString();
         if ("file".equals(p_195643_1_.getProtocol())) {
            s = s.replace("file:", "file://");
         }

         return new String[]{"xdg-open", s};
      }

      @OnlyIn(Dist.CLIENT)
      public void openURI(String p_195640_1_) {
         try {
            this.openURL((new URI(p_195640_1_)).toURL());
         } catch (MalformedURLException | IllegalArgumentException | URISyntaxException urisyntaxexception) {
            Util.LOGGER.error("Couldn't open uri '{}'", p_195640_1_, urisyntaxexception);
         }

      }
   }

   enum IdentityStrategy implements Strategy<Object> {
      INSTANCE;

      public int hashCode(Object p_hashCode_1_) {
         return System.identityHashCode(p_hashCode_1_);
      }

      public boolean equals(Object p_equals_1_, Object p_equals_2_) {
         return p_equals_1_ == p_equals_2_;
      }
   }
}
