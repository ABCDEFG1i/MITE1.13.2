package net.minecraft.entity.ai.attributes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.LowerStringMap;

public abstract class AbstractAttributeMap {
   protected final Map<IAttribute, IAttributeInstance> attributes = Maps.newHashMap();
   protected final Map<String, IAttributeInstance> attributesByName = new LowerStringMap<>();
   protected final Multimap<IAttribute, IAttribute> descendantsByParent = HashMultimap.create();

   public IAttributeInstance getAttributeInstance(IAttribute p_111151_1_) {
      return this.attributes.get(p_111151_1_);
   }

   @Nullable
   public IAttributeInstance getAttributeInstanceByName(String p_111152_1_) {
      return this.attributesByName.get(p_111152_1_);
   }

   //MITEMODDED
   public IAttributeInstance replaceAttribute(IAttribute attribute){
      IAttributeInstance attributeInstance = this.createInstance(attribute);
      this.attributesByName.replace(attribute.getName(),attributeInstance);
      this.attributes.replace(attribute,attributeInstance);
      return attributeInstance;
   }

   public IAttributeInstance registerAttribute(IAttribute p_111150_1_) {
      if (this.attributesByName.containsKey(p_111150_1_.getName())) {
         throw new IllegalArgumentException("Attribute is already registered!");
      } else {
         IAttributeInstance iattributeinstance = this.createInstance(p_111150_1_);
         this.attributesByName.put(p_111150_1_.getName(), iattributeinstance);
         this.attributes.put(p_111150_1_, iattributeinstance);

         for(IAttribute iattribute = p_111150_1_.getParent(); iattribute != null; iattribute = iattribute.getParent()) {
            this.descendantsByParent.put(iattribute, p_111150_1_);
         }

         return iattributeinstance;
      }
   }

   protected abstract IAttributeInstance createInstance(IAttribute p_180376_1_);

   public Collection<IAttributeInstance> getAllAttributes() {
      return this.attributesByName.values();
   }

   public void onAttributeModified(IAttributeInstance p_180794_1_) {
   }

   public void removeAttributeModifiers(Multimap<String, AttributeModifier> p_111148_1_) {
      for(Entry<String, AttributeModifier> entry : p_111148_1_.entries()) {
         IAttributeInstance iattributeinstance = this.getAttributeInstanceByName(entry.getKey());
         if (iattributeinstance != null) {
            iattributeinstance.removeModifier(entry.getValue());
         }
      }

   }

   public void applyAttributeModifiers(Multimap<String, AttributeModifier> p_111147_1_) {
      for(Entry<String, AttributeModifier> entry : p_111147_1_.entries()) {
         IAttributeInstance iattributeinstance = this.getAttributeInstanceByName(entry.getKey());
         if (iattributeinstance != null) {
            iattributeinstance.removeModifier(entry.getValue());
            iattributeinstance.applyModifier(entry.getValue());
         }
      }

   }
}
