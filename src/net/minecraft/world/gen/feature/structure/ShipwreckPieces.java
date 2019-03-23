package net.minecraft.world.gen.feature.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class ShipwreckPieces {
   private static final BlockPos STRUCTURE_OFFSET = new BlockPos(4, 0, 15);
   private static final ResourceLocation[] field_204761_a = new ResourceLocation[]{new ResourceLocation("shipwreck/with_mast"), new ResourceLocation("shipwreck/sideways_full"), new ResourceLocation("shipwreck/sideways_fronthalf"), new ResourceLocation("shipwreck/sideways_backhalf"), new ResourceLocation("shipwreck/rightsideup_full"), new ResourceLocation("shipwreck/rightsideup_fronthalf"), new ResourceLocation("shipwreck/rightsideup_backhalf"), new ResourceLocation("shipwreck/with_mast_degraded"), new ResourceLocation("shipwreck/rightsideup_full_degraded"), new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"), new ResourceLocation("shipwreck/rightsideup_backhalf_degraded")};
   private static final ResourceLocation[] field_204762_b = new ResourceLocation[]{new ResourceLocation("shipwreck/with_mast"), new ResourceLocation("shipwreck/upsidedown_full"), new ResourceLocation("shipwreck/upsidedown_fronthalf"), new ResourceLocation("shipwreck/upsidedown_backhalf"), new ResourceLocation("shipwreck/sideways_full"), new ResourceLocation("shipwreck/sideways_fronthalf"), new ResourceLocation("shipwreck/sideways_backhalf"), new ResourceLocation("shipwreck/rightsideup_full"), new ResourceLocation("shipwreck/rightsideup_fronthalf"), new ResourceLocation("shipwreck/rightsideup_backhalf"), new ResourceLocation("shipwreck/with_mast_degraded"), new ResourceLocation("shipwreck/upsidedown_full_degraded"), new ResourceLocation("shipwreck/upsidedown_fronthalf_degraded"), new ResourceLocation("shipwreck/upsidedown_backhalf_degraded"), new ResourceLocation("shipwreck/sideways_full_degraded"), new ResourceLocation("shipwreck/sideways_fronthalf_degraded"), new ResourceLocation("shipwreck/sideways_backhalf_degraded"), new ResourceLocation("shipwreck/rightsideup_full_degraded"), new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"), new ResourceLocation("shipwreck/rightsideup_backhalf_degraded")};

   public static void registerShipwreckPieces() {
      StructureIO.registerStructureComponent(ShipwreckPieces.Piece.class, "Shipwreck");
   }

   public static void func_204760_a(TemplateManager p_204760_0_, BlockPos p_204760_1_, Rotation p_204760_2_, List<StructurePiece> p_204760_3_, Random p_204760_4_, ShipwreckConfig p_204760_5_) {
      ResourceLocation resourcelocation = p_204760_5_.field_204753_a ? field_204761_a[p_204760_4_.nextInt(field_204761_a.length)] : field_204762_b[p_204760_4_.nextInt(field_204762_b.length)];
      p_204760_3_.add(new ShipwreckPieces.Piece(p_204760_0_, resourcelocation, p_204760_1_, p_204760_2_, p_204760_5_.field_204753_a));
   }

   public static class Piece extends TemplateStructurePiece {
      private Rotation field_204755_d;
      private ResourceLocation field_204756_e;
      private boolean field_204757_f;

      public Piece() {
      }

      public Piece(TemplateManager p_i48904_1_, ResourceLocation p_i48904_2_, BlockPos p_i48904_3_, Rotation p_i48904_4_, boolean p_i48904_5_) {
         super(0);
         this.templatePosition = p_i48904_3_;
         this.field_204755_d = p_i48904_4_;
         this.field_204756_e = p_i48904_2_;
         this.field_204757_f = p_i48904_5_;
         this.func_204754_a(p_i48904_1_);
      }

      protected void writeStructureToNBT(NBTTagCompound p_143012_1_) {
         super.writeStructureToNBT(p_143012_1_);
         p_143012_1_.setString("Template", this.field_204756_e.toString());
         p_143012_1_.setBoolean("isBeached", this.field_204757_f);
         p_143012_1_.setString("Rot", this.field_204755_d.name());
      }

      protected void readStructureFromNBT(NBTTagCompound p_143011_1_, TemplateManager p_143011_2_) {
         super.readStructureFromNBT(p_143011_1_, p_143011_2_);
         this.field_204756_e = new ResourceLocation(p_143011_1_.getString("Template"));
         this.field_204757_f = p_143011_1_.getBoolean("isBeached");
         this.field_204755_d = Rotation.valueOf(p_143011_1_.getString("Rot"));
         this.func_204754_a(p_143011_2_);
      }

      private void func_204754_a(TemplateManager p_204754_1_) {
         Template template = p_204754_1_.getTemplateDefaulted(this.field_204756_e);
         PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.field_204755_d).setReplacedBlock(Blocks.AIR).setMirror(Mirror.NONE).setCenterOffset(ShipwreckPieces.STRUCTURE_OFFSET);
         this.setup(template, this.templatePosition, placementsettings);
      }

      protected void handleDataMarker(String p_186175_1_, BlockPos p_186175_2_, IWorld p_186175_3_, Random p_186175_4_, MutableBoundingBox p_186175_5_) {
         if ("map_chest".equals(p_186175_1_)) {
            TileEntityLockableLoot.setLootTable(p_186175_3_, p_186175_4_, p_186175_2_.down(), LootTableList.CHESTS_SHIPWRECK_MAP);
         } else if ("treasure_chest".equals(p_186175_1_)) {
            TileEntityLockableLoot.setLootTable(p_186175_3_, p_186175_4_, p_186175_2_.down(), LootTableList.CHESTS_SHIPWRECK_TREASURE);
         } else if ("supply_chest".equals(p_186175_1_)) {
            TileEntityLockableLoot.setLootTable(p_186175_3_, p_186175_4_, p_186175_2_.down(), LootTableList.CHESTS_SHIPWRECK_SUPPLY);
         }

      }

      public boolean addComponentParts(IWorld p_74875_1_, Random p_74875_2_, MutableBoundingBox p_74875_3_, ChunkPos p_74875_4_) {
         int i = 256;
         int j = 0;
         BlockPos blockpos = this.templatePosition.add(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1);

         for(BlockPos blockpos1 : BlockPos.getAllInBox(this.templatePosition, blockpos)) {
            int k = p_74875_1_.getHeight(this.field_204757_f ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG, blockpos1.getX(), blockpos1.getZ());
            j += k;
            i = Math.min(i, k);
         }

         j = j / (this.template.getSize().getX() * this.template.getSize().getZ());
         int l = this.field_204757_f ? i - this.template.getSize().getY() / 2 - p_74875_2_.nextInt(3) : j;
         this.templatePosition = new BlockPos(this.templatePosition.getX(), l, this.templatePosition.getZ());
         return super.addComponentParts(p_74875_1_, p_74875_2_, p_74875_3_, p_74875_4_);
      }
   }
}
