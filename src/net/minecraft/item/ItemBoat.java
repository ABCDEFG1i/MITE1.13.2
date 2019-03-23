package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemBoat extends Item {
   private final EntityBoat.Type type;

   public ItemBoat(EntityBoat.Type p_i48526_1_, Item.Properties p_i48526_2_) {
      super(p_i48526_2_);
      this.type = p_i48526_1_;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      float f = 1.0F;
      float f1 = p_77659_2_.prevRotationPitch + (p_77659_2_.rotationPitch - p_77659_2_.prevRotationPitch) * 1.0F;
      float f2 = p_77659_2_.prevRotationYaw + (p_77659_2_.rotationYaw - p_77659_2_.prevRotationYaw) * 1.0F;
      double d0 = p_77659_2_.prevPosX + (p_77659_2_.posX - p_77659_2_.prevPosX) * 1.0D;
      double d1 = p_77659_2_.prevPosY + (p_77659_2_.posY - p_77659_2_.prevPosY) * 1.0D + (double)p_77659_2_.getEyeHeight();
      double d2 = p_77659_2_.prevPosZ + (p_77659_2_.posZ - p_77659_2_.prevPosZ) * 1.0D;
      Vec3d vec3d = new Vec3d(d0, d1, d2);
      float f3 = MathHelper.cos(-f2 * ((float)Math.PI / 180F) - (float)Math.PI);
      float f4 = MathHelper.sin(-f2 * ((float)Math.PI / 180F) - (float)Math.PI);
      float f5 = -MathHelper.cos(-f1 * ((float)Math.PI / 180F));
      float f6 = MathHelper.sin(-f1 * ((float)Math.PI / 180F));
      float f7 = f4 * f5;
      float f8 = f3 * f5;
      double d3 = 5.0D;
      Vec3d vec3d1 = vec3d.add((double)f7 * 5.0D, (double)f6 * 5.0D, (double)f8 * 5.0D);
      RayTraceResult raytraceresult = p_77659_1_.rayTraceBlocks(vec3d, vec3d1, RayTraceFluidMode.ALWAYS);
      if (raytraceresult == null) {
         return new ActionResult<>(EnumActionResult.PASS, itemstack);
      } else {
         Vec3d vec3d2 = p_77659_2_.getLook(1.0F);
         boolean flag = false;
         List<Entity> list = p_77659_1_.func_72839_b(p_77659_2_, p_77659_2_.getEntityBoundingBox().expand(vec3d2.x * 5.0D, vec3d2.y * 5.0D, vec3d2.z * 5.0D).grow(1.0D));

         for(int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (entity.canBeCollidedWith()) {
               AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow((double)entity.getCollisionBorderSize());
               if (axisalignedbb.contains(vec3d)) {
                  flag = true;
               }
            }
         }

         if (flag) {
            return new ActionResult<>(EnumActionResult.PASS, itemstack);
         } else if (raytraceresult.type == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = raytraceresult.getBlockPos();
            Block block = p_77659_1_.getBlockState(blockpos).getBlock();
            EntityBoat entityboat = new EntityBoat(p_77659_1_, raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
            entityboat.setBoatType(this.type);
            entityboat.rotationYaw = p_77659_2_.rotationYaw;
            if (!p_77659_1_.isCollisionBoxesEmpty(entityboat, entityboat.getEntityBoundingBox().grow(-0.1D))) {
               return new ActionResult<>(EnumActionResult.FAIL, itemstack);
            } else {
               if (!p_77659_1_.isRemote) {
                  p_77659_1_.spawnEntity(entityboat);
               }

               if (!p_77659_2_.capabilities.isCreativeMode) {
                  itemstack.shrink(1);
               }

               p_77659_2_.func_71029_a(StatList.ITEM_USED.func_199076_b(this));
               return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
            }
         } else {
            return new ActionResult<>(EnumActionResult.PASS, itemstack);
         }
      }
   }
}
