package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerVillagerArmor;
import net.minecraft.client.renderer.entity.model.ModelZombieVillager;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderZombieVillager extends RenderBiped<EntityZombieVillager> {
   private static final ResourceLocation ZOMBIE_VILLAGER_TEXTURES = new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png");
   private static final ResourceLocation ZOMBIE_VILLAGER_FARMER_LOCATION = new ResourceLocation("textures/entity/zombie_villager/zombie_farmer.png");
   private static final ResourceLocation ZOMBIE_VILLAGER_LIBRARIAN_LOC = new ResourceLocation("textures/entity/zombie_villager/zombie_librarian.png");
   private static final ResourceLocation ZOMBIE_VILLAGER_PRIEST_LOCATION = new ResourceLocation("textures/entity/zombie_villager/zombie_priest.png");
   private static final ResourceLocation ZOMBIE_VILLAGER_SMITH_LOCATION = new ResourceLocation("textures/entity/zombie_villager/zombie_smith.png");
   private static final ResourceLocation ZOMBIE_VILLAGER_BUTCHER_LOCATION = new ResourceLocation("textures/entity/zombie_villager/zombie_butcher.png");

   public RenderZombieVillager(RenderManager p_i47186_1_) {
      super(p_i47186_1_, new ModelZombieVillager(), 0.5F);
      this.addLayer(new LayerVillagerArmor(this));
   }

   protected ResourceLocation getEntityTexture(EntityZombieVillager p_110775_1_) {
      switch(p_110775_1_.getProfession()) {
      case 0:
         return ZOMBIE_VILLAGER_FARMER_LOCATION;
      case 1:
         return ZOMBIE_VILLAGER_LIBRARIAN_LOC;
      case 2:
         return ZOMBIE_VILLAGER_PRIEST_LOCATION;
      case 3:
         return ZOMBIE_VILLAGER_SMITH_LOCATION;
      case 4:
         return ZOMBIE_VILLAGER_BUTCHER_LOCATION;
      case 5:
      default:
         return ZOMBIE_VILLAGER_TEXTURES;
      }
   }

   protected void applyRotations(EntityZombieVillager p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
      if (p_77043_1_.isConverting()) {
         p_77043_3_ += (float)(Math.cos((double)p_77043_1_.ticksExisted * 3.25D) * Math.PI * 0.25D);
      }

      super.applyRotations(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
   }
}
