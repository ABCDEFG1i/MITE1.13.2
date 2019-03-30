package net.minecraft.block;

import com.google.common.collect.Maps;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.Map;

public class BlockFlowerPot extends Block {
   private static final Map<Block, Block> field_196451_b = Maps.newHashMap();
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
   private final Block field_196452_c;

   public BlockFlowerPot(Block p_i48395_1_, Block.Properties p_i48395_2_) {
      super(p_i48395_2_);
      this.field_196452_c = p_i48395_1_;
      field_196451_b.put(p_i48395_1_, this);
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPE;
   }

   public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
      return EnumBlockRenderType.MODEL;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      ItemStack itemstack = p_196250_4_.getHeldItem(p_196250_5_);
      Item item = itemstack.getItem();
      Block block = item instanceof ItemBlock ? field_196451_b.getOrDefault(((ItemBlock)item).getBlock(), Blocks.AIR) : Blocks.AIR;
      boolean flag = block == Blocks.AIR;
      boolean flag1 = this.field_196452_c == Blocks.AIR;
      if (flag != flag1) {
         if (flag1) {
            p_196250_2_.setBlockState(p_196250_3_, block.getDefaultState(), 3);
            p_196250_4_.addStat(StatList.POT_FLOWER);
            if (!p_196250_4_.capabilities.isCreativeMode) {
               itemstack.shrink(1);
            }
         } else {
            ItemStack itemstack1 = new ItemStack(this.field_196452_c);
            if (itemstack.isEmpty()) {
               p_196250_4_.setHeldItem(p_196250_5_, itemstack1);
            } else if (!p_196250_4_.addItemStackToInventory(itemstack1)) {
               p_196250_4_.dropItem(itemstack1, false);
            }

            p_196250_2_.setBlockState(p_196250_3_, Blocks.FLOWER_POT.getDefaultState(), 3);
         }
      }

      return true;
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, IBlockState p_185473_3_) {
      return this.field_196452_c == Blocks.AIR ? super.getItem(p_185473_1_, p_185473_2_, p_185473_3_) : new ItemStack(this.field_196452_c);
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Blocks.FLOWER_POT;
   }

    public void dropBlockAsItemWithChance(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, float chanceToDrop, int fortuneLevel) {
        super.dropBlockAsItemWithChance(blockCurrentState, worldIn, blockAt, chanceToDrop, fortuneLevel);
      if (this.field_196452_c != Blocks.AIR) {
          spawnAsEntity(worldIn, blockAt, new ItemStack(this.field_196452_c));
      }

   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_ == EnumFacing.DOWN && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
