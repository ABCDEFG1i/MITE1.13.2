package net.minecraft.entity.ai.attributes;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IAttributeInstance {
   IAttribute getAttribute();

   double getBaseValue();

   void setBaseValue(double p_111128_1_);

   Collection<AttributeModifier> getModifiersByOperation(int p_111130_1_);

   Collection<AttributeModifier> getModifiers();

   boolean hasModifier(AttributeModifier p_180374_1_);

   @Nullable
   AttributeModifier getModifier(UUID p_111127_1_);

   void applyModifier(AttributeModifier p_111121_1_);

   void removeModifier(AttributeModifier p_111124_1_);

   void removeModifier(UUID p_188479_1_);

   @OnlyIn(Dist.CLIENT)
   void removeAllModifiers();

   double getAttributeValue();
}
