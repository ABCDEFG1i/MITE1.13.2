package net.minecraft.client.renderer;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.fluid.IFluidState;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockRendererDispatcher implements IResourceManagerReloadListener {
   private final BlockModelShapes blockModelShapes;
   private final BlockModelRenderer blockModelRenderer;
   private final ChestRenderer chestRenderer = new ChestRenderer();
   private final BlockFluidRenderer fluidRenderer;
   private final Random random = new Random();

   public BlockRendererDispatcher(BlockModelShapes p_i46577_1_, BlockColors p_i46577_2_) {
      this.blockModelShapes = p_i46577_1_;
      this.blockModelRenderer = new BlockModelRenderer(p_i46577_2_);
      this.fluidRenderer = new BlockFluidRenderer();
   }

   public BlockModelShapes getBlockModelShapes() {
      return this.blockModelShapes;
   }

   public void renderBlockDamage(IBlockState p_175020_1_, BlockPos p_175020_2_, TextureAtlasSprite p_175020_3_, IWorldReader p_175020_4_) {
      if (p_175020_1_.getRenderType() == EnumBlockRenderType.MODEL) {
         IBakedModel ibakedmodel = this.blockModelShapes.func_178125_b(p_175020_1_);
         long i = p_175020_1_.getPositionRandom(p_175020_2_);
         IBakedModel ibakedmodel1 = (new SimpleBakedModel.Builder(p_175020_1_, ibakedmodel, p_175020_3_, this.random, i)).func_177645_b();
         this.blockModelRenderer.func_199324_a(p_175020_4_, ibakedmodel1, p_175020_1_, p_175020_2_, Tessellator.getInstance().getBuffer(), true, this.random, i);
      }
   }

   public boolean func_195475_a(IBlockState p_195475_1_, BlockPos p_195475_2_, IWorldReader p_195475_3_, BufferBuilder p_195475_4_, Random p_195475_5_) {
      try {
         EnumBlockRenderType enumblockrendertype = p_195475_1_.getRenderType();
         if (enumblockrendertype == EnumBlockRenderType.INVISIBLE) {
            return false;
         } else {
            switch(enumblockrendertype) {
            case MODEL:
               return this.blockModelRenderer.func_199324_a(p_195475_3_, this.func_184389_a(p_195475_1_), p_195475_1_, p_195475_2_, p_195475_4_, true, p_195475_5_, p_195475_1_.getPositionRandom(p_195475_2_));
            case ENTITYBLOCK_ANIMATED:
               return false;
            default:
               return false;
            }
         }
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
         CrashReportCategory.addBlockInfo(crashreportcategory, p_195475_2_, p_195475_1_);
         throw new ReportedException(crashreport);
      }
   }

   public boolean func_205318_a(BlockPos p_205318_1_, IWorldReader p_205318_2_, BufferBuilder p_205318_3_, IFluidState p_205318_4_) {
      try {
         return this.fluidRenderer.render(p_205318_2_, p_205318_1_, p_205318_3_, p_205318_4_);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating liquid in world");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
         CrashReportCategory.addBlockInfo(crashreportcategory, p_205318_1_, null);
         throw new ReportedException(crashreport);
      }
   }

   public BlockModelRenderer getBlockModelRenderer() {
      return this.blockModelRenderer;
   }

   public IBakedModel func_184389_a(IBlockState p_184389_1_) {
      return this.blockModelShapes.func_178125_b(p_184389_1_);
   }

   public void renderBlockBrightness(IBlockState p_175016_1_, float p_175016_2_) {
      EnumBlockRenderType enumblockrendertype = p_175016_1_.getRenderType();
      if (enumblockrendertype != EnumBlockRenderType.INVISIBLE) {
         switch(enumblockrendertype) {
         case MODEL:
            IBakedModel ibakedmodel = this.func_184389_a(p_175016_1_);
            this.blockModelRenderer.func_178266_a(ibakedmodel, p_175016_1_, p_175016_2_, true);
            break;
         case ENTITYBLOCK_ANIMATED:
            this.chestRenderer.renderChestBrightness(p_175016_1_.getBlock(), p_175016_2_);
         }

      }
   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      this.fluidRenderer.initAtlasSprites();
   }
}
