package net.minecraft.advancements;

import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.structure.Structure;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;

public class StructureAdvancements extends Advancement {
    public static final ArrayList structureAdvancements = Lists.newArrayList();
    public StructureAdvancements(ResourceLocation id, @Nullable Advancement parent, @Nullable DisplayInfo displayInfo, Structure structureReward, Map<String, Criterion> criterion, String[][] requirements) {
        super(id, parent, displayInfo, new StructureRewards(structureReward), criterion, requirements);
    }
}
