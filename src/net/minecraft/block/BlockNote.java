package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockNote extends Block {
   public static final EnumProperty<NoteBlockInstrument> INSTRUMENT = BlockStateProperties.NOTE_BLOCK_INSTRUMENT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final IntegerProperty NOTE = BlockStateProperties.NOTE_0_24;

   public BlockNote(Block.Properties p_i48359_1_) {
      super(p_i48359_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(INSTRUMENT, NoteBlockInstrument.HARP).with(NOTE, Integer.valueOf(0)).with(POWERED, Boolean.valueOf(false)));
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getDefaultState().with(INSTRUMENT, NoteBlockInstrument.byState(p_196258_1_.getWorld().getBlockState(p_196258_1_.getPos().down())));
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_ == EnumFacing.DOWN ? p_196271_1_.with(INSTRUMENT, NoteBlockInstrument.byState(p_196271_3_)) : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
      boolean flag = p_189540_2_.isBlockPowered(p_189540_3_);
      if (flag != p_189540_1_.get(POWERED)) {
         if (flag) {
            this.triggerNote(p_189540_2_, p_189540_3_);
         }

         p_189540_2_.setBlockState(p_189540_3_, p_189540_1_.with(POWERED, Boolean.valueOf(flag)), 3);
      }

   }

   private void triggerNote(World p_196482_1_, BlockPos p_196482_2_) {
      if (p_196482_1_.getBlockState(p_196482_2_.up()).isAir()) {
         p_196482_1_.addBlockEvent(p_196482_2_, this, 0, 0);
      }

   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (p_196250_2_.isRemote) {
         return true;
      } else {
         p_196250_1_ = p_196250_1_.cycle(NOTE);
         p_196250_2_.setBlockState(p_196250_3_, p_196250_1_, 3);
         this.triggerNote(p_196250_2_, p_196250_3_);
         p_196250_4_.addStat(StatList.TUNE_NOTEBLOCK);
         return true;
      }
   }

   public void onBlockClicked(IBlockState p_196270_1_, World p_196270_2_, BlockPos p_196270_3_, EntityPlayer p_196270_4_) {
      if (!p_196270_2_.isRemote) {
         this.triggerNote(p_196270_2_, p_196270_3_);
         p_196270_4_.addStat(StatList.PLAY_NOTEBLOCK);
      }
   }

   public boolean eventReceived(IBlockState p_189539_1_, World p_189539_2_, BlockPos p_189539_3_, int p_189539_4_, int p_189539_5_) {
      int i = p_189539_1_.get(NOTE);
      float f = (float)Math.pow(2.0D, (double)(i - 12) / 12.0D);
      p_189539_2_.playSound((EntityPlayer)null, p_189539_3_, p_189539_1_.get(INSTRUMENT).getSound(), SoundCategory.RECORDS, 3.0F, f);
      p_189539_2_.spawnParticle(Particles.NOTE, (double)p_189539_3_.getX() + 0.5D, (double)p_189539_3_.getY() + 1.2D, (double)p_189539_3_.getZ() + 0.5D, (double)i / 24.0D, 0.0D, 0.0D);
      return true;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(INSTRUMENT, POWERED, NOTE);
   }
}
