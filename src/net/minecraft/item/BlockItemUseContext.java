package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockItemUseContext extends ItemUseContext {
   private final BlockPos field_196014_j;
   protected boolean field_196013_a = true;

   public BlockItemUseContext(ItemUseContext p_i47813_1_) {
      this(p_i47813_1_.getWorld(), p_i47813_1_.getPlayer(), p_i47813_1_.getItem(), p_i47813_1_.getPos(), p_i47813_1_.getFace(), p_i47813_1_.getHitX(), p_i47813_1_.getHitY(), p_i47813_1_.getHitZ());
   }

   public BlockItemUseContext(World p_i47814_1_, @Nullable EntityPlayer p_i47814_2_, ItemStack p_i47814_3_, BlockPos p_i47814_4_, EnumFacing p_i47814_5_, float p_i47814_6_, float p_i47814_7_, float p_i47814_8_) {
      super(p_i47814_1_, p_i47814_2_, p_i47814_3_, p_i47814_4_, p_i47814_5_, p_i47814_6_, p_i47814_7_, p_i47814_8_);
      this.field_196014_j = this.pos.offset(this.face);
      this.field_196013_a = this.getWorld().getBlockState(this.pos).isReplaceable(this);
   }

   public BlockPos getPos() {
      return this.field_196013_a ? this.pos : this.field_196014_j;
   }

   public boolean func_196011_b() {
      return this.field_196013_a || this.getWorld().getBlockState(this.getPos()).isReplaceable(this);
   }

   public boolean func_196012_c() {
      return this.field_196013_a;
   }

   public EnumFacing func_196010_d() {
      return EnumFacing.getFacingDirections(this.player)[0];
   }

   public EnumFacing[] func_196009_e() {
      EnumFacing[] aenumfacing = EnumFacing.getFacingDirections(this.player);
      if (this.field_196013_a) {
         return aenumfacing;
      } else {
         int i;
         for(i = 0; i < aenumfacing.length && aenumfacing[i] != this.face.getOpposite(); ++i) {
            ;
         }

         if (i > 0) {
            System.arraycopy(aenumfacing, 0, aenumfacing, 1, i);
            aenumfacing[0] = this.face.getOpposite();
         }

         return aenumfacing;
      }
   }
}
