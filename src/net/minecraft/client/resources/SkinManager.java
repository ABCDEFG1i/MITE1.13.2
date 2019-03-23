package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinManager {
   private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
   private final TextureManager textureManager;
   private final File skinCacheDir;
   private final MinecraftSessionService sessionService;
   private final LoadingCache<GameProfile, Map<Type, MinecraftProfileTexture>> skinCacheLoader;

   public SkinManager(TextureManager p_i1044_1_, File p_i1044_2_, MinecraftSessionService p_i1044_3_) {
      this.textureManager = p_i1044_1_;
      this.skinCacheDir = p_i1044_2_;
      this.sessionService = p_i1044_3_;
      this.skinCacheLoader = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<GameProfile, Map<Type, MinecraftProfileTexture>>() {
         public Map<Type, MinecraftProfileTexture> load(GameProfile p_load_1_) throws Exception {
            try {
               return Minecraft.getInstance().getSessionService().getTextures(p_load_1_, false);
            } catch (Throwable var3) {
               return Maps.newHashMap();
            }
         }
      });
   }

   public ResourceLocation loadSkin(MinecraftProfileTexture p_152792_1_, Type p_152792_2_) {
      return this.loadSkin(p_152792_1_, p_152792_2_, (SkinManager.SkinAvailableCallback)null);
   }

   public ResourceLocation loadSkin(final MinecraftProfileTexture p_152789_1_, final Type p_152789_2_, @Nullable final SkinManager.SkinAvailableCallback p_152789_3_) {
      String s = Hashing.sha1().hashUnencodedChars(p_152789_1_.getHash()).toString();
      final ResourceLocation resourcelocation = new ResourceLocation("skins/" + s);
      ITextureObject itextureobject = this.textureManager.getTexture(resourcelocation);
      if (itextureobject != null) {
         if (p_152789_3_ != null) {
            p_152789_3_.onSkinTextureAvailable(p_152789_2_, resourcelocation, p_152789_1_);
         }
      } else {
         File file1 = new File(this.skinCacheDir, s.length() > 2 ? s.substring(0, 2) : "xx");
         File file2 = new File(file1, s);
         final IImageBuffer iimagebuffer = p_152789_2_ == Type.SKIN ? new ImageBufferDownload() : null;
         ThreadDownloadImageData threaddownloadimagedata = new ThreadDownloadImageData(file2, p_152789_1_.getUrl(), DefaultPlayerSkin.getDefaultSkinLegacy(), new IImageBuffer() {
            public NativeImage parseUserSkin(NativeImage p_195786_1_) {
               return iimagebuffer != null ? iimagebuffer.parseUserSkin(p_195786_1_) : p_195786_1_;
            }

            public void skinAvailable() {
               if (iimagebuffer != null) {
                  iimagebuffer.skinAvailable();
               }

               if (p_152789_3_ != null) {
                  p_152789_3_.onSkinTextureAvailable(p_152789_2_, resourcelocation, p_152789_1_);
               }

            }
         });
         this.textureManager.loadTexture(resourcelocation, threaddownloadimagedata);
      }

      return resourcelocation;
   }

   public void loadProfileTextures(GameProfile p_152790_1_, SkinManager.SkinAvailableCallback p_152790_2_, boolean p_152790_3_) {
      THREAD_POOL.submit(() -> {
         Map<Type, MinecraftProfileTexture> map = Maps.newHashMap();

         try {
            map.putAll(this.sessionService.getTextures(p_152790_1_, p_152790_3_));
         } catch (InsecureTextureException var7) {
            ;
         }

         if (map.isEmpty()) {
            p_152790_1_.getProperties().clear();
            if (p_152790_1_.getId().equals(Minecraft.getInstance().getSession().getProfile().getId())) {
               p_152790_1_.getProperties().putAll(Minecraft.getInstance().getProfileProperties());
               map.putAll(this.sessionService.getTextures(p_152790_1_, false));
            } else {
               this.sessionService.fillProfileProperties(p_152790_1_, p_152790_3_);

               try {
                  map.putAll(this.sessionService.getTextures(p_152790_1_, p_152790_3_));
               } catch (InsecureTextureException var6) {
                  ;
               }
            }
         }

         Minecraft.getInstance().addScheduledTask(() -> {
            if (map.containsKey(Type.SKIN)) {
               this.loadSkin(map.get(Type.SKIN), Type.SKIN, p_152790_2_);
            }

            if (map.containsKey(Type.CAPE)) {
               this.loadSkin(map.get(Type.CAPE), Type.CAPE, p_152790_2_);
            }

         });
      });
   }

   public Map<Type, MinecraftProfileTexture> loadSkinFromCache(GameProfile p_152788_1_) {
      return this.skinCacheLoader.getUnchecked(p_152788_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public interface SkinAvailableCallback {
      void onSkinTextureAvailable(Type p_onSkinTextureAvailable_1_, ResourceLocation p_onSkinTextureAvailable_2_, MinecraftProfileTexture p_onSkinTextureAvailable_3_);
   }
}
