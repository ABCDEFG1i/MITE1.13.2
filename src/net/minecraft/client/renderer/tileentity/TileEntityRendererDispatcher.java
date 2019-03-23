package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.model.ModelShulker;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityConduit;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityRendererDispatcher {
   private final Map<Class<? extends TileEntity>, TileEntityRenderer<? extends TileEntity>> renderers = Maps.newHashMap();
   public static TileEntityRendererDispatcher instance = new TileEntityRendererDispatcher();
   public FontRenderer fontRenderer;
   public static double staticPlayerX;
   public static double staticPlayerY;
   public static double staticPlayerZ;
   public TextureManager textureManager;
   public World world;
   public Entity entity;
   public float entityYaw;
   public float entityPitch;
   public RayTraceResult cameraHitResult;
   public double entityX;
   public double entityY;
   public double entityZ;

   private TileEntityRendererDispatcher() {
      this.renderers.put(TileEntitySign.class, new TileEntitySignRenderer());
      this.renderers.put(TileEntityMobSpawner.class, new TileEntityMobSpawnerRenderer());
      this.renderers.put(TileEntityPiston.class, new TileEntityPistonRenderer());
      this.renderers.put(TileEntityChest.class, new TileEntityChestRenderer<>());
      this.renderers.put(TileEntityEnderChest.class, new TileEntityChestRenderer<>());
      this.renderers.put(TileEntityEnchantmentTable.class, new TileEntityEnchantmentTableRenderer());
      this.renderers.put(TileEntityEndPortal.class, new TileEntityEndPortalRenderer());
      this.renderers.put(TileEntityEndGateway.class, new TileEntityEndGatewayRenderer());
      this.renderers.put(TileEntityBeacon.class, new TileEntityBeaconRenderer());
      this.renderers.put(TileEntitySkull.class, new TileEntitySkullRenderer());
      this.renderers.put(TileEntityBanner.class, new TileEntityBannerRenderer());
      this.renderers.put(TileEntityStructure.class, new TileEntityStructureRenderer());
      this.renderers.put(TileEntityShulkerBox.class, new TileEntityShulkerBoxRenderer(new ModelShulker()));
      this.renderers.put(TileEntityBed.class, new TileEntityBedRenderer());
      this.renderers.put(TileEntityConduit.class, new TileEntityConduitRenderer());

      for(TileEntityRenderer<?> tileentityrenderer : this.renderers.values()) {
         tileentityrenderer.setRendererDispatcher(this);
      }

   }

   public <T extends TileEntity> TileEntityRenderer<T> getRenderer(Class<? extends TileEntity> p_147546_1_) {
      TileEntityRenderer<? extends TileEntity> tileentityrenderer = this.renderers.get(p_147546_1_);
      if (tileentityrenderer == null && p_147546_1_ != TileEntity.class) {
         tileentityrenderer = this.getRenderer((Class<? extends TileEntity>)p_147546_1_.getSuperclass());
         this.renderers.put(p_147546_1_, tileentityrenderer);
      }

      return (TileEntityRenderer<T>)tileentityrenderer;
   }

   @Nullable
   public <T extends TileEntity> TileEntityRenderer<T> getRenderer(@Nullable TileEntity p_147547_1_) {
      return p_147547_1_ == null ? null : this.getRenderer(p_147547_1_.getClass());
   }

   public void prepare(World p_190056_1_, TextureManager p_190056_2_, FontRenderer p_190056_3_, Entity p_190056_4_, RayTraceResult p_190056_5_, float p_190056_6_) {
      if (this.world != p_190056_1_) {
         this.setWorld(p_190056_1_);
      }

      this.textureManager = p_190056_2_;
      this.entity = p_190056_4_;
      this.fontRenderer = p_190056_3_;
      this.cameraHitResult = p_190056_5_;
      this.entityYaw = p_190056_4_.prevRotationYaw + (p_190056_4_.rotationYaw - p_190056_4_.prevRotationYaw) * p_190056_6_;
      this.entityPitch = p_190056_4_.prevRotationPitch + (p_190056_4_.rotationPitch - p_190056_4_.prevRotationPitch) * p_190056_6_;
      this.entityX = p_190056_4_.lastTickPosX + (p_190056_4_.posX - p_190056_4_.lastTickPosX) * (double)p_190056_6_;
      this.entityY = p_190056_4_.lastTickPosY + (p_190056_4_.posY - p_190056_4_.lastTickPosY) * (double)p_190056_6_;
      this.entityZ = p_190056_4_.lastTickPosZ + (p_190056_4_.posZ - p_190056_4_.lastTickPosZ) * (double)p_190056_6_;
   }

   public void render(TileEntity p_180546_1_, float p_180546_2_, int p_180546_3_) {
      if (p_180546_1_.getDistanceSq(this.entityX, this.entityY, this.entityZ) < p_180546_1_.getMaxRenderDistanceSquared()) {
         RenderHelper.enableStandardItemLighting();
         int i = this.world.getCombinedLight(p_180546_1_.getPos(), 0);
         int j = i % 65536;
         int k = i / 65536;
         OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, (float)j, (float)k);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         BlockPos blockpos = p_180546_1_.getPos();
         this.render(p_180546_1_, (double)blockpos.getX() - staticPlayerX, (double)blockpos.getY() - staticPlayerY, (double)blockpos.getZ() - staticPlayerZ, p_180546_2_, p_180546_3_, false);
      }

   }

   public void render(TileEntity p_147549_1_, double p_147549_2_, double p_147549_4_, double p_147549_6_, float p_147549_8_) {
      this.render(p_147549_1_, p_147549_2_, p_147549_4_, p_147549_6_, p_147549_8_, -1, false);
   }

   public void renderAsItem(TileEntity p_203601_1_) {
      this.render(p_203601_1_, 0.0D, 0.0D, 0.0D, 0.0F, -1, true);
   }

   public void render(TileEntity p_203602_1_, double p_203602_2_, double p_203602_4_, double p_203602_6_, float p_203602_8_, int p_203602_9_, boolean p_203602_10_) {
      TileEntityRenderer<TileEntity> tileentityrenderer = this.getRenderer(p_203602_1_);
      if (tileentityrenderer != null) {
         try {
            if (!p_203602_10_ && (!p_203602_1_.hasWorld() || !p_203602_1_.getBlockState().getBlock().hasTileEntity())) {
               return;
            }

            tileentityrenderer.render(p_203602_1_, p_203602_2_, p_203602_4_, p_203602_6_, p_203602_8_, p_203602_9_);
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Block Entity");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block Entity Details");
            p_203602_1_.addInfoToCrashReport(crashreportcategory);
            throw new ReportedException(crashreport);
         }
      }

   }

   public void setWorld(@Nullable World p_147543_1_) {
      this.world = p_147543_1_;
      if (p_147543_1_ == null) {
         this.entity = null;
      }

   }

   public FontRenderer getFontRenderer() {
      return this.fontRenderer;
   }
}
