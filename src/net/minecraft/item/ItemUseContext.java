package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemUseContext {
   protected final EntityPlayer player;
   protected final float hitX;
   protected final float hitY;
   protected final float hitZ;
   protected final EnumFacing face;
   protected final World world;
   protected final ItemStack item;
   protected final BlockPos pos;

   public ItemUseContext(EntityPlayer p_i47796_1_, ItemStack p_i47796_2_, BlockPos p_i47796_3_, EnumFacing p_i47796_4_, float p_i47796_5_, float p_i47796_6_, float p_i47796_7_) {
      this(p_i47796_1_.world, p_i47796_1_, p_i47796_2_, p_i47796_3_, p_i47796_4_, p_i47796_5_, p_i47796_6_, p_i47796_7_);
   }

   protected ItemUseContext(World p_i47797_1_, @Nullable EntityPlayer p_i47797_2_, ItemStack p_i47797_3_, BlockPos p_i47797_4_, EnumFacing p_i47797_5_, float p_i47797_6_, float p_i47797_7_, float p_i47797_8_) {
      this.player = p_i47797_2_;
      this.face = p_i47797_5_;
      this.hitX = p_i47797_6_;
      this.hitY = p_i47797_7_;
      this.hitZ = p_i47797_8_;
      this.pos = p_i47797_4_;
      this.item = p_i47797_3_;
      this.world = p_i47797_1_;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public ItemStack getItem() {
      return this.item;
   }

   @Nullable
   public EntityPlayer getPlayer() {
      return this.player;
   }

   public World getWorld() {
      return this.world;
   }

   public EnumFacing getFace() {
      return this.face;
   }

   public float getHitX() {
      return this.hitX;
   }

   public float getHitY() {
      return this.hitY;
   }

   public float getHitZ() {
      return this.hitZ;
   }

   public EnumFacing getPlacementHorizontalFacing() {
      return this.player == null ? EnumFacing.NORTH : this.player.getHorizontalFacing();
   }

   public boolean isPlacerSneaking() {
      return this.player != null && this.player.isSneaking();
   }

   public float getPlacementYaw() {
      return this.player == null ? 0.0F : this.player.rotationYaw;
   }
}
