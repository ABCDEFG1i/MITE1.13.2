package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityAnvil;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockAnvil extends BlockFalling implements ITileEntityProvider {
   private int currentDamage;
   private final int maxDamage;
   private final int repairLevel;
   private BlockPos blockAt;
   private World worldIn;
   private final ItemTier tier;
   public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
   private static final VoxelShape field_196436_c = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
   private static final VoxelShape field_196439_y = Block.makeCuboidShape(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D);
   private static final VoxelShape field_196440_z = Block.makeCuboidShape(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D);
   private static final VoxelShape field_196434_A = Block.makeCuboidShape(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D);
   private static final VoxelShape field_196435_B = Block.makeCuboidShape(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D);
   private static final VoxelShape field_196437_C = Block.makeCuboidShape(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D);
   private static final VoxelShape field_196438_D = Block.makeCuboidShape(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D);
   private static final VoxelShape X_AXIS_AABB = VoxelShapes.func_197872_a(field_196436_c, VoxelShapes.func_197872_a(field_196439_y, VoxelShapes.func_197872_a(field_196440_z, field_196434_A)));
   private static final VoxelShape Z_AXIS_AABB = VoxelShapes.func_197872_a(field_196436_c, VoxelShapes.func_197872_a(field_196435_B, VoxelShapes.func_197872_a(field_196437_C, field_196438_D)));

   BlockAnvil(Block.Properties p_i48450_1_, ItemTier tier, int maxDamage, int repairLevel) {
      super(p_i48450_1_);
      this.repairLevel = repairLevel;
      this.maxDamage = maxDamage;
      this.tier =tier;
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH));
   }
   BlockAnvil(Block.Properties p_i48450_1_,ItemTier tier, int currentDamage, int maxDamage, int repairLevel) {
      super(p_i48450_1_);
      this.repairLevel = repairLevel;
      this.currentDamage = currentDamage;
      this.maxDamage = maxDamage;
      this.tier = tier;
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH));
   }

   @Override
   public void fillItemGroup(ItemGroup p_149666_1_, NonNullList<ItemStack> p_149666_2_) {
      ItemStack shownItem = new ItemStack(this);
      NBTTagCompound tag = new NBTTagCompound();
      NBTTagCompound blockEntityTag = new NBTTagCompound();
      blockEntityTag.setString("id", "minecraft:anvil");
      blockEntityTag.setInteger("Damage",currentDamage);
      tag.setTag("BlockEntityTag", blockEntityTag);
      tag.setInteger("Damage",currentDamage);
      shownItem.setTag(tag);
      p_149666_2_.add(shownItem);
   }

   @Nullable
   @Override
   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntityAnvil();
   }

   public int getRepairLevel() {
      return repairLevel;
   }


   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getDefaultState().with(FACING, p_196258_1_.getPlacementHorizontalFacing().rotateY());
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (!p_196250_2_.isRemote) {
         this.blockAt = p_196250_3_;
         this.worldIn = p_196250_2_;
         p_196250_4_.displayGui(new BlockAnvil.Anvil(p_196250_2_, p_196250_3_,this.repairLevel));
      }

      return true;
   }
   @Override
   public void dropBlockAsItemWithChance(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, float chanceToDrop, int fortuneLevel) {
      ItemStack result = new ItemStack(((IItemProvider) this).asItem());
      NBTTagCompound tag = new NBTTagCompound();
      NBTTagCompound blockEntityTag = new NBTTagCompound();
      blockEntityTag.setString("id", "minecraft:anvil");
      int damage =  ((TileEntityAnvil)worldIn.getTileEntity(blockAt)).getDamage();
      blockEntityTag.setInteger("Damage",damage);
      tag.setTag("BlockEntityTag", blockEntityTag);
      tag.setInteger("Damage",damage);
      result.setTag(tag);
      spawnAsEntity(worldIn, blockAt, result);
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      EnumFacing enumfacing = p_196244_1_.get(FACING);
      return enumfacing.getAxis() == EnumFacing.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
   }

   protected void onStartFalling(EntityFallingBlock p_149829_1_) {
      p_149829_1_.setHurtEntities(true);
   }

   public void onEndFalling(World p_176502_1_, BlockPos p_176502_2_, IBlockState p_176502_3_, IBlockState p_176502_4_) {
      p_176502_1_.playEvent(1031, p_176502_2_, 0);
   }

   public void onBroken(World p_190974_1_, BlockPos p_190974_2_) {
      p_190974_1_.playEvent(1029, p_190974_2_, 0);
   }

   @Nullable
   public IBlockState damage(int p_196433_0_) {
      TileEntity tileEntityAnvil = this.worldIn.getTileEntity(this.blockAt);
      if (tileEntityAnvil instanceof TileEntityAnvil){
         TileEntityAnvil anvilTileEntity =  (TileEntityAnvil) tileEntityAnvil;
         anvilTileEntity.setDamage(anvilTileEntity.getDamage() + p_196433_0_);
         this.currentDamage = anvilTileEntity.getDamage();
      }
      if (currentDamage>=this.maxDamage){
         return null;
      }
      if (this.currentDamage>=maxDamage/3*2){
         return this.getDamagedState().with(FACING,this.worldIn.getBlockState(blockAt).get(FACING));
      }else if (currentDamage<=maxDamage/3){
         return this.getDefaultState().with(FACING,this.worldIn.getBlockState(blockAt).get(FACING));
      } else if(currentDamage<=maxDamage/3*2 && currentDamage>=maxDamage/3) {
         return this.getChippedState().with(FACING,this.worldIn.getBlockState(blockAt).get(FACING));
      }

      return null;
   }

   private IBlockState getChippedState(){
     switch (this.tier){
        case COPPER:
           return Blocks.CHIPPED_COPPER_ANVIL.getDefaultState();
        case SILVER:
           return Blocks.CHIPPED_SILVER_ANVIL.getDefaultState();
        case GOLD:
           return Blocks.CHIPPED_GOLD_ANVIL.getDefaultState();
        case ANCIENT_METAL:
           return Blocks.CHIPPED_ANCIENT_METAL_ANVIL.getDefaultState();
        case MITHRIL:
           return Blocks.CHIPPED_MITHRIL_ANVIL.getDefaultState();
        case TUNGSTEN:
           return Blocks.CHIPPED_TUNGSTEN_ANVIL.getDefaultState();
        case ADAMANTIUM:
           return Blocks.CHIPPED_ADAMANTIUM_ANVIL.getDefaultState();
           default:
              return Blocks.CHIPPED_IRON_ANVIL.getDefaultState();
     }
   }
   private IBlockState getDamagedState(){
      switch (this.tier){
         case COPPER:
            return Blocks.DAMAGED_COPPER_ANVIL.getDefaultState();
         case SILVER:
            return Blocks.DAMAGED_SILVER_ANVIL.getDefaultState();
         case GOLD:
            return Blocks.DAMAGED_GOLD_ANVIL.getDefaultState();
         case ANCIENT_METAL:
            return Blocks.DAMAGED_ANCIENT_METAL_ANVIL.getDefaultState();
         case MITHRIL:
            return Blocks.DAMAGED_MITHRIL_ANVIL.getDefaultState();
         case TUNGSTEN:
            return Blocks.DAMAGED_TUNGSTEN_ANVIL.getDefaultState();
         case ADAMANTIUM:
            return Blocks.DAMAGED_ADAMANTIUM_ANVIL.getDefaultState();
         default:
            return Blocks.DAMAGED_IRON_ANVIL.getDefaultState();
      }
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.with(FACING, p_185499_2_.rotate(p_185499_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(FACING);
   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   public static class Anvil implements IInteractionObject {
      private final World world;
      private final BlockPos position;
      private final int repairLevel;

      public Anvil(World p_i45741_1_, BlockPos p_i45741_2_,int repairLevel) {
         this.world = p_i45741_1_;
         this.position = p_i45741_2_;
         this.repairLevel = repairLevel;
      }

      public ITextComponent getName() {
         return new TextComponentTranslation(Blocks.IRON_ANVIL.getTranslationKey());
      }

      public boolean hasCustomName() {
         return false;
      }

      @Nullable
      public ITextComponent getCustomName() {
         return null;
      }

      public Container createContainer(InventoryPlayer p_174876_1_, EntityPlayer p_174876_2_) {
         return new ContainerRepair(p_174876_1_, this.world, this.position, p_174876_2_,this.repairLevel);
      }

      public int getGuiLevel() {
         return repairLevel;
      }

      public String getGuiID() {
         return "minecraft:anvil";
      }
   }
}
