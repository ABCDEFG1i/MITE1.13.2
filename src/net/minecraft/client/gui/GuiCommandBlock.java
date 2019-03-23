package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CPacketUpdateCommandBlock;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiCommandBlock extends GuiCommandBlockBase {
   private final TileEntityCommandBlock commandBlock;
   private GuiButton modeBtn;
   private GuiButton conditionalBtn;
   private GuiButton autoExecBtn;
   private TileEntityCommandBlock.Mode commandBlockMode = TileEntityCommandBlock.Mode.REDSTONE;
   private boolean conditional;
   private boolean automatic;

   public GuiCommandBlock(TileEntityCommandBlock p_i46596_1_) {
      this.commandBlock = p_i46596_1_;
   }

   CommandBlockBaseLogic func_195231_h() {
      return this.commandBlock.getCommandBlockLogic();
   }

   int func_195236_i() {
      return 135;
   }

   protected void initGui() {
      super.initGui();
      this.modeBtn = this.addButton(new GuiButton(5, this.width / 2 - 50 - 100 - 4, 165, 100, 20, I18n.format("advMode.mode.sequence")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiCommandBlock.this.nextMode();
            GuiCommandBlock.this.updateMode();
         }
      });
      this.conditionalBtn = this.addButton(new GuiButton(6, this.width / 2 - 50, 165, 100, 20, I18n.format("advMode.mode.unconditional")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiCommandBlock.this.conditional = !GuiCommandBlock.this.conditional;
            GuiCommandBlock.this.updateConditional();
         }
      });
      this.autoExecBtn = this.addButton(new GuiButton(7, this.width / 2 + 50 + 4, 165, 100, 20, I18n.format("advMode.mode.redstoneTriggered")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiCommandBlock.this.automatic = !GuiCommandBlock.this.automatic;
            GuiCommandBlock.this.updateAutoExec();
         }
      });
      this.field_195240_g.enabled = false;
      this.field_195242_i.enabled = false;
      this.modeBtn.enabled = false;
      this.conditionalBtn.enabled = false;
      this.autoExecBtn.enabled = false;
   }

   public void updateGui() {
      CommandBlockBaseLogic commandblockbaselogic = this.commandBlock.getCommandBlockLogic();
      this.commandTextField.setText(commandblockbaselogic.getCommand());
      this.field_195238_s = commandblockbaselogic.shouldTrackOutput();
      this.commandBlockMode = this.commandBlock.getMode();
      this.conditional = this.commandBlock.isConditional();
      this.automatic = this.commandBlock.isAuto();
      this.func_195233_j();
      this.updateMode();
      this.updateConditional();
      this.updateAutoExec();
      this.field_195240_g.enabled = true;
      this.field_195242_i.enabled = true;
      this.modeBtn.enabled = true;
      this.conditionalBtn.enabled = true;
      this.autoExecBtn.enabled = true;
   }

   public void onResize(Minecraft p_175273_1_, int p_175273_2_, int p_175273_3_) {
      super.onResize(p_175273_1_, p_175273_2_, p_175273_3_);
      this.func_195233_j();
      this.updateMode();
      this.updateConditional();
      this.updateAutoExec();
      this.field_195240_g.enabled = true;
      this.field_195242_i.enabled = true;
      this.modeBtn.enabled = true;
      this.conditionalBtn.enabled = true;
      this.autoExecBtn.enabled = true;
   }

   protected void func_195235_a(CommandBlockBaseLogic p_195235_1_) {
      this.mc.getConnection().sendPacket(new CPacketUpdateCommandBlock(new BlockPos(p_195235_1_.getPositionVector()), this.commandTextField.getText(), this.commandBlockMode, p_195235_1_.shouldTrackOutput(), this.conditional, this.automatic));
   }

   private void updateMode() {
      switch(this.commandBlockMode) {
      case SEQUENCE:
         this.modeBtn.displayString = I18n.format("advMode.mode.sequence");
         break;
      case AUTO:
         this.modeBtn.displayString = I18n.format("advMode.mode.auto");
         break;
      case REDSTONE:
         this.modeBtn.displayString = I18n.format("advMode.mode.redstone");
      }

   }

   private void nextMode() {
      switch(this.commandBlockMode) {
      case SEQUENCE:
         this.commandBlockMode = TileEntityCommandBlock.Mode.AUTO;
         break;
      case AUTO:
         this.commandBlockMode = TileEntityCommandBlock.Mode.REDSTONE;
         break;
      case REDSTONE:
         this.commandBlockMode = TileEntityCommandBlock.Mode.SEQUENCE;
      }

   }

   private void updateConditional() {
      if (this.conditional) {
         this.conditionalBtn.displayString = I18n.format("advMode.mode.conditional");
      } else {
         this.conditionalBtn.displayString = I18n.format("advMode.mode.unconditional");
      }

   }

   private void updateAutoExec() {
      if (this.automatic) {
         this.autoExecBtn.displayString = I18n.format("advMode.mode.autoexec.bat");
      } else {
         this.autoExecBtn.displayString = I18n.format("advMode.mode.redstoneTriggered");
      }

   }
}
