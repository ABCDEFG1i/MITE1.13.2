package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockAbstractSkull;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSkullWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelDragonHead;
import net.minecraft.client.renderer.entity.model.ModelHumanoidHead;
import net.minecraft.client.renderer.entity.model.ModelSkeletonHead;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntitySkullRenderer extends TileEntityRenderer<TileEntitySkull> {
   public static TileEntitySkullRenderer instance;
   private static final Map<BlockSkull.ISkullType, ModelBase> field_199358_e = Util.make(Maps.newHashMap(), (p_209262_0_) -> {
      ModelSkeletonHead modelskeletonhead = new ModelSkeletonHead(0, 0, 64, 32);
      ModelSkeletonHead modelskeletonhead1 = new ModelHumanoidHead();
      ModelDragonHead modeldragonhead = new ModelDragonHead(0.0F);
      p_209262_0_.put(BlockSkull.Types.SKELETON, modelskeletonhead);
      p_209262_0_.put(BlockSkull.Types.WITHER_SKELETON, modelskeletonhead);
      p_209262_0_.put(BlockSkull.Types.PLAYER, modelskeletonhead1);
      p_209262_0_.put(BlockSkull.Types.ZOMBIE, modelskeletonhead1);
      p_209262_0_.put(BlockSkull.Types.CREEPER, modelskeletonhead);
      p_209262_0_.put(BlockSkull.Types.DRAGON, modeldragonhead);
   });
   private static final Map<BlockSkull.ISkullType, ResourceLocation> field_199357_d = Util.make(Maps.newHashMap(), (p_209263_0_) -> {
      p_209263_0_.put(BlockSkull.Types.SKELETON, new ResourceLocation("textures/entity/skeleton/skeleton.png"));
      p_209263_0_.put(BlockSkull.Types.WITHER_SKELETON, new ResourceLocation("textures/entity/skeleton/wither_skeleton.png"));
      p_209263_0_.put(BlockSkull.Types.ZOMBIE, new ResourceLocation("textures/entity/zombie/zombie.png"));
      p_209263_0_.put(BlockSkull.Types.CREEPER, new ResourceLocation("textures/entity/creeper/creeper.png"));
      p_209263_0_.put(BlockSkull.Types.DRAGON, new ResourceLocation("textures/entity/enderdragon/dragon.png"));
      p_209263_0_.put(BlockSkull.Types.PLAYER, DefaultPlayerSkin.getDefaultSkinLegacy());
   });

   public void render(TileEntitySkull p_199341_1_, double p_199341_2_, double p_199341_4_, double p_199341_6_, float p_199341_8_, int p_199341_9_) {
      float f = p_199341_1_.getAnimationProgress(p_199341_8_);
      IBlockState iblockstate = p_199341_1_.getBlockState();
      boolean flag = iblockstate.getBlock() instanceof BlockSkullWall;
      EnumFacing enumfacing = flag ? iblockstate.get(BlockSkullWall.FACING) : null;
      float f1 = 22.5F * (float)(flag ? (2 + enumfacing.getHorizontalIndex()) * 4 : iblockstate.get(BlockSkull.ROTATION));
      this.render((float)p_199341_2_, (float)p_199341_4_, (float)p_199341_6_, enumfacing, f1, ((BlockAbstractSkull)iblockstate.getBlock()).getSkullType(), p_199341_1_.getPlayerProfile(), p_199341_9_, f);
   }

   public void setRendererDispatcher(TileEntityRendererDispatcher p_147497_1_) {
      super.setRendererDispatcher(p_147497_1_);
      instance = this;
   }

   public void render(float p_199355_1_, float p_199355_2_, float p_199355_3_, @Nullable EnumFacing p_199355_4_, float p_199355_5_, BlockSkull.ISkullType p_199355_6_, @Nullable GameProfile p_199355_7_, int p_199355_8_, float p_199355_9_) {
      ModelBase modelbase = field_199358_e.get(p_199355_6_);
      if (p_199355_8_ >= 0) {
         this.bindTexture(DESTROY_STAGES[p_199355_8_]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(4.0F, 2.0F, 1.0F);
         GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         this.bindTexture(this.func_199356_a(p_199355_6_, p_199355_7_));
      }

      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      if (p_199355_4_ == null) {
         GlStateManager.translatef(p_199355_1_ + 0.5F, p_199355_2_, p_199355_3_ + 0.5F);
      } else {
         switch(p_199355_4_) {
         case NORTH:
            GlStateManager.translatef(p_199355_1_ + 0.5F, p_199355_2_ + 0.25F, p_199355_3_ + 0.74F);
            break;
         case SOUTH:
            GlStateManager.translatef(p_199355_1_ + 0.5F, p_199355_2_ + 0.25F, p_199355_3_ + 0.26F);
            break;
         case WEST:
            GlStateManager.translatef(p_199355_1_ + 0.74F, p_199355_2_ + 0.25F, p_199355_3_ + 0.5F);
            break;
         case EAST:
         default:
            GlStateManager.translatef(p_199355_1_ + 0.26F, p_199355_2_ + 0.25F, p_199355_3_ + 0.5F);
         }
      }

      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlphaTest();
      if (p_199355_6_ == BlockSkull.Types.PLAYER) {
         GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
      }

      modelbase.render(null, p_199355_9_, 0.0F, 0.0F, p_199355_5_, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      if (p_199355_8_ >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

   }

   private ResourceLocation func_199356_a(BlockSkull.ISkullType p_199356_1_, @Nullable GameProfile p_199356_2_) {
      ResourceLocation resourcelocation = field_199357_d.get(p_199356_1_);
      if (p_199356_1_ == BlockSkull.Types.PLAYER && p_199356_2_ != null) {
         Minecraft minecraft = Minecraft.getInstance();
         Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(p_199356_2_);
         if (map.containsKey(Type.SKIN)) {
            resourcelocation = minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN);
         } else {
            resourcelocation = DefaultPlayerSkin.getDefaultSkin(EntityPlayer.getUUID(p_199356_2_));
         }
      }

      return resourcelocation;
   }
}
