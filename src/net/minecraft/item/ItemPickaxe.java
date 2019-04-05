package net.minecraft.item;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.IMineLevel;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import java.util.Set;

public class ItemPickaxe extends ItemTool {
    private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE,
            Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.POWERED_RAIL,
            Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK,
            Blocks.LAPIS_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.BLUE_ICE,
            Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE,
            Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.GRANITE,
            Blocks.POLISHED_GRANITE, Blocks.DIORITE, Blocks.POLISHED_DIORITE, Blocks.ANDESITE, Blocks.POLISHED_ANDESITE,
            Blocks.STONE_SLAB, Blocks.SANDSTONE_SLAB, Blocks.PETRIFIED_OAK_SLAB, Blocks.COBBLESTONE_SLAB,
            Blocks.BRICK_SLAB, Blocks.STONE_BRICK_SLAB, Blocks.NETHER_BRICK_SLAB, Blocks.QUARTZ_SLAB,
            Blocks.RED_SANDSTONE_SLAB, Blocks.PURPUR_SLAB, Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_RED_SANDSTONE,
            Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_STONE, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE);

    protected ItemPickaxe(int maxUses,IItemTier itemTier, int attackDamage, float attackSpeed, Item.Properties properties) {
        super(maxUses,(float) attackDamage, attackSpeed, itemTier, EFFECTIVE_ON, properties);
    }


    public boolean canHarvestBlock(IBlockState p_150897_1_) {
        Block block = p_150897_1_.getBlock();
        int i = this.getTier().getHarvestLevel();
        if (block instanceof IMineLevel){
            return i >= ((IMineLevel) block).getMineLevel();
        }else{
            Material material = p_150897_1_.getMaterial();
            return material == Material.ROCK || material == Material.IRON || material == Material.ANVIL;
        }
    }

    public float getDestroySpeed(ItemStack itemstack, IBlockState targetBlock) {
        Material material = targetBlock.getMaterial();
        if (material != Material.IRON && material != Material.ANVIL && material != Material.ROCK) {
            return super.getDestroySpeed(itemstack, targetBlock);
        } else {
            return this.efficiency;
        }
    }
}
