package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockChest extends BlockContainer implements IBucketPickupHandler, ILiquidContainer {
   public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
   public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape field_196316_c = Block.makeCuboidShape(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D);
   protected static final VoxelShape field_196317_y = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D);
   protected static final VoxelShape field_196318_z = Block.makeCuboidShape(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
   protected static final VoxelShape field_196313_A = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);
   protected static final VoxelShape field_196315_B = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

   protected BlockChest(Block.Properties p_i48430_1_) {
      super(p_i48430_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(TYPE, ChestType.SINGLE).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasCustomBreakingProgress(IBlockState p_190946_1_) {
      return true;
   }

   public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
      return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_1_.get(WATERLOGGED)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      if (p_196271_3_.getBlock() == this && p_196271_2_.getAxis().isHorizontal()) {
         ChestType chesttype = p_196271_3_.get(TYPE);
         if (p_196271_1_.get(TYPE) == ChestType.SINGLE && chesttype != ChestType.SINGLE && p_196271_1_.get(FACING) == p_196271_3_.get(FACING) && getDirectionToAttached(p_196271_3_) == p_196271_2_.getOpposite()) {
            return p_196271_1_.with(TYPE, chesttype.opposite());
         }
      } else if (getDirectionToAttached(p_196271_1_) == p_196271_2_) {
         return p_196271_1_.with(TYPE, ChestType.SINGLE);
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      if (p_196244_1_.get(TYPE) == ChestType.SINGLE) {
         return field_196315_B;
      } else {
         switch(getDirectionToAttached(p_196244_1_)) {
         case NORTH:
         default:
            return field_196316_c;
         case SOUTH:
            return field_196317_y;
         case WEST:
            return field_196318_z;
         case EAST:
            return field_196313_A;
         }
      }
   }

   public static EnumFacing getDirectionToAttached(IBlockState p_196311_0_) {
      EnumFacing enumfacing = p_196311_0_.get(FACING);
      return p_196311_0_.get(TYPE) == ChestType.LEFT ? enumfacing.rotateY() : enumfacing.rotateYCCW();
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      ChestType chesttype = ChestType.SINGLE;
      EnumFacing enumfacing = p_196258_1_.getPlacementHorizontalFacing().getOpposite();
      IFluidState ifluidstate = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      boolean flag = p_196258_1_.isPlacerSneaking();
      EnumFacing enumfacing1 = p_196258_1_.getFace();
      if (enumfacing1.getAxis().isHorizontal() && flag) {
         EnumFacing enumfacing2 = this.getDirectionToAttach(p_196258_1_, enumfacing1.getOpposite());
         if (enumfacing2 != null && enumfacing2.getAxis() != enumfacing1.getAxis()) {
            enumfacing = enumfacing2;
            chesttype = enumfacing2.rotateYCCW() == enumfacing1.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
         }
      }

      if (chesttype == ChestType.SINGLE && !flag) {
         if (enumfacing == this.getDirectionToAttach(p_196258_1_, enumfacing.rotateY())) {
            chesttype = ChestType.LEFT;
         } else if (enumfacing == this.getDirectionToAttach(p_196258_1_, enumfacing.rotateYCCW())) {
            chesttype = ChestType.RIGHT;
         }
      }

      return this.getDefaultState().with(FACING, enumfacing).with(TYPE, chesttype).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
   }

   public Fluid pickupFluid(IWorld p_204508_1_, BlockPos p_204508_2_, IBlockState p_204508_3_) {
      if (p_204508_3_.get(WATERLOGGED)) {
         p_204508_1_.setBlockState(p_204508_2_, p_204508_3_.with(WATERLOGGED, Boolean.valueOf(false)), 3);
         return Fluids.WATER;
      } else {
         return Fluids.EMPTY;
      }
   }

   public IFluidState getFluidState(IBlockState p_204507_1_) {
      return p_204507_1_.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_204507_1_);
   }

   public boolean canContainFluid(IBlockReader p_204510_1_, BlockPos p_204510_2_, IBlockState p_204510_3_, Fluid p_204510_4_) {
      return !p_204510_3_.get(WATERLOGGED) && p_204510_4_ == Fluids.WATER;
   }

   public boolean receiveFluid(IWorld p_204509_1_, BlockPos p_204509_2_, IBlockState p_204509_3_, IFluidState p_204509_4_) {
      if (!p_204509_3_.get(WATERLOGGED) && p_204509_4_.getFluid() == Fluids.WATER) {
         if (!p_204509_1_.isRemote()) {
            p_204509_1_.setBlockState(p_204509_2_, p_204509_3_.with(WATERLOGGED, Boolean.valueOf(true)), 3);
            p_204509_1_.getPendingFluidTicks().scheduleTick(p_204509_2_, Fluids.WATER, Fluids.WATER.getTickRate(p_204509_1_));
         }

         return true;
      } else {
         return false;
      }
   }

   @Nullable
   private EnumFacing getDirectionToAttach(BlockItemUseContext p_196312_1_, EnumFacing p_196312_2_) {
      IBlockState iblockstate = p_196312_1_.getWorld().getBlockState(p_196312_1_.getPos().offset(p_196312_2_));
      return iblockstate.getBlock() == this && iblockstate.get(TYPE) == ChestType.SINGLE ? iblockstate.get(FACING) : null;
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, IBlockState p_180633_3_, EntityLivingBase p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasDisplayName()) {
         TileEntity tileentity = p_180633_1_.getTileEntity(p_180633_2_);
         if (tileentity instanceof TileEntityChest) {
            ((TileEntityChest)tileentity).setCustomName(p_180633_5_.getDisplayName());
         }
      }

   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         TileEntity tileentity = p_196243_2_.getTileEntity(p_196243_3_);
         if (tileentity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(p_196243_2_, p_196243_3_, (IInventory)tileentity);
            p_196243_2_.updateComparatorOutputLevel(p_196243_3_, this);
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (p_196250_2_.isRemote) {
         return true;
      } else {
         ILockableContainer ilockablecontainer = this.getContainer(p_196250_1_, p_196250_2_, p_196250_3_, false);
         if (ilockablecontainer != null) {
            p_196250_4_.displayGUIChest(ilockablecontainer);
            p_196250_4_.func_71029_a(this.func_196310_d());
         }

         return true;
      }
   }

   protected Stat<ResourceLocation> func_196310_d() {
      return StatList.CUSTOM.func_199076_b(StatList.OPEN_CHEST);
   }

   @Nullable
   public ILockableContainer getContainer(IBlockState p_196309_1_, World p_196309_2_, BlockPos p_196309_3_, boolean p_196309_4_) {
      TileEntity tileentity = p_196309_2_.getTileEntity(p_196309_3_);
      if (!(tileentity instanceof TileEntityChest)) {
         return null;
      } else if (!p_196309_4_ && this.isBlocked(p_196309_2_, p_196309_3_)) {
         return null;
      } else {
         ILockableContainer ilockablecontainer = (TileEntityChest)tileentity;
         ChestType chesttype = p_196309_1_.get(TYPE);
         if (chesttype == ChestType.SINGLE) {
            return ilockablecontainer;
         } else {
            BlockPos blockpos = p_196309_3_.offset(getDirectionToAttached(p_196309_1_));
            IBlockState iblockstate = p_196309_2_.getBlockState(blockpos);
            if (iblockstate.getBlock() == this) {
               ChestType chesttype1 = iblockstate.get(TYPE);
               if (chesttype1 != ChestType.SINGLE && chesttype != chesttype1 && iblockstate.get(FACING) == p_196309_1_.get(FACING)) {
                  if (!p_196309_4_ && this.isBlocked(p_196309_2_, blockpos)) {
                     return null;
                  }

                  TileEntity tileentity1 = p_196309_2_.getTileEntity(blockpos);
                  if (tileentity1 instanceof TileEntityChest) {
                     ILockableContainer ilockablecontainer1 = chesttype == ChestType.RIGHT ? ilockablecontainer : (ILockableContainer)tileentity1;
                     ILockableContainer ilockablecontainer2 = chesttype == ChestType.RIGHT ? (ILockableContainer)tileentity1 : ilockablecontainer;
                     ilockablecontainer = new InventoryLargeChest(new TextComponentTranslation("container.chestDouble"), ilockablecontainer1, ilockablecontainer2);
                  }
               }
            }

            return ilockablecontainer;
         }
      }
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntityChest();
   }

   private boolean isBlocked(World p_176457_1_, BlockPos p_176457_2_) {
      return this.isBelowSolidBlock(p_176457_1_, p_176457_2_) || this.isOcelotSittingOnChest(p_176457_1_, p_176457_2_);
   }

   private boolean isBelowSolidBlock(IBlockReader p_176456_1_, BlockPos p_176456_2_) {
      return p_176456_1_.getBlockState(p_176456_2_.up()).isNormalCube();
   }

   private boolean isOcelotSittingOnChest(World p_176453_1_, BlockPos p_176453_2_) {
      List<EntityOcelot> list = p_176453_1_.getEntitiesWithinAABB(EntityOcelot.class, new AxisAlignedBB((double)p_176453_2_.getX(), (double)(p_176453_2_.getY() + 1), (double)p_176453_2_.getZ(), (double)(p_176453_2_.getX() + 1), (double)(p_176453_2_.getY() + 2), (double)(p_176453_2_.getZ() + 1)));
      if (!list.isEmpty()) {
         for(EntityOcelot entityocelot : list) {
            if (entityocelot.isSitting()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean hasComparatorInputOverride(IBlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return Container.calcRedstoneFromInventory(this.getContainer(p_180641_1_, p_180641_2_, p_180641_3_, false));
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.with(FACING, p_185499_2_.rotate(p_185499_1_.get(FACING)));
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation(p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(FACING, TYPE, WATERLOGGED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
