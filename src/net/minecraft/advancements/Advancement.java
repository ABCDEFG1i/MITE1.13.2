package net.minecraft.advancements;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ArrayUtils;

public class Advancement {
   private final Advancement parent;
   private final DisplayInfo display;
   private final AdvancementRewards rewards;
   private final ResourceLocation id;
   private final Map<String, Criterion> criteria;
   private final String[][] requirements;
   private final Set<Advancement> children = Sets.newLinkedHashSet();
   private final ITextComponent displayText;

   public Advancement(ResourceLocation p_i47472_1_, @Nullable Advancement p_i47472_2_, @Nullable DisplayInfo p_i47472_3_, AdvancementRewards p_i47472_4_, Map<String, Criterion> p_i47472_5_, String[][] p_i47472_6_) {
      this.id = p_i47472_1_;
      this.display = p_i47472_3_;
      this.criteria = ImmutableMap.copyOf(p_i47472_5_);
      this.parent = p_i47472_2_;
      this.rewards = p_i47472_4_;
      this.requirements = p_i47472_6_;
      if (p_i47472_2_ != null) {
         p_i47472_2_.addChild(this);
      }

      if (p_i47472_3_ == null) {
         this.displayText = new TextComponentString(p_i47472_1_.toString());
      } else {
         ITextComponent itextcomponent = p_i47472_3_.getTitle();
         TextFormatting textformatting = p_i47472_3_.getFrame().getFormat();
         ITextComponent itextcomponent1 = itextcomponent.func_212638_h().applyTextStyle(textformatting).appendText("\n").appendSibling(p_i47472_3_.getDescription());
         ITextComponent itextcomponent2 = itextcomponent.func_212638_h().applyTextStyle((p_211567_1_) -> {
            p_211567_1_.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, itextcomponent1));
         });
         this.displayText = (new TextComponentString("[")).appendSibling(itextcomponent2).appendText("]").applyTextStyle(textformatting);
      }

   }

   public Advancement.Builder copy() {
      return new Advancement.Builder(this.parent == null ? null : this.parent.getId(), this.display, this.rewards, this.criteria, this.requirements);
   }

   @Nullable
   public Advancement getParent() {
      return this.parent;
   }

   @Nullable
   public DisplayInfo getDisplay() {
      return this.display;
   }

   public AdvancementRewards getRewards() {
      return this.rewards;
   }

   public String toString() {
      return "SimpleAdvancement{id=" + this.getId() + ", parent=" + (this.parent == null ? "null" : this.parent.getId()) + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + '}';
   }

   public Iterable<Advancement> getChildren() {
      return this.children;
   }

   public Map<String, Criterion> getCriteria() {
      return this.criteria;
   }

   @OnlyIn(Dist.CLIENT)
   public int getRequirementCount() {
      return this.requirements.length;
   }

   public void addChild(Advancement p_192071_1_) {
      this.children.add(p_192071_1_);
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Advancement)) {
         return false;
      } else {
         Advancement advancement = (Advancement)p_equals_1_;
         return this.id.equals(advancement.id);
      }
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public String[][] getRequirements() {
      return this.requirements;
   }

   public ITextComponent getDisplayText() {
      return this.displayText;
   }

   public static class Builder {
      private ResourceLocation parentId;
      private Advancement parent;
      private DisplayInfo display;
      private AdvancementRewards rewards = AdvancementRewards.EMPTY;
      private Map<String, Criterion> criteria = Maps.newLinkedHashMap();
      private String[][] requirements;
      private RequirementsStrategy requirementsStrategy = RequirementsStrategy.AND;

      private Builder(@Nullable ResourceLocation p_i47414_1_, @Nullable DisplayInfo p_i47414_2_, AdvancementRewards p_i47414_3_, Map<String, Criterion> p_i47414_4_, String[][] p_i47414_5_) {
         this.parentId = p_i47414_1_;
         this.display = p_i47414_2_;
         this.rewards = p_i47414_3_;
         this.criteria = p_i47414_4_;
         this.requirements = p_i47414_5_;
      }

      private Builder() {
      }

      public static Advancement.Builder builder() {
         return new Advancement.Builder();
      }

      public Advancement.Builder withParent(Advancement p_203905_1_) {
         this.parent = p_203905_1_;
         return this;
      }

      public Advancement.Builder withParentId(ResourceLocation p_200272_1_) {
         this.parentId = p_200272_1_;
         return this;
      }

      public Advancement.Builder withDisplay(IItemProvider p_203902_1_, ITextComponent p_203902_2_, ITextComponent p_203902_3_, @Nullable ResourceLocation p_203902_4_, FrameType p_203902_5_, boolean p_203902_6_, boolean p_203902_7_, boolean p_203902_8_) {
         return this.withDisplay(new DisplayInfo(new ItemStack(p_203902_1_.asItem()), p_203902_2_, p_203902_3_, p_203902_4_, p_203902_5_, p_203902_6_, p_203902_7_, p_203902_8_));
      }

      public Advancement.Builder withDisplay(DisplayInfo p_203903_1_) {
         this.display = p_203903_1_;
         return this;
      }

      public Advancement.Builder withRewards(AdvancementRewards.Builder p_200271_1_) {
         return this.withRewards(p_200271_1_.build());
      }

      public Advancement.Builder withRewards(AdvancementRewards p_200274_1_) {
         this.rewards = p_200274_1_;
         return this;
      }

      public Advancement.Builder withCriterion(String p_200275_1_, ICriterionInstance p_200275_2_) {
         return this.withCriterion(p_200275_1_, new Criterion(p_200275_2_));
      }

      public Advancement.Builder withCriterion(String p_200276_1_, Criterion p_200276_2_) {
         if (this.criteria.containsKey(p_200276_1_)) {
            throw new IllegalArgumentException("Duplicate criterion " + p_200276_1_);
         } else {
            this.criteria.put(p_200276_1_, p_200276_2_);
            return this;
         }
      }

      public Advancement.Builder withRequirementsStrategy(RequirementsStrategy p_200270_1_) {
         this.requirementsStrategy = p_200270_1_;
         return this;
      }

      public boolean resolveParent(Function<ResourceLocation, Advancement> p_192058_1_) {
         if (this.parentId == null) {
            return true;
         } else {
            if (this.parent == null) {
               this.parent = p_192058_1_.apply(this.parentId);
            }

            return this.parent != null;
         }
      }

      public Advancement build(ResourceLocation p_192056_1_) {
         if (!this.resolveParent((p_199750_0_) -> {
            return null;
         })) {
            throw new IllegalStateException("Tried to build incomplete advancement!");
         } else {
            if (this.requirements == null) {
               this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
            }

            return new Advancement(p_192056_1_, this.parent, this.display, this.rewards, this.criteria, this.requirements);
         }
      }

      public Advancement register(Consumer<Advancement> p_203904_1_, String p_203904_2_) {
         Advancement advancement = this.build(new ResourceLocation(p_203904_2_));
         p_203904_1_.accept(advancement);
         return advancement;
      }

      public JsonObject serialize() {
         if (this.requirements == null) {
            this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
         }

         JsonObject jsonobject = new JsonObject();
         if (this.parent != null) {
            jsonobject.addProperty("parent", this.parent.getId().toString());
         } else if (this.parentId != null) {
            jsonobject.addProperty("parent", this.parentId.toString());
         }

         if (this.display != null) {
            jsonobject.add("display", this.display.serialize());
         }

         jsonobject.add("rewards", this.rewards.serialize());
         JsonObject jsonobject1 = new JsonObject();

         for(Entry<String, Criterion> entry : this.criteria.entrySet()) {
            jsonobject1.add(entry.getKey(), entry.getValue().serialize());
         }

         jsonobject.add("criteria", jsonobject1);
         JsonArray jsonarray1 = new JsonArray();

         for(String[] astring : this.requirements) {
            JsonArray jsonarray = new JsonArray();

            for(String s : astring) {
               jsonarray.add(s);
            }

            jsonarray1.add(jsonarray);
         }

         jsonobject.add("requirements", jsonarray1);
         return jsonobject;
      }

      public void writeTo(PacketBuffer p_192057_1_) {
         if (this.parentId == null) {
            p_192057_1_.writeBoolean(false);
         } else {
            p_192057_1_.writeBoolean(true);
            p_192057_1_.writeResourceLocation(this.parentId);
         }

         if (this.display == null) {
            p_192057_1_.writeBoolean(false);
         } else {
            p_192057_1_.writeBoolean(true);
            this.display.write(p_192057_1_);
         }

         Criterion.serializeToNetwork(this.criteria, p_192057_1_);
         p_192057_1_.writeVarInt(this.requirements.length);

         for(String[] astring : this.requirements) {
            p_192057_1_.writeVarInt(astring.length);

            for(String s : astring) {
               p_192057_1_.writeString(s);
            }
         }

      }

      public String toString() {
         return "Task Advancement{parentId=" + this.parentId + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + '}';
      }

      public static Advancement.Builder deserialize(JsonObject p_192059_0_, JsonDeserializationContext p_192059_1_) {
         ResourceLocation resourcelocation = p_192059_0_.has("parent") ? new ResourceLocation(JsonUtils.getString(p_192059_0_, "parent")) : null;
         DisplayInfo displayinfo = p_192059_0_.has("display") ? DisplayInfo.deserialize(JsonUtils.getJsonObject(p_192059_0_, "display"), p_192059_1_) : null;
         AdvancementRewards advancementrewards = JsonUtils.deserializeClass(p_192059_0_, "rewards", AdvancementRewards.EMPTY, p_192059_1_, AdvancementRewards.class);
         Map<String, Criterion> map = Criterion.criteriaFromJson(JsonUtils.getJsonObject(p_192059_0_, "criteria"), p_192059_1_);
         if (map.isEmpty()) {
            throw new JsonSyntaxException("Advancement criteria cannot be empty");
         } else {
            JsonArray jsonarray = JsonUtils.getJsonArray(p_192059_0_, "requirements", new JsonArray());
            String[][] astring = new String[jsonarray.size()][];

            for(int i = 0; i < jsonarray.size(); ++i) {
               JsonArray jsonarray1 = JsonUtils.getJsonArray(jsonarray.get(i), "requirements[" + i + "]");
               astring[i] = new String[jsonarray1.size()];

               for(int j = 0; j < jsonarray1.size(); ++j) {
                  astring[i][j] = JsonUtils.getString(jsonarray1.get(j), "requirements[" + i + "][" + j + "]");
               }
            }

            if (astring.length == 0) {
               astring = new String[map.size()][];
               int k = 0;

               for(String s2 : map.keySet()) {
                  astring[k++] = new String[]{s2};
               }
            }

            for(String[] astring1 : astring) {
               if (astring1.length == 0 && map.isEmpty()) {
                  throw new JsonSyntaxException("Requirement entry cannot be empty");
               }

               for(String s : astring1) {
                  if (!map.containsKey(s)) {
                     throw new JsonSyntaxException("Unknown required criterion '" + s + "'");
                  }
               }
            }

            for(String s1 : map.keySet()) {
               boolean flag = false;

               for(String[] astring2 : astring) {
                  if (ArrayUtils.contains(astring2, s1)) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  throw new JsonSyntaxException("Criterion '" + s1 + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
               }
            }

            return new Advancement.Builder(resourcelocation, displayinfo, advancementrewards, map, astring);
         }
      }

      public static Advancement.Builder readFrom(PacketBuffer p_192060_0_) {
         ResourceLocation resourcelocation = p_192060_0_.readBoolean() ? p_192060_0_.readResourceLocation() : null;
         DisplayInfo displayinfo = p_192060_0_.readBoolean() ? DisplayInfo.read(p_192060_0_) : null;
         Map<String, Criterion> map = Criterion.criteriaFromNetwork(p_192060_0_);
         String[][] astring = new String[p_192060_0_.readVarInt()][];

         for(int i = 0; i < astring.length; ++i) {
            astring[i] = new String[p_192060_0_.readVarInt()];

            for(int j = 0; j < astring[i].length; ++j) {
               astring[i][j] = p_192060_0_.readString(32767);
            }
         }

         return new Advancement.Builder(resourcelocation, displayinfo, AdvancementRewards.EMPTY, map, astring);
      }

      public Map<String, Criterion> func_200277_c() {
         return this.criteria;
      }
   }
}
