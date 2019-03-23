package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.LowerStringMap;

public class AttributeMap extends AbstractAttributeMap {
   private final Set<IAttributeInstance> dirtyInstances = Sets.newHashSet();
   protected final Map<String, IAttributeInstance> instancesByName = new LowerStringMap<>();

   public ModifiableAttributeInstance getAttributeInstance(IAttribute p_111151_1_) {
      return (ModifiableAttributeInstance)super.getAttributeInstance(p_111151_1_);
   }

   public ModifiableAttributeInstance getAttributeInstanceByName(String p_111152_1_) {
      IAttributeInstance iattributeinstance = super.getAttributeInstanceByName(p_111152_1_);
      if (iattributeinstance == null) {
         iattributeinstance = this.instancesByName.get(p_111152_1_);
      }

      return (ModifiableAttributeInstance)iattributeinstance;
   }

   public IAttributeInstance registerAttribute(IAttribute p_111150_1_) {
      IAttributeInstance iattributeinstance = super.registerAttribute(p_111150_1_);
      if (p_111150_1_ instanceof RangedAttribute && ((RangedAttribute)p_111150_1_).getDescription() != null) {
         this.instancesByName.put(((RangedAttribute)p_111150_1_).getDescription(), iattributeinstance);
      }

      return iattributeinstance;
   }

   protected IAttributeInstance createInstance(IAttribute p_180376_1_) {
      return new ModifiableAttributeInstance(this, p_180376_1_);
   }

   public void onAttributeModified(IAttributeInstance p_180794_1_) {
      if (p_180794_1_.getAttribute().getShouldWatch()) {
         this.dirtyInstances.add(p_180794_1_);
      }

      for(IAttribute iattribute : this.descendantsByParent.get(p_180794_1_.getAttribute())) {
         ModifiableAttributeInstance modifiableattributeinstance = this.getAttributeInstance(iattribute);
         if (modifiableattributeinstance != null) {
            modifiableattributeinstance.flagForUpdate();
         }
      }

   }

   public Set<IAttributeInstance> getDirtyInstances() {
      return this.dirtyInstances;
   }

   public Collection<IAttributeInstance> getWatchedAttributes() {
      Set<IAttributeInstance> set = Sets.newHashSet();

      for(IAttributeInstance iattributeinstance : this.getAllAttributes()) {
         if (iattributeinstance.getAttribute().getShouldWatch()) {
            set.add(iattributeinstance);
         }
      }

      return set;
   }
}
