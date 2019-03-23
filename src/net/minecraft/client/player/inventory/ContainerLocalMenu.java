package net.minecraft.client.player.inventory;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ContainerLocalMenu extends InventoryBasic implements ILockableContainer {
   private final String guiID;
   private final Map<Integer, Integer> dataValues = Maps.newHashMap();

   public ContainerLocalMenu(String p_i46276_1_, ITextComponent p_i46276_2_, int p_i46276_3_) {
      super(p_i46276_2_, p_i46276_3_);
      this.guiID = p_i46276_1_;
   }

   public int getField(int p_174887_1_) {
      return this.dataValues.containsKey(p_174887_1_) ? this.dataValues.get(p_174887_1_) : 0;
   }

   public void setField(int p_174885_1_, int p_174885_2_) {
      this.dataValues.put(p_174885_1_, p_174885_2_);
   }

   public int getFieldCount() {
      return this.dataValues.size();
   }

   public boolean isLocked() {
      return false;
   }

   public void setLockCode(LockCode p_174892_1_) {
   }

   public LockCode getLockCode() {
      return LockCode.EMPTY_CODE;
   }

   public String getGuiID() {
      return this.guiID;
   }

   public Container createContainer(InventoryPlayer p_174876_1_, EntityPlayer p_174876_2_) {
      throw new UnsupportedOperationException();
   }
}
