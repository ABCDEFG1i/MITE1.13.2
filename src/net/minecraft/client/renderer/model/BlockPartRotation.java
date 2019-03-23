package net.minecraft.client.renderer.model;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockPartRotation {
   public final Vector3f field_178344_a;
   public final EnumFacing.Axis field_178342_b;
   public final float field_178343_c;
   public final boolean field_178341_d;

   public BlockPartRotation(Vector3f p_i47623_1_, EnumFacing.Axis p_i47623_2_, float p_i47623_3_, boolean p_i47623_4_) {
      this.field_178344_a = p_i47623_1_;
      this.field_178342_b = p_i47623_2_;
      this.field_178343_c = p_i47623_3_;
      this.field_178341_d = p_i47623_4_;
   }
}
