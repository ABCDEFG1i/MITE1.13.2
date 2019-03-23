package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;

public class DistancePredicate {
   public static final DistancePredicate ANY = new DistancePredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED);
   private final MinMaxBounds.FloatBound x;
   private final MinMaxBounds.FloatBound y;
   private final MinMaxBounds.FloatBound z;
   private final MinMaxBounds.FloatBound horizontal;
   private final MinMaxBounds.FloatBound absolute;

   public DistancePredicate(MinMaxBounds.FloatBound p_i49724_1_, MinMaxBounds.FloatBound p_i49724_2_, MinMaxBounds.FloatBound p_i49724_3_, MinMaxBounds.FloatBound p_i49724_4_, MinMaxBounds.FloatBound p_i49724_5_) {
      this.x = p_i49724_1_;
      this.y = p_i49724_2_;
      this.z = p_i49724_3_;
      this.horizontal = p_i49724_4_;
      this.absolute = p_i49724_5_;
   }

   public static DistancePredicate func_203995_a(MinMaxBounds.FloatBound p_203995_0_) {
      return new DistancePredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, p_203995_0_, MinMaxBounds.FloatBound.UNBOUNDED);
   }

   public static DistancePredicate func_203993_b(MinMaxBounds.FloatBound p_203993_0_) {
      return new DistancePredicate(MinMaxBounds.FloatBound.UNBOUNDED, p_203993_0_, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED);
   }

   public boolean test(double p_193422_1_, double p_193422_3_, double p_193422_5_, double p_193422_7_, double p_193422_9_, double p_193422_11_) {
      float f = (float)(p_193422_1_ - p_193422_7_);
      float f1 = (float)(p_193422_3_ - p_193422_9_);
      float f2 = (float)(p_193422_5_ - p_193422_11_);
      if (this.x.test(MathHelper.abs(f)) && this.y.test(MathHelper.abs(f1)) && this.z.test(MathHelper.abs(f2))) {
         if (!this.horizontal.testSquared((double)(f * f + f2 * f2))) {
            return false;
         } else {
            return this.absolute.testSquared((double)(f * f + f1 * f1 + f2 * f2));
         }
      } else {
         return false;
      }
   }

   public static DistancePredicate deserialize(@Nullable JsonElement p_193421_0_) {
      if (p_193421_0_ != null && !p_193421_0_.isJsonNull()) {
         JsonObject jsonobject = JsonUtils.getJsonObject(p_193421_0_, "distance");
         MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound.func_211356_a(jsonobject.get("x"));
         MinMaxBounds.FloatBound minmaxbounds$floatbound1 = MinMaxBounds.FloatBound.func_211356_a(jsonobject.get("y"));
         MinMaxBounds.FloatBound minmaxbounds$floatbound2 = MinMaxBounds.FloatBound.func_211356_a(jsonobject.get("z"));
         MinMaxBounds.FloatBound minmaxbounds$floatbound3 = MinMaxBounds.FloatBound.func_211356_a(jsonobject.get("horizontal"));
         MinMaxBounds.FloatBound minmaxbounds$floatbound4 = MinMaxBounds.FloatBound.func_211356_a(jsonobject.get("absolute"));
         return new DistancePredicate(minmaxbounds$floatbound, minmaxbounds$floatbound1, minmaxbounds$floatbound2, minmaxbounds$floatbound3, minmaxbounds$floatbound4);
      } else {
         return ANY;
      }
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("x", this.x.serialize());
         jsonobject.add("y", this.y.serialize());
         jsonobject.add("z", this.z.serialize());
         jsonobject.add("horizontal", this.horizontal.serialize());
         jsonobject.add("absolute", this.absolute.serialize());
         return jsonobject;
      }
   }
}
