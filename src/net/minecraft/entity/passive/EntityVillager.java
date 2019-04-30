package net.minecraft.entity.passive;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.*;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Random;

public class EntityVillager extends EntityAgeable implements INpc, IMerchant {
    private static final EntityVillager.ITradeList[][][][] DEFAULT_TRADE_LIST_MAP = new EntityVillager.ITradeList[][][][]{
            {{{new EntityVillager.EmeraldForItems(Items.WHEAT, new EntityVillager.PriceInfo(18, 22)),
                    new EntityVillager.EmeraldForItems(Items.POTATO, new EntityVillager.PriceInfo(15, 19)),
                    new EntityVillager.EmeraldForItems(Items.CARROT, new EntityVillager.PriceInfo(15, 19)),
                    new EntityVillager.ListItemForEmeralds(Items.BREAD, new EntityVillager.PriceInfo(-4, -2))},
                    {new EntityVillager.EmeraldForItems(Blocks.PUMPKIN, new EntityVillager.PriceInfo(8, 13)),
                            new EntityVillager.ListItemForEmeralds(Items.PUMPKIN_PIE,
                                    new EntityVillager.PriceInfo(-3, -2))},
                    {new EntityVillager.EmeraldForItems(Blocks.MELON, new EntityVillager.PriceInfo(7, 12)),
                            new EntityVillager.ListItemForEmeralds(Items.APPLE, new EntityVillager.PriceInfo(-7, -5))},
                    {new EntityVillager.ListItemForEmeralds(Items.COOKIE, new EntityVillager.PriceInfo(-10, -6)),
                            new EntityVillager.ListItemForEmeralds(Blocks.CAKE, new EntityVillager.PriceInfo(1, 1))}},
                    {{new EntityVillager.EmeraldForItems(Items.STRING, new EntityVillager.PriceInfo(15, 20)),
                            new EntityVillager.EmeraldForItems(Items.COAL, new EntityVillager.PriceInfo(16, 24)),
                            new EntityVillager.ItemAndEmeraldToItem(Items.COD, new EntityVillager.PriceInfo(6, 6),
                                    Items.COOKED_COD, new EntityVillager.PriceInfo(6, 6)),
                            new EntityVillager.ItemAndEmeraldToItem(Items.SALMON, new EntityVillager.PriceInfo(6, 6),
                                    Items.COOKED_SALMON, new EntityVillager.PriceInfo(6, 6))},
                            {new EntityVillager.ListEnchantedItemForEmeralds(Items.FISHING_ROD,
                                    new EntityVillager.PriceInfo(7, 8))}},
                    {{new EntityVillager.EmeraldForItems(Blocks.WHITE_WOOL, new EntityVillager.PriceInfo(16, 22)),
                            new EntityVillager.ListItemForEmeralds(Items.SHEARS, new EntityVillager.PriceInfo(3, 4))},
                            {new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.WHITE_WOOL),
                                    new EntityVillager.PriceInfo(1, 2)),
                                    new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.ORANGE_WOOL),
                                            new EntityVillager.PriceInfo(1, 2)),
                                    new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.MAGENTA_WOOL),
                                            new EntityVillager.PriceInfo(1, 2)),
                                    new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.LIGHT_BLUE_WOOL),
                                            new EntityVillager.PriceInfo(1, 2)),
                                    new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.YELLOW_WOOL),
                                            new EntityVillager.PriceInfo(1, 2)),
                                    new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.LIME_WOOL),
                                            new EntityVillager.PriceInfo(1, 2)),
                                    new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.PINK_WOOL),
                                            new EntityVillager.PriceInfo(1, 2)),
                                    new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.GRAY_WOOL),
                                            new EntityVillager.PriceInfo(1, 2)),
                                    new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.LIGHT_GRAY_WOOL),
                                            new EntityVillager.PriceInfo(1, 2)),
                                    new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.CYAN_WOOL),
                                            new EntityVillager.PriceInfo(1, 2)),
                                    new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.PURPLE_WOOL),
                                            new EntityVillager.PriceInfo(1, 2)),
                                    new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.BLUE_WOOL),
                                            new EntityVillager.PriceInfo(1, 2)),
                                    new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.BROWN_WOOL),
                                            new EntityVillager.PriceInfo(1, 2)),
                                    new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.GREEN_WOOL),
                                            new EntityVillager.PriceInfo(1, 2)),
                                    new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.RED_WOOL),
                                            new EntityVillager.PriceInfo(1, 2)),
                                    new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.BLACK_WOOL),
                                            new EntityVillager.PriceInfo(1, 2))}},
                    {{new EntityVillager.EmeraldForItems(Items.STRING, new EntityVillager.PriceInfo(15, 20)),
                            new EntityVillager.ListItemForEmeralds(Items.ARROW, new EntityVillager.PriceInfo(-12, -8))},
                            {new EntityVillager.ListItemForEmeralds(Items.BOW, new EntityVillager.PriceInfo(2, 3)),
                                    new EntityVillager.ItemAndEmeraldToItem(Blocks.GRAVEL,
                                            new EntityVillager.PriceInfo(10, 10), Items.FLINT,
                                            new EntityVillager.PriceInfo(6, 10))}}},
            {{{new EntityVillager.EmeraldForItems(Items.PAPER, new EntityVillager.PriceInfo(24, 36)),
                    new EntityVillager.ListEnchantedBookForEmeralds()},
                    {new EntityVillager.EmeraldForItems(Items.BOOK, new EntityVillager.PriceInfo(8, 10)),
                            new EntityVillager.ListItemForEmeralds(Items.COMPASS, new EntityVillager.PriceInfo(10, 12)),
                            new EntityVillager.ListItemForEmeralds(Blocks.BOOKSHELF,
                                    new EntityVillager.PriceInfo(3, 4))},
                    {new EntityVillager.EmeraldForItems(Items.WRITTEN_BOOK, new EntityVillager.PriceInfo(2, 2)),
                            new EntityVillager.ListItemForEmeralds(Items.CLOCK, new EntityVillager.PriceInfo(10, 12)),
                            new EntityVillager.ListItemForEmeralds(Blocks.GLASS, new EntityVillager.PriceInfo(-5, -3))},
                    {new EntityVillager.ListEnchantedBookForEmeralds()},
                    {new EntityVillager.ListEnchantedBookForEmeralds()},
                    {new EntityVillager.ListItemForEmeralds(Items.NAME_TAG, new EntityVillager.PriceInfo(20, 22))}},
                    {{new EntityVillager.EmeraldForItems(Items.PAPER, new EntityVillager.PriceInfo(24, 36))},
                            {new EntityVillager.EmeraldForItems(Items.COMPASS, new EntityVillager.PriceInfo(1, 1))},
                            {new EntityVillager.ListItemForEmeralds(Items.MAP, new EntityVillager.PriceInfo(7, 11))},
                            {new EntityVillager.TreasureMapForEmeralds(new EntityVillager.PriceInfo(12, 20), "Monument",
                                    MapDecoration.Type.MONUMENT),
                                    new EntityVillager.TreasureMapForEmeralds(new EntityVillager.PriceInfo(16, 28),
                                            "Mansion", MapDecoration.Type.MANSION)}}},
            {{{new EntityVillager.EmeraldForItems(Items.ROTTEN_FLESH, new EntityVillager.PriceInfo(36, 40)),
                    new EntityVillager.EmeraldForItems(Items.GOLD_INGOT, new EntityVillager.PriceInfo(8, 10))},
                    {new EntityVillager.ListItemForEmeralds(Items.REDSTONE, new EntityVillager.PriceInfo(-4, -1)),
                            new EntityVillager.ListItemForEmeralds(new ItemStack(Items.LAPIS_LAZULI),
                                    new EntityVillager.PriceInfo(-2, -1))},
                    {new EntityVillager.ListItemForEmeralds(Items.ENDER_PEARL, new EntityVillager.PriceInfo(4, 7)),
                            new EntityVillager.ListItemForEmeralds(Blocks.GLOWSTONE,
                                    new EntityVillager.PriceInfo(-3, -1))},
                    {new EntityVillager.ListItemForEmeralds(Items.EXPERIENCE_BOTTLE,
                            new EntityVillager.PriceInfo(3, 11))}}},
            {{{new EntityVillager.EmeraldForItems(Items.COAL, new EntityVillager.PriceInfo(16, 24)),
                    new EntityVillager.ListItemForEmeralds(Items.IRON_HELMET, new EntityVillager.PriceInfo(4, 6))},
                    {new EntityVillager.EmeraldForItems(Items.IRON_INGOT, new EntityVillager.PriceInfo(7, 9)),
                            new EntityVillager.ListItemForEmeralds(Items.IRON_CHESTPLATE,
                                    new EntityVillager.PriceInfo(10, 14))},
                    {new EntityVillager.EmeraldForItems(Items.DIAMOND, new EntityVillager.PriceInfo(3, 4)),
                            new EntityVillager.ListEnchantedItemForEmeralds(Items.DIAMOND_CHESTPLATE,
                                    new EntityVillager.PriceInfo(16, 19))},
                    {new EntityVillager.ListItemForEmeralds(Items.CHAINMAIL_BOOTS, new EntityVillager.PriceInfo(5, 7)),
                            new EntityVillager.ListItemForEmeralds(Items.CHAINMAIL_LEGGINGS,
                                    new EntityVillager.PriceInfo(9, 11)),
                            new EntityVillager.ListItemForEmeralds(Items.CHAINMAIL_HELMET,
                                    new EntityVillager.PriceInfo(5, 7)),
                            new EntityVillager.ListItemForEmeralds(Items.CHAINMAIL_CHESTPLATE,
                                    new EntityVillager.PriceInfo(11, 15))}},
                    {{new EntityVillager.EmeraldForItems(Items.COAL, new EntityVillager.PriceInfo(16, 24)),
                            new EntityVillager.ListItemForEmeralds(Items.IRON_AXE, new EntityVillager.PriceInfo(6, 8))},
                            {new EntityVillager.EmeraldForItems(Items.IRON_INGOT, new EntityVillager.PriceInfo(7, 9)),
                                    new EntityVillager.ListEnchantedItemForEmeralds(Items.IRON_SWORD,
                                            new EntityVillager.PriceInfo(9, 10))},
                            {new EntityVillager.EmeraldForItems(Items.DIAMOND, new EntityVillager.PriceInfo(3, 4))}},
                    {{new EntityVillager.EmeraldForItems(Items.COAL, new EntityVillager.PriceInfo(16, 24)),
                            new EntityVillager.ListEnchantedItemForEmeralds(Items.IRON_SHOVEL,
                                    new EntityVillager.PriceInfo(5, 7))},
                            {new EntityVillager.EmeraldForItems(Items.IRON_INGOT, new EntityVillager.PriceInfo(7, 9)),
                                    new EntityVillager.ListEnchantedItemForEmeralds(Items.IRON_PICKAXE,
                                            new EntityVillager.PriceInfo(9, 11))},
                            {new EntityVillager.EmeraldForItems(Items.DIAMOND, new EntityVillager.PriceInfo(3, 4))}}},
            {{{new EntityVillager.EmeraldForItems(Items.PORKCHOP, new EntityVillager.PriceInfo(14, 18)),
                    new EntityVillager.EmeraldForItems(Items.CHICKEN, new EntityVillager.PriceInfo(14, 18))},
                    {new EntityVillager.EmeraldForItems(Items.COAL, new EntityVillager.PriceInfo(16, 24)),
                            new EntityVillager.ListItemForEmeralds(Items.COOKED_PORKCHOP,
                                    new EntityVillager.PriceInfo(-7, -5)),
                            new EntityVillager.ListItemForEmeralds(Items.COOKED_CHICKEN,
                                    new EntityVillager.PriceInfo(-8, -6))}},
                    {{new EntityVillager.EmeraldForItems(Items.LEATHER, new EntityVillager.PriceInfo(9, 12)),
                            new EntityVillager.ListItemForEmeralds(Items.LEATHER_LEGGINGS,
                                    new EntityVillager.PriceInfo(2, 4))},
                            {new EntityVillager.ListEnchantedItemForEmeralds(Items.LEATHER_CHESTPLATE,
                                    new EntityVillager.PriceInfo(7, 12))},
                            {new EntityVillager.ListItemForEmeralds(Items.SADDLE,
                                    new EntityVillager.PriceInfo(8, 10))}}}, {new EntityVillager.ITradeList[0][]}};
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DataParameter<Integer> PROFESSION = EntityDataManager.createKey(EntityVillager.class,
            DataSerializers.VARINT);
    private final InventoryBasic villagerInventory = new InventoryBasic(new TextComponentString("Items"), 8);
    private boolean areAdditionalTasksSet;
    @Nullable
    private MerchantRecipeList buyingList;
    @Nullable
    private EntityPlayer buyingPlayer;
    private int careerId;
    private int careerLevel;
    private boolean isLookingForHome;
    private boolean isMating;
    private boolean isPlaying;
    private boolean isWillingToMate;
    private String lastBuyingPlayer;
    private boolean needsInitilization;
    private int randomTickDivider;
    private int timeUntilReset;
    private Village village;
    private int wealth;

    public EntityVillager(World p_i1747_1_) {
        this(p_i1747_1_, 0);
    }

    public EntityVillager(World p_i1748_1_, int p_i1748_2_) {
        super(EntityType.VILLAGER, p_i1748_1_);
        this.setProfession(p_i1748_2_);
        this.setSize(0.6F, 1.95F);
        ((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
        this.setCanPickUpLoot(true);
    }

    public boolean canAbondonItems() {
        return this.hasEnoughItems(2);
    }

    private boolean canVillagerPickupItem(Item p_175558_1_) {
        return p_175558_1_ == Items.BREAD || p_175558_1_ == Items.POTATO || p_175558_1_ == Items.CARROT || p_175558_1_ == Items.WHEAT || p_175558_1_ == Items.WHEAT_SEEDS || p_175558_1_ == Items.BEETROOT || p_175558_1_ == Items.BEETROOT_SEEDS;
    }

    public EntityVillager createChild(EntityAgeable p_90011_1_) {
        EntityVillager entityvillager = new EntityVillager(this.world);
        entityvillager.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entityvillager)), null, null);
        return entityvillager;
    }

    public boolean processInteract(EntityPlayer p_184645_1_, EnumHand p_184645_2_) {
        ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
        boolean flag = itemstack.getItem() == Items.NAME_TAG;
        if (flag) {
            itemstack.interactWithEntity(p_184645_1_, this, p_184645_2_);
            return true;
        } else if (itemstack.getItem() != Items.VILLAGER_SPAWN_EGG && this.isEntityAlive() && !this.isTrading() && !this.isChild()) {
            if (this.buyingList == null) {
                this.populateBuyingList();
            }

            if (p_184645_2_ == EnumHand.MAIN_HAND) {
                p_184645_1_.addStat(StatList.TALKED_TO_VILLAGER);
            }

            if (!this.world.isRemote && !this.buyingList.isEmpty()) {
                this.setCustomer(p_184645_1_);
                p_184645_1_.displayVillagerTradeGui(this);
            } else if (this.buyingList.isEmpty()) {
                return super.processInteract(p_184645_1_, p_184645_2_);
            }

            return true;
        } else {
            return super.processInteract(p_184645_1_, p_184645_2_);
        }
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(PROFESSION, 0);
    }

    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        super.writeEntityToNBT(p_70014_1_);
        p_70014_1_.setInteger("Profession", this.getProfession());
        p_70014_1_.setInteger("Riches", this.wealth);
        p_70014_1_.setInteger("Career", this.careerId);
        p_70014_1_.setInteger("CareerLevel", this.careerLevel);
        p_70014_1_.setBoolean("Willing", this.isWillingToMate);
        if (this.buyingList != null) {
            p_70014_1_.setTag("Offers", this.buyingList.getRecipiesAsTags());
        }

        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.villagerInventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.villagerInventory.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                nbttaglist.add(itemstack.write(new NBTTagCompound()));
            }
        }

        p_70014_1_.setTag("Inventory", nbttaglist);
    }

    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        super.readEntityFromNBT(p_70037_1_);
        this.setProfession(p_70037_1_.getInteger("Profession"));
        this.wealth = p_70037_1_.getInteger("Riches");
        this.careerId = p_70037_1_.getInteger("Career");
        this.careerLevel = p_70037_1_.getInteger("CareerLevel");
        this.isWillingToMate = p_70037_1_.getBoolean("Willing");
        if (p_70037_1_.hasKey("Offers", 10)) {
            NBTTagCompound nbttagcompound = p_70037_1_.getCompoundTag("Offers");
            this.buyingList = new MerchantRecipeList(nbttagcompound);
        }

        NBTTagList nbttaglist = p_70037_1_.getTagList("Inventory", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            ItemStack itemstack = ItemStack.loadFromNBT(nbttaglist.getCompoundTagAt(i));
            if (!itemstack.isEmpty()) {
                this.villagerInventory.addItem(itemstack);
            }
        }

        this.setCanPickUpLoot(true);
        this.setAdditionalAItasks();
    }

    protected void onGrowingAdult() {
        if (this.getProfession() == 0) {
            this.tasks.addTask(8, new EntityAIHarvestFarmland(this, 0.6D));
        }

        super.onGrowingAdult();
    }

    public IEntityLivingData finalizeMobSpawn(DifficultyInstance p_190672_1_, @Nullable IEntityLivingData p_190672_2_, @Nullable NBTTagCompound p_190672_3_, boolean p_190672_4_) {
        p_190672_2_ = super.onInitialSpawn(p_190672_1_, p_190672_2_, p_190672_3_);
        if (p_190672_4_) {
            this.setProfession(this.world.rand.nextInt(6));
        }

        this.setAdditionalAItasks();
        this.populateBuyingList();
        return p_190672_2_;
    }

    @OnlyIn(Dist.CLIENT)
    private void func_195400_a(IParticleData p_195400_1_) {
        for (int i = 0; i < 5; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(p_195400_1_,
                    this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width,
                    this.posY + 1.0D + (double) (this.rand.nextFloat() * this.height),
                    this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1, d2);
        }

    }

    public boolean getIsWillingToMate(boolean p_175550_1_) {
        if (!this.isWillingToMate && p_175550_1_ && this.hasEnoughFoodToBreed()) {
            boolean flag = false;

            for (int i = 0; i < this.villagerInventory.getSizeInventory(); ++i) {
                ItemStack itemstack = this.villagerInventory.getStackInSlot(i);
                if (!itemstack.isEmpty()) {
                    if (itemstack.getItem() == Items.BREAD && itemstack.getCount() >= 3) {
                        flag = true;
                        this.villagerInventory.decrStackSize(i, 3);
                    } else if ((itemstack.getItem() == Items.POTATO || itemstack.getItem() == Items.CARROT) && itemstack.getCount() >= 12) {
                        flag = true;
                        this.villagerInventory.decrStackSize(i, 12);
                    }
                }

                if (flag) {
                    this.world.setEntityState(this, (byte) 18);
                    this.isWillingToMate = true;
                    break;
                }
            }
        }

        return this.isWillingToMate;
    }

    public int getProfession() {
        return Math.max(this.dataManager.get(PROFESSION) % 6, 0);
    }

    public void setProfession(int p_70938_1_) {
        this.dataManager.set(PROFESSION, p_70938_1_);
    }

    public InventoryBasic getVillagerInventory() {
        return this.villagerInventory;
    }

    public boolean hasEnoughFoodToBreed() {
        return this.hasEnoughItems(1);
    }

    private boolean hasEnoughItems(int p_175559_1_) {
        boolean flag = this.getProfession() == 0;

        for (int i = 0; i < this.villagerInventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.villagerInventory.getStackInSlot(i);
            Item item = itemstack.getItem();
            int j = itemstack.getCount();
            if (item == Items.BREAD && j >= 3 * p_175559_1_ || item == Items.POTATO && j >= 12 * p_175559_1_ || item == Items.CARROT && j >= 12 * p_175559_1_ || item == Items.BEETROOT && j >= 12 * p_175559_1_) {
                return true;
            }

            if (flag && item == Items.WHEAT && j >= 9 * p_175559_1_) {
                return true;
            }
        }

        return false;
    }

    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAvoidEntity<>(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));
        this.tasks.addTask(1, new EntityAIAvoidEntity<>(this, EntityEvoker.class, 12.0F, 0.8D, 0.8D));
        this.tasks.addTask(1, new EntityAIAvoidEntity<>(this, EntityVindicator.class, 8.0F, 0.8D, 0.8D));
        this.tasks.addTask(1, new EntityAIAvoidEntity<>(this, EntityVex.class, 8.0F, 0.6D, 0.6D));
        this.tasks.addTask(1, new EntityAITradePlayer(this));
        this.tasks.addTask(1, new EntityAILookAtTradePlayer(this));
        this.tasks.addTask(2, new EntityAIMoveIndoors(this));
        this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6D));
        this.tasks.addTask(6, new EntityAIVillagerMate(this));
        this.tasks.addTask(7, new EntityAIFollowGolem(this));
        this.tasks.addTask(9, new EntityAIWatchClosestWithoutMoving(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(9, new EntityAIVillagerInteract(this));
        this.tasks.addTask(9, new EntityAIWanderAvoidWater(this, 0.6D));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
    }

    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte p_70103_1_) {
        if (p_70103_1_ == 12) {
            this.func_195400_a(Particles.HEART);
        } else if (p_70103_1_ == 13) {
            this.func_195400_a(Particles.ANGRY_VILLAGER);
        } else if (p_70103_1_ == 14) {
            this.func_195400_a(Particles.HAPPY_VILLAGER);
        } else {
            super.handleStatusUpdate(p_70103_1_);
        }

    }

    protected SoundEvent getAmbientSound() {
        return this.isTrading() ? SoundEvents.ENTITY_VILLAGER_TRADE : SoundEvents.ENTITY_VILLAGER_AMBIENT;
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return LootTableList.ENTITIES_VILLAGER;
    }

    protected void updateEquipmentIfNeeded(EntityItem p_175445_1_) {
        ItemStack itemstack = p_175445_1_.getItem();
        Item item = itemstack.getItem();
        if (this.canVillagerPickupItem(item)) {
            ItemStack itemstack1 = this.villagerInventory.addItem(itemstack);
            if (itemstack1.isEmpty()) {
                p_175445_1_.setDead();
            } else {
                itemstack.setCount(itemstack1.getCount());
            }
        }

    }

    public boolean canDespawn() {
        return false;
    }    public void setCustomer(@Nullable EntityPlayer p_70932_1_) {
        this.buyingPlayer = p_70932_1_;
    }

    protected void updateAITasks() {
        if (--this.randomTickDivider <= 0) {
            BlockPos blockpos = new BlockPos(this);
            this.world.getVillageCollection().addToVillagerPositionList(blockpos);
            this.randomTickDivider = 70 + this.rand.nextInt(50);
            this.village = this.world.getVillageCollection().getNearestVillage(blockpos, 32);
            if (this.village == null) {
                this.detachHome();
            } else {
                BlockPos blockpos1 = this.village.getCenter();
                this.setHomePosAndDistance(blockpos1, this.village.getVillageRadius());
                if (this.isLookingForHome) {
                    this.isLookingForHome = false;
                    this.village.setDefaultPlayerReputation(5);
                }
            }
        }

        if (!this.isTrading() && this.timeUntilReset > 0) {
            --this.timeUntilReset;
            if (this.timeUntilReset <= 0) {
                if (this.needsInitilization) {
                    for (MerchantRecipe merchantrecipe : this.buyingList) {
                        if (merchantrecipe.isRecipeDisabled()) {
                            merchantrecipe.increaseMaxTradeUses(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
                        }
                    }

                    this.populateBuyingList();
                    this.needsInitilization = false;
                    if (this.village != null && this.lastBuyingPlayer != null) {
                        this.world.setEntityState(this, (byte) 14);
                        this.village.modifyPlayerReputation(this.lastBuyingPlayer, 1);
                    }
                }

                this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 0));
            }
        }

        super.updateAITasks();
    }    @Nullable
    public EntityPlayer getCustomer() {
        return this.buyingPlayer;
    }

    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
        return this.finalizeMobSpawn(p_204210_1_, p_204210_2_, p_204210_3_, true);
    }

    public boolean canBeLeashedTo(EntityPlayer p_184652_1_) {
        return false;
    }

    public boolean replaceItemInInventory(int p_174820_1_, ItemStack p_174820_2_) {
        if (super.replaceItemInInventory(p_174820_1_, p_174820_2_)) {
            return true;
        } else {
            int i = p_174820_1_ - 300;
            if (i >= 0 && i < this.villagerInventory.getSizeInventory()) {
                this.villagerInventory.setInventorySlotContents(i, p_174820_2_);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean isFarmItemInInventory() {
        for (int i = 0; i < this.villagerInventory.getSizeInventory(); ++i) {
            Item item = this.villagerInventory.getStackInSlot(i).getItem();
            if (item == Items.WHEAT_SEEDS || item == Items.POTATO || item == Items.CARROT || item == Items.BEETROOT_SEEDS) {
                return true;
            }
        }

        return false;
    }
    public void useRecipe(MerchantRecipe p_70933_1_) {
        p_70933_1_.incrementToolUses();
        this.livingSoundTime = -this.getTalkInterval();
        this.playSound(SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());
        int i = 3 + this.rand.nextInt(4);
        if (p_70933_1_.getToolUses() == 1 || this.rand.nextInt(5) == 0) {
            this.timeUntilReset = 40;
            this.needsInitilization = true;
            this.isWillingToMate = true;
            if (this.buyingPlayer != null) {
                this.lastBuyingPlayer = this.buyingPlayer.getGameProfile().getName();
            } else {
                this.lastBuyingPlayer = null;
            }

            i += 5;
        }

        if (p_70933_1_.getItemToBuy().getItem() == Items.EMERALD) {
            this.wealth += p_70933_1_.getItemToBuy().getCount();
        }

        if (p_70933_1_.getRewardsExp()) {
            this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY + 0.5D, this.posZ, i));
        }

        if (this.buyingPlayer instanceof EntityPlayerMP) {
            CriteriaTriggers.VILLAGER_TRADE.trigger((EntityPlayerMP) this.buyingPlayer, this,
                    p_70933_1_.getItemToSell());
        }

    }

    public boolean isMating() {
        return this.isMating;
    }    public void verifySellingItem(ItemStack p_110297_1_) {
        if (!this.world.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
            this.livingSoundTime = -this.getTalkInterval();
            this.playSound(p_110297_1_.isEmpty() ? SoundEvents.ENTITY_VILLAGER_NO : SoundEvents.ENTITY_VILLAGER_YES,
                    this.getSoundVolume(), this.getSoundPitch());
        }

    }

    public void setMating(boolean p_70947_1_) {
        this.isMating = p_70947_1_;
    }    @Nullable
    public MerchantRecipeList getRecipes(EntityPlayer p_70934_1_) {
        if (this.buyingList == null) {
            this.populateBuyingList();
        }

        return this.buyingList;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    public void setPlaying(boolean p_70939_1_) {
        this.isPlaying = p_70939_1_;
    }    @OnlyIn(Dist.CLIENT)
    public void setRecipes(@Nullable MerchantRecipeList p_70930_1_) {
    }

    public boolean isTrading() {
        return this.buyingPlayer != null;
    }    public World getWorld() {
        return this.world;
    }

    public void onStruckByLightning(EntityLightningBolt p_70077_1_) {
        if (!this.world.isRemote && !this.isDead) {
            EntityWitch entitywitch = new EntityWitch(this.world);
            entitywitch.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            entitywitch.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entitywitch)), null, null);
            entitywitch.setNoAI(this.isAIDisabled());
            if (this.hasCustomName()) {
                entitywitch.setCustomName(this.getCustomName());
                entitywitch.setCustomNameVisible(this.isCustomNameVisible());
            }

            this.world.spawnEntity(entitywitch);
            this.setDead();
        }
    }    public BlockPos getPos() {
        return new BlockPos(this);
    }

    public ITextComponent getDisplayName() {
        Team team = this.getTeam();
        ITextComponent itextcomponent = this.getCustomName();
        if (itextcomponent != null) {
            return ScorePlayerTeam.formatMemberName(team, itextcomponent).applyTextStyle((p_211519_1_) -> {
                p_211519_1_.setHoverEvent(this.getHoverEvent()).setInsertion(this.getCachedUniqueIdString());
            });
        } else {
            if (this.buyingList == null) {
                this.populateBuyingList();
            }

            String s = null;
            switch (this.getProfession()) {
                case 0:
                    if (this.careerId == 1) {
                        s = "farmer";
                    } else if (this.careerId == 2) {
                        s = "fisherman";
                    } else if (this.careerId == 3) {
                        s = "shepherd";
                    } else if (this.careerId == 4) {
                        s = "fletcher";
                    }
                    break;
                case 1:
                    if (this.careerId == 1) {
                        s = "librarian";
                    } else if (this.careerId == 2) {
                        s = "cartographer";
                    }
                    break;
                case 2:
                    s = "cleric";
                    break;
                case 3:
                    if (this.careerId == 1) {
                        s = "armorer";
                    } else if (this.careerId == 2) {
                        s = "weapon_smith";
                    } else if (this.careerId == 3) {
                        s = "tool_smith";
                    }
                    break;
                case 4:
                    if (this.careerId == 1) {
                        s = "butcher";
                    } else if (this.careerId == 2) {
                        s = "leatherworker";
                    }
                    break;
                case 5:
                    s = "nitwit";
            }

            if (s != null) {
                ITextComponent itextcomponent1 = (new TextComponentTranslation(
                        this.getType().getTranslationKey() + '.' + s)).applyTextStyle((p_211520_1_) -> {
                    p_211520_1_.setHoverEvent(this.getHoverEvent()).setInsertion(this.getCachedUniqueIdString());
                });
                if (team != null) {
                    itextcomponent1.applyTextStyle(team.getColor());
                }

                return itextcomponent1;
            } else {
                return super.getDisplayName();
            }
        }
    }

    public float getEyeHeight() {
        return this.isChild() ? 0.81F : 1.62F;
    }

    private void populateBuyingList() {
        EntityVillager.ITradeList[][][] aentityvillager$itradelist = DEFAULT_TRADE_LIST_MAP[this.getProfession()];
        if (this.careerId != 0 && this.careerLevel != 0) {
            ++this.careerLevel;
        } else {
            this.careerId = this.rand.nextInt(aentityvillager$itradelist.length) + 1;
            this.careerLevel = 1;
        }

        if (this.buyingList == null) {
            this.buyingList = new MerchantRecipeList();
        }

        int i = this.careerId - 1;
        int j = this.careerLevel - 1;
        if (i >= 0 && i < aentityvillager$itradelist.length) {
            EntityVillager.ITradeList[][] aentityvillager$itradelist1 = aentityvillager$itradelist[i];
            if (j >= 0 && j < aentityvillager$itradelist1.length) {
                EntityVillager.ITradeList[] aentityvillager$itradelist2 = aentityvillager$itradelist1[j];

                for (EntityVillager.ITradeList entityvillager$itradelist : aentityvillager$itradelist2) {
                    entityvillager$itradelist.addMerchantRecipe(this, this.buyingList, this.rand);
                }
            }

        }
    }

    private void setAdditionalAItasks() {
        if (!this.areAdditionalTasksSet) {
            this.areAdditionalTasksSet = true;
            if (this.isChild()) {
                this.tasks.addTask(8, new EntityAIPlay(this, 0.32D));
            } else if (this.getProfession() == 0) {
                this.tasks.addTask(6, new EntityAIHarvestFarmland(this, 0.6D));
            }

        }
    }

    public void setIsWillingToMate(boolean p_175549_1_) {
        this.isWillingToMate = p_175549_1_;
    }

    public void setLookingForHome() {
        this.isLookingForHome = true;
    }

    public void setRevengeTarget(@Nullable EntityLivingBase p_70604_1_) {
        super.setRevengeTarget(p_70604_1_);
        if (this.village != null && p_70604_1_ != null) {
            this.village.addOrRenewAgressor(p_70604_1_);
            if (p_70604_1_ instanceof EntityPlayer) {
                int i = -1;
                if (this.isChild()) {
                    i = -3;
                }

                this.village.modifyPlayerReputation(((EntityPlayer) p_70604_1_).getGameProfile().getName(), i);
                if (this.isEntityAlive()) {
                    this.world.setEntityState(this, (byte) 13);
                }
            }
        }

    }

    public void onDeath(DamageSource p_70645_1_) {
        if (this.village != null) {
            Entity entity = p_70645_1_.getTrueSource();
            if (entity != null) {
                if (entity instanceof EntityPlayer) {
                    this.village.modifyPlayerReputation(((EntityPlayer) entity).getGameProfile().getName(), -2);
                } else if (entity instanceof IMob) {
                    this.village.endMatingSeason();
                }
            } else {
                EntityPlayer entityplayer = this.world.getClosestPlayerToEntity(this, 16.0D);
                if (entityplayer != null) {
                    this.village.endMatingSeason();
                }
            }
        }

        super.onDeath(p_70645_1_);
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VILLAGER_DEATH;
    }

    public boolean wantsMoreFood() {
        boolean flag = this.getProfession() == 0;
        if (flag) {
            return !this.hasEnoughItems(5);
        } else {
            return !this.hasEnoughItems(1);
        }
    }

    public interface ITradeList {
        void addMerchantRecipe(IMerchant p_190888_1_, MerchantRecipeList p_190888_2_, Random p_190888_3_);
    }

    public static class EmeraldForItems implements EntityVillager.ITradeList {
        public Item buyingItem;
        public EntityVillager.PriceInfo price;

        public EmeraldForItems(IItemProvider p_i48216_1_, EntityVillager.PriceInfo p_i48216_2_) {
            this.buyingItem = p_i48216_1_.asItem();
            this.price = p_i48216_2_;
        }

        public void addMerchantRecipe(IMerchant p_190888_1_, MerchantRecipeList p_190888_2_, Random p_190888_3_) {
            ItemStack itemstack = new ItemStack(this.buyingItem,
                    this.price == null ? 1 : this.price.getPrice(p_190888_3_));
            p_190888_2_.add(new MerchantRecipe(itemstack, Items.EMERALD));
        }
    }

    public static class ItemAndEmeraldToItem implements EntityVillager.ITradeList {
        public EntityVillager.PriceInfo buyingPriceInfo;
        public ItemStack field_199763_a;
        public ItemStack field_199764_c;
        public EntityVillager.PriceInfo sellingPriceInfo;

        public ItemAndEmeraldToItem(IItemProvider p_i48215_1_, EntityVillager.PriceInfo p_i48215_2_, Item p_i48215_3_, EntityVillager.PriceInfo p_i48215_4_) {
            this.field_199763_a = new ItemStack(p_i48215_1_);
            this.buyingPriceInfo = p_i48215_2_;
            this.field_199764_c = new ItemStack(p_i48215_3_);
            this.sellingPriceInfo = p_i48215_4_;
        }

        public void addMerchantRecipe(IMerchant p_190888_1_, MerchantRecipeList p_190888_2_, Random p_190888_3_) {
            int i = this.buyingPriceInfo.getPrice(p_190888_3_);
            int j = this.sellingPriceInfo.getPrice(p_190888_3_);
            p_190888_2_.add(
                    new MerchantRecipe(new ItemStack(this.field_199763_a.getItem(), i), new ItemStack(Items.EMERALD),
                            new ItemStack(this.field_199764_c.getItem(), j)));
        }
    }

    public static class ListEnchantedBookForEmeralds implements EntityVillager.ITradeList {
        public void addMerchantRecipe(IMerchant p_190888_1_, MerchantRecipeList p_190888_2_, Random p_190888_3_) {
            Enchantment enchantment = IRegistry.field_212628_q.func_186801_a(p_190888_3_);
            int i = MathHelper.nextInt(p_190888_3_, enchantment.getMinLevel(), enchantment.getMaxLevel());
            ItemStack itemstack = ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(enchantment, i));
            int j = 2 + p_190888_3_.nextInt(5 + i * 10) + 3 * i;
            if (enchantment.isTreasureEnchantment()) {
                j *= 2;
            }

            if (j > 64) {
                j = 64;
            }

            p_190888_2_.add(new MerchantRecipe(new ItemStack(Items.BOOK), new ItemStack(Items.EMERALD, j), itemstack));
        }
    }

    public static class ListEnchantedItemForEmeralds implements EntityVillager.ITradeList {
        public ItemStack enchantedItemStack;
        public EntityVillager.PriceInfo priceInfo;

        public ListEnchantedItemForEmeralds(Item p_i45814_1_, EntityVillager.PriceInfo p_i45814_2_) {
            this.enchantedItemStack = new ItemStack(p_i45814_1_);
            this.priceInfo = p_i45814_2_;
        }

        public void addMerchantRecipe(IMerchant p_190888_1_, MerchantRecipeList p_190888_2_, Random p_190888_3_) {
            int i = 1;
            if (this.priceInfo != null) {
                i = this.priceInfo.getPrice(p_190888_3_);
            }

            ItemStack itemstack = new ItemStack(Items.EMERALD, i);
            ItemStack itemstack1 = EnchantmentHelper.addRandomEnchantment(p_190888_3_,
                    new ItemStack(this.enchantedItemStack.getItem()), 5 + p_190888_3_.nextInt(15), false);
            p_190888_2_.add(new MerchantRecipe(itemstack, itemstack1));
        }
    }

    public static class ListItemForEmeralds implements EntityVillager.ITradeList {
        public ItemStack itemToBuy;
        public EntityVillager.PriceInfo priceInfo;

        public ListItemForEmeralds(Block p_i48004_1_, EntityVillager.PriceInfo p_i48004_2_) {
            this(new ItemStack(p_i48004_1_), p_i48004_2_);
        }

        public ListItemForEmeralds(Item p_i45811_1_, EntityVillager.PriceInfo p_i45811_2_) {
            this(new ItemStack(p_i45811_1_), p_i45811_2_);
        }

        public ListItemForEmeralds(ItemStack p_i45812_1_, EntityVillager.PriceInfo p_i45812_2_) {
            this.itemToBuy = p_i45812_1_;
            this.priceInfo = p_i45812_2_;
        }

        public void addMerchantRecipe(IMerchant p_190888_1_, MerchantRecipeList p_190888_2_, Random p_190888_3_) {
            int i = 1;
            if (this.priceInfo != null) {
                i = this.priceInfo.getPrice(p_190888_3_);
            }

            ItemStack itemstack;
            ItemStack itemstack1;
            if (i < 0) {
                itemstack = new ItemStack(Items.EMERALD);
                itemstack1 = new ItemStack(this.itemToBuy.getItem(), -i);
            } else {
                itemstack = new ItemStack(Items.EMERALD, i);
                itemstack1 = new ItemStack(this.itemToBuy.getItem());
            }

            p_190888_2_.add(new MerchantRecipe(itemstack, itemstack1));
        }
    }

    public static class PriceInfo extends Tuple<Integer, Integer> {
        public PriceInfo(int p_i45810_1_, int p_i45810_2_) {
            super(p_i45810_1_, p_i45810_2_);
            if (p_i45810_2_ < p_i45810_1_) {
                EntityVillager.LOGGER.warn("PriceRange({}, {}) invalid, {} smaller than {}", p_i45810_1_, p_i45810_2_,
                        p_i45810_2_, p_i45810_1_);
            }

        }

        public int getPrice(Random p_179412_1_) {
            return this.getA() >= this.getB() ? this.getA() : this.getA() + p_179412_1_.nextInt(
                    this.getB() - this.getA() + 1);
        }
    }

    static class TreasureMapForEmeralds implements EntityVillager.ITradeList {
        public String destination;
        public MapDecoration.Type destinationType;
        public EntityVillager.PriceInfo value;

        public TreasureMapForEmeralds(EntityVillager.PriceInfo p_i47340_1_, String p_i47340_2_, MapDecoration.Type p_i47340_3_) {
            this.value = p_i47340_1_;
            this.destination = p_i47340_2_;
            this.destinationType = p_i47340_3_;
        }

        public void addMerchantRecipe(IMerchant p_190888_1_, MerchantRecipeList p_190888_2_, Random p_190888_3_) {
            int i = this.value.getPrice(p_190888_3_);
            World world = p_190888_1_.getWorld();
            BlockPos blockpos = world.func_211157_a(this.destination, p_190888_1_.getPos(), 100, true);
            if (blockpos != null) {
                ItemStack itemstack = ItemMap.setupNewMap(world, blockpos.getX(), blockpos.getZ(), (byte) 2, true,
                        true);
                ItemMap.renderBiomePreviewMap(world, itemstack);
                MapData.addTargetDecoration(itemstack, blockpos, "+", this.destinationType);
                itemstack.setDisplayName(
                        new TextComponentTranslation("filled_map." + this.destination.toLowerCase(Locale.ROOT)));
                p_190888_2_.add(
                        new MerchantRecipe(new ItemStack(Items.EMERALD, i), new ItemStack(Items.COMPASS), itemstack));
            }

        }
    }
















}
