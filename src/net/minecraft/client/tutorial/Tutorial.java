package net.minecraft.client.tutorial;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentKeybind;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Tutorial {
   private final Minecraft minecraft;
   @Nullable
   private ITutorialStep tutorialStep;

   public Tutorial(Minecraft p_i47578_1_) {
      this.minecraft = p_i47578_1_;
   }

   public void handleMovement(MovementInput p_193293_1_) {
      if (this.tutorialStep != null) {
         this.tutorialStep.handleMovement(p_193293_1_);
      }

   }

   public void onMouseMove(double p_195872_1_, double p_195872_3_) {
      if (this.tutorialStep != null) {
         this.tutorialStep.onMouseMove(p_195872_1_, p_195872_3_);
      }

   }

   public void onMouseHover(@Nullable WorldClient p_193297_1_, @Nullable RayTraceResult p_193297_2_) {
      if (this.tutorialStep != null && p_193297_2_ != null && p_193297_1_ != null) {
         this.tutorialStep.onMouseHover(p_193297_1_, p_193297_2_);
      }

   }

   public void onHitBlock(WorldClient p_193294_1_, BlockPos p_193294_2_, IBlockState p_193294_3_, float p_193294_4_) {
      if (this.tutorialStep != null) {
         this.tutorialStep.onHitBlock(p_193294_1_, p_193294_2_, p_193294_3_, p_193294_4_);
      }

   }

   public void openInventory() {
      if (this.tutorialStep != null) {
         this.tutorialStep.openInventory();
      }

   }

   public void handleSetSlot(ItemStack p_193301_1_) {
      if (this.tutorialStep != null) {
         this.tutorialStep.handleSetSlot(p_193301_1_);
      }

   }

   public void stop() {
      if (this.tutorialStep != null) {
         this.tutorialStep.onStop();
         this.tutorialStep = null;
      }
   }

   public void reload() {
      if (this.tutorialStep != null) {
         this.stop();
      }

      this.tutorialStep = this.minecraft.gameSettings.tutorialStep.create(this);
   }

   public void tick() {
      if (this.tutorialStep != null) {
         if (this.minecraft.world != null) {
            this.tutorialStep.tick();
         } else {
            this.stop();
         }
      } else if (this.minecraft.world != null) {
         this.reload();
      }

   }

   public void setStep(TutorialSteps p_193292_1_) {
      this.minecraft.gameSettings.tutorialStep = p_193292_1_;
      this.minecraft.gameSettings.saveOptions();
      if (this.tutorialStep != null) {
         this.tutorialStep.onStop();
         this.tutorialStep = p_193292_1_.create(this);
      }

   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   public GameType getGameType() {
      return this.minecraft.playerController == null ? GameType.NOT_SET : this.minecraft.playerController.getCurrentGameType();
   }

   public static ITextComponent createKeybindComponent(String p_193291_0_) {
      return (new TextComponentKeybind("key." + p_193291_0_)).applyTextStyle(TextFormatting.BOLD);
   }
}
