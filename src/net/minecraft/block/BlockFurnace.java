package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockFurnace extends BlockContainer {
   public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
   public static final BooleanProperty LIT = BlockRedstoneTorch.LIT;
   private final int maxHeatLevel;

   protected BlockFurnace(int maxHeatLevel,Block.Properties p_i48393_1_) {
      super(p_i48393_1_);
      this.maxHeatLevel = maxHeatLevel;
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(LIT, Boolean.FALSE));
   }

   public int getLightValue(IBlockState p_149750_1_) {
      return p_149750_1_.get(LIT) ? super.getLightValue(p_149750_1_) : 0;
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (p_196250_2_.isRemote) {
         return true;
      } else {
         TileEntity tileentity = p_196250_2_.getTileEntity(p_196250_3_);
         if (tileentity instanceof TileEntityFurnace) {
            p_196250_4_.displayGUIChest(((TileEntityFurnace) tileentity).setMaxHeatLevel(maxHeatLevel));
            p_196250_4_.addStat(StatList.INTERACT_WITH_FURNACE);
         }

         return true;
      }
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntityFurnace();
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getDefaultState().with(FACING, p_196258_1_.getPlacementHorizontalFacing().getOpposite());
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, IBlockState p_180633_3_, EntityLivingBase p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasDisplayName()) {
         TileEntity tileentity = p_180633_1_.getTileEntity(p_180633_2_);
         if (tileentity instanceof TileEntityFurnace) {
            ((TileEntityFurnace)tileentity).setCustomName(p_180633_5_.getDisplayName());
         }
      }

   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         TileEntity tileentity = p_196243_2_.getTileEntity(p_196243_3_);
         if (tileentity instanceof TileEntityFurnace) {
            InventoryHelper.dropInventoryItems(p_196243_2_, p_196243_3_, (TileEntityFurnace)tileentity);
            p_196243_2_.updateComparatorOutputLevel(p_196243_3_, this);
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public boolean hasComparatorInputOverride(IBlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return Container.calcRedstone(p_180641_2_.getTileEntity(p_180641_3_));
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_1_.get(LIT)) {
         double d0 = (double)p_180655_3_.getX() + 0.5D;
         double d1 = (double)p_180655_3_.getY();
         double d2 = (double)p_180655_3_.getZ() + 0.5D;
         if (p_180655_4_.nextDouble() < 0.1D) {
            p_180655_2_.playSound(d0, d1, d2, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
         }

         EnumFacing enumfacing = p_180655_1_.get(FACING);
         EnumFacing.Axis enumfacing$axis = enumfacing.getAxis();
         double d3 = 0.52D;
         double d4 = p_180655_4_.nextDouble() * 0.6D - 0.3D;
         double d5 = enumfacing$axis == EnumFacing.Axis.X ? (double)enumfacing.getXOffset() * 0.52D : d4;
         double d6 = p_180655_4_.nextDouble() * 6.0D / 16.0D;
         double d7 = enumfacing$axis == EnumFacing.Axis.Z ? (double)enumfacing.getZOffset() * 0.52D : d4;
         p_180655_2_.spawnParticle(Particles.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
         p_180655_2_.spawnParticle(Particles.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
      }
   }

   public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
      return EnumBlockRenderType.MODEL;
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.with(FACING, p_185499_2_.rotate(p_185499_1_.get(FACING)));
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation(p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(FACING, LIT);
   }
}
