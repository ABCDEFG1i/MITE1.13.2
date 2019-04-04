package net.minecraft.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.resources.SimpleResource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ScreenShotHelper {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

   public static void func_148260_a(File p_148260_0_, int p_148260_1_, int p_148260_2_, Framebuffer p_148260_3_, Consumer<ITextComponent> p_148260_4_) {
      func_148259_a(p_148260_0_, null, p_148260_1_, p_148260_2_, p_148260_3_, p_148260_4_);
   }

   public static void func_148259_a(File p_148259_0_, @Nullable String p_148259_1_, int p_148259_2_, int p_148259_3_, Framebuffer p_148259_4_, Consumer<ITextComponent> p_148259_5_) {
      NativeImage nativeimage = createScreenshot(p_148259_2_, p_148259_3_, p_148259_4_);
      File file1 = new File(p_148259_0_, "screenshots");
      file1.mkdir();
      File file2;
      if (p_148259_1_ == null) {
         file2 = getTimestampedPNGFileForDirectory(file1);
      } else {
         file2 = new File(file1, p_148259_1_);
      }

      SimpleResource.RESOURCE_IO_EXECUTOR.execute(() -> {
         try {
            nativeimage.write(file2);
            ITextComponent itextcomponent = (new TextComponentString(file2.getName())).applyTextStyle(TextFormatting.UNDERLINE).applyTextStyle((p_212451_1_) -> {
               p_212451_1_.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath()));
            });
            p_148259_5_.accept(new TextComponentTranslation("screenshot.success", itextcomponent));
         } catch (Exception exception) {
            LOGGER.warn("Couldn't save screenshot", exception);
            p_148259_5_.accept(new TextComponentTranslation("screenshot.failure", exception.getMessage()));
         } finally {
            nativeimage.close();
         }

      });
   }

   public static NativeImage createScreenshot(int p_198052_0_, int p_198052_1_, Framebuffer p_198052_2_) {
      if (OpenGlHelper.isFramebufferEnabled()) {
         p_198052_0_ = p_198052_2_.framebufferTextureWidth;
         p_198052_1_ = p_198052_2_.framebufferTextureHeight;
      }

      NativeImage nativeimage = new NativeImage(p_198052_0_, p_198052_1_, false);
      if (OpenGlHelper.isFramebufferEnabled()) {
         GlStateManager.bindTexture(p_198052_2_.framebufferTexture);
         nativeimage.downloadFromTexture(0, true);
      } else {
         nativeimage.downloadFromFramebuffer(true);
      }

      nativeimage.flip();
      return nativeimage;
   }

   private static File getTimestampedPNGFileForDirectory(File p_74290_0_) {
      String s = DATE_FORMAT.format(new Date());
      int i = 1;

      while(true) {
         File file1 = new File(p_74290_0_, s + (i == 1 ? "" : "_" + i) + ".png");
         if (!file1.exists()) {
            return file1;
         }

         ++i;
      }
   }
}
