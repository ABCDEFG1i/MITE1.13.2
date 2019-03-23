package net.minecraft.client.gui;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Random;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SharedConstants;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class GuiCreateWorld extends GuiScreen {
   private final GuiScreen parentScreen;
   private GuiTextField worldNameField;
   private GuiTextField worldSeedField;
   private String saveDirName;
   private String gameMode = "survival";
   private String savedGameMode;
   private boolean generateStructuresEnabled = true;
   private boolean allowCheats;
   private boolean allowCheatsWasSetByUser;
   private boolean bonusChestEnabled;
   private boolean hardCoreMode;
   private boolean alreadyGenerated;
   private boolean inMoreWorldOptionsDisplay;
   private GuiButton field_195355_B;
   private GuiButton btnGameMode;
   private GuiButton btnMoreOptions;
   private GuiButton btnMapFeatures;
   private GuiButton btnBonusItems;
   private GuiButton btnMapType;
   private GuiButton btnAllowCommands;
   private GuiButton btnCustomizeType;
   private String gameModeDesc1;
   private String gameModeDesc2;
   private String worldSeed;
   private String worldName;
   private int selectedIndex;
   public NBTTagCompound chunkProviderSettingsJson = new NBTTagCompound();
   private static final String[] DISALLOWED_FILENAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

   public GuiCreateWorld(GuiScreen p_i46320_1_) {
      this.parentScreen = p_i46320_1_;
      this.worldSeed = "";
      this.worldName = I18n.format("selectWorld.newWorld");
   }

   public void tick() {
      this.worldNameField.tick();
      this.worldSeedField.tick();
   }

   protected void initGui() {
      this.mc.keyboardListener.enableRepeatEvents(true);
      this.field_195355_B = this.addButton(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("selectWorld.create")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiCreateWorld.this.func_195352_j();
         }
      });
      this.addButton(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiCreateWorld.this.mc.displayGuiScreen(GuiCreateWorld.this.parentScreen);
         }
      });
      this.btnGameMode = this.addButton(new GuiButton(2, this.width / 2 - 75, 115, 150, 20, I18n.format("selectWorld.gameMode")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            if ("survival".equals(GuiCreateWorld.this.gameMode)) {
               if (!GuiCreateWorld.this.allowCheatsWasSetByUser) {
                  GuiCreateWorld.this.allowCheats = false;
               }

               GuiCreateWorld.this.hardCoreMode = false;
               GuiCreateWorld.this.gameMode = "hardcore";
               GuiCreateWorld.this.hardCoreMode = true;
               GuiCreateWorld.this.btnAllowCommands.enabled = false;
               GuiCreateWorld.this.btnBonusItems.enabled = false;
               GuiCreateWorld.this.updateDisplayState();
            } else if ("hardcore".equals(GuiCreateWorld.this.gameMode)) {
               if (!GuiCreateWorld.this.allowCheatsWasSetByUser) {
                  GuiCreateWorld.this.allowCheats = true;
               }

               GuiCreateWorld.this.hardCoreMode = false;
               GuiCreateWorld.this.gameMode = "creative";
               GuiCreateWorld.this.updateDisplayState();
               GuiCreateWorld.this.hardCoreMode = false;
               GuiCreateWorld.this.btnAllowCommands.enabled = true;
               GuiCreateWorld.this.btnBonusItems.enabled = true;
            } else {
               if (!GuiCreateWorld.this.allowCheatsWasSetByUser) {
                  GuiCreateWorld.this.allowCheats = false;
               }

               GuiCreateWorld.this.gameMode = "survival";
               GuiCreateWorld.this.updateDisplayState();
               GuiCreateWorld.this.btnAllowCommands.enabled = true;
               GuiCreateWorld.this.btnBonusItems.enabled = true;
               GuiCreateWorld.this.hardCoreMode = false;
            }

            GuiCreateWorld.this.updateDisplayState();
         }
      });
      this.btnMoreOptions = this.addButton(new GuiButton(3, this.width / 2 - 75, 187, 150, 20, I18n.format("selectWorld.moreWorldOptions")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiCreateWorld.this.toggleMoreWorldOptions();
         }
      });
      this.btnMapFeatures = this.addButton(new GuiButton(4, this.width / 2 - 155, 100, 150, 20, I18n.format("selectWorld.mapFeatures")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiCreateWorld.this.generateStructuresEnabled = !GuiCreateWorld.this.generateStructuresEnabled;
            GuiCreateWorld.this.updateDisplayState();
         }
      });
      this.btnMapFeatures.visible = false;
      this.btnBonusItems = this.addButton(new GuiButton(7, this.width / 2 + 5, 151, 150, 20, I18n.format("selectWorld.bonusItems")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiCreateWorld.this.bonusChestEnabled = !GuiCreateWorld.this.bonusChestEnabled;
            GuiCreateWorld.this.updateDisplayState();
         }
      });
      this.btnBonusItems.visible = false;
      this.btnMapType = this.addButton(new GuiButton(5, this.width / 2 + 5, 100, 150, 20, I18n.format("selectWorld.mapType")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiCreateWorld.this.selectedIndex++;
            if (GuiCreateWorld.this.selectedIndex >= WorldType.WORLD_TYPES.length) {
               GuiCreateWorld.this.selectedIndex = 0;
            }

            while(!GuiCreateWorld.this.canSelectCurWorldType()) {
               GuiCreateWorld.this.selectedIndex++;
               if (GuiCreateWorld.this.selectedIndex >= WorldType.WORLD_TYPES.length) {
                  GuiCreateWorld.this.selectedIndex = 0;
               }
            }

            GuiCreateWorld.this.chunkProviderSettingsJson = new NBTTagCompound();
            GuiCreateWorld.this.updateDisplayState();
            GuiCreateWorld.this.showMoreWorldOptions(GuiCreateWorld.this.inMoreWorldOptionsDisplay);
         }
      });
      this.btnMapType.visible = false;
      this.btnAllowCommands = this.addButton(new GuiButton(6, this.width / 2 - 155, 151, 150, 20, I18n.format("selectWorld.allowCommands")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiCreateWorld.this.allowCheatsWasSetByUser = true;
            GuiCreateWorld.this.allowCheats = !GuiCreateWorld.this.allowCheats;
            GuiCreateWorld.this.updateDisplayState();
         }
      });
      this.btnAllowCommands.visible = false;
      this.btnCustomizeType = this.addButton(new GuiButton(8, this.width / 2 + 5, 120, 150, 20, I18n.format("selectWorld.customizeType")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            if (WorldType.WORLD_TYPES[GuiCreateWorld.this.selectedIndex] == WorldType.FLAT) {
               GuiCreateWorld.this.mc.displayGuiScreen(new GuiCreateFlatWorld(GuiCreateWorld.this, GuiCreateWorld.this.chunkProviderSettingsJson));
            }

            if (WorldType.WORLD_TYPES[GuiCreateWorld.this.selectedIndex] == WorldType.BUFFET) {
               GuiCreateWorld.this.mc.displayGuiScreen(new GuiCreateBuffetWorld(GuiCreateWorld.this, GuiCreateWorld.this.chunkProviderSettingsJson));
            }

         }
      });
      this.btnCustomizeType.visible = false;
      this.worldNameField = new GuiTextField(9, this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
      this.worldNameField.setFocused(true);
      this.worldNameField.setText(this.worldName);
      this.worldSeedField = new GuiTextField(10, this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
      this.worldSeedField.setText(this.worldSeed);
      this.showMoreWorldOptions(this.inMoreWorldOptionsDisplay);
      this.calcSaveDirName();
      this.updateDisplayState();
   }

   private void calcSaveDirName() {
      this.saveDirName = this.worldNameField.getText().trim();

      for(char c0 : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
         this.saveDirName = this.saveDirName.replace(c0, '_');
      }

      if (StringUtils.isEmpty(this.saveDirName)) {
         this.saveDirName = "World";
      }

      this.saveDirName = getUncollidingSaveDirName(this.mc.getSaveLoader(), this.saveDirName);
   }

   private void updateDisplayState() {
      this.btnGameMode.displayString = I18n.format("selectWorld.gameMode") + ": " + I18n.format("selectWorld.gameMode." + this.gameMode);
      this.gameModeDesc1 = I18n.format("selectWorld.gameMode." + this.gameMode + ".line1");
      this.gameModeDesc2 = I18n.format("selectWorld.gameMode." + this.gameMode + ".line2");
      this.btnMapFeatures.displayString = I18n.format("selectWorld.mapFeatures") + " ";
      if (this.generateStructuresEnabled) {
         this.btnMapFeatures.displayString = this.btnMapFeatures.displayString + I18n.format("options.on");
      } else {
         this.btnMapFeatures.displayString = this.btnMapFeatures.displayString + I18n.format("options.off");
      }

      this.btnBonusItems.displayString = I18n.format("selectWorld.bonusItems") + " ";
      if (this.bonusChestEnabled && !this.hardCoreMode) {
         this.btnBonusItems.displayString = this.btnBonusItems.displayString + I18n.format("options.on");
      } else {
         this.btnBonusItems.displayString = this.btnBonusItems.displayString + I18n.format("options.off");
      }

      this.btnMapType.displayString = I18n.format("selectWorld.mapType") + " " + I18n.format(WorldType.WORLD_TYPES[this.selectedIndex].getTranslationKey());
      this.btnAllowCommands.displayString = I18n.format("selectWorld.allowCommands") + " ";
      if (this.allowCheats && !this.hardCoreMode) {
         this.btnAllowCommands.displayString = this.btnAllowCommands.displayString + I18n.format("options.on");
      } else {
         this.btnAllowCommands.displayString = this.btnAllowCommands.displayString + I18n.format("options.off");
      }

   }

   public static String getUncollidingSaveDirName(ISaveFormat p_146317_0_, String p_146317_1_) {
      p_146317_1_ = p_146317_1_.replaceAll("[\\./\"]", "_");

      for(String s : DISALLOWED_FILENAMES) {
         if (p_146317_1_.equalsIgnoreCase(s)) {
            p_146317_1_ = "_" + p_146317_1_ + "_";
         }
      }

      while(p_146317_0_.getWorldInfo(p_146317_1_) != null) {
         p_146317_1_ = p_146317_1_ + "-";
      }

      return p_146317_1_;
   }

   public void onGuiClosed() {
      this.mc.keyboardListener.enableRepeatEvents(false);
   }

   private void func_195352_j() {
      this.mc.displayGuiScreen((GuiScreen)null);
      if (!this.alreadyGenerated) {
         this.alreadyGenerated = true;
         long i = (new Random()).nextLong();
         String s = this.worldSeedField.getText();
         if (!StringUtils.isEmpty(s)) {
            try {
               long j = Long.parseLong(s);
               if (j != 0L) {
                  i = j;
               }
            } catch (NumberFormatException var6) {
               i = (long)s.hashCode();
            }
         }

         WorldSettings worldsettings = new WorldSettings(i, GameType.getByName(this.gameMode), this.generateStructuresEnabled, this.hardCoreMode, WorldType.WORLD_TYPES[this.selectedIndex]);
         worldsettings.func_205390_a(Dynamic.convert(NBTDynamicOps.INSTANCE, JsonOps.INSTANCE, this.chunkProviderSettingsJson));
         if (this.bonusChestEnabled && !this.hardCoreMode) {
            worldsettings.enableBonusChest();
         }

         if (this.allowCheats && !this.hardCoreMode) {
            worldsettings.enableCommands();
         }

         this.mc.launchIntegratedServer(this.saveDirName, this.worldNameField.getText().trim(), worldsettings);
      }
   }

   private boolean canSelectCurWorldType() {
      WorldType worldtype = WorldType.WORLD_TYPES[this.selectedIndex];
      if (worldtype != null && worldtype.canBeCreated()) {
         return worldtype == WorldType.DEBUG_ALL_BLOCK_STATES ? isShiftKeyDown() : true;
      } else {
         return false;
      }
   }

   private void toggleMoreWorldOptions() {
      this.showMoreWorldOptions(!this.inMoreWorldOptionsDisplay);
   }

   private void showMoreWorldOptions(boolean p_146316_1_) {
      this.inMoreWorldOptionsDisplay = p_146316_1_;
      if (WorldType.WORLD_TYPES[this.selectedIndex] == WorldType.DEBUG_ALL_BLOCK_STATES) {
         this.btnGameMode.visible = !this.inMoreWorldOptionsDisplay;
         this.btnGameMode.enabled = false;
         if (this.savedGameMode == null) {
            this.savedGameMode = this.gameMode;
         }

         this.gameMode = "spectator";
         this.btnMapFeatures.visible = false;
         this.btnBonusItems.visible = false;
         this.btnMapType.visible = this.inMoreWorldOptionsDisplay;
         this.btnAllowCommands.visible = false;
         this.btnCustomizeType.visible = false;
      } else {
         this.btnGameMode.visible = !this.inMoreWorldOptionsDisplay;
         this.btnGameMode.enabled = true;
         if (this.savedGameMode != null) {
            this.gameMode = this.savedGameMode;
            this.savedGameMode = null;
         }

         this.btnMapFeatures.visible = this.inMoreWorldOptionsDisplay && WorldType.WORLD_TYPES[this.selectedIndex] != WorldType.CUSTOMIZED;
         this.btnBonusItems.visible = this.inMoreWorldOptionsDisplay;
         this.btnMapType.visible = this.inMoreWorldOptionsDisplay;
         this.btnAllowCommands.visible = this.inMoreWorldOptionsDisplay;
         this.btnCustomizeType.visible = this.inMoreWorldOptionsDisplay && WorldType.WORLD_TYPES[this.selectedIndex].func_205393_e();
      }

      this.updateDisplayState();
      if (this.inMoreWorldOptionsDisplay) {
         this.btnMoreOptions.displayString = I18n.format("gui.done");
      } else {
         this.btnMoreOptions.displayString = I18n.format("selectWorld.moreWorldOptions");
      }

   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      if (this.worldNameField.isFocused() && !this.inMoreWorldOptionsDisplay) {
         this.worldNameField.charTyped(p_charTyped_1_, p_charTyped_2_);
         this.worldName = this.worldNameField.getText();
         this.field_195355_B.enabled = !this.worldNameField.getText().isEmpty();
         this.calcSaveDirName();
         return true;
      } else if (this.worldSeedField.isFocused() && this.inMoreWorldOptionsDisplay) {
         this.worldSeedField.charTyped(p_charTyped_1_, p_charTyped_2_);
         this.worldSeed = this.worldSeedField.getText();
         return true;
      } else {
         return super.charTyped(p_charTyped_1_, p_charTyped_2_);
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (this.worldNameField.isFocused() && !this.inMoreWorldOptionsDisplay) {
         this.worldNameField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
         this.worldName = this.worldNameField.getText();
         this.field_195355_B.enabled = !this.worldNameField.getText().isEmpty();
         this.calcSaveDirName();
      } else if (this.worldSeedField.isFocused() && this.inMoreWorldOptionsDisplay) {
         this.worldSeedField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
         this.worldSeed = this.worldSeedField.getText();
      }

      if (this.field_195355_B.enabled && (p_keyPressed_1_ == 257 || p_keyPressed_1_ == 335)) {
         this.func_195352_j();
      }

      return true;
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         return this.inMoreWorldOptionsDisplay ? this.worldSeedField.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) : this.worldNameField.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.format("selectWorld.create"), this.width / 2, 20, -1);
      if (this.inMoreWorldOptionsDisplay) {
         this.drawString(this.fontRenderer, I18n.format("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
         this.drawString(this.fontRenderer, I18n.format("selectWorld.seedInfo"), this.width / 2 - 100, 85, -6250336);
         if (this.btnMapFeatures.visible) {
            this.drawString(this.fontRenderer, I18n.format("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, -6250336);
         }

         if (this.btnAllowCommands.visible) {
            this.drawString(this.fontRenderer, I18n.format("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
         }

         this.worldSeedField.drawTextField(p_73863_1_, p_73863_2_, p_73863_3_);
         if (WorldType.WORLD_TYPES[this.selectedIndex].hasInfoNotice()) {
            this.fontRenderer.drawSplitString(I18n.format(WorldType.WORLD_TYPES[this.selectedIndex].getInfoTranslationKey()), this.btnMapType.x + 2, this.btnMapType.y + 22, this.btnMapType.getWidth(), 10526880);
         }
      } else {
         this.drawString(this.fontRenderer, I18n.format("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
         this.drawString(this.fontRenderer, I18n.format("selectWorld.resultFolder") + " " + this.saveDirName, this.width / 2 - 100, 85, -6250336);
         this.worldNameField.drawTextField(p_73863_1_, p_73863_2_, p_73863_3_);
         this.drawCenteredString(this.fontRenderer, this.gameModeDesc1, this.width / 2, 137, -6250336);
         this.drawCenteredString(this.fontRenderer, this.gameModeDesc2, this.width / 2, 149, -6250336);
      }

      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }

   public void recreateFromExistingWorld(WorldInfo p_146318_1_) {
      this.worldName = I18n.format("selectWorld.newWorld.copyOf", p_146318_1_.getWorldName());
      this.worldSeed = p_146318_1_.getSeed() + "";
      WorldType worldtype = p_146318_1_.getTerrainType() == WorldType.CUSTOMIZED ? WorldType.DEFAULT : p_146318_1_.getTerrainType();
      this.selectedIndex = worldtype.getId();
      this.chunkProviderSettingsJson = p_146318_1_.getGeneratorOptions();
      this.generateStructuresEnabled = p_146318_1_.isMapFeaturesEnabled();
      this.allowCheats = p_146318_1_.areCommandsAllowed();
      if (p_146318_1_.isHardcoreModeEnabled()) {
         this.gameMode = "hardcore";
      } else if (p_146318_1_.getGameType().isSurvivalOrAdventure()) {
         this.gameMode = "survival";
      } else if (p_146318_1_.getGameType().isCreative()) {
         this.gameMode = "creative";
      }

   }
}
