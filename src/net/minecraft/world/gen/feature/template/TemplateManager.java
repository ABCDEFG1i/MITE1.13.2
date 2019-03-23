package net.minecraft.world.gen.feature.template;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TemplateManager implements IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ResourceLocation, Template> templates = Maps.newHashMap();
   private final DataFixer fixer;
   private final MinecraftServer field_195432_d;
   private final Path field_195433_e;

   public TemplateManager(MinecraftServer p_i49570_1_, File p_i49570_2_, DataFixer p_i49570_3_) {
      this.field_195432_d = p_i49570_1_;
      this.fixer = p_i49570_3_;
      this.field_195433_e = p_i49570_2_.toPath().resolve("generated").normalize();
      p_i49570_1_.getResourceManager().addReloadListener(this);
   }

   public Template getTemplateDefaulted(ResourceLocation p_200220_1_) {
      Template template = this.getTemplate(p_200220_1_);
      if (template == null) {
         template = new Template();
         this.templates.put(p_200220_1_, template);
      }

      return template;
   }

   @Nullable
   public Template getTemplate(ResourceLocation p_200219_1_) {
      return this.templates.computeIfAbsent(p_200219_1_, (p_209204_1_) -> {
         Template template = this.loadTemplateFile(p_209204_1_);
         return template != null ? template : this.loadTemplateResource(p_209204_1_);
      });
   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      this.templates.clear();
   }

   @Nullable
   private Template loadTemplateResource(ResourceLocation p_209201_1_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_209201_1_.getNamespace(), "structures/" + p_209201_1_.getPath() + ".nbt");

      try (IResource iresource = this.field_195432_d.getResourceManager().getResource(resourcelocation)) {
         Template template = this.loadTemplate(iresource.getInputStream());
         return template;
      } catch (FileNotFoundException var18) {
         return null;
      } catch (Throwable throwable) {
         LOGGER.error("Couldn't load structure {}: {}", p_209201_1_, throwable.toString());
         return null;
      }
   }

   @Nullable
   private Template loadTemplateFile(ResourceLocation p_195428_1_) {
      if (!this.field_195433_e.toFile().isDirectory()) {
         return null;
      } else {
         Path path = this.func_209510_b(p_195428_1_, ".nbt");

         try {
            InputStream inputstream = new FileInputStream(path.toFile());
            Throwable throwable = null;

            Template template;
            try {
               template = this.loadTemplate(inputstream);
            } catch (Throwable throwable2) {
               throwable = throwable2;
               throw throwable2;
            } finally {
               if (inputstream != null) {
                  if (throwable != null) {
                     try {
                        inputstream.close();
                     } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                     }
                  } else {
                     inputstream.close();
                  }
               }

            }

            return template;
         } catch (FileNotFoundException var18) {
            return null;
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't load structure from {}", path, ioexception);
            return null;
         }
      }
   }

   private Template loadTemplate(InputStream p_209205_1_) throws IOException {
      NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(p_209205_1_);
      if (!nbttagcompound.hasKey("DataVersion", 99)) {
         nbttagcompound.setInteger("DataVersion", 500);
      }

      Template template = new Template();
      template.read(NBTUtil.func_210822_a(this.fixer, DataFixTypes.STRUCTURE, nbttagcompound, nbttagcompound.getInteger("DataVersion")));
      return template;
   }

   public boolean writeToFile(ResourceLocation p_195429_1_) {
      Template template = this.templates.get(p_195429_1_);
      if (template == null) {
         return false;
      } else {
         Path path = this.func_209510_b(p_195429_1_, ".nbt");
         Path path1 = path.getParent();
         if (path1 == null) {
            return false;
         } else {
            try {
               Files.createDirectories(Files.exists(path1) ? path1.toRealPath() : path1);
            } catch (IOException var19) {
               LOGGER.error("Failed to create parent directory: {}", (Object)path1);
               return false;
            }

            NBTTagCompound nbttagcompound = template.writeToNBT(new NBTTagCompound());

            try {
               OutputStream outputstream = new FileOutputStream(path.toFile());
               Throwable throwable = null;

               try {
                  CompressedStreamTools.writeCompressed(nbttagcompound, outputstream);
               } catch (Throwable throwable2) {
                  throwable = throwable2;
                  throw throwable2;
               } finally {
                  if (outputstream != null) {
                     if (throwable != null) {
                        try {
                           outputstream.close();
                        } catch (Throwable throwable1) {
                           throwable.addSuppressed(throwable1);
                        }
                     } else {
                        outputstream.close();
                     }
                  }

               }

               return true;
            } catch (Throwable var21) {
               return false;
            }
         }
      }
   }

   private Path func_209509_a(ResourceLocation p_209509_1_, String p_209509_2_) {
      try {
         Path path = this.field_195433_e.resolve(p_209509_1_.getNamespace());
         Path path1 = path.resolve("structures");
         return Util.func_209535_a(path1, p_209509_1_.getPath(), p_209509_2_);
      } catch (InvalidPathException invalidpathexception) {
         throw new ResourceLocationException("Invalid resource path: " + p_209509_1_, invalidpathexception);
      }
   }

   private Path func_209510_b(ResourceLocation p_209510_1_, String p_209510_2_) {
      if (p_209510_1_.getPath().contains("//")) {
         throw new ResourceLocationException("Invalid resource path: " + p_209510_1_);
      } else {
         Path path = this.func_209509_a(p_209510_1_, p_209510_2_);
         if (path.startsWith(this.field_195433_e) && Util.isPathNormal(path) && Util.isPathValidForWindows(path)) {
            return path;
         } else {
            throw new ResourceLocationException("Invalid resource path: " + path);
         }
      }
   }

   public void remove(ResourceLocation p_189941_1_) {
      this.templates.remove(p_189941_1_);
   }
}
