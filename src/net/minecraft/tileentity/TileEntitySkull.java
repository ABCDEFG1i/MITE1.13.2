package net.minecraft.tileentity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.ITickable;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntitySkull extends TileEntity implements ITickable {
   private GameProfile playerProfile;
   private int dragonAnimatedTicks;
   private boolean dragonAnimated;
   private boolean shouldDrop = true;
   private static PlayerProfileCache profileCache;
   private static MinecraftSessionService sessionService;

   public TileEntitySkull() {
      super(TileEntityType.SKULL);
   }

   public static void setProfileCache(PlayerProfileCache p_184293_0_) {
      profileCache = p_184293_0_;
   }

   public static void setSessionService(MinecraftSessionService p_184294_0_) {
      sessionService = p_184294_0_;
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
      super.writeToNBT(p_189515_1_);
      if (this.playerProfile != null) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         NBTUtil.writeGameProfile(nbttagcompound, this.playerProfile);
         p_189515_1_.setTag("Owner", nbttagcompound);
      }

      return p_189515_1_;
   }

   public void readFromNBT(NBTTagCompound p_145839_1_) {
      super.readFromNBT(p_145839_1_);
      if (p_145839_1_.hasKey("Owner", 10)) {
         this.setPlayerProfile(NBTUtil.readGameProfileFromNBT(p_145839_1_.getCompoundTag("Owner")));
      } else if (p_145839_1_.hasKey("ExtraType", 8)) {
         String s = p_145839_1_.getString("ExtraType");
         if (!StringUtils.isNullOrEmpty(s)) {
            this.setPlayerProfile(new GameProfile((UUID)null, s));
         }
      }

   }

   public void tick() {
      Block block = this.getBlockState().getBlock();
      if (block == Blocks.DRAGON_HEAD || block == Blocks.DRAGON_WALL_HEAD) {
         if (this.world.isBlockPowered(this.pos)) {
            this.dragonAnimated = true;
            ++this.dragonAnimatedTicks;
         } else {
            this.dragonAnimated = false;
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getAnimationProgress(float p_184295_1_) {
      return this.dragonAnimated ? (float)this.dragonAnimatedTicks + p_184295_1_ : (float)this.dragonAnimatedTicks;
   }

   @Nullable
   public GameProfile getPlayerProfile() {
      return this.playerProfile;
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 4, this.getUpdateTag());
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public void setPlayerProfile(@Nullable GameProfile p_195485_1_) {
      this.playerProfile = p_195485_1_;
      this.updatePlayerProfile();
   }

   private void updatePlayerProfile() {
      this.playerProfile = updateGameProfile(this.playerProfile);
      this.markDirty();
   }

   public static GameProfile updateGameProfile(GameProfile p_174884_0_) {
      if (p_174884_0_ != null && !StringUtils.isNullOrEmpty(p_174884_0_.getName())) {
         if (p_174884_0_.isComplete() && p_174884_0_.getProperties().containsKey("textures")) {
            return p_174884_0_;
         } else if (profileCache != null && sessionService != null) {
            GameProfile gameprofile = profileCache.getGameProfileForUsername(p_174884_0_.getName());
            if (gameprofile == null) {
               return p_174884_0_;
            } else {
               Property property = Iterables.getFirst(gameprofile.getProperties().get("textures"), (Property)null);
               if (property == null) {
                  gameprofile = sessionService.fillProfileProperties(gameprofile, true);
               }

               return gameprofile;
            }
         } else {
            return p_174884_0_;
         }
      } else {
         return p_174884_0_;
      }
   }

   public static void disableDrop(IBlockReader p_195486_0_, BlockPos p_195486_1_) {
      TileEntity tileentity = p_195486_0_.getTileEntity(p_195486_1_);
      if (tileentity instanceof TileEntitySkull) {
         TileEntitySkull tileentityskull = (TileEntitySkull)tileentity;
         tileentityskull.shouldDrop = false;
      }

   }

   public boolean shouldDrop() {
      return this.shouldDrop;
   }
}
