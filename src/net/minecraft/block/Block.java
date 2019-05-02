package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.trees.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.StatList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

public class Block implements IItemProvider {
    public static final ObjectIntIdentityMap<IBlockState> BLOCK_STATE_IDS = new ObjectIntIdentityMap<>();
    protected static final Logger LOGGER = LogManager.getLogger();
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>> SHOULD_SIDE_RENDER_CACHE = ThreadLocal
            .withInitial(() -> {
                Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>(
                        200) {
                    protected void rehash(int p_rehash_1_) {
                    }
                };
                object2bytelinkedopenhashmap.defaultReturnValue((byte) 127);
                return object2bytelinkedopenhashmap;
            });
    private static final EnumFacing[] field_212556_a = new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST,
            EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.DOWN, EnumFacing.UP};
    protected final float blockHardness;
    protected final MaterialColor blockMapColor;
    protected final float blockResistance;
    protected final boolean isSolid;
    protected final int lightValue;
    protected final Material material;
    protected final boolean needsRandomTick;
    protected final SoundType soundType;
    protected final StateContainer<Block, IBlockState> stateContainer;
    private final float slipperiness;
    private final boolean variableOpacity;
    private IBlockState defaultState;
    @Nullable
    private String translationKey;

    public Block(Block.Properties p_i48440_1_) {
        StateContainer.Builder<Block, IBlockState> builder = new StateContainer.Builder<>(this);
        this.fillStateContainer(builder);
        this.stateContainer = builder.create(BlockState::new);
        this.setDefaultState(this.stateContainer.getBaseState());
        this.material = p_i48440_1_.material;
        this.blockMapColor = p_i48440_1_.blockMapColor;
        this.isSolid = p_i48440_1_.isSolid;
        this.soundType = p_i48440_1_.soundType;
        this.lightValue = p_i48440_1_.lightValue;
        this.blockResistance = p_i48440_1_.blockResistance;
        this.blockHardness = p_i48440_1_.blockHardness;
        this.needsRandomTick = p_i48440_1_.needsRandomTick;
        this.slipperiness = p_i48440_1_.isSlipperiness;
        this.variableOpacity = p_i48440_1_.variableOpacity;
    }

    @Nullable
    public static RayTraceResult collisionRayTrace(IBlockState p_180636_0_, World p_180636_1_, BlockPos p_180636_2_, Vec3d p_180636_3_, Vec3d p_180636_4_) {
        RayTraceResult raytraceresult = p_180636_0_.getShape(p_180636_1_, p_180636_2_)
                .func_212433_a(p_180636_3_, p_180636_4_, p_180636_2_);
        if (raytraceresult != null) {
            RayTraceResult raytraceresult1 = p_180636_0_.getRaytraceShape(p_180636_1_, p_180636_2_)
                    .func_212433_a(p_180636_3_, p_180636_4_, p_180636_2_);
            if (raytraceresult1 != null && raytraceresult1.hitVec.subtract(p_180636_3_)
                    .lengthSquared() < raytraceresult.hitVec.subtract(p_180636_3_).lengthSquared()) {
                raytraceresult.sideHit = raytraceresult1.sideHit;
            }
        }

        return raytraceresult;
    }

    public static boolean doesSideFillSquare(VoxelShape p_208061_0_, EnumFacing p_208061_1_) {
        VoxelShape voxelshape = p_208061_0_.func_212434_a(p_208061_1_);
        return isOpaque(voxelshape);
    }

    public static IBlockState func_199601_a(IBlockState p_199601_0_, IBlockState p_199601_1_, World p_199601_2_, BlockPos p_199601_3_) {
        VoxelShape voxelshape = VoxelShapes.func_197882_b(p_199601_0_.getCollisionShape(p_199601_2_, p_199601_3_),
                p_199601_1_.getCollisionShape(p_199601_2_, p_199601_3_), IBooleanFunction.ONLY_SECOND)
                .withOffset((double) p_199601_3_.getX(), (double) p_199601_3_.getY(), (double) p_199601_3_.getZ());

        for (Entity entity : p_199601_2_.func_72839_b(null, voxelshape.getBoundingBox())) {
            double d0 = VoxelShapes.func_212437_a(EnumFacing.Axis.Y,
                    entity.getEntityBoundingBox().offset(0.0D, 1.0D, 0.0D), Stream.of(voxelshape), -1.0D);
            entity.setPositionAndUpdate(entity.posX, entity.posY + 1.0D + d0, entity.posZ);
        }

        return p_199601_1_;
    }

    public static Block getBlockFromItem(@Nullable Item p_149634_0_) {
        return p_149634_0_ instanceof ItemBlock ? ((ItemBlock) p_149634_0_).getBlock() : Blocks.AIR;
    }

    public static IBlockState getStateById(int p_196257_0_) {
        IBlockState iblockstate = BLOCK_STATE_IDS.getByValue(p_196257_0_);
        return iblockstate == null ? Blocks.AIR.getDefaultState() : iblockstate;
    }

    public static int getStateId(@Nullable IBlockState p_196246_0_) {
        if (p_196246_0_ == null) {
            return 0;
        } else {
            int i = BLOCK_STATE_IDS.get(p_196246_0_);
            return i == -1 ? 0 : i;
        }
    }

    public static IBlockState getValidBlockForPosition(IBlockState p_199770_0_, IWorld p_199770_1_, BlockPos p_199770_2_) {
        IBlockState iblockstate = p_199770_0_;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (EnumFacing enumfacing : field_212556_a) {
            blockpos$mutableblockpos.setPos(p_199770_2_).move(enumfacing);
            iblockstate = iblockstate.updatePostPlacement(enumfacing,
                    p_199770_1_.getBlockState(blockpos$mutableblockpos), p_199770_1_, p_199770_2_,
                    blockpos$mutableblockpos);
        }

        return iblockstate;
    }

    public static boolean isDirt(Block p_196245_0_) {
        return p_196245_0_ == Blocks.DIRT || p_196245_0_ == Blocks.COARSE_DIRT || p_196245_0_ == Blocks.PODZOL;
    }

    public static boolean isExceptBlockForAttachWithPiston(Block p_193382_0_) {
        return isExceptionBlockForAttaching(
                p_193382_0_) || p_193382_0_ == Blocks.PISTON || p_193382_0_ == Blocks.STICKY_PISTON || p_193382_0_ == Blocks.PISTON_HEAD;
    }

    protected static boolean isExceptionBlockForAttaching(Block p_193384_0_) {
        return p_193384_0_ instanceof BlockShulkerBox || p_193384_0_ instanceof BlockLeaves || p_193384_0_.isIn(
                BlockTags.TRAPDOORS) || p_193384_0_ instanceof BlockStainedGlass || p_193384_0_ == Blocks.BEACON || p_193384_0_ == Blocks.CAULDRON || p_193384_0_ == Blocks.GLASS || p_193384_0_ == Blocks.GLOWSTONE || p_193384_0_ == Blocks.ICE || p_193384_0_ == Blocks.SEA_LANTERN || p_193384_0_ == Blocks.CONDUIT;
    }

    public static boolean isOpaque(VoxelShape p_208062_0_) {
        return !VoxelShapes.func_197879_c(VoxelShapes.func_197868_b(), p_208062_0_, IBooleanFunction.ONLY_FIRST);
    }

    public static boolean isRock(Block p_196252_0_) {
        return p_196252_0_ == Blocks.STONE || p_196252_0_ == Blocks.GRANITE || p_196252_0_ == Blocks.DIORITE || p_196252_0_ == Blocks.ANDESITE;
    }

    public static VoxelShape makeCuboidShape(double p_208617_0_, double p_208617_2_, double p_208617_4_, double p_208617_6_, double p_208617_8_, double p_208617_10_) {
        return VoxelShapes.func_197873_a(p_208617_0_ / 16.0D, p_208617_2_ / 16.0D, p_208617_4_ / 16.0D,
                p_208617_6_ / 16.0D, p_208617_8_ / 16.0D, p_208617_10_ / 16.0D);
    }

    private static void register(ResourceLocation p_196249_0_, Block p_196249_1_) {
        IRegistry.field_212618_g.func_82595_a(p_196249_0_, p_196249_1_);
    }

    private static void register(String resourceLocation, Block block) {
        register(new ResourceLocation(resourceLocation), block);
    }

    public static void registerBlocks() {
        Block air = new BlockAir(Block.Properties.createBlockProperties(Material.AIR).setNonSolid());
        register(IRegistry.field_212618_g.func_212609_b(), air);
        Block stone = new BlockStone(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.STONE)
                .setHardnessAndResistance(2.4F, 6.0F));
        register("stone", stone);
        register("granite", new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.DIRT)
                .setHardnessAndResistance(1.5F, 6.0F)));
        register("polished_granite", new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.DIRT)
                .setHardnessAndResistance(1.5F, 6.0F)));
        register("diorite", new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.QUARTZ)
                .setHardnessAndResistance(1.5F, 6.0F)));
        register("polished_diorite", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.QUARTZ)
                        .setHardnessAndResistance(1.5F, 6.0F)));
        register("andesite", new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.STONE)
                .setHardnessAndResistance(1.5F, 6.0F)));
        register("polished_andesite", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.STONE)
                        .setHardnessAndResistance(1.5F, 6.0F)));
        register("grass_block", new BlockGrass(Block.Properties.createBlockProperties(Material.GRASS)
                .needsRandomTick()
                .setHardnessAndResistance(2.1F)
                .setSoundType(SoundType.PLANT)));
        register("dirt", new BlockFalling(Block.Properties.createBlockProperties(Material.GROUND, MaterialColor.DIRT)
                .setHardnessAndResistance(1.9F)
                .setSoundType(SoundType.GROUND)));
        register("coarse_dirt", new Block(Block.Properties.createBlockProperties(Material.GROUND, MaterialColor.DIRT)
                .setHardnessAndResistance(2.1F)
                .setSoundType(SoundType.GROUND)));
        register("podzol", new BlockDirtSnowy(
                Block.Properties.createBlockProperties(Material.GROUND, MaterialColor.OBSIDIAN)
                        .setHardnessAndResistance(1.9F)
                        .setSoundType(SoundType.GROUND)));
        Block cobblestone = new Block(
                Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(2.0F, 6.0F));
        register("cobblestone", cobblestone);
        Block oakPlanks = new Block(Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.WOOD)
                .setHardnessAndResistance(2.0F, 3.0F)
                .setSoundType(SoundType.WOOD));
        Block sprucePlanks = new Block(Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.OBSIDIAN)
                .setHardnessAndResistance(2.0F, 3.0F)
                .setSoundType(SoundType.WOOD));
        Block birchPlanks = new Block(Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.SAND)
                .setHardnessAndResistance(2.0F, 3.0F)
                .setSoundType(SoundType.WOOD));
        Block junglePlanks = new Block(Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.DIRT)
                .setHardnessAndResistance(2.0F, 3.0F)
                .setSoundType(SoundType.WOOD));
        Block acaciaPlanks = new Block(Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.ADOBE)
                .setHardnessAndResistance(2.0F, 3.0F)
                .setSoundType(SoundType.WOOD));
        Block darkOakPlanks = new Block(Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.BROWN)
                .setHardnessAndResistance(2.0F, 3.0F)
                .setSoundType(SoundType.WOOD));
        register("oak_planks", oakPlanks);
        register("spruce_planks", sprucePlanks);
        register("birch_planks", birchPlanks);
        register("jungle_planks", junglePlanks);
        register("acacia_planks", acaciaPlanks);
        register("dark_oak_planks", darkOakPlanks);
        Block oakSapling = new BlockSapling(new OakTree(), Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .needsRandomTick()
                .instantDestruction()
                .setSoundType(SoundType.PLANT));
        Block spruceSapling = new BlockSapling(new SpruceTree(), Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .needsRandomTick()
                .instantDestruction()
                .setSoundType(SoundType.PLANT));
        Block birchSapling = new BlockSapling(new BirchTree(), Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .needsRandomTick()
                .instantDestruction()
                .setSoundType(SoundType.PLANT));
        Block jugleSapling = new BlockSapling(new JungleTree(), Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .needsRandomTick()
                .instantDestruction()
                .setSoundType(SoundType.PLANT));
        Block acaciaSapling = new BlockSapling(new AcaciaTree(), Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .needsRandomTick()
                .instantDestruction()
                .setSoundType(SoundType.PLANT));
        Block darkOakSapling = new BlockSapling(new DarkOakTree(),
                Block.Properties.createBlockProperties(Material.PLANTS)
                        .setNonSolid()
                        .needsRandomTick()
                        .instantDestruction()
                        .setSoundType(SoundType.PLANT));
        register("oak_sapling", oakSapling);
        register("spruce_sapling", spruceSapling);
        register("birch_sapling", birchSapling);
        register("jungle_sapling", jugleSapling);
        register("acacia_sapling", acaciaSapling);
        register("dark_oak_sapling", darkOakSapling);
        register("bedrock", new BlockEmptyDrops(
                Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(-1.0F, 3600000.0F)));
        register("water", new BlockFlowingFluid(Fluids.WATER,
                Block.Properties.createBlockProperties(Material.WATER).setNonSolid().setHardnessAndResistance(100.0F)));
        register("lava", new BlockFlowingFluid(Fluids.LAVA, Block.Properties.createBlockProperties(Material.LAVA)
                .setNonSolid()
                .needsRandomTick()
                .setHardnessAndResistance(100.0F)
                .setLightLevel(15)));
        register("sand", new BlockSand(14406560,
                Block.Properties.createBlockProperties(Material.SAND, MaterialColor.SAND)
                        .setHardnessAndResistance(1.9F)
                        .setSoundType(SoundType.SAND)));
        register("red_sand", new BlockSand(11098145,
                Block.Properties.createBlockProperties(Material.SAND, MaterialColor.ADOBE)
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.SAND)));
        register("gravel", new BlockGravel(Block.Properties.createBlockProperties(Material.SAND, MaterialColor.STONE)
                .setHardnessAndResistance(1.9F)
                .setSoundType(SoundType.GROUND)));
        register("gold_ore",
                new BlockOre(Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(3.0F, 3.0F),
                        2));
        register("iron_ore",
                new BlockOre(Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(3.0F, 3.0F),
                        2));
        register("coal_ore",
                new BlockOre(Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(3.0F, 3.0F),
                        2));
        register("oak_log", new BlockLog(MaterialColor.WOOD,
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.OBSIDIAN)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("spruce_log", new BlockLog(MaterialColor.OBSIDIAN,
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.BROWN)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("birch_log", new BlockLog(MaterialColor.SAND,
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.QUARTZ)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("jungle_log", new BlockLog(MaterialColor.DIRT,
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.OBSIDIAN)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("acacia_log", new BlockLog(MaterialColor.ADOBE,
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.STONE)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("dark_oak_log", new BlockLog(MaterialColor.BROWN,
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.BROWN)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("stripped_spruce_log", new BlockLog(MaterialColor.OBSIDIAN,
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.OBSIDIAN)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("stripped_birch_log", new BlockLog(MaterialColor.SAND,
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.SAND)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("stripped_jungle_log", new BlockLog(MaterialColor.DIRT,
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.DIRT)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("stripped_acacia_log", new BlockLog(MaterialColor.ADOBE,
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.ADOBE)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("stripped_dark_oak_log", new BlockLog(MaterialColor.BROWN,
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.BROWN)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("stripped_oak_log", new BlockLog(MaterialColor.WOOD,
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.WOOD)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("oak_wood", new BlockRotatedPillar(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.WOOD)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("spruce_wood", new BlockRotatedPillar(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.OBSIDIAN)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("birch_wood", new BlockRotatedPillar(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.SAND)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("jungle_wood", new BlockRotatedPillar(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.DIRT)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("acacia_wood", new BlockRotatedPillar(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.ADOBE)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("dark_oak_wood", new BlockRotatedPillar(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.BROWN)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("stripped_oak_wood", new BlockRotatedPillar(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.WOOD)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("stripped_spruce_wood", new BlockRotatedPillar(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.OBSIDIAN)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("stripped_birch_wood", new BlockRotatedPillar(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.SAND)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("stripped_jungle_wood", new BlockRotatedPillar(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.DIRT)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("stripped_acacia_wood", new BlockRotatedPillar(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.ADOBE)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("stripped_dark_oak_wood", new BlockRotatedPillar(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.BROWN)
                        .setHardnessAndResistance(2.0F)
                        .setSoundType(SoundType.WOOD)));
        register("oak_leaves", new BlockLeaves(Block.Properties.createBlockProperties(Material.LEAVES)
                .setHardnessAndResistance(1.0F)
                .needsRandomTick()
                .setSoundType(SoundType.PLANT)));
        register("spruce_leaves", new BlockLeaves(Block.Properties.createBlockProperties(Material.LEAVES)
                .setHardnessAndResistance(1.0F)
                .needsRandomTick()
                .setSoundType(SoundType.PLANT)));
        register("birch_leaves", new BlockLeaves(Block.Properties.createBlockProperties(Material.LEAVES)
                .setHardnessAndResistance(1.0F)
                .needsRandomTick()
                .setSoundType(SoundType.PLANT)));
        register("jungle_leaves", new BlockLeaves(Block.Properties.createBlockProperties(Material.LEAVES)
                .setHardnessAndResistance(1.0F)
                .needsRandomTick()
                .setSoundType(SoundType.PLANT)));
        register("acacia_leaves", new BlockLeaves(Block.Properties.createBlockProperties(Material.LEAVES)
                .setHardnessAndResistance(1.0F)
                .needsRandomTick()
                .setSoundType(SoundType.PLANT)));
        register("dark_oak_leaves", new BlockLeaves(Block.Properties.createBlockProperties(Material.LEAVES)
                .setHardnessAndResistance(1.0F)
                .needsRandomTick()
                .setSoundType(SoundType.PLANT)));
        register("sponge", new BlockSponge(Block.Properties.createBlockProperties(Material.SPONGE)
                .setHardnessAndResistance(0.6F)
                .setSoundType(SoundType.PLANT)));
        register("wet_sponge", new BlockWetSponge(Block.Properties.createBlockProperties(Material.SPONGE)
                .setHardnessAndResistance(0.6F)
                .setSoundType(SoundType.PLANT)));
        register("glass", new BlockGlass(Block.Properties.createBlockProperties(Material.GLASS)
                .setHardnessAndResistance(0.3F)
                .setSoundType(SoundType.GLASS)));
        register("lapis_ore",
                new BlockOre(Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(3.0F, 3.0F),
                        2));
        register("lapis_block", new BlockMaterial(
                Block.Properties.createBlockProperties(Material.IRON, MaterialColor.LAPIS)
                        .setHardnessAndResistance(3.0F, 3.0F), 2));
        register("dispenser", new BlockDispenser(
                Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(3.5F)));
        Block block15 = new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.SAND)
                .setHardnessAndResistance(0.8F));
        register("sandstone", block15);
        register("chiseled_sandstone", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.SAND)
                        .setHardnessAndResistance(0.8F)));
        register("cut_sandstone", new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.SAND)
                .setHardnessAndResistance(0.8F)));
        register("note_block", new BlockNote(Block.Properties.createBlockProperties(Material.WOOD)
                .setSoundType(SoundType.WOOD)
                .setHardnessAndResistance(0.8F)));
        register("white_bed", new BlockBed(EnumDyeColor.WHITE, Block.Properties.createBlockProperties(Material.CLOTH)
                .setSoundType(SoundType.WOOD)
                .setHardnessAndResistance(0.2F)));
        register("orange_bed", new BlockBed(EnumDyeColor.ORANGE, Block.Properties.createBlockProperties(Material.CLOTH)
                .setSoundType(SoundType.WOOD)
                .setHardnessAndResistance(0.2F)));
        register("magenta_bed", new BlockBed(EnumDyeColor.MAGENTA,
                Block.Properties.createBlockProperties(Material.CLOTH)
                        .setSoundType(SoundType.WOOD)
                        .setHardnessAndResistance(0.2F)));
        register("light_blue_bed", new BlockBed(EnumDyeColor.LIGHT_BLUE,
                Block.Properties.createBlockProperties(Material.CLOTH)
                        .setSoundType(SoundType.WOOD)
                        .setHardnessAndResistance(0.2F)));
        register("yellow_bed", new BlockBed(EnumDyeColor.YELLOW, Block.Properties.createBlockProperties(Material.CLOTH)
                .setSoundType(SoundType.WOOD)
                .setHardnessAndResistance(0.2F)));
        register("lime_bed", new BlockBed(EnumDyeColor.LIME, Block.Properties.createBlockProperties(Material.CLOTH)
                .setSoundType(SoundType.WOOD)
                .setHardnessAndResistance(0.2F)));
        register("pink_bed", new BlockBed(EnumDyeColor.PINK, Block.Properties.createBlockProperties(Material.CLOTH)
                .setSoundType(SoundType.WOOD)
                .setHardnessAndResistance(0.2F)));
        register("gray_bed", new BlockBed(EnumDyeColor.GRAY, Block.Properties.createBlockProperties(Material.CLOTH)
                .setSoundType(SoundType.WOOD)
                .setHardnessAndResistance(0.2F)));
        register("light_gray_bed", new BlockBed(EnumDyeColor.LIGHT_GRAY,
                Block.Properties.createBlockProperties(Material.CLOTH)
                        .setSoundType(SoundType.WOOD)
                        .setHardnessAndResistance(0.2F)));
        register("cyan_bed", new BlockBed(EnumDyeColor.CYAN, Block.Properties.createBlockProperties(Material.CLOTH)
                .setSoundType(SoundType.WOOD)
                .setHardnessAndResistance(0.2F)));
        register("purple_bed", new BlockBed(EnumDyeColor.PURPLE, Block.Properties.createBlockProperties(Material.CLOTH)
                .setSoundType(SoundType.WOOD)
                .setHardnessAndResistance(0.2F)));
        register("blue_bed", new BlockBed(EnumDyeColor.BLUE, Block.Properties.createBlockProperties(Material.CLOTH)
                .setSoundType(SoundType.WOOD)
                .setHardnessAndResistance(0.2F)));
        register("brown_bed", new BlockBed(EnumDyeColor.BROWN, Block.Properties.createBlockProperties(Material.CLOTH)
                .setSoundType(SoundType.WOOD)
                .setHardnessAndResistance(0.2F)));
        register("green_bed", new BlockBed(EnumDyeColor.GREEN, Block.Properties.createBlockProperties(Material.CLOTH)
                .setSoundType(SoundType.WOOD)
                .setHardnessAndResistance(0.2F)));
        register("red_bed", new BlockBed(EnumDyeColor.RED, Block.Properties.createBlockProperties(Material.CLOTH)
                .setSoundType(SoundType.WOOD)
                .setHardnessAndResistance(0.2F)));
        register("black_bed", new BlockBed(EnumDyeColor.BLACK, Block.Properties.createBlockProperties(Material.CLOTH)
                .setSoundType(SoundType.WOOD)
                .setHardnessAndResistance(0.2F)));
        register("powered_rail", new BlockRailPowered(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .setNonSolid()
                .setHardnessAndResistance(0.7F)
                .setSoundType(SoundType.METAL)));
        register("detector_rail", new BlockRailDetector(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .setNonSolid()
                .setHardnessAndResistance(0.7F)
                .setSoundType(SoundType.METAL)));
        register("sticky_piston", new BlockPistonBase(true,
                Block.Properties.createBlockProperties(Material.PISTON).setHardnessAndResistance(0.5F)));
        register("cobweb", new BlockWeb(
                Block.Properties.createBlockProperties(Material.WEB).setNonSolid().setHardnessAndResistance(4.0F)));
        Block grass = new BlockTallGrass(Block.Properties.createBlockProperties(Material.VINE)
                .setNonSolid()
                .setHardnessAndResistance(0.1F)
                .setSoundType(SoundType.PLANT));
        Block fern = new BlockTallGrass(Block.Properties.createBlockProperties(Material.VINE)
                .setNonSolid()
                .setHardnessAndResistance(0.1F)
                .setSoundType(SoundType.PLANT));
        Block deadBush = new BlockDeadBush(Block.Properties.createBlockProperties(Material.VINE, MaterialColor.WOOD)
                .setNonSolid()
                .setHardnessAndResistance(0.1F)
                .setSoundType(SoundType.PLANT));
        register("grass", grass);
        register("fern", fern);
        register("dead_bush", deadBush);
        Block seaGrass = new BlockSeaGrass(Block.Properties.createBlockProperties(Material.SEA_GRASS)
                .setNonSolid()
                .instantDestruction()
                .setSoundType(SoundType.WET_GRASS));
        register("seagrass", seaGrass);
        register("tall_seagrass", new BlockSeaGrassTall(seaGrass,
                Block.Properties.createBlockProperties(Material.SEA_GRASS)
                        .setNonSolid()
                        .setHardnessAndResistance(0.1F)
                        .setSoundType(SoundType.WET_GRASS)));
        register("piston", new BlockPistonBase(false,
                Block.Properties.createBlockProperties(Material.PISTON).setHardnessAndResistance(0.5F)));
        register("piston_head", new BlockPistonExtension(
                Block.Properties.createBlockProperties(Material.PISTON).setHardnessAndResistance(0.5F)));
        register("white_wool", new Block(Block.Properties.createBlockProperties(Material.CLOTH, MaterialColor.SNOW)
                .setHardnessAndResistance(0.8F)
                .setSoundType(SoundType.CLOTH)));
        register("orange_wool", new Block(Block.Properties.createBlockProperties(Material.CLOTH, MaterialColor.ADOBE)
                .setHardnessAndResistance(0.8F)
                .setSoundType(SoundType.CLOTH)));
        register("magenta_wool", new Block(Block.Properties.createBlockProperties(Material.CLOTH, MaterialColor.MAGENTA)
                .setHardnessAndResistance(0.8F)
                .setSoundType(SoundType.CLOTH)));
        register("light_blue_wool", new Block(
                Block.Properties.createBlockProperties(Material.CLOTH, MaterialColor.LIGHT_BLUE)
                        .setHardnessAndResistance(0.8F)
                        .setSoundType(SoundType.CLOTH)));
        register("yellow_wool", new Block(Block.Properties.createBlockProperties(Material.CLOTH, MaterialColor.YELLOW)
                .setHardnessAndResistance(0.8F)
                .setSoundType(SoundType.CLOTH)));
        register("lime_wool", new Block(Block.Properties.createBlockProperties(Material.CLOTH, MaterialColor.LIME)
                .setHardnessAndResistance(0.8F)
                .setSoundType(SoundType.CLOTH)));
        register("pink_wool", new Block(Block.Properties.createBlockProperties(Material.CLOTH, MaterialColor.PINK)
                .setHardnessAndResistance(0.8F)
                .setSoundType(SoundType.CLOTH)));
        register("gray_wool", new Block(Block.Properties.createBlockProperties(Material.CLOTH, MaterialColor.GRAY)
                .setHardnessAndResistance(0.8F)
                .setSoundType(SoundType.CLOTH)));
        register("light_gray_wool", new Block(
                Block.Properties.createBlockProperties(Material.CLOTH, MaterialColor.SILVER)
                        .setHardnessAndResistance(0.8F)
                        .setSoundType(SoundType.CLOTH)));
        register("cyan_wool", new Block(Block.Properties.createBlockProperties(Material.CLOTH, MaterialColor.CYAN)
                .setHardnessAndResistance(0.8F)
                .setSoundType(SoundType.CLOTH)));
        register("purple_wool", new Block(Block.Properties.createBlockProperties(Material.CLOTH, MaterialColor.PURPLE)
                .setHardnessAndResistance(0.8F)
                .setSoundType(SoundType.CLOTH)));
        register("blue_wool", new Block(Block.Properties.createBlockProperties(Material.CLOTH, MaterialColor.BLUE)
                .setHardnessAndResistance(0.8F)
                .setSoundType(SoundType.CLOTH)));
        register("brown_wool", new Block(Block.Properties.createBlockProperties(Material.CLOTH, MaterialColor.BROWN)
                .setHardnessAndResistance(0.8F)
                .setSoundType(SoundType.CLOTH)));
        register("green_wool", new Block(Block.Properties.createBlockProperties(Material.CLOTH, MaterialColor.GREEN)
                .setHardnessAndResistance(0.8F)
                .setSoundType(SoundType.CLOTH)));
        register("red_wool", new Block(Block.Properties.createBlockProperties(Material.CLOTH, MaterialColor.RED)
                .setHardnessAndResistance(0.8F)
                .setSoundType(SoundType.CLOTH)));
        register("black_wool", new Block(Block.Properties.createBlockProperties(Material.CLOTH, MaterialColor.BLACK)
                .setHardnessAndResistance(0.8F)
                .setSoundType(SoundType.CLOTH)));
        register("moving_piston", new BlockPistonMoving(Block.Properties.createBlockProperties(Material.PISTON)
                .setHardnessAndResistance(-1.0F)
                .variableOpacity()));
        Block block20 = new BlockFlower(Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .instantDestruction()
                .setSoundType(SoundType.PLANT));
        Block block21 = new BlockFlower(Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .instantDestruction()
                .setSoundType(SoundType.PLANT));
        Block block22 = new BlockFlower(Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .instantDestruction()
                .setSoundType(SoundType.PLANT));
        Block block23 = new BlockFlower(Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .instantDestruction()
                .setSoundType(SoundType.PLANT));
        Block block24 = new BlockFlower(Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .instantDestruction()
                .setSoundType(SoundType.PLANT));
        Block block25 = new BlockFlower(Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .instantDestruction()
                .setSoundType(SoundType.PLANT));
        Block block26 = new BlockFlower(Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .instantDestruction()
                .setSoundType(SoundType.PLANT));
        Block block27 = new BlockFlower(Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .instantDestruction()
                .setSoundType(SoundType.PLANT));
        Block pinkTulip = new BlockFlower(Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .instantDestruction()
                .setSoundType(SoundType.PLANT));
        Block oxeyDaisy = new BlockFlower(Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .instantDestruction()
                .setSoundType(SoundType.PLANT));
        register("dandelion", block20);
        register("poppy", block21);
        register("blue_orchid", block22);
        register("allium", block23);
        register("azure_bluet", block24);
        register("red_tulip", block25);
        register("orange_tulip", block26);
        register("white_tulip", block27);
        register("pink_tulip", pinkTulip);
        register("oxeye_daisy", oxeyDaisy);
        Block brownMushroom = new BlockMushroom(Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .needsRandomTick()
                .instantDestruction()
                .setSoundType(SoundType.PLANT)
                .setLightLevel(1));
        Block redMushroom = new BlockMushroom(Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .needsRandomTick()
                .instantDestruction()
                .setSoundType(SoundType.PLANT));
        register("brown_mushroom", brownMushroom);
        register("red_mushroom", redMushroom);
        register("gold_block", new BlockMaterial(
                Block.Properties.createBlockProperties(Material.IRON, MaterialColor.GOLD)
                        .setHardnessAndResistance(3.0F, 6.0F)
                        .setSoundType(SoundType.METAL), 3));
        register("iron_block", new BlockMaterial(
                Block.Properties.createBlockProperties(Material.IRON, MaterialColor.IRON)
                        .setHardnessAndResistance(5.0F, 6.0F)
                        .setSoundType(SoundType.METAL), 4));
        Block block32 = new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.RED)
                .setHardnessAndResistance(2.0F, 6.0F));
        register("bricks", block32);
        register("tnt", new BlockTNT(Block.Properties.createBlockProperties(Material.TNT)
                .instantDestruction()
                .setSoundType(SoundType.PLANT)));
        register("bookshelf", new BlockBookshelf(Block.Properties.createBlockProperties(Material.WOOD)
                .setHardnessAndResistance(1.5F)
                .setSoundType(SoundType.WOOD)));
        register("mossy_cobblestone",
                new Block(Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(2.0F, 6.0F)));
        register("obsidian", new BlockMaterial(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.BLACK)
                        .setHardnessAndResistance(50.0F, 1200.0F), 3));
        register("torch", new BlockTorch(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .setNonSolid()
                .instantDestruction()
                .setLightLevel(14)
                .setSoundType(SoundType.WOOD)));
        register("wall_torch", new BlockTorchWall(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .setNonSolid()
                .instantDestruction()
                .setLightLevel(14)
                .setSoundType(SoundType.WOOD)));
        register("fire", new BlockFire(Block.Properties.createBlockProperties(Material.FIRE, MaterialColor.TNT)
                .setNonSolid()
                .needsRandomTick()
                .instantDestruction()
                .setLightLevel(15)
                .setSoundType(SoundType.CLOTH)));
        register("spawner", new BlockMobSpawner(Block.Properties.createBlockProperties(Material.ROCK)
                .setHardnessAndResistance(5.0F)
                .setSoundType(SoundType.METAL)));
        register("oak_stairs", new BlockStairs(oakPlanks.getDefaultState(),
                Block.Properties.createBlockPropertiesFromBlock(oakPlanks)));
        register("chest", new BlockChest(Block.Properties.createBlockProperties(Material.WOOD)
                .setHardnessAndResistance(2.5F)
                .setSoundType(SoundType.WOOD)));
        register("redstone_wire", new BlockRedstoneWire(
                Block.Properties.createBlockProperties(Material.CIRCUITS).setNonSolid().instantDestruction()));
        register("diamond_ore",
                new BlockOre(Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(3.0F, 3.0F),
                        4));
        register("diamond_block", new BlockMaterial(
                Block.Properties.createBlockProperties(Material.IRON, MaterialColor.DIAMOND)
                        .setHardnessAndResistance(5.0F, 6.0F)
                        .setSoundType(SoundType.METAL), 5));
        register("wheat", new BlockCrops(Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .needsRandomTick()
                .instantDestruction()
                .setSoundType(SoundType.PLANT)));
        Block block33 = new BlockFarmland(Block.Properties.createBlockProperties(Material.GROUND)
                .needsRandomTick()
                .setHardnessAndResistance(0.6F)
                .setSoundType(SoundType.GROUND));
        register("farmland", block33);
        register("furnace", new BlockFurnace(Block.Properties.createBlockProperties(Material.FURNACE)
                .setHardnessAndResistance(3.5F)
                .setLightLevel(13)));
        register("sign", new BlockStandingSign(Block.Properties.createBlockProperties(Material.WOOD)
                .setNonSolid()
                .setHardnessAndResistance(1.0F)
                .setSoundType(SoundType.WOOD)));
        register("oak_door", new BlockDoor(
                Block.Properties.createBlockProperties(Material.WOOD, oakPlanks.blockMapColor)
                        .setHardnessAndResistance(3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("ladder", new BlockLadder(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .setHardnessAndResistance(0.4F)
                .setSoundType(SoundType.LADDER)));
        register("rail", new BlockRail(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .setNonSolid()
                .setHardnessAndResistance(0.7F)
                .setSoundType(SoundType.METAL)));
        register("cobblestone_stairs", new BlockStairs(cobblestone.getDefaultState(),
                Block.Properties.createBlockPropertiesFromBlock(cobblestone)));
        register("wall_sign", new BlockWallSign(Block.Properties.createBlockProperties(Material.WOOD)
                .setNonSolid()
                .setHardnessAndResistance(1.0F)
                .setSoundType(SoundType.WOOD)));
        register("lever", new BlockLever(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .setNonSolid()
                .setHardnessAndResistance(0.5F)
                .setSoundType(SoundType.WOOD)));
        register("stone_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.MOBS,
                Block.Properties.createBlockProperties(Material.ROCK).setNonSolid().setHardnessAndResistance(0.5F)));
        register("iron_door", new BlockDoor(Block.Properties.createBlockProperties(Material.IRON, MaterialColor.IRON)
                .setHardnessAndResistance(5.0F)
                .setSoundType(SoundType.METAL)));
        register("oak_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.EVERYTHING,
                Block.Properties.createBlockProperties(Material.WOOD, oakPlanks.blockMapColor)
                        .setNonSolid()
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.WOOD)));
        register("spruce_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.EVERYTHING,
                Block.Properties.createBlockProperties(Material.WOOD, sprucePlanks.blockMapColor)
                        .setNonSolid()
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.WOOD)));
        register("birch_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.EVERYTHING,
                Block.Properties.createBlockProperties(Material.WOOD, birchPlanks.blockMapColor)
                        .setNonSolid()
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.WOOD)));
        register("jungle_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.EVERYTHING,
                Block.Properties.createBlockProperties(Material.WOOD, junglePlanks.blockMapColor)
                        .setNonSolid()
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.WOOD)));
        register("acacia_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.EVERYTHING,
                Block.Properties.createBlockProperties(Material.WOOD, acaciaPlanks.blockMapColor)
                        .setNonSolid()
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.WOOD)));
        register("dark_oak_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.EVERYTHING,
                Block.Properties.createBlockProperties(Material.WOOD, darkOakPlanks.blockMapColor)
                        .setNonSolid()
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.WOOD)));
        register("redstone_ore", new BlockRedstoneOre(Block.Properties.createBlockProperties(Material.ROCK)
                .needsRandomTick()
                .setLightLevel(9)
                .setHardnessAndResistance(3.0F, 3.0F)));
        register("redstone_torch", new BlockRedstoneTorch(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .setNonSolid()
                .instantDestruction()
                .setLightLevel(7)
                .setSoundType(SoundType.WOOD)));
        register("redstone_wall_torch", new BlockRedstoneTorchWall(
                Block.Properties.createBlockProperties(Material.CIRCUITS)
                        .setNonSolid()
                        .instantDestruction()
                        .setLightLevel(7)
                        .setSoundType(SoundType.WOOD)));
        register("stone_button", new BlockButtonStone(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .setNonSolid()
                .setHardnessAndResistance(0.5F)));
        register("snow", new BlockSnowLayer(Block.Properties.createBlockProperties(Material.SNOW)
                .needsRandomTick()
                .setHardnessAndResistance(0.1F)
                .setSoundType(SoundType.SNOW)));
        register("ice", new BlockIce(Block.Properties.createBlockProperties(Material.ICE)
                .setSlipperiness(0.98F)
                .needsRandomTick()
                .setHardnessAndResistance(0.5F)
                .setSoundType(SoundType.GLASS)));
        register("snow_block", new BlockSnow(Block.Properties.createBlockProperties(Material.CRAFTED_SNOW)
                .needsRandomTick()
                .setHardnessAndResistance(0.2F)
                .setSoundType(SoundType.SNOW)));
        Block cactus = new BlockCactus(Block.Properties.createBlockProperties(Material.CACTUS)
                .needsRandomTick()
                .setHardnessAndResistance(0.4F)
                .setSoundType(SoundType.CLOTH));
        register("cactus", cactus);
        register("clay", new BlockClay(Block.Properties.createBlockProperties(Material.CLAY)
                .setHardnessAndResistance(0.6F)
                .setSoundType(SoundType.GROUND)));
        register("sugar_cane", new BlockReed(Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .needsRandomTick()
                .instantDestruction()
                .setSoundType(SoundType.PLANT)));
        register("jukebox", new BlockJukebox(Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.DIRT)
                .setHardnessAndResistance(2.0F, 6.0F)));
        register("oak_fence", new BlockFence(
                Block.Properties.createBlockProperties(Material.WOOD, oakPlanks.blockMapColor)
                        .setHardnessAndResistance(2.0F, 3.0F)
                        .setSoundType(SoundType.WOOD)));
        BlockStemGrown blockstemgrown = new BlockPumpkin(
                Block.Properties.createBlockProperties(Material.GOURD, MaterialColor.ADOBE)
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD));
        register("pumpkin", blockstemgrown);
        register("netherrack", new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.NETHERRACK)
                .setHardnessAndResistance(0.4F)));
        register("soul_sand", new BlockSoulSand(
                Block.Properties.createBlockProperties(Material.SAND, MaterialColor.BROWN)
                        .needsRandomTick()
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.SAND)));
        register("glowstone", new BlockGlowstone(
                Block.Properties.createBlockProperties(Material.GLASS, MaterialColor.SAND)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)
                        .setLightLevel(15)));
        register("nether_portal", new BlockPortal(Block.Properties.createBlockProperties(Material.PORTAL)
                .setNonSolid()
                .needsRandomTick()
                .setHardnessAndResistance(-1.0F)
                .setSoundType(SoundType.GLASS)
                .setLightLevel(11)));
        register("carved_pumpkin", new BlockCarvedPumpkin(
                Block.Properties.createBlockProperties(Material.GOURD, MaterialColor.ADOBE)
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("jack_o_lantern", new BlockCarvedPumpkin(
                Block.Properties.createBlockProperties(Material.GOURD, MaterialColor.ADOBE)
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)
                        .setLightLevel(15)));
        register("cake", new BlockCake(Block.Properties.createBlockProperties(Material.CAKE)
                .setHardnessAndResistance(0.5F)
                .setSoundType(SoundType.CLOTH)));
        register("repeater", new BlockRedstoneRepeater(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .instantDestruction()
                .setSoundType(SoundType.WOOD)));
        register("white_stained_glass", new BlockStainedGlass(EnumDyeColor.WHITE,
                Block.Properties.createBlockProperties(Material.GLASS, EnumDyeColor.WHITE)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("orange_stained_glass", new BlockStainedGlass(EnumDyeColor.ORANGE,
                Block.Properties.createBlockProperties(Material.GLASS, EnumDyeColor.ORANGE)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("magenta_stained_glass", new BlockStainedGlass(EnumDyeColor.MAGENTA,
                Block.Properties.createBlockProperties(Material.GLASS, EnumDyeColor.MAGENTA)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("light_blue_stained_glass", new BlockStainedGlass(EnumDyeColor.LIGHT_BLUE,
                Block.Properties.createBlockProperties(Material.GLASS, EnumDyeColor.LIGHT_BLUE)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("yellow_stained_glass", new BlockStainedGlass(EnumDyeColor.YELLOW,
                Block.Properties.createBlockProperties(Material.GLASS, EnumDyeColor.YELLOW)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("lime_stained_glass", new BlockStainedGlass(EnumDyeColor.LIME,
                Block.Properties.createBlockProperties(Material.GLASS, EnumDyeColor.LIME)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("pink_stained_glass", new BlockStainedGlass(EnumDyeColor.PINK,
                Block.Properties.createBlockProperties(Material.GLASS, EnumDyeColor.PINK)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("gray_stained_glass", new BlockStainedGlass(EnumDyeColor.GRAY,
                Block.Properties.createBlockProperties(Material.GLASS, EnumDyeColor.GRAY)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("light_gray_stained_glass", new BlockStainedGlass(EnumDyeColor.LIGHT_GRAY,
                Block.Properties.createBlockProperties(Material.GLASS, EnumDyeColor.LIGHT_GRAY)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("cyan_stained_glass", new BlockStainedGlass(EnumDyeColor.CYAN,
                Block.Properties.createBlockProperties(Material.GLASS, EnumDyeColor.CYAN)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("purple_stained_glass", new BlockStainedGlass(EnumDyeColor.PURPLE,
                Block.Properties.createBlockProperties(Material.GLASS, EnumDyeColor.PURPLE)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("blue_stained_glass", new BlockStainedGlass(EnumDyeColor.BLUE,
                Block.Properties.createBlockProperties(Material.GLASS, EnumDyeColor.BLUE)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("brown_stained_glass", new BlockStainedGlass(EnumDyeColor.BROWN,
                Block.Properties.createBlockProperties(Material.GLASS, EnumDyeColor.BROWN)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("green_stained_glass", new BlockStainedGlass(EnumDyeColor.GREEN,
                Block.Properties.createBlockProperties(Material.GLASS, EnumDyeColor.GREEN)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("red_stained_glass", new BlockStainedGlass(EnumDyeColor.RED,
                Block.Properties.createBlockProperties(Material.GLASS, EnumDyeColor.RED)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("black_stained_glass", new BlockStainedGlass(EnumDyeColor.BLACK,
                Block.Properties.createBlockProperties(Material.GLASS, EnumDyeColor.BLACK)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("oak_trapdoor", new BlockTrapDoor(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.WOOD)
                        .setHardnessAndResistance(3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("spruce_trapdoor", new BlockTrapDoor(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.OBSIDIAN)
                        .setHardnessAndResistance(3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("birch_trapdoor", new BlockTrapDoor(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.SAND)
                        .setHardnessAndResistance(3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("jungle_trapdoor", new BlockTrapDoor(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.DIRT)
                        .setHardnessAndResistance(3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("acacia_trapdoor", new BlockTrapDoor(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.ADOBE)
                        .setHardnessAndResistance(3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("dark_oak_trapdoor", new BlockTrapDoor(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.BROWN)
                        .setHardnessAndResistance(3.0F)
                        .setSoundType(SoundType.WOOD)));
        Block block35 = new Block(
                Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(1.5F, 6.0F));
        Block block36 = new Block(
                Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(1.5F, 6.0F));
        Block block37 = new Block(
                Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(1.5F, 6.0F));
        Block block38 = new Block(
                Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(1.5F, 6.0F));
        register("infested_stone", new BlockSilverfish(stone,
                Block.Properties.createBlockProperties(Material.CLAY).setHardnessAndResistance(0.0F, 0.75F)));
        register("infested_cobblestone", new BlockSilverfish(cobblestone,
                Block.Properties.createBlockProperties(Material.CLAY).setHardnessAndResistance(0.0F, 0.75F)));
        register("infested_stone_bricks", new BlockSilverfish(block35,
                Block.Properties.createBlockProperties(Material.CLAY).setHardnessAndResistance(0.0F, 0.75F)));
        register("infested_mossy_stone_bricks", new BlockSilverfish(block36,
                Block.Properties.createBlockProperties(Material.CLAY).setHardnessAndResistance(0.0F, 0.75F)));
        register("infested_cracked_stone_bricks", new BlockSilverfish(block37,
                Block.Properties.createBlockProperties(Material.CLAY).setHardnessAndResistance(0.0F, 0.75F)));
        register("infested_chiseled_stone_bricks", new BlockSilverfish(block38,
                Block.Properties.createBlockProperties(Material.CLAY).setHardnessAndResistance(0.0F, 0.75F)));
        register("stone_bricks", block35);
        register("mossy_stone_bricks", block36);
        register("cracked_stone_bricks", block37);
        register("chiseled_stone_bricks", block38);
        Block hugeBrownMushroom = new BlockHugeMushroom(brownMushroom,
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.DIRT)
                        .setHardnessAndResistance(0.2F)
                        .setSoundType(SoundType.WOOD));
        register("brown_mushroom_block", hugeBrownMushroom);
        Block hugeRedMushroom = new BlockHugeMushroom(redMushroom,
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.RED)
                        .setHardnessAndResistance(0.2F)
                        .setSoundType(SoundType.WOOD));
        register("red_mushroom_block", hugeRedMushroom);
        register("mushroom_stem", new BlockHugeMushroom(null,
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.WHITE_STAINED_HARDENED_CLAY)
                        .setHardnessAndResistance(0.2F)
                        .setSoundType(SoundType.WOOD)));
        register("iron_bars", new BlockPane(Block.Properties.createBlockProperties(Material.IRON, MaterialColor.AIR)
                .setHardnessAndResistance(5.0F, 6.0F)
                .setSoundType(SoundType.METAL)));
        register("glass_pane", new BlockGlassPane(Block.Properties.createBlockProperties(Material.GLASS)
                .setHardnessAndResistance(0.3F)
                .setSoundType(SoundType.GLASS)));
        BlockStemGrown melon = new BlockMelon(Block.Properties.createBlockProperties(Material.GOURD, MaterialColor.LIME)
                .setHardnessAndResistance(1.0F)
                .setSoundType(SoundType.WOOD));
        register("melon", melon);
        register("attached_pumpkin_stem", new BlockAttachedStem(blockstemgrown,
                Block.Properties.createBlockProperties(Material.PLANTS)
                        .setNonSolid()
                        .instantDestruction()
                        .setSoundType(SoundType.WOOD)));
        register("attached_melon_stem", new BlockAttachedStem(melon,
                Block.Properties.createBlockProperties(Material.PLANTS)
                        .setNonSolid()
                        .instantDestruction()
                        .setSoundType(SoundType.WOOD)));
        register("pumpkin_stem", new BlockStem(blockstemgrown, Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .needsRandomTick()
                .instantDestruction()
                .setSoundType(SoundType.WOOD)));
        register("melon_stem", new BlockStem(melon, Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .needsRandomTick()
                .instantDestruction()
                .setSoundType(SoundType.WOOD)));
        register("vine", new BlockVine(Block.Properties.createBlockProperties(Material.VINE)
                .setNonSolid()
                .needsRandomTick()
                .setHardnessAndResistance(0.2F)
                .setSoundType(SoundType.PLANT)));
        register("oak_fence_gate", new BlockFenceGate(
                Block.Properties.createBlockProperties(Material.WOOD, oakPlanks.blockMapColor)
                        .setHardnessAndResistance(2.0F, 3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("brick_stairs",
                new BlockStairs(block32.getDefaultState(), Block.Properties.createBlockPropertiesFromBlock(block32)));
        register("stone_brick_stairs",
                new BlockStairs(block35.getDefaultState(), Block.Properties.createBlockPropertiesFromBlock(block35)));
        register("mycelium", new BlockMycelium(
                Block.Properties.createBlockProperties(Material.GRASS, MaterialColor.PURPLE)
                        .needsRandomTick()
                        .setHardnessAndResistance(0.6F)
                        .setSoundType(SoundType.PLANT)));
        register("lily_pad", new BlockLilyPad(Block.Properties.createBlockProperties(Material.PLANTS)
                .instantDestruction()
                .setSoundType(SoundType.PLANT)));
        Block netherBricks = new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.NETHERRACK)
                .setHardnessAndResistance(2.0F, 6.0F));
        register("nether_bricks", netherBricks);
        register("nether_brick_fence", new BlockFence(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.NETHERRACK)
                        .setHardnessAndResistance(2.0F, 6.0F)));
        register("nether_brick_stairs", new BlockStairs(netherBricks.getDefaultState(),
                Block.Properties.createBlockPropertiesFromBlock(netherBricks)));
        register("nether_wart", new BlockNetherWart(
                Block.Properties.createBlockProperties(Material.PLANTS, MaterialColor.RED)
                        .setNonSolid()
                        .needsRandomTick()));
        register("enchanting_table", new BlockEnchantmentTable(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.RED)
                        .setHardnessAndResistance(5.0F, 1200.0F)));
        register("brewing_stand", new BlockBrewingStand(
                Block.Properties.createBlockProperties(Material.IRON).setHardnessAndResistance(0.5F).setLightLevel(1)));
        register("cauldron", new BlockCauldron(
                Block.Properties.createBlockProperties(Material.IRON, MaterialColor.STONE)
                        .setHardnessAndResistance(2.0F)));
        register("end_portal", new BlockEndPortal(
                Block.Properties.createBlockProperties(Material.PORTAL, MaterialColor.BLACK)
                        .setNonSolid()
                        .setLightLevel(15)
                        .setHardnessAndResistance(-1.0F, 3600000.0F)));
        register("end_portal_frame", new BlockEndPortalFrame(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GREEN)
                        .setSoundType(SoundType.GLASS)
                        .setLightLevel(1)
                        .setHardnessAndResistance(-1.0F, 3600000.0F)));
        register("end_stone", new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.SAND)
                .setHardnessAndResistance(3.0F, 9.0F)));
        register("dragon_egg", new BlockDragonEgg(
                Block.Properties.createBlockProperties(Material.DRAGON_EGG, MaterialColor.BLACK)
                        .setHardnessAndResistance(3.0F, 9.0F)
                        .setLightLevel(1)));
        register("redstone_lamp", new BlockRedstoneLamp(Block.Properties.createBlockProperties(Material.REDSTONE_LIGHT)
                .setLightLevel(15)
                .setHardnessAndResistance(0.3F)
                .setSoundType(SoundType.GLASS)));
        register("cocoa", new BlockCocoa(Block.Properties.createBlockProperties(Material.PLANTS)
                .needsRandomTick()
                .setHardnessAndResistance(0.2F, 3.0F)
                .setSoundType(SoundType.WOOD)));
        register("sandstone_stairs",
                new BlockStairs(block15.getDefaultState(), Block.Properties.createBlockPropertiesFromBlock(block15)));
        register("emerald_ore",
                new BlockOre(Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(3.0F, 3.0F),
                        3));
        register("ender_chest", new BlockEnderChest(Block.Properties.createBlockProperties(Material.ROCK)
                .setHardnessAndResistance(22.5F, 600.0F)
                .setLightLevel(7)));
        BlockTripWireHook blocktripwirehook = new BlockTripWireHook(
                Block.Properties.createBlockProperties(Material.CIRCUITS).setNonSolid());
        register("tripwire_hook", blocktripwirehook);
        register("tripwire", new BlockTripWire(blocktripwirehook,
                Block.Properties.createBlockProperties(Material.CIRCUITS).setNonSolid()));
        register("emerald_block", new BlockMaterial(
                Block.Properties.createBlockProperties(Material.IRON, MaterialColor.EMERALD)
                        .setHardnessAndResistance(5.0F, 6.0F)
                        .setSoundType(SoundType.METAL), 4));
        register("spruce_stairs", new BlockStairs(sprucePlanks.getDefaultState(),
                Block.Properties.createBlockPropertiesFromBlock(sprucePlanks)));
        register("birch_stairs", new BlockStairs(birchPlanks.getDefaultState(),
                Block.Properties.createBlockPropertiesFromBlock(birchPlanks)));
        register("jungle_stairs", new BlockStairs(junglePlanks.getDefaultState(),
                Block.Properties.createBlockPropertiesFromBlock(junglePlanks)));
        register("command_block", new BlockCommandBlock(
                Block.Properties.createBlockProperties(Material.IRON, MaterialColor.BROWN)
                        .setHardnessAndResistance(-1.0F, 3600000.0F)));
        register("beacon", new BlockBeacon(Block.Properties.createBlockProperties(Material.GLASS, MaterialColor.DIAMOND)
                .setHardnessAndResistance(3.0F)
                .setLightLevel(15)));
        register("cobblestone_wall", new BlockWall(Block.Properties.createBlockPropertiesFromBlock(cobblestone)));
        register("mossy_cobblestone_wall", new BlockWall(Block.Properties.createBlockPropertiesFromBlock(cobblestone)));
        register("flower_pot", new BlockFlowerPot(air,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_oak_sapling", new BlockFlowerPot(oakSapling,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_spruce_sapling", new BlockFlowerPot(spruceSapling,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_birch_sapling", new BlockFlowerPot(birchSapling,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_jungle_sapling", new BlockFlowerPot(jugleSapling,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_acacia_sapling", new BlockFlowerPot(acaciaSapling,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_dark_oak_sapling", new BlockFlowerPot(darkOakSapling,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_fern", new BlockFlowerPot(fern,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_dandelion", new BlockFlowerPot(block20,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_poppy", new BlockFlowerPot(block21,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_blue_orchid", new BlockFlowerPot(block22,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_allium", new BlockFlowerPot(block23,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_azure_bluet", new BlockFlowerPot(block24,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_red_tulip", new BlockFlowerPot(block25,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_orange_tulip", new BlockFlowerPot(block26,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_white_tulip", new BlockFlowerPot(block27,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_pink_tulip", new BlockFlowerPot(pinkTulip,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_oxeye_daisy", new BlockFlowerPot(oxeyDaisy,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_red_mushroom", new BlockFlowerPot(redMushroom,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_brown_mushroom", new BlockFlowerPot(brownMushroom,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_dead_bush", new BlockFlowerPot(deadBush,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("potted_cactus", new BlockFlowerPot(cactus,
                Block.Properties.createBlockProperties(Material.CIRCUITS).instantDestruction()));
        register("carrots", new BlockCarrot(Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .needsRandomTick()
                .instantDestruction()
                .setSoundType(SoundType.PLANT)));
        register("potatoes", new BlockPotato(Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .needsRandomTick()
                .instantDestruction()
                .setSoundType(SoundType.PLANT)));
        register("oak_button", new BlockButtonWood(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .setNonSolid()
                .setHardnessAndResistance(0.5F)
                .setSoundType(SoundType.WOOD)));
        register("spruce_button", new BlockButtonWood(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .setNonSolid()
                .setHardnessAndResistance(0.5F)
                .setSoundType(SoundType.WOOD)));
        register("birch_button", new BlockButtonWood(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .setNonSolid()
                .setHardnessAndResistance(0.5F)
                .setSoundType(SoundType.WOOD)));
        register("jungle_button", new BlockButtonWood(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .setNonSolid()
                .setHardnessAndResistance(0.5F)
                .setSoundType(SoundType.WOOD)));
        register("acacia_button", new BlockButtonWood(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .setNonSolid()
                .setHardnessAndResistance(0.5F)
                .setSoundType(SoundType.WOOD)));
        register("dark_oak_button", new BlockButtonWood(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .setNonSolid()
                .setHardnessAndResistance(0.5F)
                .setSoundType(SoundType.WOOD)));
        register("skeleton_wall_skull", new BlockSkullWall(BlockSkull.Types.SKELETON,
                Block.Properties.createBlockProperties(Material.CIRCUITS).setHardnessAndResistance(1.0F)));
        register("skeleton_skull", new BlockSkull(BlockSkull.Types.SKELETON,
                Block.Properties.createBlockProperties(Material.CIRCUITS).setHardnessAndResistance(1.0F)));
        register("wither_skeleton_wall_skull", new BlockSkullWitherWall(
                Block.Properties.createBlockProperties(Material.CIRCUITS).setHardnessAndResistance(1.0F)));
        register("wither_skeleton_skull", new BlockSkullWither(
                Block.Properties.createBlockProperties(Material.CIRCUITS).setHardnessAndResistance(1.0F)));
        register("zombie_wall_head", new BlockSkullWall(BlockSkull.Types.ZOMBIE,
                Block.Properties.createBlockProperties(Material.CIRCUITS).setHardnessAndResistance(1.0F)));
        register("zombie_head", new BlockSkull(BlockSkull.Types.ZOMBIE,
                Block.Properties.createBlockProperties(Material.CIRCUITS).setHardnessAndResistance(1.0F)));
        register("player_wall_head", new BlockSkullWallPlayer(
                Block.Properties.createBlockProperties(Material.CIRCUITS).setHardnessAndResistance(1.0F)));
        register("player_head", new BlockSkullPlayer(
                Block.Properties.createBlockProperties(Material.CIRCUITS).setHardnessAndResistance(1.0F)));
        register("creeper_wall_head", new BlockSkullWall(BlockSkull.Types.CREEPER,
                Block.Properties.createBlockProperties(Material.CIRCUITS).setHardnessAndResistance(1.0F)));
        register("creeper_head", new BlockSkull(BlockSkull.Types.CREEPER,
                Block.Properties.createBlockProperties(Material.CIRCUITS).setHardnessAndResistance(1.0F)));
        register("dragon_wall_head", new BlockSkullWall(BlockSkull.Types.DRAGON,
                Block.Properties.createBlockProperties(Material.CIRCUITS).setHardnessAndResistance(1.0F)));
        register("dragon_head", new BlockSkull(BlockSkull.Types.DRAGON,
                Block.Properties.createBlockProperties(Material.CIRCUITS).setHardnessAndResistance(1.0F)));
        register("iron_anvil", new BlockAnvil(Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.IRON)
                .setHardnessAndResistance(2.5F, 1200.0F)
                .setSoundType(SoundType.ANVIL),ItemTier.IRON,396800,3));
        register("chipped_iron_anvil", new BlockAnvil(
                Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.IRON)
                        .setHardnessAndResistance(2.4F, 1200.0F)
                        .setSoundType(SoundType.ANVIL),ItemTier.IRON,132267,396800,3));
        register("damaged_iron_anvil", new BlockAnvil(
                Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.IRON)
                        .setHardnessAndResistance(2.3F, 1200.0F)
                        .setSoundType(SoundType.ANVIL),ItemTier.IRON,264533,396800,3));
        register("trapped_chest", new BlockTrappedChest(Block.Properties.createBlockProperties(Material.WOOD)
                .setHardnessAndResistance(2.5F)
                .setSoundType(SoundType.WOOD)));
        register("light_weighted_pressure_plate", new BlockPressurePlateWeighted(15,
                Block.Properties.createBlockProperties(Material.IRON, MaterialColor.GOLD)
                        .setNonSolid()
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.WOOD)));
        register("heavy_weighted_pressure_plate", new BlockPressurePlateWeighted(150,
                Block.Properties.createBlockProperties(Material.IRON)
                        .setNonSolid()
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.WOOD)));
        register("comparator", new BlockRedstoneComparator(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .instantDestruction()
                .setSoundType(SoundType.WOOD)));
        register("daylight_detector", new BlockDaylightDetector(Block.Properties.createBlockProperties(Material.WOOD)
                .setHardnessAndResistance(0.2F)
                .setSoundType(SoundType.WOOD)));
        register("redstone_block", new BlockRedstone(
                Block.Properties.createBlockProperties(Material.IRON, MaterialColor.TNT)
                        .setHardnessAndResistance(5.0F, 6.0F)
                        .setSoundType(SoundType.METAL)));
        register("nether_quartz_ore", new BlockOre(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.NETHERRACK)
                        .setHardnessAndResistance(3.0F, 3.0F), 2));
        register("hopper", new BlockHopper(Block.Properties.createBlockProperties(Material.IRON, MaterialColor.STONE)
                .setHardnessAndResistance(3.0F, 4.8F)
                .setSoundType(SoundType.METAL)));
        Block block42 = new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.QUARTZ)
                .setHardnessAndResistance(0.8F));
        register("quartz_block", block42);
        register("chiseled_quartz_block", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.QUARTZ)
                        .setHardnessAndResistance(0.8F)));
        register("quartz_pillar", new BlockRotatedPillar(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.QUARTZ)
                        .setHardnessAndResistance(0.8F)));
        register("quartz_stairs",
                new BlockStairs(block42.getDefaultState(), Block.Properties.createBlockPropertiesFromBlock(block42)));
        register("activator_rail", new BlockRailPowered(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .setNonSolid()
                .setHardnessAndResistance(0.7F)
                .setSoundType(SoundType.METAL)));
        register("dropper",
                new BlockDropper(Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(3.5F)));
        register("white_terracotta", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.WHITE_STAINED_HARDENED_CLAY)
                        .setHardnessAndResistance(1.25F, 4.2F)));
        register("orange_terracotta", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.ORANGE_STAINED_HARDENED_CLAY)
                        .setHardnessAndResistance(1.25F, 4.2F)));
        register("magenta_terracotta", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.MAGENTA_STAINED_HARDENED_CLAY)
                        .setHardnessAndResistance(1.25F, 4.2F)));
        register("light_blue_terracotta", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.LIGHT_BLUE_STAINED_HARDENED_CLAY)
                        .setHardnessAndResistance(1.25F, 4.2F)));
        register("yellow_terracotta", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.YELLOW_STAINED_HARDENED_CLAY)
                        .setHardnessAndResistance(1.25F, 4.2F)));
        register("lime_terracotta", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.LIME_STAINED_HARDENED_CLAY)
                        .setHardnessAndResistance(1.25F, 4.2F)));
        register("pink_terracotta", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.PINK_STAINED_HARDENED_CLAY)
                        .setHardnessAndResistance(1.25F, 4.2F)));
        register("gray_terracotta", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY_STAINED_HARDENED_CLAY)
                        .setHardnessAndResistance(1.25F, 4.2F)));
        register("light_gray_terracotta", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.SILVER_STAINED_HARDENED_CLAY)
                        .setHardnessAndResistance(1.25F, 4.2F)));
        register("cyan_terracotta", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.CYAN_STAINED_HARDENED_CLAY)
                        .setHardnessAndResistance(1.25F, 4.2F)));
        register("purple_terracotta", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.PURPLE_STAINED_HARDENED_CLAY)
                        .setHardnessAndResistance(1.25F, 4.2F)));
        register("blue_terracotta", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.BLUE_STAINED_HARDENED_CLAY)
                        .setHardnessAndResistance(1.25F, 4.2F)));
        register("brown_terracotta", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.BROWN_STAINED_HARDENED_CLAY)
                        .setHardnessAndResistance(1.25F, 4.2F)));
        register("green_terracotta", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GREEN_STAINED_HARDENED_CLAY)
                        .setHardnessAndResistance(1.25F, 4.2F)));
        register("red_terracotta", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.RED_STAINED_HARDENED_CLAY)
                        .setHardnessAndResistance(1.25F, 4.2F)));
        register("black_terracotta", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.BLACK_STAINED_HARDENED_CLAY)
                        .setHardnessAndResistance(1.25F, 4.2F)));
        register("white_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.WHITE,
                Block.Properties.createBlockProperties(Material.GLASS)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("orange_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.ORANGE,
                Block.Properties.createBlockProperties(Material.GLASS)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("magenta_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.MAGENTA,
                Block.Properties.createBlockProperties(Material.GLASS)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("light_blue_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.LIGHT_BLUE,
                Block.Properties.createBlockProperties(Material.GLASS)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("yellow_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.YELLOW,
                Block.Properties.createBlockProperties(Material.GLASS)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("lime_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.LIME,
                Block.Properties.createBlockProperties(Material.GLASS)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("pink_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.PINK,
                Block.Properties.createBlockProperties(Material.GLASS)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("gray_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.GRAY,
                Block.Properties.createBlockProperties(Material.GLASS)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("light_gray_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.LIGHT_GRAY,
                Block.Properties.createBlockProperties(Material.GLASS)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("cyan_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.CYAN,
                Block.Properties.createBlockProperties(Material.GLASS)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("purple_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.PURPLE,
                Block.Properties.createBlockProperties(Material.GLASS)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("blue_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.BLUE,
                Block.Properties.createBlockProperties(Material.GLASS)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("brown_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.BROWN,
                Block.Properties.createBlockProperties(Material.GLASS)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("green_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.GREEN,
                Block.Properties.createBlockProperties(Material.GLASS)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("red_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.RED,
                Block.Properties.createBlockProperties(Material.GLASS)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("black_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.BLACK,
                Block.Properties.createBlockProperties(Material.GLASS)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)));
        register("acacia_stairs", new BlockStairs(acaciaPlanks.getDefaultState(),
                Block.Properties.createBlockPropertiesFromBlock(acaciaPlanks)));
        register("dark_oak_stairs", new BlockStairs(darkOakPlanks.getDefaultState(),
                Block.Properties.createBlockPropertiesFromBlock(darkOakPlanks)));
        register("slime_block", new BlockSlime(
                Block.Properties.createBlockProperties(Material.CLAY, MaterialColor.GRASS)
                        .setSlipperiness(0.8F)
                        .setSoundType(SoundType.SLIME)));
        register("barrier", new BlockBarrier(
                Block.Properties.createBlockProperties(Material.BARRIER).setHardnessAndResistance(-1.0F, 3600000.8F)));
        register("iron_trapdoor", new BlockTrapDoor(Block.Properties.createBlockProperties(Material.IRON)
                .setHardnessAndResistance(5.0F)
                .setSoundType(SoundType.METAL)));
        Block prismarine = new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.CYAN)
                .setHardnessAndResistance(1.5F, 6.0F));
        register("prismarine", prismarine);
        Block block44 = new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.DIAMOND)
                .setHardnessAndResistance(1.5F, 6.0F));
        register("prismarine_bricks", block44);
        Block darkPrismarine = new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.DIAMOND)
                .setHardnessAndResistance(1.5F, 6.0F));
        register("dark_prismarine", darkPrismarine);
        register("prismarine_stairs", new BlockStairs(prismarine.getDefaultState(),
                Block.Properties.createBlockPropertiesFromBlock(prismarine)));
        register("prismarine_brick_stairs",
                new BlockStairs(block44.getDefaultState(), Block.Properties.createBlockPropertiesFromBlock(block44)));
        register("dark_prismarine_stairs", new BlockStairs(darkPrismarine.getDefaultState(),
                Block.Properties.createBlockPropertiesFromBlock(darkPrismarine)));
        register("prismarine_slab", new BlockSlab(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.CYAN)
                        .setHardnessAndResistance(1.5F, 6.0F)));
        register("prismarine_brick_slab", new BlockSlab(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.DIAMOND)
                        .setHardnessAndResistance(1.5F, 6.0F)));
        register("dark_prismarine_slab", new BlockSlab(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.DIAMOND)
                        .setHardnessAndResistance(1.5F, 6.0F)));
        register("sea_lantern", new BlockSeaLantern(
                Block.Properties.createBlockProperties(Material.GLASS, MaterialColor.QUARTZ)
                        .setHardnessAndResistance(0.3F)
                        .setSoundType(SoundType.GLASS)
                        .setLightLevel(15)));
        register("hay_block", new BlockHay(Block.Properties.createBlockProperties(Material.GRASS, MaterialColor.YELLOW)
                .setHardnessAndResistance(0.5F)
                .setSoundType(SoundType.PLANT)));
        register("white_carpet", new BlockCarpet(EnumDyeColor.WHITE,
                Block.Properties.createBlockProperties(Material.CARPET, MaterialColor.SNOW)
                        .setHardnessAndResistance(0.1F)
                        .setSoundType(SoundType.CLOTH)));
        register("orange_carpet", new BlockCarpet(EnumDyeColor.ORANGE,
                Block.Properties.createBlockProperties(Material.CARPET, MaterialColor.ADOBE)
                        .setHardnessAndResistance(0.1F)
                        .setSoundType(SoundType.CLOTH)));
        register("magenta_carpet", new BlockCarpet(EnumDyeColor.MAGENTA,
                Block.Properties.createBlockProperties(Material.CARPET, MaterialColor.MAGENTA)
                        .setHardnessAndResistance(0.1F)
                        .setSoundType(SoundType.CLOTH)));
        register("light_blue_carpet", new BlockCarpet(EnumDyeColor.LIGHT_BLUE,
                Block.Properties.createBlockProperties(Material.CARPET, MaterialColor.LIGHT_BLUE)
                        .setHardnessAndResistance(0.1F)
                        .setSoundType(SoundType.CLOTH)));
        register("yellow_carpet", new BlockCarpet(EnumDyeColor.YELLOW,
                Block.Properties.createBlockProperties(Material.CARPET, MaterialColor.YELLOW)
                        .setHardnessAndResistance(0.1F)
                        .setSoundType(SoundType.CLOTH)));
        register("lime_carpet", new BlockCarpet(EnumDyeColor.LIME,
                Block.Properties.createBlockProperties(Material.CARPET, MaterialColor.LIME)
                        .setHardnessAndResistance(0.1F)
                        .setSoundType(SoundType.CLOTH)));
        register("pink_carpet", new BlockCarpet(EnumDyeColor.PINK,
                Block.Properties.createBlockProperties(Material.CARPET, MaterialColor.PINK)
                        .setHardnessAndResistance(0.1F)
                        .setSoundType(SoundType.CLOTH)));
        register("gray_carpet", new BlockCarpet(EnumDyeColor.GRAY,
                Block.Properties.createBlockProperties(Material.CARPET, MaterialColor.GRAY)
                        .setHardnessAndResistance(0.1F)
                        .setSoundType(SoundType.CLOTH)));
        register("light_gray_carpet", new BlockCarpet(EnumDyeColor.LIGHT_GRAY,
                Block.Properties.createBlockProperties(Material.CARPET, MaterialColor.SILVER)
                        .setHardnessAndResistance(0.1F)
                        .setSoundType(SoundType.CLOTH)));
        register("cyan_carpet", new BlockCarpet(EnumDyeColor.CYAN,
                Block.Properties.createBlockProperties(Material.CARPET, MaterialColor.CYAN)
                        .setHardnessAndResistance(0.1F)
                        .setSoundType(SoundType.CLOTH)));
        register("purple_carpet", new BlockCarpet(EnumDyeColor.PURPLE,
                Block.Properties.createBlockProperties(Material.CARPET, MaterialColor.PURPLE)
                        .setHardnessAndResistance(0.1F)
                        .setSoundType(SoundType.CLOTH)));
        register("blue_carpet", new BlockCarpet(EnumDyeColor.BLUE,
                Block.Properties.createBlockProperties(Material.CARPET, MaterialColor.BLUE)
                        .setHardnessAndResistance(0.1F)
                        .setSoundType(SoundType.CLOTH)));
        register("brown_carpet", new BlockCarpet(EnumDyeColor.BROWN,
                Block.Properties.createBlockProperties(Material.CARPET, MaterialColor.BROWN)
                        .setHardnessAndResistance(0.1F)
                        .setSoundType(SoundType.CLOTH)));
        register("green_carpet", new BlockCarpet(EnumDyeColor.GREEN,
                Block.Properties.createBlockProperties(Material.CARPET, MaterialColor.GREEN)
                        .setHardnessAndResistance(0.1F)
                        .setSoundType(SoundType.CLOTH)));
        register("red_carpet", new BlockCarpet(EnumDyeColor.RED,
                Block.Properties.createBlockProperties(Material.CARPET, MaterialColor.RED)
                        .setHardnessAndResistance(0.1F)
                        .setSoundType(SoundType.CLOTH)));
        register("black_carpet", new BlockCarpet(EnumDyeColor.BLACK,
                Block.Properties.createBlockProperties(Material.CARPET, MaterialColor.BLACK)
                        .setHardnessAndResistance(0.1F)
                        .setSoundType(SoundType.CLOTH)));
        register("terracotta", new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.ADOBE)
                .setHardnessAndResistance(1.25F, 4.2F)));
        register("coal_block", new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.BLACK)
                .setHardnessAndResistance(5.0F, 6.0F)));
        register("packed_ice", new BlockPackedIce(Block.Properties.createBlockProperties(Material.PACKED_ICE)
                .setSlipperiness(0.98F)
                .setHardnessAndResistance(0.5F)
                .setSoundType(SoundType.GLASS)));
        register("sunflower", new BlockTallFlower(Block.Properties.createBlockProperties(Material.VINE)
                .setNonSolid()
                .instantDestruction()
                .setSoundType(SoundType.PLANT)));
        register("lilac", new BlockTallFlower(Block.Properties.createBlockProperties(Material.VINE)
                .setNonSolid()
                .instantDestruction()
                .setSoundType(SoundType.PLANT)));
        register("rose_bush", new BlockTallFlower(Block.Properties.createBlockProperties(Material.VINE)
                .setNonSolid()
                .instantDestruction()
                .setSoundType(SoundType.PLANT)));
        register("peony", new BlockTallFlower(Block.Properties.createBlockProperties(Material.VINE)
                .setNonSolid()
                .instantDestruction()
                .setSoundType(SoundType.PLANT)));
        register("tall_grass", new BlockShearableDoublePlant(grass,
                Block.Properties.createBlockProperties(Material.VINE)
                        .setNonSolid()
                        .setHardnessAndResistance(0.1F)
                        .setSoundType(SoundType.PLANT)));
        register("large_fern", new BlockShearableDoublePlant(fern, Block.Properties.createBlockProperties(Material.VINE)
                .setNonSolid()
                .setHardnessAndResistance(0.1F)
                .setSoundType(SoundType.PLANT)));
        register("white_banner", new BlockBanner(EnumDyeColor.WHITE,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("orange_banner", new BlockBanner(EnumDyeColor.ORANGE,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("magenta_banner", new BlockBanner(EnumDyeColor.MAGENTA,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("light_blue_banner", new BlockBanner(EnumDyeColor.LIGHT_BLUE,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("yellow_banner", new BlockBanner(EnumDyeColor.YELLOW,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("lime_banner", new BlockBanner(EnumDyeColor.LIME, Block.Properties.createBlockProperties(Material.WOOD)
                .setNonSolid()
                .setHardnessAndResistance(1.0F)
                .setSoundType(SoundType.WOOD)));
        register("pink_banner", new BlockBanner(EnumDyeColor.PINK, Block.Properties.createBlockProperties(Material.WOOD)
                .setNonSolid()
                .setHardnessAndResistance(1.0F)
                .setSoundType(SoundType.WOOD)));
        register("gray_banner", new BlockBanner(EnumDyeColor.GRAY, Block.Properties.createBlockProperties(Material.WOOD)
                .setNonSolid()
                .setHardnessAndResistance(1.0F)
                .setSoundType(SoundType.WOOD)));
        register("light_gray_banner", new BlockBanner(EnumDyeColor.LIGHT_GRAY,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("cyan_banner", new BlockBanner(EnumDyeColor.CYAN, Block.Properties.createBlockProperties(Material.WOOD)
                .setNonSolid()
                .setHardnessAndResistance(1.0F)
                .setSoundType(SoundType.WOOD)));
        register("purple_banner", new BlockBanner(EnumDyeColor.PURPLE,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("blue_banner", new BlockBanner(EnumDyeColor.BLUE, Block.Properties.createBlockProperties(Material.WOOD)
                .setNonSolid()
                .setHardnessAndResistance(1.0F)
                .setSoundType(SoundType.WOOD)));
        register("brown_banner", new BlockBanner(EnumDyeColor.BROWN,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("green_banner", new BlockBanner(EnumDyeColor.GREEN,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("red_banner", new BlockBanner(EnumDyeColor.RED, Block.Properties.createBlockProperties(Material.WOOD)
                .setNonSolid()
                .setHardnessAndResistance(1.0F)
                .setSoundType(SoundType.WOOD)));
        register("black_banner", new BlockBanner(EnumDyeColor.BLACK,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("white_wall_banner", new BlockBannerWall(EnumDyeColor.WHITE,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("orange_wall_banner", new BlockBannerWall(EnumDyeColor.ORANGE,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("magenta_wall_banner", new BlockBannerWall(EnumDyeColor.MAGENTA,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("light_blue_wall_banner", new BlockBannerWall(EnumDyeColor.LIGHT_BLUE,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("yellow_wall_banner", new BlockBannerWall(EnumDyeColor.YELLOW,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("lime_wall_banner", new BlockBannerWall(EnumDyeColor.LIME,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("pink_wall_banner", new BlockBannerWall(EnumDyeColor.PINK,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("gray_wall_banner", new BlockBannerWall(EnumDyeColor.GRAY,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("light_gray_wall_banner", new BlockBannerWall(EnumDyeColor.LIGHT_GRAY,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("cyan_wall_banner", new BlockBannerWall(EnumDyeColor.CYAN,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("purple_wall_banner", new BlockBannerWall(EnumDyeColor.PURPLE,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("blue_wall_banner", new BlockBannerWall(EnumDyeColor.BLUE,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("brown_wall_banner", new BlockBannerWall(EnumDyeColor.BROWN,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("green_wall_banner", new BlockBannerWall(EnumDyeColor.GREEN,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("red_wall_banner", new BlockBannerWall(EnumDyeColor.RED,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("black_wall_banner", new BlockBannerWall(EnumDyeColor.BLACK,
                Block.Properties.createBlockProperties(Material.WOOD)
                        .setNonSolid()
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        Block block46 = new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.ADOBE)
                .setHardnessAndResistance(0.8F));
        register("red_sandstone", block46);
        register("chiseled_red_sandstone", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.ADOBE)
                        .setHardnessAndResistance(0.8F)));
        register("cut_red_sandstone", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.ADOBE)
                        .setHardnessAndResistance(0.8F)));
        register("red_sandstone_stairs",
                new BlockStairs(block46.getDefaultState(), Block.Properties.createBlockPropertiesFromBlock(block46)));
        register("oak_slab", new BlockSlab(Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.WOOD)
                .setHardnessAndResistance(2.0F, 3.0F)
                .setSoundType(SoundType.WOOD)));
        register("spruce_slab", new BlockSlab(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.OBSIDIAN)
                        .setHardnessAndResistance(2.0F, 3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("birch_slab", new BlockSlab(Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.SAND)
                .setHardnessAndResistance(2.0F, 3.0F)
                .setSoundType(SoundType.WOOD)));
        register("jungle_slab", new BlockSlab(Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.DIRT)
                .setHardnessAndResistance(2.0F, 3.0F)
                .setSoundType(SoundType.WOOD)));
        register("acacia_slab", new BlockSlab(Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.ADOBE)
                .setHardnessAndResistance(2.0F, 3.0F)
                .setSoundType(SoundType.WOOD)));
        register("dark_oak_slab", new BlockSlab(
                Block.Properties.createBlockProperties(Material.WOOD, MaterialColor.BROWN)
                        .setHardnessAndResistance(2.0F, 3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("stone_slab", new BlockSlab(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.STONE)
                .setHardnessAndResistance(2.0F, 6.0F)));
        register("sandstone_slab", new BlockSlab(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.SAND)
                        .setHardnessAndResistance(2.0F, 6.0F)));
        register("petrified_oak_slab", new BlockSlab(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.WOOD)
                        .setHardnessAndResistance(2.0F, 6.0F)));
        register("cobblestone_slab", new BlockSlab(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.STONE)
                        .setHardnessAndResistance(2.0F, 6.0F)));
        register("brick_slab", new BlockSlab(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.RED)
                .setHardnessAndResistance(2.0F, 6.0F)));
        register("stone_brick_slab", new BlockSlab(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.STONE)
                        .setHardnessAndResistance(2.0F, 6.0F)));
        register("nether_brick_slab", new BlockSlab(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.NETHERRACK)
                        .setHardnessAndResistance(2.0F, 6.0F)));
        register("quartz_slab", new BlockSlab(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.QUARTZ)
                        .setHardnessAndResistance(2.0F, 6.0F)));
        register("red_sandstone_slab", new BlockSlab(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.ADOBE)
                        .setHardnessAndResistance(2.0F, 6.0F)));
        register("purpur_slab", new BlockSlab(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.MAGENTA)
                        .setHardnessAndResistance(2.0F, 6.0F)));
        register("smooth_stone", new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.STONE)
                .setHardnessAndResistance(2.0F, 6.0F)));
        register("smooth_sandstone", new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.SAND)
                .setHardnessAndResistance(2.0F, 6.0F)));
        register("smooth_quartz", new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.QUARTZ)
                .setHardnessAndResistance(2.0F, 6.0F)));
        register("smooth_red_sandstone", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.ADOBE)
                        .setHardnessAndResistance(2.0F, 6.0F)));
        register("spruce_fence_gate", new BlockFenceGate(
                Block.Properties.createBlockProperties(Material.WOOD, sprucePlanks.blockMapColor)
                        .setHardnessAndResistance(2.0F, 3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("birch_fence_gate", new BlockFenceGate(
                Block.Properties.createBlockProperties(Material.WOOD, birchPlanks.blockMapColor)
                        .setHardnessAndResistance(2.0F, 3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("jungle_fence_gate", new BlockFenceGate(
                Block.Properties.createBlockProperties(Material.WOOD, junglePlanks.blockMapColor)
                        .setHardnessAndResistance(2.0F, 3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("acacia_fence_gate", new BlockFenceGate(
                Block.Properties.createBlockProperties(Material.WOOD, acaciaPlanks.blockMapColor)
                        .setHardnessAndResistance(2.0F, 3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("dark_oak_fence_gate", new BlockFenceGate(
                Block.Properties.createBlockProperties(Material.WOOD, darkOakPlanks.blockMapColor)
                        .setHardnessAndResistance(2.0F, 3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("spruce_fence", new BlockFence(
                Block.Properties.createBlockProperties(Material.WOOD, sprucePlanks.blockMapColor)
                        .setHardnessAndResistance(2.0F, 3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("birch_fence", new BlockFence(
                Block.Properties.createBlockProperties(Material.WOOD, birchPlanks.blockMapColor)
                        .setHardnessAndResistance(2.0F, 3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("jungle_fence", new BlockFence(
                Block.Properties.createBlockProperties(Material.WOOD, junglePlanks.blockMapColor)
                        .setHardnessAndResistance(2.0F, 3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("acacia_fence", new BlockFence(
                Block.Properties.createBlockProperties(Material.WOOD, acaciaPlanks.blockMapColor)
                        .setHardnessAndResistance(2.0F, 3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("dark_oak_fence", new BlockFence(
                Block.Properties.createBlockProperties(Material.WOOD, darkOakPlanks.blockMapColor)
                        .setHardnessAndResistance(2.0F, 3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("spruce_door", new BlockDoor(
                Block.Properties.createBlockProperties(Material.WOOD, sprucePlanks.blockMapColor)
                        .setHardnessAndResistance(3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("birch_door", new BlockDoor(
                Block.Properties.createBlockProperties(Material.WOOD, birchPlanks.blockMapColor)
                        .setHardnessAndResistance(3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("jungle_door", new BlockDoor(
                Block.Properties.createBlockProperties(Material.WOOD, junglePlanks.blockMapColor)
                        .setHardnessAndResistance(3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("acacia_door", new BlockDoor(
                Block.Properties.createBlockProperties(Material.WOOD, acaciaPlanks.blockMapColor)
                        .setHardnessAndResistance(3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("dark_oak_door", new BlockDoor(
                Block.Properties.createBlockProperties(Material.WOOD, darkOakPlanks.blockMapColor)
                        .setHardnessAndResistance(3.0F)
                        .setSoundType(SoundType.WOOD)));
        register("end_rod", new BlockEndRod(Block.Properties.createBlockProperties(Material.CIRCUITS)
                .instantDestruction()
                .setLightLevel(14)
                .setSoundType(SoundType.WOOD)));
        BlockChorusPlant blockchorusplant = new BlockChorusPlant(
                Block.Properties.createBlockProperties(Material.PLANTS, MaterialColor.PURPLE)
                        .setHardnessAndResistance(0.4F)
                        .setSoundType(SoundType.WOOD));
        register("chorus_plant", blockchorusplant);
        register("chorus_flower", new BlockChorusFlower(blockchorusplant,
                Block.Properties.createBlockProperties(Material.PLANTS, MaterialColor.PURPLE)
                        .needsRandomTick()
                        .setHardnessAndResistance(0.4F)
                        .setSoundType(SoundType.WOOD)));
        Block block47 = new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.MAGENTA)
                .setHardnessAndResistance(1.5F, 6.0F));
        register("purpur_block", block47);
        register("purpur_pillar", new BlockRotatedPillar(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.MAGENTA)
                        .setHardnessAndResistance(1.5F, 6.0F)));
        register("purpur_stairs",
                new BlockStairs(block47.getDefaultState(), Block.Properties.createBlockPropertiesFromBlock(block47)));
        register("end_stone_bricks", new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.SAND)
                .setHardnessAndResistance(0.8F)));
        register("beetroots", new BlockBeetroot(Block.Properties.createBlockProperties(Material.PLANTS)
                .setNonSolid()
                .needsRandomTick()
                .instantDestruction()
                .setSoundType(SoundType.PLANT)));
        Block block48 = new BlockGrassPath(Block.Properties.createBlockProperties(Material.GROUND)
                .setHardnessAndResistance(0.65F)
                .setSoundType(SoundType.PLANT));
        register("grass_path", block48);
        register("end_gateway", new BlockEndGateway(
                Block.Properties.createBlockProperties(Material.PORTAL, MaterialColor.BLACK)
                        .setNonSolid()
                        .setLightLevel(15)
                        .setHardnessAndResistance(-1.0F, 3600000.0F)));
        register("repeating_command_block", new BlockCommandBlock(
                Block.Properties.createBlockProperties(Material.IRON, MaterialColor.PURPLE)
                        .setHardnessAndResistance(-1.0F, 3600000.0F)));
        register("chain_command_block", new BlockCommandBlock(
                Block.Properties.createBlockProperties(Material.IRON, MaterialColor.GREEN)
                        .setHardnessAndResistance(-1.0F, 3600000.0F)));
        register("frosted_ice", new BlockFrostedIce(Block.Properties.createBlockProperties(Material.ICE)
                .setSlipperiness(0.98F)
                .needsRandomTick()
                .setHardnessAndResistance(0.5F)
                .setSoundType(SoundType.GLASS)));
        register("magma_block", new BlockMagma(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.NETHERRACK)
                        .setLightLevel(3)
                        .needsRandomTick()
                        .setHardnessAndResistance(0.5F)));
        register("nether_wart_block", new Block(
                Block.Properties.createBlockProperties(Material.GRASS, MaterialColor.RED)
                        .setHardnessAndResistance(1.0F)
                        .setSoundType(SoundType.WOOD)));
        register("red_nether_bricks", new Block(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.NETHERRACK)
                        .setHardnessAndResistance(2.0F, 6.0F)));
        register("bone_block", new BlockRotatedPillar(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.SAND)
                        .setHardnessAndResistance(2.0F)));
        register("structure_void",
                new BlockStructureVoid(Block.Properties.createBlockProperties(Material.STRUCTURE_VOID).setNonSolid()));
        register("observer", new BlockObserver(
                Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(3.0F)));
        register("shulker_box", new BlockShulkerBox(null,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.PURPLE)
                        .setHardnessAndResistance(2.0F)
                        .variableOpacity()));
        register("white_shulker_box", new BlockShulkerBox(EnumDyeColor.WHITE,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.SNOW)
                        .setHardnessAndResistance(2.0F)
                        .variableOpacity()));
        register("orange_shulker_box", new BlockShulkerBox(EnumDyeColor.ORANGE,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.ADOBE)
                        .setHardnessAndResistance(2.0F)
                        .variableOpacity()));
        register("magenta_shulker_box", new BlockShulkerBox(EnumDyeColor.MAGENTA,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.MAGENTA)
                        .setHardnessAndResistance(2.0F)
                        .variableOpacity()));
        register("light_blue_shulker_box", new BlockShulkerBox(EnumDyeColor.LIGHT_BLUE,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.LIGHT_BLUE)
                        .setHardnessAndResistance(2.0F)
                        .variableOpacity()));
        register("yellow_shulker_box", new BlockShulkerBox(EnumDyeColor.YELLOW,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.YELLOW)
                        .setHardnessAndResistance(2.0F)
                        .variableOpacity()));
        register("lime_shulker_box", new BlockShulkerBox(EnumDyeColor.LIME,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.LIME)
                        .setHardnessAndResistance(2.0F)
                        .variableOpacity()));
        register("pink_shulker_box", new BlockShulkerBox(EnumDyeColor.PINK,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.PINK)
                        .setHardnessAndResistance(2.0F)
                        .variableOpacity()));
        register("gray_shulker_box", new BlockShulkerBox(EnumDyeColor.GRAY,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                        .setHardnessAndResistance(2.0F)
                        .variableOpacity()));
        register("light_gray_shulker_box", new BlockShulkerBox(EnumDyeColor.LIGHT_GRAY,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.SILVER)
                        .setHardnessAndResistance(2.0F)
                        .variableOpacity()));
        register("cyan_shulker_box", new BlockShulkerBox(EnumDyeColor.CYAN,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.CYAN)
                        .setHardnessAndResistance(2.0F)
                        .variableOpacity()));
        register("purple_shulker_box", new BlockShulkerBox(EnumDyeColor.PURPLE,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.PURPLE_STAINED_HARDENED_CLAY)
                        .setHardnessAndResistance(2.0F)
                        .variableOpacity()));
        register("blue_shulker_box", new BlockShulkerBox(EnumDyeColor.BLUE,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.BLUE)
                        .setHardnessAndResistance(2.0F)
                        .variableOpacity()));
        register("brown_shulker_box", new BlockShulkerBox(EnumDyeColor.BROWN,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.BROWN)
                        .setHardnessAndResistance(2.0F)
                        .variableOpacity()));
        register("green_shulker_box", new BlockShulkerBox(EnumDyeColor.GREEN,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GREEN)
                        .setHardnessAndResistance(2.0F)
                        .variableOpacity()));
        register("red_shulker_box", new BlockShulkerBox(EnumDyeColor.RED,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.RED)
                        .setHardnessAndResistance(2.0F)
                        .variableOpacity()));
        register("black_shulker_box", new BlockShulkerBox(EnumDyeColor.BLACK,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.BLACK)
                        .setHardnessAndResistance(2.0F)
                        .variableOpacity()));
        register("white_glazed_terracotta", new BlockGlazedTerracotta(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.WHITE)
                        .setHardnessAndResistance(1.4F)));
        register("orange_glazed_terracotta", new BlockGlazedTerracotta(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.ORANGE)
                        .setHardnessAndResistance(1.4F)));
        register("magenta_glazed_terracotta", new BlockGlazedTerracotta(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.MAGENTA)
                        .setHardnessAndResistance(1.4F)));
        register("light_blue_glazed_terracotta", new BlockGlazedTerracotta(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.LIGHT_BLUE)
                        .setHardnessAndResistance(1.4F)));
        register("yellow_glazed_terracotta", new BlockGlazedTerracotta(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.YELLOW)
                        .setHardnessAndResistance(1.4F)));
        register("lime_glazed_terracotta", new BlockGlazedTerracotta(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.LIME)
                        .setHardnessAndResistance(1.4F)));
        register("pink_glazed_terracotta", new BlockGlazedTerracotta(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.PINK)
                        .setHardnessAndResistance(1.4F)));
        register("gray_glazed_terracotta", new BlockGlazedTerracotta(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.GRAY)
                        .setHardnessAndResistance(1.4F)));
        register("light_gray_glazed_terracotta", new BlockGlazedTerracotta(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.LIGHT_GRAY)
                        .setHardnessAndResistance(1.4F)));
        register("cyan_glazed_terracotta", new BlockGlazedTerracotta(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.CYAN)
                        .setHardnessAndResistance(1.4F)));
        register("purple_glazed_terracotta", new BlockGlazedTerracotta(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.PURPLE)
                        .setHardnessAndResistance(1.4F)));
        register("blue_glazed_terracotta", new BlockGlazedTerracotta(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.BLUE)
                        .setHardnessAndResistance(1.4F)));
        register("brown_glazed_terracotta", new BlockGlazedTerracotta(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.BROWN)
                        .setHardnessAndResistance(1.4F)));
        register("green_glazed_terracotta", new BlockGlazedTerracotta(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.GREEN)
                        .setHardnessAndResistance(1.4F)));
        register("red_glazed_terracotta", new BlockGlazedTerracotta(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.RED)
                        .setHardnessAndResistance(1.4F)));
        register("black_glazed_terracotta", new BlockGlazedTerracotta(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.BLACK)
                        .setHardnessAndResistance(1.4F)));
        Block whiteConcrete = new Block(Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.WHITE)
                .setHardnessAndResistance(1.8F));
        Block orangeConcrete = new Block(Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.ORANGE)
                .setHardnessAndResistance(1.8F));
        Block magentaConcrete = new Block(Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.MAGENTA)
                .setHardnessAndResistance(1.8F));
        Block lightBlueConcrete = new Block(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.LIGHT_BLUE)
                        .setHardnessAndResistance(1.8F));
        Block yellowConcrete = new Block(Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.YELLOW)
                .setHardnessAndResistance(1.8F));
        Block limeConcrete = new Block(Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.LIME)
                .setHardnessAndResistance(1.8F));
        Block pinkConcrete = new Block(Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.PINK)
                .setHardnessAndResistance(1.8F));
        Block grayConcrete = new Block(Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.GRAY)
                .setHardnessAndResistance(1.8F));
        Block lightGrayConcrete = new Block(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.LIGHT_GRAY)
                        .setHardnessAndResistance(1.8F));
        Block cyanConcrete = new Block(Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.CYAN)
                .setHardnessAndResistance(1.8F));
        Block purpleConcrete = new Block(Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.PURPLE)
                .setHardnessAndResistance(1.8F));
        Block blueConcrete = new Block(Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.BLUE)
                .setHardnessAndResistance(1.8F));
        Block brownConcrete = new Block(Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.BROWN)
                .setHardnessAndResistance(1.8F));
        Block greenConcrete = new Block(Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.GREEN)
                .setHardnessAndResistance(1.8F));
        Block redConcrete = new Block(
                Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.RED).setHardnessAndResistance(1.8F));
        Block blackConcrete = new Block(Block.Properties.createBlockProperties(Material.ROCK, EnumDyeColor.BLACK)
                .setHardnessAndResistance(1.8F));
        register("white_concrete", whiteConcrete);
        register("orange_concrete", orangeConcrete);
        register("magenta_concrete", magentaConcrete);
        register("light_blue_concrete", lightBlueConcrete);
        register("yellow_concrete", yellowConcrete);
        register("lime_concrete", limeConcrete);
        register("pink_concrete", pinkConcrete);
        register("gray_concrete", grayConcrete);
        register("light_gray_concrete", lightGrayConcrete);
        register("cyan_concrete", cyanConcrete);
        register("purple_concrete", purpleConcrete);
        register("blue_concrete", blueConcrete);
        register("brown_concrete", brownConcrete);
        register("green_concrete", greenConcrete);
        register("red_concrete", redConcrete);
        register("black_concrete", blackConcrete);
        register("white_concrete_powder", new BlockConcretePowder(whiteConcrete,
                Block.Properties.createBlockProperties(Material.SAND, EnumDyeColor.WHITE)
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.SAND)));
        register("orange_concrete_powder", new BlockConcretePowder(orangeConcrete,
                Block.Properties.createBlockProperties(Material.SAND, EnumDyeColor.ORANGE)
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.SAND)));
        register("magenta_concrete_powder", new BlockConcretePowder(magentaConcrete,
                Block.Properties.createBlockProperties(Material.SAND, EnumDyeColor.MAGENTA)
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.SAND)));
        register("light_blue_concrete_powder", new BlockConcretePowder(lightBlueConcrete,
                Block.Properties.createBlockProperties(Material.SAND, EnumDyeColor.LIGHT_BLUE)
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.SAND)));
        register("yellow_concrete_powder", new BlockConcretePowder(yellowConcrete,
                Block.Properties.createBlockProperties(Material.SAND, EnumDyeColor.YELLOW)
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.SAND)));
        register("lime_concrete_powder", new BlockConcretePowder(limeConcrete,
                Block.Properties.createBlockProperties(Material.SAND, EnumDyeColor.LIME)
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.SAND)));
        register("pink_concrete_powder", new BlockConcretePowder(pinkConcrete,
                Block.Properties.createBlockProperties(Material.SAND, EnumDyeColor.PINK)
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.SAND)));
        register("gray_concrete_powder", new BlockConcretePowder(grayConcrete,
                Block.Properties.createBlockProperties(Material.SAND, EnumDyeColor.GRAY)
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.SAND)));
        register("light_gray_concrete_powder", new BlockConcretePowder(lightGrayConcrete,
                Block.Properties.createBlockProperties(Material.SAND, EnumDyeColor.LIGHT_GRAY)
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.SAND)));
        register("cyan_concrete_powder", new BlockConcretePowder(cyanConcrete,
                Block.Properties.createBlockProperties(Material.SAND, EnumDyeColor.CYAN)
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.SAND)));
        register("purple_concrete_powder", new BlockConcretePowder(purpleConcrete,
                Block.Properties.createBlockProperties(Material.SAND, EnumDyeColor.PURPLE)
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.SAND)));
        register("blue_concrete_powder", new BlockConcretePowder(blueConcrete,
                Block.Properties.createBlockProperties(Material.SAND, EnumDyeColor.BLUE)
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.SAND)));
        register("brown_concrete_powder", new BlockConcretePowder(brownConcrete,
                Block.Properties.createBlockProperties(Material.SAND, EnumDyeColor.BROWN)
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.SAND)));
        register("green_concrete_powder", new BlockConcretePowder(greenConcrete,
                Block.Properties.createBlockProperties(Material.SAND, EnumDyeColor.GREEN)
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.SAND)));
        register("red_concrete_powder", new BlockConcretePowder(redConcrete,
                Block.Properties.createBlockProperties(Material.SAND, EnumDyeColor.RED)
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.SAND)));
        register("black_concrete_powder", new BlockConcretePowder(blackConcrete,
                Block.Properties.createBlockProperties(Material.SAND, EnumDyeColor.BLACK)
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.SAND)));
        BlockKelpTop blockkelptop = new BlockKelpTop(Block.Properties.createBlockProperties(Material.OCEAN_PLANT)
                .setNonSolid()
                .needsRandomTick()
                .instantDestruction()
                .setSoundType(SoundType.WET_GRASS));
        register("kelp", blockkelptop);
        register("kelp_plant", new BlockKelp(blockkelptop, Block.Properties.createBlockProperties(Material.OCEAN_PLANT)
                .setNonSolid()
                .instantDestruction()
                .setSoundType(SoundType.WET_GRASS)));
        register("dried_kelp_block", new Block(
                Block.Properties.createBlockProperties(Material.GRASS, MaterialColor.BROWN)
                        .setHardnessAndResistance(0.5F, 2.5F)
                        .setSoundType(SoundType.PLANT)));
        register("turtle_egg", new BlockTurtleEgg(
                Block.Properties.createBlockProperties(Material.DRAGON_EGG, MaterialColor.SILVER)
                        .setHardnessAndResistance(0.5F)
                        .setSoundType(SoundType.METAL)
                        .needsRandomTick()));
        Block deadTubeCoralBlock = new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                .setHardnessAndResistance(1.5F, 6.0F));
        Block deadBrainCoralBlock = new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                .setHardnessAndResistance(1.5F, 6.0F));
        Block deadBubbleCoralBlock = new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                .setHardnessAndResistance(1.5F, 6.0F));
        Block deadFireCoralBlock = new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                .setHardnessAndResistance(1.5F, 6.0F));
        Block deadHornCoralBlock = new Block(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                .setHardnessAndResistance(1.5F, 6.0F));
        register("dead_tube_coral_block", deadTubeCoralBlock);
        register("dead_brain_coral_block", deadBrainCoralBlock);
        register("dead_bubble_coral_block", deadBubbleCoralBlock);
        register("dead_fire_coral_block", deadFireCoralBlock);
        register("dead_horn_coral_block", deadHornCoralBlock);
        register("tube_coral_block", new BlockCoral(deadTubeCoralBlock,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.BLUE)
                        .setHardnessAndResistance(1.5F, 6.0F)
                        .setSoundType(SoundType.CORAL)));
        register("brain_coral_block", new BlockCoral(deadBrainCoralBlock,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.PINK)
                        .setHardnessAndResistance(1.5F, 6.0F)
                        .setSoundType(SoundType.CORAL)));
        register("bubble_coral_block", new BlockCoral(deadBubbleCoralBlock,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.PURPLE)
                        .setHardnessAndResistance(1.5F, 6.0F)
                        .setSoundType(SoundType.CORAL)));
        register("fire_coral_block", new BlockCoral(deadFireCoralBlock,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.RED)
                        .setHardnessAndResistance(1.5F, 6.0F)
                        .setSoundType(SoundType.CORAL)));
        register("horn_coral_block", new BlockCoral(deadHornCoralBlock,
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.YELLOW)
                        .setHardnessAndResistance(1.5F, 6.0F)
                        .setSoundType(SoundType.CORAL)));
        Block deadTubeCoral = new BlockCoralPlantDead(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                        .setNonSolid()
                        .instantDestruction());
        Block deadBrainCoral = new BlockCoralPlantDead(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                        .setNonSolid()
                        .instantDestruction());
        Block deadBubbleCoral = new BlockCoralPlantDead(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                        .setNonSolid()
                        .instantDestruction());
        Block deadFireCoral = new BlockCoralPlantDead(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                        .setNonSolid()
                        .instantDestruction());
        Block deadHornCoral = new BlockCoralPlantDead(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                        .setNonSolid()
                        .instantDestruction());
        register("dead_tube_coral", deadTubeCoral);
        register("dead_brain_coral", deadBrainCoral);
        register("dead_bubble_coral", deadBubbleCoral);
        register("dead_fire_coral", deadFireCoral);
        register("dead_horn_coral", deadHornCoral);
        register("tube_coral", new BlockCoralPlant(deadTubeCoral,
                Block.Properties.createBlockProperties(Material.OCEAN_PLANT, MaterialColor.BLUE)
                        .setNonSolid()
                        .instantDestruction()
                        .setSoundType(SoundType.WET_GRASS)));
        register("brain_coral", new BlockCoralPlant(deadBrainCoral,
                Block.Properties.createBlockProperties(Material.OCEAN_PLANT, MaterialColor.PINK)
                        .setNonSolid()
                        .instantDestruction()
                        .setSoundType(SoundType.WET_GRASS)));
        register("bubble_coral", new BlockCoralPlant(deadBubbleCoral,
                Block.Properties.createBlockProperties(Material.OCEAN_PLANT, MaterialColor.PURPLE)
                        .setNonSolid()
                        .instantDestruction()
                        .setSoundType(SoundType.WET_GRASS)));
        register("fire_coral", new BlockCoralPlant(deadFireCoral,
                Block.Properties.createBlockProperties(Material.OCEAN_PLANT, MaterialColor.RED)
                        .setNonSolid()
                        .instantDestruction()
                        .setSoundType(SoundType.WET_GRASS)));
        register("horn_coral", new BlockCoralPlant(deadHornCoral,
                Block.Properties.createBlockProperties(Material.OCEAN_PLANT, MaterialColor.YELLOW)
                        .setNonSolid()
                        .instantDestruction()
                        .setSoundType(SoundType.WET_GRASS)));
        Block deadTubeCoralWallFan = new BlockCoralWallFanDead(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                        .setNonSolid()
                        .instantDestruction());
        Block deadBrainCoralWallFan = new BlockCoralWallFanDead(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                        .setNonSolid()
                        .instantDestruction());
        Block deadBubbleCoralWallFan = new BlockCoralWallFanDead(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                        .setNonSolid()
                        .instantDestruction());
        Block deadFireCoralWallFan = new BlockCoralWallFanDead(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                        .setNonSolid()
                        .instantDestruction());
        Block deadHornCoralWallFan = new BlockCoralWallFanDead(
                Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                        .setNonSolid()
                        .instantDestruction());
        register("dead_tube_coral_wall_fan", deadTubeCoralWallFan);
        register("dead_brain_coral_wall_fan", deadBrainCoralWallFan);
        register("dead_bubble_coral_wall_fan", deadBubbleCoralWallFan);
        register("dead_fire_coral_wall_fan", deadFireCoralWallFan);
        register("dead_horn_coral_wall_fan", deadHornCoralWallFan);
        register("tube_coral_wall_fan", new BlockCoralWallFan(deadTubeCoralWallFan,
                Block.Properties.createBlockProperties(Material.OCEAN_PLANT, MaterialColor.BLUE)
                        .setNonSolid()
                        .instantDestruction()
                        .setSoundType(SoundType.WET_GRASS)));
        register("brain_coral_wall_fan", new BlockCoralWallFan(deadBrainCoralWallFan,
                Block.Properties.createBlockProperties(Material.OCEAN_PLANT, MaterialColor.PINK)
                        .setNonSolid()
                        .instantDestruction()
                        .setSoundType(SoundType.WET_GRASS)));
        register("bubble_coral_wall_fan", new BlockCoralWallFan(deadBubbleCoralWallFan,
                Block.Properties.createBlockProperties(Material.OCEAN_PLANT, MaterialColor.PURPLE)
                        .setNonSolid()
                        .instantDestruction()
                        .setSoundType(SoundType.WET_GRASS)));
        register("fire_coral_wall_fan", new BlockCoralWallFan(deadFireCoralWallFan,
                Block.Properties.createBlockProperties(Material.OCEAN_PLANT, MaterialColor.RED)
                        .setNonSolid()
                        .instantDestruction()
                        .setSoundType(SoundType.WET_GRASS)));
        register("horn_coral_wall_fan", new BlockCoralWallFan(deadHornCoralWallFan,
                Block.Properties.createBlockProperties(Material.OCEAN_PLANT, MaterialColor.YELLOW)
                        .setNonSolid()
                        .instantDestruction()
                        .setSoundType(SoundType.WET_GRASS)));
        Block block80 = new BlockCoralFan(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                .setNonSolid()
                .instantDestruction());
        Block block81 = new BlockCoralFan(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                .setNonSolid()
                .instantDestruction());
        Block block82 = new BlockCoralFan(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                .setNonSolid()
                .instantDestruction());
        Block block83 = new BlockCoralFan(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                .setNonSolid()
                .instantDestruction());
        Block block84 = new BlockCoralFan(Block.Properties.createBlockProperties(Material.ROCK, MaterialColor.GRAY)
                .setNonSolid()
                .instantDestruction());
        register("dead_tube_coral_fan", block80);
        register("dead_brain_coral_fan", block81);
        register("dead_bubble_coral_fan", block82);
        register("dead_fire_coral_fan", block83);
        register("dead_horn_coral_fan", block84);
        register("tube_coral_fan", new BlockCoralFin(block80,
                Block.Properties.createBlockProperties(Material.OCEAN_PLANT, MaterialColor.BLUE)
                        .setNonSolid()
                        .instantDestruction()
                        .setSoundType(SoundType.WET_GRASS)));
        register("brain_coral_fan", new BlockCoralFin(block81,
                Block.Properties.createBlockProperties(Material.OCEAN_PLANT, MaterialColor.PINK)
                        .setNonSolid()
                        .instantDestruction()
                        .setSoundType(SoundType.WET_GRASS)));
        register("bubble_coral_fan", new BlockCoralFin(block82,
                Block.Properties.createBlockProperties(Material.OCEAN_PLANT, MaterialColor.PURPLE)
                        .setNonSolid()
                        .instantDestruction()
                        .setSoundType(SoundType.WET_GRASS)));
        register("fire_coral_fan", new BlockCoralFin(block83,
                Block.Properties.createBlockProperties(Material.OCEAN_PLANT, MaterialColor.RED)
                        .setNonSolid()
                        .instantDestruction()
                        .setSoundType(SoundType.WET_GRASS)));
        register("horn_coral_fan", new BlockCoralFin(block84,
                Block.Properties.createBlockProperties(Material.OCEAN_PLANT, MaterialColor.YELLOW)
                        .setNonSolid()
                        .instantDestruction()
                        .setSoundType(SoundType.WET_GRASS)));
        register("sea_pickle", new BlockSeaPickle(
                Block.Properties.createBlockProperties(Material.OCEAN_PLANT, MaterialColor.GREEN)
                        .setLightLevel(3)
                        .setSoundType(SoundType.SLIME)));
        register("blue_ice", new BlockBlueIce(Block.Properties.createBlockProperties(Material.PACKED_ICE)
                .setHardnessAndResistance(2.8F)
                .setSlipperiness(0.989F)
                .setSoundType(SoundType.GLASS)));
        register("conduit", new BlockConduit(
                Block.Properties.createBlockProperties(Material.GLASS, MaterialColor.DIAMOND)
                        .setHardnessAndResistance(3.0F)
                        .setLightLevel(15)));
        register("void_air", new BlockAir(Block.Properties.createBlockProperties(Material.AIR).setNonSolid()));
        register("cave_air", new BlockAir(Block.Properties.createBlockProperties(Material.AIR).setNonSolid()));
        register("bubble_column",
                new BlockBubbleColumn(Block.Properties.createBlockProperties(Material.BUBBLE_COLUMN).setNonSolid()));
        register("structure_block", new BlockStructure(
                Block.Properties.createBlockProperties(Material.IRON, MaterialColor.SILVER)
                        .setHardnessAndResistance(-1.0F, 3600000.0F)));

        //MITE Block Start
        {
            //Ores
            {
                register("copper_ore",
                        new BlockOre(Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(2.5F, 3.0F),
                                2));
                register("silver_ore",
                        new BlockOre(Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(2.5F, 3.0F),
                                2));
                register("mithril_ore",
                        new BlockOre(Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(3.5F, 6.0F),
                                3));
                register("tungsten_ore",
                        new BlockOre(Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(4.0F, 15.0F),
                                4));
                register("adamantium_ore",
                        new BlockOre(Block.Properties.createBlockProperties(Material.ROCK).setHardnessAndResistance(4.5F, 30.0F),
                                5));
            }

            //Material Blocks
            {
                register("adamantium_block",
                        new BlockMaterial(Block.Properties.createBlockProperties(Material.IRON).setHardnessAndResistance(307.2F, 3600000.0F),
                                7));
                register("tungsten_block",
                        new BlockMaterial(Block.Properties.createBlockProperties(Material.IRON).setHardnessAndResistance(153.6F, 1800000.0F),
                                6));
                register("mithril_block",
                        new BlockMaterial(Block.Properties.createBlockProperties(Material.IRON).setHardnessAndResistance(76.8F, 90000.0F),
                                5));
                register("ancient_metal_block",
                        new BlockMaterial(Block.Properties.createBlockProperties(Material.IRON).setHardnessAndResistance(19.2F,90000.0F),
                                4));
                register("silver_block",
                        new BlockMaterial(Block.Properties.createBlockProperties(Material.IRON).setHardnessAndResistance(4.8F, 45000.0F),
                                3));
                register("copper_block",
                        new BlockMaterial(Block.Properties.createBlockProperties(Material.IRON).setHardnessAndResistance(4.8F, 45000.0F),
                                3));
            }
            //ANVILS
            {
                //copper
                register("copper_anvil", new BlockAnvil(Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.BROWN)
                        .setHardnessAndResistance(2.5F, 1200.0F)
                        .setSoundType(SoundType.ANVIL),ItemTier.COPPER,198400,2));
                register("chipped_copper_anvil", new BlockAnvil(
                        Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.IRON)
                                .setHardnessAndResistance(2.4F, 1200.0F)
                                .setSoundType(SoundType.ANVIL),ItemTier.COPPER,66133,198400,2));
                register("damaged_copper_anvil", new BlockAnvil(
                        Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.IRON)
                                .setHardnessAndResistance(2.3F, 1200.0F)
                                .setSoundType(SoundType.ANVIL),ItemTier.COPPER,132266,198400,2));

                //silver
                register("silver_anvil", new BlockAnvil(Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.SILVER)
                        .setHardnessAndResistance(2.5F, 1200.0F)
                        .setSoundType(SoundType.ANVIL),ItemTier.SILVER,198400,2));
                register("chipped_silver_anvil", new BlockAnvil(
                        Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.IRON)
                                .setHardnessAndResistance(2.4F, 1200.0F)
                                .setSoundType(SoundType.ANVIL),ItemTier.SILVER,66133,198400,2));
                register("damaged_silver_anvil", new BlockAnvil(
                        Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.IRON)
                                .setHardnessAndResistance(2.3F, 1200.0F)
                                .setSoundType(SoundType.ANVIL),ItemTier.SILVER,132266,198400,2));

                //gold
                register("gold_anvil", new BlockAnvil(Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.GOLD)
                        .setHardnessAndResistance(2.5F, 1200.0F)
                        .setSoundType(SoundType.ANVIL),ItemTier.GOLD,198400,2));
                register("chipped_gold_anvil", new BlockAnvil(
                        Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.IRON)
                                .setHardnessAndResistance(2.4F, 1200.0F)
                                .setSoundType(SoundType.ANVIL),ItemTier.GOLD,66133,198400,2));
                register("damaged_gold_anvil", new BlockAnvil(
                        Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.IRON)
                                .setHardnessAndResistance(2.3F, 1200.0F)
                                .setSoundType(SoundType.ANVIL),ItemTier.GOLD,132266,198400,2));

                //ancient_metal
                register("ancient_metal_anvil", new BlockAnvil(Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.IRON)
                        .setHardnessAndResistance(2.5F, 1200.0F)
                        .setSoundType(SoundType.ANVIL),ItemTier.ANCIENT_METAL,793600,3));
                register("chipped_ancient_metal_anvil", new BlockAnvil(
                        Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.IRON)
                                .setHardnessAndResistance(2.4F, 1200.0F)
                                .setSoundType(SoundType.ANVIL),ItemTier.ANCIENT_METAL,264533,793600,3));
                register("damaged_ancient_metal_anvil", new BlockAnvil(
                        Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.IRON)
                                .setHardnessAndResistance(2.3F, 1200.0F)
                                .setSoundType(SoundType.ANVIL),ItemTier.ANCIENT_METAL,529066,793600,3));

                //mithril
                register("mithril_anvil", new BlockAnvil(Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.SILVER)
                        .setHardnessAndResistance(2.5F, 1200.0F)
                        .setSoundType(SoundType.ANVIL),ItemTier.MITHRIL,3174400,4));
                register("chipped_mithril_anvil", new BlockAnvil(
                        Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.IRON)
                                .setHardnessAndResistance(2.4F, 1200.0F)
                                .setSoundType(SoundType.ANVIL),ItemTier.MITHRIL,1058133,3174400,4));
                register("damaged_mithril_anvil", new BlockAnvil(
                        Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.IRON)
                                .setHardnessAndResistance(2.3F, 1200.0F)
                                .setSoundType(SoundType.ANVIL),ItemTier.MITHRIL,2116266,3174400,4));

                //tungsten
                register("tungsten_anvil", new BlockAnvil(Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.GRAY)
                        .setHardnessAndResistance(2.5F, 1200.0F)
                        .setSoundType(SoundType.ANVIL),ItemTier.TUNGSTEN,6348800,5));
                register("chipped_tungsten_anvil", new BlockAnvil(
                        Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.IRON)
                                .setHardnessAndResistance(2.4F, 1200.0F)
                                .setSoundType(SoundType.ANVIL),ItemTier.TUNGSTEN,2116266,6348800,5));
                register("damaged_tungsten_anvil", new BlockAnvil(
                        Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.IRON)
                                .setHardnessAndResistance(2.3F, 1200.0F)
                                .setSoundType(SoundType.ANVIL),ItemTier.TUNGSTEN,4232532,6348800,5));

                //adamantium
                register("adamantium_anvil", new BlockAnvil(Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.PURPLE)
                        .setHardnessAndResistance(2.5F, 1200.0F)
                        .setSoundType(SoundType.ANVIL),ItemTier.ADAMANTIUM,12697600,6));
                register("chipped_adamantium_anvil", new BlockAnvil(
                        Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.IRON)
                                .setHardnessAndResistance(2.4F, 1200.0F)
                                .setSoundType(SoundType.ANVIL),ItemTier.ADAMANTIUM,4232533,12697600,6));
                register("damaged_adamantium_anvil", new BlockAnvil(
                        Block.Properties.createBlockProperties(Material.ANVIL, MaterialColor.IRON)
                                .setHardnessAndResistance(2.3F, 1200.0F)
                                .setSoundType(SoundType.ANVIL),ItemTier.ADAMANTIUM,8465066,12697600,6));
            }

            //Crafting Tables
            {
                register("flint_crafting_table", new BlockWorkbench(1,Block.Properties.createBlockProperties(Material.WORKBENCH)
                        .setHardnessAndResistance(2.5F)
                        .setSoundType(SoundType.WOOD)));
                register("obsidian_crafting_table", new BlockWorkbench(1,Block.Properties.createBlockProperties(Material.WORKBENCH)
                        .setHardnessAndResistance(2.5F)
                        .setSoundType(SoundType.WOOD)));
                register("copper_crafting_table", new BlockWorkbench(2,Block.Properties.createBlockProperties(Material.WORKBENCH)
                        .setHardnessAndResistance(2.5F)
                        .setSoundType(SoundType.WOOD)));
                register("silver_crafting_table", new BlockWorkbench(2,Block.Properties.createBlockProperties(Material.WORKBENCH)
                        .setHardnessAndResistance(2.5F)
                        .setSoundType(SoundType.WOOD)));
                register("iron_crafting_table", new BlockWorkbench(3,Block.Properties.createBlockProperties(Material.WORKBENCH)
                        .setHardnessAndResistance(2.5F)
                        .setSoundType(SoundType.WOOD)));
                register("gold_crafting_table", new BlockWorkbench(2,Block.Properties.createBlockProperties(Material.WORKBENCH)
                        .setHardnessAndResistance(2.5F)
                        .setSoundType(SoundType.WOOD)));
                register("ancient_metal_crafting_table", new BlockWorkbench(4,Block.Properties.createBlockProperties(Material.WORKBENCH)
                        .setHardnessAndResistance(2.5F)
                        .setSoundType(SoundType.WOOD)));
                register("mithril_crafting_table", new BlockWorkbench(5,Block.Properties.createBlockProperties(Material.WORKBENCH)
                        .setHardnessAndResistance(2.5F)
                        .setSoundType(SoundType.WOOD)));
                register("tungsten_crafting_table", new BlockWorkbench(6,Block.Properties.createBlockProperties(Material.WORKBENCH)
                        .setHardnessAndResistance(2.5F)
                        .setSoundType(SoundType.WOOD)));
                register("adamantium_crafting_table", new BlockWorkbench(7,Block.Properties.createBlockProperties(Material.WORKBENCH)
                        .setHardnessAndResistance(2.5F)
                        .setSoundType(SoundType.WOOD)));
            }
        }

        for (Block block85 : IRegistry.field_212618_g) {
            for (IBlockState iblockstate : block85.getStateContainer().getValidStates()) {
                BLOCK_STATE_IDS.add(iblockstate);
            }
        }

    }

    public static void replaceBlock(IBlockState p_196263_0_, IBlockState p_196263_1_, IWorld p_196263_2_, BlockPos p_196263_3_, int p_196263_4_) {
        if (p_196263_1_ != p_196263_0_) {
            if (p_196263_1_.isAir()) {
                if (!p_196263_2_.isRemote()) {
                    p_196263_2_.destroyBlock(p_196263_3_, (p_196263_4_ & 32) == 0);
                }
            } else {
                p_196263_2_.setBlockState(p_196263_3_, p_196263_1_, p_196263_4_ & -33);
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static boolean shouldSideBeRendered(IBlockState p_176225_0_, IBlockReader p_176225_1_, BlockPos p_176225_2_, EnumFacing p_176225_3_) {
        BlockPos blockpos = p_176225_2_.offset(p_176225_3_);
        IBlockState iblockstate = p_176225_1_.getBlockState(blockpos);
        if (p_176225_0_.isSideInvisible(iblockstate, p_176225_3_)) {
            return false;
        } else if (iblockstate.isSolid()) {
            Block.RenderSideCacheKey block$rendersidecachekey = new Block.RenderSideCacheKey(p_176225_0_, iblockstate,
                    p_176225_3_);
            Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap = SHOULD_SIDE_RENDER_CACHE
                    .get();
            byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block$rendersidecachekey);
            if (b0 != 127) {
                return b0 != 0;
            } else {
                VoxelShape voxelshape = p_176225_0_.getRenderShape(p_176225_1_, p_176225_2_);
                VoxelShape voxelshape1 = iblockstate.getRenderShape(p_176225_1_, blockpos);
                boolean flag = !VoxelShapes.func_197875_a(voxelshape, voxelshape1, p_176225_3_);
                if (object2bytelinkedopenhashmap.size() == 200) {
                    object2bytelinkedopenhashmap.removeLastByte();
                }

                object2bytelinkedopenhashmap.putAndMoveToFirst(block$rendersidecachekey, (byte) (flag ? 1 : 0));
                return flag;
            }
        } else {
            return true;
        }
    }

    public static void spawnAsEntity(World p_180635_0_, BlockPos p_180635_1_, ItemStack p_180635_2_) {
        if (!p_180635_0_.isRemote && !p_180635_2_.isEmpty() && p_180635_0_.getGameRules().getBoolean("doTileDrops")) {
            float f = 0.5F;
            double d0 = (double) (p_180635_0_.rand.nextFloat() * 0.5F) + 0.25D;
            double d1 = (double) (p_180635_0_.rand.nextFloat() * 0.5F) + 0.25D;
            double d2 = (double) (p_180635_0_.rand.nextFloat() * 0.5F) + 0.25D;
            EntityItem entityitem = new EntityItem(p_180635_0_, (double) p_180635_1_.getX() + d0,
                    (double) p_180635_1_.getY() + d1, (double) p_180635_1_.getZ() + d2, p_180635_2_);
            entityitem.setDefaultPickupDelay();
            p_180635_0_.spawnEntity(entityitem);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack p_190948_1_, @Nullable IBlockReader p_190948_2_, List<ITextComponent> p_190948_3_, ITooltipFlag p_190948_4_) {
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
        switch (p_196266_4_) {
            case LAND:
                return !isOpaque(this.getCollisionShape(p_196266_1_, p_196266_2_, p_196266_3_));
            case WATER:
                return p_196266_2_.getFluidState(p_196266_3_).isTagged(FluidTags.WATER);
            case AIR:
                return !isOpaque(this.getCollisionShape(p_196266_1_, p_196266_2_, p_196266_3_));
            default:
                return false;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(IBlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
    }

    public Item asItem() {
        return Item.getItemFromBlock(this);
    }

    public boolean canDropFromExplosion(Explosion p_149659_1_) {
        return true;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean canEntitySpawn(IBlockState p_189872_1_, Entity p_189872_2_) {
        return true;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean canProvidePower(IBlockState p_149744_1_) {
        return false;
    }

    protected boolean canSilkHarvest() {
        return this.getDefaultState().isFullCube() && !this.hasTileEntity();
    }

    public boolean canSpawnInBlock() {
        return !this.material.isSolid() && !this.material.isLiquid();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean causesSuffocation(IBlockState p_176214_1_) {
        return this.material.blocksMovement() && p_176214_1_.isFullCube();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void dropBlockAsItemWithChance(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, float chanceToDrop, int fortuneLevel) {
        if (!worldIn.isRemote) {
            int i = this.getItemsToDropCount(blockCurrentState, fortuneLevel, worldIn, blockAt, worldIn.rand);

            for (int j = 0; j < i; ++j) {
                if (!(chanceToDrop < 1.0F) || !(worldIn.rand.nextFloat() > chanceToDrop)) {
                    Item item = this.getItemDropped(blockCurrentState, worldIn, blockAt, fortuneLevel).asItem();
                    if (item != Items.AIR) {
                        spawnAsEntity(worldIn, blockAt, new ItemStack(item));
                    }
                }
            }
        }
    }

    public void dropXpOnBlockBreak(World p_180637_1_, BlockPos p_180637_2_, int p_180637_3_) {
        if (!p_180637_1_.isRemote && p_180637_1_.getGameRules().getBoolean("doTileDrops")) {
            while (p_180637_3_ > 0) {
                int i = EntityXPOrb.getXPSplit(p_180637_3_);
                p_180637_3_ -= i;
                p_180637_1_.spawnEntity(new EntityXPOrb(p_180637_1_, (double) p_180637_2_.getX() + 0.5D,
                        (double) p_180637_2_.getY() + 0.5D, (double) p_180637_2_.getZ() + 0.5D, i));
            }
        }

    }

    @Deprecated
    public boolean eventReceived(IBlockState p_189539_1_, World p_189539_2_, BlockPos p_189539_3_, int p_189539_4_, int p_189539_5_) {
        return false;
    }

    public void fillItemGroup(ItemGroup p_149666_1_, NonNullList<ItemStack> p_149666_2_) {
        p_149666_2_.add(new ItemStack(this));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
    }

    public void fillWithRain(World p_176224_1_, BlockPos p_176224_2_) {
    }

    /**
     * @deprecated
     */
    @Deprecated
    public MaterialColor func_180659_g(IBlockState p_180659_1_, IBlockReader p_180659_2_, BlockPos p_180659_3_) {
        return this.blockMapColor;
    }

    public boolean func_200123_i(IBlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
        return !isOpaque(p_200123_1_.getShape(p_200123_2_, p_200123_3_)) && p_200123_1_.getFluidState().isEmpty();
    }

    @Deprecated
    @OnlyIn(Dist.CLIENT)
    public float getAmbientOcclusionLightValue(IBlockState p_185485_1_) {
        return p_185485_1_.isBlockNormalCube() ? 0.2F : 1.0F;
    }

    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return BlockFaceShape.SOLID;
    }

    @Deprecated
    public float getBlockHardness(IBlockState p_176195_1_, IBlockReader p_176195_2_, BlockPos p_176195_3_) {
        return this.blockHardness;
    }

    @Deprecated
    public VoxelShape getCollisionShape(IBlockState p_196268_1_, IBlockReader p_196268_2_, BlockPos p_196268_3_) {
        return this.isSolid ? p_196268_1_.getShape(p_196268_2_, p_196268_3_) : VoxelShapes.func_197880_a();
    }

    @Deprecated
    public int getComparatorInputOverride(IBlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
        return 0;
    }

    public final IBlockState getDefaultState() {
        return this.defaultState;
    }

    protected final void setDefaultState(IBlockState p_180632_1_) {
        this.defaultState = p_180632_1_;
    }

    public float getExplosionResistance() {
        return this.blockResistance;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public IFluidState getFluidState(IBlockState p_204507_1_) {
        return Fluids.EMPTY.getDefaultState();
    }

    public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, IBlockState p_185473_3_) {
        return new ItemStack(this);
    }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
        return this;
    }

    public int getItemsToDropCount(IBlockState p_196251_1_, int p_196251_2_, World p_196251_3_, BlockPos p_196251_4_, Random p_196251_5_) {
        return this.quantityDropped(p_196251_1_, p_196251_5_);
    }

    @Deprecated
    public int getLightValue(IBlockState p_149750_1_) {
        return this.lightValue;
    }

    @Deprecated
    public Material getMaterial(IBlockState p_149688_1_) {
        return this.material;
    }

    @OnlyIn(Dist.CLIENT)
    public ITextComponent getNameTextComponent() {
        return new TextComponentTranslation(this.getTranslationKey());
    }

    @Deprecated
    public Vec3d getOffset(IBlockState p_190949_1_, IBlockReader p_190949_2_, BlockPos p_190949_3_) {
        Block.EnumOffsetType block$enumoffsettype = this.getOffsetType();
        if (block$enumoffsettype == Block.EnumOffsetType.NONE) {
            return Vec3d.ZERO;
        } else {
            long i = MathHelper.getCoordinateRandom(p_190949_3_.getX(), 0, p_190949_3_.getZ());
            return new Vec3d(((double) ((float) (i & 15L) / 15.0F) - 0.5D) * 0.5D,
                    block$enumoffsettype == Block.EnumOffsetType.XYZ ? ((double) ((float) (i >> 4 & 15L) / 15.0F) - 1.0D) * 0.2D : 0.0D,
                    ((double) ((float) (i >> 8 & 15L) / 15.0F) - 0.5D) * 0.5D);
        }
    }

    public Block.EnumOffsetType getOffsetType() {
        return Block.EnumOffsetType.NONE;
    }

    @Deprecated
    public int getOpacity(IBlockState p_200011_1_, IBlockReader p_200011_2_, BlockPos p_200011_3_) {
        if (p_200011_1_.isOpaqueCube(p_200011_2_, p_200011_3_)) {
            return p_200011_2_.getMaxLightLevel();
        } else {
            return p_200011_1_.func_200131_a(p_200011_2_, p_200011_3_) ? 0 : 1;
        }
    }

    @Deprecated
    @OnlyIn(Dist.CLIENT)
    public int getPackedLightmapCoords(IBlockState p_185484_1_, IWorldReader p_185484_2_, BlockPos p_185484_3_) {
        int i = p_185484_2_.getCombinedLight(p_185484_3_, p_185484_1_.getLightValue());
        if (i == 0 && p_185484_1_.getBlock() instanceof BlockSlab) {
            p_185484_3_ = p_185484_3_.down();
            p_185484_1_ = p_185484_2_.getBlockState(p_185484_3_);
            return p_185484_2_.getCombinedLight(p_185484_3_, p_185484_1_.getLightValue());
        } else {
            return i;
        }
    }

    @Deprecated
    public float getPlayerRelativeBlockHardness(IBlockState p_180647_1_, EntityPlayer p_180647_2_, IBlockReader p_180647_3_, BlockPos p_180647_4_) {
        float f = p_180647_1_.getBlockHardness(p_180647_3_, p_180647_4_);
        if (f == -1.0F) {
            return 0.0F;
        } else {
            double i = p_180647_2_.canHarvestBlock(p_180647_1_) ? 30 : -1;
            return p_180647_2_.getDigSpeed(p_180647_1_) / f / (float) i;
        }
    }

    @Deprecated
    @OnlyIn(Dist.CLIENT)
    public long getPositionRandom(IBlockState p_209900_1_, BlockPos p_209900_2_) {
        return MathHelper.getPositionRandom(p_209900_2_);
    }

    @Deprecated
    public EnumPushReaction getPushReaction(IBlockState p_149656_1_) {
        return this.material.getPushReaction();
    }

    @Deprecated
    public VoxelShape getRaytraceShape(IBlockState p_199600_1_, IBlockReader p_199600_2_, BlockPos p_199600_3_) {
        return VoxelShapes.func_197880_a();
    }

    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Deprecated
    public VoxelShape getRenderShape(IBlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_) {
        return p_196247_1_.getShape(p_196247_2_, p_196247_3_);
    }

    @Deprecated
    public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
        return EnumBlockRenderType.MODEL;
    }

    @Deprecated
    public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
        return VoxelShapes.func_197868_b();
    }

    protected ItemStack getSilkTouchDrop(IBlockState p_180643_1_) {
        return new ItemStack(this);
    }

    public float getSlipperiness() {
        return this.slipperiness;
    }

    public SoundType getSoundType() {
        return this.soundType;
    }

    public StateContainer<Block, IBlockState> getStateContainer() {
        return this.stateContainer;
    }

    @Nullable
    public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        return this.getDefaultState();
    }

    @Deprecated
    public int getStrongPower(IBlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, EnumFacing p_176211_4_) {
        return 0;
    }

    public boolean getTickRandomly(IBlockState p_149653_1_) {
        return this.needsRandomTick;
    }

    public String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.makeTranslationKey("block", IRegistry.field_212618_g.func_177774_c(this));
        }

        return this.translationKey;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public int getWeakPower(IBlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, EnumFacing p_180656_4_) {
        return 0;
    }

    public void harvestBlock(World p_180657_1_, EntityPlayer p_180657_2_, BlockPos p_180657_3_, IBlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
        p_180657_2_.func_71029_a(StatList.BLOCK_MINED.func_199076_b(this));
        p_180657_2_.addExhaustion(0.005F);
        if (this.canSilkHarvest() && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, p_180657_6_) > 0) {
            ItemStack itemstack = this.getSilkTouchDrop(p_180657_4_);
            spawnAsEntity(p_180657_1_, p_180657_3_, itemstack);
        } else {
            int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, p_180657_6_);
            p_180657_4_.dropBlockAsItem(p_180657_1_, p_180657_3_, i);
        }

    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean hasComparatorInputOverride(IBlockState p_149740_1_) {
        return false;
    }

    /**
     * @deprecated
     */
    @Deprecated
    @OnlyIn(Dist.CLIENT)
    public boolean hasCustomBreakingProgress(IBlockState p_190946_1_) {
        return false;
    }

    public boolean hasTileEntity() {
        return this instanceof ITileEntityProvider;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isAir(IBlockState p_196261_1_) {
        return false;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isBlockNormalCube(IBlockState p_149637_1_) {
        return p_149637_1_.getMaterial().blocksMovement() && p_149637_1_.isFullCube();
    }

    public boolean isCollidable(IBlockState p_200293_1_) {
        return this.isCollidable();
    }

    public boolean isCollidable() {
        return true;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isFullCube(IBlockState p_149686_1_) {
        return true;
    }

    public boolean isIn(Tag<Block> p_203417_1_) {
        return p_203417_1_.contains(this);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isNormalCube(IBlockState p_149721_1_) {
        return p_149721_1_.getMaterial().isOpaque() && p_149721_1_.isFullCube() && !p_149721_1_.canProvidePower();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public final boolean isOpaqueCube(IBlockState p_200012_1_, IBlockReader p_200012_2_, BlockPos p_200012_3_) {
        boolean flag = p_200012_1_.isSolid();
        VoxelShape voxelshape = flag ? p_200012_1_.getRenderShape(p_200012_2_,
                p_200012_3_) : VoxelShapes.func_197880_a();
        return isOpaque(voxelshape);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isReplaceable(IBlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
        return this.material.isReplaceable() && p_196253_2_.getItem().getItem() != this.asItem();
    }

    /**
     * @deprecated
     */
    @Deprecated
    @OnlyIn(Dist.CLIENT)
    public boolean isSideInvisible(IBlockState p_200122_1_, IBlockState p_200122_2_, EnumFacing p_200122_3_) {
        return false;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isSolid(IBlockState p_200124_1_) {
        return this.isSolid && p_200124_1_.getBlock().getRenderLayer() == BlockRenderLayer.SOLID;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isTopSolid(IBlockState p_185481_1_) {
        return p_185481_1_.getMaterial().isOpaque() && p_185481_1_.isFullCube();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
        return true;
    }

    public boolean isVariableOpacity() {
        return this.variableOpacity;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
        return p_185471_1_;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean needsPostProcessing(IBlockState p_201783_1_, IBlockReader p_201783_2_, BlockPos p_201783_3_) {
        return false;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
        return false;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void onBlockClicked(IBlockState p_196270_1_, World p_196270_2_, BlockPos p_196270_3_, EntityPlayer p_196270_4_) {
    }

    public void onBlockHarvested(World p_176208_1_, BlockPos p_176208_2_, IBlockState p_176208_3_, EntityPlayer p_176208_4_) {
        p_176208_1_.playEvent(p_176208_4_, 2001, p_176208_2_, getStateId(p_176208_3_));
    }

    public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, IBlockState p_180633_3_, @Nullable EntityLivingBase p_180633_4_, ItemStack p_180633_5_) {
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void onEntityCollision(IBlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
    }

    public void onEntityWalk(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {
    }

    public void onExplosionDestroy(World p_180652_1_, BlockPos p_180652_2_, Explosion p_180652_3_) {
    }

    public void onFallenUpon(World p_180658_1_, BlockPos p_180658_2_, Entity p_180658_3_, float p_180658_4_) {
        p_180658_3_.fall(p_180658_4_, 1.0F);
    }

    public void onLanded(IBlockReader p_176216_1_, Entity p_176216_2_) {
        p_176216_2_.motionY = 0.0D;
    }

    public void onPlayerDestroy(IWorld p_176206_1_, BlockPos p_176206_2_, IBlockState p_176206_3_) {
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
    }

    public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
        return 1;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void randomTick(IBlockState p_196265_1_, World p_196265_2_, BlockPos p_196265_3_, Random p_196265_4_) {
        this.tick(p_196265_1_, p_196265_2_, p_196265_3_, p_196265_4_);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
        return p_185499_1_;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
    }

    public int tickRate(IWorldReaderBase p_149738_1_) {
        return 10;
    }

    public String toString() {
        return "Block{" + IRegistry.field_212618_g.func_177774_c(this) + "}";
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void updateDiagonalNeighbors(IBlockState p_196248_1_, IWorld p_196248_2_, BlockPos p_196248_3_, int p_196248_4_) {
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void updateNeighbors(IBlockState p_196242_1_, IWorld p_196242_2_, BlockPos p_196242_3_, int p_196242_4_) {
        try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
            for (EnumFacing enumfacing : field_212556_a) {
                blockpos$pooledmutableblockpos.setPos(p_196242_3_).move(enumfacing);
                IBlockState iblockstate = p_196242_2_.getBlockState(blockpos$pooledmutableblockpos);
                IBlockState iblockstate1 = iblockstate.updatePostPlacement(enumfacing.getOpposite(), p_196242_1_,
                        p_196242_2_, blockpos$pooledmutableblockpos, p_196242_3_);
                replaceBlock(iblockstate, iblockstate1, p_196242_2_, blockpos$pooledmutableblockpos, p_196242_4_);
            }
        }

    }

    /**
     * @deprecated
     */
    @Deprecated
    public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        return p_196271_1_;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public final boolean useNeighborBrightness(IBlockState p_200125_1_, IBlockReader p_200125_2_, BlockPos p_200125_3_) {
        return !p_200125_1_.isOpaqueCube(p_200125_2_, p_200125_3_) && p_200125_1_.getOpacity(p_200125_2_,
                p_200125_3_) == p_200125_2_.getMaxLightLevel();
    }

    public enum EnumOffsetType {
        NONE, XZ, XYZ
    }

    public static class Properties {
        private float blockHardness;
        private MaterialColor blockMapColor;
        private float blockResistance;
        private float isSlipperiness = 0.6F;
        private boolean isSolid = true;
        private int lightValue;
        private Material material;
        private boolean needsRandomTick;
        private SoundType soundType = SoundType.STONE;
        private boolean variableOpacity;

        private Properties(Material material, MaterialColor blockMapColor) {
            this.material = material;
            this.blockMapColor = blockMapColor;
        }

        static Block.Properties createBlockProperties(Material p_200949_0_, MaterialColor p_200949_1_) {
            return new Block.Properties(p_200949_0_, p_200949_1_);
        }

        static Block.Properties createBlockProperties(Material p_200945_0_) {
            return createBlockProperties(p_200945_0_, p_200945_0_.func_151565_r());
        }

        static Block.Properties createBlockProperties(Material p_200952_0_, EnumDyeColor p_200952_1_) {
            return createBlockProperties(p_200952_0_, p_200952_1_.getMapColor());
        }

        static Block.Properties createBlockPropertiesFromBlock(Block block) {
            Block.Properties block$properties = new Block.Properties(block.material, block.blockMapColor);
            block$properties.material = block.material;
            block$properties.blockHardness = block.blockHardness;
            block$properties.blockResistance = block.blockResistance;
            block$properties.isSolid = block.isSolid;
            block$properties.needsRandomTick = block.needsRandomTick;
            block$properties.lightValue = block.lightValue;
            block$properties.blockMapColor = block.blockMapColor;
            block$properties.soundType = block.soundType;
            block$properties.isSlipperiness = block.getSlipperiness();
            block$properties.variableOpacity = block.variableOpacity;
            return block$properties;
        }

        Block.Properties instantDestruction() {
            return this.setHardnessAndResistance(0.0F);
        }

        Block.Properties needsRandomTick() {
            this.needsRandomTick = true;
            return this;
        }

        Block.Properties setHardnessAndResistance(float hardnessAndResistance) {
            this.setHardnessAndResistance(hardnessAndResistance, hardnessAndResistance);
            return this;
        }

        Block.Properties setHardnessAndResistance(float blockHardness, float blockResistance) {
            this.blockHardness = blockHardness;
            this.blockResistance = Math.max(0.0F, blockResistance);
            return this;
        }

        Block.Properties setLightLevel(int value) {
            this.lightValue = value;
            return this;
        }

        Block.Properties setNonSolid() {
            this.isSolid = false;
            return this;
        }

        Block.Properties setSlipperiness(float value) {
            this.isSlipperiness = value;
            return this;
        }

        Block.Properties setSoundType(SoundType value) {
            this.soundType = value;
            return this;
        }

        public Block.Properties variableOpacity() {
            this.variableOpacity = true;
            return this;
        }
    }

    public static final class RenderSideCacheKey {
        private final IBlockState adjacentState;
        private final EnumFacing side;
        private final IBlockState state;

        public RenderSideCacheKey(IBlockState p_i49791_1_, IBlockState p_i49791_2_, EnumFacing p_i49791_3_) {
            this.state = p_i49791_1_;
            this.adjacentState = p_i49791_2_;
            this.side = p_i49791_3_;
        }

        public int hashCode() {
            return Objects.hash(this.state, this.adjacentState, this.side);
        }

        public boolean equals(Object p_equals_1_) {
            if (this == p_equals_1_) {
                return true;
            } else if (!(p_equals_1_ instanceof Block.RenderSideCacheKey)) {
                return false;
            } else {
                Block.RenderSideCacheKey block$rendersidecachekey = (Block.RenderSideCacheKey) p_equals_1_;
                return this.state == block$rendersidecachekey.state && this.adjacentState == block$rendersidecachekey.adjacentState && this.side == block$rendersidecachekey.side;
            }
        }
    }
}
