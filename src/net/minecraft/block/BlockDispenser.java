package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BlockSourceImpl;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockDispenser extends BlockContainer {
   public static final DirectionProperty FACING = BlockDirectional.FACING;
   public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
   private static final Map<Item, IBehaviorDispenseItem> DISPENSE_BEHAVIOR_REGISTRY = Util.make(new Object2ObjectOpenHashMap<>(), (p_212564_0_) -> {
      p_212564_0_.defaultReturnValue(new BehaviorDefaultDispenseItem());
   });

   public static void registerDispenseBehavior(IItemProvider p_199774_0_, IBehaviorDispenseItem p_199774_1_) {
      DISPENSE_BEHAVIOR_REGISTRY.put(p_199774_0_.asItem(), p_199774_1_);
   }

   protected BlockDispenser(Block.Properties p_i48414_1_) {
      super(p_i48414_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(TRIGGERED, Boolean.valueOf(false)));
   }

   public int tickRate(IWorldReaderBase p_149738_1_) {
      return 4;
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (p_196250_2_.isRemote) {
         return true;
      } else {
         TileEntity tileentity = p_196250_2_.getTileEntity(p_196250_3_);
         if (tileentity instanceof TileEntityDispenser) {
            p_196250_4_.displayGUIChest((TileEntityDispenser)tileentity);
            if (tileentity instanceof TileEntityDropper) {
               p_196250_4_.addStat(StatList.INSPECT_DROPPER);
            } else {
               p_196250_4_.addStat(StatList.INSPECT_DISPENSER);
            }
         }

         return true;
      }
   }

   protected void dispense(World p_176439_1_, BlockPos p_176439_2_) {
      BlockSourceImpl blocksourceimpl = new BlockSourceImpl(p_176439_1_, p_176439_2_);
      TileEntityDispenser tileentitydispenser = blocksourceimpl.getBlockTileEntity();
      int i = tileentitydispenser.getDispenseSlot();
      if (i < 0) {
         p_176439_1_.playEvent(1001, p_176439_2_, 0);
      } else {
         ItemStack itemstack = tileentitydispenser.getStackInSlot(i);
         IBehaviorDispenseItem ibehaviordispenseitem = this.getBehavior(itemstack);
         if (ibehaviordispenseitem != IBehaviorDispenseItem.NOOP) {
            tileentitydispenser.setInventorySlotContents(i, ibehaviordispenseitem.dispense(blocksourceimpl, itemstack));
         }

      }
   }

   protected IBehaviorDispenseItem getBehavior(ItemStack p_149940_1_) {
      return DISPENSE_BEHAVIOR_REGISTRY.get(p_149940_1_.getItem());
   }

   public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
      boolean flag = p_189540_2_.isBlockPowered(p_189540_3_) || p_189540_2_.isBlockPowered(p_189540_3_.up());
      boolean flag1 = p_189540_1_.get(TRIGGERED);
      if (flag && !flag1) {
         p_189540_2_.getPendingBlockTicks().scheduleTick(p_189540_3_, this, this.tickRate(p_189540_2_));
         p_189540_2_.setBlockState(p_189540_3_, p_189540_1_.with(TRIGGERED, Boolean.valueOf(true)), 4);
      } else if (!flag && flag1) {
         p_189540_2_.setBlockState(p_189540_3_, p_189540_1_.with(TRIGGERED, Boolean.valueOf(false)), 4);
      }

   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (!p_196267_2_.isRemote) {
         this.dispense(p_196267_2_, p_196267_3_);
      }

   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntityDispenser();
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getDefaultState().with(FACING, p_196258_1_.func_196010_d().getOpposite());
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, IBlockState p_180633_3_, EntityLivingBase p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasDisplayName()) {
         TileEntity tileentity = p_180633_1_.getTileEntity(p_180633_2_);
         if (tileentity instanceof TileEntityDispenser) {
            ((TileEntityDispenser)tileentity).setCustomName(p_180633_5_.getDisplayName());
         }
      }

   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         TileEntity tileentity = p_196243_2_.getTileEntity(p_196243_3_);
         if (tileentity instanceof TileEntityDispenser) {
            InventoryHelper.dropInventoryItems(p_196243_2_, p_196243_3_, (TileEntityDispenser)tileentity);
            p_196243_2_.updateComparatorOutputLevel(p_196243_3_, this);
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public static IPosition getDispensePosition(IBlockSource p_149939_0_) {
      EnumFacing enumfacing = p_149939_0_.getBlockState().get(FACING);
      double d0 = p_149939_0_.getX() + 0.7D * (double)enumfacing.getXOffset();
      double d1 = p_149939_0_.getY() + 0.7D * (double)enumfacing.getYOffset();
      double d2 = p_149939_0_.getZ() + 0.7D * (double)enumfacing.getZOffset();
      return new PositionImpl(d0, d1, d2);
   }

   public boolean hasComparatorInputOverride(IBlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return Container.calcRedstone(p_180641_2_.getTileEntity(p_180641_3_));
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
      p_206840_1_.add(FACING, TRIGGERED);
   }
}
