package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemWrittenBook extends Item {
   public ItemWrittenBook(Item.Properties p_i48454_1_) {
      super(p_i48454_1_);
   }

   public static boolean validBookTagContents(@Nullable NBTTagCompound p_77828_0_) {
      if (!ItemWritableBook.isNBTValid(p_77828_0_)) {
         return false;
      } else if (!p_77828_0_.hasKey("title", 8)) {
         return false;
      } else {
         String s = p_77828_0_.getString("title");
         return s.length() <= 32 && p_77828_0_.hasKey("author", 8);
      }
   }

   public static int getGeneration(ItemStack p_179230_0_) {
      return p_179230_0_.getTag().getInteger("generation");
   }

   public ITextComponent getDisplayName(ItemStack p_200295_1_) {
      if (p_200295_1_.hasTag()) {
         NBTTagCompound nbttagcompound = p_200295_1_.getTag();
         String s = nbttagcompound.getString("title");
         if (!StringUtils.isNullOrEmpty(s)) {
            return new TextComponentString(s);
         }
      }

      return super.getDisplayName(p_200295_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      if (p_77624_1_.hasTag()) {
         NBTTagCompound nbttagcompound = p_77624_1_.getTag();
         String s = nbttagcompound.getString("author");
         if (!StringUtils.isNullOrEmpty(s)) {
            p_77624_3_.add((new TextComponentTranslation("book.byAuthor", s)).applyTextStyle(TextFormatting.GRAY));
         }

         p_77624_3_.add((new TextComponentTranslation("book.generation." + nbttagcompound.getInteger("generation"))).applyTextStyle(TextFormatting.GRAY));
      }

   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      if (!p_77659_1_.isRemote) {
         this.resolveContents(itemstack, p_77659_2_);
      }

      p_77659_2_.openBook(itemstack, p_77659_3_);
      p_77659_2_.func_71029_a(StatList.ITEM_USED.func_199076_b(this));
      return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
   }

   private void resolveContents(ItemStack p_179229_1_, EntityPlayer p_179229_2_) {
      NBTTagCompound nbttagcompound = p_179229_1_.getTag();
      if (nbttagcompound != null && !nbttagcompound.getBoolean("resolved")) {
         nbttagcompound.setBoolean("resolved", true);
         if (validBookTagContents(nbttagcompound)) {
            NBTTagList nbttaglist = nbttagcompound.getTagList("pages", 8);

            for(int i = 0; i < nbttaglist.size(); ++i) {
               String s = nbttaglist.getStringTagAt(i);

               ITextComponent itextcomponent;
               try {
                  itextcomponent = ITextComponent.Serializer.fromJsonLenient(s);
                  itextcomponent = TextComponentUtils.func_197680_a(p_179229_2_.getCommandSource(), itextcomponent, p_179229_2_);
               } catch (Exception var9) {
                  itextcomponent = new TextComponentString(s);
               }

               nbttaglist.set(i, new NBTTagString(ITextComponent.Serializer.toJson(itextcomponent)));
            }

            nbttagcompound.setTag("pages", nbttaglist);
            if (p_179229_2_ instanceof EntityPlayerMP && p_179229_2_.getHeldItemMainhand() == p_179229_1_) {
               Slot slot = p_179229_2_.openContainer.getSlotFromInventory(p_179229_2_.inventory, p_179229_2_.inventory.currentItem);
               ((EntityPlayerMP)p_179229_2_).connection.sendPacket(new SPacketSetSlot(0, slot.slotNumber, p_179229_1_));
            }

         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect(ItemStack p_77636_1_) {
      return true;
   }
}
