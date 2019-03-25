package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Item implements IItemProvider {
   public static final Map<Block, Item> BLOCK_TO_ITEM = Maps.newHashMap();
   private static final IItemPropertyGetter DAMAGED_GETTER = (p_210306_0_, p_210306_1_, p_210306_2_) -> {
      return p_210306_0_.isDamaged() ? 1.0F : 0.0F;
   };
   private static final IItemPropertyGetter DAMAGE_GETTER = (p_210307_0_, p_210307_1_, p_210307_2_) -> {
      return MathHelper.clamp((float)p_210307_0_.getDamage() / (float)p_210307_0_.getMaxDamage(), 0.0F, 1.0F);
   };
   private static final IItemPropertyGetter LEFTHANDED_GETTER = (p_210305_0_, p_210305_1_, p_210305_2_) -> {
      return p_210305_2_ != null && p_210305_2_.getPrimaryHand() != EnumHandSide.RIGHT ? 1.0F : 0.0F;
   };
   private static final IItemPropertyGetter COOLDOWN_GETTER = (p_210308_0_, p_210308_1_, p_210308_2_) -> {
      return p_210308_2_ instanceof EntityPlayer ? ((EntityPlayer)p_210308_2_).getCooldownTracker().getCooldown(p_210308_0_.getItem(), 0.0F) : 0.0F;
   };
   protected static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
   protected static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
   protected static Random random = new Random();
   public final Map<ResourceLocation, IItemPropertyGetter> properties = Maps.newHashMap();
   protected final ItemGroup group;
   private final EnumRarity rarity;
   private final int maxStackSize;
   private final int maxDamage;
   private final Item containerItem;
   @Nullable
   private String translationKey;

   public static int getIdFromItem(Item p_150891_0_) {
      return p_150891_0_ == null ? 0 : IRegistry.field_212630_s.func_148757_b(p_150891_0_);
   }

   public static Item getItemById(int p_150899_0_) {
      return IRegistry.field_212630_s.func_148754_a(p_150899_0_);
   }

   @Deprecated
   public static Item getItemFromBlock(Block p_150898_0_) {
      Item item = BLOCK_TO_ITEM.get(p_150898_0_);
      return item == null ? Items.AIR : item;
   }

   public Item(Item.Properties p_i48487_1_) {
      this.addPropertyOverride(new ResourceLocation("lefthanded"), LEFTHANDED_GETTER);
      this.addPropertyOverride(new ResourceLocation("cooldown"), COOLDOWN_GETTER);
      this.group = p_i48487_1_.field_200923_d;
      this.rarity = p_i48487_1_.field_208104_e;
      this.containerItem = p_i48487_1_.field_200922_c;
      this.maxDamage = p_i48487_1_.field_200921_b;
      this.maxStackSize = p_i48487_1_.field_200920_a;
      if (this.maxDamage > 0) {
         this.addPropertyOverride(new ResourceLocation("damaged"), DAMAGED_GETTER);
         this.addPropertyOverride(new ResourceLocation("damage"), DAMAGE_GETTER);
      }

   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public IItemPropertyGetter getPropertyGetter(ResourceLocation p_185045_1_) {
      return this.properties.get(p_185045_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasCustomProperties() {
      return !this.properties.isEmpty();
   }

   public boolean updateItemStackNBT(NBTTagCompound p_179215_1_) {
      return false;
   }

   public boolean canPlayerBreakBlockWhileHolding(IBlockState p_195938_1_, World p_195938_2_, BlockPos p_195938_3_, EntityPlayer p_195938_4_) {
      return true;
   }

   public Item asItem() {
      return this;
   }

   public final void addPropertyOverride(ResourceLocation p_185043_1_, IItemPropertyGetter p_185043_2_) {
      this.properties.put(p_185043_1_, p_185043_2_);
   }

   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      return EnumActionResult.PASS;
   }

   public float getDestroySpeed(ItemStack p_150893_1_, IBlockState p_150893_2_) {
      return 1.0F;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      return new ActionResult<>(EnumActionResult.PASS, p_77659_2_.getHeldItem(p_77659_3_));
   }

   public ItemStack onItemUseFinish(ItemStack p_77654_1_, World p_77654_2_, EntityLivingBase p_77654_3_) {
      return p_77654_1_;
   }

   public final int getMaxStackSize() {
      return this.maxStackSize;
   }

   public final int getMaxDamage() {
      return this.maxDamage;
   }

   public boolean isDamageable() {
      return this.maxDamage > 0;
   }

   public boolean hitEntity(ItemStack p_77644_1_, EntityLivingBase p_77644_2_, EntityLivingBase p_77644_3_) {
      return false;
   }

   public boolean onBlockDestroyed(ItemStack p_179218_1_, World p_179218_2_, IBlockState p_179218_3_, BlockPos p_179218_4_, EntityLivingBase p_179218_5_) {
      return false;
   }

   public boolean canHarvestBlock(IBlockState p_150897_1_) {
      return false;
   }

   public boolean itemInteractionForEntity(ItemStack p_111207_1_, EntityPlayer p_111207_2_, EntityLivingBase p_111207_3_, EnumHand p_111207_4_) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getName() {
      return new TextComponentTranslation(this.getTranslationKey());
   }

   protected String getDefaultTranslationKey() {
      if (this.translationKey == null) {
         this.translationKey = Util.makeTranslationKey("item", IRegistry.field_212630_s.func_177774_c(this));
      }

      return this.translationKey;
   }

   public String getTranslationKey() {
      return this.getDefaultTranslationKey();
   }

   public String getTranslationKey(ItemStack p_77667_1_) {
      return this.getTranslationKey();
   }

   public boolean getShareTag() {
      return true;
   }

   @Nullable
   public final Item getContainerItem() {
      return this.containerItem;
   }

   public boolean hasContainerItem() {
      return this.containerItem != null;
   }

   public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
   }

   public void onCreated(ItemStack p_77622_1_, World p_77622_2_, EntityPlayer p_77622_3_) {
   }

   public boolean isComplex() {
      return false;
   }

   public EnumAction getUseAction(ItemStack p_77661_1_) {
      return EnumAction.NONE;
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return 0;
   }

   public void onPlayerStoppedUsing(ItemStack p_77615_1_, World p_77615_2_, EntityLivingBase p_77615_3_, int p_77615_4_) {
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
   }

   public ITextComponent getDisplayName(ItemStack p_200295_1_) {
      return new TextComponentTranslation(this.getTranslationKey(p_200295_1_));
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect(ItemStack p_77636_1_) {
      return p_77636_1_.isEnchanted();
   }

   public EnumRarity getRarity(ItemStack p_77613_1_) {
      if (!p_77613_1_.isEnchanted()) {
         return this.rarity;
      } else {
         switch(this.rarity) {
         case COMMON:
         case UNCOMMON:
            return EnumRarity.RARE;
         case RARE:
            return EnumRarity.EPIC;
         case EPIC:
         default:
            return this.rarity;
         }
      }
   }

   public boolean isEnchantable(ItemStack p_77616_1_) {
      return this.getMaxStackSize() == 1 && this.isDamageable();
   }

   @Nullable
   protected RayTraceResult rayTrace(World p_77621_1_, EntityPlayer p_77621_2_, boolean p_77621_3_) {
      float f = p_77621_2_.rotationPitch;
      float f1 = p_77621_2_.rotationYaw;
      double d0 = p_77621_2_.posX;
      double d1 = p_77621_2_.posY + (double)p_77621_2_.getEyeHeight();
      double d2 = p_77621_2_.posZ;
      Vec3d vec3d = new Vec3d(d0, d1, d2);
      float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
      float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
      float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
      float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      double d3 = 5.0D;
      Vec3d vec3d1 = vec3d.add((double)f6 * 5.0D, (double)f5 * 5.0D, (double)f7 * 5.0D);
      return p_77621_1_.rayTraceBlocks(vec3d, vec3d1, p_77621_3_ ? RayTraceFluidMode.SOURCE_ONLY : RayTraceFluidMode.NEVER, false, false);
   }

   public int getItemEnchantability() {
      return 0;
   }

   public void fillItemGroup(ItemGroup p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
      if (this.isInGroup(p_150895_1_)) {
         p_150895_2_.add(new ItemStack(this));
      }

   }

   protected boolean isInGroup(ItemGroup p_194125_1_) {
      ItemGroup itemgroup = this.getGroup();
      return itemgroup != null && (p_194125_1_ == ItemGroup.SEARCH || p_194125_1_ == itemgroup);
   }

   @Nullable
   public final ItemGroup getGroup() {
      return this.group;
   }

   public boolean getIsRepairable(ItemStack p_82789_1_, ItemStack p_82789_2_) {
      return false;
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot p_111205_1_) {
      return HashMultimap.create();
   }

   public static void registerItems() {
      register(Blocks.AIR, new ItemAir(Blocks.AIR, new Item.Properties()));
      register(Blocks.STONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GRANITE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.POLISHED_GRANITE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DIORITE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.POLISHED_DIORITE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ANDESITE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.POLISHED_ANDESITE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GRASS_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DIRT, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.COARSE_DIRT, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PODZOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.COBBLESTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.OAK_PLANKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SPRUCE_PLANKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BIRCH_PLANKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.JUNGLE_PLANKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ACACIA_PLANKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DARK_OAK_PLANKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.OAK_SAPLING, ItemGroup.DECORATIONS);
      register(Blocks.SPRUCE_SAPLING, ItemGroup.DECORATIONS);
      register(Blocks.BIRCH_SAPLING, ItemGroup.DECORATIONS);
      register(Blocks.JUNGLE_SAPLING, ItemGroup.DECORATIONS);
      register(Blocks.ACACIA_SAPLING, ItemGroup.DECORATIONS);
      register(Blocks.DARK_OAK_SAPLING, ItemGroup.DECORATIONS);
      register(Blocks.BEDROCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SAND, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_SAND, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GRAVEL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GOLD_ORE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.IRON_ORE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.COAL_ORE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.OAK_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SPRUCE_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BIRCH_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.JUNGLE_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ACACIA_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DARK_OAK_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_OAK_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_SPRUCE_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_BIRCH_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_JUNGLE_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_ACACIA_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_DARK_OAK_LOG, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_OAK_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_SPRUCE_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_BIRCH_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_JUNGLE_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_ACACIA_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRIPPED_DARK_OAK_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.OAK_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SPRUCE_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BIRCH_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.JUNGLE_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ACACIA_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DARK_OAK_WOOD, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.OAK_LEAVES, ItemGroup.DECORATIONS);
      register(Blocks.SPRUCE_LEAVES, ItemGroup.DECORATIONS);
      register(Blocks.BIRCH_LEAVES, ItemGroup.DECORATIONS);
      register(Blocks.JUNGLE_LEAVES, ItemGroup.DECORATIONS);
      register(Blocks.ACACIA_LEAVES, ItemGroup.DECORATIONS);
      register(Blocks.DARK_OAK_LEAVES, ItemGroup.DECORATIONS);
      register(Blocks.SPONGE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.WET_SPONGE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LAPIS_ORE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LAPIS_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DISPENSER, ItemGroup.REDSTONE);
      register(Blocks.SANDSTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CHISELED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CUT_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.NOTE_BLOCK, ItemGroup.REDSTONE);
      register(Blocks.POWERED_RAIL, ItemGroup.TRANSPORTATION);
      register(Blocks.DETECTOR_RAIL, ItemGroup.TRANSPORTATION);
      register(Blocks.STICKY_PISTON, ItemGroup.REDSTONE);
      register(Blocks.COBWEB, ItemGroup.DECORATIONS);
      register(Blocks.GRASS, ItemGroup.DECORATIONS);
      register(Blocks.FERN, ItemGroup.DECORATIONS);
      register(Blocks.DEAD_BUSH, ItemGroup.DECORATIONS);
      register(Blocks.SEAGRASS, ItemGroup.DECORATIONS);
      register(Blocks.SEA_PICKLE, ItemGroup.DECORATIONS);
      register(Blocks.PISTON, ItemGroup.REDSTONE);
      register(Blocks.WHITE_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ORANGE_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.MAGENTA_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_BLUE_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.YELLOW_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIME_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PINK_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GRAY_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_GRAY_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CYAN_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PURPLE_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLUE_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BROWN_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GREEN_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLACK_WOOL, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DANDELION, ItemGroup.DECORATIONS);
      register(Blocks.POPPY, ItemGroup.DECORATIONS);
      register(Blocks.BLUE_ORCHID, ItemGroup.DECORATIONS);
      register(Blocks.ALLIUM, ItemGroup.DECORATIONS);
      register(Blocks.AZURE_BLUET, ItemGroup.DECORATIONS);
      register(Blocks.RED_TULIP, ItemGroup.DECORATIONS);
      register(Blocks.ORANGE_TULIP, ItemGroup.DECORATIONS);
      register(Blocks.WHITE_TULIP, ItemGroup.DECORATIONS);
      register(Blocks.PINK_TULIP, ItemGroup.DECORATIONS);
      register(Blocks.OXEYE_DAISY, ItemGroup.DECORATIONS);
      register(Blocks.BROWN_MUSHROOM, ItemGroup.DECORATIONS);
      register(Blocks.RED_MUSHROOM, ItemGroup.DECORATIONS);
      register(Blocks.GOLD_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.IRON_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.OAK_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SPRUCE_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BIRCH_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.JUNGLE_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ACACIA_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DARK_OAK_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STONE_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SANDSTONE_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PETRIFIED_OAK_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.COBBLESTONE_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STONE_BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.NETHER_BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.QUARTZ_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_SANDSTONE_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PURPUR_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PRISMARINE_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PRISMARINE_BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DARK_PRISMARINE_SLAB, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SMOOTH_QUARTZ, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SMOOTH_RED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SMOOTH_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SMOOTH_STONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BRICKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.TNT, ItemGroup.REDSTONE);
      register(Blocks.BOOKSHELF, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.MOSSY_COBBLESTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.OBSIDIAN, ItemGroup.BUILDING_BLOCKS);
      register(new ItemWallOrFloor(Blocks.TORCH, Blocks.WALL_TORCH, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(Blocks.END_ROD, ItemGroup.DECORATIONS);
      register(Blocks.CHORUS_PLANT, ItemGroup.DECORATIONS);
      register(Blocks.CHORUS_FLOWER, ItemGroup.DECORATIONS);
      register(Blocks.PURPUR_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PURPUR_PILLAR, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PURPUR_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SPAWNER);
      register(Blocks.OAK_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CHEST, ItemGroup.DECORATIONS);
      register(Blocks.DIAMOND_ORE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DIAMOND_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CRAFTING_TABLE, ItemGroup.DECORATIONS);
      register(Blocks.FARMLAND, ItemGroup.DECORATIONS);
      register(Blocks.FURNACE, ItemGroup.DECORATIONS);
      register(Blocks.LADDER, ItemGroup.DECORATIONS);
      register(Blocks.RAIL, ItemGroup.TRANSPORTATION);
      register(Blocks.COBBLESTONE_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LEVER, ItemGroup.REDSTONE);
      register(Blocks.STONE_PRESSURE_PLATE, ItemGroup.REDSTONE);
      register(Blocks.OAK_PRESSURE_PLATE, ItemGroup.REDSTONE);
      register(Blocks.SPRUCE_PRESSURE_PLATE, ItemGroup.REDSTONE);
      register(Blocks.BIRCH_PRESSURE_PLATE, ItemGroup.REDSTONE);
      register(Blocks.JUNGLE_PRESSURE_PLATE, ItemGroup.REDSTONE);
      register(Blocks.ACACIA_PRESSURE_PLATE, ItemGroup.REDSTONE);
      register(Blocks.DARK_OAK_PRESSURE_PLATE, ItemGroup.REDSTONE);
      register(Blocks.REDSTONE_ORE, ItemGroup.BUILDING_BLOCKS);
      register(new ItemWallOrFloor(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, (new Item.Properties()).func_200916_a(ItemGroup.REDSTONE)));
      register(Blocks.STONE_BUTTON, ItemGroup.REDSTONE);
      register(Blocks.SNOW, ItemGroup.DECORATIONS);
      register(Blocks.ICE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SNOW_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CACTUS, ItemGroup.DECORATIONS);
      register(Blocks.CLAY, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.JUKEBOX, ItemGroup.DECORATIONS);
      register(Blocks.OAK_FENCE, ItemGroup.DECORATIONS);
      register(Blocks.SPRUCE_FENCE, ItemGroup.DECORATIONS);
      register(Blocks.BIRCH_FENCE, ItemGroup.DECORATIONS);
      register(Blocks.JUNGLE_FENCE, ItemGroup.DECORATIONS);
      register(Blocks.ACACIA_FENCE, ItemGroup.DECORATIONS);
      register(Blocks.DARK_OAK_FENCE, ItemGroup.DECORATIONS);
      register(Blocks.PUMPKIN, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CARVED_PUMPKIN, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.NETHERRACK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SOUL_SAND, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GLOWSTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.JACK_O_LANTERN, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.OAK_TRAPDOOR, ItemGroup.REDSTONE);
      register(Blocks.SPRUCE_TRAPDOOR, ItemGroup.REDSTONE);
      register(Blocks.BIRCH_TRAPDOOR, ItemGroup.REDSTONE);
      register(Blocks.JUNGLE_TRAPDOOR, ItemGroup.REDSTONE);
      register(Blocks.ACACIA_TRAPDOOR, ItemGroup.REDSTONE);
      register(Blocks.DARK_OAK_TRAPDOOR, ItemGroup.REDSTONE);
      register(Blocks.INFESTED_STONE, ItemGroup.DECORATIONS);
      register(Blocks.INFESTED_COBBLESTONE, ItemGroup.DECORATIONS);
      register(Blocks.INFESTED_STONE_BRICKS, ItemGroup.DECORATIONS);
      register(Blocks.INFESTED_MOSSY_STONE_BRICKS, ItemGroup.DECORATIONS);
      register(Blocks.INFESTED_CRACKED_STONE_BRICKS, ItemGroup.DECORATIONS);
      register(Blocks.INFESTED_CHISELED_STONE_BRICKS, ItemGroup.DECORATIONS);
      register(Blocks.STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.MOSSY_STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CRACKED_STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CHISELED_STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BROWN_MUSHROOM_BLOCK, ItemGroup.DECORATIONS);
      register(Blocks.RED_MUSHROOM_BLOCK, ItemGroup.DECORATIONS);
      register(Blocks.MUSHROOM_STEM, ItemGroup.DECORATIONS);
      register(Blocks.IRON_BARS, ItemGroup.DECORATIONS);
      register(Blocks.GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.MELON, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.VINE, ItemGroup.DECORATIONS);
      register(Blocks.OAK_FENCE_GATE, ItemGroup.REDSTONE);
      register(Blocks.SPRUCE_FENCE_GATE, ItemGroup.REDSTONE);
      register(Blocks.BIRCH_FENCE_GATE, ItemGroup.REDSTONE);
      register(Blocks.JUNGLE_FENCE_GATE, ItemGroup.REDSTONE);
      register(Blocks.ACACIA_FENCE_GATE, ItemGroup.REDSTONE);
      register(Blocks.DARK_OAK_FENCE_GATE, ItemGroup.REDSTONE);
      register(Blocks.BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STONE_BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.MYCELIUM, ItemGroup.BUILDING_BLOCKS);
      register(new ItemLilyPad(Blocks.LILY_PAD, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(Blocks.NETHER_BRICKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.NETHER_BRICK_FENCE, ItemGroup.DECORATIONS);
      register(Blocks.NETHER_BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ENCHANTING_TABLE, ItemGroup.DECORATIONS);
      register(Blocks.END_PORTAL_FRAME, ItemGroup.DECORATIONS);
      register(Blocks.END_STONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.END_STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
      register(new ItemBlock(Blocks.DRAGON_EGG, (new Item.Properties()).func_208103_a(EnumRarity.EPIC)));
      register(Blocks.REDSTONE_LAMP, ItemGroup.REDSTONE);
      register(Blocks.SANDSTONE_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.EMERALD_ORE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ENDER_CHEST, ItemGroup.DECORATIONS);
      register(Blocks.TRIPWIRE_HOOK, ItemGroup.REDSTONE);
      register(Blocks.EMERALD_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SPRUCE_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BIRCH_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.JUNGLE_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(new ItemGMOnly(Blocks.COMMAND_BLOCK, (new Item.Properties()).func_208103_a(EnumRarity.EPIC)));
      register(new ItemBlock(Blocks.BEACON, (new Item.Properties()).func_200916_a(ItemGroup.MISC).func_208103_a(EnumRarity.RARE)));
      register(Blocks.COBBLESTONE_WALL, ItemGroup.DECORATIONS);
      register(Blocks.MOSSY_COBBLESTONE_WALL, ItemGroup.DECORATIONS);
      register(Blocks.OAK_BUTTON, ItemGroup.REDSTONE);
      register(Blocks.SPRUCE_BUTTON, ItemGroup.REDSTONE);
      register(Blocks.BIRCH_BUTTON, ItemGroup.REDSTONE);
      register(Blocks.JUNGLE_BUTTON, ItemGroup.REDSTONE);
      register(Blocks.ACACIA_BUTTON, ItemGroup.REDSTONE);
      register(Blocks.DARK_OAK_BUTTON, ItemGroup.REDSTONE);
      register(Blocks.ANVIL, ItemGroup.DECORATIONS);
      register(Blocks.CHIPPED_ANVIL, ItemGroup.DECORATIONS);
      register(Blocks.DAMAGED_ANVIL, ItemGroup.DECORATIONS);
      register(Blocks.TRAPPED_CHEST, ItemGroup.REDSTONE);
      register(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, ItemGroup.REDSTONE);
      register(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, ItemGroup.REDSTONE);
      register(Blocks.DAYLIGHT_DETECTOR, ItemGroup.REDSTONE);
      register(Blocks.REDSTONE_BLOCK, ItemGroup.REDSTONE);
      register(Blocks.NETHER_QUARTZ_ORE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.HOPPER, ItemGroup.REDSTONE);
      register(Blocks.CHISELED_QUARTZ_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.QUARTZ_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.QUARTZ_PILLAR, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.QUARTZ_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ACTIVATOR_RAIL, ItemGroup.TRANSPORTATION);
      register(Blocks.DROPPER, ItemGroup.REDSTONE);
      register(Blocks.WHITE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ORANGE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.MAGENTA_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_BLUE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.YELLOW_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIME_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PINK_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GRAY_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_GRAY_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CYAN_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PURPLE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLUE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BROWN_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GREEN_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLACK_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BARRIER);
      register(Blocks.IRON_TRAPDOOR, ItemGroup.REDSTONE);
      register(Blocks.HAY_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.WHITE_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.ORANGE_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.MAGENTA_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.LIGHT_BLUE_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.YELLOW_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.LIME_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.PINK_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.GRAY_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.LIGHT_GRAY_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.CYAN_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.PURPLE_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.BLUE_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.BROWN_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.GREEN_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.RED_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.BLACK_CARPET, ItemGroup.DECORATIONS);
      register(Blocks.TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.COAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PACKED_ICE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ACACIA_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DARK_OAK_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SLIME_BLOCK, ItemGroup.DECORATIONS);
      register(Blocks.GRASS_PATH, ItemGroup.DECORATIONS);
      register(new ItemBlockTall(Blocks.SUNFLOWER, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlockTall(Blocks.LILAC, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlockTall(Blocks.ROSE_BUSH, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlockTall(Blocks.PEONY, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlockTall(Blocks.TALL_GRASS, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlockTall(Blocks.LARGE_FERN, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(Blocks.WHITE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ORANGE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.MAGENTA_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_BLUE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.YELLOW_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIME_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PINK_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GRAY_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_GRAY_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CYAN_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PURPLE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLUE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BROWN_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GREEN_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLACK_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.WHITE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.ORANGE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.MAGENTA_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.YELLOW_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.LIME_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.PINK_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.GRAY_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.CYAN_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.PURPLE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.BLUE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.BROWN_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.GREEN_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.RED_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.BLACK_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
      register(Blocks.PRISMARINE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PRISMARINE_BRICKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DARK_PRISMARINE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PRISMARINE_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PRISMARINE_BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DARK_PRISMARINE_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.SEA_LANTERN, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CHISELED_RED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CUT_RED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_SANDSTONE_STAIRS, ItemGroup.BUILDING_BLOCKS);
      register(new ItemGMOnly(Blocks.REPEATING_COMMAND_BLOCK, (new Item.Properties()).func_208103_a(EnumRarity.EPIC)));
      register(new ItemGMOnly(Blocks.CHAIN_COMMAND_BLOCK, (new Item.Properties()).func_208103_a(EnumRarity.EPIC)));
      register(Blocks.MAGMA_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.NETHER_WART_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_NETHER_BRICKS, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BONE_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.STRUCTURE_VOID);
      register(Blocks.OBSERVER, ItemGroup.REDSTONE);
      register(new ItemBlock(Blocks.SHULKER_BOX, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.WHITE_SHULKER_BOX, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.ORANGE_SHULKER_BOX, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.MAGENTA_SHULKER_BOX, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.LIGHT_BLUE_SHULKER_BOX, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.YELLOW_SHULKER_BOX, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.LIME_SHULKER_BOX, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.PINK_SHULKER_BOX, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.GRAY_SHULKER_BOX, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.LIGHT_GRAY_SHULKER_BOX, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.CYAN_SHULKER_BOX, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.PURPLE_SHULKER_BOX, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.BLUE_SHULKER_BOX, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.BROWN_SHULKER_BOX, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.GREEN_SHULKER_BOX, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.RED_SHULKER_BOX, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBlock(Blocks.BLACK_SHULKER_BOX, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(Blocks.WHITE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.ORANGE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.MAGENTA_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.YELLOW_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.LIME_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.PINK_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.GRAY_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.CYAN_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.PURPLE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.BLUE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.BROWN_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.GREEN_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.RED_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.BLACK_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
      register(Blocks.WHITE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ORANGE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.MAGENTA_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_BLUE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.YELLOW_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIME_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PINK_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GRAY_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_GRAY_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CYAN_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PURPLE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLUE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BROWN_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GREEN_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLACK_CONCRETE, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.WHITE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.ORANGE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.MAGENTA_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_BLUE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.YELLOW_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIME_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PINK_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GRAY_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.LIGHT_GRAY_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.CYAN_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.PURPLE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLUE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BROWN_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.GREEN_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.RED_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BLACK_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.TURTLE_EGG, ItemGroup.MISC);
      register(Blocks.DEAD_TUBE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DEAD_BRAIN_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DEAD_BUBBLE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DEAD_FIRE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.DEAD_HORN_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.TUBE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BRAIN_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.BUBBLE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.FIRE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.HORN_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
      register(Blocks.TUBE_CORAL, ItemGroup.DECORATIONS);
      register(Blocks.BRAIN_CORAL, ItemGroup.DECORATIONS);
      register(Blocks.BUBBLE_CORAL, ItemGroup.DECORATIONS);
      register(Blocks.FIRE_CORAL, ItemGroup.DECORATIONS);
      register(Blocks.HORN_CORAL, ItemGroup.DECORATIONS);
      register(Blocks.field_212586_jZ, ItemGroup.DECORATIONS);
      register(Blocks.field_212587_ka, ItemGroup.DECORATIONS);
      register(Blocks.field_212588_kb, ItemGroup.DECORATIONS);
      register(Blocks.field_212589_kc, ItemGroup.DECORATIONS);
      register(Blocks.field_212585_jY, ItemGroup.DECORATIONS);
      register(new ItemWallOrFloor(Blocks.TUBE_CORAL_FAN, Blocks.TUBE_CORAL_WALL_FAN, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemWallOrFloor(Blocks.BRAIN_CORAL_FAN, Blocks.BRAIN_CORAL_WALL_FAN, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemWallOrFloor(Blocks.BUBBLE_CORAL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemWallOrFloor(Blocks.FIRE_CORAL_FAN, Blocks.FIRE_CORAL_WALL_FAN, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemWallOrFloor(Blocks.HORN_CORAL_FAN, Blocks.HORN_CORAL_WALL_FAN, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemWallOrFloor(Blocks.DEAD_TUBE_CORAL_FAN, Blocks.DEAD_TUBE_CORAL_WALL_FAN, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemWallOrFloor(Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_WALL_FAN, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemWallOrFloor(Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemWallOrFloor(Blocks.DEAD_FIRE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_WALL_FAN, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemWallOrFloor(Blocks.DEAD_HORN_CORAL_FAN, Blocks.DEAD_HORN_CORAL_WALL_FAN, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(Blocks.BLUE_ICE, ItemGroup.BUILDING_BLOCKS);
      register(new ItemBlock(Blocks.CONDUIT, (new Item.Properties()).func_200916_a(ItemGroup.MISC).func_208103_a(EnumRarity.RARE)));
      register(new ItemBlockTall(Blocks.IRON_DOOR, (new Item.Properties()).func_200916_a(ItemGroup.REDSTONE)));
      register(new ItemBlockTall(Blocks.OAK_DOOR, (new Item.Properties()).func_200916_a(ItemGroup.REDSTONE)));
      register(new ItemBlockTall(Blocks.SPRUCE_DOOR, (new Item.Properties()).func_200916_a(ItemGroup.REDSTONE)));
      register(new ItemBlockTall(Blocks.BIRCH_DOOR, (new Item.Properties()).func_200916_a(ItemGroup.REDSTONE)));
      register(new ItemBlockTall(Blocks.JUNGLE_DOOR, (new Item.Properties()).func_200916_a(ItemGroup.REDSTONE)));
      register(new ItemBlockTall(Blocks.ACACIA_DOOR, (new Item.Properties()).func_200916_a(ItemGroup.REDSTONE)));
      register(new ItemBlockTall(Blocks.DARK_OAK_DOOR, (new Item.Properties()).func_200916_a(ItemGroup.REDSTONE)));
      register(Blocks.REPEATER, ItemGroup.REDSTONE);
      register(Blocks.COMPARATOR, ItemGroup.REDSTONE);
      register(new ItemGMOnly(Blocks.STRUCTURE_BLOCK, (new Item.Properties()).func_208103_a(EnumRarity.EPIC)));
      registerItem("turtle_helmet", new ItemArmor(ArmorMaterial.TURTLE, EntityEquipmentSlot.HEAD, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("scute", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("iron_shovel", new ItemSpade(ItemTier.IRON, 1.5F, -3.0F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("iron_pickaxe", new ItemPickaxe(ItemTier.IRON, 1, -2.8F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("iron_axe", new ItemAxe(ItemTier.IRON, 6.0F, -3.1F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("flint_and_steel", new ItemFlintAndSteel((new Item.Properties()).func_200918_c(64).func_200916_a(ItemGroup.TOOLS)));
      registerItem("apple", new ItemFood(4, 0.3F, false, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("bow", new ItemBow((new Item.Properties()).func_200918_c(384).func_200916_a(ItemGroup.COMBAT)));
      registerItem("arrow", new ItemArrow((new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("coal", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("charcoal", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("diamond", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("iron_ingot", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("gold_ingot", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("iron_sword", new ItemSword(ItemTier.IRON, 3, -2.4F, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("wooden_sword", new ItemSword(ItemTier.WOOD, 3, -2.4F, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("wooden_shovel", new ItemSpade(ItemTier.WOOD, 1.5F, -3.0F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("wooden_pickaxe", new ItemPickaxe(ItemTier.WOOD, 1, -2.8F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("wooden_axe", new ItemAxe(ItemTier.WOOD, 6.0F, -3.2F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("flint_shovel", new ItemSpade(ItemTier.FLINT, 1.5F, -3.0F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("flint_pickaxe", new ItemPickaxe(ItemTier.FLINT, 1, -2.8F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("flint_axe", new ItemAxe(ItemTier.FLINT, 7.0F, -3.2F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("diamond_sword", new ItemSword(ItemTier.DIAMOND, 3, -2.4F, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("diamond_shovel", new ItemSpade(ItemTier.DIAMOND, 1.5F, -3.0F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("diamond_pickaxe", new ItemPickaxe(ItemTier.DIAMOND, 1, -2.8F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("diamond_axe", new ItemAxe(ItemTier.DIAMOND, 5.0F, -3.0F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("stick", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("bowl", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("mushroom_stew", new ItemSoup(6, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.FOOD)));
      registerItem("golden_sword", new ItemSword(ItemTier.GOLD, 3, -2.4F, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("golden_shovel", new ItemSpade(ItemTier.GOLD, 1.5F, -3.0F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("golden_pickaxe", new ItemPickaxe(ItemTier.GOLD, 1, -2.8F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("golden_axe", new ItemAxe(ItemTier.GOLD, 6.0F, -3.0F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("string", new ItemString((new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("feather", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("gunpowder", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("wooden_hoe", new ItemHoe(ItemTier.WOOD, -3.0F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("iron_hoe", new ItemHoe(ItemTier.IRON, -1.0F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("diamond_hoe", new ItemHoe(ItemTier.DIAMOND, 0.0F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("golden_hoe", new ItemHoe(ItemTier.GOLD, -3.0F, (new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("wheat_seeds", new ItemSeeds(Blocks.WHEAT, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("wheat", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("bread", new ItemFood(5, 0.6F, false, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("leather_helmet", new ItemArmorDyeable(ArmorMaterial.LEATHER, EntityEquipmentSlot.HEAD, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("leather_chestplate", new ItemArmorDyeable(ArmorMaterial.LEATHER, EntityEquipmentSlot.CHEST, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("leather_leggings", new ItemArmorDyeable(ArmorMaterial.LEATHER, EntityEquipmentSlot.LEGS, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("leather_boots", new ItemArmorDyeable(ArmorMaterial.LEATHER, EntityEquipmentSlot.FEET, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("chainmail_helmet", new ItemArmor(ArmorMaterial.CHAIN, EntityEquipmentSlot.HEAD, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("chainmail_chestplate", new ItemArmor(ArmorMaterial.CHAIN, EntityEquipmentSlot.CHEST, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("chainmail_leggings", new ItemArmor(ArmorMaterial.CHAIN, EntityEquipmentSlot.LEGS, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("chainmail_boots", new ItemArmor(ArmorMaterial.CHAIN, EntityEquipmentSlot.FEET, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("iron_helmet", new ItemArmor(ArmorMaterial.IRON, EntityEquipmentSlot.HEAD, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("iron_chestplate", new ItemArmor(ArmorMaterial.IRON, EntityEquipmentSlot.CHEST, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("iron_leggings", new ItemArmor(ArmorMaterial.IRON, EntityEquipmentSlot.LEGS, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("iron_boots", new ItemArmor(ArmorMaterial.IRON, EntityEquipmentSlot.FEET, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("diamond_helmet", new ItemArmor(ArmorMaterial.DIAMOND, EntityEquipmentSlot.HEAD, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("diamond_chestplate", new ItemArmor(ArmorMaterial.DIAMOND, EntityEquipmentSlot.CHEST, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("diamond_leggings", new ItemArmor(ArmorMaterial.DIAMOND, EntityEquipmentSlot.LEGS, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("diamond_boots", new ItemArmor(ArmorMaterial.DIAMOND, EntityEquipmentSlot.FEET, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("golden_helmet", new ItemArmor(ArmorMaterial.GOLD, EntityEquipmentSlot.HEAD, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("golden_chestplate", new ItemArmor(ArmorMaterial.GOLD, EntityEquipmentSlot.CHEST, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("golden_leggings", new ItemArmor(ArmorMaterial.GOLD, EntityEquipmentSlot.LEGS, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("golden_boots", new ItemArmor(ArmorMaterial.GOLD, EntityEquipmentSlot.FEET, (new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("flint", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("porkchop", new ItemFood(3, 0.3F, true, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("cooked_porkchop", new ItemFood(8, 0.8F, true, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("painting", new ItemHangingEntity(EntityPainting.class, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("golden_apple", (new ItemAppleGold(4, 1.2F, false, (new Item.Properties()).func_200916_a(ItemGroup.FOOD).func_208103_a(EnumRarity.RARE))).setAlwaysEdible());
      registerItem("enchanted_golden_apple", (new ItemAppleGoldEnchanted(4, 1.2F, false, (new Item.Properties()).func_200916_a(ItemGroup.FOOD).func_208103_a(EnumRarity.EPIC))).setAlwaysEdible());
      registerItem("sign", new ItemSign((new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.DECORATIONS)));
      Item item = new ItemBucket(Fluids.EMPTY, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.MISC));
      registerItem("bucket", item);
      registerItem("water_bucket", new ItemBucket(Fluids.WATER, (new Item.Properties()).func_200919_a(item).func_200917_a(1).func_200916_a(ItemGroup.MISC)));
      registerItem("lava_bucket", new ItemBucket(Fluids.LAVA, (new Item.Properties()).func_200919_a(item).func_200917_a(1).func_200916_a(ItemGroup.MISC)));
      registerItem("minecart", new ItemMinecart(EntityMinecart.Type.RIDEABLE, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.TRANSPORTATION)));
      registerItem("saddle", new ItemSaddle((new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.TRANSPORTATION)));
      registerItem("redstone", new ItemBlock(Blocks.REDSTONE_WIRE, (new Item.Properties()).func_200916_a(ItemGroup.REDSTONE)));
      registerItem("snowball", new ItemSnowball((new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.MISC)));
      registerItem("oak_boat", new ItemBoat(EntityBoat.Type.OAK, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.TRANSPORTATION)));
      registerItem("leather", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("milk_bucket", new ItemBucketMilk((new Item.Properties()).func_200919_a(item).func_200917_a(1).func_200916_a(ItemGroup.MISC)));
      registerItem("pufferfish_bucket", new ItemBucketFish(EntityType.PUFFERFISH, Fluids.WATER, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC)));
      registerItem("salmon_bucket", new ItemBucketFish(EntityType.SALMON, Fluids.WATER, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC)));
      registerItem("cod_bucket", new ItemBucketFish(EntityType.COD, Fluids.WATER, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC)));
      registerItem("tropical_fish_bucket", new ItemBucketFish(EntityType.TROPICAL_FISH, Fluids.WATER, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC)));
      registerItem("brick", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("clay_ball", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      register(Blocks.SUGAR_CANE, ItemGroup.MISC);
      register(Blocks.KELP, ItemGroup.MISC);
      register(Blocks.DRIED_KELP_BLOCK, ItemGroup.BUILDING_BLOCKS);
      registerItem("paper", new Item((new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("book", new ItemBook((new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("slime_ball", new Item((new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("chest_minecart", new ItemMinecart(EntityMinecart.Type.CHEST, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.TRANSPORTATION)));
      registerItem("furnace_minecart", new ItemMinecart(EntityMinecart.Type.FURNACE, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.TRANSPORTATION)));
      registerItem("egg", new ItemEgg((new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("compass", new ItemCompass((new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("fishing_rod", new ItemFishingRod((new Item.Properties()).func_200918_c(64).func_200916_a(ItemGroup.TOOLS)));
      registerItem("clock", new ItemClock((new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("glowstone_dust", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("cod", new ItemFishFood(ItemFishFood.FishType.COD, false, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("salmon", new ItemFishFood(ItemFishFood.FishType.SALMON, false, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("tropical_fish", new ItemFishFood(ItemFishFood.FishType.TROPICAL_FISH, false, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("pufferfish", new ItemFishFood(ItemFishFood.FishType.PUFFERFISH, false, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("cooked_cod", new ItemFishFood(ItemFishFood.FishType.COD, true, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("cooked_salmon", new ItemFishFood(ItemFishFood.FishType.SALMON, true, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("ink_sac", new ItemDye(EnumDyeColor.BLACK, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("rose_red", new ItemDye(EnumDyeColor.RED, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("cactus_green", new ItemDye(EnumDyeColor.GREEN, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("cocoa_beans", new ItemCocoa(EnumDyeColor.BROWN, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("lapis_lazuli", new ItemDye(EnumDyeColor.BLUE, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("purple_dye", new ItemDye(EnumDyeColor.PURPLE, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("cyan_dye", new ItemDye(EnumDyeColor.CYAN, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("light_gray_dye", new ItemDye(EnumDyeColor.LIGHT_GRAY, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("gray_dye", new ItemDye(EnumDyeColor.GRAY, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("pink_dye", new ItemDye(EnumDyeColor.PINK, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("lime_dye", new ItemDye(EnumDyeColor.LIME, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("dandelion_yellow", new ItemDye(EnumDyeColor.YELLOW, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("light_blue_dye", new ItemDye(EnumDyeColor.LIGHT_BLUE, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("magenta_dye", new ItemDye(EnumDyeColor.MAGENTA, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("orange_dye", new ItemDye(EnumDyeColor.ORANGE, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("bone_meal", new ItemBoneMeal(EnumDyeColor.WHITE, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("bone", new Item((new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("sugar", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      register(new ItemBlock(Blocks.CAKE, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.FOOD)));
      register(new ItemBed(Blocks.WHITE_BED, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.ORANGE_BED, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.MAGENTA_BED, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.LIGHT_BLUE_BED, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.YELLOW_BED, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.LIME_BED, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.PINK_BED, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.GRAY_BED, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.LIGHT_GRAY_BED, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.CYAN_BED, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.PURPLE_BED, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.BLUE_BED, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.BROWN_BED, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.GREEN_BED, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.RED_BED, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      register(new ItemBed(Blocks.BLACK_BED, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("cookie", new ItemFood(2, 0.1F, false, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("filled_map", new ItemMap(new Item.Properties()));
      registerItem("shears", new ItemShears((new Item.Properties()).func_200918_c(238).func_200916_a(ItemGroup.TOOLS)));
      registerItem("melon_slice", new ItemFood(2, 0.3F, false, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("dried_kelp", (new ItemFood(1, 0.3F, false, (new Item.Properties()).func_200916_a(ItemGroup.FOOD))).setFastEating());
      registerItem("pumpkin_seeds", new ItemSeeds(Blocks.PUMPKIN_STEM, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("melon_seeds", new ItemSeeds(Blocks.MELON_STEM, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("beef", new ItemFood(3, 0.3F, true, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("cooked_beef", new ItemFood(8, 0.8F, true, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("chicken", (new ItemFood(2, 0.3F, true, (new Item.Properties()).func_200916_a(ItemGroup.FOOD))).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), 0.3F));
      registerItem("cooked_chicken", new ItemFood(6, 0.6F, true, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("rotten_flesh", (new ItemFood(4, 0.1F, true, (new Item.Properties()).func_200916_a(ItemGroup.FOOD))).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), 0.8F));
      registerItem("ender_pearl", new ItemEnderPearl((new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.MISC)));
      registerItem("blaze_rod", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("ghast_tear", new Item((new Item.Properties()).func_200916_a(ItemGroup.BREWING)));
      registerItem("gold_nugget", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("nether_wart", new ItemSeeds(Blocks.NETHER_WART, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("potion", new ItemPotion((new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.BREWING)));
      Item item1 = new ItemGlassBottle((new Item.Properties()).func_200916_a(ItemGroup.BREWING));
      registerItem("glass_bottle", item1);
      registerItem("spider_eye", (new ItemFood(2, 0.8F, false, (new Item.Properties()).func_200916_a(ItemGroup.FOOD))).setPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0), 1.0F));
      registerItem("fermented_spider_eye", new Item((new Item.Properties()).func_200916_a(ItemGroup.BREWING)));
      registerItem("blaze_powder", new Item((new Item.Properties()).func_200916_a(ItemGroup.BREWING)));
      registerItem("magma_cream", new Item((new Item.Properties()).func_200916_a(ItemGroup.BREWING)));
      register(Blocks.BREWING_STAND, ItemGroup.BREWING);
      register(Blocks.CAULDRON, ItemGroup.BREWING);
      registerItem("ender_eye", new ItemEnderEye((new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("glistering_melon_slice", new Item((new Item.Properties()).func_200916_a(ItemGroup.BREWING)));
      registerItem("bat_spawn_egg", new ItemSpawnEgg(EntityType.BAT, 4996656, 986895, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("blaze_spawn_egg", new ItemSpawnEgg(EntityType.BLAZE, 16167425, 16775294, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("cave_spider_spawn_egg", new ItemSpawnEgg(EntityType.CAVE_SPIDER, 803406, 11013646, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("chicken_spawn_egg", new ItemSpawnEgg(EntityType.CHICKEN, 10592673, 16711680, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("cod_spawn_egg", new ItemSpawnEgg(EntityType.COD, 12691306, 15058059, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("cow_spawn_egg", new ItemSpawnEgg(EntityType.COW, 4470310, 10592673, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("creeper_spawn_egg", new ItemSpawnEgg(EntityType.CREEPER, 894731, 0, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("dolphin_spawn_egg", new ItemSpawnEgg(EntityType.DOLPHIN, 2243405, 16382457, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("donkey_spawn_egg", new ItemSpawnEgg(EntityType.DONKEY, 5457209, 8811878, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("drowned_spawn_egg", new ItemSpawnEgg(EntityType.DROWNED, 9433559, 7969893, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("elder_guardian_spawn_egg", new ItemSpawnEgg(EntityType.ELDER_GUARDIAN, 13552826, 7632531, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("enderman_spawn_egg", new ItemSpawnEgg(EntityType.ENDERMAN, 1447446, 0, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("endermite_spawn_egg", new ItemSpawnEgg(EntityType.ENDERMITE, 1447446, 7237230, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("evoker_spawn_egg", new ItemSpawnEgg(EntityType.EVOKER, 9804699, 1973274, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("ghast_spawn_egg", new ItemSpawnEgg(EntityType.GHAST, 16382457, 12369084, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("guardian_spawn_egg", new ItemSpawnEgg(EntityType.GUARDIAN, 5931634, 15826224, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("horse_spawn_egg", new ItemSpawnEgg(EntityType.HORSE, 12623485, 15656192, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("husk_spawn_egg", new ItemSpawnEgg(EntityType.HUSK, 7958625, 15125652, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("llama_spawn_egg", new ItemSpawnEgg(EntityType.LLAMA, 12623485, 10051392, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("magma_cube_spawn_egg", new ItemSpawnEgg(EntityType.MAGMA_CUBE, 3407872, 16579584, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("mooshroom_spawn_egg", new ItemSpawnEgg(EntityType.MOOSHROOM, 10489616, 12040119, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("mule_spawn_egg", new ItemSpawnEgg(EntityType.MULE, 1769984, 5321501, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("ocelot_spawn_egg", new ItemSpawnEgg(EntityType.OCELOT, 15720061, 5653556, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("parrot_spawn_egg", new ItemSpawnEgg(EntityType.PARROT, 894731, 16711680, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("phantom_spawn_egg", new ItemSpawnEgg(EntityType.PHANTOM, 4411786, 8978176, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("pig_spawn_egg", new ItemSpawnEgg(EntityType.PIG, 15771042, 14377823, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("polar_bear_spawn_egg", new ItemSpawnEgg(EntityType.POLAR_BEAR, 15921906, 9803152, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("pufferfish_spawn_egg", new ItemSpawnEgg(EntityType.PUFFERFISH, 16167425, 3654642, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("rabbit_spawn_egg", new ItemSpawnEgg(EntityType.RABBIT, 10051392, 7555121, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("salmon_spawn_egg", new ItemSpawnEgg(EntityType.SALMON, 10489616, 951412, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("sheep_spawn_egg", new ItemSpawnEgg(EntityType.SHEEP, 15198183, 16758197, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("shulker_spawn_egg", new ItemSpawnEgg(EntityType.SHULKER, 9725844, 5060690, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("silverfish_spawn_egg", new ItemSpawnEgg(EntityType.SILVERFISH, 7237230, 3158064, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("skeleton_spawn_egg", new ItemSpawnEgg(EntityType.SKELETON, 12698049, 4802889, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("skeleton_horse_spawn_egg", new ItemSpawnEgg(EntityType.SKELETON_HORSE, 6842447, 15066584, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("slime_spawn_egg", new ItemSpawnEgg(EntityType.SLIME, 5349438, 8306542, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("spider_spawn_egg", new ItemSpawnEgg(EntityType.SPIDER, 3419431, 11013646, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("squid_spawn_egg", new ItemSpawnEgg(EntityType.SQUID, 2243405, 7375001, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("stray_spawn_egg", new ItemSpawnEgg(EntityType.STRAY, 6387319, 14543594, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("tropical_fish_spawn_egg", new ItemSpawnEgg(EntityType.TROPICAL_FISH, 15690005, 16775663, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("turtle_spawn_egg", new ItemSpawnEgg(EntityType.TURTLE, 15198183, 44975, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("vex_spawn_egg", new ItemSpawnEgg(EntityType.VEX, 8032420, 15265265, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("villager_spawn_egg", new ItemSpawnEgg(EntityType.VILLAGER, 5651507, 12422002, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("vindicator_spawn_egg", new ItemSpawnEgg(EntityType.VINDICATOR, 9804699, 2580065, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("witch_spawn_egg", new ItemSpawnEgg(EntityType.WITCH, 3407872, 5349438, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("wither_skeleton_spawn_egg", new ItemSpawnEgg(EntityType.WITHER_SKELETON, 1315860, 4672845, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("wolf_spawn_egg", new ItemSpawnEgg(EntityType.WOLF, 14144467, 13545366, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("zombie_spawn_egg", new ItemSpawnEgg(EntityType.ZOMBIE, 44975, 7969893, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("zombie_horse_spawn_egg", new ItemSpawnEgg(EntityType.ZOMBIE_HORSE, 3232308, 9945732, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("zombie_pigman_spawn_egg", new ItemSpawnEgg(EntityType.ZOMBIE_PIGMAN, 15373203, 5009705, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("zombie_villager_spawn_egg", new ItemSpawnEgg(EntityType.ZOMBIE_VILLAGER, 5651507, 7969893, (new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("experience_bottle", new ItemExpBottle((new Item.Properties()).func_200916_a(ItemGroup.MISC).func_208103_a(EnumRarity.UNCOMMON)));
      registerItem("fire_charge", new ItemFireCharge((new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("writable_book", new ItemWritableBook((new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC)));
      registerItem("written_book", new ItemWrittenBook((new Item.Properties()).func_200917_a(16)));
      registerItem("emerald", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("item_frame", new ItemItemFrame((new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS)));
      register(Blocks.FLOWER_POT, ItemGroup.DECORATIONS);
      registerItem("carrot", new ItemSeedFood(3, 0.6F, Blocks.CARROTS, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("potato", new ItemSeedFood(1, 0.3F, Blocks.POTATOES, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("baked_potato", new ItemFood(5, 0.6F, false, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("poisonous_potato", (new ItemFood(2, 0.3F, false, (new Item.Properties()).func_200916_a(ItemGroup.FOOD))).setPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0), 0.6F));
      registerItem("map", new ItemEmptyMap((new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("golden_carrot", new ItemFood(6, 1.2F, false, (new Item.Properties()).func_200916_a(ItemGroup.BREWING)));
      register(new ItemWallOrFloor(Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS).func_208103_a(EnumRarity.UNCOMMON)));
      register(new ItemWallOrFloor(Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS).func_208103_a(EnumRarity.UNCOMMON)));
      register(new ItemSkull(Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS).func_208103_a(EnumRarity.UNCOMMON)));
      register(new ItemWallOrFloor(Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS).func_208103_a(EnumRarity.UNCOMMON)));
      register(new ItemWallOrFloor(Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS).func_208103_a(EnumRarity.UNCOMMON)));
      register(new ItemWallOrFloor(Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, (new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS).func_208103_a(EnumRarity.UNCOMMON)));
      registerItem("carrot_on_a_stick", new ItemCarrotOnAStick((new Item.Properties()).func_200918_c(25).func_200916_a(ItemGroup.TRANSPORTATION)));
      registerItem("nether_star", new ItemSimpleFoiled((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS).func_208103_a(EnumRarity.UNCOMMON)));
      registerItem("pumpkin_pie", new ItemFood(8, 0.3F, false, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("firework_rocket", new ItemFireworkRocket((new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("firework_star", new ItemFireworkStar((new Item.Properties()).func_200916_a(ItemGroup.MISC)));
      registerItem("enchanted_book", new ItemEnchantedBook((new Item.Properties()).func_200917_a(1).func_208103_a(EnumRarity.UNCOMMON)));
      registerItem("nether_brick", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("quartz", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("tnt_minecart", new ItemMinecart(EntityMinecart.Type.TNT, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.TRANSPORTATION)));
      registerItem("hopper_minecart", new ItemMinecart(EntityMinecart.Type.HOPPER, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.TRANSPORTATION)));
      registerItem("prismarine_shard", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("prismarine_crystals", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("rabbit", new ItemFood(3, 0.3F, true, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("cooked_rabbit", new ItemFood(5, 0.6F, true, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("rabbit_stew", new ItemSoup(10, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.FOOD)));
      registerItem("rabbit_foot", new Item((new Item.Properties()).func_200916_a(ItemGroup.BREWING)));
      registerItem("rabbit_hide", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("armor_stand", new ItemArmorStand((new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("iron_horse_armor", new Item((new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC)));
      registerItem("golden_horse_armor", new Item((new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC)));
      registerItem("diamond_horse_armor", new Item((new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC)));
      registerItem("lead", new ItemLead((new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("name_tag", new ItemNameTag((new Item.Properties()).func_200916_a(ItemGroup.TOOLS)));
      registerItem("command_block_minecart", new ItemMinecart(EntityMinecart.Type.COMMAND_BLOCK, (new Item.Properties()).func_200917_a(1)));
      registerItem("mutton", new ItemFood(2, 0.3F, true, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("cooked_mutton", new ItemFood(6, 0.8F, true, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("white_banner", new ItemBanner(Blocks.WHITE_BANNER, Blocks.WHITE_WALL_BANNER, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("orange_banner", new ItemBanner(Blocks.ORANGE_BANNER, Blocks.ORANGE_WALL_BANNER, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("magenta_banner", new ItemBanner(Blocks.MAGENTA_BANNER, Blocks.MAGENTA_WALL_BANNER, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("light_blue_banner", new ItemBanner(Blocks.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("yellow_banner", new ItemBanner(Blocks.YELLOW_BANNER, Blocks.YELLOW_WALL_BANNER, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("lime_banner", new ItemBanner(Blocks.LIME_BANNER, Blocks.LIME_WALL_BANNER, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("pink_banner", new ItemBanner(Blocks.PINK_BANNER, Blocks.PINK_WALL_BANNER, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("gray_banner", new ItemBanner(Blocks.GRAY_BANNER, Blocks.GRAY_WALL_BANNER, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("light_gray_banner", new ItemBanner(Blocks.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("cyan_banner", new ItemBanner(Blocks.CYAN_BANNER, Blocks.CYAN_WALL_BANNER, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("purple_banner", new ItemBanner(Blocks.PURPLE_BANNER, Blocks.PURPLE_WALL_BANNER, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("blue_banner", new ItemBanner(Blocks.BLUE_BANNER, Blocks.BLUE_WALL_BANNER, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("brown_banner", new ItemBanner(Blocks.BROWN_BANNER, Blocks.BROWN_WALL_BANNER, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("green_banner", new ItemBanner(Blocks.GREEN_BANNER, Blocks.GREEN_WALL_BANNER, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("red_banner", new ItemBanner(Blocks.RED_BANNER, Blocks.RED_WALL_BANNER, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("black_banner", new ItemBanner(Blocks.BLACK_BANNER, Blocks.BLACK_WALL_BANNER, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.DECORATIONS)));
      registerItem("end_crystal", new ItemEndCrystal((new Item.Properties()).func_200916_a(ItemGroup.DECORATIONS).func_208103_a(EnumRarity.RARE)));
      registerItem("chorus_fruit", (new ItemChorusFruit(4, 0.3F, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS))).setAlwaysEdible());
      registerItem("popped_chorus_fruit", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("beetroot", new ItemFood(1, 0.6F, false, (new Item.Properties()).func_200916_a(ItemGroup.FOOD)));
      registerItem("beetroot_seeds", new ItemSeeds(Blocks.BEETROOTS, (new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("beetroot_soup", new ItemSoup(6, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.FOOD)));
      registerItem("dragon_breath", new Item((new Item.Properties()).func_200919_a(item1).func_200916_a(ItemGroup.BREWING).func_208103_a(EnumRarity.UNCOMMON)));
      registerItem("splash_potion", new ItemSplashPotion((new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.BREWING)));
      registerItem("spectral_arrow", new ItemSpectralArrow((new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("tipped_arrow", new ItemTippedArrow((new Item.Properties()).func_200916_a(ItemGroup.COMBAT)));
      registerItem("lingering_potion", new ItemLingeringPotion((new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.BREWING)));
      registerItem("shield", new ItemShield((new Item.Properties()).func_200918_c(336).func_200916_a(ItemGroup.COMBAT)));
      registerItem("elytra", new ItemElytra((new Item.Properties()).func_200918_c(432).func_200916_a(ItemGroup.TRANSPORTATION).func_208103_a(EnumRarity.UNCOMMON)));
      registerItem("spruce_boat", new ItemBoat(EntityBoat.Type.SPRUCE, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.TRANSPORTATION)));
      registerItem("birch_boat", new ItemBoat(EntityBoat.Type.BIRCH, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.TRANSPORTATION)));
      registerItem("jungle_boat", new ItemBoat(EntityBoat.Type.JUNGLE, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.TRANSPORTATION)));
      registerItem("acacia_boat", new ItemBoat(EntityBoat.Type.ACACIA, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.TRANSPORTATION)));
      registerItem("dark_oak_boat", new ItemBoat(EntityBoat.Type.DARK_OAK, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.TRANSPORTATION)));
      registerItem("totem_of_undying", new Item((new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.COMBAT).func_208103_a(EnumRarity.UNCOMMON)));
      registerItem("shulker_shell", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("iron_nugget", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("knowledge_book", new ItemKnowledgeBook((new Item.Properties()).func_200917_a(1)));
      registerItem("debug_stick", new ItemDebugStick((new Item.Properties()).func_200917_a(1)));
      registerItem("music_disc_13", new ItemRecord(1, SoundEvents.MUSIC_DISC_13, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC).func_208103_a(EnumRarity.RARE)));
      registerItem("music_disc_cat", new ItemRecord(2, SoundEvents.MUSIC_DISC_CAT, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC).func_208103_a(EnumRarity.RARE)));
      registerItem("music_disc_blocks", new ItemRecord(3, SoundEvents.MUSIC_DISC_BLOCKS, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC).func_208103_a(EnumRarity.RARE)));
      registerItem("music_disc_chirp", new ItemRecord(4, SoundEvents.MUSIC_DISC_CHIRP, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC).func_208103_a(EnumRarity.RARE)));
      registerItem("music_disc_far", new ItemRecord(5, SoundEvents.MUSIC_DISC_FAR, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC).func_208103_a(EnumRarity.RARE)));
      registerItem("music_disc_mall", new ItemRecord(6, SoundEvents.MUSIC_DISC_MALL, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC).func_208103_a(EnumRarity.RARE)));
      registerItem("music_disc_mellohi", new ItemRecord(7, SoundEvents.MUSIC_DISC_MELLOHI, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC).func_208103_a(EnumRarity.RARE)));
      registerItem("music_disc_stal", new ItemRecord(8, SoundEvents.MUSIC_DISC_STAL, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC).func_208103_a(EnumRarity.RARE)));
      registerItem("music_disc_strad", new ItemRecord(9, SoundEvents.MUSIC_DISC_STRAD, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC).func_208103_a(EnumRarity.RARE)));
      registerItem("music_disc_ward", new ItemRecord(10, SoundEvents.MUSIC_DISC_WARD, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC).func_208103_a(EnumRarity.RARE)));
      registerItem("music_disc_11", new ItemRecord(11, SoundEvents.MUSIC_DISC_11, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC).func_208103_a(EnumRarity.RARE)));
      registerItem("music_disc_wait", new ItemRecord(12, SoundEvents.MUSIC_DISC_WAIT, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.MISC).func_208103_a(EnumRarity.RARE)));
      registerItem("trident", new ItemTrident((new Item.Properties()).func_200918_c(250).func_200916_a(ItemGroup.COMBAT)));
      registerItem("phantom_membrane", new Item((new Item.Properties()).func_200916_a(ItemGroup.BREWING)));
      registerItem("nautilus_shell", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS)));
      registerItem("heart_of_the_sea", new Item((new Item.Properties()).func_200916_a(ItemGroup.MATERIALS).func_208103_a(EnumRarity.UNCOMMON)));
   }

   private static void register(Block p_179216_0_) {
      register(new ItemBlock(p_179216_0_, new Item.Properties()));
   }

   private static void register(Block p_200879_0_, ItemGroup p_200879_1_) {
      register(new ItemBlock(p_200879_0_, (new Item.Properties()).func_200916_a(p_200879_1_)));
   }

   private static void register(ItemBlock p_200126_0_) {
      register(p_200126_0_.getBlock(), p_200126_0_);
   }

   protected static void register(Block p_179214_0_, Item p_179214_1_) {
      register(IRegistry.field_212618_g.func_177774_c(p_179214_0_), p_179214_1_);
   }

   private static void registerItem(String p_195936_0_, Item p_195936_1_) {
      register(new ResourceLocation(p_195936_0_), p_195936_1_);
   }

   private static void register(ResourceLocation p_195940_0_, Item p_195940_1_) {
      if (p_195940_1_ instanceof ItemBlock) {
         ((ItemBlock)p_195940_1_).addToBlockToItemMap(BLOCK_TO_ITEM, p_195940_1_);
      }

      IRegistry.field_212630_s.func_82595_a(p_195940_0_, p_195940_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getDefaultInstance() {
      return new ItemStack(this);
   }

   public boolean isIn(Tag<Item> p_206844_1_) {
      return p_206844_1_.contains(this);
   }

   public static class Properties {
      private int field_200920_a = 64;
      private int field_200921_b;
      private Item field_200922_c;
      private ItemGroup field_200923_d;
      private EnumRarity field_208104_e = EnumRarity.COMMON;

      public Item.Properties func_200917_a(int p_200917_1_) {
         if (this.field_200921_b > 0) {
            throw new RuntimeException("Unable to have damage AND stack.");
         } else {
            this.field_200920_a = p_200917_1_;
            return this;
         }
      }

      public Item.Properties func_200915_b(int p_200915_1_) {
         return this.field_200921_b == 0 ? this.func_200918_c(p_200915_1_) : this;
      }

      private Item.Properties func_200918_c(int p_200918_1_) {
         this.field_200921_b = p_200918_1_;
         this.field_200920_a = 1;
         return this;
      }

      public Item.Properties func_200919_a(Item p_200919_1_) {
         this.field_200922_c = p_200919_1_;
         return this;
      }

      public Item.Properties func_200916_a(ItemGroup p_200916_1_) {
         this.field_200923_d = p_200916_1_;
         return this;
      }

      public Item.Properties func_208103_a(EnumRarity p_208103_1_) {
         this.field_208104_e = p_208103_1_;
         return this;
      }
   }
}
