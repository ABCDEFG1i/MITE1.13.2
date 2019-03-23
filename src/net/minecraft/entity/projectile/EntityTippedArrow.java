package net.minecraft.entity.projectile;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityTippedArrow extends EntityArrow {
   private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityTippedArrow.class, DataSerializers.VARINT);
   private PotionType potion = PotionTypes.EMPTY;
   private final Set<PotionEffect> customPotionEffects = Sets.newHashSet();
   private boolean fixedColor;

   public EntityTippedArrow(World p_i46756_1_) {
      super(EntityType.ARROW, p_i46756_1_);
   }

   public EntityTippedArrow(World p_i46757_1_, double p_i46757_2_, double p_i46757_4_, double p_i46757_6_) {
      super(EntityType.ARROW, p_i46757_2_, p_i46757_4_, p_i46757_6_, p_i46757_1_);
   }

   public EntityTippedArrow(World p_i46758_1_, EntityLivingBase p_i46758_2_) {
      super(EntityType.ARROW, p_i46758_2_, p_i46758_1_);
   }

   public void setPotionEffect(ItemStack p_184555_1_) {
      if (p_184555_1_.getItem() == Items.TIPPED_ARROW) {
         this.potion = PotionUtils.getPotionFromItem(p_184555_1_);
         Collection<PotionEffect> collection = PotionUtils.getFullEffectsFromItem(p_184555_1_);
         if (!collection.isEmpty()) {
            for(PotionEffect potioneffect : collection) {
               this.customPotionEffects.add(new PotionEffect(potioneffect));
            }
         }

         int i = getCustomColor(p_184555_1_);
         if (i == -1) {
            this.refreshColor();
         } else {
            this.setFixedColor(i);
         }
      } else if (p_184555_1_.getItem() == Items.ARROW) {
         this.potion = PotionTypes.EMPTY;
         this.customPotionEffects.clear();
         this.dataManager.set(COLOR, -1);
      }

   }

   public static int getCustomColor(ItemStack p_191508_0_) {
      NBTTagCompound nbttagcompound = p_191508_0_.getTag();
      return nbttagcompound != null && nbttagcompound.hasKey("CustomPotionColor", 99) ? nbttagcompound.getInteger("CustomPotionColor") : -1;
   }

   private void refreshColor() {
      this.fixedColor = false;
      this.dataManager.set(COLOR, PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(this.potion, this.customPotionEffects)));
   }

   public void addEffect(PotionEffect p_184558_1_) {
      this.customPotionEffects.add(p_184558_1_);
      this.getDataManager().set(COLOR, PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(this.potion, this.customPotionEffects)));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(COLOR, -1);
   }

   public void tick() {
      super.tick();
      if (this.world.isRemote) {
         if (this.inGround) {
            if (this.timeInGround % 5 == 0) {
               this.spawnPotionParticles(1);
            }
         } else {
            this.spawnPotionParticles(2);
         }
      } else if (this.inGround && this.timeInGround != 0 && !this.customPotionEffects.isEmpty() && this.timeInGround >= 600) {
         this.world.setEntityState(this, (byte)0);
         this.potion = PotionTypes.EMPTY;
         this.customPotionEffects.clear();
         this.dataManager.set(COLOR, -1);
      }

   }

   private void spawnPotionParticles(int p_184556_1_) {
      int i = this.getColor();
      if (i != -1 && p_184556_1_ > 0) {
         double d0 = (double)(i >> 16 & 255) / 255.0D;
         double d1 = (double)(i >> 8 & 255) / 255.0D;
         double d2 = (double)(i >> 0 & 255) / 255.0D;

         for(int j = 0; j < p_184556_1_; ++j) {
            this.world.spawnParticle(Particles.ENTITY_EFFECT, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, d0, d1, d2);
         }

      }
   }

   public int getColor() {
      return this.dataManager.get(COLOR);
   }

   private void setFixedColor(int p_191507_1_) {
      this.fixedColor = true;
      this.dataManager.set(COLOR, p_191507_1_);
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      if (this.potion != PotionTypes.EMPTY && this.potion != null) {
         p_70014_1_.setString("Potion", IRegistry.field_212621_j.func_177774_c(this.potion).toString());
      }

      if (this.fixedColor) {
         p_70014_1_.setInteger("Color", this.getColor());
      }

      if (!this.customPotionEffects.isEmpty()) {
         NBTTagList nbttaglist = new NBTTagList();

         for(PotionEffect potioneffect : this.customPotionEffects) {
            nbttaglist.add((INBTBase)potioneffect.write(new NBTTagCompound()));
         }

         p_70014_1_.setTag("CustomPotionEffects", nbttaglist);
      }

   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      if (p_70037_1_.hasKey("Potion", 8)) {
         this.potion = PotionUtils.getPotionTypeFromNBT(p_70037_1_);
      }

      for(PotionEffect potioneffect : PotionUtils.getFullEffectsFromTag(p_70037_1_)) {
         this.addEffect(potioneffect);
      }

      if (p_70037_1_.hasKey("Color", 99)) {
         this.setFixedColor(p_70037_1_.getInteger("Color"));
      } else {
         this.refreshColor();
      }

   }

   protected void arrowHit(EntityLivingBase p_184548_1_) {
      super.arrowHit(p_184548_1_);

      for(PotionEffect potioneffect : this.potion.getEffects()) {
         p_184548_1_.addPotionEffect(new PotionEffect(potioneffect.getPotion(), Math.max(potioneffect.getDuration() / 8, 1), potioneffect.getAmplifier(), potioneffect.isAmbient(), potioneffect.doesShowParticles()));
      }

      if (!this.customPotionEffects.isEmpty()) {
         for(PotionEffect potioneffect1 : this.customPotionEffects) {
            p_184548_1_.addPotionEffect(potioneffect1);
         }
      }

   }

   protected ItemStack getArrowStack() {
      if (this.customPotionEffects.isEmpty() && this.potion == PotionTypes.EMPTY) {
         return new ItemStack(Items.ARROW);
      } else {
         ItemStack itemstack = new ItemStack(Items.TIPPED_ARROW);
         PotionUtils.addPotionToItemStack(itemstack, this.potion);
         PotionUtils.appendEffects(itemstack, this.customPotionEffects);
         if (this.fixedColor) {
            itemstack.getOrCreateTag().setInteger("CustomPotionColor", this.getColor());
         }

         return itemstack;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 0) {
         int i = this.getColor();
         if (i != -1) {
            double d0 = (double)(i >> 16 & 255) / 255.0D;
            double d1 = (double)(i >> 8 & 255) / 255.0D;
            double d2 = (double)(i >> 0 & 255) / 255.0D;

            for(int j = 0; j < 20; ++j) {
               this.world.spawnParticle(Particles.ENTITY_EFFECT, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, d0, d1, d2);
            }
         }
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }
}
