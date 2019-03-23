package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockStructure extends BlockContainer {
   public static final EnumProperty<StructureMode> MODE = BlockStateProperties.STRUCTURE_BLOCK_MODE;

   protected BlockStructure(Block.Properties p_i48314_1_) {
      super(p_i48314_1_);
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntityStructure();
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      TileEntity tileentity = p_196250_2_.getTileEntity(p_196250_3_);
      return tileentity instanceof TileEntityStructure ? ((TileEntityStructure)tileentity).usedBy(p_196250_4_) : false;
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, IBlockState p_180633_3_, @Nullable EntityLivingBase p_180633_4_, ItemStack p_180633_5_) {
      if (!p_180633_1_.isRemote) {
         if (p_180633_4_ != null) {
            TileEntity tileentity = p_180633_1_.getTileEntity(p_180633_2_);
            if (tileentity instanceof TileEntityStructure) {
               ((TileEntityStructure)tileentity).createdBy(p_180633_4_);
            }
         }

      }
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return 0;
   }

   public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
      return EnumBlockRenderType.MODEL;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getDefaultState().with(MODE, StructureMode.DATA);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(MODE);
   }

   public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
      if (!p_189540_2_.isRemote) {
         TileEntity tileentity = p_189540_2_.getTileEntity(p_189540_3_);
         if (tileentity instanceof TileEntityStructure) {
            TileEntityStructure tileentitystructure = (TileEntityStructure)tileentity;
            boolean flag = p_189540_2_.isBlockPowered(p_189540_3_);
            boolean flag1 = tileentitystructure.isPowered();
            if (flag && !flag1) {
               tileentitystructure.setPowered(true);
               this.trigger(tileentitystructure);
            } else if (!flag && flag1) {
               tileentitystructure.setPowered(false);
            }

         }
      }
   }

   private void trigger(TileEntityStructure p_189874_1_) {
      switch(p_189874_1_.getMode()) {
      case SAVE:
         p_189874_1_.save(false);
         break;
      case LOAD:
         p_189874_1_.load(false);
         break;
      case CORNER:
         p_189874_1_.unloadStructure();
      case DATA:
      }

   }
}
