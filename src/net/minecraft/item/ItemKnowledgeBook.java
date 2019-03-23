package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemKnowledgeBook extends Item {
   private static final Logger LOGGER = LogManager.getLogger();

   public ItemKnowledgeBook(Item.Properties p_i48485_1_) {
      super(p_i48485_1_);
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      NBTTagCompound nbttagcompound = itemstack.getTag();
      if (!p_77659_2_.capabilities.isCreativeMode) {
         p_77659_2_.setHeldItem(p_77659_3_, ItemStack.EMPTY);
      }

      if (nbttagcompound != null && nbttagcompound.hasKey("Recipes", 9)) {
         if (!p_77659_1_.isRemote) {
            NBTTagList nbttaglist = nbttagcompound.getTagList("Recipes", 8);
            List<IRecipe> list = Lists.newArrayList();

            for(int i = 0; i < nbttaglist.size(); ++i) {
               String s = nbttaglist.getStringTagAt(i);
               IRecipe irecipe = p_77659_1_.getServer().getRecipeManager().getRecipe(new ResourceLocation(s));
               if (irecipe == null) {
                  LOGGER.error("Invalid recipe: {}", (Object)s);
                  return new ActionResult<>(EnumActionResult.FAIL, itemstack);
               }

               list.add(irecipe);
            }

            p_77659_2_.unlockRecipes(list);
            p_77659_2_.func_71029_a(StatList.ITEM_USED.func_199076_b(this));
         }

         return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
      } else {
         LOGGER.error("Tag not valid: {}", (Object)nbttagcompound);
         return new ActionResult<>(EnumActionResult.FAIL, itemstack);
      }
   }
}
