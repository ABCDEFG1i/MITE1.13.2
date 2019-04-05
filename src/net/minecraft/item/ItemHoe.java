package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemHoe extends ItemTiered {
   private final float speed;
   protected static final Map<Block, IBlockState> field_195973_b = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.getDefaultState(), Blocks.GRASS_PATH, Blocks.FARMLAND.getDefaultState(), Blocks.DIRT, Blocks.FARMLAND.getDefaultState(), Blocks.COARSE_DIRT, Blocks.DIRT.getDefaultState()));

   public ItemHoe(int maxUses,IItemTier itemTier, float speed, Item.Properties properties) {
      super(maxUses,itemTier, properties);
      this.speed = speed;
   }

   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos();
      if (p_195939_1_.getFace() != EnumFacing.DOWN && world.getBlockState(blockpos.up()).isAir()) {
         IBlockState iblockstate = field_195973_b.get(world.getBlockState(blockpos).getBlock());
         if (iblockstate != null) {
            EntityPlayer entityplayer = p_195939_1_.getPlayer();
            world.playSound(entityplayer, blockpos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isRemote) {
               world.setBlockState(blockpos, iblockstate, 11);
               if (entityplayer != null) {
                  p_195939_1_.getItem().damageItem(1, entityplayer);
               }
            }

            return EnumActionResult.SUCCESS;
         }
      }

      return EnumActionResult.PASS;
   }

   public boolean hitEntity(ItemStack p_77644_1_, EntityLivingBase p_77644_2_, EntityLivingBase p_77644_3_) {
      p_77644_1_.damageItem(1, p_77644_3_);
      return true;
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot p_111205_1_) {
      Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(p_111205_1_);
      if (p_111205_1_ == EntityEquipmentSlot.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 0.0D, 0));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double)this.speed, 0));
      }

      return multimap;
   }
}
