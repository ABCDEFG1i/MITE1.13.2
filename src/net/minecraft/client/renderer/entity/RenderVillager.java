package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.model.ModelVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderVillager extends RenderLiving<EntityVillager> {
   private static final ResourceLocation VILLAGER_TEXTURES = new ResourceLocation("textures/entity/villager/villager.png");
   private static final ResourceLocation FARMER_VILLAGER_TEXTURES = new ResourceLocation("textures/entity/villager/farmer.png");
   private static final ResourceLocation LIBRARIAN_VILLAGER_TEXTURES = new ResourceLocation("textures/entity/villager/librarian.png");
   private static final ResourceLocation PRIEST_VILLAGER_TEXTURES = new ResourceLocation("textures/entity/villager/priest.png");
   private static final ResourceLocation SMITH_VILLAGER_TEXTURES = new ResourceLocation("textures/entity/villager/smith.png");
   private static final ResourceLocation BUTCHER_VILLAGER_TEXTURES = new ResourceLocation("textures/entity/villager/butcher.png");

   public RenderVillager(RenderManager p_i46132_1_) {
      super(p_i46132_1_, new ModelVillager(0.0F), 0.5F);
      this.addLayer(new LayerCustomHead(this.getMainModel().func_205072_a()));
   }

   public ModelVillager getMainModel() {
      return (ModelVillager)super.getMainModel();
   }

   protected ResourceLocation getEntityTexture(EntityVillager p_110775_1_) {
      switch(p_110775_1_.getProfession()) {
      case 0:
         return FARMER_VILLAGER_TEXTURES;
      case 1:
         return LIBRARIAN_VILLAGER_TEXTURES;
      case 2:
         return PRIEST_VILLAGER_TEXTURES;
      case 3:
         return SMITH_VILLAGER_TEXTURES;
      case 4:
         return BUTCHER_VILLAGER_TEXTURES;
      case 5:
      default:
         return VILLAGER_TEXTURES;
      }
   }

   protected void preRenderCallback(EntityVillager p_77041_1_, float p_77041_2_) {
      float f = 0.9375F;
      if (p_77041_1_.getGrowingAge() < 0) {
         f = (float)((double)f * 0.5D);
         this.shadowSize = 0.25F;
      } else {
         this.shadowSize = 0.5F;
      }

      GlStateManager.scalef(f, f, f);
   }
}
