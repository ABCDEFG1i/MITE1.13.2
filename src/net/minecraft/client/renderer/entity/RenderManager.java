package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityDrowned;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPhantom;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCod;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityDolphin;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityPufferFish;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySalmon;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityTropicalFish;
import net.minecraft.entity.passive.EntityTurtle;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.projectile.EntityTrident;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderManager {
   public final Map<Class<? extends Entity>, Render<? extends Entity>> entityRenderMap = Maps.newHashMap();
   private final Map<String, RenderPlayer> skinMap = Maps.newHashMap();
   private final RenderPlayer playerRenderer;
   private FontRenderer textRenderer;
   private double renderPosX;
   private double renderPosY;
   private double renderPosZ;
   public TextureManager textureManager;
   public World world;
   public Entity renderViewEntity;
   public Entity pointedEntity;
   public float playerViewY;
   public float playerViewX;
   public GameSettings options;
   public double viewerPosX;
   public double viewerPosY;
   public double viewerPosZ;
   private boolean renderOutlines;
   private boolean renderShadow = true;
   private boolean debugBoundingBox;

   public RenderManager(TextureManager p_i46180_1_, ItemRenderer p_i46180_2_) {
      this.textureManager = p_i46180_1_;
      this.entityRenderMap.put(EntityCaveSpider.class, new RenderCaveSpider(this));
      this.entityRenderMap.put(EntitySpider.class, new RenderSpider(this));
      this.entityRenderMap.put(EntityPig.class, new RenderPig(this));
      this.entityRenderMap.put(EntitySheep.class, new RenderSheep(this));
      this.entityRenderMap.put(EntityCow.class, new RenderCow(this));
      this.entityRenderMap.put(EntityMooshroom.class, new RenderMooshroom(this));
      this.entityRenderMap.put(EntityWolf.class, new RenderWolf(this));
      this.entityRenderMap.put(EntityChicken.class, new RenderChicken(this));
      this.entityRenderMap.put(EntityOcelot.class, new RenderOcelot(this));
      this.entityRenderMap.put(EntityRabbit.class, new RenderRabbit(this));
      this.entityRenderMap.put(EntityParrot.class, new RenderParrot(this));
      this.entityRenderMap.put(EntityTurtle.class, new RenderTurtle(this));
      this.entityRenderMap.put(EntitySilverfish.class, new RenderSilverfish(this));
      this.entityRenderMap.put(EntityEndermite.class, new RenderEndermite(this));
      this.entityRenderMap.put(EntityCreeper.class, new RenderCreeper(this));
      this.entityRenderMap.put(EntityEnderman.class, new RenderEnderman(this));
      this.entityRenderMap.put(EntitySnowman.class, new RenderSnowMan(this));
      this.entityRenderMap.put(EntitySkeleton.class, new RenderSkeleton(this));
      this.entityRenderMap.put(EntityWitherSkeleton.class, new RenderWitherSkeleton(this));
      this.entityRenderMap.put(EntityStray.class, new RenderStray(this));
      this.entityRenderMap.put(EntityWitch.class, new RenderWitch(this));
      this.entityRenderMap.put(EntityBlaze.class, new RenderBlaze(this));
      this.entityRenderMap.put(EntityPigZombie.class, new RenderPigZombie(this));
      this.entityRenderMap.put(EntityZombie.class, new RenderZombie(this));
      this.entityRenderMap.put(EntityZombieVillager.class, new RenderZombieVillager(this));
      this.entityRenderMap.put(EntityHusk.class, new RenderHusk(this));
      this.entityRenderMap.put(EntityDrowned.class, new RenderDrowned(this));
      this.entityRenderMap.put(EntitySlime.class, new RenderSlime(this));
      this.entityRenderMap.put(EntityMagmaCube.class, new RenderMagmaCube(this));
      this.entityRenderMap.put(EntityGiantZombie.class, new RenderGiantZombie(this, 6.0F));
      this.entityRenderMap.put(EntityGhast.class, new RenderGhast(this));
      this.entityRenderMap.put(EntitySquid.class, new RenderSquid(this));
      this.entityRenderMap.put(EntityVillager.class, new RenderVillager(this));
      this.entityRenderMap.put(EntityIronGolem.class, new RenderIronGolem(this));
      this.entityRenderMap.put(EntityBat.class, new RenderBat(this));
      this.entityRenderMap.put(EntityGuardian.class, new RenderGuardian(this));
      this.entityRenderMap.put(EntityElderGuardian.class, new RenderElderGuardian(this));
      this.entityRenderMap.put(EntityShulker.class, new RenderShulker(this));
      this.entityRenderMap.put(EntityPolarBear.class, new RenderPolarBear(this));
      this.entityRenderMap.put(EntityEvoker.class, new RenderEvoker(this));
      this.entityRenderMap.put(EntityVindicator.class, new RenderVindicator(this));
      this.entityRenderMap.put(EntityVex.class, new RenderVex(this));
      this.entityRenderMap.put(EntityIllusionIllager.class, new RenderIllusionIllager(this));
      this.entityRenderMap.put(EntityPhantom.class, new RenderPhantom(this));
      this.entityRenderMap.put(EntityPufferFish.class, new RenderPufferFish(this));
      this.entityRenderMap.put(EntitySalmon.class, new RenderSalmon(this));
      this.entityRenderMap.put(EntityCod.class, new RenderCod(this));
      this.entityRenderMap.put(EntityTropicalFish.class, new RenderTropicalFish(this));
      this.entityRenderMap.put(EntityDolphin.class, new RenderDolphin(this));
      this.entityRenderMap.put(EntityDragon.class, new RenderDragon(this));
      this.entityRenderMap.put(EntityEnderCrystal.class, new RenderEnderCrystal(this));
      this.entityRenderMap.put(EntityWither.class, new RenderWither(this));
      this.entityRenderMap.put(Entity.class, new RenderEntity(this));
      this.entityRenderMap.put(EntityPainting.class, new RenderPainting(this));
      this.entityRenderMap.put(EntityItemFrame.class, new RenderItemFrame(this, p_i46180_2_));
      this.entityRenderMap.put(EntityLeashKnot.class, new RenderLeashKnot(this));
      this.entityRenderMap.put(EntityTippedArrow.class, new RenderTippedArrow(this));
      this.entityRenderMap.put(EntitySpectralArrow.class, new RenderSpectralArrow(this));
      this.entityRenderMap.put(EntityTrident.class, new RenderTrident(this));
      this.entityRenderMap.put(EntitySnowball.class, new RenderSprite<>(this, Items.SNOWBALL, p_i46180_2_));
      this.entityRenderMap.put(EntityEnderPearl.class, new RenderSprite<>(this, Items.ENDER_PEARL, p_i46180_2_));
      this.entityRenderMap.put(EntityEnderEye.class, new RenderSprite<>(this, Items.ENDER_EYE, p_i46180_2_));
      this.entityRenderMap.put(EntityEgg.class, new RenderSprite<>(this, Items.EGG, p_i46180_2_));
      this.entityRenderMap.put(EntityPotion.class, new RenderPotion(this, p_i46180_2_));
      this.entityRenderMap.put(EntityExpBottle.class, new RenderSprite<>(this, Items.EXPERIENCE_BOTTLE, p_i46180_2_));
      this.entityRenderMap.put(EntityFireworkRocket.class, new RenderSprite<>(this, Items.FIREWORK_ROCKET, p_i46180_2_));
      this.entityRenderMap.put(EntityLargeFireball.class, new RenderFireball(this, 2.0F));
      this.entityRenderMap.put(EntitySmallFireball.class, new RenderFireball(this, 0.5F));
      this.entityRenderMap.put(EntityDragonFireball.class, new RenderDragonFireball(this));
      this.entityRenderMap.put(EntityWitherSkull.class, new RenderWitherSkull(this));
      this.entityRenderMap.put(EntityShulkerBullet.class, new RenderShulkerBullet(this));
      this.entityRenderMap.put(EntityItem.class, new RenderEntityItem(this, p_i46180_2_));
      this.entityRenderMap.put(EntityXPOrb.class, new RenderXPOrb(this));
      this.entityRenderMap.put(EntityTNTPrimed.class, new RenderTNTPrimed(this));
      this.entityRenderMap.put(EntityFallingBlock.class, new RenderFallingBlock(this));
      this.entityRenderMap.put(EntityArmorStand.class, new RenderArmorStand(this));
      this.entityRenderMap.put(EntityEvokerFangs.class, new RenderEvokerFangs(this));
      this.entityRenderMap.put(EntityMinecartTNT.class, new RenderTntMinecart(this));
      this.entityRenderMap.put(EntityMinecartMobSpawner.class, new RenderMinecartMobSpawner(this));
      this.entityRenderMap.put(EntityMinecart.class, new RenderMinecart(this));
      this.entityRenderMap.put(EntityBoat.class, new RenderBoat(this));
      this.entityRenderMap.put(EntityFishHook.class, new RenderFish(this));
      this.entityRenderMap.put(EntityAreaEffectCloud.class, new RenderAreaEffectCloud(this));
      this.entityRenderMap.put(EntityHorse.class, new RenderHorse(this));
      this.entityRenderMap.put(EntitySkeletonHorse.class, new RenderHorseUndead(this));
      this.entityRenderMap.put(EntityZombieHorse.class, new RenderHorseUndead(this));
      this.entityRenderMap.put(EntityMule.class, new RenderHorseChest(this, 0.92F));
      this.entityRenderMap.put(EntityDonkey.class, new RenderHorseChest(this, 0.87F));
      this.entityRenderMap.put(EntityLlama.class, new RenderLlama(this));
      this.entityRenderMap.put(EntityLlamaSpit.class, new RenderLlamaSpit(this));
      this.entityRenderMap.put(EntityLightningBolt.class, new RenderLightningBolt(this));
      this.playerRenderer = new RenderPlayer(this);
      this.skinMap.put("default", this.playerRenderer);
      this.skinMap.put("slim", new RenderPlayer(this, true));
   }

   public void setRenderPosition(double p_178628_1_, double p_178628_3_, double p_178628_5_) {
      this.renderPosX = p_178628_1_;
      this.renderPosY = p_178628_3_;
      this.renderPosZ = p_178628_5_;
   }

   public <T extends Entity> Render<T> getEntityClassRenderObject(Class<? extends Entity> p_78715_1_) {
      Render<T> render = (Render<T>)this.entityRenderMap.get(p_78715_1_);
      if (render == null && p_78715_1_ != Entity.class) {
         render = this.getEntityClassRenderObject((Class<? extends Entity>)p_78715_1_.getSuperclass());
         this.entityRenderMap.put(p_78715_1_, render);
      }

      return render;
   }

   @Nullable
   public <T extends Entity> Render<T> getEntityRenderObject(Entity p_78713_1_) {
      if (p_78713_1_ instanceof AbstractClientPlayer) {
         String s = ((AbstractClientPlayer)p_78713_1_).getSkinType();
         RenderPlayer renderplayer = this.skinMap.get(s);
         return (Render<T>)(renderplayer != null ? renderplayer : this.playerRenderer);
      } else {
         return this.getEntityClassRenderObject(p_78713_1_.getClass());
      }
   }

   public void cacheActiveRenderInfo(World p_180597_1_, FontRenderer p_180597_2_, Entity p_180597_3_, Entity p_180597_4_, GameSettings p_180597_5_, float p_180597_6_) {
      this.world = p_180597_1_;
      this.options = p_180597_5_;
      this.renderViewEntity = p_180597_3_;
      this.pointedEntity = p_180597_4_;
      this.textRenderer = p_180597_2_;
      if (p_180597_3_ instanceof EntityLivingBase && ((EntityLivingBase)p_180597_3_).isPlayerSleeping()) {
         IBlockState iblockstate = p_180597_1_.getBlockState(new BlockPos(p_180597_3_));
         Block block = iblockstate.getBlock();
         if (block instanceof BlockBed) {
            int i = iblockstate.get(BlockBed.HORIZONTAL_FACING).getHorizontalIndex();
            this.playerViewY = (float)(i * 90 + 180);
            this.playerViewX = 0.0F;
         }
      } else {
         this.playerViewY = p_180597_3_.prevRotationYaw + (p_180597_3_.rotationYaw - p_180597_3_.prevRotationYaw) * p_180597_6_;
         this.playerViewX = p_180597_3_.prevRotationPitch + (p_180597_3_.rotationPitch - p_180597_3_.prevRotationPitch) * p_180597_6_;
      }

      if (p_180597_5_.thirdPersonView == 2) {
         this.playerViewY += 180.0F;
      }

      this.viewerPosX = p_180597_3_.lastTickPosX + (p_180597_3_.posX - p_180597_3_.lastTickPosX) * (double)p_180597_6_;
      this.viewerPosY = p_180597_3_.lastTickPosY + (p_180597_3_.posY - p_180597_3_.lastTickPosY) * (double)p_180597_6_;
      this.viewerPosZ = p_180597_3_.lastTickPosZ + (p_180597_3_.posZ - p_180597_3_.lastTickPosZ) * (double)p_180597_6_;
   }

   public void setPlayerViewY(float p_178631_1_) {
      this.playerViewY = p_178631_1_;
   }

   public boolean isRenderShadow() {
      return this.renderShadow;
   }

   public void setRenderShadow(boolean p_178633_1_) {
      this.renderShadow = p_178633_1_;
   }

   public void setDebugBoundingBox(boolean p_178629_1_) {
      this.debugBoundingBox = p_178629_1_;
   }

   public boolean isDebugBoundingBox() {
      return this.debugBoundingBox;
   }

   public boolean isRenderMultipass(Entity p_188390_1_) {
      return this.getEntityRenderObject(p_188390_1_).isMultipass();
   }

   public boolean shouldRender(Entity p_178635_1_, ICamera p_178635_2_, double p_178635_3_, double p_178635_5_, double p_178635_7_) {
      Render<Entity> render = this.getEntityRenderObject(p_178635_1_);
      return render != null && render.shouldRender(p_178635_1_, p_178635_2_, p_178635_3_, p_178635_5_, p_178635_7_);
   }

   public void renderEntityStatic(Entity p_188388_1_, float p_188388_2_, boolean p_188388_3_) {
      if (p_188388_1_.ticksExisted == 0) {
         p_188388_1_.lastTickPosX = p_188388_1_.posX;
         p_188388_1_.lastTickPosY = p_188388_1_.posY;
         p_188388_1_.lastTickPosZ = p_188388_1_.posZ;
      }

      double d0 = p_188388_1_.lastTickPosX + (p_188388_1_.posX - p_188388_1_.lastTickPosX) * (double)p_188388_2_;
      double d1 = p_188388_1_.lastTickPosY + (p_188388_1_.posY - p_188388_1_.lastTickPosY) * (double)p_188388_2_;
      double d2 = p_188388_1_.lastTickPosZ + (p_188388_1_.posZ - p_188388_1_.lastTickPosZ) * (double)p_188388_2_;
      float f = p_188388_1_.prevRotationYaw + (p_188388_1_.rotationYaw - p_188388_1_.prevRotationYaw) * p_188388_2_;
      int i = p_188388_1_.getBrightnessForRender();
      if (p_188388_1_.isBurning()) {
         i = 15728880;
      }

      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, (float)j, (float)k);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.renderEntity(p_188388_1_, d0 - this.renderPosX, d1 - this.renderPosY, d2 - this.renderPosZ, f, p_188388_2_, p_188388_3_);
   }

   public void renderEntity(Entity p_188391_1_, double p_188391_2_, double p_188391_4_, double p_188391_6_, float p_188391_8_, float p_188391_9_, boolean p_188391_10_) {
      Render<Entity> render = null;

      try {
         render = this.getEntityRenderObject(p_188391_1_);
         if (render != null && this.textureManager != null) {
            try {
               render.setRenderOutlines(this.renderOutlines);
               render.doRender(p_188391_1_, p_188391_2_, p_188391_4_, p_188391_6_, p_188391_8_, p_188391_9_);
            } catch (Throwable throwable1) {
               throw new ReportedException(CrashReport.makeCrashReport(throwable1, "Rendering entity in world"));
            }

            try {
               if (!this.renderOutlines) {
                  render.doRenderShadowAndFire(p_188391_1_, p_188391_2_, p_188391_4_, p_188391_6_, p_188391_8_, p_188391_9_);
               }
            } catch (Throwable throwable2) {
               throw new ReportedException(CrashReport.makeCrashReport(throwable2, "Post-rendering entity in world"));
            }

            if (this.debugBoundingBox && !p_188391_1_.isInvisible() && !p_188391_10_ && !Minecraft.getInstance().isReducedDebug()) {
               try {
                  this.renderDebugBoundingBox(p_188391_1_, p_188391_2_, p_188391_4_, p_188391_6_, p_188391_8_, p_188391_9_);
               } catch (Throwable throwable) {
                  throw new ReportedException(CrashReport.makeCrashReport(throwable, "Rendering entity hitbox in world"));
               }
            }
         }

      } catch (Throwable throwable3) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable3, "Rendering entity in world");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being rendered");
         p_188391_1_.fillCrashReport(crashreportcategory);
         CrashReportCategory crashreportcategory1 = crashreport.makeCategory("Renderer details");
         crashreportcategory1.addCrashSection("Assigned renderer", render);
         crashreportcategory1.addCrashSection("Location", CrashReportCategory.getCoordinateInfo(p_188391_2_, p_188391_4_, p_188391_6_));
         crashreportcategory1.addCrashSection("Rotation", p_188391_8_);
         crashreportcategory1.addCrashSection("Delta", p_188391_9_);
         throw new ReportedException(crashreport);
      }
   }

   public void renderMultipass(Entity p_188389_1_, float p_188389_2_) {
      if (p_188389_1_.ticksExisted == 0) {
         p_188389_1_.lastTickPosX = p_188389_1_.posX;
         p_188389_1_.lastTickPosY = p_188389_1_.posY;
         p_188389_1_.lastTickPosZ = p_188389_1_.posZ;
      }

      double d0 = p_188389_1_.lastTickPosX + (p_188389_1_.posX - p_188389_1_.lastTickPosX) * (double)p_188389_2_;
      double d1 = p_188389_1_.lastTickPosY + (p_188389_1_.posY - p_188389_1_.lastTickPosY) * (double)p_188389_2_;
      double d2 = p_188389_1_.lastTickPosZ + (p_188389_1_.posZ - p_188389_1_.lastTickPosZ) * (double)p_188389_2_;
      float f = p_188389_1_.prevRotationYaw + (p_188389_1_.rotationYaw - p_188389_1_.prevRotationYaw) * p_188389_2_;
      int i = p_188389_1_.getBrightnessForRender();
      if (p_188389_1_.isBurning()) {
         i = 15728880;
      }

      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, (float)j, (float)k);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      Render<Entity> render = this.getEntityRenderObject(p_188389_1_);
      if (render != null && this.textureManager != null) {
         render.renderMultipass(p_188389_1_, d0 - this.renderPosX, d1 - this.renderPosY, d2 - this.renderPosZ, f, p_188389_2_);
      }

   }

   private void renderDebugBoundingBox(Entity p_85094_1_, double p_85094_2_, double p_85094_4_, double p_85094_6_, float p_85094_8_, float p_85094_9_) {
      GlStateManager.depthMask(false);
      GlStateManager.disableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.disableCull();
      GlStateManager.disableBlend();
      float f = p_85094_1_.width / 2.0F;
      AxisAlignedBB axisalignedbb = p_85094_1_.getEntityBoundingBox();
      WorldRenderer.func_189694_a(axisalignedbb.minX - p_85094_1_.posX + p_85094_2_, axisalignedbb.minY - p_85094_1_.posY + p_85094_4_, axisalignedbb.minZ - p_85094_1_.posZ + p_85094_6_, axisalignedbb.maxX - p_85094_1_.posX + p_85094_2_, axisalignedbb.maxY - p_85094_1_.posY + p_85094_4_, axisalignedbb.maxZ - p_85094_1_.posZ + p_85094_6_, 1.0F, 1.0F, 1.0F, 1.0F);
      Entity[] aentity = p_85094_1_.getParts();
      if (aentity != null) {
         for(Entity entity : aentity) {
            double d0 = (entity.posX - entity.prevPosX) * (double)p_85094_9_;
            double d1 = (entity.posY - entity.prevPosY) * (double)p_85094_9_;
            double d2 = (entity.posZ - entity.prevPosZ) * (double)p_85094_9_;
            AxisAlignedBB axisalignedbb1 = entity.getEntityBoundingBox();
            WorldRenderer.func_189694_a(axisalignedbb1.minX - this.renderPosX + d0, axisalignedbb1.minY - this.renderPosY + d1, axisalignedbb1.minZ - this.renderPosZ + d2, axisalignedbb1.maxX - this.renderPosX + d0, axisalignedbb1.maxY - this.renderPosY + d1, axisalignedbb1.maxZ - this.renderPosZ + d2, 0.25F, 1.0F, 0.0F, 1.0F);
         }
      }

      if (p_85094_1_ instanceof EntityLivingBase) {
         float f1 = 0.01F;
         WorldRenderer.func_189694_a(p_85094_2_ - (double)f, p_85094_4_ + (double)p_85094_1_.getEyeHeight() - (double)0.01F, p_85094_6_ - (double)f, p_85094_2_ + (double)f, p_85094_4_ + (double)p_85094_1_.getEyeHeight() + (double)0.01F, p_85094_6_ + (double)f, 1.0F, 0.0F, 0.0F, 1.0F);
      }

      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      Vec3d vec3d = p_85094_1_.getLook(p_85094_9_);
      bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos(p_85094_2_, p_85094_4_ + (double)p_85094_1_.getEyeHeight(), p_85094_6_).color(0, 0, 255, 255).endVertex();
      bufferbuilder.pos(p_85094_2_ + vec3d.x * 2.0D, p_85094_4_ + (double)p_85094_1_.getEyeHeight() + vec3d.y * 2.0D, p_85094_6_ + vec3d.z * 2.0D).color(0, 0, 255, 255).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.enableLighting();
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
   }

   public void setWorld(@Nullable World p_78717_1_) {
      this.world = p_78717_1_;
      if (p_78717_1_ == null) {
         this.renderViewEntity = null;
      }

   }

   public double getDistanceToCamera(double p_78714_1_, double p_78714_3_, double p_78714_5_) {
      double d0 = p_78714_1_ - this.viewerPosX;
      double d1 = p_78714_3_ - this.viewerPosY;
      double d2 = p_78714_5_ - this.viewerPosZ;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public FontRenderer getFontRenderer() {
      return this.textRenderer;
   }

   public void setRenderOutlines(boolean p_178632_1_) {
      this.renderOutlines = p_178632_1_;
   }
}
