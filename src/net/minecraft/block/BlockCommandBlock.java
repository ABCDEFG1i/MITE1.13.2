package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockCommandBlock extends BlockContainer {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final DirectionProperty FACING = BlockDirectional.FACING;
   public static final BooleanProperty CONDITIONAL = BlockStateProperties.CONDITIONAL;

   public BlockCommandBlock(Block.Properties p_i48425_1_) {
      super(p_i48425_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(CONDITIONAL, Boolean.valueOf(false)));
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      TileEntityCommandBlock tileentitycommandblock = new TileEntityCommandBlock();
      tileentitycommandblock.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
      return tileentitycommandblock;
   }

   public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
      if (!p_189540_2_.isRemote) {
         TileEntity tileentity = p_189540_2_.getTileEntity(p_189540_3_);
         if (tileentity instanceof TileEntityCommandBlock) {
            TileEntityCommandBlock tileentitycommandblock = (TileEntityCommandBlock)tileentity;
            boolean flag = p_189540_2_.isBlockPowered(p_189540_3_);
            boolean flag1 = tileentitycommandblock.isPowered();
            tileentitycommandblock.setPowered(flag);
            if (!flag1 && !tileentitycommandblock.isAuto() && tileentitycommandblock.getMode() != TileEntityCommandBlock.Mode.SEQUENCE) {
               if (flag) {
                  tileentitycommandblock.setConditionMet();
                  p_189540_2_.getPendingBlockTicks().scheduleTick(p_189540_3_, this, this.tickRate(p_189540_2_));
               }

            }
         }
      }
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (!p_196267_2_.isRemote) {
         TileEntity tileentity = p_196267_2_.getTileEntity(p_196267_3_);
         if (tileentity instanceof TileEntityCommandBlock) {
            TileEntityCommandBlock tileentitycommandblock = (TileEntityCommandBlock)tileentity;
            CommandBlockBaseLogic commandblockbaselogic = tileentitycommandblock.getCommandBlockLogic();
            boolean flag = !StringUtils.isNullOrEmpty(commandblockbaselogic.getCommand());
            TileEntityCommandBlock.Mode tileentitycommandblock$mode = tileentitycommandblock.getMode();
            boolean flag1 = tileentitycommandblock.isConditionMet();
            if (tileentitycommandblock$mode == TileEntityCommandBlock.Mode.AUTO) {
               tileentitycommandblock.setConditionMet();
               if (flag1) {
                  this.execute(p_196267_1_, p_196267_2_, p_196267_3_, commandblockbaselogic, flag);
               } else if (tileentitycommandblock.isConditional()) {
                  commandblockbaselogic.setSuccessCount(0);
               }

               if (tileentitycommandblock.isPowered() || tileentitycommandblock.isAuto()) {
                  p_196267_2_.getPendingBlockTicks().scheduleTick(p_196267_3_, this, this.tickRate(p_196267_2_));
               }
            } else if (tileentitycommandblock$mode == TileEntityCommandBlock.Mode.REDSTONE) {
               if (flag1) {
                  this.execute(p_196267_1_, p_196267_2_, p_196267_3_, commandblockbaselogic, flag);
               } else if (tileentitycommandblock.isConditional()) {
                  commandblockbaselogic.setSuccessCount(0);
               }
            }

            p_196267_2_.updateComparatorOutputLevel(p_196267_3_, this);
         }

      }
   }

   private void execute(IBlockState p_193387_1_, World p_193387_2_, BlockPos p_193387_3_, CommandBlockBaseLogic p_193387_4_, boolean p_193387_5_) {
      if (p_193387_5_) {
         p_193387_4_.trigger(p_193387_2_);
      } else {
         p_193387_4_.setSuccessCount(0);
      }

      executeChain(p_193387_2_, p_193387_3_, p_193387_1_.get(FACING));
   }

   public int tickRate(IWorldReaderBase p_149738_1_) {
      return 1;
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      TileEntity tileentity = p_196250_2_.getTileEntity(p_196250_3_);
      if (tileentity instanceof TileEntityCommandBlock && p_196250_4_.canUseCommandBlock()) {
         p_196250_4_.displayGuiCommandBlock((TileEntityCommandBlock)tileentity);
         return true;
      } else {
         return false;
      }
   }

   public boolean hasComparatorInputOverride(IBlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      TileEntity tileentity = p_180641_2_.getTileEntity(p_180641_3_);
      return tileentity instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock)tileentity).getCommandBlockLogic().getSuccessCount() : 0;
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, IBlockState p_180633_3_, EntityLivingBase p_180633_4_, ItemStack p_180633_5_) {
      TileEntity tileentity = p_180633_1_.getTileEntity(p_180633_2_);
      if (tileentity instanceof TileEntityCommandBlock) {
         TileEntityCommandBlock tileentitycommandblock = (TileEntityCommandBlock)tileentity;
         CommandBlockBaseLogic commandblockbaselogic = tileentitycommandblock.getCommandBlockLogic();
         if (p_180633_5_.hasDisplayName()) {
            commandblockbaselogic.setName(p_180633_5_.getDisplayName());
         }

         if (!p_180633_1_.isRemote) {
            if (p_180633_5_.getChildTag("BlockEntityTag") == null) {
               commandblockbaselogic.setTrackOutput(p_180633_1_.getGameRules().getBoolean("sendCommandFeedback"));
               tileentitycommandblock.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
            }

            if (tileentitycommandblock.getMode() == TileEntityCommandBlock.Mode.SEQUENCE) {
               boolean flag = p_180633_1_.isBlockPowered(p_180633_2_);
               tileentitycommandblock.setPowered(flag);
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

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.with(FACING, p_185499_2_.rotate(p_185499_1_.get(FACING)));
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation(p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(FACING, CONDITIONAL);
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getDefaultState().with(FACING, p_196258_1_.func_196010_d().getOpposite());
   }

   private static void executeChain(World p_193386_0_, BlockPos p_193386_1_, EnumFacing p_193386_2_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_193386_1_);
      GameRules gamerules = p_193386_0_.getGameRules();

      int i;
      IBlockState iblockstate;
      for(i = gamerules.getInt("maxCommandChainLength"); i-- > 0; p_193386_2_ = iblockstate.get(FACING)) {
         blockpos$mutableblockpos.move(p_193386_2_);
         iblockstate = p_193386_0_.getBlockState(blockpos$mutableblockpos);
         Block block = iblockstate.getBlock();
         if (block != Blocks.CHAIN_COMMAND_BLOCK) {
            break;
         }

         TileEntity tileentity = p_193386_0_.getTileEntity(blockpos$mutableblockpos);
         if (!(tileentity instanceof TileEntityCommandBlock)) {
            break;
         }

         TileEntityCommandBlock tileentitycommandblock = (TileEntityCommandBlock)tileentity;
         if (tileentitycommandblock.getMode() != TileEntityCommandBlock.Mode.SEQUENCE) {
            break;
         }

         if (tileentitycommandblock.isPowered() || tileentitycommandblock.isAuto()) {
            CommandBlockBaseLogic commandblockbaselogic = tileentitycommandblock.getCommandBlockLogic();
            if (tileentitycommandblock.setConditionMet()) {
               if (!commandblockbaselogic.trigger(p_193386_0_)) {
                  break;
               }

               p_193386_0_.updateComparatorOutputLevel(blockpos$mutableblockpos, block);
            } else if (tileentitycommandblock.isConditional()) {
               commandblockbaselogic.setSuccessCount(0);
            }
         }
      }

      if (i <= 0) {
         int j = Math.max(gamerules.getInt("maxCommandChainLength"), 0);
         LOGGER.warn("Command Block chain tried to execute more than {} steps!", j);
      }

   }
}
