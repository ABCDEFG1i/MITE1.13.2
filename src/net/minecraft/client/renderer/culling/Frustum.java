package net.minecraft.client.renderer.culling;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Frustum implements ICamera {
   private final ClippingHelper clippingHelper;
   private double x;
   private double y;
   private double z;

   public Frustum() {
      this(ClippingHelperImpl.getInstance());
   }

   public Frustum(ClippingHelper p_i46196_1_) {
      this.clippingHelper = p_i46196_1_;
   }

   public void setPosition(double p_78547_1_, double p_78547_3_, double p_78547_5_) {
      this.x = p_78547_1_;
      this.y = p_78547_3_;
      this.z = p_78547_5_;
   }

   public boolean isBoxInFrustum(double p_78548_1_, double p_78548_3_, double p_78548_5_, double p_78548_7_, double p_78548_9_, double p_78548_11_) {
      return this.clippingHelper.isBoxInFrustum(p_78548_1_ - this.x, p_78548_3_ - this.y, p_78548_5_ - this.z, p_78548_7_ - this.x, p_78548_9_ - this.y, p_78548_11_ - this.z);
   }

   public boolean isBoundingBoxInFrustum(AxisAlignedBB p_78546_1_) {
      return this.isBoxInFrustum(p_78546_1_.minX, p_78546_1_.minY, p_78546_1_.minZ, p_78546_1_.maxX, p_78546_1_.maxY, p_78546_1_.maxZ);
   }
}
