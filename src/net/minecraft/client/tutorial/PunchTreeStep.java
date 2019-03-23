package net.minecraft.client.tutorial;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.toasts.TutorialToast;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PunchTreeStep implements ITutorialStep {
   private static final ITextComponent TITLE = new TextComponentTranslation("tutorial.punch_tree.title");
   private static final ITextComponent DESCRIPTION = new TextComponentTranslation("tutorial.punch_tree.description", Tutorial.createKeybindComponent("attack"));
   private final Tutorial tutorial;
   private TutorialToast toast;
   private int timeWaiting;
   private int resetCount;

   public PunchTreeStep(Tutorial p_i47579_1_) {
      this.tutorial = p_i47579_1_;
   }

   public void tick() {
      ++this.timeWaiting;
      if (this.tutorial.getGameType() != GameType.SURVIVAL) {
         this.tutorial.setStep(TutorialSteps.NONE);
      } else {
         if (this.timeWaiting == 1) {
            EntityPlayerSP entityplayersp = this.tutorial.getMinecraft().player;
            if (entityplayersp != null) {
               if (entityplayersp.inventory.hasTag(ItemTags.LOGS)) {
                  this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                  return;
               }

               if (FindTreeStep.hasPunchedTreesPreviously(entityplayersp)) {
                  this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                  return;
               }
            }
         }

         if ((this.timeWaiting >= 600 || this.resetCount > 3) && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.TREE, TITLE, DESCRIPTION, true);
            this.tutorial.getMinecraft().getToastGui().add(this.toast);
         }

      }
   }

   public void onStop() {
      if (this.toast != null) {
         this.toast.hide();
         this.toast = null;
      }

   }

   public void onHitBlock(WorldClient p_193250_1_, BlockPos p_193250_2_, IBlockState p_193250_3_, float p_193250_4_) {
      boolean flag = p_193250_3_.isIn(BlockTags.LOGS);
      if (flag && p_193250_4_ > 0.0F) {
         if (this.toast != null) {
            this.toast.setProgress(p_193250_4_);
         }

         if (p_193250_4_ >= 1.0F) {
            this.tutorial.setStep(TutorialSteps.OPEN_INVENTORY);
         }
      } else if (this.toast != null) {
         this.toast.setProgress(0.0F);
      } else if (flag) {
         ++this.resetCount;
      }

   }

   public void handleSetSlot(ItemStack p_193252_1_) {
      if (ItemTags.LOGS.contains(p_193252_1_.getItem())) {
         this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
      }
   }
}
