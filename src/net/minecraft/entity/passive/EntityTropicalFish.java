package net.minecraft.entity.passive;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityTropicalFish extends AbstractGroupFish {
   private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityTropicalFish.class, DataSerializers.VARINT);
   private static final ResourceLocation[] field_204224_c = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_a.png"), new ResourceLocation("textures/entity/fish/tropical_b.png")};
   private static final ResourceLocation[] field_204225_bx = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_a_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_6.png")};
   private static final ResourceLocation[] field_204226_by = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_b_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_6.png")};
   public static final int[] SPECIAL_VARIANTS = new int[]{func_204214_a(EntityTropicalFish.Type.STRIPEY, EnumDyeColor.ORANGE, EnumDyeColor.GRAY), func_204214_a(EntityTropicalFish.Type.FLOPPER, EnumDyeColor.GRAY, EnumDyeColor.GRAY), func_204214_a(EntityTropicalFish.Type.FLOPPER, EnumDyeColor.GRAY, EnumDyeColor.BLUE), func_204214_a(EntityTropicalFish.Type.CLAYFISH, EnumDyeColor.WHITE, EnumDyeColor.GRAY), func_204214_a(EntityTropicalFish.Type.SUNSTREAK, EnumDyeColor.BLUE, EnumDyeColor.GRAY), func_204214_a(EntityTropicalFish.Type.KOB, EnumDyeColor.ORANGE, EnumDyeColor.WHITE), func_204214_a(EntityTropicalFish.Type.SPOTTY, EnumDyeColor.PINK, EnumDyeColor.LIGHT_BLUE), func_204214_a(EntityTropicalFish.Type.BLOCKFISH, EnumDyeColor.PURPLE, EnumDyeColor.YELLOW), func_204214_a(EntityTropicalFish.Type.CLAYFISH, EnumDyeColor.WHITE, EnumDyeColor.RED), func_204214_a(EntityTropicalFish.Type.SPOTTY, EnumDyeColor.WHITE, EnumDyeColor.YELLOW), func_204214_a(EntityTropicalFish.Type.GLITTER, EnumDyeColor.WHITE, EnumDyeColor.GRAY), func_204214_a(EntityTropicalFish.Type.CLAYFISH, EnumDyeColor.WHITE, EnumDyeColor.ORANGE), func_204214_a(EntityTropicalFish.Type.DASHER, EnumDyeColor.CYAN, EnumDyeColor.PINK), func_204214_a(EntityTropicalFish.Type.BRINELY, EnumDyeColor.LIME, EnumDyeColor.LIGHT_BLUE), func_204214_a(EntityTropicalFish.Type.BETTY, EnumDyeColor.RED, EnumDyeColor.WHITE), func_204214_a(EntityTropicalFish.Type.SNOOPER, EnumDyeColor.GRAY, EnumDyeColor.RED), func_204214_a(EntityTropicalFish.Type.BLOCKFISH, EnumDyeColor.RED, EnumDyeColor.WHITE), func_204214_a(EntityTropicalFish.Type.FLOPPER, EnumDyeColor.WHITE, EnumDyeColor.YELLOW), func_204214_a(EntityTropicalFish.Type.KOB, EnumDyeColor.RED, EnumDyeColor.WHITE), func_204214_a(EntityTropicalFish.Type.SUNSTREAK, EnumDyeColor.GRAY, EnumDyeColor.WHITE), func_204214_a(EntityTropicalFish.Type.DASHER, EnumDyeColor.CYAN, EnumDyeColor.YELLOW), func_204214_a(EntityTropicalFish.Type.FLOPPER, EnumDyeColor.YELLOW, EnumDyeColor.YELLOW)};
   private boolean field_204228_bA = true;

   private static int func_204214_a(EntityTropicalFish.Type p_204214_0_, EnumDyeColor p_204214_1_, EnumDyeColor p_204214_2_) {
      return p_204214_0_.func_212550_a() & 255 | (p_204214_0_.func_212551_b() & 255) << 8 | (p_204214_1_.getId() & 255) << 16 | (p_204214_2_.getId() & 255) << 24;
   }

   public EntityTropicalFish(World p_i48879_1_) {
      super(EntityType.TROPICAL_FISH, p_i48879_1_);
      this.setSize(0.5F, 0.4F);
   }

   @OnlyIn(Dist.CLIENT)
   public static String func_212324_b(int p_212324_0_) {
      return "entity.minecraft.tropical_fish.predefined." + p_212324_0_;
   }

   @OnlyIn(Dist.CLIENT)
   public static EnumDyeColor func_212326_d(int p_212326_0_) {
      return EnumDyeColor.byId(func_204216_dH(p_212326_0_));
   }

   @OnlyIn(Dist.CLIENT)
   public static EnumDyeColor func_212323_p(int p_212323_0_) {
      return EnumDyeColor.byId(func_204212_dI(p_212323_0_));
   }

   @OnlyIn(Dist.CLIENT)
   public static String func_212327_q(int p_212327_0_) {
      int i = func_212325_s(p_212327_0_);
      int j = func_204213_dJ(p_212327_0_);
      return "entity.minecraft.tropical_fish.type." + EntityTropicalFish.Type.func_212548_a(i, j);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(VARIANT, 0);
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("Variant", this.getVariant());
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.setVariant(p_70037_1_.getInteger("Variant"));
   }

   public void setVariant(int p_204215_1_) {
      this.dataManager.set(VARIANT, p_204215_1_);
   }

   public boolean func_204209_c(int p_204209_1_) {
      return !this.field_204228_bA;
   }

   public int getVariant() {
      return this.dataManager.get(VARIANT);
   }

   protected void setBucketData(ItemStack p_204211_1_) {
      super.setBucketData(p_204211_1_);
      NBTTagCompound nbttagcompound = p_204211_1_.getOrCreateTag();
      nbttagcompound.setInteger("BucketVariantTag", this.getVariant());
   }

   protected ItemStack getFishBucket() {
      return new ItemStack(Items.TROPICAL_FISH_BUCKET);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_TROPICAL_FISH;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_TROPICAL_FISH_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_TROPICAL_FISH_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_TROPICAL_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_TROPICAL_FISH_FLOP;
   }

   @OnlyIn(Dist.CLIENT)
   private static int func_204216_dH(int p_204216_0_) {
      return (p_204216_0_ & 16711680) >> 16;
   }

   @OnlyIn(Dist.CLIENT)
   public float[] func_204219_dC() {
      return EnumDyeColor.byId(func_204216_dH(this.getVariant())).getColorComponentValues();
   }

   @OnlyIn(Dist.CLIENT)
   private static int func_204212_dI(int p_204212_0_) {
      return (p_204212_0_ & -16777216) >> 24;
   }

   @OnlyIn(Dist.CLIENT)
   public float[] func_204222_dD() {
      return EnumDyeColor.byId(func_204212_dI(this.getVariant())).getColorComponentValues();
   }

   @OnlyIn(Dist.CLIENT)
   public static int func_212325_s(int p_212325_0_) {
      return Math.min(p_212325_0_ & 255, 1);
   }

   @OnlyIn(Dist.CLIENT)
   public int getSize() {
      return func_212325_s(this.getVariant());
   }

   @OnlyIn(Dist.CLIENT)
   private static int func_204213_dJ(int p_204213_0_) {
      return Math.min((p_204213_0_ & '\uff00') >> 8, 5);
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getPatternTexture() {
      return func_212325_s(this.getVariant()) == 0 ? field_204225_bx[func_204213_dJ(this.getVariant())] : field_204226_by[func_204213_dJ(this.getVariant())];
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getBodyTexture() {
      return field_204224_c[func_212325_s(this.getVariant())];
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      p_204210_2_ = super.onInitialSpawn(p_204210_1_, p_204210_2_, p_204210_3_);
      if (p_204210_3_ != null && p_204210_3_.hasKey("BucketVariantTag", 3)) {
         this.setVariant(p_204210_3_.getInteger("BucketVariantTag"));
         return p_204210_2_;
      } else {
         int i;
         int j;
         int k;
         int l;
         if (p_204210_2_ instanceof EntityTropicalFish.GroupData) {
            EntityTropicalFish.GroupData entitytropicalfish$groupdata = (EntityTropicalFish.GroupData)p_204210_2_;
            i = entitytropicalfish$groupdata.size;
            j = entitytropicalfish$groupdata.pattern;
            k = entitytropicalfish$groupdata.bodyColor;
            l = entitytropicalfish$groupdata.patternColor;
         } else if ((double)this.rand.nextFloat() < 0.9D) {
            int i1 = SPECIAL_VARIANTS[this.rand.nextInt(SPECIAL_VARIANTS.length)];
            i = i1 & 255;
            j = (i1 & '\uff00') >> 8;
            k = (i1 & 16711680) >> 16;
            l = (i1 & -16777216) >> 24;
            p_204210_2_ = new EntityTropicalFish.GroupData(this, i, j, k, l);
         } else {
            this.field_204228_bA = false;
            i = this.rand.nextInt(2);
            j = this.rand.nextInt(6);
            k = this.rand.nextInt(15);
            l = this.rand.nextInt(15);
         }

         this.setVariant(i | j << 8 | k << 16 | l << 24);
         return p_204210_2_;
      }
   }

   static class GroupData extends AbstractGroupFish.GroupData {
      private final int size;
      private final int pattern;
      private final int bodyColor;
      private final int patternColor;

      private GroupData(EntityTropicalFish p_i49859_1_, int p_i49859_2_, int p_i49859_3_, int p_i49859_4_, int p_i49859_5_) {
         super(p_i49859_1_);
         this.size = p_i49859_2_;
         this.pattern = p_i49859_3_;
         this.bodyColor = p_i49859_4_;
         this.patternColor = p_i49859_5_;
      }
   }

   enum Type {
      KOB(0, 0),
      SUNSTREAK(0, 1),
      SNOOPER(0, 2),
      DASHER(0, 3),
      BRINELY(0, 4),
      SPOTTY(0, 5),
      FLOPPER(1, 0),
      STRIPEY(1, 1),
      GLITTER(1, 2),
      BLOCKFISH(1, 3),
      BETTY(1, 4),
      CLAYFISH(1, 5);

      private final int field_212552_m;
      private final int field_212553_n;
      private static final EntityTropicalFish.Type[] field_212554_o = values();

      Type(int p_i49832_3_, int p_i49832_4_) {
         this.field_212552_m = p_i49832_3_;
         this.field_212553_n = p_i49832_4_;
      }

      public int func_212550_a() {
         return this.field_212552_m;
      }

      public int func_212551_b() {
         return this.field_212553_n;
      }

      @OnlyIn(Dist.CLIENT)
      public static String func_212548_a(int p_212548_0_, int p_212548_1_) {
         return field_212554_o[p_212548_1_ + 6 * p_212548_0_].func_212549_c();
      }

      @OnlyIn(Dist.CLIENT)
      public String func_212549_c() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
