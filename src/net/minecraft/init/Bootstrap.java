package net.minecraft.init;

import java.io.PrintStream;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarvedPumpkin;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSkullWither;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.EntityOptions;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBoneMeal;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.potion.PotionType;
import net.minecraft.server.DebugLoggingPrintStream;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.LoggingPrintStream;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
   public static final PrintStream SYSOUT = System.out;
   private static boolean alreadyRegistered;
   private static final Logger LOGGER = LogManager.getLogger();

   public static boolean isRegistered() {
      return alreadyRegistered;
   }

   static void registerDispenserBehaviors() {
      BlockDispenser.registerDispenseBehavior(Items.ARROW, new BehaviorProjectileDispense() {
         protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_, ItemStack p_82499_3_) {
            EntityTippedArrow entitytippedarrow = new EntityTippedArrow(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
            entitytippedarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
            return entitytippedarrow;
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.TIPPED_ARROW, new BehaviorProjectileDispense() {
         protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_, ItemStack p_82499_3_) {
            EntityTippedArrow entitytippedarrow = new EntityTippedArrow(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
            entitytippedarrow.setPotionEffect(p_82499_3_);
            entitytippedarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
            return entitytippedarrow;
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.SPECTRAL_ARROW, new BehaviorProjectileDispense() {
         protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_, ItemStack p_82499_3_) {
            EntityArrow entityarrow = new EntitySpectralArrow(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
            entityarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
            return entityarrow;
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.EGG, new BehaviorProjectileDispense() {
         protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_, ItemStack p_82499_3_) {
            return new EntityEgg(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.SNOWBALL, new BehaviorProjectileDispense() {
         protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_, ItemStack p_82499_3_) {
            return new EntitySnowball(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.EXPERIENCE_BOTTLE, new BehaviorProjectileDispense() {
         protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_, ItemStack p_82499_3_) {
            return new EntityExpBottle(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
         }

         protected float getProjectileInaccuracy() {
            return super.getProjectileInaccuracy() * 0.5F;
         }

         protected float getProjectileVelocity() {
            return super.getProjectileVelocity() * 1.25F;
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.SPLASH_POTION, new IBehaviorDispenseItem() {
         public ItemStack dispense(IBlockSource p_dispense_1_, final ItemStack p_dispense_2_) {
            return (new BehaviorProjectileDispense() {
               protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_, ItemStack p_82499_3_) {
                  return new EntityPotion(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ(), p_dispense_2_.copy());
               }

               protected float getProjectileInaccuracy() {
                  return super.getProjectileInaccuracy() * 0.5F;
               }

               protected float getProjectileVelocity() {
                  return super.getProjectileVelocity() * 1.25F;
               }
            }).dispense(p_dispense_1_, p_dispense_2_);
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.LINGERING_POTION, new IBehaviorDispenseItem() {
         public ItemStack dispense(IBlockSource p_dispense_1_, final ItemStack p_dispense_2_) {
            return (new BehaviorProjectileDispense() {
               protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_, ItemStack p_82499_3_) {
                  return new EntityPotion(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ(), p_dispense_2_.copy());
               }

               protected float getProjectileInaccuracy() {
                  return super.getProjectileInaccuracy() * 0.5F;
               }

               protected float getProjectileVelocity() {
                  return super.getProjectileVelocity() * 1.25F;
               }
            }).dispense(p_dispense_1_, p_dispense_2_);
         }
      });
      BehaviorDefaultDispenseItem behaviordefaultdispenseitem = new BehaviorDefaultDispenseItem() {
         public ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            EnumFacing enumfacing = p_82487_1_.getBlockState().get(BlockDispenser.FACING);
            EntityType<?> entitytype = ((ItemSpawnEgg)p_82487_2_.getItem()).getType(p_82487_2_.getTag());
            if (entitytype != null) {
               entitytype.spawnEntity(p_82487_1_.getWorld(), p_82487_2_, null, p_82487_1_.getBlockPos().offset(enumfacing), enumfacing != EnumFacing.UP, false);
            }

            p_82487_2_.shrink(1);
            return p_82487_2_;
         }
      };

      for(ItemSpawnEgg itemspawnegg : ItemSpawnEgg.getEggs()) {
         BlockDispenser.registerDispenseBehavior(itemspawnegg, behaviordefaultdispenseitem);
      }

      BlockDispenser.registerDispenseBehavior(Items.FIREWORK_ROCKET, new BehaviorDefaultDispenseItem() {
         public ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            EnumFacing enumfacing = p_82487_1_.getBlockState().get(BlockDispenser.FACING);
            double d0 = p_82487_1_.getX() + (double)enumfacing.getXOffset();
            double d1 = (double)((float)p_82487_1_.getBlockPos().getY() + 0.2F);
            double d2 = p_82487_1_.getZ() + (double)enumfacing.getZOffset();
            EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(p_82487_1_.getWorld(), d0, d1, d2, p_82487_2_);
            p_82487_1_.getWorld().spawnEntity(entityfireworkrocket);
            p_82487_2_.shrink(1);
            return p_82487_2_;
         }

         protected void playDispenseSound(IBlockSource p_82485_1_) {
            p_82485_1_.getWorld().playEvent(1004, p_82485_1_.getBlockPos(), 0);
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.FIRE_CHARGE, new BehaviorDefaultDispenseItem() {
         public ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            EnumFacing enumfacing = p_82487_1_.getBlockState().get(BlockDispenser.FACING);
            IPosition iposition = BlockDispenser.getDispensePosition(p_82487_1_);
            double d0 = iposition.getX() + (double)((float)enumfacing.getXOffset() * 0.3F);
            double d1 = iposition.getY() + (double)((float)enumfacing.getYOffset() * 0.3F);
            double d2 = iposition.getZ() + (double)((float)enumfacing.getZOffset() * 0.3F);
            World world = p_82487_1_.getWorld();
            Random random = world.rand;
            double d3 = random.nextGaussian() * 0.05D + (double)enumfacing.getXOffset();
            double d4 = random.nextGaussian() * 0.05D + (double)enumfacing.getYOffset();
            double d5 = random.nextGaussian() * 0.05D + (double)enumfacing.getZOffset();
            world.spawnEntity(new EntitySmallFireball(world, d0, d1, d2, d3, d4, d5));
            p_82487_2_.shrink(1);
            return p_82487_2_;
         }

         protected void playDispenseSound(IBlockSource p_82485_1_) {
            p_82485_1_.getWorld().playEvent(1018, p_82485_1_.getBlockPos(), 0);
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.OAK_BOAT, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.OAK));
      BlockDispenser.registerDispenseBehavior(Items.SPRUCE_BOAT, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.SPRUCE));
      BlockDispenser.registerDispenseBehavior(Items.BIRCH_BOAT, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.BIRCH));
      BlockDispenser.registerDispenseBehavior(Items.JUNGLE_BOAT, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.JUNGLE));
      BlockDispenser.registerDispenseBehavior(Items.DARK_OAK_BOAT, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.DARK_OAK));
      BlockDispenser.registerDispenseBehavior(Items.ACACIA_BOAT, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.ACACIA));
      IBehaviorDispenseItem ibehaviordispenseitem = new BehaviorDefaultDispenseItem() {
         private final BehaviorDefaultDispenseItem dispenseBehavior = new BehaviorDefaultDispenseItem();

         public ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            ItemBucket itembucket = (ItemBucket)p_82487_2_.getItem();
            BlockPos blockpos = p_82487_1_.getBlockPos().offset(p_82487_1_.getBlockState().get(BlockDispenser.FACING));
            World world = p_82487_1_.getWorld();
            if (itembucket.tryPlaceContainedLiquid(null, world, blockpos, null)) {
               itembucket.onLiquidPlaced(world, p_82487_2_, blockpos);
               return new ItemStack(Items.BUCKET);
            } else {
               return this.dispenseBehavior.dispense(p_82487_1_, p_82487_2_);
            }
         }
      };
      BlockDispenser.registerDispenseBehavior(Items.LAVA_BUCKET, ibehaviordispenseitem);
      BlockDispenser.registerDispenseBehavior(Items.WATER_BUCKET, ibehaviordispenseitem);
      BlockDispenser.registerDispenseBehavior(Items.SALMON_BUCKET, ibehaviordispenseitem);
      BlockDispenser.registerDispenseBehavior(Items.COD_BUCKET, ibehaviordispenseitem);
      BlockDispenser.registerDispenseBehavior(Items.PUFFERFISH_BUCKET, ibehaviordispenseitem);
      BlockDispenser.registerDispenseBehavior(Items.TROPICAL_FISH_BUCKET, ibehaviordispenseitem);
      BlockDispenser.registerDispenseBehavior(Items.BUCKET, new BehaviorDefaultDispenseItem() {
         private final BehaviorDefaultDispenseItem dispenseBehavior = new BehaviorDefaultDispenseItem();

         public ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            IWorld iworld = p_82487_1_.getWorld();
            BlockPos blockpos = p_82487_1_.getBlockPos().offset(p_82487_1_.getBlockState().get(BlockDispenser.FACING));
            IBlockState iblockstate = iworld.getBlockState(blockpos);
            Block block = iblockstate.getBlock();
            if (block instanceof IBucketPickupHandler) {
               Fluid fluid = ((IBucketPickupHandler)block).pickupFluid(iworld, blockpos, iblockstate);
               if (!(fluid instanceof FlowingFluid)) {
                  return super.dispenseStack(p_82487_1_, p_82487_2_);
               } else {
                  Item item = fluid.getFilledBucket();
                  p_82487_2_.shrink(1);
                  if (p_82487_2_.isEmpty()) {
                     return new ItemStack(item);
                  } else {
                     if (p_82487_1_.<TileEntityDispenser>getBlockTileEntity().addItemStack(new ItemStack(item)) < 0) {
                        this.dispenseBehavior.dispense(p_82487_1_, new ItemStack(item));
                     }

                     return p_82487_2_;
                  }
               }
            } else {
               return super.dispenseStack(p_82487_1_, p_82487_2_);
            }
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.FLINT_AND_STEEL, new Bootstrap.BehaviorDispenseOptional() {
         protected ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            World world = p_82487_1_.getWorld();
            this.successful = true;
            BlockPos blockpos = p_82487_1_.getBlockPos().offset(p_82487_1_.getBlockState().get(BlockDispenser.FACING));
            if (ItemFlintAndSteel.canIgnite(world, blockpos)) {
               world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
            } else {
               Block block = world.getBlockState(blockpos).getBlock();
               if (block instanceof BlockTNT) {
                  ((BlockTNT)block).explode(world, blockpos);
                  world.removeBlock(blockpos);
               } else {
                  this.successful = false;
               }
            }

            if (this.successful && p_82487_2_.attemptDamageItem(1, world.rand, null)) {
               p_82487_2_.setCount(0);
            }

            return p_82487_2_;
         }
      });
      BlockDispenser.registerDispenseBehavior(Items.BONE_MEAL, new Bootstrap.BehaviorDispenseOptional() {
         protected ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            this.successful = true;
            World world = p_82487_1_.getWorld();
            BlockPos blockpos = p_82487_1_.getBlockPos().offset(p_82487_1_.getBlockState().get(BlockDispenser.FACING));
            if (!ItemBoneMeal.applyBonemeal(p_82487_2_, world, blockpos) && !ItemBoneMeal.func_203173_b(p_82487_2_, world, blockpos,
                    null)) {
               this.successful = false;
            } else if (!world.isRemote) {
               world.playEvent(2005, blockpos, 0);
            }

            return p_82487_2_;
         }
      });
      BlockDispenser.registerDispenseBehavior(Blocks.TNT, new BehaviorDefaultDispenseItem() {
         protected ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            World world = p_82487_1_.getWorld();
            BlockPos blockpos = p_82487_1_.getBlockPos().offset(p_82487_1_.getBlockState().get(BlockDispenser.FACING));
            EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D,
                    null);
            world.spawnEntity(entitytntprimed);
            world.playSound(null, entitytntprimed.posX, entitytntprimed.posY, entitytntprimed.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            p_82487_2_.shrink(1);
            return p_82487_2_;
         }
      });
      Bootstrap.BehaviorDispenseOptional bootstrap$behaviordispenseoptional = new Bootstrap.BehaviorDispenseOptional() {
         protected ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            this.successful = !ItemArmor.dispenseArmor(p_82487_1_, p_82487_2_).isEmpty();
            return p_82487_2_;
         }
      };
      BlockDispenser.registerDispenseBehavior(Items.CREEPER_HEAD, bootstrap$behaviordispenseoptional);
      BlockDispenser.registerDispenseBehavior(Items.ZOMBIE_HEAD, bootstrap$behaviordispenseoptional);
      BlockDispenser.registerDispenseBehavior(Items.DRAGON_HEAD, bootstrap$behaviordispenseoptional);
      BlockDispenser.registerDispenseBehavior(Items.SKELETON_SKULL, bootstrap$behaviordispenseoptional);
      BlockDispenser.registerDispenseBehavior(Items.PLAYER_HEAD, bootstrap$behaviordispenseoptional);
      BlockDispenser.registerDispenseBehavior(Items.WITHER_SKELETON_SKULL, new Bootstrap.BehaviorDispenseOptional() {
         protected ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            World world = p_82487_1_.getWorld();
            EnumFacing enumfacing = p_82487_1_.getBlockState().get(BlockDispenser.FACING);
            BlockPos blockpos = p_82487_1_.getBlockPos().offset(enumfacing);
            this.successful = true;
            if (world.isAirBlock(blockpos) && BlockSkullWither.func_196299_b(world, blockpos, p_82487_2_)) {
               world.setBlockState(blockpos, Blocks.WITHER_SKELETON_SKULL.getDefaultState().with(BlockSkull.ROTATION, Integer.valueOf(enumfacing.getAxis() == EnumFacing.Axis.Y ? 0 : enumfacing.getOpposite().getHorizontalIndex() * 4)), 3);
               TileEntity tileentity = world.getTileEntity(blockpos);
               if (tileentity instanceof TileEntitySkull) {
                  BlockSkullWither.checkWitherSpawn(world, blockpos, (TileEntitySkull)tileentity);
               }

               p_82487_2_.shrink(1);
            } else if (ItemArmor.dispenseArmor(p_82487_1_, p_82487_2_).isEmpty()) {
               this.successful = false;
            }

            return p_82487_2_;
         }
      });
      BlockDispenser.registerDispenseBehavior(Blocks.CARVED_PUMPKIN, new Bootstrap.BehaviorDispenseOptional() {
         protected ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            World world = p_82487_1_.getWorld();
            BlockPos blockpos = p_82487_1_.getBlockPos().offset(p_82487_1_.getBlockState().get(BlockDispenser.FACING));
            BlockCarvedPumpkin blockcarvedpumpkin = (BlockCarvedPumpkin)Blocks.CARVED_PUMPKIN;
            this.successful = true;
            if (world.isAirBlock(blockpos) && blockcarvedpumpkin.canDispenserPlace(world, blockpos)) {
               if (!world.isRemote) {
                  world.setBlockState(blockpos, blockcarvedpumpkin.getDefaultState(), 3);
               }

               p_82487_2_.shrink(1);
            } else {
               ItemStack itemstack = ItemArmor.dispenseArmor(p_82487_1_, p_82487_2_);
               if (itemstack.isEmpty()) {
                  this.successful = false;
               }
            }

            return p_82487_2_;
         }
      });
      BlockDispenser.registerDispenseBehavior(Blocks.SHULKER_BOX.asItem(), new Bootstrap.BehaviorDispenseShulkerBox());

      for(EnumDyeColor enumdyecolor : EnumDyeColor.values()) {
         BlockDispenser.registerDispenseBehavior(BlockShulkerBox.getBlockByColor(enumdyecolor).asItem(), new Bootstrap.BehaviorDispenseShulkerBox());
      }

   }

   public static void register() {
      
      if (!alreadyRegistered) {
         alreadyRegistered = true;
         SoundEvent.registerSounds();
         Fluid.registerAll();
         Block.registerBlocks();
         BlockFire.init();
         Potion.registerPotions();
         Enchantment.registerEnchantments();
         if (EntityType.getId(EntityType.PLAYER) == null) {
            throw new IllegalStateException("Failed loading EntityTypes");
         } else {
            Item.registerItems();
            PotionType.registerPotionTypes();
            PotionBrewing.init();
            Biome.registerBiomes();
            EntityOptions.registerOptions();
            ParticleType.registerAll();
            registerDispenserBehaviors();
            ArgumentTypes.registerArgumentTypes();
            BiomeProviderType.func_212580_a();
            TileEntityType.func_212641_a();
            ChunkGeneratorType.func_212675_a();
            DimensionType.func_212680_a();
            PaintingType.validateRegistry();
            StatList.func_212734_a();
            IRegistry.func_212613_e();
            if (SharedConstants.developmentMode) {
               func_210839_a("block", IRegistry.field_212618_g, Block::getTranslationKey);
               func_210839_a("biome", IRegistry.field_212624_m, Biome::getTranslationKey);
               func_210839_a("enchantment", IRegistry.field_212628_q, Enchantment::getName);
               func_210839_a("item", IRegistry.field_212630_s, Item::getTranslationKey);
               func_210839_a("effect", IRegistry.field_212631_t, Potion::getName);
               func_210839_a("entity", IRegistry.field_212629_r, EntityType::getTranslationKey);
            }

            redirectOutputToLog();
         }
      }
   }

   private static <T> void func_210839_a(String p_210839_0_, IRegistry<T> p_210839_1_, Function<T, String> p_210839_2_) {
      LanguageMap languagemap = LanguageMap.getInstance();
      p_210839_1_.iterator().forEachRemaining((p_210840_4_) -> {
         String s = p_210839_2_.apply(p_210840_4_);
         if (!languagemap.func_210813_b(s)) {
            LOGGER.warn("Missing translation for {}: {} (key: '{}')", p_210839_0_, p_210839_1_.func_177774_c(p_210840_4_), s);
         }

      });
   }

   private static void redirectOutputToLog() {
      if (LOGGER.isDebugEnabled()) {
         System.setErr(new DebugLoggingPrintStream("STDERR", System.err));
         System.setOut(new DebugLoggingPrintStream("STDOUT", SYSOUT));
      } else {
         System.setErr(new LoggingPrintStream("STDERR", System.err));
         System.setOut(new LoggingPrintStream("STDOUT", SYSOUT));
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static void printToSYSOUT(String p_179870_0_) {
      SYSOUT.println(p_179870_0_);
   }

   public static class BehaviorDispenseBoat extends BehaviorDefaultDispenseItem {
      private final BehaviorDefaultDispenseItem dispenseBehavior = new BehaviorDefaultDispenseItem();
      private final EntityBoat.Type boatType;

      public BehaviorDispenseBoat(EntityBoat.Type p_i47023_1_) {
         this.boatType = p_i47023_1_;
      }

      public ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
         EnumFacing enumfacing = p_82487_1_.getBlockState().get(BlockDispenser.FACING);
         World world = p_82487_1_.getWorld();
         double d0 = p_82487_1_.getX() + (double)((float)enumfacing.getXOffset() * 1.125F);
         double d1 = p_82487_1_.getY() + (double)((float)enumfacing.getYOffset() * 1.125F);
         double d2 = p_82487_1_.getZ() + (double)((float)enumfacing.getZOffset() * 1.125F);
         BlockPos blockpos = p_82487_1_.getBlockPos().offset(enumfacing);
         double d3;
         if (world.getFluidState(blockpos).isTagged(FluidTags.WATER)) {
            d3 = 1.0D;
         } else {
            if (!world.getBlockState(blockpos).isAir() || !world.getFluidState(blockpos.down()).isTagged(FluidTags.WATER)) {
               return this.dispenseBehavior.dispense(p_82487_1_, p_82487_2_);
            }

            d3 = 0.0D;
         }

         EntityBoat entityboat = new EntityBoat(world, d0, d1 + d3, d2);
         entityboat.setBoatType(this.boatType);
         entityboat.rotationYaw = enumfacing.getHorizontalAngle();
         world.spawnEntity(entityboat);
         p_82487_2_.shrink(1);
         return p_82487_2_;
      }

      protected void playDispenseSound(IBlockSource p_82485_1_) {
         p_82485_1_.getWorld().playEvent(1000, p_82485_1_.getBlockPos(), 0);
      }
   }

   public abstract static class BehaviorDispenseOptional extends BehaviorDefaultDispenseItem {
      protected boolean successful = true;

      protected void playDispenseSound(IBlockSource p_82485_1_) {
         p_82485_1_.getWorld().playEvent(this.successful ? 1000 : 1001, p_82485_1_.getBlockPos(), 0);
      }
   }

   static class BehaviorDispenseShulkerBox extends Bootstrap.BehaviorDispenseOptional {
      private BehaviorDispenseShulkerBox() {
      }

      protected ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
         this.successful = false;
         Item item = p_82487_2_.getItem();
         if (item instanceof ItemBlock) {
            EnumFacing enumfacing = p_82487_1_.getBlockState().get(BlockDispenser.FACING);
            BlockPos blockpos = p_82487_1_.getBlockPos().offset(enumfacing);
            EnumFacing enumfacing1 = p_82487_1_.getWorld().isAirBlock(blockpos.down()) ? enumfacing : EnumFacing.UP;
            this.successful = ((ItemBlock)item).tryPlace(new Bootstrap.DispensePlaceContext(p_82487_1_.getWorld(), blockpos, enumfacing, p_82487_2_, enumfacing1)) == EnumActionResult.SUCCESS;
            if (this.successful) {
               p_82487_2_.shrink(1);
            }
         }

         return p_82487_2_;
      }
   }

   static class DispensePlaceContext extends BlockItemUseContext {
      private final EnumFacing field_196015_j;

      public DispensePlaceContext(World p_i47754_1_, BlockPos p_i47754_2_, EnumFacing p_i47754_3_, ItemStack p_i47754_4_, EnumFacing p_i47754_5_) {
         super(p_i47754_1_, null, p_i47754_4_, p_i47754_2_, p_i47754_5_, 0.5F, 0.0F, 0.5F);
         this.field_196015_j = p_i47754_3_;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public boolean func_196011_b() {
         return this.world.getBlockState(this.pos).isReplaceable(this);
      }

      public boolean func_196012_c() {
         return this.func_196011_b();
      }

      public EnumFacing func_196010_d() {
         return EnumFacing.DOWN;
      }

      public EnumFacing[] func_196009_e() {
         switch(this.field_196015_j) {
         case DOWN:
         default:
            return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.UP};
         case UP:
            return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
         case NORTH:
            return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.UP, EnumFacing.SOUTH};
         case SOUTH:
            return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.UP, EnumFacing.NORTH};
         case WEST:
            return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST};
         case EAST:
            return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.WEST};
         }
      }

      public EnumFacing getPlacementHorizontalFacing() {
         return this.field_196015_j.getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : this.field_196015_j;
      }

      public boolean isPlacerSneaking() {
         return false;
      }

      public float getPlacementYaw() {
         return (float)(this.field_196015_j.getHorizontalIndex() * 90);
      }
   }
}
