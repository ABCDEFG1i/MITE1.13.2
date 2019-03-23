package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.FlatGenSettings;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiFlatPresets extends GuiScreen {
   private static final List<GuiFlatPresets.LayerItem> FLAT_WORLD_PRESETS = Lists.newArrayList();
   private final GuiCreateFlatWorld parentScreen;
   private String presetsTitle;
   private String presetsShare;
   private String listText;
   private GuiFlatPresets.ListSlot list;
   private GuiButton btnSelect;
   private GuiTextField export;

   public GuiFlatPresets(GuiCreateFlatWorld p_i46318_1_) {
      this.parentScreen = p_i46318_1_;
   }

   protected void initGui() {
      this.mc.keyboardListener.enableRepeatEvents(true);
      this.presetsTitle = I18n.format("createWorld.customize.presets.title");
      this.presetsShare = I18n.format("createWorld.customize.presets.share");
      this.listText = I18n.format("createWorld.customize.presets.list");
      this.export = new GuiTextField(2, this.fontRenderer, 50, 40, this.width - 100, 20);
      this.list = new GuiFlatPresets.ListSlot();
      this.eventListeners.add(this.list);
      this.export.setMaxStringLength(1230);
      this.export.setText(this.parentScreen.func_210501_h());
      this.eventListeners.add(this.export);
      this.btnSelect = this.addButton(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("createWorld.customize.presets.select")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiFlatPresets.this.parentScreen.func_210502_a(GuiFlatPresets.this.export.getText());
            GuiFlatPresets.this.mc.displayGuiScreen(GuiFlatPresets.this.parentScreen);
         }
      });
      this.addButton(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiFlatPresets.this.mc.displayGuiScreen(GuiFlatPresets.this.parentScreen);
         }
      });
      this.updateButtonValidity();
      this.setFocused(this.list);
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_) {
      return this.list.mouseScrolled(p_mouseScrolled_1_);
   }

   public void onResize(Minecraft p_175273_1_, int p_175273_2_, int p_175273_3_) {
      String s = this.export.getText();
      this.setWorldAndResolution(p_175273_1_, p_175273_2_, p_175273_3_);
      this.export.setText(s);
   }

   public void onGuiClosed() {
      this.mc.keyboardListener.enableRepeatEvents(false);
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      this.list.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
      this.drawCenteredString(this.fontRenderer, this.presetsTitle, this.width / 2, 8, 16777215);
      this.drawString(this.fontRenderer, this.presetsShare, 50, 30, 10526880);
      this.drawString(this.fontRenderer, this.listText, 50, 70, 10526880);
      this.export.drawTextField(p_73863_1_, p_73863_2_, p_73863_3_);
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }

   public void tick() {
      this.export.tick();
      super.tick();
   }

   public void updateButtonValidity() {
      this.btnSelect.enabled = this.hasValidSelection();
   }

   private boolean hasValidSelection() {
      return this.list.selected > -1 && this.list.selected < FLAT_WORLD_PRESETS.size() || this.export.getText().length() > 1;
   }

   private static void func_199709_a(String p_199709_0_, IItemProvider p_199709_1_, Biome p_199709_2_, List<String> p_199709_3_, FlatLayerInfo... p_199709_4_) {
      FlatGenSettings flatgensettings = ChunkGeneratorType.FLAT.createChunkGenSettings();

      for(int i = p_199709_4_.length - 1; i >= 0; --i) {
         flatgensettings.getFlatLayers().add(p_199709_4_[i]);
      }

      flatgensettings.setBiome(p_199709_2_);
      flatgensettings.updateLayers();

      for(String s : p_199709_3_) {
         flatgensettings.getWorldFeatures().put(s, Maps.newHashMap());
      }

      FLAT_WORLD_PRESETS.add(new GuiFlatPresets.LayerItem(p_199709_1_.asItem(), p_199709_0_, flatgensettings.toString()));
   }

   static {
      func_199709_a(I18n.format("createWorld.customize.preset.classic_flat"), Blocks.GRASS_BLOCK, Biomes.PLAINS, Arrays.asList("village"), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(2, Blocks.DIRT), new FlatLayerInfo(1, Blocks.BEDROCK));
      func_199709_a(I18n.format("createWorld.customize.preset.tunnelers_dream"), Blocks.STONE, Biomes.MOUNTAINS, Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(230, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      func_199709_a(I18n.format("createWorld.customize.preset.water_world"), Items.WATER_BUCKET, Biomes.DEEP_OCEAN, Arrays.asList("biome_1", "oceanmonument"), new FlatLayerInfo(90, Blocks.WATER), new FlatLayerInfo(5, Blocks.SAND), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(5, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      func_199709_a(I18n.format("createWorld.customize.preset.overworld"), Blocks.GRASS, Biomes.PLAINS, Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake"), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      func_199709_a(I18n.format("createWorld.customize.preset.snowy_kingdom"), Blocks.SNOW, Biomes.SNOWY_TUNDRA, Arrays.asList("village", "biome_1"), new FlatLayerInfo(1, Blocks.SNOW), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      func_199709_a(I18n.format("createWorld.customize.preset.bottomless_pit"), Items.FEATHER, Biomes.PLAINS, Arrays.asList("village", "biome_1"), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(2, Blocks.COBBLESTONE));
      func_199709_a(I18n.format("createWorld.customize.preset.desert"), Blocks.SAND, Biomes.DESERT, Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"), new FlatLayerInfo(8, Blocks.SAND), new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      func_199709_a(I18n.format("createWorld.customize.preset.redstone_ready"), Items.REDSTONE, Biomes.DESERT, Collections.emptyList(), new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      func_199709_a(I18n.format("createWorld.customize.preset.the_void"), Blocks.BARRIER, Biomes.THE_VOID, Arrays.asList("decoration"), new FlatLayerInfo(1, Blocks.AIR));
   }

   @OnlyIn(Dist.CLIENT)
   static class LayerItem {
      public Item icon;
      public String name;
      public String generatorInfo;

      public LayerItem(Item p_i48059_1_, String p_i48059_2_, String p_i48059_3_) {
         this.icon = p_i48059_1_;
         this.name = p_i48059_2_;
         this.generatorInfo = p_i48059_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ListSlot extends GuiSlot {
      public int selected = -1;

      public ListSlot() {
         super(GuiFlatPresets.this.mc, GuiFlatPresets.this.width, GuiFlatPresets.this.height, 80, GuiFlatPresets.this.height - 37, 24);
      }

      private void func_195101_a(int p_195101_1_, int p_195101_2_, Item p_195101_3_) {
         this.blitSlotBg(p_195101_1_ + 1, p_195101_2_ + 1);
         GlStateManager.enableRescaleNormal();
         RenderHelper.enableGUIStandardItemLighting();
         GuiFlatPresets.this.itemRender.renderItemIntoGUI(new ItemStack(p_195101_3_), p_195101_1_ + 2, p_195101_2_ + 2);
         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableRescaleNormal();
      }

      private void blitSlotBg(int p_148173_1_, int p_148173_2_) {
         this.blitSlotIcon(p_148173_1_, p_148173_2_, 0, 0);
      }

      private void blitSlotIcon(int p_148171_1_, int p_148171_2_, int p_148171_3_, int p_148171_4_) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(Gui.STAT_ICONS);
         float f = 0.0078125F;
         float f1 = 0.0078125F;
         int i = 18;
         int j = 18;
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
         bufferbuilder.pos((double)(p_148171_1_ + 0), (double)(p_148171_2_ + 18), (double)this.zLevel).tex((double)((float)(p_148171_3_ + 0) * 0.0078125F), (double)((float)(p_148171_4_ + 18) * 0.0078125F)).endVertex();
         bufferbuilder.pos((double)(p_148171_1_ + 18), (double)(p_148171_2_ + 18), (double)this.zLevel).tex((double)((float)(p_148171_3_ + 18) * 0.0078125F), (double)((float)(p_148171_4_ + 18) * 0.0078125F)).endVertex();
         bufferbuilder.pos((double)(p_148171_1_ + 18), (double)(p_148171_2_ + 0), (double)this.zLevel).tex((double)((float)(p_148171_3_ + 18) * 0.0078125F), (double)((float)(p_148171_4_ + 0) * 0.0078125F)).endVertex();
         bufferbuilder.pos((double)(p_148171_1_ + 0), (double)(p_148171_2_ + 0), (double)this.zLevel).tex((double)((float)(p_148171_3_ + 0) * 0.0078125F), (double)((float)(p_148171_4_ + 0) * 0.0078125F)).endVertex();
         tessellator.draw();
      }

      protected int getSize() {
         return GuiFlatPresets.FLAT_WORLD_PRESETS.size();
      }

      protected boolean mouseClicked(int p_195078_1_, int p_195078_2_, double p_195078_3_, double p_195078_5_) {
         this.selected = p_195078_1_;
         GuiFlatPresets.this.updateButtonValidity();
         GuiFlatPresets.this.export.setText((GuiFlatPresets.FLAT_WORLD_PRESETS.get(GuiFlatPresets.this.list.selected)).generatorInfo);
         GuiFlatPresets.this.export.setCursorPositionZero();
         return true;
      }

      protected boolean isSelected(int p_148131_1_) {
         return p_148131_1_ == this.selected;
      }

      protected void drawBackground() {
      }

      protected void drawSlot(int p_192637_1_, int p_192637_2_, int p_192637_3_, int p_192637_4_, int p_192637_5_, int p_192637_6_, float p_192637_7_) {
         GuiFlatPresets.LayerItem guiflatpresets$layeritem = GuiFlatPresets.FLAT_WORLD_PRESETS.get(p_192637_1_);
         this.func_195101_a(p_192637_2_, p_192637_3_, guiflatpresets$layeritem.icon);
         GuiFlatPresets.this.fontRenderer.drawString(guiflatpresets$layeritem.name, (float)(p_192637_2_ + 18 + 5), (float)(p_192637_3_ + 6), 16777215);
      }
   }
}
