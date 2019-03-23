package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.init.Particles;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleManager {
   private static final ResourceLocation PARTICLE_TEXTURES = new ResourceLocation("textures/particle/particles.png");
   protected World world;
   private final ArrayDeque<Particle>[][] fxLayers = new ArrayDeque[4][];
   private final Queue<ParticleEmitter> particleEmitters = Queues.newArrayDeque();
   private final TextureManager renderer;
   private final Random rand = new Random();
   private final Int2ObjectMap<IParticleFactory<?>> factories = new Int2ObjectOpenHashMap<>();
   private final Queue<Particle> queue = Queues.newArrayDeque();

   public ParticleManager(World p_i1220_1_, TextureManager p_i1220_2_) {
      this.world = p_i1220_1_;
      this.renderer = p_i1220_2_;

      for(int i = 0; i < 4; ++i) {
         this.fxLayers[i] = new ArrayDeque[2];

         for(int j = 0; j < 2; ++j) {
            this.fxLayers[i][j] = Queues.newArrayDeque();
         }
      }

      this.registerFactories();
   }

   private void registerFactories() {
      this.registerFactory(Particles.AMBIENT_ENTITY_EFFECT, new ParticleSpell.AmbientMobFactory());
      this.registerFactory(Particles.ANGRY_VILLAGER, new ParticleHeart.AngryVillagerFactory());
      this.registerFactory(Particles.BARRIER, new Barrier.Factory());
      this.registerFactory(Particles.BLOCK, new ParticleDigging.Factory());
      this.registerFactory(Particles.BUBBLE, new ParticleBubble.Factory());
      this.registerFactory(Particles.BUBBLE_COLUMN_UP, new ParticleBubbleColumnUp.Factory());
      this.registerFactory(Particles.BUBBLE_POP, new ParticleBubblePop.Factory());
      this.registerFactory(Particles.CLOUD, new ParticleCloud.Factory());
      this.registerFactory(Particles.CRIT, new ParticleCrit.Factory());
      this.registerFactory(Particles.CURRENT_DOWN, new ParticleCurrentDown.Factory());
      this.registerFactory(Particles.DAMAGE_INDICATOR, new ParticleCrit.DamageIndicatorFactory());
      this.registerFactory(Particles.DRAGON_BREATH, new ParticleDragonBreath.Factory());
      this.registerFactory(Particles.DOLPHIN, new ParticleSuspendedTown.DolphinSpeedFactory());
      this.registerFactory(Particles.DRIPPING_LAVA, new ParticleDrip.LavaFactory());
      this.registerFactory(Particles.DRIPPING_WATER, new ParticleDrip.WaterFactory());
      this.registerFactory(Particles.DUST, new ParticleRedstone.Factory());
      this.registerFactory(Particles.EFFECT, new ParticleSpell.Factory());
      this.registerFactory(Particles.ELDER_GUARDIAN, new ParticleMobAppearance.Factory());
      this.registerFactory(Particles.ENCHANTED_HIT, new ParticleCrit.MagicFactory());
      this.registerFactory(Particles.ENCHANT, new ParticleEnchantmentTable.EnchantmentTable());
      this.registerFactory(Particles.END_ROD, new ParticleEndRod.Factory());
      this.registerFactory(Particles.ENTITY_EFFECT, new ParticleSpell.MobFactory());
      this.registerFactory(Particles.EXPLOSION_EMITTER, new ParticleExplosionHuge.Factory());
      this.registerFactory(Particles.EXPLOSION, new ParticleExplosionLarge.Factory());
      this.registerFactory(Particles.FALLING_DUST, new ParticleFallingDust.Factory());
      this.registerFactory(Particles.FIREWORK, new ParticleFirework.Factory());
      this.registerFactory(Particles.FISHING, new ParticleWaterWake.Factory());
      this.registerFactory(Particles.FLAME, new ParticleFlame.Factory());
      this.registerFactory(Particles.HAPPY_VILLAGER, new ParticleSuspendedTown.HappyVillagerFactory());
      this.registerFactory(Particles.HEART, new ParticleHeart.Factory());
      this.registerFactory(Particles.INSTANT_EFFECT, new ParticleSpell.InstantFactory());
      this.registerFactory(Particles.ITEM, new ParticleBreaking.Factory());
      this.registerFactory(Particles.ITEM_SLIME, new ParticleBreaking.SlimeFactory());
      this.registerFactory(Particles.ITEM_SNOWBALL, new ParticleBreaking.SnowballFactory());
      this.registerFactory(Particles.LARGE_SMOKE, new ParticleSmokeLarge.Factory());
      this.registerFactory(Particles.LAVA, new ParticleLava.Factory());
      this.registerFactory(Particles.MYCELIUM, new ParticleSuspendedTown.Factory());
      this.registerFactory(Particles.NAUTILUS, new ParticleEnchantmentTable.NautilusFactory());
      this.registerFactory(Particles.NOTE, new ParticleNote.Factory());
      this.registerFactory(Particles.POOF, new ParticleExplosion.Factory());
      this.registerFactory(Particles.PORTAL, new ParticlePortal.Factory());
      this.registerFactory(Particles.RAIN, new ParticleRain.Factory());
      this.registerFactory(Particles.SMOKE, new ParticleSmokeNormal.Factory());
      this.registerFactory(Particles.SPIT, new ParticleSpit.Factory());
      this.registerFactory(Particles.SWEEP_ATTACK, new ParticleSweepAttack.Factory());
      this.registerFactory(Particles.TOTEM_OF_UNDYING, new ParticleTotem.Factory());
      this.registerFactory(Particles.SQUID_INK, new ParticleSquidInk.Factory());
      this.registerFactory(Particles.UNDERWATER, new ParticleSuspend.Factory());
      this.registerFactory(Particles.SPLASH, new ParticleSplash.Factory());
      this.registerFactory(Particles.WITCH, new ParticleSpell.WitchFactory());
   }

   public <T extends IParticleData> void registerFactory(ParticleType<T> p_199283_1_, IParticleFactory<T> p_199283_2_) {
      this.factories.put(IRegistry.field_212632_u.func_148757_b(p_199283_1_), p_199283_2_);
   }

   public void addParticleEmitter(Entity p_199282_1_, IParticleData p_199282_2_) {
      this.particleEmitters.add(new ParticleEmitter(this.world, p_199282_1_, p_199282_2_));
   }

   public void func_199281_a(Entity p_199281_1_, IParticleData p_199281_2_, int p_199281_3_) {
      this.particleEmitters.add(new ParticleEmitter(this.world, p_199281_1_, p_199281_2_, p_199281_3_));
   }

   @Nullable
   public Particle addParticle(IParticleData p_199280_1_, double p_199280_2_, double p_199280_4_, double p_199280_6_, double p_199280_8_, double p_199280_10_, double p_199280_12_) {
      Particle particle = this.makeParticle(p_199280_1_, p_199280_2_, p_199280_4_, p_199280_6_, p_199280_8_, p_199280_10_, p_199280_12_);
      if (particle != null) {
         this.addEffect(particle);
         return particle;
      } else {
         return null;
      }
   }

   @Nullable
   private <T extends IParticleData> Particle makeParticle(T p_199927_1_, double p_199927_2_, double p_199927_4_, double p_199927_6_, double p_199927_8_, double p_199927_10_, double p_199927_12_) {
      IParticleFactory<T> iparticlefactory = (IParticleFactory<T>)this.factories.get(IRegistry.field_212632_u.func_148757_b(p_199927_1_.getType()));
      return iparticlefactory == null ? null : iparticlefactory.makeParticle(p_199927_1_, this.world, p_199927_2_, p_199927_4_, p_199927_6_, p_199927_8_, p_199927_10_, p_199927_12_);
   }

   public void addEffect(Particle p_78873_1_) {
      this.queue.add(p_78873_1_);
   }

   public void tick() {
      for(int i = 0; i < 4; ++i) {
         this.updateEffectLayer(i);
      }

      if (!this.particleEmitters.isEmpty()) {
         List<ParticleEmitter> list = Lists.newArrayList();

         for(ParticleEmitter particleemitter : this.particleEmitters) {
            particleemitter.tick();
            if (!particleemitter.isAlive()) {
               list.add(particleemitter);
            }
         }

         this.particleEmitters.removeAll(list);
      }

      if (!this.queue.isEmpty()) {
         for(Particle particle = this.queue.poll(); particle != null; particle = this.queue.poll()) {
            int j = particle.getFXLayer();
            int k = particle.shouldDisableDepth() ? 0 : 1;
            if (this.fxLayers[j][k].size() >= 16384) {
               this.fxLayers[j][k].removeFirst();
            }

            this.fxLayers[j][k].add(particle);
         }
      }

   }

   private void updateEffectLayer(int p_178922_1_) {
      this.world.profiler.startSection(String.valueOf(p_178922_1_));

      for(int i = 0; i < 2; ++i) {
         this.world.profiler.startSection(String.valueOf(i));
         this.tickParticleList(this.fxLayers[p_178922_1_][i]);
         this.world.profiler.endSection();
      }

      this.world.profiler.endSection();
   }

   private void tickParticleList(Queue<Particle> p_187240_1_) {
      if (!p_187240_1_.isEmpty()) {
         Iterator<Particle> iterator = p_187240_1_.iterator();

         while(iterator.hasNext()) {
            Particle particle = iterator.next();
            this.tickParticle(particle);
            if (!particle.isAlive()) {
               iterator.remove();
            }
         }
      }

   }

   private void tickParticle(Particle p_178923_1_) {
      try {
         p_178923_1_.tick();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking Particle");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being ticked");
         int i = p_178923_1_.getFXLayer();
         crashreportcategory.addDetail("Particle", p_178923_1_::toString);
         crashreportcategory.addDetail("Particle Type", () -> {
            if (i == 0) {
               return "MISC_TEXTURE";
            } else if (i == 1) {
               return "TERRAIN_TEXTURE";
            } else {
               return i == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i;
            }
         });
         throw new ReportedException(crashreport);
      }
   }

   public void renderParticles(Entity p_78874_1_, float p_78874_2_) {
      float f = ActiveRenderInfo.getRotationX();
      float f1 = ActiveRenderInfo.getRotationZ();
      float f2 = ActiveRenderInfo.getRotationYZ();
      float f3 = ActiveRenderInfo.getRotationXY();
      float f4 = ActiveRenderInfo.getRotationXZ();
      Particle.interpPosX = p_78874_1_.lastTickPosX + (p_78874_1_.posX - p_78874_1_.lastTickPosX) * (double)p_78874_2_;
      Particle.interpPosY = p_78874_1_.lastTickPosY + (p_78874_1_.posY - p_78874_1_.lastTickPosY) * (double)p_78874_2_;
      Particle.interpPosZ = p_78874_1_.lastTickPosZ + (p_78874_1_.posZ - p_78874_1_.lastTickPosZ) * (double)p_78874_2_;
      Particle.cameraViewDir = p_78874_1_.getLook(p_78874_2_);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.alphaFunc(516, 0.003921569F);

      for(int i_nf = 0; i_nf < 3; ++i_nf) {
         final int i = i_nf;
         for(int j = 0; j < 2; ++j) {
            if (!this.fxLayers[i][j].isEmpty()) {
               switch(j) {
               case 0:
                  GlStateManager.depthMask(false);
                  break;
               case 1:
                  GlStateManager.depthMask(true);
               }

               switch(i) {
               case 0:
               default:
                  this.renderer.bindTexture(PARTICLE_TEXTURES);
                  break;
               case 1:
                  this.renderer.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
               }

               GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               Tessellator tessellator = Tessellator.getInstance();
               BufferBuilder bufferbuilder = tessellator.getBuffer();
               bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

               for(Particle particle : this.fxLayers[i][j]) {
                  try {
                     particle.renderParticle(bufferbuilder, p_78874_1_, p_78874_2_, f, f4, f1, f2, f3);
                  } catch (Throwable throwable) {
                     CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
                     CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
                     crashreportcategory.addDetail("Particle", particle::toString);
                     crashreportcategory.addDetail("Particle Type", () -> {
                        if (i == 0) {
                           return "MISC_TEXTURE";
                        } else if (i == 1) {
                           return "TERRAIN_TEXTURE";
                        } else {
                           return i == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i;
                        }
                     });
                     throw new ReportedException(crashreport);
                  }
               }

               tessellator.draw();
            }
         }
      }

      GlStateManager.depthMask(true);
      GlStateManager.disableBlend();
      GlStateManager.alphaFunc(516, 0.1F);
   }

   public void renderLitParticles(Entity p_78872_1_, float p_78872_2_) {
      float f = ActiveRenderInfo.getRotationX();
      float f1 = ActiveRenderInfo.getRotationZ();
      float f2 = ActiveRenderInfo.getRotationYZ();
      float f3 = ActiveRenderInfo.getRotationXY();
      float f4 = ActiveRenderInfo.getRotationXZ();
      Particle.interpPosX = p_78872_1_.lastTickPosX + (p_78872_1_.posX - p_78872_1_.lastTickPosX) * (double)p_78872_2_;
      Particle.interpPosY = p_78872_1_.lastTickPosY + (p_78872_1_.posY - p_78872_1_.lastTickPosY) * (double)p_78872_2_;
      Particle.interpPosZ = p_78872_1_.lastTickPosZ + (p_78872_1_.posZ - p_78872_1_.lastTickPosZ) * (double)p_78872_2_;
      Particle.cameraViewDir = p_78872_1_.getLook(p_78872_2_);

      for(int i = 0; i < 2; ++i) {
         Queue<Particle> queue = this.fxLayers[3][i];
         if (!queue.isEmpty()) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();

            for(Particle particle : queue) {
               particle.renderParticle(bufferbuilder, p_78872_1_, p_78872_2_, f, f4, f1, f2, f3);
            }
         }
      }

   }

   public void clearEffects(@Nullable World p_78870_1_) {
      this.world = p_78870_1_;

      for(int i = 0; i < 4; ++i) {
         for(int j = 0; j < 2; ++j) {
            this.fxLayers[i][j].clear();
         }
      }

      this.particleEmitters.clear();
   }

   public void addBlockDestroyEffects(BlockPos p_180533_1_, IBlockState p_180533_2_) {
      if (!p_180533_2_.isAir()) {
         VoxelShape voxelshape = p_180533_2_.getShape(this.world, p_180533_1_);
         double d0 = 0.25D;
         voxelshape.func_197755_b((p_199284_3_, p_199284_5_, p_199284_7_, p_199284_9_, p_199284_11_, p_199284_13_) -> {
            double d1 = Math.min(1.0D, p_199284_9_ - p_199284_3_);
            double d2 = Math.min(1.0D, p_199284_11_ - p_199284_5_);
            double d3 = Math.min(1.0D, p_199284_13_ - p_199284_7_);
            int i = Math.max(2, MathHelper.ceil(d1 / 0.25D));
            int j = Math.max(2, MathHelper.ceil(d2 / 0.25D));
            int k = Math.max(2, MathHelper.ceil(d3 / 0.25D));

            for(int l = 0; l < i; ++l) {
               for(int i1 = 0; i1 < j; ++i1) {
                  for(int j1 = 0; j1 < k; ++j1) {
                     double d4 = ((double)l + 0.5D) / (double)i;
                     double d5 = ((double)i1 + 0.5D) / (double)j;
                     double d6 = ((double)j1 + 0.5D) / (double)k;
                     double d7 = d4 * d1 + p_199284_3_;
                     double d8 = d5 * d2 + p_199284_5_;
                     double d9 = d6 * d3 + p_199284_7_;
                     this.addEffect((new ParticleDigging(this.world, (double)p_180533_1_.getX() + d7, (double)p_180533_1_.getY() + d8, (double)p_180533_1_.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, p_180533_2_)).setBlockPos(p_180533_1_));
                  }
               }
            }

         });
      }
   }

   public void addBlockHitEffects(BlockPos p_180532_1_, EnumFacing p_180532_2_) {
      IBlockState iblockstate = this.world.getBlockState(p_180532_1_);
      if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
         int i = p_180532_1_.getX();
         int j = p_180532_1_.getY();
         int k = p_180532_1_.getZ();
         float f = 0.1F;
         AxisAlignedBB axisalignedbb = iblockstate.getShape(this.world, p_180532_1_).getBoundingBox();
         double d0 = (double)i + this.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - (double)0.2F) + (double)0.1F + axisalignedbb.minX;
         double d1 = (double)j + this.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - (double)0.2F) + (double)0.1F + axisalignedbb.minY;
         double d2 = (double)k + this.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - (double)0.2F) + (double)0.1F + axisalignedbb.minZ;
         if (p_180532_2_ == EnumFacing.DOWN) {
            d1 = (double)j + axisalignedbb.minY - (double)0.1F;
         }

         if (p_180532_2_ == EnumFacing.UP) {
            d1 = (double)j + axisalignedbb.maxY + (double)0.1F;
         }

         if (p_180532_2_ == EnumFacing.NORTH) {
            d2 = (double)k + axisalignedbb.minZ - (double)0.1F;
         }

         if (p_180532_2_ == EnumFacing.SOUTH) {
            d2 = (double)k + axisalignedbb.maxZ + (double)0.1F;
         }

         if (p_180532_2_ == EnumFacing.WEST) {
            d0 = (double)i + axisalignedbb.minX - (double)0.1F;
         }

         if (p_180532_2_ == EnumFacing.EAST) {
            d0 = (double)i + axisalignedbb.maxX + (double)0.1F;
         }

         this.addEffect((new ParticleDigging(this.world, d0, d1, d2, 0.0D, 0.0D, 0.0D, iblockstate)).setBlockPos(p_180532_1_).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
      }
   }

   public String getStatistics() {
      int i = 0;

      for(int j = 0; j < 4; ++j) {
         for(int k = 0; k < 2; ++k) {
            i += this.fxLayers[j][k].size();
         }
      }

      return String.valueOf(i);
   }
}
