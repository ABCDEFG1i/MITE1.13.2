package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketRecipeInfo implements Packet<INetHandlerPlayServer> {
   private CPacketRecipeInfo.Purpose purpose;
   private ResourceLocation recipe;
   private boolean isGuiOpen;
   private boolean filteringCraftable;
   private boolean field_202498_e;
   private boolean field_202499_f;

   public CPacketRecipeInfo() {
   }

   public CPacketRecipeInfo(IRecipe p_i47518_1_) {
      this.purpose = CPacketRecipeInfo.Purpose.SHOWN;
      this.recipe = p_i47518_1_.getId();
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketRecipeInfo(boolean p_i48734_1_, boolean p_i48734_2_, boolean p_i48734_3_, boolean p_i48734_4_) {
      this.purpose = CPacketRecipeInfo.Purpose.SETTINGS;
      this.isGuiOpen = p_i48734_1_;
      this.filteringCraftable = p_i48734_2_;
      this.field_202498_e = p_i48734_3_;
      this.field_202499_f = p_i48734_4_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.purpose = p_148837_1_.readEnumValue(CPacketRecipeInfo.Purpose.class);
      if (this.purpose == CPacketRecipeInfo.Purpose.SHOWN) {
         this.recipe = p_148837_1_.readResourceLocation();
      } else if (this.purpose == CPacketRecipeInfo.Purpose.SETTINGS) {
         this.isGuiOpen = p_148837_1_.readBoolean();
         this.filteringCraftable = p_148837_1_.readBoolean();
         this.field_202498_e = p_148837_1_.readBoolean();
         this.field_202499_f = p_148837_1_.readBoolean();
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.purpose);
      if (this.purpose == CPacketRecipeInfo.Purpose.SHOWN) {
         p_148840_1_.writeResourceLocation(this.recipe);
      } else if (this.purpose == CPacketRecipeInfo.Purpose.SETTINGS) {
         p_148840_1_.writeBoolean(this.isGuiOpen);
         p_148840_1_.writeBoolean(this.filteringCraftable);
         p_148840_1_.writeBoolean(this.field_202498_e);
         p_148840_1_.writeBoolean(this.field_202499_f);
      }

   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.handleRecipeBookUpdate(this);
   }

   public CPacketRecipeInfo.Purpose getPurpose() {
      return this.purpose;
   }

   public ResourceLocation func_199619_b() {
      return this.recipe;
   }

   public boolean isGuiOpen() {
      return this.isGuiOpen;
   }

   public boolean isFilteringCraftable() {
      return this.filteringCraftable;
   }

   public boolean func_202496_e() {
      return this.field_202498_e;
   }

   public boolean func_202497_f() {
      return this.field_202499_f;
   }

   public enum Purpose {
      SHOWN,
      SETTINGS
   }
}
