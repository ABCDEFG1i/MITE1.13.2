package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerBipedArmor extends LayerArmorBase<ModelBiped> {
   public LayerBipedArmor(RenderLivingBase<?> p_i46116_1_) {
      super(p_i46116_1_);
   }

   protected void initArmor() {
      this.modelLeggings = new ModelBiped(0.5F);
      this.modelArmor = new ModelBiped(1.0F);
   }

   protected void setModelSlotVisible(ModelBiped p_188359_1_, EntityEquipmentSlot p_188359_2_) {
      this.setModelVisible(p_188359_1_);
      switch(p_188359_2_) {
      case HEAD:
         p_188359_1_.bipedHead.showModel = true;
         p_188359_1_.bipedHeadwear.showModel = true;
         break;
      case CHEST:
         p_188359_1_.bipedBody.showModel = true;
         p_188359_1_.bipedRightArm.showModel = true;
         p_188359_1_.bipedLeftArm.showModel = true;
         break;
      case LEGS:
         p_188359_1_.bipedBody.showModel = true;
         p_188359_1_.bipedRightLeg.showModel = true;
         p_188359_1_.bipedLeftLeg.showModel = true;
         break;
      case FEET:
         p_188359_1_.bipedRightLeg.showModel = true;
         p_188359_1_.bipedLeftLeg.showModel = true;
      }

   }

   protected void setModelVisible(ModelBiped p_177194_1_) {
      p_177194_1_.setVisible(false);
   }
}
