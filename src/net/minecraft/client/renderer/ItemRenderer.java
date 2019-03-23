package net.minecraft.client.renderer;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRenderer implements IResourceManagerReloadListener {
   public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   private static final Set<Item> ITEM_MODEL_BLACKLIST = Sets.newHashSet(Items.AIR);
   public float zLevel;
   private final ItemModelMesher itemModelMesher;
   private final TextureManager textureManager;
   private final ItemColors itemColors;

   public ItemRenderer(TextureManager p_i46552_1_, ModelManager p_i46552_2_, ItemColors p_i46552_3_) {
      this.textureManager = p_i46552_1_;
      this.itemModelMesher = new ItemModelMesher(p_i46552_2_);

      for(Item item : IRegistry.field_212630_s) {
         if (!ITEM_MODEL_BLACKLIST.contains(item)) {
            this.itemModelMesher.func_199311_a(item, new ModelResourceLocation(IRegistry.field_212630_s.func_177774_c(item), "inventory"));
         }
      }

      this.itemColors = p_i46552_3_;
   }

   public ItemModelMesher getItemModelMesher() {
      return this.itemModelMesher;
   }

   private void func_191961_a(IBakedModel p_191961_1_, ItemStack p_191961_2_) {
      this.func_191967_a(p_191961_1_, -1, p_191961_2_);
   }

   private void func_191965_a(IBakedModel p_191965_1_, int p_191965_2_) {
      this.func_191967_a(p_191965_1_, p_191965_2_, ItemStack.EMPTY);
   }

   private void func_191967_a(IBakedModel p_191967_1_, int p_191967_2_, ItemStack p_191967_3_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
      Random random = new Random();
      long i = 42L;

      for(EnumFacing enumfacing : EnumFacing.values()) {
         random.setSeed(42L);
         this.renderQuads(bufferbuilder, p_191967_1_.func_200117_a((IBlockState)null, enumfacing, random), p_191967_2_, p_191967_3_);
      }

      random.setSeed(42L);
      this.renderQuads(bufferbuilder, p_191967_1_.func_200117_a((IBlockState)null, (EnumFacing)null, random), p_191967_2_, p_191967_3_);
      tessellator.draw();
   }

   public void func_180454_a(ItemStack p_180454_1_, IBakedModel p_180454_2_) {
      if (!p_180454_1_.isEmpty()) {
         GlStateManager.pushMatrix();
         GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
         if (p_180454_2_.func_188618_c()) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            TileEntityItemStackRenderer.instance.renderByItem(p_180454_1_);
         } else {
            this.func_191961_a(p_180454_2_, p_180454_1_);
            if (p_180454_1_.hasEffect()) {
               renderEffect(this.textureManager, () -> {
                  this.func_191965_a(p_180454_2_, -8372020);
               }, 8);
            }
         }

         GlStateManager.popMatrix();
      }
   }

   public static void renderEffect(TextureManager p_211128_0_, Runnable p_211128_1_, int p_211128_2_) {
      GlStateManager.depthMask(false);
      GlStateManager.depthFunc(514);
      GlStateManager.disableLighting();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
      p_211128_0_.bindTexture(RES_ITEM_GLINT);
      GlStateManager.matrixMode(5890);
      GlStateManager.pushMatrix();
      GlStateManager.scalef((float)p_211128_2_, (float)p_211128_2_, (float)p_211128_2_);
      float f = (float)(Util.milliTime() % 3000L) / 3000.0F / (float)p_211128_2_;
      GlStateManager.translatef(f, 0.0F, 0.0F);
      GlStateManager.rotatef(-50.0F, 0.0F, 0.0F, 1.0F);
      p_211128_1_.run();
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.scalef((float)p_211128_2_, (float)p_211128_2_, (float)p_211128_2_);
      float f1 = (float)(Util.milliTime() % 4873L) / 4873.0F / (float)p_211128_2_;
      GlStateManager.translatef(-f1, 0.0F, 0.0F);
      GlStateManager.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
      p_211128_1_.run();
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.enableLighting();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      p_211128_0_.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
   }

   private void func_175038_a(BufferBuilder p_175038_1_, BakedQuad p_175038_2_) {
      Vec3i vec3i = p_175038_2_.func_178210_d().getDirectionVec();
      p_175038_1_.putNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
   }

   private void func_191969_a(BufferBuilder p_191969_1_, BakedQuad p_191969_2_, int p_191969_3_) {
      p_191969_1_.addVertexData(p_191969_2_.func_178209_a());
      p_191969_1_.putColor4(p_191969_3_);
      this.func_175038_a(p_191969_1_, p_191969_2_);
   }

   private void renderQuads(BufferBuilder p_191970_1_, List<BakedQuad> p_191970_2_, int p_191970_3_, ItemStack p_191970_4_) {
      boolean flag = p_191970_3_ == -1 && !p_191970_4_.isEmpty();
      int i = 0;

      for(int j = p_191970_2_.size(); i < j; ++i) {
         BakedQuad bakedquad = p_191970_2_.get(i);
         int k = p_191970_3_;
         if (flag && bakedquad.func_178212_b()) {
            k = this.itemColors.getColor(p_191970_4_, bakedquad.func_178211_c());
            k = k | -16777216;
         }

         this.func_191969_a(p_191970_1_, bakedquad, k);
      }

   }

   public boolean shouldRenderItemIn3D(ItemStack p_175050_1_) {
      IBakedModel ibakedmodel = this.itemModelMesher.func_178089_a(p_175050_1_);
      return ibakedmodel == null ? false : ibakedmodel.func_177556_c();
   }

   public void func_181564_a(ItemStack p_181564_1_, ItemCameraTransforms.TransformType p_181564_2_) {
      if (!p_181564_1_.isEmpty()) {
         IBakedModel ibakedmodel = this.func_204206_b(p_181564_1_);
         this.func_184394_a(p_181564_1_, ibakedmodel, p_181564_2_, false);
      }
   }

   public IBakedModel func_184393_a(ItemStack p_184393_1_, @Nullable World p_184393_2_, @Nullable EntityLivingBase p_184393_3_) {
      IBakedModel ibakedmodel = this.itemModelMesher.func_178089_a(p_184393_1_);
      Item item = p_184393_1_.getItem();
      return !item.hasCustomProperties() ? ibakedmodel : this.func_204207_a(ibakedmodel, p_184393_1_, p_184393_2_, p_184393_3_);
   }

   public IBakedModel func_204205_b(ItemStack p_204205_1_, World p_204205_2_, EntityLivingBase p_204205_3_) {
      Item item = p_204205_1_.getItem();
      IBakedModel ibakedmodel;
      if (item == Items.TRIDENT) {
         ibakedmodel = this.itemModelMesher.func_178083_a().func_174953_a(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      } else {
         ibakedmodel = this.itemModelMesher.func_178089_a(p_204205_1_);
      }

      return !item.hasCustomProperties() ? ibakedmodel : this.func_204207_a(ibakedmodel, p_204205_1_, p_204205_2_, p_204205_3_);
   }

   public IBakedModel func_204206_b(ItemStack p_204206_1_) {
      return this.func_184393_a(p_204206_1_, (World)null, (EntityLivingBase)null);
   }

   private IBakedModel func_204207_a(IBakedModel p_204207_1_, ItemStack p_204207_2_, @Nullable World p_204207_3_, @Nullable EntityLivingBase p_204207_4_) {
      IBakedModel ibakedmodel = p_204207_1_.func_188617_f().func_209581_a(p_204207_1_, p_204207_2_, p_204207_3_, p_204207_4_);
      return ibakedmodel == null ? this.itemModelMesher.func_178083_a().func_174951_a() : ibakedmodel;
   }

   public void func_184392_a(ItemStack p_184392_1_, EntityLivingBase p_184392_2_, ItemCameraTransforms.TransformType p_184392_3_, boolean p_184392_4_) {
      if (!p_184392_1_.isEmpty() && p_184392_2_ != null) {
         IBakedModel ibakedmodel = this.func_204205_b(p_184392_1_, p_184392_2_.world, p_184392_2_);
         this.func_184394_a(p_184392_1_, ibakedmodel, p_184392_3_, p_184392_4_);
      }
   }

   protected void func_184394_a(ItemStack p_184394_1_, IBakedModel p_184394_2_, ItemCameraTransforms.TransformType p_184394_3_, boolean p_184394_4_) {
      if (!p_184394_1_.isEmpty()) {
         this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableRescaleNormal();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.pushMatrix();
         ItemCameraTransforms itemcameratransforms = p_184394_2_.func_177552_f();
         ItemCameraTransforms.func_188034_a(itemcameratransforms.func_181688_b(p_184394_3_), p_184394_4_);
         if (this.func_183005_a(itemcameratransforms.func_181688_b(p_184394_3_))) {
            GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
         }

         this.func_180454_a(p_184394_1_, p_184394_2_);
         GlStateManager.cullFace(GlStateManager.CullFace.BACK);
         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
         GlStateManager.disableBlend();
         this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
      }
   }

   private boolean func_183005_a(ItemTransformVec3f p_183005_1_) {
      return p_183005_1_.field_178363_d.getX() < 0.0F ^ p_183005_1_.field_178363_d.getY() < 0.0F ^ p_183005_1_.field_178363_d.getZ() < 0.0F;
   }

   public void renderItemIntoGUI(ItemStack p_175042_1_, int p_175042_2_, int p_175042_3_) {
      this.func_191962_a(p_175042_1_, p_175042_2_, p_175042_3_, this.func_204206_b(p_175042_1_));
   }

   protected void func_191962_a(ItemStack p_191962_1_, int p_191962_2_, int p_191962_3_, IBakedModel p_191962_4_) {
      GlStateManager.pushMatrix();
      this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.setupGuiTransform(p_191962_2_, p_191962_3_, p_191962_4_.func_177556_c());
      p_191962_4_.func_177552_f().func_181689_a(ItemCameraTransforms.TransformType.GUI);
      this.func_180454_a(p_191962_1_, p_191962_4_);
      GlStateManager.disableAlphaTest();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableLighting();
      GlStateManager.popMatrix();
      this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
   }

   private void setupGuiTransform(int p_180452_1_, int p_180452_2_, boolean p_180452_3_) {
      GlStateManager.translatef((float)p_180452_1_, (float)p_180452_2_, 100.0F + this.zLevel);
      GlStateManager.translatef(8.0F, 8.0F, 0.0F);
      GlStateManager.scalef(1.0F, -1.0F, 1.0F);
      GlStateManager.scalef(16.0F, 16.0F, 16.0F);
      if (p_180452_3_) {
         GlStateManager.enableLighting();
      } else {
         GlStateManager.disableLighting();
      }

   }

   public void renderItemAndEffectIntoGUI(ItemStack p_180450_1_, int p_180450_2_, int p_180450_3_) {
      this.renderItemAndEffectIntoGUI(Minecraft.getInstance().player, p_180450_1_, p_180450_2_, p_180450_3_);
   }

   public void renderItemAndEffectIntoGUI(@Nullable EntityLivingBase p_184391_1_, ItemStack p_184391_2_, int p_184391_3_, int p_184391_4_) {
      if (!p_184391_2_.isEmpty()) {
         this.zLevel += 50.0F;

         try {
            this.func_191962_a(p_184391_2_, p_184391_3_, p_184391_4_, this.func_184393_a(p_184391_2_, (World)null, p_184391_1_));
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering item");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being rendered");
            crashreportcategory.addDetail("Item Type", () -> {
               return String.valueOf((Object)p_184391_2_.getItem());
            });
            crashreportcategory.addDetail("Item Damage", () -> {
               return String.valueOf(p_184391_2_.getDamage());
            });
            crashreportcategory.addDetail("Item NBT", () -> {
               return String.valueOf((Object)p_184391_2_.getTag());
            });
            crashreportcategory.addDetail("Item Foil", () -> {
               return String.valueOf(p_184391_2_.hasEffect());
            });
            throw new ReportedException(crashreport);
         }

         this.zLevel -= 50.0F;
      }
   }

   public void renderItemOverlays(FontRenderer p_175030_1_, ItemStack p_175030_2_, int p_175030_3_, int p_175030_4_) {
      this.renderItemOverlayIntoGUI(p_175030_1_, p_175030_2_, p_175030_3_, p_175030_4_, (String)null);
   }

   public void renderItemOverlayIntoGUI(FontRenderer p_180453_1_, ItemStack p_180453_2_, int p_180453_3_, int p_180453_4_, @Nullable String p_180453_5_) {
      if (!p_180453_2_.isEmpty()) {
         if (p_180453_2_.getCount() != 1 || p_180453_5_ != null) {
            String s = p_180453_5_ == null ? String.valueOf(p_180453_2_.getCount()) : p_180453_5_;
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableBlend();
            p_180453_1_.drawStringWithShadow(s, (float)(p_180453_3_ + 19 - 2 - p_180453_1_.getStringWidth(s)), (float)(p_180453_4_ + 6 + 3), 16777215);
            GlStateManager.enableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
         }

         if (p_180453_2_.isDamaged()) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlphaTest();
            GlStateManager.disableBlend();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            float f = (float)p_180453_2_.getDamage();
            float f1 = (float)p_180453_2_.getMaxDamage();
            float f2 = Math.max(0.0F, (f1 - f) / f1);
            int i = Math.round(13.0F - f * 13.0F / f1);
            int j = MathHelper.hsvToRGB(f2 / 3.0F, 1.0F, 1.0F);
            this.draw(bufferbuilder, p_180453_3_ + 2, p_180453_4_ + 13, 13, 2, 0, 0, 0, 255);
            this.draw(bufferbuilder, p_180453_3_ + 2, p_180453_4_ + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
            GlStateManager.enableBlend();
            GlStateManager.enableAlphaTest();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
         }

         EntityPlayerSP entityplayersp = Minecraft.getInstance().player;
         float f3 = entityplayersp == null ? 0.0F : entityplayersp.getCooldownTracker().getCooldown(p_180453_2_.getItem(), Minecraft.getInstance().getRenderPartialTicks());
         if (f3 > 0.0F) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableTexture2D();
            Tessellator tessellator1 = Tessellator.getInstance();
            BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
            this.draw(bufferbuilder1, p_180453_3_, p_180453_4_ + MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
         }

      }
   }

   private void draw(BufferBuilder p_181565_1_, int p_181565_2_, int p_181565_3_, int p_181565_4_, int p_181565_5_, int p_181565_6_, int p_181565_7_, int p_181565_8_, int p_181565_9_) {
      p_181565_1_.begin(7, DefaultVertexFormats.POSITION_COLOR);
      p_181565_1_.pos((double)(p_181565_2_ + 0), (double)(p_181565_3_ + 0), 0.0D).color(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
      p_181565_1_.pos((double)(p_181565_2_ + 0), (double)(p_181565_3_ + p_181565_5_), 0.0D).color(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
      p_181565_1_.pos((double)(p_181565_2_ + p_181565_4_), (double)(p_181565_3_ + p_181565_5_), 0.0D).color(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
      p_181565_1_.pos((double)(p_181565_2_ + p_181565_4_), (double)(p_181565_3_ + 0), 0.0D).color(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
      Tessellator.getInstance().draw();
   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      this.itemModelMesher.rebuildCache();
   }
}
