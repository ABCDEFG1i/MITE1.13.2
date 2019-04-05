package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModifiableAttributeInstance implements IAttributeInstance {
   private final AbstractAttributeMap attributeMap;
   private final IAttribute genericAttribute;
   private final Map<Integer, Set<AttributeModifier>> mapByOperation = Maps.newHashMap();
   private final Map<String, Set<AttributeModifier>> mapByName = Maps.newHashMap();
   private final Map<UUID, AttributeModifier> mapByUUID = Maps.newHashMap();
   private double baseValue;
   private boolean needsUpdate = true;
   private double cachedValue;

   public ModifiableAttributeInstance(AbstractAttributeMap p_i1608_1_, IAttribute p_i1608_2_) {
      this.attributeMap = p_i1608_1_;
      this.genericAttribute = p_i1608_2_;
      this.baseValue = p_i1608_2_.getDefaultValue();

      for(int i = 0; i < 3; ++i) {
         this.mapByOperation.put(i, Sets.newHashSet());
      }

   }

   public IAttribute getAttribute() {
      return this.genericAttribute;
   }

   public double getBaseValue() {
      return this.baseValue;
   }

   public void setBaseValue(double p_111128_1_) {
         this.baseValue = p_111128_1_;
         this.flagForUpdate();
   }

   public Collection<AttributeModifier> getModifiersByOperation(int p_111130_1_) {
      return this.mapByOperation.get(p_111130_1_);
   }

   public Collection<AttributeModifier> getModifiers() {
      Set<AttributeModifier> set = Sets.newHashSet();

      for(int i = 0; i < 3; ++i) {
         set.addAll(this.getModifiersByOperation(i));
      }

      return set;
   }

   @Nullable
   public AttributeModifier getModifier(UUID p_111127_1_) {
      return this.mapByUUID.get(p_111127_1_);
   }

   public boolean hasModifier(AttributeModifier p_180374_1_) {
      return this.mapByUUID.get(p_180374_1_.getID()) != null;
   }

   public void applyModifier(AttributeModifier p_111121_1_) {
      if (this.getModifier(p_111121_1_.getID()) != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      } else {
         Set<AttributeModifier> set = this.mapByName.get(p_111121_1_.getName());
         if (set == null) {
            set = Sets.newHashSet();
            this.mapByName.put(p_111121_1_.getName(), set);
         }

         this.mapByOperation.get(p_111121_1_.getOperation()).add(p_111121_1_);
         set.add(p_111121_1_);
         this.mapByUUID.put(p_111121_1_.getID(), p_111121_1_);
         this.flagForUpdate();
      }
   }

   protected void flagForUpdate() {
      this.needsUpdate = true;
      this.attributeMap.onAttributeModified(this);
   }

   public void removeModifier(AttributeModifier p_111124_1_) {
      for(int i = 0; i < 3; ++i) {
         Set<AttributeModifier> set = this.mapByOperation.get(i);
         set.remove(p_111124_1_);
      }

      Set<AttributeModifier> set1 = this.mapByName.get(p_111124_1_.getName());
      if (set1 != null) {
         set1.remove(p_111124_1_);
         if (set1.isEmpty()) {
            this.mapByName.remove(p_111124_1_.getName());
         }
      }

      this.mapByUUID.remove(p_111124_1_.getID());
      this.flagForUpdate();
   }

   public void removeModifier(UUID p_188479_1_) {
      AttributeModifier attributemodifier = this.getModifier(p_188479_1_);
      if (attributemodifier != null) {
         this.removeModifier(attributemodifier);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void removeAllModifiers() {
      Collection<AttributeModifier> collection = this.getModifiers();
      if (collection != null) {
         for(AttributeModifier attributemodifier : Lists.newArrayList(collection)) {
            this.removeModifier(attributemodifier);
         }

      }
   }

   public double getAttributeValue() {
      if (this.needsUpdate) {
         this.cachedValue = this.computeValue();
         this.needsUpdate = false;
      }

      return this.cachedValue;
   }

   private double computeValue() {
      double d0 = this.getBaseValue();

      for(AttributeModifier attributemodifier : this.getAppliedModifiers(0)) {
         d0 += attributemodifier.getAmount();
      }

      double d1 = d0;

      for(AttributeModifier attributemodifier1 : this.getAppliedModifiers(1)) {
         d1 += d0 * attributemodifier1.getAmount();
      }

      for(AttributeModifier attributemodifier2 : this.getAppliedModifiers(2)) {
         d1 *= 1.0D + attributemodifier2.getAmount();
      }

      return this.genericAttribute.clampValue(d1);
   }

   private Collection<AttributeModifier> getAppliedModifiers(int p_180375_1_) {
      Set<AttributeModifier> set = Sets.newHashSet(this.getModifiersByOperation(p_180375_1_));

      for(IAttribute iattribute = this.genericAttribute.getParent(); iattribute != null; iattribute = iattribute.getParent()) {
         IAttributeInstance iattributeinstance = this.attributeMap.getAttributeInstance(iattribute);
         if (iattributeinstance != null) {
            set.addAll(iattributeinstance.getModifiersByOperation(p_180375_1_));
         }
      }

      return set;
   }
}
