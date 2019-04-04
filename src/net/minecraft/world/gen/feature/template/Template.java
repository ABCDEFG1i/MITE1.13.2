package net.minecraft.world.gen.feature.template;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePartBitSet;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class Template {
   private final List<List<Template.BlockInfo>> blocks = Lists.newArrayList();
   private final List<Template.EntityInfo> entities = Lists.newArrayList();
   private BlockPos size = BlockPos.ORIGIN;
   private String author = "?";

   public BlockPos getSize() {
      return this.size;
   }

   public void setAuthor(String p_186252_1_) {
      this.author = p_186252_1_;
   }

   public String getAuthor() {
      return this.author;
   }

   public void takeBlocksFromWorld(World p_186254_1_, BlockPos p_186254_2_, BlockPos p_186254_3_, boolean p_186254_4_, @Nullable Block p_186254_5_) {
      if (p_186254_3_.getX() >= 1 && p_186254_3_.getY() >= 1 && p_186254_3_.getZ() >= 1) {
         BlockPos blockpos = p_186254_2_.add(p_186254_3_).add(-1, -1, -1);
         List<Template.BlockInfo> list = Lists.newArrayList();
         List<Template.BlockInfo> list1 = Lists.newArrayList();
         List<Template.BlockInfo> list2 = Lists.newArrayList();
         BlockPos blockpos1 = new BlockPos(Math.min(p_186254_2_.getX(), blockpos.getX()), Math.min(p_186254_2_.getY(), blockpos.getY()), Math.min(p_186254_2_.getZ(), blockpos.getZ()));
         BlockPos blockpos2 = new BlockPos(Math.max(p_186254_2_.getX(), blockpos.getX()), Math.max(p_186254_2_.getY(), blockpos.getY()), Math.max(p_186254_2_.getZ(), blockpos.getZ()));
         this.size = p_186254_3_;

         for(BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(blockpos1, blockpos2)) {
            BlockPos blockpos3 = blockpos$mutableblockpos.subtract(blockpos1);
            IBlockState iblockstate = p_186254_1_.getBlockState(blockpos$mutableblockpos);
            if (p_186254_5_ == null || p_186254_5_ != iblockstate.getBlock()) {
               TileEntity tileentity = p_186254_1_.getTileEntity(blockpos$mutableblockpos);
               if (tileentity != null) {
                  NBTTagCompound nbttagcompound = tileentity.writeToNBT(new NBTTagCompound());
                  nbttagcompound.removeTag("x");
                  nbttagcompound.removeTag("y");
                  nbttagcompound.removeTag("z");
                  list1.add(new Template.BlockInfo(blockpos3, iblockstate, nbttagcompound));
               } else if (!iblockstate.isOpaqueCube(p_186254_1_, blockpos$mutableblockpos) && !iblockstate.isFullCube()) {
                  list2.add(new Template.BlockInfo(blockpos3, iblockstate, null));
               } else {
                  list.add(new Template.BlockInfo(blockpos3, iblockstate, null));
               }
            }
         }

         List<Template.BlockInfo> list3 = Lists.newArrayList();
         list3.addAll(list);
         list3.addAll(list1);
         list3.addAll(list2);
         this.blocks.clear();
         this.blocks.add(list3);
         if (p_186254_4_) {
            this.takeEntitiesFromWorld(p_186254_1_, blockpos1, blockpos2.add(1, 1, 1));
         } else {
            this.entities.clear();
         }

      }
   }

   private void takeEntitiesFromWorld(World p_186255_1_, BlockPos p_186255_2_, BlockPos p_186255_3_) {
      List<Entity> list = p_186255_1_.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(p_186255_2_, p_186255_3_), (p_201048_0_) -> {
         return !(p_201048_0_ instanceof EntityPlayer);
      });
      this.entities.clear();

      for(Entity entity : list) {
         Vec3d vec3d = new Vec3d(entity.posX - (double)p_186255_2_.getX(), entity.posY - (double)p_186255_2_.getY(), entity.posZ - (double)p_186255_2_.getZ());
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         entity.writeToNBTOptional(nbttagcompound);
         BlockPos blockpos;
         if (entity instanceof EntityPainting) {
            blockpos = ((EntityPainting)entity).getHangingPosition().subtract(p_186255_2_);
         } else {
            blockpos = new BlockPos(vec3d);
         }

         this.entities.add(new Template.EntityInfo(vec3d, blockpos, nbttagcompound));
      }

   }

   public Map<BlockPos, String> getDataBlocks(BlockPos p_186258_1_, PlacementSettings p_186258_2_) {
      Map<BlockPos, String> map = Maps.newHashMap();
      MutableBoundingBox mutableboundingbox = p_186258_2_.getBoundingBox();

      for(Template.BlockInfo template$blockinfo : p_186258_2_.func_204764_a(this.blocks, p_186258_1_)) {
         BlockPos blockpos = transformedBlockPos(p_186258_2_, template$blockinfo.pos).add(p_186258_1_);
         if (mutableboundingbox == null || mutableboundingbox.isVecInside(blockpos)) {
            IBlockState iblockstate = template$blockinfo.blockState;
            if (iblockstate.getBlock() == Blocks.STRUCTURE_BLOCK && template$blockinfo.tileentityData != null) {
               StructureMode structuremode = StructureMode.valueOf(template$blockinfo.tileentityData.getString("mode"));
               if (structuremode == StructureMode.DATA) {
                  map.put(blockpos, template$blockinfo.tileentityData.getString("metadata"));
               }
            }
         }
      }

      return map;
   }

   public BlockPos calculateConnectedPos(PlacementSettings p_186262_1_, BlockPos p_186262_2_, PlacementSettings p_186262_3_, BlockPos p_186262_4_) {
      BlockPos blockpos = transformedBlockPos(p_186262_1_, p_186262_2_);
      BlockPos blockpos1 = transformedBlockPos(p_186262_3_, p_186262_4_);
      return blockpos.subtract(blockpos1);
   }

   public static BlockPos transformedBlockPos(PlacementSettings p_186266_0_, BlockPos p_186266_1_) {
      return getTransformedPos(p_186266_1_, p_186266_0_.getMirror(), p_186266_0_.getRotation(), p_186266_0_.func_207664_d());
   }

   public void addBlocksToWorldChunk(IWorld p_186260_1_, BlockPos p_186260_2_, PlacementSettings p_186260_3_) {
      p_186260_3_.setBoundingBoxFromChunk();
      this.addBlocksToWorld(p_186260_1_, p_186260_2_, p_186260_3_);
   }

   public void addBlocksToWorld(IWorld p_186253_1_, BlockPos p_186253_2_, PlacementSettings p_186253_3_) {
      this.addBlocksToWorld(p_186253_1_, p_186253_2_, new IntegrityProcessor(p_186253_2_, p_186253_3_), p_186253_3_, 2);
   }

   public boolean addBlocksToWorld(IWorld p_189962_1_, BlockPos p_189962_2_, PlacementSettings p_189962_3_, int p_189962_4_) {
      return this.addBlocksToWorld(p_189962_1_, p_189962_2_, new IntegrityProcessor(p_189962_2_, p_189962_3_), p_189962_3_, p_189962_4_);
   }

   public boolean addBlocksToWorld(IWorld p_189960_1_, BlockPos p_189960_2_, @Nullable ITemplateProcessor p_189960_3_, PlacementSettings p_189960_4_, int p_189960_5_) {
      if (this.blocks.isEmpty()) {
         return false;
      } else {
         List<Template.BlockInfo> list = p_189960_4_.func_204764_a(this.blocks, p_189960_2_);
         if ((!list.isEmpty() || !p_189960_4_.getIgnoreEntities() && !this.entities.isEmpty()) && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1) {
            Block block = p_189960_4_.getReplacedBlock();
            MutableBoundingBox mutableboundingbox = p_189960_4_.getBoundingBox();
            List<BlockPos> list1 = Lists.newArrayListWithCapacity(p_189960_4_.func_204763_l() ? list.size() : 0);
            List<Pair<BlockPos, NBTTagCompound>> list2 = Lists.newArrayListWithCapacity(list.size());
            int i = Integer.MAX_VALUE;
            int j = Integer.MAX_VALUE;
            int k = Integer.MAX_VALUE;
            int l = Integer.MIN_VALUE;
            int i1 = Integer.MIN_VALUE;
            int j1 = Integer.MIN_VALUE;

            for(Template.BlockInfo template$blockinfo : list) {
               BlockPos blockpos = transformedBlockPos(p_189960_4_, template$blockinfo.pos).add(p_189960_2_);
               Template.BlockInfo template$blockinfo1 = p_189960_3_ != null ? p_189960_3_.processBlock(p_189960_1_, blockpos, template$blockinfo) : template$blockinfo;
               if (template$blockinfo1 != null) {
                  Block block1 = template$blockinfo1.blockState.getBlock();
                  if ((block == null || block != block1) && (!p_189960_4_.getIgnoreStructureBlock() || block1 != Blocks.STRUCTURE_BLOCK) && (mutableboundingbox == null || mutableboundingbox.isVecInside(blockpos))) {
                     IFluidState ifluidstate = p_189960_4_.func_204763_l() ? p_189960_1_.getFluidState(blockpos) : null;
                     IBlockState iblockstate = template$blockinfo1.blockState.mirror(p_189960_4_.getMirror());
                     IBlockState iblockstate1 = iblockstate.rotate(p_189960_4_.getRotation());
                     if (template$blockinfo1.tileentityData != null) {
                        TileEntity tileentity = p_189960_1_.getTileEntity(blockpos);
                        if (tileentity instanceof IInventory) {
                           ((IInventory)tileentity).clear();
                        }

                        p_189960_1_.setBlockState(blockpos, Blocks.BARRIER.getDefaultState(), 4);
                     }

                     if (p_189960_1_.setBlockState(blockpos, iblockstate1, p_189960_5_)) {
                        i = Math.min(i, blockpos.getX());
                        j = Math.min(j, blockpos.getY());
                        k = Math.min(k, blockpos.getZ());
                        l = Math.max(l, blockpos.getX());
                        i1 = Math.max(i1, blockpos.getY());
                        j1 = Math.max(j1, blockpos.getZ());
                        list2.add(Pair.of(blockpos, template$blockinfo.tileentityData));
                        if (template$blockinfo1.tileentityData != null) {
                           TileEntity tileentity2 = p_189960_1_.getTileEntity(blockpos);
                           if (tileentity2 != null) {
                              template$blockinfo1.tileentityData.setInteger("x", blockpos.getX());
                              template$blockinfo1.tileentityData.setInteger("y", blockpos.getY());
                              template$blockinfo1.tileentityData.setInteger("z", blockpos.getZ());
                              tileentity2.readFromNBT(template$blockinfo1.tileentityData);
                              tileentity2.mirror(p_189960_4_.getMirror());
                              tileentity2.rotate(p_189960_4_.getRotation());
                           }
                        }

                        if (ifluidstate != null && iblockstate1.getBlock() instanceof ILiquidContainer) {
                           ((ILiquidContainer)iblockstate1.getBlock()).receiveFluid(p_189960_1_, blockpos, iblockstate1, ifluidstate);
                           if (!ifluidstate.isSource()) {
                              list1.add(blockpos);
                           }
                        }
                     }
                  }
               }
            }

            boolean flag = true;
            EnumFacing[] aenumfacing = new EnumFacing[]{EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};

            while(flag && !list1.isEmpty()) {
               flag = false;
               Iterator<BlockPos> iterator = list1.iterator();

               while(iterator.hasNext()) {
                  BlockPos blockpos1 = iterator.next();
                  IFluidState ifluidstate1 = p_189960_1_.getFluidState(blockpos1);

                  for(int i2 = 0; i2 < aenumfacing.length && !ifluidstate1.isSource(); ++i2) {
                     IFluidState ifluidstate2 = p_189960_1_.getFluidState(blockpos1.offset(aenumfacing[i2]));
                     if (ifluidstate2.getHeight() > ifluidstate1.getHeight() || ifluidstate2.isSource() && !ifluidstate1.isSource()) {
                        ifluidstate1 = ifluidstate2;
                     }
                  }

                  if (ifluidstate1.isSource()) {
                     IBlockState iblockstate4 = p_189960_1_.getBlockState(blockpos1);
                     if (iblockstate4.getBlock() instanceof ILiquidContainer) {
                        ((ILiquidContainer)iblockstate4.getBlock()).receiveFluid(p_189960_1_, blockpos1, iblockstate4, ifluidstate1);
                        flag = true;
                        iterator.remove();
                     }
                  }
               }
            }

            if (i <= l) {
               VoxelShapePart voxelshapepart = new VoxelShapePartBitSet(l - i + 1, i1 - j + 1, j1 - k + 1);
               int k1 = i;
               int l1 = j;
               int j2 = k;

               for(Pair<BlockPos, NBTTagCompound> pair : list2) {
                  BlockPos blockpos2 = pair.getFirst();
                  voxelshapepart.func_199625_a(blockpos2.getX() - k1, blockpos2.getY() - l1, blockpos2.getZ() - j2, true, true);
               }

               voxelshapepart.forEachFace((p_211754_5_, p_211754_6_, p_211754_7_, p_211754_8_) -> {
                  BlockPos blockpos4 = new BlockPos(k1 + p_211754_6_, l1 + p_211754_7_, j2 + p_211754_8_);
                  BlockPos blockpos5 = blockpos4.offset(p_211754_5_);
                  IBlockState iblockstate5 = p_189960_1_.getBlockState(blockpos4);
                  IBlockState iblockstate6 = p_189960_1_.getBlockState(blockpos5);
                  IBlockState iblockstate7 = iblockstate5.updatePostPlacement(p_211754_5_, iblockstate6, p_189960_1_, blockpos4, blockpos5);
                  if (iblockstate5 != iblockstate7) {
                     p_189960_1_.setBlockState(blockpos4, iblockstate7, p_189960_5_ & -2 | 16);
                  }

                  IBlockState iblockstate8 = iblockstate6.updatePostPlacement(p_211754_5_.getOpposite(), iblockstate7, p_189960_1_, blockpos5, blockpos4);
                  if (iblockstate6 != iblockstate8) {
                     p_189960_1_.setBlockState(blockpos5, iblockstate8, p_189960_5_ & -2 | 16);
                  }

               });

               for(Pair<BlockPos, NBTTagCompound> pair1 : list2) {
                  BlockPos blockpos3 = pair1.getFirst();
                  IBlockState iblockstate2 = p_189960_1_.getBlockState(blockpos3);
                  IBlockState iblockstate3 = Block.getValidBlockForPosition(iblockstate2, p_189960_1_, blockpos3);
                  if (iblockstate2 != iblockstate3) {
                     p_189960_1_.setBlockState(blockpos3, iblockstate3, p_189960_5_ & -2 | 16);
                  }

                  p_189960_1_.notifyNeighbors(blockpos3, iblockstate3.getBlock());
                  if (pair1.getSecond() != null) {
                     TileEntity tileentity1 = p_189960_1_.getTileEntity(blockpos3);
                     if (tileentity1 != null) {
                        tileentity1.markDirty();
                     }
                  }
               }
            }

            if (!p_189960_4_.getIgnoreEntities()) {
               this.func_207668_a(p_189960_1_, p_189960_2_, p_189960_4_.getMirror(), p_189960_4_.getRotation(), p_189960_4_.func_207664_d(), mutableboundingbox);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   private void func_207668_a(IWorld p_207668_1_, BlockPos p_207668_2_, Mirror p_207668_3_, Rotation p_207668_4_, BlockPos p_207668_5_, @Nullable MutableBoundingBox p_207668_6_) {
      for(Template.EntityInfo template$entityinfo : this.entities) {
         BlockPos blockpos = getTransformedPos(template$entityinfo.blockPos, p_207668_3_, p_207668_4_, p_207668_5_).add(p_207668_2_);
         if (p_207668_6_ == null || p_207668_6_.isVecInside(blockpos)) {
            NBTTagCompound nbttagcompound = template$entityinfo.entityData;
            Vec3d vec3d = getTransformedPos(template$entityinfo.pos, p_207668_3_, p_207668_4_, p_207668_5_);
            Vec3d vec3d1 = vec3d.add((double)p_207668_2_.getX(), (double)p_207668_2_.getY(), (double)p_207668_2_.getZ());
            NBTTagList nbttaglist = new NBTTagList();
            nbttaglist.add(new NBTTagDouble(vec3d1.x));
            nbttaglist.add(new NBTTagDouble(vec3d1.y));
            nbttaglist.add(new NBTTagDouble(vec3d1.z));
            nbttagcompound.setTag("Pos", nbttaglist);
            nbttagcompound.setUniqueId("UUID", UUID.randomUUID());

            Entity entity;
            try {
               entity = EntityType.create(nbttagcompound, p_207668_1_.getWorld());
            } catch (Exception var16) {
               entity = null;
            }

            if (entity != null) {
               float f = entity.getMirroredYaw(p_207668_3_);
               f = f + (entity.rotationYaw - entity.getRotatedYaw(p_207668_4_));
               entity.setLocationAndAngles(vec3d1.x, vec3d1.y, vec3d1.z, f, entity.rotationPitch);
               p_207668_1_.spawnEntity(entity);
            }
         }
      }

   }

   public BlockPos transformedSize(Rotation p_186257_1_) {
      switch(p_186257_1_) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         return new BlockPos(this.size.getZ(), this.size.getY(), this.size.getX());
      default:
         return this.size;
      }
   }

   public static BlockPos getTransformedPos(BlockPos p_207669_0_, Mirror p_207669_1_, Rotation p_207669_2_, BlockPos p_207669_3_) {
      int i = p_207669_0_.getX();
      int j = p_207669_0_.getY();
      int k = p_207669_0_.getZ();
      boolean flag = true;
      switch(p_207669_1_) {
      case LEFT_RIGHT:
         k = -k;
         break;
      case FRONT_BACK:
         i = -i;
         break;
      default:
         flag = false;
      }

      int l = p_207669_3_.getX();
      int i1 = p_207669_3_.getZ();
      switch(p_207669_2_) {
      case COUNTERCLOCKWISE_90:
         return new BlockPos(l - i1 + k, j, l + i1 - i);
      case CLOCKWISE_90:
         return new BlockPos(l + i1 - k, j, i1 - l + i);
      case CLOCKWISE_180:
         return new BlockPos(l + l - i, j, i1 + i1 - k);
      default:
         return flag ? new BlockPos(i, j, k) : p_207669_0_;
      }
   }

   private static Vec3d getTransformedPos(Vec3d p_207667_0_, Mirror p_207667_1_, Rotation p_207667_2_, BlockPos p_207667_3_) {
      double d0 = p_207667_0_.x;
      double d1 = p_207667_0_.y;
      double d2 = p_207667_0_.z;
      boolean flag = true;
      switch(p_207667_1_) {
      case LEFT_RIGHT:
         d2 = 1.0D - d2;
         break;
      case FRONT_BACK:
         d0 = 1.0D - d0;
         break;
      default:
         flag = false;
      }

      int i = p_207667_3_.getX();
      int j = p_207667_3_.getZ();
      switch(p_207667_2_) {
      case COUNTERCLOCKWISE_90:
         return new Vec3d((double)(i - j) + d2, d1, (double)(i + j + 1) - d0);
      case CLOCKWISE_90:
         return new Vec3d((double)(i + j + 1) - d2, d1, (double)(j - i) + d0);
      case CLOCKWISE_180:
         return new Vec3d((double)(i + i + 1) - d0, d1, (double)(j + j + 1) - d2);
      default:
         return flag ? new Vec3d(d0, d1, d2) : p_207667_0_;
      }
   }

   public BlockPos getZeroPositionWithTransform(BlockPos p_189961_1_, Mirror p_189961_2_, Rotation p_189961_3_) {
      return getZeroPositionWithTransform(p_189961_1_, p_189961_2_, p_189961_3_, this.getSize().getX(), this.getSize().getZ());
   }

   public static BlockPos getZeroPositionWithTransform(BlockPos p_191157_0_, Mirror p_191157_1_, Rotation p_191157_2_, int p_191157_3_, int p_191157_4_) {
      --p_191157_3_;
      --p_191157_4_;
      int i = p_191157_1_ == Mirror.FRONT_BACK ? p_191157_3_ : 0;
      int j = p_191157_1_ == Mirror.LEFT_RIGHT ? p_191157_4_ : 0;
      BlockPos blockpos = p_191157_0_;
      switch(p_191157_2_) {
      case COUNTERCLOCKWISE_90:
         blockpos = p_191157_0_.add(j, 0, p_191157_3_ - i);
         break;
      case CLOCKWISE_90:
         blockpos = p_191157_0_.add(p_191157_4_ - j, 0, i);
         break;
      case CLOCKWISE_180:
         blockpos = p_191157_0_.add(p_191157_3_ - i, 0, p_191157_4_ - j);
         break;
      case NONE:
         blockpos = p_191157_0_.add(i, 0, j);
      }

      return blockpos;
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189552_1_) {
      if (this.blocks.isEmpty()) {
         p_189552_1_.setTag("blocks", new NBTTagList());
         p_189552_1_.setTag("palette", new NBTTagList());
      } else {
         List<Template.BasicPalette> list = Lists.newArrayList();
         Template.BasicPalette template$basicpalette = new Template.BasicPalette();
         list.add(template$basicpalette);

         for(int i = 1; i < this.blocks.size(); ++i) {
            list.add(new Template.BasicPalette());
         }

         NBTTagList nbttaglist1 = new NBTTagList();
         List<Template.BlockInfo> list1 = this.blocks.get(0);

         for(int j = 0; j < list1.size(); ++j) {
            Template.BlockInfo template$blockinfo = list1.get(j);
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setTag("pos", this.writeInts(template$blockinfo.pos.getX(), template$blockinfo.pos.getY(), template$blockinfo.pos.getZ()));
            int k = template$basicpalette.idFor(template$blockinfo.blockState);
            nbttagcompound.setInteger("state", k);
            if (template$blockinfo.tileentityData != null) {
               nbttagcompound.setTag("nbt", template$blockinfo.tileentityData);
            }

            nbttaglist1.add(nbttagcompound);

            for(int l = 1; l < this.blocks.size(); ++l) {
               Template.BasicPalette template$basicpalette1 = list.get(l);
               template$basicpalette1.addMapping((this.blocks.get(j).get(j)).blockState, k);
            }
         }

         p_189552_1_.setTag("blocks", nbttaglist1);
         if (list.size() == 1) {
            NBTTagList nbttaglist2 = new NBTTagList();

            for(IBlockState iblockstate : template$basicpalette) {
               nbttaglist2.add(NBTUtil.writeBlockState(iblockstate));
            }

            p_189552_1_.setTag("palette", nbttaglist2);
         } else {
            NBTTagList nbttaglist3 = new NBTTagList();

            for(Template.BasicPalette template$basicpalette2 : list) {
               NBTTagList nbttaglist4 = new NBTTagList();

               for(IBlockState iblockstate1 : template$basicpalette2) {
                  nbttaglist4.add(NBTUtil.writeBlockState(iblockstate1));
               }

               nbttaglist3.add(nbttaglist4);
            }

            p_189552_1_.setTag("palettes", nbttaglist3);
         }
      }

      NBTTagList nbttaglist = new NBTTagList();

      for(Template.EntityInfo template$entityinfo : this.entities) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         nbttagcompound1.setTag("pos", this.writeDoubles(template$entityinfo.pos.x, template$entityinfo.pos.y, template$entityinfo.pos.z));
         nbttagcompound1.setTag("blockPos", this.writeInts(template$entityinfo.blockPos.getX(), template$entityinfo.blockPos.getY(), template$entityinfo.blockPos.getZ()));
         if (template$entityinfo.entityData != null) {
            nbttagcompound1.setTag("nbt", template$entityinfo.entityData);
         }

         nbttaglist.add(nbttagcompound1);
      }

      p_189552_1_.setTag("entities", nbttaglist);
      p_189552_1_.setTag("size", this.writeInts(this.size.getX(), this.size.getY(), this.size.getZ()));
      p_189552_1_.setInteger("DataVersion", 1631);
      return p_189552_1_;
   }

   public void read(NBTTagCompound p_186256_1_) {
      this.blocks.clear();
      this.entities.clear();
      NBTTagList nbttaglist = p_186256_1_.getTagList("size", 3);
      this.size = new BlockPos(nbttaglist.getIntAt(0), nbttaglist.getIntAt(1), nbttaglist.getIntAt(2));
      NBTTagList nbttaglist1 = p_186256_1_.getTagList("blocks", 10);
      if (p_186256_1_.hasKey("palettes", 9)) {
         NBTTagList nbttaglist2 = p_186256_1_.getTagList("palettes", 9);

         for(int i = 0; i < nbttaglist2.size(); ++i) {
            this.func_204768_a(nbttaglist2.getTagListAt(i), nbttaglist1);
         }
      } else {
         this.func_204768_a(p_186256_1_.getTagList("palette", 10), nbttaglist1);
      }

      NBTTagList nbttaglist5 = p_186256_1_.getTagList("entities", 10);

      for(int j = 0; j < nbttaglist5.size(); ++j) {
         NBTTagCompound nbttagcompound = nbttaglist5.getCompoundTagAt(j);
         NBTTagList nbttaglist3 = nbttagcompound.getTagList("pos", 6);
         Vec3d vec3d = new Vec3d(nbttaglist3.getDoubleAt(0), nbttaglist3.getDoubleAt(1), nbttaglist3.getDoubleAt(2));
         NBTTagList nbttaglist4 = nbttagcompound.getTagList("blockPos", 3);
         BlockPos blockpos = new BlockPos(nbttaglist4.getIntAt(0), nbttaglist4.getIntAt(1), nbttaglist4.getIntAt(2));
         if (nbttagcompound.hasKey("nbt")) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("nbt");
            this.entities.add(new Template.EntityInfo(vec3d, blockpos, nbttagcompound1));
         }
      }

   }

   private void func_204768_a(NBTTagList p_204768_1_, NBTTagList p_204768_2_) {
      Template.BasicPalette template$basicpalette = new Template.BasicPalette();
      List<Template.BlockInfo> list = Lists.newArrayList();

      for(int i = 0; i < p_204768_1_.size(); ++i) {
         template$basicpalette.addMapping(NBTUtil.readBlockState(p_204768_1_.getCompoundTagAt(i)), i);
      }

      for(int j = 0; j < p_204768_2_.size(); ++j) {
         NBTTagCompound nbttagcompound = p_204768_2_.getCompoundTagAt(j);
         NBTTagList nbttaglist = nbttagcompound.getTagList("pos", 3);
         BlockPos blockpos = new BlockPos(nbttaglist.getIntAt(0), nbttaglist.getIntAt(1), nbttaglist.getIntAt(2));
         IBlockState iblockstate = template$basicpalette.stateFor(nbttagcompound.getInteger("state"));
         NBTTagCompound nbttagcompound1;
         if (nbttagcompound.hasKey("nbt")) {
            nbttagcompound1 = nbttagcompound.getCompoundTag("nbt");
         } else {
            nbttagcompound1 = null;
         }

         list.add(new Template.BlockInfo(blockpos, iblockstate, nbttagcompound1));
      }

      this.blocks.add(list);
   }

   private NBTTagList writeInts(int... p_186267_1_) {
      NBTTagList nbttaglist = new NBTTagList();

      for(int i : p_186267_1_) {
         nbttaglist.add(new NBTTagInt(i));
      }

      return nbttaglist;
   }

   private NBTTagList writeDoubles(double... p_186264_1_) {
      NBTTagList nbttaglist = new NBTTagList();

      for(double d0 : p_186264_1_) {
         nbttaglist.add(new NBTTagDouble(d0));
      }

      return nbttaglist;
   }

   static class BasicPalette implements Iterable<IBlockState> {
      public static final IBlockState DEFAULT_BLOCK_STATE = Blocks.AIR.getDefaultState();
      private final ObjectIntIdentityMap<IBlockState> ids = new ObjectIntIdentityMap<>(16);
      private int lastId;

      private BasicPalette() {
      }

      public int idFor(IBlockState p_189954_1_) {
         int i = this.ids.get(p_189954_1_);
         if (i == -1) {
            i = this.lastId++;
            this.ids.put(p_189954_1_, i);
         }

         return i;
      }

      @Nullable
      public IBlockState stateFor(int p_189955_1_) {
         IBlockState iblockstate = this.ids.getByValue(p_189955_1_);
         return iblockstate == null ? DEFAULT_BLOCK_STATE : iblockstate;
      }

      public Iterator<IBlockState> iterator() {
         return this.ids.iterator();
      }

      public void addMapping(IBlockState p_189956_1_, int p_189956_2_) {
         this.ids.put(p_189956_1_, p_189956_2_);
      }
   }

   public static class BlockInfo {
      public final BlockPos pos;
      public final IBlockState blockState;
      public final NBTTagCompound tileentityData;

      public BlockInfo(BlockPos p_i47042_1_, IBlockState p_i47042_2_, @Nullable NBTTagCompound p_i47042_3_) {
         this.pos = p_i47042_1_;
         this.blockState = p_i47042_2_;
         this.tileentityData = p_i47042_3_;
      }
   }

   public static class EntityInfo {
      public final Vec3d pos;
      public final BlockPos blockPos;
      public final NBTTagCompound entityData;

      public EntityInfo(Vec3d p_i47101_1_, BlockPos p_i47101_2_, NBTTagCompound p_i47101_3_) {
         this.pos = p_i47101_1_;
         this.blockPos = p_i47101_2_;
         this.entityData = p_i47101_3_;
      }
   }
}
