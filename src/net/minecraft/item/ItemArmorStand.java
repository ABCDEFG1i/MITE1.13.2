package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Rotations;
import net.minecraft.world.World;

public class ItemArmorStand extends Item {
   public ItemArmorStand(Item.Properties p_i48532_1_) {
      super(p_i48532_1_);
   }

   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      EnumFacing enumfacing = p_195939_1_.getFace();
      if (enumfacing == EnumFacing.DOWN) {
         return EnumActionResult.FAIL;
      } else {
         World world = p_195939_1_.getWorld();
         BlockItemUseContext blockitemusecontext = new BlockItemUseContext(p_195939_1_);
         BlockPos blockpos = blockitemusecontext.getPos();
         BlockPos blockpos1 = blockpos.up();
         if (blockitemusecontext.func_196011_b() && world.getBlockState(blockpos1).isReplaceable(blockitemusecontext)) {
            double d0 = (double)blockpos.getX();
            double d1 = (double)blockpos.getY();
            double d2 = (double)blockpos.getZ();
            List<Entity> list = world.func_72839_b(null, new AxisAlignedBB(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));
            if (!list.isEmpty()) {
               return EnumActionResult.FAIL;
            } else {
               ItemStack itemstack = p_195939_1_.getItem();
               if (!world.isRemote) {
                  world.removeBlock(blockpos);
                  world.removeBlock(blockpos1);
                  EntityArmorStand entityarmorstand = new EntityArmorStand(world, d0 + 0.5D, d1, d2 + 0.5D);
                  float f = (float)MathHelper.floor((MathHelper.wrapDegrees(p_195939_1_.getPlacementYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                  entityarmorstand.setLocationAndAngles(d0 + 0.5D, d1, d2 + 0.5D, f, 0.0F);
                  this.applyRandomRotations(entityarmorstand, world.rand);
                  EntityType.func_208048_a(world, p_195939_1_.getPlayer(), entityarmorstand, itemstack.getTag());
                  world.spawnEntity(entityarmorstand);
                  world.playSound(null, entityarmorstand.posX, entityarmorstand.posY, entityarmorstand.posZ, SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
               }

               itemstack.shrink(1);
               return EnumActionResult.SUCCESS;
            }
         } else {
            return EnumActionResult.FAIL;
         }
      }
   }

   private void applyRandomRotations(EntityArmorStand p_179221_1_, Random p_179221_2_) {
      Rotations rotations = p_179221_1_.getHeadRotation();
      float f = p_179221_2_.nextFloat() * 5.0F;
      float f1 = p_179221_2_.nextFloat() * 20.0F - 10.0F;
      Rotations rotations1 = new Rotations(rotations.getX() + f, rotations.getY() + f1, rotations.getZ());
      p_179221_1_.setHeadRotation(rotations1);
      rotations = p_179221_1_.getBodyRotation();
      f = p_179221_2_.nextFloat() * 10.0F - 5.0F;
      rotations1 = new Rotations(rotations.getX(), rotations.getY() + f, rotations.getZ());
      p_179221_1_.setBodyRotation(rotations1);
   }
}
