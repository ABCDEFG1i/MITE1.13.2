package net.minecraft.resources;

import java.util.Map;

public class ServerPackFinder implements IPackFinder {
   private final VanillaPack field_195738_a = new VanillaPack("minecraft");

   public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> p_195730_1_, ResourcePackInfo.IFactory<T> p_195730_2_) {
      T t = ResourcePackInfo.func_195793_a("vanilla", false, () -> {
         return this.field_195738_a;
      }, p_195730_2_, ResourcePackInfo.Priority.BOTTOM);
      if (t != null) {
         p_195730_1_.put("vanilla", t);
      }

   }
}
