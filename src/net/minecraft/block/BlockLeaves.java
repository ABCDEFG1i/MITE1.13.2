package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockLeaves extends Block {
   public static final IntegerProperty DISTANCE = BlockStateProperties.DISTANCE_1_7;
   public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
   protected static boolean renderTranslucent;

   public BlockLeaves(Block.Properties p_i48370_1_) {
      super(p_i48370_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(DISTANCE, Integer.valueOf(7)).with(PERSISTENT, Boolean.valueOf(false)));
   }

   public boolean getTickRandomly(IBlockState p_149653_1_) {
      return p_149653_1_.get(DISTANCE) == 7 && !p_149653_1_.get(PERSISTENT);
   }

   public void randomTick(IBlockState p_196265_1_, World p_196265_2_, BlockPos p_196265_3_, Random p_196265_4_) {
      if (!p_196265_1_.get(PERSISTENT) && p_196265_1_.get(DISTANCE) == 7) {
         p_196265_1_.dropBlockAsItem(p_196265_2_, p_196265_3_, 0);
         p_196265_2_.removeBlock(p_196265_3_);
      }

   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      p_196267_2_.setBlockState(p_196267_3_, updateDistance(p_196267_1_, p_196267_2_, p_196267_3_), 3);
   }

   public int getOpacity(IBlockState p_200011_1_, IBlockReader p_200011_2_, BlockPos p_200011_3_) {
      return 1;
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      int i = getDistance(p_196271_3_) + 1;
      if (i != 1 || p_196271_1_.get(DISTANCE) != i) {
         p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      return p_196271_1_;
   }

   private static IBlockState updateDistance(IBlockState p_208493_0_, IWorld p_208493_1_, BlockPos p_208493_2_) {
      int i = 7;

      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(EnumFacing enumfacing : EnumFacing.values()) {
            blockpos$pooledmutableblockpos.setPos(p_208493_2_).move(enumfacing);
            i = Math.min(i, getDistance(p_208493_1_.getBlockState(blockpos$pooledmutableblockpos)) + 1);
            if (i == 1) {
               break;
            }
         }
      }

      return p_208493_0_.with(DISTANCE, Integer.valueOf(i));
   }

   private static int getDistance(IBlockState p_208492_0_) {
      if (BlockTags.LOGS.contains(p_208492_0_.getBlock())) {
         return 0;
      } else {
         return p_208492_0_.getBlock() instanceof BlockLeaves ? p_208492_0_.get(DISTANCE) : 7;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_2_.isRainingAt(p_180655_3_.up()) && !p_180655_2_.getBlockState(p_180655_3_.down()).isTopSolid() && p_180655_4_.nextInt(15) == 1) {
         double d0 = (double)((float)p_180655_3_.getX() + p_180655_4_.nextFloat());
         double d1 = (double)p_180655_3_.getY() - 0.05D;
         double d2 = (double)((float)p_180655_3_.getZ() + p_180655_4_.nextFloat());
         p_180655_2_.spawnParticle(Particles.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
      }

   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return p_196264_2_.nextInt(20) == 0 ? 1 : 0;
   }

   public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      Block block = blockCurrentState.getBlock();
      if (block == Blocks.OAK_LEAVES) {
         return Blocks.OAK_SAPLING;
      } else if (block == Blocks.SPRUCE_LEAVES) {
         return Blocks.SPRUCE_SAPLING;
      } else if (block == Blocks.BIRCH_LEAVES) {
         return Blocks.BIRCH_SAPLING;
      } else if (block == Blocks.JUNGLE_LEAVES) {
         return Blocks.JUNGLE_SAPLING;
      } else if (block == Blocks.ACACIA_LEAVES) {
         return Blocks.ACACIA_SAPLING;
      } else {
         return block == Blocks.DARK_OAK_LEAVES ? Blocks.DARK_OAK_SAPLING : Blocks.OAK_SAPLING;
      }
   }

   public void dropBlockAsItemWithChance(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, float chanceToDrop, int fortuneLevel) {
      if (!worldIn.isRemote) {
         //drop saplings
         int saplingDropChance = this.getSaplingDropChance(blockCurrentState);
         if (fortuneLevel > 0) {
            //Equals 2 * 2^fortuneLevel    8                            4                          2
            //So Fortune ||| will decrease 16 ,Fortune || will decrease 8, Fortune | will decrease 4
            saplingDropChance -= 2 << fortuneLevel;
            if (saplingDropChance < 10) {
               saplingDropChance = 10;
            }
         }

         if (worldIn.rand.nextInt(saplingDropChance) == 0) {
            spawnAsEntity(worldIn, blockAt, new ItemStack(this.getItemDropped(blockCurrentState, worldIn, blockAt, fortuneLevel)));
         }
         //drop sticks
         saplingDropChance = 50;
         if (worldIn.rand.nextInt(saplingDropChance) == 0) {
            spawnAsEntity(worldIn, blockAt, new ItemStack(Items.STICK, 1));
         }


         //drop apples
         saplingDropChance = 200;
         if (fortuneLevel > 0) {
            saplingDropChance -= 10 << fortuneLevel;
            if (saplingDropChance < 40) {
               saplingDropChance = 40;
            }
         }

         this.dropApple(worldIn, blockAt, blockCurrentState, saplingDropChance);
      }

   }

   protected void dropApple(World p_196474_1_, BlockPos p_196474_2_, IBlockState p_196474_3_, int p_196474_4_) {
      if ((p_196474_3_.getBlock() == Blocks.OAK_LEAVES || p_196474_3_.getBlock() == Blocks.DARK_OAK_LEAVES) && p_196474_1_.rand.nextInt(p_196474_4_) == 0) {
         spawnAsEntity(p_196474_1_, p_196474_2_, new ItemStack(Items.APPLE));
      }

   }

   protected int getSaplingDropChance(IBlockState p_196472_1_) {
      return p_196472_1_.getBlock() == Blocks.JUNGLE_LEAVES ? 40 : 20;
   }

   @OnlyIn(Dist.CLIENT)
   public static void setRenderTranslucent(boolean p_196475_0_) {
      renderTranslucent = p_196475_0_;
   }

   public BlockRenderLayer getRenderLayer() {
      return renderTranslucent ? BlockRenderLayer.CUTOUT_MIPPED : BlockRenderLayer.SOLID;
   }

   public boolean causesSuffocation(IBlockState p_176214_1_) {
      return false;
   }

   public void harvestBlock(World p_180657_1_, EntityPlayer p_180657_2_, BlockPos p_180657_3_, IBlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      if (!p_180657_1_.isRemote && p_180657_6_.getItem() == Items.SHEARS) {
         p_180657_2_.func_71029_a(StatList.BLOCK_MINED.func_199076_b(this));
         p_180657_2_.addExhaustion(0.005F);
         spawnAsEntity(p_180657_1_, p_180657_3_, new ItemStack(this));
      } else {
         super.harvestBlock(p_180657_1_, p_180657_2_, p_180657_3_, p_180657_4_, p_180657_5_, p_180657_6_);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(DISTANCE, PERSISTENT);
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return updateDistance(this.getDefaultState().with(PERSISTENT, Boolean.valueOf(true)), p_196258_1_.getWorld(), p_196258_1_.getPos());
   }
}
