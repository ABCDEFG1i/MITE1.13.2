package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiCreateBuffetWorld extends GuiScreen {
   private static final List<ResourceLocation> BUFFET_GENERATORS = IRegistry.field_212627_p.func_148742_b().stream().filter((p_205307_0_) -> {
      return IRegistry.field_212627_p.func_212608_b(p_205307_0_).isOptionForBuffetWorld();
   }).collect(Collectors.toList());
   private final GuiCreateWorld parent;
   private final List<ResourceLocation> biomes = Lists.newArrayList();
   private final ResourceLocation[] biomeTypes = new ResourceLocation[IRegistry.field_212624_m.func_148742_b().size()];
   private String title;
   private GuiCreateBuffetWorld.BiomeList biomeList;
   private int field_205312_t;
   private GuiButton field_205313_u;

   public GuiCreateBuffetWorld(GuiCreateWorld p_i49701_1_, NBTTagCompound p_i49701_2_) {
      this.parent = p_i49701_1_;
      int i = 0;

      for(ResourceLocation resourcelocation : IRegistry.field_212624_m.func_148742_b()) {
         this.biomeTypes[i] = resourcelocation;
         ++i;
      }

      Arrays.sort(this.biomeTypes, (p_210140_0_, p_210140_1_) -> {
         String s = IRegistry.field_212624_m.func_212608_b(p_210140_0_).getDisplayName().getString();
         String s1 = IRegistry.field_212624_m.func_212608_b(p_210140_1_).getDisplayName().getString();
         return s.compareTo(s1);
      });
      this.deserialize(p_i49701_2_);
   }

   private void deserialize(NBTTagCompound p_210506_1_) {
      if (p_210506_1_.hasKey("chunk_generator", 10) && p_210506_1_.getCompoundTag("chunk_generator").hasKey("type", 8)) {
         ResourceLocation resourcelocation = new ResourceLocation(p_210506_1_.getCompoundTag("chunk_generator").getString("type"));

         for(int i = 0; i < BUFFET_GENERATORS.size(); ++i) {
            if (BUFFET_GENERATORS.get(i).equals(resourcelocation)) {
               this.field_205312_t = i;
               break;
            }
         }
      }

      if (p_210506_1_.hasKey("biome_source", 10) && p_210506_1_.getCompoundTag("biome_source").hasKey("biomes", 9)) {
         NBTTagList nbttaglist = p_210506_1_.getCompoundTag("biome_source").getTagList("biomes", 8);

         for(int j = 0; j < nbttaglist.size(); ++j) {
            this.biomes.add(new ResourceLocation(nbttaglist.getStringTagAt(j)));
         }
      }

   }

   private NBTTagCompound serialize() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      nbttagcompound1.setString("type", IRegistry.field_212625_n.func_177774_c(BiomeProviderType.FIXED).toString());
      NBTTagCompound nbttagcompound2 = new NBTTagCompound();
      NBTTagList nbttaglist = new NBTTagList();

      for(ResourceLocation resourcelocation : this.biomes) {
         nbttaglist.add((INBTBase)(new NBTTagString(resourcelocation.toString())));
      }

      nbttagcompound2.setTag("biomes", nbttaglist);
      nbttagcompound1.setTag("options", nbttagcompound2);
      NBTTagCompound nbttagcompound3 = new NBTTagCompound();
      NBTTagCompound nbttagcompound4 = new NBTTagCompound();
      nbttagcompound3.setString("type", BUFFET_GENERATORS.get(this.field_205312_t).toString());
      nbttagcompound4.setString("default_block", "minecraft:stone");
      nbttagcompound4.setString("default_fluid", "minecraft:water");
      nbttagcompound3.setTag("options", nbttagcompound4);
      nbttagcompound.setTag("biome_source", nbttagcompound1);
      nbttagcompound.setTag("chunk_generator", nbttagcompound3);
      return nbttagcompound;
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.biomeList;
   }

   protected void initGui() {
      this.mc.keyboardListener.enableRepeatEvents(true);
      this.title = I18n.format("createWorld.customize.buffet.title");
      this.biomeList = new GuiCreateBuffetWorld.BiomeList();
      this.eventListeners.add(this.biomeList);
      this.addButton(new GuiButton(2, (this.width - 200) / 2, 40, 200, 20, I18n.format("createWorld.customize.buffet.generatortype") + " " + I18n.format(Util.makeTranslationKey("generator", BUFFET_GENERATORS.get(this.field_205312_t)))) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiCreateBuffetWorld.this.field_205312_t++;
            if (GuiCreateBuffetWorld.this.field_205312_t >= GuiCreateBuffetWorld.BUFFET_GENERATORS.size()) {
               GuiCreateBuffetWorld.this.field_205312_t = 0;
            }

            this.displayString = I18n.format("createWorld.customize.buffet.generatortype") + " " + I18n.format(Util.makeTranslationKey("generator", GuiCreateBuffetWorld.BUFFET_GENERATORS.get(GuiCreateBuffetWorld.this.field_205312_t)));
         }
      });
      this.field_205313_u = this.addButton(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.done")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiCreateBuffetWorld.this.parent.chunkProviderSettingsJson = GuiCreateBuffetWorld.this.serialize();
            GuiCreateBuffetWorld.this.mc.displayGuiScreen(GuiCreateBuffetWorld.this.parent);
         }
      });
      this.addButton(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiCreateBuffetWorld.this.mc.displayGuiScreen(GuiCreateBuffetWorld.this.parent);
         }
      });
      this.func_205306_h();
   }

   public void func_205306_h() {
      this.field_205313_u.enabled = !this.biomes.isEmpty();
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawBackground(0);
      this.biomeList.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 8, 16777215);
      this.drawCenteredString(this.fontRenderer, I18n.format("createWorld.customize.buffet.generator"), this.width / 2, 30, 10526880);
      this.drawCenteredString(this.fontRenderer, I18n.format("createWorld.customize.buffet.biome"), this.width / 2, 68, 10526880);
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }

   @OnlyIn(Dist.CLIENT)
   class BiomeList extends GuiSlot {
      private BiomeList() {
         super(GuiCreateBuffetWorld.this.mc, GuiCreateBuffetWorld.this.width, GuiCreateBuffetWorld.this.height, 80, GuiCreateBuffetWorld.this.height - 37, 16);
      }

      protected int getSize() {
         return GuiCreateBuffetWorld.this.biomeTypes.length;
      }

      protected boolean mouseClicked(int p_195078_1_, int p_195078_2_, double p_195078_3_, double p_195078_5_) {
         GuiCreateBuffetWorld.this.biomes.clear();
         GuiCreateBuffetWorld.this.biomes.add(GuiCreateBuffetWorld.this.biomeTypes[p_195078_1_]);
         GuiCreateBuffetWorld.this.func_205306_h();
         return true;
      }

      protected boolean isSelected(int p_148131_1_) {
         return GuiCreateBuffetWorld.this.biomes.contains(GuiCreateBuffetWorld.this.biomeTypes[p_148131_1_]);
      }

      protected void drawBackground() {
      }

      protected void drawSlot(int p_192637_1_, int p_192637_2_, int p_192637_3_, int p_192637_4_, int p_192637_5_, int p_192637_6_, float p_192637_7_) {
         this.drawString(GuiCreateBuffetWorld.this.fontRenderer, IRegistry.field_212624_m.func_212608_b(GuiCreateBuffetWorld.this.biomeTypes[p_192637_1_]).getDisplayName().getString(), p_192637_2_ + 5, p_192637_3_ + 2, 16777215);
      }
   }
}
