package net.minecraft.tileentity;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class TileEntityDropper extends TileEntityDispenser {
   public TileEntityDropper() {
      super(TileEntityType.DROPPER);
   }

   public ITextComponent getName() {
      ITextComponent itextcomponent = this.getCustomName();
      return itextcomponent != null ? itextcomponent : new TextComponentTranslation("container.dropper");
   }

   public String getGuiID() {
      return "minecraft:dropper";
   }
}
