package net.minecraft.block;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class BlockShulkerBox extends BlockContainer {
   public static final EnumProperty<EnumFacing> FACING = BlockDirectional.FACING;
   @Nullable
   private final EnumDyeColor color;

   public BlockShulkerBox(@Nullable EnumDyeColor p_i48334_1_, Block.Properties p_i48334_2_) {
      super(p_i48334_2_);
      this.color = p_i48334_1_;
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.UP));
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntityShulkerBox(this.color);
   }

   public boolean causesSuffocation(IBlockState p_176214_1_) {
      return true;
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

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (p_196250_2_.isRemote) {
         return true;
      } else if (p_196250_4_.isSpectator()) {
         return true;
      } else {
         TileEntity tileentity = p_196250_2_.getTileEntity(p_196250_3_);
         if (tileentity instanceof TileEntityShulkerBox) {
            EnumFacing enumfacing = p_196250_1_.get(FACING);
            boolean flag;
            if (((TileEntityShulkerBox)tileentity).getAnimationStatus() == TileEntityShulkerBox.AnimationStatus.CLOSED) {
               AxisAlignedBB axisalignedbb = VoxelShapes.func_197868_b().getBoundingBox().expand((double)(0.5F * (float)enumfacing.getXOffset()), (double)(0.5F * (float)enumfacing.getYOffset()), (double)(0.5F * (float)enumfacing.getZOffset())).contract((double)enumfacing.getXOffset(), (double)enumfacing.getYOffset(), (double)enumfacing.getZOffset());
               flag = p_196250_2_.isCollisionBoxesEmpty(null, axisalignedbb.offset(p_196250_3_.offset(enumfacing)));
            } else {
               flag = true;
            }

            if (flag) {
               p_196250_4_.addStat(StatList.OPEN_SHULKER_BOX);
               p_196250_4_.displayGUIChest((IInventory)tileentity);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getDefaultState().with(FACING, p_196258_1_.getFace());
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(FACING);
   }

   public void onBlockHarvested(World p_176208_1_, BlockPos p_176208_2_, IBlockState p_176208_3_, EntityPlayer p_176208_4_) {
      if (p_176208_1_.getTileEntity(p_176208_2_) instanceof TileEntityShulkerBox) {
         TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox)p_176208_1_.getTileEntity(p_176208_2_);
         tileentityshulkerbox.setDestroyedByCreativePlayer(p_176208_4_.capabilities.isCreativeMode);
         tileentityshulkerbox.fillWithLoot(p_176208_4_);
      }

      super.onBlockHarvested(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

    public void dropBlockAsItemWithChance(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, float chanceToDrop, int fortuneLevel) {
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, IBlockState p_180633_3_, EntityLivingBase p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasDisplayName()) {
         TileEntity tileentity = p_180633_1_.getTileEntity(p_180633_2_);
         if (tileentity instanceof TileEntityShulkerBox) {
            ((TileEntityShulkerBox)tileentity).setCustomName(p_180633_5_.getDisplayName());
         }
      }

   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         TileEntity tileentity = p_196243_2_.getTileEntity(p_196243_3_);
         if (tileentity instanceof TileEntityShulkerBox) {
            TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox)tileentity;
            if (!tileentityshulkerbox.isCleared() && tileentityshulkerbox.shouldDrop()) {
               ItemStack itemstack = new ItemStack(this);
               itemstack.getOrCreateTag().setTag("BlockEntityTag", ((TileEntityShulkerBox)tileentity).saveToNbt(new NBTTagCompound()));
               if (tileentityshulkerbox.hasCustomName()) {
                  itemstack.setDisplayName(tileentityshulkerbox.getCustomName());
                  tileentityshulkerbox.setCustomName(null);
               }

               spawnAsEntity(p_196243_2_, p_196243_3_, itemstack);
            }

            p_196243_2_.updateComparatorOutputLevel(p_196243_3_, p_196243_1_.getBlock());
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_190948_1_, @Nullable IBlockReader p_190948_2_, List<ITextComponent> p_190948_3_, ITooltipFlag p_190948_4_) {
      super.addInformation(p_190948_1_, p_190948_2_, p_190948_3_, p_190948_4_);
      NBTTagCompound nbttagcompound = p_190948_1_.getChildTag("BlockEntityTag");
      if (nbttagcompound != null) {
         if (nbttagcompound.hasKey("LootTable", 8)) {
            p_190948_3_.add(new TextComponentString("???????"));
         }

         if (nbttagcompound.hasKey("Items", 9)) {
            NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(nbttagcompound, nonnulllist);
            int i = 0;
            int j = 0;

            for(ItemStack itemstack : nonnulllist) {
               if (!itemstack.isEmpty()) {
                  ++j;
                  if (i <= 4) {
                     ++i;
                     ITextComponent itextcomponent = itemstack.getDisplayName().func_212638_h();
                     itextcomponent.appendText(" x").appendText(String.valueOf(itemstack.getCount()));
                     p_190948_3_.add(itextcomponent);
                  }
               }
            }

            if (j - i > 0) {
               p_190948_3_.add((new TextComponentTranslation("container.shulkerBox.more", j - i)).applyTextStyle(TextFormatting.ITALIC));
            }
         }
      }

   }

   public EnumPushReaction getPushReaction(IBlockState p_149656_1_) {
      return EnumPushReaction.DESTROY;
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      TileEntity tileentity = p_196244_2_.getTileEntity(p_196244_3_);
      return tileentity instanceof TileEntityShulkerBox ? VoxelShapes.func_197881_a(((TileEntityShulkerBox)tileentity).getBoundingBox(p_196244_1_)) : VoxelShapes.func_197868_b();
   }

   public boolean isSolid(IBlockState p_200124_1_) {
      return false;
   }

   public boolean hasComparatorInputOverride(IBlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return Container.calcRedstoneFromInventory((IInventory)p_180641_2_.getTileEntity(p_180641_3_));
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, IBlockState p_185473_3_) {
      ItemStack itemstack = super.getItem(p_185473_1_, p_185473_2_, p_185473_3_);
      TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox)p_185473_1_.getTileEntity(p_185473_2_);
      NBTTagCompound nbttagcompound = tileentityshulkerbox.saveToNbt(new NBTTagCompound());
      if (!nbttagcompound.isEmpty()) {
         itemstack.setTagInfo("BlockEntityTag", nbttagcompound);
      }

      return itemstack;
   }

   @OnlyIn(Dist.CLIENT)
   public static EnumDyeColor getColorFromItem(Item p_190955_0_) {
      return getColorFromBlock(Block.getBlockFromItem(p_190955_0_));
   }

   @OnlyIn(Dist.CLIENT)
   public static EnumDyeColor getColorFromBlock(Block p_190954_0_) {
      return p_190954_0_ instanceof BlockShulkerBox ? ((BlockShulkerBox)p_190954_0_).getColor() : null;
   }

   public static Block getBlockByColor(EnumDyeColor p_190952_0_) {
      if (p_190952_0_ == null) {
         return Blocks.SHULKER_BOX;
      } else {
         switch(p_190952_0_) {
         case WHITE:
            return Blocks.WHITE_SHULKER_BOX;
         case ORANGE:
            return Blocks.ORANGE_SHULKER_BOX;
         case MAGENTA:
            return Blocks.MAGENTA_SHULKER_BOX;
         case LIGHT_BLUE:
            return Blocks.LIGHT_BLUE_SHULKER_BOX;
         case YELLOW:
            return Blocks.YELLOW_SHULKER_BOX;
         case LIME:
            return Blocks.LIME_SHULKER_BOX;
         case PINK:
            return Blocks.PINK_SHULKER_BOX;
         case GRAY:
            return Blocks.GRAY_SHULKER_BOX;
         case LIGHT_GRAY:
            return Blocks.LIGHT_GRAY_SHULKER_BOX;
         case CYAN:
            return Blocks.CYAN_SHULKER_BOX;
         case PURPLE:
         default:
            return Blocks.PURPLE_SHULKER_BOX;
         case BLUE:
            return Blocks.BLUE_SHULKER_BOX;
         case BROWN:
            return Blocks.BROWN_SHULKER_BOX;
         case GREEN:
            return Blocks.GREEN_SHULKER_BOX;
         case RED:
            return Blocks.RED_SHULKER_BOX;
         case BLACK:
            return Blocks.BLACK_SHULKER_BOX;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public EnumDyeColor getColor() {
      return this.color;
   }

   public static ItemStack getColoredItemStack(EnumDyeColor p_190953_0_) {
      return new ItemStack(getBlockByColor(p_190953_0_));
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.with(FACING, p_185499_2_.rotate(p_185499_1_.get(FACING)));
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation(p_185471_1_.get(FACING)));
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      EnumFacing enumfacing = p_193383_2_.get(FACING);
      TileEntityShulkerBox.AnimationStatus tileentityshulkerbox$animationstatus = ((TileEntityShulkerBox)p_193383_1_.getTileEntity(p_193383_3_)).getAnimationStatus();
      return tileentityshulkerbox$animationstatus != TileEntityShulkerBox.AnimationStatus.CLOSED && (tileentityshulkerbox$animationstatus != TileEntityShulkerBox.AnimationStatus.OPENED || enumfacing != p_193383_4_.getOpposite() && enumfacing != p_193383_4_) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
   }
}
