package net.minecraft.client.gui;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class GuiCommandBlockBase extends GuiScreen {
   protected GuiTextField commandTextField;
   protected GuiTextField field_195239_f;
   protected GuiButton field_195240_g;
   protected GuiButton field_195241_h;
   protected GuiButton field_195242_i;
   protected boolean field_195238_s;
   protected final List<String> field_209111_t = Lists.newArrayList();
   protected int field_209112_u;
   protected int field_209113_v;
   protected ParseResults<ISuggestionProvider> field_209114_w;
   protected CompletableFuture<Suggestions> field_209115_x;
   protected GuiCommandBlockBase.SuggestionsList field_209116_y;
   private boolean field_212342_z;

   public void tick() {
      this.commandTextField.tick();
   }

   abstract CommandBlockBaseLogic func_195231_h();

   abstract int func_195236_i();

   protected void initGui() {
      this.mc.keyboardListener.enableRepeatEvents(true);
      this.field_195240_g = this.addButton(new GuiButton(0, this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.format("gui.done")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiCommandBlockBase.this.func_195234_k();
         }
      });
      this.field_195241_h = this.addButton(new GuiButton(1, this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.format("gui.cancel")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiCommandBlockBase.this.func_195232_m();
         }
      });
      this.field_195242_i = this.addButton(new GuiButton(4, this.width / 2 + 150 - 20, this.func_195236_i(), 20, 20, "O") {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            CommandBlockBaseLogic commandblockbaselogic = GuiCommandBlockBase.this.func_195231_h();
            commandblockbaselogic.setTrackOutput(!commandblockbaselogic.shouldTrackOutput());
            GuiCommandBlockBase.this.func_195233_j();
         }
      });
      this.commandTextField = new GuiTextField(2, this.fontRenderer, this.width / 2 - 150, 50, 300, 20) {
         public void setFocused(boolean p_146195_1_) {
            super.setFocused(p_146195_1_);
            if (p_146195_1_) {
               GuiCommandBlockBase.this.field_195239_f.setFocused(false);
            }

         }
      };
      this.commandTextField.setMaxStringLength(32500);
      this.commandTextField.setTextFormatter(this::func_209104_a);
      this.commandTextField.setTextAcceptHandler(this::func_209103_a);
      this.eventListeners.add(this.commandTextField);
      this.field_195239_f = new GuiTextField(3, this.fontRenderer, this.width / 2 - 150, this.func_195236_i(), 276, 20) {
         public void setFocused(boolean p_146195_1_) {
            super.setFocused(p_146195_1_);
            if (p_146195_1_) {
               GuiCommandBlockBase.this.commandTextField.setFocused(false);
            }

         }
      };
      this.field_195239_f.setMaxStringLength(32500);
      this.field_195239_f.setEnabled(false);
      this.field_195239_f.setText("-");
      this.eventListeners.add(this.field_195239_f);
      this.commandTextField.setFocused(true);
      this.setFocused(this.commandTextField);
      this.func_209106_o();
   }

   public void onResize(Minecraft p_175273_1_, int p_175273_2_, int p_175273_3_) {
      String s = this.commandTextField.getText();
      this.setWorldAndResolution(p_175273_1_, p_175273_2_, p_175273_3_);
      this.func_209102_a(s);
      this.func_209106_o();
   }

   protected void func_195233_j() {
      if (this.func_195231_h().shouldTrackOutput()) {
         this.field_195242_i.displayString = "O";
         this.field_195239_f.setText(this.func_195231_h().getLastOutput().getString());
      } else {
         this.field_195242_i.displayString = "X";
         this.field_195239_f.setText("-");
      }

   }

   protected void func_195234_k() {
      CommandBlockBaseLogic commandblockbaselogic = this.func_195231_h();
      this.func_195235_a(commandblockbaselogic);
      if (!commandblockbaselogic.shouldTrackOutput()) {
         commandblockbaselogic.setLastOutput((ITextComponent)null);
      }

      this.mc.displayGuiScreen((GuiScreen)null);
   }

   public void onGuiClosed() {
      this.mc.keyboardListener.enableRepeatEvents(false);
   }

   protected abstract void func_195235_a(CommandBlockBaseLogic p_195235_1_);

   protected void func_195232_m() {
      this.func_195231_h().setTrackOutput(this.field_195238_s);
      this.mc.displayGuiScreen((GuiScreen)null);
   }

   public void close() {
      this.func_195232_m();
   }

   private void func_209103_a(int p_209103_1_, String p_209103_2_) {
      this.func_209106_o();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
         if (this.field_209116_y != null && this.field_209116_y.func_209133_b(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
            return true;
         } else {
            if (p_keyPressed_1_ == 258) {
               this.func_209109_s();
            }

            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
         }
      } else {
         this.func_195234_k();
         return true;
      }
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_) {
      return this.field_209116_y != null && this.field_209116_y.func_209232_a(MathHelper.clamp(p_mouseScrolled_1_, -1.0D, 1.0D)) ? true : super.mouseScrolled(p_mouseScrolled_1_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return this.field_209116_y != null && this.field_209116_y.func_209233_a((int)p_mouseClicked_1_, (int)p_mouseClicked_3_, p_mouseClicked_5_) ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   protected void func_209106_o() {
      this.field_209114_w = null;
      if (!this.field_212342_z) {
         this.commandTextField.setSuggestion((String)null);
         this.field_209116_y = null;
      }

      this.field_209111_t.clear();
      CommandDispatcher<ISuggestionProvider> commanddispatcher = this.mc.player.connection.func_195515_i();
      String s = this.commandTextField.getText();
      StringReader stringreader = new StringReader(s);
      if (stringreader.canRead() && stringreader.peek() == '/') {
         stringreader.skip();
      }

      this.field_209114_w = commanddispatcher.parse(stringreader, this.mc.player.connection.func_195513_b());
      if (this.field_209116_y == null || !this.field_212342_z) {
         StringReader stringreader1 = new StringReader(s.substring(0, Math.min(s.length(), this.commandTextField.getCursorPosition())));
         if (stringreader1.canRead() && stringreader1.peek() == '/') {
            stringreader1.skip();
         }

         ParseResults<ISuggestionProvider> parseresults = commanddispatcher.parse(stringreader1, this.mc.player.connection.func_195513_b());
         this.field_209115_x = commanddispatcher.getCompletionSuggestions(parseresults);
         this.field_209115_x.thenRun(() -> {
            if (this.field_209115_x.isDone()) {
               this.func_209107_u();
            }
         });
      }

   }

   private void func_209107_u() {
      if (this.field_209115_x.join().isEmpty() && !this.field_209114_w.getExceptions().isEmpty() && this.commandTextField.getCursorPosition() == this.commandTextField.getText().length()) {
         int i = 0;

         for(Entry<CommandNode<ISuggestionProvider>, CommandSyntaxException> entry : this.field_209114_w.getExceptions().entrySet()) {
            CommandSyntaxException commandsyntaxexception = entry.getValue();
            if (commandsyntaxexception.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
               ++i;
            } else {
               this.field_209111_t.add(commandsyntaxexception.getMessage());
            }
         }

         if (i > 0) {
            this.field_209111_t.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
         }
      }

      this.field_209112_u = 0;
      this.field_209113_v = this.width;
      if (this.field_209111_t.isEmpty()) {
         this.func_209108_a(TextFormatting.GRAY);
      }

      this.field_209116_y = null;
      if (this.mc.gameSettings.autoSuggestions) {
         this.func_209109_s();
      }

   }

   private String func_209104_a(String p_209104_1_, int p_209104_2_) {
      return this.field_209114_w != null ? GuiChat.func_212336_a(this.field_209114_w, p_209104_1_, p_209104_2_) : p_209104_1_;
   }

   private void func_209108_a(TextFormatting p_209108_1_) {
      CommandContextBuilder<ISuggestionProvider> commandcontextbuilder = this.field_209114_w.getContext();
      CommandContextBuilder<ISuggestionProvider> commandcontextbuilder1 = commandcontextbuilder.getLastChild();
      if (!commandcontextbuilder1.getNodes().isEmpty()) {
         CommandNode<ISuggestionProvider> commandnode;
         int i;
         if (this.field_209114_w.getReader().canRead()) {
            Entry<CommandNode<ISuggestionProvider>, StringRange> entry = Iterables.getLast(commandcontextbuilder1.getNodes().entrySet());
            commandnode = entry.getKey();
            i = entry.getValue().getEnd() + 1;
         } else if (commandcontextbuilder1.getNodes().size() > 1) {
            Entry<CommandNode<ISuggestionProvider>, StringRange> entry2 = Iterables.get(commandcontextbuilder1.getNodes().entrySet(), commandcontextbuilder1.getNodes().size() - 2);
            commandnode = entry2.getKey();
            i = entry2.getValue().getEnd() + 1;
         } else {
            if (commandcontextbuilder == commandcontextbuilder1 || commandcontextbuilder1.getNodes().isEmpty()) {
               return;
            }

            Entry<CommandNode<ISuggestionProvider>, StringRange> entry3 = Iterables.getLast(commandcontextbuilder1.getNodes().entrySet());
            commandnode = entry3.getKey();
            i = entry3.getValue().getEnd() + 1;
         }

         Map<CommandNode<ISuggestionProvider>, String> map = this.mc.player.connection.func_195515_i().getSmartUsage(commandnode, this.mc.player.connection.func_195513_b());
         List<String> list = Lists.newArrayList();
         int j = 0;

         for(Entry<CommandNode<ISuggestionProvider>, String> entry1 : map.entrySet()) {
            if (!(entry1.getKey() instanceof LiteralCommandNode)) {
               list.add(p_209108_1_ + (String)entry1.getValue());
               j = Math.max(j, this.fontRenderer.getStringWidth(entry1.getValue()));
            }
         }

         if (!list.isEmpty()) {
            this.field_209111_t.addAll(list);
            this.field_209112_u = MathHelper.clamp(this.commandTextField.func_195611_j(i) + this.fontRenderer.getStringWidth(" "), 0, this.commandTextField.func_195611_j(0) + this.fontRenderer.getStringWidth(" ") + this.commandTextField.getWidth() - j);
            this.field_209113_v = j;
         }

      }
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.format("advMode.setCommand"), this.width / 2, 20, 16777215);
      this.drawString(this.fontRenderer, I18n.format("advMode.command"), this.width / 2 - 150, 40, 10526880);
      this.commandTextField.drawTextField(p_73863_1_, p_73863_2_, p_73863_3_);
      int i = 75;
      if (!this.field_195239_f.getText().isEmpty()) {
         i = i + (5 * this.fontRenderer.FONT_HEIGHT + 1 + this.func_195236_i() - 135);
         this.drawString(this.fontRenderer, I18n.format("advMode.previousOutput"), this.width / 2 - 150, i + 4, 10526880);
         this.field_195239_f.drawTextField(p_73863_1_, p_73863_2_, p_73863_3_);
      }

      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
      if (this.field_209116_y != null) {
         this.field_209116_y.func_209129_a(p_73863_1_, p_73863_2_);
      } else {
         i = 0;

         for(String s : this.field_209111_t) {
            drawRect(this.field_209112_u - 1, 72 + 12 * i, this.field_209112_u + this.field_209113_v + 1, 84 + 12 * i, Integer.MIN_VALUE);
            this.fontRenderer.drawStringWithShadow(s, (float)this.field_209112_u, (float)(74 + 12 * i), -1);
            ++i;
         }
      }

   }

   public void func_209109_s() {
      if (this.field_209115_x != null && this.field_209115_x.isDone()) {
         Suggestions suggestions = this.field_209115_x.join();
         if (!suggestions.isEmpty()) {
            int i = 0;

            for(Suggestion suggestion : suggestions.getList()) {
               i = Math.max(i, this.fontRenderer.getStringWidth(suggestion.getText()));
            }

            int j = MathHelper.clamp(this.commandTextField.func_195611_j(suggestions.getRange().getStart()) + this.fontRenderer.getStringWidth(" "), 0, this.commandTextField.func_195611_j(0) + this.fontRenderer.getStringWidth(" ") + this.commandTextField.getWidth() - i);
            this.field_209116_y = new GuiCommandBlockBase.SuggestionsList(j, 72, i, suggestions);
         }
      }

   }

   protected void func_209102_a(String p_209102_1_) {
      this.commandTextField.setText(p_209102_1_);
   }

   @Nullable
   private static String func_212339_b(String p_212339_0_, String p_212339_1_) {
      return p_212339_1_.startsWith(p_212339_0_) ? p_212339_1_.substring(p_212339_0_.length()) : null;
   }

   @OnlyIn(Dist.CLIENT)
   class SuggestionsList {
      private final Rectangle2d field_209135_b;
      private final Suggestions field_209136_c;
      private final String field_212467_d;
      private int field_209138_e;
      private int field_209139_f;
      private Vec2f field_209140_g = Vec2f.ZERO;
      private boolean field_209141_h;

      private SuggestionsList(int p_i49843_2_, int p_i49843_3_, int p_i49843_4_, Suggestions p_i49843_5_) {
         this.field_209135_b = new Rectangle2d(p_i49843_2_ - 1, p_i49843_3_, p_i49843_4_ + 1, Math.min(p_i49843_5_.getList().size(), 7) * 12);
         this.field_209136_c = p_i49843_5_;
         this.field_212467_d = GuiCommandBlockBase.this.commandTextField.getText();
         this.func_209130_b(0);
      }

      public void func_209129_a(int p_209129_1_, int p_209129_2_) {
         int i = Math.min(this.field_209136_c.getList().size(), 7);
         int j = Integer.MIN_VALUE;
         int k = -5592406;
         boolean flag = this.field_209138_e > 0;
         boolean flag1 = this.field_209136_c.getList().size() > this.field_209138_e + i;
         boolean flag2 = flag || flag1;
         boolean flag3 = this.field_209140_g.x != (float)p_209129_1_ || this.field_209140_g.y != (float)p_209129_2_;
         if (flag3) {
            this.field_209140_g = new Vec2f((float)p_209129_1_, (float)p_209129_2_);
         }

         if (flag2) {
            Gui.drawRect(this.field_209135_b.getX(), this.field_209135_b.getY() - 1, this.field_209135_b.getX() + this.field_209135_b.getWidth(), this.field_209135_b.getY(), Integer.MIN_VALUE);
            Gui.drawRect(this.field_209135_b.getX(), this.field_209135_b.getY() + this.field_209135_b.getHeight(), this.field_209135_b.getX() + this.field_209135_b.getWidth(), this.field_209135_b.getY() + this.field_209135_b.getHeight() + 1, Integer.MIN_VALUE);
            if (flag) {
               for(int l = 0; l < this.field_209135_b.getWidth(); ++l) {
                  if (l % 2 == 0) {
                     Gui.drawRect(this.field_209135_b.getX() + l, this.field_209135_b.getY() - 1, this.field_209135_b.getX() + l + 1, this.field_209135_b.getY(), -1);
                  }
               }
            }

            if (flag1) {
               for(int j1 = 0; j1 < this.field_209135_b.getWidth(); ++j1) {
                  if (j1 % 2 == 0) {
                     Gui.drawRect(this.field_209135_b.getX() + j1, this.field_209135_b.getY() + this.field_209135_b.getHeight(), this.field_209135_b.getX() + j1 + 1, this.field_209135_b.getY() + this.field_209135_b.getHeight() + 1, -1);
                  }
               }
            }
         }

         boolean flag4 = false;

         for(int i1 = 0; i1 < i; ++i1) {
            Suggestion suggestion = this.field_209136_c.getList().get(i1 + this.field_209138_e);
            Gui.drawRect(this.field_209135_b.getX(), this.field_209135_b.getY() + 12 * i1, this.field_209135_b.getX() + this.field_209135_b.getWidth(), this.field_209135_b.getY() + 12 * i1 + 12, Integer.MIN_VALUE);
            if (p_209129_1_ > this.field_209135_b.getX() && p_209129_1_ < this.field_209135_b.getX() + this.field_209135_b.getWidth() && p_209129_2_ > this.field_209135_b.getY() + 12 * i1 && p_209129_2_ < this.field_209135_b.getY() + 12 * i1 + 12) {
               if (flag3) {
                  this.func_209130_b(i1 + this.field_209138_e);
               }

               flag4 = true;
            }

            GuiCommandBlockBase.this.fontRenderer.drawStringWithShadow(suggestion.getText(), (float)(this.field_209135_b.getX() + 1), (float)(this.field_209135_b.getY() + 2 + 12 * i1), i1 + this.field_209138_e == this.field_209139_f ? -256 : -5592406);
         }

         if (flag4) {
            Message message = this.field_209136_c.getList().get(this.field_209139_f).getTooltip();
            if (message != null) {
               GuiCommandBlockBase.this.drawHoveringText(TextComponentUtils.toTextComponent(message).getFormattedText(), p_209129_1_, p_209129_2_);
            }
         }

      }

      public boolean func_209233_a(int p_209233_1_, int p_209233_2_, int p_209233_3_) {
         if (!this.field_209135_b.contains(p_209233_1_, p_209233_2_)) {
            return false;
         } else {
            int i = (p_209233_2_ - this.field_209135_b.getY()) / 12 + this.field_209138_e;
            if (i >= 0 && i < this.field_209136_c.getList().size()) {
               this.func_209130_b(i);
               this.func_209131_a();
            }

            return true;
         }
      }

      public boolean func_209232_a(double p_209232_1_) {
         int i = (int)(GuiCommandBlockBase.this.mc.mouseHelper.getMouseX() * (double)GuiCommandBlockBase.this.mc.mainWindow.getScaledWidth() / (double)GuiCommandBlockBase.this.mc.mainWindow.getWidth());
         int j = (int)(GuiCommandBlockBase.this.mc.mouseHelper.getMouseY() * (double)GuiCommandBlockBase.this.mc.mainWindow.getScaledHeight() / (double)GuiCommandBlockBase.this.mc.mainWindow.getHeight());
         if (this.field_209135_b.contains(i, j)) {
            this.field_209138_e = MathHelper.clamp((int)((double)this.field_209138_e - p_209232_1_), 0, Math.max(this.field_209136_c.getList().size() - 7, 0));
            return true;
         } else {
            return false;
         }
      }

      public boolean func_209133_b(int p_209133_1_, int p_209133_2_, int p_209133_3_) {
         if (p_209133_1_ == 265) {
            this.func_209128_a(-1);
            this.field_209141_h = false;
            return true;
         } else if (p_209133_1_ == 264) {
            this.func_209128_a(1);
            this.field_209141_h = false;
            return true;
         } else if (p_209133_1_ == 258) {
            if (this.field_209141_h) {
               this.func_209128_a(GuiScreen.isShiftKeyDown() ? -1 : 1);
            }

            this.func_209131_a();
            return true;
         } else if (p_209133_1_ == 256) {
            this.func_209132_b();
            return true;
         } else {
            return false;
         }
      }

      public void func_209128_a(int p_209128_1_) {
         this.func_209130_b(this.field_209139_f + p_209128_1_);
         int i = this.field_209138_e;
         int j = this.field_209138_e + 7 - 1;
         if (this.field_209139_f < i) {
            this.field_209138_e = MathHelper.clamp(this.field_209139_f, 0, Math.max(this.field_209136_c.getList().size() - 7, 0));
         } else if (this.field_209139_f > j) {
            this.field_209138_e = MathHelper.clamp(this.field_209139_f - 7, 0, Math.max(this.field_209136_c.getList().size() - 7, 0));
         }

      }

      public void func_209130_b(int p_209130_1_) {
         this.field_209139_f = p_209130_1_;
         if (this.field_209139_f < 0) {
            this.field_209139_f += this.field_209136_c.getList().size();
         }

         if (this.field_209139_f >= this.field_209136_c.getList().size()) {
            this.field_209139_f -= this.field_209136_c.getList().size();
         }

         Suggestion suggestion = this.field_209136_c.getList().get(this.field_209139_f);
         GuiCommandBlockBase.this.commandTextField.setSuggestion(GuiCommandBlockBase.func_212339_b(GuiCommandBlockBase.this.commandTextField.getText(), suggestion.apply(this.field_212467_d)));
      }

      public void func_209131_a() {
         Suggestion suggestion = this.field_209136_c.getList().get(this.field_209139_f);
         GuiCommandBlockBase.this.field_212342_z = true;
         GuiCommandBlockBase.this.func_209102_a(suggestion.apply(this.field_212467_d));
         int i = suggestion.getRange().getStart() + suggestion.getText().length();
         GuiCommandBlockBase.this.commandTextField.func_212422_f(i);
         GuiCommandBlockBase.this.commandTextField.setSelectionPos(i);
         this.func_209130_b(this.field_209139_f);
         GuiCommandBlockBase.this.field_212342_z = false;
         this.field_209141_h = true;
      }

      public void func_209132_b() {
         GuiCommandBlockBase.this.field_209116_y = null;
      }
   }
}
