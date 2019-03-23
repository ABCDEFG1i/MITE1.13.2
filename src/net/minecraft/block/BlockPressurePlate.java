package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockPressurePlate extends BlockBasePressurePlate {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private final BlockPressurePlate.Sensitivity sensitivity;

   protected BlockPressurePlate(BlockPressurePlate.Sensitivity p_i48348_1_, Block.Properties p_i48348_2_) {
      super(p_i48348_2_);
      this.setDefaultState(this.stateContainer.getBaseState().with(POWERED, Boolean.valueOf(false)));
      this.sensitivity = p_i48348_1_;
   }

   protected int getRedstoneStrength(IBlockState p_176576_1_) {
      return p_176576_1_.get(POWERED) ? 15 : 0;
   }

   protected IBlockState setRedstoneStrength(IBlockState p_176575_1_, int p_176575_2_) {
      return p_176575_1_.with(POWERED, Boolean.valueOf(p_176575_2_ > 0));
   }

   protected void playClickOnSound(IWorld p_185507_1_, BlockPos p_185507_2_) {
      if (this.material == Material.WOOD) {
         p_185507_1_.playSound((EntityPlayer)null, p_185507_2_, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
      } else {
         p_185507_1_.playSound((EntityPlayer)null, p_185507_2_, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
      }

   }

   protected void playClickOffSound(IWorld p_185508_1_, BlockPos p_185508_2_) {
      if (this.material == Material.WOOD) {
         p_185508_1_.playSound((EntityPlayer)null, p_185508_2_, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.7F);
      } else {
         p_185508_1_.playSound((EntityPlayer)null, p_185508_2_, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
      }

   }

   protected int computeRedstoneStrength(World p_180669_1_, BlockPos p_180669_2_) {
      AxisAlignedBB axisalignedbb = PRESSURE_AABB.offset(p_180669_2_);
      List<? extends Entity> list;
      switch(this.sensitivity) {
      case EVERYTHING:
         list = p_180669_1_.func_72839_b((Entity)null, axisalignedbb);
         break;
      case MOBS:
         list = p_180669_1_.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
         break;
      default:
         return 0;
      }

      if (!list.isEmpty()) {
         for(Entity entity : list) {
            if (!entity.doesEntityNotTriggerPressurePlate()) {
               return 15;
            }
         }
      }

      return 0;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(POWERED);
   }

   public static enum Sensitivity {
      EVERYTHING,
      MOBS;
   }
}
