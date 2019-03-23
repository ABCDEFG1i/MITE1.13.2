package net.minecraft.util.text;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.Util;

public interface ITextComponent extends Message, Iterable<ITextComponent> {
   ITextComponent setStyle(Style p_150255_1_);

   Style getStyle();

   default ITextComponent appendText(String p_150258_1_) {
      return this.appendSibling(new TextComponentString(p_150258_1_));
   }

   ITextComponent appendSibling(ITextComponent p_150257_1_);

   String getUnformattedComponentText();

   default String getString() {
      StringBuilder stringbuilder = new StringBuilder();
      this.func_212640_c().forEach((p_212635_1_) -> {
         stringbuilder.append(p_212635_1_.getUnformattedComponentText());
      });
      return stringbuilder.toString();
   }

   default String func_212636_a(int p_212636_1_) {
      StringBuilder stringbuilder = new StringBuilder();
      Iterator<ITextComponent> iterator = this.func_212640_c().iterator();

      while(iterator.hasNext()) {
         int i = p_212636_1_ - stringbuilder.length();
         if (i <= 0) {
            break;
         }

         String s = iterator.next().getUnformattedComponentText();
         stringbuilder.append(s.length() <= i ? s : s.substring(0, i));
      }

      return stringbuilder.toString();
   }

   default String getFormattedText() {
      StringBuilder stringbuilder = new StringBuilder();
      String s = "";
      Iterator<ITextComponent> iterator = this.func_212640_c().iterator();

      while(iterator.hasNext()) {
         ITextComponent itextcomponent = iterator.next();
         String s1 = itextcomponent.getUnformattedComponentText();
         if (!s1.isEmpty()) {
            String s2 = itextcomponent.getStyle().getFormattingCode();
            if (!s2.equals(s)) {
               if (!s.isEmpty()) {
                  stringbuilder.append((Object)TextFormatting.RESET);
               }

               stringbuilder.append(s2);
               s = s2;
            }

            stringbuilder.append(s1);
         }
      }

      if (!s.isEmpty()) {
         stringbuilder.append((Object)TextFormatting.RESET);
      }

      return stringbuilder.toString();
   }

   List<ITextComponent> getSiblings();

   Stream<ITextComponent> func_212640_c();

   default Stream<ITextComponent> func_212637_f() {
      return this.func_212640_c().map(ITextComponent::func_212639_b);
   }

   default Iterator<ITextComponent> iterator() {
      return this.func_212637_f().iterator();
   }

   ITextComponent createCopy();

   default ITextComponent func_212638_h() {
      ITextComponent itextcomponent = this.createCopy();
      itextcomponent.setStyle(this.getStyle().createShallowCopy());

      for(ITextComponent itextcomponent1 : this.getSiblings()) {
         itextcomponent.appendSibling(itextcomponent1.func_212638_h());
      }

      return itextcomponent;
   }

   default ITextComponent applyTextStyle(Consumer<Style> p_211710_1_) {
      p_211710_1_.accept(this.getStyle());
      return this;
   }

   default ITextComponent applyTextStyles(TextFormatting... p_211709_1_) {
      for(TextFormatting textformatting : p_211709_1_) {
         this.applyTextStyle(textformatting);
      }

      return this;
   }

   default ITextComponent applyTextStyle(TextFormatting p_211708_1_) {
      Style style = this.getStyle();
      if (p_211708_1_.isColor()) {
         style.setColor(p_211708_1_);
      }

      if (p_211708_1_.isFancyStyling()) {
         switch(p_211708_1_) {
         case OBFUSCATED:
            style.setObfuscated(true);
            break;
         case BOLD:
            style.setBold(true);
            break;
         case STRIKETHROUGH:
            style.setStrikethrough(true);
            break;
         case UNDERLINE:
            style.setUnderlined(true);
            break;
         case ITALIC:
            style.setItalic(true);
         }
      }

      return this;
   }

   static ITextComponent func_212639_b(ITextComponent p_212639_0_) {
      ITextComponent itextcomponent = p_212639_0_.createCopy();
      itextcomponent.setStyle(p_212639_0_.getStyle().createDeepCopy());
      return itextcomponent;
   }

   public static class Serializer implements JsonDeserializer<ITextComponent>, JsonSerializer<ITextComponent> {
      private static final Gson GSON = Util.make(() -> {
         GsonBuilder gsonbuilder = new GsonBuilder();
         gsonbuilder.registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer());
         gsonbuilder.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
         gsonbuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
         return gsonbuilder.create();
      });
      private static final Field JSON_READER_POS_FIELD = Util.make(() -> {
         try {
            new JsonReader(new StringReader(""));
            Field field = JsonReader.class.getDeclaredField("pos");
            field.setAccessible(true);
            return field;
         } catch (NoSuchFieldException nosuchfieldexception) {
            throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", nosuchfieldexception);
         }
      });
      private static final Field JSON_READER_LINESTART_FIELD = Util.make(() -> {
         try {
            new JsonReader(new StringReader(""));
            Field field = JsonReader.class.getDeclaredField("lineStart");
            field.setAccessible(true);
            return field;
         } catch (NoSuchFieldException nosuchfieldexception) {
            throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", nosuchfieldexception);
         }
      });

      public ITextComponent deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         if (p_deserialize_1_.isJsonPrimitive()) {
            return new TextComponentString(p_deserialize_1_.getAsString());
         } else if (!p_deserialize_1_.isJsonObject()) {
            if (p_deserialize_1_.isJsonArray()) {
               JsonArray jsonarray1 = p_deserialize_1_.getAsJsonArray();
               ITextComponent itextcomponent1 = null;

               for(JsonElement jsonelement : jsonarray1) {
                  ITextComponent itextcomponent2 = this.deserialize(jsonelement, jsonelement.getClass(), p_deserialize_3_);
                  if (itextcomponent1 == null) {
                     itextcomponent1 = itextcomponent2;
                  } else {
                     itextcomponent1.appendSibling(itextcomponent2);
                  }
               }

               return itextcomponent1;
            } else {
               throw new JsonParseException("Don't know how to turn " + p_deserialize_1_ + " into a Component");
            }
         } else {
            JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            ITextComponent itextcomponent;
            if (jsonobject.has("text")) {
               itextcomponent = new TextComponentString(jsonobject.get("text").getAsString());
            } else if (jsonobject.has("translate")) {
               String s = jsonobject.get("translate").getAsString();
               if (jsonobject.has("with")) {
                  JsonArray jsonarray = jsonobject.getAsJsonArray("with");
                  Object[] aobject = new Object[jsonarray.size()];

                  for(int i = 0; i < aobject.length; ++i) {
                     aobject[i] = this.deserialize(jsonarray.get(i), p_deserialize_2_, p_deserialize_3_);
                     if (aobject[i] instanceof TextComponentString) {
                        TextComponentString textcomponentstring = (TextComponentString)aobject[i];
                        if (textcomponentstring.getStyle().isEmpty() && textcomponentstring.getSiblings().isEmpty()) {
                           aobject[i] = textcomponentstring.getText();
                        }
                     }
                  }

                  itextcomponent = new TextComponentTranslation(s, aobject);
               } else {
                  itextcomponent = new TextComponentTranslation(s);
               }
            } else if (jsonobject.has("score")) {
               JsonObject jsonobject1 = jsonobject.getAsJsonObject("score");
               if (!jsonobject1.has("name") || !jsonobject1.has("objective")) {
                  throw new JsonParseException("A score component needs a least a name and an objective");
               }

               itextcomponent = new TextComponentScore(JsonUtils.getString(jsonobject1, "name"), JsonUtils.getString(jsonobject1, "objective"));
               if (jsonobject1.has("value")) {
                  ((TextComponentScore)itextcomponent).setValue(JsonUtils.getString(jsonobject1, "value"));
               }
            } else if (jsonobject.has("selector")) {
               itextcomponent = new TextComponentSelector(JsonUtils.getString(jsonobject, "selector"));
            } else {
               if (!jsonobject.has("keybind")) {
                  throw new JsonParseException("Don't know how to turn " + p_deserialize_1_ + " into a Component");
               }

               itextcomponent = new TextComponentKeybind(JsonUtils.getString(jsonobject, "keybind"));
            }

            if (jsonobject.has("extra")) {
               JsonArray jsonarray2 = jsonobject.getAsJsonArray("extra");
               if (jsonarray2.size() <= 0) {
                  throw new JsonParseException("Unexpected empty array of components");
               }

               for(int j = 0; j < jsonarray2.size(); ++j) {
                  itextcomponent.appendSibling(this.deserialize(jsonarray2.get(j), p_deserialize_2_, p_deserialize_3_));
               }
            }

            itextcomponent.setStyle(p_deserialize_3_.deserialize(p_deserialize_1_, Style.class));
            return itextcomponent;
         }
      }

      private void serializeChatStyle(Style p_150695_1_, JsonObject p_150695_2_, JsonSerializationContext p_150695_3_) {
         JsonElement jsonelement = p_150695_3_.serialize(p_150695_1_);
         if (jsonelement.isJsonObject()) {
            JsonObject jsonobject = (JsonObject)jsonelement;

            for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
               p_150695_2_.add(entry.getKey(), entry.getValue());
            }
         }

      }

      public JsonElement serialize(ITextComponent p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject jsonobject = new JsonObject();
         if (!p_serialize_1_.getStyle().isEmpty()) {
            this.serializeChatStyle(p_serialize_1_.getStyle(), jsonobject, p_serialize_3_);
         }

         if (!p_serialize_1_.getSiblings().isEmpty()) {
            JsonArray jsonarray = new JsonArray();

            for(ITextComponent itextcomponent : p_serialize_1_.getSiblings()) {
               jsonarray.add(this.serialize(itextcomponent, itextcomponent.getClass(), p_serialize_3_));
            }

            jsonobject.add("extra", jsonarray);
         }

         if (p_serialize_1_ instanceof TextComponentString) {
            jsonobject.addProperty("text", ((TextComponentString)p_serialize_1_).getText());
         } else if (p_serialize_1_ instanceof TextComponentTranslation) {
            TextComponentTranslation textcomponenttranslation = (TextComponentTranslation)p_serialize_1_;
            jsonobject.addProperty("translate", textcomponenttranslation.getKey());
            if (textcomponenttranslation.getFormatArgs() != null && textcomponenttranslation.getFormatArgs().length > 0) {
               JsonArray jsonarray1 = new JsonArray();

               for(Object object : textcomponenttranslation.getFormatArgs()) {
                  if (object instanceof ITextComponent) {
                     jsonarray1.add(this.serialize((ITextComponent)object, object.getClass(), p_serialize_3_));
                  } else {
                     jsonarray1.add(new JsonPrimitive(String.valueOf(object)));
                  }
               }

               jsonobject.add("with", jsonarray1);
            }
         } else if (p_serialize_1_ instanceof TextComponentScore) {
            TextComponentScore textcomponentscore = (TextComponentScore)p_serialize_1_;
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.addProperty("name", textcomponentscore.getName());
            jsonobject1.addProperty("objective", textcomponentscore.getObjective());
            jsonobject1.addProperty("value", textcomponentscore.getUnformattedComponentText());
            jsonobject.add("score", jsonobject1);
         } else if (p_serialize_1_ instanceof TextComponentSelector) {
            TextComponentSelector textcomponentselector = (TextComponentSelector)p_serialize_1_;
            jsonobject.addProperty("selector", textcomponentselector.getSelector());
         } else {
            if (!(p_serialize_1_ instanceof TextComponentKeybind)) {
               throw new IllegalArgumentException("Don't know how to serialize " + p_serialize_1_ + " as a Component");
            }

            TextComponentKeybind textcomponentkeybind = (TextComponentKeybind)p_serialize_1_;
            jsonobject.addProperty("keybind", textcomponentkeybind.getKeybind());
         }

         return jsonobject;
      }

      public static String toJson(ITextComponent p_150696_0_) {
         return GSON.toJson(p_150696_0_);
      }

      public static JsonElement toJsonTree(ITextComponent p_200528_0_) {
         return GSON.toJsonTree(p_200528_0_);
      }

      @Nullable
      public static ITextComponent fromJson(String p_150699_0_) {
         return JsonUtils.fromJson(GSON, p_150699_0_, ITextComponent.class, false);
      }

      @Nullable
      public static ITextComponent fromJson(JsonElement p_197672_0_) {
         return GSON.fromJson(p_197672_0_, ITextComponent.class);
      }

      @Nullable
      public static ITextComponent fromJsonLenient(String p_186877_0_) {
         return JsonUtils.fromJson(GSON, p_186877_0_, ITextComponent.class, true);
      }

      public static ITextComponent fromJson(com.mojang.brigadier.StringReader p_197671_0_) {
         try {
            JsonReader jsonreader = new JsonReader(new StringReader(p_197671_0_.getRemaining()));
            jsonreader.setLenient(false);
            ITextComponent itextcomponent = GSON.getAdapter(ITextComponent.class).read(jsonreader);
            p_197671_0_.setCursor(p_197671_0_.getCursor() + func_197673_a(jsonreader));
            return itextcomponent;
         } catch (IOException ioexception) {
            throw new JsonParseException(ioexception);
         }
      }

      private static int func_197673_a(JsonReader p_197673_0_) {
         try {
            return JSON_READER_POS_FIELD.getInt(p_197673_0_) - JSON_READER_LINESTART_FIELD.getInt(p_197673_0_) + 1;
         } catch (IllegalAccessException illegalaccessexception) {
            throw new IllegalStateException("Couldn't read position of JsonReader", illegalaccessexception);
         }
      }
   }
}
