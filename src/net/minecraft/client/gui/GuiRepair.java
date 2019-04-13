package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketRenameItem;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiRepair extends GuiContainer {
    private static final ResourceLocation ANVIL_RESOURCE = new ResourceLocation("textures/gui/container/anvil.png");
    private final ContainerRepair anvil;
    private final InventoryPlayer playerInventory;
    private GuiTextField nameField;

    public GuiRepair(InventoryPlayer p_i45508_1_, World p_i45508_2_,int repairLevel) {
        super(new ContainerRepair(p_i45508_1_, p_i45508_2_, Minecraft.getInstance().player,repairLevel));
        this.playerInventory = p_i45508_1_;
        this.anvil = (ContainerRepair) this.inventorySlots;
    }

    private void func_195393_a(int p_195393_1_, String p_195393_2_) {
        if (!p_195393_2_.isEmpty()) {
            String s = p_195393_2_;
            Slot slot = this.anvil.getSlot(0);
            if (slot != null && slot.getHasStack() && !slot.getStack().hasDisplayName() && p_195393_2_.equals(
                    slot.getStack().getDisplayName().getString())) {
                s = "";
            }

            this.anvil.updateItemName(s);
            this.mc.player.connection.sendPacket(new CPacketRenameItem(s));
        }
    }

    public IGuiEventListener getFocused() {
        return this.nameField.isFocused() ? this.nameField : null;
    }

    protected void initGui() {
        super.initGui();
        this.mc.keyboardListener.enableRepeatEvents(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.nameField = new GuiTextField(0, this.fontRenderer, i + 62, j + 24, 103, 12);
        this.nameField.setTextColor(-1);
        this.nameField.setDisabledTextColour(-1);
        this.nameField.setEnableBackgroundDrawing(false);
        this.nameField.setMaxStringLength(35);
        this.nameField.setTextAcceptHandler(this::func_195393_a);
        this.eventListeners.add(this.nameField);
    }

    public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        this.drawDefaultBackground();
        super.render(p_73863_1_, p_73863_2_, p_73863_3_);
        this.renderHoveredToolTip(p_73863_1_, p_73863_2_);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.nameField.drawTextField(p_73863_1_, p_73863_2_, p_73863_3_);
    }

    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.fontRenderer.drawString(I18n.format("container.repair"), 60.0F, 6.0F, 4210752);
        if (this.anvil.getSlot(2).getHasStack()) {
            this.anvil.getSlot(2).canTakeStack(this.playerInventory.player);
        }

        GlStateManager.enableLighting();
    }

    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ANVIL_RESOURCE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(i + 59, j + 20, 0, this.ySize + (this.anvil.getSlot(0).getHasStack() ? 0 : 16), 110,
                16);
        if ((this.anvil.getSlot(0).getHasStack() || this.anvil.getSlot(1).getHasStack()) && !this.anvil.getSlot(2)
                .getHasStack()) {
            this.drawTexturedModalRect(i + 99, j + 45, this.xSize, 0, 28, 21);
        }

    }

    public void onGuiClosed() {
        super.onGuiClosed();
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    public void onResize(Minecraft p_175273_1_, int p_175273_2_, int p_175273_3_) {
        String s = this.nameField.getText();
        this.setWorldAndResolution(p_175273_1_, p_175273_2_, p_175273_3_);
        this.nameField.setText(s);
    }

    public void sendAllContents(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_) {
        this.sendSlotContents(p_71110_1_, 0, p_71110_1_.getSlot(0).getStack());
    }

    public void sendSlotContents(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_) {
        if (p_71111_2_ == 0) {
            this.nameField.setText(p_71111_3_.isEmpty() ? "" : p_71111_3_.getDisplayName().getString());
            this.nameField.setEnabled(!p_71111_3_.isEmpty());
        }

    }


}
