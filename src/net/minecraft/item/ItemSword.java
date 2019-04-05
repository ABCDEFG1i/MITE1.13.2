package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSword extends ItemTiered {
   private final float attackDamage;
   private final float attackSpeed;

   public ItemSword(int maxUses,IItemTier p_i48460_1_, int p_i48460_2_, float p_i48460_3_, Item.Properties p_i48460_4_) {
      super(maxUses,p_i48460_1_, p_i48460_4_);
      this.attackSpeed = p_i48460_3_;
      this.attackDamage = (float)p_i48460_2_ + p_i48460_1_.getAttackDamage();
   }

   public float getAttackDamage() {
      return this.attackDamage;
   }

   public boolean canPlayerBreakBlockWhileHolding(IBlockState p_195938_1_, World p_195938_2_, BlockPos p_195938_3_, EntityPlayer p_195938_4_) {
      return !p_195938_4_.isCreative();
   }

   public float getDestroySpeed(ItemStack p_150893_1_, IBlockState p_150893_2_) {
      Block block = p_150893_2_.getBlock();
      if (block == Blocks.COBWEB) {
         return 15.0F;
      } else {
         Material material = p_150893_2_.getMaterial();
         return material != Material.PLANTS && material != Material.VINE && material != Material.CORAL && !p_150893_2_.isIn(BlockTags.LEAVES) && material != Material.GOURD ? 1.0F : 1.5F;
      }
   }

   public boolean hitEntity(ItemStack p_77644_1_, EntityLivingBase p_77644_2_, EntityLivingBase p_77644_3_) {
      p_77644_1_.damageItem(1, p_77644_3_);
      return true;
   }

   public boolean onBlockDestroyed(ItemStack p_179218_1_, World p_179218_2_, IBlockState p_179218_3_, BlockPos p_179218_4_, EntityLivingBase p_179218_5_) {
      if (p_179218_3_.getBlockHardness(p_179218_2_, p_179218_4_) != 0.0F) {
         p_179218_1_.damageItem(2, p_179218_5_);
      }

      return true;
   }

   public boolean canHarvestBlock(IBlockState p_150897_1_) {
      return p_150897_1_.getBlock() == Blocks.COBWEB;
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot p_111205_1_) {
      Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(p_111205_1_);
      if (p_111205_1_ == EntityEquipmentSlot.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)this.attackDamage, 0));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double)this.attackSpeed, 0));
      }

      return multimap;
   }
}
