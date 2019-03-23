package net.minecraft.server.dedicated;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertyManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Properties serverProperties = new Properties();
   private final File serverPropertiesFile;

   public PropertyManager(File p_i46372_1_) {
      this.serverPropertiesFile = p_i46372_1_;
      if (p_i46372_1_.exists()) {
         FileInputStream fileinputstream = null;

         try {
            fileinputstream = new FileInputStream(p_i46372_1_);
            this.serverProperties.load(fileinputstream);
         } catch (Exception exception) {
            LOGGER.warn("Failed to load {}", p_i46372_1_, exception);
            this.generateNewProperties();
         } finally {
            if (fileinputstream != null) {
               try {
                  fileinputstream.close();
               } catch (IOException var11) {
                  ;
               }
            }

         }
      } else {
         LOGGER.warn("{} does not exist", (Object)p_i46372_1_);
         this.generateNewProperties();
      }

   }

   public void generateNewProperties() {
      LOGGER.info("Generating new properties file");
      this.saveProperties();
   }

   public void saveProperties() {
      FileOutputStream fileoutputstream = null;

      try {
         fileoutputstream = new FileOutputStream(this.serverPropertiesFile);
         this.serverProperties.store(fileoutputstream, "Minecraft server properties");
      } catch (Exception exception) {
         LOGGER.warn("Failed to save {}", this.serverPropertiesFile, exception);
         this.generateNewProperties();
      } finally {
         if (fileoutputstream != null) {
            try {
               fileoutputstream.close();
            } catch (IOException var10) {
               ;
            }
         }

      }

   }

   public File getPropertiesFile() {
      return this.serverPropertiesFile;
   }

   public String getStringProperty(String p_73671_1_, String p_73671_2_) {
      if (!this.serverProperties.containsKey(p_73671_1_)) {
         this.serverProperties.setProperty(p_73671_1_, p_73671_2_);
         this.saveProperties();
         this.saveProperties();
      }

      return this.serverProperties.getProperty(p_73671_1_, p_73671_2_);
   }

   public int getIntProperty(String p_73669_1_, int p_73669_2_) {
      try {
         return Integer.parseInt(this.getStringProperty(p_73669_1_, "" + p_73669_2_));
      } catch (Exception var4) {
         this.serverProperties.setProperty(p_73669_1_, "" + p_73669_2_);
         this.saveProperties();
         return p_73669_2_;
      }
   }

   public long getLongProperty(String p_179885_1_, long p_179885_2_) {
      try {
         return Long.parseLong(this.getStringProperty(p_179885_1_, "" + p_179885_2_));
      } catch (Exception var5) {
         this.serverProperties.setProperty(p_179885_1_, "" + p_179885_2_);
         this.saveProperties();
         return p_179885_2_;
      }
   }

   public boolean getBooleanProperty(String p_73670_1_, boolean p_73670_2_) {
      try {
         return Boolean.parseBoolean(this.getStringProperty(p_73670_1_, "" + p_73670_2_));
      } catch (Exception var4) {
         this.serverProperties.setProperty(p_73670_1_, "" + p_73670_2_);
         this.saveProperties();
         return p_73670_2_;
      }
   }

   public void setProperty(String p_73667_1_, Object p_73667_2_) {
      this.serverProperties.setProperty(p_73667_1_, "" + p_73667_2_);
   }

   public boolean hasProperty(String p_187239_1_) {
      return this.serverProperties.containsKey(p_187239_1_);
   }

   public void removeProperty(String p_187238_1_) {
      this.serverProperties.remove(p_187238_1_);
   }
}
