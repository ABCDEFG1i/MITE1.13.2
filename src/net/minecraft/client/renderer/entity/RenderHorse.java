package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.ModelHorseArmorBase;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHorse extends RenderAbstractHorse<EntityHorse> {
   private static final Map<String, ResourceLocation> LAYERED_LOCATION_CACHE = Maps.newHashMap();

   public RenderHorse(RenderManager p_i47205_1_) {
      super(p_i47205_1_, new ModelHorseArmorBase(), 1.1F);
   }

   protected ResourceLocation getEntityTexture(AbstractHorse p_110775_1_) {
      EntityHorse entityhorse = (EntityHorse)p_110775_1_;
      String s = entityhorse.getHorseTexture();
      ResourceLocation resourcelocation = LAYERED_LOCATION_CACHE.get(s);
      if (resourcelocation == null) {
         resourcelocation = new ResourceLocation(s);
         Minecraft.getInstance().getTextureManager().loadTexture(resourcelocation, new LayeredTexture(entityhorse.getVariantTexturePaths()));
         LAYERED_LOCATION_CACHE.put(s, resourcelocation);
      }

      return resourcelocation;
   }
}
