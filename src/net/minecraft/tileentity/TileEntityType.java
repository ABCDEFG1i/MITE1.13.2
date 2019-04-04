package net.minecraft.tileentity;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.registry.IRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileEntityType<T extends TileEntity> {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final TileEntityType<TileEntityFurnace> FURNACE = registerTileEntityType("furnace", TileEntityType.Builder.create(TileEntityFurnace::new));
   public static final TileEntityType<TileEntityChest> CHEST = registerTileEntityType("chest", TileEntityType.Builder.create(TileEntityChest::new));
   public static final TileEntityType<TileEntityTrappedChest> TRAPPED_CHEST = registerTileEntityType("trapped_chest", TileEntityType.Builder.create(TileEntityTrappedChest::new));
   public static final TileEntityType<TileEntityEnderChest> ENDER_CHEST = registerTileEntityType("ender_chest", TileEntityType.Builder.create(TileEntityEnderChest::new));
   public static final TileEntityType<TileEntityJukebox> JUKEBOX = registerTileEntityType("jukebox", TileEntityType.Builder.create(TileEntityJukebox::new));
   public static final TileEntityType<TileEntityDispenser> DISPENSER = registerTileEntityType("dispenser", TileEntityType.Builder.create(TileEntityDispenser::new));
   public static final TileEntityType<TileEntityDropper> DROPPER = registerTileEntityType("dropper", TileEntityType.Builder.create(TileEntityDropper::new));
   public static final TileEntityType<TileEntitySign> SIGN = registerTileEntityType("sign", TileEntityType.Builder.create(TileEntitySign::new));
   public static final TileEntityType<TileEntityMobSpawner> MOB_SPAWNER = registerTileEntityType("mob_spawner", TileEntityType.Builder.create(TileEntityMobSpawner::new));
   public static final TileEntityType<TileEntityPiston> PISTON = registerTileEntityType("piston", TileEntityType.Builder.create(TileEntityPiston::new));
   public static final TileEntityType<TileEntityBrewingStand> BREWING_STAND = registerTileEntityType("brewing_stand", TileEntityType.Builder.create(TileEntityBrewingStand::new));
   public static final TileEntityType<TileEntityEnchantmentTable> ENCHANTING_TABLE = registerTileEntityType("enchanting_table", TileEntityType.Builder.create(TileEntityEnchantmentTable::new));
   public static final TileEntityType<TileEntityEndPortal> END_PORTAL = registerTileEntityType("end_portal", TileEntityType.Builder.create(TileEntityEndPortal::new));
   public static final TileEntityType<TileEntityBeacon> BEACON = registerTileEntityType("beacon", TileEntityType.Builder.create(TileEntityBeacon::new));
   public static final TileEntityType<TileEntitySkull> SKULL = registerTileEntityType("skull", TileEntityType.Builder.create(TileEntitySkull::new));
   public static final TileEntityType<TileEntityDaylightDetector> DAYLIGHT_DETECTOR = registerTileEntityType("daylight_detector", TileEntityType.Builder.create(TileEntityDaylightDetector::new));
   public static final TileEntityType<TileEntityHopper> HOPPER = registerTileEntityType("hopper", TileEntityType.Builder.create(TileEntityHopper::new));
   public static final TileEntityType<TileEntityComparator> COMPARATOR = registerTileEntityType("comparator", TileEntityType.Builder.create(TileEntityComparator::new));
   public static final TileEntityType<TileEntityBanner> BANNER = registerTileEntityType("banner", TileEntityType.Builder.create(TileEntityBanner::new));
   public static final TileEntityType<TileEntityStructure> STRUCTURE_BLOCK = registerTileEntityType("structure_block", TileEntityType.Builder.create(TileEntityStructure::new));
   public static final TileEntityType<TileEntityEndGateway> END_GATEWAY = registerTileEntityType("end_gateway", TileEntityType.Builder.create(TileEntityEndGateway::new));
   public static final TileEntityType<TileEntityCommandBlock> COMMAND_BLOCK = registerTileEntityType("command_block", TileEntityType.Builder.create(TileEntityCommandBlock::new));
   public static final TileEntityType<TileEntityShulkerBox> SHULKER_BOX = registerTileEntityType("shulker_box", TileEntityType.Builder.create(TileEntityShulkerBox::new));
   public static final TileEntityType<TileEntityBed> BED = registerTileEntityType("bed", TileEntityType.Builder.create(TileEntityBed::new));
   public static final TileEntityType<TileEntityConduit> CONDUIT = registerTileEntityType("conduit", TileEntityType.Builder.create(TileEntityConduit::new));
   private final Supplier<? extends T> factory;
   private final Type<?> datafixerType;

   @Nullable
   public static ResourceLocation getId(TileEntityType<?> p_200969_0_) {
      return IRegistry.field_212626_o.func_177774_c(p_200969_0_);
   }

   public static <T extends TileEntity> TileEntityType<T> registerTileEntityType(String p_200966_0_, TileEntityType.Builder<T> p_200966_1_) {
      Type<?> type = null;

      try {
         type = DataFixesManager.getDataFixer().getSchema(DataFixUtils.makeKey(1631)).getChoiceType(TypeReferences.BLOCK_ENTITY, p_200966_0_);
      } catch (IllegalStateException illegalstateexception) {
         if (SharedConstants.developmentMode) {
            throw illegalstateexception;
         }

         LOGGER.warn("No data fixer registered for block entity {}", p_200966_0_);
      }

      TileEntityType<T> tileentitytype = p_200966_1_.build(type);
      IRegistry.field_212626_o.func_82595_a(new ResourceLocation(p_200966_0_), tileentitytype);
      return tileentitytype;
   }

   public static void func_212641_a() {
   }

   public TileEntityType(Supplier<? extends T> p_i49572_1_, Type<?> p_i49572_2_) {
      this.factory = p_i49572_1_;
      this.datafixerType = p_i49572_2_;
   }

   @Nullable
   public T create() {
      return this.factory.get();
   }

   @Nullable
   static TileEntity create(String p_200967_0_) {
      TileEntityType<?> tileentitytype = IRegistry.field_212626_o.func_212608_b(new ResourceLocation(p_200967_0_));
      return tileentitytype == null ? null : tileentitytype.create();
   }

   public static final class Builder<T extends TileEntity> {
      private final Supplier<? extends T> factory;

      private Builder(Supplier<? extends T> p_i48608_1_) {
         this.factory = p_i48608_1_;
      }

      public static <T extends TileEntity> TileEntityType.Builder<T> create(Supplier<? extends T> p_200963_0_) {
         return new TileEntityType.Builder<>(p_200963_0_);
      }

      public TileEntityType<T> build(Type<?> p_206865_1_) {
         return new TileEntityType<>(this.factory, p_206865_1_);
      }
   }
}
