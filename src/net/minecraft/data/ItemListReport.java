package net.minecraft.data;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class ItemListReport implements IDataProvider {
   private final DataGenerator generator;

   public ItemListReport(DataGenerator p_i48263_1_) {
      this.generator = p_i48263_1_;
   }

   public void act(DirectoryCache p_200398_1_) throws IOException {
      JsonObject jsonobject = new JsonObject();

      for(Item item : IRegistry.field_212630_s) {
         ResourceLocation resourcelocation = IRegistry.field_212630_s.func_177774_c(item);
         JsonObject jsonobject1 = new JsonObject();
         jsonobject1.addProperty("protocol_id", Item.getIdFromItem(item));
         jsonobject.add(resourcelocation.toString(), jsonobject1);
      }

      Path path = this.generator.getOutputFolder().resolve("reports/items.json");
      Files.createDirectories(path.getParent());

      try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
         String s = (new GsonBuilder()).setPrettyPrinting().create().toJson(jsonobject);
         bufferedwriter.write(s);
      }

   }

   public String getName() {
      return "Item List";
   }
}
