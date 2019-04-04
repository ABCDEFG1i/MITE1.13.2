package net.minecraft.entity;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SharedMonsterAttributes {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final IAttribute MAX_HEALTH = (new RangedAttribute(null, "generic.maxHealth", 20.0D, 0.0D, 1024.0D)).setDescription("Max Health").setShouldWatch(true);
   public static final IAttribute FOLLOW_RANGE = (new RangedAttribute(null, "generic.followRange", 32.0D, 0.0D, 2048.0D)).setDescription("Follow Range");
   public static final IAttribute KNOCKBACK_RESISTANCE = (new RangedAttribute(null, "generic.knockbackResistance", 0.0D, 0.0D, 1.0D)).setDescription("Knockback Resistance");
   public static final IAttribute MOVEMENT_SPEED = (new RangedAttribute(null, "generic.movementSpeed", (double)0.7F, 0.0D, 1024.0D)).setDescription("Movement Speed").setShouldWatch(true);
   public static final IAttribute FLYING_SPEED = (new RangedAttribute(null, "generic.flyingSpeed", (double)0.4F, 0.0D, 1024.0D)).setDescription("Flying Speed").setShouldWatch(true);
   public static final IAttribute ATTACK_DAMAGE = new RangedAttribute(null, "generic.attackDamage", 2.0D, 0.0D, 2048.0D);
   public static final IAttribute ATTACK_SPEED = (new RangedAttribute(null, "generic.attackSpeed", 4.0D, 0.0D, 1024.0D)).setShouldWatch(true);
   public static final IAttribute ARMOR = (new RangedAttribute(null, "generic.armor", 0.0D, 0.0D, 30.0D)).setShouldWatch(true);
   public static final IAttribute ARMOR_TOUGHNESS = (new RangedAttribute(null, "generic.armorToughness", 0.0D, 0.0D, 20.0D)).setShouldWatch(true);
   public static final IAttribute LUCK = (new RangedAttribute(null, "generic.luck", 0.0D, -1024.0D, 1024.0D)).setShouldWatch(true);

   public static NBTTagList writeBaseAttributeMapToNBT(AbstractAttributeMap p_111257_0_) {
      NBTTagList nbttaglist = new NBTTagList();

      for(IAttributeInstance iattributeinstance : p_111257_0_.getAllAttributes()) {
         nbttaglist.add(writeAttributeInstanceToNBT(iattributeinstance));
      }

      return nbttaglist;
   }

   private static NBTTagCompound writeAttributeInstanceToNBT(IAttributeInstance p_111261_0_) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      IAttribute iattribute = p_111261_0_.getAttribute();
      nbttagcompound.setString("Name", iattribute.getName());
      nbttagcompound.setDouble("Base", p_111261_0_.getBaseValue());
      Collection<AttributeModifier> collection = p_111261_0_.getModifiers();
      if (collection != null && !collection.isEmpty()) {
         NBTTagList nbttaglist = new NBTTagList();

         for(AttributeModifier attributemodifier : collection) {
            if (attributemodifier.isSaved()) {
               nbttaglist.add(writeAttributeModifierToNBT(attributemodifier));
            }
         }

         nbttagcompound.setTag("Modifiers", nbttaglist);
      }

      return nbttagcompound;
   }

   public static NBTTagCompound writeAttributeModifierToNBT(AttributeModifier p_111262_0_) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setString("Name", p_111262_0_.getName());
      nbttagcompound.setDouble("Amount", p_111262_0_.getAmount());
      nbttagcompound.setInteger("Operation", p_111262_0_.getOperation());
      nbttagcompound.setUniqueId("UUID", p_111262_0_.getID());
      return nbttagcompound;
   }

   public static void setAttributeModifiers(AbstractAttributeMap p_151475_0_, NBTTagList p_151475_1_) {
      for(int i = 0; i < p_151475_1_.size(); ++i) {
         NBTTagCompound nbttagcompound = p_151475_1_.getCompoundTagAt(i);
         IAttributeInstance iattributeinstance = p_151475_0_.getAttributeInstanceByName(nbttagcompound.getString("Name"));
         if (iattributeinstance == null) {
            LOGGER.warn("Ignoring unknown attribute '{}'", nbttagcompound.getString("Name"));
         } else {
            applyModifiersToAttributeInstance(iattributeinstance, nbttagcompound);
         }
      }

   }

   private static void applyModifiersToAttributeInstance(IAttributeInstance p_111258_0_, NBTTagCompound p_111258_1_) {
      p_111258_0_.setBaseValue(p_111258_1_.getDouble("Base"));
      if (p_111258_1_.hasKey("Modifiers", 9)) {
         NBTTagList nbttaglist = p_111258_1_.getTagList("Modifiers", 10);

         for(int i = 0; i < nbttaglist.size(); ++i) {
            AttributeModifier attributemodifier = readAttributeModifierFromNBT(nbttaglist.getCompoundTagAt(i));
            if (attributemodifier != null) {
               AttributeModifier attributemodifier1 = p_111258_0_.getModifier(attributemodifier.getID());
               if (attributemodifier1 != null) {
                  p_111258_0_.removeModifier(attributemodifier1);
               }

               p_111258_0_.applyModifier(attributemodifier);
            }
         }
      }

   }

   @Nullable
   public static AttributeModifier readAttributeModifierFromNBT(NBTTagCompound p_111259_0_) {
      UUID uuid = p_111259_0_.getUniqueId("UUID");

      try {
         return new AttributeModifier(uuid, p_111259_0_.getString("Name"), p_111259_0_.getDouble("Amount"), p_111259_0_.getInteger("Operation"));
      } catch (Exception exception) {
         LOGGER.warn("Unable to create attribute: {}", exception.getMessage());
         return null;
      }
   }
}
