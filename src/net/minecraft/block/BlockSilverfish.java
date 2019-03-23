package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSilverfish extends Block {
   private final Block mimickedBlock;
   private static final Map<Block, Block> field_196470_b = Maps.newIdentityHashMap();

   public BlockSilverfish(Block p_i48374_1_, Block.Properties p_i48374_2_) {
      super(p_i48374_2_);
      this.mimickedBlock = p_i48374_1_;
      field_196470_b.put(p_i48374_1_, this);
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return 0;
   }

   public Block getMimickedBlock() {
      return this.mimickedBlock;
   }

   public static boolean canContainSilverfish(IBlockState p_196466_0_) {
      return field_196470_b.containsKey(p_196466_0_.getBlock());
   }

   protected ItemStack getSilkTouchDrop(IBlockState p_180643_1_) {
      return new ItemStack(this.mimickedBlock);
   }

   public void dropBlockAsItemWithChance(IBlockState p_196255_1_, World p_196255_2_, BlockPos p_196255_3_, float p_196255_4_, int p_196255_5_) {
      if (!p_196255_2_.isRemote && p_196255_2_.getGameRules().getBoolean("doTileDrops")) {
         EntitySilverfish entitysilverfish = new EntitySilverfish(p_196255_2_);
         entitysilverfish.setLocationAndAngles((double)p_196255_3_.getX() + 0.5D, (double)p_196255_3_.getY(), (double)p_196255_3_.getZ() + 0.5D, 0.0F, 0.0F);
         p_196255_2_.spawnEntity(entitysilverfish);
         entitysilverfish.spawnExplosionParticle();
      }

   }

   public static IBlockState infest(Block p_196467_0_) {
      return field_196470_b.get(p_196467_0_).getDefaultState();
   }
}
