package net.minecraft.util;

import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SoundEvent {
   private final ResourceLocation soundName;

   public SoundEvent(ResourceLocation p_i46834_1_) {
      this.soundName = p_i46834_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getSoundName() {
      return this.soundName;
   }

   public static void registerSounds() {
      registerSound("ambient.cave");
      registerSound("ambient.underwater.enter");
      registerSound("ambient.underwater.exit");
      registerSound("ambient.underwater.loop");
      registerSound("ambient.underwater.loop.additions");
      registerSound("ambient.underwater.loop.additions.rare");
      registerSound("ambient.underwater.loop.additions.ultra_rare");
      registerSound("block.anvil.break");
      registerSound("block.anvil.destroy");
      registerSound("block.anvil.fall");
      registerSound("block.anvil.hit");
      registerSound("block.anvil.land");
      registerSound("block.anvil.place");
      registerSound("block.anvil.step");
      registerSound("block.anvil.use");
      registerSound("block.beacon.activate");
      registerSound("block.beacon.ambient");
      registerSound("block.beacon.deactivate");
      registerSound("block.beacon.power_select");
      registerSound("block.brewing_stand.brew");
      registerSound("block.bubble_column.bubble_pop");
      registerSound("block.bubble_column.upwards_ambient");
      registerSound("block.bubble_column.upwards_inside");
      registerSound("block.bubble_column.whirlpool_ambient");
      registerSound("block.bubble_column.whirlpool_inside");
      registerSound("block.chest.close");
      registerSound("block.chest.locked");
      registerSound("block.chest.open");
      registerSound("block.chorus_flower.death");
      registerSound("block.chorus_flower.grow");
      registerSound("block.wool.break");
      registerSound("block.wool.fall");
      registerSound("block.wool.hit");
      registerSound("block.wool.place");
      registerSound("block.wool.step");
      registerSound("block.comparator.click");
      registerSound("block.conduit.activate");
      registerSound("block.conduit.ambient");
      registerSound("block.conduit.ambient.short");
      registerSound("block.conduit.attack.target");
      registerSound("block.conduit.deactivate");
      registerSound("block.dispenser.dispense");
      registerSound("block.dispenser.fail");
      registerSound("block.dispenser.launch");
      registerSound("block.enchantment_table.use");
      registerSound("block.end_gateway.spawn");
      registerSound("block.end_portal.spawn");
      registerSound("block.end_portal_frame.fill");
      registerSound("block.ender_chest.close");
      registerSound("block.ender_chest.open");
      registerSound("block.fence_gate.close");
      registerSound("block.fence_gate.open");
      registerSound("block.fire.ambient");
      registerSound("block.fire.extinguish");
      registerSound("block.furnace.fire_crackle");
      registerSound("block.glass.break");
      registerSound("block.glass.fall");
      registerSound("block.glass.hit");
      registerSound("block.glass.place");
      registerSound("block.glass.step");
      registerSound("block.grass.break");
      registerSound("block.grass.fall");
      registerSound("block.grass.hit");
      registerSound("block.grass.place");
      registerSound("block.grass.step");
      registerSound("block.wet_grass.break");
      registerSound("block.wet_grass.fall");
      registerSound("block.wet_grass.hit");
      registerSound("block.wet_grass.place");
      registerSound("block.wet_grass.step");
      registerSound("block.coral_block.break");
      registerSound("block.coral_block.fall");
      registerSound("block.coral_block.hit");
      registerSound("block.coral_block.place");
      registerSound("block.coral_block.step");
      registerSound("block.gravel.break");
      registerSound("block.gravel.fall");
      registerSound("block.gravel.hit");
      registerSound("block.gravel.place");
      registerSound("block.gravel.step");
      registerSound("block.iron_door.close");
      registerSound("block.iron_door.open");
      registerSound("block.iron_trapdoor.close");
      registerSound("block.iron_trapdoor.open");
      registerSound("block.ladder.break");
      registerSound("block.ladder.fall");
      registerSound("block.ladder.hit");
      registerSound("block.ladder.place");
      registerSound("block.ladder.step");
      registerSound("block.lava.ambient");
      registerSound("block.lava.extinguish");
      registerSound("block.lava.pop");
      registerSound("block.lever.click");
      registerSound("block.metal.break");
      registerSound("block.metal.fall");
      registerSound("block.metal.hit");
      registerSound("block.metal.place");
      registerSound("block.metal.step");
      registerSound("block.metal_pressure_plate.click_off");
      registerSound("block.metal_pressure_plate.click_on");
      registerSound("block.note_block.basedrum");
      registerSound("block.note_block.bass");
      registerSound("block.note_block.bell");
      registerSound("block.note_block.chime");
      registerSound("block.note_block.flute");
      registerSound("block.note_block.guitar");
      registerSound("block.note_block.harp");
      registerSound("block.note_block.hat");
      registerSound("block.note_block.pling");
      registerSound("block.note_block.snare");
      registerSound("block.note_block.xylophone");
      registerSound("block.piston.contract");
      registerSound("block.piston.extend");
      registerSound("block.portal.ambient");
      registerSound("block.portal.travel");
      registerSound("block.portal.trigger");
      registerSound("block.pumpkin.carve");
      registerSound("block.redstone_torch.burnout");
      registerSound("block.sand.break");
      registerSound("block.sand.fall");
      registerSound("block.sand.hit");
      registerSound("block.sand.place");
      registerSound("block.sand.step");
      registerSound("block.shulker_box.close");
      registerSound("block.shulker_box.open");
      registerSound("block.slime_block.break");
      registerSound("block.slime_block.fall");
      registerSound("block.slime_block.hit");
      registerSound("block.slime_block.place");
      registerSound("block.slime_block.step");
      registerSound("block.snow.break");
      registerSound("block.snow.fall");
      registerSound("block.snow.hit");
      registerSound("block.snow.place");
      registerSound("block.snow.step");
      registerSound("block.stone.break");
      registerSound("block.stone.fall");
      registerSound("block.stone.hit");
      registerSound("block.stone.place");
      registerSound("block.stone.step");
      registerSound("block.stone_button.click_off");
      registerSound("block.stone_button.click_on");
      registerSound("block.stone_pressure_plate.click_off");
      registerSound("block.stone_pressure_plate.click_on");
      registerSound("block.tripwire.attach");
      registerSound("block.tripwire.click_off");
      registerSound("block.tripwire.click_on");
      registerSound("block.tripwire.detach");
      registerSound("block.water.ambient");
      registerSound("block.lily_pad.place");
      registerSound("block.wood.break");
      registerSound("block.wood.fall");
      registerSound("block.wood.hit");
      registerSound("block.wood.place");
      registerSound("block.wood.step");
      registerSound("block.wooden_button.click_off");
      registerSound("block.wooden_button.click_on");
      registerSound("block.wooden_pressure_plate.click_off");
      registerSound("block.wooden_pressure_plate.click_on");
      registerSound("block.wooden_door.close");
      registerSound("block.wooden_door.open");
      registerSound("block.wooden_trapdoor.close");
      registerSound("block.wooden_trapdoor.open");
      registerSound("enchant.thorns.hit");
      registerSound("entity.armor_stand.break");
      registerSound("entity.armor_stand.fall");
      registerSound("entity.armor_stand.hit");
      registerSound("entity.armor_stand.place");
      registerSound("entity.arrow.hit");
      registerSound("entity.arrow.hit_player");
      registerSound("entity.arrow.shoot");
      registerSound("entity.bat.ambient");
      registerSound("entity.bat.death");
      registerSound("entity.bat.hurt");
      registerSound("entity.bat.loop");
      registerSound("entity.bat.takeoff");
      registerSound("entity.blaze.ambient");
      registerSound("entity.blaze.burn");
      registerSound("entity.blaze.death");
      registerSound("entity.blaze.hurt");
      registerSound("entity.blaze.shoot");
      registerSound("entity.boat.paddle_land");
      registerSound("entity.boat.paddle_water");
      registerSound("entity.fishing_bobber.retrieve");
      registerSound("entity.fishing_bobber.splash");
      registerSound("entity.fishing_bobber.throw");
      registerSound("entity.cat.ambient");
      registerSound("entity.cat.death");
      registerSound("entity.cat.hiss");
      registerSound("entity.cat.hurt");
      registerSound("entity.cat.purr");
      registerSound("entity.cat.purreow");
      registerSound("entity.chicken.ambient");
      registerSound("entity.chicken.death");
      registerSound("entity.chicken.egg");
      registerSound("entity.chicken.hurt");
      registerSound("entity.chicken.step");
      registerSound("entity.cod.ambient");
      registerSound("entity.cod.death");
      registerSound("entity.cod.flop");
      registerSound("entity.cod.hurt");
      registerSound("entity.cow.ambient");
      registerSound("entity.cow.death");
      registerSound("entity.cow.hurt");
      registerSound("entity.cow.milk");
      registerSound("entity.cow.step");
      registerSound("entity.creeper.death");
      registerSound("entity.creeper.hurt");
      registerSound("entity.creeper.primed");
      registerSound("entity.dolphin.ambient");
      registerSound("entity.dolphin.ambient_water");
      registerSound("entity.dolphin.attack");
      registerSound("entity.dolphin.death");
      registerSound("entity.dolphin.eat");
      registerSound("entity.dolphin.hurt");
      registerSound("entity.dolphin.jump");
      registerSound("entity.dolphin.play");
      registerSound("entity.dolphin.splash");
      registerSound("entity.dolphin.swim");
      registerSound("entity.donkey.ambient");
      registerSound("entity.donkey.angry");
      registerSound("entity.donkey.chest");
      registerSound("entity.donkey.death");
      registerSound("entity.donkey.hurt");
      registerSound("entity.drowned.ambient");
      registerSound("entity.drowned.ambient_water");
      registerSound("entity.drowned.death");
      registerSound("entity.drowned.death_water");
      registerSound("entity.drowned.hurt");
      registerSound("entity.drowned.hurt_water");
      registerSound("entity.drowned.shoot");
      registerSound("entity.drowned.step");
      registerSound("entity.drowned.swim");
      registerSound("entity.egg.throw");
      registerSound("entity.elder_guardian.ambient");
      registerSound("entity.elder_guardian.ambient_land");
      registerSound("entity.elder_guardian.curse");
      registerSound("entity.elder_guardian.death");
      registerSound("entity.elder_guardian.death_land");
      registerSound("entity.elder_guardian.flop");
      registerSound("entity.elder_guardian.hurt");
      registerSound("entity.elder_guardian.hurt_land");
      registerSound("entity.ender_dragon.ambient");
      registerSound("entity.ender_dragon.death");
      registerSound("entity.ender_dragon.flap");
      registerSound("entity.ender_dragon.growl");
      registerSound("entity.ender_dragon.hurt");
      registerSound("entity.ender_dragon.shoot");
      registerSound("entity.dragon_fireball.explode");
      registerSound("entity.ender_eye.death");
      registerSound("entity.ender_eye.launch");
      registerSound("entity.enderman.ambient");
      registerSound("entity.enderman.death");
      registerSound("entity.enderman.hurt");
      registerSound("entity.enderman.scream");
      registerSound("entity.enderman.stare");
      registerSound("entity.enderman.teleport");
      registerSound("entity.endermite.ambient");
      registerSound("entity.endermite.death");
      registerSound("entity.endermite.hurt");
      registerSound("entity.endermite.step");
      registerSound("entity.ender_pearl.throw");
      registerSound("entity.evoker.ambient");
      registerSound("entity.evoker.cast_spell");
      registerSound("entity.evoker.death");
      registerSound("entity.evoker.hurt");
      registerSound("entity.evoker.prepare_attack");
      registerSound("entity.evoker.prepare_summon");
      registerSound("entity.evoker.prepare_wololo");
      registerSound("entity.evoker_fangs.attack");
      registerSound("entity.experience_bottle.throw");
      registerSound("entity.experience_orb.pickup");
      registerSound("entity.firework_rocket.blast");
      registerSound("entity.firework_rocket.blast_far");
      registerSound("entity.firework_rocket.large_blast");
      registerSound("entity.firework_rocket.large_blast_far");
      registerSound("entity.firework_rocket.launch");
      registerSound("entity.firework_rocket.shoot");
      registerSound("entity.firework_rocket.twinkle");
      registerSound("entity.firework_rocket.twinkle_far");
      registerSound("entity.fish.swim");
      registerSound("entity.generic.big_fall");
      registerSound("entity.generic.burn");
      registerSound("entity.generic.death");
      registerSound("entity.generic.drink");
      registerSound("entity.generic.eat");
      registerSound("entity.generic.explode");
      registerSound("entity.generic.extinguish_fire");
      registerSound("entity.generic.hurt");
      registerSound("entity.generic.small_fall");
      registerSound("entity.generic.splash");
      registerSound("entity.generic.swim");
      registerSound("entity.ghast.ambient");
      registerSound("entity.ghast.death");
      registerSound("entity.ghast.hurt");
      registerSound("entity.ghast.scream");
      registerSound("entity.ghast.shoot");
      registerSound("entity.ghast.warn");
      registerSound("entity.guardian.ambient");
      registerSound("entity.guardian.ambient_land");
      registerSound("entity.guardian.attack");
      registerSound("entity.guardian.death");
      registerSound("entity.guardian.death_land");
      registerSound("entity.guardian.flop");
      registerSound("entity.guardian.hurt");
      registerSound("entity.guardian.hurt_land");
      registerSound("entity.horse.ambient");
      registerSound("entity.horse.angry");
      registerSound("entity.horse.armor");
      registerSound("entity.horse.breathe");
      registerSound("entity.horse.death");
      registerSound("entity.horse.eat");
      registerSound("entity.horse.gallop");
      registerSound("entity.horse.hurt");
      registerSound("entity.horse.jump");
      registerSound("entity.horse.land");
      registerSound("entity.horse.saddle");
      registerSound("entity.horse.step");
      registerSound("entity.horse.step_wood");
      registerSound("entity.hostile.big_fall");
      registerSound("entity.hostile.death");
      registerSound("entity.hostile.hurt");
      registerSound("entity.hostile.small_fall");
      registerSound("entity.hostile.splash");
      registerSound("entity.hostile.swim");
      registerSound("entity.husk.ambient");
      registerSound("entity.husk.converted_to_zombie");
      registerSound("entity.husk.death");
      registerSound("entity.husk.hurt");
      registerSound("entity.husk.step");
      registerSound("entity.illusioner.ambient");
      registerSound("entity.illusioner.cast_spell");
      registerSound("entity.illusioner.death");
      registerSound("entity.illusioner.hurt");
      registerSound("entity.illusioner.mirror_move");
      registerSound("entity.illusioner.prepare_blindness");
      registerSound("entity.illusioner.prepare_mirror");
      registerSound("entity.iron_golem.attack");
      registerSound("entity.iron_golem.death");
      registerSound("entity.iron_golem.hurt");
      registerSound("entity.iron_golem.step");
      registerSound("entity.item.break");
      registerSound("entity.item.pickup");
      registerSound("entity.item_frame.add_item");
      registerSound("entity.item_frame.break");
      registerSound("entity.item_frame.place");
      registerSound("entity.item_frame.remove_item");
      registerSound("entity.item_frame.rotate_item");
      registerSound("entity.leash_knot.break");
      registerSound("entity.leash_knot.place");
      registerSound("entity.lightning_bolt.impact");
      registerSound("entity.lightning_bolt.thunder");
      registerSound("entity.lingering_potion.throw");
      registerSound("entity.llama.ambient");
      registerSound("entity.llama.angry");
      registerSound("entity.llama.chest");
      registerSound("entity.llama.death");
      registerSound("entity.llama.eat");
      registerSound("entity.llama.hurt");
      registerSound("entity.llama.spit");
      registerSound("entity.llama.step");
      registerSound("entity.llama.swag");
      registerSound("entity.magma_cube.death");
      registerSound("entity.magma_cube.hurt");
      registerSound("entity.magma_cube.jump");
      registerSound("entity.magma_cube.squish");
      registerSound("entity.minecart.inside");
      registerSound("entity.minecart.riding");
      registerSound("entity.mooshroom.shear");
      registerSound("entity.mule.ambient");
      registerSound("entity.mule.chest");
      registerSound("entity.mule.death");
      registerSound("entity.mule.hurt");
      registerSound("entity.painting.break");
      registerSound("entity.painting.place");
      registerSound("entity.parrot.ambient");
      registerSound("entity.parrot.death");
      registerSound("entity.parrot.eat");
      registerSound("entity.parrot.fly");
      registerSound("entity.parrot.hurt");
      registerSound("entity.parrot.imitate.blaze");
      registerSound("entity.parrot.imitate.creeper");
      registerSound("entity.parrot.imitate.drowned");
      registerSound("entity.parrot.imitate.elder_guardian");
      registerSound("entity.parrot.imitate.ender_dragon");
      registerSound("entity.parrot.imitate.enderman");
      registerSound("entity.parrot.imitate.endermite");
      registerSound("entity.parrot.imitate.evoker");
      registerSound("entity.parrot.imitate.ghast");
      registerSound("entity.parrot.imitate.husk");
      registerSound("entity.parrot.imitate.illusioner");
      registerSound("entity.parrot.imitate.magma_cube");
      registerSound("entity.parrot.imitate.phantom");
      registerSound("entity.parrot.imitate.polar_bear");
      registerSound("entity.parrot.imitate.shulker");
      registerSound("entity.parrot.imitate.silverfish");
      registerSound("entity.parrot.imitate.skeleton");
      registerSound("entity.parrot.imitate.slime");
      registerSound("entity.parrot.imitate.spider");
      registerSound("entity.parrot.imitate.stray");
      registerSound("entity.parrot.imitate.vex");
      registerSound("entity.parrot.imitate.vindicator");
      registerSound("entity.parrot.imitate.witch");
      registerSound("entity.parrot.imitate.wither");
      registerSound("entity.parrot.imitate.wither_skeleton");
      registerSound("entity.parrot.imitate.wolf");
      registerSound("entity.parrot.imitate.zombie");
      registerSound("entity.parrot.imitate.zombie_pigman");
      registerSound("entity.parrot.imitate.zombie_villager");
      registerSound("entity.parrot.step");
      registerSound("entity.phantom.ambient");
      registerSound("entity.phantom.bite");
      registerSound("entity.phantom.death");
      registerSound("entity.phantom.flap");
      registerSound("entity.phantom.hurt");
      registerSound("entity.phantom.swoop");
      registerSound("entity.pig.ambient");
      registerSound("entity.pig.death");
      registerSound("entity.pig.hurt");
      registerSound("entity.pig.saddle");
      registerSound("entity.pig.step");
      registerSound("entity.player.attack.crit");
      registerSound("entity.player.attack.knockback");
      registerSound("entity.player.attack.nodamage");
      registerSound("entity.player.attack.strong");
      registerSound("entity.player.attack.sweep");
      registerSound("entity.player.attack.weak");
      registerSound("entity.player.big_fall");
      registerSound("entity.player.breath");
      registerSound("entity.player.burp");
      registerSound("entity.player.death");
      registerSound("entity.player.hurt");
      registerSound("entity.player.hurt_drown");
      registerSound("entity.player.hurt_on_fire");
      registerSound("entity.player.levelup");
      registerSound("entity.player.small_fall");
      registerSound("entity.player.splash");
      registerSound("entity.player.splash.high_speed");
      registerSound("entity.player.swim");
      registerSound("entity.polar_bear.ambient");
      registerSound("entity.polar_bear.ambient_baby");
      registerSound("entity.polar_bear.death");
      registerSound("entity.polar_bear.hurt");
      registerSound("entity.polar_bear.step");
      registerSound("entity.polar_bear.warning");
      registerSound("entity.puffer_fish.ambient");
      registerSound("entity.puffer_fish.blow_out");
      registerSound("entity.puffer_fish.blow_up");
      registerSound("entity.puffer_fish.death");
      registerSound("entity.puffer_fish.flop");
      registerSound("entity.puffer_fish.hurt");
      registerSound("entity.puffer_fish.sting");
      registerSound("entity.rabbit.ambient");
      registerSound("entity.rabbit.attack");
      registerSound("entity.rabbit.death");
      registerSound("entity.rabbit.hurt");
      registerSound("entity.rabbit.jump");
      registerSound("entity.salmon.ambient");
      registerSound("entity.salmon.death");
      registerSound("entity.salmon.flop");
      registerSound("entity.salmon.hurt");
      registerSound("entity.sheep.ambient");
      registerSound("entity.sheep.death");
      registerSound("entity.sheep.hurt");
      registerSound("entity.sheep.shear");
      registerSound("entity.sheep.step");
      registerSound("entity.shulker.ambient");
      registerSound("entity.shulker.close");
      registerSound("entity.shulker.death");
      registerSound("entity.shulker.hurt");
      registerSound("entity.shulker.hurt_closed");
      registerSound("entity.shulker.open");
      registerSound("entity.shulker.shoot");
      registerSound("entity.shulker.teleport");
      registerSound("entity.shulker_bullet.hit");
      registerSound("entity.shulker_bullet.hurt");
      registerSound("entity.silverfish.ambient");
      registerSound("entity.silverfish.death");
      registerSound("entity.silverfish.hurt");
      registerSound("entity.silverfish.step");
      registerSound("entity.skeleton.ambient");
      registerSound("entity.skeleton.death");
      registerSound("entity.skeleton.hurt");
      registerSound("entity.skeleton.shoot");
      registerSound("entity.skeleton.step");
      registerSound("entity.skeleton_horse.ambient");
      registerSound("entity.skeleton_horse.death");
      registerSound("entity.skeleton_horse.hurt");
      registerSound("entity.skeleton_horse.swim");
      registerSound("entity.skeleton_horse.ambient_water");
      registerSound("entity.skeleton_horse.gallop_water");
      registerSound("entity.skeleton_horse.jump_water");
      registerSound("entity.skeleton_horse.step_water");
      registerSound("entity.slime.attack");
      registerSound("entity.slime.death");
      registerSound("entity.slime.hurt");
      registerSound("entity.slime.jump");
      registerSound("entity.slime.squish");
      registerSound("entity.magma_cube.death_small");
      registerSound("entity.magma_cube.hurt_small");
      registerSound("entity.magma_cube.squish_small");
      registerSound("entity.slime.death_small");
      registerSound("entity.slime.hurt_small");
      registerSound("entity.slime.jump_small");
      registerSound("entity.slime.squish_small");
      registerSound("entity.snow_golem.ambient");
      registerSound("entity.snow_golem.death");
      registerSound("entity.snow_golem.hurt");
      registerSound("entity.snow_golem.shoot");
      registerSound("entity.snowball.throw");
      registerSound("entity.spider.ambient");
      registerSound("entity.spider.death");
      registerSound("entity.spider.hurt");
      registerSound("entity.spider.step");
      registerSound("entity.splash_potion.break");
      registerSound("entity.splash_potion.throw");
      registerSound("entity.squid.ambient");
      registerSound("entity.squid.death");
      registerSound("entity.squid.hurt");
      registerSound("entity.squid.squirt");
      registerSound("entity.stray.ambient");
      registerSound("entity.stray.death");
      registerSound("entity.stray.hurt");
      registerSound("entity.stray.step");
      registerSound("entity.tnt.primed");
      registerSound("entity.tropical_fish.ambient");
      registerSound("entity.tropical_fish.death");
      registerSound("entity.tropical_fish.flop");
      registerSound("entity.tropical_fish.hurt");
      registerSound("entity.turtle.ambient_land");
      registerSound("entity.turtle.death");
      registerSound("entity.turtle.death_baby");
      registerSound("entity.turtle.egg_break");
      registerSound("entity.turtle.egg_crack");
      registerSound("entity.turtle.egg_hatch");
      registerSound("entity.turtle.hurt");
      registerSound("entity.turtle.hurt_baby");
      registerSound("entity.turtle.lay_egg");
      registerSound("entity.turtle.shamble");
      registerSound("entity.turtle.shamble_baby");
      registerSound("entity.turtle.swim");
      registerSound("entity.vex.ambient");
      registerSound("entity.vex.charge");
      registerSound("entity.vex.death");
      registerSound("entity.vex.hurt");
      registerSound("entity.villager.ambient");
      registerSound("entity.villager.death");
      registerSound("entity.villager.hurt");
      registerSound("entity.villager.no");
      registerSound("entity.villager.trade");
      registerSound("entity.villager.yes");
      registerSound("entity.vindicator.ambient");
      registerSound("entity.vindicator.death");
      registerSound("entity.vindicator.hurt");
      registerSound("entity.witch.ambient");
      registerSound("entity.witch.death");
      registerSound("entity.witch.drink");
      registerSound("entity.witch.hurt");
      registerSound("entity.witch.throw");
      registerSound("entity.wither.ambient");
      registerSound("entity.wither.break_block");
      registerSound("entity.wither.death");
      registerSound("entity.wither.hurt");
      registerSound("entity.wither.shoot");
      registerSound("entity.wither.spawn");
      registerSound("entity.wither_skeleton.ambient");
      registerSound("entity.wither_skeleton.death");
      registerSound("entity.wither_skeleton.hurt");
      registerSound("entity.wither_skeleton.step");
      registerSound("entity.wolf.ambient");
      registerSound("entity.wolf.death");
      registerSound("entity.wolf.growl");
      registerSound("entity.wolf.howl");
      registerSound("entity.wolf.hurt");
      registerSound("entity.wolf.pant");
      registerSound("entity.wolf.shake");
      registerSound("entity.wolf.step");
      registerSound("entity.wolf.whine");
      registerSound("entity.zombie.ambient");
      registerSound("entity.zombie.attack_wooden_door");
      registerSound("entity.zombie.attack_iron_door");
      registerSound("entity.zombie.break_wooden_door");
      registerSound("entity.zombie.converted_to_drowned");
      registerSound("entity.zombie.death");
      registerSound("entity.zombie.destroy_egg");
      registerSound("entity.zombie.hurt");
      registerSound("entity.zombie.infect");
      registerSound("entity.zombie.step");
      registerSound("entity.zombie_horse.ambient");
      registerSound("entity.zombie_horse.death");
      registerSound("entity.zombie_horse.hurt");
      registerSound("entity.zombie_pigman.ambient");
      registerSound("entity.zombie_pigman.angry");
      registerSound("entity.zombie_pigman.death");
      registerSound("entity.zombie_pigman.hurt");
      registerSound("entity.zombie_villager.ambient");
      registerSound("entity.zombie_villager.converted");
      registerSound("entity.zombie_villager.cure");
      registerSound("entity.zombie_villager.death");
      registerSound("entity.zombie_villager.hurt");
      registerSound("entity.zombie_villager.step");
      registerSound("item.armor.equip_chain");
      registerSound("item.armor.equip_diamond");
      registerSound("item.armor.equip_elytra");
      registerSound("item.armor.equip_generic");
      registerSound("item.armor.equip_gold");
      registerSound("item.armor.equip_iron");
      registerSound("item.armor.equip_leather");
      registerSound("item.armor.equip_turtle");
      registerSound("item.axe.strip");
      registerSound("item.bottle.empty");
      registerSound("item.bottle.fill");
      registerSound("item.bottle.fill_dragonbreath");
      registerSound("item.bucket.empty");
      registerSound("item.bucket.empty_fish");
      registerSound("item.bucket.empty_lava");
      registerSound("item.bucket.fill");
      registerSound("item.bucket.fill_fish");
      registerSound("item.bucket.fill_lava");
      registerSound("item.chorus_fruit.teleport");
      registerSound("item.elytra.flying");
      registerSound("item.firecharge.use");
      registerSound("item.flintandsteel.use");
      registerSound("item.hoe.till");
      registerSound("item.shield.block");
      registerSound("item.shield.break");
      registerSound("item.shovel.flatten");
      registerSound("item.totem.use");
      registerSound("item.trident.hit");
      registerSound("item.trident.hit_ground");
      registerSound("item.trident.return");
      registerSound("item.trident.riptide_1");
      registerSound("item.trident.riptide_2");
      registerSound("item.trident.riptide_3");
      registerSound("item.trident.throw");
      registerSound("item.trident.thunder");
      registerSound("music.creative");
      registerSound("music.credits");
      registerSound("music.dragon");
      registerSound("music.end");
      registerSound("music.game");
      registerSound("music.menu");
      registerSound("music.nether");
      registerSound("music.under_water");
      registerSound("music_disc.11");
      registerSound("music_disc.13");
      registerSound("music_disc.blocks");
      registerSound("music_disc.cat");
      registerSound("music_disc.chirp");
      registerSound("music_disc.far");
      registerSound("music_disc.mall");
      registerSound("music_disc.mellohi");
      registerSound("music_disc.stal");
      registerSound("music_disc.strad");
      registerSound("music_disc.wait");
      registerSound("music_disc.ward");
      registerSound("ui.button.click");
      registerSound("ui.toast.challenge_complete");
      registerSound("ui.toast.in");
      registerSound("ui.toast.out");
      registerSound("weather.rain");
      registerSound("weather.rain.above");
   }

   private static void registerSound(String p_187502_0_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_187502_0_);
      IRegistry.field_212633_v.func_82595_a(resourcelocation, new SoundEvent(resourcelocation));
   }
}
