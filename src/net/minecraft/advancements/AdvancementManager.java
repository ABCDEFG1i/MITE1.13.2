package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.advancements.criterion.*;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureRequirements;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AdvancementManager implements IResourceManagerReloadListener {
    public static final int EXTENSION_LENGTH = ".json".length();
    public static final int field_195441_a = "advancements/".length();
    private static final AdvancementList ADVANCEMENT_LIST = new AdvancementList();
    private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(Advancement.Builder.class,
            (JsonDeserializer<Advancement.Builder>) (p_210124_0_, p_210124_1_, p_210124_2_) -> {
                JsonObject jsonobject = JsonUtils.getJsonObject(p_210124_0_, "advancement");
                return Advancement.Builder.deserialize(jsonobject, p_210124_2_);
            })
            .registerTypeAdapter(AdvancementRewards.class, new AdvancementRewards.Deserializer())
            .registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer())
            .registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
            .registerTypeAdapterFactory(new EnumTypeAdapterFactory())
            .create();
    private static final Logger LOGGER = LogManager.getLogger();
    private boolean hasErrored;

    private void addStructureAdvancements() {
        Set<String> structures = Structure.STRUCTURES.keySet();
        Map<String, Criterion> criterionMap = new HashMap<>();
        criterionMap.put("level", new Criterion(new LevelTrigger.Instance(10).setGlobal()));
        ResourceLocation rootLocation = new ResourceLocation("minecraft:structure/root");
        Advancement root = new Advancement(rootLocation, null,
                new DisplayInfo(Blocks.OAK_FENCE.asItem().getDefaultInstance(),
                        new TextComponentTranslation("advancements.structure.root.title"),
                        new TextComponentTranslation("advancements.structure.root.description"),
                        new ResourceLocation("minecraft:textures/gui/advancements/backgrounds/structure.png"),
                        FrameType.TASK, true, true, false), AdvancementRewards.EMPTY, criterionMap,
                RequirementsStrategy.AND.createRequirements(Lists.newArrayList("level")));
        ADVANCEMENT_LIST.addAdvancements(rootLocation, root);
        for (String singleStructure : structures) {
            Structure target = Structure.STRUCTURES.get(singleStructure);
            StructureRequirements requirements = target.getRequirements();
            Map<String, Criterion> criterion = Maps.newHashMap();
            List<String> advancementRequirements = Lists.newArrayList();
            ResourceLocation location = new ResourceLocation("minecraft:structure/" + singleStructure);
            if (requirements.hasDaysRequirement()) {
                criterion.put("days", new Criterion(new DaysTrigger.Instance(requirements.daysRequirement)));
                advancementRequirements.add("days");
            }
            if (requirements.hasPlayerLevelRequirement()){
                criterion.put("level",new Criterion(new LevelTrigger.Instance(requirements.playerLevelRequirement).setGlobal()));
                advancementRequirements.add("level");
            }
            if (requirements.hasItemsRequirement()){
                //noinspection ConstantConditions
                criterion.put("items",new Criterion(InventoryChangeTrigger.Instance.createFromItemItems(requirements.itemsRequirement).setGlobal()));
                advancementRequirements.add("items");
            }
            if (requirements.needToKillEnderDragon) {
                criterion.put("enderDragon", new Criterion(KilledTrigger.Instance.func_203929_a(
                        EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.ENDER_DRAGON),
                        DamageSourcePredicate.Builder.func_203981_a().func_203980_b(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.PLAYER))
                        ).setGlobal(true)));
                advancementRequirements.add("enderDragon");
            }
            StructureAdvancements advancement = new StructureAdvancements(location, root,
                    new DisplayInfo(target.getSymbolItem().getDefaultInstance(),
                            new TextComponentTranslation("advancements.structure." + singleStructure + ".title"),
                            new TextComponentTranslation("advancements.structure." + singleStructure + ".description"),
                            null, FrameType.GOAL, true, true, false), target, criterion,
                    RequirementsStrategy.AND.createRequirements(advancementRequirements));
            ADVANCEMENT_LIST.addAdvancements(location, advancement);

        }
    }

    @Nullable
    public Advancement getAdvancement(ResourceLocation p_192778_1_) {
        return ADVANCEMENT_LIST.getAdvancement(p_192778_1_);
    }

    public Collection<Advancement> getAllAdvancements() {
        return ADVANCEMENT_LIST.getAll();
    }

    public Map<ResourceLocation, Advancement.Builder> loadCustomAdvancements(IResourceManager p_195439_1_) {
        Map<ResourceLocation, Advancement.Builder> map = Maps.newHashMap();

        for (ResourceLocation resourcelocation : p_195439_1_.getAllResourceLocations("advancements", (p_195440_0_) -> {
            return p_195440_0_.endsWith(".json");
        })) {
            String s = resourcelocation.getPath();
            ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(),
                    s.substring(field_195441_a, s.length() - EXTENSION_LENGTH));

            try (IResource iresource = p_195439_1_.getResource(resourcelocation)) {
                Advancement.Builder advancement$builder = JsonUtils.fromJson(GSON,
                        IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8),
                        Advancement.Builder.class);
                if (advancement$builder == null) {
                    LOGGER.error("Couldn't load custom advancement {} from {} as it's empty or null", resourcelocation1,
                            resourcelocation);
                } else {
                    map.put(resourcelocation1, advancement$builder);
                }
            } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
                LOGGER.error("Parsing error loading custom advancement {}: {}", resourcelocation1,
                        jsonparseexception.getMessage());
                this.hasErrored = true;
            } catch (IOException ioexception) {
                LOGGER.error("Couldn't read custom advancement {} from {}", resourcelocation1, resourcelocation,
                        ioexception);
                this.hasErrored = true;
            }
        }

        return map;
    }

    public void onResourceManagerReload(IResourceManager p_195410_1_) {
        this.hasErrored = false;
        ADVANCEMENT_LIST.clear();
        Map<ResourceLocation, Advancement.Builder> map = this.loadCustomAdvancements(p_195410_1_);
        ADVANCEMENT_LIST.loadAdvancements(map);
        addStructureAdvancements();

        for (Advancement advancement : ADVANCEMENT_LIST.getRoots()) {
            if (advancement.getDisplay() != null) {
                AdvancementTreeNode.layout(advancement);
            }
        }

    }
}
