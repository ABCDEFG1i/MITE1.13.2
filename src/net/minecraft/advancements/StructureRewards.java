package net.minecraft.advancements;

import net.minecraft.command.FunctionObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureRequirements;

import java.util.List;

public class StructureRewards extends AdvancementRewards {
    private final Structure targetStructure;
    StructureRewards(Structure structureReward) {
        super(0, new ResourceLocation[0], new ResourceLocation[0], FunctionObject.CacheableFunction.EMPTY);
        this.targetStructure = structureReward;
    }
    @Override
    public void apply(EntityPlayerMP p_192113_1_) {
        List<StructureRequirements> requirements = p_192113_1_.world.getWorldInfo().hadRequirements;
        if (!requirements.contains(targetStructure.getRequirements())){
           requirements.add(targetStructure.getRequirements());
        }
    }
}
