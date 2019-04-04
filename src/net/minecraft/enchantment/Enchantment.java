package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Enchantment {
   private final EntityEquipmentSlot[] applicableEquipmentTypes;
   private final Enchantment.Rarity rarity;
   @Nullable
   public EnumEnchantmentType type;
   @Nullable
   protected String name;

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static Enchantment getEnchantmentByID(int p_185262_0_) {
      return IRegistry.field_212628_q.func_148754_a(p_185262_0_);
   }

   protected Enchantment(Enchantment.Rarity p_i46731_1_, EnumEnchantmentType p_i46731_2_, EntityEquipmentSlot[] p_i46731_3_) {
      this.rarity = p_i46731_1_;
      this.type = p_i46731_2_;
      this.applicableEquipmentTypes = p_i46731_3_;
   }

   public List<ItemStack> getEntityEquipment(EntityLivingBase p_185260_1_) {
      List<ItemStack> list = Lists.newArrayList();

      for(EntityEquipmentSlot entityequipmentslot : this.applicableEquipmentTypes) {
         ItemStack itemstack = p_185260_1_.getItemStackFromSlot(entityequipmentslot);
         if (!itemstack.isEmpty()) {
            list.add(itemstack);
         }
      }

      return list;
   }

   public Enchantment.Rarity getRarity() {
      return this.rarity;
   }

   public int getMinLevel() {
      return 1;
   }

   public int getMaxLevel() {
      return 1;
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 1 + p_77321_1_ * 10;
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return this.getMinEnchantability(p_77317_1_) + 5;
   }

   public int calcModifierDamage(int p_77318_1_, DamageSource p_77318_2_) {
      return 0;
   }

   public float calcDamageByCreature(int p_152376_1_, CreatureAttribute p_152376_2_) {
      return 0.0F;
   }

   public final boolean isCompatibleWith(Enchantment p_191560_1_) {
      return this.canApplyTogether(p_191560_1_) && p_191560_1_.canApplyTogether(this);
   }

   protected boolean canApplyTogether(Enchantment p_77326_1_) {
      return this != p_77326_1_;
   }

   protected String getDefaultTranslationKey() {
      if (this.name == null) {
         this.name = Util.makeTranslationKey("enchantment", IRegistry.field_212628_q.func_177774_c(this));
      }

      return this.name;
   }

   public String getName() {
      return this.getDefaultTranslationKey();
   }

   public ITextComponent func_200305_d(int p_200305_1_) {
      ITextComponent itextcomponent = new TextComponentTranslation(this.getName());
      if (this.isCurse()) {
         itextcomponent.applyTextStyle(TextFormatting.RED);
      } else {
         itextcomponent.applyTextStyle(TextFormatting.GRAY);
      }

      if (p_200305_1_ != 1 || this.getMaxLevel() != 1) {
         itextcomponent.appendText(" ").appendSibling(new TextComponentTranslation("enchantment.level." + p_200305_1_));
      }

      return itextcomponent;
   }

   public boolean canApply(ItemStack p_92089_1_) {
      return this.type.canEnchantItem(p_92089_1_.getItem());
   }

   public void onEntityDamaged(EntityLivingBase p_151368_1_, Entity p_151368_2_, int p_151368_3_) {
   }

   public void onUserHurt(EntityLivingBase p_151367_1_, Entity p_151367_2_, int p_151367_3_) {
   }

   public boolean isTreasureEnchantment() {
      return false;
   }

   public boolean isCurse() {
      return false;
   }

   public static void registerEnchantments() {
      EntityEquipmentSlot[] aentityequipmentslot = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
      registerEnchantment("protection", new EnchantmentProtection(Enchantment.Rarity.COMMON, EnchantmentProtection.Type.ALL, aentityequipmentslot));
      registerEnchantment("fire_protection", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.Type.FIRE, aentityequipmentslot));
      registerEnchantment("feather_falling", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.Type.FALL, aentityequipmentslot));
      registerEnchantment("blast_protection", new EnchantmentProtection(Enchantment.Rarity.RARE, EnchantmentProtection.Type.EXPLOSION, aentityequipmentslot));
      registerEnchantment("projectile_protection", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.Type.PROJECTILE, aentityequipmentslot));
      registerEnchantment("respiration", new EnchantmentOxygen(Enchantment.Rarity.RARE, aentityequipmentslot));
      registerEnchantment("aqua_affinity", new EnchantmentWaterWorker(Enchantment.Rarity.RARE, aentityequipmentslot));
      registerEnchantment("thorns", new EnchantmentThorns(Enchantment.Rarity.VERY_RARE, aentityequipmentslot));
      registerEnchantment("depth_strider", new EnchantmentWaterWalker(Enchantment.Rarity.RARE, aentityequipmentslot));
      registerEnchantment("frost_walker", new EnchantmentFrostWalker(Enchantment.Rarity.RARE, EntityEquipmentSlot.FEET));
      registerEnchantment("binding_curse", new EnchantmentBindingCurse(Enchantment.Rarity.VERY_RARE, aentityequipmentslot));
      registerEnchantment("sharpness", new EnchantmentDamage(Enchantment.Rarity.COMMON, 0, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("smite", new EnchantmentDamage(Enchantment.Rarity.UNCOMMON, 1, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("bane_of_arthropods", new EnchantmentDamage(Enchantment.Rarity.UNCOMMON, 2, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("knockback", new EnchantmentKnockback(Enchantment.Rarity.UNCOMMON, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("fire_aspect", new EnchantmentFireAspect(Enchantment.Rarity.RARE, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("looting", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("sweeping", new EnchantmentSweepingEdge(Enchantment.Rarity.RARE, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("efficiency", new EnchantmentDigging(Enchantment.Rarity.COMMON, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("silk_touch", new EnchantmentUntouching(Enchantment.Rarity.VERY_RARE, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("unbreaking", new EnchantmentDurability(Enchantment.Rarity.UNCOMMON, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("fortune", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnumEnchantmentType.DIGGER, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("power", new EnchantmentArrowDamage(Enchantment.Rarity.COMMON, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("punch", new EnchantmentArrowKnockback(Enchantment.Rarity.RARE, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("flame", new EnchantmentArrowFire(Enchantment.Rarity.RARE, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("infinity", new EnchantmentArrowInfinite(Enchantment.Rarity.VERY_RARE, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("luck_of_the_sea", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnumEnchantmentType.FISHING_ROD, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("lure", new EnchantmentFishingSpeed(Enchantment.Rarity.RARE, EnumEnchantmentType.FISHING_ROD, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("loyalty", new EnchantmentLoyalty(Enchantment.Rarity.UNCOMMON, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("impaling", new EnchantmentImpaling(Enchantment.Rarity.RARE, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("riptide", new EnchantmentRiptide(Enchantment.Rarity.RARE, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("channeling", new EnchantmentChanneling(Enchantment.Rarity.VERY_RARE, EntityEquipmentSlot.MAINHAND));
      registerEnchantment("mending", new EnchantmentMending(Enchantment.Rarity.RARE, EntityEquipmentSlot.values()));
      registerEnchantment("vanishing_curse", new EnchantmentVanishingCurse(Enchantment.Rarity.VERY_RARE, EntityEquipmentSlot.values()));
   }

   private static void registerEnchantment(String p_210770_0_, Enchantment p_210770_1_) {
      IRegistry.field_212628_q.func_82595_a(new ResourceLocation(p_210770_0_), p_210770_1_);
   }

   public enum Rarity {
      COMMON(10),
      UNCOMMON(5),
      RARE(2),
      VERY_RARE(1);

      private final int weight;

      Rarity(int p_i47026_3_) {
         this.weight = p_i47026_3_;
      }

      public int getWeight() {
         return this.weight;
      }
   }
}
