package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

public class ItemTool extends ItemTiered {
   private final Set<Block> effectiveBlocks;
   protected float efficiency;
   protected float attackDamage;
   protected float attackSpeed;

   protected ItemTool(int maxUses,float attackDamage, float attackSpeed, IItemTier itemTier, Set<Block> effectiveBlocks, Item.Properties p_i48512_5_) {
      super(maxUses,itemTier, p_i48512_5_);
      this.effectiveBlocks = effectiveBlocks;
      this.efficiency = itemTier.getEfficiency();
      this.attackDamage = attackDamage + itemTier.getAttackDamage();
      this.attackSpeed = attackSpeed;
   }

   public float getDestroySpeed(ItemStack p_150893_1_, IBlockState p_150893_2_) {
      return this.effectiveBlocks.contains(p_150893_2_.getBlock()) ? this.efficiency : 1.0F;
   }

   public boolean hitEntity(ItemStack p_77644_1_, EntityLivingBase p_77644_2_, EntityLivingBase p_77644_3_) {
      //MITEMODDED
      p_77644_1_.damageItem(Math.round(this.attackDamage)*2, p_77644_3_);
      return true;
   }

   public boolean onBlockDestroyed(ItemStack p_179218_1_, World p_179218_2_, IBlockState p_179218_3_, BlockPos p_179218_4_, EntityLivingBase p_179218_5_) {
       if (!p_179218_2_.isRemote && p_179218_3_.getBlockHardness(p_179218_2_, p_179218_4_) != 0.0F) {
           if (!p_179218_3_.getMaterial().isToolNotRequired()) {

               p_179218_1_.damageItem(Math.round(p_179218_3_.getBlockHardness(p_179218_2_, p_179218_4_) * 100F), p_179218_5_);
           } else {
               if (effectiveBlocks.contains(p_179218_3_.getBlock())) {
                   p_179218_1_.damageItem(Math.round(p_179218_3_.getBlockHardness(p_179218_2_, p_179218_4_) * 40F),
                           p_179218_5_);
               } else {
                   p_179218_1_.damageItem(Math.round(p_179218_3_.getBlockHardness(p_179218_2_, p_179218_4_) * 10F),
                           p_179218_5_);
               }
           }
       }
      return true;
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot p_111205_1_) {
      Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(p_111205_1_);
      if (p_111205_1_ == EntityEquipmentSlot.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", (double)this.attackDamage, 0));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double)this.attackSpeed, 0));
      }

      return multimap;
   }
}
