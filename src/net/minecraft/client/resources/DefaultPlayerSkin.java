package net.minecraft.client.resources;

import java.util.UUID;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DefaultPlayerSkin {
   private static final ResourceLocation TEXTURE_STEVE = new ResourceLocation("textures/entity/steve.png");
   private static final ResourceLocation TEXTURE_ALEX = new ResourceLocation("textures/entity/alex.png");

   public static ResourceLocation getDefaultSkinLegacy() {
      return TEXTURE_STEVE;
   }

   public static ResourceLocation getDefaultSkin(UUID p_177334_0_) {
      return isSlimSkin(p_177334_0_) ? TEXTURE_ALEX : TEXTURE_STEVE;
   }

   public static String getSkinType(UUID p_177332_0_) {
      return isSlimSkin(p_177332_0_) ? "slim" : "default";
   }

   private static boolean isSlimSkin(UUID p_177333_0_) {
      return (p_177333_0_.hashCode() & 1) == 1;
   }
}
