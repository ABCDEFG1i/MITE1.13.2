package net.minecraft.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

public class Main {
   public static void main(String[] p_main_0_) throws IOException {
      OptionParser optionparser = new OptionParser();
      AbstractOptionSpec<Void> abstractoptionspec = optionparser.accepts("help", "Show the help menu").forHelp();
      OptionSpecBuilder optionspecbuilder = optionparser.accepts("server", "Include server generators");
      OptionSpecBuilder optionspecbuilder1 = optionparser.accepts("client", "Include client generators");
      OptionSpecBuilder optionspecbuilder2 = optionparser.accepts("dev", "Include development tools");
      OptionSpecBuilder optionspecbuilder3 = optionparser.accepts("reports", "Include data reports");
      OptionSpecBuilder optionspecbuilder4 = optionparser.accepts("all", "Include all generators");
      ArgumentAcceptingOptionSpec<String> argumentacceptingoptionspec = optionparser.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated");
      ArgumentAcceptingOptionSpec<String> argumentacceptingoptionspec1 = optionparser.accepts("input", "Input folder").withRequiredArg();
      OptionSet optionset = optionparser.parse(p_main_0_);
      if (!optionset.has(abstractoptionspec) && optionset.hasOptions()) {
         Path path = Paths.get(argumentacceptingoptionspec.value(optionset));
         boolean flag = optionset.has(optionspecbuilder1) || optionset.has(optionspecbuilder4);
         boolean flag1 = optionset.has(optionspecbuilder) || optionset.has(optionspecbuilder4);
         boolean flag2 = optionset.has(optionspecbuilder2) || optionset.has(optionspecbuilder4);
         boolean flag3 = optionset.has(optionspecbuilder3) || optionset.has(optionspecbuilder4);
         deleteDir(path.toString()+"\\data");
         DataGenerator datagenerator = makeGenerator(path, optionset.valuesOf(argumentacceptingoptionspec1).stream().map((p_200263_0_) -> Paths.get(p_200263_0_)).collect(Collectors.toList()), flag, flag1, flag2, flag3);
         datagenerator.run();
      } else {
         optionparser.printHelpOn(System.out);
      }
   }

   private static void deleteDir(String dirPath)
   {
      File file = new File(dirPath);
      if(file.isFile())
      {
         file.delete();
      }else
      {
         File[] files = file.listFiles();
         if(files == null)
         {
            file.delete();
         }else
         {
            for (File value : files) {
               String path =value.getAbsolutePath();
               if (path.contains("loot_tables")||path.contains("structures")||path.contains(".mcassetsroot")){
                  continue;
               }
               deleteDir(value.getAbsolutePath());
            }
            file.delete();
         }
      }
   }
   public static DataGenerator makeGenerator(Path p_200264_0_, Collection<Path> p_200264_1_, boolean p_200264_2_, boolean p_200264_3_, boolean p_200264_4_, boolean p_200264_5_) {
      DataGenerator datagenerator = new DataGenerator(p_200264_0_, p_200264_1_);
      if (p_200264_2_ || p_200264_3_) {
         datagenerator.addProvider(new SNBTToNBTConverter(datagenerator));
      }

      if (p_200264_3_) {
         datagenerator.addProvider(new FluidTagsProvider(datagenerator));
         datagenerator.addProvider(new BlockTagsProvider(datagenerator));
         datagenerator.addProvider(new ItemTagsProvider(datagenerator));
         datagenerator.addProvider(new RecipeProvider(datagenerator));
         datagenerator.addProvider(new AdvancementProvider(datagenerator));
      }

      if (p_200264_4_) {
         datagenerator.addProvider(new NBTToSNBTConverter(datagenerator));
      }

      if (p_200264_5_) {
         datagenerator.addProvider(new BlockListReport(datagenerator));
         datagenerator.addProvider(new ItemListReport(datagenerator));
         datagenerator.addProvider(new CommandsReport(datagenerator));
      }

      return datagenerator;
   }
}
