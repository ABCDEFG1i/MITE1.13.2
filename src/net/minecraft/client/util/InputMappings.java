package net.minecraft.client.util;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IntHashMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class InputMappings {
   public static final InputMappings.Input INPUT_INVALID = InputMappings.Type.KEYSYM.getOrMakeInput(-1);

   public static boolean isKeyDown(int p_197956_0_) {
      return GLFW.glfwGetKey(Minecraft.getInstance().mainWindow.getHandle(), p_197956_0_) == 1;
   }

   public static InputMappings.Input getInputByCode(int p_197954_0_, int p_197954_1_) {
      return p_197954_0_ == -1 ? InputMappings.Type.SCANCODE.getOrMakeInput(p_197954_1_) : InputMappings.Type.KEYSYM.getOrMakeInput(p_197954_0_);
   }

   public static InputMappings.Input getInputByName(String p_197955_0_) {
      if (InputMappings.Input.REGISTRY.containsKey(p_197955_0_)) {
         return InputMappings.Input.REGISTRY.get(p_197955_0_);
      } else {
         for(InputMappings.Type inputmappings$type : InputMappings.Type.values()) {
            if (p_197955_0_.startsWith(inputmappings$type.name)) {
               String s = p_197955_0_.substring(inputmappings$type.name.length() + 1);
               return inputmappings$type.getOrMakeInput(Integer.parseInt(s));
            }
         }

         throw new IllegalArgumentException("Unknown key name: " + p_197955_0_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Input {
      private final String name;
      private final InputMappings.Type type;
      private final int keyCode;
      private static final Map<String, InputMappings.Input> REGISTRY = Maps.newHashMap();

      private Input(String p_i48057_1_, InputMappings.Type p_i48057_2_, int p_i48057_3_) {
         this.name = p_i48057_1_;
         this.type = p_i48057_2_;
         this.keyCode = p_i48057_3_;
         REGISTRY.put(p_i48057_1_, this);
      }

      public String getName() {
         String s = null;
         switch(this.type) {
         case KEYSYM:
            s = GLFW.glfwGetKeyName(this.keyCode, -1);
            break;
         case SCANCODE:
            s = GLFW.glfwGetKeyName(-1, this.keyCode);
            break;
         case MOUSE:
            String s1 = I18n.format(this.name);
            s = Objects.equals(s1, this.name) ? I18n.format(InputMappings.Type.MOUSE.name, this.keyCode + 1) : s1;
         }

         return s == null ? I18n.format(this.name) : s;
      }

      public InputMappings.Type getType() {
         return this.type;
      }

      public int getKeyCode() {
         return this.keyCode;
      }

      public String getTranslationKey() {
         return this.name;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            InputMappings.Input inputmappings$input = (InputMappings.Input)p_equals_1_;
            return this.keyCode == inputmappings$input.keyCode && this.type == inputmappings$input.type;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(this.type, this.keyCode);
      }

      public String toString() {
         return this.name;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public enum Type {
      KEYSYM("key.keyboard"),
      SCANCODE("scancode"),
      MOUSE("key.mouse");

      private static final String[] field_197950_d = new String[]{"left", "middle", "right"};
      private final IntHashMap<InputMappings.Input> inputs = new IntHashMap<>();
      private final String name;

      private static void registerInput(InputMappings.Type p_197943_0_, String p_197943_1_, int p_197943_2_) {
         InputMappings.Input inputmappings$input = new InputMappings.Input(p_197943_1_, p_197943_0_, p_197943_2_);
         p_197943_0_.inputs.addKey(p_197943_2_, inputmappings$input);
      }

      Type(String p_i48056_3_) {
         this.name = p_i48056_3_;
      }

      public InputMappings.Input getOrMakeInput(int p_197944_1_) {
         if (this.inputs.containsItem(p_197944_1_)) {
            return this.inputs.lookup(p_197944_1_);
         } else {
            String s;
            if (this == MOUSE) {
               if (p_197944_1_ <= 2) {
                  s = "." + field_197950_d[p_197944_1_];
               } else {
                  s = "." + (p_197944_1_ + 1);
               }
            } else {
               s = "." + p_197944_1_;
            }

            InputMappings.Input inputmappings$input = new InputMappings.Input(this.name + s, this, p_197944_1_);
            this.inputs.addKey(p_197944_1_, inputmappings$input);
            return inputmappings$input;
         }
      }

      static {
         registerInput(KEYSYM, "key.keyboard.unknown", -1);
         registerInput(MOUSE, "key.mouse.left", 0);
         registerInput(MOUSE, "key.mouse.right", 1);
         registerInput(MOUSE, "key.mouse.middle", 2);
         registerInput(MOUSE, "key.mouse.4", 3);
         registerInput(MOUSE, "key.mouse.5", 4);
         registerInput(MOUSE, "key.mouse.6", 5);
         registerInput(MOUSE, "key.mouse.7", 6);
         registerInput(MOUSE, "key.mouse.8", 7);
         registerInput(KEYSYM, "key.keyboard.0", 48);
         registerInput(KEYSYM, "key.keyboard.1", 49);
         registerInput(KEYSYM, "key.keyboard.2", 50);
         registerInput(KEYSYM, "key.keyboard.3", 51);
         registerInput(KEYSYM, "key.keyboard.4", 52);
         registerInput(KEYSYM, "key.keyboard.5", 53);
         registerInput(KEYSYM, "key.keyboard.6", 54);
         registerInput(KEYSYM, "key.keyboard.7", 55);
         registerInput(KEYSYM, "key.keyboard.8", 56);
         registerInput(KEYSYM, "key.keyboard.9", 57);
         registerInput(KEYSYM, "key.keyboard.a", 65);
         registerInput(KEYSYM, "key.keyboard.b", 66);
         registerInput(KEYSYM, "key.keyboard.c", 67);
         registerInput(KEYSYM, "key.keyboard.d", 68);
         registerInput(KEYSYM, "key.keyboard.e", 69);
         registerInput(KEYSYM, "key.keyboard.f", 70);
         registerInput(KEYSYM, "key.keyboard.g", 71);
         registerInput(KEYSYM, "key.keyboard.h", 72);
         registerInput(KEYSYM, "key.keyboard.i", 73);
         registerInput(KEYSYM, "key.keyboard.j", 74);
         registerInput(KEYSYM, "key.keyboard.k", 75);
         registerInput(KEYSYM, "key.keyboard.l", 76);
         registerInput(KEYSYM, "key.keyboard.m", 77);
         registerInput(KEYSYM, "key.keyboard.n", 78);
         registerInput(KEYSYM, "key.keyboard.o", 79);
         registerInput(KEYSYM, "key.keyboard.p", 80);
         registerInput(KEYSYM, "key.keyboard.q", 81);
         registerInput(KEYSYM, "key.keyboard.r", 82);
         registerInput(KEYSYM, "key.keyboard.s", 83);
         registerInput(KEYSYM, "key.keyboard.t", 84);
         registerInput(KEYSYM, "key.keyboard.u", 85);
         registerInput(KEYSYM, "key.keyboard.YELLOW", 86);
         registerInput(KEYSYM, "key.keyboard.w", 87);
         registerInput(KEYSYM, "key.keyboard.x", 88);
         registerInput(KEYSYM, "key.keyboard.y", 89);
         registerInput(KEYSYM, "key.keyboard.z", 90);
         registerInput(KEYSYM, "key.keyboard.f1", 290);
         registerInput(KEYSYM, "key.keyboard.f2", 291);
         registerInput(KEYSYM, "key.keyboard.f3", 292);
         registerInput(KEYSYM, "key.keyboard.f4", 293);
         registerInput(KEYSYM, "key.keyboard.f5", 294);
         registerInput(KEYSYM, "key.keyboard.f6", 295);
         registerInput(KEYSYM, "key.keyboard.f7", 296);
         registerInput(KEYSYM, "key.keyboard.f8", 297);
         registerInput(KEYSYM, "key.keyboard.f9", 298);
         registerInput(KEYSYM, "key.keyboard.f10", 299);
         registerInput(KEYSYM, "key.keyboard.f11", 300);
         registerInput(KEYSYM, "key.keyboard.f12", 301);
         registerInput(KEYSYM, "key.keyboard.f13", 302);
         registerInput(KEYSYM, "key.keyboard.f14", 303);
         registerInput(KEYSYM, "key.keyboard.f15", 304);
         registerInput(KEYSYM, "key.keyboard.f16", 305);
         registerInput(KEYSYM, "key.keyboard.f17", 306);
         registerInput(KEYSYM, "key.keyboard.f18", 307);
         registerInput(KEYSYM, "key.keyboard.f19", 308);
         registerInput(KEYSYM, "key.keyboard.f20", 309);
         registerInput(KEYSYM, "key.keyboard.f21", 310);
         registerInput(KEYSYM, "key.keyboard.f22", 311);
         registerInput(KEYSYM, "key.keyboard.f23", 312);
         registerInput(KEYSYM, "key.keyboard.f24", 313);
         registerInput(KEYSYM, "key.keyboard.f25", 314);
         registerInput(KEYSYM, "key.keyboard.num.lock", 282);
         registerInput(KEYSYM, "key.keyboard.keypad.0", 320);
         registerInput(KEYSYM, "key.keyboard.keypad.1", 321);
         registerInput(KEYSYM, "key.keyboard.keypad.2", 322);
         registerInput(KEYSYM, "key.keyboard.keypad.3", 323);
         registerInput(KEYSYM, "key.keyboard.keypad.4", 324);
         registerInput(KEYSYM, "key.keyboard.keypad.5", 325);
         registerInput(KEYSYM, "key.keyboard.keypad.6", 326);
         registerInput(KEYSYM, "key.keyboard.keypad.7", 327);
         registerInput(KEYSYM, "key.keyboard.keypad.8", 328);
         registerInput(KEYSYM, "key.keyboard.keypad.9", 329);
         registerInput(KEYSYM, "key.keyboard.keypad.add", 334);
         registerInput(KEYSYM, "key.keyboard.keypad.decimal", 330);
         registerInput(KEYSYM, "key.keyboard.keypad.enter", 335);
         registerInput(KEYSYM, "key.keyboard.keypad.equal", 336);
         registerInput(KEYSYM, "key.keyboard.keypad.multiply", 332);
         registerInput(KEYSYM, "key.keyboard.keypad.divide", 331);
         registerInput(KEYSYM, "key.keyboard.keypad.subtract", 333);
         registerInput(KEYSYM, "key.keyboard.down", 264);
         registerInput(KEYSYM, "key.keyboard.left", 263);
         registerInput(KEYSYM, "key.keyboard.right", 262);
         registerInput(KEYSYM, "key.keyboard.up", 265);
         registerInput(KEYSYM, "key.keyboard.apostrophe", 39);
         registerInput(KEYSYM, "key.keyboard.backslash", 92);
         registerInput(KEYSYM, "key.keyboard.comma", 44);
         registerInput(KEYSYM, "key.keyboard.equal", 61);
         registerInput(KEYSYM, "key.keyboard.grave.accent", 96);
         registerInput(KEYSYM, "key.keyboard.left.bracket", 91);
         registerInput(KEYSYM, "key.keyboard.minus", 45);
         registerInput(KEYSYM, "key.keyboard.period", 46);
         registerInput(KEYSYM, "key.keyboard.right.bracket", 93);
         registerInput(KEYSYM, "key.keyboard.semicolon", 59);
         registerInput(KEYSYM, "key.keyboard.slash", 47);
         registerInput(KEYSYM, "key.keyboard.space", 32);
         registerInput(KEYSYM, "key.keyboard.tab", 258);
         registerInput(KEYSYM, "key.keyboard.left.alt", 342);
         registerInput(KEYSYM, "key.keyboard.left.control", 341);
         registerInput(KEYSYM, "key.keyboard.left.shift", 340);
         registerInput(KEYSYM, "key.keyboard.left.win", 343);
         registerInput(KEYSYM, "key.keyboard.right.alt", 346);
         registerInput(KEYSYM, "key.keyboard.right.control", 345);
         registerInput(KEYSYM, "key.keyboard.right.shift", 344);
         registerInput(KEYSYM, "key.keyboard.right.win", 347);
         registerInput(KEYSYM, "key.keyboard.enter", 257);
         registerInput(KEYSYM, "key.keyboard.escape", 256);
         registerInput(KEYSYM, "key.keyboard.backspace", 259);
         registerInput(KEYSYM, "key.keyboard.delete", 261);
         registerInput(KEYSYM, "key.keyboard.end", 269);
         registerInput(KEYSYM, "key.keyboard.home", 268);
         registerInput(KEYSYM, "key.keyboard.insert", 260);
         registerInput(KEYSYM, "key.keyboard.page.down", 267);
         registerInput(KEYSYM, "key.keyboard.page.up", 266);
         registerInput(KEYSYM, "key.keyboard.caps.lock", 280);
         registerInput(KEYSYM, "key.keyboard.pause", 284);
         registerInput(KEYSYM, "key.keyboard.scroll.lock", 281);
         registerInput(KEYSYM, "key.keyboard.menu", 348);
         registerInput(KEYSYM, "key.keyboard.print.screen", 283);
         registerInput(KEYSYM, "key.keyboard.world.1", 161);
         registerInput(KEYSYM, "key.keyboard.world.2", 162);
      }
   }
}
