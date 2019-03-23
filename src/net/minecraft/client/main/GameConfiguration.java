package net.minecraft.client.main;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.client.resources.ResourceIndexFolder;
import net.minecraft.util.Session;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GameConfiguration {
   public final GameConfiguration.UserInformation userInfo;
   public final GameConfiguration.DisplayInformation displayInfo;
   public final GameConfiguration.FolderInformation folderInfo;
   public final GameConfiguration.GameInformation gameInfo;
   public final GameConfiguration.ServerInformation serverInfo;

   public GameConfiguration(GameConfiguration.UserInformation p_i45491_1_, GameConfiguration.DisplayInformation p_i45491_2_, GameConfiguration.FolderInformation p_i45491_3_, GameConfiguration.GameInformation p_i45491_4_, GameConfiguration.ServerInformation p_i45491_5_) {
      this.userInfo = p_i45491_1_;
      this.displayInfo = p_i45491_2_;
      this.folderInfo = p_i45491_3_;
      this.gameInfo = p_i45491_4_;
      this.serverInfo = p_i45491_5_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class DisplayInformation {
      public final int width;
      public final int height;
      public final Optional<Integer> fullscreenWidth;
      public final Optional<Integer> fullscreenHeight;
      public final boolean fullscreen;

      public DisplayInformation(int p_i49505_1_, int p_i49505_2_, Optional<Integer> p_i49505_3_, Optional<Integer> p_i49505_4_, boolean p_i49505_5_) {
         this.width = p_i49505_1_;
         this.height = p_i49505_2_;
         this.fullscreenWidth = p_i49505_3_;
         this.fullscreenHeight = p_i49505_4_;
         this.fullscreen = p_i49505_5_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FolderInformation {
      public final File gameDir;
      public final File resourcePacksDir;
      public final File assetsDir;
      public final String assetIndex;

      public FolderInformation(File p_i45489_1_, File p_i45489_2_, File p_i45489_3_, @Nullable String p_i45489_4_) {
         this.gameDir = p_i45489_1_;
         this.resourcePacksDir = p_i45489_2_;
         this.assetsDir = p_i45489_3_;
         this.assetIndex = p_i45489_4_;
      }

      public ResourceIndex getAssetsIndex() {
         return (ResourceIndex)(this.assetIndex == null ? new ResourceIndexFolder(this.assetsDir) : new ResourceIndex(this.assetsDir, this.assetIndex));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class GameInformation {
      public final boolean isDemo;
      public final String version;
      public final String versionType;

      public GameInformation(boolean p_i46801_1_, String p_i46801_2_, String p_i46801_3_) {
         this.isDemo = p_i46801_1_;
         this.version = p_i46801_2_;
         this.versionType = p_i46801_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ServerInformation {
      public final String serverName;
      public final int serverPort;

      public ServerInformation(String p_i45487_1_, int p_i45487_2_) {
         this.serverName = p_i45487_1_;
         this.serverPort = p_i45487_2_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class UserInformation {
      public final Session session;
      public final PropertyMap userProperties;
      public final PropertyMap profileProperties;
      public final Proxy proxy;

      public UserInformation(Session p_i46375_1_, PropertyMap p_i46375_2_, PropertyMap p_i46375_3_, Proxy p_i46375_4_) {
         this.session = p_i46375_1_;
         this.userProperties = p_i46375_2_;
         this.profileProperties = p_i46375_3_;
         this.proxy = p_i46375_4_;
      }
   }
}
