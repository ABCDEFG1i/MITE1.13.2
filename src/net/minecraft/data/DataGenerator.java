package net.minecraft.data;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.init.Bootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataGenerator {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Collection<Path> inputFolders;
   private final Path outputFolder;
   private final List<IDataProvider> providers = Lists.newArrayList();

   public DataGenerator(Path p_i48266_1_, Collection<Path> p_i48266_2_) {
      this.outputFolder = p_i48266_1_;
      this.inputFolders = p_i48266_2_;
   }

   public Collection<Path> getInputFolders() {
      return this.inputFolders;
   }

   public Path getOutputFolder() {
      return this.outputFolder;
   }

   public void run() throws IOException {
      DirectoryCache directorycache = new DirectoryCache(this.outputFolder, "cache");
      Stopwatch stopwatch = Stopwatch.createUnstarted();

      for(IDataProvider idataprovider : this.providers) {
         LOGGER.info("Starting provider: {}", idataprovider.getName());
         stopwatch.start();
         idataprovider.act(directorycache);
         stopwatch.stop();
         LOGGER.info("{} finished after {} ms", idataprovider.getName(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
         stopwatch.reset();
      }

      directorycache.writeCache();
   }

   public void addProvider(IDataProvider p_200390_1_) {
      this.providers.add(p_200390_1_);
   }

   static {
      Bootstrap.register();
   }
}
