package net.minecraft.server.gui;

import com.mojang.util.QueueLogAppender;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftServerGui extends JComponent {
   private static final Font SERVER_GUI_FONT = new Font("Monospaced", 0, 12);
   private static final Logger LOGGER = LogManager.getLogger();
   private final DedicatedServer server;
   private Thread field_206932_d;

   public static void createServerGui(final DedicatedServer p_120016_0_) {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception var3) {
      }

      MinecraftServerGui minecraftservergui = new MinecraftServerGui(p_120016_0_);
      JFrame jframe = new JFrame("Minecraft server");
      jframe.add(minecraftservergui);
      jframe.pack();
      jframe.setLocationRelativeTo(null);
      jframe.setVisible(true);
      jframe.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent p_windowClosing_1_) {
            p_120016_0_.initiateShutdown();

            while(!p_120016_0_.isServerStopped()) {
               try {
                  Thread.sleep(100L);
               } catch (InterruptedException interruptedexception) {
                  interruptedexception.printStackTrace();
               }
            }

            System.exit(0);
         }
      });
      minecraftservergui.func_206931_a();
   }

   public MinecraftServerGui(DedicatedServer p_i2362_1_) {
      this.server = p_i2362_1_;
      this.setPreferredSize(new Dimension(854, 480));
      this.setLayout(new BorderLayout());

      try {
         this.add(this.getLogComponent(), "Center");
         this.add(this.getStatsComponent(), "West");
      } catch (Exception exception) {
         LOGGER.error("Couldn't build server GUI", exception);
      }

   }

   private JComponent getStatsComponent() throws Exception {
      JPanel jpanel = new JPanel(new BorderLayout());
      jpanel.add(new StatsComponent(this.server), "North");
      jpanel.add(this.getPlayerListComponent(), "Center");
      jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
      return jpanel;
   }

   private JComponent getPlayerListComponent() throws Exception {
      JList<?> jlist = new PlayerListComponent(this.server);
      JScrollPane jscrollpane = new JScrollPane(jlist, 22, 30);
      jscrollpane.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
      return jscrollpane;
   }

   private JComponent getLogComponent() throws Exception {
      JPanel jpanel = new JPanel(new BorderLayout());
      JTextArea jtextarea = new JTextArea();
      JScrollPane jscrollpane = new JScrollPane(jtextarea, 22, 30);
      jtextarea.setEditable(false);
      jtextarea.setFont(SERVER_GUI_FONT);
      JTextField jtextfield = new JTextField();
      jtextfield.addActionListener((p_210465_2_) -> {
         String s = jtextfield.getText().trim();
         if (!s.isEmpty()) {
            this.server.func_195581_a(s, this.server.getCommandSource());
         }

         jtextfield.setText("");
      });
      jtextarea.addFocusListener(new FocusAdapter() {
         public void focusGained(FocusEvent p_focusGained_1_) {
         }
      });
      jpanel.add(jscrollpane, "Center");
      jpanel.add(jtextfield, "South");
      jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
      this.field_206932_d = new Thread(() -> {
         String s;
         while((s = QueueLogAppender.getNextLogEvent("ServerGuiConsole")) != null) {
            this.appendLine(jtextarea, jscrollpane, s);
         }

      });
      this.field_206932_d.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      this.field_206932_d.setDaemon(true);
      return jpanel;
   }

   public void func_206931_a() {
      this.field_206932_d.start();
   }

   public void appendLine(JTextArea p_164247_1_, JScrollPane p_164247_2_, String p_164247_3_) {
      if (!SwingUtilities.isEventDispatchThread()) {
         SwingUtilities.invokeLater(() -> {
            this.appendLine(p_164247_1_, p_164247_2_, p_164247_3_);
         });
      } else {
         Document document = p_164247_1_.getDocument();
         JScrollBar jscrollbar = p_164247_2_.getVerticalScrollBar();
         boolean flag = false;
         if (p_164247_2_.getViewport().getView() == p_164247_1_) {
            flag = (double)jscrollbar.getValue() + jscrollbar.getSize().getHeight() + (double)(SERVER_GUI_FONT.getSize() * 4) > (double)jscrollbar.getMaximum();
         }

         try {
            document.insertString(document.getLength(), p_164247_3_, null);
         } catch (BadLocationException var8) {
         }

         if (flag) {
            jscrollbar.setValue(Integer.MAX_VALUE);
         }

      }
   }
}
