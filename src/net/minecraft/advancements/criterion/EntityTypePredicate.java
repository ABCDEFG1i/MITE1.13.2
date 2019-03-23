package net.minecraft.advancements.criterion;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class EntityTypePredicate {
   public static final EntityTypePredicate field_209371_a = new EntityTypePredicate();
   private static final Joiner field_209372_b = Joiner.on(", ");
   @Nullable
   private final EntityType<?> field_209373_c;

   public EntityTypePredicate(EntityType<?> p_i49390_1_) {
      this.field_209373_c = p_i49390_1_;
   }

   private EntityTypePredicate() {
      this.field_209373_c = null;
   }

   public boolean func_209368_a(EntityType<?> p_209368_1_) {
      return this.field_209373_c == null || this.field_209373_c == p_209368_1_;
   }

   public static EntityTypePredicate func_209370_a(@Nullable JsonElement p_209370_0_) {
      if (p_209370_0_ != null && !p_209370_0_.isJsonNull()) {
         String s = JsonUtils.getString(p_209370_0_, "type");
         ResourceLocation resourcelocation = new ResourceLocation(s);
         EntityType<?> entitytype = IRegistry.field_212629_r.func_212608_b(resourcelocation);
         if (entitytype == null) {
            throw new JsonSyntaxException("Unknown entity type '" + resourcelocation + "', valid types are: " + field_209372_b.join(IRegistry.field_212629_r.func_148742_b()));
         } else {
            return new EntityTypePredicate(entitytype);
         }
      } else {
         return field_209371_a;
      }
   }

   public JsonElement func_209369_a() {
      return (JsonElement)(this.field_209373_c == null ? JsonNull.INSTANCE : new JsonPrimitive(IRegistry.field_212629_r.func_177774_c(this.field_209373_c).toString()));
   }
}
