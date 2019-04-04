package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DSL.TypeReference;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NBTUtil {
   private static final Logger LOGGER = LogManager.getLogger();

   @Nullable
   public static GameProfile readGameProfileFromNBT(NBTTagCompound p_152459_0_) {
      String s = null;
      String s1 = null;
      if (p_152459_0_.hasKey("Name", 8)) {
         s = p_152459_0_.getString("Name");
      }

      if (p_152459_0_.hasKey("Id", 8)) {
         s1 = p_152459_0_.getString("Id");
      }

      try {
         UUID uuid;
         try {
            uuid = UUID.fromString(s1);
         } catch (Throwable var12) {
            uuid = null;
         }

         GameProfile gameprofile = new GameProfile(uuid, s);
         if (p_152459_0_.hasKey("Properties", 10)) {
            NBTTagCompound nbttagcompound = p_152459_0_.getCompoundTag("Properties");

            for(String s2 : nbttagcompound.getKeySet()) {
               NBTTagList nbttaglist = nbttagcompound.getTagList(s2, 10);

               for(int i = 0; i < nbttaglist.size(); ++i) {
                  NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                  String s3 = nbttagcompound1.getString("Value");
                  if (nbttagcompound1.hasKey("Signature", 8)) {
                     gameprofile.getProperties().put(s2, new Property(s2, s3, nbttagcompound1.getString("Signature")));
                  } else {
                     gameprofile.getProperties().put(s2, new Property(s2, s3));
                  }
               }
            }
         }

         return gameprofile;
      } catch (Throwable var13) {
         return null;
      }
   }

   public static NBTTagCompound writeGameProfile(NBTTagCompound p_180708_0_, GameProfile p_180708_1_) {
      if (!StringUtils.isNullOrEmpty(p_180708_1_.getName())) {
         p_180708_0_.setString("Name", p_180708_1_.getName());
      }

      if (p_180708_1_.getId() != null) {
         p_180708_0_.setString("Id", p_180708_1_.getId().toString());
      }

      if (!p_180708_1_.getProperties().isEmpty()) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();

         for(String s : p_180708_1_.getProperties().keySet()) {
            NBTTagList nbttaglist = new NBTTagList();

            for(Property property : p_180708_1_.getProperties().get(s)) {
               NBTTagCompound nbttagcompound1 = new NBTTagCompound();
               nbttagcompound1.setString("Value", property.getValue());
               if (property.hasSignature()) {
                  nbttagcompound1.setString("Signature", property.getSignature());
               }

               nbttaglist.add(nbttagcompound1);
            }

            nbttagcompound.setTag(s, nbttaglist);
         }

         p_180708_0_.setTag("Properties", nbttagcompound);
      }

      return p_180708_0_;
   }

   @VisibleForTesting
   public static boolean areNBTEquals(@Nullable INBTBase p_181123_0_, @Nullable INBTBase p_181123_1_, boolean p_181123_2_) {
      if (p_181123_0_ == p_181123_1_) {
         return true;
      } else if (p_181123_0_ == null) {
         return true;
      } else if (p_181123_1_ == null) {
         return false;
      } else if (!p_181123_0_.getClass().equals(p_181123_1_.getClass())) {
         return false;
      } else if (p_181123_0_ instanceof NBTTagCompound) {
         NBTTagCompound nbttagcompound = (NBTTagCompound)p_181123_0_;
         NBTTagCompound nbttagcompound1 = (NBTTagCompound)p_181123_1_;

         for(String s : nbttagcompound.getKeySet()) {
            INBTBase inbtbase1 = nbttagcompound.getTag(s);
            if (!areNBTEquals(inbtbase1, nbttagcompound1.getTag(s), p_181123_2_)) {
               return false;
            }
         }

         return true;
      } else if (p_181123_0_ instanceof NBTTagList && p_181123_2_) {
         NBTTagList nbttaglist = (NBTTagList)p_181123_0_;
         NBTTagList nbttaglist1 = (NBTTagList)p_181123_1_;
         if (nbttaglist.isEmpty()) {
            return nbttaglist1.isEmpty();
         } else {
            for(int i = 0; i < nbttaglist.size(); ++i) {
               INBTBase inbtbase = nbttaglist.get(i);
               boolean flag = false;

               for(int j = 0; j < nbttaglist1.size(); ++j) {
                  if (areNBTEquals(inbtbase, nbttaglist1.get(j), p_181123_2_)) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return p_181123_0_.equals(p_181123_1_);
      }
   }

   public static NBTTagCompound createUUIDTag(UUID p_186862_0_) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setLong("M", p_186862_0_.getMostSignificantBits());
      nbttagcompound.setLong("L", p_186862_0_.getLeastSignificantBits());
      return nbttagcompound;
   }

   public static UUID getUUIDFromTag(NBTTagCompound p_186860_0_) {
      return new UUID(p_186860_0_.getLong("M"), p_186860_0_.getLong("L"));
   }

   public static BlockPos getPosFromTag(NBTTagCompound p_186861_0_) {
      return new BlockPos(p_186861_0_.getInteger("X"), p_186861_0_.getInteger("Y"), p_186861_0_.getInteger("Z"));
   }

   public static NBTTagCompound createPosTag(BlockPos p_186859_0_) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setInteger("X", p_186859_0_.getX());
      nbttagcompound.setInteger("Y", p_186859_0_.getY());
      nbttagcompound.setInteger("Z", p_186859_0_.getZ());
      return nbttagcompound;
   }

   public static IBlockState readBlockState(NBTTagCompound p_190008_0_) {
      if (!p_190008_0_.hasKey("Name", 8)) {
         return Blocks.AIR.getDefaultState();
      } else {
         Block block = IRegistry.field_212618_g.func_82594_a(new ResourceLocation(p_190008_0_.getString("Name")));
         IBlockState iblockstate = block.getDefaultState();
         if (p_190008_0_.hasKey("Properties", 10)) {
            NBTTagCompound nbttagcompound = p_190008_0_.getCompoundTag("Properties");
            StateContainer<Block, IBlockState> statecontainer = block.getStateContainer();

            for(String s : nbttagcompound.getKeySet()) {
               IProperty<?> iproperty = statecontainer.getProperty(s);
               if (iproperty != null) {
                  iblockstate = setValueHelper(iblockstate, iproperty, s, nbttagcompound, p_190008_0_);
               }
            }
         }

         return iblockstate;
      }
   }

   private static <S extends IStateHolder<S>, T extends Comparable<T>> S setValueHelper(S p_193590_0_, IProperty<T> p_193590_1_, String p_193590_2_, NBTTagCompound p_193590_3_, NBTTagCompound p_193590_4_) {
      Optional<T> optional = p_193590_1_.parseValue(p_193590_3_.getString(p_193590_2_));
      if (optional.isPresent()) {
         return p_193590_0_.with(p_193590_1_, optional.get());
      } else {
         LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", p_193590_2_, p_193590_3_.getString(p_193590_2_), p_193590_4_.toString());
         return p_193590_0_;
      }
   }

   public static NBTTagCompound writeBlockState(IBlockState p_190009_0_) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setString("Name", IRegistry.field_212618_g.func_177774_c(p_190009_0_.getBlock()).toString());
      ImmutableMap<IProperty<?>, Comparable<?>> immutablemap = p_190009_0_.getValues();
      if (!immutablemap.isEmpty()) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();

         for(Entry<IProperty<?>, Comparable<?>> entry : immutablemap.entrySet()) {
            IProperty<?> iproperty = entry.getKey();
            nbttagcompound1.setString(iproperty.getName(), getName(iproperty, entry.getValue()));
         }

         nbttagcompound.setTag("Properties", nbttagcompound1);
      }

      return nbttagcompound;
   }

   private static <T extends Comparable<T>> String getName(IProperty<T> p_190010_0_, Comparable<?> p_190010_1_) {
      return p_190010_0_.getName((T)p_190010_1_);
   }

   public static NBTTagCompound func_210822_a(DataFixer p_210822_0_, TypeReference p_210822_1_, NBTTagCompound p_210822_2_, int p_210822_3_) {
      return update(p_210822_0_, p_210822_1_, p_210822_2_, p_210822_3_, 1631);
   }

   public static NBTTagCompound update(DataFixer p_210821_0_, TypeReference p_210821_1_, NBTTagCompound p_210821_2_, int p_210821_3_, int p_210821_4_) {
      return (NBTTagCompound)p_210821_0_.update(p_210821_1_, new Dynamic<>(NBTDynamicOps.INSTANCE, p_210821_2_), p_210821_3_, p_210821_4_).getValue();
   }
}
