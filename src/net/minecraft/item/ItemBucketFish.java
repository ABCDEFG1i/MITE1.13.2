package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractFish;
import net.minecraft.entity.passive.EntityTropicalFish;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemBucketFish extends ItemBucket {
   private final EntityType<?> fishType;

   public ItemBucketFish(EntityType<?> p_i49022_1_, Fluid p_i49022_2_, Item.Properties p_i49022_3_) {
      super(p_i49022_2_, p_i49022_3_);
      this.fishType = p_i49022_1_;
   }

   public void onLiquidPlaced(World p_203792_1_, ItemStack p_203792_2_, BlockPos p_203792_3_) {
      if (!p_203792_1_.isRemote) {
         this.placeFish(p_203792_1_, p_203792_2_, p_203792_3_);
      }

   }

   protected void playEmptySound(@Nullable EntityPlayer p_203791_1_, IWorld p_203791_2_, BlockPos p_203791_3_) {
      p_203791_2_.playSound(p_203791_1_, p_203791_3_, SoundEvents.ITEM_BUCKET_EMPTY_FISH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
   }

   private void placeFish(World p_205357_1_, ItemStack p_205357_2_, BlockPos p_205357_3_) {
      Entity entity = this.fishType.spawnEntity(p_205357_1_, p_205357_2_, null, p_205357_3_, true, false);
      if (entity != null) {
         ((AbstractFish)entity).setFromBucket(true);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      if (this.fishType == EntityType.TROPICAL_FISH) {
         NBTTagCompound nbttagcompound = p_77624_1_.getTag();
         if (nbttagcompound != null && nbttagcompound.hasKey("BucketVariantTag", 3)) {
            int i = nbttagcompound.getInteger("BucketVariantTag");
            TextFormatting[] atextformatting = new TextFormatting[]{TextFormatting.ITALIC, TextFormatting.GRAY};
            String s = "color.minecraft." + EntityTropicalFish.func_212326_d(i);
            String s1 = "color.minecraft." + EntityTropicalFish.func_212323_p(i);

            for(int j = 0; j < EntityTropicalFish.SPECIAL_VARIANTS.length; ++j) {
               if (i == EntityTropicalFish.SPECIAL_VARIANTS[j]) {
                  p_77624_3_.add((new TextComponentTranslation(EntityTropicalFish.func_212324_b(j))).applyTextStyles(atextformatting));
                  return;
               }
            }

            p_77624_3_.add((new TextComponentTranslation(EntityTropicalFish.func_212327_q(i))).applyTextStyles(atextformatting));
            ITextComponent itextcomponent = new TextComponentTranslation(s);
            if (!s.equals(s1)) {
               itextcomponent.appendText(", ").appendSibling(new TextComponentTranslation(s1));
            }

            itextcomponent.applyTextStyles(atextformatting);
            p_77624_3_.add(itextcomponent);
         }
      }

   }
}
