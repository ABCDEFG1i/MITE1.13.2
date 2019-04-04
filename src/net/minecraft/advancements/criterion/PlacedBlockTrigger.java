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
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.WorldServer;

public class PlacedBlockTrigger implements ICriterionTrigger<PlacedBlockTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("placed_block");
   private final Map<PlayerAdvancements, PlacedBlockTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<PlacedBlockTrigger.Instance> p_192165_2_) {
      PlacedBlockTrigger.Listeners placedblocktrigger$listeners = this.listeners.get(p_192165_1_);
      if (placedblocktrigger$listeners == null) {
         placedblocktrigger$listeners = new PlacedBlockTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, placedblocktrigger$listeners);
      }

      placedblocktrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<PlacedBlockTrigger.Instance> p_192164_2_) {
      PlacedBlockTrigger.Listeners placedblocktrigger$listeners = this.listeners.get(p_192164_1_);
      if (placedblocktrigger$listeners != null) {
         placedblocktrigger$listeners.remove(p_192164_2_);
         if (placedblocktrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public PlacedBlockTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
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
               throw new JsonSyntaxException("Unknown block state property '" + entry.getKey() + "' for block '" + IRegistry.field_212618_g.func_177774_c(block) + "'");
            }

            String s = JsonUtils.getString(entry.getValue(), entry.getKey());
            Optional<?> optional = iproperty.parseValue(s);
            if (!optional.isPresent()) {
               throw new JsonSyntaxException("Invalid block state value '" + s + "' for property '" + entry.getKey() + "' on block '" + IRegistry.field_212618_g.func_177774_c(block) + "'");
            }

            if (map == null) {
               map = Maps.newHashMap();
            }

            map.put(iproperty, optional.get());
         }
      }

      LocationPredicate locationpredicate = LocationPredicate.deserialize(p_192166_1_.get("location"));
      ItemPredicate itempredicate = ItemPredicate.deserialize(p_192166_1_.get("item"));
      return new PlacedBlockTrigger.Instance(block, map, locationpredicate, itempredicate);
   }

   public void trigger(EntityPlayerMP p_193173_1_, BlockPos p_193173_2_, ItemStack p_193173_3_) {
      IBlockState iblockstate = p_193173_1_.world.getBlockState(p_193173_2_);
      PlacedBlockTrigger.Listeners placedblocktrigger$listeners = this.listeners.get(p_193173_1_.getAdvancements());
      if (placedblocktrigger$listeners != null) {
         placedblocktrigger$listeners.trigger(iblockstate, p_193173_2_, p_193173_1_.getServerWorld(), p_193173_3_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final Block block;
      private final Map<IProperty<?>, Object> properties;
      private final LocationPredicate location;
      private final ItemPredicate item;

      public Instance(@Nullable Block p_i47566_1_, @Nullable Map<IProperty<?>, Object> p_i47566_2_, LocationPredicate p_i47566_3_, ItemPredicate p_i47566_4_) {
         super(PlacedBlockTrigger.ID);
         this.block = p_i47566_1_;
         this.properties = p_i47566_2_;
         this.location = p_i47566_3_;
         this.item = p_i47566_4_;
      }

      public static PlacedBlockTrigger.Instance func_203934_a(Block p_203934_0_) {
         return new PlacedBlockTrigger.Instance(p_203934_0_, null, LocationPredicate.ANY, ItemPredicate.ANY);
      }

      public boolean test(IBlockState p_193210_1_, BlockPos p_193210_2_, WorldServer p_193210_3_, ItemStack p_193210_4_) {
         if (this.block != null && p_193210_1_.getBlock() != this.block) {
            return false;
         } else {
            if (this.properties != null) {
               for(Entry<IProperty<?>, Object> entry : this.properties.entrySet()) {
                  if (p_193210_1_.get(entry.getKey()) != entry.getValue()) {
                     return false;
                  }
               }
            }

            if (!this.location.test(p_193210_3_, (float)p_193210_2_.getX(), (float)p_193210_2_.getY(), (float)p_193210_2_.getZ())) {
               return false;
            } else {
               return this.item.test(p_193210_4_);
            }
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         if (this.block != null) {
            jsonobject.addProperty("block", IRegistry.field_212618_g.func_177774_c(this.block).toString());
         }

         if (this.properties != null) {
            JsonObject jsonobject1 = new JsonObject();

            for(Entry<IProperty<?>, Object> entry : this.properties.entrySet()) {
               jsonobject1.addProperty(entry.getKey().getName(), Util.getValueName(entry.getKey(), entry.getValue()));
            }

            jsonobject.add("state", jsonobject1);
         }

         jsonobject.add("location", this.location.serialize());
         jsonobject.add("item", this.item.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<PlacedBlockTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47567_1_) {
         this.playerAdvancements = p_i47567_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<PlacedBlockTrigger.Instance> p_193490_1_) {
         this.listeners.add(p_193490_1_);
      }

      public void remove(ICriterionTrigger.Listener<PlacedBlockTrigger.Instance> p_193487_1_) {
         this.listeners.remove(p_193487_1_);
      }

      public void trigger(IBlockState p_193489_1_, BlockPos p_193489_2_, WorldServer p_193489_3_, ItemStack p_193489_4_) {
         List<ICriterionTrigger.Listener<PlacedBlockTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<PlacedBlockTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_193489_1_, p_193489_2_, p_193489_3_, p_193489_4_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<PlacedBlockTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
