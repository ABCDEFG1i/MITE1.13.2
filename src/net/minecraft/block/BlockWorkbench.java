package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public class BlockWorkbench extends Block {
   protected BlockWorkbench(Block.Properties p_i48422_1_) {
      super(p_i48422_1_);
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (p_196250_2_.isRemote) {
         return true;
      } else {
         p_196250_4_.displayGui(new BlockWorkbench.InterfaceCraftingTable(p_196250_2_, p_196250_3_));
         p_196250_4_.addStat(StatList.INTERACT_WITH_CRAFTING_TABLE);
         return true;
      }
   }

   public static class InterfaceCraftingTable implements IInteractionObject {
      private final World world;
      private final BlockPos position;

      public InterfaceCraftingTable(World p_i45730_1_, BlockPos p_i45730_2_) {
         this.world = p_i45730_1_;
         this.position = p_i45730_2_;
      }

      public ITextComponent getName() {
         return new TextComponentTranslation(Blocks.CRAFTING_TABLE.getTranslationKey() + ".name");
      }

      public boolean hasCustomName() {
         return false;
      }

      @Nullable
      public ITextComponent getCustomName() {
         return null;
      }

      public Container createContainer(InventoryPlayer p_174876_1_, EntityPlayer p_174876_2_) {
         return new ContainerWorkbench(p_174876_1_, this.world, this.position);
      }

      public String getGuiID() {
         return "minecraft:crafting_table";
      }
   }
}
