package net.minecraft.entity.item;

import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityMinecartChest extends EntityMinecartContainer {
   public EntityMinecartChest(World p_i1714_1_) {
      super(EntityType.CHEST_MINECART, p_i1714_1_);
   }

   public EntityMinecartChest(World p_i1715_1_, double p_i1715_2_, double p_i1715_4_, double p_i1715_6_) {
      super(EntityType.CHEST_MINECART, p_i1715_2_, p_i1715_4_, p_i1715_6_, p_i1715_1_);
   }

   public void killMinecart(DamageSource p_94095_1_) {
      super.killMinecart(p_94095_1_);
      if (this.world.getGameRules().getBoolean("doEntityDrops")) {
         this.entityDropItem(Blocks.CHEST);
      }

   }

   public int getSizeInventory() {
      return 27;
   }

   public EntityMinecart.Type getMinecartType() {
      return EntityMinecart.Type.CHEST;
   }

   public IBlockState getDefaultDisplayTile() {
      return Blocks.CHEST.getDefaultState().with(BlockChest.FACING, EnumFacing.NORTH);
   }

   public int getDefaultDisplayTileOffset() {
      return 8;
   }

   public String getGuiID() {
      return "minecraft:chest";
   }

   public Container createContainer(InventoryPlayer p_174876_1_, EntityPlayer p_174876_2_) {
      this.addLoot(p_174876_2_);
      return new ContainerChest(p_174876_1_, this, p_174876_2_);
   }
}
