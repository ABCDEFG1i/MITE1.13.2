package net.minecraft.util;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtil {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ListeningExecutorService DOWNLOADER_EXECUTOR = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool((new ThreadFactoryBuilder()).setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).setNameFormat("Downloader %d").build()));

   @OnlyIn(Dist.CLIENT)
   public static ListenableFuture<?> downloadResourcePack(File p_180192_0_, String p_180192_1_, Map<String, String> p_180192_2_, int p_180192_3_, @Nullable IProgressUpdate p_180192_4_, Proxy p_180192_5_) {
      return DOWNLOADER_EXECUTOR.submit(() -> {
         HttpURLConnection httpurlconnection = null;
         InputStream inputstream = null;
         OutputStream outputstream = null;
         if (p_180192_4_ != null) {
            p_180192_4_.func_200211_b(new TextComponentTranslation("resourcepack.downloading"));
            p_180192_4_.func_200209_c(new TextComponentTranslation("resourcepack.requesting"));
         }

         try {
            try {
               byte[] abyte = new byte[4096];
               URL url = new URL(p_180192_1_);
               httpurlconnection = (HttpURLConnection)url.openConnection(p_180192_5_);
               httpurlconnection.setInstanceFollowRedirects(true);
               float f = 0.0F;
               float f1 = (float)p_180192_2_.entrySet().size();

               for(Entry<String, String> entry : p_180192_2_.entrySet()) {
                  httpurlconnection.setRequestProperty(entry.getKey(), entry.getValue());
                  if (p_180192_4_ != null) {
                     p_180192_4_.setLoadingProgress((int)(++f / f1 * 100.0F));
                  }
               }

               inputstream = httpurlconnection.getInputStream();
               f1 = (float)httpurlconnection.getContentLength();
               int i = httpurlconnection.getContentLength();
               if (p_180192_4_ != null) {
                  p_180192_4_.func_200209_c(new TextComponentTranslation("resourcepack.progress", String.format(Locale.ROOT, "%.2f", f1 / 1000.0F / 1000.0F)));
               }

               if (p_180192_0_.exists()) {
                  long j = p_180192_0_.length();
                  if (j == (long)i) {
                     if (p_180192_4_ != null) {
                        p_180192_4_.setDoneWorking();
                     }

                     return;
                  }

                  LOGGER.warn("Deleting {} as it does not match what we currently have ({} vs our {}).", p_180192_0_, i, j);
                  FileUtils.deleteQuietly(p_180192_0_);
               } else if (p_180192_0_.getParentFile() != null) {
                  p_180192_0_.getParentFile().mkdirs();
               }

               outputstream = new DataOutputStream(new FileOutputStream(p_180192_0_));
               if (p_180192_3_ > 0 && f1 > (float)p_180192_3_) {
                  if (p_180192_4_ != null) {
                     p_180192_4_.setDoneWorking();
                  }

                  throw new IOException("Filesize is bigger than maximum allowed (file is " + f + ", limit is " + p_180192_3_ + ")");
               }

               int k;
               while((k = inputstream.read(abyte)) >= 0) {
                  f += (float)k;
                  if (p_180192_4_ != null) {
                     p_180192_4_.setLoadingProgress((int)(f / f1 * 100.0F));
                  }

                  if (p_180192_3_ > 0 && f > (float)p_180192_3_) {
                     if (p_180192_4_ != null) {
                        p_180192_4_.setDoneWorking();
                     }

                     throw new IOException("Filesize was bigger than maximum allowed (got >= " + f + ", limit was " + p_180192_3_ + ")");
                  }

                  if (Thread.interrupted()) {
                     LOGGER.error("INTERRUPTED");
                     if (p_180192_4_ != null) {
                        p_180192_4_.setDoneWorking();
                     }

                     return;
                  }

                  outputstream.write(abyte, 0, k);
               }

               if (p_180192_4_ != null) {
                  p_180192_4_.setDoneWorking();
                  return;
               }
            } catch (Throwable throwable) {
               throwable.printStackTrace();
               if (httpurlconnection != null) {
                  InputStream inputstream1 = httpurlconnection.getErrorStream();

                  try {
                     LOGGER.error(IOUtils.toString(inputstream1));
                  } catch (IOException ioexception) {
                     ioexception.printStackTrace();
                  }
               }

               if (p_180192_4_ != null) {
                  p_180192_4_.setDoneWorking();
                  return;
               }
            }

         } finally {
            IOUtils.closeQuietly(inputstream);
            IOUtils.closeQuietly(outputstream);
         }
      });
   }

   public static int getSuitableLanPort() {
      try (ServerSocket serversocket = new ServerSocket(0)) {
         int i = serversocket.getLocalPort();
         return i;
      } catch (IOException var14) {
         return 25564;
      }
   }
}
