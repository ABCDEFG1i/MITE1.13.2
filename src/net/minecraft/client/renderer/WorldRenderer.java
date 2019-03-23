package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAbstractSkull;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSign;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBoneMeal;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldRenderer implements IWorldEventListener, AutoCloseable, IResourceManagerReloadListener {
   private static final Logger field_147599_m = LogManager.getLogger();
   private static final ResourceLocation field_110927_h = new ResourceLocation("textures/environment/moon_phases.png");
   private static final ResourceLocation field_110928_i = new ResourceLocation("textures/environment/sun.png");
   private static final ResourceLocation field_110925_j = new ResourceLocation("textures/environment/clouds.png");
   private static final ResourceLocation field_110926_k = new ResourceLocation("textures/environment/end_sky.png");
   private static final ResourceLocation field_175006_g = new ResourceLocation("textures/misc/forcefield.png");
   public static final EnumFacing[] field_200006_a = EnumFacing.values();
   private final Minecraft field_72777_q;
   private final TextureManager field_72770_i;
   private final RenderManager field_175010_j;
   private WorldClient field_72769_h;
   private Set<RenderChunk> field_175009_l = Sets.newLinkedHashSet();
   private List<WorldRenderer.ContainerLocalRenderInformation> field_72755_R = Lists.newArrayListWithCapacity(69696);
   private final Set<TileEntity> field_181024_n = Sets.newHashSet();
   private ViewFrustum field_175008_n;
   private int field_72772_v = -1;
   private int field_72771_w = -1;
   private int field_72781_x = -1;
   private final VertexFormat field_175014_r;
   private VertexBuffer field_175013_s;
   private VertexBuffer field_175012_t;
   private VertexBuffer field_175011_u;
   private final int field_204606_x = 28;
   private boolean field_204607_y = true;
   private int field_204608_z = -1;
   private VertexBuffer field_204601_A;
   private int field_72773_u;
   private final Map<Integer, DestroyBlockProgress> field_72738_E = Maps.newHashMap();
   private final Map<BlockPos, ISound> field_147593_P = Maps.newHashMap();
   private final TextureAtlasSprite[] field_94141_F = new TextureAtlasSprite[10];
   private Framebuffer field_175015_z;
   private ShaderGroup field_174991_A;
   private double field_174992_B = Double.MIN_VALUE;
   private double field_174993_C = Double.MIN_VALUE;
   private double field_174987_D = Double.MIN_VALUE;
   private int field_174988_E = Integer.MIN_VALUE;
   private int field_174989_F = Integer.MIN_VALUE;
   private int field_174990_G = Integer.MIN_VALUE;
   private double field_174997_H = Double.MIN_VALUE;
   private double field_174998_I = Double.MIN_VALUE;
   private double field_174999_J = Double.MIN_VALUE;
   private double field_175000_K = Double.MIN_VALUE;
   private double field_174994_L = Double.MIN_VALUE;
   private int field_204602_S = Integer.MIN_VALUE;
   private int field_204603_T = Integer.MIN_VALUE;
   private int field_204604_U = Integer.MIN_VALUE;
   private Vec3d field_204605_V = Vec3d.ZERO;
   private int field_204800_W = -1;
   private ChunkRenderDispatcher field_174995_M;
   private ChunkRenderContainer field_174996_N;
   private int field_72739_F = -1;
   private int field_72740_G = 2;
   private int field_72748_H;
   private int field_72749_I;
   private int field_72750_J;
   private boolean field_175002_T;
   private ClippingHelper field_175001_U;
   private final Vector4f[] field_175004_V = new Vector4f[8];
   private final Vector3d field_175003_W = new Vector3d();
   private boolean field_175005_X;
   private IRenderChunkFactory field_175007_a;
   private double field_147596_f;
   private double field_147597_g;
   private double field_147602_h;
   private boolean field_147595_R = true;
   private boolean field_184386_ad;
   private final Set<BlockPos> field_184387_ae = Sets.newHashSet();

   public WorldRenderer(Minecraft p_i1249_1_) {
      this.field_72777_q = p_i1249_1_;
      this.field_175010_j = p_i1249_1_.getRenderManager();
      this.field_72770_i = p_i1249_1_.getTextureManager();
      this.field_72770_i.bindTexture(field_175006_g);
      GlStateManager.texParameteri(3553, 10242, 10497);
      GlStateManager.texParameteri(3553, 10243, 10497);
      GlStateManager.bindTexture(0);
      this.func_174971_n();
      this.field_175005_X = OpenGlHelper.useVbo();
      if (this.field_175005_X) {
         this.field_174996_N = new VboRenderList();
         this.field_175007_a = RenderChunk::new;
      } else {
         this.field_174996_N = new RenderList();
         this.field_175007_a = ListedRenderChunk::new;
      }

      this.field_175014_r = new VertexFormat();
      this.field_175014_r.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
      this.func_174963_q();
      this.func_174980_p();
      this.func_174964_o();
   }

   public void close() {
      if (this.field_174991_A != null) {
         this.field_174991_A.close();
      }

   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      this.func_174971_n();
   }

   private void func_174971_n() {
      TextureMap texturemap = this.field_72777_q.getTextureMap();
      this.field_94141_F[0] = texturemap.getSprite(ModelBakery.field_207770_h);
      this.field_94141_F[1] = texturemap.getSprite(ModelBakery.field_207771_i);
      this.field_94141_F[2] = texturemap.getSprite(ModelBakery.field_207772_j);
      this.field_94141_F[3] = texturemap.getSprite(ModelBakery.field_207773_k);
      this.field_94141_F[4] = texturemap.getSprite(ModelBakery.field_207774_l);
      this.field_94141_F[5] = texturemap.getSprite(ModelBakery.field_207775_m);
      this.field_94141_F[6] = texturemap.getSprite(ModelBakery.field_207776_n);
      this.field_94141_F[7] = texturemap.getSprite(ModelBakery.field_207777_o);
      this.field_94141_F[8] = texturemap.getSprite(ModelBakery.field_207778_p);
      this.field_94141_F[9] = texturemap.getSprite(ModelBakery.field_207779_q);
   }

   public void func_174966_b() {
      if (OpenGlHelper.shadersSupported) {
         if (ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
            ShaderLinkHelper.setNewStaticShaderLinkHelper();
         }

         ResourceLocation resourcelocation = new ResourceLocation("shaders/post/entity_outline.json");

         try {
            this.field_174991_A = new ShaderGroup(this.field_72777_q.getTextureManager(), this.field_72777_q.getResourceManager(), this.field_72777_q.getFramebuffer(), resourcelocation);
            this.field_174991_A.createBindFramebuffers(this.field_72777_q.mainWindow.getFramebufferWidth(), this.field_72777_q.mainWindow.getFramebufferHeight());
            this.field_175015_z = this.field_174991_A.getFramebufferRaw("final");
         } catch (IOException ioexception) {
            field_147599_m.warn("Failed to load shader: {}", resourcelocation, ioexception);
            this.field_174991_A = null;
            this.field_175015_z = null;
         } catch (JsonSyntaxException jsonsyntaxexception) {
            field_147599_m.warn("Failed to load shader: {}", resourcelocation, jsonsyntaxexception);
            this.field_174991_A = null;
            this.field_175015_z = null;
         }
      } else {
         this.field_174991_A = null;
         this.field_175015_z = null;
      }

   }

   public void func_174975_c() {
      if (this.func_174985_d()) {
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         this.field_175015_z.framebufferRenderExt(this.field_72777_q.mainWindow.getFramebufferWidth(), this.field_72777_q.mainWindow.getFramebufferHeight(), false);
         GlStateManager.disableBlend();
      }

   }

   protected boolean func_174985_d() {
      return this.field_175015_z != null && this.field_174991_A != null && this.field_72777_q.player != null;
   }

   private void func_174964_o() {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      if (this.field_175011_u != null) {
         this.field_175011_u.deleteGlBuffers();
      }

      if (this.field_72781_x >= 0) {
         GLAllocation.deleteDisplayLists(this.field_72781_x);
         this.field_72781_x = -1;
      }

      if (this.field_175005_X) {
         this.field_175011_u = new VertexBuffer(this.field_175014_r);
         this.func_174968_a(bufferbuilder, -16.0F, true);
         bufferbuilder.finishDrawing();
         bufferbuilder.reset();
         this.field_175011_u.bufferData(bufferbuilder.getByteBuffer());
      } else {
         this.field_72781_x = GLAllocation.generateDisplayLists(1);
         GlStateManager.newList(this.field_72781_x, 4864);
         this.func_174968_a(bufferbuilder, -16.0F, true);
         tessellator.draw();
         GlStateManager.endList();
      }

   }

   private void func_174980_p() {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      if (this.field_175012_t != null) {
         this.field_175012_t.deleteGlBuffers();
      }

      if (this.field_72771_w >= 0) {
         GLAllocation.deleteDisplayLists(this.field_72771_w);
         this.field_72771_w = -1;
      }

      if (this.field_175005_X) {
         this.field_175012_t = new VertexBuffer(this.field_175014_r);
         this.func_174968_a(bufferbuilder, 16.0F, false);
         bufferbuilder.finishDrawing();
         bufferbuilder.reset();
         this.field_175012_t.bufferData(bufferbuilder.getByteBuffer());
      } else {
         this.field_72771_w = GLAllocation.generateDisplayLists(1);
         GlStateManager.newList(this.field_72771_w, 4864);
         this.func_174968_a(bufferbuilder, 16.0F, false);
         tessellator.draw();
         GlStateManager.endList();
      }

   }

   private void func_174968_a(BufferBuilder p_174968_1_, float p_174968_2_, boolean p_174968_3_) {
      int i = 64;
      int j = 6;
      p_174968_1_.begin(7, DefaultVertexFormats.POSITION);

      for(int k = -384; k <= 384; k += 64) {
         for(int l = -384; l <= 384; l += 64) {
            float f = (float)k;
            float f1 = (float)(k + 64);
            if (p_174968_3_) {
               f1 = (float)k;
               f = (float)(k + 64);
            }

            p_174968_1_.pos((double)f, (double)p_174968_2_, (double)l).endVertex();
            p_174968_1_.pos((double)f1, (double)p_174968_2_, (double)l).endVertex();
            p_174968_1_.pos((double)f1, (double)p_174968_2_, (double)(l + 64)).endVertex();
            p_174968_1_.pos((double)f, (double)p_174968_2_, (double)(l + 64)).endVertex();
         }
      }

   }

   private void func_174963_q() {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      if (this.field_175013_s != null) {
         this.field_175013_s.deleteGlBuffers();
      }

      if (this.field_72772_v >= 0) {
         GLAllocation.deleteDisplayLists(this.field_72772_v);
         this.field_72772_v = -1;
      }

      if (this.field_175005_X) {
         this.field_175013_s = new VertexBuffer(this.field_175014_r);
         this.func_180444_a(bufferbuilder);
         bufferbuilder.finishDrawing();
         bufferbuilder.reset();
         this.field_175013_s.bufferData(bufferbuilder.getByteBuffer());
      } else {
         this.field_72772_v = GLAllocation.generateDisplayLists(1);
         GlStateManager.pushMatrix();
         GlStateManager.newList(this.field_72772_v, 4864);
         this.func_180444_a(bufferbuilder);
         tessellator.draw();
         GlStateManager.endList();
         GlStateManager.popMatrix();
      }

   }

   private void func_180444_a(BufferBuilder p_180444_1_) {
      Random random = new Random(10842L);
      p_180444_1_.begin(7, DefaultVertexFormats.POSITION);

      for(int i = 0; i < 1500; ++i) {
         double d0 = (double)(random.nextFloat() * 2.0F - 1.0F);
         double d1 = (double)(random.nextFloat() * 2.0F - 1.0F);
         double d2 = (double)(random.nextFloat() * 2.0F - 1.0F);
         double d3 = (double)(0.15F + random.nextFloat() * 0.1F);
         double d4 = d0 * d0 + d1 * d1 + d2 * d2;
         if (d4 < 1.0D && d4 > 0.01D) {
            d4 = 1.0D / Math.sqrt(d4);
            d0 = d0 * d4;
            d1 = d1 * d4;
            d2 = d2 * d4;
            double d5 = d0 * 100.0D;
            double d6 = d1 * 100.0D;
            double d7 = d2 * 100.0D;
            double d8 = Math.atan2(d0, d2);
            double d9 = Math.sin(d8);
            double d10 = Math.cos(d8);
            double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
            double d12 = Math.sin(d11);
            double d13 = Math.cos(d11);
            double d14 = random.nextDouble() * Math.PI * 2.0D;
            double d15 = Math.sin(d14);
            double d16 = Math.cos(d14);

            for(int j = 0; j < 4; ++j) {
               double d17 = 0.0D;
               double d18 = (double)((j & 2) - 1) * d3;
               double d19 = (double)((j + 1 & 2) - 1) * d3;
               double d20 = 0.0D;
               double d21 = d18 * d16 - d19 * d15;
               double d22 = d19 * d16 + d18 * d15;
               double d23 = d21 * d12 + 0.0D * d13;
               double d24 = 0.0D * d12 - d21 * d13;
               double d25 = d24 * d9 - d22 * d10;
               double d26 = d22 * d9 + d24 * d10;
               p_180444_1_.pos(d5 + d25, d6 + d23, d7 + d26).endVertex();
            }
         }
      }

   }

   public void func_72732_a(@Nullable WorldClient p_72732_1_) {
      if (this.field_72769_h != null) {
         this.field_72769_h.removeEventListener(this);
      }

      this.field_174992_B = Double.MIN_VALUE;
      this.field_174993_C = Double.MIN_VALUE;
      this.field_174987_D = Double.MIN_VALUE;
      this.field_174988_E = Integer.MIN_VALUE;
      this.field_174989_F = Integer.MIN_VALUE;
      this.field_174990_G = Integer.MIN_VALUE;
      this.field_175010_j.setWorld(p_72732_1_);
      this.field_72769_h = p_72732_1_;
      if (p_72732_1_ != null) {
         p_72732_1_.addEventListener(this);
         this.func_72712_a();
      } else {
         this.field_175009_l.clear();
         this.field_72755_R.clear();
         if (this.field_175008_n != null) {
            this.field_175008_n.deleteGlResources();
            this.field_175008_n = null;
         }

         if (this.field_174995_M != null) {
            this.field_174995_M.stopWorkerThreads();
         }

         this.field_174995_M = null;
      }

   }

   public void func_72712_a() {
      if (this.field_72769_h != null) {
         if (this.field_174995_M == null) {
            this.field_174995_M = new ChunkRenderDispatcher();
         }

         this.field_147595_R = true;
         this.field_204607_y = true;
         BlockLeaves.setRenderTranslucent(this.field_72777_q.gameSettings.fancyGraphics);
         this.field_72739_F = this.field_72777_q.gameSettings.renderDistanceChunks;
         boolean flag = this.field_175005_X;
         this.field_175005_X = OpenGlHelper.useVbo();
         if (flag && !this.field_175005_X) {
            this.field_174996_N = new RenderList();
            this.field_175007_a = ListedRenderChunk::new;
         } else if (!flag && this.field_175005_X) {
            this.field_174996_N = new VboRenderList();
            this.field_175007_a = RenderChunk::new;
         }

         if (flag != this.field_175005_X) {
            this.func_174963_q();
            this.func_174980_p();
            this.func_174964_o();
         }

         if (this.field_175008_n != null) {
            this.field_175008_n.deleteGlResources();
         }

         this.func_174986_e();
         synchronized(this.field_181024_n) {
            this.field_181024_n.clear();
         }

         this.field_175008_n = new ViewFrustum(this.field_72769_h, this.field_72777_q.gameSettings.renderDistanceChunks, this, this.field_175007_a);
         if (this.field_72769_h != null) {
            Entity entity = this.field_72777_q.getRenderViewEntity();
            if (entity != null) {
               this.field_175008_n.updateChunkPositions(entity.posX, entity.posZ);
            }
         }

         this.field_72740_G = 2;
      }
   }

   protected void func_174986_e() {
      this.field_175009_l.clear();
      this.field_174995_M.stopChunkUpdates();
   }

   public void func_72720_a(int p_72720_1_, int p_72720_2_) {
      this.func_174979_m();
      if (OpenGlHelper.shadersSupported) {
         if (this.field_174991_A != null) {
            this.field_174991_A.createBindFramebuffers(p_72720_1_, p_72720_2_);
         }

      }
   }

   public void func_180446_a(Entity p_180446_1_, ICamera p_180446_2_, float p_180446_3_) {
      if (this.field_72740_G > 0) {
         --this.field_72740_G;
      } else {
         double d0 = p_180446_1_.prevPosX + (p_180446_1_.posX - p_180446_1_.prevPosX) * (double)p_180446_3_;
         double d1 = p_180446_1_.prevPosY + (p_180446_1_.posY - p_180446_1_.prevPosY) * (double)p_180446_3_;
         double d2 = p_180446_1_.prevPosZ + (p_180446_1_.posZ - p_180446_1_.prevPosZ) * (double)p_180446_3_;
         this.field_72769_h.profiler.startSection("prepare");
         TileEntityRendererDispatcher.instance.prepare(this.field_72769_h, this.field_72777_q.getTextureManager(), this.field_72777_q.fontRenderer, this.field_72777_q.getRenderViewEntity(), this.field_72777_q.objectMouseOver, p_180446_3_);
         this.field_175010_j.cacheActiveRenderInfo(this.field_72769_h, this.field_72777_q.fontRenderer, this.field_72777_q.getRenderViewEntity(), this.field_72777_q.pointedEntity, this.field_72777_q.gameSettings, p_180446_3_);
         this.field_72748_H = 0;
         this.field_72749_I = 0;
         this.field_72750_J = 0;
         Entity entity = this.field_72777_q.getRenderViewEntity();
         double d3 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)p_180446_3_;
         double d4 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)p_180446_3_;
         double d5 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)p_180446_3_;
         TileEntityRendererDispatcher.staticPlayerX = d3;
         TileEntityRendererDispatcher.staticPlayerY = d4;
         TileEntityRendererDispatcher.staticPlayerZ = d5;
         this.field_175010_j.setRenderPosition(d3, d4, d5);
         this.field_72777_q.entityRenderer.func_180436_i();
         this.field_72769_h.profiler.endStartSection("global");
         this.field_72748_H = this.field_72769_h.func_212419_R();

         for(int i = 0; i < this.field_72769_h.weatherEffects.size(); ++i) {
            Entity entity1 = this.field_72769_h.weatherEffects.get(i);
            ++this.field_72749_I;
            if (entity1.isInRangeToRender3d(d0, d1, d2)) {
               this.field_175010_j.renderEntityStatic(entity1, p_180446_3_, false);
            }
         }

         this.field_72769_h.profiler.endStartSection("entities");
         List<Entity> list = Lists.newArrayList();
         List<Entity> list1 = Lists.newArrayList();

         try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
            for(WorldRenderer.ContainerLocalRenderInformation worldrenderer$containerlocalrenderinformation : this.field_72755_R) {
               Chunk chunk = this.field_72769_h.getChunk(worldrenderer$containerlocalrenderinformation.field_178036_a.getPosition());
               ClassInheritanceMultiMap<Entity> classinheritancemultimap = chunk.getEntityLists()[worldrenderer$containerlocalrenderinformation.field_178036_a.getPosition().getY() / 16];
               if (!classinheritancemultimap.isEmpty()) {
                  for(Entity entity2 : classinheritancemultimap) {
                     boolean flag = this.field_175010_j.shouldRender(entity2, p_180446_2_, d0, d1, d2) || entity2.isRidingOrBeingRiddenBy(this.field_72777_q.player);
                     if (flag) {
                        boolean flag1 = this.field_72777_q.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)this.field_72777_q.getRenderViewEntity()).isPlayerSleeping();
                        if ((entity2 != this.field_72777_q.getRenderViewEntity() || this.field_72777_q.gameSettings.thirdPersonView != 0 || flag1) && (!(entity2.posY >= 0.0D) || !(entity2.posY < 256.0D) || this.field_72769_h.isBlockLoaded(blockpos$pooledmutableblockpos.setPos(entity2)))) {
                           ++this.field_72749_I;
                           this.field_175010_j.renderEntityStatic(entity2, p_180446_3_, false);
                           if (this.func_184383_a(entity2, entity, p_180446_2_)) {
                              list.add(entity2);
                           }

                           if (this.field_175010_j.isRenderMultipass(entity2)) {
                              list1.add(entity2);
                           }
                        }
                     }
                  }
               }
            }
         }

         if (!list1.isEmpty()) {
            for(Entity entity3 : list1) {
               this.field_175010_j.renderMultipass(entity3, p_180446_3_);
            }
         }

         if (this.func_174985_d() && (!list.isEmpty() || this.field_184386_ad)) {
            this.field_72769_h.profiler.endStartSection("entityOutlines");
            this.field_175015_z.framebufferClear();
            this.field_184386_ad = !list.isEmpty();
            if (!list.isEmpty()) {
               GlStateManager.depthFunc(519);
               GlStateManager.disableFog();
               this.field_175015_z.bindFramebuffer(false);
               RenderHelper.disableStandardItemLighting();
               this.field_175010_j.setRenderOutlines(true);

               for(int j = 0; j < list.size(); ++j) {
                  this.field_175010_j.renderEntityStatic(list.get(j), p_180446_3_, false);
               }

               this.field_175010_j.setRenderOutlines(false);
               RenderHelper.enableStandardItemLighting();
               GlStateManager.depthMask(false);
               this.field_174991_A.render(p_180446_3_);
               GlStateManager.enableLighting();
               GlStateManager.depthMask(true);
               GlStateManager.enableFog();
               GlStateManager.enableBlend();
               GlStateManager.enableColorMaterial();
               GlStateManager.depthFunc(515);
               GlStateManager.enableDepthTest();
               GlStateManager.enableAlphaTest();
            }

            this.field_72777_q.getFramebuffer().bindFramebuffer(false);
         }

         this.field_72769_h.profiler.endStartSection("blockentities");
         RenderHelper.enableStandardItemLighting();

         for(WorldRenderer.ContainerLocalRenderInformation worldrenderer$containerlocalrenderinformation1 : this.field_72755_R) {
            List<TileEntity> list2 = worldrenderer$containerlocalrenderinformation1.field_178036_a.getCompiledChunk().getTileEntities();
            if (!list2.isEmpty()) {
               for(TileEntity tileentity1 : list2) {
                  TileEntityRendererDispatcher.instance.render(tileentity1, p_180446_3_, -1);
               }
            }
         }

         synchronized(this.field_181024_n) {
            for(TileEntity tileentity : this.field_181024_n) {
               TileEntityRendererDispatcher.instance.render(tileentity, p_180446_3_, -1);
            }
         }

         this.func_180443_s();

         for(DestroyBlockProgress destroyblockprogress : this.field_72738_E.values()) {
            BlockPos blockpos = destroyblockprogress.getPosition();
            IBlockState iblockstate = this.field_72769_h.getBlockState(blockpos);
            if (iblockstate.getBlock().hasTileEntity()) {
               TileEntity tileentity2 = this.field_72769_h.getTileEntity(blockpos);
               if (tileentity2 instanceof TileEntityChest && iblockstate.get(BlockChest.TYPE) == ChestType.LEFT) {
                  blockpos = blockpos.offset(iblockstate.get(BlockChest.FACING).rotateY());
                  tileentity2 = this.field_72769_h.getTileEntity(blockpos);
               }

               if (tileentity2 != null && iblockstate.hasCustomBreakingProgress()) {
                  TileEntityRendererDispatcher.instance.render(tileentity2, p_180446_3_, destroyblockprogress.getPartialBlockDamage());
               }
            }
         }

         this.func_174969_t();
         this.field_72777_q.entityRenderer.func_175072_h();
         this.field_72777_q.profiler.endSection();
      }
   }

   private boolean func_184383_a(Entity p_184383_1_, Entity p_184383_2_, ICamera p_184383_3_) {
      boolean flag = p_184383_2_ instanceof EntityLivingBase && ((EntityLivingBase)p_184383_2_).isPlayerSleeping();
      if (p_184383_1_ == p_184383_2_ && this.field_72777_q.gameSettings.thirdPersonView == 0 && !flag) {
         return false;
      } else if (p_184383_1_.isGlowing()) {
         return true;
      } else if (this.field_72777_q.player.isSpectator() && this.field_72777_q.gameSettings.keyBindSpectatorOutlines.isKeyDown() && p_184383_1_ instanceof EntityPlayer) {
         return p_184383_1_.ignoreFrustumCheck || p_184383_3_.isBoundingBoxInFrustum(p_184383_1_.getEntityBoundingBox()) || p_184383_1_.isRidingOrBeingRiddenBy(this.field_72777_q.player);
      } else {
         return false;
      }
   }

   public String func_72735_c() {
      int i = this.field_175008_n.renderChunks.length;
      int j = this.func_184382_g();
      return String.format("C: %d/%d %sD: %d, L: %d, %s", j, i, this.field_72777_q.renderChunksMany ? "(s) " : "", this.field_72739_F, this.field_184387_ae.size(), this.field_174995_M == null ? "null" : this.field_174995_M.getDebugInfo());
   }

   protected int func_184382_g() {
      int i = 0;

      for(WorldRenderer.ContainerLocalRenderInformation worldrenderer$containerlocalrenderinformation : this.field_72755_R) {
         CompiledChunk compiledchunk = worldrenderer$containerlocalrenderinformation.field_178036_a.compiledChunk;
         if (compiledchunk != CompiledChunk.DUMMY && !compiledchunk.isEmpty()) {
            ++i;
         }
      }

      return i;
   }

   public String func_72723_d() {
      return "E: " + this.field_72749_I + "/" + this.field_72748_H + ", B: " + this.field_72750_J;
   }

   public void func_195473_a(Entity p_195473_1_, float p_195473_2_, ICamera p_195473_3_, int p_195473_4_, boolean p_195473_5_) {
      if (this.field_72777_q.gameSettings.renderDistanceChunks != this.field_72739_F) {
         this.func_72712_a();
      }

      this.field_72769_h.profiler.startSection("camera");
      double d0 = p_195473_1_.posX - this.field_174992_B;
      double d1 = p_195473_1_.posY - this.field_174993_C;
      double d2 = p_195473_1_.posZ - this.field_174987_D;
      if (this.field_174988_E != p_195473_1_.chunkCoordX || this.field_174989_F != p_195473_1_.chunkCoordY || this.field_174990_G != p_195473_1_.chunkCoordZ || d0 * d0 + d1 * d1 + d2 * d2 > 16.0D) {
         this.field_174992_B = p_195473_1_.posX;
         this.field_174993_C = p_195473_1_.posY;
         this.field_174987_D = p_195473_1_.posZ;
         this.field_174988_E = p_195473_1_.chunkCoordX;
         this.field_174989_F = p_195473_1_.chunkCoordY;
         this.field_174990_G = p_195473_1_.chunkCoordZ;
         this.field_175008_n.updateChunkPositions(p_195473_1_.posX, p_195473_1_.posZ);
      }

      this.field_72769_h.profiler.endStartSection("renderlistcamera");
      double d3 = p_195473_1_.lastTickPosX + (p_195473_1_.posX - p_195473_1_.lastTickPosX) * (double)p_195473_2_;
      double d4 = p_195473_1_.lastTickPosY + (p_195473_1_.posY - p_195473_1_.lastTickPosY) * (double)p_195473_2_;
      double d5 = p_195473_1_.lastTickPosZ + (p_195473_1_.posZ - p_195473_1_.lastTickPosZ) * (double)p_195473_2_;
      this.field_174996_N.initialize(d3, d4, d5);
      this.field_72769_h.profiler.endStartSection("cull");
      if (this.field_175001_U != null) {
         Frustum frustum = new Frustum(this.field_175001_U);
         frustum.setPosition(this.field_175003_W.x, this.field_175003_W.y, this.field_175003_W.z);
         p_195473_3_ = frustum;
      }

      this.field_72777_q.profiler.endStartSection("culling");
      BlockPos blockpos1 = new BlockPos(d3, d4 + (double)p_195473_1_.getEyeHeight(), d5);
      RenderChunk renderchunk = this.field_175008_n.getRenderChunk(blockpos1);
      BlockPos blockpos = new BlockPos(MathHelper.floor(d3 / 16.0D) * 16, MathHelper.floor(d4 / 16.0D) * 16, MathHelper.floor(d5 / 16.0D) * 16);
      float f = p_195473_1_.getPitch(p_195473_2_);
      float f1 = p_195473_1_.getYaw(p_195473_2_);
      this.field_147595_R = this.field_147595_R || !this.field_175009_l.isEmpty() || p_195473_1_.posX != this.field_174997_H || p_195473_1_.posY != this.field_174998_I || p_195473_1_.posZ != this.field_174999_J || (double)f != this.field_175000_K || (double)f1 != this.field_174994_L;
      this.field_174997_H = p_195473_1_.posX;
      this.field_174998_I = p_195473_1_.posY;
      this.field_174999_J = p_195473_1_.posZ;
      this.field_175000_K = (double)f;
      this.field_174994_L = (double)f1;
      boolean flag = this.field_175001_U != null;
      this.field_72777_q.profiler.endStartSection("update");
      if (!flag && this.field_147595_R) {
         this.field_147595_R = false;
         this.field_72755_R = Lists.newArrayList();
         Queue<WorldRenderer.ContainerLocalRenderInformation> queue = Queues.newArrayDeque();
         Entity.setRenderDistanceWeight(MathHelper.clamp((double)this.field_72777_q.gameSettings.renderDistanceChunks / 8.0D, 1.0D, 2.5D));
         boolean flag1 = this.field_72777_q.renderChunksMany;
         if (renderchunk != null) {
            boolean flag2 = false;
            WorldRenderer.ContainerLocalRenderInformation worldrenderer$containerlocalrenderinformation3 = new WorldRenderer.ContainerLocalRenderInformation(renderchunk, (EnumFacing)null, 0);
            Set<EnumFacing> set1 = this.func_174978_c(blockpos1);
            if (set1.size() == 1) {
               Vector3f vector3f = this.func_195474_a(p_195473_1_, (double)p_195473_2_);
               EnumFacing enumfacing = EnumFacing.getFacingFromVector(vector3f.getX(), vector3f.getY(), vector3f.getZ()).getOpposite();
               set1.remove(enumfacing);
            }

            if (set1.isEmpty()) {
               flag2 = true;
            }

            if (flag2 && !p_195473_5_) {
               this.field_72755_R.add(worldrenderer$containerlocalrenderinformation3);
            } else {
               if (p_195473_5_ && this.field_72769_h.getBlockState(blockpos1).isOpaqueCube(this.field_72769_h, blockpos1)) {
                  flag1 = false;
               }

               renderchunk.setFrameIndex(p_195473_4_);
               queue.add(worldrenderer$containerlocalrenderinformation3);
            }
         } else {
            int i = blockpos1.getY() > 0 ? 248 : 8;

            for(int j = -this.field_72739_F; j <= this.field_72739_F; ++j) {
               for(int k = -this.field_72739_F; k <= this.field_72739_F; ++k) {
                  RenderChunk renderchunk1 = this.field_175008_n.getRenderChunk(new BlockPos((j << 4) + 8, i, (k << 4) + 8));
                  if (renderchunk1 != null && p_195473_3_.isBoundingBoxInFrustum(renderchunk1.boundingBox)) {
                     renderchunk1.setFrameIndex(p_195473_4_);
                     queue.add(new WorldRenderer.ContainerLocalRenderInformation(renderchunk1, (EnumFacing)null, 0));
                  }
               }
            }
         }

         this.field_72777_q.profiler.startSection("iteration");

         while(!queue.isEmpty()) {
            WorldRenderer.ContainerLocalRenderInformation worldrenderer$containerlocalrenderinformation1 = queue.poll();
            RenderChunk renderchunk3 = worldrenderer$containerlocalrenderinformation1.field_178036_a;
            EnumFacing enumfacing2 = worldrenderer$containerlocalrenderinformation1.field_178034_b;
            this.field_72755_R.add(worldrenderer$containerlocalrenderinformation1);

            for(EnumFacing enumfacing1 : field_200006_a) {
               RenderChunk renderchunk2 = this.func_181562_a(blockpos, renderchunk3, enumfacing1);
               if ((!flag1 || !worldrenderer$containerlocalrenderinformation1.func_189560_a(enumfacing1.getOpposite())) && (!flag1 || enumfacing2 == null || renderchunk3.getCompiledChunk().isVisible(enumfacing2.getOpposite(), enumfacing1)) && renderchunk2 != null && renderchunk2.setFrameIndex(p_195473_4_) && p_195473_3_.isBoundingBoxInFrustum(renderchunk2.boundingBox)) {
                  WorldRenderer.ContainerLocalRenderInformation worldrenderer$containerlocalrenderinformation = new WorldRenderer.ContainerLocalRenderInformation(renderchunk2, enumfacing1, worldrenderer$containerlocalrenderinformation1.field_178032_d + 1);
                  worldrenderer$containerlocalrenderinformation.func_189561_a(worldrenderer$containerlocalrenderinformation1.field_178035_c, enumfacing1);
                  queue.add(worldrenderer$containerlocalrenderinformation);
               }
            }
         }

         this.field_72777_q.profiler.endSection();
      }

      this.field_72777_q.profiler.endStartSection("captureFrustum");
      if (this.field_175002_T) {
         this.func_174984_a(d3, d4, d5);
         this.field_175002_T = false;
      }

      this.field_72777_q.profiler.endStartSection("rebuildNear");
      Set<RenderChunk> set = this.field_175009_l;
      this.field_175009_l = Sets.newLinkedHashSet();

      for(WorldRenderer.ContainerLocalRenderInformation worldrenderer$containerlocalrenderinformation2 : this.field_72755_R) {
         RenderChunk renderchunk4 = worldrenderer$containerlocalrenderinformation2.field_178036_a;
         if (renderchunk4.needsUpdate() || set.contains(renderchunk4)) {
            this.field_147595_R = true;
            BlockPos blockpos2 = renderchunk4.getPosition().add(8, 8, 8);
            boolean flag3 = blockpos2.distanceSq(blockpos1) < 768.0D;
            if (!renderchunk4.needsImmediateUpdate() && !flag3) {
               this.field_175009_l.add(renderchunk4);
            } else {
               this.field_72777_q.profiler.startSection("build near");
               this.field_174995_M.updateChunkNow(renderchunk4);
               renderchunk4.clearNeedsUpdate();
               this.field_72777_q.profiler.endSection();
            }
         }
      }

      this.field_175009_l.addAll(set);
      this.field_72777_q.profiler.endSection();
   }

   private Set<EnumFacing> func_174978_c(BlockPos p_174978_1_) {
      VisGraph visgraph = new VisGraph();
      BlockPos blockpos = new BlockPos(p_174978_1_.getX() >> 4 << 4, p_174978_1_.getY() >> 4 << 4, p_174978_1_.getZ() >> 4 << 4);
      Chunk chunk = this.field_72769_h.getChunk(blockpos);

      for(BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(blockpos, blockpos.add(15, 15, 15))) {
         if (chunk.getBlockState(blockpos$mutableblockpos).isOpaqueCube(this.field_72769_h, blockpos$mutableblockpos)) {
            visgraph.setOpaqueCube(blockpos$mutableblockpos);
         }
      }

      return visgraph.getVisibleFacings(p_174978_1_);
   }

   @Nullable
   private RenderChunk func_181562_a(BlockPos p_181562_1_, RenderChunk p_181562_2_, EnumFacing p_181562_3_) {
      BlockPos blockpos = p_181562_2_.getBlockPosOffset16(p_181562_3_);
      if (MathHelper.abs(p_181562_1_.getX() - blockpos.getX()) > this.field_72739_F * 16) {
         return null;
      } else if (blockpos.getY() >= 0 && blockpos.getY() < 256) {
         return MathHelper.abs(p_181562_1_.getZ() - blockpos.getZ()) > this.field_72739_F * 16 ? null : this.field_175008_n.getRenderChunk(blockpos);
      } else {
         return null;
      }
   }

   private void func_174984_a(double p_174984_1_, double p_174984_3_, double p_174984_5_) {
   }

   protected Vector3f func_195474_a(Entity p_195474_1_, double p_195474_2_) {
      float f = (float)((double)p_195474_1_.prevRotationPitch + (double)(p_195474_1_.rotationPitch - p_195474_1_.prevRotationPitch) * p_195474_2_);
      float f1 = (float)((double)p_195474_1_.prevRotationYaw + (double)(p_195474_1_.rotationYaw - p_195474_1_.prevRotationYaw) * p_195474_2_);
      if (Minecraft.getInstance().gameSettings.thirdPersonView == 2) {
         f += 180.0F;
      }

      float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
      float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
      float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
      float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
      return new Vector3f(f3 * f4, f5, f2 * f4);
   }

   public int func_195464_a(BlockRenderLayer p_195464_1_, double p_195464_2_, Entity p_195464_4_) {
      RenderHelper.disableStandardItemLighting();
      if (p_195464_1_ == BlockRenderLayer.TRANSLUCENT) {
         this.field_72777_q.profiler.startSection("translucent_sort");
         double d0 = p_195464_4_.posX - this.field_147596_f;
         double d1 = p_195464_4_.posY - this.field_147597_g;
         double d2 = p_195464_4_.posZ - this.field_147602_h;
         if (d0 * d0 + d1 * d1 + d2 * d2 > 1.0D) {
            this.field_147596_f = p_195464_4_.posX;
            this.field_147597_g = p_195464_4_.posY;
            this.field_147602_h = p_195464_4_.posZ;
            int k = 0;

            for(WorldRenderer.ContainerLocalRenderInformation worldrenderer$containerlocalrenderinformation : this.field_72755_R) {
               if (worldrenderer$containerlocalrenderinformation.field_178036_a.compiledChunk.isLayerStarted(p_195464_1_) && k++ < 15) {
                  this.field_174995_M.updateTransparencyLater(worldrenderer$containerlocalrenderinformation.field_178036_a);
               }
            }
         }

         this.field_72777_q.profiler.endSection();
      }

      this.field_72777_q.profiler.startSection("filterempty");
      int l = 0;
      boolean flag = p_195464_1_ == BlockRenderLayer.TRANSLUCENT;
      int i1 = flag ? this.field_72755_R.size() - 1 : 0;
      int i = flag ? -1 : this.field_72755_R.size();
      int j1 = flag ? -1 : 1;

      for(int j = i1; j != i; j += j1) {
         RenderChunk renderchunk = (this.field_72755_R.get(j)).field_178036_a;
         if (!renderchunk.getCompiledChunk().isLayerEmpty(p_195464_1_)) {
            ++l;
            this.field_174996_N.addRenderChunk(renderchunk, p_195464_1_);
         }
      }

      this.field_72777_q.profiler.endStartSection(() -> {
         return "render_" + p_195464_1_;
      });
      this.func_174982_a(p_195464_1_);
      this.field_72777_q.profiler.endSection();
      return l;
   }

   private void func_174982_a(BlockRenderLayer p_174982_1_) {
      this.field_72777_q.entityRenderer.func_180436_i();
      if (OpenGlHelper.useVbo()) {
         GlStateManager.enableClientState(32884);
         OpenGlHelper.glClientActiveTexture(OpenGlHelper.GL_TEXTURE0);
         GlStateManager.enableClientState(32888);
         OpenGlHelper.glClientActiveTexture(OpenGlHelper.GL_TEXTURE1);
         GlStateManager.enableClientState(32888);
         OpenGlHelper.glClientActiveTexture(OpenGlHelper.GL_TEXTURE0);
         GlStateManager.enableClientState(32886);
      }

      this.field_174996_N.renderChunkLayer(p_174982_1_);
      if (OpenGlHelper.useVbo()) {
         for(VertexFormatElement vertexformatelement : DefaultVertexFormats.BLOCK.getElements()) {
            VertexFormatElement.EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();
            int i = vertexformatelement.getIndex();
            switch(vertexformatelement$enumusage) {
            case POSITION:
               GlStateManager.disableClientState(32884);
               break;
            case UV:
               OpenGlHelper.glClientActiveTexture(OpenGlHelper.GL_TEXTURE0 + i);
               GlStateManager.disableClientState(32888);
               OpenGlHelper.glClientActiveTexture(OpenGlHelper.GL_TEXTURE0);
               break;
            case COLOR:
               GlStateManager.disableClientState(32886);
               GlStateManager.resetColor();
            }
         }
      }

      this.field_72777_q.entityRenderer.func_175072_h();
   }

   private void func_174965_a(Iterator<DestroyBlockProgress> p_174965_1_) {
      while(p_174965_1_.hasNext()) {
         DestroyBlockProgress destroyblockprogress = p_174965_1_.next();
         int i = destroyblockprogress.getCreationCloudUpdateTick();
         if (this.field_72773_u - i > 400) {
            p_174965_1_.remove();
         }
      }

   }

   public void func_72734_e() {
      ++this.field_72773_u;
      if (this.field_72773_u % 20 == 0) {
         this.func_174965_a(this.field_72738_E.values().iterator());
      }

      if (!this.field_184387_ae.isEmpty() && !this.field_174995_M.hasNoFreeRenderBuilders() && this.field_175009_l.isEmpty()) {
         Iterator<BlockPos> iterator = this.field_184387_ae.iterator();

         while(iterator.hasNext()) {
            BlockPos blockpos = iterator.next();
            iterator.remove();
            int i = blockpos.getX();
            int j = blockpos.getY();
            int k = blockpos.getZ();
            this.func_184385_a(i - 1, j - 1, k - 1, i + 1, j + 1, k + 1, false);
         }
      }

   }

   private void func_180448_r() {
      GlStateManager.disableFog();
      GlStateManager.disableAlphaTest();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.depthMask(false);
      this.field_72770_i.bindTexture(field_110926_k);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();

      for(int i = 0; i < 6; ++i) {
         GlStateManager.pushMatrix();
         if (i == 1) {
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         }

         if (i == 2) {
            GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
         }

         if (i == 3) {
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
         }

         if (i == 4) {
            GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
         }

         if (i == 5) {
            GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
         }

         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         bufferbuilder.pos(-100.0D, -100.0D, -100.0D).tex(0.0D, 0.0D).color(40, 40, 40, 255).endVertex();
         bufferbuilder.pos(-100.0D, -100.0D, 100.0D).tex(0.0D, 16.0D).color(40, 40, 40, 255).endVertex();
         bufferbuilder.pos(100.0D, -100.0D, 100.0D).tex(16.0D, 16.0D).color(40, 40, 40, 255).endVertex();
         bufferbuilder.pos(100.0D, -100.0D, -100.0D).tex(16.0D, 0.0D).color(40, 40, 40, 255).endVertex();
         tessellator.draw();
         GlStateManager.popMatrix();
      }

      GlStateManager.depthMask(true);
      GlStateManager.enableTexture2D();
      GlStateManager.enableAlphaTest();
   }

   public void func_195465_a(float p_195465_1_) {
      if (this.field_72777_q.world.dimension.getType() == DimensionType.THE_END) {
         this.func_180448_r();
      } else if (this.field_72777_q.world.dimension.isSurfaceWorld()) {
         GlStateManager.disableTexture2D();
         Vec3d vec3d = this.field_72769_h.getSkyColor(this.field_72777_q.getRenderViewEntity(), p_195465_1_);
         float f = (float)vec3d.x;
         float f1 = (float)vec3d.y;
         float f2 = (float)vec3d.z;
         GlStateManager.color3f(f, f1, f2);
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         GlStateManager.depthMask(false);
         GlStateManager.enableFog();
         GlStateManager.color3f(f, f1, f2);
         if (this.field_175005_X) {
            this.field_175012_t.bindBuffer();
            GlStateManager.enableClientState(32884);
            GlStateManager.vertexPointer(3, 5126, 12, 0);
            this.field_175012_t.drawArrays(7);
            this.field_175012_t.unbindBuffer();
            GlStateManager.disableClientState(32884);
         } else {
            GlStateManager.callList(this.field_72771_w);
         }

         GlStateManager.disableFog();
         GlStateManager.disableAlphaTest();
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         RenderHelper.disableStandardItemLighting();
         float[] afloat = this.field_72769_h.dimension.calcSunriseSunsetColors(this.field_72769_h.getCelestialAngle(p_195465_1_), p_195465_1_);
         if (afloat != null) {
            GlStateManager.disableTexture2D();
            GlStateManager.shadeModel(7425);
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(MathHelper.sin(this.field_72769_h.getCelestialAngleRadians(p_195465_1_)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
            float f3 = afloat[0];
            float f4 = afloat[1];
            float f5 = afloat[2];
            bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.pos(0.0D, 100.0D, 0.0D).color(f3, f4, f5, afloat[3]).endVertex();
            int i = 16;

            for(int j = 0; j <= 16; ++j) {
               float f6 = (float)j * ((float)Math.PI * 2F) / 16.0F;
               float f7 = MathHelper.sin(f6);
               float f8 = MathHelper.cos(f6);
               bufferbuilder.pos((double)(f7 * 120.0F), (double)(f8 * 120.0F), (double)(-f8 * 40.0F * afloat[3])).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
            }

            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.shadeModel(7424);
         }

         GlStateManager.enableTexture2D();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.pushMatrix();
         float f11 = 1.0F - this.field_72769_h.getRainStrength(p_195465_1_);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, f11);
         GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(this.field_72769_h.getCelestialAngle(p_195465_1_) * 360.0F, 1.0F, 0.0F, 0.0F);
         float f12 = 30.0F;
         this.field_72770_i.bindTexture(field_110928_i);
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
         bufferbuilder.pos((double)(-f12), 100.0D, (double)(-f12)).tex(0.0D, 0.0D).endVertex();
         bufferbuilder.pos((double)f12, 100.0D, (double)(-f12)).tex(1.0D, 0.0D).endVertex();
         bufferbuilder.pos((double)f12, 100.0D, (double)f12).tex(1.0D, 1.0D).endVertex();
         bufferbuilder.pos((double)(-f12), 100.0D, (double)f12).tex(0.0D, 1.0D).endVertex();
         tessellator.draw();
         f12 = 20.0F;
         this.field_72770_i.bindTexture(field_110927_h);
         int k = this.field_72769_h.getMoonPhase();
         int l = k % 4;
         int i1 = k / 4 % 2;
         float f13 = (float)(l + 0) / 4.0F;
         float f14 = (float)(i1 + 0) / 2.0F;
         float f15 = (float)(l + 1) / 4.0F;
         float f9 = (float)(i1 + 1) / 2.0F;
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
         bufferbuilder.pos((double)(-f12), -100.0D, (double)f12).tex((double)f15, (double)f9).endVertex();
         bufferbuilder.pos((double)f12, -100.0D, (double)f12).tex((double)f13, (double)f9).endVertex();
         bufferbuilder.pos((double)f12, -100.0D, (double)(-f12)).tex((double)f13, (double)f14).endVertex();
         bufferbuilder.pos((double)(-f12), -100.0D, (double)(-f12)).tex((double)f15, (double)f14).endVertex();
         tessellator.draw();
         GlStateManager.disableTexture2D();
         float f10 = this.field_72769_h.getStarBrightness(p_195465_1_) * f11;
         if (f10 > 0.0F) {
            GlStateManager.color4f(f10, f10, f10, f10);
            if (this.field_175005_X) {
               this.field_175013_s.bindBuffer();
               GlStateManager.enableClientState(32884);
               GlStateManager.vertexPointer(3, 5126, 12, 0);
               this.field_175013_s.drawArrays(7);
               this.field_175013_s.unbindBuffer();
               GlStateManager.disableClientState(32884);
            } else {
               GlStateManager.callList(this.field_72772_v);
            }
         }

         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.enableAlphaTest();
         GlStateManager.enableFog();
         GlStateManager.popMatrix();
         GlStateManager.disableTexture2D();
         GlStateManager.color3f(0.0F, 0.0F, 0.0F);
         double d0 = this.field_72777_q.player.getEyePosition(p_195465_1_).y - this.field_72769_h.getHorizon();
         if (d0 < 0.0D) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0F, 12.0F, 0.0F);
            if (this.field_175005_X) {
               this.field_175011_u.bindBuffer();
               GlStateManager.enableClientState(32884);
               GlStateManager.vertexPointer(3, 5126, 12, 0);
               this.field_175011_u.drawArrays(7);
               this.field_175011_u.unbindBuffer();
               GlStateManager.disableClientState(32884);
            } else {
               GlStateManager.callList(this.field_72781_x);
            }

            GlStateManager.popMatrix();
         }

         if (this.field_72769_h.dimension.isSkyColored()) {
            GlStateManager.color3f(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
         } else {
            GlStateManager.color3f(f, f1, f2);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, -((float)(d0 - 16.0D)), 0.0F);
         GlStateManager.callList(this.field_72781_x);
         GlStateManager.popMatrix();
         GlStateManager.enableTexture2D();
         GlStateManager.depthMask(true);
      }
   }

   public void func_195466_a(float p_195466_1_, double p_195466_2_, double p_195466_4_, double p_195466_6_) {
      if (this.field_72777_q.world.dimension.isSurfaceWorld()) {
         float f = 12.0F;
         float f1 = 4.0F;
         double d0 = 2.0E-4D;
         double d1 = (double)(((float)this.field_72773_u + p_195466_1_) * 0.03F);
         double d2 = (p_195466_2_ + d1) / 12.0D;
         double d3 = (double)(this.field_72769_h.dimension.getCloudHeight() - (float)p_195466_4_ + 0.33F);
         double d4 = p_195466_6_ / 12.0D + (double)0.33F;
         d2 = d2 - (double)(MathHelper.floor(d2 / 2048.0D) * 2048);
         d4 = d4 - (double)(MathHelper.floor(d4 / 2048.0D) * 2048);
         float f2 = (float)(d2 - (double)MathHelper.floor(d2));
         float f3 = (float)(d3 / 4.0D - (double)MathHelper.floor(d3 / 4.0D)) * 4.0F;
         float f4 = (float)(d4 - (double)MathHelper.floor(d4));
         Vec3d vec3d = this.field_72769_h.getCloudColour(p_195466_1_);
         int i = (int)Math.floor(d2);
         int j = (int)Math.floor(d3 / 4.0D);
         int k = (int)Math.floor(d4);
         if (i != this.field_204602_S || j != this.field_204603_T || k != this.field_204604_U || this.field_72777_q.gameSettings.shouldRenderClouds() != this.field_204800_W || this.field_204605_V.squareDistanceTo(vec3d) > 2.0E-4D) {
            this.field_204602_S = i;
            this.field_204603_T = j;
            this.field_204604_U = k;
            this.field_204605_V = vec3d;
            this.field_204800_W = this.field_72777_q.gameSettings.shouldRenderClouds();
            this.field_204607_y = true;
         }

         if (this.field_204607_y) {
            this.field_204607_y = false;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            if (this.field_204601_A != null) {
               this.field_204601_A.deleteGlBuffers();
            }

            if (this.field_204608_z >= 0) {
               GLAllocation.deleteDisplayLists(this.field_204608_z);
               this.field_204608_z = -1;
            }

            if (this.field_175005_X) {
               this.field_204601_A = new VertexBuffer(DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
               this.func_204600_a(bufferbuilder, d2, d3, d4, vec3d);
               bufferbuilder.finishDrawing();
               bufferbuilder.reset();
               this.field_204601_A.bufferData(bufferbuilder.getByteBuffer());
            } else {
               this.field_204608_z = GLAllocation.generateDisplayLists(1);
               GlStateManager.newList(this.field_204608_z, 4864);
               this.func_204600_a(bufferbuilder, d2, d3, d4, vec3d);
               tessellator.draw();
               GlStateManager.endList();
            }
         }

         GlStateManager.disableCull();
         this.field_72770_i.bindTexture(field_110925_j);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(12.0F, 1.0F, 12.0F);
         GlStateManager.translatef(-f2, f3, -f4);
         if (this.field_175005_X && this.field_204601_A != null) {
            this.field_204601_A.bindBuffer();
            GlStateManager.enableClientState(32884);
            GlStateManager.enableClientState(32888);
            OpenGlHelper.glClientActiveTexture(OpenGlHelper.GL_TEXTURE0);
            GlStateManager.enableClientState(32886);
            GlStateManager.enableClientState(32885);
            GlStateManager.vertexPointer(3, 5126, 28, 0);
            GlStateManager.texCoordPointer(2, 5126, 28, 12);
            GlStateManager.colorPointer(4, 5121, 28, 20);
            GlStateManager.normalPointer(5120, 28, 24);
            int i1 = this.field_204800_W == 2 ? 0 : 1;

            for(int k1 = i1; k1 < 2; ++k1) {
               if (k1 == 0) {
                  GlStateManager.colorMask(false, false, false, false);
               } else {
                  GlStateManager.colorMask(true, true, true, true);
               }

               this.field_204601_A.drawArrays(7);
            }

            this.field_204601_A.unbindBuffer();
            GlStateManager.disableClientState(32884);
            GlStateManager.disableClientState(32888);
            GlStateManager.disableClientState(32886);
            GlStateManager.disableClientState(32885);
            OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
         } else if (this.field_204608_z >= 0) {
            int l = this.field_204800_W == 2 ? 0 : 1;

            for(int j1 = l; j1 < 2; ++j1) {
               if (j1 == 0) {
                  GlStateManager.colorMask(false, false, false, false);
               } else {
                  GlStateManager.colorMask(true, true, true, true);
               }

               GlStateManager.callList(this.field_204608_z);
            }
         }

         GlStateManager.popMatrix();
         GlStateManager.resetColor();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.enableCull();
      }
   }

   private void func_204600_a(BufferBuilder p_204600_1_, double p_204600_2_, double p_204600_4_, double p_204600_6_, Vec3d p_204600_8_) {
      float f = 4.0F;
      float f1 = 0.00390625F;
      int i = 8;
      int j = 4;
      float f2 = 9.765625E-4F;
      float f3 = (float)MathHelper.floor(p_204600_2_) * 0.00390625F;
      float f4 = (float)MathHelper.floor(p_204600_6_) * 0.00390625F;
      float f5 = (float)p_204600_8_.x;
      float f6 = (float)p_204600_8_.y;
      float f7 = (float)p_204600_8_.z;
      float f8 = f5 * 0.9F;
      float f9 = f6 * 0.9F;
      float f10 = f7 * 0.9F;
      float f11 = f5 * 0.7F;
      float f12 = f6 * 0.7F;
      float f13 = f7 * 0.7F;
      float f14 = f5 * 0.8F;
      float f15 = f6 * 0.8F;
      float f16 = f7 * 0.8F;
      p_204600_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
      float f17 = (float)Math.floor(p_204600_4_ / 4.0D) * 4.0F;
      if (this.field_204800_W == 2) {
         for(int k = -3; k <= 4; ++k) {
            for(int l = -3; l <= 4; ++l) {
               float f18 = (float)(k * 8);
               float f19 = (float)(l * 8);
               if (f17 > -5.0F) {
                  p_204600_1_.pos((double)(f18 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + 8.0F)).tex((double)((f18 + 0.0F) * 0.00390625F + f3), (double)((f19 + 8.0F) * 0.00390625F + f4)).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                  p_204600_1_.pos((double)(f18 + 8.0F), (double)(f17 + 0.0F), (double)(f19 + 8.0F)).tex((double)((f18 + 8.0F) * 0.00390625F + f3), (double)((f19 + 8.0F) * 0.00390625F + f4)).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                  p_204600_1_.pos((double)(f18 + 8.0F), (double)(f17 + 0.0F), (double)(f19 + 0.0F)).tex((double)((f18 + 8.0F) * 0.00390625F + f3), (double)((f19 + 0.0F) * 0.00390625F + f4)).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                  p_204600_1_.pos((double)(f18 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + 0.0F)).tex((double)((f18 + 0.0F) * 0.00390625F + f3), (double)((f19 + 0.0F) * 0.00390625F + f4)).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               }

               if (f17 <= 5.0F) {
                  p_204600_1_.pos((double)(f18 + 0.0F), (double)(f17 + 4.0F - 9.765625E-4F), (double)(f19 + 8.0F)).tex((double)((f18 + 0.0F) * 0.00390625F + f3), (double)((f19 + 8.0F) * 0.00390625F + f4)).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                  p_204600_1_.pos((double)(f18 + 8.0F), (double)(f17 + 4.0F - 9.765625E-4F), (double)(f19 + 8.0F)).tex((double)((f18 + 8.0F) * 0.00390625F + f3), (double)((f19 + 8.0F) * 0.00390625F + f4)).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                  p_204600_1_.pos((double)(f18 + 8.0F), (double)(f17 + 4.0F - 9.765625E-4F), (double)(f19 + 0.0F)).tex((double)((f18 + 8.0F) * 0.00390625F + f3), (double)((f19 + 0.0F) * 0.00390625F + f4)).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                  p_204600_1_.pos((double)(f18 + 0.0F), (double)(f17 + 4.0F - 9.765625E-4F), (double)(f19 + 0.0F)).tex((double)((f18 + 0.0F) * 0.00390625F + f3), (double)((f19 + 0.0F) * 0.00390625F + f4)).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
               }

               if (k > -1) {
                  for(int i1 = 0; i1 < 8; ++i1) {
                     p_204600_1_.pos((double)(f18 + (float)i1 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + 8.0F)).tex((double)((f18 + (float)i1 + 0.5F) * 0.00390625F + f3), (double)((f19 + 8.0F) * 0.00390625F + f4)).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                     p_204600_1_.pos((double)(f18 + (float)i1 + 0.0F), (double)(f17 + 4.0F), (double)(f19 + 8.0F)).tex((double)((f18 + (float)i1 + 0.5F) * 0.00390625F + f3), (double)((f19 + 8.0F) * 0.00390625F + f4)).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                     p_204600_1_.pos((double)(f18 + (float)i1 + 0.0F), (double)(f17 + 4.0F), (double)(f19 + 0.0F)).tex((double)((f18 + (float)i1 + 0.5F) * 0.00390625F + f3), (double)((f19 + 0.0F) * 0.00390625F + f4)).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                     p_204600_1_.pos((double)(f18 + (float)i1 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + 0.0F)).tex((double)((f18 + (float)i1 + 0.5F) * 0.00390625F + f3), (double)((f19 + 0.0F) * 0.00390625F + f4)).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                  }
               }

               if (k <= 1) {
                  for(int j2 = 0; j2 < 8; ++j2) {
                     p_204600_1_.pos((double)(f18 + (float)j2 + 1.0F - 9.765625E-4F), (double)(f17 + 0.0F), (double)(f19 + 8.0F)).tex((double)((f18 + (float)j2 + 0.5F) * 0.00390625F + f3), (double)((f19 + 8.0F) * 0.00390625F + f4)).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                     p_204600_1_.pos((double)(f18 + (float)j2 + 1.0F - 9.765625E-4F), (double)(f17 + 4.0F), (double)(f19 + 8.0F)).tex((double)((f18 + (float)j2 + 0.5F) * 0.00390625F + f3), (double)((f19 + 8.0F) * 0.00390625F + f4)).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                     p_204600_1_.pos((double)(f18 + (float)j2 + 1.0F - 9.765625E-4F), (double)(f17 + 4.0F), (double)(f19 + 0.0F)).tex((double)((f18 + (float)j2 + 0.5F) * 0.00390625F + f3), (double)((f19 + 0.0F) * 0.00390625F + f4)).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                     p_204600_1_.pos((double)(f18 + (float)j2 + 1.0F - 9.765625E-4F), (double)(f17 + 0.0F), (double)(f19 + 0.0F)).tex((double)((f18 + (float)j2 + 0.5F) * 0.00390625F + f3), (double)((f19 + 0.0F) * 0.00390625F + f4)).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                  }
               }

               if (l > -1) {
                  for(int k2 = 0; k2 < 8; ++k2) {
                     p_204600_1_.pos((double)(f18 + 0.0F), (double)(f17 + 4.0F), (double)(f19 + (float)k2 + 0.0F)).tex((double)((f18 + 0.0F) * 0.00390625F + f3), (double)((f19 + (float)k2 + 0.5F) * 0.00390625F + f4)).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                     p_204600_1_.pos((double)(f18 + 8.0F), (double)(f17 + 4.0F), (double)(f19 + (float)k2 + 0.0F)).tex((double)((f18 + 8.0F) * 0.00390625F + f3), (double)((f19 + (float)k2 + 0.5F) * 0.00390625F + f4)).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                     p_204600_1_.pos((double)(f18 + 8.0F), (double)(f17 + 0.0F), (double)(f19 + (float)k2 + 0.0F)).tex((double)((f18 + 8.0F) * 0.00390625F + f3), (double)((f19 + (float)k2 + 0.5F) * 0.00390625F + f4)).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                     p_204600_1_.pos((double)(f18 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + (float)k2 + 0.0F)).tex((double)((f18 + 0.0F) * 0.00390625F + f3), (double)((f19 + (float)k2 + 0.5F) * 0.00390625F + f4)).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                  }
               }

               if (l <= 1) {
                  for(int l2 = 0; l2 < 8; ++l2) {
                     p_204600_1_.pos((double)(f18 + 0.0F), (double)(f17 + 4.0F), (double)(f19 + (float)l2 + 1.0F - 9.765625E-4F)).tex((double)((f18 + 0.0F) * 0.00390625F + f3), (double)((f19 + (float)l2 + 0.5F) * 0.00390625F + f4)).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                     p_204600_1_.pos((double)(f18 + 8.0F), (double)(f17 + 4.0F), (double)(f19 + (float)l2 + 1.0F - 9.765625E-4F)).tex((double)((f18 + 8.0F) * 0.00390625F + f3), (double)((f19 + (float)l2 + 0.5F) * 0.00390625F + f4)).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                     p_204600_1_.pos((double)(f18 + 8.0F), (double)(f17 + 0.0F), (double)(f19 + (float)l2 + 1.0F - 9.765625E-4F)).tex((double)((f18 + 8.0F) * 0.00390625F + f3), (double)((f19 + (float)l2 + 0.5F) * 0.00390625F + f4)).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                     p_204600_1_.pos((double)(f18 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + (float)l2 + 1.0F - 9.765625E-4F)).tex((double)((f18 + 0.0F) * 0.00390625F + f3), (double)((f19 + (float)l2 + 0.5F) * 0.00390625F + f4)).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                  }
               }
            }
         }
      } else {
         int j1 = 1;
         int k1 = 32;

         for(int l1 = -32; l1 < 32; l1 += 32) {
            for(int i2 = -32; i2 < 32; i2 += 32) {
               p_204600_1_.pos((double)(l1 + 0), (double)f17, (double)(i2 + 32)).tex((double)((float)(l1 + 0) * 0.00390625F + f3), (double)((float)(i2 + 32) * 0.00390625F + f4)).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               p_204600_1_.pos((double)(l1 + 32), (double)f17, (double)(i2 + 32)).tex((double)((float)(l1 + 32) * 0.00390625F + f3), (double)((float)(i2 + 32) * 0.00390625F + f4)).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               p_204600_1_.pos((double)(l1 + 32), (double)f17, (double)(i2 + 0)).tex((double)((float)(l1 + 32) * 0.00390625F + f3), (double)((float)(i2 + 0) * 0.00390625F + f4)).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               p_204600_1_.pos((double)(l1 + 0), (double)f17, (double)(i2 + 0)).tex((double)((float)(l1 + 0) * 0.00390625F + f3), (double)((float)(i2 + 0) * 0.00390625F + f4)).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
            }
         }
      }

   }

   public void func_174967_a(long p_174967_1_) {
      this.field_147595_R |= this.field_174995_M.runChunkUploads(p_174967_1_);
      if (!this.field_175009_l.isEmpty()) {
         Iterator<RenderChunk> iterator = this.field_175009_l.iterator();

         while(iterator.hasNext()) {
            RenderChunk renderchunk = iterator.next();
            boolean flag;
            if (renderchunk.needsImmediateUpdate()) {
               flag = this.field_174995_M.updateChunkNow(renderchunk);
            } else {
               flag = this.field_174995_M.updateChunkLater(renderchunk);
            }

            if (!flag) {
               break;
            }

            renderchunk.clearNeedsUpdate();
            iterator.remove();
            long i = p_174967_1_ - Util.nanoTime();
            if (i < 0L) {
               break;
            }
         }
      }

   }

   public void func_180449_a(Entity p_180449_1_, float p_180449_2_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      WorldBorder worldborder = this.field_72769_h.getWorldBorder();
      double d0 = (double)(this.field_72777_q.gameSettings.renderDistanceChunks * 16);
      if (!(p_180449_1_.posX < worldborder.maxX() - d0) || !(p_180449_1_.posX > worldborder.minX() + d0) || !(p_180449_1_.posZ < worldborder.maxZ() - d0) || !(p_180449_1_.posZ > worldborder.minZ() + d0)) {
         double d1 = 1.0D - worldborder.getClosestDistance(p_180449_1_) / d0;
         d1 = Math.pow(d1, 4.0D);
         double d2 = p_180449_1_.lastTickPosX + (p_180449_1_.posX - p_180449_1_.lastTickPosX) * (double)p_180449_2_;
         double d3 = p_180449_1_.lastTickPosY + (p_180449_1_.posY - p_180449_1_.lastTickPosY) * (double)p_180449_2_;
         double d4 = p_180449_1_.lastTickPosZ + (p_180449_1_.posZ - p_180449_1_.lastTickPosZ) * (double)p_180449_2_;
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         this.field_72770_i.bindTexture(field_175006_g);
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         int i = worldborder.getStatus().getColor();
         float f = (float)(i >> 16 & 255) / 255.0F;
         float f1 = (float)(i >> 8 & 255) / 255.0F;
         float f2 = (float)(i & 255) / 255.0F;
         GlStateManager.color4f(f, f1, f2, (float)d1);
         GlStateManager.polygonOffset(-3.0F, -3.0F);
         GlStateManager.enablePolygonOffset();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.enableAlphaTest();
         GlStateManager.disableCull();
         float f3 = (float)(Util.milliTime() % 3000L) / 3000.0F;
         float f4 = 0.0F;
         float f5 = 0.0F;
         float f6 = 128.0F;
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
         bufferbuilder.setTranslation(-d2, -d3, -d4);
         double d5 = Math.max((double)MathHelper.floor(d4 - d0), worldborder.minZ());
         double d6 = Math.min((double)MathHelper.ceil(d4 + d0), worldborder.maxZ());
         if (d2 > worldborder.maxX() - d0) {
            float f7 = 0.0F;

            for(double d7 = d5; d7 < d6; f7 += 0.5F) {
               double d8 = Math.min(1.0D, d6 - d7);
               float f8 = (float)d8 * 0.5F;
               bufferbuilder.pos(worldborder.maxX(), 256.0D, d7).tex((double)(f3 + f7), (double)(f3 + 0.0F)).endVertex();
               bufferbuilder.pos(worldborder.maxX(), 256.0D, d7 + d8).tex((double)(f3 + f8 + f7), (double)(f3 + 0.0F)).endVertex();
               bufferbuilder.pos(worldborder.maxX(), 0.0D, d7 + d8).tex((double)(f3 + f8 + f7), (double)(f3 + 128.0F)).endVertex();
               bufferbuilder.pos(worldborder.maxX(), 0.0D, d7).tex((double)(f3 + f7), (double)(f3 + 128.0F)).endVertex();
               ++d7;
            }
         }

         if (d2 < worldborder.minX() + d0) {
            float f9 = 0.0F;

            for(double d9 = d5; d9 < d6; f9 += 0.5F) {
               double d12 = Math.min(1.0D, d6 - d9);
               float f12 = (float)d12 * 0.5F;
               bufferbuilder.pos(worldborder.minX(), 256.0D, d9).tex((double)(f3 + f9), (double)(f3 + 0.0F)).endVertex();
               bufferbuilder.pos(worldborder.minX(), 256.0D, d9 + d12).tex((double)(f3 + f12 + f9), (double)(f3 + 0.0F)).endVertex();
               bufferbuilder.pos(worldborder.minX(), 0.0D, d9 + d12).tex((double)(f3 + f12 + f9), (double)(f3 + 128.0F)).endVertex();
               bufferbuilder.pos(worldborder.minX(), 0.0D, d9).tex((double)(f3 + f9), (double)(f3 + 128.0F)).endVertex();
               ++d9;
            }
         }

         d5 = Math.max((double)MathHelper.floor(d2 - d0), worldborder.minX());
         d6 = Math.min((double)MathHelper.ceil(d2 + d0), worldborder.maxX());
         if (d4 > worldborder.maxZ() - d0) {
            float f10 = 0.0F;

            for(double d10 = d5; d10 < d6; f10 += 0.5F) {
               double d13 = Math.min(1.0D, d6 - d10);
               float f13 = (float)d13 * 0.5F;
               bufferbuilder.pos(d10, 256.0D, worldborder.maxZ()).tex((double)(f3 + f10), (double)(f3 + 0.0F)).endVertex();
               bufferbuilder.pos(d10 + d13, 256.0D, worldborder.maxZ()).tex((double)(f3 + f13 + f10), (double)(f3 + 0.0F)).endVertex();
               bufferbuilder.pos(d10 + d13, 0.0D, worldborder.maxZ()).tex((double)(f3 + f13 + f10), (double)(f3 + 128.0F)).endVertex();
               bufferbuilder.pos(d10, 0.0D, worldborder.maxZ()).tex((double)(f3 + f10), (double)(f3 + 128.0F)).endVertex();
               ++d10;
            }
         }

         if (d4 < worldborder.minZ() + d0) {
            float f11 = 0.0F;

            for(double d11 = d5; d11 < d6; f11 += 0.5F) {
               double d14 = Math.min(1.0D, d6 - d11);
               float f14 = (float)d14 * 0.5F;
               bufferbuilder.pos(d11, 256.0D, worldborder.minZ()).tex((double)(f3 + f11), (double)(f3 + 0.0F)).endVertex();
               bufferbuilder.pos(d11 + d14, 256.0D, worldborder.minZ()).tex((double)(f3 + f14 + f11), (double)(f3 + 0.0F)).endVertex();
               bufferbuilder.pos(d11 + d14, 0.0D, worldborder.minZ()).tex((double)(f3 + f14 + f11), (double)(f3 + 128.0F)).endVertex();
               bufferbuilder.pos(d11, 0.0D, worldborder.minZ()).tex((double)(f3 + f11), (double)(f3 + 128.0F)).endVertex();
               ++d11;
            }
         }

         tessellator.draw();
         bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
         GlStateManager.enableCull();
         GlStateManager.disableAlphaTest();
         GlStateManager.polygonOffset(0.0F, 0.0F);
         GlStateManager.disablePolygonOffset();
         GlStateManager.enableAlphaTest();
         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
      }
   }

   private void func_180443_s() {
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.enableBlend();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
      GlStateManager.polygonOffset(-1.0F, -10.0F);
      GlStateManager.enablePolygonOffset();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableAlphaTest();
      GlStateManager.pushMatrix();
   }

   private void func_174969_t() {
      GlStateManager.disableAlphaTest();
      GlStateManager.polygonOffset(0.0F, 0.0F);
      GlStateManager.disablePolygonOffset();
      GlStateManager.enableAlphaTest();
      GlStateManager.depthMask(true);
      GlStateManager.popMatrix();
   }

   public void func_174981_a(Tessellator p_174981_1_, BufferBuilder p_174981_2_, Entity p_174981_3_, float p_174981_4_) {
      double d0 = p_174981_3_.lastTickPosX + (p_174981_3_.posX - p_174981_3_.lastTickPosX) * (double)p_174981_4_;
      double d1 = p_174981_3_.lastTickPosY + (p_174981_3_.posY - p_174981_3_.lastTickPosY) * (double)p_174981_4_;
      double d2 = p_174981_3_.lastTickPosZ + (p_174981_3_.posZ - p_174981_3_.lastTickPosZ) * (double)p_174981_4_;
      if (!this.field_72738_E.isEmpty()) {
         this.field_72770_i.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         this.func_180443_s();
         p_174981_2_.begin(7, DefaultVertexFormats.BLOCK);
         p_174981_2_.setTranslation(-d0, -d1, -d2);
         p_174981_2_.noColor();
         Iterator<DestroyBlockProgress> iterator = this.field_72738_E.values().iterator();

         while(iterator.hasNext()) {
            DestroyBlockProgress destroyblockprogress = iterator.next();
            BlockPos blockpos = destroyblockprogress.getPosition();
            Block block = this.field_72769_h.getBlockState(blockpos).getBlock();
            if (!(block instanceof BlockChest) && !(block instanceof BlockEnderChest) && !(block instanceof BlockSign) && !(block instanceof BlockAbstractSkull)) {
               double d3 = (double)blockpos.getX() - d0;
               double d4 = (double)blockpos.getY() - d1;
               double d5 = (double)blockpos.getZ() - d2;
               if (d3 * d3 + d4 * d4 + d5 * d5 > 1024.0D) {
                  iterator.remove();
               } else {
                  IBlockState iblockstate = this.field_72769_h.getBlockState(blockpos);
                  if (!iblockstate.isAir()) {
                     int i = destroyblockprogress.getPartialBlockDamage();
                     TextureAtlasSprite textureatlassprite = this.field_94141_F[i];
                     BlockRendererDispatcher blockrendererdispatcher = this.field_72777_q.getBlockRendererDispatcher();
                     blockrendererdispatcher.renderBlockDamage(iblockstate, blockpos, textureatlassprite, this.field_72769_h);
                  }
               }
            }
         }

         p_174981_1_.draw();
         p_174981_2_.setTranslation(0.0D, 0.0D, 0.0D);
         this.func_174969_t();
      }

   }

   public void func_72731_b(EntityPlayer p_72731_1_, RayTraceResult p_72731_2_, int p_72731_3_, float p_72731_4_) {
      if (p_72731_3_ == 0 && p_72731_2_.type == RayTraceResult.Type.BLOCK) {
         BlockPos blockpos = p_72731_2_.getBlockPos();
         IBlockState iblockstate = this.field_72769_h.getBlockState(blockpos);
         if (!iblockstate.isAir() && this.field_72769_h.getWorldBorder().contains(blockpos)) {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.lineWidth(Math.max(2.5F, (float)this.field_72777_q.mainWindow.getFramebufferWidth() / 1920.0F * 2.5F));
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.matrixMode(5889);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(1.0F, 1.0F, 0.999F);
            double d0 = p_72731_1_.lastTickPosX + (p_72731_1_.posX - p_72731_1_.lastTickPosX) * (double)p_72731_4_;
            double d1 = p_72731_1_.lastTickPosY + (p_72731_1_.posY - p_72731_1_.lastTickPosY) * (double)p_72731_4_;
            double d2 = p_72731_1_.lastTickPosZ + (p_72731_1_.posZ - p_72731_1_.lastTickPosZ) * (double)p_72731_4_;
            func_195463_b(iblockstate.getShape(this.field_72769_h, blockpos), (double)blockpos.getX() - d0, (double)blockpos.getY() - d1, (double)blockpos.getZ() - d2, 0.0F, 0.0F, 0.0F, 0.4F);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
         }
      }

   }

   public static void func_195470_a(VoxelShape p_195470_0_, double p_195470_1_, double p_195470_3_, double p_195470_5_, float p_195470_7_, float p_195470_8_, float p_195470_9_, float p_195470_10_) {
      List<AxisAlignedBB> list = p_195470_0_.toBoundingBoxList();
      int i = MathHelper.ceil((double)list.size() / 3.0D);

      for(int j = 0; j < list.size(); ++j) {
         AxisAlignedBB axisalignedbb = list.get(j);
         float f = ((float)j % (float)i + 1.0F) / (float)i;
         float f1 = (float)(j / i);
         float f2 = f * (float)(f1 == 0.0F ? 1 : 0);
         float f3 = f * (float)(f1 == 1.0F ? 1 : 0);
         float f4 = f * (float)(f1 == 2.0F ? 1 : 0);
         func_195463_b(VoxelShapes.func_197881_a(axisalignedbb.offset(0.0D, 0.0D, 0.0D)), p_195470_1_, p_195470_3_, p_195470_5_, f2, f3, f4, 1.0F);
      }

   }

   public static void func_195463_b(VoxelShape p_195463_0_, double p_195463_1_, double p_195463_3_, double p_195463_5_, float p_195463_7_, float p_195463_8_, float p_195463_9_, float p_195463_10_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
      p_195463_0_.func_197754_a((p_195468_11_, p_195468_13_, p_195468_15_, p_195468_17_, p_195468_19_, p_195468_21_) -> {
         bufferbuilder.pos(p_195468_11_ + p_195463_1_, p_195468_13_ + p_195463_3_, p_195468_15_ + p_195463_5_).color(p_195463_7_, p_195463_8_, p_195463_9_, p_195463_10_).endVertex();
         bufferbuilder.pos(p_195468_17_ + p_195463_1_, p_195468_19_ + p_195463_3_, p_195468_21_ + p_195463_5_).color(p_195463_7_, p_195463_8_, p_195463_9_, p_195463_10_).endVertex();
      });
      tessellator.draw();
   }

   public static void func_189697_a(AxisAlignedBB p_189697_0_, float p_189697_1_, float p_189697_2_, float p_189697_3_, float p_189697_4_) {
      func_189694_a(p_189697_0_.minX, p_189697_0_.minY, p_189697_0_.minZ, p_189697_0_.maxX, p_189697_0_.maxY, p_189697_0_.maxZ, p_189697_1_, p_189697_2_, p_189697_3_, p_189697_4_);
   }

   public static void func_189694_a(double p_189694_0_, double p_189694_2_, double p_189694_4_, double p_189694_6_, double p_189694_8_, double p_189694_10_, float p_189694_12_, float p_189694_13_, float p_189694_14_, float p_189694_15_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
      func_189698_a(bufferbuilder, p_189694_0_, p_189694_2_, p_189694_4_, p_189694_6_, p_189694_8_, p_189694_10_, p_189694_12_, p_189694_13_, p_189694_14_, p_189694_15_);
      tessellator.draw();
   }

   public static void func_189698_a(BufferBuilder p_189698_0_, double p_189698_1_, double p_189698_3_, double p_189698_5_, double p_189698_7_, double p_189698_9_, double p_189698_11_, float p_189698_13_, float p_189698_14_, float p_189698_15_, float p_189698_16_) {
      p_189698_0_.pos(p_189698_1_, p_189698_3_, p_189698_5_).color(p_189698_13_, p_189698_14_, p_189698_15_, 0.0F).endVertex();
      p_189698_0_.pos(p_189698_1_, p_189698_3_, p_189698_5_).color(p_189698_13_, p_189698_14_, p_189698_15_, p_189698_16_).endVertex();
      p_189698_0_.pos(p_189698_7_, p_189698_3_, p_189698_5_).color(p_189698_13_, p_189698_14_, p_189698_15_, p_189698_16_).endVertex();
      p_189698_0_.pos(p_189698_7_, p_189698_3_, p_189698_11_).color(p_189698_13_, p_189698_14_, p_189698_15_, p_189698_16_).endVertex();
      p_189698_0_.pos(p_189698_1_, p_189698_3_, p_189698_11_).color(p_189698_13_, p_189698_14_, p_189698_15_, p_189698_16_).endVertex();
      p_189698_0_.pos(p_189698_1_, p_189698_3_, p_189698_5_).color(p_189698_13_, p_189698_14_, p_189698_15_, p_189698_16_).endVertex();
      p_189698_0_.pos(p_189698_1_, p_189698_9_, p_189698_5_).color(p_189698_13_, p_189698_14_, p_189698_15_, p_189698_16_).endVertex();
      p_189698_0_.pos(p_189698_7_, p_189698_9_, p_189698_5_).color(p_189698_13_, p_189698_14_, p_189698_15_, p_189698_16_).endVertex();
      p_189698_0_.pos(p_189698_7_, p_189698_9_, p_189698_11_).color(p_189698_13_, p_189698_14_, p_189698_15_, p_189698_16_).endVertex();
      p_189698_0_.pos(p_189698_1_, p_189698_9_, p_189698_11_).color(p_189698_13_, p_189698_14_, p_189698_15_, p_189698_16_).endVertex();
      p_189698_0_.pos(p_189698_1_, p_189698_9_, p_189698_5_).color(p_189698_13_, p_189698_14_, p_189698_15_, p_189698_16_).endVertex();
      p_189698_0_.pos(p_189698_1_, p_189698_9_, p_189698_11_).color(p_189698_13_, p_189698_14_, p_189698_15_, 0.0F).endVertex();
      p_189698_0_.pos(p_189698_1_, p_189698_3_, p_189698_11_).color(p_189698_13_, p_189698_14_, p_189698_15_, p_189698_16_).endVertex();
      p_189698_0_.pos(p_189698_7_, p_189698_9_, p_189698_11_).color(p_189698_13_, p_189698_14_, p_189698_15_, 0.0F).endVertex();
      p_189698_0_.pos(p_189698_7_, p_189698_3_, p_189698_11_).color(p_189698_13_, p_189698_14_, p_189698_15_, p_189698_16_).endVertex();
      p_189698_0_.pos(p_189698_7_, p_189698_9_, p_189698_5_).color(p_189698_13_, p_189698_14_, p_189698_15_, 0.0F).endVertex();
      p_189698_0_.pos(p_189698_7_, p_189698_3_, p_189698_5_).color(p_189698_13_, p_189698_14_, p_189698_15_, p_189698_16_).endVertex();
      p_189698_0_.pos(p_189698_7_, p_189698_3_, p_189698_5_).color(p_189698_13_, p_189698_14_, p_189698_15_, 0.0F).endVertex();
   }

   public static void func_189696_b(AxisAlignedBB p_189696_0_, float p_189696_1_, float p_189696_2_, float p_189696_3_, float p_189696_4_) {
      func_189695_b(p_189696_0_.minX, p_189696_0_.minY, p_189696_0_.minZ, p_189696_0_.maxX, p_189696_0_.maxY, p_189696_0_.maxZ, p_189696_1_, p_189696_2_, p_189696_3_, p_189696_4_);
   }

   public static void func_189695_b(double p_189695_0_, double p_189695_2_, double p_189695_4_, double p_189695_6_, double p_189695_8_, double p_189695_10_, float p_189695_12_, float p_189695_13_, float p_189695_14_, float p_189695_15_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
      func_189693_b(bufferbuilder, p_189695_0_, p_189695_2_, p_189695_4_, p_189695_6_, p_189695_8_, p_189695_10_, p_189695_12_, p_189695_13_, p_189695_14_, p_189695_15_);
      tessellator.draw();
   }

   public static void func_189693_b(BufferBuilder p_189693_0_, double p_189693_1_, double p_189693_3_, double p_189693_5_, double p_189693_7_, double p_189693_9_, double p_189693_11_, float p_189693_13_, float p_189693_14_, float p_189693_15_, float p_189693_16_) {
      p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
      p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
   }

   private void func_184385_a(int p_184385_1_, int p_184385_2_, int p_184385_3_, int p_184385_4_, int p_184385_5_, int p_184385_6_, boolean p_184385_7_) {
      this.field_175008_n.markBlocksForUpdate(p_184385_1_, p_184385_2_, p_184385_3_, p_184385_4_, p_184385_5_, p_184385_6_, p_184385_7_);
   }

   public void notifyBlockUpdate(IBlockReader p_184376_1_, BlockPos p_184376_2_, IBlockState p_184376_3_, IBlockState p_184376_4_, int p_184376_5_) {
      int i = p_184376_2_.getX();
      int j = p_184376_2_.getY();
      int k = p_184376_2_.getZ();
      this.func_184385_a(i - 1, j - 1, k - 1, i + 1, j + 1, k + 1, (p_184376_5_ & 8) != 0);
   }

   public void notifyLightSet(BlockPos p_174959_1_) {
      this.field_184387_ae.add(p_174959_1_.toImmutable());
   }

   public void markBlockRangeForRenderUpdate(int p_147585_1_, int p_147585_2_, int p_147585_3_, int p_147585_4_, int p_147585_5_, int p_147585_6_) {
      this.func_184385_a(p_147585_1_ - 1, p_147585_2_ - 1, p_147585_3_ - 1, p_147585_4_ + 1, p_147585_5_ + 1, p_147585_6_ + 1, false);
   }

   public void playRecord(@Nullable SoundEvent p_184377_1_, BlockPos p_184377_2_) {
      ISound isound = this.field_147593_P.get(p_184377_2_);
      if (isound != null) {
         this.field_72777_q.getSoundHandler().stop(isound);
         this.field_147593_P.remove(p_184377_2_);
      }

      if (p_184377_1_ != null) {
         ItemRecord itemrecord = ItemRecord.getBySound(p_184377_1_);
         if (itemrecord != null) {
            this.field_72777_q.ingameGUI.setRecordPlayingMessage(itemrecord.getRecordDescription().getFormattedText());
         }

         ISound simplesound = SimpleSound.func_184372_a(p_184377_1_, (float)p_184377_2_.getX(), (float)p_184377_2_.getY(), (float)p_184377_2_.getZ());
         this.field_147593_P.put(p_184377_2_, simplesound);
         this.field_72777_q.getSoundHandler().play(simplesound);
      }

      this.func_193054_a(this.field_72769_h, p_184377_2_, p_184377_1_ != null);
   }

   private void func_193054_a(World p_193054_1_, BlockPos p_193054_2_, boolean p_193054_3_) {
      for(EntityLivingBase entitylivingbase : p_193054_1_.getEntitiesWithinAABB(EntityLivingBase.class, (new AxisAlignedBB(p_193054_2_)).grow(3.0D))) {
         entitylivingbase.setPartying(p_193054_2_, p_193054_3_);
      }

   }

   public void playSoundToAllNearExcept(@Nullable EntityPlayer p_184375_1_, SoundEvent p_184375_2_, SoundCategory p_184375_3_, double p_184375_4_, double p_184375_6_, double p_184375_8_, float p_184375_10_, float p_184375_11_) {
   }

   public void addParticle(IParticleData p_195461_1_, boolean p_195461_2_, double p_195461_3_, double p_195461_5_, double p_195461_7_, double p_195461_9_, double p_195461_11_, double p_195461_13_) {
      this.addParticle(p_195461_1_, p_195461_2_, false, p_195461_3_, p_195461_5_, p_195461_7_, p_195461_9_, p_195461_11_, p_195461_13_);
   }

   public void addParticle(IParticleData p_195462_1_, boolean p_195462_2_, boolean p_195462_3_, double p_195462_4_, double p_195462_6_, double p_195462_8_, double p_195462_10_, double p_195462_12_, double p_195462_14_) {
      try {
         this.func_195469_b(p_195462_1_, p_195462_2_, p_195462_3_, p_195462_4_, p_195462_6_, p_195462_8_, p_195462_10_, p_195462_12_, p_195462_14_);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while adding particle");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being added");
         crashreportcategory.addCrashSection("ID", p_195462_1_.getType().getId());
         crashreportcategory.addCrashSection("Parameters", p_195462_1_.getParameters());
         crashreportcategory.addDetail("Position", () -> {
            return CrashReportCategory.getCoordinateInfo(p_195462_4_, p_195462_6_, p_195462_8_);
         });
         throw new ReportedException(crashreport);
      }
   }

   private <T extends IParticleData> void func_195467_a(T p_195467_1_, double p_195467_2_, double p_195467_4_, double p_195467_6_, double p_195467_8_, double p_195467_10_, double p_195467_12_) {
      this.addParticle(p_195467_1_, p_195467_1_.getType().getAlwaysShow(), p_195467_2_, p_195467_4_, p_195467_6_, p_195467_8_, p_195467_10_, p_195467_12_);
   }

   @Nullable
   private Particle func_195471_b(IParticleData p_195471_1_, boolean p_195471_2_, double p_195471_3_, double p_195471_5_, double p_195471_7_, double p_195471_9_, double p_195471_11_, double p_195471_13_) {
      return this.func_195469_b(p_195471_1_, p_195471_2_, false, p_195471_3_, p_195471_5_, p_195471_7_, p_195471_9_, p_195471_11_, p_195471_13_);
   }

   @Nullable
   private Particle func_195469_b(IParticleData p_195469_1_, boolean p_195469_2_, boolean p_195469_3_, double p_195469_4_, double p_195469_6_, double p_195469_8_, double p_195469_10_, double p_195469_12_, double p_195469_14_) {
      Entity entity = this.field_72777_q.getRenderViewEntity();
      if (this.field_72777_q != null && entity != null && this.field_72777_q.effectRenderer != null) {
         int i = this.func_190572_a(p_195469_3_);
         double d0 = entity.posX - p_195469_4_;
         double d1 = entity.posY - p_195469_6_;
         double d2 = entity.posZ - p_195469_8_;
         if (p_195469_2_) {
            return this.field_72777_q.effectRenderer.addParticle(p_195469_1_, p_195469_4_, p_195469_6_, p_195469_8_, p_195469_10_, p_195469_12_, p_195469_14_);
         } else if (d0 * d0 + d1 * d1 + d2 * d2 > 1024.0D) {
            return null;
         } else {
            return i > 1 ? null : this.field_72777_q.effectRenderer.addParticle(p_195469_1_, p_195469_4_, p_195469_6_, p_195469_8_, p_195469_10_, p_195469_12_, p_195469_14_);
         }
      } else {
         return null;
      }
   }

   private int func_190572_a(boolean p_190572_1_) {
      int i = this.field_72777_q.gameSettings.particleSetting;
      if (p_190572_1_ && i == 2 && this.field_72769_h.rand.nextInt(10) == 0) {
         i = 1;
      }

      if (i == 1 && this.field_72769_h.rand.nextInt(3) == 0) {
         i = 2;
      }

      return i;
   }

   public void onEntityAdded(Entity p_72703_1_) {
   }

   public void onEntityRemoved(Entity p_72709_1_) {
   }

   public void func_72728_f() {
   }

   public void broadcastSound(int p_180440_1_, BlockPos p_180440_2_, int p_180440_3_) {
      switch(p_180440_1_) {
      case 1023:
      case 1028:
      case 1038:
         Entity entity = this.field_72777_q.getRenderViewEntity();
         if (entity != null) {
            double d0 = (double)p_180440_2_.getX() - entity.posX;
            double d1 = (double)p_180440_2_.getY() - entity.posY;
            double d2 = (double)p_180440_2_.getZ() - entity.posZ;
            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            double d4 = entity.posX;
            double d5 = entity.posY;
            double d6 = entity.posZ;
            if (d3 > 0.0D) {
               d4 += d0 / d3 * 2.0D;
               d5 += d1 / d3 * 2.0D;
               d6 += d2 / d3 * 2.0D;
            }

            if (p_180440_1_ == 1023) {
               this.field_72769_h.playSound(d4, d5, d6, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
            } else if (p_180440_1_ == 1038) {
               this.field_72769_h.playSound(d4, d5, d6, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
            } else {
               this.field_72769_h.playSound(d4, d5, d6, SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.HOSTILE, 5.0F, 1.0F, false);
            }
         }
      default:
      }
   }

   public void playEvent(EntityPlayer p_180439_1_, int p_180439_2_, BlockPos p_180439_3_, int p_180439_4_) {
      Random random = this.field_72769_h.rand;
      switch(p_180439_2_) {
      case 1000:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1001:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
         break;
      case 1002:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
         break;
      case 1003:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
         break;
      case 1004:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_FIREWORK_ROCKET_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
         break;
      case 1005:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1006:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1007:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1008:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_FENCE_GATE_OPEN, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1009:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);
         break;
      case 1010:
         if (Item.getItemById(p_180439_4_) instanceof ItemRecord) {
            this.field_72769_h.playRecord(p_180439_3_, ((ItemRecord)Item.getItemById(p_180439_4_)).getSound());
         } else {
            this.field_72769_h.playRecord(p_180439_3_, (SoundEvent)null);
         }
         break;
      case 1011:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1012:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1013:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1014:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1015:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1016:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1017:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1018:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1019:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1020:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1021:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1022:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1024:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1025:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.NEUTRAL, 0.05F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1026:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1027:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1029:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1030:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1031:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3F, this.field_72769_h.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1032:
         this.field_72777_q.getSoundHandler().play(SimpleSound.func_184371_a(SoundEvents.BLOCK_PORTAL_TRAVEL, random.nextFloat() * 0.4F + 0.8F));
         break;
      case 1033:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1034:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1035:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1036:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1037:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1039:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_PHANTOM_BITE, SoundCategory.HOSTILE, 0.3F, this.field_72769_h.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1040:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, SoundCategory.NEUTRAL, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1041:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_HUSK_CONVERTED_TO_ZOMBIE, SoundCategory.NEUTRAL, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 2000:
         EnumFacing enumfacing = EnumFacing.byIndex(p_180439_4_);
         int i = enumfacing.getXOffset();
         int j1 = enumfacing.getYOffset();
         int j = enumfacing.getZOffset();
         double d13 = (double)p_180439_3_.getX() + (double)i * 0.6D + 0.5D;
         double d15 = (double)p_180439_3_.getY() + (double)j1 * 0.6D + 0.5D;
         double d16 = (double)p_180439_3_.getZ() + (double)j * 0.6D + 0.5D;

         for(int l1 = 0; l1 < 10; ++l1) {
            double d18 = random.nextDouble() * 0.2D + 0.01D;
            double d21 = d13 + (double)i * 0.01D + (random.nextDouble() - 0.5D) * (double)j * 0.5D;
            double d23 = d15 + (double)j1 * 0.01D + (random.nextDouble() - 0.5D) * (double)j1 * 0.5D;
            double d25 = d16 + (double)j * 0.01D + (random.nextDouble() - 0.5D) * (double)i * 0.5D;
            double d26 = (double)i * d18 + random.nextGaussian() * 0.01D;
            double d27 = (double)j1 * d18 + random.nextGaussian() * 0.01D;
            double d9 = (double)j * d18 + random.nextGaussian() * 0.01D;
            this.func_195467_a(Particles.SMOKE, d21, d23, d25, d26, d27, d9);
         }
         break;
      case 2001:
         IBlockState iblockstate = Block.getStateById(p_180439_4_);
         if (!iblockstate.isAir()) {
            SoundType soundtype = iblockstate.getBlock().getSoundType();
            this.field_72769_h.playSound(p_180439_3_, soundtype.getBreakSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F, false);
         }

         this.field_72777_q.effectRenderer.addBlockDestroyEffects(p_180439_3_, iblockstate);
         break;
      case 2002:
      case 2007:
         double d10 = (double)p_180439_3_.getX();
         double d11 = (double)p_180439_3_.getY();
         double d12 = (double)p_180439_3_.getZ();

         for(int k1 = 0; k1 < 8; ++k1) {
            this.func_195467_a(new ItemParticleData(Particles.ITEM, new ItemStack(Items.SPLASH_POTION)), d10, d11, d12, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D);
         }

         float f5 = (float)(p_180439_4_ >> 16 & 255) / 255.0F;
         float f = (float)(p_180439_4_ >> 8 & 255) / 255.0F;
         float f1 = (float)(p_180439_4_ >> 0 & 255) / 255.0F;
         IParticleData iparticledata = p_180439_2_ == 2007 ? Particles.INSTANT_EFFECT : Particles.EFFECT;

         for(int l = 0; l < 100; ++l) {
            double d17 = random.nextDouble() * 4.0D;
            double d20 = random.nextDouble() * Math.PI * 2.0D;
            double d4 = Math.cos(d20) * d17;
            double d6 = 0.01D + random.nextDouble() * 0.5D;
            double d8 = Math.sin(d20) * d17;
            Particle particle1 = this.func_195471_b(iparticledata, iparticledata.getType().getAlwaysShow(), d10 + d4 * 0.1D, d11 + 0.3D, d12 + d8 * 0.1D, d4, d6, d8);
            if (particle1 != null) {
               float f4 = 0.75F + random.nextFloat() * 0.25F;
               particle1.setColor(f5 * f4, f * f4, f1 * f4);
               particle1.multiplyVelocity((float)d17);
            }
         }

         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0F, this.field_72769_h.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 2003:
         double d0 = (double)p_180439_3_.getX() + 0.5D;
         double d1 = (double)p_180439_3_.getY();
         double d2 = (double)p_180439_3_.getZ() + 0.5D;

         for(int k = 0; k < 8; ++k) {
            this.func_195467_a(new ItemParticleData(Particles.ITEM, new ItemStack(Items.ENDER_EYE)), d0, d1, d2, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D);
         }

         for(double d14 = 0.0D; d14 < (Math.PI * 2D); d14 += 0.15707963267948966D) {
            this.func_195467_a(Particles.PORTAL, d0 + Math.cos(d14) * 5.0D, d1 - 0.4D, d2 + Math.sin(d14) * 5.0D, Math.cos(d14) * -5.0D, 0.0D, Math.sin(d14) * -5.0D);
            this.func_195467_a(Particles.PORTAL, d0 + Math.cos(d14) * 5.0D, d1 - 0.4D, d2 + Math.sin(d14) * 5.0D, Math.cos(d14) * -7.0D, 0.0D, Math.sin(d14) * -7.0D);
         }
         break;
      case 2004:
         for(int i2 = 0; i2 < 20; ++i2) {
            double d19 = (double)p_180439_3_.getX() + 0.5D + ((double)this.field_72769_h.rand.nextFloat() - 0.5D) * 2.0D;
            double d22 = (double)p_180439_3_.getY() + 0.5D + ((double)this.field_72769_h.rand.nextFloat() - 0.5D) * 2.0D;
            double d24 = (double)p_180439_3_.getZ() + 0.5D + ((double)this.field_72769_h.rand.nextFloat() - 0.5D) * 2.0D;
            this.field_72769_h.spawnParticle(Particles.SMOKE, d19, d22, d24, 0.0D, 0.0D, 0.0D);
            this.field_72769_h.spawnParticle(Particles.FLAME, d19, d22, d24, 0.0D, 0.0D, 0.0D);
         }
         break;
      case 2005:
         ItemBoneMeal.spawnBonemealParticles(this.field_72769_h, p_180439_3_, p_180439_4_);
         break;
      case 2006:
         for(int i1 = 0; i1 < 200; ++i1) {
            float f2 = random.nextFloat() * 4.0F;
            float f3 = random.nextFloat() * ((float)Math.PI * 2F);
            double d3 = (double)(MathHelper.cos(f3) * f2);
            double d5 = 0.01D + random.nextDouble() * 0.5D;
            double d7 = (double)(MathHelper.sin(f3) * f2);
            Particle particle = this.func_195471_b(Particles.DRAGON_BREATH, false, (double)p_180439_3_.getX() + d3 * 0.1D, (double)p_180439_3_.getY() + 0.3D, (double)p_180439_3_.getZ() + d7 * 0.1D, d3, d5, d7);
            if (particle != null) {
               particle.multiplyVelocity(f2);
            }
         }

         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 1.0F, this.field_72769_h.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 3000:
         this.field_72769_h.addParticle(Particles.EXPLOSION_EMITTER, true, (double)p_180439_3_.getX() + 0.5D, (double)p_180439_3_.getY() + 0.5D, (double)p_180439_3_.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.BLOCK_END_GATEWAY_SPAWN, SoundCategory.BLOCKS, 10.0F, (1.0F + (this.field_72769_h.rand.nextFloat() - this.field_72769_h.rand.nextFloat()) * 0.2F) * 0.7F, false);
         break;
      case 3001:
         this.field_72769_h.playSound(p_180439_3_, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 64.0F, 0.8F + this.field_72769_h.rand.nextFloat() * 0.3F, false);
      }

   }

   public void sendBlockBreakProgress(int p_180441_1_, BlockPos p_180441_2_, int p_180441_3_) {
      if (p_180441_3_ >= 0 && p_180441_3_ < 10) {
         DestroyBlockProgress destroyblockprogress = this.field_72738_E.get(p_180441_1_);
         if (destroyblockprogress == null || destroyblockprogress.getPosition().getX() != p_180441_2_.getX() || destroyblockprogress.getPosition().getY() != p_180441_2_.getY() || destroyblockprogress.getPosition().getZ() != p_180441_2_.getZ()) {
            destroyblockprogress = new DestroyBlockProgress(p_180441_1_, p_180441_2_);
            this.field_72738_E.put(p_180441_1_, destroyblockprogress);
         }

         destroyblockprogress.setPartialBlockDamage(p_180441_3_);
         destroyblockprogress.setCloudUpdateTick(this.field_72773_u);
      } else {
         this.field_72738_E.remove(p_180441_1_);
      }

   }

   public boolean func_184384_n() {
      return this.field_175009_l.isEmpty() && this.field_174995_M.hasNoChunkUpdates();
   }

   public void func_174979_m() {
      this.field_147595_R = true;
      this.field_204607_y = true;
   }

   public void func_181023_a(Collection<TileEntity> p_181023_1_, Collection<TileEntity> p_181023_2_) {
      synchronized(this.field_181024_n) {
         this.field_181024_n.removeAll(p_181023_1_);
         this.field_181024_n.addAll(p_181023_2_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ContainerLocalRenderInformation {
      private final RenderChunk field_178036_a;
      private final EnumFacing field_178034_b;
      private byte field_178035_c;
      private final int field_178032_d;

      private ContainerLocalRenderInformation(RenderChunk p_i46248_2_, @Nullable EnumFacing p_i46248_3_, int p_i46248_4_) {
         this.field_178036_a = p_i46248_2_;
         this.field_178034_b = p_i46248_3_;
         this.field_178032_d = p_i46248_4_;
      }

      public void func_189561_a(byte p_189561_1_, EnumFacing p_189561_2_) {
         this.field_178035_c = (byte)(this.field_178035_c | p_189561_1_ | 1 << p_189561_2_.ordinal());
      }

      public boolean func_189560_a(EnumFacing p_189560_1_) {
         return (this.field_178035_c & 1 << p_189560_1_.ordinal()) > 0;
      }
   }
}
