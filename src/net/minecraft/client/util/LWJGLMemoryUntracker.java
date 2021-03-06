package net.minecraft.client.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.Pointer;

@OnlyIn(Dist.CLIENT)
public class LWJGLMemoryUntracker {
   @Nullable
   private static final MethodHandle HANDLE = Util.make(() -> {
      try {
         Lookup lookup = MethodHandles.lookup();
         Class<?> oclass = Class.forName("org.lwjgl.system.MemoryManage$DebugAllocator");
         Method method = oclass.getDeclaredMethod("untrack", Long.TYPE);
         method.setAccessible(true);
         Field field = Class.forName("org.lwjgl.system.MemoryUtil$LazyInit").getDeclaredField("ALLOCATOR");
         field.setAccessible(true);
         Object object = field.get(null);
         return oclass.isInstance(object) ? lookup.unreflect(method) : null;
      } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | ClassNotFoundException classnotfoundexception) {
         throw new RuntimeException(classnotfoundexception);
      }
   });

   public static void untrack(long p_197933_0_) {
      if (HANDLE != null) {
         try {
            HANDLE.invoke(p_197933_0_);
         } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
         }
      }
   }

   public static void untrack(Pointer p_211545_0_) {
      untrack(p_211545_0_.address());
   }
}
