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

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;

public class LevelTrigger implements ICriterionTrigger<LevelTrigger.Instance> {
    public static final ResourceLocation ID = new ResourceLocation("level");
    private final Map<PlayerAdvancements, LevelTrigger.Listeners> listeners = Maps.newHashMap();
    public ResourceLocation getId() {
        return ID;
    }

    public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<LevelTrigger.Instance> p_192165_2_) {
        LevelTrigger.Listeners leveltrigger$listeners = this.listeners.get(p_192165_1_);
        if (leveltrigger$listeners == null) {
            leveltrigger$listeners = new LevelTrigger.Listeners(p_192165_1_);
            this.listeners.put(p_192165_1_, leveltrigger$listeners);
        }

        leveltrigger$listeners.add(p_192165_2_);
    }

    public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<LevelTrigger.Instance> p_192164_2_) {
        LevelTrigger.Listeners leveltrigger$listeners = this.listeners.get(p_192164_1_);
        if (leveltrigger$listeners != null) {
            leveltrigger$listeners.remove(p_192164_2_);
            if (leveltrigger$listeners.isEmpty()) {
                this.listeners.remove(p_192164_1_);
            }
        }

    }

    public void removeAllListeners(PlayerAdvancements p_192167_1_) {
        this.listeners.remove(p_192167_1_);
    }

    //WARNING DO NOT use this
    public LevelTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
        return new LevelTrigger.Instance(p_192166_1_.getAsInt());
    }

    public void trigger(EntityPlayerMP p_193182_1_, int level) {
        LevelTrigger.Listeners leveltrigger$listeners = this.listeners.get(p_193182_1_.getAdvancements());
        if (leveltrigger$listeners != null) {
            leveltrigger$listeners.trigger(p_193182_1_,level);
        }

    }

    public static class Instance extends AbstractCriterionInstance {
        private final int min;
        private boolean isGlobal;

        public Instance(int min) {
            super(LevelTrigger.ID);
            this.min = min;
        }
        public LevelTrigger.Instance setGlobal(){
            this.isGlobal = true;
            return this;
        }
    }

    static class Listeners {
        private final List<Listener<Instance>> listeners = Lists.newArrayList();
        private final PlayerAdvancements playerAdvancements;

        public Listeners(PlayerAdvancements p_i47496_1_) {
            this.playerAdvancements = p_i47496_1_;
        }

        public void add(ICriterionTrigger.Listener<LevelTrigger.Instance> p_193502_1_) {
            this.listeners.add(p_193502_1_);
        }

        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public void remove(ICriterionTrigger.Listener<LevelTrigger.Instance> p_193500_1_) {
            this.listeners.remove(p_193500_1_);
        }

        public void trigger(EntityPlayerMP playerMP,int level) {
            synchronized (this) {
                //WARNING There cannot replace with for-each because the grantCriterion() method will delete the element in the array and cause ConcurrentModificationException
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < listeners.size(); i++) {
                    Listener<Instance> listener = listeners.get(i);
                    if (level >= listener.getCriterionInstance().min) {
                        listener.grantCriterion(playerAdvancements);
                        if (listener.getCriterionInstance().isGlobal){
                            for (EntityPlayer playerEntity : playerMP.world.playerEntities) {
                                if (playerEntity instanceof EntityPlayerMP){
                                    listener.grantCriterion(((EntityPlayerMP) playerEntity).getAdvancements());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
