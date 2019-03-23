package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemHangingEntity extends Item {
   private final Class<? extends EntityHanging> hangingEntityClass;

   public ItemHangingEntity(Class<? extends EntityHanging> p_i48489_1_, Item.Properties p_i48489_2_) {
      super(p_i48489_2_);
      this.hangingEntityClass = p_i48489_1_;
   }

   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      BlockPos blockpos = p_195939_1_.getPos();
      EnumFacing enumfacing = p_195939_1_.getFace();
      BlockPos blockpos1 = blockpos.offset(enumfacing);
      EntityPlayer entityplayer = p_195939_1_.getPlayer();
      if (entityplayer != null && !this.canPlace(entityplayer, enumfacing, p_195939_1_.getItem(), blockpos1)) {
         return EnumActionResult.FAIL;
      } else {
         World world = p_195939_1_.getWorld();
         EntityHanging entityhanging = this.createEntity(world, blockpos1, enumfacing);
         if (entityhanging != null && entityhanging.onValidSurface()) {
            if (!world.isRemote) {
               entityhanging.playPlaceSound();
               world.spawnEntity(entityhanging);
            }

            p_195939_1_.getItem().shrink(1);
         }

         return EnumActionResult.SUCCESS;
      }
   }

   protected boolean canPlace(EntityPlayer p_200127_1_, EnumFacing p_200127_2_, ItemStack p_200127_3_, BlockPos p_200127_4_) {
      return !p_200127_2_.getAxis().isVertical() && p_200127_1_.canPlayerEdit(p_200127_4_, p_200127_2_, p_200127_3_);
   }

   @Nullable
   private EntityHanging createEntity(World p_179233_1_, BlockPos p_179233_2_, EnumFacing p_179233_3_) {
      if (this.hangingEntityClass == EntityPainting.class) {
         return new EntityPainting(p_179233_1_, p_179233_2_, p_179233_3_);
      } else {
         return this.hangingEntityClass == EntityItemFrame.class ? new EntityItemFrame(p_179233_1_, p_179233_2_, p_179233_3_) : null;
      }
   }
}
