package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.BlockAbstractBanner;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityBanner extends TileEntity implements INameable {
   private ITextComponent name;
   private EnumDyeColor baseColor = EnumDyeColor.WHITE;
   private NBTTagList patterns;
   private boolean patternDataSet;
   private List<BannerPattern> patternList;
   private List<EnumDyeColor> colorList;
   private String patternResourceLocation;

   public TileEntityBanner() {
      super(TileEntityType.BANNER);
   }

   public TileEntityBanner(EnumDyeColor p_i47731_1_) {
      this();
      this.baseColor = p_i47731_1_;
   }

   public void loadFromItemStack(ItemStack p_195534_1_, EnumDyeColor p_195534_2_) {
      this.patterns = null;
      NBTTagCompound nbttagcompound = p_195534_1_.getChildTag("BlockEntityTag");
      if (nbttagcompound != null && nbttagcompound.hasKey("Patterns", 9)) {
         this.patterns = nbttagcompound.getTagList("Patterns", 10).copy();
      }

      this.baseColor = p_195534_2_;
      this.patternList = null;
      this.colorList = null;
      this.patternResourceLocation = "";
      this.patternDataSet = true;
      this.name = p_195534_1_.hasDisplayName() ? p_195534_1_.getDisplayName() : null;
   }

   public ITextComponent getName() {
      return this.name != null ? this.name : new TextComponentTranslation("block.minecraft.banner");
   }

   public boolean hasCustomName() {
      return this.name != null;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.name;
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
      super.writeToNBT(p_189515_1_);
      if (this.patterns != null) {
         p_189515_1_.setTag("Patterns", this.patterns);
      }

      if (this.name != null) {
         p_189515_1_.setString("CustomName", ITextComponent.Serializer.toJson(this.name));
      }

      return p_189515_1_;
   }

   public void readFromNBT(NBTTagCompound p_145839_1_) {
      super.readFromNBT(p_145839_1_);
      if (p_145839_1_.hasKey("CustomName", 8)) {
         this.name = ITextComponent.Serializer.fromJson(p_145839_1_.getString("CustomName"));
      }

      if (this.hasWorld()) {
         this.baseColor = ((BlockAbstractBanner)this.getBlockState().getBlock()).getColor();
      } else {
         this.baseColor = null;
      }

      this.patterns = p_145839_1_.getTagList("Patterns", 10);
      this.patternList = null;
      this.colorList = null;
      this.patternResourceLocation = null;
      this.patternDataSet = true;
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 6, this.getUpdateTag());
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public static int getPatterns(ItemStack p_175113_0_) {
      NBTTagCompound nbttagcompound = p_175113_0_.getChildTag("BlockEntityTag");
      return nbttagcompound != null && nbttagcompound.hasKey("Patterns") ? nbttagcompound.getTagList("Patterns", 10).size() : 0;
   }

   @OnlyIn(Dist.CLIENT)
   public List<BannerPattern> getPatternList() {
      this.initializeBannerData();
      return this.patternList;
   }

   @OnlyIn(Dist.CLIENT)
   public List<EnumDyeColor> getColorList() {
      this.initializeBannerData();
      return this.colorList;
   }

   @OnlyIn(Dist.CLIENT)
   public String getPatternResourceLocation() {
      this.initializeBannerData();
      return this.patternResourceLocation;
   }

   @OnlyIn(Dist.CLIENT)
   private void initializeBannerData() {
      if (this.patternList == null || this.colorList == null || this.patternResourceLocation == null) {
         if (!this.patternDataSet) {
            this.patternResourceLocation = "";
         } else {
            this.patternList = Lists.newArrayList();
            this.colorList = Lists.newArrayList();
            EnumDyeColor enumdyecolor = this.getBaseColor(this::getBlockState);
            if (enumdyecolor == null) {
               this.patternResourceLocation = "banner_missing";
            } else {
               this.patternList.add(BannerPattern.BASE);
               this.colorList.add(enumdyecolor);
               this.patternResourceLocation = "b" + enumdyecolor.getId();
               if (this.patterns != null) {
                  for(int i = 0; i < this.patterns.size(); ++i) {
                     NBTTagCompound nbttagcompound = this.patterns.getCompoundTagAt(i);
                     BannerPattern bannerpattern = BannerPattern.byHash(nbttagcompound.getString("Pattern"));
                     if (bannerpattern != null) {
                        this.patternList.add(bannerpattern);
                        int j = nbttagcompound.getInteger("Color");
                        this.colorList.add(EnumDyeColor.byId(j));
                        this.patternResourceLocation = this.patternResourceLocation + bannerpattern.getHashname() + j;
                     }
                  }
               }
            }

         }
      }
   }

   public static void removeBannerData(ItemStack p_175117_0_) {
      NBTTagCompound nbttagcompound = p_175117_0_.getChildTag("BlockEntityTag");
      if (nbttagcompound != null && nbttagcompound.hasKey("Patterns", 9)) {
         NBTTagList nbttaglist = nbttagcompound.getTagList("Patterns", 10);
         if (!nbttaglist.isEmpty()) {
            nbttaglist.remove(nbttaglist.size() - 1);
            if (nbttaglist.isEmpty()) {
               p_175117_0_.removeChildTag("BlockEntityTag");
            }

         }
      }
   }

   public ItemStack getItem(IBlockState p_190615_1_) {
      ItemStack itemstack = new ItemStack(BlockBanner.forColor(this.getBaseColor(() -> {
         return p_190615_1_;
      })));
      if (this.patterns != null && !this.patterns.isEmpty()) {
         itemstack.getOrCreateChildTag("BlockEntityTag").setTag("Patterns", this.patterns.copy());
      }

      if (this.name != null) {
         itemstack.setDisplayName(this.name);
      }

      return itemstack;
   }

   public EnumDyeColor getBaseColor(Supplier<IBlockState> p_195533_1_) {
      if (this.baseColor == null) {
         this.baseColor = ((BlockAbstractBanner)p_195533_1_.get().getBlock()).getColor();
      }

      return this.baseColor;
   }
}
