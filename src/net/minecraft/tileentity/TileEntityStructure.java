package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.Rotation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityStructure extends TileEntity {
   private ResourceLocation name;
   private String author = "";
   private String metadata = "";
   private BlockPos position = new BlockPos(0, 1, 0);
   private BlockPos size = BlockPos.ORIGIN;
   private Mirror mirror = Mirror.NONE;
   private Rotation rotation = Rotation.NONE;
   private StructureMode mode = StructureMode.DATA;
   private boolean ignoreEntities = true;
   private boolean powered;
   private boolean showAir;
   private boolean showBoundingBox = true;
   private float integrity = 1.0F;
   private long seed;

   public TileEntityStructure() {
      super(TileEntityType.STRUCTURE_BLOCK);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
      super.writeToNBT(p_189515_1_);
      p_189515_1_.setString("name", this.getName());
      p_189515_1_.setString("author", this.author);
      p_189515_1_.setString("metadata", this.metadata);
      p_189515_1_.setInteger("posX", this.position.getX());
      p_189515_1_.setInteger("posY", this.position.getY());
      p_189515_1_.setInteger("posZ", this.position.getZ());
      p_189515_1_.setInteger("sizeX", this.size.getX());
      p_189515_1_.setInteger("sizeY", this.size.getY());
      p_189515_1_.setInteger("sizeZ", this.size.getZ());
      p_189515_1_.setString("rotation", this.rotation.toString());
      p_189515_1_.setString("mirror", this.mirror.toString());
      p_189515_1_.setString("mode", this.mode.toString());
      p_189515_1_.setBoolean("ignoreEntities", this.ignoreEntities);
      p_189515_1_.setBoolean("powered", this.powered);
      p_189515_1_.setBoolean("showair", this.showAir);
      p_189515_1_.setBoolean("showboundingbox", this.showBoundingBox);
      p_189515_1_.setFloat("integrity", this.integrity);
      p_189515_1_.setLong("seed", this.seed);
      return p_189515_1_;
   }

   public void readFromNBT(NBTTagCompound p_145839_1_) {
      super.readFromNBT(p_145839_1_);
      this.setName(p_145839_1_.getString("name"));
      this.author = p_145839_1_.getString("author");
      this.metadata = p_145839_1_.getString("metadata");
      int i = MathHelper.clamp(p_145839_1_.getInteger("posX"), -32, 32);
      int j = MathHelper.clamp(p_145839_1_.getInteger("posY"), -32, 32);
      int k = MathHelper.clamp(p_145839_1_.getInteger("posZ"), -32, 32);
      this.position = new BlockPos(i, j, k);
      int l = MathHelper.clamp(p_145839_1_.getInteger("sizeX"), 0, 32);
      int i1 = MathHelper.clamp(p_145839_1_.getInteger("sizeY"), 0, 32);
      int j1 = MathHelper.clamp(p_145839_1_.getInteger("sizeZ"), 0, 32);
      this.size = new BlockPos(l, i1, j1);

      try {
         this.rotation = Rotation.valueOf(p_145839_1_.getString("rotation"));
      } catch (IllegalArgumentException var11) {
         this.rotation = Rotation.NONE;
      }

      try {
         this.mirror = Mirror.valueOf(p_145839_1_.getString("mirror"));
      } catch (IllegalArgumentException var10) {
         this.mirror = Mirror.NONE;
      }

      try {
         this.mode = StructureMode.valueOf(p_145839_1_.getString("mode"));
      } catch (IllegalArgumentException var9) {
         this.mode = StructureMode.DATA;
      }

      this.ignoreEntities = p_145839_1_.getBoolean("ignoreEntities");
      this.powered = p_145839_1_.getBoolean("powered");
      this.showAir = p_145839_1_.getBoolean("showair");
      this.showBoundingBox = p_145839_1_.getBoolean("showboundingbox");
      if (p_145839_1_.hasKey("integrity")) {
         this.integrity = p_145839_1_.getFloat("integrity");
      } else {
         this.integrity = 1.0F;
      }

      this.seed = p_145839_1_.getLong("seed");
      this.updateBlockState();
   }

   private void updateBlockState() {
      if (this.world != null) {
         BlockPos blockpos = this.getPos();
         IBlockState iblockstate = this.world.getBlockState(blockpos);
         if (iblockstate.getBlock() == Blocks.STRUCTURE_BLOCK) {
            this.world.setBlockState(blockpos, iblockstate.with(BlockStructure.MODE, this.mode), 2);
         }

      }
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 7, this.getUpdateTag());
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public boolean usedBy(EntityPlayer p_189701_1_) {
      if (!p_189701_1_.canUseCommandBlock()) {
         return false;
      } else {
         if (p_189701_1_.getEntityWorld().isRemote) {
            p_189701_1_.openEditStructure(this);
         }

         return true;
      }
   }

   public String getName() {
      return this.name == null ? "" : this.name.toString();
   }

   public boolean hasName() {
      return this.name != null;
   }

   public void setName(@Nullable String p_184404_1_) {
      this.setName(StringUtils.isNullOrEmpty(p_184404_1_) ? null : ResourceLocation.makeResourceLocation(p_184404_1_));
   }

   public void setName(@Nullable ResourceLocation p_210163_1_) {
      this.name = p_210163_1_;
   }

   public void createdBy(EntityLivingBase p_189720_1_) {
      this.author = p_189720_1_.getName().getString();
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPosition() {
      return this.position;
   }

   public void setPosition(BlockPos p_184414_1_) {
      this.position = p_184414_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getStructureSize() {
      return this.size;
   }

   public void setSize(BlockPos p_184409_1_) {
      this.size = p_184409_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public Mirror getMirror() {
      return this.mirror;
   }

   public void setMirror(Mirror p_184411_1_) {
      this.mirror = p_184411_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public Rotation getRotation() {
      return this.rotation;
   }

   public void setRotation(Rotation p_184408_1_) {
      this.rotation = p_184408_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public String getMetadata() {
      return this.metadata;
   }

   public void setMetadata(String p_184410_1_) {
      this.metadata = p_184410_1_;
   }

   public StructureMode getMode() {
      return this.mode;
   }

   public void setMode(StructureMode p_184405_1_) {
      this.mode = p_184405_1_;
      IBlockState iblockstate = this.world.getBlockState(this.getPos());
      if (iblockstate.getBlock() == Blocks.STRUCTURE_BLOCK) {
         this.world.setBlockState(this.getPos(), iblockstate.with(BlockStructure.MODE, p_184405_1_), 2);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void nextMode() {
      switch(this.getMode()) {
      case SAVE:
         this.setMode(StructureMode.LOAD);
         break;
      case LOAD:
         this.setMode(StructureMode.CORNER);
         break;
      case CORNER:
         this.setMode(StructureMode.DATA);
         break;
      case DATA:
         this.setMode(StructureMode.SAVE);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean ignoresEntities() {
      return this.ignoreEntities;
   }

   public void setIgnoresEntities(boolean p_184406_1_) {
      this.ignoreEntities = p_184406_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public float getIntegrity() {
      return this.integrity;
   }

   public void setIntegrity(float p_189718_1_) {
      this.integrity = p_189718_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public long getSeed() {
      return this.seed;
   }

   public void setSeed(long p_189725_1_) {
      this.seed = p_189725_1_;
   }

   public boolean detectSize() {
      if (this.mode != StructureMode.SAVE) {
         return false;
      } else {
         BlockPos blockpos = this.getPos();
         int i = 80;
         BlockPos blockpos1 = new BlockPos(blockpos.getX() - 80, 0, blockpos.getZ() - 80);
         BlockPos blockpos2 = new BlockPos(blockpos.getX() + 80, 255, blockpos.getZ() + 80);
         List<TileEntityStructure> list = this.getNearbyCornerBlocks(blockpos1, blockpos2);
         List<TileEntityStructure> list1 = this.filterRelatedCornerBlocks(list);
         if (list1.size() < 1) {
            return false;
         } else {
            MutableBoundingBox mutableboundingbox = this.calculateEnclosingBoundingBox(blockpos, list1);
            if (mutableboundingbox.maxX - mutableboundingbox.minX > 1 && mutableboundingbox.maxY - mutableboundingbox.minY > 1 && mutableboundingbox.maxZ - mutableboundingbox.minZ > 1) {
               this.position = new BlockPos(mutableboundingbox.minX - blockpos.getX() + 1, mutableboundingbox.minY - blockpos.getY() + 1, mutableboundingbox.minZ - blockpos.getZ() + 1);
               this.size = new BlockPos(mutableboundingbox.maxX - mutableboundingbox.minX - 1, mutableboundingbox.maxY - mutableboundingbox.minY - 1, mutableboundingbox.maxZ - mutableboundingbox.minZ - 1);
               this.markDirty();
               IBlockState iblockstate = this.world.getBlockState(blockpos);
               this.world.notifyBlockUpdate(blockpos, iblockstate, iblockstate, 3);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   private List<TileEntityStructure> filterRelatedCornerBlocks(List<TileEntityStructure> p_184415_1_) {
      Predicate<TileEntityStructure> predicate = (p_200665_1_) -> {
         return p_200665_1_.mode == StructureMode.CORNER && Objects.equals(this.name, p_200665_1_.name);
      };
      return p_184415_1_.stream().filter(predicate).collect(Collectors.toList());
   }

   private List<TileEntityStructure> getNearbyCornerBlocks(BlockPos p_184418_1_, BlockPos p_184418_2_) {
      List<TileEntityStructure> list = Lists.newArrayList();

      for(BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(p_184418_1_, p_184418_2_)) {
         IBlockState iblockstate = this.world.getBlockState(blockpos$mutableblockpos);
         if (iblockstate.getBlock() == Blocks.STRUCTURE_BLOCK) {
            TileEntity tileentity = this.world.getTileEntity(blockpos$mutableblockpos);
            if (tileentity != null && tileentity instanceof TileEntityStructure) {
               list.add((TileEntityStructure)tileentity);
            }
         }
      }

      return list;
   }

   private MutableBoundingBox calculateEnclosingBoundingBox(BlockPos p_184416_1_, List<TileEntityStructure> p_184416_2_) {
      MutableBoundingBox mutableboundingbox;
      if (p_184416_2_.size() > 1) {
         BlockPos blockpos = p_184416_2_.get(0).getPos();
         mutableboundingbox = new MutableBoundingBox(blockpos, blockpos);
      } else {
         mutableboundingbox = new MutableBoundingBox(p_184416_1_, p_184416_1_);
      }

      for(TileEntityStructure tileentitystructure : p_184416_2_) {
         BlockPos blockpos1 = tileentitystructure.getPos();
         if (blockpos1.getX() < mutableboundingbox.minX) {
            mutableboundingbox.minX = blockpos1.getX();
         } else if (blockpos1.getX() > mutableboundingbox.maxX) {
            mutableboundingbox.maxX = blockpos1.getX();
         }

         if (blockpos1.getY() < mutableboundingbox.minY) {
            mutableboundingbox.minY = blockpos1.getY();
         } else if (blockpos1.getY() > mutableboundingbox.maxY) {
            mutableboundingbox.maxY = blockpos1.getY();
         }

         if (blockpos1.getZ() < mutableboundingbox.minZ) {
            mutableboundingbox.minZ = blockpos1.getZ();
         } else if (blockpos1.getZ() > mutableboundingbox.maxZ) {
            mutableboundingbox.maxZ = blockpos1.getZ();
         }
      }

      return mutableboundingbox;
   }

   public boolean save() {
      return this.save(true);
   }

   public boolean save(boolean p_189712_1_) {
      if (this.mode == StructureMode.SAVE && !this.world.isRemote && this.name != null) {
         BlockPos blockpos = this.getPos().add(this.position);
         WorldServer worldserver = (WorldServer)this.world;
         TemplateManager templatemanager = worldserver.getStructureTemplateManager();

         Template template;
         try {
            template = templatemanager.getTemplateDefaulted(this.name);
         } catch (ResourceLocationException var8) {
            return false;
         }

         template.takeBlocksFromWorld(this.world, blockpos, this.size, !this.ignoreEntities, Blocks.STRUCTURE_VOID);
         template.setAuthor(this.author);
         if (p_189712_1_) {
            try {
               return templatemanager.writeToFile(this.name);
            } catch (ResourceLocationException var7) {
               return false;
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean load() {
      return this.load(true);
   }

   public boolean load(boolean p_189714_1_) {
      if (this.mode == StructureMode.LOAD && !this.world.isRemote && this.name != null) {
         BlockPos blockpos = this.getPos();
         BlockPos blockpos1 = blockpos.add(this.position);
         WorldServer worldserver = (WorldServer)this.world;
         TemplateManager templatemanager = worldserver.getStructureTemplateManager();

         Template template;
         try {
            template = templatemanager.getTemplate(this.name);
         } catch (ResourceLocationException var10) {
            return false;
         }

         if (template == null) {
            return false;
         } else {
            if (!StringUtils.isNullOrEmpty(template.getAuthor())) {
               this.author = template.getAuthor();
            }

            BlockPos blockpos2 = template.getSize();
            boolean flag = this.size.equals(blockpos2);
            if (!flag) {
               this.size = blockpos2;
               this.markDirty();
               IBlockState iblockstate = this.world.getBlockState(blockpos);
               this.world.notifyBlockUpdate(blockpos, iblockstate, iblockstate, 3);
            }

            if (p_189714_1_ && !flag) {
               return false;
            } else {
               PlacementSettings placementsettings = (new PlacementSettings()).setMirror(this.mirror).setRotation(this.rotation).setIgnoreEntities(this.ignoreEntities).setChunk(
                       null).setReplacedBlock(
                       null).setIgnoreStructureBlock(false);
               if (this.integrity < 1.0F) {
                  placementsettings.setIntegrity(MathHelper.clamp(this.integrity, 0.0F, 1.0F)).setSeed(this.seed);
               }

               template.addBlocksToWorldChunk(this.world, blockpos1, placementsettings);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public void unloadStructure() {
      if (this.name != null) {
         WorldServer worldserver = (WorldServer)this.world;
         TemplateManager templatemanager = worldserver.getStructureTemplateManager();
         templatemanager.remove(this.name);
      }
   }

   public boolean isStructureLoadable() {
      if (this.mode == StructureMode.LOAD && !this.world.isRemote && this.name != null) {
         WorldServer worldserver = (WorldServer)this.world;
         TemplateManager templatemanager = worldserver.getStructureTemplateManager();

         try {
            return templatemanager.getTemplate(this.name) != null;
         } catch (ResourceLocationException var4) {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isPowered() {
      return this.powered;
   }

   public void setPowered(boolean p_189723_1_) {
      this.powered = p_189723_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean showsAir() {
      return this.showAir;
   }

   public void setShowAir(boolean p_189703_1_) {
      this.showAir = p_189703_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean showsBoundingBox() {
      return this.showBoundingBox;
   }

   public void setShowBoundingBox(boolean p_189710_1_) {
      this.showBoundingBox = p_189710_1_;
   }

   public enum UpdateCommand {
      UPDATE_DATA,
      SAVE_AREA,
      LOAD_AREA,
      SCAN_AREA
   }
}
