[中文版](https://github.com/XiaoYuOvO/MITE1.13.2/blob/master/ChangeLogCN.md)
# B0.5.0 ChangeLog
## Blocks
* Add all kinds of material anvils
## Game mechanics
* 1,Now we need to unlock structures to make them can spawn in world.The requirements are in \
    the advancement page,you need to get to 10 levels to unlock the root advancement of all structures
* 2,Make enchanted books cannot merge together

## Generate
* The animals in Taiga is fewer now

## Rendering
* 1,Make players always can see the durability of items
* 2,Fix the textures of iron anvils

## Bug Fix
* 1,Fix cannot craft fireworks,color clothes and cloning books
* 2,Fix an issue where multiplayer games could not be played due to packet transfer
* 3,Fix [MC-101233](https://bugs.mojang.com/browse/MC-101233)

## Other
* Removed tutorial step of punching trees
---
# B0.4.0 ChangeLog
## Game mechanics:
* Now the item is more easily damaged: \
  The received damage value of tools is 100 times the hardness of the digging block(Axes are 45 times)\
 The received damage value of swords is 50
     
## Item
* 1, Add swords, pickaxes, shovels, axes, hoe of all levels of the vanilla 1.6.4 MITE.
* 2, adding a new material - tungsten, better than Mithril, worse than Adamantium, durability is twice the size of Mithril's

## Blocks
* Add new ores and metal blocks of various materials
---
# B0.3.1 ChangeLog
## Bug Fix
* The health of player is 20 when join world for first time,it should be 6
---
# B0.3.0 ChangeLog
## Game mechanics:
* 1, Upgrade requires a higher experience value:\
   Set the level to n\
   Required experience per level = 10 (n + 1)\
   Total experience per level = 5n2+15n
* 2 run, jump and destroy blocks' hunger value plus (X1.5)
* 3, dirt sand leaves slowdown:\
   11s 10.5s 10s
* 4, death drops one-third of the experience before death, if there is no level, it will fall to a negative number

## Generate:
* 1, the animal production rate is reduced, generating weights, halving the size of each group
* 2, generate creatures every 128 days

## Item:
* Remove wooden pickaxe, wooden axe, wooden hoe

## Bug fix:
* Player's health is no real 3 hearts on respawn

## Anti-cheating:
* 1, coordinate deletion
* 2, prohibiting the use of cheating
* 3, update the saves file version, now only supports the opening of the MITE saves
---
# B0.2.1 ChangeLog
## Items
* Add flint tools
* Add salad
* Make seeds eatable

## Item drops
* Make flint's drop chance lower
* Make leaves drop sticks

## Game mechanic
* Changed the hunger mechanism to make the game harder

## Block
* Dirt will drop down like gravel and sand

## Technical
* Make the language files can include in the jar file