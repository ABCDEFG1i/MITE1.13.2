package net.minecraft.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemRecord extends Item {
   private static final Map<SoundEvent, ItemRecord> RECORDS = Maps.newHashMap();
   private static final List<ItemRecord> field_195976_b = Lists.newArrayList();
   private final int comparatorValue;
   private final SoundEvent sound;

   protected ItemRecord(int p_i48475_1_, SoundEvent p_i48475_2_, Item.Properties p_i48475_3_) {
      super(p_i48475_3_);
      this.comparatorValue = p_i48475_1_;
      this.sound = p_i48475_2_;
      RECORDS.put(this.sound, this);
      field_195976_b.add(this);
   }

   public static ItemRecord getRandom(Random p_195974_0_) {
      return field_195976_b.get(p_195974_0_.nextInt(field_195976_b.size()));
   }

   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos();
      IBlockState iblockstate = world.getBlockState(blockpos);
      if (iblockstate.getBlock() == Blocks.JUKEBOX && !iblockstate.get(BlockJukebox.HAS_RECORD)) {
         ItemStack itemstack = p_195939_1_.getItem();
         if (!world.isRemote) {
            ((BlockJukebox)Blocks.JUKEBOX).insertRecord(world, blockpos, iblockstate, itemstack);
            world.playEvent((EntityPlayer)null, 1010, blockpos, Item.getIdFromItem(this));
            itemstack.shrink(1);
            EntityPlayer entityplayer = p_195939_1_.getPlayer();
            if (entityplayer != null) {
               entityplayer.addStat(StatList.PLAY_RECORD);
            }
         }

         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.PASS;
      }
   }

   public int getComparatorValue() {
      return this.comparatorValue;
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      p_77624_3_.add(this.getRecordDescription().applyTextStyle(TextFormatting.GRAY));
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getRecordDescription() {
      return new TextComponentTranslation(this.getTranslationKey() + ".desc");
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static ItemRecord getBySound(SoundEvent p_185074_0_) {
      return RECORDS.get(p_185074_0_);
   }

   @OnlyIn(Dist.CLIENT)
   public SoundEvent getSound() {
      return this.sound;
   }
}
