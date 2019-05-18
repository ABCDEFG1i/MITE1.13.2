package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import javax.annotation.Nullable;
import java.util.Random;

import static java.lang.Thread.*;

public class BlockWorldSpawnPortal extends BlockNetherPortal {
    public BlockWorldSpawnPortal(Properties p_i48352_1_) {
        super(p_i48352_1_);
    }
    public boolean trySpawnPortal(IWorld p_176548_1_, BlockPos p_176548_2_) {
        BlockWorldSpawnPortal.Size blockportal$size = this.func_201816_b(p_176548_1_, p_176548_2_);
        if (blockportal$size != null) {
            blockportal$size.placePortalBlocks();
            return true;
        } else {
            return false;
        }
    }
    public void onEntityCollision(IBlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
        if (!p_196262_4_.isRiding() && !p_196262_4_.isBeingRidden() && p_196262_4_.isNonBoss()) {
            p_196262_4_.inWorldspawnPortal= true;
            p_196262_4_.setPortal(p_196262_3_,DimensionType.OVERWORLD);
        }

    }
    @Nullable
    public BlockWorldSpawnPortal.Size func_201816_b(IWorld p_201816_1_, BlockPos p_201816_2_) {
        BlockWorldSpawnPortal.Size blockportal$size = new BlockWorldSpawnPortal.Size(p_201816_1_, p_201816_2_, EnumFacing.Axis.X);
        if (blockportal$size.isValid() && blockportal$size.portalBlockCount == 0) {
            return blockportal$size;
        } else {
            BlockWorldSpawnPortal.Size blockportal$size1 = new BlockWorldSpawnPortal.Size(p_201816_1_, p_201816_2_, EnumFacing.Axis.Z);
            return blockportal$size1.isValid() && blockportal$size1.portalBlockCount == 0 ? blockportal$size1 : null;
        }
    }
    public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        EnumFacing.Axis enumfacing$axis = p_196271_2_.getAxis();
        EnumFacing.Axis enumfacing$axis1 = p_196271_1_.get(AXIS);
        boolean flag = enumfacing$axis1 != enumfacing$axis && enumfacing$axis.isHorizontal();
        return !flag && p_196271_3_.getBlock() != this && !(new BlockWorldSpawnPortal.Size(p_196271_4_, p_196271_5_, enumfacing$axis1)).func_208508_f() ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
    }
    public BlockPattern.PatternHelper createPatternHelper(IWorld p_181089_1_, BlockPos p_181089_2_) {
        EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.Z;
        BlockWorldSpawnPortal.Size blockportal$size = new BlockWorldSpawnPortal.Size(p_181089_1_, p_181089_2_, EnumFacing.Axis.X);
        LoadingCache<BlockPos, BlockWorldState> loadingcache = BlockPattern.createLoadingCache(p_181089_1_, true);
        if (!blockportal$size.isValid()) {
            enumfacing$axis = EnumFacing.Axis.X;
            blockportal$size = new BlockWorldSpawnPortal.Size(p_181089_1_, p_181089_2_, EnumFacing.Axis.Z);
        }

        if (!blockportal$size.isValid()) {
            return new BlockPattern.PatternHelper(p_181089_2_, EnumFacing.NORTH, EnumFacing.UP, loadingcache, 1, 1, 1);
        } else {
            int[] aint = new int[EnumFacing.AxisDirection.values().length];
            EnumFacing enumfacing = blockportal$size.rightDir.rotateYCCW();
            BlockPos blockpos = blockportal$size.bottomLeft.up(blockportal$size.getHeight() - 1);

            for(EnumFacing.AxisDirection enumfacing$axisdirection : EnumFacing.AxisDirection.values()) {
                BlockPattern.PatternHelper blockpattern$patternhelper = new BlockPattern.PatternHelper(enumfacing.getAxisDirection() == enumfacing$axisdirection ? blockpos : blockpos.offset(blockportal$size.rightDir, blockportal$size.getWidth() - 1), EnumFacing.getFacingFromAxis(enumfacing$axisdirection, enumfacing$axis), EnumFacing.UP, loadingcache, blockportal$size.getWidth(), blockportal$size.getHeight(), 1);

                for(int i = 0; i < blockportal$size.getWidth(); ++i) {
                    for(int j = 0; j < blockportal$size.getHeight(); ++j) {
                        BlockWorldState blockworldstate = blockpattern$patternhelper.translateOffset(i, j, 1);
                        if (!blockworldstate.getBlockState().isAir()) {
                            ++aint[enumfacing$axisdirection.ordinal()];
                        }
                    }
                }
            }

            EnumFacing.AxisDirection enumfacing$axisdirection1 = EnumFacing.AxisDirection.POSITIVE;

            for(EnumFacing.AxisDirection enumfacing$axisdirection2 : EnumFacing.AxisDirection.values()) {
                if (aint[enumfacing$axisdirection2.ordinal()] < aint[enumfacing$axisdirection1.ordinal()]) {
                    enumfacing$axisdirection1 = enumfacing$axisdirection2;
                }
            }

            return new BlockPattern.PatternHelper(enumfacing.getAxisDirection() == enumfacing$axisdirection1 ? blockpos : blockpos.offset(blockportal$size.rightDir, blockportal$size.getWidth() - 1), EnumFacing.getFacingFromAxis(enumfacing$axisdirection1, enumfacing$axis), EnumFacing.UP, loadingcache, blockportal$size.getWidth(), blockportal$size.getHeight(), 1);
        }
    }

    @Override
    public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {

    }
    public static class Size extends BlockNetherPortal.Size{
        public Size(IWorld p_i48740_1_, BlockPos p_i48740_2_, EnumFacing.Axis p_i48740_3_) {
            super(p_i48740_1_, p_i48740_2_, p_i48740_3_);
        }
        protected int calculatePortalHeight() {
            label56:
            for(this.height = 0; this.height < 21; ++this.height) {
                for(int i = 0; i < this.width; ++i) {
                    BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i).up(this.height);
                    IBlockState iblockstate = this.world.getBlockState(blockpos);
                    if (!this.func_196900_a(iblockstate)) {
                        break label56;
                    }

                    Block block = iblockstate.getBlock();
                    if (block == Blocks.WORLDSPAWN_PORTAL) {
                        ++this.portalBlockCount;
                    }

                    if (i == 0) {
                        block = this.world.getBlockState(blockpos.offset(this.leftDir)).getBlock();
                        if (block != Blocks.OBSIDIAN) {
                            break label56;
                        }
                    } else if (i == this.width - 1) {
                        block = this.world.getBlockState(blockpos.offset(this.rightDir)).getBlock();
                        if (block != Blocks.OBSIDIAN) {
                            break label56;
                        }
                    }
                }
            }

            for(int j = 0; j < this.width; ++j) {
                if (this.world.getBlockState(this.bottomLeft.offset(this.rightDir, j).up(this.height)).getBlock() != Blocks.OBSIDIAN) {
                    this.height = 0;
                    break;
                }
            }

            if (this.height <= 21 && this.height >= 3) {
                return this.height;
            } else {
                this.bottomLeft = null;
                this.width = 0;
                this.height = 0;
                return 0;
            }
        }
        protected boolean func_196900_a(IBlockState p_196900_1_) {
            Block block = p_196900_1_.getBlock();
            return p_196900_1_.isAir() || block == Blocks.FIRE || block == Blocks.WORLDSPAWN_PORTAL;
        }
        public void placePortalBlocks() {
            for(int i = 0; i < this.width; ++i) {
                BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i);

                for(int j = 0; j < this.height; ++j) {
                    this.world.setBlockState(blockpos.up(j), Blocks.WORLDSPAWN_PORTAL.getDefaultState().with(BlockWorldSpawnPortal.AXIS, this.axis), 18);
                }
            }

        }
    }

}
