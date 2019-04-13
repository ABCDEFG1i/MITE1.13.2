package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ItemStack {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ItemStack EMPTY = new ItemStack((Item)null);
   public static final DecimalFormat DECIMALFORMAT = createAttributeModifierDecimalFormat();
   private int count;
   private int animationsToGo;
   @Deprecated
   private final Item item;
   private NBTTagCompound tag;
   private boolean isEmpty;
   private EntityItemFrame itemFrame;
   private BlockWorldState canDestroyCacheBlock;
   private boolean canDestroyCacheResult;
   private BlockWorldState canPlaceOnCacheBlock;
   private boolean canPlaceOnCacheResult;

   private static DecimalFormat createAttributeModifierDecimalFormat() {
      DecimalFormat decimalformat = new DecimalFormat("#.##");
      decimalformat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
      return decimalformat;
   }

   public ItemStack(IItemProvider p_i48203_1_) {
      this(p_i48203_1_, 1);
   }

   public ItemStack(IItemProvider p_i48204_1_, int p_i48204_2_) {
      this.item = p_i48204_1_ == null ? null : p_i48204_1_.asItem();
      this.count = p_i48204_2_;
      this.updateEmptyState();
   }

   private void updateEmptyState() {
      this.isEmpty = false;
      this.isEmpty = this.isEmpty();
   }

   private ItemStack(NBTTagCompound p_i47263_1_) {
      Item item = IRegistry.field_212630_s.func_212608_b(new ResourceLocation(p_i47263_1_.getString("id")));
      this.item = item == null ? Items.AIR : item;
      this.count = p_i47263_1_.getByte("Count");
      if (p_i47263_1_.hasKey("tag", 10)) {
         this.tag = p_i47263_1_.getCompoundTag("tag");
         this.getItem().updateItemStackNBT(p_i47263_1_);
      }

      if (this.getItem().isDamageable()) {
         this.setDamage(this.getDamage());
      }

      this.updateEmptyState();
   }

   public static ItemStack loadFromNBT(NBTTagCompound p_199557_0_) {
      try {
         return new ItemStack(p_199557_0_);
      } catch (RuntimeException runtimeexception) {
         LOGGER.debug("Tried to load invalid item: {}", p_199557_0_, runtimeexception);
         return EMPTY;
      }
   }

   public boolean isEmpty() {
      if (this == EMPTY) {
         return true;
      } else if (this.getItem() != null && this.getItem() != Items.AIR) {
         return this.count <= 0;
      } else {
         return true;
      }
   }

   public ItemStack split(int p_77979_1_) {
      int i = Math.min(p_77979_1_, this.count);
      ItemStack itemstack = this.copy();
      itemstack.setCount(i);
      this.shrink(i);
      return itemstack;
   }

   public Item getItem() {
      return this.isEmpty ? Items.AIR : this.item;
   }

   public EnumActionResult onItemUse(ItemUseContext p_196084_1_) {
      EntityPlayer entityplayer = p_196084_1_.getPlayer();
      BlockPos blockpos = p_196084_1_.getPos();
      BlockWorldState blockworldstate = new BlockWorldState(p_196084_1_.getWorld(), blockpos, false);
      if (entityplayer != null && !entityplayer.capabilities.allowEdit && !this.canPlaceOn(p_196084_1_.getWorld().getTags(), blockworldstate)) {
         return EnumActionResult.PASS;
      } else {
         Item item = this.getItem();
         EnumActionResult enumactionresult = item.onItemUse(p_196084_1_);
         if (entityplayer != null && enumactionresult == EnumActionResult.SUCCESS) {
            entityplayer.func_71029_a(StatList.ITEM_USED.func_199076_b(item));
         }

         return enumactionresult;
      }
   }

   public float getDestroySpeed(IBlockState p_150997_1_) {
      return this.getItem().getDestroySpeed(this, p_150997_1_);
   }

   public ActionResult<ItemStack> useItemRightClick(World p_77957_1_, EntityPlayer p_77957_2_, EnumHand p_77957_3_) {
      return this.getItem().onItemRightClick(p_77957_1_, p_77957_2_, p_77957_3_);
   }

   public ItemStack onItemUseFinish(World p_77950_1_, EntityLivingBase p_77950_2_) {
      return this.getItem().onItemUseFinish(this, p_77950_1_, p_77950_2_);
   }

   public NBTTagCompound write(NBTTagCompound p_77955_1_) {
      ResourceLocation resourcelocation = IRegistry.field_212630_s.func_177774_c(this.getItem());
      p_77955_1_.setString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
      p_77955_1_.setByte("Count", (byte)this.count);
      if (this.tag != null) {
         p_77955_1_.setTag("tag", this.tag);
      }

      return p_77955_1_;
   }

   public int getMaxStackSize() {
      return this.getItem().getMaxStackSize();
   }

   public boolean isStackable() {
      return this.getMaxStackSize() > 1 && (!this.isDamageable() || !this.isDamaged());
   }

   public boolean isDamageable() {
      if (!this.isEmpty && this.getItem().getMaxDamage() > 0) {
         NBTTagCompound nbttagcompound = this.getTag();
         return nbttagcompound == null || !nbttagcompound.getBoolean("Unbreakable");
      } else {
         return false;
      }
   }

   public boolean isDamaged() {
      return this.isDamageable() && this.getDamage() > 0;
   }

   public int getDamage() {
      return this.tag == null ? 0 : this.tag.getInteger("Damage");
   }

   public void setDamage(int p_196085_1_) {
      this.getOrCreateTag().setInteger("Damage", Math.max(0, p_196085_1_));
   }

   public int getMaxDamage() {
      return this.getItem().getMaxDamage();
   }

   public boolean attemptDamageItem(int p_96631_1_, Random p_96631_2_, @Nullable EntityPlayerMP p_96631_3_) {
      if (!this.isDamageable()) {
         return false;
      } else {
         if (p_96631_1_ > 0) {
            int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, this);
            int j = 0;

            for(int k = 0; i > 0 && k < p_96631_1_; ++k) {
               if (EnchantmentDurability.negateDamage(this, i, p_96631_2_)) {
                  ++j;
               }
            }

            p_96631_1_ -= j;
            if (p_96631_1_ <= 0) {
               return false;
            }
         }

         if (p_96631_3_ != null && p_96631_1_ != 0) {
            CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(p_96631_3_, this, this.getDamage() + p_96631_1_);
         }

         int l = this.getDamage() + p_96631_1_;
         this.setDamage(l);
         return l >= this.getMaxDamage();
      }
   }

   public void damageItem(int p_77972_1_, EntityLivingBase p_77972_2_) {
      if (!(p_77972_2_ instanceof EntityPlayer) || !((EntityPlayer)p_77972_2_).capabilities.isCreativeMode) {
         if (this.isDamageable()) {
            if (this.attemptDamageItem(p_77972_1_, p_77972_2_.getRNG(), p_77972_2_ instanceof EntityPlayerMP ? (EntityPlayerMP)p_77972_2_ : null)) {
               p_77972_2_.renderBrokenItemStack(this);
               Item item = this.getItem();
               this.shrink(1);
               if (p_77972_2_ instanceof EntityPlayer) {
                  ((EntityPlayer)p_77972_2_).func_71029_a(StatList.ITEM_BROKEN.func_199076_b(item));
               }

               this.setDamage(0);
            }

         }
      }
   }

   public void hitEntity(EntityLivingBase p_77961_1_, EntityPlayer p_77961_2_) {
      Item item = this.getItem();
      if (item.hitEntity(this, p_77961_1_, p_77961_2_)) {
         p_77961_2_.func_71029_a(StatList.ITEM_USED.func_199076_b(item));
      }

   }

   public void onBlockDestroyed(World p_179548_1_, IBlockState p_179548_2_, BlockPos p_179548_3_, EntityPlayer p_179548_4_) {
      Item item = this.getItem();
      if (item.onBlockDestroyed(this, p_179548_1_, p_179548_2_, p_179548_3_, p_179548_4_)) {
         p_179548_4_.func_71029_a(StatList.ITEM_USED.func_199076_b(item));
      }

   }

   public boolean canHarvestBlock(IBlockState p_150998_1_) {
      return this.getItem().canHarvestBlock(p_150998_1_);
   }

   public boolean interactWithEntity(EntityPlayer p_111282_1_, EntityLivingBase p_111282_2_, EnumHand p_111282_3_) {
      return this.getItem().itemInteractionForEntity(this, p_111282_1_, p_111282_2_, p_111282_3_);
   }

   public ItemStack copy() {
      ItemStack itemstack = new ItemStack(this.getItem(), this.count);
      itemstack.setAnimationsToGo(this.getAnimationsToGo());
      if (this.tag != null) {
         itemstack.tag = this.tag.copy();
      }

      return itemstack;
   }

   public static boolean areItemStackTagsEqual(ItemStack p_77970_0_, ItemStack p_77970_1_) {
      if (p_77970_0_.isEmpty() && p_77970_1_.isEmpty()) {
         return true;
      } else if (!p_77970_0_.isEmpty() && !p_77970_1_.isEmpty()) {
         if (p_77970_0_.tag == null && p_77970_1_.tag != null) {
            return false;
         } else {
            return p_77970_0_.tag == null || p_77970_0_.tag.equals(p_77970_1_.tag);
         }
      } else {
         return false;
      }
   }

   public static boolean areItemStacksEqual(ItemStack p_77989_0_, ItemStack p_77989_1_) {
      if (p_77989_0_.isEmpty() && p_77989_1_.isEmpty()) {
         return true;
      } else {
         return (!p_77989_0_.isEmpty() && !p_77989_1_.isEmpty()) && p_77989_0_.isItemStackEqual(p_77989_1_);
      }
   }

   private boolean isItemStackEqual(ItemStack p_77959_1_) {
      if (this.count != p_77959_1_.count) {
         return false;
      } else if (this.getItem() != p_77959_1_.getItem()) {
         return false;
      } else if (this.tag == null && p_77959_1_.tag != null) {
         return false;
      } else {
         return this.tag == null || this.tag.equals(p_77959_1_.tag);
      }
   }

   public static boolean areItemsEqual(ItemStack p_179545_0_, ItemStack p_179545_1_) {
      if (p_179545_0_ == p_179545_1_) {
         return true;
      } else {
         return (!p_179545_0_.isEmpty() && !p_179545_1_.isEmpty()) && p_179545_0_.isItemEqual(p_179545_1_);
      }
   }

   public static boolean areItemsEqualIgnoreDurability(ItemStack p_185132_0_, ItemStack p_185132_1_) {
      if (p_185132_0_ == p_185132_1_) {
         return true;
      } else {
         return (!p_185132_0_.isEmpty() && !p_185132_1_.isEmpty()) && p_185132_0_.isItemEqualIgnoreDurability(
                 p_185132_1_);
      }
   }

   public boolean isItemEqual(ItemStack p_77969_1_) {
      return !p_77969_1_.isEmpty() && this.getItem() == p_77969_1_.getItem();
   }

   public boolean isItemEqualIgnoreDurability(ItemStack p_185136_1_) {
      if (!this.isDamageable()) {
         return this.isItemEqual(p_185136_1_);
      } else {
         return !p_185136_1_.isEmpty() && this.getItem() == p_185136_1_.getItem();
      }
   }

   public String getTranslationKey() {
      return this.getItem().getTranslationKey(this);
   }

   public String toString() {
      return this.count + "x" + this.getItem().getTranslationKey();
   }

   public void inventoryTick(World p_77945_1_, Entity p_77945_2_, int p_77945_3_, boolean p_77945_4_) {
      if (this.animationsToGo > 0) {
         --this.animationsToGo;
      }

      if (this.getItem() != null) {
         this.getItem().inventoryTick(this, p_77945_1_, p_77945_2_, p_77945_3_, p_77945_4_);
      }

   }

   public void onCrafting(World p_77980_1_, EntityPlayer p_77980_2_, int p_77980_3_) {
      p_77980_2_.func_71064_a(StatList.ITEM_CRAFTED.func_199076_b(this.getItem()), p_77980_3_);
      this.getItem().onCreated(this, p_77980_1_, p_77980_2_);
   }

   public int getUseDuration() {
      return this.getItem().getUseDuration(this);
   }

   public EnumAction getUseAction() {
      return this.getItem().getUseAction(this);
   }

   public void onPlayerStoppedUsing(World p_77974_1_, EntityLivingBase p_77974_2_, int p_77974_3_) {
      this.getItem().onPlayerStoppedUsing(this, p_77974_1_, p_77974_2_, p_77974_3_);
   }

   public boolean hasTag() {
      return !this.isEmpty && this.tag != null && !this.tag.isEmpty();
   }

   @Nullable
   public NBTTagCompound getTag() {
      return this.tag;
   }

   public NBTTagCompound getOrCreateTag() {
      if (this.tag == null) {
         this.setTag(new NBTTagCompound());
      }

      return this.tag;
   }

   public NBTTagCompound getOrCreateChildTag(String p_190925_1_) {
      if (this.tag != null && this.tag.hasKey(p_190925_1_, 10)) {
         return this.tag.getCompoundTag(p_190925_1_);
      } else {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         this.setTagInfo(p_190925_1_, nbttagcompound);
         return nbttagcompound;
      }
   }

   @Nullable
   public NBTTagCompound getChildTag(String p_179543_1_) {
      return this.tag != null && this.tag.hasKey(p_179543_1_, 10) ? this.tag.getCompoundTag(p_179543_1_) : null;
   }

   public void removeChildTag(String p_196083_1_) {
      if (this.tag != null && this.tag.hasKey(p_196083_1_)) {
         this.tag.removeTag(p_196083_1_);
         if (this.tag.isEmpty()) {
            this.tag = null;
         }
      }

   }

   public NBTTagList getEnchantmentTagList() {
      return this.tag != null ? this.tag.getTagList("Enchantments", 10) : new NBTTagList();
   }

   public void setTag(@Nullable NBTTagCompound p_77982_1_) {
      this.tag = p_77982_1_;
   }

   public ITextComponent getDisplayName() {
      NBTTagCompound nbttagcompound = this.getChildTag("display");
      if (nbttagcompound != null && nbttagcompound.hasKey("Name", 8)) {
         try {
            ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(nbttagcompound.getString("Name"));
            if (itextcomponent != null) {
               return itextcomponent;
            }

            nbttagcompound.removeTag("Name");
         } catch (JsonParseException var3) {
            nbttagcompound.removeTag("Name");
         }
      }

      return this.getItem().getDisplayName(this);
   }

   public ItemStack setDisplayName(@Nullable ITextComponent p_200302_1_) {
      NBTTagCompound nbttagcompound = this.getOrCreateChildTag("display");
      if (p_200302_1_ != null) {
         nbttagcompound.setString("Name", ITextComponent.Serializer.toJson(p_200302_1_));
      } else {
         nbttagcompound.removeTag("Name");
      }

      return this;
   }

   public void clearCustomName() {
      NBTTagCompound nbttagcompound = this.getChildTag("display");
      if (nbttagcompound != null) {
         nbttagcompound.removeTag("Name");
         if (nbttagcompound.isEmpty()) {
            this.removeChildTag("display");
         }
      }

      if (this.tag != null && this.tag.isEmpty()) {
         this.tag = null;
      }

   }

   public boolean hasDisplayName() {
      NBTTagCompound nbttagcompound = this.getChildTag("display");
      return nbttagcompound != null && nbttagcompound.hasKey("Name", 8);
   }

   @OnlyIn(Dist.CLIENT)
   public List<ITextComponent> getTooltip(@Nullable EntityPlayer p_82840_1_, ITooltipFlag p_82840_2_) {
      List<ITextComponent> list = Lists.newArrayList();
      ITextComponent itextcomponent = (new TextComponentString("")).appendSibling(this.getDisplayName()).applyTextStyle(this.getRarity().color);
      if (this.hasDisplayName()) {
         itextcomponent.applyTextStyle(TextFormatting.ITALIC);
      }

      list.add(itextcomponent);
      if (!p_82840_2_.isAdvanced() && !this.hasDisplayName() && this.getItem() == Items.FILLED_MAP) {
         list.add((new TextComponentString("#" + ItemMap.getMapId(this))).applyTextStyle(TextFormatting.GRAY));
      }

      int i = 0;
      if (this.hasTag() && this.tag.hasKey("HideFlags", 99)) {
         i = this.tag.getInteger("HideFlags");
      }

      if ((i & 32) == 0) {
         this.getItem().addInformation(this, p_82840_1_ == null ? null : p_82840_1_.world, list, p_82840_2_);
      }

      if (this.hasTag()) {
         if ((i & 1) == 0) {
            NBTTagList nbttaglist = this.getEnchantmentTagList();

            for(int j = 0; j < nbttaglist.size(); ++j) {
               NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(j);
               Enchantment enchantment = IRegistry.field_212628_q.func_212608_b(ResourceLocation.makeResourceLocation(nbttagcompound.getString("id")));
               if (enchantment != null) {
                  list.add(enchantment.func_200305_d(nbttagcompound.getInteger("lvl")));
               }
            }
         }

         if (this.tag.hasKey("display", 10)) {
            NBTTagCompound nbttagcompound1 = this.tag.getCompoundTag("display");
            if (nbttagcompound1.hasKey("color", 3)) {
               if (p_82840_2_.isAdvanced()) {
                  list.add((new TextComponentTranslation("item.color", String.format("#%06X", nbttagcompound1.getInteger("color")))).applyTextStyle(TextFormatting.GRAY));
               } else {
                  list.add((new TextComponentTranslation("item.dyed")).applyTextStyles(TextFormatting.GRAY,
                          TextFormatting.ITALIC));
               }
            }

            if (nbttagcompound1.getTagId("Lore") == 9) {
               NBTTagList nbttaglist3 = nbttagcompound1.getTagList("Lore", 8);

               for(int i1 = 0; i1 < nbttaglist3.size(); ++i1) {
                  list.add((new TextComponentString(nbttaglist3.getStringTagAt(i1))).applyTextStyles(TextFormatting.DARK_PURPLE,
                          TextFormatting.ITALIC));
               }
            }
         }
      }

      for(EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values()) {
         Multimap<String, AttributeModifier> multimap = this.getAttributeModifiers(entityequipmentslot);
         if (!multimap.isEmpty() && (i & 2) == 0) {
            list.add(new TextComponentString(""));
            list.add((new TextComponentTranslation("item.modifiers." + entityequipmentslot.getName())).applyTextStyle(TextFormatting.GRAY));

            for(Entry<String, AttributeModifier> entry : multimap.entries()) {
               AttributeModifier attributemodifier = entry.getValue();
               double d0 = attributemodifier.getAmount();
               boolean flag = false;
               if (p_82840_1_ != null) {
                  if (attributemodifier.getID() == Item.ATTACK_DAMAGE_MODIFIER) {
                     d0 = d0 + p_82840_1_.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
                     d0 = d0 + (double)EnchantmentHelper.getModifierForCreature(this, CreatureAttribute.UNDEFINED);
                     flag = true;
                  } else if (attributemodifier.getID() == Item.ATTACK_SPEED_MODIFIER) {
                     d0 += p_82840_1_.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue();
                     flag = true;
                  }
               }

               double d1;
               if (attributemodifier.getOperation() != 1 && attributemodifier.getOperation() != 2) {
                  d1 = d0;
               } else {
                  d1 = d0 * 100.0D;
               }

               if (flag) {
                  list.add((new TextComponentString(" ")).appendSibling(new TextComponentTranslation("attribute.modifier.equals." + attributemodifier.getOperation(), DECIMALFORMAT.format(d1), new TextComponentTranslation("attribute.name." + entry.getKey()))).applyTextStyle(TextFormatting.DARK_GREEN));
               } else if (d0 > 0.0D) {
                  list.add((new TextComponentTranslation("attribute.modifier.plus." + attributemodifier.getOperation(), DECIMALFORMAT.format(d1), new TextComponentTranslation("attribute.name." + entry.getKey()))).applyTextStyle(TextFormatting.BLUE));
               } else if (d0 < 0.0D) {
                  d1 = d1 * -1.0D;
                  list.add((new TextComponentTranslation("attribute.modifier.take." + attributemodifier.getOperation(), DECIMALFORMAT.format(d1), new TextComponentTranslation("attribute.name." + entry.getKey()))).applyTextStyle(TextFormatting.RED));
               }
            }
         }
      }

      if (this.hasTag() && this.getTag().getBoolean("Unbreakable") && (i & 4) == 0) {
         list.add((new TextComponentTranslation("item.unbreakable")).applyTextStyle(TextFormatting.BLUE));
      }

      if (this.hasTag() && this.tag.hasKey("CanDestroy", 9) && (i & 8) == 0) {
         NBTTagList nbttaglist1 = this.tag.getTagList("CanDestroy", 8);
         if (!nbttaglist1.isEmpty()) {
            list.add(new TextComponentString(""));
            list.add((new TextComponentTranslation("item.canBreak")).applyTextStyle(TextFormatting.GRAY));

            for(int k = 0; k < nbttaglist1.size(); ++k) {
               list.addAll(getPlacementTooltip(nbttaglist1.getStringTagAt(k)));
            }
         }
      }

      if (this.hasTag() && this.tag.hasKey("CanPlaceOn", 9) && (i & 16) == 0) {
         NBTTagList nbttaglist2 = this.tag.getTagList("CanPlaceOn", 8);
         if (!nbttaglist2.isEmpty()) {
            list.add(new TextComponentString(""));
            list.add((new TextComponentTranslation("item.canPlace")).applyTextStyle(TextFormatting.GRAY));

            for(int l = 0; l < nbttaglist2.size(); ++l) {
               list.addAll(getPlacementTooltip(nbttaglist2.getStringTagAt(l)));
            }
         }
      }

      //MITEMODDED Changed to make can always see the durability
      if (this.isDamageable()) {
         list.add(new TextComponentTranslation("item.durability", this.getMaxDamage() - this.getDamage(), this.getMaxDamage()));
      }
      if (p_82840_2_.isAdvanced()) {


         list.add((new TextComponentString(IRegistry.field_212630_s.func_177774_c(this.getItem()).toString())).applyTextStyle(TextFormatting.DARK_GRAY));
         if (this.hasTag()) {
            list.add((new TextComponentTranslation("item.nbt_tags", this.getTag().getKeySet().size())).applyTextStyle(TextFormatting.DARK_GRAY));
         }
      }

      return list;
   }

   @OnlyIn(Dist.CLIENT)
   private static Collection<ITextComponent> getPlacementTooltip(String p_206845_0_) {
      try {
         BlockStateParser blockstateparser = (new BlockStateParser(new StringReader(p_206845_0_), true)).parse(true);
         IBlockState iblockstate = blockstateparser.func_197249_b();
         ResourceLocation resourcelocation = blockstateparser.func_199829_d();
         boolean flag = iblockstate != null;
         boolean flag1 = resourcelocation != null;
         if (flag || flag1) {
            if (flag) {
               return Lists.newArrayList(iblockstate.getBlock().getNameTextComponent().applyTextStyle(TextFormatting.DARK_GRAY));
            }

            Tag<Block> tag = BlockTags.getCollection().get(resourcelocation);
            if (tag != null) {
               Collection<Block> collection = tag.getAllElements();
               if (!collection.isEmpty()) {
                  return collection.stream().map(Block::getNameTextComponent).map((p_211702_0_) -> {
                     return p_211702_0_.applyTextStyle(TextFormatting.DARK_GRAY);
                  }).collect(Collectors.toList());
               }
            }
         }
      } catch (CommandSyntaxException var8) {
      }

      return Lists.newArrayList((new TextComponentString("missingno")).applyTextStyle(TextFormatting.DARK_GRAY));
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect() {
      return this.getItem().hasEffect(this);
   }

   public EnumRarity getRarity() {
      return this.getItem().getRarity(this);
   }

   public boolean isEnchantable() {
      if (!this.getItem().isEnchantable(this)) {
         return false;
      } else {
         return !this.isEnchanted();
      }
   }

   public void addEnchantment(Enchantment p_77966_1_, int p_77966_2_) {
      this.getOrCreateTag();
      if (!this.tag.hasKey("Enchantments", 9)) {
         this.tag.setTag("Enchantments", new NBTTagList());
      }

      NBTTagList nbttaglist = this.tag.getTagList("Enchantments", 10);
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setString("id", String.valueOf(IRegistry.field_212628_q.func_177774_c(p_77966_1_)));
      nbttagcompound.setShort("lvl", (short)((byte)p_77966_2_));
      nbttaglist.add(nbttagcompound);
   }

   public boolean isEnchanted() {
      if (this.tag != null && this.tag.hasKey("Enchantments", 9)) {
         return !this.tag.getTagList("Enchantments", 10).isEmpty();
      } else {
         return false;
      }
   }

   public void setTagInfo(String p_77983_1_, INBTBase p_77983_2_) {
      this.getOrCreateTag().setTag(p_77983_1_, p_77983_2_);
   }

   public boolean isOnItemFrame() {
      return this.itemFrame != null;
   }

   public void setItemFrame(@Nullable EntityItemFrame p_82842_1_) {
      this.itemFrame = p_82842_1_;
   }

   @Nullable
   public EntityItemFrame getItemFrame() {
      return this.isEmpty ? null : this.itemFrame;
   }



   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot p_111283_1_) {
      Multimap<String, AttributeModifier> multimap;
      if (this.hasTag() && this.tag.hasKey("AttributeModifiers", 9)) {
         multimap = HashMultimap.create();
         NBTTagList nbttaglist = this.tag.getTagList("AttributeModifiers", 10);

         for(int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            AttributeModifier attributemodifier = SharedMonsterAttributes.readAttributeModifierFromNBT(nbttagcompound);
            if (attributemodifier != null && (!nbttagcompound.hasKey("Slot", 8) || nbttagcompound.getString("Slot").equals(p_111283_1_.getName())) && attributemodifier.getID().getLeastSignificantBits() != 0L && attributemodifier.getID().getMostSignificantBits() != 0L) {
               multimap.put(nbttagcompound.getString("AttributeName"), attributemodifier);
            }
         }
      } else {
         multimap = this.getItem().getAttributeModifiers(p_111283_1_);
      }

      return multimap;
   }

   public void addAttributeModifier(String p_185129_1_, AttributeModifier p_185129_2_, @Nullable EntityEquipmentSlot p_185129_3_) {
      this.getOrCreateTag();
      if (!this.tag.hasKey("AttributeModifiers", 9)) {
         this.tag.setTag("AttributeModifiers", new NBTTagList());
      }

      NBTTagList nbttaglist = this.tag.getTagList("AttributeModifiers", 10);
      NBTTagCompound nbttagcompound = SharedMonsterAttributes.writeAttributeModifierToNBT(p_185129_2_);
      nbttagcompound.setString("AttributeName", p_185129_1_);
      if (p_185129_3_ != null) {
         nbttagcompound.setString("Slot", p_185129_3_.getName());
      }

      nbttaglist.add(nbttagcompound);
   }

   public ITextComponent getTextComponent() {
      ITextComponent itextcomponent = (new TextComponentString("")).appendSibling(this.getDisplayName());
      if (this.hasDisplayName()) {
         itextcomponent.applyTextStyle(TextFormatting.ITALIC);
      }

      ITextComponent itextcomponent1 = TextComponentUtils.wrapInSquareBrackets(itextcomponent);
      if (!this.isEmpty) {
         NBTTagCompound nbttagcompound = this.write(new NBTTagCompound());
         itextcomponent1.applyTextStyle(this.getRarity().color).applyTextStyle((p_211700_1_) -> {
            p_211700_1_.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new TextComponentString(nbttagcompound.toString())));
         });
      }

      return itextcomponent1;
   }

   private static boolean isStateAndTileEntityEqual(BlockWorldState p_206846_0_, @Nullable BlockWorldState p_206846_1_) {
      if (p_206846_1_ != null && p_206846_0_.getBlockState() == p_206846_1_.getBlockState()) {
         if (p_206846_0_.getTileEntity() == null && p_206846_1_.getTileEntity() == null) {
            return true;
         } else {
            return (p_206846_0_.getTileEntity() != null && p_206846_1_.getTileEntity() != null) && Objects.equals(
                    p_206846_0_.getTileEntity().writeToNBT(new NBTTagCompound()),
                    p_206846_1_.getTileEntity().writeToNBT(new NBTTagCompound()));
         }
      } else {
         return false;
      }
   }

   public boolean canDestroy(NetworkTagManager p_206848_1_, BlockWorldState p_206848_2_) {
      if (isStateAndTileEntityEqual(p_206848_2_, this.canDestroyCacheBlock)) {
         return this.canDestroyCacheResult;
      } else {
         this.canDestroyCacheBlock = p_206848_2_;
         if (this.hasTag() && this.tag.hasKey("CanDestroy", 9)) {
            NBTTagList nbttaglist = this.tag.getTagList("CanDestroy", 8);

            for(int i = 0; i < nbttaglist.size(); ++i) {
               String s = nbttaglist.getStringTagAt(i);

               try {
                  Predicate<BlockWorldState> predicate = BlockPredicateArgument.blockPredicateArgument().parse(new StringReader(s)).create(p_206848_1_);
                  if (predicate.test(p_206848_2_)) {
                     this.canDestroyCacheResult = true;
                     return true;
                  }
               } catch (CommandSyntaxException var7) {
               }
            }
         }

         this.canDestroyCacheResult = false;
         return false;
      }
   }

   public boolean canPlaceOn(NetworkTagManager p_206847_1_, BlockWorldState p_206847_2_) {
      if (isStateAndTileEntityEqual(p_206847_2_, this.canPlaceOnCacheBlock)) {
         return this.canPlaceOnCacheResult;
      } else {
         this.canPlaceOnCacheBlock = p_206847_2_;
         if (this.hasTag() && this.tag.hasKey("CanPlaceOn", 9)) {
            NBTTagList nbttaglist = this.tag.getTagList("CanPlaceOn", 8);

            for(int i = 0; i < nbttaglist.size(); ++i) {
               String s = nbttaglist.getStringTagAt(i);

               try {
                  Predicate<BlockWorldState> predicate = BlockPredicateArgument.blockPredicateArgument().parse(new StringReader(s)).create(p_206847_1_);
                  if (predicate.test(p_206847_2_)) {
                     this.canPlaceOnCacheResult = true;
                     return true;
                  }
               } catch (CommandSyntaxException var7) {
               }
            }
         }

         this.canPlaceOnCacheResult = false;
         return false;
      }
   }

   public int getAnimationsToGo() {
      return this.animationsToGo;
   }

   public void setAnimationsToGo(int p_190915_1_) {
      this.animationsToGo = p_190915_1_;
   }

   public int getCount() {
      return this.isEmpty ? 0 : this.count;
   }

   public void setCount(int p_190920_1_) {
      this.count = p_190920_1_;
      this.updateEmptyState();
   }

   public void grow(int p_190917_1_) {
      this.setCount(this.count + p_190917_1_);
   }

   public void shrink(int p_190918_1_) {
      this.grow(-p_190918_1_);
   }
}
