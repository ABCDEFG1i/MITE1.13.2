package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IntegratedPlayerList extends PlayerList {
   private NBTTagCompound hostPlayerData;

   public IntegratedPlayerList(IntegratedServer p_i1314_1_) {
      super(p_i1314_1_);
      this.setViewDistance(10);
   }

   protected void writePlayerData(EntityPlayerMP p_72391_1_) {
      if (p_72391_1_.getName().getString().equals(this.getServerInstance().getServerOwner())) {
         this.hostPlayerData = p_72391_1_.writeToNBT(new NBTTagCompound());
      }

      super.writePlayerData(p_72391_1_);
   }

   public ITextComponent func_206258_a(SocketAddress p_206258_1_, GameProfile p_206258_2_) {
      return p_206258_2_.getName().equalsIgnoreCase(this.getServerInstance().getServerOwner()) && this.getPlayerByUsername(p_206258_2_.getName()) != null ? new TextComponentTranslation("multiplayer.disconnect.name_taken") : super.func_206258_a(p_206258_1_, p_206258_2_);
   }

   public IntegratedServer getServerInstance() {
      return (IntegratedServer)super.getServerInstance();
   }

   public NBTTagCompound getHostPlayerData() {
      return this.hostPlayerData;
   }
}
