package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;

public class LocationPredicate {
   public static final LocationPredicate ANY = new LocationPredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED,
           null,
           null,
           null);
   private final MinMaxBounds.FloatBound x;
   private final MinMaxBounds.FloatBound y;
   private final MinMaxBounds.FloatBound z;
   @Nullable
   private final Biome biome;
   @Nullable
   private final String feature;
   @Nullable
   private final DimensionType dimension;

   public LocationPredicate(MinMaxBounds.FloatBound p_i49721_1_, MinMaxBounds.FloatBound p_i49721_2_, MinMaxBounds.FloatBound p_i49721_3_, @Nullable Biome p_i49721_4_, @Nullable String p_i49721_5_, @Nullable DimensionType p_i49721_6_) {
      this.x = p_i49721_1_;
      this.y = p_i49721_2_;
      this.z = p_i49721_3_;
      this.biome = p_i49721_4_;
      this.feature = p_i49721_5_;
      this.dimension = p_i49721_6_;
   }

   public static LocationPredicate func_204010_a(Biome p_204010_0_) {
      return new LocationPredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, p_204010_0_,
              null,
              null);
   }

   public static LocationPredicate func_204008_a(DimensionType p_204008_0_) {
      return new LocationPredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED,
              null,
              null, p_204008_0_);
   }

   public static LocationPredicate func_204007_a(String p_204007_0_) {
      return new LocationPredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED,
              null, p_204007_0_,
              null);
   }

   public boolean test(WorldServer p_193452_1_, double p_193452_2_, double p_193452_4_, double p_193452_6_) {
      return this.test(p_193452_1_, (float)p_193452_2_, (float)p_193452_4_, (float)p_193452_6_);
   }

   public boolean test(WorldServer p_193453_1_, float p_193453_2_, float p_193453_3_, float p_193453_4_) {
      if (!this.x.test(p_193453_2_)) {
         return false;
      } else if (!this.y.test(p_193453_3_)) {
         return false;
      } else if (!this.z.test(p_193453_4_)) {
         return false;
      } else if (this.dimension != null && this.dimension != p_193453_1_.dimension.getType()) {
         return false;
      } else {
         BlockPos blockpos = new BlockPos((double)p_193453_2_, (double)p_193453_3_, (double)p_193453_4_);
         if (this.biome != null && this.biome != p_193453_1_.getBiome(blockpos)) {
            return false;
         } else {
            return this.feature == null || Feature.isPositionInStructureExact(p_193453_1_, this.feature, blockpos);
         }
      }
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (!this.x.isUnbounded() || !this.y.isUnbounded() || !this.z.isUnbounded()) {
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.add("x", this.x.serialize());
            jsonobject1.add("y", this.y.serialize());
            jsonobject1.add("z", this.z.serialize());
            jsonobject.add("position", jsonobject1);
         }

         if (this.dimension != null) {
            jsonobject.addProperty("dimension", DimensionType.func_212678_a(this.dimension).toString());
         }

         if (this.feature != null) {
            jsonobject.addProperty("feature", this.feature);
         }

         if (this.biome != null) {
            jsonobject.addProperty("biome", IRegistry.field_212624_m.func_177774_c(this.biome).toString());
         }

         return jsonobject;
      }
   }

   public static LocationPredicate deserialize(@Nullable JsonElement p_193454_0_) {
      if (p_193454_0_ != null && !p_193454_0_.isJsonNull()) {
         JsonObject jsonobject = JsonUtils.getJsonObject(p_193454_0_, "location");
         JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonobject, "position", new JsonObject());
         MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound.func_211356_a(jsonobject1.get("x"));
         MinMaxBounds.FloatBound minmaxbounds$floatbound1 = MinMaxBounds.FloatBound.func_211356_a(jsonobject1.get("y"));
         MinMaxBounds.FloatBound minmaxbounds$floatbound2 = MinMaxBounds.FloatBound.func_211356_a(jsonobject1.get("z"));
         DimensionType dimensiontype = jsonobject.has("dimension") ? DimensionType.func_193417_a(new ResourceLocation(JsonUtils.getString(jsonobject, "dimension"))) : null;
         String s = jsonobject.has("feature") ? JsonUtils.getString(jsonobject, "feature") : null;
         Biome biome = null;
         if (jsonobject.has("biome")) {
            ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(jsonobject, "biome"));
            biome = IRegistry.field_212624_m.func_212608_b(resourcelocation);
            if (biome == null) {
               throw new JsonSyntaxException("Unknown biome '" + resourcelocation + "'");
            }
         }

         return new LocationPredicate(minmaxbounds$floatbound, minmaxbounds$floatbound1, minmaxbounds$floatbound2, biome, s, dimensiontype);
      } else {
         return ANY;
      }
   }
}
