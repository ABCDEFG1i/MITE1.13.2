package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.net.www.protocol.file.FileURLConnection;

import javax.annotation.Nullable;
import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ResourceIndex {
   protected static final Logger LOGGER = LogManager.getLogger();
   private final Map<String, File> resourceMap = Maps.newHashMap();

   protected ResourceIndex() {
   }

   public ResourceIndex(File p_i1047_1_, String p_i1047_2_) {
      File file1 = new File(p_i1047_1_, "objects");
      File file2 = new File(p_i1047_1_, "indexes/" + p_i1047_2_ + ".json");
      BufferedReader bufferedreader = null;

      try {
         bufferedreader = Files.newReader(file2, StandardCharsets.UTF_8);
         JsonObject jsonobject = JsonUtils.func_212743_a(bufferedreader);
         JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonobject, "objects", null);
         if (jsonobject1 != null) {
            for(Entry<String, JsonElement> entry : jsonobject1.entrySet()) {
               JsonObject jsonobject2 = (JsonObject)entry.getValue();
               String s = entry.getKey();
               String[] astring = s.split("/", 2);
               String s1 = astring.length == 1 ? astring[0] : astring[0] + ":" + astring[1];
               String s2 = JsonUtils.getString(jsonobject2, "hash");
               File file3 = new File(file1, s2.substring(0, 2) + "/" + s2);
               this.resourceMap.put(s1, file3);
            }
         }
         for (String file : loadResourceFromJarByFolder("/assets/minecraft/lang")) {
            String languageSymbol = "minecraft:lang/" + file.substring(file.lastIndexOf("/") + 1);
            resourceMap.replace(languageSymbol, new File(file));
         }
      } catch (JsonParseException var20) {
         LOGGER.error("Unable to parse resource index file: {}", file2);
      } catch (FileNotFoundException var21) {
         LOGGER.error("Can't find the resource index file: {}", file2);
      } catch (IOException e) {
         LOGGER.error("Can't extract language files: {}", e);
      } finally {
         IOUtils.closeQuietly(bufferedreader);
      }

   }

   @Nullable
   public File getFile(ResourceLocation p_188547_1_) {
      String s = p_188547_1_.toString();
      return this.resourceMap.get(s);
   }


   private ArrayList<String> loadResourceFromJarByFolder(String folderPath) throws IOException {
      URL url = getClass().getResource(folderPath);
      URLConnection urlConnection = url.openConnection();
      if (urlConnection instanceof FileURLConnection) {
         return copyFileResources(url, folderPath);
      } else if (urlConnection instanceof JarURLConnection) {
         return copyJarResources((JarURLConnection) urlConnection);
      }
      return new ArrayList<>();
   }

   private ArrayList<String> copyFileResources(URL url, String folderPath) throws IOException {
      File root = new File(url.getPath());
      ArrayList<String> list = new ArrayList<>();
      if (root.isDirectory()) {
         File[] files = root.listFiles();
         for (File file : files) {
            if (file.isDirectory()) {
               list.addAll(loadResourceFromJarByFolder(folderPath + "/" + file.getName()));
            } else {
               list.add(loadResourceFromJar(folderPath + "/" + file.getName()));
            }
         }
      }
      return list;
   }

   private ArrayList<String> copyJarResources(JarURLConnection jarURLConnection) throws IOException {
      JarFile jarFile = jarURLConnection.getJarFile();
      Enumeration<JarEntry> entrys = jarFile.entries();
      ArrayList<String> list = new ArrayList<>();
      while (entrys.hasMoreElements()) {
         JarEntry entry = entrys.nextElement();
         if (entry.getName().startsWith(jarURLConnection.getEntryName()) && !entry.getName().endsWith("/")) {
            list.add(loadResourceFromJar("/" + entry.getName()));
         }
      }
      jarFile.close();
      return list;
   }

   private String loadResourceFromJar(String path) throws IOException {
      if (!path.startsWith("/")) {
         throw new IllegalArgumentException("The path has to be absolute (start with '/').");
      }

      if (path.endsWith("/")) {
         throw new IllegalArgumentException("The path has to be absolute (cat not end with '/').");
      }

      int index = path.lastIndexOf('/');

      String filename = path.substring(index + 1);
      URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
      String resourceFolder = java.net.URLDecoder.decode(url.getPath(), "utf-8");
      if (resourceFolder.endsWith(".jar")) {
         resourceFolder = resourceFolder.substring(0, resourceFolder.lastIndexOf('/') + 1);
      }
      if (!System.getProperty("os.name").toLowerCase().contains("linux")) {
         resourceFolder = resourceFolder.substring(1);
      }
      String folderPath = (resourceFolder + path.substring(0, index + 1)).replace("/assets/minecraft", "");

      // If the folder does not exist yet, it will be created. If the folder
      // exists already, it will be ignored
      File dir = new File(folderPath);
      if (!dir.exists()) {
         dir.mkdirs();
      }

      // If the file does not exist yet, it will be created. If the file
      // exists already, it will be ignored
      filename = folderPath + filename;
      File file = new File(filename);

      if (!file.exists() && !file.createNewFile()) {
         LOGGER.error("create file :{} failed", filename);
         return "";
      }

      // Prepare buffer for data copying
      byte[] buffer = new byte[1024];
      int readBytes;

      // Open and check input stream
      URL url1 = getClass().getResource(path);
      URLConnection urlConnection = url1.openConnection();
      InputStream is = urlConnection.getInputStream();

      if (is == null) {
         throw new FileNotFoundException("File " + path + " was not found inside JAR.");
      }

      // Open output stream and copy data between source file in JAR and the
      // temporary file
      OutputStream os = new FileOutputStream(file);
      try {
         while ((readBytes = is.read(buffer)) != -1) {
            os.write(buffer, 0, readBytes);
         }
      } finally {
         // If read/write fails, close streams safely before throwing an
         // exception
         os.close();
         is.close();
      }
      return filename;
   }

   @Nullable
   public File func_200009_a(String p_200009_1_) {
      return this.resourceMap.get(p_200009_1_);
   }

   public Collection<String> func_211685_a(String p_211685_1_, int p_211685_2_, Predicate<String> p_211685_3_) {
      return this.resourceMap.keySet().stream().filter((p_211684_0_) -> {
         return !p_211684_0_.endsWith(".mcmeta");
      }).map(ResourceLocation::new).map(ResourceLocation::getPath).filter((p_211683_1_) -> {
         return p_211683_1_.startsWith(p_211685_1_ + "/");
      }).filter(p_211685_3_).collect(Collectors.toList());
   }
}
