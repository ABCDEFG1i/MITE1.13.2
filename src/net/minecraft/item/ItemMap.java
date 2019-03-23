package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemMap extends ItemMapBase {
   public ItemMap(Item.Properties p_i48482_1_) {
      super(p_i48482_1_);
   }

   public static ItemStack setupNewMap(World p_195952_0_, int p_195952_1_, int p_195952_2_, byte p_195952_3_, boolean p_195952_4_, boolean p_195952_5_) {
      ItemStack itemstack = new ItemStack(Items.FILLED_MAP);
      func_195951_a(itemstack, p_195952_0_, p_195952_1_, p_195952_2_, p_195952_3_, p_195952_4_, p_195952_5_, p_195952_0_.dimension.getType());
      return itemstack;
   }

   @Nullable
   public static MapData getMapData(ItemStack p_195950_0_, World p_195950_1_) {
      MapData mapdata = loadMapData(p_195950_1_, "map_" + getMapId(p_195950_0_));
      if (mapdata == null && !p_195950_1_.isRemote) {
         mapdata = func_195951_a(p_195950_0_, p_195950_1_, p_195950_1_.getWorldInfo().getSpawnX(), p_195950_1_.getWorldInfo().getSpawnZ(), 3, false, false, p_195950_1_.dimension.getType());
      }

      return mapdata;
   }

   public static int getMapId(ItemStack p_195949_0_) {
      NBTTagCompound nbttagcompound = p_195949_0_.getTag();
      return nbttagcompound != null && nbttagcompound.hasKey("map", 99) ? nbttagcompound.getInteger("map") : 0;
   }

   private static MapData func_195951_a(ItemStack p_195951_0_, World p_195951_1_, int p_195951_2_, int p_195951_3_, int p_195951_4_, boolean p_195951_5_, boolean p_195951_6_, DimensionType p_195951_7_) {
      int i = p_195951_1_.func_212410_a(DimensionType.OVERWORLD, "map");
      MapData mapdata = new MapData("map_" + i);
      mapdata.func_212440_a(p_195951_2_, p_195951_3_, p_195951_4_, p_195951_5_, p_195951_6_, p_195951_7_);
      p_195951_1_.func_212409_a(DimensionType.OVERWORLD, mapdata.getName(), mapdata);
      p_195951_0_.getOrCreateTag().setInteger("map", i);
      return mapdata;
   }

   @Nullable
   public static MapData loadMapData(IWorld p_195953_0_, String p_195953_1_) {
      return p_195953_0_.func_212411_a(DimensionType.OVERWORLD, MapData::new, p_195953_1_);
   }

   public void updateMapData(World p_77872_1_, Entity p_77872_2_, MapData p_77872_3_) {
      if (p_77872_1_.dimension.getType() == p_77872_3_.dimension && p_77872_2_ instanceof EntityPlayer) {
         int i = 1 << p_77872_3_.scale;
         int j = p_77872_3_.xCenter;
         int k = p_77872_3_.zCenter;
         int l = MathHelper.floor(p_77872_2_.posX - (double)j) / i + 64;
         int i1 = MathHelper.floor(p_77872_2_.posZ - (double)k) / i + 64;
         int j1 = 128 / i;
         if (p_77872_1_.dimension.isNether()) {
            j1 /= 2;
         }

         MapData.MapInfo mapdata$mapinfo = p_77872_3_.getMapInfo((EntityPlayer)p_77872_2_);
         ++mapdata$mapinfo.step;
         boolean flag = false;

         for(int k1 = l - j1 + 1; k1 < l + j1; ++k1) {
            if ((k1 & 15) == (mapdata$mapinfo.step & 15) || flag) {
               flag = false;
               double d0 = 0.0D;

               for(int l1 = i1 - j1 - 1; l1 < i1 + j1; ++l1) {
                  if (k1 >= 0 && l1 >= -1 && k1 < 128 && l1 < 128) {
                     int i2 = k1 - l;
                     int j2 = l1 - i1;
                     boolean flag1 = i2 * i2 + j2 * j2 > (j1 - 2) * (j1 - 2);
                     int k2 = (j / i + k1 - 64) * i;
                     int l2 = (k / i + l1 - 64) * i;
                     Multiset<MaterialColor> multiset = LinkedHashMultiset.create();
                     Chunk chunk = p_77872_1_.getChunk(new BlockPos(k2, 0, l2));
                     if (!chunk.isEmpty()) {
                        int i3 = k2 & 15;
                        int j3 = l2 & 15;
                        int k3 = 0;
                        double d1 = 0.0D;
                        if (p_77872_1_.dimension.isNether()) {
                           int l3 = k2 + l2 * 231871;
                           l3 = l3 * l3 * 31287121 + l3 * 11;
                           if ((l3 >> 20 & 1) == 0) {
                              multiset.add(Blocks.DIRT.getDefaultState().func_185909_g(p_77872_1_, BlockPos.ORIGIN), 10);
                           } else {
                              multiset.add(Blocks.STONE.getDefaultState().func_185909_g(p_77872_1_, BlockPos.ORIGIN), 100);
                           }

                           d1 = 100.0D;
                        } else {
                           BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                           for(int i4 = 0; i4 < i; ++i4) {
                              for(int j4 = 0; j4 < i; ++j4) {
                                 int k4 = chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE, i4 + i3, j4 + j3) + 1;
                                 IBlockState iblockstate;
                                 if (k4 <= 1) {
                                    iblockstate = Blocks.BEDROCK.getDefaultState();
                                 } else {
                                    while(true) {
                                       --k4;
                                       iblockstate = chunk.getBlockState(i4 + i3, k4, j4 + j3);
                                       blockpos$mutableblockpos.setPos((chunk.x << 4) + i4 + i3, k4, (chunk.z << 4) + j4 + j3);
                                       if (iblockstate.func_185909_g(p_77872_1_, blockpos$mutableblockpos) != MaterialColor.AIR || k4 <= 0) {
                                          break;
                                       }
                                    }

                                    if (k4 > 0 && !iblockstate.getFluidState().isEmpty()) {
                                       int l4 = k4 - 1;

                                       while(true) {
                                          IBlockState iblockstate1 = chunk.getBlockState(i4 + i3, l4--, j4 + j3);
                                          ++k3;
                                          if (l4 <= 0 || iblockstate1.getFluidState().isEmpty()) {
                                             break;
                                          }
                                       }

                                       iblockstate = this.func_211698_a(p_77872_1_, iblockstate, blockpos$mutableblockpos);
                                    }
                                 }

                                 p_77872_3_.removeStaleBanners(p_77872_1_, (chunk.x << 4) + i4 + i3, (chunk.z << 4) + j4 + j3);
                                 d1 += (double)k4 / (double)(i * i);
                                 multiset.add(iblockstate.func_185909_g(p_77872_1_, blockpos$mutableblockpos));
                              }
                           }
                        }

                        k3 = k3 / (i * i);
                        double d2 = (d1 - d0) * 4.0D / (double)(i + 4) + ((double)(k1 + l1 & 1) - 0.5D) * 0.4D;
                        int i5 = 1;
                        if (d2 > 0.6D) {
                           i5 = 2;
                        }

                        if (d2 < -0.6D) {
                           i5 = 0;
                        }

                        MaterialColor materialcolor = Iterables.getFirst(Multisets.copyHighestCountFirst(multiset), MaterialColor.AIR);
                        if (materialcolor == MaterialColor.WATER) {
                           d2 = (double)k3 * 0.1D + (double)(k1 + l1 & 1) * 0.2D;
                           i5 = 1;
                           if (d2 < 0.5D) {
                              i5 = 2;
                           }

                           if (d2 > 0.9D) {
                              i5 = 0;
                           }
                        }

                        d0 = d1;
                        if (l1 >= 0 && i2 * i2 + j2 * j2 < j1 * j1 && (!flag1 || (k1 + l1 & 1) != 0)) {
                           byte b0 = p_77872_3_.colors[k1 + l1 * 128];
                           byte b1 = (byte)(materialcolor.colorIndex * 4 + i5);
                           if (b0 != b1) {
                              p_77872_3_.colors[k1 + l1 * 128] = b1;
                              p_77872_3_.updateMapData(k1, l1);
                              flag = true;
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }

   private IBlockState func_211698_a(World p_211698_1_, IBlockState p_211698_2_, BlockPos p_211698_3_) {
      IFluidState ifluidstate = p_211698_2_.getFluidState();
      return !ifluidstate.isEmpty() && !Block.doesSideFillSquare(p_211698_2_.getCollisionShape(p_211698_1_, p_211698_3_), EnumFacing.UP) ? ifluidstate.getBlockState() : p_211698_2_;
   }

   private static boolean func_195954_a(Biome[] p_195954_0_, int p_195954_1_, int p_195954_2_, int p_195954_3_) {
      return p_195954_0_[p_195954_2_ * p_195954_1_ + p_195954_3_ * p_195954_1_ * 128 * p_195954_1_].getBaseHeight() >= 0.0F;
   }

   public static void renderBiomePreviewMap(World p_190905_0_, ItemStack p_190905_1_) {
      MapData mapdata = getMapData(p_190905_1_, p_190905_0_);
      if (mapdata != null) {
         if (p_190905_0_.dimension.getType() == mapdata.dimension) {
            int i = 1 << mapdata.scale;
            int j = mapdata.xCenter;
            int k = mapdata.zCenter;
            Biome[] abiome = p_190905_0_.getChunkProvider().getChunkGenerator().getBiomeProvider().getBiomes((j / i - 64) * i, (k / i - 64) * i, 128 * i, 128 * i, false);

            for(int l = 0; l < 128; ++l) {
               for(int i1 = 0; i1 < 128; ++i1) {
                  if (l > 0 && i1 > 0 && l < 127 && i1 < 127) {
                     Biome biome = abiome[l * i + i1 * i * 128 * i];
                     int j1 = 8;
                     if (func_195954_a(abiome, i, l - 1, i1 - 1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l - 1, i1 + 1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l - 1, i1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l + 1, i1 - 1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l + 1, i1 + 1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l + 1, i1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l, i1 - 1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l, i1 + 1)) {
                        --j1;
                     }

                     int k1 = 3;
                     MaterialColor materialcolor = MaterialColor.AIR;
                     if (biome.getBaseHeight() < 0.0F) {
                        materialcolor = MaterialColor.ADOBE;
                        if (j1 > 7 && i1 % 2 == 0) {
                           k1 = (l + (int)(MathHelper.sin((float)i1 + 0.0F) * 7.0F)) / 8 % 5;
                           if (k1 == 3) {
                              k1 = 1;
                           } else if (k1 == 4) {
                              k1 = 0;
                           }
                        } else if (j1 > 7) {
                           materialcolor = MaterialColor.AIR;
                        } else if (j1 > 5) {
                           k1 = 1;
                        } else if (j1 > 3) {
                           k1 = 0;
                        } else if (j1 > 1) {
                           k1 = 0;
                        }
                     } else if (j1 > 0) {
                        materialcolor = MaterialColor.BROWN;
                        if (j1 > 3) {
                           k1 = 1;
                        } else {
                           k1 = 3;
                        }
                     }

                     if (materialcolor != MaterialColor.AIR) {
                        mapdata.colors[l + i1 * 128] = (byte)(materialcolor.colorIndex * 4 + k1);
                        mapdata.updateMapData(l, i1);
                     }
                  }
               }
            }

         }
      }
   }

   public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
      if (!p_77663_2_.isRemote) {
         MapData mapdata = getMapData(p_77663_1_, p_77663_2_);
         if (p_77663_3_ instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)p_77663_3_;
            mapdata.updateVisiblePlayers(entityplayer, p_77663_1_);
         }

         if (p_77663_5_ || p_77663_3_ instanceof EntityPlayer && ((EntityPlayer)p_77663_3_).getHeldItemOffhand() == p_77663_1_) {
            this.updateMapData(p_77663_2_, p_77663_3_, mapdata);
         }

      }
   }

   @Nullable
   public Packet<?> getUpdatePacket(ItemStack p_150911_1_, World p_150911_2_, EntityPlayer p_150911_3_) {
      return getMapData(p_150911_1_, p_150911_2_).getMapPacket(p_150911_1_, p_150911_2_, p_150911_3_);
   }

   public void onCreated(ItemStack p_77622_1_, World p_77622_2_, EntityPlayer p_77622_3_) {
      NBTTagCompound nbttagcompound = p_77622_1_.getTag();
      if (nbttagcompound != null && nbttagcompound.hasKey("map_scale_direction", 99)) {
         scaleMap(p_77622_1_, p_77622_2_, nbttagcompound.getInteger("map_scale_direction"));
         nbttagcompound.removeTag("map_scale_direction");
      }

   }

   protected static void scaleMap(ItemStack p_185063_0_, World p_185063_1_, int p_185063_2_) {
      MapData mapdata = getMapData(p_185063_0_, p_185063_1_);
      if (mapdata != null) {
         func_195951_a(p_185063_0_, p_185063_1_, mapdata.xCenter, mapdata.zCenter, MathHelper.clamp(mapdata.scale + p_185063_2_, 0, 4), mapdata.trackingPosition, mapdata.unlimitedTracking, mapdata.dimension);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      if (p_77624_4_.isAdvanced()) {
         MapData mapdata = p_77624_2_ == null ? null : getMapData(p_77624_1_, p_77624_2_);
         if (mapdata != null) {
            p_77624_3_.add((new TextComponentTranslation("filled_map.id", getMapId(p_77624_1_))).applyTextStyle(TextFormatting.GRAY));
            p_77624_3_.add((new TextComponentTranslation("filled_map.scale", 1 << mapdata.scale)).applyTextStyle(TextFormatting.GRAY));
            p_77624_3_.add((new TextComponentTranslation("filled_map.level", mapdata.scale, 4)).applyTextStyle(TextFormatting.GRAY));
         } else {
            p_77624_3_.add((new TextComponentTranslation("filled_map.unknown")).applyTextStyle(TextFormatting.GRAY));
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static int getColor(ItemStack p_190907_0_) {
      NBTTagCompound nbttagcompound = p_190907_0_.getChildTag("display");
      if (nbttagcompound != null && nbttagcompound.hasKey("MapColor", 99)) {
         int i = nbttagcompound.getInteger("MapColor");
         return -16777216 | i & 16777215;
      } else {
         return -12173266;
      }
   }

   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      IBlockState iblockstate = p_195939_1_.getWorld().getBlockState(p_195939_1_.getPos());
      if (iblockstate.isIn(BlockTags.BANNERS)) {
         if (!p_195939_1_.world.isRemote) {
            MapData mapdata = getMapData(p_195939_1_.getItem(), p_195939_1_.getWorld());
            mapdata.tryAddBanner(p_195939_1_.getWorld(), p_195939_1_.getPos());
         }

         return EnumActionResult.SUCCESS;
      } else {
         return super.onItemUse(p_195939_1_);
      }
   }
}
