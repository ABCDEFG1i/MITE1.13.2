package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class TextureManager implements ITickable, IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ResourceLocation RESOURCE_LOCATION_EMPTY = new ResourceLocation("");
   private final Map<ResourceLocation, ITextureObject> mapTextureObjects = Maps.newHashMap();
   private final List<ITickable> listTickables = Lists.newArrayList();
   private final Map<String, Integer> mapTextureCounters = Maps.newHashMap();
   private final IResourceManager resourceManager;

   public TextureManager(IResourceManager p_i1284_1_) {
      this.resourceManager = p_i1284_1_;
   }

   public void bindTexture(ResourceLocation p_110577_1_) {
      ITextureObject itextureobject = this.mapTextureObjects.get(p_110577_1_);
      if (itextureobject == null) {
         itextureobject = new SimpleTexture(p_110577_1_);
         this.loadTexture(p_110577_1_, itextureobject);
      }

      itextureobject.bindTexture();
   }

   public boolean loadTickableTexture(ResourceLocation p_110580_1_, ITickableTextureObject p_110580_2_) {
      if (this.loadTexture(p_110580_1_, p_110580_2_)) {
         this.listTickables.add(p_110580_2_);
         return true;
      } else {
         return false;
      }
   }

   public boolean loadTexture(ResourceLocation p_110579_1_, ITextureObject p_110579_2_) {
      boolean flag = true;

      try {
         p_110579_2_.loadTexture(this.resourceManager);
      } catch (IOException ioexception) {
         if (p_110579_1_ != RESOURCE_LOCATION_EMPTY) {
            LOGGER.warn("Failed to load texture: {}", p_110579_1_, ioexception);
         }

         p_110579_2_ = MissingTextureSprite.getDynamicTexture();
         this.mapTextureObjects.put(p_110579_1_, p_110579_2_);
         flag = false;
      } catch (Throwable throwable) {
         final ITextureObject p_110579_2_f = p_110579_2_;
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Registering texture");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Resource location being registered");
         crashreportcategory.addCrashSection("Resource location", p_110579_1_);
         crashreportcategory.addDetail("Texture object class", () -> {
            return p_110579_2_f.getClass().getName();
         });
         throw new ReportedException(crashreport);
      }

      this.mapTextureObjects.put(p_110579_1_, p_110579_2_);
      return flag;
   }

   public ITextureObject getTexture(ResourceLocation p_110581_1_) {
      return this.mapTextureObjects.get(p_110581_1_);
   }

   public ResourceLocation getDynamicTextureLocation(String p_110578_1_, DynamicTexture p_110578_2_) {
      Integer integer = this.mapTextureCounters.get(p_110578_1_);
      if (integer == null) {
         integer = 1;
      } else {
         integer = integer + 1;
      }

      this.mapTextureCounters.put(p_110578_1_, integer);
      ResourceLocation resourcelocation = new ResourceLocation(String.format("dynamic/%s_%d", p_110578_1_, integer));
      this.loadTexture(resourcelocation, p_110578_2_);
      return resourcelocation;
   }

   public void tick() {
      for(ITickable itickable : this.listTickables) {
         itickable.tick();
      }

   }

   public void deleteTexture(ResourceLocation p_147645_1_) {
      ITextureObject itextureobject = this.getTexture(p_147645_1_);
      if (itextureobject != null) {
         TextureUtil.deleteTexture(itextureobject.getGlTextureId());
      }

   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      MissingTextureSprite.getDynamicTexture();
      Iterator<Entry<ResourceLocation, ITextureObject>> iterator = this.mapTextureObjects.entrySet().iterator();

      while(iterator.hasNext()) {
         Entry<ResourceLocation, ITextureObject> entry = iterator.next();
         ResourceLocation resourcelocation = entry.getKey();
         ITextureObject itextureobject = entry.getValue();
         if (itextureobject == MissingTextureSprite.getDynamicTexture() && !resourcelocation.equals(MissingTextureSprite.getLocation())) {
            iterator.remove();
         } else {
            this.loadTexture(entry.getKey(), itextureobject);
         }
      }

   }
}
