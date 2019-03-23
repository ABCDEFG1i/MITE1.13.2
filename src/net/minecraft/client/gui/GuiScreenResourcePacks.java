package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackInfoClient;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiScreenResourcePacks extends GuiScreen {
   private final GuiScreen parentScreen;
   @Nullable
   private GuiResourcePackAvailable availableResourcePacksList;
   @Nullable
   private GuiResourcePackSelected selectedResourcePacksList;
   private boolean changed;

   public GuiScreenResourcePacks(GuiScreen p_i45050_1_) {
      this.parentScreen = p_i45050_1_;
   }

   protected void initGui() {
      this.addButton(new GuiOptionButton(2, this.width / 2 - 154, this.height - 48, I18n.format("resourcePack.openFolder")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            Util.getOSType().openFile(GuiScreenResourcePacks.this.mc.getFileResourcePacks());
         }
      });
      this.addButton(new GuiOptionButton(1, this.width / 2 + 4, this.height - 48, I18n.format("gui.done")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            if (GuiScreenResourcePacks.this.changed) {
               List<ResourcePackInfoClient> list1 = Lists.newArrayList();

               for(ResourcePackListEntryFound resourcepacklistentryfound : GuiScreenResourcePacks.this.selectedResourcePacksList.getChildren()) {
                  list1.add(resourcepacklistentryfound.func_195017_i());
               }

               Collections.reverse(list1);
               GuiScreenResourcePacks.this.mc.getResourcePackList().func_198985_a(list1);
               GuiScreenResourcePacks.this.mc.gameSettings.resourcePacks.clear();
               GuiScreenResourcePacks.this.mc.gameSettings.incompatibleResourcePacks.clear();

               for(ResourcePackInfoClient resourcepackinfoclient2 : list1) {
                  if (!resourcepackinfoclient2.func_195798_h()) {
                     GuiScreenResourcePacks.this.mc.gameSettings.resourcePacks.add(resourcepackinfoclient2.getName());
                     if (!resourcepackinfoclient2.func_195791_d().func_198968_a()) {
                        GuiScreenResourcePacks.this.mc.gameSettings.incompatibleResourcePacks.add(resourcepackinfoclient2.getName());
                     }
                  }
               }

               GuiScreenResourcePacks.this.mc.gameSettings.saveOptions();
               GuiScreenResourcePacks.this.mc.refreshResources();
            }

            GuiScreenResourcePacks.this.mc.displayGuiScreen(GuiScreenResourcePacks.this.parentScreen);
         }
      });
      GuiResourcePackAvailable guiresourcepackavailable = this.availableResourcePacksList;
      GuiResourcePackSelected guiresourcepackselected = this.selectedResourcePacksList;
      this.availableResourcePacksList = new GuiResourcePackAvailable(this.mc, 200, this.height);
      this.availableResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
      if (guiresourcepackavailable != null) {
         this.availableResourcePacksList.getChildren().addAll(guiresourcepackavailable.getChildren());
      }

      this.eventListeners.add(this.availableResourcePacksList);
      this.selectedResourcePacksList = new GuiResourcePackSelected(this.mc, 200, this.height);
      this.selectedResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 + 4);
      if (guiresourcepackselected != null) {
         this.selectedResourcePacksList.getChildren().addAll(guiresourcepackselected.getChildren());
      }

      this.eventListeners.add(this.selectedResourcePacksList);
      if (!this.changed) {
         this.availableResourcePacksList.getChildren().clear();
         this.selectedResourcePacksList.getChildren().clear();
         ResourcePackList<ResourcePackInfoClient> resourcepacklist = this.mc.getResourcePackList();
         resourcepacklist.reloadPacksFromFinders();
         List<ResourcePackInfoClient> list = Lists.newArrayList(resourcepacklist.func_198978_b());
         list.removeAll(resourcepacklist.getPackInfos());

         for(ResourcePackInfoClient resourcepackinfoclient : list) {
            this.availableResourcePacksList.func_195095_a(new ResourcePackListEntryFound(this, resourcepackinfoclient));
         }

         for(ResourcePackInfoClient resourcepackinfoclient1 : Lists.reverse(Lists.newArrayList(resourcepacklist.getPackInfos()))) {
            this.selectedResourcePacksList.func_195095_a(new ResourcePackListEntryFound(this, resourcepackinfoclient1));
         }
      }

   }

   public void func_195301_a(ResourcePackListEntryFound p_195301_1_) {
      this.availableResourcePacksList.getChildren().remove(p_195301_1_);
      p_195301_1_.func_195020_a(this.selectedResourcePacksList);
      this.markChanged();
   }

   public void func_195305_b(ResourcePackListEntryFound p_195305_1_) {
      this.selectedResourcePacksList.getChildren().remove(p_195305_1_);
      this.availableResourcePacksList.func_195095_a(p_195305_1_);
      this.markChanged();
   }

   public boolean func_195312_c(ResourcePackListEntryFound p_195312_1_) {
      return this.selectedResourcePacksList.getChildren().contains(p_195312_1_);
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawBackground(0);
      this.availableResourcePacksList.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
      this.selectedResourcePacksList.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
      this.drawCenteredString(this.fontRenderer, I18n.format("resourcePack.title"), this.width / 2, 16, 16777215);
      this.drawCenteredString(this.fontRenderer, I18n.format("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }

   public void markChanged() {
      this.changed = true;
   }
}
