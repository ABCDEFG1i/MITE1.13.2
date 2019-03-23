package net.minecraft.client.renderer.model;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IBakedModel {
   List<BakedQuad> func_200117_a(@Nullable IBlockState p_200117_1_, @Nullable EnumFacing p_200117_2_, Random p_200117_3_);

   boolean func_177555_b();

   boolean func_177556_c();

   boolean func_188618_c();

   TextureAtlasSprite func_177554_e();

   ItemCameraTransforms func_177552_f();

   ItemOverrideList func_188617_f();
}
