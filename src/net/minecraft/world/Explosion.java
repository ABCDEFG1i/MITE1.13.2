package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Explosion {
   private final boolean causesFire;
   private final boolean damagesTerrain;
   private final Random random = new Random();
   private final World world;
   private final double x;
   private final double y;
   private final double z;
   private final Entity exploder;
   private final float size;
   private DamageSource damageSource;
   private final List<BlockPos> affectedBlockPositions = Lists.newArrayList();
   private final Map<EntityPlayer, Vec3d> playerKnockbackMap = Maps.newHashMap();

   @OnlyIn(Dist.CLIENT)
   public Explosion(World p_i45752_1_, @Nullable Entity p_i45752_2_, double p_i45752_3_, double p_i45752_5_, double p_i45752_7_, float p_i45752_9_, List<BlockPos> p_i45752_10_) {
      this(p_i45752_1_, p_i45752_2_, p_i45752_3_, p_i45752_5_, p_i45752_7_, p_i45752_9_, false, true, p_i45752_10_);
   }

   @OnlyIn(Dist.CLIENT)
   public Explosion(World p_i45753_1_, @Nullable Entity p_i45753_2_, double p_i45753_3_, double p_i45753_5_, double p_i45753_7_, float p_i45753_9_, boolean p_i45753_10_, boolean p_i45753_11_, List<BlockPos> p_i45753_12_) {
      this(p_i45753_1_, p_i45753_2_, p_i45753_3_, p_i45753_5_, p_i45753_7_, p_i45753_9_, p_i45753_10_, p_i45753_11_);
      this.affectedBlockPositions.addAll(p_i45753_12_);
   }

   public Explosion(World p_i45754_1_, @Nullable Entity p_i45754_2_, double p_i45754_3_, double p_i45754_5_, double p_i45754_7_, float p_i45754_9_, boolean p_i45754_10_, boolean p_i45754_11_) {
      this.world = p_i45754_1_;
      this.exploder = p_i45754_2_;
      this.size = p_i45754_9_;
      this.x = p_i45754_3_;
      this.y = p_i45754_5_;
      this.z = p_i45754_7_;
      this.causesFire = p_i45754_10_;
      this.damagesTerrain = p_i45754_11_;
      this.damageSource = DamageSource.causeExplosionDamage(this);
   }

   public void doExplosionA() {
      Set<BlockPos> set = Sets.newHashSet();
      int i = 16;

      for(int j = 0; j < 16; ++j) {
         for(int k = 0; k < 16; ++k) {
            for(int l = 0; l < 16; ++l) {
               if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                  double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
                  double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
                  double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
                  double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                  d0 = d0 / d3;
                  d1 = d1 / d3;
                  d2 = d2 / d3;
                  float f = this.size * (0.7F + this.world.rand.nextFloat() * 0.6F);
                  double d4 = this.x;
                  double d6 = this.y;
                  double d8 = this.z;

                  for(float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                     BlockPos blockpos = new BlockPos(d4, d6, d8);
                     IBlockState iblockstate = this.world.getBlockState(blockpos);
                     IFluidState ifluidstate = this.world.getFluidState(blockpos);
                     if (!iblockstate.isAir() || !ifluidstate.isEmpty()) {
                        float f2 = Math.max(iblockstate.getBlock().getExplosionResistance(), ifluidstate.getExplosionResistance());
                        if (this.exploder != null) {
                           f2 = this.exploder.getExplosionResistance(this, this.world, blockpos, iblockstate, ifluidstate, f2);
                        }

                        f -= (f2 + 0.3F) * 0.3F;
                     }

                     if (f > 0.0F && (this.exploder == null || this.exploder.canExplosionDestroyBlock(this, this.world, blockpos, iblockstate, f))) {
                        set.add(blockpos);
                     }

                     d4 += d0 * (double)0.3F;
                     d6 += d1 * (double)0.3F;
                     d8 += d2 * (double)0.3F;
                  }
               }
            }
         }
      }

      this.affectedBlockPositions.addAll(set);
      float f3 = this.size * 2.0F;
      int k1 = MathHelper.floor(this.x - (double)f3 - 1.0D);
      int l1 = MathHelper.floor(this.x + (double)f3 + 1.0D);
      int i2 = MathHelper.floor(this.y - (double)f3 - 1.0D);
      int i1 = MathHelper.floor(this.y + (double)f3 + 1.0D);
      int j2 = MathHelper.floor(this.z - (double)f3 - 1.0D);
      int j1 = MathHelper.floor(this.z + (double)f3 + 1.0D);
      List<Entity> list = this.world.func_72839_b(this.exploder, new AxisAlignedBB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
      Vec3d vec3d = new Vec3d(this.x, this.y, this.z);

      for(int k2 = 0; k2 < list.size(); ++k2) {
         Entity entity = list.get(k2);
         if (!entity.isImmuneToExplosions()) {
            double d12 = entity.getDistance(this.x, this.y, this.z) / (double)f3;
            if (d12 <= 1.0D) {
               double d5 = entity.posX - this.x;
               double d7 = entity.posY + (double)entity.getEyeHeight() - this.y;
               double d9 = entity.posZ - this.z;
               double d13 = (double)MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
               if (d13 != 0.0D) {
                  d5 = d5 / d13;
                  d7 = d7 / d13;
                  d9 = d9 / d13;
                  double d14 = (double)this.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
                  double d10 = (1.0D - d12) * d14;
                  entity.attackEntityFrom(this.getDamageSource(), (float)((int)((d10 * d10 + d10) / 2.0D * 7.0D * (double)f3 + 1.0D)));
                  double d11 = d10;
                  if (entity instanceof EntityLivingBase) {
                     d11 = EnchantmentProtection.getBlastDamageReduction((EntityLivingBase)entity, d10);
                  }

                  entity.motionX += d5 * d11;
                  entity.motionY += d7 * d11;
                  entity.motionZ += d9 * d11;
                  if (entity instanceof EntityPlayer) {
                     EntityPlayer entityplayer = (EntityPlayer)entity;
                     if (!entityplayer.isSpectator() && (!entityplayer.isCreative() || !entityplayer.capabilities.isFlying)) {
                        this.playerKnockbackMap.put(entityplayer, new Vec3d(d5 * d10, d7 * d10, d9 * d10));
                     }
                  }
               }
            }
         }
      }

   }

   public void doExplosionB(boolean p_77279_1_) {
      this.world.playSound((EntityPlayer)null, this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
      if (!(this.size < 2.0F) && this.damagesTerrain) {
         this.world.spawnParticle(Particles.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
      } else {
         this.world.spawnParticle(Particles.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
      }

      if (this.damagesTerrain) {
         for(BlockPos blockpos : this.affectedBlockPositions) {
            IBlockState iblockstate = this.world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();
            if (p_77279_1_) {
               double d0 = (double)((float)blockpos.getX() + this.world.rand.nextFloat());
               double d1 = (double)((float)blockpos.getY() + this.world.rand.nextFloat());
               double d2 = (double)((float)blockpos.getZ() + this.world.rand.nextFloat());
               double d3 = d0 - this.x;
               double d4 = d1 - this.y;
               double d5 = d2 - this.z;
               double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
               d3 = d3 / d6;
               d4 = d4 / d6;
               d5 = d5 / d6;
               double d7 = 0.5D / (d6 / (double)this.size + 0.1D);
               d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
               d3 = d3 * d7;
               d4 = d4 * d7;
               d5 = d5 * d7;
               this.world.spawnParticle(Particles.POOF, (d0 + this.x) / 2.0D, (d1 + this.y) / 2.0D, (d2 + this.z) / 2.0D, d3, d4, d5);
               this.world.spawnParticle(Particles.SMOKE, d0, d1, d2, d3, d4, d5);
            }

            if (!iblockstate.isAir()) {
               if (block.canDropFromExplosion(this)) {
                  iblockstate.dropBlockAsItemWithChance(this.world, blockpos, 1.0F / this.size, 0);
               }

               this.world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 3);
               block.onExplosionDestroy(this.world, blockpos, this);
            }
         }
      }

      if (this.causesFire) {
         for(BlockPos blockpos1 : this.affectedBlockPositions) {
            if (this.world.getBlockState(blockpos1).isAir() && this.world.getBlockState(blockpos1.down()).isOpaqueCube(this.world, blockpos1.down()) && this.random.nextInt(3) == 0) {
               this.world.setBlockState(blockpos1, Blocks.FIRE.getDefaultState());
            }
         }
      }

   }

   public DamageSource getDamageSource() {
      return this.damageSource;
   }

   public void setDamageSource(DamageSource p_199592_1_) {
      this.damageSource = p_199592_1_;
   }

   public Map<EntityPlayer, Vec3d> getPlayerKnockbackMap() {
      return this.playerKnockbackMap;
   }

   @Nullable
   public EntityLivingBase getExplosivePlacedBy() {
      if (this.exploder == null) {
         return null;
      } else if (this.exploder instanceof EntityTNTPrimed) {
         return ((EntityTNTPrimed)this.exploder).getTntPlacedBy();
      } else {
         return this.exploder instanceof EntityLivingBase ? (EntityLivingBase)this.exploder : null;
      }
   }

   public void clearAffectedBlockPositions() {
      this.affectedBlockPositions.clear();
   }

   public List<BlockPos> getAffectedBlockPositions() {
      return this.affectedBlockPositions;
   }
}
