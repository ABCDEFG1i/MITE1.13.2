package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;

public class BlockBanner extends BlockAbstractBanner {
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_0_15;
   private static final Map<EnumDyeColor, Block> BANNERS_BY_COLOR = Maps.newHashMap();
   private static final VoxelShape SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

   public BlockBanner(EnumDyeColor p_i48448_1_, Block.Properties p_i48448_2_) {
      super(p_i48448_1_, p_i48448_2_);
      this.setDefaultState(this.stateContainer.getBaseState().with(ROTATION, Integer.valueOf(0)));
      BANNERS_BY_COLOR.put(p_i48448_1_, this);
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      return p_196260_2_.getBlockState(p_196260_3_.down()).getMaterial().isSolid();
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPE;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getDefaultState().with(ROTATION, Integer.valueOf(MathHelper.floor((double)((180.0F + p_196258_1_.getPlacementYaw()) * 16.0F / 360.0F) + 0.5D) & 15));
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_ == EnumFacing.DOWN && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.with(ROTATION, Integer.valueOf(p_185499_2_.rotate(p_185499_1_.get(ROTATION), 16)));
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.with(ROTATION, Integer.valueOf(p_185471_2_.mirrorRotation(p_185471_1_.get(ROTATION), 16)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(ROTATION);
   }

   public static Block forColor(EnumDyeColor p_196287_0_) {
      return BANNERS_BY_COLOR.getOrDefault(p_196287_0_, Blocks.WHITE_BANNER);
   }
}
