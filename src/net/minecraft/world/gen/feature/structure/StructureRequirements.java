package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public enum StructureRequirements {
    ALL(20, null, 10, false),
    ZOMBIE_VILLAGE(60, Lists.newArrayList(Items.IRON_PICKAXE), 30, false),
    NORMAL_VILLAGE(130, Lists.newArrayList(Items.TUNGSTEN_PICKAXE, Items.CARROT, Items.POTATO, Items.WHEAT, Items.BEETROOT), 60,
            false),
    MANSION(90, null, 60, true),
    MONUMENT(80, Lists.newArrayList(Items.MITHRIL_PICKAXE, Blocks.GOLD_BLOCK.asItem()), 40, false);

    public final int daysRequirement;
    public final ArrayList<Item> itemsRequirement;
    public final boolean needToKillEnderDragon;
    public final int playerLevelRequirement;

    StructureRequirements(int daysRequirement, @Nullable ArrayList<Item> itemsRequirement, int playerLevelRequirement, boolean needToKillEnderDragon) {
        this.daysRequirement = daysRequirement;
        this.itemsRequirement = itemsRequirement;
        this.playerLevelRequirement = playerLevelRequirement;
        this.needToKillEnderDragon = needToKillEnderDragon;
    }

    public boolean hasDaysRequirement() {
        return daysRequirement!=0;
    }

    public boolean hasItemsRequirement() {
        return itemsRequirement!=null;
    }

    public boolean hasPlayerLevelRequirement() {
        return playerLevelRequirement!=0;
    }
}
