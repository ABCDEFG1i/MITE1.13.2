package net.minecraft.block.material;

public final class Material {
    public static final Material AIR = (new Material.Builder(MaterialColor.AIR)).doesNotBlockMovement().notOpaque()
                                                                                .notSolid().replaceable().build();
    public static final Material ANVIL = (new Material.Builder(MaterialColor.IRON)).pushBlocks().build();
    public static final Material BARRIER = (new Material.Builder(MaterialColor.AIR)).requiresTool().pushBlocks()
                                                                                    .build();
    public static final Material BUBBLE_COLUMN = (new Material.Builder(MaterialColor.WATER)).doesNotBlockMovement()
                                                                                            .notOpaque().notSolid()
                                                                                            .pushDestroys()
                                                                                            .replaceable().liquid()
                                                                                            .build();
    public static final Material CACTUS = (new Material.Builder(MaterialColor.FOLIAGE)).notOpaque().pushDestroys()
                                                                                       .build();
    public static final Material CAKE = (new Material.Builder(MaterialColor.AIR)).pushDestroys().build();
    public static final Material CARPET = (new Material.Builder(MaterialColor.CLOTH)).doesNotBlockMovement().notOpaque()
                                                                                     .notSolid().flammable().build();
    public static final Material CIRCUITS = (new Material.Builder(MaterialColor.AIR)).doesNotBlockMovement().notOpaque()
                                                                                     .notSolid().pushDestroys().build();
    public static final Material CLAY = (new Material.Builder(MaterialColor.CLAY)).build();
    public static final Material CLOTH = (new Material.Builder(MaterialColor.CLOTH)).flammable().build();
    public static final Material CORAL = (new Material.Builder(MaterialColor.FOLIAGE)).pushDestroys().build();
    public static final Material CRAFTED_SNOW = (new Material.Builder(MaterialColor.SNOW)).requiresTool().build();
    public static final Material DRAGON_EGG = (new Material.Builder(MaterialColor.FOLIAGE)).pushDestroys().build();
    public static final Material FIRE = (new Material.Builder(MaterialColor.AIR)).doesNotBlockMovement().notOpaque()
                                                                                 .notSolid().pushDestroys()
                                                                                 .replaceable().build();
    public static final Material FURNACE = (new Material.Builder(MaterialColor.STONE)).flammable().build();
    public static final Material GLASS = (new Material.Builder(MaterialColor.AIR)).notOpaque().build();
    public static final Material GOURD = (new Material.Builder(MaterialColor.FOLIAGE)).pushDestroys().build();
    public static final Material GRASS = (new Material.Builder(MaterialColor.GRASS)).build();
    public static final Material GROUND = (new Material.Builder(MaterialColor.DIRT)).build();
    public static final Material ICE = (new Material.Builder(MaterialColor.ICE)).notOpaque().build();
    public static final Material IRON = (new Material.Builder(MaterialColor.IRON)).requiresTool().build();
    public static final Material LAVA = (new Material.Builder(MaterialColor.TNT)).doesNotBlockMovement().notOpaque()
                                                                                 .notSolid().pushDestroys()
                                                                                 .replaceable().liquid().build();
    public static final Material LEAVES = (new Material.Builder(MaterialColor.FOLIAGE)).flammable().notOpaque()
                                                                                       .pushDestroys().build();
    public static final Material OCEAN_PLANT = (new Material.Builder(MaterialColor.WATER)).doesNotBlockMovement()
                                                                                          .notOpaque().notSolid()
                                                                                          .pushDestroys().build();
    public static final Material PACKED_ICE = (new Material.Builder(MaterialColor.ICE)).build();
    public static final Material PISTON = (new Material.Builder(MaterialColor.STONE)).pushBlocks().build();
    public static final Material PLANTS = (new Material.Builder(MaterialColor.FOLIAGE)).doesNotBlockMovement()
                                                                                       .notOpaque().notSolid()
                                                                                       .pushDestroys().build();
    public static final Material PORTAL = (new Material.Builder(MaterialColor.AIR)).doesNotBlockMovement().notOpaque()
                                                                                   .notSolid().pushBlocks().build();
    public static final Material REDSTONE_LIGHT = (new Material.Builder(MaterialColor.AIR)).build();
    public static final Material ROCK = (new Material.Builder(MaterialColor.STONE)).requiresTool().build();
    public static final Material SAND = (new Material.Builder(MaterialColor.SAND)).build();
    public static final Material SEA_GRASS = (new Material.Builder(MaterialColor.WATER)).doesNotBlockMovement()
                                                                                        .notOpaque().notSolid()
                                                                                        .pushDestroys().replaceable()
                                                                                        .build();
    public static final Material SNOW = (new Material.Builder(MaterialColor.SNOW)).doesNotBlockMovement().notOpaque()
                                                                                  .notSolid().pushDestroys()
                                                                                  .replaceable().requiresTool().build();
    public static final Material SPONGE = (new Material.Builder(MaterialColor.YELLOW)).build();
    public static final Material STRUCTURE_VOID = (new Material.Builder(MaterialColor.AIR)).doesNotBlockMovement()
                                                                                           .notOpaque().notSolid()
                                                                                           .replaceable().build();
    public static final Material TNT = (new Material.Builder(MaterialColor.TNT)).flammable().notOpaque().build();
    public static final Material VINE = (new Material.Builder(MaterialColor.FOLIAGE)).doesNotBlockMovement().notOpaque()
                                                                                     .notSolid().pushDestroys()
                                                                                     .replaceable().flammable().build();
    public static final Material WATER = (new Material.Builder(MaterialColor.WATER)).doesNotBlockMovement().notOpaque()
                                                                                    .notSolid().pushDestroys()
                                                                                    .replaceable().liquid().build();
    public static final Material WEB = (new Material.Builder(MaterialColor.CLOTH)).doesNotBlockMovement().notOpaque()
                                                                                  .pushDestroys().requiresTool()
                                                                                  .build();
    public static final Material WOOD = (new Material.Builder(MaterialColor.WOOD)).requiresTool().flammable().build();
    public static final Material WORKBENCH = (new Material.Builder(MaterialColor.AIR)).build();
    private final boolean blocksMovement;
    private final MaterialColor color;
    private final boolean flammable;
    private final boolean isLiquid;
    private final boolean isOpaque;
    private final boolean isSolid;
    private final EnumPushReaction pushReaction;
    private final boolean replaceable;
    private final boolean requiresNoTool;

    public Material(MaterialColor p_i48243_1_, boolean p_i48243_2_, boolean p_i48243_3_, boolean p_i48243_4_, boolean p_i48243_5_, boolean p_i48243_6_, boolean p_i48243_7_, boolean p_i48243_8_, EnumPushReaction p_i48243_9_) {
        this.color = p_i48243_1_;
        this.isLiquid = p_i48243_2_;
        this.isSolid = p_i48243_3_;
        this.blocksMovement = p_i48243_4_;
        this.isOpaque = p_i48243_5_;
        this.requiresNoTool = p_i48243_6_;
        this.flammable = p_i48243_7_;
        this.replaceable = p_i48243_8_;
        this.pushReaction = p_i48243_9_;
    }

    public boolean blocksMovement() {
        return this.blocksMovement;
    }

    public MaterialColor func_151565_r() {
        return this.color;
    }

    public EnumPushReaction getPushReaction() {
        return this.pushReaction;
    }

    public boolean isFlammable() {
        return this.flammable;
    }

    public boolean isLiquid() {
        return this.isLiquid;
    }

    public boolean isOpaque() {
        return this.isOpaque;
    }

    public boolean isReplaceable() {
        return this.replaceable;
    }

    public boolean isSolid() {
        return this.isSolid;
    }

    public boolean isToolNotRequired() {
        return this.requiresNoTool;
    }

    public static class Builder {
        private final MaterialColor color;
        private boolean blocksMovement = true;
        private boolean canBurn;
        private boolean isLiquid;
        private boolean isOpaque = true;
        private boolean isReplaceable;
        private boolean isSolid = true;
        private EnumPushReaction pushReaction = EnumPushReaction.NORMAL;
        private boolean requiresNoTool = true;

        public Builder(MaterialColor p_i48270_1_) {
            this.color = p_i48270_1_;
        }

        public Material build() {
            return new Material(this.color, this.isLiquid, this.isSolid, this.blocksMovement, this.isOpaque,
                                this.requiresNoTool, this.canBurn, this.isReplaceable, this.pushReaction);
        }

        public Material.Builder doesNotBlockMovement() {
            this.blocksMovement = false;
            return this;
        }

        protected Material.Builder flammable() {
            this.canBurn = true;
            return this;
        }

        public Material.Builder liquid() {
            this.isLiquid = true;
            return this;
        }

        private Material.Builder notOpaque() {
            this.isOpaque = false;
            return this;
        }

        public Material.Builder notSolid() {
            this.isSolid = false;
            return this;
        }

        protected Material.Builder pushBlocks() {
            this.pushReaction = EnumPushReaction.BLOCK;
            return this;
        }

        protected Material.Builder pushDestroys() {
            this.pushReaction = EnumPushReaction.DESTROY;
            return this;
        }

        public Material.Builder replaceable() {
            this.isReplaceable = true;
            return this;
        }

        protected Material.Builder requiresTool() {
            this.requiresNoTool = false;
            return this;
        }
    }
}
