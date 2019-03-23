package net.minecraft.client.renderer.model;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IUnbakedModel {
   Collection<ResourceLocation> func_187965_e();

   Collection<ResourceLocation> func_209559_a(Function<ResourceLocation, IUnbakedModel> p_209559_1_, Set<String> p_209559_2_);

   @Nullable
   IBakedModel func_209558_a(Function<ResourceLocation, IUnbakedModel> p_209558_1_, Function<ResourceLocation, TextureAtlasSprite> p_209558_2_, ModelRotation p_209558_3_, boolean p_209558_4_);
}
