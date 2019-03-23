package net.minecraft.block;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class BlockSkullPlayer extends BlockSkull {
   protected BlockSkullPlayer(Block.Properties p_i48354_1_) {
      super(BlockSkull.Types.PLAYER, p_i48354_1_);
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, IBlockState p_180633_3_, @Nullable EntityLivingBase p_180633_4_, ItemStack p_180633_5_) {
      super.onBlockPlacedBy(p_180633_1_, p_180633_2_, p_180633_3_, p_180633_4_, p_180633_5_);
      TileEntity tileentity = p_180633_1_.getTileEntity(p_180633_2_);
      if (tileentity instanceof TileEntitySkull) {
         TileEntitySkull tileentityskull = (TileEntitySkull)tileentity;
         GameProfile gameprofile = null;
         if (p_180633_5_.hasTag()) {
            NBTTagCompound nbttagcompound = p_180633_5_.getTag();
            if (nbttagcompound.hasKey("SkullOwner", 10)) {
               gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
            } else if (nbttagcompound.hasKey("SkullOwner", 8) && !StringUtils.isBlank(nbttagcompound.getString("SkullOwner"))) {
               gameprofile = new GameProfile((UUID)null, nbttagcompound.getString("SkullOwner"));
            }
         }

         tileentityskull.setPlayerProfile(gameprofile);
      }

   }
}
