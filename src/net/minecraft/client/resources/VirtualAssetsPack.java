package net.minecraft.client.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VirtualAssetsPack extends VanillaPack {
   private final ResourceIndex field_195785_b;

   public VirtualAssetsPack(ResourceIndex p_i48115_1_) {
      super("minecraft", "realms");
      this.field_195785_b = p_i48115_1_;
   }

   @Nullable
   protected InputStream func_195782_c(ResourcePackType p_195782_1_, ResourceLocation p_195782_2_) {
      if (p_195782_1_ == ResourcePackType.CLIENT_RESOURCES) {
         File file1 = this.field_195785_b.getFile(p_195782_2_);
         if (file1 != null && file1.exists()) {
            try {
               return new FileInputStream(file1);
            } catch (FileNotFoundException var5) {
               ;
            }
         }
      }

      return super.func_195782_c(p_195782_1_, p_195782_2_);
   }

   @Nullable
   protected InputStream func_200010_a(String p_200010_1_) {
      File file1 = this.field_195785_b.func_200009_a(p_200010_1_);
      if (file1 != null && file1.exists()) {
         try {
            return new FileInputStream(file1);
         } catch (FileNotFoundException var4) {
            ;
         }
      }

      return super.func_200010_a(p_200010_1_);
   }

   public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType p_195758_1_, String p_195758_2_, int p_195758_3_, Predicate<String> p_195758_4_) {
      Collection<ResourceLocation> collection = super.getAllResourceLocations(p_195758_1_, p_195758_2_, p_195758_3_, p_195758_4_);
      collection.addAll(this.field_195785_b.func_211685_a(p_195758_2_, p_195758_3_, p_195758_4_).stream().map(ResourceLocation::new).collect(Collectors.toList()));
      return collection;
   }
}
