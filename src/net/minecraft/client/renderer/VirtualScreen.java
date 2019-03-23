package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.GameSettings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Monitor;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.lwjgl.glfw.GLFWMonitorCallbackI;

@OnlyIn(Dist.CLIENT)
public final class VirtualScreen implements AutoCloseable {
   private final Minecraft mc;
   private final Map<Long, Monitor> monitorMap = Maps.newHashMap();
   private final Map<Long, MainWindow> unusedMap = Maps.newHashMap();
   private final Map<MainWindow, Monitor> windowToMonitorMap = Maps.newHashMap();

   public VirtualScreen(Minecraft p_i47668_1_) {
      this.mc = p_i47668_1_;
      GLFW.glfwSetMonitorCallback(this::onMonitorConfigurationChange);
      PointerBuffer pointerbuffer = GLFW.glfwGetMonitors();

      for(int i = 0; i < pointerbuffer.limit(); ++i) {
         long j = pointerbuffer.get(i);
         this.monitorMap.put(j, new Monitor(this, j));
      }

   }

   private void onMonitorConfigurationChange(long p_198056_1_, int p_198056_3_) {
      if (p_198056_3_ == 262145) {
         this.monitorMap.put(p_198056_1_, new Monitor(this, p_198056_1_));
      } else if (p_198056_3_ == 262146) {
         this.monitorMap.remove(p_198056_1_);
      }

   }

   public Monitor getMonitor(long p_198054_1_) {
      return this.monitorMap.get(p_198054_1_);
   }

   public Monitor getMonitor(MainWindow p_198055_1_) {
      long i = GLFW.glfwGetWindowMonitor(p_198055_1_.getHandle());
      if (i != 0L) {
         return this.monitorMap.get(i);
      } else {
         Monitor monitor = this.monitorMap.values().iterator().next();
         int j = -1;
         int k = p_198055_1_.getWindowX();
         int l = k + p_198055_1_.getWidth();
         int i1 = p_198055_1_.getWindowY();
         int j1 = i1 + p_198055_1_.getHeight();

         for(Monitor monitor1 : this.monitorMap.values()) {
            int k1 = monitor1.getVirtualPosX();
            int l1 = k1 + monitor1.getDefaultVideoMode().getWidth();
            int i2 = monitor1.getVirtualPosY();
            int j2 = i2 + monitor1.getDefaultVideoMode().getHeight();
            int k2 = MathHelper.clamp(k, k1, l1);
            int l2 = MathHelper.clamp(l, k1, l1);
            int i3 = MathHelper.clamp(i1, i2, j2);
            int j3 = MathHelper.clamp(j1, i2, j2);
            int k3 = Math.max(0, l2 - k2);
            int l3 = Math.max(0, j3 - i3);
            int i4 = k3 * l3;
            if (i4 > j) {
               monitor = monitor1;
               j = i4;
            }
         }

         if (monitor != this.windowToMonitorMap.get(p_198055_1_)) {
            this.windowToMonitorMap.put(p_198055_1_, monitor);
            GameSettings.Options.FULLSCREEN_RESOLUTION.setValueMax((float)monitor.getVideoModeCount());
         }

         return monitor;
      }
   }

   public MainWindow createMainWindow(GameConfiguration.DisplayInformation p_198053_1_, String p_198053_2_) {
      return new MainWindow(this.mc, this, p_198053_1_, p_198053_2_);
   }

   public void close() {
      GLFWMonitorCallback glfwmonitorcallback = GLFW.glfwSetMonitorCallback((GLFWMonitorCallbackI)null);
      if (glfwmonitorcallback != null) {
         glfwmonitorcallback.free();
      }

   }
}
