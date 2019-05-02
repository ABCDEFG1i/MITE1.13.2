package net.minecraft.entity.item;

import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityMinecartFurnace extends EntityMinecart {
   private static final DataParameter<Boolean> POWERED = EntityDataManager.createKey(EntityMinecartFurnace.class, DataSerializers.BOOLEAN);
   private int fuel;
   public double pushX;
   public double pushZ;
   private static final Ingredient field_195407_e = Ingredient.fromItems(Items.COAL, Items.CHARCOAL);

   public EntityMinecartFurnace(World p_i1718_1_) {
      super(EntityType.FURNACE_MINECART, p_i1718_1_);
   }

   public EntityMinecartFurnace(World p_i1719_1_, double p_i1719_2_, double p_i1719_4_, double p_i1719_6_) {
      super(EntityType.FURNACE_MINECART, p_i1719_1_, p_i1719_2_, p_i1719_4_, p_i1719_6_);
   }

   public EntityMinecart.Type getMinecartType() {
      return EntityMinecart.Type.FURNACE;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(POWERED, false);
   }

   public void tick() {
      super.tick();
      if (this.fuel > 0) {
         --this.fuel;
      }

      if (this.fuel <= 0) {
         this.pushX = 0.0D;
         this.pushZ = 0.0D;
      }

      this.setMinecartPowered(this.fuel > 0);
      if (this.isMinecartPowered() && this.rand.nextInt(4) == 0) {
         this.world.spawnParticle(Particles.LARGE_SMOKE, this.posX, this.posY + 0.8D, this.posZ, 0.0D, 0.0D, 0.0D);
      }

   }

   protected double getMaximumSpeed() {
      return 0.2D;
   }

   public void killMinecart(DamageSource p_94095_1_) {
      super.killMinecart(p_94095_1_);
      if (!p_94095_1_.isExplosion() && this.world.getGameRules().getBoolean("doEntityDrops")) {
         this.entityDropItem(Blocks.COBBLESTONE_FURNACE);
      }

   }

   protected void moveAlongTrack(BlockPos p_180460_1_, IBlockState p_180460_2_) {
      super.moveAlongTrack(p_180460_1_, p_180460_2_);
      double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;
      if (d0 > 1.0E-4D && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.001D) {
         d0 = (double)MathHelper.sqrt(d0);
         this.pushX /= d0;
         this.pushZ /= d0;
         if (this.pushX * this.motionX + this.pushZ * this.motionZ < 0.0D) {
            this.pushX = 0.0D;
            this.pushZ = 0.0D;
         } else {
            double d1 = d0 / this.getMaximumSpeed();
            this.pushX *= d1;
            this.pushZ *= d1;
         }
      }

   }

   protected void applyDrag() {
      double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;
      if (d0 > 1.0E-4D) {
         d0 = (double)MathHelper.sqrt(d0);
         this.pushX /= d0;
         this.pushZ /= d0;
         double d1 = 1.0D;
         this.motionX *= (double)0.8F;
         this.motionY *= 0.0D;
         this.motionZ *= (double)0.8F;
         this.motionX += this.pushX * 1.0D;
         this.motionZ += this.pushZ * 1.0D;
      } else {
         this.motionX *= (double)0.98F;
         this.motionY *= 0.0D;
         this.motionZ *= (double)0.98F;
      }

      super.applyDrag();
   }

   public boolean processInitialInteract(EntityPlayer p_184230_1_, EnumHand p_184230_2_) {
      ItemStack itemstack = p_184230_1_.getHeldItem(p_184230_2_);
      if (field_195407_e.test(itemstack) && this.fuel + 3600 <= 32000) {
         if (!p_184230_1_.capabilities.isCreativeMode) {
            itemstack.shrink(1);
         }

         this.fuel += 3600;
      }

      this.pushX = this.posX - p_184230_1_.posX;
      this.pushZ = this.posZ - p_184230_1_.posZ;
      return true;
   }

   protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setDouble("PushX", this.pushX);
      p_70014_1_.setDouble("PushZ", this.pushZ);
      p_70014_1_.setShort("Fuel", (short)this.fuel);
   }

   protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.pushX = p_70037_1_.getDouble("PushX");
      this.pushZ = p_70037_1_.getDouble("PushZ");
      this.fuel = p_70037_1_.getShort("Fuel");
   }

   protected boolean isMinecartPowered() {
      return this.dataManager.get(POWERED);
   }

   protected void setMinecartPowered(boolean p_94107_1_) {
      this.dataManager.set(POWERED, p_94107_1_);
   }

   public IBlockState getDefaultDisplayTile() {
      return Blocks.COBBLESTONE_FURNACE.getDefaultState().with(BlockFurnace.FACING, EnumFacing.NORTH).with(BlockFurnace.LIT, Boolean.valueOf(this.isMinecartPowered()));
   }
}
