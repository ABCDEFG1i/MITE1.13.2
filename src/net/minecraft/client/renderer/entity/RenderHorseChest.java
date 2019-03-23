package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.entity.model.ModelHorseArmorChests;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHorseChest extends RenderAbstractHorse<EntityHorse> {
   private static final Map<Class<?>, ResourceLocation> field_195635_a = Maps.newHashMap(ImmutableMap.of(EntityDonkey.class, new ResourceLocation("textures/entity/horse/donkey.png"), EntityMule.class, new ResourceLocation("textures/entity/horse/mule.png")));

   public RenderHorseChest(RenderManager p_i48144_1_, float p_i48144_2_) {
      super(p_i48144_1_, new ModelHorseArmorChests(), p_i48144_2_);
   }

   protected ResourceLocation getEntityTexture(AbstractHorse p_110775_1_) {
      return field_195635_a.get(p_110775_1_.getClass());
   }
}
