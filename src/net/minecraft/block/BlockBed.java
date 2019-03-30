package net.minecraft.block;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class BlockBed extends BlockHorizontal implements ITileEntityProvider {
   public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
   public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);
   private final EnumDyeColor color;

   public BlockBed(EnumDyeColor p_i48442_1_, Block.Properties p_i48442_2_) {
      super(p_i48442_2_);
      this.color = p_i48442_1_;
      this.setDefaultState(this.stateContainer.getBaseState().with(PART, BedPart.FOOT).with(OCCUPIED, Boolean.valueOf(false)));
   }

   public MaterialColor func_180659_g(IBlockState p_180659_1_, IBlockReader p_180659_2_, BlockPos p_180659_3_) {
      return p_180659_1_.get(PART) == BedPart.FOOT ? this.color.getMapColor() : MaterialColor.CLOTH;
   }

   public boolean onBlockActivated(IBlockState blockState, World worldIn, BlockPos bedPos, EntityPlayer player, EnumHand hand, EnumFacing face, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (worldIn.isRemote) {
         return true;
      } else {
         if (blockState.get(PART) != BedPart.HEAD) {
            bedPos = bedPos.offset(blockState.get(HORIZONTAL_FACING));
            blockState = worldIn.getBlockState(bedPos);
            if (blockState.getBlock() != this) {
               return true;
            }
         }

         if (worldIn.dimension.canRespawnHere() && worldIn.getBiome(bedPos) != Biomes.NETHER) {
            if (blockState.get(OCCUPIED)) {
               EntityPlayer entityplayer = this.getPlayerInBed(worldIn, bedPos);
               if (entityplayer != null) {
                  player.sendStatusMessage(new TextComponentTranslation("block.minecraft.bed.occupied"), true);
                  return true;
               }

               blockState = blockState.with(OCCUPIED, Boolean.FALSE);
               worldIn.setBlockState(bedPos, blockState, 4);
            }

            EntityPlayer.SleepResult sleepResult = player.trySleep(bedPos);
            if (sleepResult == EntityPlayer.SleepResult.OK) {
               blockState = blockState.with(OCCUPIED, Boolean.TRUE);
               worldIn.setBlockState(bedPos, blockState, 4);
               player.setNaturalHealSpeed(640);
               return true;
            } else {
               if (sleepResult == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW) {
                  player.sendStatusMessage(new TextComponentTranslation("block.minecraft.bed.no_sleep"), true);
               } else if (sleepResult == EntityPlayer.SleepResult.NOT_SAFE) {
                  player.sendStatusMessage(new TextComponentTranslation("block.minecraft.bed.not_safe"), true);
               } else if (sleepResult == EntityPlayer.SleepResult.TOO_FAR_AWAY) {
                  player.sendStatusMessage(new TextComponentTranslation("block.minecraft.bed.too_far_away"), true);
               }

               return true;
            }
         } else {
            worldIn.removeBlock(bedPos);
            BlockPos blockpos = bedPos.offset(blockState.get(HORIZONTAL_FACING).getOpposite());
            if (worldIn.getBlockState(blockpos).getBlock() == this) {
               worldIn.removeBlock(blockpos);
            }

            worldIn.createExplosion(null, DamageSource.func_199683_a(), (double)bedPos.getX() + 0.5D, (double)bedPos.getY() + 0.5D, (double)bedPos.getZ() + 0.5D, 5.0F, true, true);
            return true;
         }
      }
   }

   @Nullable
   private EntityPlayer getPlayerInBed(World p_176470_1_, BlockPos p_176470_2_) {
      for(EntityPlayer entityplayer : p_176470_1_.playerEntities) {
         if (entityplayer.isPlayerSleeping() && entityplayer.bedLocation.equals(p_176470_2_)) {
            return entityplayer;
         }
      }

      return null;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public void onFallenUpon(World p_180658_1_, BlockPos p_180658_2_, Entity p_180658_3_, float p_180658_4_) {
      super.onFallenUpon(p_180658_1_, p_180658_2_, p_180658_3_, p_180658_4_ * 0.5F);
   }

   public void onLanded(IBlockReader p_176216_1_, Entity p_176216_2_) {
      if (p_176216_2_.isSneaking()) {
         super.onLanded(p_176216_1_, p_176216_2_);
      } else if (p_176216_2_.motionY < 0.0D) {
         p_176216_2_.motionY = -p_176216_2_.motionY * (double)0.66F;
         if (!(p_176216_2_ instanceof EntityLivingBase)) {
            p_176216_2_.motionY *= 0.8D;
         }
      }

   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ == func_208070_a(p_196271_1_.get(PART), p_196271_1_.get(HORIZONTAL_FACING))) {
         return p_196271_3_.getBlock() == this && p_196271_3_.get(PART) != p_196271_1_.get(PART) ? p_196271_1_.with(OCCUPIED, p_196271_3_.get(OCCUPIED)) : Blocks.AIR.getDefaultState();
      } else {
         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   private static EnumFacing func_208070_a(BedPart p_208070_0_, EnumFacing p_208070_1_) {
      return p_208070_0_ == BedPart.FOOT ? p_208070_1_ : p_208070_1_.getOpposite();
   }

   public void harvestBlock(World p_180657_1_, EntityPlayer p_180657_2_, BlockPos p_180657_3_, IBlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      super.harvestBlock(p_180657_1_, p_180657_2_, p_180657_3_, Blocks.AIR.getDefaultState(), p_180657_5_, p_180657_6_);
   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
         p_196243_2_.removeTileEntity(p_196243_3_);
      }
   }

   public void onBlockHarvested(World p_176208_1_, BlockPos p_176208_2_, IBlockState p_176208_3_, EntityPlayer p_176208_4_) {
      BedPart bedpart = p_176208_3_.get(PART);
      boolean flag = bedpart == BedPart.HEAD;
      BlockPos blockpos = p_176208_2_.offset(func_208070_a(bedpart, p_176208_3_.get(HORIZONTAL_FACING)));
      IBlockState iblockstate = p_176208_1_.getBlockState(blockpos);
      if (iblockstate.getBlock() == this && iblockstate.get(PART) != bedpart) {
         p_176208_1_.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
         p_176208_1_.playEvent(p_176208_4_, 2001, blockpos, Block.getStateId(iblockstate));
         if (!p_176208_1_.isRemote && !p_176208_4_.isCreative()) {
            if (flag) {
               p_176208_3_.dropBlockAsItem(p_176208_1_, p_176208_2_, 0);
            } else {
               iblockstate.dropBlockAsItem(p_176208_1_, blockpos, 0);
            }
         }

         p_176208_4_.func_71029_a(StatList.BLOCK_MINED.func_199076_b(this));
      }

      super.onBlockHarvested(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      EnumFacing enumfacing = p_196258_1_.getPlacementHorizontalFacing();
      BlockPos blockpos = p_196258_1_.getPos();
      BlockPos blockpos1 = blockpos.offset(enumfacing);
      return p_196258_1_.getWorld().getBlockState(blockpos1).isReplaceable(p_196258_1_) ? this.getDefaultState().with(HORIZONTAL_FACING, enumfacing) : null;
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
        return (blockCurrentState.get(PART) == BedPart.FOOT ? Items.AIR : super.getItemDropped(blockCurrentState, worldIn, blockAt, fortuneLevel));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPE;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasCustomBreakingProgress(IBlockState p_190946_1_) {
      return true;
   }

   @Nullable
   public static BlockPos getSafeExitLocation(IBlockReader p_176468_0_, BlockPos p_176468_1_, int p_176468_2_) {
      EnumFacing enumfacing = p_176468_0_.getBlockState(p_176468_1_).get(HORIZONTAL_FACING);
      int i = p_176468_1_.getX();
      int j = p_176468_1_.getY();
      int k = p_176468_1_.getZ();

      for(int l = 0; l <= 1; ++l) {
         int i1 = i - enumfacing.getXOffset() * l - 1;
         int j1 = k - enumfacing.getZOffset() * l - 1;
         int k1 = i1 + 2;
         int l1 = j1 + 2;

         for(int i2 = i1; i2 <= k1; ++i2) {
            for(int j2 = j1; j2 <= l1; ++j2) {
               BlockPos blockpos = new BlockPos(i2, j, j2);
               if (hasRoomForPlayer(p_176468_0_, blockpos)) {
                  if (p_176468_2_ <= 0) {
                     return blockpos;
                  }

                  --p_176468_2_;
               }
            }
         }
      }

      return null;
   }

   protected static boolean hasRoomForPlayer(IBlockReader p_176469_0_, BlockPos p_176469_1_) {
      return p_176469_0_.getBlockState(p_176469_1_.down()).isTopSolid() && !p_176469_0_.getBlockState(p_176469_1_).getMaterial().isSolid() && !p_176469_0_.getBlockState(p_176469_1_.up()).getMaterial().isSolid();
   }

   public EnumPushReaction getPushReaction(IBlockState p_149656_1_) {
      return EnumPushReaction.DESTROY;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
      return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(HORIZONTAL_FACING, PART, OCCUPIED);
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntityBed(this.color);
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, IBlockState p_180633_3_, @Nullable EntityLivingBase p_180633_4_, ItemStack p_180633_5_) {
      super.onBlockPlacedBy(p_180633_1_, p_180633_2_, p_180633_3_, p_180633_4_, p_180633_5_);
      if (!p_180633_1_.isRemote) {
         BlockPos blockpos = p_180633_2_.offset(p_180633_3_.get(HORIZONTAL_FACING));
         p_180633_1_.setBlockState(blockpos, p_180633_3_.with(PART, BedPart.HEAD), 3);
         p_180633_1_.notifyNeighbors(p_180633_2_, Blocks.AIR);
         p_180633_3_.updateNeighbors(p_180633_1_, p_180633_2_, 3);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public EnumDyeColor getColor() {
      return this.color;
   }

   @OnlyIn(Dist.CLIENT)
   public long getPositionRandom(IBlockState p_209900_1_, BlockPos p_209900_2_) {
      BlockPos blockpos = p_209900_2_.offset(p_209900_1_.get(HORIZONTAL_FACING), p_209900_1_.get(PART) == BedPart.HEAD ? 0 : 1);
      return MathHelper.getCoordinateRandom(blockpos.getX(), p_209900_2_.getY(), blockpos.getZ());
   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
