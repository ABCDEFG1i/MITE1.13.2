package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDaylightDetector;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockDaylightDetector extends BlockContainer {
   public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
   public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);

   public BlockDaylightDetector(Block.Properties p_i48419_1_) {
      super(p_i48419_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(POWER, Integer.valueOf(0)).with(INVERTED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPE;
   }

   public int getWeakPower(IBlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, EnumFacing p_180656_4_) {
      return p_180656_1_.get(POWER);
   }

   public static void updatePower(IBlockState p_196319_0_, World p_196319_1_, BlockPos p_196319_2_) {
      if (p_196319_1_.dimension.hasSkyLight()) {
         int i = p_196319_1_.getLightFor(EnumLightType.SKY, p_196319_2_) - p_196319_1_.getSkylightSubtracted();
         float f = p_196319_1_.getCelestialAngleRadians(1.0F);
         boolean flag = p_196319_0_.get(INVERTED);
         if (flag) {
            i = 15 - i;
         } else if (i > 0) {
            float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
            f = f + (f1 - f) * 0.2F;
            i = Math.round((float)i * MathHelper.cos(f));
         }

         i = MathHelper.clamp(i, 0, 15);
         if (p_196319_0_.get(POWER) != i) {
            p_196319_1_.setBlockState(p_196319_2_, p_196319_0_.with(POWER, Integer.valueOf(i)), 3);
         }

      }
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (p_196250_4_.isAllowEdit()) {
         if (p_196250_2_.isRemote) {
            return true;
         } else {
            IBlockState iblockstate = p_196250_1_.cycle(INVERTED);
            p_196250_2_.setBlockState(p_196250_3_, iblockstate, 4);
            updatePower(iblockstate, p_196250_2_, p_196250_3_);
            return true;
         }
      } else {
         return super.onBlockActivated(p_196250_1_, p_196250_2_, p_196250_3_, p_196250_4_, p_196250_5_, p_196250_6_, p_196250_7_, p_196250_8_, p_196250_9_);
      }
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
      return EnumBlockRenderType.MODEL;
   }

   public boolean canProvidePower(IBlockState p_149744_1_) {
      return true;
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntityDaylightDetector();
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(POWER, INVERTED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return p_193383_4_ == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }
}
