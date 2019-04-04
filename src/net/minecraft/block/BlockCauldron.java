package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmorDyeable;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockCauldron extends Block {
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_0_3;
   protected static final VoxelShape INSIDE = Block.makeCuboidShape(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
   protected static final VoxelShape WALLS = VoxelShapes.func_197878_a(VoxelShapes.func_197868_b(), INSIDE, IBooleanFunction.ONLY_FIRST);

   public BlockCauldron(Block.Properties p_i48431_1_) {
      super(p_i48431_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(LEVEL, Integer.valueOf(0)));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return WALLS;
   }

   public boolean isSolid(IBlockState p_200124_1_) {
      return false;
   }

   public VoxelShape getRaytraceShape(IBlockState p_199600_1_, IBlockReader p_199600_2_, BlockPos p_199600_3_) {
      return INSIDE;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public void onEntityCollision(IBlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      int i = p_196262_1_.get(LEVEL);
      float f = (float)p_196262_3_.getY() + (6.0F + (float)(3 * i)) / 16.0F;
      if (!p_196262_2_.isRemote && p_196262_4_.isBurning() && i > 0 && p_196262_4_.getEntityBoundingBox().minY <= (double)f) {
         p_196262_4_.extinguish();
         this.setWaterLevel(p_196262_2_, p_196262_3_, p_196262_1_, i - 1);
      }

   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      ItemStack itemstack = p_196250_4_.getHeldItem(p_196250_5_);
      if (itemstack.isEmpty()) {
         return true;
      } else {
         int i = p_196250_1_.get(LEVEL);
         Item item = itemstack.getItem();
         if (item == Items.WATER_BUCKET) {
            if (i < 3 && !p_196250_2_.isRemote) {
               if (!p_196250_4_.capabilities.isCreativeMode) {
                  p_196250_4_.setHeldItem(p_196250_5_, new ItemStack(Items.BUCKET));
               }

               p_196250_4_.addStat(StatList.FILL_CAULDRON);
               this.setWaterLevel(p_196250_2_, p_196250_3_, p_196250_1_, 3);
               p_196250_2_.playSound(null, p_196250_3_, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            return true;
         } else if (item == Items.BUCKET) {
            if (i == 3 && !p_196250_2_.isRemote) {
               if (!p_196250_4_.capabilities.isCreativeMode) {
                  itemstack.shrink(1);
                  if (itemstack.isEmpty()) {
                     p_196250_4_.setHeldItem(p_196250_5_, new ItemStack(Items.WATER_BUCKET));
                  } else if (!p_196250_4_.inventory.addItemStackToInventory(new ItemStack(Items.WATER_BUCKET))) {
                     p_196250_4_.dropItem(new ItemStack(Items.WATER_BUCKET), false);
                  }
               }

               p_196250_4_.addStat(StatList.USE_CAULDRON);
               this.setWaterLevel(p_196250_2_, p_196250_3_, p_196250_1_, 0);
               p_196250_2_.playSound(null, p_196250_3_, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            return true;
         } else if (item == Items.GLASS_BOTTLE) {
            if (i > 0 && !p_196250_2_.isRemote) {
               if (!p_196250_4_.capabilities.isCreativeMode) {
                  ItemStack itemstack4 = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), PotionTypes.WATER);
                  p_196250_4_.addStat(StatList.USE_CAULDRON);
                  itemstack.shrink(1);
                  if (itemstack.isEmpty()) {
                     p_196250_4_.setHeldItem(p_196250_5_, itemstack4);
                  } else if (!p_196250_4_.inventory.addItemStackToInventory(itemstack4)) {
                     p_196250_4_.dropItem(itemstack4, false);
                  } else if (p_196250_4_ instanceof EntityPlayerMP) {
                     ((EntityPlayerMP)p_196250_4_).sendContainerToPlayer(p_196250_4_.inventoryContainer);
                  }
               }

               p_196250_2_.playSound(null, p_196250_3_, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
               this.setWaterLevel(p_196250_2_, p_196250_3_, p_196250_1_, i - 1);
            }

            return true;
         } else if (item == Items.POTION && PotionUtils.getPotionFromItem(itemstack) == PotionTypes.WATER) {
            if (i < 3 && !p_196250_2_.isRemote) {
               if (!p_196250_4_.capabilities.isCreativeMode) {
                  ItemStack itemstack3 = new ItemStack(Items.GLASS_BOTTLE);
                  p_196250_4_.addStat(StatList.USE_CAULDRON);
                  p_196250_4_.setHeldItem(p_196250_5_, itemstack3);
                  if (p_196250_4_ instanceof EntityPlayerMP) {
                     ((EntityPlayerMP)p_196250_4_).sendContainerToPlayer(p_196250_4_.inventoryContainer);
                  }
               }

               p_196250_2_.playSound(null, p_196250_3_, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
               this.setWaterLevel(p_196250_2_, p_196250_3_, p_196250_1_, i + 1);
            }

            return true;
         } else {
            if (i > 0 && item instanceof ItemArmorDyeable) {
               ItemArmorDyeable itemarmordyeable = (ItemArmorDyeable)item;
               if (itemarmordyeable.hasColor(itemstack) && !p_196250_2_.isRemote) {
                  itemarmordyeable.removeColor(itemstack);
                  this.setWaterLevel(p_196250_2_, p_196250_3_, p_196250_1_, i - 1);
                  p_196250_4_.addStat(StatList.CLEAN_ARMOR);
                  return true;
               }
            }

            if (i > 0 && item instanceof ItemBanner) {
               if (TileEntityBanner.getPatterns(itemstack) > 0 && !p_196250_2_.isRemote) {
                  ItemStack itemstack2 = itemstack.copy();
                  itemstack2.setCount(1);
                  TileEntityBanner.removeBannerData(itemstack2);
                  p_196250_4_.addStat(StatList.CLEAN_BANNER);
                  if (!p_196250_4_.capabilities.isCreativeMode) {
                     itemstack.shrink(1);
                     this.setWaterLevel(p_196250_2_, p_196250_3_, p_196250_1_, i - 1);
                  }

                  if (itemstack.isEmpty()) {
                     p_196250_4_.setHeldItem(p_196250_5_, itemstack2);
                  } else if (!p_196250_4_.inventory.addItemStackToInventory(itemstack2)) {
                     p_196250_4_.dropItem(itemstack2, false);
                  } else if (p_196250_4_ instanceof EntityPlayerMP) {
                     ((EntityPlayerMP)p_196250_4_).sendContainerToPlayer(p_196250_4_.inventoryContainer);
                  }
               }

               return true;
            } else if (i > 0 && item instanceof ItemBlock) {
               Block block = ((ItemBlock)item).getBlock();
               if (block instanceof BlockShulkerBox && !p_196250_2_.isRemote()) {
                  ItemStack itemstack1 = new ItemStack(Blocks.SHULKER_BOX, 1);
                  if (itemstack.hasTag()) {
                     itemstack1.setTag(itemstack.getTag().copy());
                  }

                  p_196250_4_.setHeldItem(p_196250_5_, itemstack1);
                  this.setWaterLevel(p_196250_2_, p_196250_3_, p_196250_1_, i - 1);
                  p_196250_4_.addStat(StatList.field_212740_X);
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }

   public void setWaterLevel(World p_176590_1_, BlockPos p_176590_2_, IBlockState p_176590_3_, int p_176590_4_) {
      p_176590_1_.setBlockState(p_176590_2_, p_176590_3_.with(LEVEL, Integer.valueOf(MathHelper.clamp(p_176590_4_, 0, 3))), 2);
      p_176590_1_.updateComparatorOutputLevel(p_176590_2_, this);
   }

   public void fillWithRain(World p_176224_1_, BlockPos p_176224_2_) {
      if (p_176224_1_.rand.nextInt(20) == 1) {
         float f = p_176224_1_.getBiome(p_176224_2_).getTemperature(p_176224_2_);
         if (!(f < 0.15F)) {
            IBlockState iblockstate = p_176224_1_.getBlockState(p_176224_2_);
            if (iblockstate.get(LEVEL) < 3) {
               p_176224_1_.setBlockState(p_176224_2_, iblockstate.cycle(LEVEL), 2);
            }

         }
      }
   }

   public boolean hasComparatorInputOverride(IBlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return p_180641_1_.get(LEVEL);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(LEVEL);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      if (p_193383_4_ == EnumFacing.UP) {
         return BlockFaceShape.BOWL;
      } else {
         return p_193383_4_ == EnumFacing.DOWN ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
      }
   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
