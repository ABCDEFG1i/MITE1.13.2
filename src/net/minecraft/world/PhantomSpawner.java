package net.minecraft.world;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityPhantom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class PhantomSpawner {
   private int field_203233_a;

   public int spawnMobs(World p_203232_1_, boolean p_203232_2_, boolean p_203232_3_) {
      if (!p_203232_2_) {
         return 0;
      } else {
         Random random = p_203232_1_.rand;
         --this.field_203233_a;
         if (this.field_203233_a > 0) {
            return 0;
         } else {
            this.field_203233_a += (60 + random.nextInt(60)) * 20;
            if (p_203232_1_.getSkylightSubtracted() < 5 && p_203232_1_.dimension.hasSkyLight()) {
               return 0;
            } else {
               int i = 0;

               for(EntityPlayer entityplayer : p_203232_1_.playerEntities) {
                  if (!entityplayer.isSpectator()) {
                     BlockPos blockpos = new BlockPos(entityplayer);
                     if (!p_203232_1_.dimension.hasSkyLight() || blockpos.getY() >= p_203232_1_.getSeaLevel() && p_203232_1_.canSeeSky(blockpos)) {
                        DifficultyInstance difficultyinstance = p_203232_1_.getDifficultyForLocation(blockpos);
                        if (difficultyinstance.isHarderThan(random.nextFloat() * 3.0F)) {
                           StatisticsManagerServer statisticsmanagerserver = ((EntityPlayerMP)entityplayer).getStats();
                           int j = MathHelper.clamp(statisticsmanagerserver.func_77444_a(StatList.CUSTOM.func_199076_b(StatList.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
                           int k = 24000;
                           if (random.nextInt(j) >= 72000) {
                              BlockPos blockpos1 = blockpos.up(20 + random.nextInt(15)).east(-10 + random.nextInt(21)).south(-10 + random.nextInt(21));
                              IBlockState iblockstate = p_203232_1_.getBlockState(blockpos1);
                              IFluidState ifluidstate = p_203232_1_.getFluidState(blockpos1);
                              if (WorldEntitySpawner.func_206851_a(iblockstate, ifluidstate)) {
                                 IEntityLivingData ientitylivingdata = null;
                                 int l = 1 + random.nextInt(difficultyinstance.func_203095_a().getId() + 1);

                                 for(int i1 = 0; i1 < l; ++i1) {
                                    EntityPhantom entityphantom = new EntityPhantom(p_203232_1_);
                                    entityphantom.moveToBlockPosAndAngles(blockpos1, 0.0F, 0.0F);
                                    ientitylivingdata = entityphantom.onInitialSpawn(difficultyinstance, ientitylivingdata, (NBTTagCompound)null);
                                    p_203232_1_.spawnEntity(entityphantom);
                                 }

                                 i += l;
                              }
                           }
                        }
                     }
                  }
               }

               return i;
            }
         }
      }
   }
}
