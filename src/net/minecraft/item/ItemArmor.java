package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemArmor extends Item {
   private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
   public static final IBehaviorDispenseItem DISPENSER_BEHAVIOR = new BehaviorDefaultDispenseItem() {
      protected ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
         ItemStack itemstack = ItemArmor.dispenseArmor(p_82487_1_, p_82487_2_);
         return itemstack.isEmpty() ? super.dispenseStack(p_82487_1_, p_82487_2_) : itemstack;
      }
   };
   protected final EntityEquipmentSlot armorType;
   protected final int damageReduceAmount;
   protected final float toughness;
   protected final IArmorMaterial material;

   public static ItemStack dispenseArmor(IBlockSource p_185082_0_, ItemStack p_185082_1_) {
      BlockPos blockpos = p_185082_0_.getBlockPos().offset(p_185082_0_.getBlockState().get(BlockDispenser.FACING));
      List<EntityLivingBase> list = p_185082_0_.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(blockpos), EntitySelectors.NOT_SPECTATING.and(new EntitySelectors.ArmoredMob(p_185082_1_)));
      if (list.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         EntityLivingBase entitylivingbase = list.get(0);
         EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(p_185082_1_);
         ItemStack itemstack = p_185082_1_.split(1);
         entitylivingbase.setItemStackToSlot(entityequipmentslot, itemstack);
         if (entitylivingbase instanceof EntityLiving) {
            ((EntityLiving)entitylivingbase).setDropChance(entityequipmentslot, 2.0F);
            ((EntityLiving)entitylivingbase).enablePersistence();
         }

         return p_185082_1_;
      }
   }

   public ItemArmor(IArmorMaterial p_i48534_1_, EntityEquipmentSlot p_i48534_2_, Item.Properties p_i48534_3_) {
      super(p_i48534_3_.func_200915_b(p_i48534_1_.getDurability(p_i48534_2_)));
      this.material = p_i48534_1_;
      this.armorType = p_i48534_2_;
      this.damageReduceAmount = p_i48534_1_.getDamageReductionAmount(p_i48534_2_);
      this.toughness = p_i48534_1_.getToughness();
      BlockDispenser.registerDispenseBehavior(this, DISPENSER_BEHAVIOR);
   }

   public EntityEquipmentSlot getEquipmentSlot() {
      return this.armorType;
   }

   public int getItemEnchantability() {
      return this.material.getEnchantability();
   }

   public IArmorMaterial getArmorMaterial() {
      return this.material;
   }

   public boolean getIsRepairable(ItemStack p_82789_1_, ItemStack p_82789_2_) {
      return this.material.getRepairMaterial().test(p_82789_2_) || super.getIsRepairable(p_82789_1_, p_82789_2_);
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
      ItemStack itemstack1 = p_77659_2_.getItemStackFromSlot(entityequipmentslot);
      if (itemstack1.isEmpty()) {
         p_77659_2_.setItemStackToSlot(entityequipmentslot, itemstack.copy());
         itemstack.setCount(0);
         return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
      } else {
         return new ActionResult<>(EnumActionResult.FAIL, itemstack);
      }
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot p_111205_1_) {
      Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(p_111205_1_);
      if (p_111205_1_ == this.armorType) {
         multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIERS[p_111205_1_.getIndex()], "Armor modifier", (double)this.damageReduceAmount, 0));
         multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIERS[p_111205_1_.getIndex()], "Armor toughness", (double)this.toughness, 0));
      }

      return multimap;
   }

   public int getDamageReduceAmount() {
      return this.damageReduceAmount;
   }
}
