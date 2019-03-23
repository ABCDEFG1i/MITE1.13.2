package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;

public class EnterBlockTrigger implements ICriterionTrigger<EnterBlockTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("enter_block");
   private final Map<PlayerAdvancements, EnterBlockTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<EnterBlockTrigger.Instance> p_192165_2_) {
      EnterBlockTrigger.Listeners enterblocktrigger$listeners = this.listeners.get(p_192165_1_);
      if (enterblocktrigger$listeners == null) {
         enterblocktrigger$listeners = new EnterBlockTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, enterblocktrigger$listeners);
      }

      enterblocktrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<EnterBlockTrigger.Instance> p_192164_2_) {
      EnterBlockTrigger.Listeners enterblocktrigger$listeners = this.listeners.get(p_192164_1_);
      if (enterblocktrigger$listeners != null) {
         enterblocktrigger$listeners.remove(p_192164_2_);
         if (enterblocktrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public EnterBlockTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      Block block = null;
      if (p_192166_1_.has("block")) {
         ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(p_192166_1_, "block"));
         if (!IRegistry.field_212618_g.func_212607_c(resourcelocation)) {
            throw new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
         }

         block = IRegistry.field_212618_g.func_82594_a(resourcelocation);
      }

      Map<IProperty<?>, Object> map = null;
      if (p_192166_1_.has("state")) {
         if (block == null) {
            throw new JsonSyntaxException("Can't define block state without a specific block type");
         }

         StateContainer<Block, IBlockState> statecontainer = block.getStateContainer();

         for(Entry<String, JsonElement> entry : JsonUtils.getJsonObject(p_192166_1_, "state").entrySet()) {
            IProperty<?> iproperty = statecontainer.getProperty(entry.getKey());
            if (iproperty == null) {
               throw new JsonSyntaxException("Unknown block state property '" + (String)entry.getKey() + "' for block '" + IRegistry.field_212618_g.func_177774_c(block) + "'");
            }

            String s = JsonUtils.getString(entry.getValue(), entry.getKey());
            Optional<?> optional = iproperty.parseValue(s);
            if (!optional.isPresent()) {
               throw new JsonSyntaxException("Invalid block state value '" + s + "' for property '" + (String)entry.getKey() + "' on block '" + IRegistry.field_212618_g.func_177774_c(block) + "'");
            }

            if (map == null) {
               map = Maps.newHashMap();
            }

            map.put(iproperty, optional.get());
         }
      }

      return new EnterBlockTrigger.Instance(block, map);
   }

   public void trigger(EntityPlayerMP p_192193_1_, IBlockState p_192193_2_) {
      EnterBlockTrigger.Listeners enterblocktrigger$listeners = this.listeners.get(p_192193_1_.getAdvancements());
      if (enterblocktrigger$listeners != null) {
         enterblocktrigger$listeners.trigger(p_192193_2_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final Block block;
      private final Map<IProperty<?>, Object> properties;

      public Instance(@Nullable Block p_i47451_1_, @Nullable Map<IProperty<?>, Object> p_i47451_2_) {
         super(EnterBlockTrigger.ID);
         this.block = p_i47451_1_;
         this.properties = p_i47451_2_;
      }

      public static EnterBlockTrigger.Instance func_203920_a(Block p_203920_0_) {
         return new EnterBlockTrigger.Instance(p_203920_0_, (Map<IProperty<?>, Object>)null);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         if (this.block != null) {
            jsonobject.addProperty("block", IRegistry.field_212618_g.func_177774_c(this.block).toString());
            if (this.properties != null && !this.properties.isEmpty()) {
               JsonObject jsonobject1 = new JsonObject();

               for(Entry<IProperty<?>, ?> entry : this.properties.entrySet()) {
                  jsonobject1.addProperty(entry.getKey().getName(), Util.getValueName(entry.getKey(), entry.getValue()));
               }

               jsonobject.add("state", jsonobject1);
            }
         }

         return jsonobject;
      }

      public boolean test(IBlockState p_192260_1_) {
         if (this.block != null && p_192260_1_.getBlock() != this.block) {
            return false;
         } else {
            if (this.properties != null) {
               for(Entry<IProperty<?>, Object> entry : this.properties.entrySet()) {
                  if (p_192260_1_.get(entry.getKey()) != entry.getValue()) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<EnterBlockTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47452_1_) {
         this.playerAdvancements = p_i47452_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<EnterBlockTrigger.Instance> p_192472_1_) {
         this.listeners.add(p_192472_1_);
      }

      public void remove(ICriterionTrigger.Listener<EnterBlockTrigger.Instance> p_192469_1_) {
         this.listeners.remove(p_192469_1_);
      }

      public void trigger(IBlockState p_192471_1_) {
         List<ICriterionTrigger.Listener<EnterBlockTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<EnterBlockTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_192471_1_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<EnterBlockTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
