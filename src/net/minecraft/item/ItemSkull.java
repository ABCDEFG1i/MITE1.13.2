package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.lang3.StringUtils;

public class ItemSkull extends ItemWallOrFloor {
   public ItemSkull(Block p_i48477_1_, Block p_i48477_2_, Item.Properties p_i48477_3_) {
      super(p_i48477_1_, p_i48477_2_, p_i48477_3_);
   }

   public ITextComponent getDisplayName(ItemStack p_200295_1_) {
      if (p_200295_1_.getItem() == Items.PLAYER_HEAD && p_200295_1_.hasTag()) {
         String s = null;
         NBTTagCompound nbttagcompound = p_200295_1_.getTag();
         if (nbttagcompound.hasKey("SkullOwner", 8)) {
            s = nbttagcompound.getString("SkullOwner");
         } else if (nbttagcompound.hasKey("SkullOwner", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("SkullOwner");
            if (nbttagcompound1.hasKey("Name", 8)) {
               s = nbttagcompound1.getString("Name");
            }
         }

         if (s != null) {
            return new TextComponentTranslation(this.getTranslationKey() + ".named", s);
         }
      }

      return super.getDisplayName(p_200295_1_);
   }

   public boolean updateItemStackNBT(NBTTagCompound p_179215_1_) {
      super.updateItemStackNBT(p_179215_1_);
      if (p_179215_1_.hasKey("SkullOwner", 8) && !StringUtils.isBlank(p_179215_1_.getString("SkullOwner"))) {
         GameProfile gameprofile = new GameProfile((UUID)null, p_179215_1_.getString("SkullOwner"));
         gameprofile = TileEntitySkull.updateGameProfile(gameprofile);
         p_179215_1_.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
         return true;
      } else {
         return false;
      }
   }
}
