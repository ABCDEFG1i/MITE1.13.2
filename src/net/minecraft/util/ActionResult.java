package net.minecraft.util;

public class ActionResult<T> {
   private final EnumActionResult type;
   private final T result;

   public ActionResult(EnumActionResult p_i46821_1_, T p_i46821_2_) {
      this.type = p_i46821_1_;
      this.result = p_i46821_2_;
   }

   public EnumActionResult getType() {
      return this.type;
   }

   public T getResult() {
      return this.result;
   }
}
