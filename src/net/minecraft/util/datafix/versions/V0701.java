package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V0701 extends Schema {
   public V0701(int p_i49586_1_, Schema p_i49586_2_) {
      super(p_i49586_1_, p_i49586_2_);
   }

   protected static void registerEntity(Schema p_206624_0_, Map<String, Supplier<TypeTemplate>> p_206624_1_, String p_206624_2_) {
      p_206624_0_.register(p_206624_1_, p_206624_2_, () -> {
         return V0100.func_206605_a(p_206624_0_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
      registerEntity(p_registerEntities_1_, map, "WitherSkeleton");
      registerEntity(p_registerEntities_1_, map, "Stray");
      return map;
   }
}
