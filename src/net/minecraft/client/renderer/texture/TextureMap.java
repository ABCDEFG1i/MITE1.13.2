package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class TextureMap extends AbstractTexture implements ITickableTextureObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ResourceLocation LOCATION_BLOCKS_TEXTURE = new ResourceLocation("textures/atlas/blocks.png");
   private final List<TextureAtlasSprite> listAnimatedSprites = Lists.newArrayList();
   private final Set<ResourceLocation> sprites = Sets.newHashSet();
   private final Map<ResourceLocation, TextureAtlasSprite> mapUploadedSprites = Maps.newHashMap();
   private final String basePath;
   private int mipmapLevels;
   private final TextureAtlasSprite missingImage = MissingTextureSprite.getSprite();

   public TextureMap(String p_i46099_1_) {
      this.basePath = p_i46099_1_;
   }

   public void loadTexture(IResourceManager p_195413_1_) throws IOException {
   }

   public void stitch(IResourceManager p_195426_1_, Iterable<ResourceLocation> p_195426_2_) {
      this.sprites.clear();
      p_195426_2_.forEach((p_195423_2_) -> {
         this.registerSprite(p_195426_1_, p_195423_2_);
      });
      this.stitch(p_195426_1_);
   }

   public void stitch(IResourceManager p_195421_1_) {
      int i = Minecraft.getGLMaximumTextureSize();
      Stitcher stitcher = new Stitcher(i, i, 0, this.mipmapLevels);
      this.clear();
      int j = Integer.MAX_VALUE;
      int k = 1 << this.mipmapLevels;

      for(ResourceLocation resourcelocation : this.sprites) {
         if (!this.missingImage.getName().equals(resourcelocation)) {
            ResourceLocation resourcelocation1 = this.getSpritePath(resourcelocation);

            TextureAtlasSprite textureatlassprite;
            try (IResource iresource = p_195421_1_.getResource(resourcelocation1)) {
               PngSizeInfo pngsizeinfo = new PngSizeInfo(iresource);
               AnimationMetadataSection animationmetadatasection = iresource.getMetadata(AnimationMetadataSection.field_195817_a);
               textureatlassprite = new TextureAtlasSprite(resourcelocation, pngsizeinfo, animationmetadatasection);
            } catch (RuntimeException runtimeexception) {
               LOGGER.error("Unable to parse metadata from {} : {}", resourcelocation1, runtimeexception);
               continue;
            } catch (IOException ioexception) {
               LOGGER.error("Using missing texture, unable to load {} : {}", resourcelocation1, ioexception);
               continue;
            }

            j = Math.min(j, Math.min(textureatlassprite.getWidth(), textureatlassprite.getHeight()));
            int j1 = Math.min(Integer.lowestOneBit(textureatlassprite.getWidth()), Integer.lowestOneBit(textureatlassprite.getHeight()));
            if (j1 < k) {
               LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", resourcelocation1, textureatlassprite.getWidth(), textureatlassprite.getHeight(), MathHelper.log2(k), MathHelper.log2(j1));
               k = j1;
            }

            stitcher.addSprite(textureatlassprite);
         }
      }

      int l = Math.min(j, k);
      int i1 = MathHelper.log2(l);
      if (i1 < this.mipmapLevels) {
         LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.basePath, this.mipmapLevels, i1, l);
         this.mipmapLevels = i1;
      }

      this.missingImage.generateMipmaps(this.mipmapLevels);
      stitcher.addSprite(this.missingImage);

      try {
         stitcher.doStitch();
      } catch (StitcherException stitcherexception) {
         throw stitcherexception;
      }

      LOGGER.info("Created: {}x{} {}-atlas", stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), this.basePath);
      TextureUtil.allocateTextureImpl(this.getGlTextureId(), this.mipmapLevels, stitcher.getCurrentWidth(), stitcher.getCurrentHeight());

      for(TextureAtlasSprite textureatlassprite1 : stitcher.getStichSlots()) {
         if (textureatlassprite1 == this.missingImage || this.loadSprite(p_195421_1_, textureatlassprite1)) {
            this.mapUploadedSprites.put(textureatlassprite1.getName(), textureatlassprite1);

            try {
               textureatlassprite1.func_195663_q();
            } catch (Throwable throwable) {
               CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Stitching texture atlas");
               CrashReportCategory crashreportcategory = crashreport.makeCategory("Texture being stitched together");
               crashreportcategory.addCrashSection("Atlas path", this.basePath);
               crashreportcategory.addCrashSection("Sprite", textureatlassprite1);
               throw new ReportedException(crashreport);
            }

            if (textureatlassprite1.hasAnimationMetadata()) {
               this.listAnimatedSprites.add(textureatlassprite1);
            }
         }
      }

   }

   private boolean loadSprite(IResourceManager p_195422_1_, TextureAtlasSprite p_195422_2_) {
      ResourceLocation resourcelocation = this.getSpritePath(p_195422_2_.getName());
      IResource iresource = null;

      label62: {
         boolean flag;
         try {
            iresource = p_195422_1_.getResource(resourcelocation);
            p_195422_2_.loadSpriteFrames(iresource, this.mipmapLevels + 1);
            break label62;
         } catch (RuntimeException runtimeexception) {
            LOGGER.error("Unable to parse metadata from {}", resourcelocation, runtimeexception);
            flag = false;
         } catch (IOException ioexception) {
            LOGGER.error("Using missing texture, unable to load {}", resourcelocation, ioexception);
            flag = false;
            return flag;
         } finally {
            IOUtils.closeQuietly(iresource);
         }

         return flag;
      }

      try {
         p_195422_2_.generateMipmaps(this.mipmapLevels);
         return true;
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Applying mipmap");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Sprite being mipmapped");
         crashreportcategory.addDetail("Sprite name", () -> {
            return p_195422_2_.getName().toString();
         });
         crashreportcategory.addDetail("Sprite size", () -> {
            return p_195422_2_.getWidth() + " x " + p_195422_2_.getHeight();
         });
         crashreportcategory.addDetail("Sprite frames", () -> {
            return p_195422_2_.getFrameCount() + " frames";
         });
         crashreportcategory.addCrashSection("Mipmap levels", this.mipmapLevels);
         throw new ReportedException(crashreport);
      }
   }

   private ResourceLocation getSpritePath(ResourceLocation p_195420_1_) {
      return new ResourceLocation(p_195420_1_.getNamespace(), String.format("%s/%s%s", this.basePath, p_195420_1_.getPath(), ".png"));
   }

   public TextureAtlasSprite getAtlasSprite(String p_110572_1_) {
      return this.getSprite(new ResourceLocation(p_110572_1_));
   }

   public void updateAnimations() {
      this.bindTexture();

      for(TextureAtlasSprite textureatlassprite : this.listAnimatedSprites) {
         textureatlassprite.updateAnimation();
      }

   }

   public void registerSprite(IResourceManager p_199362_1_, ResourceLocation p_199362_2_) {
      if (p_199362_2_ == null) {
         throw new IllegalArgumentException("Location cannot be null!");
      } else {
         this.sprites.add(p_199362_2_);
      }
   }

   public void tick() {
      this.updateAnimations();
   }

   public void setMipmapLevels(int p_147633_1_) {
      this.mipmapLevels = p_147633_1_;
   }

   public TextureAtlasSprite getSprite(ResourceLocation p_195424_1_) {
      TextureAtlasSprite textureatlassprite = this.mapUploadedSprites.get(p_195424_1_);
      return textureatlassprite == null ? this.missingImage : textureatlassprite;
   }

   public void clear() {
      for(TextureAtlasSprite textureatlassprite : this.mapUploadedSprites.values()) {
         textureatlassprite.clearFramesTextureData();
      }

      this.mapUploadedSprites.clear();
      this.listAnimatedSprites.clear();
   }
}
