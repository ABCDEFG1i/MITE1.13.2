package net.minecraft.world;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class BossInfoServer extends BossInfo {
   private final Set<EntityPlayerMP> players = Sets.newHashSet();
   private final Set<EntityPlayerMP> readOnlyPlayers = Collections.unmodifiableSet(this.players);
   private boolean visible = true;

   public BossInfoServer(ITextComponent p_i46839_1_, BossInfo.Color p_i46839_2_, BossInfo.Overlay p_i46839_3_) {
      super(MathHelper.getRandomUUID(), p_i46839_1_, p_i46839_2_, p_i46839_3_);
   }

   public void setPercent(float p_186735_1_) {
      if (p_186735_1_ != this.percent) {
         super.setPercent(p_186735_1_);
         this.sendUpdate(SPacketUpdateBossInfo.Operation.UPDATE_PCT);
      }

   }

   public void setColor(BossInfo.Color p_186745_1_) {
      if (p_186745_1_ != this.color) {
         super.setColor(p_186745_1_);
         this.sendUpdate(SPacketUpdateBossInfo.Operation.UPDATE_STYLE);
      }

   }

   public void setOverlay(BossInfo.Overlay p_186746_1_) {
      if (p_186746_1_ != this.overlay) {
         super.setOverlay(p_186746_1_);
         this.sendUpdate(SPacketUpdateBossInfo.Operation.UPDATE_STYLE);
      }

   }

   public BossInfo setDarkenSky(boolean p_186741_1_) {
      if (p_186741_1_ != this.darkenSky) {
         super.setDarkenSky(p_186741_1_);
         this.sendUpdate(SPacketUpdateBossInfo.Operation.UPDATE_PROPERTIES);
      }

      return this;
   }

   public BossInfo setPlayEndBossMusic(boolean p_186742_1_) {
      if (p_186742_1_ != this.playEndBossMusic) {
         super.setPlayEndBossMusic(p_186742_1_);
         this.sendUpdate(SPacketUpdateBossInfo.Operation.UPDATE_PROPERTIES);
      }

      return this;
   }

   public BossInfo setCreateFog(boolean p_186743_1_) {
      if (p_186743_1_ != this.createFog) {
         super.setCreateFog(p_186743_1_);
         this.sendUpdate(SPacketUpdateBossInfo.Operation.UPDATE_PROPERTIES);
      }

      return this;
   }

   public void setName(ITextComponent p_186739_1_) {
      if (!Objects.equal(p_186739_1_, this.name)) {
         super.setName(p_186739_1_);
         this.sendUpdate(SPacketUpdateBossInfo.Operation.UPDATE_NAME);
      }

   }

   private void sendUpdate(SPacketUpdateBossInfo.Operation p_186759_1_) {
      if (this.visible) {
         SPacketUpdateBossInfo spacketupdatebossinfo = new SPacketUpdateBossInfo(p_186759_1_, this);

         for(EntityPlayerMP entityplayermp : this.players) {
            entityplayermp.connection.sendPacket(spacketupdatebossinfo);
         }
      }

   }

   public void addPlayer(EntityPlayerMP p_186760_1_) {
      if (this.players.add(p_186760_1_) && this.visible) {
         p_186760_1_.connection.sendPacket(new SPacketUpdateBossInfo(SPacketUpdateBossInfo.Operation.ADD, this));
      }

   }

   public void removePlayer(EntityPlayerMP p_186761_1_) {
      if (this.players.remove(p_186761_1_) && this.visible) {
         p_186761_1_.connection.sendPacket(new SPacketUpdateBossInfo(SPacketUpdateBossInfo.Operation.REMOVE, this));
      }

   }

   public void removeAllPlayers() {
      if (!this.players.isEmpty()) {
         for(EntityPlayerMP entityplayermp : this.players) {
            this.removePlayer(entityplayermp);
         }
      }

   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean p_186758_1_) {
      if (p_186758_1_ != this.visible) {
         this.visible = p_186758_1_;

         for(EntityPlayerMP entityplayermp : this.players) {
            entityplayermp.connection.sendPacket(new SPacketUpdateBossInfo(p_186758_1_ ? SPacketUpdateBossInfo.Operation.ADD : SPacketUpdateBossInfo.Operation.REMOVE, this));
         }
      }

   }

   public Collection<EntityPlayerMP> getPlayers() {
      return this.readOnlyPlayers;
   }
}
