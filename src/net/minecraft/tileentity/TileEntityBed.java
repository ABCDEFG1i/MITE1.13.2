package net.minecraft.tileentity;

import net.minecraft.block.BlockBed;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityBed extends TileEntity {
   private EnumDyeColor color;

   public TileEntityBed() {
      super(TileEntityType.BED);
   }

   public TileEntityBed(EnumDyeColor p_i47730_1_) {
      this();
      this.setColor(p_i47730_1_);
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 11, this.getUpdateTag());
   }

   @OnlyIn(Dist.CLIENT)
   public EnumDyeColor getColor() {
      if (this.color == null) {
         this.color = ((BlockBed)this.getBlockState().getBlock()).getColor();
      }

      return this.color;
   }

   public void setColor(EnumDyeColor p_193052_1_) {
      this.color = p_193052_1_;
   }
}
