package net.minecraft.network.datasync;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;

public class DataSerializers {
   private static final IntIdentityHashBiMap<DataSerializer<?>> REGISTRY = new IntIdentityHashBiMap<>(16);
   public static final DataSerializer<Byte> BYTE = new DataSerializer<Byte>() {
      public void write(PacketBuffer p_187160_1_, Byte p_187160_2_) {
         p_187160_1_.writeByte(p_187160_2_);
      }

      public Byte read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readByte();
      }

      public DataParameter<Byte> createKey(int p_187161_1_) {
         return new DataParameter<>(p_187161_1_, this);
      }

      public Byte copyValue(Byte p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final DataSerializer<Integer> VARINT = new DataSerializer<Integer>() {
      public void write(PacketBuffer p_187160_1_, Integer p_187160_2_) {
         p_187160_1_.writeVarInt(p_187160_2_);
      }

      public Integer read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readVarInt();
      }

      public DataParameter<Integer> createKey(int p_187161_1_) {
         return new DataParameter<>(p_187161_1_, this);
      }

      public Integer copyValue(Integer p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final DataSerializer<Float> FLOAT = new DataSerializer<Float>() {
      public void write(PacketBuffer p_187160_1_, Float p_187160_2_) {
         p_187160_1_.writeFloat(p_187160_2_);
      }

      public Float read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readFloat();
      }

      public DataParameter<Float> createKey(int p_187161_1_) {
         return new DataParameter<>(p_187161_1_, this);
      }

      public Float copyValue(Float p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final DataSerializer<String> STRING = new DataSerializer<String>() {
      public void write(PacketBuffer p_187160_1_, String p_187160_2_) {
         p_187160_1_.writeString(p_187160_2_);
      }

      public String read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readString(32767);
      }

      public DataParameter<String> createKey(int p_187161_1_) {
         return new DataParameter<>(p_187161_1_, this);
      }

      public String copyValue(String p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final DataSerializer<ITextComponent> TEXT_COMPONENT = new DataSerializer<ITextComponent>() {
      public void write(PacketBuffer p_187160_1_, ITextComponent p_187160_2_) {
         p_187160_1_.writeTextComponent(p_187160_2_);
      }

      public ITextComponent read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readTextComponent();
      }

      public DataParameter<ITextComponent> createKey(int p_187161_1_) {
         return new DataParameter<>(p_187161_1_, this);
      }

      public ITextComponent copyValue(ITextComponent p_192717_1_) {
         return p_192717_1_.func_212638_h();
      }
   };
   public static final DataSerializer<Optional<ITextComponent>> OPTIONAL_TEXT_COMPONENT = new DataSerializer<Optional<ITextComponent>>() {
      public void write(PacketBuffer p_187160_1_, Optional<ITextComponent> p_187160_2_) {
         if (p_187160_2_.isPresent()) {
            p_187160_1_.writeBoolean(true);
            p_187160_1_.writeTextComponent(p_187160_2_.get());
         } else {
            p_187160_1_.writeBoolean(false);
         }

      }

      public Optional<ITextComponent> read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readBoolean() ? Optional.of(p_187159_1_.readTextComponent()) : Optional.empty();
      }

      public DataParameter<Optional<ITextComponent>> createKey(int p_187161_1_) {
         return new DataParameter<>(p_187161_1_, this);
      }

      public Optional<ITextComponent> copyValue(Optional<ITextComponent> p_192717_1_) {
         return p_192717_1_.isPresent() ? Optional.of(p_192717_1_.get().func_212638_h()) : Optional.empty();
      }
   };
   public static final DataSerializer<ItemStack> ITEM_STACK = new DataSerializer<ItemStack>() {
      public void write(PacketBuffer p_187160_1_, ItemStack p_187160_2_) {
         p_187160_1_.writeItemStack(p_187160_2_);
      }

      public ItemStack read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readItemStack();
      }

      public DataParameter<ItemStack> createKey(int p_187161_1_) {
         return new DataParameter<>(p_187161_1_, this);
      }

      public ItemStack copyValue(ItemStack p_192717_1_) {
         return p_192717_1_.copy();
      }
   };
   public static final DataSerializer<Optional<IBlockState>> OPTIONAL_BLOCK_STATE = new DataSerializer<Optional<IBlockState>>() {
      public void write(PacketBuffer p_187160_1_, Optional<IBlockState> p_187160_2_) {
         if (p_187160_2_.isPresent()) {
            p_187160_1_.writeVarInt(Block.getStateId(p_187160_2_.get()));
         } else {
            p_187160_1_.writeVarInt(0);
         }

      }

      public Optional<IBlockState> read(PacketBuffer p_187159_1_) {
         int i = p_187159_1_.readVarInt();
         return i == 0 ? Optional.empty() : Optional.of(Block.getStateById(i));
      }

      public DataParameter<Optional<IBlockState>> createKey(int p_187161_1_) {
         return new DataParameter<>(p_187161_1_, this);
      }

      public Optional<IBlockState> copyValue(Optional<IBlockState> p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final DataSerializer<Boolean> BOOLEAN = new DataSerializer<Boolean>() {
      public void write(PacketBuffer p_187160_1_, Boolean p_187160_2_) {
         p_187160_1_.writeBoolean(p_187160_2_);
      }

      public Boolean read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readBoolean();
      }

      public DataParameter<Boolean> createKey(int p_187161_1_) {
         return new DataParameter<>(p_187161_1_, this);
      }

      public Boolean copyValue(Boolean p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final DataSerializer<IParticleData> PARTICLE_DATA = new DataSerializer<IParticleData>() {
      public void write(PacketBuffer p_187160_1_, IParticleData p_187160_2_) {
         p_187160_1_.writeVarInt(IRegistry.field_212632_u.func_148757_b(p_187160_2_.getType()));
         p_187160_2_.write(p_187160_1_);
      }

      public IParticleData read(PacketBuffer p_187159_1_) {
         return this.func_200543_a(p_187159_1_, IRegistry.field_212632_u.func_148754_a(p_187159_1_.readVarInt()));
      }

      private <T extends IParticleData> T func_200543_a(PacketBuffer p_200543_1_, ParticleType<T> p_200543_2_) {
         return p_200543_2_.getDeserializer().read(p_200543_2_, p_200543_1_);
      }

      public DataParameter<IParticleData> createKey(int p_187161_1_) {
         return new DataParameter<>(p_187161_1_, this);
      }

      public IParticleData copyValue(IParticleData p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final DataSerializer<Rotations> ROTATIONS = new DataSerializer<Rotations>() {
      public void write(PacketBuffer p_187160_1_, Rotations p_187160_2_) {
         p_187160_1_.writeFloat(p_187160_2_.getX());
         p_187160_1_.writeFloat(p_187160_2_.getY());
         p_187160_1_.writeFloat(p_187160_2_.getZ());
      }

      public Rotations read(PacketBuffer p_187159_1_) {
         return new Rotations(p_187159_1_.readFloat(), p_187159_1_.readFloat(), p_187159_1_.readFloat());
      }

      public DataParameter<Rotations> createKey(int p_187161_1_) {
         return new DataParameter<>(p_187161_1_, this);
      }

      public Rotations copyValue(Rotations p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final DataSerializer<BlockPos> BLOCK_POS = new DataSerializer<BlockPos>() {
      public void write(PacketBuffer p_187160_1_, BlockPos p_187160_2_) {
         p_187160_1_.writeBlockPos(p_187160_2_);
      }

      public BlockPos read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readBlockPos();
      }

      public DataParameter<BlockPos> createKey(int p_187161_1_) {
         return new DataParameter<>(p_187161_1_, this);
      }

      public BlockPos copyValue(BlockPos p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final DataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS = new DataSerializer<Optional<BlockPos>>() {
      public void write(PacketBuffer p_187160_1_, Optional<BlockPos> p_187160_2_) {
         p_187160_1_.writeBoolean(p_187160_2_.isPresent());
         if (p_187160_2_.isPresent()) {
            p_187160_1_.writeBlockPos(p_187160_2_.get());
         }

      }

      public Optional<BlockPos> read(PacketBuffer p_187159_1_) {
         return !p_187159_1_.readBoolean() ? Optional.empty() : Optional.of(p_187159_1_.readBlockPos());
      }

      public DataParameter<Optional<BlockPos>> createKey(int p_187161_1_) {
         return new DataParameter<>(p_187161_1_, this);
      }

      public Optional<BlockPos> copyValue(Optional<BlockPos> p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final DataSerializer<EnumFacing> FACING = new DataSerializer<EnumFacing>() {
      public void write(PacketBuffer p_187160_1_, EnumFacing p_187160_2_) {
         p_187160_1_.writeEnumValue(p_187160_2_);
      }

      public EnumFacing read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readEnumValue(EnumFacing.class);
      }

      public DataParameter<EnumFacing> createKey(int p_187161_1_) {
         return new DataParameter<>(p_187161_1_, this);
      }

      public EnumFacing copyValue(EnumFacing p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final DataSerializer<Optional<UUID>> OPTIONAL_UNIQUE_ID = new DataSerializer<Optional<UUID>>() {
      public void write(PacketBuffer p_187160_1_, Optional<UUID> p_187160_2_) {
         p_187160_1_.writeBoolean(p_187160_2_.isPresent());
         if (p_187160_2_.isPresent()) {
            p_187160_1_.writeUniqueId(p_187160_2_.get());
         }

      }

      public Optional<UUID> read(PacketBuffer p_187159_1_) {
         return !p_187159_1_.readBoolean() ? Optional.empty() : Optional.of(p_187159_1_.readUniqueId());
      }

      public DataParameter<Optional<UUID>> createKey(int p_187161_1_) {
         return new DataParameter<>(p_187161_1_, this);
      }

      public Optional<UUID> copyValue(Optional<UUID> p_192717_1_) {
         return p_192717_1_;
      }
   };
   public static final DataSerializer<NBTTagCompound> COMPOUND_TAG = new DataSerializer<NBTTagCompound>() {
      public void write(PacketBuffer p_187160_1_, NBTTagCompound p_187160_2_) {
         p_187160_1_.writeCompoundTag(p_187160_2_);
      }

      public NBTTagCompound read(PacketBuffer p_187159_1_) {
         return p_187159_1_.readCompoundTag();
      }

      public DataParameter<NBTTagCompound> createKey(int p_187161_1_) {
         return new DataParameter<>(p_187161_1_, this);
      }

      public NBTTagCompound copyValue(NBTTagCompound p_192717_1_) {
         return p_192717_1_.copy();
      }
   };

   public static void registerSerializer(DataSerializer<?> p_187189_0_) {
      REGISTRY.add(p_187189_0_);
   }

   @Nullable
   public static DataSerializer<?> getSerializer(int p_187190_0_) {
      return REGISTRY.get(p_187190_0_);
   }

   public static int getSerializerId(DataSerializer<?> p_187188_0_) {
      return REGISTRY.getId(p_187188_0_);
   }

   static {
      registerSerializer(BYTE);
      registerSerializer(VARINT);
      registerSerializer(FLOAT);
      registerSerializer(STRING);
      registerSerializer(TEXT_COMPONENT);
      registerSerializer(OPTIONAL_TEXT_COMPONENT);
      registerSerializer(ITEM_STACK);
      registerSerializer(BOOLEAN);
      registerSerializer(ROTATIONS);
      registerSerializer(BLOCK_POS);
      registerSerializer(OPTIONAL_BLOCK_POS);
      registerSerializer(FACING);
      registerSerializer(OPTIONAL_UNIQUE_ID);
      registerSerializer(OPTIONAL_BLOCK_STATE);
      registerSerializer(COMPOUND_TAG);
      registerSerializer(PARTICLE_DATA);
   }
}
