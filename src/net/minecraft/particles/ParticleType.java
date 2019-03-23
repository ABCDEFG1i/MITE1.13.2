package net.minecraft.particles;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class ParticleType<T extends IParticleData> {
   private final ResourceLocation resourceLocation;
   private final boolean alwaysShow;
   private final IParticleData.IDeserializer<T> deserializer;

   protected ParticleType(ResourceLocation p_i49348_1_, boolean p_i49348_2_, IParticleData.IDeserializer<T> p_i49348_3_) {
      this.resourceLocation = p_i49348_1_;
      this.alwaysShow = p_i49348_2_;
      this.deserializer = p_i49348_3_;
   }

   public static void registerAll() {
      register("ambient_entity_effect", false);
      register("angry_villager", false);
      register("barrier", false);
      register("block", false, BlockParticleData.DESERIALIZER);
      register("bubble", false);
      register("cloud", false);
      register("crit", false);
      register("damage_indicator", true);
      register("dragon_breath", false);
      register("dripping_lava", false);
      register("dripping_water", false);
      register("dust", false, RedstoneParticleData.DESERIALIZER);
      register("effect", false);
      register("elder_guardian", true);
      register("enchanted_hit", false);
      register("enchant", false);
      register("end_rod", false);
      register("entity_effect", false);
      register("explosion_emitter", true);
      register("explosion", true);
      register("falling_dust", false, BlockParticleData.DESERIALIZER);
      register("firework", false);
      register("fishing", false);
      register("flame", false);
      register("happy_villager", false);
      register("heart", false);
      register("instant_effect", false);
      register("item", false, ItemParticleData.DESERIALIZER);
      register("item_slime", false);
      register("item_snowball", false);
      register("large_smoke", false);
      register("lava", false);
      register("mycelium", false);
      register("note", false);
      register("poof", true);
      register("portal", false);
      register("rain", false);
      register("smoke", false);
      register("spit", true);
      register("squid_ink", true);
      register("sweep_attack", true);
      register("totem_of_undying", false);
      register("underwater", false);
      register("splash", false);
      register("witch", false);
      register("bubble_pop", false);
      register("current_down", false);
      register("bubble_column_up", false);
      register("nautilus", false);
      register("dolphin", false);
   }

   public ResourceLocation getId() {
      return this.resourceLocation;
   }

   public boolean getAlwaysShow() {
      return this.alwaysShow;
   }

   public IParticleData.IDeserializer<T> getDeserializer() {
      return this.deserializer;
   }

   private static void register(String p_197572_0_, boolean p_197572_1_) {
      IRegistry.field_212632_u.func_82595_a(new ResourceLocation(p_197572_0_), new BasicParticleType(new ResourceLocation(p_197572_0_), p_197572_1_));
   }

   private static <T extends IParticleData> void register(String p_197573_0_, boolean p_197573_1_, IParticleData.IDeserializer<T> p_197573_2_) {
      IRegistry.field_212632_u.func_82595_a(new ResourceLocation(p_197573_0_), new ParticleType<>(new ResourceLocation(p_197573_0_), p_197573_1_, p_197573_2_));
   }
}
