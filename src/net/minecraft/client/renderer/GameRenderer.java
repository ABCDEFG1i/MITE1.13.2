package net.minecraft.client.renderer;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.resources.SimpleResource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.GameType;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GameRenderer implements AutoCloseable, IResourceManagerReloadListener {
   private static final Logger field_147710_q = LogManager.getLogger();
   private static final ResourceLocation field_110924_q = new ResourceLocation("textures/environment/rain.png");
   private static final ResourceLocation field_110923_r = new ResourceLocation("textures/environment/snow.png");
   private final Minecraft field_78531_r;
   private final IResourceManager field_147711_ac;
   private final Random field_78537_ab = new Random();
   private float field_78530_s;
   public final FirstPersonRenderer field_78516_c;
   private final MapItemRenderer field_147709_v;
   private int field_78529_t;
   private Entity field_78528_u;
   private final float field_78490_B = 4.0F;
   private float field_78491_C = 4.0F;
   private float field_78507_R;
   private float field_78506_S;
   private float field_82831_U;
   private float field_82832_V;
   private boolean field_175074_C = true;
   private boolean field_175073_D = true;
   private long field_184374_E;
   private long field_78508_Y = Util.milliTime();
   private final LightTexture field_78513_d;
   private int field_78534_ac;
   private final float[] field_175076_N = new float[1024];
   private final float[] field_175077_O = new float[1024];
   private final FogRenderer field_205003_A;
   private boolean field_175078_W;
   private double field_78503_V = 1.0D;
   private double field_78502_W;
   private double field_78509_X;
   private ItemStack field_190566_ab;
   private int field_190567_ac;
   private float field_190568_ad;
   private float field_190569_ae;
   private ShaderGroup field_147707_d;
   private float field_203000_X;
   private float field_203001_Y;
   private static final ResourceLocation[] field_147712_ad = new ResourceLocation[]{new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json"), new ResourceLocation("shaders/post/creeper.json"), new ResourceLocation("shaders/post/spider.json")};
   public static final int field_147708_e = field_147712_ad.length;
   private int field_147713_ae = field_147708_e;
   private boolean field_175083_ad;
   private int field_175084_ae;

   public GameRenderer(Minecraft p_i45076_1_, IResourceManager p_i45076_2_) {
      this.field_78531_r = p_i45076_1_;
      this.field_147711_ac = p_i45076_2_;
      this.field_78516_c = p_i45076_1_.getFirstPersonRenderer();
      this.field_147709_v = new MapItemRenderer(p_i45076_1_.getTextureManager());
      this.field_78513_d = new LightTexture(this);
      this.field_205003_A = new FogRenderer(this);
      this.field_147707_d = null;

      for(int i = 0; i < 32; ++i) {
         for(int j = 0; j < 32; ++j) {
            float f = (float)(j - 16);
            float f1 = (float)(i - 16);
            float f2 = MathHelper.sqrt(f * f + f1 * f1);
            this.field_175076_N[i << 5 | j] = -f1 / f2;
            this.field_175077_O[i << 5 | j] = f / f2;
         }
      }

   }

   public void close() {
      this.field_78513_d.close();
      this.field_147709_v.close();
      this.func_181022_b();
   }

   public boolean func_147702_a() {
      return OpenGlHelper.shadersSupported && this.field_147707_d != null;
   }

   public void func_181022_b() {
      if (this.field_147707_d != null) {
         this.field_147707_d.close();
      }

      this.field_147707_d = null;
      this.field_147713_ae = field_147708_e;
   }

   public void func_175071_c() {
      this.field_175083_ad = !this.field_175083_ad;
   }

   public void func_175066_a(@Nullable Entity p_175066_1_) {
      if (OpenGlHelper.shadersSupported) {
         if (this.field_147707_d != null) {
            this.field_147707_d.close();
         }

         this.field_147707_d = null;
         if (p_175066_1_ instanceof EntityCreeper) {
            this.func_175069_a(new ResourceLocation("shaders/post/creeper.json"));
         } else if (p_175066_1_ instanceof EntitySpider) {
            this.func_175069_a(new ResourceLocation("shaders/post/spider.json"));
         } else if (p_175066_1_ instanceof EntityEnderman) {
            this.func_175069_a(new ResourceLocation("shaders/post/invert.json"));
         }

      }
   }

   public void func_175069_a(ResourceLocation p_175069_1_) {
      if (this.field_147707_d != null) {
         this.field_147707_d.close();
      }

      try {
         this.field_147707_d = new ShaderGroup(this.field_78531_r.getTextureManager(), this.field_147711_ac, this.field_78531_r.getFramebuffer(), p_175069_1_);
         this.field_147707_d.createBindFramebuffers(this.field_78531_r.mainWindow.getFramebufferWidth(), this.field_78531_r.mainWindow.getFramebufferHeight());
         this.field_175083_ad = true;
      } catch (IOException ioexception) {
         field_147710_q.warn("Failed to load shader: {}", p_175069_1_, ioexception);
         this.field_147713_ae = field_147708_e;
         this.field_175083_ad = false;
      } catch (JsonSyntaxException jsonsyntaxexception) {
         field_147710_q.warn("Failed to load shader: {}", p_175069_1_, jsonsyntaxexception);
         this.field_147713_ae = field_147708_e;
         this.field_175083_ad = false;
      }

   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      if (this.field_147707_d != null) {
         this.field_147707_d.close();
      }

      this.field_147707_d = null;
      if (this.field_147713_ae == field_147708_e) {
         this.func_175066_a(this.field_78531_r.getRenderViewEntity());
      } else {
         this.func_175069_a(field_147712_ad[this.field_147713_ae]);
      }

   }

   public void func_78464_a() {
      if (OpenGlHelper.shadersSupported && ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
         ShaderLinkHelper.setNewStaticShaderLinkHelper();
      }

      this.func_78477_e();
      this.field_78513_d.tick();
      this.field_78491_C = 4.0F;
      if (this.field_78531_r.getRenderViewEntity() == null) {
         this.field_78531_r.setRenderViewEntity(this.field_78531_r.player);
      }

      this.field_203001_Y = this.field_203000_X;
      this.field_203000_X += (this.field_78531_r.getRenderViewEntity().getEyeHeight() - this.field_203000_X) * 0.5F;
      ++this.field_78529_t;
      this.field_78516_c.tick();
      this.func_78484_h();
      this.field_82832_V = this.field_82831_U;
      if (this.field_78531_r.ingameGUI.getBossOverlay().shouldDarkenSky()) {
         this.field_82831_U += 0.05F;
         if (this.field_82831_U > 1.0F) {
            this.field_82831_U = 1.0F;
         }
      } else if (this.field_82831_U > 0.0F) {
         this.field_82831_U -= 0.0125F;
      }

      if (this.field_190567_ac > 0) {
         --this.field_190567_ac;
         if (this.field_190567_ac == 0) {
            this.field_190566_ab = null;
         }
      }

   }

   public ShaderGroup func_147706_e() {
      return this.field_147707_d;
   }

   public void func_147704_a(int p_147704_1_, int p_147704_2_) {
      if (OpenGlHelper.shadersSupported) {
         if (this.field_147707_d != null) {
            this.field_147707_d.createBindFramebuffers(p_147704_1_, p_147704_2_);
         }

         this.field_78531_r.renderGlobal.func_72720_a(p_147704_1_, p_147704_2_);
      }
   }

   public void func_78473_a(float p_78473_1_) {
      Entity entity = this.field_78531_r.getRenderViewEntity();
      if (entity != null) {
         if (this.field_78531_r.world != null) {
            this.field_78531_r.profiler.startSection("pick");
            this.field_78531_r.pointedEntity = null;
            double d0 = (double)this.field_78531_r.playerController.getBlockReachDistance();
            this.field_78531_r.objectMouseOver = entity.rayTrace(d0, p_78473_1_, RayTraceFluidMode.NEVER);
            Vec3d vec3d = entity.getEyePosition(p_78473_1_);
            boolean flag = false;
            double d1 = d0;
            if (this.field_78531_r.playerController.extendedReach()) {
               d1 = 6.0D;
               d0 = d1;
            } else {
               if (d0 > 3.0D) {
                  flag = true;
               }

            }

            if (this.field_78531_r.objectMouseOver != null) {
               d1 = this.field_78531_r.objectMouseOver.hitVec.distanceTo(vec3d);
            }

            Vec3d vec3d1 = entity.getLook(1.0F);
            Vec3d vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
            this.field_78528_u = null;
            Vec3d vec3d3 = null;
            List<Entity> list = this.field_78531_r.world.func_175674_a(entity, entity.getEntityBoundingBox().expand(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0).grow(1.0D, 1.0D, 1.0D), EntitySelectors.NOT_SPECTATING.and(Entity::canBeCollidedWith));
            double d2 = d1;

            for (Entity entity1 : list) {
               AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox()
                       .grow((double) entity1.getCollisionBorderSize());
               RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);
               if (axisalignedbb.contains(vec3d)) {
                  if (d2 >= 0.0D) {
                     this.field_78528_u = entity1;
                     vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                     d2 = 0.0D;
                  }
               } else if (raytraceresult != null) {
                  double d3 = vec3d.distanceTo(raytraceresult.hitVec);
                  if (d3 < d2 || d2 == 0.0D) {
                     if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity()) {
                        if (d2 == 0.0D) {
                           this.field_78528_u = entity1;
                           vec3d3 = raytraceresult.hitVec;
                        }
                     } else {
                        this.field_78528_u = entity1;
                        vec3d3 = raytraceresult.hitVec;
                        d2 = d3;
                     }
                  }
               }
            }

            if (this.field_78528_u != null && flag && vec3d.distanceTo(vec3d3) > 3.0D) {
               this.field_78528_u = null;
               this.field_78531_r.objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, vec3d3, null, new BlockPos(vec3d3));
            }

            if (this.field_78528_u != null && (d2 < d1 || this.field_78531_r.objectMouseOver == null)) {
               this.field_78531_r.objectMouseOver = new RayTraceResult(this.field_78528_u, vec3d3);
               if (this.field_78528_u instanceof EntityLivingBase || this.field_78528_u instanceof EntityItemFrame) {
                  this.field_78531_r.pointedEntity = this.field_78528_u;
               }
            }

            this.field_78531_r.profiler.endSection();
         }
      }
   }

   private void func_78477_e() {
      float f = 1.0F;
      if (this.field_78531_r.getRenderViewEntity() instanceof AbstractClientPlayer) {
         AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer)this.field_78531_r.getRenderViewEntity();
         f = abstractclientplayer.getFovModifier();
      }

      this.field_78506_S = this.field_78507_R;
      this.field_78507_R += (f - this.field_78507_R) * 0.5F;
      if (this.field_78507_R > 1.5F) {
         this.field_78507_R = 1.5F;
      }

      if (this.field_78507_R < 0.1F) {
         this.field_78507_R = 0.1F;
      }

   }

   private double func_195459_a(float p_195459_1_, boolean p_195459_2_) {
      if (this.field_175078_W) {
         return 90.0D;
      } else {
         Entity entity = this.field_78531_r.getRenderViewEntity();
         double d0 = 70.0D;
         if (p_195459_2_) {
            d0 = this.field_78531_r.gameSettings.fovSetting;
            d0 = d0 * (double)(this.field_78506_S + (this.field_78507_R - this.field_78506_S) * p_195459_1_);
         }

         if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getHealth() <= 0.0F) {
            float f = (float)((EntityLivingBase)entity).deathTime + p_195459_1_;
            d0 /= (double)((1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F);
         }

         IFluidState ifluidstate = ActiveRenderInfo.getFluidStateAtEntityViewpoint(this.field_78531_r.world, entity, p_195459_1_);
         if (!ifluidstate.isEmpty()) {
            d0 = d0 * 60.0D / 70.0D;
         }

         return d0;
      }
   }

   private void func_78482_e(float p_78482_1_) {
      if (this.field_78531_r.getRenderViewEntity() instanceof EntityLivingBase) {
         EntityLivingBase entitylivingbase = (EntityLivingBase)this.field_78531_r.getRenderViewEntity();
         float f = (float)entitylivingbase.hurtTime - p_78482_1_;
         if (entitylivingbase.getHealth() <= 0.0F) {
            float f1 = (float)entitylivingbase.deathTime + p_78482_1_;
            GlStateManager.rotatef(40.0F - 8000.0F / (f1 + 200.0F), 0.0F, 0.0F, 1.0F);
         }

         if (f < 0.0F) {
            return;
         }

         f = f / (float)entitylivingbase.maxHurtTime;
         f = MathHelper.sin(f * f * f * f * (float)Math.PI);
         float f2 = entitylivingbase.attackedAtYaw;
         GlStateManager.rotatef(-f2, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(-f * 14.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(f2, 0.0F, 1.0F, 0.0F);
      }

   }

   private void func_78475_f(float p_78475_1_) {
      if (this.field_78531_r.getRenderViewEntity() instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)this.field_78531_r.getRenderViewEntity();
         float f = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
         float f1 = -(entityplayer.distanceWalkedModified + f * p_78475_1_);
         float f2 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * p_78475_1_;
         float f3 = entityplayer.prevCameraPitch + (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * p_78475_1_;
         GlStateManager.translatef(MathHelper.sin(f1 * (float)Math.PI) * f2 * 0.5F, -Math.abs(MathHelper.cos(f1 * (float)Math.PI) * f2), 0.0F);
         GlStateManager.rotatef(MathHelper.sin(f1 * (float)Math.PI) * f2 * 3.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(Math.abs(MathHelper.cos(f1 * (float)Math.PI - 0.2F) * f2) * 5.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(f3, 1.0F, 0.0F, 0.0F);
      }
   }

   private void func_78467_g(float p_78467_1_) {
      Entity entity = this.field_78531_r.getRenderViewEntity();
      float f = this.field_203001_Y + (this.field_203000_X - this.field_203001_Y) * p_78467_1_;
      double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)p_78467_1_;
      double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)p_78467_1_ + (double)entity.getEyeHeight();
      double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)p_78467_1_;
      if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPlayerSleeping()) {
         f = (float)((double)f + 1.0D);
         GlStateManager.translatef(0.0F, 0.3F, 0.0F);
         if (!this.field_78531_r.gameSettings.debugCamEnable) {
            BlockPos blockpos = new BlockPos(entity);
            IBlockState iblockstate = this.field_78531_r.world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();
            if (block instanceof BlockBed) {
               GlStateManager.rotatef(iblockstate.get(BlockBed.HORIZONTAL_FACING).getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * p_78467_1_ + 180.0F, 0.0F, -1.0F, 0.0F);
            GlStateManager.rotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * p_78467_1_, -1.0F, 0.0F, 0.0F);
         }
      } else if (this.field_78531_r.gameSettings.thirdPersonView > 0) {
         double d3 = (double)(this.field_78491_C + (4.0F - this.field_78491_C) * p_78467_1_);
         if (this.field_78531_r.gameSettings.debugCamEnable) {
            GlStateManager.translatef(0.0F, 0.0F, (float)(-d3));
         } else {
            float f1 = entity.rotationYaw;
            float f2 = entity.rotationPitch;
            if (this.field_78531_r.gameSettings.thirdPersonView == 2) {
               f2 += 180.0F;
            }

            double d4 = (double)(-MathHelper.sin(f1 * ((float)Math.PI / 180F)) * MathHelper.cos(f2 * ((float)Math.PI / 180F))) * d3;
            double d5 = (double)(MathHelper.cos(f1 * ((float)Math.PI / 180F)) * MathHelper.cos(f2 * ((float)Math.PI / 180F))) * d3;
            double d6 = (double)(-MathHelper.sin(f2 * ((float)Math.PI / 180F))) * d3;

            for(int i = 0; i < 8; ++i) {
               float f3 = (float)((i & 1) * 2 - 1);
               float f4 = (float)((i >> 1 & 1) * 2 - 1);
               float f5 = (float)((i >> 2 & 1) * 2 - 1);
               f3 = f3 * 0.1F;
               f4 = f4 * 0.1F;
               f5 = f5 * 0.1F;
               RayTraceResult raytraceresult = this.field_78531_r.world.rayTraceBlocks(new Vec3d(d0 + (double)f3, d1 + (double)f4, d2 + (double)f5), new Vec3d(d0 - d4 + (double)f3 + (double)f5, d1 - d6 + (double)f4, d2 - d5 + (double)f5));
               if (raytraceresult != null) {
                  double d7 = raytraceresult.hitVec.distanceTo(new Vec3d(d0, d1, d2));
                  if (d7 < d3) {
                     d3 = d7;
                  }
               }
            }

            if (this.field_78531_r.gameSettings.thirdPersonView == 2) {
               GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotatef(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
            GlStateManager.translatef(0.0F, 0.0F, (float)(-d3));
            GlStateManager.rotatef(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
         }
      } else if (!this.field_175078_W) {
         GlStateManager.translatef(0.0F, 0.0F, 0.05F);
      }

      if (!this.field_78531_r.gameSettings.debugCamEnable) {
         GlStateManager.rotatef(entity.getPitch(p_78467_1_), 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(entity.getYaw(p_78467_1_) + 180.0F, 0.0F, 1.0F, 0.0F);
      }

      GlStateManager.translatef(0.0F, -f, 0.0F);
   }

   private void func_195460_g(float p_195460_1_) {
      this.field_78530_s = (float)(this.field_78531_r.gameSettings.renderDistanceChunks * 16);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      if (this.field_78503_V != 1.0D) {
         GlStateManager.translatef((float)this.field_78502_W, (float)(-this.field_78509_X), 0.0F);
         GlStateManager.scaled(this.field_78503_V, this.field_78503_V, 1.0D);
      }

      GlStateManager.multMatrixf(Matrix4f.perspective(this.func_195459_a(p_195460_1_, true), (float)this.field_78531_r.mainWindow.getFramebufferWidth() / (float)this.field_78531_r.mainWindow.getFramebufferHeight(), 0.05F, this.field_78530_s * MathHelper.SQRT_2));
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      this.func_78482_e(p_195460_1_);
      if (this.field_78531_r.gameSettings.viewBobbing) {
         this.func_78475_f(p_195460_1_);
      }

      float f = this.field_78531_r.player.prevTimeInPortal + (this.field_78531_r.player.timeInPortal - this.field_78531_r.player.prevTimeInPortal) * p_195460_1_;
      if (f > 0.0F) {
         int i = 20;
         if (this.field_78531_r.player.isPotionActive(MobEffects.NAUSEA)) {
            i = 7;
         }

         float f1 = 5.0F / (f * f + 5.0F) - f * 0.04F;
         f1 = f1 * f1;
         GlStateManager.rotatef(((float)this.field_78529_t + p_195460_1_) * (float)i, 0.0F, 1.0F, 1.0F);
         GlStateManager.scalef(1.0F / f1, 1.0F, 1.0F);
         GlStateManager.rotatef(-((float)this.field_78529_t + p_195460_1_) * (float)i, 0.0F, 1.0F, 1.0F);
      }

      this.func_78467_g(p_195460_1_);
   }

   private void func_195457_h(float p_195457_1_) {
      if (!this.field_175078_W) {
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrixf(Matrix4f.perspective(this.func_195459_a(p_195457_1_, false), (float)this.field_78531_r.mainWindow.getFramebufferWidth() / (float)this.field_78531_r.mainWindow.getFramebufferHeight(), 0.05F, this.field_78530_s * 2.0F));
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.pushMatrix();
         this.func_78482_e(p_195457_1_);
         if (this.field_78531_r.gameSettings.viewBobbing) {
            this.func_78475_f(p_195457_1_);
         }

         boolean flag = this.field_78531_r.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)this.field_78531_r.getRenderViewEntity()).isPlayerSleeping();
         if (this.field_78531_r.gameSettings.thirdPersonView == 0 && !flag && !this.field_78531_r.gameSettings.hideGUI && this.field_78531_r.playerController.getCurrentGameType() != GameType.SPECTATOR) {
            this.func_180436_i();
            this.field_78516_c.renderItemInFirstPerson(p_195457_1_);
            this.func_175072_h();
         }

         GlStateManager.popMatrix();
         if (this.field_78531_r.gameSettings.thirdPersonView == 0 && !flag) {
            this.field_78516_c.renderOverlays(p_195457_1_);
            this.func_78482_e(p_195457_1_);
         }

         if (this.field_78531_r.gameSettings.viewBobbing) {
            this.func_78475_f(p_195457_1_);
         }

      }
   }

   public void func_175072_h() {
      this.field_78513_d.func_205108_b();
   }

   public void func_180436_i() {
      this.field_78513_d.func_205109_c();
   }

   public float func_180438_a(EntityLivingBase p_180438_1_, float p_180438_2_) {
      int i = p_180438_1_.getActivePotionEffect(MobEffects.NIGHT_VISION).getDuration();
      return i > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)i - p_180438_2_) * (float)Math.PI * 0.2F) * 0.3F;
   }

   public void func_195458_a(float p_195458_1_, long p_195458_2_, boolean p_195458_4_) {
      if (!this.field_78531_r.isGameFocused() && this.field_78531_r.gameSettings.pauseOnLostFocus && (!this.field_78531_r.gameSettings.touchscreen || !this.field_78531_r.mouseHelper.isRightDown())) {
         if (Util.milliTime() - this.field_78508_Y > 500L) {
            this.field_78531_r.displayInGameMenu();
         }
      } else {
         this.field_78508_Y = Util.milliTime();
      }

      if (!this.field_78531_r.skipRenderWorld) {
         int i = (int)(this.field_78531_r.mouseHelper.getMouseX() * (double)this.field_78531_r.mainWindow.getScaledWidth() / (double)this.field_78531_r.mainWindow.getWidth());
         int j = (int)(this.field_78531_r.mouseHelper.getMouseY() * (double)this.field_78531_r.mainWindow.getScaledHeight() / (double)this.field_78531_r.mainWindow.getHeight());
         int k = this.field_78531_r.gameSettings.limitFramerate;
         if (p_195458_4_ && this.field_78531_r.world != null) {
            this.field_78531_r.profiler.startSection("level");
            int l = Math.min(Minecraft.getDebugFPS(), k);
            l = Math.max(l, 60);
            long i1 = Util.nanoTime() - p_195458_2_;
            long j1 = Math.max((long)(1000000000 / l / 4) - i1, 0L);
            this.func_78471_a(p_195458_1_, Util.nanoTime() + j1);
            if (this.field_78531_r.isSingleplayer() && this.field_184374_E < Util.milliTime() - 1000L) {
               this.field_184374_E = Util.milliTime();
               if (!this.field_78531_r.getIntegratedServer().isWorldIconSet()) {
                  this.func_184373_n();
               }
            }

            if (OpenGlHelper.shadersSupported) {
               this.field_78531_r.renderGlobal.func_174975_c();
               if (this.field_147707_d != null && this.field_175083_ad) {
                  GlStateManager.matrixMode(5890);
                  GlStateManager.pushMatrix();
                  GlStateManager.loadIdentity();
                  this.field_147707_d.render(p_195458_1_);
                  GlStateManager.popMatrix();
               }

               this.field_78531_r.getFramebuffer().bindFramebuffer(true);
            }

            this.field_78531_r.profiler.endStartSection("gui");
            if (!this.field_78531_r.gameSettings.hideGUI || this.field_78531_r.currentScreen != null) {
               GlStateManager.alphaFunc(516, 0.1F);
               this.field_78531_r.mainWindow.setupOverlayRendering();
               this.func_190563_a(this.field_78531_r.mainWindow.getScaledWidth(), this.field_78531_r.mainWindow.getScaledHeight(), p_195458_1_);
               this.field_78531_r.ingameGUI.renderGameOverlay(p_195458_1_);
            }

            this.field_78531_r.profiler.endSection();
         } else {
            GlStateManager.viewport(0, 0, this.field_78531_r.mainWindow.getFramebufferWidth(), this.field_78531_r.mainWindow.getFramebufferHeight());
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            this.field_78531_r.mainWindow.setupOverlayRendering();
         }

         if (this.field_78531_r.currentScreen != null) {
            GlStateManager.clear(256);

            try {
               this.field_78531_r.currentScreen.render(i, j, this.field_78531_r.getTickLength());
            } catch (Throwable throwable) {
               CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering screen");
               CrashReportCategory crashreportcategory = crashreport.makeCategory("Screen render details");
               crashreportcategory.addDetail("Screen name", () -> {
                  return this.field_78531_r.currentScreen.getClass().getCanonicalName();
               });
               crashreportcategory.addDetail("Mouse location", () -> {
                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.field_78531_r.mouseHelper.getMouseX(), this.field_78531_r.mouseHelper.getMouseY());
               });
               crashreportcategory.addDetail("Screen size", () -> {
                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.field_78531_r.mainWindow.getScaledWidth(), this.field_78531_r.mainWindow.getScaledHeight(), this.field_78531_r.mainWindow.getFramebufferWidth(), this.field_78531_r.mainWindow.getFramebufferHeight(), this.field_78531_r.mainWindow.getGuiScaleFactor());
               });
               throw new ReportedException(crashreport);
            }
         }

      }
   }

   private void func_184373_n() {
      if (this.field_78531_r.renderGlobal.func_184382_g() > 10 && this.field_78531_r.renderGlobal.func_184384_n() && !this.field_78531_r.getIntegratedServer().isWorldIconSet()) {
         NativeImage nativeimage = ScreenShotHelper.createScreenshot(this.field_78531_r.mainWindow.getFramebufferWidth(), this.field_78531_r.mainWindow.getFramebufferHeight(), this.field_78531_r.getFramebuffer());
         SimpleResource.RESOURCE_IO_EXECUTOR.execute(() -> {
            int i = nativeimage.getWidth();
            int j = nativeimage.getHeight();
            int k = 0;
            int l = 0;
            if (i > j) {
               k = (i - j) / 2;
               i = j;
            } else {
               l = (j - i) / 2;
               j = i;
            }

            try (NativeImage nativeimage1 = new NativeImage(64, 64, false)) {
               nativeimage.resizeSubRectTo(k, l, i, j, nativeimage1);
               nativeimage1.write(this.field_78531_r.getIntegratedServer().getWorldIconFile());
            } catch (IOException ioexception) {
               field_147710_q.warn("Couldn't save auto screenshot", ioexception);
            } finally {
               nativeimage.close();
            }

         });
      }

   }

   public void func_152430_c(float p_152430_1_) {
      this.field_78531_r.mainWindow.setupOverlayRendering();
   }

   private boolean func_175070_n() {
      if (!this.field_175073_D) {
         return false;
      } else {
         Entity entity = this.field_78531_r.getRenderViewEntity();
         boolean flag = entity instanceof EntityPlayer && !this.field_78531_r.gameSettings.hideGUI;
         if (flag && !((EntityPlayer)entity).capabilities.allowEdit) {
            ItemStack itemstack = ((EntityPlayer)entity).getHeldItemMainhand();
            if (this.field_78531_r.objectMouseOver != null && this.field_78531_r.objectMouseOver.type == RayTraceResult.Type.BLOCK) {
               BlockPos blockpos = this.field_78531_r.objectMouseOver.getBlockPos();
               Block block = this.field_78531_r.world.getBlockState(blockpos).getBlock();
               if (this.field_78531_r.playerController.getCurrentGameType() == GameType.SPECTATOR) {
                  flag = block.hasTileEntity() && this.field_78531_r.world.getTileEntity(blockpos) instanceof IInventory;
               } else {
                  BlockWorldState blockworldstate = new BlockWorldState(this.field_78531_r.world, blockpos, false);
                  flag = !itemstack.isEmpty() && (itemstack.canDestroy(this.field_78531_r.world.getTags(), blockworldstate) || itemstack.canPlaceOn(this.field_78531_r.world.getTags(), blockworldstate));
               }
            }
         }

         return flag;
      }
   }

   public void func_78471_a(float p_78471_1_, long p_78471_2_) {
      this.field_78513_d.func_205106_a(p_78471_1_);
      if (this.field_78531_r.getRenderViewEntity() == null) {
         this.field_78531_r.setRenderViewEntity(this.field_78531_r.player);
      }

      this.func_78473_a(p_78471_1_);
      GlStateManager.enableDepthTest();
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.5F);
      this.field_78531_r.profiler.startSection("center");
      this.func_181560_a(p_78471_1_, p_78471_2_);
      this.field_78531_r.profiler.endSection();
   }

   private void func_181560_a(float p_181560_1_, long p_181560_2_) {
      WorldRenderer worldrenderer = this.field_78531_r.renderGlobal;
      ParticleManager particlemanager = this.field_78531_r.effectRenderer;
      boolean flag = this.func_175070_n();
      GlStateManager.enableCull();
      this.field_78531_r.profiler.endStartSection("clear");
      GlStateManager.viewport(0, 0, this.field_78531_r.mainWindow.getFramebufferWidth(), this.field_78531_r.mainWindow.getFramebufferHeight());
      this.field_205003_A.updateFogColor(p_181560_1_);
      GlStateManager.clear(16640);
      this.field_78531_r.profiler.endStartSection("camera");
      this.func_195460_g(p_181560_1_);
      ActiveRenderInfo.updateRenderInfo(this.field_78531_r.player, this.field_78531_r.gameSettings.thirdPersonView == 2, this.field_78530_s);
      this.field_78531_r.profiler.endStartSection("frustum");
      ClippingHelperImpl.getInstance();
      this.field_78531_r.profiler.endStartSection("culling");
      ICamera icamera = new Frustum();
      Entity entity = this.field_78531_r.getRenderViewEntity();
      double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)p_181560_1_;
      double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)p_181560_1_;
      double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)p_181560_1_;
      icamera.setPosition(d0, d1, d2);
      if (this.field_78531_r.gameSettings.renderDistanceChunks >= 4) {
         this.field_205003_A.setupFog(-1, p_181560_1_);
         this.field_78531_r.profiler.endStartSection("sky");
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrixf(Matrix4f.perspective(this.func_195459_a(p_181560_1_, true), (float)this.field_78531_r.mainWindow.getFramebufferWidth() / (float)this.field_78531_r.mainWindow.getFramebufferHeight(), 0.05F, this.field_78530_s * 2.0F));
         GlStateManager.matrixMode(5888);
         worldrenderer.func_195465_a(p_181560_1_);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrixf(Matrix4f.perspective(this.func_195459_a(p_181560_1_, true), (float)this.field_78531_r.mainWindow.getFramebufferWidth() / (float)this.field_78531_r.mainWindow.getFramebufferHeight(), 0.05F, this.field_78530_s * MathHelper.SQRT_2));
         GlStateManager.matrixMode(5888);
      }

      this.field_205003_A.setupFog(0, p_181560_1_);
      GlStateManager.shadeModel(7425);
      if (entity.posY + (double)entity.getEyeHeight() < 128.0D) {
         this.func_195456_a(worldrenderer, p_181560_1_, d0, d1, d2);
      }

      this.field_78531_r.profiler.endStartSection("prepareterrain");
      this.field_205003_A.setupFog(0, p_181560_1_);
      this.field_78531_r.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      RenderHelper.disableStandardItemLighting();
      this.field_78531_r.profiler.endStartSection("terrain_setup");
      worldrenderer.func_195473_a(entity, p_181560_1_, icamera, this.field_175084_ae++, this.field_78531_r.player.isSpectator());
      this.field_78531_r.profiler.endStartSection("updatechunks");
      this.field_78531_r.renderGlobal.func_174967_a(p_181560_2_);
      this.field_78531_r.profiler.endStartSection("terrain");
      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.disableAlphaTest();
      worldrenderer.func_195464_a(BlockRenderLayer.SOLID, (double)p_181560_1_, entity);
      GlStateManager.enableAlphaTest();
      worldrenderer.func_195464_a(BlockRenderLayer.CUTOUT_MIPPED, (double)p_181560_1_, entity);
      this.field_78531_r.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      worldrenderer.func_195464_a(BlockRenderLayer.CUTOUT, (double)p_181560_1_, entity);
      this.field_78531_r.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
      GlStateManager.shadeModel(7424);
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      RenderHelper.enableStandardItemLighting();
      this.field_78531_r.profiler.endStartSection("entities");
      worldrenderer.func_180446_a(entity, icamera, p_181560_1_);
      RenderHelper.disableStandardItemLighting();
      this.func_175072_h();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      if (flag && this.field_78531_r.objectMouseOver != null) {
         EntityPlayer entityplayer = (EntityPlayer)entity;
         GlStateManager.disableAlphaTest();
         this.field_78531_r.profiler.endStartSection("outline");
         worldrenderer.func_72731_b(entityplayer, this.field_78531_r.objectMouseOver, 0, p_181560_1_);
         GlStateManager.enableAlphaTest();
      }

      if (this.field_78531_r.debugRenderer.shouldRender()) {
         this.field_78531_r.debugRenderer.renderDebug(p_181560_1_, p_181560_2_);
      }

      this.field_78531_r.profiler.endStartSection("destroyProgress");
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      this.field_78531_r.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      worldrenderer.func_174981_a(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), entity, p_181560_1_);
      this.field_78531_r.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
      GlStateManager.disableBlend();
      this.func_180436_i();
      this.field_78531_r.profiler.endStartSection("litParticles");
      particlemanager.renderLitParticles(entity, p_181560_1_);
      RenderHelper.disableStandardItemLighting();
      this.field_205003_A.setupFog(0, p_181560_1_);
      this.field_78531_r.profiler.endStartSection("particles");
      particlemanager.renderParticles(entity, p_181560_1_);
      this.func_175072_h();
      GlStateManager.depthMask(false);
      GlStateManager.enableCull();
      this.field_78531_r.profiler.endStartSection("weather");
      this.func_78474_d(p_181560_1_);
      GlStateManager.depthMask(true);
      worldrenderer.func_180449_a(entity, p_181560_1_);
      GlStateManager.disableBlend();
      GlStateManager.enableCull();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.alphaFunc(516, 0.1F);
      this.field_205003_A.setupFog(0, p_181560_1_);
      GlStateManager.enableBlend();
      GlStateManager.depthMask(false);
      this.field_78531_r.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      GlStateManager.shadeModel(7425);
      this.field_78531_r.profiler.endStartSection("translucent");
      worldrenderer.func_195464_a(BlockRenderLayer.TRANSLUCENT, (double)p_181560_1_, entity);
      GlStateManager.shadeModel(7424);
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GlStateManager.disableFog();
      if (entity.posY + (double)entity.getEyeHeight() >= 128.0D) {
         this.field_78531_r.profiler.endStartSection("aboveClouds");
         this.func_195456_a(worldrenderer, p_181560_1_, d0, d1, d2);
      }

      this.field_78531_r.profiler.endStartSection("hand");
      if (this.field_175074_C) {
         GlStateManager.clear(256);
         this.func_195457_h(p_181560_1_);
      }

   }

   private void func_195456_a(WorldRenderer p_195456_1_, float p_195456_2_, double p_195456_3_, double p_195456_5_, double p_195456_7_) {
      if (this.field_78531_r.gameSettings.shouldRenderClouds() != 0) {
         this.field_78531_r.profiler.endStartSection("clouds");
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrixf(Matrix4f.perspective(this.func_195459_a(p_195456_2_, true), (float)this.field_78531_r.mainWindow.getFramebufferWidth() / (float)this.field_78531_r.mainWindow.getFramebufferHeight(), 0.05F, this.field_78530_s * 4.0F));
         GlStateManager.matrixMode(5888);
         GlStateManager.pushMatrix();
         this.field_205003_A.setupFog(0, p_195456_2_);
         p_195456_1_.func_195466_a(p_195456_2_, p_195456_3_, p_195456_5_, p_195456_7_);
         GlStateManager.disableFog();
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrixf(Matrix4f.perspective(this.func_195459_a(p_195456_2_, true), (float)this.field_78531_r.mainWindow.getFramebufferWidth() / (float)this.field_78531_r.mainWindow.getFramebufferHeight(), 0.05F, this.field_78530_s * MathHelper.SQRT_2));
         GlStateManager.matrixMode(5888);
      }

   }

   private void func_78484_h() {
      float f = this.field_78531_r.world.getRainStrength(1.0F);
      if (!this.field_78531_r.gameSettings.fancyGraphics) {
         f /= 2.0F;
      }

      if (f != 0.0F) {
         this.field_78537_ab.setSeed((long)this.field_78529_t * 312987231L);
         Entity entity = this.field_78531_r.getRenderViewEntity();
         IWorldReaderBase iworldreaderbase = this.field_78531_r.world;
         BlockPos blockpos = new BlockPos(entity);
         int i = 10;
         double d0 = 0.0D;
         double d1 = 0.0D;
         double d2 = 0.0D;
         int j = 0;
         int k = (int)(100.0F * f * f);
         if (this.field_78531_r.gameSettings.particleSetting == 1) {
            k >>= 1;
         } else if (this.field_78531_r.gameSettings.particleSetting == 2) {
            k = 0;
         }

         for(int l = 0; l < k; ++l) {
            BlockPos blockpos1 = iworldreaderbase.getHeight(Heightmap.Type.MOTION_BLOCKING, blockpos.add(this.field_78537_ab.nextInt(10) - this.field_78537_ab.nextInt(10), 0, this.field_78537_ab.nextInt(10) - this.field_78537_ab.nextInt(10)));
            Biome biome = iworldreaderbase.getBiome(blockpos1);
            BlockPos blockpos2 = blockpos1.down();
            if (blockpos1.getY() <= blockpos.getY() + 10 && blockpos1.getY() >= blockpos.getY() - 10 && biome.getPrecipitation() == Biome.RainType.RAIN && biome.getTemperature(blockpos1) >= 0.15F) {
               double d3 = this.field_78537_ab.nextDouble();
               double d4 = this.field_78537_ab.nextDouble();
               IBlockState iblockstate = iworldreaderbase.getBlockState(blockpos2);
               IFluidState ifluidstate = iworldreaderbase.getFluidState(blockpos1);
               VoxelShape voxelshape = iblockstate.getCollisionShape(iworldreaderbase, blockpos2);
               double d7 = voxelshape.func_197760_b(EnumFacing.Axis.Y, d3, d4);
               double d8 = (double)ifluidstate.getHeight();
               double d5;
               double d6;
               if (d7 >= d8) {
                  d5 = d7;
                  d6 = voxelshape.func_197764_a(EnumFacing.Axis.Y, d3, d4);
               } else {
                  d5 = 0.0D;
                  d6 = 0.0D;
               }

               if (d5 > -Double.MAX_VALUE) {
                  if (!ifluidstate.isTagged(FluidTags.LAVA) && iblockstate.getBlock() != Blocks.MAGMA_BLOCK) {
                     ++j;
                     if (this.field_78537_ab.nextInt(j) == 0) {
                        d0 = (double)blockpos2.getX() + d3;
                        d1 = (double)((float)blockpos2.getY() + 0.1F) + d5 - 1.0D;
                        d2 = (double)blockpos2.getZ() + d4;
                     }

                     this.field_78531_r.world.spawnParticle(Particles.RAIN, (double)blockpos2.getX() + d3, (double)((float)blockpos2.getY() + 0.1F) + d5, (double)blockpos2.getZ() + d4, 0.0D, 0.0D, 0.0D);
                  } else {
                     this.field_78531_r.world.spawnParticle(Particles.SMOKE, (double)blockpos1.getX() + d3, (double)((float)blockpos1.getY() + 0.1F) - d6, (double)blockpos1.getZ() + d4, 0.0D, 0.0D, 0.0D);
                  }
               }
            }
         }

         if (j > 0 && this.field_78537_ab.nextInt(3) < this.field_78534_ac++) {
            this.field_78534_ac = 0;
            if (d1 > (double)(blockpos.getY() + 1) && iworldreaderbase.getHeight(Heightmap.Type.MOTION_BLOCKING, blockpos).getY() > MathHelper.floor((float)blockpos.getY())) {
               this.field_78531_r.world.playSound(d0, d1, d2, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1F, 0.5F, false);
            } else {
               this.field_78531_r.world.playSound(d0, d1, d2, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2F, 1.0F, false);
            }
         }

      }
   }

   protected void func_78474_d(float p_78474_1_) {
      float f = this.field_78531_r.world.getRainStrength(p_78474_1_);
      if (!(f <= 0.0F)) {
         this.func_180436_i();
         Entity entity = this.field_78531_r.getRenderViewEntity();
         World world = this.field_78531_r.world;
         int i = MathHelper.floor(entity.posX);
         int j = MathHelper.floor(entity.posY);
         int k = MathHelper.floor(entity.posZ);
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         GlStateManager.disableCull();
         GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.alphaFunc(516, 0.1F);
         double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)p_78474_1_;
         double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)p_78474_1_;
         double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)p_78474_1_;
         int l = MathHelper.floor(d1);
         int i1 = 5;
         if (this.field_78531_r.gameSettings.fancyGraphics) {
            i1 = 10;
         }

         int j1 = -1;
         float f1 = (float)this.field_78529_t + p_78474_1_;
         bufferbuilder.setTranslation(-d0, -d1, -d2);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int k1 = k - i1; k1 <= k + i1; ++k1) {
            for(int l1 = i - i1; l1 <= i + i1; ++l1) {
               int i2 = (k1 - k + 16) * 32 + l1 - i + 16;
               double d3 = (double)this.field_175076_N[i2] * 0.5D;
               double d4 = (double)this.field_175077_O[i2] * 0.5D;
               blockpos$mutableblockpos.setPos(l1, 0, k1);
               Biome biome = world.getBiome(blockpos$mutableblockpos);
               if (biome.getPrecipitation() != Biome.RainType.NONE) {
                  int j2 = world.getHeight(Heightmap.Type.MOTION_BLOCKING, blockpos$mutableblockpos).getY();
                  int k2 = j - i1;
                  int l2 = j + i1;
                  if (k2 < j2) {
                     k2 = j2;
                  }

                  if (l2 < j2) {
                     l2 = j2;
                  }

                  int i3 = j2;
                  if (j2 < l) {
                     i3 = l;
                  }

                  if (k2 != l2) {
                     this.field_78537_ab.setSeed((long)(l1 * l1 * 3121 + l1 * 45238971 ^ k1 * k1 * 418711 + k1 * 13761));
                     blockpos$mutableblockpos.setPos(l1, k2, k1);
                     float f2 = biome.getTemperature(blockpos$mutableblockpos);
                     if (f2 >= 0.15F) {
                        if (j1 != 0) {
                           if (j1 >= 0) {
                              tessellator.draw();
                           }

                           j1 = 0;
                           this.field_78531_r.getTextureManager().bindTexture(field_110924_q);
                           bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                        }

                        double d5 = -((double)(this.field_78529_t + l1 * l1 * 3121 + l1 * 45238971 + k1 * k1 * 418711 + k1 * 13761 & 31) + (double)p_78474_1_) / 32.0D * (3.0D + this.field_78537_ab.nextDouble());
                        double d6 = (double)((float)l1 + 0.5F) - entity.posX;
                        double d7 = (double)((float)k1 + 0.5F) - entity.posZ;
                        float f3 = MathHelper.sqrt(d6 * d6 + d7 * d7) / (float)i1;
                        float f4 = ((1.0F - f3 * f3) * 0.5F + 0.5F) * f;
                        blockpos$mutableblockpos.setPos(l1, i3, k1);
                        int j3 = world.getCombinedLight(blockpos$mutableblockpos, 0);
                        int k3 = j3 >> 16 & '\uffff';
                        int l3 = j3 & '\uffff';
                        bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)l2, (double)k1 - d4 + 0.5D).tex(0.0D, (double)k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                        bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)l2, (double)k1 + d4 + 0.5D).tex(1.0D, (double)k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                        bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)k2, (double)k1 + d4 + 0.5D).tex(1.0D, (double)l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                        bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)k2, (double)k1 - d4 + 0.5D).tex(0.0D, (double)l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                     } else {
                        if (j1 != 1) {
                           if (j1 >= 0) {
                              tessellator.draw();
                           }

                           j1 = 1;
                           this.field_78531_r.getTextureManager().bindTexture(field_110923_r);
                           bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                        }

                        double d8 = (double)(-((float)(this.field_78529_t & 511) + p_78474_1_) / 512.0F);
                        double d9 = this.field_78537_ab.nextDouble() + (double)f1 * 0.01D * (double)((float)this.field_78537_ab.nextGaussian());
                        double d10 = this.field_78537_ab.nextDouble() + (double)(f1 * (float)this.field_78537_ab.nextGaussian()) * 0.001D;
                        double d11 = (double)((float)l1 + 0.5F) - entity.posX;
                        double d12 = (double)((float)k1 + 0.5F) - entity.posZ;
                        float f6 = MathHelper.sqrt(d11 * d11 + d12 * d12) / (float)i1;
                        float f5 = ((1.0F - f6 * f6) * 0.3F + 0.5F) * f;
                        blockpos$mutableblockpos.setPos(l1, i3, k1);
                        int i4 = (world.getCombinedLight(blockpos$mutableblockpos, 0) * 3 + 15728880) / 4;
                        int j4 = i4 >> 16 & '\uffff';
                        int k4 = i4 & '\uffff';
                        bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)l2, (double)k1 - d4 + 0.5D).tex(0.0D + d9, (double)k2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                        bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)l2, (double)k1 + d4 + 0.5D).tex(1.0D + d9, (double)k2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                        bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)k2, (double)k1 + d4 + 0.5D).tex(1.0D + d9, (double)l2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                        bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)k2, (double)k1 - d4 + 0.5D).tex(0.0D + d9, (double)l2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                     }
                  }
               }
            }
         }

         if (j1 >= 0) {
            tessellator.draw();
         }

         bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
         GlStateManager.enableCull();
         GlStateManager.disableBlend();
         GlStateManager.alphaFunc(516, 0.1F);
         this.func_175072_h();
      }
   }

   public void func_191514_d(boolean p_191514_1_) {
      this.field_205003_A.applyFog(p_191514_1_);
   }

   public void func_190564_k() {
      this.field_190566_ab = null;
      this.field_147709_v.clearLoadedMaps();
   }

   public MapItemRenderer func_147701_i() {
      return this.field_147709_v;
   }

   public static void func_189692_a(FontRenderer p_189692_0_, String p_189692_1_, float p_189692_2_, float p_189692_3_, float p_189692_4_, int p_189692_5_, float p_189692_6_, float p_189692_7_, boolean p_189692_8_, boolean p_189692_9_) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef(p_189692_2_, p_189692_3_, p_189692_4_);
      GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-p_189692_6_, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef((float)(p_189692_8_ ? -1 : 1) * p_189692_7_, 1.0F, 0.0F, 0.0F);
      GlStateManager.scalef(-0.025F, -0.025F, 0.025F);
      GlStateManager.disableLighting();
      GlStateManager.depthMask(false);
      if (!p_189692_9_) {
         GlStateManager.disableDepthTest();
      }

      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      int i = p_189692_0_.getStringWidth(p_189692_1_) / 2;
      GlStateManager.disableTexture2D();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos((double)(-i - 1), (double)(-1 + p_189692_5_), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      bufferbuilder.pos((double)(-i - 1), (double)(8 + p_189692_5_), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      bufferbuilder.pos((double)(i + 1), (double)(8 + p_189692_5_), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      bufferbuilder.pos((double)(i + 1), (double)(-1 + p_189692_5_), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      if (!p_189692_9_) {
         p_189692_0_.drawString(p_189692_1_, (float)(-p_189692_0_.getStringWidth(p_189692_1_) / 2), (float)p_189692_5_, 553648127);
         GlStateManager.enableDepthTest();
      }

      GlStateManager.depthMask(true);
      p_189692_0_.drawString(p_189692_1_, (float)(-p_189692_0_.getStringWidth(p_189692_1_) / 2), (float)p_189692_5_, p_189692_9_ ? 553648127 : -1);
      GlStateManager.enableLighting();
      GlStateManager.disableBlend();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   public void func_190565_a(ItemStack p_190565_1_) {
      this.field_190566_ab = p_190565_1_;
      this.field_190567_ac = 40;
      this.field_190568_ad = this.field_78537_ab.nextFloat() * 2.0F - 1.0F;
      this.field_190569_ae = this.field_78537_ab.nextFloat() * 2.0F - 1.0F;
   }

   private void func_190563_a(int p_190563_1_, int p_190563_2_, float p_190563_3_) {
      if (this.field_190566_ab != null && this.field_190567_ac > 0) {
         int i = 40 - this.field_190567_ac;
         float f = ((float)i + p_190563_3_) / 40.0F;
         float f1 = f * f;
         float f2 = f * f1;
         float f3 = 10.25F * f2 * f1 - 24.95F * f1 * f1 + 25.5F * f2 - 13.8F * f1 + 4.0F * f;
         float f4 = f3 * (float)Math.PI;
         float f5 = this.field_190568_ad * (float)(p_190563_1_ / 4);
         float f6 = this.field_190569_ae * (float)(p_190563_2_ / 4);
         GlStateManager.enableAlphaTest();
         GlStateManager.pushMatrix();
         GlStateManager.pushLightingAttrib();
         GlStateManager.enableDepthTest();
         GlStateManager.disableCull();
         RenderHelper.enableStandardItemLighting();
         GlStateManager.translatef((float)(p_190563_1_ / 2) + f5 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)), (float)(p_190563_2_ / 2) + f6 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)), -50.0F);
         float f7 = 50.0F + 175.0F * MathHelper.sin(f4);
         GlStateManager.scalef(f7, -f7, f7);
         GlStateManager.rotatef(900.0F * MathHelper.abs(MathHelper.sin(f4)), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(6.0F * MathHelper.cos(f * 8.0F), 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(6.0F * MathHelper.cos(f * 8.0F), 0.0F, 0.0F, 1.0F);
         this.field_78531_r.getItemRenderer().func_181564_a(this.field_190566_ab, ItemCameraTransforms.TransformType.FIXED);
         GlStateManager.popAttrib();
         GlStateManager.popMatrix();
         RenderHelper.disableStandardItemLighting();
         GlStateManager.enableCull();
         GlStateManager.disableDepthTest();
      }
   }

   public Minecraft func_205000_l() {
      return this.field_78531_r;
   }

   public float func_205002_d(float p_205002_1_) {
      return this.field_82832_V + (this.field_82831_U - this.field_82832_V) * p_205002_1_;
   }

   public float func_205001_m() {
      return this.field_78530_s;
   }
}
