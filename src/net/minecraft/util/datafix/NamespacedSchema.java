package net.minecraft.util.datafix;

import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.ResourceLocation;

public class NamespacedSchema extends Schema {
   public NamespacedSchema(int p_i49612_1_, Schema p_i49612_2_) {
      super(p_i49612_1_, p_i49612_2_);
   }

   public static String ensureNamespaced(String p_206477_0_) {
      ResourceLocation resourcelocation = ResourceLocation.makeResourceLocation(p_206477_0_);
      return resourcelocation != null ? resourcelocation.toString() : p_206477_0_;
   }

   public Type<?> getChoiceType(TypeReference p_getChoiceType_1_, String p_getChoiceType_2_) {
      return super.getChoiceType(p_getChoiceType_1_, ensureNamespaced(p_getChoiceType_2_));
   }
}
