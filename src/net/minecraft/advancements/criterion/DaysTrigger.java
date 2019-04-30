package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Map;

public class DaysTrigger implements ICriterionTrigger<DaysTrigger.Instance> {
    public static final ResourceLocation ID = new ResourceLocation("days");
    private final Map<PlayerAdvancements, DaysTrigger.Listeners> listeners = Maps.newHashMap();

    public ResourceLocation getId() {
        return ID;
    }

    public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<DaysTrigger.Instance> p_192165_2_) {
        DaysTrigger.Listeners daytrigger$listeners = this.listeners.get(p_192165_1_);
        if (daytrigger$listeners == null) {
            daytrigger$listeners = new DaysTrigger.Listeners(p_192165_1_);
            this.listeners.put(p_192165_1_, daytrigger$listeners);
        }

        daytrigger$listeners.add(p_192165_2_);
    }

    public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<DaysTrigger.Instance> p_192164_2_) {
        DaysTrigger.Listeners daytrigger$listeners = this.listeners.get(p_192164_1_);
        if (daytrigger$listeners != null) {
            daytrigger$listeners.remove(p_192164_2_);
            if (daytrigger$listeners.isEmpty()) {
                this.listeners.remove(p_192164_1_);
            }
        }

    }

    public void removeAllListeners(PlayerAdvancements p_192167_1_) {
        this.listeners.remove(p_192167_1_);
    }

    //WARNING DO NOT use this
    public DaysTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
        return new DaysTrigger.Instance(p_192166_1_.getAsInt());
    }

    public void trigger(World world, long day) {
        for (EntityPlayer player : world.playerEntities) {
            if (player instanceof EntityPlayerMP) {
                DaysTrigger.Listeners daytrigger$listeners = this.listeners.get(
                        ((EntityPlayerMP) player).getAdvancements());
                if (daytrigger$listeners != null) {
                    daytrigger$listeners.trigger(day);
                }
            }
        }

    }

    public static class Instance extends AbstractCriterionInstance {
        private final int min;

        public Instance(int min) {
            super(DaysTrigger.ID);
            this.min = min;
        }
    }

    static class Listeners {
        private final ArrayList<Listener<Instance>> listeners = Lists.newArrayList();
        private final PlayerAdvancements playerAdvancements;

        public Listeners(PlayerAdvancements p_i47496_1_) {
            this.playerAdvancements = p_i47496_1_;
        }

        public void add(ICriterionTrigger.Listener<DaysTrigger.Instance> p_193502_1_) {
            this.listeners.add(p_193502_1_);
        }

        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public void remove(ICriterionTrigger.Listener<DaysTrigger.Instance> p_193500_1_) {
            this.listeners.remove(p_193500_1_);
        }

        public void trigger(long day) {
            //WARNING There cannot replace with for-each because the grantCriterion() method will delete the element in the array and cause ConcurrentModificationException
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < listeners.size(); i++) {
                Listener<Instance> listener = listeners.get(i);
                if (day >= listener.getCriterionInstance().min) {
                    listener.grantCriterion(playerAdvancements);
                }
            }
        }
    }
}

