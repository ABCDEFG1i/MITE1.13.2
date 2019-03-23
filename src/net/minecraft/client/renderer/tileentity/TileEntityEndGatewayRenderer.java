package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityEndGatewayRenderer extends TileEntityEndPortalRenderer {
   private static final ResourceLocation END_GATEWAY_BEAM_TEXTURE = new ResourceLocation("textures/entity/end_gateway_beam.png");

   public void render(TileEntityEndPortal p_199341_1_, double p_199341_2_, double p_199341_4_, double p_199341_6_, float p_199341_8_, int p_199341_9_) {
      GlStateManager.disableFog();
      TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway)p_199341_1_;
      if (tileentityendgateway.isSpawning() || tileentityendgateway.isCoolingDown()) {
         GlStateManager.alphaFunc(516, 0.1F);
         this.bindTexture(END_GATEWAY_BEAM_TEXTURE);
         float f = tileentityendgateway.isSpawning() ? tileentityendgateway.getSpawnPercent(p_199341_8_) : tileentityendgateway.getCooldownPercent(p_199341_8_);
         double d0 = tileentityendgateway.isSpawning() ? 256.0D - p_199341_4_ : 50.0D;
         f = MathHelper.sin(f * (float)Math.PI);
         int i = MathHelper.floor((double)f * d0);
         float[] afloat = tileentityendgateway.isSpawning() ? EnumDyeColor.MAGENTA.getColorComponentValues() : EnumDyeColor.PURPLE.getColorComponentValues();
         TileEntityBeaconRenderer.func_188205_a(p_199341_2_, p_199341_4_, p_199341_6_, (double)p_199341_8_, (double)f, tileentityendgateway.getWorld().getTotalWorldTime(), 0, i, afloat, 0.15D, 0.175D);
         TileEntityBeaconRenderer.func_188205_a(p_199341_2_, p_199341_4_, p_199341_6_, (double)p_199341_8_, (double)f, tileentityendgateway.getWorld().getTotalWorldTime(), 0, -i, afloat, 0.15D, 0.175D);
      }

      super.render(p_199341_1_, p_199341_2_, p_199341_4_, p_199341_6_, p_199341_8_, p_199341_9_);
      GlStateManager.enableFog();
   }

   protected int getPasses(double p_191286_1_) {
      return super.getPasses(p_191286_1_) + 1;
   }

   protected float getOffset() {
      return 1.0F;
   }
}
