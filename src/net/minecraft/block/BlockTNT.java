package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockTNT extends Block {
   public static final BooleanProperty field_212569_a = BlockStateProperties.field_212646_x;

   public BlockTNT(Block.Properties p_i48309_1_) {
      super(p_i48309_1_);
      this.setDefaultState(this.getDefaultState().with(field_212569_a, Boolean.valueOf(false)));
   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      if (p_196259_4_.getBlock() != p_196259_1_.getBlock()) {
         if (p_196259_2_.isBlockPowered(p_196259_3_)) {
            this.explode(p_196259_2_, p_196259_3_);
            p_196259_2_.removeBlock(p_196259_3_);
         }

      }
   }

   public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
      if (p_189540_2_.isBlockPowered(p_189540_3_)) {
         this.explode(p_189540_2_, p_189540_3_);
         p_189540_2_.removeBlock(p_189540_3_);
      }

   }

   public void dropBlockAsItemWithChance(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, float chanceToDrop, int fortuneLevel) {
      if (!blockCurrentState.get(field_212569_a)) {
         super.dropBlockAsItemWithChance(blockCurrentState, worldIn, blockAt, chanceToDrop, fortuneLevel);
      }
   }

   public void onBlockHarvested(World p_176208_1_, BlockPos p_176208_2_, IBlockState p_176208_3_, EntityPlayer p_176208_4_) {
      if (!p_176208_1_.isRemote() && !p_176208_4_.isCreative() && p_176208_3_.get(field_212569_a)) {
         this.explode(p_176208_1_, p_176208_2_);
      }

      super.onBlockHarvested(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

   public void onExplosionDestroy(World p_180652_1_, BlockPos p_180652_2_, Explosion p_180652_3_) {
      if (!p_180652_1_.isRemote) {
         EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(p_180652_1_, (double)((float)p_180652_2_.getX() + 0.5F), (double)p_180652_2_.getY(), (double)((float)p_180652_2_.getZ() + 0.5F), p_180652_3_.getExplosivePlacedBy());
         entitytntprimed.setFuse((short)(p_180652_1_.rand.nextInt(entitytntprimed.getFuse() / 4) + entitytntprimed.getFuse() / 8));
         p_180652_1_.spawnEntity(entitytntprimed);
      }
   }

   public void explode(World p_196534_1_, BlockPos p_196534_2_) {
      this.explode(p_196534_1_, p_196534_2_, null);
   }

   private void explode(World p_196535_1_, BlockPos p_196535_2_, @Nullable EntityLivingBase p_196535_3_) {
      if (!p_196535_1_.isRemote) {
         EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(p_196535_1_, (double)((float)p_196535_2_.getX() + 0.5F), (double)p_196535_2_.getY(), (double)((float)p_196535_2_.getZ() + 0.5F), p_196535_3_);
         p_196535_1_.spawnEntity(entitytntprimed);
         p_196535_1_.playSound(null, entitytntprimed.posX, entitytntprimed.posY, entitytntprimed.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      ItemStack itemstack = p_196250_4_.getHeldItem(p_196250_5_);
      Item item = itemstack.getItem();
      if (item != Items.FLINT_AND_STEEL && item != Items.FIRE_CHARGE) {
         return super.onBlockActivated(p_196250_1_, p_196250_2_, p_196250_3_, p_196250_4_, p_196250_5_, p_196250_6_, p_196250_7_, p_196250_8_, p_196250_9_);
      } else {
         this.explode(p_196250_2_, p_196250_3_, p_196250_4_);
         p_196250_2_.setBlockState(p_196250_3_, Blocks.AIR.getDefaultState(), 11);
         if (item == Items.FLINT_AND_STEEL) {
            itemstack.damageItem(1, p_196250_4_);
         } else {
            itemstack.shrink(1);
         }

         return true;
      }
   }

   public void onEntityCollision(IBlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_2_.isRemote && p_196262_4_ instanceof EntityArrow) {
         EntityArrow entityarrow = (EntityArrow)p_196262_4_;
         Entity entity = entityarrow.func_212360_k();
         if (entityarrow.isBurning()) {
            this.explode(p_196262_2_, p_196262_3_, entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null);
            p_196262_2_.removeBlock(p_196262_3_);
         }
      }

   }

   public boolean canDropFromExplosion(Explosion p_149659_1_) {
      return false;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(field_212569_a);
   }
}
