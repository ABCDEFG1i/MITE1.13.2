package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderPotion extends RenderSprite<EntityPotion> {
   public RenderPotion(RenderManager p_i46136_1_, ItemRenderer p_i46136_2_) {
      super(p_i46136_1_, Items.POTION, p_i46136_2_);
   }

   public ItemStack getStackToRender(EntityPotion p_177082_1_) {
      return p_177082_1_.getPotion();
   }
}
