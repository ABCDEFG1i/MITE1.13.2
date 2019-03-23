package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityPainting extends EntityHanging {
   public PaintingType art;

   public EntityPainting(World p_i1599_1_) {
      super(EntityType.PAINTING, p_i1599_1_);
   }

   public EntityPainting(World p_i45849_1_, BlockPos p_i45849_2_, EnumFacing p_i45849_3_) {
      super(EntityType.PAINTING, p_i45849_1_, p_i45849_2_);
      List<PaintingType> list = Lists.newArrayList();
      int i = 0;

      for(PaintingType paintingtype : IRegistry.field_212620_i) {
         this.art = paintingtype;
         this.updateFacingWithBoundingBox(p_i45849_3_);
         if (this.onValidSurface()) {
            list.add(paintingtype);
            int j = paintingtype.getWidth() * paintingtype.getHeight();
            if (j > i) {
               i = j;
            }
         }
      }

      if (!list.isEmpty()) {
         Iterator<PaintingType> iterator = list.iterator();

         while(iterator.hasNext()) {
            PaintingType paintingtype1 = iterator.next();
            if (paintingtype1.getWidth() * paintingtype1.getHeight() < i) {
               iterator.remove();
            }
         }

         this.art = list.get(this.rand.nextInt(list.size()));
      }

      this.updateFacingWithBoundingBox(p_i45849_3_);
   }

   @OnlyIn(Dist.CLIENT)
   public EntityPainting(World p_i48559_1_, BlockPos p_i48559_2_, EnumFacing p_i48559_3_, PaintingType p_i48559_4_) {
      this(p_i48559_1_, p_i48559_2_, p_i48559_3_);
      this.art = p_i48559_4_;
      this.updateFacingWithBoundingBox(p_i48559_3_);
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      p_70014_1_.setString("Motive", IRegistry.field_212620_i.func_177774_c(this.art).toString());
      super.writeEntityToNBT(p_70014_1_);
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      this.art = IRegistry.field_212620_i.func_82594_a(ResourceLocation.makeResourceLocation(p_70037_1_.getString("Motive")));
      super.readEntityFromNBT(p_70037_1_);
   }

   public int getWidthPixels() {
      return this.art.getWidth();
   }

   public int getHeightPixels() {
      return this.art.getHeight();
   }

   public void onBroken(@Nullable Entity p_110128_1_) {
      if (this.world.getGameRules().getBoolean("doEntityDrops")) {
         this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);
         if (p_110128_1_ instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)p_110128_1_;
            if (entityplayer.capabilities.isCreativeMode) {
               return;
            }
         }

         this.entityDropItem(Items.PAINTING);
      }
   }

   public void playPlaceSound() {
      this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
   }

   public void setLocationAndAngles(double p_70012_1_, double p_70012_3_, double p_70012_5_, float p_70012_7_, float p_70012_8_) {
      this.setPosition(p_70012_1_, p_70012_3_, p_70012_5_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
      BlockPos blockpos = this.hangingPosition.add(p_180426_1_ - this.posX, p_180426_3_ - this.posY, p_180426_5_ - this.posZ);
      this.setPosition((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
   }
}
